package net.parostroj.timetable.gui.pm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.model.AttributesHolder;
import net.parostroj.timetable.model.LocalizedString;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainsCycleItem;
import net.parostroj.timetable.model.TrainsCycleType;
import net.parostroj.timetable.utils.AttributeReference;
import net.parostroj.timetable.utils.Reference;

public class LocalizationTypeFactory {

    public static LocalizationTypeFactory createInstance() {
        return new LocalizationTypeFactory();
    }

    public Collection<LocalizationType<AttributeReference<LocalizedString>>> createTypes(TrainDiagram diagram) {
        Collection<LocalizationType<AttributeReference<LocalizedString>>> types = new ArrayList<>();
        types.add(createTrainCommentsType(diagram));
        types.add(createCirculationItemCommentsType(diagram));
        types.add(createCirculationNamesType(diagram));
        return types;
    }

    public ARLocalizationType<AttributeReference<LocalizedString>> createEditFromAttributeHolder(TrainDiagram diagram, AttributesHolder holder, String category) {
        return new ARLocalizationType<AttributeReference<LocalizedString>>(
                holder.toString(),
                holder.getAttributes().getAttributesMap(category).keySet().stream()
                        .map(key -> AttributeReference.create(holder, category, key, LocalizedString.class))
                        .collect(Collectors.toList()),
                ref -> ((AttributeReference<?>) ref).getName(),
                diagram.getLocales()) {
                    @Override
                    protected AttributeReference<LocalizedString> createImpl(String key) {
                        holder.getAttributes().set(key, LocalizedString.fromString(""), category);
                        return AttributeReference.create(holder, category, key, LocalizedString.class);
                    }
        };
    }

    public LocalizationType<AttributeReference<LocalizedString>> createFromAttributeHolder(TrainDiagram diagram, AttributesHolder holder, String category) {
        return new LocalizationType<>(
                holder.toString(),
                holder.getAttributes().getAttributesMap(category).keySet().stream()
                        .map(key -> AttributeReference.create(holder, category, key, LocalizedString.class))
                        .collect(Collectors.toList()),
                ref -> ref.get().toString(),
                diagram.getLocales());
    }

    public LocalizationType<AttributeReference<LocalizedString>> createCirculationNamesType(TrainDiagram diagram) {
        return new LocalizationType<>(
                ResourceLoader.getString("localization.type.circulation.type.names"),
                diagram.getCycleTypes().stream()
                        .filter(type -> !type.isDefaultType() && type.getDisplayName() != null)
                        .map(type -> AttributeReference.create(type, TrainsCycleType.ATTR_DISPLAY_NAME, LocalizedString.class))
                        .collect(Collectors.toList()),
                ref -> ref.get().getDefaultString(),
                diagram.getLocales());
    }

    public LocalizationType<AttributeReference<LocalizedString>> createCirculationItemCommentsType(TrainDiagram diagram) {
        return new LocalizationType<>(
                ResourceLoader.getString("localization.type.circulation.item.comments"),
                diagram.getCycles().stream().flatMap(cycle -> cycle.getItems().stream())
                        .filter(cycle -> cycle.getComment() != null)
                        .map(cycle -> AttributeReference.create(cycle, TrainsCycleItem.ATTR_COMMENT, LocalizedString.class))
                        .collect(Collectors.toList()),
                ref -> getCirculationItemDesc(ref),
                diagram.getLocales());
    }

    public LocalizationType<AttributeReference<LocalizedString>> createTrainCommentsType(TrainDiagram diagram) {
        return new LocalizationType<>(
                ResourceLoader.getString("localization.type.train.comments"),
                diagram.getTrains().stream().flatMap(train -> train.getTimeIntervalList().stream())
                        .filter(interval -> interval.getComment() != null)
                        .map(interval -> AttributeReference.create(interval, TimeInterval.ATTR_COMMENT, LocalizedString.class))
                        .collect(Collectors.toList()),
                ref -> getTimeIntervalDesc(ref),
                diagram.getLocales());
    }

    private static String getCirculationItemDesc(Reference<LocalizedString> ref) {
        TrainsCycleItem circulationItem = (TrainsCycleItem) ((AttributeReference<?>) ref).getHolder();
        String circDesc = circulationItem.getCycle().getDisplayDescription();
        String trainDesc = circulationItem.getTrain().getName();
        return String.format("%s (%s: %s)", trainDesc, circulationItem.getCycle().getName(), circDesc);
    }

    private static String getTimeIntervalDesc(Reference<LocalizedString> ref) {
        TimeInterval interval = (TimeInterval) ((AttributeReference<?>) ref).getHolder();
        String trainDesc = interval.getTrain().getName();
        String nodeDesc = interval.getOwnerAsNode().getName();
        return String.format("%s (%s)", trainDesc, nodeDesc);
    }
}
