/*
 * TimetableImage.java
 * 
 * Created on 23.9.2007, 9:13:55
 */
package net.parostroj.timetable.model;

import java.io.File;
import net.parostroj.timetable.visitors.TrainDiagramVisitor;
import net.parostroj.timetable.visitors.Visitable;

/**
 * Image information.
 * 
 * @author jub
 */
public class TimetableImage implements ObjectWithId, Visitable {

    private final String id;
    private final int imageHeight;
    private final int imageWidth;
    private final String filename;
    private File imageFile;

    TimetableImage(String id, String filename, int width, int height) {
        this.filename = filename;
        this.imageWidth = width;
        this.imageHeight = height;
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getFilename() {
        return filename;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public File getImageFile() {
        return imageFile;
    }

    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
    }

    @Override
    public void accept(TrainDiagramVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return String.format("%s (%d x %d)", filename, imageWidth, imageHeight);
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
