package net.parostroj.timetable.output2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 * Saves html needed for HTML pages with output.
 * 
 * @author jub
 */
public class ImageSaver {
    public enum Image {
        SIGNAL("signal.gif"), CONTROL_STATION("control_station.gif"),
        TRAPEZOID_SIGN("trapezoid_sign.gif");
        
        private String name;
        
        private Image(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
    
    public void saveImage(Image image, File directory) throws IOException {
        URL resLocation = ImageSaver.class.getResource("/images/" + image.getName());
        this.saveImage(new File(directory,image.getName()), resLocation);
    }

    private void saveImage(File location, URL resLocation) throws IOException {
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
    
    public void saveTrainTimetableImages(File directory) throws IOException {
       this.saveImage(Image.SIGNAL, directory);
       this.saveImage(Image.CONTROL_STATION, directory);
       this.saveImage(Image.TRAPEZOID_SIGN, directory);
    }
}
