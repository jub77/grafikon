package net.parostroj.timetable.output2.gt;

import java.awt.Shape;
import java.util.Collections;
import java.util.List;

public abstract class RegionCollector<T> {

    private RegionSelector<T> selector;

    public RegionSelector<T> getSelector() {
        return selector;
    }

    public void setSelector(RegionSelector<T> selector) {
        this.selector = selector;
    }

    protected abstract List<T> getItemsForPoint(int x, int y, int radius);

    public void deselectItems() {
        if (this.selector != null) {
            this.selector.regionsSelected(Collections.<T>emptyList());
        }
    }

    public boolean selectItems(int x, int y, int radius) {
        if (this.selector != null) {
            List<T> list = this.getItemsForPoint(x, y, radius);
            this.selector.regionsSelected(list);
            return !list.isEmpty();
        } else {
            return false;
        }
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
}
