import net.parostroj.timetable.model.computation.TrainRouteSelection
import javax.swing.JOptionPane
import javax.swing.JScrollPane
import javax.swing.JTextArea

TrainRouteSelection trc = new TrainRouteSelection()
def allValid = true
def texts = []
for (train in diagram.trains) {
    def valid = trc.isTrainTracksValid(train)
    if (!valid) {
        texts << "${train.name.translate()} - invalid"
    }
    allValid = allValid && valid
}
if (texts) {
    def area = new JTextArea()
    area.text = texts.join('\n')
    area.columns = 45
    area.rows = 15
    area.setFont(new java.awt.Font("Monospaced", 0, 12))
    def pane = new JScrollPane(area)
    JOptionPane.showMessageDialog(parent, pane)
}
