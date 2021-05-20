package net.parostroj.timetable.gui.utils;

import java.math.BigDecimal;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.parostroj.timetable.model.Line;
import net.parostroj.timetable.model.Route;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.units.LengthUnit;
import net.parostroj.timetable.model.units.SpeedUnit;
import net.parostroj.timetable.model.units.UnitUtil;

public class NetItemConversionUtil {

    public String getLineLengthString(Line line, LengthUnit defaultLengthUnit) {
        LengthUnit lengthUnit = line.getDiagram().getAttributes().get(TrainDiagram.ATTR_EDIT_LENGTH_UNIT, LengthUnit.class, defaultLengthUnit);
        BigDecimal cValue = lengthUnit.convertFrom(new BigDecimal(line.getLength()), LengthUnit.MM);
        return UnitUtil.convertToString("#0.###", cValue) + lengthUnit.getUnitsOfString();
    }

    public String getLineSpeedString(Line line, SpeedUnit defaultSpeedUnit) {
        SpeedUnit speedUnit = line.getDiagram().getAttributes().get(TrainDiagram.ATTR_EDIT_SPEED_UNIT, SpeedUnit.class, defaultSpeedUnit);
        BigDecimal sValue = speedUnit.convertFrom(new BigDecimal(line.getTopSpeed()), SpeedUnit.KMPH);
        return UnitUtil.convertToString("#0", sValue) + speedUnit.getUnitsOfString();
    }

    public String collectRoutesString(Line line) {
        return collectRoutes(line)
                .map(Route::getName)
                .collect(Collectors.joining(", "));
    }

    public Stream<Route> collectRoutes(Line line) {
        return line.getDiagram().getRoutes().stream()
                .filter(Route::isNetPart)
                .filter(route -> route.contains(line));
    }
}
