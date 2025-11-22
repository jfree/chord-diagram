package org.jfree.chord.data;

import org.jfree.data.general.Dataset;

import java.util.List;

public interface ChordDataset extends Dataset {

    // get a list of keys
    List<String> getKeys();

    // value of the flow from Key1 -> Key2
    double getValue(String sourceKey, String destinationKey);

    default double getTotalInflux(String destinationKey) {
        return getKeys().stream()
                .map(sourceKey -> getValue(sourceKey, destinationKey))
                .reduce(0d, Double::sum);
    }

    default double getTotalOutflux(String sourceKey) {
        return getKeys().stream()
                .map(destinationKey -> getValue(sourceKey, destinationKey))
                .reduce(0d, Double::sum);
    }
}
