package net.parostroj.timetable.model.save;

import com.google.common.io.Files;
import net.parostroj.timetable.model.TimetableImage;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.ls.LSException;
import net.parostroj.timetable.model.ls.LSSink;
import net.parostroj.timetable.model.ls.LSSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.HashMap;
import java.util.Map;

/**
 * Class for loading/saving images from/to gtm.
 *
 * @author jub
 */
public class LoadSaveImages {
    /**
     * saves images for timetable.
     *
     * @param diagram train diagram
     * @param sink sink
     */
    public void saveTimetableImages(TrainDiagram diagram, LSSink sink) throws IOException, LSException {
        for (TimetableImage image : diagram.getImages()) {
            // copy image to zip
            String itemName = "images/" + image.getFilename();
            if (image.getImageFile() == null)
                // skip images without image file
                continue;
            File imageFile = image.getImageFile();
            WritableByteChannel oc = Channels.newChannel(sink.nextItem(itemName));
            try (FileInputStream is = new FileInputStream(imageFile)) {
                FileChannel ic = is.getChannel();
                ic.transferTo(0, ic.size(), oc);
            }
        }
    }

    /**
     * loads images for timetable.
     *
     * @param diagram train diagram
     * @param source source
     */
    public void loadTimetableImages(TrainDiagram diagram, LSSource source) throws IOException, LSException {
        Map<String, TimetableImage> images = new HashMap<>();
        for (TimetableImage image : diagram.getImages()) {
            images.put("images/" + image.getFilename(), image);
        }
        LSSource.Item item;
        while ((item = source.nextItem()) != null) {
            if (images.containsKey(item.name())) {
                File tempFile = File.createTempFile("gt_", ".temp");
                InputStream is = item.stream();
                Files.asByteSink(tempFile).writeFrom(is);
                tempFile.deleteOnExit();
            } else {
                throw new LSException("Unexpected image: " + item.name());
            }
        }
    }
}
