package net.parostroj.timetable.model.ls;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

class ZipStreamSink implements LSSink {

    private final ZipOutputStream zos;

    public ZipStreamSink(ZipOutputStream zos) {
        this.zos = zos;
    }

    @Override
    public OutputStream nextItem(String name) throws LSException {
        try {
            zos.putNextEntry(new ZipEntry(name));
            return zos;
        } catch (IOException e) {
            throw new LSException("Error creating entry");
        }
    }

    @Override
    public void close() throws LSException {
        try {
            zos.close();
        } catch (IOException e) {
            throw new LSException(e);
        }
    }
}
