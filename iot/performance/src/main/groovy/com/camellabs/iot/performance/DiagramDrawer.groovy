package com.camellabs.iot.performance

import org.jfree.chart.ChartFactory
import org.jfree.chart.ChartUtilities
import org.jfree.chart.JFreeChart
import org.jfree.chart.plot.PlotOrientation
import org.jfree.data.category.DefaultCategoryDataset

class DiagramDrawer {

    def draw(String testGroup, List<TestResult> results) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        results.each { result ->
            dataset.setValue(result.messagesPerSecond, "msg/sec", result.label)
        }
        JFreeChart chart = ChartFactory.createBarChart("Message flow comparison",
                "Raspberry Pi 2: " + testGroup, "msg/sec", dataset, PlotOrientation.VERTICAL,
                false, true, false);
        def diagramsBase = new File('/tmp/gateway-performance')
        diagramsBase.mkdirs()
        ChartUtilities.saveChartAsPNG(new File(diagramsBase, testGroup + ".png"), chart, 800, 600);
    }

    public static void main(String[] args) {
        new DiagramDrawer().draw('xxx', new TestResult('Xxxx', "MQTT QOS 0", 2500), new TestResult('Xxxx',"MQTT QOS 1", 120), new TestResult('Xxxx', "MQTT QOS 2", 120))
    }

}
