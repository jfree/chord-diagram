package org.jfree.chord.plot;

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

import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.PlotState;
import org.jfree.chart.text.TextUtils;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.chord.data.ChordDataset;

public class ChordDiagram extends Plot {

    private static class ArcSegment {
        double startAngle;
        double endAngle;
        double value;
        String type; // "outflow" or "inflow"
        String targetGroup; // outflow target or inflow source

        // constructor
        ArcSegment(double startAngle, double endAngle, double value, String type, String targetGroup) {
            this.startAngle = startAngle;
            this.endAngle = endAngle;
            this.value = value;
            this.type = type;
            this.targetGroup = targetGroup;
        }
    }

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
        double innerR = r * 0.95;
        var spacing = 2.0 * Math.PI * 0.05 / dataset.getKeys().size();

        // Calculate total flow of all nodes
        double totalFlow = dataset.getKeys().stream()
                .mapToDouble(k -> dataset.getTotalOutflux(k) + dataset.getTotalInflux(k))
                .sum();

        // store segments for debug markers / flows
        Map<String, java.util.List<ArcSegment>> groupSegments = new HashMap<>();

        /*
         * Ordering convention: for each group, draw outflow first, start with the
         * biggest value, when outflow
         */

        double startAngle = 0.0;
        for (String key : dataset.getKeys()) {
            double keyFlow = dataset.getTotalOutflux(key) + dataset.getTotalInflux(key);
            double angle = (keyFlow / totalFlow) * 2 * Math.PI; // arc length in radians
            double endAngle = startAngle + angle;

            // Outer arc (full radius)
            Arc2D outerArc = new Arc2D.Double(
                    cx - r, cy - r, 2 * r, 2 * r,
                    Math.toDegrees(startAngle + spacing),
                    Math.toDegrees(angle - spacing),
                    Arc2D.PIE);

            // Inner arc (smaller radius to mask center)
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

            // TODO add a separate entity for the connector and have a tooltip to show the
            // values
            drawLabel(g2, cx, cy, r, startAngle, endAngle, color, key);
            drawScale(g2, cx, cy, r, startAngle, endAngle, spacing, keyFlow);

            // --- Step1.5: compute segments aligned with ring ---
            java.util.List<ArcSegment> segments = new java.util.ArrayList<>();
            double cursor = startAngle + spacing / 2; // shift cursor by half spacing

            // outflow segments
            for (String target : dataset.getKeys()) {
                double val = dataset.getValue(key, target);
                if (val <= 0)
                    continue;
                double segAngle = val / keyFlow * (angle - spacing); // subtract spacing from total angle
                ArcSegment seg = new ArcSegment(cursor, cursor + segAngle, val, "outflow", target);
                segments.add(seg);
                cursor += segAngle;
            }

            // inflow segments
            for (String source : dataset.getKeys()) {
                double val = dataset.getValue(source, key);
                if (val <= 0)
                    continue;
                double segAngle = val / keyFlow * (angle - spacing);
                ArcSegment seg = new ArcSegment(cursor, cursor + segAngle, val, "inflow", source);
                segments.add(seg);
                cursor += segAngle;
            }

            groupSegments.put(key, segments);
            startAngle = endAngle;
        }
        // draw debug marker
        double markerR = r + 10;
        for (java.util.List<ArcSegment> segments : groupSegments.values()) {
            for (ArcSegment seg : segments) {
                double mid = (seg.startAngle + seg.endAngle) / 2;
                // now mid already includes spacing offset
                double mx = cx + markerR * Math.cos(mid);
                double my = cy - markerR * Math.sin(mid);
                g2.setColor(seg.type.equals("outflow") ? Color.RED : Color.BLUE);
                g2.fillOval((int) (mx - 2), (int) (my - 2), 4, 4);
            }
        }

        // --- Step 2: draw flows between outflow and inflow with source group color ---
        double flowInnerR = innerR; // start/end radius inside the ring
        for (String sourceGroup : groupSegments.keySet()) {
            java.util.List<ArcSegment> segments = groupSegments.get(sourceGroup);
            for (ArcSegment seg : segments) {
                if (!seg.type.equals("outflow"))
                    continue; // only outflow

                String targetGroup = seg.targetGroup;
                java.util.List<ArcSegment> targetSegments = groupSegments.get(targetGroup);
                if (targetSegments == null)
                    continue;

                // find corresponding inflow segment
                ArcSegment inflowSeg = null;
                for (ArcSegment tSeg : targetSegments) {
                    if (tSeg.type.equals("inflow") && tSeg.targetGroup.equals(sourceGroup)) {
                        inflowSeg = tSeg;
                        break;
                    }
                }
                if (inflowSeg == null)
                    continue;

                // compute mid points
                double midOut = (seg.startAngle + seg.endAngle) / 2;
                double midIn = (inflowSeg.startAngle + inflowSeg.endAngle) / 2;

                double xOut = cx + flowInnerR * Math.cos(midOut);
                double yOut = cy - flowInnerR * Math.sin(midOut);
                double xIn = cx + flowInnerR * Math.cos(midIn);
                double yIn = cy - flowInnerR * Math.sin(midIn);

                // draw curve
                double ctrlX = cx;
                double ctrlY = cy;

                Paint sourceColor = sectionPaintMap.getOrDefault(sourceGroup, Color.GRAY);
                Color strokeColor;
                if (sourceColor instanceof Color) {
                    Color c = (Color) sourceColor;
                    strokeColor = new Color(c.getRed(), c.getGreen(), c.getBlue(), 120); // semi-transparent
                } else {
                    strokeColor = new Color(128, 128, 128, 120);
                }
                g2.setColor(strokeColor);
                g2.setStroke(new java.awt.BasicStroke((float) Math.max(1.0, seg.value / 10.0f))); // optional: thickness
                                                                                                  // by value

                java.awt.geom.QuadCurve2D curve = new java.awt.geom.QuadCurve2D.Double();
                curve.setCurve(xOut, yOut, ctrlX, ctrlY, xIn, yIn);
                g2.draw(curve);
            }
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
