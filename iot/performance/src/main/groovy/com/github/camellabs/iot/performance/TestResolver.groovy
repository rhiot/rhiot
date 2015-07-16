package com.github.camellabs.iot.performance

interface TestResolver {

    List<TestSpecification> testsForKit(String kit)

}