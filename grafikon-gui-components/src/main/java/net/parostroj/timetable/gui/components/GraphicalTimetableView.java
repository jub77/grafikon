package net.parostroj.timetable.gui.components;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import javax.swing.*;

import net.parostroj.timetable.gui.components.GTViewSettings.Key;
import net.parostroj.timetable.gui.components.GTViewSettings.Selection;
import net.parostroj.timetable.gui.components.GTViewSettings.TrainColors;
import net.parostroj.timetable.gui.components.GTViewSettings.Type;
import net.parostroj.timetable.gui.dialogs.EditRoutesDialog;
import net.parostroj.timetable.gui.dialogs.GTViewZoomDialog;
import net.parostroj.timetable.gui.dialogs.RouteSelectionDialog;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.visitors.AbstractEventVisitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Graphical timetable view.
 *
 * @author jub
 */
public class GraphicalTimetableView extends javax.swing.JPanel implements Scrollable {

    private static final Logger LOG = LoggerFactory.getLogger(GraphicalTimetableView.class.getName());

    static {
        ToolTipManager.sharedInstance().setReshowDelay(0);
        ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
    }

    public static interface RSListener {
        public void routeSelected(Route route);
    }

    private interface ToolTipHelper {

        public List<TrainsCycleItem> getEngineCycles(TimeInterval interval);
        public List<TrainsCycleItem> getTrainUnitCycles(TimeInterval interval);
        public List<TrainsCycleItem> getDriverCycles(TimeInterval interval);
    }

    private static class RouteRadioButtonMenuItem extends JRadioButtonMenuItem {

        private final Route route;

        public RouteRadioButtonMenuItem(Wrapper<Route> routeWrapper) {
            super(routeWrapper.toString());
            this.route = routeWrapper.getElement();
        }

        public Route getRoute() {
            return route;
        }
    }

    private final static int MIN_WIDTH = 1000;
    private final static int MAX_WIDTH = 10000;
    private final static int WIDTH_STEPS = 10;
    private final static int INITIAL_WIDTH = 4;
    private final static int SELECTION_RADIUS = 5;
    private final static int ROUTE_COUNT = 20;
    private final static int WIDTH_TO_HEIGHT_RATIO = 5;

    protected GTViewSettings settings;

    private GTDraw draw;
    private TrainRegionCollector trainRegionCollector;
    private TrainSelector trainSelector;
    private Route route;
    private EditRoutesDialog editRoutesDialog;
    private TrainDiagram diagram;
    private RSListener rsListener;

    private TextTemplate toolTipTemplateLine;
    private TextTemplate toolTipTemplateNode;
    private TimeInterval lastToolTipInterval;
    private final Map<String, Object> toolTipformattingMap = new HashMap<String, Object>();
    private Dimension preferredSize = new Dimension(MIN_WIDTH, MIN_WIDTH / WIDTH_TO_HEIGHT_RATIO);

    private JMenuItem routesMenuItem;

