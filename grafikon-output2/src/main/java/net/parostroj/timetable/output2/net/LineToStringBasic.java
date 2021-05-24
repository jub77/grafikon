package net.parostroj.timetable.output2.net;

import java.util.function.Function;
import java.util.function.Supplier;
import net.parostroj.timetable.model.Line;
import net.parostroj.timetable.model.units.LengthUnit;
import net.parostroj.timetable.model.units.SpeedUnit;

public class LineToStringBasic implements Function<Line, String> {

    private final Supplier<LengthUnit> lengthUnit;
    private final Supplier<SpeedUnit> speedUnit;
    private final NetItemConversionUtil conv;

    public LineToStringBasic(Supplier<LengthUnit> lengthUnit, Supplier<SpeedUnit> speedUnit) {
        this.lengthUnit = lengthUnit;
        this.speedUnit = speedUnit;
        this.conv = new NetItemConversionUtil();
    }

    @Override
    public String apply(Line line) {
        if (speedUnit == null || lengthUnit == null) {
            return line.toString();
        }
        StringBuilder result = new StringBuilder(conv.collectRoutesString(line));
        if (result.length() != 0) {
            result.append('\n');
        }
        result.append(conv.getLineLengthString(line, lengthUnit.get()));
        Integer topSpeed = line.getTopSpeed();
        if (topSpeed != null) {
            result.append(" (").append(conv.getLineSpeedString(line, speedUnit.get())).append(')');
        }
        return result.toString();
    }
}
