package net.parostroj.timetable.model.freight;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.parostroj.timetable.model.FreightConnectionFilter;
import net.parostroj.timetable.model.FreightConnectionFilter.FilterResult;
import net.parostroj.timetable.model.FreightNet;
import net.parostroj.timetable.model.Script;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;

/**
 * Custom net connection filter read from freight net.
 *
 * @author jub
 */
class CustomNetFilterConnectionStrategy extends BaseConnectionStrategy {

    private final FreightConnectionFilter netFilter;
    private final FreightConnectionFilter trainFilter;

    static class Builder {
        FreightConnectionFilter netFilter;
        FreightConnectionFilter trainFilter;

        void setNetFilter(FreightConnectionFilter filter) {
            netFilter = filter;
        }

        void setTrainFilter(FreightConnectionFilter filter) {
            trainFilter = filter;
        }
    }

    public CustomNetFilterConnectionStrategy(TrainDiagram diagram) {
        this(diagram, diagram.getFreightNet().getAttribute(FreightNet.ATTR_CUSTOM_CONNECTION_FILTER, Script.class));
    }

    public CustomNetFilterConnectionStrategy(TrainDiagram diagram, Script script) {
        super(diagram);
        Builder builder = new Builder();
        Map<String, Object> mapping = new HashMap<>();
        mapping.put("builder", builder);
        for (FilterResult filterResult : FilterResult.values()) {
            mapping.put(filterResult.getKey(), filterResult);
        }
        script.evaluateWithException(mapping);
        this.netFilter = builder.netFilter;
        this.trainFilter = builder.trainFilter;
    }

    @Override
    public Map<Train, List<FreightConnectionPath>> getFreightPassedInNode(TimeInterval fromInterval) {
        return getFreightPassedInNode(fromInterval, trainFilter != null ? trainFilter : FreightConnectionFilter::empty);
    }

    @Override
    public List<FreightConnectionPath> getFreightToNodes(TimeInterval fromInterval) {
        return getFreightToNodes(fromInterval, trainFilter != null ? trainFilter : FreightConnectionFilter::empty);
    }

    @Override
    public List<FreightConnectionPath> getFreightToNodesNet(TimeInterval fromInterval) {
        return getFreightToNodes(fromInterval, netFilter != null ? netFilter : FreightConnectionFilter::empty);
    }
}
