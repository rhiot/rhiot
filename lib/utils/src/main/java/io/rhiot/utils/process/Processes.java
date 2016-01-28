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
package io.rhiot.utils.process;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

import static java.util.Arrays.asList;

public final class Processes {

    private static final Logger LOG = LoggerFactory.getLogger(Processes.class);

    private Processes() {
    }

    public static boolean canExecuteCommand(String... command) {
        try {
            new DefaultProcessManager().executeAndJoinOutput(command);
        } catch (ProcessExecutionException e) {
            LOG.debug("Cannot execute command " + asList(command) + " because of:", e);
            return false;
        }
        return true;
    }

}
