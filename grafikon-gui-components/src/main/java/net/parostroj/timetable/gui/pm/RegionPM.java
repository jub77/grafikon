package net.parostroj.timetable.gui.pm;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.beanfabrics.model.*;
import org.beanfabrics.support.Operation;
import org.beanfabrics.support.Validation;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

import net.parostroj.timetable.model.FreightColor;
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
    final IEnumeratedValuesPM<Region> superRegion;
    final ListPM<ColorMappingPM> colorMap;

    final OperationPM ok = new OperationPM();
    final IOperationPM add = new OperationPM();
    final IOperationPM remove = new OperationPM();

    private WeakReference<Region> regionRef;

    public RegionPM(Collection<Locale> locales) {
        locale = new EnumeratedValuesPM<>(EnumeratedValuesPM.createValueMap(
                locales, l -> l.getDisplayName(l)), "-");
        name.setMandatory(true);
        superRegion = new EnumeratedValuesPM<>();
        colorMap = new ListPM<>();
        PMManager.setup(this);
    }

    public void init(Region region, Collection<Region> allRegions) {
        this.regionRef = new WeakReference<>(region);
        this.name.setText(region.getName());
        this.locale.setValue(region.getAttribute(Region.ATTR_LOCALE, Locale.class));
        Collection<Region> regions = allRegions.stream().filter(r -> r != region).sorted().collect(Collectors.toList());
        this.superRegion.addValues(EnumeratedValuesPM.createValueMap(regions, item -> item.getName(), "-"));
        this.superRegion.setValue(region.getSuperRegion());
        // color map
        Map<FreightColor, Region> cMap = region.getColorMap();
        colorMap.clear();
        for (Map.Entry<FreightColor, Region> entry : cMap.entrySet()) {
            ColorMappingPM mapping = new ColorMappingPM();
            mapping.set(entry.getKey(), entry.getValue(), allRegions);
            colorMap.add(mapping);
        }
    }

    @Validation(path = { "remove" })
    public boolean isSelectedMapping() {
        return !colorMap.getSelection().isEmpty();
    }

    @Validation(path = { "ok" })
    public boolean isNameValid() {
        return !ObjectsUtil.isEmpty(name.getText());
    }

    @Operation(path = "add")
    public boolean add() {
        ColorMappingPM mapping = new ColorMappingPM();
        mapping.set(null, null, Iterables.filter(superRegion.getValues(), Predicates.notNull()));
        colorMap.add(mapping);
        return true;
    }

    @Operation(path = "remove")
    public boolean remove() {
        for (ColorMappingPM mapping : colorMap.getSelection()) {
            colorMap.remove(mapping);
        }
        return true;
    }

    @Operation(path = "ok")
    public boolean ok() {
        Region region = regionRef.get();
        if (region != null) {
            // write back
            region.setAttribute(Region.ATTR_LOCALE, locale.getValue());
            region.setName(ObjectsUtil.checkAndTrim(name.getText()));
            region.setSuperRegion(this.superRegion.getValue());
            // color mapping
            HashMap<FreightColor, Region> map = new HashMap<>();
            for (ColorMappingPM cMapping : colorMap) {
                FreightColor mColor = cMapping.color.getValue();
                Region mRegion = cMapping.region.getValue();
                if (mColor != null && mRegion != null) {
                    map.put(mColor, mRegion);
                }
            }
            region.setColorMap(map);
        }
        return true;
    }
}
