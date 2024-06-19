package mazes.logic.carvers;

import graphs.EdgeWithData;
import graphs.minspantrees.MinimumSpanningTreeFinder;
import mazes.entities.Room;
import mazes.entities.Wall;
import mazes.logic.MazeGraph;
import graphs.minspantrees.MinimumSpanningTree;
import graphs.minspantrees.KruskalMinimumSpanningTreeFinder;
import java.util.Random;
import java.util.Set;
import java.util.Collection;
import java.util.HashSet;
/**
 * Carves out a maze based on Kruskal's algorithm.
 */
public class KruskalMazeCarver extends MazeCarver {
    MinimumSpanningTreeFinder<MazeGraph, Room, EdgeWithData<Room, Wall>> minimumSpanningTreeFinder;
    private final Random rand;

    public KruskalMazeCarver(MinimumSpanningTreeFinder
                                 <MazeGraph, Room, EdgeWithData<Room, Wall>> minimumSpanningTreeFinder) {
        this.minimumSpanningTreeFinder = minimumSpanningTreeFinder;
        this.rand = new Random();
    }

    public KruskalMazeCarver(MinimumSpanningTreeFinder
                                 <MazeGraph, Room, EdgeWithData<Room, Wall>> minimumSpanningTreeFinder,
                             long seed) {
        this.minimumSpanningTreeFinder = minimumSpanningTreeFinder;
        this.rand = new Random(seed);
    }

    @Override
    protected Set<Wall> chooseWallsToRemove(Set<Wall> walls) {

        Collection<EdgeWithData<Room, Wall>> edgesWithData = new HashSet<>();

        // Add all walls as edges with random weights
        for (Wall wall : walls) {
            double weight = rand.nextDouble(); // assign a random weight
            edgesWithData.add(new EdgeWithData<>(wall.getRoom1(), wall.getRoom2(), weight, wall));
        }

        // Create a new MazeGraph with the edges
        MazeGraph mazeGraph = new MazeGraph(edgesWithData);

        // Find the minimum spanning tree using Kruskal's algorithm
        KruskalMinimumSpanningTreeFinder<MazeGraph, Room, EdgeWithData<Room, Wall>> finder =
            new KruskalMinimumSpanningTreeFinder<>();

        MinimumSpanningTree<Room, EdgeWithData<Room, Wall>> mst = finder.findMinimumSpanningTree(mazeGraph);

        // Extract the walls from the MST
        Set<Wall> wallsToRemove = new HashSet<>();
        if (mst.exists()) {
            for (EdgeWithData<Room, Wall> edge : mst.edges()) {
                wallsToRemove.add(edge.data());
            }
        } else {
            throw new IllegalStateException("Maze graph is not connected, no MST exists!");
        }

        return wallsToRemove;
    }
}
