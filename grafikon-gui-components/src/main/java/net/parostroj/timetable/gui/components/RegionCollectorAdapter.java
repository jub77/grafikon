package net.parostroj.timetable.gui.components;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.*;

public class RegionCollectorAdapter<T> extends RegionCollector<T> {

    protected Map<T, List<Shape>> regions = new HashMap<T, List<Shape>>();;
    protected int radius;

    public RegionCollectorAdapter(int radius) {
        this.radius = radius;
    }

    @Override
    protected List<T> getItemsForPoint(int x, int y) {
        Rectangle2D cursor = new Rectangle2D.Double(x - radius, y - radius, radius * 2, radius * 2);
        List<T> list = new LinkedList<T>();
        for (T region : regions.keySet()) {
            for (Shape shape : regions.get(region)) {
                if (shape.intersects(cursor)) {
                    list.add(region);
                    break;
                }
            }
        }
        return list;
    }

    @Override
    public void clear() {
        regions.clear();
    }

    @Override
    public void addRegion(T region, Shape shape) {
        if (!regions.containsKey(region)) {
            regions.put(region, new LinkedList<Shape>());
        }
        regions.get(region).add(shape);
    }
}
