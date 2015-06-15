package net.parostroj.timetable.output2;

import java.io.InputStream;

public interface OutputResources {

    /**
     * Returns resource for given key or null if there is no available one.
     *
     * @param key key for resource
     * @return stream
     */
    InputStream getStream(String key);
}
