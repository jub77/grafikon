package net.parostroj.timetable.gui.views;

import javax.swing.JTextArea;

import net.parostroj.timetable.gui.views.NetSelectionModel.Action;
import net.parostroj.timetable.gui.views.NetSelectionModel.NetSelectionListener;
import net.parostroj.timetable.model.Line;
import net.parostroj.timetable.model.Node;

public class NetItemInfo implements NetSelectionListener {

    private JTextArea text;

    public NetItemInfo(JTextArea text) {
        this.text = text;
    }

    @Override
    public void selection(Action action, Node node, Line line) {
        switch (action) {
            case LINE_SELECTED:
                text.setText(Integer.toString(line.getLength()));
                break;
            case NODE_SELECTED:
                text.setText(node.getName());
                break;
            case NOTHING_SELECTED:
                text.setText(null);
                break;
        }
    }
}
