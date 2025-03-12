package net.parostroj.timetable.gui.wrappers;

import net.parostroj.timetable.model.*;

/**
 * Wrapper delegate for all base elements of train diagram.
 *
 * @author jub
 */
public class ElementWrapperDelegate extends BasicWrapperDelegate<Object> {

    @Override
    public String toString(Object element) {
        return switch (element) {
            case EngineClass engineClass -> engineClass.getName();
            case LineClass lineClass -> lineClass.getName();
            case Node node -> node.getName();
            case TrainsCycle cycle -> cycle.getName();
            case TrainType type -> {
                LocalizedString abbr = type.getAbbr();
                LocalizedString desc = type.getDesc();
                String abbrTrans = abbr.translate();
                yield desc != null ? abbrTrans + " - " + desc.translate() : abbrTrans;
            }
            case TrainTypeCategory category -> category.getName().translate();
            case TimeInterval interval -> interval.getOwner().toString();
            case TimetableImage ignored -> element.toString();
            case TextItem ignored -> element.toString();
            case Route ignored -> element.toString();
            case Train train -> train.getDefaultName();
            case OutputTemplate template -> template.getKey();
            case TrainsCycleType cType -> cType.getName().translate();
            case Group group -> group.getName();
            case LineTrack lt -> lt.getNumber();
            case NodeTrack nt -> nt.getNumber() + (nt.isPlatform() ? " [" : "");
            case null, default -> element == null ? "-" : super.toString(element);
        };
    }
}
