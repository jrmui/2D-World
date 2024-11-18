package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;

import java.awt.*;

public class Main {

    public static void main(String[] args) {

        String saveFile = "save.txt";

        TETile[][] world = new TETile[World.WIDTH][World.HEIGHT];

        World worldObj = Menu.displayMenu(World.WIDTH * 16, World.HEIGHT * 16, saveFile, world);
        StdDraw.setCanvasSize(World.WIDTH, World.HEIGHT);

        TERenderer ter = new TERenderer();
        ter.initialize(World.WIDTH, World.HEIGHT);

        renderBoard(ter, worldObj);
        runGame(ter, worldObj);
    }

    /** Generates the avatar's movements based on their key presses, WASD,
     * and keeps running until the user quits where the program quite and saves the board.
     * @param ter
     * @param worldObj
     */
    public static void runGame(TERenderer ter, World worldObj) {
        boolean isGameOver = false;
        String quit = "";

        while (!isGameOver) {

            displayHUD(worldObj);
            worldObj.visibleWorld = worldObj.visibleWorld();

            if (StdDraw.hasNextKeyTyped()) {
                char currChar = Character.toUpperCase(StdDraw.nextKeyTyped());

                if (currChar == ':') {
                    quit += currChar;
                } else if (currChar == 'Q') {
                    if (quit.equals(":")) {
                        Menu.saveBoard(worldObj);
                        System.exit(0);
                    }
                } else if (currChar == 'T') {
                    worldObj.lightOn = !(worldObj.getLightOn());
                } /*else if (currChar == 'O') {
                    LightSource.oToggle(worldObj);
                }*/ else {
                    worldObj.avatar.move(worldObj.getAvatar(), worldObj.getWorld(), currChar);
                    worldObj.visibleWorld = worldObj.visibleWorld();
                    // worldObj.checkToggle(); FOR LIGHT SOURCE
                    // StdDraw.pause(100);
                }
            }
            renderBoard(ter, worldObj);
        }
        ter.renderFrame(worldObj.getWorld());
    }

    /** Renders the current layout of the world with the avatar on display.
     *
     * @param ter The tile renderer used to render the world
     * @param worldObj
     */
    public static void renderBoard(TERenderer ter, World worldObj) {
        StdDraw.clear(new Color(0, 0, 0));
        if (worldObj.getLightOn()) {
            ter.drawTiles(worldObj.getWorld());
        } else {
            ter.drawTiles(worldObj.getVisibleWorld());
        }
        StdDraw.show();
    }

    public static void displayHUD(World worldObj) {
        StdDraw.setPenColor(StdDraw.WHITE); // sets color of text to white
        int xLightStatus = 4;
        int xTile = World.WIDTH - 4;
        int yTile = World.HEIGHT - 1;
        boolean lightStatus = worldObj.getLightOn();
        if (lightStatus) {
            StdDraw.text(xTile, yTile, "Tile: " + getTileTouched(worldObj.getWorld()));
        } else {
            StdDraw.text(xTile, yTile, "Tile: " + getTileTouched(worldObj.getVisibleWorld()));
        }
        String lightStat;
        if (lightStatus) {
            lightStat = "On";
        } else {
            lightStat = "Off";
        }
        StdDraw.text(xLightStatus, yTile, "Lights (T): " + lightStat);
        StdDraw.show();
    }

    public static String getTileTouched(TETile[][] world) {
        int mouseX = (int) StdDraw.mouseX();
        int mouseY = (int) StdDraw.mouseY();
        if (mouseX < World.WIDTH && mouseY < World.HEIGHT) {
            TETile tile = world[mouseX][mouseY];
            return tile.description();
        }
        return "";
    }
}
