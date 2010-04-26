package net.parostroj.timetable.model.ls.impl4;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import net.parostroj.timetable.model.TimetableImage;

/**
 * Storage for information about images.
 * 
 * @author jub
 */
@XmlRootElement(name = "image")
@XmlType(propOrder = {"filename", "imageWidth", "imageHeight"})
public class LSImage {

    private String filename;
    private int imageWidth;
    private int imageHeight;

    public LSImage(TimetableImage image) {
        this.filename = image.getFilename();
        this.imageHeight = image.getImageHeight();
        this.imageWidth = image.getImageWidth();
    }

    public LSImage() {
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @XmlElement(name = "image_height")
    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    @XmlElement(name = "image_width")
    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }
    
    public TimetableImage createTimetableImage() {
        TimetableImage image = new TimetableImage(filename, imageWidth, imageHeight);
        return image;
    }
}
