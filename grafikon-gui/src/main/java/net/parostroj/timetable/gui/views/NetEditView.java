/*
 * NetEditView.java
 *
 * Created on 30.11.2008, 14:48:51
 */
package net.parostroj.timetable.gui.views;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;

import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.ApplicationModelEvent;
import net.parostroj.timetable.gui.ApplicationModelEventType;
import net.parostroj.timetable.gui.ApplicationModelListener;
import net.parostroj.timetable.gui.actions.execution.ActionContext;
import net.parostroj.timetable.gui.actions.execution.ActionHandler;
import net.parostroj.timetable.gui.actions.execution.EventDispatchAfterModelAction;
import net.parostroj.timetable.gui.actions.execution.ModelAction;
import net.parostroj.timetable.gui.dialogs.CreateLineDialog;
import net.parostroj.timetable.gui.dialogs.EditLineDialog;
import net.parostroj.timetable.gui.dialogs.EditNodeDialog;
import net.parostroj.timetable.gui.dialogs.SaveImageDialog;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.utils.CheckingUtils;
import net.parostroj.timetable.utils.IdGenerator;
import net.parostroj.timetable.utils.ResourceLoader;
import net.parostroj.timetable.utils.Tuple;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphics2D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

/**
 * View for editing net.
 *
 * @author jub
 */
public class NetEditView extends javax.swing.JPanel implements NetSelectionModel.NetSelectionListener, ApplicationModelListener {

    private static final Logger LOG = LoggerFactory.getLogger(NetEditView.class);

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
    private Action saveNetImageAction;

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

    public class SaveNetImageAction extends AbstractAction {

        private SaveImageDialog dialog;

        public SaveNetImageAction(String name) {
            super(name);
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            SaveImageDialog saveDialog = getDialog();
            dialog.setLocationRelativeTo(NetEditView.this);
            Dimension graphSize = netView.getGraphSize();
            dialog.setSaveSize(new Dimension(graphSize.width + 10, graphSize.height + 10));
            saveDialog.setVisible(true);

            if (!dialog.isSave())
                return;

            ActionContext actionContext = new ActionContext(NetEditView.this);
            ModelAction action = new EventDispatchAfterModelAction(actionContext) {
                
                private boolean error;
                
                @Override
                protected void backgroundAction() {
                    setWaitDialogVisible(true);
                    setWaitMessage(ResourceLoader.getString("wait.message.image.save"));
                    long time = System.currentTimeMillis();
                    try {
                        Dimension saveSize = dialog.getSaveSize();
    
                        if (dialog.getImageType() == SaveImageDialog.Type.PNG) {
                            BufferedImage img = new BufferedImage(saveSize.width, saveSize.height, BufferedImage.TYPE_INT_RGB);
                            Graphics2D g2d = img.createGraphics();
                            g2d.setColor(Color.white);
                            g2d.fillRect(0, 0, saveSize.width, saveSize.height);
    
                            netView.paintGraph(g2d);
    
                            try {
                                ImageIO.write(img, "png", dialog.getSaveFile());
                            } catch (IOException e) {
                                LOG.warn("Error saving file: " + dialog.getSaveFile(), e);
                                error = true;
                            }
                        } else if (dialog.getImageType() == SaveImageDialog.Type.SVG) {
                            DOMImplementation domImpl =
                                    GenericDOMImplementation.getDOMImplementation();
    
                            // Create an instance of org.w3c.dom.Document.
                            String svgNS = "http://www.w3.org/2000/svg";
                            Document document = domImpl.createDocument(svgNS, "svg", null);
    
                            SVGGeneratorContext context = SVGGeneratorContext.createDefault(document);
                            SVGGraphics2D g2d = new SVGGraphics2D(context, false);
    
                            g2d.setSVGCanvasSize(saveSize);
    
                            netView.paintGraph(g2d);
    
                            // write to ouput - do not use css style
                            boolean useCSS = false;
                            try {
                                Writer out = new OutputStreamWriter(new FileOutputStream(dialog.getSaveFile()), "UTF-8");
                                g2d.stream(out, useCSS);
                            } catch (IOException e) {
                                LOG.warn("Error saving file: " + dialog.getSaveFile(), e);
                                error = true;
                            }
                        }
                    } finally {
                        LOG.debug("Image save finished in {}ms", System.currentTimeMillis() - time);
                        setWaitDialogVisible(false);
                    }
                }
                
                @Override
                protected void eventDispatchActionAfter() {
                    if (error) {
                        JOptionPane.showMessageDialog(NetEditView.this, ResourceLoader.getString("save.image.error"), ResourceLoader.getString("save.image.error.text"), JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            ActionHandler.getInstance().execute(action);
        }

        private SaveImageDialog getDialog() {
            if (dialog == null) {
                dialog = new SaveImageDialog((Frame)NetEditView.this.getTopLevelAncestor(), true);
                dialog.setSizeChangeEnabled(false);
            }
            return dialog;
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
        saveNetImageAction = new SaveNetImageAction(ResourceLoader.getString("net.edit.save.image") + " ...");
        saveNetImageAction.setEnabled(true);

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
        jButton1 = new javax.swing.JButton();

        newNodeButton.setAction(newNodeAction);
        newNodeButton.setEnabled(false);

        newLineButton.setAction(newLineAction);
        newLineButton.setEnabled(false);

        editButton.setAction(editAction);
        editButton.setEnabled(false);

        deleteButton.setAction(deleteAction);
        deleteButton.setEnabled(false);

        scrollPane.setViewportView(netView);

        jButton1.setAction(saveNetImageAction);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(scrollPane, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(newNodeButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(newLineButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(editButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 219, Short.MAX_VALUE)
                        .addComponent(jButton1)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(newNodeButton)
                        .addComponent(newLineButton)
                        .addComponent(editButton)
                        .addComponent(deleteButton))
                    .addComponent(jButton1))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
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
