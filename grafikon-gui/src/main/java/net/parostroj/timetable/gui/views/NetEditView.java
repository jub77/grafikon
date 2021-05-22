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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventObject;
import java.util.Optional;

import javax.imageio.ImageIO;
import javax.swing.*;

import net.parostroj.timetable.gui.*;
import net.parostroj.timetable.gui.actions.execution.*;
import net.parostroj.timetable.gui.dialogs.EditLineDialog;
import net.parostroj.timetable.gui.dialogs.EditNodeDialog;
import net.parostroj.timetable.gui.dialogs.SaveImageDialog;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.GuiIcon;
import net.parostroj.timetable.gui.utils.NetItemInfo;
import net.parostroj.timetable.gui.views.graph.*;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.events.Event;
import net.parostroj.timetable.model.units.LengthUnit;
import net.parostroj.timetable.model.units.SpeedUnit;
import net.parostroj.timetable.output2.net.NetGraphAdapter;
import net.parostroj.timetable.utils.*;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphics2D;
import org.jgrapht.ListenableGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphOutline;
import com.mxgraph.swing.handler.*;
import com.mxgraph.swing.util.mxGraphActions;
import com.mxgraph.swing.view.mxICellEditor;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.view.mxCellState;

/**
 * View for editing net.
 *
 * @author jub
 */
public class NetEditView extends javax.swing.JPanel implements NetSelectionModel.NetSelectionListener, mxIEventListener {

    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(NetEditView.class);

    private transient ApplicationModel model;
    private transient NetSelectionModel netEditModel;

    private transient NetItemInfo netItemInfo;

    private EditNodeDialog editNodeDialog;
    private EditLineDialog editLineDialog;

    private final transient Action newNodeAction;
    private final transient Action editAction;
    private final transient Action deleteAction;
    private final transient Action saveNetImageAction;
    private final transient Action zoomInAction;
    private final transient Action zoomOutAction;

    private transient NetGraphAdapter graph;
    private NetGraphComponent graphComponent;
    private mxGraphOutline graphOutline;
    private transient NodeInsertHandler insertHandler;
    private transient mxRubberband selectionHandler;
    private final Collection<JComponent> controls = new ArrayList<>();

    private JPanel outlinePanel;
    private JPanel rightPanel;
    private JPanel buttonPanel;

    private JLabel textArea;

    private int cnt = 0;

    public class NewNodeAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

