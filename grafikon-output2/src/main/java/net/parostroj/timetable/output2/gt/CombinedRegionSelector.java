package net.parostroj.timetable.output2.gt;

import java.util.ArrayList;
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
    public List<T> getSelected() {
        List<T> result = new ArrayList<T>();
        for (RegionSelector<T> selector : selectors) {
            List<T> selected = selector.getSelected();
            if (selected != null) {
                result.addAll(selected);
            }
        }
        return result;
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
