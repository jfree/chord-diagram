package org.jfree.chord.data;

import org.jfree.data.general.AbstractDataset;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChordDatasetTest {

    @Test
    void totalFlux() {
        var dataset = new TestChordDataset();
        assertAll(
                () -> assertEquals(7d, dataset.getTotalInflux("k1")),
                () -> assertEquals(16d, dataset.getTotalInflux("k2")),
                () -> assertEquals(5d, dataset.getTotalOutflux("k1")),
                () -> assertEquals(18d, dataset.getTotalOutflux("k2"))
        );
    }

    public static class TestChordDataset extends AbstractDataset implements ChordDataset {
        @Override
        public List<String> getKeys() {
            return List.of("k1", "k2");
        }

        @Override
        public double getValue(String sourceKey, String destinationKey) {
            var result = Map.of("k1k1", 1d, "k1k2", 4d, "k2k1", 6d, "k2k2", 12d);
            return result.get(sourceKey + destinationKey);
        }
    }
}
