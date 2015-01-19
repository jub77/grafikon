package net.parostroj.timetable.model.templates;

import javax.xml.bind.annotation.XmlType;

/**
 * Template information.
 * 
 * @author jub
 */
@XmlType(propOrder = {"name", "filename"})
public class Template {
    
    private String name;
    private String filename;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
