package org.jfree.chord.plot;

import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.util.Args;

import java.awt.*;

public class ChordNodeEntity extends ChartEntity {

    /** The source key. */
    private String key;

    /**
     * Creates a new instance.
     *
     * @param key  the node key ({@code null} not permitted).
     * @param area  the outline of the entity ({@code null} not permitted).
     * @param toolTipText  the tool tip text.
     */
    public ChordNodeEntity(String key, Shape area, String toolTipText) {
        super(area, toolTipText);
        Args.nullNotPermitted(key, "key");
        this.key = key;
    }

    /**
     * Creates a new instance.
     *
     * @param area  the outline of the entity ({@code null} not permitted).
     * @param toolTipText  the tool tip text.
     * @param urlText  the URL text.
     */
    public ChordNodeEntity(Shape area, String toolTipText, String urlText) {
        super(area, toolTipText, urlText);
    }

    /**
     * Returns the node key.
     *
     * @return The node key (never {@code null}).
     */
    public String getKey() {
        return this.key;
    }

    /**
     * Returns a string representation of this instance, primarily for
     * debugging purposes.
     *
     * @return A string.
     */
    @Override
    public String toString() {
        return "[ChordSourceEntity: " + this.key + "]";
    }

}