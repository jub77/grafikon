package net.parostroj.timetable.model.ls.impl4;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import net.parostroj.timetable.model.TextItem;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.ls.LSException;

/**
 * Class for storing text items.
 *
 * @author jub
 */
@XmlRootElement(name="text_item")
@XmlType(propOrder={"id", "name", "template", "type", "attributes"})
public class LSTextItem {

    private String id;
    private String name;
    private LSTextTemplate template;
    private String type;
    private LSAttributes attributes;

    public LSTextItem() {
    }

    public LSTextItem(TextItem item) {
        this.id = item.getId();
        this.template = item.getTemplate() == null ? null : new LSTextTemplate(item.getTemplate());
        this.type = item.getType().getKey();
        this.name = item.getName();
        this.attributes = new LSAttributes(item.getAttributes());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LSTextTemplate getTemplate() {
        return template;
    }

    public void setTemplate(LSTextTemplate template) {
        this.template = template;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LSAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(LSAttributes attributes) {
        this.attributes = attributes;
    }

    public TextItem createTextItem(TrainDiagram diagram) throws LSException {
        TextItem item = new TextItem(id, diagram);
        item.setAttributes(attributes.createAttributes(diagram));
        if (template != null) {
            item.setTemplate(template.createTextTemplate());
        }
        item.setType(TextItem.Type.fromKey(type));
        item.setName(name);
        return item;
    }
}
