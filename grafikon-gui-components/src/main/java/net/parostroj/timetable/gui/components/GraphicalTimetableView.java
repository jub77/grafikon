package net.parostroj.timetable.gui.components;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.gui.components.GTViewSettings.Key;
import net.parostroj.timetable.gui.dialogs.EditRoutesDialog;
import net.parostroj.timetable.gui.dialogs.GTViewZoomDialog;
import net.parostroj.timetable.gui.dialogs.RouteSelectionDialog;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.model.GrafikonException;
import net.parostroj.timetable.model.Route;
import net.parostroj.timetable.model.TextTemplate;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainsCycleItem;
import net.parostroj.timetable.model.events.Event;
import net.parostroj.timetable.model.freight.FreightConnectionPath;
import net.parostroj.timetable.output2.gt.GTDraw;
import net.parostroj.timetable.output2.gt.GTOrientation;
import net.parostroj.timetable.output2.gt.RegionCollector;
import net.parostroj.timetable.output2.util.OutputFreightUtil;

/**
 * Graphical timetable view - with interaction.
 *
 * @author jub
 */
public class GraphicalTimetableView extends GraphicalTimetableViewDraw  {

    public static String MOUSE_OVER_HANDLER_KEY = "mouse.over.handler";

    private static final Logger log = LoggerFactory.getLogger(GraphicalTimetableView.class);

    private final static int SELECTION_RADIUS = 5;
    private final static int ROUTE_COUNT = 20;

    private enum MouseAction { ENTER, EXIT, MOVE }

    static {
        ToolTipManager.sharedInstance().setReshowDelay(0);
        ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
    }

    public interface GTViewListener {
        public void routeSelected(Route route);
        public void diagramChanged(TrainDiagram diagram);
        public void settingsChanged(GTViewSettings settings);
    }

    public interface MouseOverHandler {
        void mouseOverIntervals(Collection<TimeInterval> intervals);
    }

    private interface ToolTipHelper {
        public Collection<TrainsCycleItem> getEngineCycles(TimeInterval interval);
        public Collection<TrainsCycleItem> getTrainUnitCycles(TimeInterval interval);
        public Collection<TrainsCycleItem> getDriverCycles(TimeInterval interval);
        public Collection<String> getFreight(TimeInterval interval);
        public Map<Train, List<String>> getPassedFreight(TimeInterval interval);
    }

    private List<GTViewListener> listeners;

    private TextTemplate toolTipTemplateLine;
    private TextTemplate toolTipTemplateNode;
    private TimeInterval lastToolTipInterval;
    private final Map<String, Object> toolTipformattingMap = new HashMap<>();

    private OutputFreightUtil freightUtil = new OutputFreightUtil();

