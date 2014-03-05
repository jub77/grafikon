/*
 * StatusBar.java
 *
 * Created on 4. září 2007, 16:54
 */
package net.parostroj.timetable.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import net.parostroj.timetable.mediator.GTEventsReceiverColleague;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainsCycleType;
import net.parostroj.timetable.model.changes.*;
import net.parostroj.timetable.model.changes.DiagramChange.Action;
import net.parostroj.timetable.model.events.GTEvent;
import net.parostroj.timetable.model.events.GTEventType;
import net.parostroj.timetable.utils.ResourceLoader;

/**
 * Status bar for the application.
 *
 * @author jub
 */
public class StatusBar extends javax.swing.JPanel implements ApplicationModelListener {

    private static final int TIMEOUT = 20000;

    private final Timer timer;

    /** Creates new form StatusBar */
    public StatusBar() {
        initComponents();
        updateLeft("");
        updateCenter("");
        updateRight("");
        timer = new Timer(TIMEOUT, new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
                updateCenter("");
            }
        });
        timer.setRepeats(false);
    }

    public void setModel(ApplicationModel model) {
        model.getMediator().addColleague(new GTEventsReceiverColleague(true) {

            private final TrackedCheckVisitor tcv = new TrackedCheckVisitor();
            private final TransformVisitor tv = new TransformVisitor();

            @Override
            public void processGTEventAll(GTEvent<?> event) {
                event.accept(tcv);
                if (!tcv.isTracked() || isIgnoredEvent(event))
                    return;
                event.accept(tv);
                DiagramChange change = tv.getChange();
                String text = this.transformChange(change);
                if (!"".equals(text)) {
                    updateCenter(text);
                }
            }

            private boolean isIgnoredEvent(GTEvent<?> event) {
            	if (event.getType() == GTEventType.ATTRIBUTE && event.getSource() instanceof Node) {
            		String attribName = event.getAttributeChange().getName();
					return attribName.equals("positionX") || attribName.equals("positionY");
            	} else {
            		return false;
            	}
			}

			private String transformChange(DiagramChange change) {
                StringBuilder b = new StringBuilder();
                b.append(change.getType()).append(": ");
                b.append(change.getObject() != null ? change.getObject() : change.getType()).append(' ');
                if (change.getAction() == Action.MODIFIED) {
                    b.append('(');
                    if (change.getDescriptions() != null) {
                        boolean endl = false;
                        for (DiagramChangeDescription d : change.getDescriptions()) {
                            if (endl)
                                b.append(',');
                            else
                                endl = true;
                            b.append(d.getFormattedDescription());
                        }
                    }
                    b.append(')');
                } else {
                    b.append('(').append(change.getAction()).append(')');
                }
                return b.toString();
            }
        });
    }

    @Override
    public void modelChanged(ApplicationModelEvent event) {
        // left
        switch (event.getType()) {
            case SET_DIAGRAM_CHANGED: case NEW_TRAIN: case DELETE_TRAIN:
                this.updateTrainCount(event.getModel().getDiagram());
                break;
            default:
                // nothing
                break;
        }
        // right
        switch (event.getType()) {
            case SET_DIAGRAM_CHANGED: case NEW_CYCLE: case DELETED_CYCLE:
                this.updateCirculations(event.getModel().getDiagram());
                break;
            default:
                // nothing
                break;
        }
    }

    private void updateCirculations(TrainDiagram diagram) {
        if (diagram == null) {
            updateLeft("");
        } else {
            int drivers = diagram.getCycles(TrainsCycleType.DRIVER_CYCLE).size();
            int engines = diagram.getCycles(TrainsCycleType.ENGINE_CYCLE).size();
            int trainUnits = diagram.getCycles(TrainsCycleType.TRAIN_UNIT_CYCLE).size();
            String text = String.format("%d, %d, %d", engines, trainUnits, drivers);
            updateLeft(text);
        }
    }

    private void updateTrainCount(TrainDiagram diagram) {
        if (diagram == null) {
            updateRight("");
        } else {
            updateRight(ResourceLoader.getString("status.bar.trains") + " " + diagram.getTrains().size());
        }
    }

    private void updateLeft(String text) {
        left.setText(text);
    }

    private void updateRight(String text) {
        right.setText(text);
    }

    private void updateCenter(String text) {
        center.setText(text);
        if (!"".equals(text)) {
            // start timer
            if (timer != null) {
                timer.stop();
                timer.setInitialDelay(TIMEOUT);
                timer.start();
            }
        }
    }

    private void initComponents() {

        left = new javax.swing.JTextField();
        left.setColumns(15);
        center = new javax.swing.JTextField();
        right = new javax.swing.JTextField();
        right.setColumns(15);

        left.setEditable(false);

        center.setEditable(false);

        right.setEditable(false);
        setLayout(new BorderLayout(0, 0));
        add(left, BorderLayout.WEST);
        add(center, BorderLayout.CENTER);
        add(right, BorderLayout.EAST);
    }

    private javax.swing.JTextField center;
    private javax.swing.JTextField left;
    private javax.swing.JTextField right;
}
