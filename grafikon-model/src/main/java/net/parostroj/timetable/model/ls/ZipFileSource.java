package net.parostroj.timetable.model.ls;

import net.parostroj.timetable.model.RuntimeInfo;
import net.parostroj.timetable.model.TrainDiagram;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.zip.ZipInputStream;

class ZipFileSource extends ZipStreamSource {

    private final File file;

    ZipFileSource(File file) throws FileNotFoundException {
        super(new ZipInputStream(new FileInputStream(file)));
        this.file = file;
    }

    @Override
    public void updateInfo(TrainDiagram diagram) {
        diagram.getRuntimeInfo().setAttribute(RuntimeInfo.ATTR_FILE, file);
    }
}
