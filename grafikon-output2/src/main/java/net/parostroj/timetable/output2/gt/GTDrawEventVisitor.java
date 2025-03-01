package net.parostroj.timetable.output2.gt;

import net.parostroj.timetable.output2.gt.GTDraw.Refresh;
import net.parostroj.timetable.visitors.EventVisitor;

/**
 * Visitor with refresh result.
 *
 * @author jub
 */
public class GTDrawEventVisitor implements EventVisitor {

    private Refresh refresh = Refresh.NONE;

    public Refresh getRefresh() {
        return refresh;
    }

    public void setRefresh(Refresh refresh) {
        this.refresh = refresh;
    }
}
