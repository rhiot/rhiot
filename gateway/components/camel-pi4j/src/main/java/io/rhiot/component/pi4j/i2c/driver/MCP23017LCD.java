/**
 * Licensed to the Rhiot under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.rhiot.component.pi4j.i2c.driver;

import java.io.IOException;

import io.rhiot.component.pi4j.i2c.I2CEndpoint;
import io.rhiot.component.pi4j.i2c.I2CProducer;
import com.pi4j.io.i2c.I2CDevice;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * This code was ported from Marcus Hirt's work 2013 available here, http://hirt.se/blog/?p=464
 * 
 * This code was ported from the AdaFruit_CharLCDPlate.py code, in which the following license notes were present:
 * # Python library for Adafruit RGB-backlit LCD plate for Raspberry Pi.
 * # Written by Adafruit Industries.  MIT license.
 * # This is essentially a complete rewrite, but the calling syntax
 * # and constants are based on code from lrvick and LiquidCrystal.
 * # lrvic - https://github.com/lrvick/raspi-hd44780/blob/master/hd44780.py
 * # LiquidCrystal - https://github.com/arduino/Arduino/blob/master/libraries/LiquidCrystal/LiquidCrystal.cpp
 *
 */
public class MCP23017LCD extends I2CProducer {

    public enum Direction {
        LEFT, RIGHT;
    }

    private static final Logger LOG = LoggerFactory.getLogger(MCP23017LCD.class);

    public static final String LCD_BLINK_CURSOR = "CamelLCDBlinkCursor";
    public static final String LCD_CURSOR = "CamelLCDCursor";
    public static final String LCD_COLOR = "CamelLCDColor";

    // LCD Commands
    private static final int LCD_CLEARDISPLAY = 0x01;
    private static final int LCD_RETURNHOME = 0x02;
    private static final int LCD_ENTRYMODESET = 0x04;
    private static final int LCD_DISPLAYCONTROL = 0x08;
    private static final int LCD_CURSORSHIFT = 0x10;
    // private static final int LCD_FUNCTIONSET = 0x20;
    // private static final int LCD_SETCGRAMADDR = 0x40;
    private static final int LCD_SETDDRAMADDR = 0x80;

    // Flags for display on/off control
    private static final int LCD_DISPLAYON = 0x04;
    // private static final int LCD_DISPLAYOFF = 0x00;
    private static final int LCD_CURSORON = 0x02;
    private static final int LCD_CURSOROFF = 0x00;
    private static final int LCD_BLINKON = 0x01;
    private static final int LCD_BLINKOFF = 0x00;

    // Flags for display entry mode
    // private static final int LCD_ENTRYRIGHT = 0x00;
    private static final int LCD_ENTRYLEFT = 0x02;
    private static final int LCD_ENTRYSHIFTINCREMENT = 0x01;
    private static final int LCD_ENTRYSHIFTDECREMENT = 0x00;

    // Flags for display/cursor shift
    private static final int LCD_DISPLAYMOVE = 0x08;
    private static final int LCD_CURSORMOVE = 0x00;
    private static final int LCD_MOVERIGHT = 0x04;
    private static final int LCD_MOVELEFT = 0x00;

    // Port expander registers
    // IOCON when Bank 0 active
    private static final int MCP23017_IOCON_BANK0 = 0x0A;
    // IOCON when Bank 1 active
    private static final int MCP23017_IOCON_BANK1 = 0x15;

    // These are register addresses when in Bank 1 only:
    private static final int MCP23017_GPIOA = 0x09;
    private static final int MCP23017_IODIRB = 0x10;
    private static final int MCP23017_GPIOB = 0x19;

    // The LCD data pins (D4-D7) connect to MCP pins 12-9 (PORTB4-1), in
    // that order. Because this sequence is 'reversed,' a direct shift
    // won't work. This table remaps 4-bit data values to MCP PORTB
    // outputs, incorporating both the reverse and shift.
    private static final int[] SHIFT_REVERSE = { 0x00, 0x10, 0x08, 0x18, 0x04, 0x14, 0x0C, 0x1C, 0x02, 0x12, 0x0A, 0x1A,
            0x06, 0x16, 0x0E, 0x1E };

    private static final int[] ROW_OFFSETS = new int[] { 0x00, 0x40, 0x14, 0x54 };

