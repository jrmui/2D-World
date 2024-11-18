package core;

public class Edge implements Comparable<Edge> {
    Room roomA; // from roomA
    Room roomB; // to roomB
    double dist; // distance between roomA and roomB

    public Edge(Room a, Room b, double dist) {
        this.roomA = a;
        this.roomB = b;
        this.dist = dist;
    }

    // compares two Edge objects
    // determined by their distances (shorter distance means edge1 is less than edge2)
    public int compareTo(Edge other) {
        return Double.compare(this.dist, other.dist);
    }

}
