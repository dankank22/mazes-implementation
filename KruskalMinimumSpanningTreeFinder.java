package graphs.minspantrees;

import disjointsets.DisjointSets;
import disjointsets.QuickFindDisjointSets;
import graphs.BaseEdge;
import graphs.KruskalGraph;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Computes minimum spanning trees using Kruskal's algorithm.
 * @see MinimumSpanningTreeFinder for more documentation.
 */
public class KruskalMinimumSpanningTreeFinder<G extends KruskalGraph<V, E>, V, E extends BaseEdge<V, E>>
    implements MinimumSpanningTreeFinder<G, V, E> {

    protected DisjointSets<V> createDisjointSets() {
        return new QuickFindDisjointSets<>();
        /*
        Disable the line above and enable the one below after you've finished implementing
        your `UnionBySizeCompressingDisjointSets`.
         */
        // return new UnionBySizeCompressingDisjointSets<>();

        /*
        Otherwise, do not change this method.
        We override this during grading to test your code using our correct implementation so that
        you don't lose extra points if your implementation is buggy.
         */
    }

    @Override
    public MinimumSpanningTree<V, E> findMinimumSpanningTree(G graph) {

            DisjointSets<V> disjointSets = createDisjointSets();

            // Initialize a disjoint set for each vertex
            for (V vertex : graph.allVertices()) {
                disjointSets.makeSet(vertex);
            }

            // Handle the case for an empty graph (0 vertices and 0 edges)
            if (graph.allVertices().isEmpty()) {
                return new MinimumSpanningTree.Success<V, E>(new ArrayList<>());
            }

            // Sort all the edges of the graph by weight
            List<E> edges = new ArrayList<>(graph.allEdges());
            edges.sort(Comparator.comparingDouble(E::weight));

            // This will store the edges of the MST
            List<E> mstEdges = new ArrayList<>();

            // Iterate over the sorted edges and add them to the MST if it doesn't form a cycle
            for (E edge : edges) {
                V from = edge.from();
                V to = edge.to();

                // Check if the vertices of the edge are in different components
                if (disjointSets.findSet(from) != disjointSets.findSet(to)) {
                    mstEdges.add(edge); // This edge can be added to the MST
                    disjointSets.union(from, to); // Merge the components in the disjoint set data structure
                }
            }

            // If the number of edges added to the MST equals the number of vertices minus 1, we have an MST
            if (mstEdges.size() == graph.allVertices().size() - 1) {
                return new MinimumSpanningTree.Success<V, E>(mstEdges);
            } else {
                return new MinimumSpanningTree.Failure<V, E>();
            }
        }

}
