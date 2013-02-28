/*
 * NetEditView.java
 *
 * Created on 30.11.2008, 14:48:51
 */
package net.parostroj.timetable.gui.views;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.ApplicationModelEvent;
import net.parostroj.timetable.gui.ApplicationModelEventType;
import net.parostroj.timetable.gui.ApplicationModelListener;
import net.parostroj.timetable.gui.actions.execution.ActionContext;
import net.parostroj.timetable.gui.actions.execution.ActionHandler;
import net.parostroj.timetable.gui.actions.execution.EventDispatchAfterModelAction;
import net.parostroj.timetable.gui.actions.execution.ModelAction;
import net.parostroj.timetable.gui.dialogs.EditLineDialog;
import net.parostroj.timetable.gui.dialogs.EditNodeDialog;
import net.parostroj.timetable.gui.dialogs.SaveImageDialog;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.events.TrainDiagramEvent;
import net.parostroj.timetable.model.events.TrainDiagramListener;
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
import com.mxgraph.swing.handler.mxConnectPreview;
import com.mxgraph.swing.handler.mxConnectionHandler;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.view.mxCellState;

/**
 * View for editing net.
 *
 * @author jub
 */
public class NetEditView extends javax.swing.JPanel implements NetSelectionModel.NetSelectionListener, ApplicationModelListener, TrainDiagramListener, mxIEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(NetEditView.class);

    private ApplicationModel model;
    private NetSelectionModel netEditModel;

    private EditNodeDialog editNodeDialog;
    private EditLineDialog editLineDialog;

    private Action newNodeAction;
    private Action editAction;
    private Action deleteAction;
    private Action saveNetImageAction;
    private Action zoomInAction;
    private Action zoomOutAction;

    private NetGraphAdapter graph;
    private NetGraphComponent graphComponent;
    private JPanel panel;


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
                            BufferedImage img = new BufferedImage(saveSize.width, saveSize.height, BufferedImage.TYPE_INT_RGB);
                            Graphics2D g2d = img.createGraphics();
                            g2d.setColor(Color.white);
                            g2d.fillRect(0, 0, saveSize.width, saveSize.height);

                            graphComponent.getViewport().getView().paint(g2d);

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

                            graphComponent.getViewport().getView().paint(g2d);

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
        newNodeAction = new NewNodeAction("*");
        editAction = new EditAction(ResourceLoader.getString("button.edit") + " ...");
        editAction.setEnabled(false);
        deleteAction = new DeleteAction(ResourceLoader.getString("button.delete"));
        deleteAction.setEnabled(false);
        saveNetImageAction = new SaveNetImageAction(ResourceLoader.getString("net.edit.save.image") + " ...");
        saveNetImageAction.setEnabled(false);
        zoomInAction = new AbstractAction("+") {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (graphComponent != null)
					graphComponent.zoomIn();
			}
		};
		zoomOutAction = new AbstractAction("-") {
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
        editNodeDialog = new EditNodeDialog((Frame)this.getTopLevelAncestor());
        editLineDialog = new EditLineDialog((Frame)this.getTopLevelAncestor(), true);
    }

    /**
     * @param model model to be set
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
        add(panel, BorderLayout.SOUTH);
        panel.setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel();
        panel.add(BorderLayout.WEST, buttonPanel);
        GridBagLayout layoutButtonPanel = new GridBagLayout();
        buttonPanel.setLayout(layoutButtonPanel);
        javax.swing.JButton newNodeButton = new javax.swing.JButton();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        buttonPanel.add(newNodeButton, gbc);
        newNodeButton.setAction(newNodeAction);
        javax.swing.JButton editButton = new javax.swing.JButton();
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = 0;
        gbc.gridy = 1;
        buttonPanel.add(editButton, gbc);
        editButton.setAction(editAction);
        javax.swing.JButton zoomIn = new javax.swing.JButton(zoomInAction);
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = 0;
        gbc.gridy = 2;
        buttonPanel.add(zoomIn, gbc);
        javax.swing.JButton saveNetImageButton = new javax.swing.JButton();
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        buttonPanel.add(saveNetImageButton, gbc);
        saveNetImageButton.setAction(saveNetImageAction);
        javax.swing.JButton deleteButton = new javax.swing.JButton();
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = 1;
        gbc.gridy = 1;
        buttonPanel.add(deleteButton, gbc);
        deleteButton.setAction(deleteAction);
        javax.swing.JButton zoomOut = new javax.swing.JButton(zoomOutAction);
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = 1;
        gbc.gridy = 2;
        buttonPanel.add(zoomOut, gbc);
    }

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
            this.updateNode((Node)event.getObject());
            break;
        case MODIFIED_LINE:
            // redraw line
            this.updateLine((Line)event.getObject());
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
    	if (graphComponent != null)
    		this.remove(graphComponent);
        graphComponent = null;
        graph = null;
        if (net == null)
            return;

        graph = new NetGraphAdapter((ListenableGraph<Node, Line>) net.getGraph(), model);
        graph.setCellsEditable(false);
        graph.setConnectableEdges(false);
        graph.setAllowDanglingEdges(false);
        graph.setEdgeLabelsMovable(false);
        graph.getSelectionModel().setSingleSelection(true);
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
        graphComponent.setPanning(true);
        graphComponent.setPageBackgroundColor(panel.getBackground());
        graphComponent.getConnectionHandler().setHandleEnabled(true);

        this.add(graphComponent, BorderLayout.CENTER);

        mxConnectionHandler connectionHandler = graphComponent.getConnectionHandler();
        connectionHandler.setConnectPreview(new mxConnectPreview(graphComponent) {
        	@Override
        	protected Object createCell(mxCellState startState, String style) {
        		mxCell cell = (mxCell) super.createCell(startState, style);
        		if (graph.getModel().isEdge(cell)) {
        			cell.setStyle((style == null || "".equals(style) ? "" : style + ";") + "endArrow=none;startArrow=none");
        		}
        		return cell;
        	}

        	@Override
        	public Object stop(boolean commit, MouseEvent e) {
        		Object result = super.stop(commit, e);
        		if (commit && result instanceof mxCell && ((mxCell) result).isEdge()) {
        			// remove the added cell for edge and create new line, creating new edge by callback
					mxCell cell = (mxCell) result;
					Node srcNode = (Node) ((NodeCell) cell.getSource()).getValue();
					Node dstNode = (Node) ((NodeCell) cell.getTarget()).getValue();
	                Line l = model.getDiagram().createLine(IdGenerator.getInstance().getId(), 1000, srcNode, dstNode, Line.UNLIMITED_SPEED);
	                LineTrack track = new LineTrack(IdGenerator.getInstance().getId(), "1");
	                l.addTrack(track);
					model.getDiagram().getNet().addLine(srcNode, dstNode, l);

	                model.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.NEW_LINE, model, l));

	                graph.removeCells(new Object[] { result });
				}
				return result;
        	}
        });

        mxGraphOutline outline = graphComponent.createOutline();
        panel.add(BorderLayout.CENTER, outline);

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
                Route route = (Route)event.getObject();
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
		    			node.setPositionX((int) (node.getPositionX() + (Double) evt.getProperty("dx")));
		    			node.setPositionY((int) (node.getPositionY() + (Double) evt.getProperty("dy")));
	    			}
	    		}
	    	}
    	}
    }
}
