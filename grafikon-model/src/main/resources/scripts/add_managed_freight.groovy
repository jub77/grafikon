for (train in diagram.trains) {
    if (train.type?.category?.key == "freight" && !train.cyclesMap.values().any{c -> c.cycle.attributes['freight']}) {
        train.getAttributes().setBool(net.parostroj.timetable.model.Train.ATTR_MANAGED_FREIGHT, true)
    }
}
