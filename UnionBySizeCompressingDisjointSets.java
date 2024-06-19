package disjointsets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UnionBySizeCompressingDisjointSets<T> implements DisjointSets<T> {
    List<Integer> pointers;
    private final HashMap<T, Integer> itemToIndexMap;

    public UnionBySizeCompressingDisjointSets() {
        this.pointers = new ArrayList<>();
        this.itemToIndexMap = new HashMap<>();
    }

    @Override
    public void makeSet(T item) {
        if (itemToIndexMap.containsKey(item)) {
            throw new IllegalArgumentException("Item is already in a set.");
        }
        itemToIndexMap.put(item, pointers.size());
        pointers.add(-1);  // -1 indicates the root of a set of size 1
    }

    @Override
    public int findSet(T item) {
        Integer index = itemToIndexMap.get(item);
        if (index == null) {
            throw new IllegalArgumentException("Item is not in any set.");
        }
        return findSet(index);
    }

    private int findSet(int index) {
        if (pointers.get(index) < 0) {
            return index;
        } else {
            int root = findSet(pointers.get(index));
            pointers.set(index, root);  // Path compression
            return root;
        }
    }

    @Override
    public boolean union(T item1, T item2) {
        Integer index1 = itemToIndexMap.get(item1);
        Integer index2 = itemToIndexMap.get(item2);

        if (index1 == null || index2 == null) {
            throw new IllegalArgumentException("One or both items are not in any set.");
        }

        return unionByIndex(index1, index2);
    }

    private boolean unionByIndex(int index1, int index2) {
        int root1 = findSet(index1);
        int root2 = findSet(index2);

        if (root1 == root2) {
            return false;
        }

        // Union by size
        int size1 = -pointers.get(root1);
        int size2 = -pointers.get(root2);
        if (size1 <= size2) {
            pointers.set(root1, root2);
            pointers.set(root2, -(size1 + size2));
        } else {
            pointers.set(root2, root1);
            pointers.set(root1, -(size1 + size2));
        }

        return true;
    }
}
