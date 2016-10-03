package net.parostroj.timetable.gui.pm;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

import org.beanfabrics.model.AbstractPM;
import org.beanfabrics.model.ITextPM;
import org.beanfabrics.model.PMManager;
import org.beanfabrics.model.TextPM;

import net.parostroj.timetable.model.ls.ModelVersion;
import net.parostroj.timetable.model.templates.Template;

public class TemplatePM extends AbstractPM implements IPM<Template> {

    final LocalizedStringDefaultPM name;
    final LocalizedStringDefaultPM description;
    final ITextPM version;

    private WeakReference<Template> ref;

    public TemplatePM() {
        name = new LocalizedStringDefaultPM();
        description = new LocalizedStringDefaultPM();
        version = new TextPM();
        version.setEditable(false);
        PMManager.setup(this);
    }

    @Override
    public void init(Template template) {
        this.init(template, Collections.emptyList());
    }

    public void init(Template template, Collection<Locale> availableLocales) {
        ref = new WeakReference<>(template);
        name.init(template.getName(), availableLocales);
        description.init(template.getDescription(), availableLocales);
        initVersion(template);
    }

    private void initVersion(Template template) {
        ModelVersion ver = template.getVersion();
        version.setText(ver == null ? "" : ver.toString());
    }

    public Template getTemplate() {
        return ref == null ? null : ref.get();
    }

    public boolean ok() {
        // write back
        Template template = ref != null ? ref.get() : null;
        if (template != null) {
            // write values back
            template.setName(name.getCurrentEdit().get());
            template.setDescription(description.getCurrentEdit().get());
        }
        return true;
    }
}
