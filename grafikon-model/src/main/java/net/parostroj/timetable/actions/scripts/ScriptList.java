package net.parostroj.timetable.actions.scripts;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * List of scripts.
 *
 * @author jub
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
record ScriptList(List<ScriptDescription> scripts) {}
