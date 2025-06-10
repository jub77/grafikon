package net.parostroj.timetable.model.ls;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

class DirectorySink implements LSSink {

    private final File file;
    private OutputStream current;
    private final List<String> files;

    public DirectorySink(File file) {
        this.file = file;
        this.files = new ArrayList<>();
    }

    @Override
    public OutputStream nextItem(String name) throws LSException {
        try {
            if (current != null) {
                current.close();
                current = null;
            }
            File itemFile = new File(file, name);
            itemFile.getParentFile().mkdirs();
            current = new FileOutputStream(itemFile);
            files.add(name);
            return current;
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
                File fileList = new File(file, DirectorySource.FILE_LIST);
                try (Writer fw = new BufferedWriter(new FileWriter(fileList, StandardCharsets.UTF_8))) {
                    for (String name : files) {
                        fw.write(name);
                        fw.write("\n");
                    }
                }
                files.clear();
            } catch (IOException e) {
                throw new LSException(e);
            }
        }
    }
}
