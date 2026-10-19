/**
 * Checks of Mission 0. Run:  java -cp out DisjointSetTest
 * Add your own checks below: a large chain, a union of a set with itself, count after many unions.
 */
public class DisjointSetTest {

    static int failed = 0;

    static void check(boolean ok, String what) {
        System.out.println((ok ? "ok    " : "FAIL  ") + what);
        if (!ok) failed++;
    }

    public static void main(String[] args) {
        DisjointSet d = new DisjointSet(6);
        check(d.count() == 6, "6 sets at the start");
        check(!d.connected(0, 1), "0 and 1 are apart at the start");
        check(d.union(0, 1), "union(0, 1) joins two sets");
        check(d.connected(0, 1), "0 and 1 are together");
        check(!d.union(1, 0), "union(1, 0) again returns false");
        check(d.count() == 5, "5 sets after one union");
        d.union(2, 3); d.union(1, 3);
        check(d.connected(0, 2), "0 and 2 are together through 1 and 3");
        check(d.count() == 3, "3 sets: {0,1,2,3} {4} {5}");
        check(d.find(4) == 4 && d.find(5) == 5, "a single cell is its own root");

        // Union of an element with itself.
        DisjointSet self = new DisjointSet(3);
        check(!self.union(1, 1), "union of an element with itself");
        check(self.count() == 3, "self-union does not change count");

        // Count after many unions.
        DisjointSet many = new DisjointSet(8);
        many.union(0, 1);
        many.union(2, 3);
        many.union(4, 5);
        many.union(6, 7);
        check(many.count() == 4, "four sets after four unions");

        many.union(0, 2);
        many.union(4, 6);
        check(many.count() == 2, "two sets after six unions");

        many.union(0, 4);
        check(many.count() == 1, "one set after seven unions");
        check(many.connected(1, 7), "all elements are connected");

        // Find returns the same root for connected elements.
        int root = many.find(0);
        check(many.find(1) == root && many.find(7) == root, "find returns the common root");

        System.out.println(failed == 0 ? "All checks passed." : failed + " checks failed.");
    }
}
