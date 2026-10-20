/**
 * Missions 4 and 5. The slides: a directed graph. Cells are 0 .. n-1, slide i goes from s.from()[i] to s.to()[i]
 * and takes s.time()[i]. There are no slides from a cell to itself, and the exit can always be reached from the start.
 *
 * Ready for you: Graphs.slidesFrom(s)[u] is the list of the slides that leave cell u, and Graphs.slidesInto(s)[v] is the list
 * of the slides that enter cell v (reversed graph). The lists hold slide numbers i; the cell at the other end is s.to()[i]
 * or s.from()[i].
 */
import java.util.*;

public class Zones {

    /**
     * Mission 4. Kosaraju. Returns zoneOf: zoneOf[cell] = number of the zone, numbers 0 .. zones-1.
     * Number the zones in the order in which the second pass finds them. That order is a topological order
     * of the zone graph: every slide between two zones goes from a smaller number to a larger number.
     *
     * Plan: 1) DFS on the slides, write the cells in the order in which they are finished (use a stack, not recursion);
     * 2) go through the cells from the last finished to the first; if a cell has no zone yet, DFS from it on the reversed
     * slides (slidesInto) and give every cell it reaches the next zone number.
     * Is DAG in Main is 1 when the number of zones equals the number of cells (no cycle), so you do not need a separate check.
     */
    public static int[] kosaraju(Input.Slides s) {
        int n = s.n();

        int[][] from = Graphs.slidesFrom(s);
        int[][] into = Graphs.slidesInto(s);

        boolean[] visited = new boolean[n];
        int[] order = new int[n];
        int orderSize = 0;

        // First pass: DFS on the original graph.
        int[] stack = new int[n];
        int[] nextIndex = new int[n];

        for (int start = 0; start < n; start++) {
            if (visited[start]) {
                continue;
            }

            int top = 0;
            stack[top++] = start;
            visited[start] = true;

            while (top > 0) {
                int cell = stack[top - 1];

                if (nextIndex[cell] < from[cell].length) {
                    int slide = from[cell][nextIndex[cell]++];
                    int next = s.to()[slide];

                    if (!visited[next]) {
                        visited[next] = true;
                        stack[top++] = next;
                    }
                } else {
                    order[orderSize++] = cell;
                    top--;
                }
            }
        }

        // Second pass: DFS on the reversed graph.
        int[] zoneOf = new int[n];
        java.util.Arrays.fill(zoneOf, -1);

        int zone = 0;

        for (int i = orderSize - 1; i >= 0; i--) {
            int start = order[i];

            if (zoneOf[start] != -1) {
                continue;
            }

            int top = 0;
            stack[top++] = start;
            zoneOf[start] = zone;

            while (top > 0) {
                int cell = stack[--top];

                for (int slide : into[cell]) {
                    int next = s.from()[slide];

                    if (zoneOf[next] == -1) {
                        zoneOf[next] = zone;
                        stack[top++] = next;
                    }
                }
            }

            zone++;
        }

        return zoneOf;
    }

    /**
     * Mission 5. The graph of zones: the edge X to Y costs the cheapest slide from X to Y, walking inside a zone is free.
     * Go through the zones in the order of their numbers (no priority queue). Returns two numbers:
     * [0] flow stages: the number of zones on the longest chain of zones;
     * [1] the cost of the cheapest route from the zone of cell 0 to the zone of the last cell.
     */
    public static long[] routes(
            Input.Slides s, int[] zoneOf, int zoneCount) {

        @SuppressWarnings("unchecked")
        Map<Integer, Integer>[] edges = new HashMap[zoneCount];

        // Build the graph of zones.
        for (int i = 0; i < s.count(); i++) {
            int fromZone = zoneOf[s.from()[i]];
            int toZone = zoneOf[s.to()[i]];

            // Slides inside the same zone cost nothing.
            if (fromZone == toZone) {
                continue;
            }

            if (edges[fromZone] == null) {
                edges[fromZone] = new HashMap<>();
            }

            int cost = s.time()[i];
            edges[fromZone].merge(
                    toZone, cost, Math::min);
        }

        // Longest chain of zones.
        long[] stages = new long[zoneCount];
        java.util.Arrays.fill(stages, 1);

        // Cheapest route from zone 0.
        long INF = Long.MAX_VALUE / 4;
        long[] cheapest = new long[zoneCount];
        java.util.Arrays.fill(cheapest, INF);
        cheapest[zoneOf[0]] = 0;

        long maxStages = 1;

        // Zone IDs are already in topological order.
        for (int z = 0; z < zoneCount; z++) {
            maxStages = Math.max(maxStages, stages[z]);

            if (edges[z] == null) {
                continue;
            }

            for (Map.Entry<Integer, Integer> edge
                    : edges[z].entrySet()) {

                int next = edge.getKey();
                int cost = edge.getValue();

                stages[next] = Math.max(
                        stages[next], stages[z] + 1);

                if (cheapest[z] != INF) {
                    cheapest[next] = Math.min(
                            cheapest[next], cheapest[z] + cost);
                }
            }
        }

        return new long[]{
                maxStages,
                cheapest[zoneOf[s.n() - 1]]
        };
    }
}