    /** Creates new form TrainGraphicalTimetableView */
    public GraphicalTimetableView() {
        initComponents();

        this.settings = this.getDefaultViewSettings();

        this.setGTWidth(settings);
        this.setBackground(Color.WHITE);

        this.addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                resize();
            }
        });

        trainRegionCollector = new TrainRegionCollector(SELECTION_RADIUS);

        this.addSizesToMenu();

        // tool tips
        ToolTipManager.sharedInstance().registerComponent(this);
        try {
            toolTipTemplateLine = TextTemplate.createTextTemplate(ResourceLoader.getString("gt.desc.interval.line"), TextTemplate.Language.GROOVY);
            toolTipTemplateNode = TextTemplate.createTextTemplate(ResourceLoader.getString("gt.desc.interval.node"), TextTemplate.Language.GROOVY);
        } catch (GrafikonException e) {
            LOG.error("Error creating template for time interval.", e);
        }
        toolTipformattingMap.put("helper", new ToolTipHelper() {

            @Override
            public List<TrainsCycleItem> getEngineCycles(TimeInterval interval) {
                return interval.getTrain().getCycleItemsForInterval(TrainsCycleType.ENGINE_CYCLE, interval);
            }

            @Override
            public List<TrainsCycleItem> getTrainUnitCycles(TimeInterval interval) {
                return interval.getTrain().getCycleItemsForInterval(TrainsCycleType.TRAIN_UNIT_CYCLE, interval);
            }

            @Override
            public List<TrainsCycleItem> getDriverCycles(TimeInterval interval) {
                return interval.getTrain().getCycleItemsForInterval(TrainsCycleType.DRIVER_CYCLE, interval);
            }
        });
        routesMenuItem = new JMenuItem(ResourceLoader.getString("gt.routes") + "...");
        routesMenu.add(routesMenuItem);
        routesMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // show list of routes
                RouteSelectionDialog dialog = new RouteSelectionDialog((Frame)GraphicalTimetableView.this.getTopLevelAncestor(), true);
                dialog.setLocationRelativeTo(GraphicalTimetableView.this.getParent());
                dialog.setListValues(diagram.getRoutes(), getRoute());
                dialog.setListener(new RouteSelectionDialog.RSListener() {
                    @Override
                    public void routeSelected(Route route) {
                        // set selected route
                        setRoute(route);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    private EditRoutesDialog getRouteDialog() {
        if (editRoutesDialog == null)
            editRoutesDialog = new EditRoutesDialog((Window)this.getTopLevelAncestor(), true);
        return editRoutesDialog;
    }

    private TrainDiagramListener currentListener;

    public void setTrainDiagram(TrainDiagram diagram) {
        if (currentListener != null && this.diagram != null) {
            this.diagram.removeListener(currentListener);
        }
        if (diagram == null) {
            this.diagram = null;
            this.currentListener = null;
            this.setRoute(null);
        } else {
            this.diagram = diagram;
            this.createMenuForRoutes(diagram.getRoutes());
            this.setComponentPopupMenu(popupMenu);
            this.currentListener = new TrainDiagramVisitEventListener(new AbstractEventVisitor() {

                @Override
                public void visit(TrainDiagramEvent event) {
                    switch (event.getType()) {
                        case ROUTE_ADDED: case ROUTE_REMOVED:
                            routesChanged(event);
                            break;
                        case TRAIN_ADDED:
                            if (trainRegionCollector != null)
                                trainRegionCollector.newTrain((Train)event.getObject());
                            repaint();
                            break;
                        case TRAIN_REMOVED:
                            if (trainRegionCollector != null)
                                trainRegionCollector.deleteTrain((Train)event.getObject());
                            draw.removedTrain((Train) event.getObject());
                            repaint();
                            break;
                        case ATTRIBUTE:
                            String name = event.getAttributeChange().getName();
                            if (TrainDiagram.ATTR_FROM_TIME.equals(name) || TrainDiagram.ATTR_TO_TIME.equals(name)) {
                                setTimeRange();
                                setGTWidth(settings);
                                recreateDraw();
                            }
                            if (TrainDiagram.ATTR_TRAIN_NAME_TEMPLATE.equals(name) ||
                                    TrainDiagram.ATTR_TRAIN_COMPLETE_NAME_TEMPLATE.equals(name))
                                repaint();
                            break;
                        default:
                            break;
                    }
                }

                @Override
                public void visit(TrainEvent event) {
                    trainChanged(event);
                }

                @Override
                public void visit(LineEvent event) {
                    lineChanged(event);
                }

                @Override
                public void visit(TrainTypeEvent event) {
                    trainTypeChanged(event);
                }

                @Override
                public void visit(NodeEvent event) {
                    nodeChanged(event);
                }
            });
            this.diagram.addListenerWithNested(this.currentListener);
            this.setTimeRange();
            this.setGTWidth(settings);
            if (diagram.getRoutes().size() > 0) {
                this.setRoute(diagram.getRoutes().get(0));
            } else {
                this.setRoute(null);
            }
        }
    }

    protected GTViewSettings getDefaultViewSettings() {
        GTViewSettings config = (new GTViewSettings())
                .set(Key.BORDER_X, 1.5f)
                .set(Key.BORDER_Y, 1.5f)
                .set(Key.STATION_GAP_X, 15)
                .set(Key.TYPE, Type.CLASSIC)
                .set(Key.TRAIN_COLORS, TrainColors.BY_TYPE)
                .set(Key.SELECTION, Selection.TRAIN)
                .set(Key.TRAIN_NAMES, Boolean.TRUE)
                .set(Key.ARRIVAL_DEPARTURE_DIGITS, Boolean.FALSE)
                .set(Key.EXTENDED_LINES, Boolean.FALSE)
                .set(Key.TECHNOLOGICAL_TIME, Boolean.FALSE)
                .set(Key.IGNORE_TIME_LIMITS, Boolean.FALSE)
                .set(Key.VIEW_SIZE, INITIAL_WIDTH)
                .set(Key.ZOOM, 1.0f);
        return config;
    }

    private void routesChanged(TrainDiagramEvent event) {
        // changed list of routes
        this.createMenuForRoutes(diagram.getRoutes());
        // check current route
        if (event.getType() == GTEventType.ROUTE_REMOVED && event.getObject().equals(this.getRoute())) {
            if (!diagram.getRoutes().isEmpty())
                this.setRoute(diagram.getRoutes().get(0));
            else
                this.setRoute(null);
        }
        if (event.getType() == GTEventType.ROUTE_ADDED && this.getRoute() == null) {
            this.setRoute((Route)event.getObject());
        }
    }

    private void trainChanged(TrainEvent event) {
        switch (event.getType()) {
            case TIME_INTERVAL_LIST: case TECHNOLOGICAL:
                if (trainRegionCollector != null)
                    trainRegionCollector.modifiedTrain(event.getSource());
                this.repaint();
                break;
            case ATTRIBUTE:
                if (event.getAttributeChange().getName().equals("number") || event.getAttributeChange().getName().equals("type")) {
                    draw.changedTextTrain(event.getSource());
                    this.repaint();
                }
                break;
            default:
                break;
        }
    }

    private void lineChanged(LineEvent event) {
        switch (event.getType()) {
            case TRACK_ATTRIBUTE:
                if (this.getRoute() != null && this.getRoute().contains(event.getSource())) {
                    // redraw all
                    recreateDraw();
                }
                break;
            default:
                break;
        }
    }

    private void nodeChanged(NodeEvent event) {
        switch (event.getType()) {
            case ATTRIBUTE:
                if (event.getAttributeChange().getName().equals("name")) {
                    draw.changedTextNode(event.getSource());
                }
                break;
            default:
                break;
        }
    }

    private void trainTypeChanged(TrainTypeEvent event) {
        switch (event.getType()) {
            case ATTRIBUTE:
                if (event.getAttributeChange().getName().equals("color") || event.getAttributeChange().getName().equals("trainNameTemplate"))
                    draw.changedTextAllTrains();
                    // repaint
                    this.repaint();
                break;
            default:
                break;
        }
    }

    private void setGTWidth(GTViewSettings config) {
        Integer start = null;
        Integer end = null;
        int size = config.get(Key.VIEW_SIZE, Integer.class);
        if (!config.getOption(Key.IGNORE_TIME_LIMITS)) {
            start = config.get(Key.START_TIME, Integer.class);
            end = config.get(Key.END_TIME, Integer.class);
        }
        if (start == null)
            start = 0;
        if (end == null)
            end = TimeInterval.DAY;
        double ratio = (double)(end - start) / TimeInterval.DAY;
        int newWidth = MIN_WIDTH + (size - 1) * ((MAX_WIDTH - MIN_WIDTH) / (WIDTH_STEPS - 1));
        newWidth = (int) (newWidth * ratio);
        int newHeight = newWidth / WIDTH_TO_HEIGHT_RATIO;
        preferredSize = new Dimension(newWidth, newHeight);
        this.revalidate();
    }

    public void setTrainSelector(TrainSelector trainSelector) {
        this.trainSelector = trainSelector;
    }

    public void setRoute(Route route) {
        if (this.route == route)
            return;
        this.route = route;
        this.recreateDraw();
        this.activateRouteMenuItem(route);
        if (rsListener != null) {
            rsListener.routeSelected(route);
        }
    }

    /**
     * @return the rsListener
     */
    public RSListener getRsListener() {
        return rsListener;
    }

    /**
     * @param rsListener the rsListener to set
     */
    public void setRsListener(RSListener rsListener) {
        this.rsListener = rsListener;
    }

    private void setTimeRange() {
        if (diagram == null) {
            settings.remove(Key.START_TIME);
            settings.remove(Key.END_TIME);
        } else {
            Integer from = (Integer) diagram.getAttribute(TrainDiagram.ATTR_FROM_TIME);
            Integer to = (Integer) diagram.getAttribute(TrainDiagram.ATTR_TO_TIME);
            if (from != null)
                settings.set(Key.START_TIME, from);
            else
                settings.remove(Key.START_TIME);
            if (to != null)
                settings.set(Key.END_TIME, to);
            else
                settings.remove(Key.END_TIME);
        }
    }

    private void recreateDraw() {
        Route drawnRoute = this.getRoute();
        if (drawnRoute == null) {
            draw = null;
        } else {
            trainRegionCollector.clear();
            GTViewSettings config = this.getSettings();
            config.set(GTViewSettings.Key.SIZE, this.getSize());
            if (settings.get(Key.TYPE) == Type.CLASSIC) {
                draw = new GTDrawClassic(config, drawnRoute, trainRegionCollector);
            } else if (settings.get(Key.TYPE) == Type.WITH_TRACKS) {
                draw = new GTDrawWithNodeTracks(config, drawnRoute, trainRegionCollector);
            }
        }
        this.repaint();
    }

    private void resize() {
        recreateDraw();
        trainRegionCollector.clear();
        this.repaint();
    }

    public Route getRoute() {
        return this.route;
    }

    protected TrainDiagram getDiagram() {
        return diagram;
    }

    /**
     * returns copy of settings.
     *
     * @return copy of settings
     */
    public GTViewSettings getSettings() {
        return new GTViewSettings(settings);
    }

    /**
     * @return settings
     */
    GTViewSettings getSettingsInternal() {
        return settings;
    }

    public void setSettings(GTViewSettings settings) {
        if (settings == null)
            return;

        this.settings = settings;

        switch (settings.get(Key.TYPE, Type.class)) {
            case CLASSIC:
                classicMenuItem.setSelected(true);
                break;
            case WITH_TRACKS:
                withTracksMenuItem.setSelected(true);
                break;
        }

        String sizeStr = Integer.toString(settings.get(Key.VIEW_SIZE, Integer.class));
        for (Object elem : sizesMenu.getMenuComponents()) {
            if (elem instanceof JRadioButtonMenuItem) {
                JRadioButtonMenuItem item = (JRadioButtonMenuItem)elem;
                if (item.getActionCommand().equals(sizeStr)) {
                    item.setSelected(true);
                    break;
                }
            }
        }
        this.setTimeRange();
        this.setGTWidth(settings);
        trainNamesCheckBoxMenuItem.setSelected(settings.getOption(GTViewSettings.Key.TRAIN_NAMES));
        addigitsCheckBoxMenuItem.setSelected(settings.getOption(GTViewSettings.Key.ARRIVAL_DEPARTURE_DIGITS));
        techTimeCheckBoxMenuItem.setSelected(settings.getOption(GTViewSettings.Key.TECHNOLOGICAL_TIME));
        extendedLinesCheckBoxMenuItem.setSelected(settings.getOption(GTViewSettings.Key.EXTENDED_LINES));
        ignoreTimeLimitsCheckBoxMenuItem.setSelected(settings.getOption(Key.IGNORE_TIME_LIMITS));
        this.recreateDraw();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        popupMenu = new javax.swing.JPopupMenu();
        routesMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem routesEditMenuItem = new javax.swing.JMenuItem();
        javax.swing.JSeparator jSeparator1 = new javax.swing.JSeparator();
        javax.swing.JMenu typesMenu = new javax.swing.JMenu();
        classicMenuItem = new javax.swing.JRadioButtonMenuItem();
        withTracksMenuItem = new javax.swing.JRadioButtonMenuItem();
        sizesMenu = new javax.swing.JMenu();
        javax.swing.JMenu preferencesMenu = new javax.swing.JMenu();
        addigitsCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        extendedLinesCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        trainNamesCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        techTimeCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        ignoreTimeLimitsCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        javax.swing.ButtonGroup typesButtonGroup = new javax.swing.ButtonGroup();
        routesGroup = new javax.swing.ButtonGroup();

        routesMenu.setText(ResourceLoader.getString("gt.routes")); // NOI18N
        popupMenu.add(routesMenu);

        routesEditMenuItem.setText(ResourceLoader.getString("gt.routes.edit")); // NOI18N
        routesEditMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                routesEditMenuItemActionPerformed(evt);
            }
        });
        popupMenu.add(routesEditMenuItem);
        popupMenu.add(jSeparator1);

        typesMenu.setText(ResourceLoader.getString("gt.type")); // NOI18N

        typesButtonGroup.add(classicMenuItem);
        classicMenuItem.setSelected(true);
        classicMenuItem.setText(ResourceLoader.getString("gt.classic")); // NOI18N
        classicMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                classicMenuItemActionPerformed(evt);
            }
        });
        typesMenu.add(classicMenuItem);

        typesButtonGroup.add(withTracksMenuItem);
        withTracksMenuItem.setText(ResourceLoader.getString("gt.withtracks")); // NOI18N
        withTracksMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                withTracksMenuItemActionPerformed(evt);
            }
        });
        typesMenu.add(withTracksMenuItem);

        popupMenu.add(typesMenu);

        sizesMenu.setText(ResourceLoader.getString("gt.sizes")); // NOI18N
        popupMenu.add(sizesMenu);

        preferencesMenu.setText(ResourceLoader.getString("gt.preferences")); // NOI18N

        addigitsCheckBoxMenuItem.setText(ResourceLoader.getString("gt.addigits")); // NOI18N
        addigitsCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                preferencesCheckBoxMenuItemActionPerformed(evt);
            }
        });
        preferencesMenu.add(addigitsCheckBoxMenuItem);

        extendedLinesCheckBoxMenuItem.setText(ResourceLoader.getString("gt.extendedlines")); // NOI18N
        extendedLinesCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                preferencesCheckBoxMenuItemActionPerformed(evt);
            }
        });
        preferencesMenu.add(extendedLinesCheckBoxMenuItem);

        trainNamesCheckBoxMenuItem.setSelected(true);
        trainNamesCheckBoxMenuItem.setText(ResourceLoader.getString("gt.trainnames")); // NOI18N
        trainNamesCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                preferencesCheckBoxMenuItemActionPerformed(evt);
            }
        });
        preferencesMenu.add(trainNamesCheckBoxMenuItem);

        techTimeCheckBoxMenuItem.setText(ResourceLoader.getString("gt.technological.time")); // NOI18N
        techTimeCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                preferencesCheckBoxMenuItemActionPerformed(evt);
            }
        });
        preferencesMenu.add(techTimeCheckBoxMenuItem);

        ignoreTimeLimitsCheckBoxMenuItem.setText(ResourceLoader.getString("gt.ignore.time.limits")); // NOI18N
        ignoreTimeLimitsCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                preferencesCheckBoxMenuItemActionPerformed(evt);
            }
        });
        preferencesMenu.add(ignoreTimeLimitsCheckBoxMenuItem);

        popupMenu.add(preferencesMenu);

        javax.swing.JMenuItem zoomMenuItem = new javax.swing.JMenuItem(ResourceLoader.getString("gt.zoom") + "...");
        zoomMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // select zoom
                GTViewZoomDialog dialog = new GTViewZoomDialog((Window) getTopLevelAncestor(), true);
                dialog.setLocationRelativeTo(getParent());
                Float oldZoom = settings.get(Key.ZOOM, Float.class);
                Float newZoom = dialog.showDialog(oldZoom);
                if (newZoom != null && newZoom.floatValue() != oldZoom.floatValue()) {
                    settings.set(Key.ZOOM, newZoom);
                    recreateDraw();
                }
            }
        });
        popupMenu.add(zoomMenuItem);

        setDoubleBuffered(false);
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });
        setLayout(null);
    }// </editor-fold>//GEN-END:initComponents

    private void withTracksMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_withTracksMenuItemActionPerformed
        this.settings.set(Key.TYPE, Type.WITH_TRACKS);
        this.recreateDraw();
    }//GEN-LAST:event_withTracksMenuItemActionPerformed

    private void classicMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_classicMenuItemActionPerformed
        this.settings.set(Key.TYPE, Type.CLASSIC);
        this.recreateDraw();
    }//GEN-LAST:event_classicMenuItemActionPerformed

    private void routesEditMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_routesEditMenuItemActionPerformed
        if (diagram == null) {
            return;
        }
        getRouteDialog().setLocationRelativeTo(this.getParent());
        getRouteDialog().showDialog(diagram);
    }//GEN-LAST:event_routesEditMenuItemActionPerformed

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        // selection of the train
        if (trainRegionCollector != null) {
            List<TimeInterval> selectedIntervals = trainRegionCollector.getTrainForPoint(evt.getX(), evt.getY());
            if (trainSelector != null) {
                if (selectedIntervals.isEmpty())
                    trainSelector.selectTrainInterval(null);
                else {
                    TimeInterval oldInterval = trainSelector.getSelectedTrainInterval();
                    if (oldInterval == null)
                        trainSelector.selectTrainInterval(selectedIntervals.get(0));
                    else {
                        TimeInterval newSelection = this.getNextSelected(selectedIntervals, oldInterval);
                        trainSelector.selectTrainInterval(newSelection);
                    }
                }
            }
        }
    }//GEN-LAST:event_formMouseClicked

    @Override
    public String getToolTipText(MouseEvent event) {
        if (trainRegionCollector == null)
            return null;
        List<TimeInterval> intervals = trainRegionCollector.getTrainForPoint(event.getX(), event.getY());

        if (lastToolTipInterval == null) {
            if (!intervals.isEmpty())
                lastToolTipInterval = intervals.get(0);
        } else {
            lastToolTipInterval = null;
        }
        return lastToolTipInterval !=null ? this.formatTimeInterval() : null;
    }

    private String formatTimeInterval() {
        toolTipformattingMap.put("interval", lastToolTipInterval);
        if (lastToolTipInterval.isLineOwner())
            return toolTipTemplateLine.evaluate(toolTipformattingMap);
        else
            return toolTipTemplateNode.evaluate(toolTipformattingMap);
    }

    private TimeInterval getNextSelected(List<TimeInterval> list, TimeInterval oldInterval) {
        int oldIndex = list.indexOf(oldInterval);
        if (oldIndex == -1)
            return list.get(0);
        else {
            Selection selection = settings.get(Key.SELECTION, Selection.class);
            if (selection == Selection.INTERVAL) {
                oldIndex += 1;
                if (oldIndex >= list.size())
                    oldIndex = 0;
                return list.get(oldIndex);
            } else {
                int newIndex = oldIndex;
                Train oldTrain = oldInterval.getTrain();
                Train selectedTrain = oldTrain;
                do {
                    newIndex++;
                    if (newIndex >= list.size())
                        newIndex = 0;
                    selectedTrain = list.get(newIndex).getTrain();
                } while (selectedTrain == oldTrain && newIndex != oldIndex);
                return list.get(newIndex);
            }
        }
    }

    private void preferencesCheckBoxMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_preferencesCheckBoxMenuItemActionPerformed
        settings.set(Key.ARRIVAL_DEPARTURE_DIGITS, addigitsCheckBoxMenuItem.isSelected());
        settings.set(Key.EXTENDED_LINES, extendedLinesCheckBoxMenuItem.isSelected());
        settings.set(Key.TECHNOLOGICAL_TIME, techTimeCheckBoxMenuItem.isSelected());
        settings.set(Key.IGNORE_TIME_LIMITS, ignoreTimeLimitsCheckBoxMenuItem.isSelected());
        settings.set(Key.TRAIN_NAMES, trainNamesCheckBoxMenuItem.isSelected());
        if (evt.getSource() == ignoreTimeLimitsCheckBoxMenuItem) {
            this.setTimeRange();
            this.setGTWidth(settings);
        }
        // recreate draw
        this.recreateDraw();
    }//GEN-LAST:event_preferencesCheckBoxMenuItemActionPerformed

    @Override
    public void paint(Graphics g) {
        long time = System.currentTimeMillis();
        super.paint(g);

        if (draw != null)
            draw.draw((Graphics2D)g);
        else {
            // draw information about context menu
            g.drawString(ResourceLoader.getString("gt.contextmenu.info"), 20, 20);
        }
        LOG.trace("Finished paint in {}ms", Long.toString(System.currentTimeMillis() - time));
    }

    private void createMenuForRoutes(List<Route> routes) {
        routesGroup = new ButtonGroup();
        routesMenu.removeAll();
        // sort routes
        routes = new ArrayList<Route>(routes);
        Collections.sort(routes, new Comparator<Route>() {
            @Override
            public int compare(Route o1, Route o2) {
                return o1.toString().compareTo(o2.toString());
            }
        });
        int i = 0;
        for (Route lRoute : routes) {
            if (i++ >= ROUTE_COUNT) {
                // add item for list of routes
                routesMenu.add(new javax.swing.JSeparator());
                routesMenu.add(routesMenuItem);
                break;
            }
            RouteRadioButtonMenuItem item = new RouteRadioButtonMenuItem(Wrapper.getWrapper(lRoute));
            routesMenu.add(item);
            routesGroup.add(item);
            if (lRoute == this.getRoute())
                item.setSelected(true);
            item.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    RouteRadioButtonMenuItem button = (RouteRadioButtonMenuItem)e.getSource();
                    setRoute(button.getRoute());
                }
            });
        }
    }

    private void activateRouteMenuItem(Route route) {
        routesGroup.clearSelection();
        for (int i =0; i < routesMenu.getItemCount(); i++) {
            JMenuItem item = routesMenu.getItem(i);
            if (item instanceof RouteRadioButtonMenuItem) {
                RouteRadioButtonMenuItem rItem = (RouteRadioButtonMenuItem)item;
                if (rItem.getRoute().equals(route))
                    rItem.setSelected(true);
            }
        }
    }

    private void addSizesToMenu() {
        ButtonGroup group = new ButtonGroup();
        for (int i = 1; i <= WIDTH_STEPS; i++) {
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(Integer.toString(i));
            item.setActionCommand(Integer.toString(i));
            int currentSize = settings.get(Key.VIEW_SIZE, Integer.class);
            if (i == currentSize)
                item.setSelected(true);
            item.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int size = Integer.parseInt(e.getActionCommand());
                    settings.set(Key.VIEW_SIZE, size);
                    setGTWidth(settings);
                    recreateDraw();
                }
            });
            group.add(item);
            sizesMenu.add(item);
        }

    }

    public GTDraw getGtDraw() {
        return draw;
    }

    public void setDisableStationNames(Boolean disable) {
        settings.setOption(Key.DISABLE_STATION_NAMES, disable);
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension pSize = new Dimension(preferredSize);
        if (getParent() instanceof JViewport) {
            JViewport viewport = (JViewport) getParent();
            Dimension eSize = viewport.getExtentSize();
            pSize.height = eSize.height;
            if (eSize.width > pSize.width)
                pSize.width = eSize.width;
        }
        return pSize;
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        int value = 0;
        if (orientation == SwingConstants.VERTICAL) {
            value = visibleRect.height / 10;
        } else {
            value = visibleRect.width / 10;
        }
        return value;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        int value = 0;
        if (orientation == SwingConstants.VERTICAL) {
            value = visibleRect.height;
        } else {
            value = visibleRect.width;
        }
        return value;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBoxMenuItem addigitsCheckBoxMenuItem;
    private javax.swing.JRadioButtonMenuItem classicMenuItem;
    private javax.swing.JCheckBoxMenuItem extendedLinesCheckBoxMenuItem;
    private javax.swing.JCheckBoxMenuItem ignoreTimeLimitsCheckBoxMenuItem;
    protected javax.swing.JPopupMenu popupMenu;
    private javax.swing.ButtonGroup routesGroup;
    private javax.swing.JMenu routesMenu;
    private javax.swing.JMenu sizesMenu;
    private javax.swing.JCheckBoxMenuItem techTimeCheckBoxMenuItem;
    private javax.swing.JCheckBoxMenuItem trainNamesCheckBoxMenuItem;
    private javax.swing.JRadioButtonMenuItem withTracksMenuItem;
    // End of variables declaration//GEN-END:variables
}
