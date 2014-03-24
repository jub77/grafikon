package net.parostroj.timetable.gui.wrappers;

import net.parostroj.timetable.model.TimeConverter;
import net.parostroj.timetable.model.TrainsCycleItem;

/**
 * Delegate for trains cycle items.
 *
 * @author jub
 */
public class TrainsCycleWrapperItemDelegate implements WrapperDelegate {

    private final boolean showComment;

    public TrainsCycleWrapperItemDelegate() {
        this(true);
    }

    public TrainsCycleWrapperItemDelegate(boolean showComment) {
        this.showComment = showComment;
    }

    @Override
    public String toString(Object element) {
        return toStringItem((TrainsCycleItem) element);
    }

    @Override
    public int compare(Object o1, Object o2) {
        return toStringItem((TrainsCycleItem) o1).compareTo(toStringItem((TrainsCycleItem) o2));
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
