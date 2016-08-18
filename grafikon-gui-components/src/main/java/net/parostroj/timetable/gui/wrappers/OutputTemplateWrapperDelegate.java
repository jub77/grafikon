package net.parostroj.timetable.gui.wrappers;

import net.parostroj.timetable.gui.utils.OutputTypeUtil;
import net.parostroj.timetable.model.OutputTemplate;

/**
 * Delegate for output template.
 *
 * @author jub
 */
public class OutputTemplateWrapperDelegate extends BasicWrapperDelegate<OutputTemplate> {

    private boolean addType;

    public OutputTemplateWrapperDelegate() {
        this(true);
    }

    public OutputTemplateWrapperDelegate(boolean addType) {
        this.addType = addType;
    }

    private String getTypeInfo(OutputTemplate template) {
        if (template == null) {
            return "";
        }
        return OutputTypeUtil.convertOutputType(template);
    }

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
        return addType ?
                String.format("%2$s: %1$s", text, getTypeInfo(element)) :
                text;
    }
}
