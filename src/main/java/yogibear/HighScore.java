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
     * Returns the achievement date as a formatted string
     * in YYYY-MM-DD HH:MM format.
     * 
     * @return the date achieved in YYYY-MM-DD HH:MM format
     */
    public String getFormattedDate() {
        return dateAchieved.toString().substring(0, 16);
    }
}
