package net.parostroj.timetable.model.ls.impl4;

import java.io.*;

import net.parostroj.timetable.model.TimetableImage;

import net.parostroj.timetable.model.ls.LSException;
import net.parostroj.timetable.model.ls.LSSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.*;

/**
 * Class for loading/saving images from/to gtm.
 *
 * @author jub
 */
public class FileLoadSaveImages {

    private static final Logger log = LoggerFactory.getLogger(FileLoadSaveImages.class);

    private final String location;

    public FileLoadSaveImages(String location) {
        this.location = location;
    }

    /**
     * saves image for timetable.
     *
     * @param image image
     * @param sink sink
     */
    public void saveTimetableImage(TimetableImage image, LSSink sink) throws IOException, LSException {
        // copy image to zip
        String itemName = location + image.getFilename();
        if (image.getImageFile() == null) {
            // skip images without image file
            log.warn("Skipping image {} because the gtm doesn't contain a file.", image.getFilename());
            return;
        }

        ByteSource src = Files.asByteSource(image.getImageFile());
        src.copyTo(sink.nextItem(itemName));
    }

    /**
     * loads image for timetable.
     *
     * @param is input stream
     * @return file
     */
    public File loadTimetableImage(InputStream is) throws IOException {
        File tempFile = File.createTempFile("gt_", ".temp");

        Files.asByteSink(tempFile).writeFrom(is);
        tempFile.deleteOnExit();

        return tempFile;
    }
}
