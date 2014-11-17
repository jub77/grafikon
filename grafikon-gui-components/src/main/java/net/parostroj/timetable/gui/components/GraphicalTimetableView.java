package net.parostroj.timetable.gui.components;

import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.*;

import javax.swing.*;

import net.parostroj.timetable.gui.components.GTViewSettings.Key;
import net.parostroj.timetable.gui.dialogs.EditRoutesDialog;
import net.parostroj.timetable.gui.dialogs.GTViewZoomDialog;
import net.parostroj.timetable.gui.dialogs.RouteSelectionDialog;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.events.TrainDiagramEvent;
import net.parostroj.timetable.output2.gt.*;
import net.parostroj.timetable.output2.gt.GTDraw.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Graphical timetable view - with interaction.
 *
 * @author jub
 */
public class GraphicalTimetableView extends GraphicalTimetableViewDraw  {

    private static final Logger log = LoggerFactory.getLogger(GraphicalTimetableView.class);

    private final static int INITIAL_WIDTH = 4;
    private final static int SELECTION_RADIUS = 5;
    private final static int WIDTH_STEPS = 10;
    private final static int ROUTE_COUNT = 20;
    private final static int MIN_WIDTH = 1000;
    private final static int MAX_WIDTH = 10000;
    private final static int WIDTH_TO_HEIGHT_RATIO = 5;

    static {
        ToolTipManager.sharedInstance().setReshowDelay(0);
        ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
    }

    public static interface GTViewListener {
        public void routeSelected(Route route);
        public void diagramChanged(TrainDiagram diagram);
        public void settingsChanged(GTViewSettings settings);
    }

    private interface ToolTipHelper {
        public Collection<TrainsCycleItem> getEngineCycles(TimeInterval interval);
        public Collection<TrainsCycleItem> getTrainUnitCycles(TimeInterval interval);
        public Collection<TrainsCycleItem> getDriverCycles(TimeInterval interval);
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

    private List<GTViewListener> listeners;

    private TextTemplate toolTipTemplateLine;
    private TextTemplate toolTipTemplateNode;
    private TimeInterval lastToolTipInterval;
    private final Map<String, Object> toolTipformattingMap = new HashMap<String, Object>();

    private Dimension preferredSize = new Dimension(MIN_WIDTH, MIN_WIDTH / WIDTH_TO_HEIGHT_RATIO);

    public GraphicalTimetableView() {
        this.initComponents();

        this.listeners = new ArrayList<GTViewListener>();

        this.addSizesToMenu();
        // binding for tool tips
        toolTipformattingMap.put("helper", new ToolTipHelper() {

            @Override
            public Collection<TrainsCycleItem> getEngineCycles(TimeInterval interval) {
                return interval.getTrain().getCycleItemsForInterval(diagram.getEngineCycleType(), interval);
            }

            @Override
            public Collection<TrainsCycleItem> getTrainUnitCycles(TimeInterval interval) {
                return interval.getTrain().getCycleItemsForInterval(diagram.getTrainUnitCycleType(), interval);
            }

            @Override
            public Collection<TrainsCycleItem> getDriverCycles(TimeInterval interval) {
                return interval.getTrain().getCycleItemsForInterval(diagram.getDriverCycleType(), interval);
            }
        });
        // tool tips
        ToolTipManager.sharedInstance().registerComponent(this);
        try {
            toolTipTemplateLine = TextTemplate.createTextTemplate(ResourceLoader.getString("gt.desc.interval.line"), TextTemplate.Language.GROOVY);
            toolTipTemplateNode = TextTemplate.createTextTemplate(ResourceLoader.getString("gt.desc.interval.node"), TextTemplate.Language.GROOVY);
        } catch (GrafikonException e) {
            log.error("Error creating template for time interval.", e);
        }
        // routes menu
        routesMenuItem = new JMenuItem(ResourceLoader.getString("gt.routes") + "...");
        routesMenu.add(routesMenuItem);
        routesMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // show list of routes
                RouteSelectionDialog dialog = new RouteSelectionDialog((Window) GraphicalTimetableView.this.getTopLevelAncestor(), true);
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
        // set size
        this.setGTWidth(settings);
    }

