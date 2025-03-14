package net.parostroj.timetable.model.save;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import net.parostroj.timetable.model.TimetableImage;
import net.parostroj.timetable.model.TrainDiagram;

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
     * @param os zip output stream
     */
    public void saveTimetableImages(TrainDiagram diagram, ZipOutputStream os) throws IOException {
        for (TimetableImage image : diagram.getImages()) {
            // copy image to zip
            ZipEntry entry = new ZipEntry("images/" + image.getFilename());
            if (image.getImageFile() == null)
                // skip images without image file
                continue;
            File imageFile = image.getImageFile();
            entry.setSize(imageFile.length());
            os.putNextEntry(entry);
            WritableByteChannel oc = Channels.newChannel(os);
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
     * @param zipFile zip file
     */
    public void loadTimetableImages(TrainDiagram diagram, ZipFile zipFile) throws IOException {
        for (TimetableImage image : diagram.getImages()) {
            ZipEntry entry = zipFile.getEntry("images/" + image.getFilename());
            if (entry != null) {
                File tempFile = File.createTempFile("gt_", ".temp");
                InputStream is = zipFile.getInputStream(entry);
                ReadableByteChannel ic = Channels.newChannel(is);
                try (FileOutputStream os = new FileOutputStream(tempFile)) {
                    FileChannel oc = os.getChannel();
                    oc.transferFrom(ic, 0, entry.getSize());
                }
                image.setImageFile(tempFile);
                tempFile.deleteOnExit();
            }
        }
    }
}
