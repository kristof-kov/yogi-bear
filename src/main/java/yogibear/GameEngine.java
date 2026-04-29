package yogibear;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;

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
    
    // timer
    private long gameStartTime;
    private long elapsedTime;
    
    // invincibility
    private boolean invincible = false;
    private long invincibilityStart;
    private final long INVINCIBILITY_DURATION = 2000; // 2 másodperc
    
    // score
    private final ScoreManager scoreManager;

    // assets
    private Font pixelFont;
    private final Image heartIcon;
    private final Image basketIcon;
    private final Image clockIcon;
    private final Image hudBackground;

    public GameEngine() {
        super();
        setFocusable(true);
        background = new ImageIcon("data/images/grass_background.png").getImage();
        scoreManager = new ScoreManager();
        
        setupKeyBindings();
        startNewGame();
        
        gameTimer = new Timer(1000 / FPS, new GameLoopListener());
        gameTimer.start();

        try {
            pixelFont = Font.createFont(Font.TRUETYPE_FONT,
                    new File("data/fonts/PressStart2P-Regular.ttf")).deriveFont(16f);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(pixelFont);
        } catch (Exception e) {
            pixelFont = new Font("Arial", Font.BOLD, 10);
            System.err.println("Font not found, using Arial instead");
        }

        heartIcon = ImageCache.getImage("data/images/heart.png");
        basketIcon = ImageCache.getImage("data/images/basket.png");
        clockIcon = ImageCache.getImage("data/images/clock.png");
        hudBackground = ImageCache.getImage("data/images/hud_background.png");
        
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

        paused = false;
        invincible = false;
        yogi = null;

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
            g.setColor(Color.WHITE);
            g.setFont(pixelFont.deriveFont(20f));
            String text = "PAUSED";
            int textWidth = g.getFontMetrics().stringWidth(text);
            g.drawString(text, getWidth() / 2 - textWidth / 2, getHeight() / 2);
        }
        
        if (gameOver) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.WHITE);
            g.setFont(pixelFont.deriveFont(20f));
            String text = "GAME OVER";
            int textWidth = g.getFontMetrics().stringWidth(text);
            g.drawString(text, getWidth() / 2 - textWidth / 2, getHeight() / 2);
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
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int barHeight = 50;
        int y = getHeight() - barHeight;

        // background
        g2.drawImage(hudBackground, 0, y, getWidth(), barHeight, null);

        // line
        g2.setColor(new Color(255, 255, 255, 60));
        g2.drawLine(0, y, getWidth(), y);

        int iconSize = 32;
        int iconY = y + (barHeight - iconSize) / 2;
        int textY = y + barHeight / 2 + 8;

        g2.setFont(pixelFont);
        g2.setColor(Color.WHITE);

        // one third for placement
        int third = getWidth() / 3;
        FontMetrics fm = g2.getFontMetrics();

        // lives
        String livesText = "x " + yogi.getLives();
        int livesTextW = fm.stringWidth(livesText);
        int livesGroupW = iconSize + 6 + livesTextW;
        int x1 = third / 4 - livesGroupW / 2;
        g2.drawImage(heartIcon, x1, iconY, iconSize, iconSize, null);
        drawShadowedString(g2, livesText, x1 + iconSize + 6, textY);

        // baskets
        String basketText = "x " + yogi.getBasketsCollected();
        int basketTextW = fm.stringWidth(basketText);
        int basketGroupW = iconSize + 6 + basketTextW;
        int x2 = 3 * third / 4 - basketGroupW / 2;
        g2.drawImage(basketIcon, x2, iconY, iconSize, iconSize, null);
        drawShadowedString(g2, basketText, x2 + iconSize + 6, textY);

        // level
        String levelText = "LEVEL " + (currentLevel.getLevelNumber() + 1);
        int levelTextWidth = g2.getFontMetrics().stringWidth(levelText);
        drawShadowedString(g2, levelText, getWidth() / 2 - levelTextWidth / 2, textY);

        // time
        String timeText = formatTime(elapsedTime);
        int timeTextW = fm.stringWidth(timeText);
        int timeGroupW = iconSize + 6 + timeTextW;
        int x4 = third * 2 + third / 2 - timeGroupW / 2;
        g2.drawImage(clockIcon, x4, iconY, iconSize, iconSize, null);
        drawShadowedString(g2, timeText, x4 + iconSize + 6, textY);
    }
    
    private void drawShadowedString(Graphics2D g2, String text, int x, int y) {
        g2.setColor(Color.BLACK);
        g2.drawString(text, x + 3, y + 3);
        g2.setColor(Color.WHITE);
        g2.drawString(text, x, y);
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