    public GraphicalTimetableView() {
        this.initComponents();

        this.listeners = new ArrayList<>();

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

            @Override
            public Collection<String> getFreight(TimeInterval interval) {
                if (interval.isNodeOwner() && interval.isFreightFrom()) {
                    TrainDiagram diagram = interval.getTrain().getDiagram();
                    List<FreightConnectionPath> fConns = diagram.getFreightNet().getFreightToNodes(interval);
                    return freightUtil.freightListToString(fConns, Locale.getDefault());
                } else {
                    return Collections.emptyList();
                }
            }

            @Override
            public Map<Train, List<String>> getPassedFreight(TimeInterval interval) {
                if (interval.isNodeOwner()) {
                    TrainDiagram diagram = interval.getTrain().getDiagram();
                    return diagram.getFreightNet().getFreightPassedInNode(interval).entrySet().stream()
                            .collect(Collectors.toMap(e -> e.getKey(),
                                    e -> freightUtil.freightListToString(e.getValue(), Locale.getDefault())));
                } else {
                    return Collections.emptyMap();
                }
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
        routesMenuItem = new JMenuItem(ResourceLoader.getString("gt.routes.select"));
        routesMenu.add(routesMenuItem);
        routesMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // show list of routes
                RouteSelectionDialog dialog = new RouteSelectionDialog((Window) GraphicalTimetableView.this.getTopLevelAncestor(), true);
                dialog.setLocationRelativeTo(GraphicalTimetableView.this.getParent());
                dialog.setListValues(diagram.getRoutes(), getRoute());
                dialog.setListener(route -> setRoute(route));
                dialog.setVisible(true);
            }
        });
    }

    @Override
    public String getToolTipText(MouseEvent event) {
        RegionCollector<TimeInterval> collector = getRegionCollector(TimeInterval.class);
        if (collector == null) {
            return null;
        }

        List<TimeInterval> intervals = collector.getItemsForPointRadiuses(event.getX(), event.getY(), 2, SELECTION_RADIUS);

        if (lastToolTipInterval == null) {
            if (!intervals.isEmpty()) {
                lastToolTipInterval = intervals.get(0);
            }
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

    private void createMenuForRoutes(Collection<Route> routesCollection) {
        // remove items
        routesMenu.removeAllItems();
        // remove rest
        routesMenu.removeAll();
        // sort routes
        List<Route> routes = new ArrayList<>(routesCollection);
        Collections.sort(routes, (o1, o2) -> o1.toString().compareTo(o2.toString()));
        int i = 0;
        for (Route lRoute : routes) {
            if (i++ >= ROUTE_COUNT) {
                // add item for list of routes
                routesMenu.add(new javax.swing.JSeparator());
                routesMenu.add(routesMenuItem);
                break;
            }
            Wrapper<Route> routeWrapper = Wrapper.getWrapper(lRoute);
            routesMenu.addItem(routeWrapper.toString(), lRoute);
        }
        routesMenu.setSelectedItem(this.getRoute());
    }

    private void addSizesToMenu() {
        for (int i = 1; i <= WIDTH_STEPS; i++) {
            sizesMenu.addItem(Integer.toString(i), i);
        }
        sizesMenu.setSelectedItem(settings.get(Key.VIEW_SIZE, Integer.class));
        sizesMenu.addListener(size -> {
            settings.set(Key.VIEW_SIZE, size);
            recreateDraw();
        });
    }

    private void addOrientationToMenu(boolean add) {
        orientationMenu.setVisible(add);
    }

    private void checkDrawType(GTViewSettings settings) {
        Collection<?> allowed = (Collection<?>) settings.get(GTViewSettings.Key.TYPE_LIST);
        GTDraw.Type type = settings.getGTDrawType();
        if (allowed != null && !allowed.isEmpty() && !allowed.contains(type)) {
            settings.set(GTViewSettings.Key.TYPE, allowed.iterator().next());
        }
    }

    private void updateTypesMenu(GTViewSettings settings) {
        final Collection<?> allowedTypes = (Collection<?>) settings.get(GTViewSettings.Key.TYPE_LIST);
        final GTDraw.Type selected = settings.getGTDrawType();
        if (allowedTypes == null || allowedTypes.isEmpty()) {
            typesMenu.setAllItemsVisible(true);
        } else {
            typesMenu.setAllItemsVisible(false);
            for (Object typeObject : allowedTypes) {
                GTDraw.Type type = (GTDraw.Type) typeObject;
                typesMenu.setItemVisible(type, true);
            }
        }
        typesMenu.setSelectedItem(selected, true);
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
        routesMenu = new SelectionMenu<>();
        javax.swing.JMenuItem routesEditMenuItem = new javax.swing.JMenuItem();
        javax.swing.JSeparator jSeparator1 = new javax.swing.JSeparator();
        typesMenu = new SelectionMenu<>();
        sizesMenu = new SelectionMenu<>();
        orientationMenu = new SelectionMenu<>();
        preferencesMenu = new ChoicesMenu<>();

        routesMenu.setText(ResourceLoader.getString("gt.routes")); // NOI18N
        popupMenu.add(routesMenu);

        routesEditMenuItem.setText(ResourceLoader.getString("gt.routes.edit")); // NOI18N
        routesEditMenuItem.addActionListener(evt -> {
            if (diagram == null) {
                return;
            }
            getRouteDialog().setLocationRelativeTo(GraphicalTimetableView.this.getParent());
            getRouteDialog().showDialog(diagram);
        });
        popupMenu.add(routesEditMenuItem);
        popupMenu.add(jSeparator1);

        typesMenu.setText(ResourceLoader.getString("gt.type")); // NOI18N

        typesMenu.addItem(ResourceLoader.getString("gt.classic"), GTDraw.Type.CLASSIC); // NOI18N
        typesMenu.addItem(ResourceLoader.getString("gt.classic.station.stops"), GTDraw.Type.CLASSIC_STATION_STOPS); // NOI18N
        typesMenu.addItem(ResourceLoader.getString("gt.withtracks"), GTDraw.Type.WITH_TRACKS); // NOI18N
        typesMenu.setSelectedItem(GTDraw.Type.CLASSIC);
        typesMenu.addListener(value -> {
            settings.set(Key.TYPE, value);
            recreateDraw();
        });

        popupMenu.add(typesMenu);

        sizesMenu.setText(ResourceLoader.getString("gt.sizes")); // NOI18N
        popupMenu.add(sizesMenu);

        orientationMenu.setText(ResourceLoader.getString("gt.orientation")); // NOI18N
        orientationMenu.addItem(ResourceLoader.getString("gt.orientation.left.right"), GTOrientation.LEFT_RIGHT); // NOI18N
        orientationMenu.addItem(ResourceLoader.getString("gt.orientation.top.down"), GTOrientation.TOP_DOWN); // NOI18N
        orientationMenu.setSelectedItem(GTOrientation.LEFT_RIGHT);
        orientationMenu.addListener(value -> {
            settings.set(Key.ORIENTATION, value);
            setSettings(settings);
        });
        popupMenu.add(orientationMenu);
        orientationMenu.setVisible(false);

        preferencesMenu.setText(ResourceLoader.getString("gt.preferences")); // NOI18N

        preferencesMenu.addItem(ResourceLoader.getString("gt.addigits"), Key.ARRIVAL_DEPARTURE_DIGITS); // NOI18N
        preferencesMenu.addItem(ResourceLoader.getString("gt.extendedlines"), Key.EXTENDED_LINES); // NOI18N
        preferencesMenu.addItem(ResourceLoader.getString("gt.trainnames"), Key.TRAIN_NAMES); // NOI18N
        preferencesMenu.addItem(ResourceLoader.getString("gt.technological.time"), Key.TECHNOLOGICAL_TIME); // NOI18N
        preferencesMenu.addItem(ResourceLoader.getString("gt.train.ends"), Key.TRAIN_ENDS); // NOI18N
        preferencesMenu.addItem(ResourceLoader.getString("gt.ignore.time.limits"), Key.IGNORE_TIME_LIMITS); // NOI18N
        preferencesMenu.addItem(ResourceLoader.getString("gt.to.train.scroll"), Key.TO_TRAIN_SCROLL); // NOI18N
        preferencesMenu.addItem(ResourceLoader.getString("gt.to.train.change.route"), Key.TO_TRAIN_CHANGE_ROUTE); // NOI18N

        preferencesMenu.addListener((value, selected) -> {
            settings.setOption(value, selected);
            // recreate draw
            recreateDraw();
        });

        popupMenu.add(preferencesMenu);

        routesMenu.addListener(route -> setRoute(route));

        javax.swing.JMenuItem zoomMenuItem = new javax.swing.JMenuItem(ResourceLoader.getString("gt.zoom"));
        zoomMenuItem.addActionListener(e -> {
            // select zoom
            GTViewZoomDialog dialog = new GTViewZoomDialog((Window) getTopLevelAncestor(), true);
            dialog.setLocationRelativeTo(getParent());
            Float oldZoom = settings.get(Key.ZOOM, Float.class);
            Float newZoom = dialog.showDialog(oldZoom);
            if (newZoom != null && newZoom.floatValue() != oldZoom.floatValue()) {
                settings.set(Key.ZOOM, newZoom);
                recreateDraw();
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
                            break;
                        }
                    } else {
                        if (!selected) {
                            selected = collector.selectItemsRadiuses(evt.getX(), evt.getY(), 2, SELECTION_RADIUS);
                        } else {
                            collector.deselectItems();
                        }
                    }
                }
                repaint();
            }
        });
        java.awt.event.MouseAdapter motionListener = new java.awt.event.MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                handle(e, MouseAction.EXIT);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                handle(e, MouseAction.ENTER);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                handle(e, MouseAction.MOVE);
            }

            private void handle(MouseEvent e, MouseAction action) {
                MouseOverHandler moh = gtStorage.getParameter(MOUSE_OVER_HANDLER_KEY, MouseOverHandler.class);
                if (moh != null) {
                    RegionCollector<TimeInterval> collector = getRegionCollector(TimeInterval.class);
                    if (collector != null) {
                        List<TimeInterval> items = collector.getItemsForPointRadiuses(e.getX(), e.getY(), 2, SELECTION_RADIUS);
                        moh.mouseOverIntervals(items);
                    }
                }
            }
        };

        addMouseListener(motionListener);
        addMouseMotionListener(motionListener);
        setLayout(null);
    }

    public <T> void selectItems(List<T> items, Class<T> clazz) {
        for (Class<?> cls : gtStorage.getCollectorClasses()) {
            if (clazz != cls) {
                gtStorage.getCollector(cls).deselectItems();
            }
        }
        gtStorage.getCollector(clazz).selectItems(items);
        repaint();
        checkAndScroll(items, clazz);
    }

    private EditRoutesDialog getRouteDialog() {
        if (editRoutesDialog == null) {
            editRoutesDialog = new EditRoutesDialog((Window)this.getTopLevelAncestor(), true);
        }
        return editRoutesDialog;
    }

    @Override
    public void setSettings(GTViewSettings settings) {
        this.checkDrawType(settings);
        super.setSettings(settings);

        this.updateTypesMenu(settings);

        Integer size = settings.get(Key.VIEW_SIZE, Integer.class);
        sizesMenu.setSelectedItem(size, true);

        this.setPreferencesValue(Key.TRAIN_NAMES, settings);
        this.setPreferencesValue(Key.ARRIVAL_DEPARTURE_DIGITS, settings);
        this.setPreferencesValue(Key.TECHNOLOGICAL_TIME, settings);
        this.setPreferencesValue(Key.EXTENDED_LINES, settings);
        this.setPreferencesValue(Key.IGNORE_TIME_LIMITS, settings);
        this.setPreferencesValue(Key.TO_TRAIN_SCROLL, settings);
        this.setPreferencesValue(Key.TO_TRAIN_CHANGE_ROUTE, settings);
        this.setPreferencesValue(Key.TRAIN_ENDS, settings);

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

    public void addListener(GTViewListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void setRoute(Route route) {
        if (this.route == route) {
            return;
        }
        super.setRoute(route);
        routesMenu.setSelectedItem(route);
        this.fireRouteSelected(route);
    }

    @Override
    protected void routesChanged(Event event) {
        // changed list of routes
        this.createMenuForRoutes(diagram.getRoutes());

        super.routesChanged(event);
    }

    protected javax.swing.JPopupMenu popupMenu;
    private SelectionMenu<Route> routesMenu;
    private SelectionMenu<Integer> sizesMenu;
    private SelectionMenu<GTDraw.Type> typesMenu;
    private SelectionMenu<GTOrientation> orientationMenu;
    private ChoicesMenu<Key> preferencesMenu;
    private final javax.swing.JMenuItem routesMenuItem;

    private EditRoutesDialog editRoutesDialog;
}
