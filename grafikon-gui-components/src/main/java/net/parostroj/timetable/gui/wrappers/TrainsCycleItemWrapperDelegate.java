package net.parostroj.timetable.gui.wrappers;

import net.parostroj.timetable.model.TimeConverter;
import net.parostroj.timetable.model.TrainsCycleItem;
import net.parostroj.timetable.utils.ObjectsUtil;

/**
 * Delegate for trains cycle items.
 *
 * @author jub
 */
public class TrainsCycleItemWrapperDelegate extends BasicWrapperDelegate<TrainsCycleItem> {

    private final boolean showComment;

    public TrainsCycleItemWrapperDelegate() {
        this(true);
    }

    public TrainsCycleItemWrapperDelegate(boolean showComment) {
        this.showComment = showComment;
    }

    @Override
    public String toString(TrainsCycleItem element) {
        return toStringItem(element);
    }

    public String toStringItem(TrainsCycleItem item) {
        TimeConverter c = item.getTrain().getDiagram().getTimeConverter();
        String str = String.format("%s (%s[%s],%s[%s])", item.getTrain().getName(),
                        item.getFromInterval().getOwnerAsNode().getName(),
                        c.convertIntToText(item.getStartTime()),
                        item.getToInterval().getOwnerAsNode().getName(),
                        c.convertIntToText(item.getEndTime()));
        String comment = ObjectsUtil.checkAndTrim(item.getComment());
        if (showComment && comment != null) {
            str = String.format("%s - %s", str, comment);
        }
        return str;
    }
}
