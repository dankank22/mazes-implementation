package graphs.shortestpaths;

import graphs.BaseEdge;
import graphs.Graph;
import priorityqueues.ExtrinsicMinPQ;
import priorityqueues.DoubleMapMinPQ;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DijkstraShortestPathFinder<G extends Graph<V, E>, V, E extends BaseEdge<V, E>>
    extends SPTShortestPathFinder<G, V, E> {

    protected <T> ExtrinsicMinPQ<T> createMinPQ() {
        return new DoubleMapMinPQ<>();
    }

    @Override
    protected Map<V, E> constructShortestPathsTree(G graph, V start, V end) {
        Map<V, E> edgeTo = new HashMap<>();
        Map<V, Double> distTo = new HashMap<>();
        ExtrinsicMinPQ<V> pq = createMinPQ();

        // Initialize only the starting vertex
        distTo.put(start, 0.0);
        pq.add(start, 0.0);

        // Process the graph
        while (!pq.isEmpty()) {
            V current = pq.removeMin();
            // Early exit if we reached the end
            if (current.equals(end)) {
                break;
            }

            for (E edge : graph.outgoingEdgesFrom(current)) {
                V next = edge.to();
                double newDist = distTo.get(current) + edge.weight();
                // Initialize distances for vertices as we encounter them
                if (!distTo.containsKey(next) || newDist < distTo.get(next)) {
                    distTo.put(next, newDist);
                    edgeTo.put(next, edge);

                    if (pq.contains(next)) {
                        pq.changePriority(next, newDist);
                    } else {
                        pq.add(next, newDist);
                    }
                }
            }
        }

        return edgeTo;
    }

    @Override
    protected ShortestPath<V, E> extractShortestPath(Map<V, E> spt, V start, V end) {

        if (start.equals(end)) {
            return new ShortestPath.SingleVertex<>(start);
        }
        if (!spt.containsKey(end)) {
            return new ShortestPath.Failure<>();
        }

        List<E> path = new ArrayList<>();
        for (V at = end; at != null && !at.equals(start); at = spt.get(at).from()) {
            E edge = spt.get(at);
            path.add(0, edge); // add at beginning
        }

        // Handle the case of start == end
        if (path.isEmpty() && start.equals(end)) {
            return new ShortestPath.SingleVertex<>(start);
        }

        return new ShortestPath.Success<>(path);
    }
}
