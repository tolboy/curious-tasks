package ru.tolboy.ipcounter.container;

/**
 * Container to store a set of integers.
 */
public interface IntContainer {
    /**
     * Add an int number to the container
     *
     * @param number - integer number
     */
    void add(int number);

    /**
     * Count the distinct integer numbers
     *
     * @return count of distinct numbers in the container
     */
    long countDistinct();

    /**
     * Adds all the elements in the specified container to this container
     * if they're not already present (not supported).
     *
     * @param other int container
     */
    default void addAll(IntContainer other) {
        throw new UnsupportedOperationException();
    }
}
