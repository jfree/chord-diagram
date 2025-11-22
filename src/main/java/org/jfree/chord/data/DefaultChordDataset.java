package org.jfree.chord.data;

import org.jfree.data.general.AbstractDataset;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultChordDataset extends AbstractDataset implements ChordDataset {

    private final List<Relations> data = new ArrayList<>();

    @Override
    public List<String> getKeys() {
        return data.stream().map(r -> r.sourceKey).sorted().collect(Collectors.toList());
    }

    @Override
    public double getValue(String sourceKey, String destinationKey) {
        return data.stream()
                .filter(r -> sourceKey.equals(r.sourceKey) && destinationKey.equals(r.destinationKey))
                .map(r -> r.value)
                .findAny()
                .orElse(0d);
    }

    public void addEntry(String sourceKey, String destinationKey, double value) {
        data.add(new Relations(sourceKey, destinationKey, value));
    }

    private static record Relations(String sourceKey, String destinationKey, double value) {
    }
}
