package net.parostroj.timetable.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import net.parostroj.timetable.model.events.AttributeChange;
import net.parostroj.timetable.model.events.Event;
import net.parostroj.timetable.utils.ObjectsUtil;
import net.parostroj.timetable.visitors.TrainDiagramVisitor;
import net.parostroj.timetable.visitors.Visitable;

/**
 * Region - for station attributes -> freight net support.
 *
 * @author jub
 */
public class Region implements Visitable, ObjectWithId, AttributesHolder, RegionAttributes, TrainDiagramPart, ItemListObject {

    private final TrainDiagram diagram;
    private final String id;
    private final Attributes attributes;

    // only dynamic view on sub regions
    private final Set<Region> subRegions;

    private boolean events;

    Region(String id, TrainDiagram diagram) {
        this.id = id;
        this.diagram = diagram;
        this.attributes = new Attributes((attrs, change) -> {
            if (events) diagram.fireEvent(new Event(diagram, Region.this, change));
        });
        this.attributes.addListener((attrs, change) -> {
            if (change.checkName(ATTR_SUPER_REGION)) {
                Region oldR = (Region) change.getOldValue();
                Region newR = (Region) change.getNewValue();
                if (oldR != null) oldR.removeSubRegion(Region.this);
                if (newR != null) newR.addSubRegion(Region.this);
            }
        });
        this.subRegions = new HashSet<>();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public TrainDiagram getDiagram() {
        return diagram;
    }

    @Override
    public Attributes getAttributes() {
        return attributes;
    }

    public String getName() {
        return attributes.get(ATTR_NAME, String.class);
    }

    public void setName(String name) {
        attributes.set(ATTR_NAME, name);
    }

    public Region getSuperRegion() {
        return getAttributes().get(ATTR_SUPER_REGION, Region.class);
    }

    public void setSuperRegion(Region superRegion) {
        getAttributes().setRemove(ATTR_SUPER_REGION, superRegion);
    }

    public Map<FreightColor, Region> getColorMap() {
        return getAttributes().getAsMap(ATTR_FREIGHT_COLOR_MAP, FreightColor.class, Region.class, Collections.emptyMap());
    }

    public void setColorMap(Map<FreightColor, Region> colorMap) {
        getAttributes().setRemove(ATTR_FREIGHT_COLOR_MAP, ObjectsUtil.checkEmpty(colorMap));
    }

    private void addSubRegion(Region added) {
        Set<Region> old = ImmutableSet.copyOf(subRegions);
        subRegions.add(added);
        fireSubRegionsEvent(old);
    }

    private void removeSubRegion(Region removed) {
        Set<Region> old = ImmutableSet.copyOf(subRegions);
        subRegions.remove(removed);
        fireSubRegionsEvent(old);
    }

    private void fireSubRegionsEvent(Set<Region> old) {
        if (events) diagram.fireEvent(new Event(diagram, this, new AttributeChange(ATTR_SUB_REGIONS, old, subRegions)));
    }

    public Set<Region> getSubRegions() {
        return subRegions;
    }

    public boolean isSuperRegion() {
        return !subRegions.isEmpty();
    }

    @Override
    public void added() {
        events = true;
    }

    @Override
    public void removed() {
        events = false;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public void accept(TrainDiagramVisitor visitor) {
        visitor.visit(this);
    }
}
