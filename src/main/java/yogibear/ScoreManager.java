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
     * Inicializálja az adatbázis-kapcsolatot.
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
                System.out.println("Highscores tábla létrehozva.");
            }
            tables.close();
            
        } catch (ClassNotFoundException e) {
            System.err.println("Derby driver nem található: " + e.getMessage());
            System.err.println("Győződj meg róla, hogy a derby.jar a classpath-ban van!");
        } catch (SQLException e) {
            System.err.println("Adatbázis inicializálási hiba: " + e.getMessage());
        }
    }
    
    /**
     * Új pontszámot ment el az adatbázisba
     * 
     * @param playerName a játékos neve
     * @param basketsCollected összegyűjtött kosarak száma
     * @param timeElapsed a játék ideje ezredmásodpercben
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
            System.out.println("Pontszám mentve: " + playerName + " - " + basketsCollected + " kosár");
        } catch (SQLException e) {
            System.err.println("Hiba a pontszám mentésekor: " + e.getMessage());
        }
    }
    
    /**
     * Lekéri az adatbázis legjobb pontszámait.
     * 
     * Rendezés: 
     * elsődlegesen a kosarak szerint,
     * másodlagosan az idő szerint
     * 
     * @param limit a visszaadott eredmények maximális száma
     * @return a legjobb eredményeket tartalmazó listát
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
            System.err.println("Hiba a pontszámok lekérésekor: " + e.getMessage());
        }
        
        return scores;
    }
    
    
    /**
     * Megjeleníti a ranglistát egy Swing párbeszédablakban.
     * 
     * 
     * @param parent a szülő komponens, melyhez az ablak tartozik
     */
    public void showLeaderboard(Component parent) {
        List<HighScore> topScores = getTopScores(10);
        
        if (topScores.isEmpty()) {
            JOptionPane.showMessageDialog(parent,
                "Még nincs eredmény az adatbázisban.",
                "Ranglista üres",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String[] columnNames = {"Helyezés", "Név", "Kosarak", "Idő", "Dátum"};
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
                "Top 10 Eredmény", JOptionPane.PLAIN_MESSAGE);
    }
    
    /**
     * Az eltelt időt ezredmásodpercből perc:másodperc
     * formátumú szöveggé alakítja.
     * 
     * @param millis eltelt idő ezredmásodpercben
     * @return az idő formázva (mm:ss)
     */
    private String formatTime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
    
    /**
     * Lezárja az adatbázis-kapcsolatot.
     */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Hiba az adatbázis lezárásakor: " + e.getMessage());
        }
    }

}
