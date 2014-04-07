package net.parostroj.timetable.gui.components;

import java.util.List;

public abstract class RegionCollector<T> {

    private RegionSelector<T> selector;

    public RegionSelector<T> getSelector() {
        return selector;
    }

    public void setSelector(RegionSelector<T> selector) {
        this.selector = selector;
    }

    protected abstract List<T> getItemsForPoint(int x, int y);

    public void selectItems(int x, int y) {
        if (this.selector != null) {
            this.selector.regionsSelected(this.getItemsForPoint(x, y));
        }
    }
}
