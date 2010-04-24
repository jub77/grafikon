package net.parostroj.timetable.model.ls.impl4;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import net.parostroj.timetable.model.TextItem;
import net.parostroj.timetable.model.TrainDiagram;

/**
 * Class for storing text items.
 *
 * @author jub
 */
@XmlRootElement(name="text_item")
@XmlType(propOrder={"id", "name", "text", "type", "attributes"})
public class LSTextItem {

    private String id;
    private String name;
    private String text;
    private String type;
    private LSAttributes attributes;

    public LSTextItem() {
    }

    public LSTextItem(TextItem item) {
        this.id = item.getId();
        this.text = item.getText();
        this.type = item.getType();
        this.name = item.getName();
        this.attributes = new LSAttributes(item.getAttributes());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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

    public TextItem createTextItem(TrainDiagram diagram) {
        TextItem item = new TextItem(id, diagram);
        item.setAttributes(attributes.createAttributes(diagram));
        item.setText(text);
        item.setType(type);
        item.setName(name);
        return item;
    }
}
