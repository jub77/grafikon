package net.parostroj.timetable.model.ls.impl3;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import net.parostroj.timetable.model.Attributes;
import net.parostroj.timetable.model.TrainDiagram;

/**
 * Storage class for attributes.
 * 
 * @author jub
 */
@XmlRootElement(name = "attributes")
public class LSAttributes {

    private List<LSAttributesItem> attributes;

    /**
     * Default constructor.
     */
    public LSAttributes() {
    }

    public LSAttributes(Attributes attributes) {
        this.attributes = new LinkedList<LSAttributesItem>();
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            if (entry.getValue() != null) {
                LSAttributesItem lItem = new LSAttributesItem(entry.getKey(), entry.getValue());
                this.attributes.add(lItem);
            }
        }
    }

    @XmlElement(name = "attribute")
    public List<LSAttributesItem> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<LSAttributesItem> attributes) {
        this.attributes = attributes;
    }

    public Attributes createAttributes() {
        return this.createAttributes(null);
    }

    public Attributes createAttributes(TrainDiagram diagram) {
        Attributes lAttributes = new Attributes();
        if (this.attributes != null) {
            for (LSAttributesItem lItem : this.attributes) {
                lAttributes.put(lItem.getKey(), lItem.convertValue(diagram));
            }
        }
        return lAttributes;
    }
}
