package yogibear;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BasketTest {

    @Test
    void testInitiallyNotCollected() {
        Basket basket = new Basket(0, 0, 40, 40, null);
        assertFalse(basket.isCollected());
    }

    @Test
    void testCollect() {
        Basket basket = new Basket(0, 0, 40, 40, null);
        basket.collect();
        assertTrue(basket.isCollected());
    }

    @Test
    void testCollectTwice() {
        Basket basket = new Basket(0, 0, 40, 40, null);
        basket.collect();
        basket.collect();
        assertTrue(basket.isCollected());
    }
}
