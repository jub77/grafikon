package net.parostroj.timetable.utils;

import java.util.Map;

/**
 * Interface for string with substitution.
 *
 * @author jub
 */
public interface SubstitutedString {

    /**
     * Substitute parts of string from binding.
     *
     * @param binding binding
     * @return substituted string
     */
    String substitute(Map<String, Object> binding);
}
