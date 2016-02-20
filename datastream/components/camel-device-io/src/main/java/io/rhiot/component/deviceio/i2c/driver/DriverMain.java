package io.rhiot.component.deviceio.i2c.driver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DriverMain {

    private static final transient Logger LOG = LoggerFactory.getLogger(DriverMain.class);

    public static void main(String[] args) throws Exception {

        Class clazz = DriverMain.class.getClassLoader().loadClass("io.rhiot.component.deviceio.i2c.driver.BMP180Driver");

        I2CDriver bmp = (I2CDriver) clazz.newInstance();

        bmp.start();

        LOG.info(bmp.get().toString());

        bmp.stop();

    }

}
