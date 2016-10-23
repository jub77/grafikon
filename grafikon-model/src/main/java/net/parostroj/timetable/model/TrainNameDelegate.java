package net.parostroj.timetable.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.parostroj.timetable.model.Train.NameType;
import net.parostroj.timetable.model.events.AttributeChange;
import net.parostroj.timetable.model.events.Event;
import net.parostroj.timetable.utils.CachedValue;

import static net.parostroj.timetable.model.TrainAttributes.*;

/**
 * Delegate for handling creation and other functionality dealing with names of trains.
 *
 * @author jub
 */
class TrainNameDelegate {

    private final Train train;

    private final CachedValue<String> _cachedName;
    private final CachedValue<String> _cachedCompleteName;
    private Map<String, Object> _cachedBinding;

    public TrainNameDelegate(Train train) {
        this.train = train;
        _cachedName = new CachedValue<>();
        _cachedCompleteName = new CachedValue<>();
    }

    public String getName() {
        if (!_cachedName.isCached()) {
            this.refreshName();
        }
        return _cachedName.getValue();
    }

    public String getCompleteName() {
        if (!_cachedCompleteName.isCached()) {
            this.refreshCompleteName();
        }
        return _cachedCompleteName.getValue();
    }

    public TranslatedString getName(Train.NameType nameType) {
        return new TranslatedStringTrain(nameType);
    }

    public void refreshCachedNames() {
        refreshName();
        refreshCompleteName();
    }

    private void refreshName() {
        String oldName = _cachedName.getValue();
        String newName = this.getNameImpl(NameType.NORMAL);
        if (_cachedName.set(newName)) {
            train.fireEvent(new Event(train, new AttributeChange(ATTR_NAME, oldName, newName)));
        }
    }

    private void refreshCompleteName() {
        String oldName = _cachedCompleteName.getValue();
        String newName = this.getNameImpl(NameType.COMPLETE);
        if (_cachedCompleteName.set(newName)) {
            train.fireEvent(new Event(train, new AttributeChange(ATTR_COMPLETE_NAME, oldName, newName)));
        }
    }

    private String getNameImpl(NameType nameType) {
        TrainType type = train.getType();
        return type != null ? formatTrainName(nameType) : train.getNumber();
    }

    /**
     * formats the train complete name according to template.
     *
     * @param nameType type of name
     * @return formatted complete train name
     */
    public String formatTrainName(NameType nameType) {
        TextTemplate template = train.getType().getNameTemplate(nameType);
        return template.evaluate(TextTemplate.getBinding(train));
    }

    /**
     * @return binding for template
     */
    Map<String,Object> createTemplateBinding() {
        return this.createTemplateBinding(Locale.getDefault());
    }

    /**
     * @return binding for template
     */
    Map<String,Object> createTemplateBinding(Locale locale) {
        if (_cachedBinding == null) {
            _cachedBinding = new HashMap<>();
            _cachedBinding.put("train", train);
            _cachedBinding.put("stations", new Stations());
        }
        _cachedBinding.put("type", train.getType());
        _cachedBinding.put("locale", locale != null ? locale.toLanguageTag() : null);
        return _cachedBinding;
    }

    /**
     * Wrapper for accessing stations for text templates.
     */
    public class Stations {

        public Node getAt(int index) {
            return train.getIntervalList().get(index * 2).getOwnerAsNode();
        }

        public Node get(int index) {
            return getAt(index);
        }

        public Node getFirst() {
            return train.getFirstInterval().getOwnerAsNode();
        }

        public Node getLast() {
            return train.getLastInterval().getOwnerAsNode();
        }
    }

    private final class TranslatedStringTrain implements TranslatedString {

        private NameType nameType;

        public TranslatedStringTrain(NameType nameType) {
            this.nameType = nameType;
        }

        @Override
        public String getDefaultString() {
            return nameType == NameType.COMPLETE ? getCompleteName() : getName();
        }

        @Override
        public String translate(Locale locale) {
            TrainType type = train.getType();
            if (type == null) {
                return getDefaultString();
            } else {
                TextTemplate template = type.getNameTemplate(nameType);
                Map<String, Object> binding = TextTemplate.getBinding(train, locale);
                return template.evaluate(binding);
            }
        }

        @Override
        public Collection<Locale> getLocales() {
            return train.getDiagram().getLocales();
        }
    }
}
