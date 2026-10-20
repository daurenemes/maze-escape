import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Missions 1 and 2. Building the maze out of the walls.
 * A wall k joins the cells w.a()[k] and w.b()[k] and costs w.cost()[k]. Indexes start from 0,
 * the line number in walls.txt is the index + 1. The cells are connected by DisjointSet.
 */
public class Maze {

    /**
     * Mission 1. Take the walls in the order of the file. Break a wall when its two cells are in different sets.
     * Returns the indexes of the broken walls, in the order of breaking.
     */
    public static int[] breakWalls(Input.Walls w) {
        DisjointSet dsu = new DisjointSet(w.rows() * w.cols());
        int[] broken = new int[w.a().length];
        int count = 0;

        for (int i = 0; i < w.a().length; i++) {
            if (dsu.union(w.a()[i], w.b()[i])) {
                broken[count] = i;
                count++;
            }
        }

        return java.util.Arrays.copyOf(broken, count);
    }

    /**
     * Mission 2. Kruskal: the same, but take the walls from the cheapest to the most expensive.
     * Returns the indexes of the broken walls.
     */
    public static int[] cheapestMaze(Input.Walls w) {
        DisjointSet dsu = new DisjointSet(w.rows() * w.cols());

        Integer[] indexes = new Integer[w.a().length];

        for (int i = 0; i < indexes.length; i++) {
            indexes[i] = i;
        }

        java.util.Arrays.sort(indexes, (i, j) -> Long.compare(w.cost()[i], w.cost()[j]));

        int[] broken = new int[indexes.length];
        int count = 0;

        for (int i : indexes) {
            if (dsu.union(w.a()[i], w.b()[i])) {
                broken[count] = i;
                count++;
            }
        }

        return java.util.Arrays.copyOf(broken, count);
    }

    /**
     * Mission 2. Prim with a priority queue. Returns the total cost of the cheapest maze.
     * It must be equal to the total cost of cheapestMaze.
     */
    public static long primCost(Input.Walls w) {
        int n = w.cells();

        List<List<int[]>> graph = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            graph.add(new ArrayList<>());
        }

        for (int i = 0; i < w.count(); i++) {
            int a = w.a()[i];
            int b = w.b()[i];
            int cost = w.cost()[i];

            graph.get(a).add(new int[]{b, cost});
            graph.get(b).add(new int[]{a, cost});
        }

        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(edge -> edge[1]));

        boolean[] visited = new boolean[n];
        pq.offer(new int[]{0, 0});

        long totalCost = 0;
        int visitedCount = 0;

        while (!pq.isEmpty() && visitedCount < n) {
            int[] edge = pq.poll();

            int cell = edge[0];
            int cost = edge[1];

            if  (visited[cell]) {
                continue;
            }

            visited[cell] = true;
            visitedCount++;
            totalCost += cost;

            for (int[] next : graph.get(cell)) {
                if (!visited[next[0]]) {
                    pq.offer(next);
                }
            }
        }

        if (visitedCount != n) {
            throw new IllegalArgumentException("Maze graph is disconnected");
        }

        return totalCost;
    }
}
