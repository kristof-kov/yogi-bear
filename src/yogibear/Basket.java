package yogibear;

import java.awt.Image;

/**
 *
 * @author kovi
 */
public class Basket extends Sprite {
    
    private boolean collected;

    public Basket(int x, int y, int width, int height, Image image) {
        super(x, y, width, height, image);
        this.collected = false;
    }

    public boolean isCollected() {
        return collected;
    }

    public void collect() {
        this.collected = true;
    }
}
