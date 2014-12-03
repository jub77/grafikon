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

    public TrainStrokeCache(float baseWidth, float zoom) {
        this.baseWidth = baseWidth;
        this.zoom = zoom;
        this.dashMap = new EnumMap<LineType, float[]>(LineType.class);
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
            stroke = createTrainStroke(lineType, 1.0f, 1.0f);
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
        float lWidth = zoom * this.baseWidth * wRatio;
        float[] dashes = dashMap.get(type);
        if (dashes == null) {
            return new BasicStroke(lWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 10.0f);
        } else {
            return new BasicStroke(lWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER,
                    10.0f, DrawUtils.zoomDashes(dashes, zoom, lRatio), 0f);
        }
    }
}
