package com.github.camellabs.iot.gateway

import java.lang.annotation.Inherited
import java.lang.annotation.Retention
import java.lang.annotation.Target

import static java.lang.annotation.ElementType.TYPE
import static java.lang.annotation.RetentionPolicy.RUNTIME

@Inherited
@Target(TYPE)
@Retention(RUNTIME)
@interface GatewayVerticle {

    String conditionProperty() default ''

}