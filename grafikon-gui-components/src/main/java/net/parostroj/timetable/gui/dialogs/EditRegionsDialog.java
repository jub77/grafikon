package net.parostroj.timetable.gui.dialogs;

import java.awt.Frame;
import java.util.Collection;
import java.util.Locale;

import net.parostroj.timetable.model.Region;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.utils.IdGenerator;

public class EditRegionsDialog extends EditItemsDialog<Region, TrainDiagram> {

    private final Collection<Locale> locales;

    public EditRegionsDialog(Frame parent, boolean modal, Collection<Locale> locales) {
        super(parent, modal, false, true, true);
        this.locales = locales;
    }

    @Override
    protected Collection<Region> getList() {
        return element.getNet().getRegions().get();
    }

    @Override
    protected void add(Region item, int index) {
        element.getNet().getRegions().add(item, index);
    }

    @Override
    protected void remove(Region item) {
        element.getNet().getRegions().remove(item);
    }

    @Override
    protected void move(Region item, int oldIndex, int newIndex) {
        element.getNet().getRegions().move(oldIndex, newIndex);
    }

    @Override
    protected boolean deleteAllowed(Region item) {
        return true;
    }

    @Override
    protected Region createNew(String name) {
        return element.createRegion(IdGenerator.getInstance().getId(), name);
    }

    @Override
    protected void edit(Region region) {
        EditRegionDialog dialog = new EditRegionDialog(this, true, locales);
        dialog.setLocationRelativeTo(this);
        dialog.showDialog(region);
        dialog.dispose();
    }
}
