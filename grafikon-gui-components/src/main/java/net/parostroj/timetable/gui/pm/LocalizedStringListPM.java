package net.parostroj.timetable.gui.pm;

import org.beanfabrics.event.ElementsDeselectedEvent;
import org.beanfabrics.event.ElementsSelectedEvent;
import org.beanfabrics.event.ListAdapter;
import org.beanfabrics.model.AbstractPM;
import org.beanfabrics.model.ListPM;
import org.beanfabrics.model.PMManager;
import org.beanfabrics.model.TextPM;

import net.parostroj.timetable.model.LocalizedString;
import net.parostroj.timetable.utils.Reference;

public class LocalizedStringListPM extends AbstractPM {

    private final class LStringPM extends TextPM {

        private Reference<LocalizedString> localizedStringRef;

        public LStringPM(Reference<LocalizedString> lStringRef) {
            this.localizedStringRef = lStringRef;
        }

        @Override
        public String getText() {
            return localizedStringRef == null ? null
                    : (type == null || type.getConversion() == null
                            ? localizedStringRef.get().getDefaultString()
                            : type.getConversion().toString(localizedStringRef));
        }

        @Override
        public boolean isEditable() {
            return false;
        }
    }

    final ListPM<LStringPM> list;
    final LocalizedStringPM selected;

    private LocalizationType type;

    public LocalizedStringListPM() {
        list = new ListPM<>();
        selected = new LocalizedStringPM();
        list.addListListener(new ListAdapter() {
            @Override
            public void elementsSelected(ElementsSelectedEvent evt) {
                LStringPM at = list.getAt(evt.getBeginIndex());
                EditResult result = selected.init(type.getNewOrEdited(at.localizedStringRef), type.getLocales());
                type.addOrUpdateEdited(at.localizedStringRef, result);
            }

            @Override
            public void elementsDeselected(ElementsDeselectedEvent evt) {
                selected.init(null, null);
            }
        });
        PMManager.setup(this);
    }

    public void init(LocalizationType type) {
        this.type = type;
        list.clear();
        for (Reference<LocalizedString> stringRef : type.getStrings()) {
            list.add(new LStringPM(stringRef));
        }
        list.revalidateElements();
    }
}
