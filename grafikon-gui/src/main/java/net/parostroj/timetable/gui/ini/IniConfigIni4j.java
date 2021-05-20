package net.parostroj.timetable.gui.ini;

import org.ini4j.Ini;

class IniConfigIni4j implements IniConfig {

    private final Ini ini;

    public IniConfigIni4j(Ini ini) {
        this.ini = ini;
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
}
