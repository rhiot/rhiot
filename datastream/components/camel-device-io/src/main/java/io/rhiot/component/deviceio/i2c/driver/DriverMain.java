package io.rhiot.component.deviceio.i2c.driver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DriverMain {

    private static final transient Logger LOG = LoggerFactory.getLogger(DriverMain.class);

    public static void main(String[] args) throws Exception {

        Class clazz = DriverMain.class.getClassLoader()
                .loadClass("io.rhiot.component.deviceio.i2c.driver.BMP180Driver");

        I2CDriver driver = (I2CDriver) clazz.newInstance();

        driver.start();

        LOG.info(driver.get().toString());

        driver.stop();

    }

}
