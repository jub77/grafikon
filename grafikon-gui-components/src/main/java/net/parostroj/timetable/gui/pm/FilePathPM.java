package net.parostroj.timetable.gui.pm;

import java.io.File;

import org.beanfabrics.model.PMManager;
import org.beanfabrics.model.TextPM;
import org.beanfabrics.support.Validation;

public class FilePathPM extends TextPM {

    public enum Type { FILE, DIRECTORY, ANY }

    boolean exist;

    public FilePathPM(final Type type) {
        addPropertyChangeListener(evt -> {
            if (evt.getPropertyName().equals("text")) {
                File file = getFile();
                exist = file.exists();
                if (exist) {
                    switch (type) {
                        case FILE: exist = file.isFile(); break;
                        case DIRECTORY: exist = file.isDirectory(); break;
                        default: // nothing
                            break;
                    }
                }
            }
        });
        PMManager.setup(this);
    }

    public File getFile() {
        return new File(getText());
    }

    public void setFile(File file) {
        setText(file.getPath());
    }

    @Validation
    public boolean fileExists() {
        return exist;
    }
}
