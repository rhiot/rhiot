package io.rhiot.component.kura.gpio;

public enum KuraGPIOState {
    LOW(false), HIGH(true);

    boolean value = false;

    KuraGPIOState(boolean b) {
        value = b;
    }

}
