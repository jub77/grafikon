package net.parostroj.timetable.model;

@FunctionalInterface
public interface ObjectReference<T extends ObjectWithId> {

    String getRefId();
    default T getObject(ObjectMapping<T> mapping) {
        return mapping.getObject(getRefId());
    }

    static <O extends ObjectWithId> ObjectReference<O> create(String id) {
        return () -> id;
    }

    static <O extends ObjectWithId> ObjectReference<O> create(ObjectWithId object) {
        return create(object.getId());
    }
}
