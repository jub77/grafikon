package net.parostroj.timetable.gui.ini;

import java.io.IOException;

public interface IniConfig {

    IniConfigSection getSection(String name);

    void removeSection(String name);

    void load() throws IOException;

    void save() throws IOException;
}
