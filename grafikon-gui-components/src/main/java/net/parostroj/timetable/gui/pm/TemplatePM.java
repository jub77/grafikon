package net.parostroj.timetable.gui.pm;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import net.parostroj.timetable.loader.DataItem;
import org.beanfabrics.model.AbstractPM;
import org.beanfabrics.model.ITextPM;
import org.beanfabrics.model.PMManager;
import org.beanfabrics.model.TextPM;

import net.parostroj.timetable.model.ls.ModelVersion;

public class TemplatePM extends AbstractPM implements IPM<DataItem> {

    final LocalizedStringDefaultPM name;
    final LocalizedStringDefaultPM description;
    final ITextPM version;

    private Reference<DataItem> ref;

    public TemplatePM() {
        name = new LocalizedStringDefaultPM();
        description = new LocalizedStringDefaultPM();
        version = new TextPM();
        version.setEditable(false);
        PMManager.setup(this);
    }

    @Override
    public void init(DataItem template) {
        this.init(template, Collections.emptyList());
    }

    public void init(DataItem template, Collection<Locale> availableLocales) {
        ref = new WeakReference<>(template);
        name.init(template.name(), availableLocales);
        description.init(template.description(), availableLocales);
        initVersion(template);
    }

    private void initVersion(DataItem template) {
        ModelVersion ver = template.version();
        version.setText(ver == null ? "" : ver.toString());
    }

    public DataItem getTemplate() {
        return ref == null ? null : ref.get();
    }

    public boolean ok() {
        // write back
        DataItem template = ref != null ? ref.get() : null;
        if (template != null) {
            DataItem newTemplate = new DataItem(template.id(), template.id(), Map.of(), template.version(),
                    name.getCurrentEdit().get(), description.getCurrentEdit().get());
            // write values back
            ref = new SoftReference<>(newTemplate);
        }
        return true;
    }
}
