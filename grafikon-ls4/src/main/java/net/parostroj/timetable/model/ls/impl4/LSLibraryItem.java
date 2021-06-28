package net.parostroj.timetable.model.ls.impl4;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import net.parostroj.timetable.model.EngineClass;
import net.parostroj.timetable.model.LineClass;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.ObjectWithId;
import net.parostroj.timetable.model.OutputTemplate;
import net.parostroj.timetable.model.TrainType;
import net.parostroj.timetable.model.TrainTypeCategory;
import net.parostroj.timetable.model.library.LibraryBuilder;
import net.parostroj.timetable.model.library.LibraryItem;
import net.parostroj.timetable.model.library.LibraryItemType;
import net.parostroj.timetable.model.ls.LSException;

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
            case NODE:
                object = new LSNode((Node) item.getObject());
                break;
            case OUTPUT_TEMPLATE:
                object = new LSOutputTemplate((OutputTemplate) item.getObject());
                break;
            case ENGINE_CLASS:
                object = new LSEngineClass((EngineClass) item.getObject());
                break;
            case TRAIN_TYPE:
                object = new LSTrainType((TrainType) item.getObject());
                break;
            case LINE_CLASS:
                object = new LSLineClass((LineClass) item.getObject());
                break;
            case TRAIN_TYPE_CATEGORY:
                object = new LSTrainTypeCategory((TrainTypeCategory) item.getObject());
                break;
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
        @XmlElement(name = "train_type", type = LSTrainType.class),
        @XmlElement(name = "line_class", type = LSLineClass.class),
        @XmlElement(name = "train_type_category", type = LSTrainTypeCategory.class)
    })
    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public LibraryItem createLibraryItem(LibraryBuilder libraryBuilder) throws LSException {
        LibraryItemType type = LibraryItemType.valueOf(getType());
        ObjectWithId object = null;
        switch (type) {
            case ENGINE_CLASS:
                object = ((LSEngineClass) getObject()).createEngineClass(id -> {
                    ObjectWithId foundObject = libraryBuilder.getObjectById(id);
                    return foundObject instanceof LineClass ? (LineClass) foundObject : null;
                });
                break;
            case LINE_CLASS:
                object = ((LSLineClass) getObject()).createLineClass();
                break;
            case NODE:
                object = ((LSNode) getObject()).createNode(libraryBuilder.getPartFactory(), libraryBuilder::getObjectById);
                break;
            case OUTPUT_TEMPLATE:
                object = ((LSOutputTemplate) getObject()).createOutputTemplate(libraryBuilder.getPartFactory(), libraryBuilder::getObjectById);
                break;
            case TRAIN_TYPE:
                object = ((LSTrainType) getObject()).createTrainType(libraryBuilder.getPartFactory(), libraryBuilder::getObjectById,
                        id -> {
                            ObjectWithId foundObject = libraryBuilder.getObjectById(id);
                            return foundObject instanceof TrainTypeCategory ? (TrainTypeCategory) foundObject : null;
                        });
                break;
            case TRAIN_TYPE_CATEGORY:
                object = ((LSTrainTypeCategory) getObject()).createTrainTypeCategory();
                break;
        }
        LibraryItem item = libraryBuilder.addObject(object);
        item.getAttributes().add(this.getAttributes().createAttributes(libraryBuilder::getObjectById));
        return item;
    }
}
