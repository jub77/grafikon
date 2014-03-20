/*
 * NetEditView.java
 *
 * Created on 30.11.2008, 14:48:51
 */
package net.parostroj.timetable.gui.views;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventObject;

import javax.imageio.ImageIO;
import javax.swing.*;

import net.parostroj.timetable.gui.*;
import net.parostroj.timetable.gui.actions.execution.*;
import net.parostroj.timetable.gui.dialogs.EditLineDialog;
import net.parostroj.timetable.gui.dialogs.EditNodeDialog;
import net.parostroj.timetable.gui.dialogs.SaveImageDialog;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.GuiIcon;
import net.parostroj.timetable.gui.views.graph.*;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.events.TrainDiagramEvent;
import net.parostroj.timetable.model.events.TrainDiagramListener;
import net.parostroj.timetable.model.units.LengthUnit;
import net.parostroj.timetable.utils.CheckingUtils;
import net.parostroj.timetable.utils.IdGenerator;
import net.parostroj.timetable.utils.ResourceLoader;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphics2D;
import org.jgrapht.ListenableGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphOutline;
import com.mxgraph.swing.handler.*;
import com.mxgraph.swing.util.mxGraphActions;
import com.mxgraph.swing.view.mxICellEditor;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.view.mxCellState;
import javax.swing.border.EmptyBorder;

/**
 * View for editing net.
 *
 * @author jub
 */
