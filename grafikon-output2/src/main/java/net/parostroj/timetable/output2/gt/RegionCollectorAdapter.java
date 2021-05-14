package net.parostroj.timetable.output2.gt;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

public abstract class RegionCollectorAdapter<T> extends RegionCollector<T> {

    protected Multimap<T, Shape> regions = LinkedListMultimap.create();

    protected RegionCollectorAdapter() {
    }

    @Override
    public List<T> getItemsForPoint(int x, int y, int radius) {
        Rectangle2D cursor = new Rectangle2D.Double(x - radius, y - radius, radius * 2, radius * 2);
        List<T> list = new LinkedList<>();
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
    public Rectangle getRectangleForItems(List<T> items) {
        Rectangle rectangle = null;
        for (T item : items) {
            Collection<Shape> shapes = regions.get(item);
            if (shapes != null) {
                for (Shape shape : shapes) {
                    Rectangle bounds = shape.getBounds();
                    if (rectangle == null) {
                        rectangle = bounds;
                    } else {
                        rectangle = rectangle.union(bounds);
                    }
                }
            }
        }
        return rectangle;
    }

    @Override
    public void clear() {
        regions.clear();
    }

    @Override
    public void addRegion(T region, Shape shape) {
        regions.put(region, shape);
    }
}
