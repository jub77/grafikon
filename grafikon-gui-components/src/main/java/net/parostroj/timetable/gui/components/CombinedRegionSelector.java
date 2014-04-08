package net.parostroj.timetable.gui.components;

import java.util.Arrays;
import java.util.List;

public class CombinedRegionSelector<T> implements RegionSelector<T> {

    private final List<RegionSelector<T>> selectors;

    public CombinedRegionSelector(RegionSelector<T>... selectors) {
        this.selectors = Arrays.asList(selectors);
    }

    @Override
    public void regionsSelected(List<T> regions) {
        for (RegionSelector<T> selector : selectors) {
            selector.regionsSelected(regions);
        }
    }

    @Override
    public void editSelected() {
        for (RegionSelector<T> selector : selectors) {
            selector.editSelected();
        }
    }
}
