package net.parostroj.timetable.model.save;

import net.parostroj.timetable.model.SortPattern;
import net.parostroj.timetable.model.SortPatternGroup;

/**
 * Sort pattern.
 * 
 * @author jub
 */
public class LSSortPattern {
    
    private String pattern;
    
    private LSSortPatternGroup[] rule;
    
    public LSSortPattern() {}
    
    public LSSortPattern(SortPattern sPattern) {
        pattern = sPattern.getPattern();
        rule = new LSSortPatternGroup[sPattern.getGroups().size()];
        int i = 0;
        for (SortPatternGroup group : sPattern.getGroups()) {
            rule[i++] = new LSSortPatternGroup(group.getGroup(), group.getType().getId());
        }
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public LSSortPatternGroup[] getRule() {
        return rule;
    }

    public void setRule(LSSortPatternGroup[] rule) {
        this.rule = rule;
    }
    
    public SortPattern getSortPattern() {
        SortPattern sPattern = new SortPattern(pattern);
        // no rules, no pattern
        if (rule == null)
            return null;
        for (LSSortPatternGroup g : rule) {
            sPattern.getGroups().add(new SortPatternGroup(g.getGroup(), SortPatternGroup.Type.fromId(g.getType())));
        }
        return sPattern;
    }
}
