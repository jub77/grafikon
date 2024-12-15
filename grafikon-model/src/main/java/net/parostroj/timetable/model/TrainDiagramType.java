package net.parostroj.timetable.model;

import java.util.Set;

/**
 * Type of diagram.
 */
public enum TrainDiagramType {
    NORMAL(
            Set.of(TextTemplate.Language.PLAIN, TextTemplate.Language.SIMPLE),
            Set.of(),
            false),
    RAW(
            Set.of(TextTemplate.Language.PLAIN, TextTemplate.Language.SIMPLE, TextTemplate.Language.GROOVY),
            Set.of(Script.Language.GROOVY),
            true);

    private final Set<TextTemplate.Language> templateAllowed;
    private final Set<Script.Language> scriptAllowed;
    private final boolean outputTemplateAllowed;

    TrainDiagramType(Set<TextTemplate.Language> templateAllowed, Set<Script.Language> scriptAllowed,
            boolean outputTemplateAllowed) {
        this.templateAllowed = templateAllowed;
        this.scriptAllowed = scriptAllowed;
        this.outputTemplateAllowed = outputTemplateAllowed;
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
}
