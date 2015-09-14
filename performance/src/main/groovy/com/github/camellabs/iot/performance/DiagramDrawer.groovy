/**
 * Licensed to the Camel Labs under one or more
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
package com.github.camellabs.iot.performance

import org.jfree.chart.ChartFactory
import org.jfree.chart.ChartUtilities
import org.jfree.chart.JFreeChart
import org.jfree.data.category.DefaultCategoryDataset

import static org.jfree.chart.plot.PlotOrientation.VERTICAL

class DiagramDrawer {

    def draw(String testGroup, List<TestResult> results) {
        def dataset = new DefaultCategoryDataset()
        results.each { result -> addDataFromResult(dataset, result) }
        JFreeChart chart = ChartFactory.createBarChart("Message flow comparison",
                "Raspberry Pi 2: " + testGroup, "msg/sec", dataset, VERTICAL,
                false, true, false);
        def diagramsBase = new File('/tmp/gateway-performance')
        diagramsBase.mkdirs()
        ChartUtilities.saveChartAsPNG(new File(diagramsBase, testGroup + ".png"), chart, 800, 600);
    }

    private addDataFromResult(DefaultCategoryDataset dataset, TestResult result) {
        dataset.setValue(result.messagesPerSecond(), "msg/sec", "${result.label}: ${result.messagesPerSecond().round()}")
    }

}
