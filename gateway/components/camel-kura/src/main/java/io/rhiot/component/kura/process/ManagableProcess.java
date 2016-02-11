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
package io.rhiot.component.kura.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static java.util.Collections.unmodifiableList;
import static java.util.concurrent.Executors.newSingleThreadExecutor;

public class ManagableProcess {

    private final ExecutorService executor = newSingleThreadExecutor();

    private final String[] command;

    private Process process;

    private final List<String> output = new LinkedList<>();

    public ManagableProcess(String... command) {
        this.command = command;
    }

    // Life-cycle

    public void start() {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    process = new ProcessBuilder().redirectErrorStream(true).command(command).start();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        output.add(line);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void stop() {
        process.destroy();
        executor.shutdown();
    }

    // Getters

    public List<String> output() {
        return unmodifiableList(new ArrayList<>(output));
    }

}
