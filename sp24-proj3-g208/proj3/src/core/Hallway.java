package core;

import tileengine.Tileset;
import tileengine.TETile;

import java.util.Set;

public class Hallway {
    public static void generateHallway(TETile[][] world, Room roomA, Room roomB) {
        Posn startPos = roomA.center;
        Posn endPos = roomB.center;

        // hallway starts @ center of roomA
        int startX = startPos.getX();
        int startY = startPos.getY();

        // hallway ends @ center of roomB
        int endX = endPos.getX();
        int endY = endPos.getY();

        // determines dir of hallway (since Integer.compare outputs -1, 0, or 1)
        int dx = Integer.compare(endX, startX);
        int dy = Integer.compare(endY, startY);

        // make horizontal hallway
        int currentX = startX;
        while (currentX != endX) {
            if (world[currentX][startY] != Tileset.FLOOR) {
                world[currentX][startY] = Tileset.FLOOR;
            }
            currentX += dx;
        }

        // make vertical hallway
        int currentY = startY;
        while (currentY != endY) {
            if (world[endX][currentY] != Tileset.FLOOR) {
                world[endX][currentY] = Tileset.FLOOR;
            }
            currentY += dy;
        }
    }

    // add the hallways into the world
    public static void addHallWays(TETile[][] world, Set<Edge> edges) {
        for (Edge edge : edges) {
            generateHallway(world, edge.roomA, edge.roomB);
        }
    }

}

