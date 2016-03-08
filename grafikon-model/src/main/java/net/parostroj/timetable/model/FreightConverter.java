package net.parostroj.timetable.model;

import java.util.Collection;

import net.parostroj.timetable.actions.TextList;

/**
 * Conversion to string representation.
 *
 * @author jub
 */
class FreightConverter {

    String freightDstListToString(Collection<FreightDst> list) {
        StringBuilder builder = new StringBuilder();
        TextList output = new TextList(builder, ",");
        output.addItems(list);
        output.finish();
        return builder.toString();
    }
}
