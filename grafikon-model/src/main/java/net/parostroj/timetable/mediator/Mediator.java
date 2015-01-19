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

    private final Map<Colleague, Class<?>> colleagues = new HashMap<Colleague, Class<?>>();

    private final SetMultimap<Class<?>, Colleague> colleaguesForClass = HashMultimap.create();

    public void addColleague(Colleague collegue) {
        this.addColleague(collegue, Object.class);
    }

    public void addColleague(Colleague colleague, Class<?> clazz) {
        if (colleagues.keySet().contains(colleague))
            throw new IllegalStateException("Mediator already contains colleague.");
        if (colleague instanceof ColleagueWithBackReference)
            ((ColleagueWithBackReference)colleague).setMediator(this);
        colleagues.put(colleague, clazz);
        this.colleaguesForClass.put(clazz, colleague);
    }

    public void removeColleague(Colleague collegue) {
        if (!colleagues.keySet().contains(collegue))
            throw new IllegalStateException("Mediator doesn't contain colleague.");
        if (collegue instanceof ColleagueWithBackReference)
            ((ColleagueWithBackReference)collegue).setMediator(null);
        Class<?> clazz = colleagues.remove(collegue);
        this.colleaguesForClass.remove(clazz, collegue);
    }

    public Set<Colleague> getColleagues() {
        return Collections.unmodifiableSet(colleagues.keySet());
    }

    public Class<?> getClassForColleague(Colleague colleague) {
        return colleagues.get(colleague);
    }

    public Set<Colleague> getColleaguesForClass(Class<?> clazz) {
        if (colleaguesForClass.containsKey(clazz)) {
            return Collections.unmodifiableSet(colleaguesForClass.get(clazz));
        } else {
            return Collections.emptySet();
        }
    }

    public void sendMessage(Object message) {
        // skip null values
        if (message == null)
            return;
        // distribute per class
        for (Class<?> cls : colleaguesForClass.keySet()) {
            if (cls.equals(Object.class) || cls.isAssignableFrom(message.getClass())) {
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
