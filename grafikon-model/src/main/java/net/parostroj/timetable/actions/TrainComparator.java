package net.parostroj.timetable.actions;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.parostroj.timetable.model.SortPattern;
import net.parostroj.timetable.model.SortPatternGroup;
import net.parostroj.timetable.model.Train;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Comparator that uses train numbers.
 *
 * @author jub
 */
public class TrainComparator implements Comparator<Train> {

    private static final Logger log = LoggerFactory.getLogger(TrainComparator.class);

    public enum Type {ASC, DESC; }

    private final Type type;

    private final Pattern pattern;

    private final SortPattern sortPattern;

    public TrainComparator(Type type, SortPattern pattern) {
        this.type = type;
        this.pattern = Pattern.compile(pattern.getPattern());
        this.sortPattern = pattern;
    }

    @Override
    public int compare(Train o1, Train o2) {
        // checks
        if (sortPattern.getGroups().isEmpty()) {
            log.error("Pattern groups are empty.");
            throw new IllegalArgumentException("Pattern groups are empty.");
        }
        Matcher m1 = pattern.matcher(o1.getNumber());
        Matcher m2 = pattern.matcher(o2.getNumber());
        if (!m1.matches() || !m2.matches()) {
            log.error("Pattern doesn't match: {}", sortPattern.getPattern());
            throw new IllegalArgumentException("Pattern doesn't match: " + sortPattern.getPattern());
        }
        // loop
        for (SortPatternGroup group : sortPattern.getGroups()) {
            String s1 = m1.group(group.getGroup());
            String s2 = m2.group(group.getGroup());
            // switch
            if (type == Type.DESC) {
                String aux = s1;
                s1 = s2;
                s2 = aux;
            }
            int res = 0;
            switch (group.getType()) {
                case STRING:
                    res = s1.compareTo(s2);
                    if (res != 0)
                        return res;
                    break;
                case NUMBER:
                    Integer tn1 = ("".equals(s1))? Integer.MAX_VALUE : Integer.valueOf(s1);
                    Integer tn2 = ("".equals(s2))? Integer.MAX_VALUE : Integer.valueOf(s2);
                    res = tn1.compareTo(tn2);
                    if (res != 0)
                        return res;
                    break;
            }
        }
        return 0;
    }
}
