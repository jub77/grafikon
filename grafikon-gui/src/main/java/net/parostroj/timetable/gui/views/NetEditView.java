/*
 * NetEditView.java
 *
 * Created on 30.11.2008, 14:48:51
 */
package net.parostroj.timetable.gui.views;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import net.parostroj.timetable.gui.*;
import net.parostroj.timetable.gui.dialogs.*;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.utils.CheckingUtils;
import net.parostroj.timetable.utils.IdGenerator;
import net.parostroj.timetable.utils.ResourceLoader;
import net.parostroj.timetable.utils.Tuple;

/**
 * View for editing net.
 *
 * @author jub
 */
public class NetEditView extends javax.swing.JPanel implements NetSelectionModel.NetSelectionListener, ApplicationModelListener {

    private ApplicationModel model;
    private NetSelectionModel netEditModel;
    private NetViewMarqueeHandler marqueeHandler;

    private EditNodeDialog editNodeDialog;
    private EditLineDialog editLineDialog;
    private CreateLineDialog createLineDialog;

    private Action newNodeAction;
    private Action newLineAction;
    private Action editAction;
    private Action deleteAction;

    public class NewNodeAction extends AbstractAction {

        public NewNodeAction(String name) {
            super(name);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (model.getDiagram() != null) {
                String result = JOptionPane.showInputDialog(NetEditView.this, ResourceLoader.getString("nl.name"));
                // do not create if empty or cancel selected
                if (result == null || result.equals(""))
                    return;
                Node n = model.getDiagram().createNode(IdGenerator.getInstance().getId(), NodeType.STATION, result, result);
                NodeTrack track = new NodeTrack(IdGenerator.getInstance().getId(), "1");
                track.setPlatform(true);
                n.addTrack(track);
                model.getDiagram().getNet().addNode(n);
                model.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.NEW_NODE,model,n));
            }
        }
    }

    public class NewLineAction extends AbstractAction {

        public NewLineAction(String name) {
            super(name);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (model.getDiagram() != null) {
                createLineDialog.updateNodes();
                createLineDialog.setLocationRelativeTo(NetEditView.this);
                createLineDialog.setVisible(true);

                // test if there ok was selected
                if (createLineDialog.getSelectedNodes() == null)
                    return;

                Tuple<Node> selected = createLineDialog.getSelectedNodes();
                // create new line
                Line l = model.getDiagram().createLine(IdGenerator.getInstance().getId(), 1000, selected.first, selected.second, Line.UNLIMITED_SPEED);
                LineTrack track = new LineTrack(IdGenerator.getInstance().getId(), "1");
                l.addTrack(track);
                model.getDiagram().getNet().addLine(selected.first, selected.second, l);

                model.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.NEW_LINE, model, l));
            }
        }
    }

    public class EditAction extends AbstractAction {

        public EditAction(String name) {
            super(name);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // edit line
            if (netEditModel.getSelectedLine() != null) {
                Line selectedLine = netEditModel.getSelectedLine();
                editLineDialog.setLine(selectedLine);
                editLineDialog.setLocationRelativeTo(NetEditView.this);
                editLineDialog.setVisible(true);
                if (editLineDialog.isModified()) {
                    model.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.MODIFIED_LINE, model, selectedLine));
                }
            }
            // edit node
            if (netEditModel.getSelectedNode() != null) {
                Node selectedNode = netEditModel.getSelectedNode();
                editNodeDialog.setNode(selectedNode, model.getProgramSettings().getLengthUnit());
                editNodeDialog.setLocationRelativeTo(NetEditView.this);
                editNodeDialog.setVisible(true);
                if (editNodeDialog.isModified()) {
                    model.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.MODIFIED_NODE, model, selectedNode));
                }
            }
        }
    }

    public class DeleteAction extends AbstractAction {

        public DeleteAction(String name) {
            super(name);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Component comp = NetEditView.this;
            // delete node
            if (netEditModel.getSelectedNode() != null) {
                Node selectedNode = netEditModel.getSelectedNode();
                if (!selectedNode.isEmpty() || !model.getDiagram().getNet().getLinesOf(selectedNode).isEmpty() || CheckingUtils.checkRoutesForNode(selectedNode, model.getDiagram().getRoutes())) {
                    if (!selectedNode.isEmpty())
                        JOptionPane.showMessageDialog(comp, ResourceLoader.getString("nl.error.notempty"),ResourceLoader.getString("nl.error.title"),JOptionPane.ERROR_MESSAGE);
                    else if (!model.getDiagram().getNet().getLinesOf(selectedNode).isEmpty())
                        JOptionPane.showMessageDialog(comp, ResourceLoader.getString("nl.error.linesexist"),ResourceLoader.getString("nl.error.title"),JOptionPane.ERROR_MESSAGE);
                    else
                        JOptionPane.showMessageDialog(comp, ResourceLoader.getString("ne.error.routepart"),ResourceLoader.getString("nl.error.title"),JOptionPane.ERROR_MESSAGE);
                } else {
                    model.getDiagram().getNet().removeNode(selectedNode);
                    model.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.DELETE_NODE, model, selectedNode));
                }
            }
            // delete line
            if (netEditModel.getSelectedLine() != null) {
                Line selectedLine = netEditModel.getSelectedLine();
                if (!selectedLine.isEmpty() || CheckingUtils.checkRoutesForLine(selectedLine, model.getDiagram().getRoutes())) {
                    if (!selectedLine.isEmpty())
                        JOptionPane.showMessageDialog(comp, ResourceLoader.getString("nl.error.notempty"),ResourceLoader.getString("nl.error.title"),JOptionPane.ERROR_MESSAGE);
                    else
                        JOptionPane.showMessageDialog(comp, ResourceLoader.getString("ne.error.routepart"),ResourceLoader.getString("nl.error.title"),JOptionPane.ERROR_MESSAGE);
                } else {
                    model.getDiagram().getNet().removeLine(selectedLine);
                    model.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.DELETE_LINE, model, selectedLine));
                }
            }
        }
    }

    /** Creates new form NetEditView */
    public NetEditView() {
        newNodeAction = new NewNodeAction(ResourceLoader.getString("net.edit.new.node") + " ...");
        newLineAction = new NewLineAction(ResourceLoader.getString("net.edit.new.line") + " ...");
        editAction = new EditAction(ResourceLoader.getString("button.edit") + " ...");
        editAction.setEnabled(false);
        deleteAction = new DeleteAction(ResourceLoader.getString("button.delete"));
        deleteAction.setEnabled(false);


        initComponents();
        initializeListeners();
    }

    private void initializeListeners() {
        // add net edit model to net view
        netEditModel = new NetSelectionModel();
        marqueeHandler = new NetViewMarqueeHandler();
        netView.setGraphCallbacks(netEditModel, marqueeHandler);
        netEditModel.addNetSelectionListener(this);
    }

    private void initializeDialogs() {
        // initialize dialogs
        editNodeDialog = new EditNodeDialog((Frame)this.getTopLevelAncestor());
        editLineDialog = new EditLineDialog((Frame)this.getTopLevelAncestor(), true);
        createLineDialog = new CreateLineDialog((Frame)this.getTopLevelAncestor(), true);
    }

    /**
     * @param model model to be set
     */
    public void setModel(ApplicationModel model) {
        this.initializeDialogs();
        this.model = model;
        netView.setModel(model);
        createLineDialog.setModel(model);
        editLineDialog.setModel(model);
        model.addListener(this);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.JButton newNodeButton = new javax.swing.JButton();
        javax.swing.JButton newLineButton = new javax.swing.JButton();
        javax.swing.JButton editButton = new javax.swing.JButton();
        javax.swing.JButton deleteButton = new javax.swing.JButton();
        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane();
        netView = new net.parostroj.timetable.gui.views.NetView();

        newNodeButton.setAction(newNodeAction);
        newNodeButton.setEnabled(false);

        newLineButton.setAction(newLineAction);
        newLineButton.setEnabled(false);

        editButton.setAction(editAction);
        editButton.setEnabled(false);

        deleteButton.setAction(deleteAction);
        deleteButton.setEnabled(false);

        scrollPane.setViewportView(netView);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(newNodeButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(newLineButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(editButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(newNodeButton)
                    .addComponent(newLineButton)
                    .addComponent(editButton)
                    .addComponent(deleteButton))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private net.parostroj.timetable.gui.views.NetView netView;
    // End of variables declaration//GEN-END:variables

    @Override
    public void selection(NetSelectionModel.Action action, Node node, Line line) {
        switch (action) {
            case LINE_SELECTED: case NODE_SELECTED:
                editAction.setEnabled(true);
                deleteAction.setEnabled(true);
                break;
            case NOTHING_SELECTED:
                editAction.setEnabled(false);
                deleteAction.setEnabled(false);
                break;
        }
    }

    @Override
    public void modelChanged(ApplicationModelEvent event) {
        if (event.getType() == ApplicationModelEventType.SET_DIAGRAM_CHANGED) {
            newLineAction.setEnabled(event.getModel().getDiagram() != null);
            newNodeAction.setEnabled(event.getModel().getDiagram() != null);
        }
    }
}
