package net.parostroj.timetable.model.imports;

import java.util.LinkedList;
import java.util.List;

import net.parostroj.timetable.model.*;
import net.parostroj.timetable.utils.Triplet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Circulations import.
 *
 * @author jub
 */
public class TrainsCycleImport extends Import {

    private static final Logger log = LoggerFactory.getLogger(TrainsCycleImport.class);

    public TrainsCycleImport(TrainDiagram diagram, ImportMatch match, boolean overwrite) {
        super(diagram, match, overwrite);
    }

    @Override
    protected ObjectWithId importObjectImpl(ObjectWithId importedObject) {
        // check class
        if (!(importedObject instanceof TrainsCycle importedCycle)) {
            // skip other objects
            return null;
        }

        // check if cycle already exist
        TrainsCycle checkedCycle = this.getCycle(importedCycle);
        if (checkedCycle != null) {
            if (overwrite) {
                checkedCycle.getType().getCycles().remove(checkedCycle);
            } else {
                String message = "circulation already exists";
                this.addError(importedCycle, message);
                log.debug("{}: {}", message, checkedCycle);
                return null;
            }
        }

        // create a new cycle
        TrainsCycleType cycleType = this.getCycleType(importedCycle.getType());
        if (cycleType == null) {
            String message = "circulation type missing: " + importedCycle.getType();
            this.addError(importedCycle, message);
            log.debug(message);
            return null;
        }
        TrainsCycle cycle = new TrainsCycle(this.getId(importedCycle), this.getDiagram(),
                importedCycle.getName(), importedCycle.getDescription(), cycleType);
        cycle.getAttributes().add(this.importAttributes(importedCycle.getAttributes()));

        // import cycles
        List<Triplet<Train, TimeInterval, TimeInterval>> items = this.createCycleItems(importedCycle);
        // error if items is null
        if (items == null) {
            return null;
        }
        for (int i = 0; i < items.size(); i++) {
            Triplet<Train, TimeInterval, TimeInterval> item = items.get(i);
            TrainsCycleItem importedItem = importedCycle.getItems().get(i);
            TrainsCycleItem cycleItem = new TrainsCycleItem(cycle, item.first, importedItem.getComment(), item.second, item.third);
            cycleItem.getAttributes().add(this.importAttributes(importedItem.getAttributes()));
            cycle.addItem(cycleItem);
        }

        cycle.getType().getCycles().add(cycle);
        this.addImportedObject(cycle);
        log.trace("Successfully imported circulation: {}", cycle);
        return cycle;
    }

    private List<Triplet<Train, TimeInterval, TimeInterval>> createCycleItems(TrainsCycle cycle) {
        List<Triplet<Train, TimeInterval, TimeInterval>> items = new LinkedList<>();
        for (TrainsCycleItem item : cycle.getItems()) {
            Train train = this.getTrain(item.getTrain());
            if (train == null) {
                String message = "train missing: " + item.getTrain();
                this.addError(cycle, message);
                log.debug(message);
                return null;
            }
            // get intervals
            int from = item.getTrain().getTimeIntervalList().indexOf(item.getFromInterval());
            int to = item.getTrain().getTimeIntervalList().indexOf(item.getToInterval());
            List<TimeInterval> intervalList = train.getTimeIntervalList();
            int tlSize = intervalList.size();
            TimeInterval fi = (from < tlSize) ? train.getTimeIntervalList().get(from) : null;
            TimeInterval ti = (to < tlSize) ? train.getTimeIntervalList().get(to) : null;
            if (fi == null || ti == null) {
                String message = "intervals does not match";
                this.addError(cycle, message);
                log.debug("{}: {}", cycle, message);
                return null;
            }
            // check nodes of intervals
            Node fn = this.getNode(item.getFromInterval().getOwnerAsNode());
            Node tn = this.getNode(item.getToInterval().getOwnerAsNode());
            if (fn != fi.getOwnerAsNode() || tn != ti.getOwnerAsNode()) {
                String message = "nodes does not match";
                this.addError(cycle, message);
                log.debug("{}: {}", cycle, message);
                return null;
            }
            if (from == 0) {
                fi = null;
            }
            if (to == tlSize - 1) {
                ti = null;
            }
            items.add(new Triplet<>(train, fi, ti));
        }
        return items;
    }

}
