package yogibear;

import java.awt.Graphics;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.ImageIcon;


/**
 *
 * @author kovi
 */
public class Level {
    
    // egy mező legyen 40x40
    private final int TILE_SIZE = 40;
    
    private ArrayList<Obstacle> obstacles;
    private ArrayList<Basket> baskets;
    private ArrayList<Ranger> rangers;
    
    private int yogiStartX;
    private int yogiStartY;
    
    private final int levelNumber;

    public Level(int levelNumber) throws IOException {
        this.levelNumber = levelNumber;
        loadLevel("data/levels/level" + String.format("%02d", levelNumber) + ".txt");
    }
    
    /**
     * Kezeli egy pálya betöltését
     * 
     * @param levelPath pálya elérési címe
     * @throws IOException 
     */
    private void loadLevel(String levelPath) throws IOException {
        obstacles = new ArrayList<>();
        baskets = new ArrayList<>();
        rangers = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(levelPath))) {
            String line;
            int y = 0;
            
            while ((line = br.readLine()) != null) {
                for (int x = 0; x < line.length(); x++) {
                    char tile = line.charAt(x);
                    int posX = x * TILE_SIZE;
                    int posY = y * TILE_SIZE;
                    
                    switch (tile) {
                        case '#' -> {
                            // Akadály (fa)
                            Image obstacleTreeImg = new ImageIcon("data/images/obstacleTree.png").getImage();
                            obstacles.add(new Obstacle(posX, posY, TILE_SIZE, TILE_SIZE, obstacleTreeImg));
                        }
                        case '@' -> {
                            // Akadály (szikla)
                            Image obstacleRockImg = new ImageIcon("data/images/obstacleRock.png").getImage();
                            obstacles.add(new Obstacle(posX, posY, TILE_SIZE, TILE_SIZE, obstacleRockImg));
                        }
                        case 'B' -> {
                            // Kosár
                            Image basketImg = new ImageIcon("data/images/basket.png").getImage();
                            baskets.add(new Basket(posX, posY, TILE_SIZE, TILE_SIZE, basketImg));
                        }
                        case 'P' -> {
                            // Játékos start pozíció
                            yogiStartX = posX;
                            yogiStartY = posY;
                        }
                        case 'H' -> {
                            // Vízszintes vadőr
                            Image rangerImg = new ImageIcon("data/images/ranger.png").getImage();
                            rangers.add(new Ranger(posX, posY, TILE_SIZE, TILE_SIZE, rangerImg,
                                    Ranger.PatrolDirection.HORIZONTAL, 2, 80));
                        }
                        case 'V' -> {
                            // Függőleges vadőr
                            Image vRangerImg = new ImageIcon("data/images/ranger.png").getImage();
                            rangers.add(new Ranger(posX, posY, TILE_SIZE, TILE_SIZE, vRangerImg,
                                    Ranger.PatrolDirection.VERTICAL, 2, 80));
                        }
                    }
                }
                y++;
            }
        }
    }
    
    /**
     * Ellenőrzi, hogy a játékos ütközik-e
     * az akadályokkal
     * 
     * @param yogi
     * @param newX ellenőrizendő mező X-koordinátája
     * @param newY ellenőrizendő mező Y-koordinátája
     * @return igaz, ha ütközik, egyébként false
     */
    public boolean checkCollisionWithObstacles(Yogi yogi, int newX, int newY) {
        int oldX = yogi.getX();
        int oldY = yogi.getY();
        
        yogi.setX(newX);
        yogi.setY(newY);
        
        boolean collides = false;
        for (Obstacle obstacle : obstacles) {
            if (yogi.collides(obstacle)) {
                collides = true;
                break;
            }
        }
        
        yogi.setX(oldX);
        yogi.setY(oldY);
        
        return collides;
    }
    
    /**
     * Ellenőrzi, hogy ütközünk-e a kosarakkal, és
     * ha igen, akkor visszaadja azt
     * 
     * @param yogi
     * @return Ha felvettünk egy kosarat, akkor visszaadja azt, egyébként null
     */
    public Basket checkBasketCollection(Yogi yogi) {
        for (Basket basket : baskets) {
            if (!basket.isCollected() && yogi.collides(basket)) {
                basket.collect();
                return basket;
            }
        }
        return null;
    }
    
    
    /**
     * Ellenőrzi, hogy felvettük-e az
     * összes kosarat
     * 
     * @return true ha velvettük az összes kosarat, amúgy false
     */
    public boolean isCompleted() {
        for (Basket basket : baskets) {
            if (!basket.isCollected()) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Kirajzolja a különböző objektumokat
     * 
     * @param g kirajzolandó grafika
     */
    public void draw(Graphics g) {
        for (Obstacle obstacle : obstacles) {
            obstacle.draw(g);
        }
        for (Basket basket : baskets) {
            if (!basket.isCollected()) {
                basket.draw(g);
            }
        }
        for (Ranger ranger : rangers) {
            ranger.draw(g);
        }
    }
    
    /**
     * A még fel nem vett kosarak számát adja vissza
     * 
     * @return még fel nem vett kosarak száma
     */
    public int getRemainingBaskets() {
        int count = 0;
        for (Basket basket : baskets) {
            if (!basket.isCollected()) {
                count++;
            }
        }
        return count;
    }

    public ArrayList<Ranger> getRangers() {
        return rangers;
    }
    
    public ArrayList<Obstacle> getObstacles() {
        return obstacles;
    }

    public int getYogiStartX() {
        return yogiStartX;
    }

    public int getYogiStartY() {
        return yogiStartY;
    }
    
    public int getLevelNumber() {
        return levelNumber;
    }
}
