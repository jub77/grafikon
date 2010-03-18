package net.parostroj.timetable.model.save.version02;

import java.util.Map;
import net.parostroj.timetable.model.Attributes;

/**
 * Storage class for attributes.
 * 
 * @author jub
 */
public class LSAttributes {
    
    private LSAttributesItem[] attribute;
    
    /**
     * Default constructor.
     */
    public LSAttributes() {
    }

    public LSAttributes(Attributes attributes, LSTransformationData data) {
        int size = attributes.size();
        attribute = new LSAttributesItem[size];
        int i = 0;
        for (Map.Entry<String,Object> entry : attributes.entrySet()) {
            LSAttributesItem lItem = new LSAttributesItem(entry.getKey(), entry.getValue());
            attribute[i++] = lItem;
        }
    }

    public LSAttributesItem[] getAttribute() {
        return attribute;
    }

    public void setAttribute(LSAttributesItem[] item) {
        this.attribute = item;
    }
    
    public Attributes convertToAttributes() {
        Attributes attributes = new Attributes();
        if (attribute != null)
            for (LSAttributesItem lItem : attribute) {
                attributes.put(lItem.getKey(), lItem.convertValue());
            }
        return attributes;
    }
}
