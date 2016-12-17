package net.parostroj.timetable.model;

import java.util.Collection;
import java.util.Locale;

import net.parostroj.timetable.actions.TextList;

/**
 * Conversion to string representation.
 *
 * @author jub
 */
class FreightConverter {

    String freightDstListToString(Locale locale, Node from, Collection<FreightDst> list) {
        StringBuilder builder = new StringBuilder();
        new TextList(builder, ",").addItems(list, dst -> dst.toString(locale, from, true, true)).finish();
        return builder.toString();
    }
}
