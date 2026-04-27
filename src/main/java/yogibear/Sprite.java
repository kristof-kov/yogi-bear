package yogibear;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

/**
 *
 * @author kovi
 */
public class Sprite {
    // ezek a sprite bal felső koordinátái
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected Image image;
    
    protected boolean animated = false;
    protected int frameWidth;
    protected int frameHeight;
    protected int framesPerRow;
    protected int currentFrame = 0;
    protected int currentAnimationRow = 0;
    protected int frameCounter = 0;
    private int frameDelay = 8; // minden 8 tickben vált frame-t

    public Sprite(int x, int y, int width, int height, Image image) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.image = image;
    }    
    
    public void draw(Graphics g) {
        if (!animated) {
            g.drawImage(image, x, y, width, height, null);
            return;
        }
        
        int srcX = currentFrame * frameWidth;
        int srcY = currentAnimationRow * frameHeight;
        
        g.drawImage(image, 
                x, y, x + width, y + height, // cél
                srcX, srcY, srcX + frameWidth, srcY + frameHeight, // forrás
                null);            
    }
    
    public void update(boolean moving) {
        if (!animated || !moving) {
            currentFrame = 0;
            frameCounter = 0;
            return;
        }
        
        frameCounter++;
        if (frameCounter >= frameDelay) {
            frameCounter = 0;
            currentFrame = (currentFrame + 1) % framesPerRow;
        }
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
