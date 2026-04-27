package yogibear;

import java.awt.Image;
import java.util.ArrayList;

/**
 *
 * @author kovi
 */
public class Ranger extends Sprite {
    
    public static final int DEFAULT_SPEED = 2;
    public static final int DEFAULT_DETECTION_RANGE = 80;
    
    private final int detectionRange;
    private final int speed;
    private boolean movingForward;
    private final PatrolDirection direction;
    
    /**
     * Enum a vízszintes és függőleges
     * járőr-irányokhoz
     */
    public enum PatrolDirection {
        HORIZONTAL, VERTICAL
    }

    public Ranger(int x, int y, int width, int height, Image image, 
                  PatrolDirection direction, int speed, int detectionRange) {
        
        super(x, y, width, height, image);
        this.direction = direction;
        this.speed = speed;
        this.detectionRange = detectionRange;
        this.movingForward = true;
        
        this.animated = true;
        this.frameWidth = 32;
        this.frameHeight = 32;
        this.framesPerRow = 6;
                
    }
    
    /**
     * Beállítja a járőrözés irányát, valamint
     * kezeli az akadályokat.
     * 
     * @param obstacles akadályok listája
     */
    public void patrol(ArrayList<Obstacle> obstacles) {
        int nextX = x;
        int nextY = y;
        
        if (direction == PatrolDirection.HORIZONTAL) {
            nextX += (movingForward ? speed : -speed);
            currentAnimationRow = movingForward ? 2 : 1;
        } else  {
            nextY += (movingForward ? speed : -speed);
            currentAnimationRow = movingForward ? 0 : 3;
        }
        
        // ha akadály, forduljon meg
        if (collidesWithObstacle(nextX, nextY, obstacles)) {
            movingForward = !movingForward;
            return;
        }
        
        // különben mehet
        x = nextX;
        y = nextY;
    }
    
    /**
     * Eldönti, hogy egy őr ütközik-e egy akadállyal
     * 
     * @param testX az ellenőrizendő mező X-koordinátája
     * @param testY az ellenőrizendő mező Y-koordinátája
     * @param obstacles akadályok listája
     * 
     * @return ha ütközik true, egyébként false
     */
    private boolean collidesWithObstacle(int testX, int testY, ArrayList<Obstacle> obstacles) {
        if (obstacles == null) {
            return false;
        }
        
        int oldX = x;
        int oldY = y;
        
        x = testX;
        y = testY;
        
        boolean hit = false;
        for (Obstacle o : obstacles) {
            if (this.collides(o)) {
                hit = true;
                break;
            }
        }
        
        x = oldX;
        y = oldY;
        
        return hit;
    }
    
    /**
     * Eldönti, hogy az őr elég közel van-e Maci Lacihoz
     * 
     * @param yogi
     * @return true, ha elég közel van Maci Laci, egyébként false
     */
    public boolean detectsYogi(Yogi yogi) {
        double distance = this.distanceTo(yogi);
        return distance <= detectionRange;
    }

    public int getDetectionRange() {
        return detectionRange;
    }

    public PatrolDirection getDirection() {
        return direction;
    }
}
