package core;

import java.util.Set;
import java.util.Objects;
import java.util.*;

public class Posn implements Comparable<Posn> {

    private int x;
    private int y;

    // (x,y)

    public Posn(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /** return x value of posn */
    public int getX() {
        return x;
    }

    /** return y value of posn */
    public int getY() {
        return y;
    }

    /** Adds two posn's together */
    public Posn addPosn(Posn p1, Posn p2) {
        return new Posn(p1.getX() + p2.getX(), p1.getY() + p2.getY());
    }

    /** returns a list of the POSNs of the perimter
     * @param start
     * @param w
     * @param h
     * return set
     */
    public static Set<Posn> genPerimeter(Posn start, int w, int h) {
        int startX = start.getX();
        int startY = start.getY();
        Set<Posn> perimeterSet = new TreeSet<>();
        for (int x = 0; x < w; x++) {
            perimeterSet.add(new Posn(startX + x, startY)); // bottom perimeter line
            perimeterSet.add(new Posn(startX + x, startY + h - 1)); // top perimeter line
        }
        for (int y = 0; y < h; y++) {
            perimeterSet.add(new Posn(startX, startY + y)); // left perimeter line
            perimeterSet.add(new Posn(startX + w - 1, startY + y)); // right perimeter line
        }
        return perimeterSet;
    }

    /** returns a list of the POSNs of the actual area inside the perimeter
     * @param start
     * @param w
     * @param h
     * return set
     */
    public static Set<Posn> genAreaInside(Posn start, int w, int h) {
        int startX = start.getX();
        int startY = start.getY();
        Set<Posn> areaInsideSet = new TreeSet<>();
        for (int x = 1; x < w - 1; x++) {
            for (int y = 1; y < h - 1; y++) {
                areaInsideSet.add(new Posn(startX + x, startY + y));
            }
        }
        return areaInsideSet;
    }

    /** equals()
     * checks if equal
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Posn other = (Posn) obj;
        return this.x == other.getX() && this.y == other.getY();
    }


    // Custom comparator for Posn objects for the treeSet
    // compares based on X values first, then Y values
    @Override
    public int compareTo(Posn other) {
        int compareX = Integer.compare(this.x, other.x);
        if (compareX != 0) {
            return compareX; // if the x values aren't equal, return the lesser
            // negatives would indicate this is less than other
            // positive would indicate this is greater than other
        }
        return Integer.compare(this.y, other.y); // otherwise evaluate based on y-values
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public static double distance(Posn p1, Posn p2) {
        int dx = p1.getX() - p2.getX();
        int dy = p1.getY() - p2.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }


    // THIS IS JUST TO TEST THAT GETPERIMETER WORKED
    public static void main(String[] args) {

        int w = 4;
        int h = 4;
        Posn start = new Posn(0, 0);

        Set<Posn> trialPerimeter = genPerimeter(start, w, h);
        Set<Posn> trialAreaInside = genAreaInside(start, w, h);

        for (Posn cur : trialPerimeter) {
            System.out.println("(" + cur.getX() + ", " + cur.getY() + ")");
        }
        System.out.println("\n");
        for (Posn cur : trialAreaInside) {
            System.out.println("(" + cur.getX() + ", " + cur.getY() + ")");
        }
    }

    // for testing purposes
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

}