    private int portA = 0x00;
    private int portB = 0x00;
    private int ddrB = 0x10;
    private int displayShift = LCD_CURSORMOVE | LCD_MOVERIGHT;
    private int displayMode = LCD_ENTRYLEFT | LCD_ENTRYSHIFTDECREMENT;
    private int displayControl = LCD_DISPLAYON | LCD_CURSOROFF | LCD_BLINKOFF;
    private MCP23017LCDColor color = MCP23017LCDColor.WHITE;
    private String startupText = "Ready ";

    private boolean clearOnEachMessage = true;

    public MCP23017LCD(I2CEndpoint endpoint, I2CDevice device) {
        super(endpoint, device);
    }

    private void clear() throws IOException {
        internalWrite(LCD_CLEARDISPLAY);
    }

    protected void doStart() throws Exception {
        LOG.debug("doStart");
        // Set MCP23017 IOCON register to Bank 0 with sequential operation.
        // If chip is already set for Bank 0, this will just write to OLATB,
        // which won't seriously bother anything on the plate right now
        // (blue backlight LED will come on, but that's done in the next
        // step anyway).
        write(MCP23017_IOCON_BANK1, (byte) 0);

        // Brute force reload ALL registers to known state. This also
        // sets up all the input pins, pull-ups, etc. for the Pi Plate.
        // NOTE(marcus/9 dec 2013): 0x3F assumes that GPA5 is input too -
        // it is however not connected.
        byte[] registers = { 0x3F, // IODIRA R+G LEDs=outputs, buttons=inputs
                (byte) ddrB, // IODIRB LCD D7=input, Blue LED=output
                0x3F, // IPOLA Invert polarity on button inputs
                0x00, // IPOLB
                0x00, // GPINTENA Disable interrupt-on-change
                0x00, // GPINTENB
                0x00, // DEFVALA
                0x00, // DEFVALB
                0x00, // INTCONA
                0x00, // INTCONB
                0x00, // IOCON
                0x00, // IOCON
                0x3F, // GPPUA Enable pull-ups on buttons
                0x00, // GPPUB
                0x00, // INTFA
                0x00, // INTFB
                0x00, // INTCAPA
                0x00, // INTCAPB
                (byte) portA, // GPIOA
                (byte) portB, // GPIOB
                (byte) portA, // OLATA 0 on all outputs; side effect
                              // of
                (byte) portB // OLATB turning on R+G+B backlight
                             // LEDs.
        };
        write(0, registers, 0, registers.length);

        // Switch to Bank 1 and disable sequential operation.
        // From this point forward, the register addresses do NOT match
        // the list immediately above. Instead, use the constants defined
        // at the start of the class. Also, the address register will no
        // longer increment automatically after this -- multi-byte
        // operations must be broken down into single-byte calls.
        write(MCP23017_IOCON_BANK0, (byte) 0xA0);

        internalWrite(0x33); // Init
        internalWrite(0x32); // Init
        internalWrite(0x28); // 2 line 5x8 matrix
        internalWrite(LCD_CLEARDISPLAY);
        internalWrite(LCD_CURSORSHIFT | displayShift);
        internalWrite(LCD_ENTRYMODESET | displayMode);
        internalWrite(LCD_DISPLAYCONTROL | displayControl);
        internalWrite(LCD_RETURNHOME);

        setColor(color);
        writeTextToDevice(this.startupText);
    }

    protected void doStop() throws Exception {
        writeTextToDevice("");
        portA = 0xC0; // Turn off LEDs on the way out
        portB = 0x01;
        sleep(2);
        write(MCP23017_IOCON_BANK1, (byte) 0);
        byte[] registers = { 0x3F, // IODIRA
                (byte) ddrB, // IODIRB
                0x0, // IPOLA
                0x0, // IPOLB
                0x0, // GPINTENA
                0x0, // GPINTENB
                0x0, // DEFVALA
                0x0, // DEFVALB
                0x0, // INTCONA
                0x0, // INTCONB
                0x0, // IOCON
                0x0, // IOCON
                0x3F, // GPPUA
                0x0, // GPPUB
                0x0, // INTFA
                0x0, // INTFB
                0x0, // INTCAPA
                0x0, // INTCAPB
                (byte) portA, // GPIOA
                (byte) portB, // GPIOB
                (byte) portA, // OLATA
                (byte) portB // OLATB
        };
        write(0, registers, 0, registers.length);
    }

