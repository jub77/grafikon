/*
 * TimetableImage.java
 * 
 * Created on 23.9.2007, 9:13:55
 */
package net.parostroj.timetable.model;

import java.io.File;

/**
 * Pair with name of the image and its height.
 * 
 * @author jub
 */
public class TimetableImage {

    private int height;
    private int imageHeight;
    private int imageWidth;
    private String filename;
    private File imageFile;

    public TimetableImage(String filename) {
        this.filename = filename;
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

    public File getImageFile() {
        return imageFile;
    }

    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
    }

    @Override
    public String toString() {
        return String.format("%s (%d - %d x %d)", filename, height, imageWidth, imageHeight);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TimetableImage other = (TimetableImage) obj;
        if ((Object)this.filename != other.filename && (this.filename == null || !this.filename.equals(other.filename))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.filename != null ? this.filename.hashCode() : 0);
        return hash;
    }
}
