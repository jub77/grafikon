package net.parostroj.timetable.gui.wrappers;

import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.model.ManagedFreight;

/**
 * ManagedFreight wrapper delegate.
 *
 * @author jub
 */
public class ManagedFreightWrapperDelegate extends BasicWrapperDelegate<ManagedFreight> {

    @Override
    public String toString(ManagedFreight element) {
        return ResourceLoader.getString("managed.freight." + element.getKey());
    }
}
