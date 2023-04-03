package ru.tolboy.ipcounter.container;

import java.util.BitSet;

/**
 * An implementation of {@link IntContainer} that uses two {@link BitSet} for storing of int numbers.
 */
public class BitSetContainer implements IntContainer {
    private final BitSet positive = new BitSet(Integer.MAX_VALUE);
    private final BitSet negative = new BitSet(Integer.MAX_VALUE);

    @Override
    public void add(int i) {
        if (i >= 0) {
            positive.set(i);
        } else {
            negative.set(~i);
        }
    }

    @Override
    public long countDistinct() {
        return (long) positive.cardinality() + negative.cardinality();
    }
}

