import net.parostroj.timetable.gui.dialogs.ElementSelectionDialog

def allTrains = diagram.trains.findAll{train -> !train.attributes['managed.freight'] && train.type?.category?.key == "freight" && !train.cyclesMap.values().any{c -> c.cycle.attributes['freight']}}

if (!allTrains) return

def select = new ElementSelectionDialog(parent, true)
select.setLocationRelativeTo(parent)
def trains = select.selectElements(allTrains)


for (train in trains) {
    train.getAttributes().setBool('managed.freight', true)
}
