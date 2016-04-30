package net.parostroj.timetable.model.ls.impl4;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Attachment - contains only reference to data.
 *
 * @author jub
 */
@XmlType
public class LSAttachment {

    private String name;
    private String type;
    private String ref;
    private byte[] binaryData;
    private String textData;

    public LSAttachment() {
    }

    public LSAttachment(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public LSAttachment(String name, String type, String ref) {
        this(name, type);
        this.ref = ref;
    }

    @XmlAttribute
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @XmlAttribute
    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public byte[] getBinaryData() {
        return binaryData;
    }

    public void setBinaryData(byte[] binaryData) {
        this.binaryData = binaryData;
    }

    public String getTextData() {
        return textData;
    }

    public void setTextData(String textData) {
        this.textData = textData;
    }

    @Override
    public String toString() {
        return ref != null ? String.format("<%s,%s,%s>", name, type, ref) : String.format("<%s,%s>", name, type);
    }
}
