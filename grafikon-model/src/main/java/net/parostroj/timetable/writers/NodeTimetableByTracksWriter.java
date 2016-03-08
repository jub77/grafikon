package net.parostroj.timetable.writers;

import java.io.IOException;
import java.util.Formatter;
import java.util.List;

import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.NodeTrack;
import net.parostroj.timetable.model.TimeConverter;
import net.parostroj.timetable.model.TimeInterval;

/**
 * This class writes node timetable divided by tracks.
 *
 * @author jub
 */
public class NodeTimetableByTracksWriter {

    /**
     * writes node timetable.
     *
     * @param node node
     * @param str appendable
     */
    public void writeNodeTimetable(Node node, Appendable str) throws IOException {
    	TimeConverter c = node.getDiagram().getTimeConverter();

        str.append("Node: ");
        str.append(node.getName()).append('\n');
        List<NodeTrack> tracks = node.getTracks();

        for (NodeTrack track : tracks) {
            str.append("Track: " + track.getNumber()).append('\n');
            for (TimeInterval interval : track.getTimeIntervalList()) {
                @SuppressWarnings("resource")
                Formatter f = new Formatter(str);
                f.format("%1$-20s", interval.getTrain().getCompleteName());
                str.append(c.convertIntToText(interval.getStart(), true));
                str.append(" ").append(c.convertIntToText(interval.getEnd(), true)).append("\n");
                f.flush();
            }
        }
    }
}
