package net.parostroj.timetable.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import net.parostroj.timetable.gui.components.ElementSelectionPanel;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.gui.wrappers.WrapperListModel;
import net.parostroj.timetable.gui.wrappers.WrapperListModel.ObjectListener;
import net.parostroj.timetable.model.TrainsCycle;
import net.parostroj.timetable.model.TrainsCycleType;

public class CirculationSequenceDialog extends JDialog {

    private final ElementSelectionPanel<TrainsCycle> elementSelectionPanel;
    private TrainsCycle circulation;

    public CirculationSequenceDialog(Window window, ModalityType modality) {
        super(window, modality);
        getContentPane().setLayout(new BorderLayout());
        JPanel contentPanel = new JPanel();
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new BorderLayout(0, 0));

        elementSelectionPanel = new ElementSelectionPanel<TrainsCycle>(true, false);
        contentPanel.add(elementSelectionPanel);

        elementSelectionPanel.getSelected().setObjectListener(new ObjectListener<TrainsCycle>() {
            @Override
            public void added(TrainsCycle added, int index) {
                WrapperListModel<TrainsCycle> list = elementSelectionPanel.getSelected();
                if (list.getSize() > 1) {
                    // get last (previously)
                    TrainsCycle element = list.getElementAt(list.getSize() - 2).getElement();
                    element.connectToSequenceAsNext(added);
                }
            }

            @Override
            public void removed(TrainsCycle removed) {
                removed.removeFromSequence();
            }

            @Override
            public void moved(TrainsCycle moved, int fromIndex, int toIndex) {
                if (fromIndex < toIndex) {
                    moved.moveForwardInSequence();
                } else {
                    moved.moveBackwardInSequence();
                }
            }
        });

        pack();
    }

    public void showDialog(TrainsCycle circulation) {
        this.circulation = circulation;
        this.fillFree();
        this.setVisible(true);
    }

    private void fillFree() {
        elementSelectionPanel.clear();
        TrainsCycleType type = circulation.getType();
        // left - not selected part
        for (TrainsCycle cc : type.getCycles()) {
            if (cc != circulation && !cc.isPartOfSequence()) {
                elementSelectionPanel.getNotSelected().addWrapper(Wrapper.getWrapper(cc));
            }
        }
        // right - selected
        List<Wrapper<TrainsCycle>> selCirc = new ArrayList<Wrapper<TrainsCycle>>();
        circulation.applyToSequence(tc -> selCirc.add(Wrapper.getWrapper(tc)));
        elementSelectionPanel.getSelected().setListOfWrappers(selCirc);
    }
}
