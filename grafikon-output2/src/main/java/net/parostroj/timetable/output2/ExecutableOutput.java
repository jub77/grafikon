package net.parostroj.timetable.output2;

/**
 * Helper class which couples output and params.
 *
 * @author jub
 */
public class ExecutableOutput {

    private final Output output;
    private final OutputParams params;

    public ExecutableOutput(Output output, OutputParams params) {
        this.output = output;
        this.params = params;
    }

    public Output getOutput() {
        return output;
    }

    public OutputParams getParams() {
        return params;
    }

    public void execute() throws OutputException {
        output.write(params);
    }
}
