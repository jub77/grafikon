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
import net.parostroj.timetable.model.changes.*;
import net.parostroj.timetable.model.changes.DiagramChange.Action;
import net.parostroj.timetable.model.events.GTEvent;
import net.parostroj.timetable.model.events.GTEventType;
import net.parostroj.timetable.model.events.TrainDiagramEvent;
import net.parostroj.timetable.utils.ObjectsUtil;
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
        model.getMediator().addColleague(new GTEventsReceiverColleague() {

            private final TrackedCheckVisitor tcv = new TrackedCheckVisitor();
            private final TransformVisitor tv = new TransformVisitor();

            @Override
            public void processGTEventAll(GTEvent<?> event) {
                event.accept(tcv);
                if (!tcv.isTracked() || isIgnoredEvent(event))
                    return;
                event.accept(tv);
                DiagramChange change = tv.getChange();
                String text = ObjectsUtil.checkAndTrim(this.transformChange(change));
                if (text != null) {
                    updateCenter(text);
                }
            }

            @Override
            public void processTrainDiagramEvent(TrainDiagramEvent event) {
                switch (event.getType()) {
                    case TRAINS_CYCLE_ADDED: case TRAINS_CYCLE_REMOVED:
                        updateCirculations(event.getSource());
                        break;
                    case TRAIN_ADDED: case TRAIN_REMOVED:
                        updateTrainCount(event.getSource());
                        break;
                    default:
                        // nothing
                        break;
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
                            if (endl) {
                                b.append(',');
                            } else {
                                endl = true;
                            }
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
            case SET_DIAGRAM_CHANGED:
                this.updateTrainCount(event.getModel().getDiagram());
                break;
            default:
                // nothing
                break;
        }
        // right
        switch (event.getType()) {
            case SET_DIAGRAM_CHANGED:
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
            int drivers = diagram.getDriverCycles().size();
            int engines = diagram.getEngineCycles().size();
            int trainUnits = diagram.getTrainUnitCycles().size();
            String text = String.format("%s: %d, %s: %d, %s: %d",
                    ResourceLoader.getString("sbar.engines"), engines,
                    ResourceLoader.getString("sbar.train.units"), trainUnits,
                    ResourceLoader.getString("sbar.drivers"), drivers);
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
        text = ObjectsUtil.checkAndTrim(text);
        center.setText(text);
        if (text != null) {
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
        left.setColumns(25);
        center = new javax.swing.JTextField();
        right = new javax.swing.JTextField();
        right.setColumns(10);

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
