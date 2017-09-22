package net.parostroj.timetable.output2.gt;

import java.awt.Rectangle;
import java.awt.Shape;
import java.util.Collections;
import java.util.List;

import net.parostroj.timetable.model.events.Event;

public abstract class RegionCollector<T> {

    private RegionSelector<T> selector;

    public RegionSelector<T> getSelector() {
        return selector;
    }

    public void setSelector(RegionSelector<T> selector) {
        this.selector = selector;
    }

    public abstract List<T> getItemsForPoint(int x, int y, int radius);

    public List<T> getItemsForPointRadiuses(int x, int y, int... radiuses) {
        List<T> selection = null;
        for (int radius : radiuses) {
            selection = getItemsForPoint(x, y, radius);
            if (!selection.isEmpty()) {
                break;
            }
        }
        return selection;
    }

    public abstract Rectangle getRectangleForItems(List<T> items);

    public void deselectItems() {
        if (this.selector != null) {
            this.selectImpl(Collections.<T>emptyList());
        }
    }

    public boolean selectItemsRadiuses(int x, int y, int... radiuses) {
        if (this.selector != null) {
            List<T> list = this.getItemsForPointRadiuses(x, y, radiuses);
            return this.selectImpl(list);
        } else {
            return false;
        }
    }

    public boolean selectItems(int x, int y, int radius) {
        return this.selectItemsRadiuses(x, y, radius);
    }

    public boolean selectItems(List<T> items) {
        boolean selected = false;
        if (this.selector != null) {
            selected = selectImpl(items);
        }
        return selected;
    }

    private boolean selectImpl(List<T> list) {
        return this.selector.regionsSelected(list);
    }

    public boolean editSelected() {
        if (this.selector != null) {
            return this.selector.editSelected();
        } else {
            return false;
        }
    }

    public abstract void clear();

    public abstract void addRegion(T region, Shape shape);

    public abstract void processEvent(Event event);
}