		@Override
        public void actionPerformed(ActionEvent e) {
            if (model.getDiagram() != null) {
                String result = ResourceLoader.getString("node.station.text") + " " + (++cnt);
                Point location;
                if (e instanceof ActionEventWithLocation) {
                    location = ((ActionEventWithLocation) e).getLocation();
                } else {
                    location = new Point(20, 20);
                }
                TrainDiagramPartFactory factory = model.getDiagram().getPartFactory();
                Node n = factory.createNode(factory.createId());
                n.setType(NodeType.STATION);
                n.setName(result);
                n.setAbbr(result.substring(0, 1) + cnt);
                NodeTrack track = new NodeTrack(factory.createId(), n, "1");
                track.setPlatform(true);
                n.getTracks().add(track);
                n.setLocation(new Location(location.x, location.y));
                // connectors and switches
                TrackConnector lc = factory.createDefaultConnector(factory.createId(), n, "1", Node.Side.LEFT, Optional.of(track));
                TrackConnector rc = factory.createDefaultConnector(factory.createId(), n, "2", Node.Side.RIGHT, Optional.of(track));
                n.getConnectors().add(lc);
                n.getConnectors().add(rc);
                model.getDiagram().getNet().addNode(n);
            }
        }
    }

    public class EditAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

		@Override
        public void actionPerformed(ActionEvent e) {
            Collection<Object> objects = netEditModel.getSelectedObjects();
            if (!objects.isEmpty()) {
                Object selected = objects.iterator().next();
                // edit line
                if (selected instanceof Line) {
                    Line selectedLine = (Line) selected;
                    editLineDialog.setLocationRelativeTo(NetEditView.this);
                    editLineDialog.showDialog(selectedLine, model.getProgramSettings().getLengthUnit());
                }
                // edit node
                if (selected instanceof Node) {
                    Node selectedNode = (Node) selected;
                    editNodeDialog.setLocationRelativeTo(NetEditView.this);
                    editNodeDialog.showDialog(selectedNode,
                            model.getDiagram().getAttributes().get(TrainDiagram.ATTR_EDIT_LENGTH_UNIT, LengthUnit.class, model.getProgramSettings().getLengthUnit()),
                            model.getDiagram().getAttributes().get(TrainDiagram.ATTR_EDIT_SPEED_UNIT, SpeedUnit.class, SpeedUnit.KMPH));
                }
            }
        }
    }

    public class DeleteAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

		@Override
        public void actionPerformed(ActionEvent e) {
            Component comp = NetEditView.this;
            // delete all lines
            for (Object o : netEditModel.getSelectedObjects()) {
                if (o instanceof Line && !deleteLine(comp, (Line) o)) {
                    return;
                }
            }
            // delete all nodes
            for (Object o : netEditModel.getSelectedObjects()) {
                if (o instanceof Node && !deleteNode(comp, (Node) o)) {
                    return;
                }
            }
        }

        private boolean deleteNode(Component comp, Node selectedNode) {
            if (!selectedNode.isEmpty() || !model.getDiagram().getNet().getLinesOf(selectedNode).isEmpty()
                    || CheckingUtils.checkRoutesForNode(selectedNode, model.getDiagram().getRoutes())) {
                if (!selectedNode.isEmpty()) {
                    GuiComponentUtils.showError(ResourceLoader.getString("nl.error.notempty"), comp);
                } else if (!model.getDiagram().getNet().getLinesOf(selectedNode).isEmpty()) {
                    GuiComponentUtils.showError(ResourceLoader.getString("nl.error.linesexist"), comp);
                } else {
                    GuiComponentUtils.showError(ResourceLoader.getString("ne.error.routepart"), comp);
                }
                return false;
            } else {
                model.getDiagram().getNet().removeNode(selectedNode);
                return true;
            }
        }

        private boolean deleteLine(Component comp, Line selectedLine) {
            if (!selectedLine.isEmpty()
                    || CheckingUtils.checkRoutesForLine(selectedLine, model.getDiagram().getRoutes())) {
                if (!selectedLine.isEmpty()) {
                    GuiComponentUtils.showError(ResourceLoader.getString("nl.error.notempty"), comp);
                } else {
                    GuiComponentUtils.showError(ResourceLoader.getString("ne.error.routepart"), comp);
                }
                return false;
            } else {
                model.getDiagram().getNet().removeLine(selectedLine);
                return true;
            }
        }
    }

    public class SaveNetImageAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

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
                                log.warn("Error saving file: " + dialog.getSaveFile(), e);
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
                            try {
                                Writer out = new OutputStreamWriter(new FileOutputStream(dialog.getSaveFile()), StandardCharsets.UTF_8);
                                g2d.stream(out, false);
                            } catch (Exception e) {
                                log.warn("Error saving file: " + dialog.getSaveFile(), e);
                                error = true;
                            }
                        }
                    } finally {
                        log.debug("Image save finished in {}ms", System.currentTimeMillis() - time);
                        setWaitDialogVisible(false);
                    }
                }

                @Override
                protected void eventDispatchActionAfter() {
                    if (error) {
                        GuiComponentUtils.showError(ResourceLoader.getString("save.image.error.text"), NetEditView.this);
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

            private static final long serialVersionUID = 1L;

			@Override
            public void actionPerformed(ActionEvent e) {
                if (graphComponent != null)
                    graphComponent.zoomIn();
            }
        };
        zoomOutAction = new AbstractAction() {

            private static final long serialVersionUID = 1L;

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
        netItemInfo = new NetItemInfo(textArea, () -> model != null ? model.getProgramSettings() : null);
        netEditModel.addNetSelectionListener(netItemInfo);
    }

    private void initializeDialogs() {
        // initialize dialogs
        editNodeDialog = new EditNodeDialog((Frame) this.getTopLevelAncestor(), true);
        editLineDialog = new EditLineDialog((Frame) this.getTopLevelAncestor(), true);
    }

    /**
     * @param model
     *            model to be set
     */
    public void setModel(ApplicationModel model) {
        this.initializeDialogs();
        this.model = model;
        updateActions(model);
        setNet(model);
        model.getMediator().addColleague(new ApplicationGTEventColleague() {
            @Override
            public void processNodeEvent(Event event) {
                if (event.getType() == Event.Type.ATTRIBUTE) {
                    Node node = (Node) event.getSource();
                    updateNode(node);
                    if (event.getAttributeChange().checkName(Node.ATTR_LOCATION)) {
                        updateNodeLocation(node);
                    }
                }
            }

            @Override
            public void processLineEvent(Event event) {
                if (event.getType() == Event.Type.ATTRIBUTE || (event.getType() == Event.Type.ADDED ||
                        event.getType() == Event.Type.REMOVED) && event.getObject() instanceof Track) {
                    updateLine((Line) event.getSource());
                }
            }

            @Override
            public void processTrainDiagramEvent(Event event) {
                switch (event.getType()) {
                    case ADDED:
                    case REMOVED:
                        if (event.getObject() instanceof Route) {
                            Route route = (Route) event.getObject();
                            for (RouteSegment seg : route.getSegments()) {
                                if (seg instanceof Line) {
                                    updateLine((Line) seg);
                                }
                            }
                        }
                        break;
                    case OBJECT_ATTRIBUTE:
                        updateNodeForObjectCollectionAttribute(Region.class, Region.ATTR_NAME, Node.ATTR_REGIONS, event);
                        updateNodeForObjectAttribute(Company.class, Company.ATTR_ABBR, Node.ATTR_COMPANY, event);
                        break;
                    case ATTRIBUTE:
                        if (event.getAttributeChange().checkName(TrainDiagram.ATTR_EDIT_LENGTH_UNIT, TrainDiagram.ATTR_EDIT_SPEED_UNIT)) {
                            updateAll();
                        }
                        break;
                    default:
                        break;
                }
            }

            private <T> void updateNodeForObjectCollectionAttribute(Class<T> objectClass, String objectAttribute, String nodeAttribute, Event event) {
                if (objectClass.isInstance(event.getObject()) && event.getAttributeChange().checkName(objectAttribute)) {
                    T object = objectClass.cast(event.getObject());
                    for (Node node : ((TrainDiagram) event.getSource()).getNet().getNodes()) {
                        Collection<T> attribute = node.getAttributeAsCollection(nodeAttribute, objectClass);
                        if (attribute != null && attribute.contains(object)) {
                            updateNode(node);
                        }
                    }
                }
            }

            private <T> void updateNodeForObjectAttribute(Class<T> objectClass, String objectAttribute, String nodeAttribute, Event event) {
                if (objectClass.isInstance(event.getObject()) && event.getAttributeChange().checkName(objectAttribute)) {
                    T object = objectClass.cast(event.getObject());
                    for (Node node : ((TrainDiagram) event.getSource()).getNet().getNodes()) {
                        if (node.getAttribute(nodeAttribute, objectClass) == object) {
                            updateNode(node);
                        }
                    }
                }
            }

            @Override
            public void processNetEvent(Event event) {
                if (event.getType() == Event.Type.ADDED && event.getObject() instanceof Node) {
                    Node node = (Node) event.getObject();
                    updateNodeLocation(node);
                }
            }

            @Override
            public void processApplicationEvent(ApplicationModelEvent event) {
                switch (event.getType()) {
                    case SET_DIAGRAM_CHANGED:
                        netEditModel.clearSelection();
                        updateActions(event.getModel());
                        if (event.getModel().getDiagram() != null) {
                            setNet(event.getModel());

                        }
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(panel, BorderLayout.SOUTH);
        panel.setLayout(new BorderLayout());

        rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        panel.add(rightPanel, BorderLayout.CENTER);

        outlinePanel = new JPanel();
        outlinePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        outlinePanel.setLayout(new BorderLayout());
        rightPanel.add(outlinePanel, BorderLayout.WEST);

        buttonPanel = new JPanel();
        panel.add(BorderLayout.WEST, buttonPanel);
        GridBagLayout layoutButtonPanel = new GridBagLayout();
        buttonPanel.setLayout(layoutButtonPanel);
        final JToggleButton newNodeButton = GuiComponentUtils.createToggleButton(GuiIcon.ADD_DOC, 2);
        newNodeButton.setEnabled(false);
        newNodeButton.addItemListener(e -> {
            if (insertHandler != null)
                insertHandler.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
        });
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.gridx = 1;
        gbc.gridy = 0;
        buttonPanel.add(newNodeButton, gbc);
        final JToggleButton newLineButton = GuiComponentUtils.createToggleButton(GuiIcon.CONNECT, 2);
        newLineButton.setEnabled(false);
        newLineButton.addItemListener(e -> {
            if (graphComponent != null)
                graphComponent.getConnectionHandler().setEnabled(e.getStateChange() == ItemEvent.SELECTED);
        });
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.gridx = 2;
        gbc.gridy = 0;
        buttonPanel.add(newLineButton, gbc);
        final JToggleButton selectionButton = GuiComponentUtils.createToggleButton(GuiIcon.SELECT, 2);
        selectionButton.setEnabled(false);
        selectionButton.addItemListener(e -> {
            if (selectionHandler != null)
                selectionHandler.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
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
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.fill = GridBagConstraints.HORIZONTAL;
        gbc2.anchor = GridBagConstraints.NORTH;
        gbc2.gridx = 0;
        gbc2.gridy = 1;
        buttonPanel.add(editButton, gbc2);
        javax.swing.JButton zoomIn = GuiComponentUtils.createButton(GuiIcon.ZOOM_IN, 2, zoomInAction);
        GridBagConstraints gbc3 = new GridBagConstraints();
        gbc3.fill = GridBagConstraints.HORIZONTAL;
        gbc3.anchor = GridBagConstraints.NORTH;
        gbc3.gridx = 0;
        gbc3.gridy = 2;
        buttonPanel.add(zoomIn, gbc3);
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
        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.fill = GridBagConstraints.HORIZONTAL;
        gbc1.anchor = GridBagConstraints.NORTH;
        gbc1.gridx = 1;
        gbc1.gridy = 1;
        buttonPanel.add(deleteButton, gbc1);
        javax.swing.JButton zoomOut = GuiComponentUtils.createButton(GuiIcon.ZOOM_OUT, 2, zoomOutAction);
        GridBagConstraints gbc4 = new GridBagConstraints();
        gbc4.fill = GridBagConstraints.HORIZONTAL;
        gbc4.anchor = GridBagConstraints.NORTH;
        gbc4.gridx = 1;
        gbc4.gridy = 2;
        buttonPanel.add(zoomOut, gbc4);

        textArea = new JLabel();
        textArea.setVerticalAlignment(SwingConstants.TOP);
        // font the same as label
        rightPanel.add(textArea, BorderLayout.CENTER);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    @Override
    public void selection(Collection<Object> items) {
        editAction.setEnabled(items.size() == 1);
        deleteAction.setEnabled(!items.isEmpty());
    }

    private Object getIfSingleSelected() {
        Object[] cells = graph.getSelectionCells();
        return cells != null && cells.length == 1 ? ((mxCell) cells[0]).getValue() : null;
    }

    private void updateNode(Node node) {
        graph.cellLabelChanged(graph.getVertexToCellMap().get(node), node, true);
        netItemInfo.updateItem(getIfSingleSelected());
    }

    private void updateAll() {
        graph.refresh();
    }

    private void updateNodeLocation(Node node) {
        if (moveBlock) {
            return;
        }
        moveBlock = true;
        try {
            mxCell cell = graph.getVertexToCellMap().get(node);
            mxGeometry geometry = cell.getGeometry();
            graph.moveCells(new Object[] { cell }, node.getLocation().getX() - geometry.getX(), node.getLocation().getY() - geometry.getY());
        } finally {
            moveBlock = false;
        }
    }

    private void updateLine(Line line) {
        graph.cellLabelChanged(graph.getEdgeToCellMap().get(line), line, true);
        netItemInfo.updateItem(getIfSingleSelected());
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
            this.outlinePanel.remove(graphOutline);
        }
        for (JComponent c : controls) {
            c.setEnabled(isNet);
        }
        graphComponent = null;
        graph = null;
        if (net == null) {
            return;
        }

        // TODO set states of handlers according to buttons ...
        graph = new NetGraphAdapter((ListenableGraph<Node, Line>) net.getGraph(),
                model.getProgramSettings()::getLengthUnit,
                model.getProgramSettings()::getSpeedUnit);
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
        graph.setCellsDisconnectable(false);
        graph.setDisconnectOnMove(false);

        graphComponent = new NetGraphComponent(graph);
        graphComponent.setDoubleBuffered(false);
        graphComponent.setDragEnabled(false);
        graphComponent.getViewport().setOpaque(true);
        graphComponent.getViewport().setBackground(Color.WHITE);
        graphComponent.setPageBackgroundColor(outlinePanel.getBackground());
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
                    String checkedStyle = ObjectsUtil.checkAndTrim(style);
                    cell.setStyle((checkedStyle == null ? "" : checkedStyle + ";")
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
                    Node srcNode = (Node) cell.getSource().getValue();
                    Node dstNode = (Node) cell.getTarget().getValue();
                    TrainDiagramPartFactory factory = model.getDiagram().getPartFactory();
                    Line l = factory.createLine(factory.createId());
                    l.setLength(1000);
                    LineTrack track = new LineTrack(IdGenerator.getInstance().getId(), l, "1");
                    l.getTracks().add(track);
                    model.getDiagram().getNet().addLine(l, srcNode, dstNode);

                    // add to connectors
                    addToConnector(srcNode, factory, track, Node.Side.RIGHT, "2");
                    addToConnector(dstNode, factory, track, Node.Side.LEFT, "1");

                    graph.removeCells(new Object[] { result });
                }
                return result;
            }

            private void addToConnector(Node srcNode, TrainDiagramPartFactory factory,
                    LineTrack track, Node.Side side, String name) {
                Optional<TrackConnector> connWithoutLineTrack = srcNode.getConnectors()
                        .find(c -> !c.getLineTrack().isPresent());
                connWithoutLineTrack.orElseGet(() -> {
                    TrackConnector connector = factory.createDefaultConnector(
                            IdGenerator.getInstance().getId(), srcNode, name, side,
                            Optional.empty());
                    srcNode.getConnectors().add(connector);
                    return connector;
                }).setLineTrack(Optional.of(track));
            }
        });

        graphOutline = graphComponent.createOutline();
        outlinePanel.add(BorderLayout.CENTER, graphOutline);

        graph.addListener(mxEvent.CELLS_MOVED, this);
        graph.getModel().beginUpdate();
        try {
            for (Node node : net.getNodes()) {
                updateNodeLocation(node);
            }
        } finally {
            graph.getModel().endUpdate();
        }
        Dimension size = buttonPanel.getSize();
        size.width = size.width * 2;
        graphOutline.setPreferredSize(size);
        rightPanel.validate();
        validate();
    }

    private void updateActions(ApplicationModel model) {
        boolean isDiagram = model != null && model.getDiagram() != null;
        newNodeAction.setEnabled(isDiagram);
        saveNetImageAction.setEnabled(isDiagram);
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

                        int x = (int) (mxCell.getGeometry().getX());
                        int y = (int) (mxCell.getGeometry().getY());

                        if (moveBlock) {
                            continue;
                        }
                        moveBlock = true;
                        try {
                            node.setLocation(new Location(x, y));
                        } finally {
                            moveBlock = false;
                        }
                    }
                }
            }
        }
    }

    private boolean moveBlock = false;
}
