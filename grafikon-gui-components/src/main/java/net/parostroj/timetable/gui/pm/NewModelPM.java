package net.parostroj.timetable.gui.pm;

import java.math.BigDecimal;
import java.util.concurrent.Callable;

import net.parostroj.timetable.model.Scale;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.ls.LSException;
import net.parostroj.timetable.model.templates.Template;
import net.parostroj.timetable.model.templates.TemplateLoader;

import org.beanfabrics.model.*;
import org.beanfabrics.support.Operation;
import org.beanfabrics.support.Validation;
import org.beanfabrics.validation.ValidationState;

public class NewModelPM extends AbstractPM {

    private static final String TIME_SCALE_ERROR_MESSAGE = "";
    private static final double INIT_TIME_SCALE = 5.0;
    private static final double MIN_TIME_SCALE = 1.0;
    private static final double MAX_TIME_SCALE = 100;
    private static final double SELECTION_MAX_TIME_SCALE = 10.0;
    private static final double TIME_SCALE_STEP = 0.5;

    final IEnumeratedValuesPM<Scale> scale = new EnumeratedValuesPM<>(EnumeratedValuesPM.createValueMap(
            Scale.getPredefined(), i -> i.getName()));
    final BigDecimalPM timeScale = new BigDecimalPM();
    final IEnumeratedValuesPM<Template> template;

    final OperationPM ok = new OperationPM();

    private Callable<TrainDiagram> createTask;

    private TemplateLoader<TrainDiagram> templateLoader;

    public NewModelPM(TemplateLoader<TrainDiagram> templateLoader) throws LSException {
        this.templateLoader = templateLoader;

        // time scale
        Options<BigDecimal> options = new Options<>();
        IFormat<BigDecimal> format = timeScale.getFormat();
        for (double d = MIN_TIME_SCALE; d <= SELECTION_MAX_TIME_SCALE; d += TIME_SCALE_STEP) {
            BigDecimal bigDecimal = BigDecimal.valueOf(d);
            options.put(bigDecimal, format.format(bigDecimal));
        }
        timeScale.setRestrictedToOptions(false);
        timeScale.setMandatory(true);
        timeScale.setOptions(options);
        timeScale.getValidator().add(() -> {
            double value = timeScale.getBigDecimal().doubleValue();
            return value >= MIN_TIME_SCALE && value <= MAX_TIME_SCALE ? null : new ValidationState(TIME_SCALE_ERROR_MESSAGE);
        });
        timeScale.setBigDecimal(BigDecimal.valueOf(INIT_TIME_SCALE));
        // templates
        template = new EnumeratedValuesPM<>(EnumeratedValuesPM.createValueMap(templateLoader.getTemplateList().getTemplates(),
                i -> i.getName().translate()));
        // setup
        PMManager.setup(this);
    }

    public void init() {
    }

    public Callable<TrainDiagram> getResult() {
        Callable<TrainDiagram> result = this.createTask;
        this.createTask = null;
        return result;
    }

    private void create() {
        final Template templateInstance = template.getValue();
        final Scale scaleValue = scale.getValue();
        final double timeScaleValue = timeScale.getBigDecimal().doubleValue();

        this.createTask = new Callable<TrainDiagram>() {

            @Override
            public TrainDiagram call() throws LSException {
                TrainDiagram diagram = templateLoader.loadTemplate(templateInstance);
                diagram.setAttribute(TrainDiagram.ATTR_SCALE, scaleValue);
                diagram.setAttribute(TrainDiagram.ATTR_TIME_SCALE, timeScaleValue);
                return diagram;
            }
        };
    }

    @Operation(path = "ok")
    public boolean ok() {
        this.create();
        return true;
    }

    @Validation(path = "ok")
    public boolean check() {
        return timeScale.isValid();
    }

}
