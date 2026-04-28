package yogibear;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 *
 * @author kovi
 */
public class YogiBearGUI {
    private final JFrame frame;
    private GameEngine gameArea;

    public YogiBearGUI() {
        frame = new JFrame("Yogi Bear");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                gameArea.closeResources();
                System.exit(0);
            }
        });
        frame.setLayout(new BorderLayout());
        
        // Menüsáv
        JMenuBar menuBar = new JMenuBar();
        
        JMenu gameMenu = new JMenu("Game");
        JMenuItem newGameItem = new JMenuItem("New Game");
        JMenuItem leaderboardItem = new JMenuItem("Leaderboard");
        JMenuItem exitItem = new JMenuItem("Exit");
        
        newGameItem.addActionListener(e -> startNewGame());
        leaderboardItem.addActionListener(e -> showLeaderboard());
        exitItem.addActionListener(e -> System.exit(0));
        
        gameMenu.add(newGameItem);
        gameMenu.add(leaderboardItem);
        gameMenu.addSeparator();
        gameMenu.add(exitItem);
        
        JMenu helpMenu = new JMenu("Help");
        JMenuItem controlsItem = new JMenuItem("Controls");
        
        controlsItem.addActionListener(e -> showControls());
        
        helpMenu.add(controlsItem);
        
        menuBar.add(gameMenu);
        menuBar.add(helpMenu);
        
        frame.setJMenuBar(menuBar);
        
        // Játékterület
        gameArea = new GameEngine();
        frame.add(gameArea, BorderLayout.CENTER);
        
        frame.setPreferredSize(new Dimension(800, 600));
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
        gameArea.requestFocusInWindow();
    }
    
    private void startNewGame() {
        frame.remove(gameArea);
        gameArea = new GameEngine();
        frame.add(gameArea, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
        gameArea.requestFocusInWindow();
    }
    
    private void showLeaderboard() {
        gameArea.showLeaderboard();
    }
    
    private void showControls() {
        javax.swing.JOptionPane.showMessageDialog(frame,
            "Controls:\n\n" +
            "W - Up\n" +
            "S - Down\n" +
            "A - Left\n" +
            "D - Right\n" +
            "ESC - Pause\n\n" +
            "Goal: Collect all the picnic baskets,\n" +
            "but avoid the rangers!",
            "Controls",
            javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }
}
