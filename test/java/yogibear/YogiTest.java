package yogibear;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class YogiTest {

    private Yogi yogi;

    @BeforeEach
    void setUp() {
        yogi = new Yogi(100, 200, 32, 32, null);
    }

    @Test
    void testInitialLives() {
        assertEquals(3, yogi.getLives());
    }

    @Test
    void testLoseLife() {
        yogi.loseLife();
        assertEquals(2, yogi.getLives());
    }

    @Test
    void testIsAlive_noLives() {
        yogi.setLives(0);
        assertFalse(yogi.isAlive());
    }

    @Test
    void testCollectBasket() {
        yogi.collectBasket();
        assertEquals(1, yogi.getBasketsCollected());
    }

    @Test
    void testResetToStart() {
        yogi.setX(999);
        yogi.setY(999);
        yogi.resetToStart();
        assertEquals(100, yogi.getX());
        assertEquals(200, yogi.getY());
    }
}
