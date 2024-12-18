package net.parostroj.timetable.model;

import java.util.Collection;
import java.util.List;

public interface OutputTemplateStorage {
    OutputTemplate getTemplateById(String id);
    Collection<OutputTemplate> getTemplates();

    static OutputTemplateStorage createEmpty() {
        return new OutputTemplateStorage() {
            @Override
            public OutputTemplate getTemplateById(String id) {
                return null;
            }

            @Override
            public Collection<OutputTemplate> getTemplates() {
                return List.of();
            }
        };
    }
}
