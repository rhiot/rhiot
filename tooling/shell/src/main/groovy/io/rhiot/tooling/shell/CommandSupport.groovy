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
package io.rhiot.tooling.shell

import io.rhiot.utils.WithLogger
import org.apache.commons.lang3.StringUtils

import static java.lang.Boolean.parseBoolean
import static org.apache.commons.lang3.StringUtils.isBlank
import static org.apache.commons.lang3.StringUtils.isBlank

abstract class CommandSupport implements WithLogger {

    protected String parameter(String parameter, String defaultValue) {
        isBlank(parameter) ? defaultValue : parameter
    }

    protected String requiredParameter(String parameterName, String parameterValue) {
        if(isBlank(parameterValue)) {
            throw new IllegalArgumentException("Parameter '${parameterName}' is required.")
        }
        parameterValue
    }

    protected String parameter(String parameter, Closure<String> closure) {
        isBlank(parameter) ? closure() : parameter
    }

    protected int parameter(String parameterName, String parameter, int defaultValue) {
        try {
            isBlank(parameter) ? defaultValue : parameter.toInteger()
        } catch(NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Cannot parse option ${parameterName} with value ${parameter}. Expected integer number.", e)
        }
    }

    String execute(String... command) {
        def output = []
        try {
            output = doExecute(output, command)
        } catch (Exception e) {
            log().warn('Error occurred when processing the command: {}', command)
            if(isBlank(e.message)) {
                output << "Error ${e.class.simpleName} occurred when processing the command."
            } else {
                output << e.message
            }
        }
        log().debug('Output collected: {}', output)
        output.join('\n')
    }

    String executeOrPrintHelp(Boolean help, String... command) {
        if(help) {
            printHelp()
        } else {
            execute(command)
        }
    }

    protected String printHelp() {
        ''
    }

    abstract protected List<String> doExecute(List<String> output, String... command)

    // Helpers

    static String optionalBoolean(Boolean value) {
        value != null && value ? 'true' : 'false'
    }

}
