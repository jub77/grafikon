package net.parostroj.timetable.gui.pm;

import java.text.CollationKey;
import java.text.Collator;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;

import org.beanfabrics.Path;
import org.beanfabrics.event.ElementsDeselectedEvent;
import org.beanfabrics.event.ElementsSelectedEvent;
import org.beanfabrics.event.ListAdapter;
import org.beanfabrics.model.AbstractPM;
import org.beanfabrics.model.ListPM;
import org.beanfabrics.model.PMManager;
import org.beanfabrics.model.TextPM;

import net.parostroj.timetable.model.LocalizedString;
import net.parostroj.timetable.utils.Reference;

public class LocalizedStringListPM<T extends Reference<LocalizedString>> extends AbstractPM implements IPM<LocalizationType<T>> {

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

        @Override
        public String toString() {
            return getText();
        }

        @Override
        public Comparable<?> getComparable() {
            class DynamicTextComparable implements Comparable<DynamicTextComparable> {

                private CollationKey key;

                public DynamicTextComparable() {
                    key = Collator.getInstance().getCollationKey(getText());
                }

                @Override
                public int compareTo(DynamicTextComparable o) {
                    return key.compareTo(o.key);
                }
            }
            return new DynamicTextComparable();
        }
    }

    final ListPM<LStringPM> list;
    final LocalizedStringPM selected;

    private LocalizationType<T> type;
    private boolean sorted;

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

    @Override
    public void init(LocalizationType<T> type) {
        this.init(type, null);
    }

    public void setSorted(boolean sorted) {
        this.sorted = sorted;
    }

    public boolean isSorted() {
        return sorted;
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
        this.sortList();
    }

    private LStringPM addReferenceImpl(T stringRef) {
        LStringPM lStringPM = new LStringPM(stringRef);
        list.add(lStringPM);
        return lStringPM;
    }

    private void sortList() {
        if (sorted) {
            list.sortBy(true, new Path("this"));
        }
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
        this.sortList();
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

    public T getRef(Predicate<T> predicate) {
        return StreamSupport.stream(list.spliterator(), false).map(pm -> pm.localizedStringRef).filter(predicate)
                .findAny().orElse(null);
    }
}
