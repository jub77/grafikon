package net.parostroj.timetable.writers;

import java.io.IOException;
import java.util.Formatter;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.utils.TimeConverter;

/**
 * This class writes train timetable.
 *
 * @author jub
 */
public class TrainTimetableWriter {

    private static final String FORMAT = "%02d:%02d";

    /**
     * writes train timetable.
     *
     * @param train train
     * @param str appendable
     */
    public void writeTrainTimetable(Train train, Appendable str) throws IOException {
        str.append("Train: ");
        str.append(train.getCompleteName()).append('\n');
        for (TimeInterval time : train.getTimeIntervalList()) {
            Node node = time.getOwner().asNode();
            if (node != null) {
                Formatter f = new Formatter(str);
                f.format("%1$-20s", node.getName());
                if (time.isFirst() || !time.isStop()) {
                    str.append("      ").append(TimeConverter.formatIntToText(time.getEnd(), FORMAT)).append("\n");
                } else if (time.isLast()) {
                    str.append(TimeConverter.formatIntToText(time.getStart(), FORMAT)).append("\n");
                } else if (time.isStop()) {
                    str.append(TimeConverter.formatIntToText(time.getStart(), FORMAT)).append(" ");
                    str.append(TimeConverter.formatIntToText(time.getEnd(), FORMAT)).append("\n");
                } else {
                    str.append('\n');
                }
            }
        }
    }
}
