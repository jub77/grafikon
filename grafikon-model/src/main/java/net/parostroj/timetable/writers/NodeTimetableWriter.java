package net.parostroj.timetable.writers;

import java.io.IOException;
import java.util.Formatter;
import java.util.List;

import net.parostroj.timetable.model.*;

/**
 * This class writes node timetable.
 *
 * @author jub
 */
public class NodeTimetableWriter {

    /**
     * writes node timetable.
     *
     * @param node node
     * @param str appendable
     */
    public void writeNodeTimetable(Node node, Appendable str) throws IOException {
    	TimeConverter c = node.getTrainDiagram().getTimeConverter();

    	str.append("Node: ");
        str.append(node.getName()).append('\n');
        List<NodeTrack> tracks = node.getTracks();

        TimeIntervalList list = new TimeIntervalList();

        for (NodeTrack track : tracks) {
            for (TimeInterval interval : track.getTimeIntervalList()) {
                list.addIntervalByNormalizedStartTime(interval);
            }
        }

        for (TimeInterval item : list) {
            @SuppressWarnings("resource")
            Formatter f = new Formatter(str);
            f.format("%1$-20s", item.getTrain().getCompleteName());
            str.append(c.convertIntToText(item.getStart(), true));
            str.append(" ");
            str.append(c.convertIntToText(item.getEnd(), true));
            str.append(" [track: ");
            str.append(item.getTrack().toString()).append("]\n");
            f.flush();
        }
    }
}
