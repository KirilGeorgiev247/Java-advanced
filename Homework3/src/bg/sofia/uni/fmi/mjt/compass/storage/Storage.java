package bg.sofia.uni.fmi.mjt.compass.storage;

public interface Storage<K, V> {

    boolean has(K key);

    V get(K key);

    void put(K key, V value);
}
