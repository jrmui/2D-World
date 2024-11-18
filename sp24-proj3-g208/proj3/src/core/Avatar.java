package core;

import tileengine.TETile;
import tileengine.Tileset;
import java.util.Random;

public class Avatar {

    TETile character;
    String mvmtString;

    // current x-position of Avatar
    int currX;

    // current y-position of Avatar
    int currY;
    Posn currPos;
    TETile prevTile;

    /** Constructs an Avatar that a user can move using WASD and interacts with the world
     * through movement.
     *
     */
    public Avatar(TETile[][] world, Random random) {
        this.character = Tileset.AVATAR;
        spawn(world, random);
        this.currPos = new Posn(currX, currY);
    }

    /** Spawns the avatar at a random, valid location within the map,
     * either in a room or a hallway.
     *
     * @param world The 2D array world that avatar is spawning into
     * @param random Random number generator used to generate random coordinates
     *               for the avatar to spawn at
     */
    public void spawn(TETile[][] world, Random random) {
        boolean validLocation = false;
        int x, y;

        while (!validLocation) {
            x = random.nextInt(World.WIDTH);
            y = random.nextInt(World.HEIGHT);

            if (world[x][y].description() == Tileset.FLOOR.description()) {
                prevTile = world[x][y];
                world[x][y] = this.character;
                this.currX = x;
                this.currY = y;
                validLocation = true;
            }
        }
    }

    /** Generates the movement for the avatar given a string of key presses.
     *
     * @param world A TETile[][] array or world that avatar will be moving in
     *//*
    public void movement(Avatar avatar, TETile[][] world) {
        for (char c: mvmtString.toCharArray()) {
            move(avatar, world, c);
        }
    }*/

    /** Moves an Avatar based on key pressed and checks that location is valid,
     * meaning it is within the bounds of the world and is not a wall.
     * @param world TETile[][] array that avatar will move in
     * @param c The movement key that the user presses
     */
    public void move(Avatar avatar, TETile[][] world, char c) {
        int dx = 0, dy = 0;

        if (c == 'W') {
            dy = 1; // moves up 1
        } else if (c == 'S') {
            dy = -1; // moves down 1
        } else if (c == 'A') {
            dx = -1; // moves left 1
        } else if (c == 'D') {
            dx = 1; // moves right 1
        }

        int nextX = getCurrX() + dx;
        int nextY = getCurrY() + dy;
        TETile nextTile = world[nextX][nextY]; // tile that avatar will move to

        if (nextX < World.WIDTH && nextY < World.HEIGHT) {
            if (nextTile.description() == Tileset.FLOOR.description() || nextTile == Tileset.LIGHT) {
                world[getCurrX()][getCurrY()] = prevTile;
                prevTile = world[nextX][nextY];
                world[nextX][nextY] = getCharacter();
                avatar.currX = nextX;
                avatar.currY = nextY;
                currPos = new Posn(nextX, nextY);
            }
        }
    }

    public int getCurrX() {
        return this.currX;
    }

    public int getCurrY() {
        return this.currY;
    }

    public TETile getCharacter() {
        return character;
    }

    public Posn getCurrPos() {
        return currPos;
    }
}
