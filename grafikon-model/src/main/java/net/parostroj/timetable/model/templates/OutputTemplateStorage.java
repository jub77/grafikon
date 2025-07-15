package net.parostroj.timetable.model.templates;

import net.parostroj.timetable.model.LocalizedString;
import net.parostroj.timetable.model.OutputTemplate;
import net.parostroj.timetable.model.OutputTemplateMapping;

import java.util.Collection;
import java.util.Map;

public interface OutputTemplateStorage extends OutputTemplateMapping {

    record Category(String id, LocalizedString name) {}

    Collection<OutputTemplate> getTemplates();

    Map<Category, Collection<OutputTemplate>> getTemplatesByCategory();
}
