package net.parostroj.timetable.output2.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;

/**
 * Helper methods.
 *
 * @author jub
 */
public final class ResourceHelper {

    private ResourceHelper() {}

    private static final Logger log = LoggerFactory.getLogger(ResourceHelper.class);

    public static String readResource(final String filename, final ClassLoader cl) {
        try {
            ByteSource bs = new ByteSource() {
                @Override
                public InputStream openStream() throws IOException {
                    return getStream(filename, cl);
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
}
