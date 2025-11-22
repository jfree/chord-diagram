package org.jfree.chord.demo;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chord.data.ChordDataset;
import org.jfree.chord.plot.ChordDiagram;

import javax.swing.*;
import java.awt.*;

public class ChordPlotDemo1 extends JFrame {

    /**
     * Creates a new demo application.
     *
     * @param title  the frame title.
     */
    public ChordPlotDemo1(String title) {
        super(title);
        ChordDataset dataset = createDataset();
        JFreeChart chart = createChart("Demo Chart", dataset);
        JPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(760, 500));
        setContentPane(chartPanel);
    }

    private static ChordDataset createDataset() {
        return null; // TODO
        //DefaultChordDataset<String> dataset = new DefaultFlowDataset<>();
    }

    private static JFreeChart createChart(String title, ChordDataset dataset) {
        ChordDiagram plot = new ChordDiagram(dataset);
        plot.setBackgroundPaint(Color.BLACK);
        return new JFreeChart(title, plot);
    }

}
