package net.parostroj.timetable.gui.wrappers;

import net.parostroj.timetable.model.TimeConverter;
import net.parostroj.timetable.model.TrainsCycleItem;

/**
 * Delegate for trains cycle items.
 *
 * @author jub
 */
public class TrainsCycleItemWrapperDelegate extends BasicWrapperDelegate {

    private final boolean showComment;

    public TrainsCycleItemWrapperDelegate() {
        this(true);
    }

    public TrainsCycleItemWrapperDelegate(boolean showComment) {
        this.showComment = showComment;
    }

    @Override
    public String toString(Object element) {
        return toStringItem((TrainsCycleItem) element);
    }

    public String toStringItem(TrainsCycleItem item) {
        TimeConverter c = item.getTrain().getTrainDiagram().getTimeConverter();
        String str = String.format("%s (%s[%s],%s[%s])", item.getTrain().getName(),
                        item.getFromInterval().getOwnerAsNode().getName(),
                        c.convertIntToText(item.getStartTime()),
                        item.getToInterval().getOwnerAsNode().getName(),
                        c.convertIntToText(item.getEndTime()));
        if (showComment && item.getComment() != null && !"".equals(item.getComment().trim())) {
            str = String.format("%s - %s", str, item.getComment());
        }
        return str;
    }
}
