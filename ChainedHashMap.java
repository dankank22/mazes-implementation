package maps;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * @see AbstractIterableMap
 * @see Map
 */
public class ChainedHashMap<K, V> extends AbstractIterableMap<K, V> {
    private static final double DEFAULT_RESIZING_LOAD_FACTOR_THRESHOLD = 0.75;
    private static final int DEFAULT_INITIAL_CHAIN_COUNT = 10;
    private static final int DEFAULT_INITIAL_CHAIN_CAPACITY = 5;

    /*
    Warning:
    You may not rename this field or change its type.
    We will be inspecting it in our secret tests.
     */
    AbstractIterableMap<K, V>[] chains;
    private double resizingLoadFactorThreshold;
    private int initialChainCount;
    private int chainInitialCapacity;

    private int size;

    // You're encouraged to add extra fields (and helper methods) though!

    /**
     * Constructs a new ChainedHashMap with default resizing load factor threshold,
     * default initial chain count, and default initial chain capacity.
     */
    public ChainedHashMap() {
        this(DEFAULT_RESIZING_LOAD_FACTOR_THRESHOLD, DEFAULT_INITIAL_CHAIN_COUNT, DEFAULT_INITIAL_CHAIN_CAPACITY);
    }

    /**
     * Constructs a new ChainedHashMap with the given parameters.
     *
     * @param resizingLoadFactorThreshold the load factor threshold for resizing. When the load factor
     *                                    exceeds this value, the hash table resizes. Must be > 0.
     * @param initialChainCount the initial number of chains for your hash table. Must be > 0.
     * @param chainInitialCapacity the initial capacity of each ArrayMap chain created by the map.
     *                             Must be > 0.
     */
    public ChainedHashMap(double resizingLoadFactorThreshold, int initialChainCount, int chainInitialCapacity) {
        this.resizingLoadFactorThreshold = resizingLoadFactorThreshold;
        this.initialChainCount = initialChainCount;
        this.chainInitialCapacity = chainInitialCapacity;
        this.size = 0;
        this.chains = createArrayOfChains(initialChainCount);

    }

    /**
     * This method will return a new, empty array of the given size that can contain
     * {@code AbstractIterableMap<K, V>} objects.
     *
     * Note that each element in the array will initially be null.
     *
     * Note: You do not need to modify this method.
     * @see ArrayMap createArrayOfEntries method for more background on why we need this method
     */
    @SuppressWarnings("unchecked")
    private AbstractIterableMap<K, V>[] createArrayOfChains(int arraySize) {
        return (AbstractIterableMap<K, V>[]) new AbstractIterableMap[arraySize];
    }

    /**
     * Returns a new chain.
     *
     * This method will be overridden by the grader so that your ChainedHashMap implementation
     * is graded using our solution ArrayMaps.
     *
     * Note: You do not need to modify this method.
     */
    protected AbstractIterableMap<K, V> createChain(int initialSize) {
        return new ArrayMap<>(initialSize);
    }

    @Override
    public V get(Object key) {
        int hashCode = key.hashCode();
        int index = Math.abs(hashCode)% initialChainCount;
        if (key == null || chains[index] == null) {
            return null;
        }
        return chains[index].get(key);
    }

    @Override
    public V put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        if ((double) size/initialChainCount >= resizingLoadFactorThreshold) {
            resize();
        }
        int hashCode = key.hashCode();
        int index = Math.abs(hashCode) % initialChainCount;
        AbstractIterableMap<K, V> currChain = chains[index];
        if (currChain == null) {
            currChain = createChain(chainInitialCapacity);
            chains[index] = currChain;
        }
        if (currChain.get(key) == null) {
            size++;
        }

        if (currChain == null) {
            // Create a new instance of the chain (replace ChainType with the actual type)
            currChain = new ArrayMap<>(initialChainCount);

            // Set the newly created chain back to the map
            chains[index] = currChain;
        }
        return currChain.put(key, value);
    }

    private void resize() {
        initialChainCount = initialChainCount * 2;
        AbstractIterableMap<K, V>[] newMap = createArrayOfChains(initialChainCount);
        for (int i = 0; i < chains.length; i++) {
            if (chains[i] != null) {
                for (Entry<K, V> entry : chains[i]) {
                    int index = Math.abs(entry.getKey().hashCode()) % newMap.length;
                    if (newMap[index] == null) {
                        newMap[index] = createChain(chainInitialCapacity);
                    }
                    newMap[index].put(entry.getKey(), entry.getValue());
                }
            }
        }
        chains = newMap;
    }
    @Override
    public V remove(Object key) {
        if (size == 0 || (!containsKey(key))) {
            return null;
        }
        int hashCode = key.hashCode();
        int index = Math.abs(hashCode)% initialChainCount;
        AbstractIterableMap<K, V> currChain = chains[index];
        if (currChain.isEmpty()) {
            currChain = null;
        }
        V value = currChain.remove(key);
        size--;
        return value;
    }

    @Override
    public void clear() {
        chains = createArrayOfChains(DEFAULT_INITIAL_CHAIN_COUNT);
        size = 0;
    }

    @Override
    public boolean containsKey(Object key) {
        if (key == null) {
            return false;
        }
        int hashCode = key.hashCode();
        int index = Math.abs(hashCode)% initialChainCount;
        AbstractIterableMap<K, V> currChain = chains[index];
        if (currChain == null) {
            return false;
        }
        return currChain.containsKey(key);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        // Note: you won't need to change this method (unless you add more constructor parameters)
        return new ChainedHashMapIterator<>(this.chains);
    }

    /*
    See the assignment webpage for tips and restrictions on implementing this iterator.
     */

    private static class ChainedHashMapIterator<K, V> implements Iterator<Map.Entry<K, V>> {
        private AbstractIterableMap<K, V>[] chains;
        private int currIndex;
        private Iterator<Map.Entry<K, V>> currIterator;


        // You may add more fields and constructor parameters

        public ChainedHashMapIterator(AbstractIterableMap<K, V>[] chains) {
            this.chains = chains;
            this.currIndex = 0;
            getNextIterator();
        }

        @Override
        public boolean hasNext() {
            return currIndex < chains.length;
        }

        @Override
        public Map.Entry<K, V> next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more elements");
            }
            Map.Entry<K, V> nextKeyValue = currIterator.next();
            if (!currIterator.hasNext()) {
                currIndex++;
                getNextIterator();
            }
            return nextKeyValue;
        }

        private void getNextIterator() {
            while (currIndex < chains.length && (chains[currIndex] == null || chains[currIndex].isEmpty())) {
                currIndex++;
            }
            if (hasNext()) {
                currIterator = chains[currIndex].iterator();
            }
            else {
                currIterator = null;
            }
        }
    }
}
