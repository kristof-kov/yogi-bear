package yogibear;

import java.awt.Component;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author kovi
 */
public class ScoreManager {
    
    private static final String DB_URL = "jdbc:derby:" + System.getProperty("user.dir") + "/yogibearDB;create=true";
    private Connection connection;

    public ScoreManager() {
        initDatabase();
    }
    
    /**
     * Initializes the database connection.
     */
    private void initDatabase() {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            connection = DriverManager.getConnection(DB_URL);
            
            DatabaseMetaData meta = connection.getMetaData();
            ResultSet tables = meta.getTables(null, null, "HIGHSCORES", null);
            
            if (!tables.next()) {
                String createTableSQL = "CREATE TABLE highscores (" +
                        "id INT NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY," +
                        "player_name VARCHAR(100) NOT NULL," +
                        "baskets_collected INT NOT NULL," +
                        "time_elapsed BIGINT NOT NULL," +
                        "date_achieved TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                        ")";
                
                Statement stmt = connection.createStatement();
                stmt.execute(createTableSQL);
                stmt.close();
                System.out.println("Highscores table created.");
            }
            tables.close();
            
        } catch (ClassNotFoundException e) {
            System.err.println("Derby driver not found: " + e.getMessage());
            System.err.println("Make sure derby.jar is on the classpath!");
        } catch (SQLException e) {
            System.err.println("Database initialisation error: " + e.getMessage());
        }
    }
    
    /**
     * Saves a new score to the database.
     * 
     * @param playerName the player's name
     * @param basketsCollected number of baskets collected
     * @param timeElapsed elapsed game time in milliseconds
     */
    public void addScore(String playerName, int basketsCollected, long timeElapsed) {
        String insertSQL = "INSERT INTO highscores (player_name, baskets_collected, time_elapsed) VALUES (?, ?, ?)";
        
        try {
            PreparedStatement pstmt = connection.prepareStatement(insertSQL);
            pstmt.setString(1, playerName);
            pstmt.setInt(2, basketsCollected);
            pstmt.setLong(3, timeElapsed);
            pstmt.executeUpdate();
            pstmt.close();
            System.out.println("Score saved: " + playerName + " - " + basketsCollected + " baskets");
        } catch (SQLException e) {
            System.err.println("Error saving score: " + e.getMessage());
        }
    }
    
    /**
     * Retrieves the top scores from the database.
     * 
     * Sorted primarily by baskets collected (descending),
     * secondarily by time elapsed (ascending).
     * 
     * @param limit maximum number of results to return
     * @return list of top scores
     */
    public List<HighScore> getTopScores(int limit) {
        List<HighScore> scores = new ArrayList<>();
        String selectSQL = "SELECT player_name, baskets_collected, time_elapsed, date_achieved " +
                          "FROM highscores " +
                          "ORDER BY baskets_collected DESC, time_elapsed ASC " +
                          "FETCH FIRST ? ROWS ONLY";
        
        try {
            PreparedStatement pstmt = connection.prepareStatement(selectSQL);
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                HighScore score = new HighScore(
                    rs.getString("player_name"),
                    rs.getInt("baskets_collected"),
                    rs.getLong("time_elapsed"),
                    rs.getTimestamp("date_achieved")
                );
                scores.add(score);
            }
            
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.err.println("Error retrieving scores: " + e.getMessage());
        }
        
        return scores;
    }
    
    
    /**
     * Displays the leaderboard in a Swing dialog.
     * 
     * @param parent the parent component to attach the dialog to
     */
    public void showLeaderboard(Component parent) {
        List<HighScore> topScores = getTopScores(10);
        
        if (topScores.isEmpty()) {
            JOptionPane.showMessageDialog(parent,
                "No scores in the database yet.",
                "Leaderboard Empty",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String[] columnNames = {"Rank", "Name", "Baskets", "Time", "Date"};
        Object[][] data = new Object[topScores.size()][5];
        
        for (int i = 0; i < topScores.size(); i++) {
            HighScore score = topScores.get(i);
            data[i][0] = (i + 1);
            data[i][1] = score.getPlayerName();
            data[i][2] = score.getBasketsCollected();
            data[i][3] = formatTime(score.getTimeElapsed());
            data[i][4] = score.getFormattedDate();
        }
        
        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(80);
        table.getColumnModel().getColumn(3).setPreferredWidth(80);
        table.getColumnModel().getColumn(4).setPreferredWidth(150);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new java.awt.Dimension(550, 300));
        
        JOptionPane.showMessageDialog(parent, scrollPane, 
                "Top 10 Scores", JOptionPane.PLAIN_MESSAGE);
    }
    
    /**
     * Converts elapsed time from milliseconds to a mm:ss formatted string.
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
    
    /**
     * Closes the database connection.
     */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing the database: " + e.getMessage());
        }
    }

}
