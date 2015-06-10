package net.parostroj.timetable.output2;

import net.parostroj.timetable.model.ExecutableTextTemplate;

public interface TemplateFactory {

    ExecutableTextTemplate getTemplate(String type);
}
