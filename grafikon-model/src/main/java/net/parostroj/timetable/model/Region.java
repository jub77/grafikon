package net.parostroj.timetable.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    // dynamic view on nodes directly in this region
    private final Set<Node> nodes;

    private boolean events;

    Region(String id, TrainDiagram diagram) {
        this.id = id;
        this.diagram = diagram;
        this.attributes = new Attributes((attrs, change) -> {
            Event event = new Event(diagram, Region.this, change);
            fireEvent(event);
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
        this.nodes = new HashSet<>();
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

    public Map<FreightColor, Region> getFreightColorMap() {
        return getAttributes().getAsMap(ATTR_FREIGHT_COLOR_MAP, FreightColor.class, Region.class, Collections.emptyMap());
    }

    public void setFreightColorMap(Map<FreightColor, Region> colorMap) {
        getAttributes().setRemove(ATTR_FREIGHT_COLOR_MAP, ObjectsUtil.checkEmpty(colorMap));
    }

    public Locale getLocale() {
        return attributes.get(ATTR_LOCALE, Locale.class);
    }

    public void setLocale(Locale locale) {
        attributes.setRemove(ATTR_LOCALE, locale);
    }

    public boolean isColorCenter() {
        return attributes.getBool(ATTR_COLOR_CENTER);
    }

    public void setColorCenter(boolean colorCenter) {
        attributes.setBool(ATTR_COLOR_CENTER, colorCenter);
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
        Event event = new Event(diagram, this, new AttributeChange(ATTR_SUB_REGIONS, old, subRegions));
        fireEvent(event);
    }

    private void fireNodesEvent(Set<Node> old) {
        Event event = new Event(diagram, this, new AttributeChange(ATTR_NODES, old, nodes));
        fireEvent(event);
    }

    private void fireEvent(Event event) {
        if (events) {
            diagram.fireEvent(event);
        }
    }

    public Set<Region> getSubRegions() {
        return subRegions;
    }

    public boolean isSuperRegion() {
        return !subRegions.isEmpty();
    }

    void addNode(Node node) {
        Set<Node> old = ImmutableSet.copyOf(nodes);
        nodes.add(node);
        fireNodesEvent(old);
    }

    void removeNode(Node node) {
        Set<Node> old = ImmutableSet.copyOf(nodes);
        nodes.remove(node);
        fireNodesEvent(old);
    }

    public Set<Node> getNodes() {
        return nodes;
    }

    public Set<Node> getAllNodes() {
        return getNodesImpl().collect(Collectors.toSet());
    }

    protected Stream<Node> getNodesImpl() {
        if (isSuperRegion()) {
            return getSubRegions().stream().flatMap(sr -> sr.getNodesImpl());
        } else {
            return getNodes().stream();
        }
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
