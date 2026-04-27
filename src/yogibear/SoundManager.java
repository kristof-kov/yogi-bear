package yogibear;

import javax.sound.sampled.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author kovi
 */
public class SoundManager {
    
    private static final Map<String, Clip> sounds = new HashMap<>();
    
    /**
     * Betölt egy hangfájlt a memóriába a megadott névvel.
     * 
     * @param name a hang azonosítója
     * @param path a hangfájl elérési útja
     */
    public static void load(String name, String path) {
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(new File(path));
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            sounds.put(name, clip);
        } catch (Exception e) {
            System.err.println("Error when importing sound: " + path + " - " + e.getMessage());
        }
    }
    
    /**
     * Lejétsza a megadott nevű hangot.
     * 
     * @param name a lejátszandó hang azonosítója
     */
    public static void play(String name) {
        Clip clip = sounds.get(name);
        if (clip != null) {
            clip.setFramePosition(0);
            clip.start();
        }
    }
    
    /**
     * Ismételve lejátssza a megadott nevű hangot.
     * 
     * @param name a lejátszandó hang azonosítója
     */
    public static void loop(String name) {
        Clip clip = sounds.get(name);
        if (clip != null && !clip.isRunning()) {
            clip.setFramePosition(0);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }
    
    /**
     * Leállítja a megadott nevű hangot, ha éppen játszódik.
     * 
     * @param name a megállítandó hang azonosítója
     */
    public static void stop(String name) {
        Clip clip = sounds.get(name);
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }
}