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
     * Enum for horizontal and vertical patrol directions.
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
     * Sets the patrol direction and handles obstacle collisions.
     * 
     * @param obstacles list of obstacles
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
        
        // if obstacle ahead, turn around
        if (collidesWithObstacle(nextX, nextY, obstacles)) {
            movingForward = !movingForward;
            return;
        }
        
        // otherwise keep moving
        x = nextX;
        y = nextY;
    }
    
    /**
     * Determines whether the ranger collides with an obstacle.
     * 
     * @param testX the X-coordinate of the tile to check
     * @param testY the Y-coordinate of the tile to check
     * @param obstacles list of obstacles
     * @return true if there is a collision, false otherwise
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
     * Determines whether the ranger is close enough to detect Yogi.
     * 
     * @param yogi Yogi Bear
     * @return true if Yogi is within detection range, false otherwise
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
