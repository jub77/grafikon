package net.parostroj.timetable.gui.wrappers;

import net.parostroj.timetable.gui.utils.OutputTypeUtil;
import net.parostroj.timetable.model.OutputTemplate;

/**
 * Delegate for output template.
 *
 * @author jub
 */
public class OutputTemplateWrapperDelegate extends BasicWrapperDelegate<OutputTemplate> {

    private final boolean addType;
    private final boolean addSource;

    public OutputTemplateWrapperDelegate() {
        this(true, true);
    }

    public OutputTemplateWrapperDelegate(boolean addType, boolean addSource) {
        this.addType = addType;
        this.addSource = addSource;
    }

    private String getTypeInfo(OutputTemplate template) {
        if (template == null) {
            return "";
        }
        return OutputTypeUtil.convertOutputType(template);
    }

    @Override
    public String toCompareString(OutputTemplate element) {
        return toString(element);
    }

    @Override
    public String toString(OutputTemplate element) {
        String text = element.getKey();
        if (element.getName() != null) {
            text = element.getName().translate();
        }
        if (addType) {
            text = getTypeInfo(element) + ": " + text;
        }
        if (addSource && element.getAttribute(OutputTemplate.ATTR_SOURCE, String.class) != null) {
            text = text + " [" + element.getAttribute(OutputTemplate.ATTR_SOURCE, String.class) + "]";
        }
        return text;
    }
}
