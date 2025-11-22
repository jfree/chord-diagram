package org.jfree.chord.plot;

import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.PlotState;
import org.jfree.chart.text.TextUtils;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.chord.data.ChordDataset;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

public class ChordDiagram extends Plot {

    private ChordDataset dataset;
    private Map<String, Paint> sectionPaintMap;
    private ChordNodeLabelGenerator nodeToolTipGenerator;

    private final int ticksPerFullCircle = 50;
    private final Font scaleFont = new Font("SansSerif", Font.PLAIN, 11);
    private final Font labelFont = new Font("SansSerif", Font.PLAIN, 15);

    public ChordDiagram(ChordDataset dataset) {
        this.dataset = dataset;
        this.sectionPaintMap = new HashMap<>();
        this.nodeToolTipGenerator = new DefaultChordNodeLabelGenerator();
    }

    @Override
    public String getPlotType() {
        return "ChordDiagram";
    }

    public void setSectionPaint(String key, Paint paint) {
        // null argument check delegated...
        this.sectionPaintMap.put(key, paint);
        fireChangeEvent();
    }

    public ChordNodeLabelGenerator getNodeToolTipGenerator() {
        return nodeToolTipGenerator;
    }

    public void setNodeToolTipGenerator(ChordNodeLabelGenerator nodeToolTipGenerator) {
        this.nodeToolTipGenerator = nodeToolTipGenerator;
    }

    @Override
    public void draw(Graphics2D g2, Rectangle2D area, Point2D anchor, PlotState parentState, PlotRenderingInfo info) {
        // use default JFreeChart background handling
        drawBackground(g2, area);

        EntityCollection entities = null;
        if (info != null) {
            info.setPlotArea(area);
            entities = info.getOwner().getEntityCollection();
        }

        float r = (float) (Math.min(area.getWidth(), area.getHeight()) / 2 * 0.8);
        double cx = area.getCenterX();
        double cy = area.getCenterY();

        // Calculate total flow of all nodes
        double totalFlow = dataset.getKeys().stream()
                .mapToDouble(k -> dataset.getTotalOutflux(k) + dataset.getTotalInflux(k))
                .sum();

        /*
         * Ordering convention: for each group, draw outflow first, start with the
         * biggest value, when outflow
         */

        double startAngle = 0.0;
        for (String key : dataset.getKeys()) {
            double keyFlow = dataset.getTotalOutflux(key) + dataset.getTotalInflux(key);
            double angle = (keyFlow / totalFlow) * 2 * Math.PI; // arc length in radians
            double endAngle = startAngle + angle;

            var spacing = 2.0 * Math.PI * 0.05 / dataset.getKeys().size();

            // Outer arc (full radius)
            Arc2D outerArc = new Arc2D.Double(
                    cx - r, cy - r, 2 * r, 2 * r,
                    Math.toDegrees(startAngle + spacing),
                    Math.toDegrees(angle - spacing),
                    Arc2D.PIE);

            // Inner arc (smaller radius to mask center)
            double innerR = r * 0.95; // keep 5% thickness
            Arc2D innerArc = new Arc2D.Double(
                    cx - innerR, cy - innerR, 2 * innerR, 2 * innerR,
                    Math.toDegrees(startAngle + spacing),
                    Math.toDegrees(angle - spacing),
                    Arc2D.PIE);

            // Subtract inner from outer
            Area ring = new Area(outerArc);
            ring.subtract(new Area(innerArc));

            var color = this.sectionPaintMap.get(key);
            if (color == null) {
                color = Color.GRAY;
            }
            g2.setPaint(color);
            g2.fill(ring);

            if (entities != null) {
                String toolTipText = null;
                if (nodeToolTipGenerator != null) {
                    toolTipText = nodeToolTipGenerator.generateLabel(key, dataset);
                }
                entities.add(new ChordNodeEntity(key, ring, toolTipText));
            }

            // TODO add a separate entity for the connector and have a tooltip to show the values
            drawLabel(g2, cx, cy, r, startAngle, endAngle, color, key);
            drawScale(g2, cx, cy, r, startAngle, endAngle, spacing, keyFlow);
            startAngle = endAngle;
        }
    }

    private void drawScale(
            Graphics2D g2,
            double cx, double cy, double r,
            double startAngle, double endAngle, double spacing,
            double totalValue) {

        g2.setColor(Color.BLACK);
        var circle = new Arc2D.Double(
                cx - r, cy - r, 2 * r, 2 * r,
                Math.toDegrees(startAngle + spacing),
                Math.toDegrees(endAngle - startAngle - spacing),
                Arc2D.OPEN);
        g2.draw(circle);

        var angleForOneTick = 2 * Math.PI / ticksPerFullCircle;
        var numberOfTicks = (endAngle - startAngle - spacing) / angleForOneTick;
        for (int i = 0; i < numberOfTicks; i++) {
            var a = startAngle + spacing + i * angleForOneTick;
            var x1 = (float) (cx + r * Math.cos(a));
            var y1 = (float) (cy - r * Math.sin(a));
            var x2 = x1 + 0.012 * r * Math.cos(a);
            var y2 = y1 - 0.012 * r * Math.sin(a);
            var line = new Line2D.Double(x1, y1, x2, y2);
            g2.draw(line);

            var value = i / numberOfTicks * totalValue;
            var val = String.format("%2.0f", value);
            var x3 = (float) (x1 + 0.06 * r * Math.cos(a));
            var y3 = (float) (y1 - 0.06 * r * Math.sin(a));
            g2.setFont(scaleFont);
            if (i > 0) {
                TextUtils.drawRotatedString(val, g2, x3, y3, TextAnchor.CENTER, 0.5 * Math.PI - a, TextAnchor.CENTER);
            }
        }
    }

    private void drawLabel(
            Graphics2D g2,
            double cx, double cy, double r,
            double startAngle, double endAngle,
            Paint paint, String label) {
        g2.setPaint(paint);
        var a = 0.5 * (startAngle + endAngle);

        var x = (float) (cx + 1.14 * r * Math.cos(a));
        var y = (float) (cy - 1.14 * r * Math.sin(a));
        g2.setFont(labelFont);
        TextUtils.drawRotatedString(label, g2, x, y, TextAnchor.CENTER, 0.5 * Math.PI - a, TextAnchor.CENTER);
    }
}
