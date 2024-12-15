package net.parostroj.timetable.model.ls.impl3;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import net.parostroj.timetable.model.TimetableImage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteSource;
import com.google.common.io.Files;

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
     * @param os zip output stream
     */
    public void saveTimetableImage(TimetableImage image, ZipOutputStream os) throws IOException {
        // copy image to zip
        ZipEntry entry = new ZipEntry(location + image.getFilename());
        if (image.getImageFile() == null) {
            // skip images without image file
            log.warn("Skipping image {} because the gtm doesn't contain a file.", image.getFilename());
            return;
        }

        ByteSource src = Files.asByteSource(image.getImageFile());
        entry.setSize(src.size());
        os.putNextEntry(entry);
        src.copyTo(os);
    }

    /**
     * loads image for timetable.
     *
     * @param is input stream
     * @return file
     */
    public File loadTimetableImage(ZipInputStream is) throws IOException {
        File tempFile = File.createTempFile("gt_", ".temp");

        Files.asByteSink(tempFile).writeFrom(is);
        tempFile.deleteOnExit();

        return tempFile;
    }
}
