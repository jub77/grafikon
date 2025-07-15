package net.parostroj.timetable.model.ls;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class ResourceDirectorySource implements LSSource {

    static final String FILE_LIST = "files";

    private final String location;
    private final List<String> files = new ArrayList<>();
    private InputStream current;
    private Iterator<String> iterator;

    ResourceDirectorySource(String location) throws IOException {
        this.location = location;
        try (BufferedReader fr = new BufferedReader(new InputStreamReader(getStream(location + "/" + FILE_LIST), StandardCharsets.UTF_8))) {
            String line;
            while ((line = fr.readLine()) != null) {
                files.add(line);
            }
        }
        iterator = files.iterator();
    }

    private InputStream getStream(String filename) {
        return this.getClass().getResourceAsStream(filename);
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
                current = getStream(location + "/" + name);
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
