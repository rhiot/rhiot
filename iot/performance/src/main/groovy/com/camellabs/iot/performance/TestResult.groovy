package com.camellabs.iot.performance

class TestResult {

    String testGroup

    String label

    double messagesPerSecond

    TestResult(String testGroup, String label, double messagesPerSecond) {
        this.testGroup = testGroup
        this.label = label
        this.messagesPerSecond = messagesPerSecond
    }

}
