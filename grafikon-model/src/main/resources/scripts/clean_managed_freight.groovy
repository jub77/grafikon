for (train in diagram.trains) {
    for (interval in train.nodeIntervals) {
        interval.removeAttribute("managed.freight.override")
    }
    train.removeAttribute("managed.freight.override")
}
