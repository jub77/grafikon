import javax.swing.*

def numbers = [:]
def duplicate = [:]

for (train in diagram.trains) {
    if (numbers.containsKey(train.number)) {
        if (duplicate.containsKey(train.number)) {
            duplicate[train.number] << train
        } else {
            duplicate[train.number] = [numbers[train.number], train]
        }
    } else {
        numbers[train.number] = train
    }
}

if (!duplicate.isEmpty()) {
    def area = new JTextArea()
    area.text = duplicate.values().collect{i -> i.join(", ")}.join('\n')
    area.columns = 35
    area.rows = 15
    area.setFont(new java.awt.Font("Monospaced", 0, 12))
    def pane = new JScrollPane(area)
    JOptionPane.showMessageDialog(null, pane)
}
