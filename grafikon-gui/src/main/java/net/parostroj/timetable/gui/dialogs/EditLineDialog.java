/*
 * EditLineDialog.java
 *
 * Created on 30. září 2007, 11:13
 */
package net.parostroj.timetable.gui.dialogs;

import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.math.BigDecimal;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import org.beanfabrics.Path;
import org.beanfabrics.swing.BnButton;
import org.beanfabrics.swing.table.BnColumn;
import org.beanfabrics.swing.table.BnTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.gui.pm.LinePM;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.GuiIcon;
import net.parostroj.timetable.model.Line;
import net.parostroj.timetable.model.LineClass;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.units.LengthUnit;
import net.parostroj.timetable.model.units.UnitUtil;
import net.parostroj.timetable.utils.ObjectsUtil;
import net.parostroj.timetable.utils.ResourceLoader;

/**
 * Editation of a line.
 *
 * @author jub
 */
public class EditLineDialog extends BaseEditDialog<LinePM> {

    private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(EditLineDialog.class);

    private static final LineClass noneLineClass;

    static {
        noneLineClass = new LineClass(null);
        noneLineClass.setName(ResourceLoader.getString("line.class.none"));
    }

    private Line line;

    /** Creates new form EditLineDialog */
    public EditLineDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        // set units
        lengthEditBox.setUnits(LengthUnit.getScaleDependent());
    }

    public void showDialog(Line line, LengthUnit lengthUnit) {
        this.line = line;
        this.setPresentationModel(new LinePM(line));
        TrainDiagram diagram = line.getDiagram();

        lengthEditBox.setUnit(diagram.getAttributes().get(TrainDiagram.ATTR_EDIT_LENGTH_UNIT, LengthUnit.class, lengthUnit));

        // update track for from and to (direct)
        Node from = line.getFrom();
        Node to = line.getTo();
        fromToLabel.setText(from.getName() + " - " + to.getName());

        if (line.getTopSpeed() == null) {
            speedTextField.setText("");
        } else {
            speedTextField.setText(Integer.toString(line.getTopSpeed()));
        }

        lengthEditBox.setValueInUnit(new BigDecimal(line.getLength()), LengthUnit.MM);

        controlledCheckBox.setSelected(Boolean.TRUE.equals(line.getAttribute(Line.ATTR_CONTROLLED, Boolean.class)));

        // update line class combo box
        List<LineClass> classes = line.getDiagram().getNet().getLineClasses();
        lineClassComboBox.removeAllItems();
        lineClassComboBox.addItem(noneLineClass);
        for (LineClass clazz : classes) {
            lineClassComboBox.addItem(clazz);
        }
        if (line.getAttribute(Line.ATTR_CLASS, LineClass.class) == null)
            lineClassComboBox.setSelectedItem(noneLineClass);
        else
            lineClassComboBox.setSelectedItem(line.getAttribute(Line.ATTR_CLASS, LineClass.class));

        // update line class back combo box
        lineClassBackComboBox.removeAllItems();
        lineClassBackComboBox.addItem(noneLineClass);
        for (LineClass clazz : classes)
            lineClassBackComboBox.addItem(clazz);
        if (line.getAttribute(Line.ATTR_CLASS_BACK, LineClass.class) == null)
            lineClassBackComboBox.setSelectedItem(lineClassComboBox.getSelectedItem());
        else
            lineClassBackComboBox.setSelectedItem(line.getAttribute(Line.ATTR_CLASS_BACK, LineClass.class));

        this.pack();

        this.setVisible(true);
    }

    private void writeValuesBack() {
        int length = line.getLength();
        try {
            length = UnitUtil.convert(lengthEditBox.getValueInUnit(LengthUnit.MM));
        } catch (ArithmeticException e) {
            log.warn("Value overflow: {}", lengthEditBox.getValueInUnit(LengthUnit.MM));
        }
        Integer speed = line.getTopSpeed();
        try {
            String speedText = ObjectsUtil.checkAndTrim(speedTextField.getText());
            if (speedText != null) {
                speed = Integer.parseInt(speedTextField.getText());
                if (speed <= 0) {
                    speed = null;
                }
            } else {
                speed = null;
            }
        } catch (NumberFormatException e) {
            log.warn("Cannot convert string to int (speed).", e);
        }
        if (line.getLength() != length && length > 0) {
            line.setLength(length);
        }

        if (line.getTopSpeed() != speed) {
            line.setTopSpeed(speed);
        }

        Boolean bool = line.getAttribute(Line.ATTR_CONTROLLED, Boolean.class);
        if ((bool == null && controlledCheckBox.isSelected()) || (bool != null && controlledCheckBox.isSelected() != bool.booleanValue()))
            line.setAttribute(Line.ATTR_CONTROLLED, controlledCheckBox.isSelected());

        // set line class
        line.getAttributes().setRemove(Line.ATTR_CLASS, lineClassComboBox.getSelectedItem() == noneLineClass ? null : lineClassComboBox.getSelectedItem());
        // set line class back
        line.getAttributes().setRemove(Line.ATTR_CLASS_BACK, lineClassBackComboBox.getSelectedItem() == lineClassComboBox.getSelectedItem()
                        || lineClassBackComboBox.getSelectedItem() == noneLineClass ? null : lineClassBackComboBox.getSelectedItem());
    }

    private void initComponents() {
        javax.swing.JLabel lengthLabel = new javax.swing.JLabel();
        speedTextField = new javax.swing.JTextField();
        speedTextField.setColumns(30);
        speedTextField.setHorizontalAlignment(JTextField.RIGHT);
        controlledCheckBox = new javax.swing.JCheckBox();
        javax.swing.JLabel speedLabel = new javax.swing.JLabel();
        BnButton okButton = new BnButton();
        javax.swing.JButton cancelButton = new javax.swing.JButton();
        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane();
        BnButton newTrackButton = GuiComponentUtils.createBnButton(GuiIcon.ADD, 1);
        BnButton deleteTrackButton = GuiComponentUtils.createBnButton(GuiIcon.REMOVE, 1);
        BnButton upButton = GuiComponentUtils.createBnButton(GuiIcon.GO_UP, 1);
        BnButton downButton = GuiComponentUtils.createBnButton(GuiIcon.GO_DOWN, 1);
        javax.swing.JLabel tracksLabel = new javax.swing.JLabel();
        javax.swing.JLabel controlledLabel = new javax.swing.JLabel();
        lineClassComboBox = new javax.swing.JComboBox<>();
        javax.swing.JLabel lineClassLabel = new javax.swing.JLabel();
        fromToLabel = new javax.swing.JLabel();
        lengthEditBox = new net.parostroj.timetable.gui.components.ValueWithUnitEditBox();
        javax.swing.JLabel lineClassBackLabel = new javax.swing.JLabel();
        lineClassBackComboBox = new javax.swing.JComboBox<>();

        setTitle(ResourceLoader.getString("editline.title")); // NOI18N
        setResizable(false);

        tracksTable.setModelProvider(localProvider);
        tracksTable.setPath(new Path("tracks"));
        Dimension ttSize = tracksTable.getPreferredScrollableViewportSize();
        ttSize.height = 5 * tracksTable.getRowHeight();
        ttSize.width = 0;
        tracksTable.setPreferredScrollableViewportSize(ttSize);
        scrollPane.setViewportView(tracksTable);

        tracksTable.addColumn(new BnColumn(new Path("number"), ResourceLoader.getString("editline.track")));

        newTrackButton.setModelProvider(localProvider);
        newTrackButton.setPath(new Path("tracks.create"));
        deleteTrackButton.setModelProvider(localProvider);
        deleteTrackButton.setPath(new Path("tracks.delete"));
        upButton.setModelProvider(localProvider);
        upButton.setPath(new Path("tracks.moveUp"));
        downButton.setModelProvider(localProvider);
        downButton.setPath(new Path("tracks.moveDown"));

        lengthLabel.setText(ResourceLoader.getString("editline.length")); // NOI18N

        speedLabel.setText(ResourceLoader.getString("editline.speed")); // NOI18N

        okButton.setText(ResourceLoader.getString("button.ok")); // NOI18N
        okButton.addActionListener(evt -> {
            this.writeValuesBack();
            this.setVisible(false);
        });
        okButton.setModelProvider(localProvider);
        okButton.setPath(new Path("ok"));

        cancelButton.setText(ResourceLoader.getString("button.cancel")); // NOI18N
        cancelButton.addActionListener(evt -> this.setVisible(false));

        tracksLabel.setText(ResourceLoader.getString("editline.tracks")); // NOI18N

        controlledLabel.setText(ResourceLoader.getString("editline.type.controlled") + ": "); // NOI18N

        lineClassComboBox.addItemListener(this::lineClassChanged);

        lineClassLabel.setText(ResourceLoader.getString("editline.lineclass")); // NOI18N

        fromToLabel.setText(" ");

        lineClassBackLabel.setText(ResourceLoader.getString("editline.lineclass.back")); // NOI18N

        lineClassBackComboBox.addItemListener(this::lineClassChanged);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(Alignment.TRAILING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(okButton)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(cancelButton))
                        .addComponent(fromToLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(Alignment.LEADING)
                                .addComponent(lineClassBackLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(Alignment.LEADING, false)
                                    .addComponent(tracksLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(speedLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(controlledLabel)
                                    .addComponent(lineClassLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addComponent(lengthLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(Alignment.LEADING)
                                .addComponent(controlledCheckBox)
                                .addComponent(speedTextField, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addGroup(layout.createParallelGroup(Alignment.LEADING, false)
                                        .addComponent(deleteTrackButton)
                                        .addComponent(newTrackButton)
                                        .addComponent(upButton)
                                        .addComponent(downButton)))
                                .addComponent(lineClassComboBox, 0, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                                .addComponent(lengthEditBox, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                                .addComponent(lineClassBackComboBox, 0, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))))
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(fromToLabel)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.CENTER)
                        .addComponent(lengthLabel)
                        .addComponent(lengthEditBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(speedTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(speedLabel))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.CENTER)
                        .addComponent(controlledCheckBox)
                        .addComponent(controlledLabel))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lineClassComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(lineClassLabel))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lineClassBackLabel)
                        .addComponent(lineClassBackComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(Alignment.LEADING)
                                .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(newTrackButton)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(deleteTrackButton)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(upButton)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(downButton)))
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                                .addComponent(cancelButton)
                                .addComponent(okButton)))
                        .addComponent(tracksLabel))
                    .addContainerGap())
        );
        getContentPane().setLayout(layout);

        pack();
    }

    private void lineClassChanged(java.awt.event.ItemEvent evt) {
        // check consistency
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            Object lc = lineClassComboBox.getSelectedItem();
            Object lcb = lineClassBackComboBox.getSelectedItem();
            Object nlc = lc;
            Object nlcb = lcb;

            if (lc == noneLineClass) {
                nlc = noneLineClass;
                nlcb = noneLineClass;
            } else if (lc != noneLineClass && lcb == noneLineClass) {
                nlc = lc;
                nlcb = lc;
            }
            if (lc != nlc)
                lineClassComboBox.setSelectedItem(nlc);
            if (lcb != nlcb)
                lineClassBackComboBox.setSelectedItem(nlcb);
        }
    }

    private javax.swing.JCheckBox controlledCheckBox;
    private javax.swing.JLabel fromToLabel;
    private net.parostroj.timetable.gui.components.ValueWithUnitEditBox lengthEditBox;
    private javax.swing.JComboBox<LineClass> lineClassBackComboBox;
    private javax.swing.JComboBox<LineClass> lineClassComboBox;
    private javax.swing.JTextField speedTextField;
    private BnTable tracksTable = new BnTable();
}
