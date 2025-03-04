package net.parostroj.timetable.mediator;

import java.util.*;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

/**
 * Mediator for messages.
 *
 * @author jub
 */
public class Mediator {

    private final Map<Colleague, List<Class<?>>> colleagues = new HashMap<>();

    private final SetMultimap<Class<?>, Colleague> colleaguesForClass = HashMultimap.create();

    public void addColleague(Colleague collegue) {
        this.addColleague(collegue, Object.class);
    }

    public void addColleague(Colleague colleague, Class<?>... clazz) {
        if (colleagues.containsKey(colleague)) {
            throw new IllegalStateException("Mediator already contains colleague.");
        }
        List<Class<?>> classes = List.of(clazz);
        colleagues.put(colleague, classes);
        classes.forEach(cls -> this.colleaguesForClass.put(cls, colleague));
    }

    public void removeColleague(Colleague collegue) {
        if (!colleagues.containsKey(collegue)) {
            throw new IllegalStateException("Mediator doesn't contain colleague.");
        }
        List<Class<?>> classes = colleagues.remove(collegue);
        classes.forEach(cls -> this.colleaguesForClass.remove(cls, collegue));
    }

    public void sendMessage(Object message) {
        // skip null values
        if (message == null)
            return;
        // distribute per class
        for (Class<?> cls : colleaguesForClass.keySet()) {
            if (cls.isAssignableFrom(message.getClass())) {
                distributeMessage(message, colleaguesForClass.get(cls));
            }
        }
    }

    private void distributeMessage(Object message, Set<Colleague> setOfColleagues) {
        for (Colleague colleague : setOfColleagues) {
            colleague.receiveMessage(message);
        }
    }
}
