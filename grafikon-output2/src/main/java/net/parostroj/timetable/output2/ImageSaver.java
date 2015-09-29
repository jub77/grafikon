package net.parostroj.timetable.output2;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

import net.parostroj.timetable.model.TimetableImage;
import net.parostroj.timetable.model.TrainDiagram;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.*;

/**
 * Saves html needed for HTML pages with output.
 *
 * @author jub
 */
public class ImageSaver {

    private static final Logger log = LoggerFactory.getLogger(ImageSaver.class);

    private static final Set<String> PREDEFINED_IMAGES;

    private final TrainDiagram diagram;

    static {
        Set<String> images = new HashSet<String>();
        images.add("signal.gif");
        images.add("control_station.gif");
        images.add("trapezoid_sign.gif");
        images.add("arrow_seq.svg");
        PREDEFINED_IMAGES = Collections.unmodifiableSet(images);
    }

    public ImageSaver(TrainDiagram diagram) {
        this.diagram = diagram;
    }

    public void saveImage(String image, File directory, OutputResources resources) throws IOException {
        URL resLocation = null;
        if (PREDEFINED_IMAGES.contains(image)) {
            resLocation = ImageSaver.class.getResource("/images/" + image);
        } else {
            for (TimetableImage i : diagram.getImages()) {
                if (i.getFilename().equals(image)) {
                    resLocation = i.getImageFile().toURI().toURL();
                }
            }
        }
        if (resLocation != null) {
            this.saveImage(new File(directory,image), resLocation);
        } else {
            try (InputStream stream = resources.getStream(image)) {
                if (stream != null) {
                    saveImageStream(new File(directory, image), stream);
                } else {
                    log.warn("Image {} not found.", image);
                }
            }
        }
    }

    private void saveImageStream(File location, InputStream stream) throws IOException {
        log.trace("Saving stream to file {}", location.getName());
        Files.asByteSink(location).writeFrom(stream);
    }

    private void saveImage(File location, URL resLocation) throws IOException {
        log.trace("Saving file {}", location.getName());
        Resources.asByteSource(resLocation).copyTo(Files.asByteSink(location));
    }
}