    public MCP23017LCDColor getColor() {
        return color;
    }

    public String getStartupText() {
        return startupText;
    }

    private void home() throws IOException {
        internalWrite(LCD_RETURNHOME);
    }

    private void internalWrite(int value) throws IOException {
        waitOnLCDBusyFlag();
        int bitmask = portB & 0x01; // Mask out PORTB LCD control bits

        byte[] data = out4(bitmask, value);
        write(MCP23017_GPIOB, data, 0, 4);
        portB = data[3];

        // If a poll-worthy instruction was issued, reconfigure D7
        // pin as input to indicate need for polling on next call.
        if (value == LCD_CLEARDISPLAY || value == LCD_RETURNHOME) {
            ddrB |= 0x10;
            write(MCP23017_IODIRB, (byte) ddrB);
        }
    }

    public boolean isAutoScroll() {
        return (displayControl & LCD_ENTRYSHIFTINCREMENT) > 0;
    }

    private boolean isBlinkCursor() {
        return (displayControl & LCD_BLINKON) > 0;
    }

    public boolean isClearOnEachMessage() {
        return clearOnEachMessage;
    }

    private boolean isCursor() {
        return (displayControl & LCD_CURSORON) > 0;
    }

    private boolean isDisplay() {
        return (displayControl & LCD_DISPLAYON) > 0;
    }

    private void manageProperties(Exchange exchange) throws IOException {
        MCP23017LCDColor color = exchange.getIn().getHeader(LCD_COLOR, MCP23017LCDColor.class);

        if (color != null) {
            setColor(color);
        }

        Boolean cursor = exchange.getIn().getHeader(LCD_CURSOR, Boolean.class);
        if (cursor != null) {
            setCursor(cursor.booleanValue());
        }

        Boolean blink = exchange.getIn().getHeader(LCD_BLINK_CURSOR, Boolean.class);

        if (blink != null) {
            setBlinkCursor(blink.booleanValue());
        }

    }

    private byte[] out4(int bitmask, int value) {
        int hi = bitmask | SHIFT_REVERSE[value >> 4];
        int lo = bitmask | SHIFT_REVERSE[value & 0x0F];

        return new byte[] { (byte) (hi | 0x20), (byte) hi, (byte) (lo | 0x20), (byte) lo };
    }

    @Override
    public void process(Exchange exchange) throws Exception {

        manageProperties(exchange);

        if (LOG.isDebugEnabled()) {
            LOG.debug(">> " + exchange.toString());
        }

        if (clearOnEachMessage) {
            clear();
        }

        setText(exchange.getIn().getBody(String.class));
    }

    private void scrollDisplay(Direction direction) throws IOException {
        if (direction == Direction.LEFT) {
            displayShift = LCD_DISPLAYMOVE | LCD_MOVELEFT;
            internalWrite(LCD_CURSORSHIFT | displayShift);
        } else {
            displayShift = LCD_DISPLAYMOVE | LCD_MOVERIGHT;
            internalWrite(LCD_CURSORSHIFT | displayShift);
        }
    }

    public void setAutoScroll(boolean enable) throws IOException {
        if (enable) {
            // This will 'right justify' text from the cursor
            displayMode |= LCD_ENTRYSHIFTINCREMENT;
            internalWrite(LCD_ENTRYMODESET | displayMode);
        } else {
            // This will 'left justify' text from the cursor
            displayMode &= ~LCD_ENTRYSHIFTINCREMENT;
            internalWrite(LCD_ENTRYMODESET | displayMode);
        }
    }

    public void setBlinkCursor(boolean enable) throws IOException {
        if (enable) {
            displayControl |= LCD_BLINKON;
            internalWrite(LCD_DISPLAYCONTROL | displayControl);
        } else {
            displayControl &= ~LCD_BLINKON;
            internalWrite(LCD_DISPLAYCONTROL | displayControl);
        }
    }

    public void setClearOnEachMessage(boolean clearOnEachMessage) {
        this.clearOnEachMessage = clearOnEachMessage;
    }

