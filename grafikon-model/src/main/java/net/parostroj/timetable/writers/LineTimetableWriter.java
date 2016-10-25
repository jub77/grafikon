package net.parostroj.timetable.writers;

import java.io.IOException;
import java.util.Formatter;

import net.parostroj.timetable.model.*;

/**
 * This class writes line timetable.
 *
 * @author jub
 */
public class LineTimetableWriter {

    /**
     * writes line timetable.
     *
     * @param line line
     * @param str appendable
     */
    public void writeLineTimetable(Line line, Appendable str) throws IOException {
    	TimeConverter c = line.getDiagram().getTimeConverter();

        // get end nodes ...
        Node ss = line.getFrom();
        Node st = line.getTo();

        str.append("Line: ");
        str.append(ss.getName());
        str.append(" - ");
        str.append(st.getName());
        str.append('\n');

        for (LineTrack track : line.getTracks()) {
            for (TimeInterval interval : track.getTimeIntervalList()) {
                @SuppressWarnings("resource")
                Formatter f = new Formatter(str);
                f.format("%1$-20s", interval.getTrain().getDefaultCompleteName());
                str.append(c.convertIntToTextFull(interval.getStart(), true));
                str.append(" ").append(c.convertIntToTextFull(interval.getEnd(), true));
                str.append(" [direction: ").append(interval.getTo().getAbbr()).append("]");
                str.append('\n');
                f.flush();
            }
        }
    }
}
