package net.parostroj.timetable.model.ls.impl4;

import java.io.*;
import java.nio.channels.*;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import net.parostroj.timetable.model.TimetableImage;

/**
 * Class for loading/saving images from/to gtm.
 * 
 * @author jub
 */
public class FileLoadSaveImages {

    private static final Logger LOG = Logger.getLogger(FileLoadSaveImages.class.getName());
    private final String location;
    
    public FileLoadSaveImages(String location) {
        this.location = location;
    }
    
    /**
     * saves image for timetable.
     * 
     * @param image image
     * @param os zip output stream
     * @throws java.io.IOException
     */
    public void saveTimetableImage(TimetableImage image, ZipOutputStream os) throws IOException {
        // copy image to zip
        ZipEntry entry = new ZipEntry(location + image.getFilename());
        if (image.getImageFile() == null) {
            // skip images without image file
            LOG.warning(String.format("Skipping image %s because the gtm doesn't contain a file.", image.getFilename()));
            return;
        }
        FileInputStream is = new FileInputStream(image.getImageFile());
        try {
            FileChannel ic = is.getChannel();
            entry.setSize(ic.size());
            os.putNextEntry(entry);
            WritableByteChannel oc = Channels.newChannel(os);
            ic.transferTo(0, ic.size(), oc);
        } finally {
            is.close();
        }
    }

    private static long CHUNK = 100000;
    
    /**
     * loads image for timetable.
     * 
     * @param is input stream
     * @param entry current entry
     * @return file
     * @throws java.io.IOException
     */
    public File loadTimetableImage(ZipInputStream is, ZipEntry entry) throws IOException {
        File tempFile = File.createTempFile("gt_", ".temp");
        ReadableByteChannel ic = Channels.newChannel(is);
        FileOutputStream os = new FileOutputStream(tempFile);
        FileChannel oc = os.getChannel();
        try {
            long position = 0;
            long read = 0;
            while ((read = oc.transferFrom(ic, position, CHUNK)) != 0) {
                position += read;
            }
            tempFile.deleteOnExit();
        } finally {
            os.close();
        }
        return tempFile;
    }
}
