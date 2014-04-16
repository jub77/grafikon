package net.parostroj.timetable.output2.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import net.parostroj.timetable.utils.ResourceBundleUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;

/**
 * Helper methods.
 *
 * @author jub
 */
public class ResourceHelper {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceHelper.class.getName());

    public static String readResource(final String filename, final ClassLoader cl) {
        try {
            ByteSource bs = new ByteSource() {
                @Override
                public InputStream openStream() throws IOException {
                    InputStream fis = (cl != null) ? cl.getResourceAsStream(filename) : ClassLoader
                            .getSystemResourceAsStream(filename);
                    return fis;
                }
            };
            CharSource cs = bs.asCharSource(Charsets.UTF_8);
            return cs.read();
        } catch (IOException e) {
            LOG.warn(e.getMessage(), e);
            return "";
        }
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
}
