package io.rhiot.component.deviceio.i2c.driver;

public interface I2CDriver {

    public void init() throws Exception;

    public void start() throws Exception;

    public void stop() throws Exception;

    public void shutdown() throws Exception;

    public void suspend() throws Exception;

    public void resume() throws Exception;

}
