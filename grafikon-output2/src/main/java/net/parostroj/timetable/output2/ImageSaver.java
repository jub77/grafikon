package net.parostroj.timetable.output2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import net.parostroj.timetable.model.TimetableImage;
import net.parostroj.timetable.model.TrainDiagram;

/**
 * Saves html needed for HTML pages with output.
 * 
 * @author jub
 */
public class ImageSaver {

    private static final Set<String> PREDEFINED_IMAGES;
    private static final Logger LOG = Logger.getLogger(ImageSaver.class.getName());

    private TrainDiagram diagram;

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
            LOG.warning(String.format("Image %s not found.", image));
    }

    private void saveImage(File location, URL resLocation) throws IOException {
        LOG.finer(String.format("Saving file %s.", location.getName()));
        InputStream s = resLocation.openStream();
        OutputStream os = new FileOutputStream(location);
        try {
            byte[] buffer = new byte[5000];
            int len = 0;
            while ((len = s.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
        } finally {
            os.close();
            s.close();
        }
    }
}
