package net.parostroj.timetable.gui.pm;

import org.beanfabrics.model.AbstractPM;
import org.beanfabrics.model.OperationPM;
import org.beanfabrics.model.PMManager;
import org.beanfabrics.model.TextPM;
import org.beanfabrics.support.OnChange;
import org.beanfabrics.support.Operation;
import org.beanfabrics.support.Validation;

import net.parostroj.timetable.model.LocalizedString;
import net.parostroj.timetable.utils.AttributeReference;

public class ARLocalizedStringListPM<T extends AttributeReference<LocalizedString>> extends AbstractPM {

    final LocalizedStringListPM<T> localized;
    final TextPM newKey;

    final OperationPM add;
    final OperationPM remove;
    final OperationPM ok;

    private boolean nonEmpty;
    private boolean selected;
    private ARLocalizationType<T> type;

    public ARLocalizedStringListPM() {
        localized = new LocalizedStringListPM<>();
        add = new OperationPM();
        remove = new OperationPM();
        ok = new OperationPM();
        newKey = new TextPM();

        PMManager.setup(this);
    }

    public void init(ARLocalizationType<T> type) {
        this.type = type;
        this.init(type, null);
    }

    public void init(ARLocalizationType<T> type, T preSelected) {
        localized.init(type, preSelected);
    }

    @Operation(path = { "add" })
    public boolean add() {
        T newRef = type.createNew(newKey.getText());
        localized.addReference(newRef);
        newKey.setText("");
        localized.selectReference(newRef);
        return true;
    }

    @Operation(path = { "remove" })
    public boolean remove() {
        T ref = localized.getSelectedReference();
        type.addToRemove(ref);
        localized.removeReference(ref);
        return true;
    }

    @Operation(path = { "ok" })
    public boolean ok() {
        type.writeBack();
        return true;
    }

    @OnChange(path = {"localized.list"})
    public void changedList() {
        boolean oldSelected = selected;
        selected = !localized.list.getSelection().isEmpty();
        if (oldSelected != selected) {
            this.getPropertyChangeSupport().firePropertyChange("selected", oldSelected, selected);
        }
    }

    @OnChange(path = { "newKey" })
    public void editedKey() {
        boolean oldNonEmpty = nonEmpty;
        nonEmpty = !newKey.isEmpty();
        if (nonEmpty != oldNonEmpty) {
            this.getPropertyChangeSupport().firePropertyChange("nonEmpty", oldNonEmpty, nonEmpty);
        }
    }

    @Validation(path = { "add" })
    public boolean canAdd() {
        return nonEmpty;
    }

    @Validation(path = {"remove"})
    public boolean canRemove() {
        return selected;
    }

    public void setSorted(boolean sorted) {
        localized.setSorted(sorted);
    }
}
