package net.parostroj.timetable.model.validators;

import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.model.events.Event.Type;

/**
 * Company remove correction.
 *
 * @author jub
 */
public class CompanyRemoveValidator implements TrainDiagramValidator {

    private final TrainDiagram diagram;

    public CompanyRemoveValidator(TrainDiagram diagram) {
        this.diagram = diagram;
    }

    @Override
    public boolean validate(Event event) {
        if (event.getSource() instanceof TrainDiagram && event.getType() == Type.REMOVED && event.getObject() instanceof Company) {
            Company removedCompany = (Company) event.getObject();
            // remove company from circulations...
            for (TrainsCycleType type : diagram.getCycleTypes()) {
                for (TrainsCycle circulation : type.getCycles()) {
                    Company company = circulation.getAttribute(TrainsCycle.ATTR_COMPANY, Company.class);
                    if (company == removedCompany) {
                        circulation.removeAttribute(TrainsCycle.ATTR_COMPANY);
                    }
                }
            }
            return true;
        }
        return false;
    }
}
