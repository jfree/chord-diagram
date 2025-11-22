package org.jfree.chord.data;

import org.jfree.data.general.Dataset;

import java.util.List;

public interface ChordDataset<K extends Comparable<K>> extends Dataset {

    // get a list of keys
    List<K> getKeys();

    // value of the flow from Key1 -> Key2
    double getValue(K sourceKey, K destinationKey);

}
