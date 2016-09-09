package net.parostroj.timetable.gui.actions.impl;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

import javax.swing.filechooser.FileFilter;

public class ExtendedFileFilter extends FileFilter {

    private Collection<FileFilter> filters;

    public ExtendedFileFilter(FileFilter... filters) {
        this.filters = Arrays.asList(filters);
    }

    @Override
    public boolean accept(File pathname) {
        for (FileFilter f : filters) {
            if (f.accept(pathname)) return true;
        }
        return false;
    }

    @Override
    public String getDescription() {
        return null;
    }
}
