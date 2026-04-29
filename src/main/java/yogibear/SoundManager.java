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
     * Loads a sound file into memory under the given name.
     * 
     * @param name identifier for the sound
     * @param path file path of the sound
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
     * Plays the sound with the given name.
     * 
     * @param name identifier of the sound to play
     */
    public static void play(String name) {
        Clip clip = sounds.get(name);
        if (clip != null) {
            clip.setFramePosition(0);
            clip.start();
        }
    }
    
    /**
     * Plays the sound with the given name on a continuous loop.
     * 
     * @param name identifier of the sound to loop
     */
    public static void loop(String name) {
        Clip clip = sounds.get(name);
        if (clip != null && !clip.isRunning()) {
            clip.setFramePosition(0);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }
    
    /**
     * Stops the sound with the given name if it is currently playing.
     * 
     * @param name identifier of the sound to stop
     */
    public static void stop(String name) {
        Clip clip = sounds.get(name);
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }
    
    /**
     * Returns true if the sound with the given name is currently playing.
     * 
     * @param name identifier of the sound
     * @return true if playing, false otherwise
     */
    public static boolean isPlaying(String name) {
        Clip clip = sounds.get(name);
        return clip != null && clip.isRunning();
    }
}