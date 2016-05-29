package net.parostroj.timetable.gui.wrappers;

import java.util.Locale;

import net.parostroj.timetable.model.OutputTemplate;

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
            text = element.getDescription().translate(Locale.getDefault());
            // keep only the first line
            int index = text.indexOf('\n');
            if (index != -1) {
                text = text.substring(0, index);
            }
        }
        return text;
    }
}
