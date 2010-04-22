package net.parostroj.timetable.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.Train;

/**
 * Sort and filter.
 *
 * @author jub
 */
public class TrainSortByNodeFilter {
    public List<Train> sortAndFilter(Collection<Train> trains, Node node) {
        List<TimeInterval> result = new ArrayList<TimeInterval>();
        for (Train train : trains) {
            for (TimeInterval interval : train.getTimeIntervalList()) {
                if (interval.isNodeOwner() && node == interval.getOwner()) {
                    result.add(interval);
                    // break inner cycle
                    break;
                }
            }
        }
        // sort result
        Collections.sort(result, new Comparator<TimeInterval>() {

            @Override
            public int compare(TimeInterval o1, TimeInterval o2) {
                return Integer.valueOf(o1.getStart()).compareTo(Integer.valueOf(o2.getStart()));
            }
        });

        // create list of train
        List<Train> trainsOut = new ArrayList<Train>(result.size());
        for (TimeInterval interval : result) {
            trainsOut.add(interval.getTrain());
        }

        return trainsOut;
    }
}
