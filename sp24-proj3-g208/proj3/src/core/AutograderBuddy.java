package core;

import tileengine.TETile;
import tileengine.Tileset;

// import utils.FileUtils;

public class AutograderBuddy {

    private static final String SAVE_FILE = "save.txt";

    /**
     * Simulates a game, but doesn't render anything or call any StdDraw
     * methods. Instead, returns the world that would result if the input string
     * had been typed on the keyboard.
     * <p>
     * Recall that strings ending in ":q" should cause the game to quit and
     * save. To "quit" in this method, save the game to a file, then just return
     * the TETile[][]. Do not call System.exit(0) in this method.
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public static TETile[][] getWorldFromInput(String input) {

        // ex: "N543SWWWWDD:Q - seed: 543, move up 4x, move right 2x
        // returns a world as if we went into main and pressed that

        String seedString = ""; // String containing the seed
        long seed;
        char currChar;

        boolean seedDone = false;
        TETile[][] world = new TETile[World.WIDTH][World.HEIGHT];
        World worldObj = null;

        int index = 0;

        // only grabs seed or loads world
        while (!seedDone) {
            currChar = Character.toUpperCase(input.charAt(index));
            if (currChar == 'N') {
                while (currChar != 'S' && index < input.length() - 1) {
                    index++;
                    currChar = Character.toUpperCase(input.charAt(index));
                    if (Character.isDigit(currChar)) {
                        seedString += currChar;
                    }
                }
                seed = Long.parseLong(seedString);
                worldObj = new World(world, seed);
                seedDone = true;
            } else if (currChar == 'L') {
                worldObj = Menu.loadBoard(SAVE_FILE);
                seedDone = true;
            }
            index++; // to get off of S or L character
        }

        while (index < input.length()) { // !completedWorld
            currChar = Character.toUpperCase(input.charAt(index));
            if (currChar == ':' && Character.toUpperCase(input.charAt(index + 1)) == 'Q') {
                Menu.saveBoard(worldObj); // should be initialized???
                // return worldObj.getWorld();
            } else if (currChar == 'W' || currChar == 'A' || currChar == 'S' || currChar == 'D') {
                worldObj.getAvatar().move(worldObj.getAvatar(), worldObj.getWorld(), currChar);
            }
            index++;
        }

        return worldObj.getWorld();
    }

    //public String[] getSeedAndMovement() - abstraction?

    /**
     * Used to tell the autograder which tiles are the floor/ground (including
     * any lights/items resting on the ground). Change this
     * method if you add additional tiles.
     */
    public static boolean isGroundTile(TETile t) {
        return t.character() == Tileset.FLOOR.character()
                || t.character() == Tileset.AVATAR.character()
                || t.character() == Tileset.FLOWER.character();
    }

    /**
     * Used to tell the autograder while tiles are the walls/boundaries. Change
     * this method if you add additional tiles.
     */
    public static boolean isBoundaryTile(TETile t) {
        return t.character() == Tileset.WALL.character()
                || t.character() == Tileset.LOCKED_DOOR.character()
                || t.character() == Tileset.UNLOCKED_DOOR.character();
    }
}
