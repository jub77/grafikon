package net.parostroj.timetable.model.ls.impl4;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.OutputTemplate;
import net.parostroj.timetable.model.library.LibraryItem;
import net.parostroj.timetable.model.library.LibraryItemType;

/**
 * Storage for library item.
 *
 * @author jub
 */
@XmlRootElement(name = "library_item")
@XmlType(propOrder = { "type", "attributes", "object" })
public class LSLibraryItem {

    private String type;
    private LSAttributes attributes;
    private Object object;

    public LSLibraryItem() {
    }

    public LSLibraryItem(LibraryItem item) {
        type = item.getType().name();
        attributes = new LSAttributes(item.getAttributes());
        object = item.getType() == LibraryItemType.NODE ? new LSNode((Node) item.getItem()) : new LSOutputTemplate((OutputTemplate) item.getItem());
    }

    public LSAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(LSAttributes attributes) {
        this.attributes = attributes;
    }

    @XmlAttribute
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @XmlElements({
        @XmlElement(name = "node", type = LSNode.class),
        @XmlElement(name = "output_template", type = LSOutputTemplate.class)
    })
    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
