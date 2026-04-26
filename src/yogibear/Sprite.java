package yogibear;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

/**
 *
 * @author kovi
 */
public class Sprite {
    // ezek a sprite jobb felső koordinátái
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected Image image;

    public Sprite(int x, int y, int width, int height, Image image) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.image = image;
    }    
    
    public void draw(Graphics g) {
        g.drawImage(image, x, y, width, height, null);
    }
    
    /**
     * Ellenőrzi, hogy a sprite ütközik-e egy másik sprite-al
     * @param other a másik sprite
     * @return 
     */
    public boolean collides(Sprite other) {
        Rectangle rect = new Rectangle(x, y, width, height);
        Rectangle otherRect = new Rectangle(other.x, other.y, other.width, other.height);        
        return rect.intersects(otherRect);
    }
    
    /**
     * Két sprite között kiszámolja a távolságot
     * @param other a másik sprite
     * @return 
     */
    public double distanceTo(Sprite other) {
        int centerX = x + width / 2;
        int centerY = y + height / 2;
        int otherCenterX = other.x + other.width / 2;
        int otherCenterY = other.y + other.height / 2;
        
        int dx = centerX - otherCenterX;
        int dy = centerY - otherCenterY;
        
        return Math.sqrt(dx * dx + dy * dy);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }
}
