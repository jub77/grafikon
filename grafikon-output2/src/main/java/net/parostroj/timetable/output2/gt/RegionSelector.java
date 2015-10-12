package net.parostroj.timetable.output2.gt;

import java.util.List;

public interface RegionSelector<T> {

    boolean regionsSelected(List<T> regions);

    List<T> getSelected();

    boolean editSelected();

}
