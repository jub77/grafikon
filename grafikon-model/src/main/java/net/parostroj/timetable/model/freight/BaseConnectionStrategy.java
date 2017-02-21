package net.parostroj.timetable.model.freight;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Iterators;

import net.parostroj.timetable.model.FNConnection;
import net.parostroj.timetable.model.FreightConnectionFilter;
import net.parostroj.timetable.model.FreightConnectionFilter.FilterContext;
import net.parostroj.timetable.model.FreightConnectionFilter.FilterResult;
import net.parostroj.timetable.model.FreightNet;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;

class BaseConnectionStrategy implements FreightConnectionStrategy {

    private FreightNet freightNet;
    private RegionGraphDelegate regionGraphDelegate;

    public BaseConnectionStrategy(TrainDiagram diagram) {
        this.freightNet = diagram.getFreightNet();
        this.regionGraphDelegate = new RegionGraphDelegate(diagram.getNet(), this);
    }

    @Override
    public Map<Train, List<FreightConnectionPath>> getFreightPassedInNode(TimeInterval fromInterval) {
        return this.getFreightPassedInNode(fromInterval, FreightConnectionFilter::empty);
    }

    protected Map<Train, List<FreightConnectionPath>> getFreightPassedInNode(TimeInterval fromInterval, FreightConnectionFilter filter) {
        if (!fromInterval.isNodeOwner()) {
            throw new IllegalArgumentException("Only node intervals allowed.");
        }
        Map<Train, List<FreightConnectionPath>> result = new LinkedHashMap<>();
        List<FNConnection> connections = freightNet.getTrainsFrom(fromInterval);
        for (FNConnection conn : connections) {
            List<FreightConnectionPath> nodes = this.getFreightToNodesImpl(conn.getTo(),
                    conn.getFreightDstFilter(filter, true));
            result.put(conn.getTo().getTrain(), nodes);
        }
        return result;
    }

    @Override
    public List<FreightConnectionPath> getFreightToNodes(TimeInterval fromInterval) {
        return this.getFreightToNodes(fromInterval, FreightConnectionFilter::empty);
    }

    @Override
    public List<FreightConnectionPath> getFreightToNodesNet(TimeInterval fromInterval) {
        return this.getFreightToNodes(fromInterval, FreightConnectionFilter::empty);
    }

    protected List<FreightConnectionPath> getFreightToNodes(TimeInterval fromInterval, FreightConnectionFilter filter) {
        if (!fromInterval.isNodeOwner()) {
            throw new IllegalArgumentException("Only node intervals allowed.");
        }
        return this.getFreightToNodesImpl(fromInterval, filter);
    }

    private List<FreightConnectionPath> getFreightToNodesImpl(TimeInterval fromInterval, FreightConnectionFilter filter) {
        List<FreightConnectionPath> result = new LinkedList<>();
        this.getFreightToNodesImpl(fromInterval.getOwnerAsNode(), fromInterval,
                Collections.<TrainConnection>emptyList(), result, new HashSet<FNConnection>(), filter,
                new FilterContext(fromInterval));
        return result;
    }

    private void getFreightToNodesImpl(Node start, TimeInterval fromInterval, List<TrainConnection> path,
            List<FreightConnectionPath> result, Set<FNConnection> used, FreightConnectionFilter filter,
            FilterContext context) {
        FilterResult filterResult = FilterResult.OK;
        Iterator<TimeInterval> intervals = fromInterval.getTrain().iterator();
        Iterators.find(intervals, interval -> interval == fromInterval);
        intervals = Iterators.filter(intervals,
                interval -> interval.isNodeOwner() && (interval.isFreightTo() || interval.isFreightConnection()));
        while (intervals.hasNext()) {
            TimeInterval i = intervals.next();
            if (i.isFreight()) {
                FreightConnectionPath newDst = FreightFactory.createFreightNodeConnection(start,
                        i.getOwnerAsNode(), i.isRegionCenterTransfer(), createNewPath(path, fromInterval, i));
                filterResult = filter.accepted(context, newDst, 0);
                if (filterResult == FilterResult.STOP_EXCLUDE) {
                    break;
                }
                if (filterResult != FilterResult.IGNORE) {
                    result.add(newDst);
                }
                if (filterResult == FilterResult.STOP_INCLUDE) {
                    break;
                }
            }
            for (FNConnection conn : freightNet.getTrainsFrom(i)) {
                if (!used.contains(conn)) {
                    used.add(conn);
                    List<TrainConnection> newPath = createNewPath(path, fromInterval, conn.getFrom());
                    this.getFreightToNodesImpl(start, conn.getTo(), newPath, result, used,
                            conn.getFreightDstFilter(filter, false), context);
                }
            }
        }
    }

    private List<TrainConnection> createNewPath(List<TrainConnection> path, TimeInterval from, TimeInterval to) {
        List<TrainConnection> newPath = new ArrayList<>(path.size() + 1);
        newPath.addAll(path);
        newPath.add(new RegionGraphDelegate.TrainConnectionImpl(from, to));
        return newPath;
    }

    @Override
    public Collection<NodeConnectionNodes> getRegionConnectionNodes() {
        return regionGraphDelegate.getRegionConnectionNodes();
    }

    @Override
    public Collection<NodeConnectionEdges> getRegionConnectionEdges() {
        return regionGraphDelegate.getRegionConnectionEdges();
    }
}
