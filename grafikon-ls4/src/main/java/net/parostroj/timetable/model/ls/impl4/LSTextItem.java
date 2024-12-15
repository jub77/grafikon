package net.parostroj.timetable.model.ls.impl4;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import net.parostroj.timetable.model.TextItem;
import net.parostroj.timetable.model.ls.LSException;

/**
 * Class for storing text items.
 *
 * @author jub
 */
@XmlRootElement(name="text_item")
@XmlType(propOrder={"id", "name", "template", "attributes"})
public class LSTextItem {

    private String id;
    private String name;
    private LSTextTemplate template;
    private LSAttributes attributes;

    public LSTextItem() {
    }

    public LSTextItem(TextItem item) {
        this.id = item.getId();
        this.template = item.getTemplate() == null ? null : new LSTextTemplate(item.getTemplate());
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

    public TextItem createTextItem(LSContext context) throws LSException {
        TextItem item = new TextItem(id, context.getDiagram());
        item.getAttributes().add(attributes.createAttributes(context));
        if (template != null) {
            item.setTemplate(template.createTextTemplate(context));
        }
        item.setName(name);
        return item;
    }
}
