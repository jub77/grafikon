package net.parostroj.timetable.output2;

import java.util.List;

/**
 * Output.
 *
 * @author jub
 */
public interface Output {

    public void write(OutputParam... params) throws OutputException;

    public void write(List<OutputParam> params) throws OutputException;

    public void write(OutputParams params) throws OutputException;

    public OutputParams getParams();
}
