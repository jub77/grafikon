package net.parostroj.timetable.model.ls.impl4;

import java.util.LinkedList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlType;
import net.parostroj.timetable.model.SortPattern;
import net.parostroj.timetable.model.SortPatternGroup;

/**
 * Storage for sort pattern.
 *
 * @author jub
 */
@XmlType(propOrder = {"pattern", "rules"})
public class LSSortPattern {

    private String pattern;
    private List<LSSortPatternGroup> rules;

    public LSSortPattern() {
    }

    public LSSortPattern(SortPattern sPattern) {
        pattern = sPattern.getPattern();
        rules = new LinkedList<>();
        for (SortPatternGroup group : sPattern.getGroups()) {
            rules.add(new LSSortPatternGroup(group.getGroup(), group.getType().getId()));
        }
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    @XmlElementWrapper
    @XmlElement(name = "rule")
    public List<LSSortPatternGroup> getRules() {
        return rules;
    }

    public void setRules(List<LSSortPatternGroup> rules) {
        this.rules = rules;
    }

    public SortPattern createSortPattern() {
        SortPattern sPattern = new SortPattern(pattern);
        // no rules, no pattern
        if (this.rules == null) {
            return null;
        }
        for (LSSortPatternGroup g : this.rules) {
            sPattern.getGroups().add(new SortPatternGroup(g.getGroup(), SortPatternGroup.Type.fromId(g.getType())));
        }
        return sPattern;
    }
}
