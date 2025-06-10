package net.parostroj.timetable.model.ls;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class DirectorySource implements LSSource {

    static final String FILE_LIST = "files";

    private final File file;
    private final List<String> files = new ArrayList<>();
    private InputStream current;
    private Iterator<String> iterator;

    DirectorySource(File file) throws IOException {
        this.file = file;
        try (BufferedReader fr = new BufferedReader(new FileReader(new File(file, FILE_LIST)))) {
            String line;
            while ((line = fr.readLine()) != null) {
                files.add(line);
            }
        }
        iterator = files.iterator();
    }

    @Override
    public Item nextItem() throws LSException {
        try {
            if (current != null) {
                current.close();
                current = null;
            }
            if (iterator.hasNext()) {
                String name = iterator.next();
                File itemFile = new File(file, name);
                current = new FileInputStream(itemFile);
                return new Item(name, current);
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new LSException(e);
        }
    }

    @Override
    public void close() throws LSException {
        if (current != null) {
            try {
                current.close();
                current = null;
                iterator = files.iterator();
            } catch (IOException e) {
                throw new LSException(e);
            }
        }
    }
}
