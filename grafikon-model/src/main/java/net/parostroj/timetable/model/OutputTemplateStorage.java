package net.parostroj.timetable.model;

public interface OutputTemplateStorage {
    OutputTemplate getTemplateById(String id);

    static OutputTemplateStorage createEmpty() {
        return id -> null;
    }
}
