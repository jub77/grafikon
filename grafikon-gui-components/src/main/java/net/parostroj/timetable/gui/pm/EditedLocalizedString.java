package net.parostroj.timetable.gui.pm;

import net.parostroj.timetable.model.LocalizedString;
import net.parostroj.timetable.utils.Reference;

public class EditedLocalizedString {

    private final Reference<LocalizedString> reference;
    private LolizationEditResult editResult;

    public EditedLocalizedString(Reference<LocalizedString> reference, LolizationEditResult editResult) {
        this.reference = reference;
        this.editResult = editResult;
    }

    public Reference<LocalizedString> getReference() {
        return reference;
    }

    public LolizationEditResult getEdited() {
        return editResult;
    }

    void updateEdited(LolizationEditResult edited) {
        this.editResult = edited;
    }

    public void writeBack() {
        reference.set(editResult.get());
    }
}
