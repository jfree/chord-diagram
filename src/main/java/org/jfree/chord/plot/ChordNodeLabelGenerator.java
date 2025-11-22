package org.jfree.chord.plot;

import org.jfree.chord.data.ChordDataset;

public interface ChordNodeLabelGenerator {

    String generateLabel(String nodeKey, ChordDataset dataset);
}
