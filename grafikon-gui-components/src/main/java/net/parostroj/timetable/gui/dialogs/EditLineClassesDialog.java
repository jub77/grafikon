package net.parostroj.timetable.gui.dialogs;

import java.awt.Frame;
import java.util.Collection;

import net.parostroj.timetable.model.*;
import net.parostroj.timetable.utils.IdGenerator;

public class EditLineClassesDialog extends EditItemsDialog<LineClass, TrainDiagram> {

    public EditLineClassesDialog(Frame parent, boolean modal) {
        super(parent, modal);
    }

    @Override
    protected Collection<LineClass> getList() {
        return element.getNet().getLineClasses().toList();
    }

    @Override
    protected void add(LineClass item, int index) {
        element.getNet().getLineClasses().add(item, index);
    }

    @Override
    protected void remove(LineClass item) {
        element.getNet().getLineClasses().remove(item);
    }

    @Override
    protected void move(LineClass item, int oldIndex, int newIndex) {
        element.getNet().getLineClasses().move(oldIndex, newIndex);
    }

    @Override
    protected boolean deleteAllowed(LineClass lineClass) {
        if (lineClass == null)
            return false;
        for (Line line : element.getNet().getLines()) {
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
