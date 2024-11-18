package core;

// import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;
import tileengine.TETile;
import tileengine.Tileset;
import utils.FileUtils;

import java.awt.Color;
import java.awt.Font;

// @source https://introcs.cs.princeton.edu/java/stdlib/javadoc/StdDraw.html

public class Menu {

    static Font titleFont = new Font("Arial", Font.BOLD, 80);
    static Font instructions = new Font("Arial", Font.ITALIC, 20);
    static Font standard = new Font("Arial", Font.BOLD, 30);
    private static final String SAVE_FILE = "save.txt";

    public static World displayMenu(int width, int height, String filename, TETile[][] board) {
        StdDraw.setCanvasSize(width, height);
        StdDraw.setXscale(0, width);
        StdDraw.setYscale(0, height);
        StdDraw.enableDoubleBuffering();

        StdDraw.clear(new Color(0, 0, 0)); // set background black

        boolean seedSubmitted = false;
        StringBuilder inputSeed = new StringBuilder();

        while (!seedSubmitted) {

            double midX = width * 0.5;
            double midY = height * 0.5;

            createStartMenu(width, height); // draw the starting menu
            StdDraw.show(); // needed to show the stuff above

            boolean pressed = false;
            boolean choseNewWorld = false;
            boolean choseLoadWorld = false;
            boolean choseQuit = false;

            if (StdDraw.isMousePressed()) {
                double mouseX = StdDraw.mouseX();
                double mouseY = StdDraw.mouseY();
                boolean isNewWorldClicked = isMouseOnText(mouseX, mouseY, height * 0.5, standard);
                boolean isLoadWorldClicked = isMouseOnText(mouseX, mouseY, height * 0.45, standard);
                boolean isQuitClicked = isMouseOnText(mouseX, mouseY, height * 0.4, standard);

                if (isNewWorldClicked) {
                    choseNewWorld = true;
                    pressed = true;
                } else if (isLoadWorldClicked) {
                    choseLoadWorld = true;
                    pressed = true;
                } else if (isQuitClicked) {
                    choseQuit = true;
                    pressed = true;
                }
            }

            char key = ' ';
            // start key - first key thats been pressed
            if (StdDraw.hasNextKeyTyped()) {
                key = Character.toUpperCase(StdDraw.nextKeyTyped());
            }

            if (key == 'N' || choseNewWorld) {
                StdDraw.setPenColor(0, 255, 0); // set font to green
                StdDraw.text(midX, height * 0.3, "input SEED number followed by 'S'");
                StdDraw.show();
                System.out.print("Seed: ");
                boolean noS = true;
                while (noS) {
                    if (StdDraw.hasNextKeyTyped()) {
                        char nextTyped = Character.toUpperCase(StdDraw.nextKeyTyped());
                        if (nextTyped == 'S') {
                            noS = false;
                            seedSubmitted = true;
                        } else if (nextTyped == '\b' && !inputSeed.isEmpty()) {
                            inputSeed.deleteCharAt(inputSeed.length() - 1);
                        } else {
                            if (Character.isDigit(nextTyped)) {
                                System.out.print(nextTyped);
                                inputSeed.append(nextTyped);
                            }
                        }
                        createStartMenu(width, height);
                        StdDraw.setPenColor(0, 255, 0); // set font to green
                        StdDraw.text(midX, height * 0.3, "input SEED number followed by 'S'");
                        StdDraw.text(midX, height * 0.25, String.valueOf(inputSeed));
                        StdDraw.show();
                    }
                }
                if (String.valueOf(inputSeed).isEmpty()) {
                    System.exit(0);
                }
            } else if (key == 'L' || choseLoadWorld) {
                if (!FileUtils.fileExists(filename)) {
                    System.exit(0);
                } else {
                    return loadBoard(filename);
                }
                return loadBoard(filename);
            } else if (key == 'Q' || choseQuit) {
                System.exit(0);
            }
        }

        long seed = Long.parseLong(String.valueOf(inputSeed));
        return new World(board, seed);
    }

