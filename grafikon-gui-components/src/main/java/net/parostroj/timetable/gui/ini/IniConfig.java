package net.parostroj.timetable.gui.ini;

public interface IniConfig {

    IniConfigSection getSection(String name);

    void removeSection(String name);
}
