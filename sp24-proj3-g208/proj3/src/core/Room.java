package core;

import tileengine.TETile;
import tileengine.Tileset;

import java.util.*;

public class Room {
    Posn lowerCord; // (x,y) represents lowerCord
    // public TETile[][] area; // as in the area of ONE specific ROOM
    int w, h;
    int ID;
    Posn center;
    // ArrayList<Room> rooms = new ArrayList<>(); // list of all Rooms in a World

    public Room(Posn position, int w, int h, int id) {
        this.lowerCord = position;
        this.w = w;
        this.h = h;
        this.ID = id;

        // find the center of a room
        int centerX = this.lowerCord.getX() + this.w / 2;
        int centerY = this.lowerCord.getY() + this.h / 2;
        this.center = new Posn(centerX, centerY);
    }

    public static Room generateRoom(int w, int h, Posn maxDim, TETile[][] world, Random random, ArrayList<Room> rooms) {

        boolean addedNewRoom = false;
        Room ans = null;
        while (!addedNewRoom) {
            // subtract w and h to make sure the new room is in bound
            int randX = random.nextInt(maxDim.getX() - w); //
            int randY = random.nextInt(maxDim.getY() - h); //
            Posn lowerCorner = new Posn(randX, randY); // create new POSN for lower-left corner

            // create a room object
            Room room = new Room(lowerCorner, w, h, rooms.size());

            // if it's valid
            if (!overlap(room, world)) {
                // currently takes in the actual world, and checks if the tile is empty, or actually just overrides it
                genArea(world, room.lowerCord, room.w, room.h);
                ans = room;
                addedNewRoom = true; // successfully added ONE room!
            }
        }
        return ans;
    }

    /**
     * given a certain room and the world,
     * returns true if the room overlaps with a room in the world,
     * and returns false if it fits in the world
     * @param room
     * @param world
     * @return True if room overlaps with an existing room and false if it doesn't
     */

    public static boolean overlap(Room room, TETile[][] world) {
        Set<Posn> perimeter = Posn.genPerimeter(room.lowerCord, room.w, room.h);
        for (Posn curPosn : perimeter) {
            TETile curTile = world[curPosn.getX()][curPosn.getY()];
            if (curTile == Tileset.FLOOR || curTile == Tileset.WALL) {
                return true;
            }
        }
        return false;
    }

    /**
     * Generates a RECTANGULAR room with WIDTH w and HEIGHT h
     * @param w Width of the room
     * @param h Height of the room
     */
    public static void genArea(TETile[][] world, Posn start, int w, int h) {
        Set<Posn> areaInside = Posn.genAreaInside(start, w, h);
        for (Posn curAreaPosn : areaInside) {
            world[curAreaPosn.getX()][curAreaPosn.getY()] = Tileset.FLOOR;
        }

        Set<Posn> perimeter = Posn.genPerimeter(start, w, h);
        for (Posn curPerPosn : perimeter) {
            world[curPerPosn.getX()][curPerPosn.getY()] = Tileset.WALL;
        }
    }

    public static void genAllEdges(ArrayList<Room> rooms, TETile[][] world) {
        TreeSet<Edge> edges = new TreeSet<>();

        // find and add edge between every room --> will create n(n - 1)/2 pairs
        for (int i = 0; i < rooms.size(); i++) {
            for (int j = i + 1; j < rooms.size(); j++) {
                Room room1 = rooms.get(i);
                Room room2 = rooms.get(j);
                double dist = Posn.distance(room1.center, room2.center);
                Edge edgeBetweenr1r2 = new Edge(room1, room2, dist);
                edges.add(edgeBetweenr1r2);
            }
        }

        // create the disjoint set in order to keep track of cycles
        // this creates the MST (like percolation idea)
        int[] parents = new int[rooms.size()];
        for (int i = 0; i < rooms.size(); i++) {
            parents[i] = i; // currently all parents are set to itself
        }

        // TreeSet<Edge> mst = new TreeSet<>();
        int curNumEdges = 0;
        while (curNumEdges < rooms.size() - 1) {
            for (Edge edge : edges) {
                int r1 = find(parents, edge.roomA.ID); // ultimate room that room1 is connected to
                int r2 = find(parents, edge.roomB.ID); // ultimate room that room2 is connected to
                if (r1 != r2) { // if roomA and roomB currently aren't connected in a cycle
                    // mst.add(edge);
                    union(parents, edge.roomA.ID, edge.roomB.ID);
                    Hallway.generateHallway(world, edge.roomA, edge.roomB);
                    curNumEdges++;
                }
            }
        }
        // return mst;
    }

    /**
     * takes a disjoing set (list) that stores each room's parent (or the room it ultimately connects to),
     *       the first room added into the list will be the root room
     * i is the index of the room itself
     * @param parents
     * @param i
     * @return
     */
    private static int find(int[] parents, int i) {
        if (parents[i] != i) { // if the parent of the room is not currently itself (that means it has a parent)
            // , implement path compression and keep going until we find the room's parent
            parents[i] = find(parents, parents[i]); // path compression
        }
        return parents[i]; // there is no parent
    }

    /**
     * union just connects the two together
     * @param parent
     * @param i
     * @param j
     */
    private static void union(int[] parent, int i, int j) {
        int root1 = find(parent, i);
        int root2 = find(parent, j);
        parent[root2] = root1;
    }

    // TO-DO: if there are adjacent rooms, merge the walls and create a bigger room
}

