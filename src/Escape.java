import java.util.List;

/**
 * Mission 3. Escape: DFS from cell 0 to the last cell (rows * cols - 1) in the maze of Mission 1.
 * Two neighbouring cells are connected when the wall between them is broken.
 * The neighbours are tried in this order: up, right, down, left (cell - cols, cell + 1, cell + cols, cell - 1).
 * The ready method Graphs.open(w, broken) gives you the open neighbours of every cell, in no particular order:
 * to visit them in the required order, go through the four cells above one by one and take those that are in the list.
 * The program must not overflow the stack on a maze of 1000 by 1000 cells.
 */
public class Escape {

    /**
     * path: the cells of the way out, from 0 to the last cell.
     * visited: how many cells are marked as visited at the moment the exit is marked (the start and the exit are counted).
     * deadEnds: how many cells of the maze have exactly one open neighbour.
     */
    public record Result(int[] path, int visited, int deadEnds) {}

    public static Result escape(Input.Walls w, int[] broken) {
        int cols = w.cols();
        int n = w.cells();
        int exit = n - 1;

        int[][] graph = Graphs.open(w, broken);

        boolean[] visited = new boolean[n];
        int[] parent = new int[n];
        java.util.Arrays.fill(parent, -1);

        // Each stack entry stores a cell.
        int[] stack = new int[n];
        int[] nextDirection = new int[n];
        int top = 0;

        stack[top++] = 0;
        visited[0] = true;
        int visitedCount = 1;

        int deadEnds = 0;
        for (int cell = 0; cell < n; cell++) {
            if (graph[cell].length == 1) {
                deadEnds++;
            }
        }

        // Up, right, down, left.
        int[] dr = {-1, 0, 1, 0};
        int[] dc = {0, 1, 0, -1};

        while (top > 0) {
            int cell = stack[top - 1];

            if (cell == exit) {
                break;
            }

            if (nextDirection[cell] == 4) {
                top--;
                continue;
            }

            int direction = nextDirection[cell]++;
            int row = cell / cols;
            int col = cell % cols;

            int nr = row + dr[direction];
            int nc = col + dc[direction];

            if (nr < 0 || nr >= w.rows() || nc < 0 || nc >= cols) {
                continue;
            }

            int next = nr * cols + nc;

            if (visited[next]) {
                continue;
            }

            boolean connected = false;
            for (int neighbour : graph[cell]) {
                if (neighbour == next) {
                    connected = true;
                    break;
                }
            }

            if (!connected) {
                continue;
            }

            visited[next] = true;
            visitedCount++;
            parent[next] = cell;
            stack[top++] = next;
        }

        if (!visited[exit]) {
            throw new IllegalStateException("Exit is unreachable");
        }

        int length = 0;
        for (int cell = exit; cell != -1; cell = parent[cell]) {
            length++;
        }

        int[] path = new int[length];
        int cell = exit;

        for (int i = length - 1; i >= 0; i--) {
            path[i] = cell;
            cell = parent[cell];
        }

        return new Result(path, visitedCount, deadEnds);
    }
}
