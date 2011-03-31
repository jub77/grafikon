package net.parostroj.timetable.model.save.version02;

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
        for (String name : attributes.names()) {
            LSAttributesItem lItem = new LSAttributesItem(name, attributes.get(name));
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
                attributes.set(lItem.getKey(), lItem.convertValue());
            }
        return attributes;
    }
}
