package net.parostroj.timetable.output2;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import net.parostroj.timetable.model.TimetableImage;
import net.parostroj.timetable.model.TrainDiagram;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;
import com.google.common.io.Resources;

/**
 * Saves html needed for HTML pages with output.
 *
 * @author jub
 */
public class ImageSaver {

    private static final Set<String> PREDEFINED_IMAGES;
    private static final Logger LOG = LoggerFactory.getLogger(ImageSaver.class.getName());

    private final TrainDiagram diagram;

    static {
        Set<String> images = new HashSet<String>();
        images.add("signal.gif");
        images.add("control_station.gif");
        images.add("trapezoid_sign.gif");
        PREDEFINED_IMAGES = Collections.unmodifiableSet(images);
    }

    public ImageSaver(TrainDiagram diagram) {
        this.diagram = diagram;
    }

    public void saveImage(String image, File directory) throws IOException {
        URL resLocation = null;
        if (PREDEFINED_IMAGES.contains(image))
            resLocation = ImageSaver.class.getResource("/images/" + image);
        else {
            List<TimetableImage> images = diagram.getImages();
            for (TimetableImage i : images) {
                if (i.getFilename().equals(image)) {
                    resLocation = i.getImageFile().toURI().toURL();
                }
            }
        }
        if (resLocation != null)
            this.saveImage(new File(directory,image), resLocation);
        else
            LOG.warn("Image {} not found.", image);
    }

    private void saveImage(File location, URL resLocation) throws IOException {
        LOG.trace("Saving file {}.", location.getName());
        Resources.asByteSource(resLocation).copyTo(Files.asByteSink(location));
    }
}
