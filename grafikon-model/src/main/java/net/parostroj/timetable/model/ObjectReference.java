package net.parostroj.timetable.model;

public interface ObjectReference<T extends ObjectWithId> {

    String getRefId();
    T getObject(ObjectMapping<T> mapping);

    static <O extends ObjectWithId> ObjectReference<O> create(String id) {
        return new ObjectReference<>() {
            @Override
            public String getRefId() {
                return id;
            }

            @Override
            public O getObject(ObjectMapping<O> mapping) {
                return mapping.getObject(id);
            }
        };
    }

    static <O extends ObjectWithId> ObjectReference<O> create(ObjectWithId object) {
        return create(object.getId());
    }
}