    public static void createStartMenu(int width, int height) {
        StdDraw.clear(new Color(0, 0, 0)); // set background black


        double midX = width * 0.5;
        double midY = height * 0.5;

        StdDraw.setPenColor(255, 0, 0); // set font to red
        StdDraw.setFont(titleFont);
        StdDraw.text(midX, height * 0.8, "CS 61B: THE GAME"); //
        StdDraw.setFont(instructions);
        StdDraw.setPenColor(255, 255, 0); // set font to yellow
        StdDraw.text(midX, height * 0.6, "Please choose an option:");
        StdDraw.setFont(standard);
        StdDraw.setPenColor(255, 255, 255); // set font to white
        StdDraw.text(midX, height * 0.5, "NEW WORLD (N)");
        StdDraw.text(midX, height * 0.45, "LOAD WORLD (L)");
        StdDraw.text(midX, height * 0.4, "QUIT (Q)");
    }

    public static void saveBoard(World world) {

        StringBuilder board = new StringBuilder();
        board.append(world.getSeed());
        board.append("\n");
        // board.append(World.WIDTH).append(" ").append(World.HEIGHT).append("\n");

        TETile[][] layout = world.getWorld();
        // input tiles already have dimensions of [width][height]
        for (int row = World.HEIGHT - 1; row >= 0; row--) { // start top down
            for (int col = 0; col < World.WIDTH; col++) {
                // save it as the transpose tho
                board.append(layout[col][row].character()); // save the character
            }
            board.append("\n"); // Append newline character after each row
            FileUtils.writeFile(SAVE_FILE, board.toString());
        }
    }

    public static World loadBoard(String filename) {
        // read in the file.
        String file = FileUtils.readFile(filename);

        // split the file based on the new line character.
        String[] lines = file.split("\n");

        // create a TETile[][] to load the board from the file into
        int width = World.WIDTH;
        int height = World.HEIGHT;

        if (lines[0].isEmpty()) {
            System.exit(0);
        }

        long seed = Long.parseLong(lines[0]);
        TETile[][] newBoard = new TETile[width][height];

        World newWorld = new World(newBoard, seed);

        // load the state of the board from the given filename
        for (int row = height - 1; row >= 0; row--) { // start from the top row when traversing
            for (int col = 0; col < width; col++) {
                int line = height - row;
                char curTile = lines[line].charAt(col); // grabs current character

                if (curTile == '@') {
                    newWorld.getAvatar().currX = col;
                    newWorld.getAvatar().currY = row;
                }
                newWorld.getWorld()[col][row] = getTile(curTile); // change so that it's based on the character
            }
        }

        for (LightSource light : newWorld.getLights()) {
            if (light.on) {
                light.toggleOn(newWorld.getWorld());
            } else {
                light.toggleOff(newWorld.getWorld());
            }
        }
        return newWorld;
    }

    // not 100% if this is the best way to do this...
    /** given the character, returns the tile */
    public static TETile getTile(char character) {
        switch (character) {
            case '@':
                return Tileset.AVATAR;
            case '#':
                return Tileset.WALL;
            case '·':
                return Tileset.FLOOR;
            case ' ':
                return Tileset.NOTHING;
            case '"':
                return Tileset.GRASS;
            case '≈':
                return Tileset.WATER;
            case '❀':
                return Tileset.FLOWER;
            case '█':
                return Tileset.LOCKED_DOOR;
            case '▢':
                return Tileset.UNLOCKED_DOOR;
            case '▒':
                return Tileset.SAND;
            case '▲':
                return Tileset.MOUNTAIN;
            case '♠':
                return Tileset.TREE;
            case '❂':
                return Tileset.LIGHT;
            default:
                return Tileset.NOTHING;
        }
    }

    public static boolean isMouseOnText(double mouseX, double mouseY, double textY, Font font) {
        double textHeight = font.getSize();

        double textBottomY = textY - textHeight / 2;
        double textTopY = textY + textHeight / 2;

        return mouseY >= textBottomY && mouseY <= textTopY;
    }

}
