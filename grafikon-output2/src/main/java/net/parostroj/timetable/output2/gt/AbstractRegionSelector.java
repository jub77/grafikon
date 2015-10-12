package net.parostroj.timetable.output2.gt;

import java.util.List;

public abstract class AbstractRegionSelector<T> implements RegionSelector<T> {

    private List<T> selected;

    @Override
    public List<T> getSelected() {
        return selected;
    }

    @Override
    public boolean regionsSelected(List<T> regions) {
        this.selected = regions;
        return !regions.isEmpty();
    }

    @Override
    public boolean editSelected() {
        return false;
    }
}
