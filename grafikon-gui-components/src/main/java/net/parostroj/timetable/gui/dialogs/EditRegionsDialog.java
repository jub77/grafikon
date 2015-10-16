package net.parostroj.timetable.gui.dialogs;

import java.awt.Frame;
import java.util.Collection;
import java.util.Locale;

import net.parostroj.timetable.model.Region;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainDiagramPartFactory;
import net.parostroj.timetable.utils.ObjectsUtil;

public class EditRegionsDialog extends EditItemsDialog<Region, TrainDiagram> {

    private final Collection<Locale> locales;

    public EditRegionsDialog(Frame parent, boolean modal, Collection<Locale> locales) {
        super(parent, modal, false, true, true);
        this.locales = locales;
    }

    @Override
    protected Collection<Region> getList() {
        return element.getNet().getRegions().toCollection();
    }

    @Override
    protected void add(Region item, int index) {
        element.getNet().getRegions().add(item);
    }

    @Override
    protected void remove(Region item) {
        element.getNet().getRegions().remove(item);
    }

    @Override
    protected void move(Region item, int oldIndex, int newIndex) {
        throw new IllegalStateException();
    }

    @Override
    protected boolean deleteAllowed(Region item) {
        return true;
    }

    @Override
    protected Region createNew(String name) {
        TrainDiagramPartFactory factory = element.getPartFactory();
        Region newRegion = factory.createRegion(factory.createId());
        newRegion.setName(name);
        return newRegion;
    }

    @Override
    protected void edit(Region region) {
        EditRegionDialog dialog = new EditRegionDialog(this, true, locales);
        dialog.setLocationRelativeTo(this);
        String regionName = region.getName();
        dialog.showDialog(region);
        dialog.dispose();
        if (!ObjectsUtil.compareWithNull(regionName, region.getName())) {
            this.refreshAll();
        }
    }
}
