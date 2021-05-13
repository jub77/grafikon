/*
 * LSRoute.java
 *
 * Created on 8.9.2007, 11:53:31
 */

package net.parostroj.timetable.model.save.version01;

import net.parostroj.timetable.model.Route;
import net.parostroj.timetable.model.NetSegment;

/**
 * L/S class for Route.
 *
 * @author jub
 */
public class LSRoute {

    private int[] ids;

    public LSRoute(Route route, LSTransformationData data) {
        ids = new int[route.getSegments().size()];
        int i = 0;
        for (NetSegment<?> segment : route.getSegments()) {
            int id = data.getIdForObject(segment);
            ids[i++] = id;
        }
    }

    public LSRoute() {}

    public int[] getIds() {
        return ids;
    }

    public void setIds(int[] ids) {
        this.ids = ids;
    }

    public void visit(LSVisitor visitor) {
        visitor.visit(this);
    }
}
