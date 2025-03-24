package net.parostroj.timetable.gui.ini;

import org.ini4j.Ini;

import java.io.File;
import java.io.IOException;

class IniConfigIni4j implements IniConfig {

    private final Ini ini;
    private final File file;

    public IniConfigIni4j() {
        this(null);
    }

    public IniConfigIni4j(File file) {
        this.file = file;
        this.ini = new Ini();
        this.ini.getConfig().setEscape(false);
        this.ini.getConfig().setEmptySection(true);
    }

    @Override
    public IniConfigSection getSection(String name) {
        if (!ini.containsKey(name)) {
            ini.add(name);
        }
        return new IniConfigSectionIni4j(ini, ini.get(name));
    }

    @Override
    public void removeSection(String name) {
        ini.remove(name);
    }

    @Override
    public void load() throws IOException {
        if (file != null && file.exists()) {
            ini.load(file);
        }
    }

    @Override
    public void save() throws IOException {
        if (file != null) {
            ini.store(file);
        }
    }
}
