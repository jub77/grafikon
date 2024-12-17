package net.parostroj.timetable.utils;

import java.util.UUID;

/**
 * Generator for creating ids.
 *
 * @author jub
 */
public class IdGenerator {
    /** Instance for singletone. */
    private static final IdGenerator instance = new IdGenerator();

    /**
     * @return singleton instance
     */
    public static IdGenerator getInstance() {
        return instance;
    }

    /**
     * @return generated id
     */
    public String getId() {
        return UUID.randomUUID().toString();
    }
}
