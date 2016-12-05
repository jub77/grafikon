package net.parostroj.timetable.gui.pm;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Locale;
import java.util.stream.Collectors;

import org.beanfabrics.model.*;
import org.beanfabrics.support.Operation;
import org.beanfabrics.support.Validation;

import net.parostroj.timetable.model.Region;
import net.parostroj.timetable.utils.ObjectsUtil;

/**
 * View model of region.
 *
 * @author jub
 */
public class RegionPM extends AbstractPM {

    final TextPM name = new TextPM();
    final IEnumeratedValuesPM<Locale> locale;
    IEnumeratedValuesPM<Region> superRegion;

    final OperationPM ok = new OperationPM();

    private WeakReference<Region> regionRef;

    public RegionPM(Collection<Locale> locales) {
        locale = new EnumeratedValuesPM<>(EnumeratedValuesPM.createValueMap(
                locales, l -> l.getDisplayName(l)), "-");
        name.setMandatory(true);
        superRegion = new EnumeratedValuesPM<>();
        PMManager.setup(this);
    }

    public void init(Region region, Collection<Region> allRegions) {
        this.regionRef = new WeakReference<>(region);
        this.name.setText(region.getName());
        this.locale.setValue(region.getAttribute(Region.ATTR_LOCALE, Locale.class));
        Collection<Region> regions = allRegions.stream().filter(r -> r != region).sorted().collect(Collectors.toList());
        this.superRegion.addValues(EnumeratedValuesPM.createValueMap(regions, item -> item.getName(), "-"));
        this.superRegion.setValue(region.getSuperRegion());
    }

    @Validation(path = { "ok" })
    public boolean isNameValid() {
        return !ObjectsUtil.isEmpty(name.getText());
    }

    @Operation(path = "ok")
    public boolean ok() {
        Region region = regionRef.get();
        if (region != null) {
            // write back
            region.setAttribute(Region.ATTR_LOCALE, locale.getValue());
            region.setName(ObjectsUtil.checkAndTrim(name.getText()));
            region.setSuperRegion(this.superRegion.getValue());
        }
        return true;
    }
}
