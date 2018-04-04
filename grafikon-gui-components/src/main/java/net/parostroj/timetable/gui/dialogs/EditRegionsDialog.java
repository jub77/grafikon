package net.parostroj.timetable.gui.dialogs;

import java.awt.Window;
import java.util.Collection;
import java.util.Locale;

import net.parostroj.timetable.gui.wrappers.BasicWrapperDelegate;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.model.Region;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainDiagramPartFactory;
import net.parostroj.timetable.utils.ObjectsUtil;

public class EditRegionsDialog extends EditItemsDialog<Region, TrainDiagram> {

    private static final long serialVersionUID = 1L;

	private Collection<Locale> locales;

    public EditRegionsDialog(Window parent, boolean modal, boolean move, boolean edit, boolean newByName, boolean copy,
            boolean multiple) {
        super(parent, modal, move, edit, newByName, copy, multiple);
    }

    public static EditRegionsDialog newInstance(Window parent, boolean modal, Collection<Locale> locales) {
        EditRegionsDialog dialog = newBuilder(EditRegionsDialog.class).setEdit(true).setNewByName(true).build(parent, modal);
        dialog.setLocales(locales);
        return dialog;
    }

    private void setLocales(Collection<Locale> locales) {
        this.locales = locales;
    }

    @Override
    protected Collection<Region> getList() {
        return element.getNet().getRegions();
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
    protected Wrapper<Region> createWrapper(Region item) {
        return Wrapper.getWrapper(item, new BasicWrapperDelegate<Region>() {
            @Override
            public String toString(Region element) {
                return element.getSuperRegion() == null ? element.getName()
                        : String.format("%s [%s]", element.getName(), element.getSuperRegion().getName());
            }
        });
    }

    @Override
    protected void edit(Region region) {
        EditRegionDialog dialog = new EditRegionDialog(this, true, locales);
        dialog.setLocationRelativeTo(this);
        String regionName = region.getName();
        dialog.showDialog(region, element);
        dialog.dispose();
        if (!ObjectsUtil.compareWithNull(regionName, region.getName())) {
            this.refreshAll();
        }
    }
}
