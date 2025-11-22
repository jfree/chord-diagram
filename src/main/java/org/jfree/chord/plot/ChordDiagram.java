package org.jfree.chord.plot;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.PlotState;
import org.jfree.chord.data.ChordDataset;

public class ChordDiagram extends Plot {

    private ChordDataset dataset;

    public ChordDiagram(ChordDataset dataset) {
        this.dataset = dataset;
    }

    @Override
    public String getPlotType() {
        return "ChordDiagram";
    }

    @Override
    public void draw(Graphics2D g2, Rectangle2D area, Point2D anchor, PlotState parentState, PlotRenderingInfo info) {
        // use default JFreeChart background handling
        drawBackground(g2, area);
        g2.setColor(Color.LIGHT_GRAY);

        double r = Math.min(area.getWidth(), area.getHeight()) / 2 * 0.8;
        double cx = area.getCenterX();
        double cy = area.getCenterY();

        // Calculate total flow of all nodes
        double totalFlow = dataset.getKeys().stream()
                .mapToDouble(k -> dataset.getTotalOutflux(k) + dataset.getTotalInflux(k))
                .sum();

        double startAngle = 0.0;
        for (String key : dataset.getKeys()) {
            double keyFlow = dataset.getTotalOutflux(key) + dataset.getTotalInflux(key);
            double angle = (keyFlow / totalFlow) * 2 * Math.PI; // arc length in radians
            double endAngle = startAngle + angle;

            Arc2D arc = new Arc2D.Double(
                    cx - r, cy - r, 2 * r, 2 * r,
                    Math.toDegrees(startAngle),
                    Math.toDegrees(angle),
                    Arc2D.PIE);

            g2.fill(arc);
        }
    }
}
