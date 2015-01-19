package net.parostroj.timetable.output2.gt;

import java.util.List;

public interface RegionSelector<T> {

    void regionsSelected(List<T> regions);

    List<T> getSelected();

    boolean editSelected();

}
