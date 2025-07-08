package net.parostroj.timetable.model;

import java.util.Map;
import java.util.Set;

public interface Permissions {

    boolean isOutputTemplateAllowed();
    Set<TextTemplate.Language> getAllowedTemplate();
    Set<Script.Language> getAllowedScript();

    default TextTemplate createTextTemplate(String template, TextTemplate.Language language) {
        if (getAllowedTemplate().contains(language)) {
            return TextTemplate.create(template, language);
        } else {
            return null;
        }
    }

    default Script createScript(String sourceCode, Script.Language language) {
        if (getAllowedScript().contains(language)) {
            return Script.create(sourceCode, language);
        } else {
            return null;
        }
    }

    default boolean isAllowed(TextTemplate.Language lng) {
        return getAllowedTemplate().contains(lng);
    }

    default boolean isAllowed(Script.Language lng) {
        return getAllowedScript().contains(lng);
    }

    static Permissions forType(TrainDiagramType type) {
        return PermissionsImpl.forType.get(type);
    }
}

class PermissionsImpl implements Permissions {

     static final Map<TrainDiagramType, Permissions> forType = Map.of(
            TrainDiagramType.NORMAL, new PermissionsImpl(
                    Set.of(TextTemplate.Language.PLAIN, TextTemplate.Language.SIMPLE),
                    Set.of(),
                    false),
            TrainDiagramType.RAW, new PermissionsImpl(
                    Set.of(TextTemplate.Language.PLAIN, TextTemplate.Language.SIMPLE, TextTemplate.Language.GROOVY),
                    Set.of(Script.Language.GROOVY),
                    true)
    );

    private final Set<TextTemplate.Language> templateAllowed;
    private final Set<Script.Language> scriptAllowed;
    private final boolean outputTemplateAllowed;

    PermissionsImpl(Set<TextTemplate.Language> templateAllowed, Set<Script.Language> scriptAllowed, boolean outputTemplateAllowed) {
        this.templateAllowed = templateAllowed;
        this.scriptAllowed = scriptAllowed;
        this.outputTemplateAllowed = outputTemplateAllowed;
    }

    @Override
    public boolean isOutputTemplateAllowed() {
        return outputTemplateAllowed;
    }

    @Override
    public Set<Script.Language> getAllowedScript() {
        return scriptAllowed;
    }

    @Override
    public Set<TextTemplate.Language> getAllowedTemplate() {
        return templateAllowed;
    }
}
