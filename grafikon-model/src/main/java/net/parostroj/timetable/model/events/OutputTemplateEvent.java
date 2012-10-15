package net.parostroj.timetable.model.events;

import net.parostroj.timetable.model.OutputTemplate;
import net.parostroj.timetable.visitors.EventVisitor;

/**
 * Output template event.
 *
 * @author jub
 */
public class OutputTemplateEvent extends GTEvent<OutputTemplate> {

    public OutputTemplateEvent(OutputTemplate template, GTEventType type) {
        super(template, type);
    }

    public OutputTemplateEvent(OutputTemplate template, AttributeChange change) {
        super(template, GTEventType.ATTRIBUTE);
        this.setAttributeChange(change);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("OutputTemplateEvent[");
        builder.append(getSource()).append(',');
        builder.append(getType());
        if (getType() == GTEventType.ATTRIBUTE) {
            builder.append(',').append(getAttributeChange());
        }
        builder.append(']');
        return builder.toString();
    }

    @Override
    public void accept(EventVisitor visitor) {
        visitor.visit(this);
    }

}
