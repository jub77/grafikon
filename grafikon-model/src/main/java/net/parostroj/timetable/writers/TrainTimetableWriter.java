package net.parostroj.timetable.writers;

import java.io.IOException;
import java.util.Formatter;

import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.TimeConverter;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.Train;

/**
 * This class writes train timetable.
 *
 * @author jub
 */
public class TrainTimetableWriter {

    /**
     * writes train timetable.
     *
     * @param train train
     * @param str appendable
     */
    public void writeTrainTimetable(Train train, Appendable str) throws IOException {
    	TimeConverter c = train.getDiagram().getTimeConverter();

        str.append("Train: ");
        str.append(train.getCompleteName()).append('\n');
        for (TimeInterval time : train.getTimeIntervalList()) {
            Node node = time.getOwnerAsNode();
            if (node != null) {
                @SuppressWarnings("resource")
                Formatter f = new Formatter(str);
                f.format("%1$-20s", node.getName());
                if (time.isFirst() || !time.isStop()) {
                    str.append("      ").append(c.convertIntToText(time.getEnd(), true)).append("\n");
                } else if (time.isLast()) {
                    str.append(c.convertIntToText(time.getStart(), true)).append("\n");
                } else if (time.isStop()) {
                    str.append(c.convertIntToText(time.getStart(), true)).append(" ");
                    str.append(c.convertIntToText(time.getEnd(), true)).append("\n");
                } else {
                    str.append('\n');
                }
                f.flush();
            }
        }
    }
}
