package net.parostroj.timetable.writers;

import java.io.IOException;
import java.util.Formatter;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.utils.TimeConverter;

/**
 * This class writes line timetable.
 *
 * @author jub
 */
public class LineTimetableWriter {

    private static final String FORMAT = "%02d:%02d";

    /**
     * writes line timetable.
     *
     * @param line line
     * @param str appendable
     */
    public void writeLineTimetable(Line line, Appendable str) throws IOException {
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
                Formatter f = new Formatter(str);
                f.format("%1$-20s", interval.getTrain().getCompleteName());
                str.append(TimeConverter.formatIntToText(interval.getStart(), FORMAT));
                str.append(" ").append(TimeConverter.formatIntToText(interval.getEnd(), FORMAT));
                str.append(" [direction: ").append(interval.getTo().getAbbr()).append("]");
                str.append('\n');
                f.close();
            }
        }
    }
}
