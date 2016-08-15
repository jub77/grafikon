package net.parostroj.timetable.gui.wrappers;

import net.parostroj.timetable.model.OutputTemplate;

/**
 * Delegate for output template.
 *
 * @author jub
 */
public class OutputTemplateWrapperDelegate extends BasicWrapperDelegate<OutputTemplate> {

    @Override
    protected String toCompareString(OutputTemplate element) {
        return toString(element);
    }

    @Override
    public String toString(OutputTemplate element) {
        String text = element.getKey();
        if (element.getName() != null) {
            text = element.getName().translate();
        }
        return text;
    }
}
