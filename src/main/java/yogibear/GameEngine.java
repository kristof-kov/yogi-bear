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
    
    // Időmérő
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
     * Beállítja a billentyűzetes irányítást.
     * WASD mozgás, ESC szünet.
     * 
     * Csak akkor indul mozgás, ha nincs megállítva
     * vagy vége a játéknak.
     */
    private void setupKeyBindings() {
        // WASD mozgás
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
     * Új játékot indít.
     * 
     * Nullázza az eltelt időt, visszaállítja a gameOver 
     * állapotot és betölti az első pályát.
     */
    public void startNewGame() {
        gameStartTime = System.currentTimeMillis();
        elapsedTime = 0;
        gameOver = false;
        loadLevel(0);
    }
    
    /**
     * Betölti a megadott sorszámú pályát.
     * 
     * @param levelNum betölteni kívánt pálya sorszáma
     */
    private void loadLevel(int levelNum) {
        try {
            currentLevel = new Level(levelNum);
            Image yogiImage = ImageCache.getImage("data/images/yogi_sheet.png");
            
            if (yogi == null) {
                // Első pálya
                yogi = new Yogi(currentLevel.getYogiStartX(), currentLevel.getYogiStartY(), 
                                YOGI_SIZE, YOGI_SIZE, yogiImage);
            } else {
                // Többi pálya
                int currentLives = yogi.getLives();
                int currentBaskets = yogi.getBasketsCollected();
                
                yogi = new Yogi(currentLevel.getYogiStartX(), currentLevel.getYogiStartY(), 
                                YOGI_SIZE, YOGI_SIZE, yogiImage);
                
                yogi.setLives(currentLives);
                yogi.setBasketsCollected(currentBaskets);
            }
        } catch (IOException ex) {
            System.err.println("Hiba a pálya betöltésekor: " + ex.getMessage());
            // Ha nincs több pálya, nyertünk
            if (levelNum > 0) {
                gameWon();
            }
        }
    }
    
    /**
     * A következő pályára léptet. Ha elértük a maximális
     * pályaszámot, akkor győzelemmel lezárja a játékot.
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
     * Győzelmi állapotot állít be és bekéri a játékos nevét.
     * Elmenti a pontszámot az adatbázisba.
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
     * Játék vége állapotot állít be és bekéri a játékos nevét.
     * Elmenti a pontszámot az adatbázisba.
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
     * A panel kirajzolását végzi.
     * 
     * Kirajzolja a hátteret, a pályát, Maci Lacit, valamint
     * megállítás vagy a játék vége esetén az overlayt
     * 
     * @param g a kirajzoláshoz használt grafikus kontextus
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Háttér
        g.drawImage(background, 0, 0, getWidth(), getHeight(), null);
        
        // Pálya elemek
        if (currentLevel != null) {
            currentLevel.draw(g);
        }
        
        // Maci Laci (villogás invincibility alatt)
        if (yogi != null && (!invincible || (System.currentTimeMillis() / 200) % 2 == 0)) {
            yogi.draw(g);
        }
        
        // HUD
        drawHUD(g);
        
        // Paused vagy Game Over üzenet
        if (paused) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
        
        if (gameOver) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            g.drawString("GAME OVER", getWidth() / 2 - 80, getHeight() / 2);
        }
    }
    
    /**
     * Kirajzolja a HUD-ot (életek, kosarak, pálya száma, eltelt idő).
     * Jobb felső sarokban, félig átlátszó háttérrel.
     * 
     * @param g a kirajzoláshoz használt grafikus kontextus
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
        
        // Életek
        g.drawString("Lives: " + yogi.getLives(), textX, y + 25);
        
        // Összesen gyűjtött
        g.drawString("Baskets: " + yogi.getBasketsCollected(), textX, y + 50);
        
        // Pálya szám
        g.drawString("Level: " + (currentLevel.getLevelNumber() + 1), textX, y + 75);
        
        // Idő
        g.drawString("Time: " + formatTime(elapsedTime), textX, y + 100);

    }
    
    /**
     * Az eltelt időt ezredmásodpercből perc:másodperc formátumú
     * szöveggé alakítja.
     * 
     * @param millis eltelt idő ezredmásodpercben
     * @return formázott idő (mm:ss)
     */
    private String formatTime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    
    class GameLoopListener implements ActionListener {
        /**
         * A játékciklus időzített eseménykezelője.
         * 
         * Minden tick esetén (ha nincs vége vagy megállítva a játék):
         * frissíti az eltelt időt, 
         * kezeli az invincibility-t,
         * mozgatja Maci Lacit és kezeli az ütközéseket,
         * kezeli a kosárgyűjtést,
         * frissíti a vadőrök mozgását és a detektálást,
         * ellenőrzi a pálya teljesítését.
         * 
         * A tick végén újrarajzol.
         * 
         * @param e az időzítő által küldött esemény
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!paused && !gameOver) {
                // Időmérő frissítés
                elapsedTime = System.currentTimeMillis() - gameStartTime;
                
                // Invincibility timer
                if (invincible && System.currentTimeMillis() - invincibilityStart > INVINCIBILITY_DURATION) {
                    invincible = false;
                }
                
                // Maci Laci mozgás
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
                
                // Ütközés akadályokkal
                if (currentLevel.checkCollisionWithObstacles(yogi, yogi.getX(), yogi.getY())) {
                    yogi.setX(oldX);
                    yogi.setY(oldY);
                }
                
                // Kosár gyűjtés
                Basket collected = currentLevel.checkBasketCollection(yogi);
                if (collected != null) {
                    yogi.collectBasket();
                    SoundManager.play("pickup");
                }
                
                // Vadőrök járőrözése és detektálás
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
                
                // Pálya befejezése
                if (currentLevel.isCompleted()) {
                    SoundManager.play("success");
                    nextLevel();
                }
            }
            
            repaint();
        }
    }
    
    /**
     * Megjeleníti a ranglistát egy Swing párbeszédablakban.
     * A ranglista adatait a ScoreManager szolgáltatja.
     */
    public void showLeaderboard() {
        scoreManager.showLeaderboard(this);
    }
    
    public void closeResources() {
        scoreManager.close();
    }
}
