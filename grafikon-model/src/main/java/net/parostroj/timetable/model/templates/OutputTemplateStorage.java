package net.parostroj.timetable.model.templates;

import net.parostroj.timetable.model.OutputTemplate;
import net.parostroj.timetable.model.OutputTemplateMapping;

import java.util.Collection;

public interface OutputTemplateStorage extends OutputTemplateMapping {
    Collection<OutputTemplate> getTemplates();
}
