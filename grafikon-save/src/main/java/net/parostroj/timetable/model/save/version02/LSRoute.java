/*
 * LSRoute.java
 *
 * Created on 8.9.2007, 11:53:31
 */

package net.parostroj.timetable.model.save.version02;

import java.util.UUID;
import net.parostroj.timetable.model.Route;
import net.parostroj.timetable.model.RouteSegment;

/**
 * L/S class for Route.
 *
 * @author jub
 */
public class LSRoute {

    private String name;

    private String uuid;

    private boolean netPart;

    private int[] ids;

    public LSRoute(Route route, LSTransformationData data) {
        ids = new int[route.getSegments().size()];
        int i = 0;
        for (RouteSegment<?> segment : route.getSegments()) {
            int id = data.getIdForObject(segment);
            ids[i++] = id;
        }
        name = route.getName();
        uuid = route.getId();
        netPart = route.isNetPart();
    }

    public LSRoute() {}

    public int[] getIds() {
        return ids;
    }

    public void setIds(int[] ids) {
        this.ids = ids;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isNetPart() {
        return netPart;
    }

    public void setNetPart(boolean netPart) {
        this.netPart = netPart;
    }

    public String getUuid() {
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void visit(LSVisitor visitor) {
        visitor.visit(this);
    }
}
