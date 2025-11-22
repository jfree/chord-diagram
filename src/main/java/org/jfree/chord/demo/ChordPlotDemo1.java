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
        result.addEntry("Africa", "Africa", 3.142471);
        result.addEntry("East Asia", "East Asia", 1.630997);
        result.addEntry("South Asia", "East Asia", 0.525881);
        result.addEntry("South East Asia", "East Asia", 0.145264);
        result.addEntry("Africa", "Europe", 2.107883);
        result.addEntry("East Asia", "Europe", 0.601265);
        result.addEntry("Europe", "Europe", 2.401476);
        result.addEntry("Latin America", "Europe", 1.762587);
        result.addEntry("North America", "Europe", 1.215929);
        result.addEntry("Oceania", "Europe", 0.17037);
        result.addEntry("South Asia", "Europe", 1.390272);
        result.addEntry("South East Asia", "Europe", 0.468762);
        result.addEntry("Soviet Union", "Europe", 0.60923);
        result.addEntry("West Asia", "Europe", 0.449623);
        result.addEntry("Latin America", "Latin America", 0.879198);
        result.addEntry("North America", "Latin America", 0.276908);
        result.addEntry("Africa", "North America", 0.540887);
        result.addEntry("East Asia", "North America", 0.97306);
        result.addEntry("Latin America", "North America", 3.627847);
        result.addEntry("South Asia", "North America", 1.508008);
        result.addEntry("South East Asia", "North America", 1.057904);
        result.addEntry("West Asia", "North America", 0.169274);
        result.addEntry("Africa", "Oceania", 0.155988);
        result.addEntry("East Asia", "Oceania", 0.333608);
        result.addEntry("Oceania", "Oceania", 0.190706);
        result.addEntry("South Asia", "Oceania", 0.34742);
        result.addEntry("South East Asia", "Oceania", 0.278746);
        result.addEntry("South Asia", "South Asia", 1.307907);
        result.addEntry("East Asia", "South East Asia", 0.380388);
        result.addEntry("South East Asia", "South East Asia", 0.781316);
        result.addEntry("Soviet Union", "Soviet Union", 1.870501);
        result.addEntry("Africa", "West Asia", 0.673004);
        result.addEntry("East Asia", "West Asia",   0.869311);
        result.addEntry("South Asia", "West Asia",   4.902081);
        result.addEntry("West Asia", "West Asia", 0.927243);
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
            new Color(63,17,81),

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

}
