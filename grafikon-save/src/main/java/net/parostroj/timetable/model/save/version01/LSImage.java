/*
 * LSImage.java
 * 
 * Created on 23.9.2007, 9:18:21
 */
package net.parostroj.timetable.model.save.version01;

import net.parostroj.timetable.model.TimetableImage;

/**
 * Storage for images at the end of the timetable.
 * 
 * @author jub
 */
public class LSImage {
    
    private int height;
    
    private String filename;
    
    public LSImage(TimetableImage image) {
        this.filename = image.getFilename();
    }

    public LSImage() {
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void visit(LSVisitor visitor) {
        visitor.visit(this);
    }
}
