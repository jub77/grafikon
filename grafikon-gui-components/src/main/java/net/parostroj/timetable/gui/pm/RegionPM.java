package net.parostroj.timetable.gui.pm;

import java.lang.ref.WeakReference;
import java.util.*;

import net.parostroj.timetable.model.Region;

import org.beanfabrics.model.*;
import org.beanfabrics.support.Operation;

/**
 * View model of region.
 *
 * @author jub
 */
public class RegionPM extends AbstractPM {

    final TextPM name = new TextPM();
    final IEnumeratedValuesPM<Locale> locale;

    final OperationPM ok = new OperationPM();

    private WeakReference<Region> regionRef;

    public RegionPM(Collection<Locale> locales) {
        locale = new EnumeratedValuesPM<Locale>(EnumeratedValuesPM.createValueMap(
                locales, l -> l.getDisplayName()), "-");
        name.setEditable(false);
        PMManager.setup(this);
    }

    public void init(Region region) {
        this.regionRef = new WeakReference<Region>(region);
        this.name.setText(region.getName());
        this.locale.setValue(region.getAttribute(Region.ATTR_LOCALE, Locale.class));
    }

    @Operation(path = "ok")
    public boolean ok() {
        Region region = regionRef.get();
        if (region != null) {
            // write back
            region.setAttribute(Region.ATTR_LOCALE, locale.getValue());
        }
        return true;
    }
}
