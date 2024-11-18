package core;

import tileengine.TETile;
import tileengine.Tileset;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

public class LightSource {
    Posn xy;
    TETile light;
    Random r;
    boolean on;

    public LightSource(Posn start, int w, int h, Random r) {
        this.xy = spawnLight(start, w, h);
        this.light = Tileset.LIGHT;
        this.r = r;
        this.on = r.nextInt(2) == 1;
    }

    public Posn spawnLight(Posn start, int w, int h) {
        Set<Posn> areaInside = Posn.genAreaInside(start, w, h);
        ArrayList<Posn> areaInsideList = new ArrayList<>(areaInside);
        int size = areaInsideList.size();
        Random random = new Random();
        Posn spawnCoord = areaInsideList.get(random.nextInt(size));

        return spawnCoord;
    }

    public void toggleOn(TETile[][] world) {
        int spawnRadius = 3;

        int lightX = xy.getX();
        int lightY = xy.getY();
        int minX = Math.max(0, lightX - spawnRadius);
        int minY = Math.max(0, lightY - spawnRadius);
        int maxX = Math.min(World.WIDTH - 1, lightX + spawnRadius);
        int maxY = Math.min(World.HEIGHT - 1, lightY + spawnRadius);

        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                Posn cur = new Posn(x, y);
                if (!hasWallBetween(xy, cur, world) && world[x][y] == Tileset.FLOOR && !cur.equals(xy)) {
                    int intensity = (int) calculateIntensity(xy, new Posn(x, y)) * 40;
                    TETile curTile = world[x][y];
                    int red = Tileset.LIGHT.getBackgroundColor().getRed();
                    int green = Tileset.LIGHT.getBackgroundColor().getGreen();
                    int blue = Tileset.LIGHT.getBackgroundColor().getBlue();
                    int newRed = Math.max(0, red - intensity);
                    int newGreen = Math.max(0, green - intensity);
                    int newBlue = Math.max(0, blue - intensity);
                    curTile = new TETile(new Color(newRed, newGreen, newBlue), curTile);
                    world[x][y] = curTile;
                }
            }
        }
    }

    public void toggleOff(TETile[][] world) {
        int spawnRadius = 3;

        int lightX = xy.getX();
        int lightY = xy.getY();
        int minX = Math.max(0, lightX - spawnRadius);
        int minY = Math.max(0, lightY - spawnRadius);
        int maxX = Math.min(World.WIDTH - 1, lightX + spawnRadius);
        int maxY = Math.min(World.HEIGHT - 1, lightY + spawnRadius);

        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                Posn cur = new Posn(x, y);
                if (world[x][y].description() == Tileset.FLOOR.description() && !cur.equals(xy)) {
                    world[x][y] = Tileset.FLOOR;
                }
            }
        }
    }

    public void toggler(TETile[][] world, Avatar avatar, boolean close) {
        if (avatar.getCurrPos().equals(xy) || close) {
            on = !on;
            if (on) {
                toggleOn(world);
                System.out.println("toggle on");
            } else {
                toggleOff(world);
                System.out.println("toggle off");
            }
        }
    }

    public static void oToggle(World world) {
        int minX = Math.max(0, world.getAvatar().currX - 1);
        int minY = Math.max(0, world.getAvatar().currY - 1);
        int maxX = Math.min(World.WIDTH - 1, world.getAvatar().currX + 1);
        int maxY = Math.min(World.HEIGHT - 1, world.getAvatar().currY + 1);

        for (LightSource light : world.getLights()) {
            Posn lightPos = light.getXY();
            int lightX = lightPos.getX();
            int lightY = lightPos.getY();

            if (lightX >= minX && lightX <= maxX && lightY >= minY && lightY <= maxY) {
                light.toggler(world.getWorld(), world.getAvatar(), true);
                System.out.println("WORKORNOWORK");
            }
        }
    }

    public double calculateIntensity(Posn light, Posn curTile) {
        int lightX = light.getX();
        int lightY = light.getY();
        int curTileX = curTile.getX();
        int curTileY = curTile.getY();

        double dist = Math.sqrt(Math.pow(lightX - curTileX, 2) + Math.pow(lightY - curTileY, 2));
        return dist;
    }

    public boolean hasWallBetween(Posn light, Posn curTile, TETile[][] world) {
        int lightX = light.getX();
        int lightY = light.getY();
        int curTileX = curTile.getX();
        int curTileY = curTile.getY();

        int dy, dx;

        if (lightX < curTileX) {
            dx = 1;
        } else {
            dx = -1;
        }

        if (lightY < curTileY) {
            dy = 1;
        } else {
            dy = -1;
        }

        while (lightX != curTileX) {
            if (world[lightX][lightY] == Tileset.WALL) {
                return true;
            }
            lightX += dx;
        }

        while (lightY != curTileY) {
            if (world[lightX][lightY] == Tileset.WALL) {
                return true;
            }
            lightY += dy;
        }
        return false;
    }

    public Posn getXY() {
        return xy;
    }
}
