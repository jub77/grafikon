package net.parostroj.timetable.model;

@FunctionalInterface
public interface OutputTemplateMapping {
    OutputTemplate getTemplateById(String id);

    static OutputTemplateMapping createEmpty() {
        return id -> null;
    }
}
