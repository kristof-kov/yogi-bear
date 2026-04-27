package yogibear;

import java.awt.Image;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;

/**
 *
 * @author kovi
 */
public class ImageCache {
    
    private static final Map<String, Image> cache = new HashMap<>();
    
    public static Image getImage(String path) {
        if (!cache.containsKey(path)) {
            cache.put(path, new ImageIcon(path).getImage());
        }
        return cache.get(path);
    }
    
    public static void clear() {
        cache.clear();
    }
}
