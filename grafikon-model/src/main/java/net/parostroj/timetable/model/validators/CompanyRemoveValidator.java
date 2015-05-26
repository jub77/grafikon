package net.parostroj.timetable.model.validators;

import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.events.*;

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
    public boolean validate(GTEvent<?> event) {
        if (event instanceof TrainDiagramEvent && event.getType() == GTEventType.COMPANY_REMOVED) {
            Company removedCompany = (Company) ((TrainDiagramEvent) event).getObject();
            // remove company from circulations...
            for (TrainsCycleType type : diagram.getCycleTypes()) {
                for (TrainsCycle circulation : diagram.getCycles(type)) {
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
