package yogibear;

import java.awt.Image;

/**
 *
 * @author kovi
 */
public class Yogi extends Sprite {
    
    public enum Direction {
        DOWN, LEFT, RIGHT, UP
    }
    
    private Direction direction = Direction.DOWN;
    
    private static final int MOVEMENT_SPEED = 3;
    private static final int STARTING_LIVES = 3;
    
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
        this.lives = STARTING_LIVES;
        this.basketsCollected = 0;
        
        this.animated = true;
        this.frameWidth = 32;
        this.frameHeight = 32;
        this.framesPerRow = 3;
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
            direction = Direction.UP;
            currentAnimationRow = 3;
        }
        if (movingDown) {
            newY += MOVEMENT_SPEED;
            direction = Direction.DOWN;
            currentAnimationRow = 0;
        }
        if (movingLeft) {
            newX -= MOVEMENT_SPEED;
            direction = Direction.LEFT;
            currentAnimationRow = 1;
        }
        if (movingRight) {
            newX += MOVEMENT_SPEED;
            direction = Direction.RIGHT;
            currentAnimationRow = 2;
        }
        
        // Határ ellenőrzés
        if (newX >= 0 && newX + width <= worldWidth) {
            x = newX;
        }
        if (newY >= 0 && newY + height <= worldHeight) {
            y = newY;
        }
    }
    
    @Override
    public void update(boolean moving) {
        if (!moving) {
            currentFrame = 1;
            frameCounter = 0;
            return;
        }
        super.update(moving);
    }
    
    public boolean isMoving() {
        return movingUp || movingDown || movingLeft || movingRight;
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
