package net.parostroj.timetable.model.ls;

import net.parostroj.timetable.model.RuntimeInfo;
import net.parostroj.timetable.model.TrainDiagram;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.zip.ZipOutputStream;

class ZipFileSink extends ZipStreamSink {

    private final File file;

    public ZipFileSink(File file) throws FileNotFoundException {
        super(new ZipOutputStream(new FileOutputStream(file)));
        this.file = file;
    }

    @Override
    public void updateInfo(TrainDiagram diagram) {
        diagram.getRuntimeInfo().setAttribute(RuntimeInfo.ATTR_FILE, file);
    }
}
