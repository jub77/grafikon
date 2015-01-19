package net.parostroj.timetable.gui.dialogs;

import java.awt.Frame;
import java.util.Collection;

import net.parostroj.timetable.model.Line;
import net.parostroj.timetable.model.LineClass;
import net.parostroj.timetable.model.TimeIntervalDirection;
import net.parostroj.timetable.utils.IdGenerator;

public class EditLineClassesDialog extends EditItemsDialog<LineClass> {

    public EditLineClassesDialog(Frame parent, boolean modal) {
        super(parent, modal);
    }

    @Override
    protected Collection<LineClass> getList() {
        return diagram.getNet().getLineClasses();
    }

    @Override
    protected void add(LineClass item, int index) {
        diagram.getNet().addLineClass(item, index);
    }

    @Override
    protected void remove(LineClass item) {
        diagram.getNet().removeLineClass(item);
    }

    @Override
    protected void move(LineClass item, int oldIndex, int newIndex) {
        diagram.getNet().moveLineClass(oldIndex, newIndex);
    }

    @Override
    protected boolean deleteAllowed(LineClass lineClass) {
        if (lineClass == null)
            return false;
        for (Line line : diagram.getNet().getLines()) {
            if (line.getLineClass(TimeIntervalDirection.FORWARD) == lineClass)
                return false;
            if (line.getLineClass(TimeIntervalDirection.BACKWARD) == lineClass)
                return false;
        }
        return true;
    }

    @Override
    protected LineClass createNew(String name) {
        return new LineClass(IdGenerator.getInstance().getId(), name);
    }

}
