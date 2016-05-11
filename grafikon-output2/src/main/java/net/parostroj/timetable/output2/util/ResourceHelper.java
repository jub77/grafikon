package net.parostroj.timetable.output2.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import net.parostroj.timetable.output2.Translator;
import net.parostroj.timetable.utils.ResourceBundleUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;

/**
 * Helper methods.
 *
 * @author jub
 */
public class ResourceHelper {

    private static final Logger log = LoggerFactory.getLogger(ResourceHelper.class);

    public static String readResource(final String filename, final ClassLoader cl) {
        try {
            ByteSource bs = new ByteSource() {
                @Override
                public InputStream openStream() throws IOException {
                    InputStream fis = getStream(filename, cl);
                    return fis;
                }
            };
            CharSource cs = bs.asCharSource(StandardCharsets.UTF_8);
            return cs.read();
        } catch (IOException e) {
            log.warn(e.getMessage(), e);
            return "";
        }
    }

    public static InputStream getStream(String resource, ClassLoader classLoader) {
        if (classLoader != null)
            return classLoader.getResourceAsStream(resource);
        else
            return ClassLoader.getSystemResourceAsStream(resource);
    }

    public static void addTextsToMap(Map<String, Object> map, String prefix, Locale locale, String bundleName) {
        ResourceBundle bundle = ResourceBundleUtil.getBundle(bundleName, ResourceHelper.class.getClassLoader(), locale, Locale.ENGLISH);
        int prefixLength = prefix.length();
        for (String key : bundle.keySet()) {
            if (key.startsWith(prefix)) {
                map.put(key.substring(prefixLength), bundle.getString(key));
            }
        }
    }

    public static Translator getBundleTranslator(String bundleName, String... prefixes) {
        return (key, locale) -> {
            ResourceBundle bundle = ResourceBundleUtil.getBundle(bundleName, ResourceHelper.class.getClassLoader(), locale, Locale.ENGLISH);
            String text = null;
            for (String prefix : prefixes) {
                String pKey = prefix + key;
                if (bundle.containsKey(pKey)) {
                    text = bundle.getString(pKey);
                    break;
                }
            }
            return text == null ? bundle.getString(key) : text;
        };
    }
}
