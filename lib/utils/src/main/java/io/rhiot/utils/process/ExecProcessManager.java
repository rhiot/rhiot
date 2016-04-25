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

import org.apache.commons.exec.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Process manager based on commons-exec, optionally waits for timeout or kills the process. Default is to not timeout.
 */
public class ExecProcessManager extends ExecutorBasedProcessManager {

    private static final Logger LOG = LoggerFactory.getLogger(ExecProcessManager.class);
    
    private int timeout;

    public ExecProcessManager() {
    }

    public ExecProcessManager(int timeout) {
        this.timeout = timeout;
    }

    @Override
    public List<String> executeAndJoinOutput(String... command) {

        CommandLine cmdLine = CommandLine.parse(String.join(" ", command));
        DefaultExecutor executor = new DefaultExecutor();
        executor.setExitValue(0);
        ExecResultHandler resultHandler = null;
        
        if (getTimeout() > 0) {
            ExecuteWatchdog watchdog = new ExecuteWatchdog(getTimeout());
            executor.setWatchdog(watchdog);
            resultHandler = new ExecResultHandler(watchdog);
        }
        try {
            CollectingLogOutputStream outAndErr = new CollectingLogOutputStream();
            executor.setStreamHandler(new PumpStreamHandler(outAndErr));
            if (resultHandler != null) {
                executor.execute(cmdLine, resultHandler);
            } else {
                executor.execute(cmdLine);
            }
            resultHandler.waitFor();
            return outAndErr.getLines();
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private class ExecResultHandler extends DefaultExecuteResultHandler {

        private ExecuteWatchdog watchdog;

        public ExecResultHandler(final ExecuteWatchdog watchdog) {
            this.watchdog = watchdog;
        }

        public ExecResultHandler(final int exitValue) {
            LOG.debug("Installation completed with exitValue [{}]", exitValue);
            super.onProcessComplete(exitValue);
        }

        @Override
        public void onProcessComplete(final int exitValue) {
            super.onProcessComplete(exitValue);
            LOG.debug("Successfully installed");
        }

        @Override
        public void onProcessFailed(final ExecuteException e) {
            super.onProcessFailed(e);
            if (watchdog != null && watchdog.killedProcess()) {
                LOG.warn("Installation killed by watchdog");
            } else {
                LOG.warn("Installation failed due to [{}]", e.getMessage());
            }
        }
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
