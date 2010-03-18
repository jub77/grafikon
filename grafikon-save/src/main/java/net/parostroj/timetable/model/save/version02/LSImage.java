/*
 * LSImage.java
 * 
 * Created on 23.9.2007, 9:18:21
 */

package net.parostroj.timetable.model.save.version02;

import net.parostroj.timetable.model.TimetableImage;

/**
 * Storage for images at the end of the timetable.
 * 
 * @author jub
 */
public class LSImage {
    
    private int height;

    private int imageWidth;
    
    private int imageHeight;
    
    private String filename;
    
    public LSImage(TimetableImage image) {
        this.height = image.getHeight();
        this.imageWidth = image.getImageWidth();
        this.imageHeight = image.getImageHeight();
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

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public void visit(LSVisitor visitor) {
        visitor.visit(this);
    }
}
