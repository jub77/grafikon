package net.parostroj.timetable.output2.template;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import net.parostroj.timetable.model.Attachment;
import net.parostroj.timetable.model.OutputTemplate;
import net.parostroj.timetable.output2.OutputResources;


public class TemplateOutputResources implements OutputResources {

    private final OutputTemplate template;

    public TemplateOutputResources(OutputTemplate template) {
        this.template = template;
    }

    @Override
    public InputStream getStream(String key) {
        if (template != null) {
            for (Attachment attachment : template.getAttachments()) {
                if (attachment.getName().equals(key)) {
                    return attachment.toStream(StandardCharsets.UTF_8);
                }
            }
        }
        return null;
    }
}
