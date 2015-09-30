package net.parostroj.timetable.gui.pm;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Locale;

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
public class RegionPM extends AbstractPM implements IPM<Region> {

    final TextPM name = new TextPM();
    final IEnumeratedValuesPM<Locale> locale;

    final OperationPM ok = new OperationPM();

    private WeakReference<Region> regionRef;

    public RegionPM(Collection<Locale> locales) {
        locale = new EnumeratedValuesPM<Locale>(EnumeratedValuesPM.createValueMap(
                locales, l -> l.getDisplayName(l)), "-");
        name.setMandatory(true);
        PMManager.setup(this);
    }

    public void init(Region region) {
        this.regionRef = new WeakReference<Region>(region);
        this.name.setText(region.getName());
        this.locale.setValue(region.getAttribute(Region.ATTR_LOCALE, Locale.class));
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
        }
        return true;
    }
}