    public void setColor(MCP23017LCDColor color) throws IOException {
        int c = ~color.getValue();
        portA = (portA & 0x3F) | ((c & 0x03) << 6);
        portB = (portB & 0xFE) | ((c & 0x04) >> 2);
        // Has to be done as two writes because sequential operation is off.
        getDevice().write(MCP23017_GPIOA, (byte) portA);
        getDevice().write(MCP23017_GPIOB, (byte) portB);
        this.color = color;
    }

    public void setCursor(boolean enable) throws IOException {
        if (enable) {
            displayControl |= LCD_CURSORON;
            internalWrite(LCD_DISPLAYCONTROL | displayControl);
        } else {
            displayControl &= ~LCD_CURSORON;
            internalWrite(LCD_DISPLAYCONTROL | displayControl);
        }
    }

    private void setCursorPosition(int row, int column) throws IOException {
        internalWrite(LCD_SETDDRAMADDR | (column + ROW_OFFSETS[row]));
    }

    public void setDisplay(boolean enable) throws IOException {
        if (enable) {
            displayControl |= LCD_DISPLAYON;
            internalWrite(LCD_DISPLAYCONTROL | displayControl);
        } else {
            displayControl &= ~LCD_DISPLAYON;
            internalWrite(LCD_DISPLAYCONTROL | displayControl);
        }
    }

    public void setStartupText(String startupText) {
        this.startupText = startupText;
    }

    private void setText(int row, String string) throws IOException {
        setCursorPosition(row, 0);
        writeTextToDevice(string);
    }

    private void setText(String s) throws IOException {
        String[] str = s.split("\n");
        for (int i = 0; i < str.length; i++) {
            setText(i, str[i]);
        }
    }

    private void setTextFlowDirection(Direction direction) throws IOException {
        if (direction == Direction.LEFT) {
            // This is for text that flows right to left
            displayMode &= ~LCD_ENTRYLEFT;
            internalWrite(LCD_ENTRYMODESET | displayMode);
        } else {
            // This is for text that flows left to right
            displayMode |= LCD_ENTRYLEFT;
            internalWrite(LCD_ENTRYMODESET | displayMode);
        }
    }

    private void waitOnLCDBusyFlag() throws IOException {
        // The speed of LCD accesses is inherently limited by I2C through the
        // port expander. A 'well behaved program' is expected to poll the
        // LCD to know that a prior instruction completed. But the timing of
        // most instructions is a known uniform 37 ms. The enable strobe
        // can't even be twiddled that fast through I2C, so it's a safe bet
        // with these instructions to not waste time polling (which requires
        // several I2C transfers for reconfiguring the port direction).
        // The D7 pin is set as input when a potentially time-consuming
        // instruction has been issued (e.g. screen clear), as well as on
        // startup, and polling will then occur before more commands or data
        // are issued.

        // If pin D7 is in input state, poll LCD busy flag until clear.
        if ((ddrB & 0x10) != 0) {
            int lo = (portB & 0x01) | 0x40;
            int hi = lo | 0x20; // E=1 (strobe)
            write(MCP23017_GPIOB, (byte) lo);
            while (true) {
                write((byte) hi); // Strobe high (enable)
                int bits = read(); // First nybble contains busy
                                   // state
                getDevice().write(MCP23017_GPIOB, new byte[] { (byte) lo, (byte) hi, (byte) lo }, 0, 3); // Strobe
                // low,
                // high,
                // low.
                // Second
                // nybble
                // (A3)
                // is
                // ignored.
                if ((bits & 0x02) == 0)
                    break; // D7=0, not busy
            }
            portB = lo;
            ddrB &= 0xEF; // Polling complete, change D7 pin to output
            write(MCP23017_IODIRB, (byte) ddrB);
        }
    }

    private void writeTextToDevice(String s) throws IOException {
        int sLen = s.length();
        int bytesLen = 4 * sLen;
        if (sLen < 1) {
            return;
        }

        waitOnLCDBusyFlag();
        int bitmask = portB & 0x01; // Mask out PORTB LCD control bits
        bitmask |= 0x80; // Set data bit

        byte[] bytes = new byte[4 * sLen];
        for (int i = 0; i < sLen; i++) {
            byte[] data = out4(bitmask, s.charAt(i));
            for (int j = 0; j < 4; j++) {
                bytes[(i * 4) + j] = data[j];
            }
        }
        getDevice().write(MCP23017_GPIOB, bytes, 0, bytesLen);
        portB = bytes[bytesLen - 1];
    }

}
