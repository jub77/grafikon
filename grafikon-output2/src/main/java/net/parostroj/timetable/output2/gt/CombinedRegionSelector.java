package net.parostroj.timetable.output2.gt;

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
    public boolean editSelected() {
        boolean edited = false;
        for (RegionSelector<T> selector : selectors) {
            edited = selector.editSelected();
            if (edited) {
                break;
            }
        }
        return edited;
    }
}
