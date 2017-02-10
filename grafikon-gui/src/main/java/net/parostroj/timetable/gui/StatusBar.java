/*
 * StatusBar.java
 *
 * Created on 4. září 2007, 16:54
 */
package net.parostroj.timetable.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.Timer;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import net.parostroj.timetable.mediator.GTEventsReceiverColleague;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.changes.*;
import net.parostroj.timetable.model.changes.DiagramChange.Action;
import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.utils.ObjectsUtil;
import net.parostroj.timetable.utils.ResourceLoader;

/**
 * Status bar for the application.
 *
 * @author jub
 */
public class StatusBar extends javax.swing.JPanel implements ApplicationModelListener {

    private static final int LEFT_SIDE_COLUMNS = 24;
    private static final int RIGHT_SIDE_COLUMNS = 34;

    private static final int TIMEOUT = 20000;

    private final Timer timer;

    private boolean changed;
    private DateTimeFormatter format = DateTimeFormat.mediumDateTime();

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
            public void processGTEventAll(Event event) {
                EventProcessing.visit(event, tcv);
                if (!tcv.isTracked() || isIgnoredEvent(event)) {
                    return;
                }
                EventProcessing.visit(event, tv);
                DiagramChange change = tv.getChange();
                String text = ObjectsUtil.checkAndTrim(this.transformChange(change));
                if (text != null) {
                    updateCenter(text);
                }
            }

            @Override
            public void processTrainDiagramEvent(Event event) {
                TrainDiagram diagram = (TrainDiagram) event.getSource();
                switch (event.getType()) {
                    case ADDED: case REMOVED:
                        if (event.getObject() instanceof TrainsCycle) {
                            updateTrainsAndCirculations(diagram);
                        }
                        if (event.getObject() instanceof Train) {
                            updateTrainsAndCirculations(diagram);
                        }
                        break;
                    case ATTRIBUTE:
                        if (event.getAttributeChange().checkName(TrainDiagram.ATTR_SAVE_VERSION, TrainDiagram.ATTR_SAVE_TIMESTAMP)) {
                            updateVersionAndTimestamp(diagram);
                        }
                        break;
                    default:
                        // nothing
                        break;
                }
            }

            private boolean isIgnoredEvent(Event event) {
                if (event.getType() == Event.Type.ATTRIBUTE && event.getSource() instanceof Node) {
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
        TrainDiagram diagram = event.getModel().getDiagram();
        switch (event.getType()) {
            case SET_DIAGRAM_CHANGED:
                changed = false;
                this.updateTrainsAndCirculations(diagram);
                this.updateVersionAndTimestamp(diagram);
                break;
            case MODEL_SAVED:
                changed = false;
                this.updateVersionAndTimestamp(diagram);
                break;
            case MODEL_CHANGED:
                changed = true;
                this.updateVersionAndTimestamp(diagram);
                break;
            default:
                // nothing
                break;
        }
    }

    private void updateTrainsAndCirculations(TrainDiagram diagram) {
        if (diagram == null) {
            updateRight("");
        } else {
            int drivers = diagram.getDriverCycleType().getCycles().size();
            int engines = diagram.getEngineCycleType().getCycles().size();
            int trainUnits = diagram.getTrainUnitCycleType().getCycles().size();
            String text = String.format("%s %d, %s: %d, %s: %d, %s: %d",
                    ResourceLoader.getString("status.bar.trains"), diagram.getTrains().size(),
                    ResourceLoader.getString("sbar.engines"), engines,
                    ResourceLoader.getString("sbar.train.units"), trainUnits,
                    ResourceLoader.getString("sbar.drivers"), drivers);
            updateRight(text);
        }
    }

    private void updateVersionAndTimestamp(TrainDiagram diagram) {
        if (diagram == null) {
            updateLeft("");
        } else {
            Date timestamp = diagram.getSaveTimestamp();
            String text = String.format("%s [%d]",
                    timestamp != null ? format.print(timestamp.getTime()) : "-",
                    diagram.getSaveVersion());
            if (changed) {
                text += " *";
            }
            updateLeft(text);
        }
    }

    private void updateLeft(String text) {
        left.setText(text);
        left.setCaretPosition(0);
    }

    private void updateRight(String text) {
        right.setText(text);
        right.setCaretPosition(0);
    }

    private void updateCenter(String text) {
        text = ObjectsUtil.checkAndTrim(text);
        center.setText(text);
        center.setCaretPosition(0);
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
        left.setColumns(LEFT_SIDE_COLUMNS);
        center = new javax.swing.JTextField();
        right = new javax.swing.JTextField();
        right.setColumns(RIGHT_SIDE_COLUMNS);

        left.setEditable(false);

        center.setEditable(false);

        right.setEditable(false);
        right.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        setLayout(new BorderLayout(0, 0));
        add(left, BorderLayout.WEST);
        add(center, BorderLayout.CENTER);
        add(right, BorderLayout.EAST);
    }

    private javax.swing.JTextField center;
    private javax.swing.JTextField left;
    private javax.swing.JTextField right;
}
