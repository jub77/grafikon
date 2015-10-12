package net.parostroj.timetable.output2.gt;

import java.awt.Rectangle;
import java.awt.Shape;
import java.util.Collections;
import java.util.List;

import net.parostroj.timetable.model.events.GTEvent;

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

    public boolean selectItems(int x, int y, int radius) {
        if (this.selector != null) {
            List<T> list = this.getItemsForPoint(x, y, radius);
            this.selectImpl(list);
            return !list.isEmpty();
        } else {
            return false;
        }
    }

    public void selectItems(List<T> items) {
        if (this.selector != null) {
            selectImpl(items);
        }
    }

    private void selectImpl(List<T> list) {
        this.selector.regionsSelected(list);
    }

    public boolean editSelected() {
        if (this.selector != null) {
            return this.selector.editSelected();
        } else {
            return false;
        }
    }

    abstract public void clear();

    abstract public void addRegion(T region, Shape shape);

    abstract public void processEvent(GTEvent<?> event);
}
