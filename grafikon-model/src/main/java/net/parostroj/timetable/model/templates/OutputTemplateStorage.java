package net.parostroj.timetable.model.templates;

import net.parostroj.timetable.model.OutputTemplateMapping;

public interface OutputTemplateStorage extends OutputTemplateMapping {
    static OutputTemplateStorage createEmpty() {
        return id -> null;
    }
}
