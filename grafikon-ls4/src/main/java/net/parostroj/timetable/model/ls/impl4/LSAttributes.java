package net.parostroj.timetable.model.ls.impl4;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import net.parostroj.timetable.model.Attributes;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.ls.LSException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Storage class for attributes.
 * 
 * @author jub
 */
@XmlRootElement(name = "attributes")
public class LSAttributes {

    private static final Logger LOG = LoggerFactory.getLogger(LSAttributes.class.getName());
    private List<LSAttributesItem> attributes;

    /**
     * Default constructor.
     */
    public LSAttributes() {
    }

    public LSAttributes(Attributes attributes) {
        this.attributes = new LinkedList<LSAttributesItem>();
        this.addAttributes(attributes.getAttributesMap(), null);
        for (String category : attributes.getCategories())
            this.addAttributes(attributes.getAttributesMap(category), category);
    }
    
    private void addAttributes(Map<String, Object> map, String category) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() != null)
                this.attributes.add(new LSAttributesItem(entry.getKey(), entry.getValue(), category));
        }
    }

    @XmlElement(name = "attribute")
    public List<LSAttributesItem> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<LSAttributesItem> attributes) {
        this.attributes = attributes;
    }

    public Attributes createAttributes() throws LSException {
        return this.createAttributes(null);
    }

    public Attributes createAttributes(TrainDiagram diagram) throws LSException {
        Attributes lAttributes = new Attributes();
        if (this.attributes != null) {
            for (LSAttributesItem lItem : this.attributes) {
                Object value = lItem.convertValue(diagram);
                if (value != null)
                    lAttributes.set(lItem.getKey(), value, lItem.getCategory());
                else
                    LOG.warn("Null value for attribute: {}, value: {}", lItem.getKey(), lItem.getValue());
            }
        }
        return lAttributes;
    }
}
