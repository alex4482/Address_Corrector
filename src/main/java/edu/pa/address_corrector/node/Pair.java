package edu.pa.address_corrector.node;

public class Pair<T, K> {
    private final T key;
    private final K value;

    public Pair(T newKey, K newValue) {
        key = newKey;
        value = newValue;
    }

    public K getValue() {
        return value;
    }

    public T getKey() {
        return key;
    }
}
