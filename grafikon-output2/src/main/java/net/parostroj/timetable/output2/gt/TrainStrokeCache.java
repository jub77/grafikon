package net.parostroj.timetable.output2.gt;

import java.awt.BasicStroke;
import java.awt.Stroke;
import java.util.*;

import net.parostroj.timetable.model.LineType;
import net.parostroj.timetable.model.TrainType;

/**
 * Cache of train strokes.
 *
 * @author jub
 */
public class TrainStrokeCache {

    private final Map<LineType, float[]> dashMap;
    private final Map<TrainType, Stroke> strokes;
    private final Map<LineType, Stroke> strokesByLineType;
    private final float baseWidth;
    private final float zoom;
    private final float baseDashLenght;

    /**
     * Creates cache with line type lines based on dashes defined by {@link #add(LineType, float[])}
     * method.
     */
    public TrainStrokeCache(float baseWidth, float zoom) {
        this.baseWidth = baseWidth;
        this.zoom = zoom;
        this.dashMap = new EnumMap<LineType, float[]>(LineType.class);
        this.strokes = new HashMap<TrainType, Stroke>();
        this.strokesByLineType = new EnumMap<LineType, Stroke>(LineType.class);
        this.baseDashLenght = 0f;
    }

    /**
     * Creates cache with line type lines based on {@link #baseDashLenght} and computation based
     * on that value.
     */
    public TrainStrokeCache(float baseWidth, float zoom, float baseDashLength) {
        this.baseWidth = baseWidth;
        this.zoom = zoom;
        this.baseDashLenght = baseDashLength;
        this.dashMap = null;
        this.strokes = new HashMap<TrainType, Stroke>();
        this.strokesByLineType = new EnumMap<LineType, Stroke>(LineType.class);
    }

    public void add(LineType type, float[] dashes) {
        dashMap.put(type, dashes);
    }

    public Stroke getStroke() {
        return getStroke(LineType.SOLID);
    }

    public Stroke getStroke(LineType lineType) {
        Stroke stroke = strokesByLineType.get(lineType);
        if (stroke == null) {
            stroke = this.createTrainStroke(lineType, 1.0f, 1.0f);
            strokesByLineType.put(lineType, stroke);
        }
        return stroke;
    }

    public Stroke getStroke(TrainType type) {
        Stroke stroke = null;
        if (type == null) {
            stroke = getStroke();
        } else {
            stroke = strokes.get(type);
            if (stroke == null) {
                stroke = this.createTrainStroke(type.getLineType(),
                        (float) type.getLineWidth(), (float) type.getLineLength());
                strokes.put(type, stroke);
            }
        }
        return stroke;
    }

    public void clear() {
        strokes.clear();
        strokesByLineType.clear();
    }

    private Stroke createTrainStroke(LineType type, float wRatio, float lRatio) {
        float width = zoom * this.baseWidth * wRatio;
        float[] dashes = null;
        if (dashMap != null) {
            // based on dashMap
            dashes = dashMap.get(type);
            if (dashes != null) {
                dashes = DrawUtils.zoomDashes(dashes, zoom, lRatio);
            }
        } else {
            // based on computation
            dashes = this.computeDashes(type, width, lRatio);
        }
        if (dashes == null) {
            return new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 10.0f);
        } else {
            return new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER,
                    10.0f, dashes, 0f);
        }
    }

    private float[] computeDashes(LineType type, float width, float lRatio) {
        float[] dashes = null;
        switch (type) {
            case SOLID:
                // no dashes
                break;
            case DASH:
                dashes = new float[] { baseDashLenght * zoom * lRatio, 3f * width };
                break;
            case DASH_AND_DOT:
                dashes = new float[] { baseDashLenght * zoom * lRatio, 2.5f * width , 0f, 2.5f * width};
                break;
            case DOT:
                dashes = new float[] { 0f, 2.5f * width };
                break;
        }
        return dashes;
    }
}
