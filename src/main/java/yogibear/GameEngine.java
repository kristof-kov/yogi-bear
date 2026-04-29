package yogibear;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import java.awt.Graphics2D;

/**
 *
 * @author kovi
 */
public class GameEngine extends JPanel {
    
    private static final int FPS = 60;
    private static final int YOGI_SIZE = 32;
    private static final int MAX_LEVELS = 10;
    
    private final Image background;
    private Level currentLevel;
    private Yogi yogi;
    private final Timer gameTimer;
    private boolean paused = false;
    private boolean gameOver = false;
    
    // Timer
    private long gameStartTime;
    private long elapsedTime;
    
    // Invincibility
    private boolean invincible = false;
    private long invincibilityStart;
    private final long INVINCIBILITY_DURATION = 2000; // 2 másodperc
    
    // Score
    private final ScoreManager scoreManager;

    public GameEngine() {
        super();
        setFocusable(true);
        background = new ImageIcon("data/images/grass_background.png").getImage();
        scoreManager = new ScoreManager();
        
        setupKeyBindings();
        startNewGame();
        
        gameTimer = new Timer(1000 / FPS, new GameLoopListener());
        gameTimer.start();
        
        SoundManager.load("pickup", "data/sounds/pickup.wav");
        SoundManager.load("caught", "data/sounds/caught.wav");
        SoundManager.load("footstep", "data/sounds/footstep.wav");
        SoundManager.load("success", "data/sounds/success.wav");
        SoundManager.load("gameover", "data/sounds/gameover.wav");
        SoundManager.load("gamewon", "data/sounds/gamewon.wav");
    }
    
