package net.parostroj.timetable.actions;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

public class TextList {

    private final StringBuilder output;
    private final String end;
    private boolean first;
    private final String delimiter;
    private final String start;


    public TextList(StringBuilder output, String delimiter) {
        this(output, "", "", delimiter);
    }

    public TextList(StringBuilder output, String start, String end, String delimiter) {
        this.output = output;
        this.start = start;
        this.delimiter = delimiter;
        this.end = end;
        this.first = true;
    }

    public TextList add(String text) {
        if (!first) {
            output.append(delimiter);
        } else {
            output.append(start);
        }
        first = false;
        output.append(text);
        return this;
    }

    public TextList append(String text) {
        output.append(text);
        return this;
    }

    public <T> TextList addItems(Iterable<T> iterable) {
        for (T item : iterable) {
            this.add(item.toString());
        }
        return this;
    }

    public <T> TextList addItems(Iterable<T> iterable, Function<T, String> f) {
        return this.addItems(Iterables.transform(iterable, f));
    }

    public void finish() {
        if (!first) {
            output.append(end);
        }
        first = true;
    }
}
