package net.parostroj.timetable.output2.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper methods.
 *
 * @author jub
 */
public class ResourceHelper {

    private static final Logger LOG = Logger.getLogger(ResourceHelper.class.getName());

    public static String readResource(String filename) {
        try {
            InputStream fis = ResourceHelper.class.getResourceAsStream(filename);
            Reader in = new InputStreamReader(fis, "utf-8");
            char[] buffer = new char[1000];
            int read;
            StringBuilder result = new StringBuilder();
            while ((read = in.read(buffer)) != -1) {
                result.append(buffer, 0, read);
            }
            in.close();
            return result.toString();
        } catch (IOException e) {
            LOG.log(Level.WARNING, e.getMessage(), e);
            return "";
        }
    }

    public static void addTextsToMap(Map<String, Object> map, String prefix, Locale locale, String bundleName) {
        ResourceBundle bundle = ResourceBundle.getBundle(bundleName, locale);
        int prefixLength = prefix.length();
        for (String key : bundle.keySet()) {
            if (key.startsWith(prefix)) {
                map.put(key.substring(prefixLength), bundle.getString(key));
            }
        }
    }
}
