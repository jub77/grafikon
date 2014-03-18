package net.parostroj.timetable.model;

import java.util.LinkedList;
import java.util.List;

/**
 * Sorting information for trains.
 * 
 * @author jub
 */
public class SortPattern {

    private String pattern;
    private List<SortPatternGroup> groups;

    public SortPattern(String pattern) {
        this.pattern = pattern;
        this.groups = new LinkedList<SortPatternGroup>();
    }

    public List<SortPatternGroup> getGroups() {
        return groups;
    }

    public String getPattern() {
        return pattern;
    }
}