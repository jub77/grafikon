/*
 * Command.java
 * 
 * Created on 4.9.2007, 9:10:20
 */

package net.parostroj.timetable.gui.commands;

import net.parostroj.timetable.gui.ApplicationModel;

/**
 * Abstract class for commands.
 * 
 * @author jub
 */
public abstract class Command {

    /**
     * executes command on model.
     * 
     * @param model application model
     * @throws net.parostroj.timetable.gui.commands.CommandException 
     */
    abstract public void execute(ApplicationModel model) throws CommandException;
    
    /**
     * undoes command.
     * 
     * @param model application model
     * @throws net.parostroj.timetable.gui.commands.CommandException 
     */
    abstract public void undo(ApplicationModel model) throws CommandException;
}
