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

public class LocalizedStringListPM<T extends Reference<LocalizedString>> extends AbstractPM {

    private final class LStringPM extends TextPM {

        private T localizedStringRef;

        public LStringPM(T lStringRef) {
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

    private LocalizationType<T> type;

    public LocalizedStringListPM() {
        list = new ListPM<>();
        selected = new LocalizedStringPM();
        list.addListListener(new ListAdapter() {
            @Override
            public void elementsSelected(ElementsSelectedEvent evt) {
                LStringPM at = list.getAt(evt.getBeginIndex());
                selectStringImpl(at.localizedStringRef);
            }

            @Override
            public void elementsDeselected(ElementsDeselectedEvent evt) {
                selected.init(null, null);
            }
        });
        PMManager.setup(this);
    }

    public void init(LocalizationType<T> type) {
        this.init(type, null);
    }

    public void init(LocalizationType<T> type, T preSelected) {
        this.type = type;
        list.clear();
        for (T stringRef : type.getStrings()) {
            LStringPM lStringPM = addReferenceImpl(stringRef);
            if (preSelected != null && preSelected.equals(stringRef)) {
                list.getSelection().add(lStringPM);
            }
        }
        list.revalidateElements();
    }

    private LStringPM addReferenceImpl(T stringRef) {
        LStringPM lStringPM = new LStringPM(stringRef);
        list.add(lStringPM);
        return lStringPM;
    }

    private void selectStringImpl(T ref) {
        LolizationEditResult result = selected.init(type.getNewOrEditedString(ref), type.getLocales());
        type.addOrUpdateEdited(ref, result);
    }

    public void selectReference(T ref) {
        list.getSelection().clear();
        for (LStringPM pm : list) {
            if (pm.localizedStringRef.equals(ref)) {
                list.getSelection().add(pm);
                break;
            }
        }
    }

    public T getSelectedReference() {
        LStringPM at = list.getAt(list.getSelection().getMinIndex());
        return at.localizedStringRef;
    }

    public void addReference(T ref) {
        this.addReferenceImpl(ref);
    }

    public void removeReference(T ref) {
        LStringPM pm = null;
        for (LStringPM item : list) {
            if (item.localizedStringRef.equals(ref)) {
                pm = item;
                break;
            }
        }
        if (pm != null) {
            list.remove(pm);
        }
    }
}