    @Override
    public String getToolTipText(MouseEvent event) {
        if (trainRegionCollector == null)
            return null;
        List<TimeInterval> intervals = trainRegionCollector.getItemsForPoint(event.getX(), event.getY(), SELECTION_RADIUS);

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
        if (lastToolTipInterval.isLineOwner()) {
            return toolTipTemplateLine.evaluate(toolTipformattingMap);
        } else {
            return toolTipTemplateNode.evaluate(toolTipformattingMap);
        }
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

    private void addOrientationToMenu(boolean add) {
        orientationMenu.setVisible(add);
    }

    protected void setGTWidth(GTViewSettings config) {
        Integer start = null;
        Integer end = null;
        int size = config.get(Key.VIEW_SIZE, Integer.class);
        if (!config.getOption(Key.IGNORE_TIME_LIMITS)) {
            start = config.get(Key.START_TIME, Integer.class);
            end = config.get(Key.END_TIME, Integer.class);
        }
        if (start == null) {
            start = 0;
        }
        if (end == null) {
            end = TimeInterval.DAY;
        }
        double ratio = (double)(end - start) / TimeInterval.DAY;
        int newWidth = MIN_WIDTH + (size - 1) * ((MAX_WIDTH - MIN_WIDTH) / (WIDTH_STEPS - 1));
        newWidth = (int) (newWidth * ratio);
        int newHeight = newWidth / WIDTH_TO_HEIGHT_RATIO;
        switch (config.get(Key.ORIENTATION, GTOrientation.class)) {
            case LEFT_RIGHT:
                preferredSize = new Dimension(newWidth, newHeight);
                break;
            case TOP_DOWN:
                preferredSize = new Dimension(newHeight, newWidth);
                break;
        }
        this.revalidate();
    }

    @Override
    public void setTrainDiagram(TrainDiagram diagram) {
        super.setTrainDiagram(diagram);
        if (diagram == null) {
            // do nothing
        } else {
            this.createMenuForRoutes(diagram.getRoutes());
            this.setComponentPopupMenu(popupMenu);
        }
        this.fireDiagramChanged(diagram);
    }

    private void initComponents() {
        popupMenu = new javax.swing.JPopupMenu();
        routesMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem routesEditMenuItem = new javax.swing.JMenuItem();
        javax.swing.JSeparator jSeparator1 = new javax.swing.JSeparator();
        typesMenu = new SelectionMenu<GTDraw.Type>();
        sizesMenu = new javax.swing.JMenu();
        orientationMenu = new SelectionMenu<GTOrientation>();
        preferencesMenu = new ChoicesMenu<Key>();
        routesGroup = new javax.swing.ButtonGroup();

        routesMenu.setText(ResourceLoader.getString("gt.routes")); // NOI18N
        popupMenu.add(routesMenu);

        routesEditMenuItem.setText(ResourceLoader.getString("gt.routes.edit")); // NOI18N
        routesEditMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (diagram == null) {
                    return;
                }
                getRouteDialog().setLocationRelativeTo(GraphicalTimetableView.this.getParent());
                getRouteDialog().showDialog(diagram);
            }
        });
        popupMenu.add(routesEditMenuItem);
        popupMenu.add(jSeparator1);

        typesMenu.setText(ResourceLoader.getString("gt.type")); // NOI18N

        typesMenu.addItem(ResourceLoader.getString("gt.classic"), GTDraw.Type.CLASSIC); // NOI18N
        typesMenu.addItem(ResourceLoader.getString("gt.classic.station.stops"), GTDraw.Type.CLASSIC_STATION_STOPS); // NOI18N
        typesMenu.addItem(ResourceLoader.getString("gt.withtracks"), GTDraw.Type.WITH_TRACKS); // NOI18N
        typesMenu.setSelectedItem(GTDraw.Type.CLASSIC);
        typesMenu.addListener(new SelectionMenu.Listener<GTDraw.Type>() {
            @Override
            public void selected(Type value) {
                settings.set(Key.TYPE, value);
                recreateDraw();
            }
        });

        popupMenu.add(typesMenu);

        sizesMenu.setText(ResourceLoader.getString("gt.sizes")); // NOI18N
        popupMenu.add(sizesMenu);

        orientationMenu.setText(ResourceLoader.getString("gt.orientation")); // NOI18N
        orientationMenu.addItem(ResourceLoader.getString("gt.orientation.left.right"), GTOrientation.LEFT_RIGHT); // NOI18N
        orientationMenu.addItem(ResourceLoader.getString("gt.orientation.top.down"), GTOrientation.TOP_DOWN); // NOI18N
        orientationMenu.setSelectedItem(GTOrientation.LEFT_RIGHT);
        orientationMenu.addListener(new SelectionMenu.Listener<GTOrientation>() {
            @Override
            public void selected(GTOrientation value) {
                settings.set(Key.ORIENTATION, value);
                setSettings(settings);
            }
        });
        popupMenu.add(orientationMenu);
        orientationMenu.setVisible(false);

        preferencesMenu.setText(ResourceLoader.getString("gt.preferences")); // NOI18N

        preferencesMenu.addItem(ResourceLoader.getString("gt.addigits"), Key.ARRIVAL_DEPARTURE_DIGITS); // NOI18N
        preferencesMenu.addItem(ResourceLoader.getString("gt.extendedlines"), Key.EXTENDED_LINES); // NOI18N
        preferencesMenu.addItem(ResourceLoader.getString("gt.trainnames"), Key.TRAIN_NAMES); // NOI18N
        preferencesMenu.addItem(ResourceLoader.getString("gt.technological.time"), Key.TECHNOLOGICAL_TIME); // NOI18N
        preferencesMenu.addItem(ResourceLoader.getString("gt.ignore.time.limits"), Key.IGNORE_TIME_LIMITS); // NOI18N
        preferencesMenu.addItem(ResourceLoader.getString("gt.to.train.scroll"), Key.TO_TRAIN_SCROLL); // NOI18N
        preferencesMenu.addItem(ResourceLoader.getString("gt.to.train.change.route"), Key.TO_TRAIN_CHANGE_ROUTE); // NOI18N

