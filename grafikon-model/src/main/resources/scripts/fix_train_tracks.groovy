import net.parostroj.timetable.model.computation.TrainRouteSelection
import net.parostroj.timetable.model.computation.TrainRouteTracksComputation

TrainRouteSelection trc = new TrainRouteSelection()
TrainRouteTracksComputation trtc = new TrainRouteTracksComputation()
def intervals = []
for (train in diagram.trains) {
    def invalidInterval = trc.getFirstTrainInvalidTrack(train)
    invalidInterval.ifPresent {interval ->
        def availableTracks = trtc.getAvailableTracksForTrain(interval.train)
        if (!availableTracks) {
            diagram.trains.remove(interval.train)
        } else {
            def iTracks = availableTracks[interval]
            if (interval.isNodeOwner()) {
                interval.train.changeNodeTrack(interval, iTracks.first())
            } else {
                interval.train.changeLineTrack(interval, iTracks.first())
            }
        }
    }
}
