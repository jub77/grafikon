package net.parostroj.timetable.gui.dialogs;

import java.awt.Frame;
import java.util.Collection;

import net.parostroj.timetable.model.Region;
import net.parostroj.timetable.utils.IdGenerator;

public class EditRegionsDialog extends EditItemsDialog<Region> {

    public EditRegionsDialog(Frame parent, boolean modal) {
        super(parent, modal);
    }

    @Override
    protected Collection<Region> getList() {
        return diagram.getNet().getRegions().get();
    }

    @Override
    protected void add(Region item, int index) {
        diagram.getNet().getRegions().add(item, index);
    }

    @Override
    protected void remove(Region item) {
        diagram.getNet().getRegions().remove(item);
    }

    @Override
    protected void move(Region item, int oldIndex, int newIndex) {
        diagram.getNet().getRegions().move(oldIndex, newIndex);
    }

    @Override
    protected boolean deleteAllowed(Region item) {
        return true;
    }

    @Override
    protected Region createNew(String name) {
        return new Region(IdGenerator.getInstance().getId(), name);
    }

}
