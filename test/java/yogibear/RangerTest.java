package yogibear;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RangerTest {

    @Test
    void testDetectsYogi_withinRange() {
        Ranger ranger = new Ranger(0, 0, 40, 40, null,
                Ranger.PatrolDirection.HORIZONTAL,
                Ranger.DEFAULT_SPEED,
                Ranger.DEFAULT_DETECTION_RANGE);
        Yogi yogi = new Yogi(50, 0, 32, 32, null);
        assertTrue(ranger.detectsYogi(yogi));
    }

    @Test
    void testDetectsYogi_outOfRange() {
        Ranger ranger = new Ranger(0, 0, 40, 40, null,
                Ranger.PatrolDirection.HORIZONTAL,
                Ranger.DEFAULT_SPEED,
                Ranger.DEFAULT_DETECTION_RANGE);
        Yogi yogi = new Yogi(500, 500, 32, 32, null);
        assertFalse(ranger.detectsYogi(yogi));
    }
}