        preferencesMenu.addListener(new ChoicesMenu.Listener<Key>() {
            @Override
            public void changed(Key value, boolean selected) {
                settings.setOption(value, selected);
                if (value == Key.IGNORE_TIME_LIMITS) {
                    setTimeRange();
                }
                // recreate draw
                recreateDraw();
            }
        });

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
                if (!SwingUtilities.isLeftMouseButton(evt)) {
                    return;
                }
                // collector/selector
                boolean selected = false;
                for (RegionCollector<?> collector : gtStorage.collectors()) {
                    if (evt.getClickCount() % 2 == 0) {
                        // indicates double click
                        if (collector.editSelected()) {
                            return;
                        }
                    } else {
                        if (!selected) {
                            selected = collector.selectItems(evt.getX(), evt.getY(), SELECTION_RADIUS);
                        } else {
                            collector.deselectItems();
                        }
                    }
                }
            }
        });
        setLayout(null);
    }

    private EditRoutesDialog getRouteDialog() {
        if (editRoutesDialog == null)
            editRoutesDialog = new EditRoutesDialog((Window)this.getTopLevelAncestor(), true);
        return editRoutesDialog;
    }

    @Override
    public void setSettings(GTViewSettings settings) {
        super.setSettings(settings);

        typesMenu.setSelectedItem(settings.getGTDrawType(), true);

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

        this.setPreferencesValue(Key.TRAIN_NAMES, settings);
        this.setPreferencesValue(Key.ARRIVAL_DEPARTURE_DIGITS, settings);
        this.setPreferencesValue(Key.TECHNOLOGICAL_TIME, settings);
        this.setPreferencesValue(Key.EXTENDED_LINES, settings);
        this.setPreferencesValue(Key.IGNORE_TIME_LIMITS, settings);
        this.setPreferencesValue(Key.TO_TRAIN_SCROLL, settings);
        this.setPreferencesValue(Key.TO_TRAIN_CHANGE_ROUTE, settings);

        orientationMenu.setSelectedItem(settings.get(Key.ORIENTATION, GTOrientation.class), true);

        this.addOrientationToMenu(settings.isOption(Key.ORIENTATION_MENU));

        this.fireSettingChanged(settings);
    }

    private void setPreferencesValue(Key key, GTViewSettings settings) {
        preferencesMenu.setItemState(key, settings.getOption(key), true);
    }

    private void fireSettingChanged(GTViewSettings settings) {
        for (GTViewListener l : listeners) {
            l.settingsChanged(settings);
        }
    }

    private void fireRouteSelected(Route route) {
        for (GTViewListener listener : this.listeners) {
            listener.routeSelected(route);
        }
    }

    private void fireDiagramChanged(TrainDiagram diagram) {
        for (GTViewListener listener : listeners) {
            listener.diagramChanged(diagram);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension pSize = new Dimension(preferredSize);
        if (getParent() instanceof JViewport) {
            JViewport viewport = (JViewport) getParent();
            Dimension eSize = viewport.getExtentSize();
            GTOrientation orientation = settings.get(Key.ORIENTATION, GTOrientation.class);
            switch (orientation) {
                case LEFT_RIGHT:
                    pSize.height = eSize.height;
                    if (eSize.width > pSize.width) {
                        pSize.width = eSize.width;
                    }
                    break;
                case TOP_DOWN:
                    pSize.width = eSize.width;
                    if (eSize.height > pSize.height) {
                        pSize.height = eSize.height;
                    }
                    break;
            }
        }
        return pSize;
    }

    /**
     * @param listener the rsListener to set
     */
    public void addListener(GTViewListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void setRoute(Route route) {
        if (this.route == route) {
            return;
        }
        super.setRoute(route);
        this.activateRouteMenuItem(route);
        this.fireRouteSelected(route);
    }

    @Override
    protected void routesChanged(TrainDiagramEvent event) {
        // changed list of routes
        this.createMenuForRoutes(diagram.getRoutes());

        super.routesChanged(event);
    }

    @Override
    protected void setTimeRange() {
        super.setTimeRange();
        this.setGTWidth(settings);
    }

    @Override
    protected GTViewSettings getDefaultViewSettings() {
        GTViewSettings config = super.getDefaultViewSettings();
        config.set(Key.VIEW_SIZE, INITIAL_WIDTH);
        config.set(Key.TYPE, GTDraw.Type.CLASSIC);
        return config;
    }

    protected javax.swing.JPopupMenu popupMenu;
    private javax.swing.ButtonGroup routesGroup;
    private javax.swing.JMenu routesMenu;
    private javax.swing.JMenu sizesMenu;
    private SelectionMenu<Type> typesMenu;
    private SelectionMenu<GTOrientation> orientationMenu;
    private ChoicesMenu<Key> preferencesMenu;
    private final javax.swing.JMenuItem routesMenuItem;

    private EditRoutesDialog editRoutesDialog;
}
