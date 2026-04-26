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
        frame = new JFrame("Maci Laci - Yogi Bear");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        
        // Menüsáv
        JMenuBar menuBar = new JMenuBar();
        
        JMenu gameMenu = new JMenu("Játék");
        JMenuItem newGameItem = new JMenuItem("Új Játék");
        JMenuItem leaderboardItem = new JMenuItem("Ranglista");
        JMenuItem exitItem = new JMenuItem("Kilépés");
        
        newGameItem.addActionListener(e -> startNewGame());
        leaderboardItem.addActionListener(e -> showLeaderboard());
        exitItem.addActionListener(e -> System.exit(0));
        
        gameMenu.add(newGameItem);
        gameMenu.add(leaderboardItem);
        gameMenu.addSeparator();
        gameMenu.add(exitItem);
        
        JMenu helpMenu = new JMenu("Súgó");
        JMenuItem controlsItem = new JMenuItem("Irányítás");
        
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
            "Irányítás:\n\n" +
            "W - Fel\n" +
            "S - Le\n" +
            "A - Balra\n" +
            "D - Jobbra\n" +
            "ESC - Szünet\n\n" +
            "Cél: Gyűjtsd össze az összes piknik kosarat,\n" +
            "de kerüld el a vadőröket!",
            "Irányítás",
            javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }
}
