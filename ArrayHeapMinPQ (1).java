package priorityqueues;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import maps.ChainedHashMap;

/**
 * @see ExtrinsicMinPQ
 */
public class ArrayHeapMinPQ<T> implements ExtrinsicMinPQ<T> {
    static final int START_INDEX = 1;
    List<PriorityNode<T>> items;
    int heapSize;

    // New field to track the positions of items in the heap
    private ChainedHashMap<T, Integer> itemPositions;

    public ArrayHeapMinPQ() {
        items = new ArrayList<>();
        items.add(null); // Dummy node
        heapSize = 0;
        itemPositions = new ChainedHashMap<>();
    }

    private void swap(int a, int b) {
        PriorityNode<T> nodeA = items.get(a);
        PriorityNode<T> nodeB = items.get(b);

        items.set(a, nodeB);
        items.set(b, nodeA);

        // Update positions in the map
        itemPositions.put(nodeA.getItem(), b);
        itemPositions.put(nodeB.getItem(), a);
    }

    @Override
    public void add(T item, double priority) {
        if (contains(item)) {
            throw new IllegalArgumentException("This item exists!");
        }
        PriorityNode<T> newNode = new PriorityNode<>(item, priority);
        items.add(newNode);
        heapSize++;
        int addedIndex = items.size() - 1;
        itemPositions.put(item, addedIndex); // Map the item to its position
        percolateUp(addedIndex);
    }

    private void percolateUp(int index) {
        while (index > START_INDEX) {
            int parentIndex = index / 2;
            if (items.get(parentIndex).getPriority() <= items.get(index).getPriority()) {
                break;
            }
            swap(parentIndex, index);
            index = parentIndex;
        }
    }

    private void percolateDown(int index) {
        while (2 * index < items.size()) {
            int leftChildIndex = 2 * index;
            int rightChildIndex = leftChildIndex + 1;
            int smallest = leftChildIndex;

            if (rightChildIndex < items.size() && items.get(rightChildIndex).getPriority() < items.get(leftChildIndex).getPriority()) {
                smallest = rightChildIndex;
            }

            if (items.get(index).getPriority() <= items.get(smallest).getPriority()) {
                break;
            }
            swap(index, smallest);
            index = smallest;
        }
    }

    @Override
    public boolean contains(T item) {
        return itemPositions.containsKey(item);
    }

    @Override
    public T peekMin() {
        if (items.size() > START_INDEX) {
            return items.get(START_INDEX).getItem();
        }
        throw new NoSuchElementException("Priority Queue is empty.");
    }

    @Override
    public T removeMin() {
        if (items.size() == START_INDEX) {
            throw new NoSuchElementException("Priority Queue is empty.");
        }
        T minItem = items.get(START_INDEX).getItem();
        itemPositions.remove(minItem); // Remove from map

        items.set(START_INDEX, items.get(items.size() - 1));
        itemPositions.put(items.get(START_INDEX).getItem(), START_INDEX); // Update new root position
        items.remove(items.size() - 1);
        heapSize--;
        if (items.size() > START_INDEX) {
            percolateDown(START_INDEX);
        }
        return minItem;
    }

    @Override
    public void changePriority(T item, double priority) {
        if (!itemPositions.containsKey(item)) {
            throw new NoSuchElementException("Item not found in Priority Queue.");
        }
        int index = itemPositions.get(item);
        double oldPriority = items.get(index).getPriority();
        items.get(index).setPriority(priority);
        if (priority < oldPriority) {
            percolateUp(index);
        } else {
            percolateDown(index);
        }
    }

    @Override
    public int size() {
        return heapSize;
    }
}

