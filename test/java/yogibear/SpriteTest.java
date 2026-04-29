package yogibear;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SpriteTest {

    @Test
    void testCollides_overlapping() {
        Sprite a = new Sprite(0, 0, 40, 40, null);
        Sprite b = new Sprite(20, 20, 40, 40, null);
        assertTrue(a.collides(b));
    }

    @Test
    void testCollides_notOverlapping() {
        Sprite a = new Sprite(0, 0, 40, 40, null);
        Sprite b = new Sprite(100, 100, 40, 40, null);
        assertFalse(a.collides(b));
    }

    @Test
    void testCollides_touching() {
        Sprite a = new Sprite(0, 0, 40, 40, null);
        Sprite b = new Sprite(40, 0, 40, 40, null);
        assertFalse(a.collides(b));
    }

    @Test
    void testDistanceTo_samePosition() {
        Sprite a = new Sprite(0, 0, 40, 40, null);
        Sprite b = new Sprite(0, 0, 40, 40, null);
        assertEquals(0.0, a.distanceTo(b));
    }

    @Test
    void testDistanceTo_knownDistance() {
        Sprite a = new Sprite(0, 0, 40, 40, null);
        Sprite b = new Sprite(30, 40, 40, 40, null);
        assertEquals(50.0, a.distanceTo(b), 0.001);
    }
}
