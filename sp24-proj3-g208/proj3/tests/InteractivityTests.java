import core.AutograderBuddy;
import core.World;
import edu.princeton.cs.algs4.StdDraw;
import org.junit.jupiter.api.Test;
import tileengine.TERenderer;
import tileengine.TETile;

public class InteractivityTests {

    @Test
    public void testMovementFromAutograderBuddy() {
        TETile[][] tiles = AutograderBuddy.getWorldFromInput("n3962503113108326091swwwwdd:q");

        TERenderer ter = new TERenderer();
        ter.initialize(tiles.length, tiles[0].length);
        ter.renderFrame(tiles);
        StdDraw.pause(5000);
    }

    @Test
    public void testSeed123456789() {
        TETile[][] tiles = AutograderBuddy.getWorldFromInput("n123456789sdddww:q");

        TERenderer ter = new TERenderer();
        ter.initialize(World.WIDTH, tiles[0].length);
        ter.renderFrame(tiles);
        StdDraw.pause(5000);
    }


}
