package net.parostroj.timetable.output2.gt;

import java.awt.Dimension;
import java.util.Locale;

import net.parostroj.timetable.model.Route;
import net.parostroj.timetable.output2.gt.GTDraw.Type;
import net.parostroj.timetable.output2.gt.GTDrawSettings.Key;

/**
 * Parameters for GTDrawOutput.
 *
 * @author jub
 */
public class GTDrawParams {

    private final Type type;
    private final Route route;
    private final GTDrawSettings settings;

    public GTDrawParams(GTDrawParams params, Route route) {
        this.type = params.type;
        this.route = route;
        this.settings = GTDrawSettings.copy(params.settings);
    }

    public GTDrawParams(Route route) {
        this(Type.CLASSIC, GTDrawSettings.create(), route);
    }

    public GTDrawParams(Type type, Route route) {
        this(type, GTDrawSettings.create(), route);
    }

    public GTDrawParams(Type type, GTDrawSettings settings, Route route) {
        if (type == null || settings == null) {
            throw new NullPointerException("Parameters cannot be null");
        }
        this.type = type;
        this.settings = settings;
        this.route = route;
    }

    public GTDrawSettings getSettings() {
        return settings;
    }

    public Type getType() {
        return type;
    }

    public Route getRoute() {
        return route;
    }

    public void setSize(int x, int y) {
        this.settings.set(Key.SIZE, new Dimension(x, y));
    }

    public void setZoom(float zoom) {
        this.settings.set(Key.ZOOM, zoom);
    }

    public void setStart(int time) {
        this.settings.set(Key.START_TIME, time);
    }

    public void setEnd(int time) {
        this.settings.set(Key.END_TIME, time);
    }

    public void setTechnologicalTime(boolean technologicalTime) {
        this.settings.setOption(Key.TECHNOLOGICAL_TIME, technologicalTime);
    }

    public void setArrivalDepartureDigits(boolean arrivalDepartureDigits) {
        this.settings.setOption(Key.ARRIVAL_DEPARTURE_DIGITS, arrivalDepartureDigits);
    }

    public void setExtendedLines(boolean extendedLines) {
        this.settings.setOption(Key.EXTENDED_LINES, extendedLines);
    }

    public void setStationNamesWidth(int width) {
        this.settings.set(Key.STATION_NAME_WIDTH, width);
    }

    public void setStationNamesWidthFixed(boolean fixed) {
        this.settings.setOption(Key.STATION_NAME_WIDTH_FIXED, fixed);
    }

    public void setLegend(boolean legend) {
        this.settings.setOption(Key.LEGEND, legend);
    }

    public void setTitle(boolean title) {
        this.settings.setOption(Key.TITLE, title);
    }

    public void setInnerSize(boolean innerSize) {
        this.settings.setOption(Key.INNER_SIZE, innerSize);
    }

    public void setTitleText(String titleText) {
        this.settings.setRemove(Key.TITLE_TEXT, titleText);
    }

    public void setVerticalOrientation(boolean vertical) {
        this.settings.set(Key.ORIENTATION, vertical ? GTOrientation.TOP_DOWN : GTOrientation.LEFT_RIGHT);
    }

    public void setTrainEnds(boolean trainEnds) {
        this.settings.set(Key.TRAIN_ENDS, trainEnds);
    }

    public void setTrainNames(boolean trainNames) {
        this.settings.set(Key.TRAIN_NAMES, trainNames);
    }

    public void setLocaleTag(String tag) {
        this.settings.set(Key.LOCALE, Locale.forLanguageTag(tag));
    }

    public void setLocale(Locale locale) {
        this.settings.set(Key.LOCALE, locale);
    }
}
