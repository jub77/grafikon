package net.parostroj.timetable.gui.wrappers;

import net.parostroj.timetable.model.LocalizedString;
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
                        this.getStartTime(c, item),
                        item.getToInterval().getOwnerAsNode().getName(),
                        c.convertIntToText(item.getEndTime()));
        LocalizedString lComment = item.getComment();
        String comment = ObjectsUtil.checkAndTrim(lComment == null ? null : lComment.getDefaultString());
        if (showComment && comment != null) {
            str = String.format("%s - %s", str, comment);
        }
        return str;
    }

    private String getStartTime(TimeConverter c, TrainsCycleItem item) {
        Integer setupTime = item.getSetupTime();
        String startTimeStr = c.convertIntToText(item.getStartTime());
        return setupTime == null ? startTimeStr
                : String.format("<%s>%s", c.convertIntToMinutesText(setupTime), startTimeStr);
    }
}
