package io.rhiot.cmd

/**
 * Created by hekonsek on 16.03.16.
 */
class StdoutOutputAppender implements OutputAppender {

    @Override
    void append(String line) {
        println line
    }

}
