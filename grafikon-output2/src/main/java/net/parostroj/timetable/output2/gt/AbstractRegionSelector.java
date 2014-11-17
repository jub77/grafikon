package net.parostroj.timetable.output2.gt;

import java.util.List;

public abstract class AbstractRegionSelector<T> implements RegionSelector<T> {

    @Override
    public void regionsSelected(List<T> regions) {
    }

    @Override
    public boolean editSelected() {
        return false;
    }
}
