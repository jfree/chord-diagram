package org.jfree.chord.demo;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ui.UIUtils;
import org.jfree.chord.data.ChordDataset;
import org.jfree.chord.data.DefaultChordDataset;
import org.jfree.chord.plot.ChordDiagram;

import javax.swing.*;
import java.awt.*;
import java.util.List;

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
        var result = new DefaultChordDataset();
        result.addEntry("KEY1", "KEY1", 2d);
        result.addEntry("KEY1", "KEY2", 3d);
        result.addEntry("KEY1", "KEY3", 4d);
        result.addEntry("KEY2", "KEY1", 5d);
        result.addEntry("KEY2", "KEY2", 6d);
        result.addEntry("KEY2", "KEY3", 7d);
        result.addEntry("KEY3", "KEY1", 8d);
        result.addEntry("KEY3", "KEY2", 9d);
        result.addEntry("KEY3", "KEY3", 12d);
        return result;
    }

    private static JFreeChart createChart(String title, ChordDataset dataset) {
        ChordDiagram plot = new ChordDiagram(dataset);

        // initialise the colors - later we can ensure this is automatic, especially to handle when new sections
        // are added to the dataset dynamically
        var keys = dataset.getKeys();
        for (var key : keys) {
            plot.setSectionPaint(key, SAMPLE_COLORS.get(keys.indexOf(key)));
        }
        return new JFreeChart(title, plot);
    }

    public static void main(String[] args) {
        ChordPlotDemo1 demo = new ChordPlotDemo1("JFreeChart: ChordPlotDemo1.java");
        demo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        demo.pack();
        UIUtils.centerFrameOnScreen(demo);
        demo.setVisible(true);
    }

    private static final List<Paint> DEFAULT_COLORS = List.of(
            new Color(239, 164, 127),
            new Color(140, 228, 139),
            new Color(155, 208, 227),
            new Color(221, 228, 95),
            new Color(118, 223, 194),
            new Color(240, 166, 184),
            new Color(231, 185, 98),
            new Color(186, 214, 150),
            new Color(217, 184, 226),
            new Color(201, 212, 116)
    );

    private static final List<Paint> SAMPLE_COLORS = List.of(
            new Color(95, 180, 126),
            new Color(64,74,133),
            new Color(133, 203, 104),
            new Color(67, 128, 140),
            new Color(76, 156, 138),
            new Color(67,41,116),
            new Color(189,221,81),
            new Color(63,102,138),
            new Color(249,232,85),
            new Color(63,17,81)
    );

}
