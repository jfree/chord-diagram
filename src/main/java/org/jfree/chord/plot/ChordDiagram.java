package org.jfree.chord.plot;

import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.PlotState;
import org.jfree.chart.text.TextUtils;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.chord.data.ChordDataset;

import java.awt.*;
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

            var spacing = 2.0 * Math.PI * 0.025 / dataset.getKeys().size();

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

            {
                g2.setPaint(color);
                var a = 0.5 * (startAngle + endAngle);

                var x = (float) (cx + 1.15 * r * Math.cos(a));
                var y = (float) (cy - 1.15 * r * Math.sin(a));
                TextUtils.drawRotatedString(key, g2, x, y, TextAnchor.CENTER, 0.5 * Math.PI - a, TextAnchor.CENTER);
            }

            {
                g2.setColor(Color.BLACK);
                var circle = new Arc2D.Double(
                        cx - r, cy - r, 2 * r, 2 * r,
                        Math.toDegrees(startAngle + spacing),
                        Math.toDegrees(angle - spacing),
                        Arc2D.OPEN);
                g2.draw(circle);
                for (int i = 0; i <= 5; i++) {
                    var a = startAngle + spacing + 0.2 * i * (endAngle - startAngle - spacing);
                    var x1 = (float) (cx + r * Math.cos(a));
                    var y1 = (float) (cy - r * Math.sin(a));
                    var x2 = x1 + 0.03 * r * Math.cos(a);
                    var y2 = y1 - 0.03 * r * Math.sin(a);
                    var line = new Line2D.Double(x1, y1, x2, y2);
                    g2.draw(line);
                }
            }

            startAngle = endAngle;
        }
    }
}
