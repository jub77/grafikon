package net.parostroj.timetable.gui.pm;

import java.util.Arrays;

import org.beanfabrics.model.AbstractPM;
import org.beanfabrics.model.PMManager;

import net.parostroj.timetable.model.FreightColor;
import net.parostroj.timetable.model.Region;

/**
 * @author jub
 */
public class ColorMappingPM extends AbstractPM {

    final IEnumeratedValuesPM<FreightColor> color;
    final IEnumeratedValuesPM<Region> region;

    public ColorMappingPM() {
        color = new EnumeratedValuesPM<>(
                EnumeratedValuesPM.createValueMap(Arrays.asList(FreightColor.values()), FreightColor::getName), "-");
        region = new EnumeratedValuesPM<>();
        PMManager.setup(this);
    }

    public void init(FreightColor color, Region region, Iterable<Region> allRegions) {
        this.set(color, region, allRegions);
    }

    public void set(FreightColor color, Region region, Iterable<Region> allRegions) {
        this.color.setValue(color);
        // fill in regions and selected region
        this.region.removeAllValues();
        this.region.addValues(EnumeratedValuesPM.createValueMap(allRegions, Region::getName, "-"));
        this.region.setValue(region);
    }
}
