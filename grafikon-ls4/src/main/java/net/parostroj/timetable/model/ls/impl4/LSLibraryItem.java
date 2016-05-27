package net.parostroj.timetable.model.ls.impl4;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.parostroj.timetable.model.EngineClass;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.OutputTemplate;
import net.parostroj.timetable.model.library.Library;
import net.parostroj.timetable.model.library.LibraryItem;

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
        switch (item.getType()) {
            case NODE: object = new LSNode((Node) item.getObject()); break;
            case OUTPUT_TEMPLATE: object = new LSOutputTemplate((OutputTemplate) item.getObject()); break;
            case ENGINE_CLASS: object = new LSEngineClass((EngineClass) item.getObject()); break;
        }
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
        @XmlElement(name = "output_template", type = LSOutputTemplate.class),
        @XmlElement(name = "engine_class", type = LSEngineClass.class),
    })
    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public LibraryItem createLibraryItem(Library library) {
        return null;
    }
}
