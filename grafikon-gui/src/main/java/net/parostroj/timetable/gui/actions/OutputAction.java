package net.parostroj.timetable.gui.actions;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.*;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.dialogs.ElementSelectionDialog;
import net.parostroj.timetable.gui.dialogs.SelectNodesDialog;
import net.parostroj.timetable.gui.dialogs.TemplateSelectDialog;
import net.parostroj.timetable.gui.utils.ActionHandler;
import net.parostroj.timetable.gui.utils.ModelAction;
import net.parostroj.timetable.model.TrainsCycle;
import net.parostroj.timetable.model.TrainsCycleType;
import net.parostroj.timetable.output2.*;
import net.parostroj.timetable.utils.ResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Output action.
 *
 * @author jub
 */
public class OutputAction extends AbstractAction {

    private static final Logger LOG = LoggerFactory.getLogger(OutputAction.class.getName());
    private ApplicationModel model;
    private Component parent;
    private TemplateSelectDialog templateSelectDialog;

    // selection variables
    private File outputFile;
    private File templateFile;
    private OutputType outputType;
    private Object selection;

    public OutputAction(ApplicationModel model, Frame frame) {
        this.model = model;
        this.templateSelectDialog = new TemplateSelectDialog(frame, true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        parent = ActionUtils.getTopLevelComponent(e.getSource());
        outputFile = null;
        templateFile = null;
        outputType = OutputType.fromActionCommand(e.getActionCommand());
        selection = null;

        if (!makeSelection())
            return;
        if (!selectTemplate())
            return;
        if (!selectOutput())
            return;
        try {
            if (outputType.isOutputFile())
                this.singleOutput();
            else
                this.multipleOutputs();
        } catch (Exception ex) {
            LOG.warn(ex.getMessage(), ex);
            String errorMessage = ResourceLoader.getString("dialog.error.saving");
            ActionUtils.showError(errorMessage + ": " + ex.getMessage(), parent);
        }
    }

    private boolean selectTemplate() {
        OutputCategory category = model.getOutputCategory();
        if (category.isTemplateSelect() && outputType != OutputType.ALL) {
            if (templateSelectDialog.selectTemplate(
                    FileChooserFactory.getInstance().getFileChooser(FileChooserFactory.Type.TEMPLATE),
                    model.getOutputTemplates().get(outputType.getOutputType()))) {
                templateFile = templateSelectDialog.getTemplate();
                if (templateFile != null && templateFile.canRead()) {
                    model.getOutputTemplates().put(outputType.getOutputType(), templateFile);
                }
            }
            return templateFile != null && templateFile.canRead();
        }
        return true;
    }

    private boolean selectOutput() {
        JFileChooser chooser = null;
        if (outputType.isOutputFile()) {
            OutputCategory category = model.getOutputCategory();
            chooser = FileChooserFactory.getInstance().getFileChooser(FileChooserFactory.Type.OUTPUT, category.getSuffix(), ResourceLoader.getString("output." + category.getSuffix()));
        } else {
            chooser = FileChooserFactory.getInstance().getFileChooser(FileChooserFactory.Type.OUTPUT_DIRECTORY);
        }
        int retVal = chooser.showSaveDialog(parent);
        if (retVal == JFileChooser.APPROVE_OPTION) {
            outputFile = chooser.getSelectedFile();
            return outputFile != null;
        } else {
            return false;
        }
    }

    private Frame getFrame() {
        if (parent instanceof Frame) {
            return (Frame) parent;
        } else {
            return null;
        }
    }

    private boolean makeSelection() {
        if (outputType.isSelection()) {
            ElementSelectionDialog<Object> selDialog = new ElementSelectionDialog<Object>(getFrame(), true);
            selDialog.setLocationRelativeTo(parent);
            selection = selDialog.selectElements(ModelUtils.selectAllElements(model.getDiagram(), outputType.getSelectionElement()));
            if (selection == null)
                return false;
        } else if (outputType == OutputType.TRAINS_SELECT_STATION) {
            SelectNodesDialog dialog = new SelectNodesDialog(getFrame(), true);
            dialog.setNodes(model.getDiagram().getNet().getNodes());
            dialog.setLocationRelativeTo(parent);
            dialog.setVisible(true);
            selection = dialog.getSelectedNode();

            if (selection == null)
                return false;
        } else if (outputType == OutputType.TRAINS_BY_DRIVER_CYCLES) {
            selection = model.getDiagram().getCycles(TrainsCycleType.DRIVER_CYCLE);
        }
        return true;
    }

    private void singleOutput() throws OutputException {
        Output output = this.createOutput(outputType);
        Object select = null;
        if (outputType.isSelection() || outputType.getSelectionParam() != null) {
            select = selection;
        }
        OutputParams params = this.createParams(output, outputFile, select, outputType);
        this.saveOutputs(Collections.singletonList(new ExecutableOutput(output, params)));
    }

    private void multipleOutputs() throws OutputException {
        List<ExecutableOutput> eOutputs = new LinkedList<ExecutableOutput>();
        if (outputType.getOutputType() != null) {
            Output output = this.createOutput(outputType);
            if (selection instanceof Collection<?>) {
                Collection<?> c = (Collection<?>)selection;
                for (Object item : c) {
                    OutputParams params = this.createParams(output, createUniqueOutputFile(item, outputFile, outputType), item, outputType);
                    eOutputs.add(new ExecutableOutput(output, params));
                }
            }
        } else if (outputType == OutputType.ALL) {
            // save all
            String suffix = model.getOutputCategory().getSuffix();
            OutputFactory of = OutputFactory.newInstance(model.getOutputCategory().getOutputFactoryType());
            of.setParameter("locale", model.getOutputLocale());
            // stations
            Output output = of.createOutput("stations");
            File oFile = new File(outputFile, ResourceLoader.getString("out.nodes") + "." + suffix);
            eOutputs.add(new ExecutableOutput(output, this.createParams(output, oFile, null, OutputType.STATIONS)));
            // trains
            output = of.createOutput("trains");
            oFile = new File(outputFile, ResourceLoader.getString("out.trains") + "." + suffix);
            eOutputs.add(new ExecutableOutput(output, this.createParams(output, oFile, null, OutputType.TRAINS)));
            // engine cycles
            output = of.createOutput("engine_cycles");
            oFile = new File(outputFile, ResourceLoader.getString("out.ec") + "." + suffix);
            eOutputs.add(new ExecutableOutput(output, this.createParams(output, oFile, null, OutputType.ENGINE_CYCLES)));
            // train unit cycles
            output = of.createOutput("train_unit_cycles");
            oFile = new File(outputFile, ResourceLoader.getString("out.tuc") + "." + suffix);
            eOutputs.add(new ExecutableOutput(output, this.createParams(output, oFile, null, OutputType.TRAIN_UNIT_CYCLES)));
            // driver cycles
            output = of.createOutput("driver_cycles");
            oFile = new File(outputFile, ResourceLoader.getString("out.dc") + "." + suffix);
            eOutputs.add(new ExecutableOutput(output, this.createParams(output, oFile, null, OutputType.DRIVER_CYCLES)));
            // start positions
            output = of.createOutput("starts");
            oFile = new File(outputFile, ResourceLoader.getString("out.sp") + "." + suffix);
            eOutputs.add(new ExecutableOutput(output, this.createParams(output, oFile, null, OutputType.STARTS)));
            // end positions
            output = of.createOutput("ends");
            oFile = new File(outputFile, ResourceLoader.getString("out.ep") + "." + suffix);
            eOutputs.add(new ExecutableOutput(output, this.createParams(output, oFile, null, OutputType.ENDS)));
            // tt by dc
            List<TrainsCycle> cycles = model.getDiagram().getCycles(TrainsCycleType.DRIVER_CYCLE);
            output = of.createOutput("trains");
            for (TrainsCycle cycle : cycles) {
                OutputParams params = this.createParams(output, createUniqueOutputFile(cycle, outputFile, OutputType.TRAINS_BY_DRIVER_CYCLES), cycle, OutputType.TRAINS_BY_DRIVER_CYCLES);
                eOutputs.add(new ExecutableOutput(output, params));
            }
        }

        this.saveOutputs(eOutputs);
    }

    private File createUniqueOutputFile(Object item, File directory, OutputType type) throws OutputException {
        if (type == OutputType.TRAINS_SELECT_DRIVER_CYCLES || type == OutputType.TRAINS_BY_DRIVER_CYCLES) {
            TrainsCycle cycle = (TrainsCycle)item;
            return new File(directory, ResourceLoader.getString("out.trains") + "_" + cycle.getName() + "." + model.getOutputCategory().getSuffix());
        }
        throw new OutputException("Error creating filenames of output files.");
    }

    private Output createOutput(OutputType type) throws OutputException {
        OutputFactory of = OutputFactory.newInstance(model.getOutputCategory().getOutputFactoryType());
        of.setParameter("locale", model.getOutputLocale());
        Output output = of.createOutput(type.getOutputType());
        return output;
    }

    private OutputParams createParams(Output output, File file, Object select, OutputType type) throws OutputException {
        // check file name for not allowed characters and some other also (e.g. ' ' - space)
        String name = file.getName();
        File parentFile = file.getParentFile();
        name = name.replaceAll("[\\\\:/\"?<>|]", "");
        if (parentFile == null)
            file = new File(name);
        else
            file = new File(parentFile, name);
        OutputParams params = output.getAvailableParams();
        // diagram
        params.setParam(DefaultOutputParam.TRAIN_DIAGRAM, model.getDiagram());
        // template
        if (templateFile != null) {
            try {
                params.setParam(DefaultOutputParam.TEMPLATE_STREAM, new FileInputStream(templateFile));
            } catch (FileNotFoundException e) {
                throw new OutputException(e);
            }
        }
        // selections
        if (select != null) {
            params.setParam(type.getSelectionParam(), select);
        }
        params.setParam(DefaultOutputParam.OUTPUT_FILE, file);
        if (type != null && type.getOutputType().equals("trains")) {
            params.setParam("title.page", model.getProgramSettings().isGenerateTitlePageTT());
        }
        return params;
    }

    private void saveOutputs(final Collection<ExecutableOutput> outputs) {
        ActionHandler.getInstance().executeAction(parent, ResourceLoader.getString("wait.message.genoutput"), new ModelAction() {

            private String errorMessage;

            @Override
            public void run() {
                long time = System.currentTimeMillis();
                try {
                    for (ExecutableOutput output : outputs) {
                        output.execute();
                    }
                } catch (Exception e) {
                    LOG.warn(e.getMessage(), e);
                    errorMessage = ResourceLoader.getString("dialog.error.saving");
                }
                time = System.currentTimeMillis() - time;
                LOG.debug("Generated in {}ms", time);
            }

            @Override
            public void afterRun() {
                if (errorMessage != null) {
                    ActionUtils.showError(errorMessage + " " + outputFile.getName(), parent);
                }
            }
        });
    }
}
