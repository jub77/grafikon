package net.parostroj.timetable.actions;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.parostroj.timetable.model.SortPattern;
import net.parostroj.timetable.model.SortPatternGroup;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.utils.ObjectsUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Comparator that uses train numbers.
 *
 * @author jub
 */
public class TrainComparator implements Comparator<Train> {

    private static final Logger log = LoggerFactory.getLogger(TrainComparator.class);

    private final Pattern pattern;
    private final SortPattern sortPattern;

    public TrainComparator(SortPattern pattern) {
        this.pattern = Pattern.compile(pattern.getPattern());
        this.sortPattern = pattern;
    }

    @Override
    public int compare(Train o1, Train o2) {
        if (o1 == null) {
            return o2 == null ? 0 : -1;
        } else if (o2 == null) {
            return 1;
        }

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
            int res;
            switch (group.getType()) {
                case STRING:
                    res = s1.compareTo(s2);
                    if (res != 0)
                        return res;
                    break;
                case NUMBER:
                    Integer tn1 = ObjectsUtil.isEmpty(s1) ? Integer.MAX_VALUE : Integer.parseInt(s1);
                    Integer tn2 = ObjectsUtil.isEmpty(s2) ? Integer.MAX_VALUE : Integer.parseInt(s2);
                    res = tn1.compareTo(tn2);
                    if (res != 0)
                        return res;
                    break;
            }
        }
        return 0;
    }
}
