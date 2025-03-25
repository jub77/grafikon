package net.parostroj.timetable.gui.ini;

import org.apache.commons.configuration2.INIConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

class IniConfigCommons implements IniConfig {

    private final INIConfiguration ini;
    private final File file;

    public IniConfigCommons() {
        this(null);
    }

    public IniConfigCommons(File file) {
        this.file = file;
        this.ini = INIConfiguration.builder().build();
    }

    @Override
    public IniConfigSection getSection(String name) {
        return new IniConfigSectionCommons(ini, name);
    }

    @Override
    public void removeSection(String name) {
        ini.clearTree(IniConfigSectionCommons.escapeKey(name));
    }

    @Override
    public void load() throws IOException {
        if (file != null && file.exists()) {
            try {
                ini.read(new FileReader(file, StandardCharsets.UTF_8));
            } catch (ConfigurationException e) {
                throw new IOException(e);
            }
        }
    }

    @Override
    public void save() throws IOException {
        if (file != null) {
            try {
                ini.write(new FileWriter(file, StandardCharsets.UTF_8));
            } catch (ConfigurationException e) {
                throw new IOException(e);
            }
        }
    }
}
