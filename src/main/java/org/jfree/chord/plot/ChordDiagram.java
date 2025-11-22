package org.jfree.chord.plot;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.PlotState;
import org.jfree.chord.data.ChordDataset;

public class ChordDiagram extends Plot{

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

        double r = Math.min(area.getWidth(), area.getHeight()) / 2 * 0.8;
        double cx = area.getCenterX();
        double cy = area.getCenterY();

        Shape circle = new Ellipse2D.Double(cx - r, cy - r, 2*r, 2*r);

        g2.setColor(Color.LIGHT_GRAY);
        g2.fill(circle);
    }
}
