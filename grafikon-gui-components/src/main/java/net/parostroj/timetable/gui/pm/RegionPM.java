package net.parostroj.timetable.gui.pm;

import java.lang.ref.WeakReference;
import java.text.Collator;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.beanfabrics.model.AbstractPM;
import org.beanfabrics.model.BooleanPM;
import org.beanfabrics.model.IBooleanPM;
import org.beanfabrics.model.IOperationPM;
import org.beanfabrics.model.ListPM;
import org.beanfabrics.model.OperationPM;
import org.beanfabrics.model.PMManager;
import org.beanfabrics.model.TextPM;
import org.beanfabrics.support.Operation;
import org.beanfabrics.support.Validation;

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
    final IBooleanPM colorRegion;
    final ListPM<ColorMappingPM> colorMap;

    final OperationPM ok = new OperationPM();
    final IOperationPM add = new OperationPM();
    final IOperationPM remove = new OperationPM();

    private WeakReference<Region> regionRef;

    private final Collator collator = Collator.getInstance();

    public RegionPM(Collection<Locale> locales) {
        locale = new EnumeratedValuesPM<>(EnumeratedValuesPM.createValueMap(
                locales, l -> l.getDisplayName(l)), "-");
        name.setMandatory(true);
        superRegion = new EnumeratedValuesPM<>();
        colorRegion = new BooleanPM();
        colorMap = new ListPM<>();
        PMManager.setup(this);
    }

    public void init(Region region, Collection<Region> allRegions) {
        this.regionRef = new WeakReference<>(region);
        this.name.setText(region.getName());
        this.locale.setValue(region.getAttribute(Region.ATTR_LOCALE, Locale.class));
        Comparator<? super Region> comparator = (o1, o2) -> collator.compare(o1.getName(), o2.getName());
        Predicate<? super Region> notThisOne = r -> r != region;
        Collection<Region> regionsForSuper = allRegions.stream()
                .filter(notThisOne)
                .filter(r -> r.getNodes().isEmpty())
                .sorted(comparator)
                .collect(Collectors.toList());
        this.superRegion.addValues(EnumeratedValuesPM.createValueMap(regionsForSuper, Region::getName, "-"));
        this.superRegion.setValue(region.getSuperRegion());
        this.colorRegion.setBoolean(region.isFreightColorRegion());
        // color map
        Map<FreightColor, Region> cMap = region.getFreightColorMap();
        colorMap.clear();
        for (Map.Entry<FreightColor, Region> entry : cMap.entrySet()) {
            ColorMappingPM mapping = new ColorMappingPM();
            mapping.set(entry.getKey(), entry.getValue(), allRegions.stream()
                    .filter(notThisOne)
                    .filter(Region::isFreightColorRegion)
                    .collect(Collectors.toSet()));
            colorMap.add(mapping);
        }
    }

    @Validation(path = { "add" })
    public boolean isColorRegion() {
        return colorRegion.getBoolean();
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
        mapping.set(null, null, Iterables.filter(superRegion.getValues(), Objects::nonNull));
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
            region.setFreightColorRegion(colorRegion.getBoolean());
            // color mapping
            Map<FreightColor, Region> map = new EnumMap<>(FreightColor.class);
            for (ColorMappingPM cMapping : colorMap) {
                FreightColor mColor = cMapping.color.getValue();
                Region mRegion = cMapping.region.getValue();
                if (mColor != null && mRegion != null) {
                    map.put(mColor, mRegion);
                }
            }
            region.setFreightColorMap(map);
        }
        return true;
    }
}
