package net.parostroj.timetable.gui.components;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;

import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

import net.parostroj.timetable.actions.RouteHelper;
import net.parostroj.timetable.gui.components.GTViewSettings.Key;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.model.events.Event;
import net.parostroj.timetable.model.events.Event.Type;
import net.parostroj.timetable.output2.gt.*;
import net.parostroj.timetable.output2.gt.GTDraw.Refresh;
import net.parostroj.timetable.utils.Tuple;
import net.parostroj.timetable.visitors.AbstractEventVisitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Graphical timetable view.
 *
 * @author jub
 */
public class GraphicalTimetableViewDraw extends javax.swing.JPanel implements Scrollable {

    private static final Logger log = LoggerFactory.getLogger(GraphicalTimetableViewDraw.class);

    protected final static int WIDTH_STEPS = 10;

    private final static int MIN_WIDTH = 1000;
    private final static int MAX_WIDTH = 10000;
    private final static int WIDTH_TO_HEIGHT_RATIO = 5;

    protected GTViewSettings settings;

    protected GTDrawFactory drawFactory;
    private GTDraw draw;
    protected Route route;
    protected TrainDiagram diagram;

    protected final GTStorage gtStorage = new GTStorage();

    private Dimension preferredSize = new Dimension(MIN_WIDTH, MIN_WIDTH / WIDTH_TO_HEIGHT_RATIO);

