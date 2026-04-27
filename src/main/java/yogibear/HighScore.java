package yogibear;

import java.sql.Timestamp;

/**
 *
 * @author kovi
 */
public class HighScore {
    
    private final String playerName;
    private final int basketsCollected;
    private final long timeElapsed;
    private final Timestamp dateAchieved;

    public HighScore(String playerName, int basketsCollected, long timeElapsed, Timestamp dateAchieved) {
        this.playerName = playerName;
        this.basketsCollected = basketsCollected;
        this.timeElapsed = timeElapsed;
        this.dateAchieved = dateAchieved;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getBasketsCollected() {
        return basketsCollected;
    }

    public long getTimeElapsed() {
        return timeElapsed;
    }
    
    /**
     * Visszaadja az eredmény dátumát formázott szövegként,
     * év-hónap-nap óra:perc formátumban.
     * 
     * @return az elérés dátuma YYYY-MM-DD HH:MM formátumban
     */
    public String getFormattedDate() {
        return dateAchieved.toString().substring(0, 16);
    }
}