public class NetEditView extends javax.swing.JPanel implements NetSelectionModel.NetSelectionListener,
        ApplicationModelListener, TrainDiagramListener, mxIEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(NetEditView.class);

    private ApplicationModel model;
    private NetSelectionModel netEditModel;

    private EditNodeDialog editNodeDialog;
    private EditLineDialog editLineDialog;

    private final Action newNodeAction;
    private final Action editAction;
    private final Action deleteAction;
    private final Action saveNetImageAction;
    private final Action zoomInAction;
    private final Action zoomOutAction;

    private NetGraphAdapter graph;
    private NetGraphComponent graphComponent;
    private mxGraphOutline graphOutline;
    private NodeInsertHandler insertHandler;
    private mxRubberband selectionHandler;
    private JPanel panel;
    private final Collection<JComponent> controls = new ArrayList<JComponent>();

    public class NewNodeAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (model.getDiagram() != null) {
                String result = JOptionPane.showInputDialog(NetEditView.this, ResourceLoader.getString("nl.name"));
                // do not create if empty or cancel selected
                if (result == null || result.equals(""))
                    return;
                Point location = null;
                if (e instanceof ActionEventWithLocation) {
                    location = ((ActionEventWithLocation) e).getLocation();
                } else {
                    location = new Point(20, 20);
                }
                Node n = model.getDiagram().createNode(IdGenerator.getInstance().getId(), NodeType.STATION, result,
                        result);
                NodeTrack track = new NodeTrack(IdGenerator.getInstance().getId(), "1");
                track.setPlatform(true);
                n.addTrack(track);
                n.setPositionX(location.x);
                n.setPositionY(location.y);
                model.getDiagram().getNet().addNode(n);
                mxCell cell = graph.getVertexToCellMap().get(n);
                graph.moveCells(new Object[] { cell }, n.getPositionX(), n.getPositionY());
            }
        }
    }

    public class EditAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            // edit line
            if (netEditModel.getSelectedLine() != null) {
                Line selectedLine = netEditModel.getSelectedLine();
                editLineDialog.setLine(selectedLine);
                editLineDialog.setLocationRelativeTo(NetEditView.this);
                editLineDialog.setVisible(true);
                if (editLineDialog.isModified()) {
                    model.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.MODIFIED_LINE, model,
                            selectedLine));
                }
            }
            // edit node
            if (netEditModel.getSelectedNode() != null) {
                Node selectedNode = netEditModel.getSelectedNode();
                editNodeDialog.setNode(selectedNode, model.getDiagram().getAttributes().get(TrainDiagram.ATTR_EDIT_LENGTH_UNIT, LengthUnit.class, model.getProgramSettings().getLengthUnit()));
                editNodeDialog.setLocationRelativeTo(NetEditView.this);
                editNodeDialog.setVisible(true);
                if (editNodeDialog.isModified()) {
                    model.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.MODIFIED_NODE, model,
                            selectedNode));
                }
            }
        }
    }

    public class DeleteAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            Component comp = NetEditView.this;
            // delete node
            if (netEditModel.getSelectedNode() != null) {
                Node selectedNode = netEditModel.getSelectedNode();
                if (!selectedNode.isEmpty() || !model.getDiagram().getNet().getLinesOf(selectedNode).isEmpty()
                        || CheckingUtils.checkRoutesForNode(selectedNode, model.getDiagram().getRoutes())) {
                    if (!selectedNode.isEmpty())
                        JOptionPane.showMessageDialog(comp, ResourceLoader.getString("nl.error.notempty"),
                                ResourceLoader.getString("nl.error.title"), JOptionPane.ERROR_MESSAGE);
                    else if (!model.getDiagram().getNet().getLinesOf(selectedNode).isEmpty())
                        JOptionPane.showMessageDialog(comp, ResourceLoader.getString("nl.error.linesexist"),
                                ResourceLoader.getString("nl.error.title"), JOptionPane.ERROR_MESSAGE);
                    else
                        JOptionPane.showMessageDialog(comp, ResourceLoader.getString("ne.error.routepart"),
                                ResourceLoader.getString("nl.error.title"), JOptionPane.ERROR_MESSAGE);
                } else {
                    model.getDiagram().getNet().removeNode(selectedNode);
                }
            }
            // delete line
            if (netEditModel.getSelectedLine() != null) {
                Line selectedLine = netEditModel.getSelectedLine();
                if (!selectedLine.isEmpty()
                        || CheckingUtils.checkRoutesForLine(selectedLine, model.getDiagram().getRoutes())) {
                    if (!selectedLine.isEmpty())
                        JOptionPane.showMessageDialog(comp, ResourceLoader.getString("nl.error.notempty"),
                                ResourceLoader.getString("nl.error.title"), JOptionPane.ERROR_MESSAGE);
                    else
                        JOptionPane.showMessageDialog(comp, ResourceLoader.getString("ne.error.routepart"),
                                ResourceLoader.getString("nl.error.title"), JOptionPane.ERROR_MESSAGE);
                } else {
                    model.getDiagram().getNet().removeLine(selectedLine);
                    model.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.DELETE_LINE, model,
                            selectedLine));
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
            Dimension graphSize = graphComponent.getViewport().getView().getPreferredSize();
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
                            BufferedImage img = new BufferedImage(saveSize.width, saveSize.height,
                                    BufferedImage.TYPE_INT_RGB);
                            Graphics2D g2d = img.createGraphics();
                            g2d.setColor(Color.white);
                            g2d.fillRect(0, 0, saveSize.width, saveSize.height);

                            graphComponent.getViewport().getView().paint(g2d);

                            try {
                                ImageIO.write(img, "png", dialog.getSaveFile());
                            } catch (Exception e) {
                                LOG.warn("Error saving file: " + dialog.getSaveFile(), e);
                                error = true;
                            }
                        } else if (dialog.getImageType() == SaveImageDialog.Type.SVG) {
                            DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();

                            // Create an instance of org.w3c.dom.Document.
                            String svgNS = "http://www.w3.org/2000/svg";
                            Document document = domImpl.createDocument(svgNS, "svg", null);

                            SVGGeneratorContext context = SVGGeneratorContext.createDefault(document);
                            SVGGraphics2D g2d = new SVGGraphics2D(context, false);

                            g2d.setSVGCanvasSize(saveSize);

                            graphComponent.getViewport().getView().paint(g2d);

                            // write to ouput - do not use css style
                            boolean useCSS = false;
                            try {
                                Writer out = new OutputStreamWriter(new FileOutputStream(dialog.getSaveFile()), "UTF-8");
                                g2d.stream(out, useCSS);
                            } catch (Exception e) {
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
                        JOptionPane.showMessageDialog(NetEditView.this, ResourceLoader.getString("save.image.error"),
                                ResourceLoader.getString("save.image.error.text"), JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            ActionHandler.getInstance().execute(action);
        }

        private SaveImageDialog getDialog() {
            if (dialog == null) {
                dialog = new SaveImageDialog((Frame) NetEditView.this.getTopLevelAncestor(), true);
                dialog.setSizeChangeEnabled(false);
            }
            return dialog;
        }
    }

    /** Creates new form NetEditView */
    public NetEditView() {
        newNodeAction = new NewNodeAction();
        editAction = new EditAction();
        editAction.setEnabled(false);
        deleteAction = new DeleteAction();
        deleteAction.setEnabled(false);
        saveNetImageAction = new SaveNetImageAction(ResourceLoader.getString("net.edit.save.image") + " ...");
        saveNetImageAction.setEnabled(false);
        zoomInAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (graphComponent != null)
                    graphComponent.zoomIn();
            }
        };
        zoomOutAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (graphComponent != null)
                    graphComponent.zoomOut();
            }
        };

        initComponents();
        initializeListeners();
    }

    private void initializeListeners() {
        // add net edit model to net view
        netEditModel = new NetSelectionModel();
        netEditModel.addNetSelectionListener(this);
    }

    private void initializeDialogs() {
        // initialize dialogs
        editNodeDialog = new EditNodeDialog((Frame) this.getTopLevelAncestor());
        editLineDialog = new EditLineDialog((Frame) this.getTopLevelAncestor(), true);
    }

    /**
     * @param model
     *            model to be set
     */
    public void setModel(ApplicationModel model) {
        this.initializeDialogs();
        this.model = model;
        editLineDialog.setModel(model);
        model.addListener(this);
        updateActions(model);
        setNet(model);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        panel = new JPanel();
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        add(panel, BorderLayout.SOUTH);
        panel.setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel();
        panel.add(BorderLayout.WEST, buttonPanel);
        GridBagLayout layoutButtonPanel = new GridBagLayout();
        buttonPanel.setLayout(layoutButtonPanel);
        final JToggleButton newNodeButton = GuiComponentUtils.createToggleButton(GuiIcon.ADD_DOC, 2);
        newNodeButton.setEnabled(false);
        newNodeButton.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (insertHandler != null)
                    insertHandler.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
            }
        });
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.gridx = 1;
        gbc.gridy = 0;
        buttonPanel.add(newNodeButton, gbc);
        final JToggleButton newLineButton = GuiComponentUtils.createToggleButton(GuiIcon.CONNECT, 2);
        newLineButton.setEnabled(false);
        newLineButton.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (graphComponent != null)
                    graphComponent.getConnectionHandler().setEnabled(e.getStateChange() == ItemEvent.SELECTED);
            }
        });
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.gridx = 2;
        gbc.gridy = 0;
        buttonPanel.add(newLineButton, gbc);
        final JToggleButton selectionButton = GuiComponentUtils.createToggleButton(GuiIcon.SELECT, 2);
        selectionButton.setEnabled(false);
        selectionButton.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (selectionHandler != null)
                    selectionHandler.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
            }
        });
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        buttonPanel.add(selectionButton, gbc);
        ButtonGroup bg = new ButtonGroup();
        bg.add(newNodeButton);
        bg.add(newLineButton);
        bg.add(selectionButton);
        selectionButton.setSelected(true);
        controls.add(newNodeButton);
        controls.add(newLineButton);
        controls.add(selectionButton);
        javax.swing.JButton editButton = GuiComponentUtils.createButton(GuiIcon.EDIT, 2, editAction);
        GridBagConstraints gbc_2 = new GridBagConstraints();
        gbc_2.fill = GridBagConstraints.HORIZONTAL;
        gbc_2.anchor = GridBagConstraints.NORTH;
        gbc_2.gridx = 0;
        gbc_2.gridy = 1;
        buttonPanel.add(editButton, gbc_2);
        javax.swing.JButton zoomIn = GuiComponentUtils.createButton(GuiIcon.ZOOM_IN, 2, zoomInAction);
        GridBagConstraints gbc_3 = new GridBagConstraints();
        gbc_3.fill = GridBagConstraints.HORIZONTAL;
        gbc_3.anchor = GridBagConstraints.NORTH;
        gbc_3.gridx = 0;
        gbc_3.gridy = 2;
        buttonPanel.add(zoomIn, gbc_3);
        javax.swing.JButton saveNetImageButton = new javax.swing.JButton();
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 4;
        gbc.insets = new Insets(10, 0, 0, 0);
        buttonPanel.add(saveNetImageButton, gbc);
        saveNetImageButton.setAction(saveNetImageAction);
        javax.swing.JButton deleteButton = GuiComponentUtils.createButton(GuiIcon.REMOVE, 2, deleteAction);
        GridBagConstraints gbc_1 = new GridBagConstraints();
        gbc_1.fill = GridBagConstraints.HORIZONTAL;
        gbc_1.anchor = GridBagConstraints.NORTH;
        gbc_1.gridx = 1;
        gbc_1.gridy = 1;
        buttonPanel.add(deleteButton, gbc_1);
        javax.swing.JButton zoomOut = GuiComponentUtils.createButton(GuiIcon.ZOOM_OUT, 2, zoomOutAction);
        GridBagConstraints gbc_4 = new GridBagConstraints();
        gbc_4.fill = GridBagConstraints.HORIZONTAL;
        gbc_4.anchor = GridBagConstraints.NORTH;
        gbc_4.gridx = 1;
        gbc_4.gridy = 2;
        buttonPanel.add(zoomOut, gbc_4);
    }

    @Override
    public void selection(NetSelectionModel.Action action, Node node, Line line) {
        switch (action) {
            case LINE_SELECTED:
            case NODE_SELECTED:
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
            updateActions(event.getModel());
        }
        switch (event.getType()) {
            case SET_DIAGRAM_CHANGED:
                if (model.getDiagram() != null) {
                    this.setNet(model);
                    model.getDiagram().addListener(this);
                }
                break;
            case MODIFIED_NODE:
                // redraw node
                this.updateNode((Node) event.getObject());
                break;
            case MODIFIED_LINE:
                // redraw line
                this.updateLine((Line) event.getObject());
                break;
            default:
                break;
        }
    }

    private void updateNode(Node node) {
        graph.cellLabelChanged(graph.getVertexToCellMap().get(node), node, true);
    }

    private void updateLine(Line line) {
        graph.cellLabelChanged(graph.getEdgeToCellMap().get(line), line, true);
    }

    private void setNet(ApplicationModel model) {
        if (model.getDiagram() != null) {
            this.setNet(model.getDiagram().getNet());
        } else {
            this.setNet((Net) null);
        }
    }

    private void setNet(Net net) {
        boolean isNet = net != null;
        if (graphComponent != null) {
            this.remove(graphComponent);
            this.panel.remove(graphOutline);
        }
        for (JComponent c : controls) {
            c.setEnabled(isNet);
        }
        graphComponent = null;
        graph = null;
        if (net == null)
            return;

        // TODO set states of handlers according to buttons ...
        graph = new NetGraphAdapter((ListenableGraph<Node, Line>) net.getGraph(), model);
        graph.setConnectableEdges(false);
        graph.setAllowDanglingEdges(false);
        graph.setEdgeLabelsMovable(false);
        graph.setDisconnectOnMove(false);
        graph.setAutoSizeCells(true);
        graph.setDropEnabled(false);
        graph.setAllowNegativeCoordinates(false);
        graph.setCellsResizable(false);
        graph.setCellsCloneable(false);
        graph.getSelectionModel().addListener(mxEvent.CHANGE, netEditModel);
        graph.setGridEnabled(false);
        graph.setCellsDisconnectable(false);
        graph.setDisconnectOnMove(false);

        graphComponent = new NetGraphComponent(graph);
        graphComponent.setDoubleBuffered(false);
        graphComponent.setDragEnabled(false);
        graphComponent.getViewport().setOpaque(true);
        graphComponent.getViewport().setBackground(Color.WHITE);
        graphComponent.setPageBackgroundColor(panel.getBackground());
        graphComponent.getConnectionHandler().setHandleEnabled(false);
        graphComponent.getConnectionHandler().setEnabled(false);

        insertHandler = new NodeInsertHandler(graphComponent, newNodeAction);
        selectionHandler = new mxRubberband(graphComponent);

        this.add(graphComponent, BorderLayout.CENTER);

        new mxKeyboardHandler(graphComponent) {

            @Override
            protected InputMap getInputMap(int condition) {
                InputMap map = null;

                if (condition == JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT) {
                    map = (InputMap) UIManager.get("ScrollPane.ancestorInputMap");
                } else if (condition == JComponent.WHEN_FOCUSED) {
                    map = new InputMap();

                    map.put(KeyStroke.getKeyStroke("ENTER"), "edit");
                    map.put(KeyStroke.getKeyStroke("DELETE"), "delete");
                    map.put(KeyStroke.getKeyStroke("ADD"), "zoomIn");
                    map.put(KeyStroke.getKeyStroke("SUBTRACT"), "zoomOut");
                    map.put(KeyStroke.getKeyStroke("control A"), "selectAll");
                    map.put(KeyStroke.getKeyStroke("control D"), "selectNone");
                }

                return map;
            }

            @Override
            protected ActionMap createActionMap() {
                ActionMap map = (ActionMap) UIManager.get("ScrollPane.actionMap");

                map.put("edit", editAction);
                map.put("delete", deleteAction);
                map.put("zoomIn", mxGraphActions.getZoomInAction());
                map.put("zoomOut", mxGraphActions.getZoomOutAction());
                map.put("selectNone", mxGraphActions.getSelectNoneAction());
                map.put("selectAll", mxGraphActions.getSelectAllAction());

                return map;
            }
        };

        graphComponent.setCellEditor(new mxICellEditor() {

            private mxCell cell;

            @Override
            public void stopEditing(boolean cancel) {
                cell = null;
            }

            @Override
            public void startEditing(Object cell, EventObject trigger) {
                this.cell = (mxCell) cell;
                if (trigger instanceof MouseEvent) {
                    editAction.actionPerformed(new ActionEvent(graphComponent, 0, null));
                    this.cell = null;
                }
            }

            @Override
            public Object getEditingCell() {
                return cell;
            }
        });

        mxConnectionHandler connectionHandler = graphComponent.getConnectionHandler();
        mxCellMarker marker = connectionHandler.getMarker();
        marker.setHotspot(1.0);
        connectionHandler.setConnectPreview(new mxConnectPreview(graphComponent) {
            @Override
            protected Object createCell(mxCellState startState, String style) {
                mxCell cell = (mxCell) super.createCell(startState, style);
                if (graph.getModel().isEdge(cell)) {
                    cell.setStyle((style == null || "".equals(style) ? "" : style + ";")
                            + "endArrow=none;startArrow=none");
                }
                return cell;
            }

            @Override
            public Object stop(boolean commit, MouseEvent e) {
                Object result = super.stop(commit, e);
                if (commit && result instanceof mxCell && ((mxCell) result).isEdge()) {
                    // remove the added cell for edge and create new line,
                    // creating new edge by callback
                    mxCell cell = (mxCell) result;
                    Node srcNode = (Node) ((NodeCell) cell.getSource()).getValue();
                    Node dstNode = (Node) ((NodeCell) cell.getTarget()).getValue();
                    Line l = model.getDiagram().createLine(IdGenerator.getInstance().getId(), 1000, srcNode, dstNode,
                            Line.UNLIMITED_SPEED);
                    LineTrack track = new LineTrack(IdGenerator.getInstance().getId(), "1");
                    l.addTrack(track);
                    model.getDiagram().getNet().addLine(srcNode, dstNode, l);

                    model.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.NEW_LINE, model, l));

                    graph.removeCells(new Object[] { result });
                }
                return result;
            }
        });

        graphOutline = graphComponent.createOutline();
        panel.add(BorderLayout.CENTER, graphOutline);

        graph.addListener(mxEvent.CELLS_MOVED, this);
        graph.getModel().beginUpdate();
        try {
            for (Node node : net.getNodes()) {
                mxCell cell = graph.getVertexToCellMap().get(node);
                graph.moveCells(new Object[] { cell }, node.getPositionX(), node.getPositionY());
            }
        } finally {
            graph.getModel().endUpdate();
        }
        panel.validate();
        validate();
    }

    private void updateActions(ApplicationModel model) {
        boolean isDiagram = model != null ? model.getDiagram() != null : false;
        newNodeAction.setEnabled(isDiagram);
        saveNetImageAction.setEnabled(isDiagram);
    }

    @Override
    public void trainDiagramChanged(TrainDiagramEvent event) {
        switch (event.getType()) {
            case ROUTE_ADDED:
            case ROUTE_REMOVED:
                Route route = (Route) event.getObject();
                for (RouteSegment seg : route.getSegments()) {
                    if (seg.asLine() != null) {
                        this.updateLine(seg.asLine());
                    }
                }
            default:
                break;
        }
    }

    @Override
    public void invoke(Object sender, mxEventObject evt) {
        if (mxEvent.CELLS_MOVED.equals(evt.getName())) {
            Object[] cells = (Object[]) evt.getProperty("cells");
            if (cells != null) {
                for (Object cell : cells) {
                    mxCell mxCell = (mxCell) cell;
                    if (mxCell.getValue() instanceof Node) {
                        Node node = (Node) mxCell.getValue();
                        node.setPositionX((int) (mxCell.getGeometry().getX()));
                        node.setPositionY((int) (mxCell.getGeometry().getY()));
                    }
                }
            }
        }
    }
}
