package net.parostroj.timetable.model.freight;

import java.util.List;

public interface NodeConnectionEdges extends NodeConnection {
    List<DirectNodeConnection> getEdges();
}
