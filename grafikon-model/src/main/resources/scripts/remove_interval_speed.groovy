for (train in diagram.trains) {
    for (i in train.timeIntervalList) {
        if (i.lineOwner) {
            i.speedLimit = null
        }
    }
    train.recalculate()
}
