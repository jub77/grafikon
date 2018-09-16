package net.parostroj.timetable.gui.dialogs;

import java.awt.Window;
import java.util.Collection;

import net.parostroj.timetable.model.*;
import net.parostroj.timetable.utils.IdGenerator;

public class EditLineClassesDialog extends EditItemsDialog<LineClass, TrainDiagram> {

    private static final long serialVersionUID = 1L;

	public EditLineClassesDialog(Window parent, boolean modal, boolean move, boolean edit, boolean newByName,
            boolean copy, boolean multiple) {
        super(parent, modal, move, edit, newByName, copy, multiple);
    }

    public static EditLineClassesDialog newInstance(Window parent, boolean modal) {
        return newBuilder(EditLineClassesDialog.class).setMove(true).setNewByName(true).build(parent, modal);
    }

    @Override
    protected Collection<LineClass> getList() {
        return element.getNet().getLineClasses();
    }

    @Override
    protected void add(LineClass item, int index) {
        element.getNet().getLineClasses().add(index, item);
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
        LineClass lineClass = new LineClass(IdGenerator.getInstance().getId());
        lineClass.setName(name);
        return lineClass;
    }

}
