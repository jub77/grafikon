package net.parostroj.timetable.gui.pm;

import net.parostroj.timetable.model.LocalizedString;
import net.parostroj.timetable.utils.Reference;

public class EditedLocalizedString {

    private final Reference<LocalizedString> reference;
    private EditResult editResult;

    public EditedLocalizedString(Reference<LocalizedString> reference, EditResult editResult) {
        this.reference = reference;
        this.editResult = editResult;
    }

    public Reference<LocalizedString> getReference() {
        return reference;
    }

    public EditResult getEdited() {
        return editResult;
    }

    void updateEdited(EditResult edited) {
        this.editResult = edited;
    }

    public void writeBack() {
        reference.set(editResult.get());
    }
}
