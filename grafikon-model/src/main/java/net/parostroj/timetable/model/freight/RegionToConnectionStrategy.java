package net.parostroj.timetable.model.freight;

import static net.parostroj.timetable.model.FreightConnectionFilter.FilterResult.OK;
import static net.parostroj.timetable.model.FreightConnectionFilter.FilterResult.STOP_INCLUDE;

import java.util.List;

import net.parostroj.timetable.model.FreightConnectionFilter.FilterContext;
import net.parostroj.timetable.model.FreightConnectionFilter.FilterResult;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.TrainDiagram;

/**
 * Stopping connection when the train reaches another region center (if it starts in one).
 *
 * @author jub
 */
class RegionToConnectionStrategy extends BaseConnectionStrategy {

    public RegionToConnectionStrategy(TrainDiagram diagram) {
        super(diagram);
    }

    @Override
    public List<FreightConnectionPath> getFreightToNodesNet(TimeInterval fromInterval) {
        return getFreightToNodes(fromInterval, this::regionTransferStop);
    }

    private FilterResult regionTransferStop(FilterContext context, FreightConnection dst, int level) {
        return dst.getTo().isRegions() ? STOP_INCLUDE : OK;
    }
}
