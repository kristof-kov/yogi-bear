package yogibear;

import java.awt.Image;

/**
 *
 * @author kovi
 */
public class Yogi extends Sprite {
    
    private final int MOVEMENT_SPEED = 3;
    private int lives;
    private int basketsCollected;
    private final int startX;
    private final int startY;
    
    private boolean movingUp = false;
    private boolean movingDown = false;
    private boolean movingLeft = false;
    private boolean movingRight = false;

    public Yogi(int x, int y, int width, int height, Image image) {
        super(x, y, width, height, image);
        this.startX = x;
        this.startY = y;
        this.lives = 3;
        this.basketsCollected = 0;
    }
    
    /**
     * A mozgást kezeli, valamint biztosítja, 
     * hogy a pályán belül maradjunk.
     * 
     * @param worldWidth a pálya szélessége
     * @param worldHeight a pálya magassága
     */
    public void move(int worldWidth, int worldHeight) {
        int newX = x;
        int newY = y;
        
        if (movingUp) {
            newY -= MOVEMENT_SPEED;
        }
        if (movingDown) {
            newY += MOVEMENT_SPEED;
        }
        if (movingLeft) {
            newX -= MOVEMENT_SPEED;
        }
        if (movingRight) {
            newX += MOVEMENT_SPEED;
        }
        
        // Határ ellenőrzés
        if (newX >= 0 && newX + width <= worldWidth) {
            x = newX;
        }
        if (newY >= 0 && newY + height <= worldHeight) {
            y = newY;
        }
    }
    
    /**
     * Visszaállítja a pozícióját a kezdő pozícióra.
     */
    public void resetToStart() {
        x = startX;
        y = startY;
    }
    
    /**
     * Elvesz egy élet pontot, és 
     * visszaállítja a pozícióját a kezdő pozícióra.
     */
    public void loseLife() {
        lives--;
        resetToStart();
    }
    
    /**
     * Kosarak számához hozzáad egyet.
     */
    public void collectBasket() {
        basketsCollected++;
    }
   
    /**
     * Ellenőrzi, hogy van-e elég élet pontja a játékosnak.
     * 
     * @return true, ha még van elég élet pontja, false ha nincs
     */
    public boolean isAlive() {
        return lives > 0;
    }

    public int getLives() {
        return lives;
    }
    
    public void setLives(int lives) {
        this.lives = lives;
    }

    public int getBasketsCollected() {
        return basketsCollected;
    }

    public void setBasketsCollected(int basketsCollected) {
        this.basketsCollected = basketsCollected;
    }

    public void setMovingUp(boolean movingUp) {
        this.movingUp = movingUp;
    }

    public void setMovingDown(boolean movingDown) {
        this.movingDown = movingDown;
    }

    public void setMovingLeft(boolean movingLeft) {
        this.movingLeft = movingLeft;
    }

    public void setMovingRight(boolean movingRight) {
        this.movingRight = movingRight;
    }
}