    /**
     * Sets up keyboard controls.
     * WASD for movement, ESC for pause.
     * 
     * Movement only starts if game is not paused or over.
     */
    private void setupKeyBindings() {
        // WASD movement
        getInputMap().put(KeyStroke.getKeyStroke("W"), "move up");
        getActionMap().put("move up", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!paused && !gameOver) yogi.setMovingUp(true);
            }
        });
        
        getInputMap().put(KeyStroke.getKeyStroke("released W"), "stop up");
        getActionMap().put("stop up", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                yogi.setMovingUp(false);
            }
        });
        
        getInputMap().put(KeyStroke.getKeyStroke("S"), "move down");
        getActionMap().put("move down", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!paused && !gameOver) yogi.setMovingDown(true);
            }
        });
        
        getInputMap().put(KeyStroke.getKeyStroke("released S"), "stop down");
        getActionMap().put("stop down", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                yogi.setMovingDown(false);
            }
        });
        
        getInputMap().put(KeyStroke.getKeyStroke("A"), "move left");
        getActionMap().put("move left", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!paused && !gameOver) yogi.setMovingLeft(true);
            }
        });
        
        getInputMap().put(KeyStroke.getKeyStroke("released A"), "stop left");
        getActionMap().put("stop left", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                yogi.setMovingLeft(false);
            }
        });
        
        getInputMap().put(KeyStroke.getKeyStroke("D"), "move right");
        getActionMap().put("move right", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!paused && !gameOver) yogi.setMovingRight(true);
            }
        });
        
        getInputMap().put(KeyStroke.getKeyStroke("released D"), "stop right");
        getActionMap().put("stop right", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                yogi.setMovingRight(false);
            }
        });
        
        // ESC megállítás
        getInputMap().put(KeyStroke.getKeyStroke("ESCAPE"), "pause");
        getActionMap().put("pause", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!gameOver) paused = !paused;
            }
        });
    }
    
    /**
     * Starts new game.
     *
     * Resets time, gameOver state and loads the first level.
     */
    public void startNewGame() {
        gameStartTime = System.currentTimeMillis();
        elapsedTime = 0;
        gameOver = false;
        loadLevel(0);
    }
    
    /**
     * Loads the specified level.
     * 
     * @param levelNum the level number to load
     */
    private void loadLevel(int levelNum) {
        try {
            currentLevel = new Level(levelNum);
            Image yogiImage = ImageCache.getImage("data/images/yogi_sheet.png");
            
            if (yogi == null) {
                // first level
                yogi = new Yogi(currentLevel.getYogiStartX(), currentLevel.getYogiStartY(), 
                                YOGI_SIZE, YOGI_SIZE, yogiImage);
            } else {
                // other levels
                int currentLives = yogi.getLives();
                int currentBaskets = yogi.getBasketsCollected();
                
                yogi = new Yogi(currentLevel.getYogiStartX(), currentLevel.getYogiStartY(), 
                                YOGI_SIZE, YOGI_SIZE, yogiImage);
                
                yogi.setLives(currentLives);
                yogi.setBasketsCollected(currentBaskets);
            }
        } catch (IOException ex) {
            System.err.println("Error loading level:" + ex.getMessage());
            // if there are no more levels, we won
            if (levelNum > 0) {
                gameWon();
            }
        }
    }
    
    /**
     * Advances to the next level. If we reached the maximum
     * number of levels, ends the game with a win.
     */
    private void nextLevel() {
        int nextLevelNum = currentLevel.getLevelNumber() + 1;
        if (nextLevelNum >= MAX_LEVELS) {
            gameWon();
        } else {
            loadLevel(nextLevelNum);
        }
    }
    
    /**
     * Sets the win state and asks for the player's name.
     * Saves the score to the database.
     */
    private void gameWon() {
        gameOver = true;
        
        SoundManager.play("gamewon");
        
        String playerName = JOptionPane.showInputDialog(this, 
                "Congratulations! You finished the game!\n" +
                "Baskets collected: " + yogi.getBasketsCollected() + "\n" +
                "Time: " + formatTime(elapsedTime) + "\n\n" +
                "Enter your name for the leaderboard:");
        
        if (playerName != null && !playerName.trim().isEmpty()) {
            scoreManager.addScore(playerName, yogi.getBasketsCollected(), elapsedTime);
        }
    }
    
    /**
     * Sets the game over state and asks for the player's name.
     * Saves the score to the database.
     */
    private void gameOverScreen() {
        gameOver = true;
        
        SoundManager.play("gameover");
        
        String playerName = JOptionPane.showInputDialog(this, 
                "Game Over!\n" +
                "Baskets collected: " + yogi.getBasketsCollected() + "\n" +
                "Time: " + formatTime(elapsedTime) + "\n\n" +
                "Enter your name for the leaderboard:");
        
        if (playerName != null && !playerName.trim().isEmpty()) {
            scoreManager.addScore(playerName, yogi.getBasketsCollected(), elapsedTime);
        }
    }
    
    /**
     * Renders the panel.
     *
     * Draws the background, the level, Yogi Bear, and
     * the overlay in case the game is paused or over.
     * 
     * @param g the graphics context used for drawing
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // background
        g.drawImage(background, 0, 0, getWidth(), getHeight(), null);
        
        // level elements
        if (currentLevel != null) {
            currentLevel.draw(g);
        }
        
        // Yogi Bear (blinking during invincibility)
        if (yogi != null && (!invincible || (System.currentTimeMillis() / 200) % 2 == 0)) {
            yogi.draw(g);
        }
        
        // HUD
        drawHUD(g);
        
        // Paused or Game Over message
        if (paused) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
        
        if (gameOver) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            g.drawString("GAME OVER", getWidth() / 2 - 160, getHeight() / 2);
        }
    }
    
    /**
     * Draws the HUD (lives, baskets, level number, elapsed time).
     * In the top right corner, with a semi-transparent background.
     * 
     * @param g the graphics context used for drawing
     */
    private void drawHUD(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        int w = 110;
        int h = 110;
        int margin = 10;

        int x = getWidth() - w - margin;
        int y = margin;

        g2.setColor(new Color(0, 0, 0, 140));
        g2.fillRoundRect(x, y, w, h, 10, 10);
    
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        
        int textX = x + 10;
        
        // lives
        g.drawString("Lives: " + yogi.getLives(), textX, y + 25);
        
        // total collected
        g.drawString("Baskets: " + yogi.getBasketsCollected(), textX, y + 50);
        
        // level number
        g.drawString("Level: " + (currentLevel.getLevelNumber() + 1), textX, y + 75);
        
        // time
        g.drawString("Time: " + formatTime(elapsedTime), textX, y + 100);

    }
    
    /**
     * Converts elapsed time from milliseconds to a
     * minutes:seconds formatted string.
     * 
     * @param millis elapsed time in milliseconds
     * @return formatted time (mm:ss)
     */
    private String formatTime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    
    class GameLoopListener implements ActionListener {
        /**
         * The timed event handler for the game loop.
         * 
         * On every tick (if the game is not paused or over):
         * updates elapsed time,
         * handles invincibility,
         * moves Yogi Bear and handles collisions,
         * handles basket collection,
         * updates rangers' patrol and detection,
         * checks level completion.
         * 
         * Redraws at the end of the tick.
         * 
         * @param e the event sent by the timer
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!paused && !gameOver) {
                // update elapsed time
                elapsedTime = System.currentTimeMillis() - gameStartTime;
                
                // invincibility timer
                if (invincible && System.currentTimeMillis() - invincibilityStart > INVINCIBILITY_DURATION) {
                    invincible = false;
                }
                
                // Yogi Bear movement
                int oldX = yogi.getX();
                int oldY = yogi.getY();
                yogi.move(getWidth(), getHeight());
                yogi.update(yogi.isMoving());
                if (yogi.isMoving()) {
                    if (!SoundManager.isPlaying("footstep")) {
                        new Thread(() -> SoundManager.loop("footstep")).start();
                    }
                } else {
                    SoundManager.stop("footstep");
                }
                
                // collision with obstacles
                if (currentLevel.checkCollisionWithObstacles(yogi, yogi.getX(), yogi.getY())) {
                    yogi.setX(oldX);
                    yogi.setY(oldY);
                }
                
                // basket collection
                Basket collected = currentLevel.checkBasketCollection(yogi);
                if (collected != null) {
                    yogi.collectBasket();
                    SoundManager.play("pickup");
                }
                
                // rangers patrol and detection
                for (Ranger ranger : currentLevel.getRangers()) {
                    ranger.patrol(currentLevel.getObstacles());
                    ranger.update(true);
                    
                    if (!invincible && ranger.detectsYogi(yogi)) {
                        yogi.loseLife();
                        invincible = true;
                        invincibilityStart = System.currentTimeMillis();
                        SoundManager.play("caught");
                        
                        if (!yogi.isAlive()) {
                            gameOverScreen();
                        }
                    }
                }
                
                // level completion
                if (currentLevel.isCompleted()) {
                    SoundManager.play("success");
                    nextLevel();
                }
            }
            
            repaint();
        }
    }
    
    /**
     * Displays the leaderboard in a Swing dialog box.
     * The leaderboard data is provided by the ScoreManager.
     */
    public void showLeaderboard() {
        scoreManager.showLeaderboard(this);
    }
    
    public void closeResources() {
        scoreManager.close();
    }
}
