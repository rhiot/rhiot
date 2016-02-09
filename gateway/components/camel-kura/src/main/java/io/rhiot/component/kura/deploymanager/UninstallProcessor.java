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
package io.rhiot.component.kura.deploymanager;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static java.lang.System.getProperty;

public class UninstallProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(UninstallProcessor.class);

    private final File deployDirectory = new File(getProperty("kura_deploy_directory", "/opt/eclipse/kura/deploy"));

    @Override
    public void process(Exchange exchange) throws Exception {
        String bundleName = exchange.getIn().getBody(String.class) + ".jar";
        File bundle = new File(deployDirectory, bundleName);
        LOG.debug("About to remove bundle ", bundle.getAbsolutePath());
        bundle.delete();
    }

}