    /** Creates new form TrainGraphicalTimetableView */
    public GraphicalTimetableViewDraw() {
        initComponents();

        this.drawFactory = new NormalGTDrawFactory();
        this.settings = this.getDefaultViewSettings();

        this.setBackground(Color.WHITE);

        this.addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                if (draw != null) {
                    Dimension size = draw.getSize();
                    if (!size.equals(getSize())) {
                        recreateDraw();
                    }
                }
            }
        });

        addRegionCollector(TimeInterval.class, new TrainRegionCollector());
    }

    private Listener currentListener;

    private Integer startTime;
    private Integer endTime;

    public void setTrainDiagram(TrainDiagram diagram) {
        if (currentListener != null && this.diagram != null) {
            this.diagram.removeAllEventListener(this.currentListener);
        }
        if (diagram == null) {
            this.diagram = null;
            this.currentListener = null;
            this.setRoute(null);
        } else {
            this.diagram = diagram;
            this.currentListener = new VisitEventListener(new AbstractEventVisitor() {

                @Override
                public void visitDiagramEvent(Event event) {
                    switch (event.getType()) {
                        case ADDED: case REMOVED:
                            if (event.getObject() instanceof Route) {
                                routesChanged(event);
                            }
                            break;
                        default:
                            break;
                    }
                }
            }) {
                @Override
                public void changed(Event event) {
                    super.changed(event);
                    for (RegionCollector<?> collector : gtStorage.collectors()) {
                        collector.processEvent(event);
                    }
                    if (draw != null) {
                        Refresh refresh = draw.processEvent(event);
                        switch (refresh) {
                            case REPAINT:
                                repaint();
                                break;
                            case RECREATE:
                                recreateDraw();
                                break;
                            case RECREATE_WITH_TIME:
                                recreateDraw();
                                break;
                            default:
                                // nothing
                                break;
                        }
                    }
                }
            };
            this.diagram.addAllEventListener(this.currentListener);
            if (!diagram.getRoutes().isEmpty()) {
                this.setRoute(diagram.getRoutes().iterator().next());
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
                .set(Key.TYPE, GTDraw.Type.CLASSIC)
                .set(Key.VIEW_SIZE, 4)
                .set(Key.TRAIN_COLORS, GTDraw.TrainColors.BY_TYPE)
                .set(Key.TRAIN_NAMES, Boolean.TRUE)
                .set(Key.ARRIVAL_DEPARTURE_DIGITS, Boolean.FALSE)
                .set(Key.EXTENDED_LINES, Boolean.FALSE)
                .set(Key.TECHNOLOGICAL_TIME, Boolean.FALSE)
                .set(Key.IGNORE_TIME_LIMITS, Boolean.FALSE)
                .set(Key.ZOOM, 1.0f)
                .set(Key.TO_TRAIN_SCROLL, Boolean.FALSE)
                .set(Key.TO_TRAIN_CHANGE_ROUTE, Boolean.FALSE)
                .set(Key.ORIENTATION, GTOrientation.LEFT_RIGHT)
                .set(Key.ORIENTATION_MENU, Boolean.TRUE)
                .set(Key.TRAIN_ENDS, Boolean.TRUE);
        return config;
    }

    protected void routesChanged(Event event) {
        // check current route
        if (event.getType() == Type.REMOVED && event.getObject() instanceof Route && event.getObject().equals(this.getRoute())) {
            if (!diagram.getRoutes().isEmpty()) {
                this.setRoute(diagram.getRoutes().iterator().next());
            } else {
                this.setRoute(null);
            }
        }
        if (event.getType() == Type.ADDED && event.getObject() instanceof Route && this.getRoute() == null) {
            this.setRoute((Route)event.getObject());
        }
    }

    public <T> void setRegionSelector(RegionSelector<T> selector, Class<T> clazz) {
        RegionCollector<T> collector = this.getRegionCollector(clazz);
        if (collector != null) {
            collector.setSelector(selector);
        }
    }

    public <T> void addRegionCollector(Class<T> clazz, RegionCollector<T> collector) {
        gtStorage.setCollector(clazz, collector);
    }

    public <T> void removeRegionCollector(Class<T> clazz) {
        gtStorage.removeCollector(clazz);
    }

    public <T> RegionCollector<T> getRegionCollector(Class<T> clazz) {
        return gtStorage.getCollector(clazz);
    }

    public void setParameter(String key, Object value) {
        gtStorage.setParameter(key, value);
    }

    public <T> T getParameter(String key, Class<T> clazz) {
        return gtStorage.getParameter(key, clazz);
    }

    public void removeParameter(String key) {
        gtStorage.removeParameter(key);
    }

    public void setDrawFactory(GTDrawFactory drawFactory) {
        this.drawFactory = drawFactory;
    }

    /**
     * sets route and refreshes view.
     *
     * @param route route to be set
     */
    public void setRoute(Route route) {
        if (this.route == route) {
            return;
        }
        this.route = route;
        this.recreateDraw();
    }

    public void setTimeLimitsOverride(Integer start, Integer end) {
        this.startTime = start;
        this.endTime = end;
        this.recreateDraw();
    }

    /**
     * refreshes gt draw and repaints view.
     */
    public void refresh() {
        this.recreateDraw();
    }

    protected Tuple<Integer> computeTimeRange(GTViewSettings settings) {
        if (diagram != null) {
            Integer from = diagram.getAttribute(TrainDiagram.ATTR_FROM_TIME, Integer.class);
            Integer to = diagram.getAttribute(TrainDiagram.ATTR_TO_TIME, Integer.class);
            if (settings.isOption(Key.IGNORE_TIME_LIMITS)) {
                from = 0;
                to = TimeInterval.DAY;
            } else {
                if (this.startTime != null) {
                    from = this.startTime;
                }
                if (this.endTime != null) {
                    to = this.endTime;
                }
            }
            return new Tuple<Integer>(from, to);
        } else {
            return new Tuple<Integer>();
        }
    }

    protected boolean updatePreferredSize(GTViewSettings config, Tuple<Integer> range) {
        Integer start = null;
        Integer end = null;
        int size = config.get(Key.VIEW_SIZE, Integer.class);
        if (!config.getOption(Key.IGNORE_TIME_LIMITS)) {
            start = range.first != null ? range.first : null;
            end = range.second != null ? range.second : null;
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
        Dimension newSize = null;
        switch (config.get(Key.ORIENTATION, GTOrientation.class)) {
            case LEFT_RIGHT:
                newSize = new Dimension(newWidth, newHeight);
                break;
            case TOP_DOWN:
                newSize = new Dimension(newHeight, newWidth);
                break;
        }
        if (!newSize.equals(preferredSize)) {
            preferredSize = newSize;
            this.revalidate();
            return true;
        } else {
            return false;
        }
    }

    protected void recreateDraw() {
        Tuple<Integer> range = this.computeTimeRange(settings);
        this.updatePreferredSize(settings, range);
        for (RegionCollector<?> collector : gtStorage.collectors()) {
            collector.clear();
        }
        draw = null;
        this.repaint();
    }

    protected GTDraw createDraw(GTViewSettings config) {
        return this.createDraw(config, getSize());
    }

    protected GTDraw createDraw(GTViewSettings config, Dimension size) {
        if (this.getRoute() == null) {
            return null;
        }
        GTDrawSettings drawSettings = config.createGTDrawSettings();
        Tuple<Integer> range = this.computeTimeRange(config);
        drawSettings.set(GTDrawSettings.Key.SIZE, size);
        if (range.first != null) {
            drawSettings.set(GTDrawSettings.Key.START_TIME, range.first);
        }
        if (range.second != null) {
            drawSettings.set(GTDrawSettings.Key.END_TIME, range.second);
        }
        return drawFactory.createInstance(config.getGTDrawType(), drawSettings, this.getRoute(), gtStorage);
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
        if (settings == null) {
            return;
        }

        this.settings = settings;

        this.recreateDraw();
    }


    private void initComponents() {
    }

    @Override
    public void paint(Graphics g) {
        long time = System.currentTimeMillis();
        super.paint(g);

        // create draw if none exists
        if (draw == null) {
            draw = this.createDraw(this.settings);
        }

        if (draw != null) {
            draw.draw((Graphics2D) g);
        } else {
            // draw information about context menu
            g.drawString(ResourceLoader.getString("gt.contextmenu.info"), 20, 20);
        }
        log.trace("Finished paint in {}ms", Long.toString(System.currentTimeMillis() - time));
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

    public <T> void checkAndScroll(final List<T> items, final Class<T> clazz) {
        // only for trains with TimeIntervals handled by TrainRegionCollector...
        if (clazz.equals(TimeInterval.class) && !items.isEmpty()) {
            RegionCollector<T> collector = gtStorage.getCollector(clazz);
            if (collector instanceof TrainRegionCollector) {
                TrainRegionCollector trainRegionCollector = (TrainRegionCollector) collector;
                Train selectedTrain = ((TimeInterval) items.get(0)).getTrain();
                if (selectedTrain != null) {
                    if (!trainRegionCollector.containsTrain(selectedTrain) &&
                            settings.getOption(Key.TO_TRAIN_CHANGE_ROUTE)) {
                        Route newRoute = RouteHelper.getBestRouteMatch(diagram.getRoutes(), selectedTrain);
                        if (newRoute != null) {
                            this.setRoute(newRoute);
                        }
                    }
                    if (settings.getOption(Key.TO_TRAIN_SCROLL)) {
                        GuiComponentUtils.runLaterInEDT(() -> scrollToItem(items, clazz));
                    }
                }
            }
        }
    }

    public <T> void scrollToItem(List<T> items, Class<T> clazz) {
        RegionCollector<T> collector = gtStorage.getCollector(clazz);
        if (collector != null) {
            Rectangle region = collector.getRectangleForItems(items);
            if (region != null) {
                this.scrollRectToVisible(region);
            }
        }
    }
}
