package net.parostroj.timetable.model.ls.impl4;

import net.parostroj.timetable.model.*;

import java.util.function.Function;

public interface LSContext {

    ObjectWithId mapId(String id);

    PartFactory getPartFactory();

    default TrainDiagram getDiagram() {
        return null;
    }

    default Permissions getPermissions() {
        return getPartFactory().getPermissions();
    }

    default LSContext overrideMapping(Function<String, ObjectWithId> mapping) {
        return new LSContext() {
            @Override
            public ObjectWithId mapId(String id) {
                ObjectWithId object = mapping.apply(id);
                return object != null ? object : LSContext.this.mapId(id);
            }

            @Override
            public PartFactory getPartFactory() {
                return LSContext.this.getPartFactory();
            }
        };
    }
}
