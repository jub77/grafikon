package net.parostroj.timetable.gui.dialogs;

import java.awt.Color;
import java.awt.Window;
import java.util.Collection;

import net.parostroj.timetable.model.CopyFactory;
import net.parostroj.timetable.model.LocalizedString;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainType;

/**
 * Dialog for selection train type for editing.
 *
 * @author jub
 */
public class TrainTypesDialog extends EditItemsDialog<TrainType, TrainDiagram> {

    private static final long serialVersionUID = 1L;

	public TrainTypesDialog(Window parent, boolean modal, boolean move, boolean edit, boolean newByName, boolean copy,
            boolean multiple) {
        super(parent, modal, move, edit, newByName, copy, multiple);
    }

    public static TrainTypesDialog newInstance(Window parent, boolean modal) {
        return newBuilder(TrainTypesDialog.class).setMove(true).setEdit(true).setNewByName(true).setMultiple(true)
                .setCopy(true).build(parent, modal);
    }

    @Override
    protected Collection<TrainType> getList() {
        return element.getTrainTypes();
    }

    @Override
    protected void add(TrainType item, int index) {
        element.getTrainTypes().add(index, item);
    }

    @Override
    protected void remove(TrainType item) {
        element.getTrainTypes().remove(item);
    }

    @Override
    protected void move(TrainType item, int oldIndex, int newIndex) {
        element.getTrainTypes().move(oldIndex, newIndex);
    }

    @Override
    protected boolean deleteAllowed(TrainType item) {
        return !element.getTrains().stream().anyMatch(train -> train.getType() == item);
    }

    @Override
    protected TrainType createNew(String name) {
        TrainType newType = element.getPartFactory().createTrainType(element.getPartFactory().createId());
        newType.setDesc(LocalizedString.fromString(name));
        newType.setAbbr(LocalizedString.fromString(name));
        newType.setColor(Color.BLACK);
        return newType;
    }

    @Override
    protected TrainType copy(String name, TrainType item) {
        CopyFactory copyFactory = new CopyFactory(element.getPartFactory());
        TrainType copiedType = copyFactory.copy(item, element.getPartFactory().createId());
        copiedType.setDesc(LocalizedString.fromString(name));
        return copiedType;
    }

    @Override
    protected void edit(TrainType item) {
        TrainTypeDialog editDialog = new TrainTypeDialog(this, true);
        editDialog.setLocationRelativeTo(this);
        editDialog.showDialog(item, element);
    }
}
