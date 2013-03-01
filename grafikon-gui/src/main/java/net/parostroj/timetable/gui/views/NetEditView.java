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
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.EventObject;

import javax.imageio.ImageIO;
import javax.swing.*;

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
import com.mxgraph.swing.handler.mxKeyboardHandler;
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
	private mxGraphOutline graphOutline;
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
                            } catch (Exception e) {
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
        editAction = new EditAction("o");
        editAction.setEnabled(false);
        deleteAction = new DeleteAction("x");
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
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        buttonPanel.add(newNodeButton, gbc);
        newNodeButton.setAction(newNodeAction);
        javax.swing.JButton editButton = new javax.swing.JButton();
        GridBagConstraints gbc_2 = new GridBagConstraints();
        gbc_2.fill = GridBagConstraints.HORIZONTAL;
        gbc_2.anchor = GridBagConstraints.NORTH;
        gbc_2.gridx = 0;
        gbc_2.gridy = 1;
        buttonPanel.add(editButton, gbc_2);
        editButton.setAction(editAction);
        javax.swing.JButton zoomIn = new javax.swing.JButton(zoomInAction);
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
        gbc.gridwidth = 3;
        buttonPanel.add(saveNetImageButton, gbc);
        saveNetImageButton.setAction(saveNetImageAction);
        javax.swing.JButton deleteButton = new javax.swing.JButton();
        GridBagConstraints gbc_1 = new GridBagConstraints();
        gbc_1.fill = GridBagConstraints.HORIZONTAL;
        gbc_1.anchor = GridBagConstraints.NORTH;
        gbc_1.gridx = 1;
        gbc_1.gridy = 1;
        buttonPanel.add(deleteButton, gbc_1);
        deleteButton.setAction(deleteAction);
        javax.swing.JButton zoomOut = new javax.swing.JButton(zoomOutAction);
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
    	if (graphComponent != null) {
    		this.remove(graphComponent);
    		this.panel.remove(graphOutline);
    	}
        graphComponent = null;
        graph = null;
        if (net == null)
            return;

        graph = new NetGraphAdapter((ListenableGraph<Node, Line>) net.getGraph(), model);
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

        new mxKeyboardHandler(graphComponent) {

        	@Override
			protected InputMap getInputMap(int condition)
        	{
        		InputMap map = null;

        		if (condition == JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
        		{
        			map = (InputMap) UIManager.get("ScrollPane.ancestorInputMap");
        		}
        		else if (condition == JComponent.WHEN_FOCUSED)
        		{
        			map = new InputMap();

        			map.put(KeyStroke.getKeyStroke("ENTER"), "edit");
        			map.put(KeyStroke.getKeyStroke("DELETE"), "delete");
        			map.put(KeyStroke.getKeyStroke("RIGHT"), "selectNext");
        			map.put(KeyStroke.getKeyStroke("LEFT"), "selectPrevious");
        			map.put(KeyStroke.getKeyStroke("ADD"), "zoomIn");
        			map.put(KeyStroke.getKeyStroke("SUBTRACT"), "zoomOut");
        		}

        		return map;
        	}

        	@Override
			protected ActionMap createActionMap()
        	{
        		ActionMap map = (ActionMap) UIManager.get("ScrollPane.actionMap");

        		map.put("edit", editAction);
        		map.put("delete", deleteAction);
        		map.put("selectNext", mxGraphActions.getSelectNextAction());
        		map.put("selectPrevious", mxGraphActions.getSelectPreviousAction());
        		map.put("zoomIn", mxGraphActions.getZoomInAction());
        		map.put("zoomOut", mxGraphActions.getZoomOutAction());

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
		    			node.setPositionX((int) (mxCell.getGeometry().getX()));
		    			node.setPositionY((int) (mxCell.getGeometry().getY()));
	    			}
	    		}
	    	}
    	}
    }
}
