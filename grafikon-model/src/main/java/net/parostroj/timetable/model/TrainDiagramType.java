package net.parostroj.timetable.model;

import java.util.Set;

/**
 * Type of diagram.
 */
public enum TrainDiagramType {
    NORMAL(
            Set.of(TextTemplate.Language.PLAIN, TextTemplate.Language.SIMPLE),
            Set.of(),
            false, "normal"),
    RAW(
            Set.of(TextTemplate.Language.PLAIN, TextTemplate.Language.SIMPLE, TextTemplate.Language.GROOVY),
            Set.of(Script.Language.GROOVY),
            true, "raw");

    private final Set<TextTemplate.Language> templateAllowed;
    private final Set<Script.Language> scriptAllowed;
    private final boolean outputTemplateAllowed;
    private final String key;

    TrainDiagramType(Set<TextTemplate.Language> templateAllowed, Set<Script.Language> scriptAllowed,
            boolean outputTemplateAllowed, String key) {
        this.templateAllowed = templateAllowed;
        this.scriptAllowed = scriptAllowed;
        this.outputTemplateAllowed = outputTemplateAllowed;
        this.key = key;
    }

    public boolean isAllowed(TextTemplate.Language language) {
        return templateAllowed.contains(language);
    }

    public boolean isAllowed(Script.Language language) {
        return scriptAllowed.contains(language);
    }

    public boolean isOutputTemplateAllowed() {
        return outputTemplateAllowed;
    }

    public String getKey() {
        return key;
    }

    public TextTemplate createTextTemplate(String template, TextTemplate.Language language) {
        if (isAllowed(language)) {
            return TextTemplate.create(template, language);
        } else {
            return null;
        }
    }

    public Script createScript(String sourceCode, Script.Language language) {
        if (isAllowed(language)) {
            return Script.create(sourceCode, language);
        } else {
            return null;
        }
    }

    public static TrainDiagramType getByKey(String key) {
        for (TrainDiagramType type : values()) {
            if (type.getKey().equals(key))
                return type;
        }
        return null;
    }
}
