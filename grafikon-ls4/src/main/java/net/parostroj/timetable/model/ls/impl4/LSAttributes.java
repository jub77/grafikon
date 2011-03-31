package net.parostroj.timetable.model.ls.impl4;

import java.util.LinkedList;
import java.util.List;

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
        for (String name : attributes.names()) {
            Object value = attributes.get(name);
            if (value != null) {
                LSAttributesItem lItem = new LSAttributesItem(name, value);
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

    public Attributes createAttributes() throws LSException {
        return this.createAttributes(null);
    }

    public Attributes createAttributes(TrainDiagram diagram) throws LSException {
        Attributes lAttributes = new Attributes();
        if (this.attributes != null) {
            for (LSAttributesItem lItem : this.attributes) {
                Object value = lItem.convertValue(diagram);
                if (value != null)
                    lAttributes.set(lItem.getKey(), value);
                else
                    LOG.warn("Null value for attribute: {}, value: {}", lItem.getKey(), lItem.getValue());
            }
        }
        return lAttributes;
    }
}
