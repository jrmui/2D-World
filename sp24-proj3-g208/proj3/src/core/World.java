package core;
import tileengine.TETile;
import tileengine.Tileset;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

public class World {

    // generates the world based on the seed
    public static final int WIDTH = 80;
    public static final int HEIGHT = 50;

    // Contains the tiles for the board.
    private TETile[][] world;

    // Generates same set of random numbers, passed into RANDOM constructor
    private long SEED;

    // List of all Rooms in a World
    private ArrayList<Room> rooms = new ArrayList<>();

    // List of all Lights in a World
    private ArrayList<LightSource> lights = new ArrayList<>();

    // Use to generate random numbers based on SEED
    Random RANDOM;

    // Avatar that users can use to interact with the world
    Avatar avatar;
    boolean lightOn = true;
    TETile[][] visibleWorld;

    public World(TETile[][] world, long seed) {
        this.world = world;
        this.SEED = seed;
        this.RANDOM = new Random(SEED);
        fillWithNothing(); // fills the world with NOTHING tiles
        generateWorld();
    }

    /** Creates a world filled with a random number of rooms,
     * all of which have randomized widths and heights
     * that are determined by a seed.
     *
     * @return A TETile[][] array populated with randomized, nonoverlapping rooms that have randomized dimensions
     */

    public void generateWorld() {

        Posn topRight = new Posn(WIDTH, HEIGHT);

        int proportionFilled = 40; // world will be filled with at least PROPORTIONFILLED % of FLOOR or WALL tiles
        double curProp = 0;
        while (curProp < proportionFilled) {
            int width = 4 + RANDOM.nextInt(7); // min WIDTH of a room is 4, max is 10
            int height = 4 + RANDOM.nextInt(7); // min HEIGHT of a room is 4, max is 10
            Room oneRoom = Room.generateRoom(width, height, topRight, world, RANDOM, rooms); // generates room in world
            rooms.add(oneRoom);
            curProp = currentProportion(world);
        }
        Room.genAllEdges(rooms, world);
        fillWalls(world);
        this.avatar = new Avatar(world, RANDOM);
        visibleWorld = visibleWorld();
        // generateLights();
    }

    /**
     * Initially fills the given 2D array of tiles with NOTHING tiles.
     */
    // tiles has been transposed
    public void fillWithNothing() {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                world[x][y] = Tileset.NOTHING;
            }
        }
    }

    /**
     * returns the current proportion of the world that is actively being filled with tiles other than NOTHING
     * @param curWorldState
     * @return
     */
    public double currentProportion(TETile[][] curWorldState) {
        double ans = 0;
        int totalArea = curWorldState.length * curWorldState[0].length;
        for (int i = 0; i < curWorldState.length; i++) {
            for (int j = 0; j < curWorldState[0].length; j++) {
                if (curWorldState[i][j] == Tileset.WALL || curWorldState[i][j] == Tileset.FLOOR) {
                    ans += 1;
                }
            }
        }
        return (ans / totalArea) * 100;
    }

    /**
     * fills the rest of the world with walls
     * @param curWorld
     */
    public void fillWalls(TETile[][] curWorld) {
        for (int i = 0; i < curWorld.length; i++) {
            for (int j = 0; j < curWorld[0].length; j++) {
                if (curWorld[i][j] == Tileset.FLOOR) {
                    if (i + 1 < curWorld.length && curWorld[i + 1][j] != Tileset.FLOOR) {
                        curWorld[i + 1][j] = Tileset.WALL;
                    }
                    if (i - 1 >= 0 && curWorld[i - 1][j] != Tileset.FLOOR) {
                        curWorld[i - 1][j] = Tileset.WALL;
                    }
                    if (j + 1 < curWorld[0].length && curWorld[i][j + 1] != Tileset.FLOOR) {
                        curWorld[i][j + 1] = Tileset.WALL;
                    }
                    if (j - 1 >= 0 && curWorld[i][j - 1] != Tileset.FLOOR) {
                        curWorld[i][j - 1] = Tileset.WALL;
                    }
                }
            }
        }
    }

    /**
     * returns a boolean[][] of visible tiles
     * @return
     */
    public TETile[][] visibleWorld() {
        TETile[][] visible = new TETile[World.WIDTH][World.HEIGHT];
        for (int x = 0; x < World.WIDTH; x++) {
            for (int y = 0; y < World.HEIGHT; y++) {
                visible[x][y] = Tileset.NOTHING;
            }
        }
        int fieldLength = 5; // only reach 5 tiles
        int avatarX = this.avatar.getCurrX();
        int avatarY = this.avatar.getCurrY();
        int minX = Math.max(0, avatarX - fieldLength);
        int minY = Math.max(0, avatarY - fieldLength);
        int maxX = Math.min(World.WIDTH - 1, avatarX + fieldLength);
        int maxY = Math.min(World.HEIGHT - 1, avatarY + fieldLength);

        // iterate through the visible range and set to actual world's tiles
        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                visible[x][y] = world[x][y];
            }
        }

        return visible;
    }

    public void generateLights() {
        for (Room room : rooms) {
            LightSource light = new LightSource(room.lowerCord, room.w, room.h, RANDOM);
            lights.add(light);
            world[light.xy.getX()][light.xy.getY()] = Tileset.LIGHT;
            if (light.on) {
                light.toggleOn(world);
            } else {
                light.toggleOff(world);
            }
        }
    }

    public void checkToggle() {
        for (LightSource light : lights) {
            light.toggler(world, avatar, false);
        }
    }


    /** printy
     * just tests the output of the world
     * @param world
     */
    public static void printy(World world) {
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                System.out.print("[" + world.getWorld()[i][j].character() + "] ");
            }
            System.out.println();
        }
    }

    public TETile[][] getWorld() {
        return world;
    }

    public long getSeed() {
        return SEED;
    }

    public Avatar getAvatar() {
        return avatar;
    }

    public boolean getLightOn() {
        return lightOn;
    }

    public TETile[][] getVisibleWorld() {
        return visibleWorld;
    }

    public ArrayList<Room> getRooms() {
        return rooms;
    }
    public ArrayList<LightSource> getLights() {
        return lights;
    }

    // unused functions??
    private TETile randomTile() {
        // The following call to nextInt() uses a bound of 3 (this is not a seed!) so
        // the result is bounded between 0, inclusive, and 3, exclusive. (0, 1, or 2)
        int tileNum = RANDOM.nextInt(2);
        return switch (tileNum) {
            case 0 -> Tileset.WATER;
            default -> Tileset.NOTHING;
        };
    }

    public void fillRest(TETile[][] curWorld) {
        for (int i = 0; i < curWorld.length; i++) {
            for (int j = 0; j < curWorld[0].length; j++) {
                TETile t = curWorld[i][j];
                if (t != Tileset.FLOOR && t != Tileset.WALL && t != Tileset.AVATAR) {
                    curWorld[i][j] = randomTile();
                }
            }
        }
    }

}
