package net.parostroj.timetable.gui.components;

import java.awt.Shape;
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

    public void selectItems(int x, int y, int radius) {
        if (this.selector != null) {
            this.selector.regionsSelected(this.getItemsForPoint(x, y, radius));
        }
    }

    public boolean editSelected() {
        boolean edited = false;
        if (this.selector != null) {
            edited = this.selector.editSelected();
        }
        return edited;
    }

    abstract public void clear();

    abstract public void addRegion(T region, Shape shape);
}
