package net.parostroj.timetable.mediator;

import java.util.*;

/**
 * Mediator for messages.
 *
 * @author jub
 */
public class Mediator {

    private Map<Colleague, Class<?>> colleagues = new HashMap<Colleague, Class<?>>();

    private Map<Class<?>, Set<Colleague>> colleaguesForClass = new HashMap<Class<?>, Set<Colleague>>();

    public void addColleague(Colleague collegue) {
        this.addColleague(collegue, Object.class);
    }

    public void addColleague(Colleague colleague, Class<?> clazz) {
        if (colleagues.keySet().contains(colleague))
            throw new IllegalStateException("Mediator already contains colleague.");
        if (colleague instanceof ColleagueWithBackReference)
            ((ColleagueWithBackReference)colleague).setMediator(this);
        colleagues.put(colleague, clazz);
        this.getSetForClass(clazz).add(colleague);
    }

    public void removeColleague(Colleague collegue) {
        if (!colleagues.keySet().contains(collegue))
            throw new IllegalStateException("Mediator doesn't contain colleague.");
        if (collegue instanceof ColleagueWithBackReference)
            ((ColleagueWithBackReference)collegue).setMediator(null);
        Class<?> clazz = colleagues.remove(collegue);
        this.getSetForClass(clazz).remove(collegue);
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

    private Set<Colleague> getSetForClass(Class<?> clazz) {
        if (!colleaguesForClass.containsKey(clazz)) {
            Set<Colleague> set = new HashSet<Colleague>();
            colleaguesForClass.put(clazz, set);
            return set;
        } else {
            return colleaguesForClass.get(clazz);
        }
    }

    public void sendMessage(Object message) {
        // skip null values
        if (message == null)
            return;
        // distribute per class
        for (Map.Entry<Class<?>, Set<Colleague>> entry : colleaguesForClass.entrySet()) {
            if (entry.getKey().equals(Object.class) || entry.getKey().isAssignableFrom(message.getClass())) {
                distributeMessage(message, entry.getValue());
            }
        }
    }

    private void distributeMessage(Object message, Set<Colleague> setOfColleagues) {
        for (Colleague colleague : setOfColleagues) {
            colleague.receiveMessage(message);
        }
    }
}
