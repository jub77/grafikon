package net.parostroj.timetable.gui.actions.execution;

import java.awt.Component;
import java.awt.Frame;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.gui.dialogs.WaitDialog;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.swing.SwingScheduler;

/**
 * Action handler - executes model actions using Reactive Streams.
 *
 * @author jub
 */
public class RsActionHandler {

    private static final Logger log = LoggerFactory.getLogger(RsActionHandler.class);

    private static final RsActionHandler instance = new RsActionHandler();

    public static RsActionHandler getInstance() {
        return instance;
    }

    public static RsActionHandler createInstance() {
        return new RsActionHandler();
    }

    private WaitDialog waitDialog;

    private RsActionHandler() {
        waitDialog = new WaitDialog((Frame) null, true);
    }

    public void execute(Execution<?> execution) {
        ActionContext context = execution.context;
        Flux<?> observable = execution.observable;
        context.addPropertyChangeListener(waitDialog);
        context.setStartTime(System.currentTimeMillis());
        observable = observable.publishOn(SwingScheduler.create());
        observable.subscribe(next -> {
            // final value is ignored
        }, exception -> {
            context.setWaitDialogVisible(false);
            Throwable toLog = exception;
            if (exception instanceof RsActionHandlerException)
            {
                toLog = exception.getCause() == null ? exception : exception.getCause();
                GuiComponentUtils.showError(
                        ((RsActionHandlerException) exception).getText(),
                        context.getLocationComponent());
            }
            log.warn("Action {} finished in {}ms with an exception",
                    context.getId(),
                    System.currentTimeMillis() - context.getStartTime(),
                    toLog);
        }, () -> {
            context.setWaitDialogVisible(false);
            if (context.isLogTime()) {
                log.debug("Action {} finished in {}ms",
                        context.getId(),
                        System.currentTimeMillis() - context.getStartTime());
            }
        });
    }

    public <T> Execution<T> newExecution(String id, Component component, T object) {
        return new Execution<>(new ActionContext(id, component), Flux.just(object));
    }

    public <T> Execution<T> newExecution(ActionContext context, T object) {
        return new Execution<>(context, Flux.just(object));
    }

    public class Execution<T> {

        protected final ActionContext context;
        protected final Flux<T> observable;

        protected Execution(ActionContext context, Flux<T> observable) {
            this.context = context;
            this.observable = observable;
        }

        public Execution<T> addConsumer(BiConsumer<ActionContext, T> consumer) {
            return new Execution<>(context,
                    observable.filter(item -> !context.isCancelled()).doOnNext(t -> consumer.accept(context, t)));
        }

        public <U> Execution<U> addAction(BiFunction<ActionContext, T, U> function) {
            return new Execution<>(context,
                    observable.filter(item -> !context.isCancelled()).map(t -> function.apply(context, t)));
        }

        public <U> Execution<U> addSplitAction(BiFunction<ActionContext, T, ? extends Iterable<U>> function) {
            return new Execution<>(context,
                    observable.filter(item -> !context.isCancelled()).flatMapIterable(t -> function.apply(context, t)));
        }

        public <U> Execution<U> addSplitObservable(BiFunction<ActionContext, T, Flux<U>> function) {
            return new Execution<>(context,
                    observable.filter(item -> !context.isCancelled()).flatMap(t -> function.apply(context, t)));
        }

        public Execution<T> onEdt() {
            return new Execution<>(context, observable.publishOn(SwingScheduler.create()));
        }

        public Execution<T> onBackground() {
            return new Execution<>(context, observable.publishOn(Schedulers.parallel()));
        }

        public void execute() {
            RsActionHandler.this.execute(this);
        }


        public Execution<T> setMessage(String message) {
            return new Execution<>(context, observable.filter(item -> !context.isCancelled()).doFirst(() -> {
                context.setWaitMessage(message);
                context.setProgress(0);
                context.setShowProgress(true);
                context.setWaitDialogVisible(true);
            }));
        }

        public Execution<T> logTime() {
            return new Execution<>(context, observable.filter(item -> !context.isCancelled()).doFirst(() -> {
                context.setLogTime(true);
            }));
        }

        public <Y> BatchExecution<Y> split(Function<T, Collection<Y>> mapping, int chunkSize) {
            return new BatchExecution<>(context, observable
                    .filter(item -> !context.isCancelled())
                    .flatMap(t -> {
                        Collection<Y> allItems = mapping.apply(t);
                        context.setAttribute("total", allItems.size());
                        context.setAttribute("current", 0);
                        return Flux.fromIterable(allItems).buffer(chunkSize);
                    }));
        }
    }

    public class BatchExecution<T> extends Execution<List<T>> {

        protected BatchExecution(ActionContext context, Flux<List<T>> observable) {
            super(context, observable);
        }

        public BatchExecution<T> addBatchConsumer(BiConsumer<ActionContext, T> consumer) {
            return new BatchExecution<>(context, observable.filter(item -> !context.isCancelled()).doOnNext(values -> {
                for (T value : values) {
                    consumer.accept(context, value);
                }
                updateProgress(values);
            }));
        }

        public BatchExecution<T> addEdtBatchConsumer(BiConsumer<ActionContext, T> consumer) {
            return new BatchExecution<>(context, observable.filter(item -> !context.isCancelled()).doOnNext(values -> {
                GuiComponentUtils.runNowInEDT(() -> {
                    try {
                        for (T value : values) {
                            consumer.accept(context, value);
                        }
                    } catch (Exception e) {
                        context.setAttribute("exception", e);
                    }
                });
                checkException();
                updateProgress(values);
            }));
        }

        private void updateProgress(List<T> values) {
            int currentSize = context.getAttribute("current", Integer.class) + values.size();
            context.setAttribute("current", currentSize);
            context.setProgress(currentSize * 100 / context.getAttribute("total", Integer.class));
        }

        private void checkException() throws RuntimeException {
            Exception exception = context.getAttribute("exception", Exception.class);
            if (exception != null) {
                if (exception instanceof RuntimeException) {
                    throw (RuntimeException) exception;
                } else {
                    throw new RuntimeException(exception);
                }
            }
        }

        @Override
        public BatchExecution<T> onBackground() {
            return wrap(super.onBackground());
        }

        @Override
        public BatchExecution<T> onEdt() {
            return wrap(super.onEdt());
        }

        private BatchExecution<T> wrap(Execution<List<T>> exec) {
            return new BatchExecution<>(exec.context, exec.observable);
        }
    }

    public static class RsActionHandlerException extends RuntimeException {

        private static final long serialVersionUID = 1L;

        private final String text;

        public RsActionHandlerException(String text, String message, Throwable cause) {
            super(message, cause);
            this.text = text;
        }

        public RsActionHandlerException(String text, String message) {
            super(message);
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }
}
