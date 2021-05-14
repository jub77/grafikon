package net.parostroj.timetable.output2.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;

import net.parostroj.timetable.model.freight.FreightConnection;
import net.parostroj.timetable.utils.Pair;

/**
 * @author jub
 */
public class FreightCollectors {

    private FreightCollectors() {}

    public static Collector<Pair<Boolean, ? extends FreightConnection>, ?, List<Pair<Boolean, List<FreightConnection>>>> freightDirectionCollector() {
        return Collector.of(FreightCollectors::supplier, FreightCollectors::accumulator,
                FreightCollectors::combine);
    }

    static List<Pair<Boolean, List<FreightConnection>>> supplier() {
        return new ArrayList<>();
    }

    static void accumulator(List<Pair<Boolean, List<FreightConnection>>> l,
            Pair<Boolean, ? extends FreightConnection> i) {
        Pair<Boolean, List<FreightConnection>> lp;
        if (l.isEmpty() || !((lp = l.get(l.size() - 1)).first == i.first)) {
            lp = new Pair<>(i.first, new ArrayList<>());
            l.add(lp);
        }
        lp.second.add(i.second);
    }

    static List<Pair<Boolean, List<FreightConnection>>> combine(
            List<Pair<Boolean, List<FreightConnection>>> l1,
            List<Pair<Boolean, List<FreightConnection>>> l2) {
        Pair<Boolean, List<FreightConnection>> lp;
        Pair<Boolean, List<FreightConnection>> fp;
        if (!l1.isEmpty() && !l2.isEmpty()
                && (lp = l1.get(l1.size() - 1)).first == (fp = l2.get(0)).first) {
            lp.second.addAll(fp.second);
            if (l2.size() > 1) {
                l1.addAll(l2.subList(1, l2.size() - 1));
            }
        } else {
            l1.addAll(l2);
        }
        return l1;
    }
}
