package net.parostroj.timetable.gui.dialogs;

import net.parostroj.timetable.gui.utils.ResourceLoader;

public enum ImportMatch {

    NAME("import.match.name"),
    ID("import.match.id");
    private String key;
    private String text;

    private ImportMatch(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        if (text == null) {
            text = ResourceLoader.getString(key);
        }
        return text;
    }
}
