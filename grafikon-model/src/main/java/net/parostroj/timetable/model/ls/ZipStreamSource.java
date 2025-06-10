package net.parostroj.timetable.model.ls;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

class ZipStreamSource implements LSSource {

    private final ZipInputStream zis;

    public ZipStreamSource(ZipInputStream zis) {
        this.zis = zis;
    }

    @Override
    public Item nextItem() throws LSException {
        try {
            ZipEntry entry = zis.getNextEntry();
            return entry != null ? new Item(entry.getName(), zis) : null;
        } catch (IOException e) {
            throw new LSException(e);
        }
    }

    @Override
    public void close() throws LSException {
        try {
            zis.close();
        } catch (IOException e) {
            throw new LSException(e);
        }
    }
}
