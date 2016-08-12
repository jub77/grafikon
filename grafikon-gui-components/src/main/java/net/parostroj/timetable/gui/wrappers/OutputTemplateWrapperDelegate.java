package net.parostroj.timetable.gui.wrappers;

import net.parostroj.timetable.model.OutputTemplate;
import net.parostroj.timetable.utils.ObjectsUtil;

/**
 * Delegate for output template.
 *
 * @author jub
 */
public class OutputTemplateWrapperDelegate extends BasicWrapperDelegate<OutputTemplate> {

    @Override
    protected String toCompareString(OutputTemplate element) {
        return element.getName();
    }

    @Override
    public String toString(OutputTemplate element) {
        String text = element.getName();
        if (element.getDescription() != null) {
            text = element.getDescription().translate();
            text = ObjectsUtil.getFirstLine(text);
        }
        return text;
    }
}
