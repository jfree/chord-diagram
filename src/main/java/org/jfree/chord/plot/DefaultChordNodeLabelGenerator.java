package org.jfree.chord.plot;

import org.jfree.chord.data.ChordDataset;

public class DefaultChordNodeLabelGenerator implements ChordNodeLabelGenerator {
    @Override
    public String generateLabel(String nodeKey, ChordDataset dataset) {
        return nodeKey + " : outgoing = " + dataset.getTotalOutflux(nodeKey) + ", incoming = " + dataset.getTotalInflux(nodeKey);
    }
}
