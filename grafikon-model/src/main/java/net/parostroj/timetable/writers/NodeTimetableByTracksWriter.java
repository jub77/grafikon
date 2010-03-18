package net.parostroj.timetable.writers;

import java.io.IOException;
import java.util.Formatter;
import java.util.List;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.NodeTrack;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.utils.TimeConverter;

/**
 * This class writes node timetable divided by tracks.
 *
 * @author jub
 */
public class NodeTimetableByTracksWriter {

    private static final String FORMAT = "%02d:%02d";

    /**
     * writes node timetable.
     *
     * @param node node
     * @param str appendable
     */
    public void writeNodeTimetable(Node node, Appendable str) throws IOException {
        str.append("Node: ");
        str.append(node.getName()).append('\n');
        List<NodeTrack> tracks = node.getTracks();

        for (NodeTrack track : tracks) {
            str.append("Track: " + track.getNumber()).append('\n');
            for (TimeInterval interval : track.getTimeIntervalList()) {
                Formatter f = new Formatter(str);
                f.format("%1$-20s", interval.getTrain().getCompleteName());
                str.append(TimeConverter.formatIntToText(interval.getStart(), FORMAT));
                str.append(" ").append(TimeConverter.formatIntToText(interval.getEnd(), FORMAT)).append("\n");
            }
        }
    }
}
