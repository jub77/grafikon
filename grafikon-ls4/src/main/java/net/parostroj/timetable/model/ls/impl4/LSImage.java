package net.parostroj.timetable.model.ls.impl4;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import net.parostroj.timetable.model.TimetableImage;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.utils.IdGenerator;

/**
 * Storage for information about images.
 *
 * @author jub
 */
@XmlRootElement(name = "image")
@XmlType(propOrder = {"id", "filename", "imageWidth", "imageHeight"})
public class LSImage {

    private String id;
    private String filename;
    private int imageWidth;
    private int imageHeight;

    public LSImage(TimetableImage image) {
        this.id = image.getId();
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TimetableImage createTimetableImage(TrainDiagram diagram) {
        String newId = this.id != null ? this.id : IdGenerator.getInstance().getId();

        return diagram.getPartFactory().createImage(newId, filename, imageWidth, imageHeight);
    }
}
