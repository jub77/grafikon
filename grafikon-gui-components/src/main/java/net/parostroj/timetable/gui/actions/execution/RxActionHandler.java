package net.parostroj.timetable.gui.actions.execution;

import java.awt.Component;
import java.awt.Frame;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.akarnokd.rxjava2.swing.SwingObservable;
import hu.akarnokd.rxjava2.swing.SwingSchedulers;
import io.reactivex.Observable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import net.parostroj.timetable.gui.dialogs.WaitDialog;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;

/**
 * Action handler - executes model actions using RxJava.
 *
 * @author jub
 */
public class RxActionHandler {

    private static final Logger log = LoggerFactory.getLogger(RxActionHandler.class);

    private static final RxActionHandler instance = new RxActionHandler();

    public static RxActionHandler getInstance() {
        return instance;
    }

    public static RxActionHandler createInstance() {
        return new RxActionHandler();
    }

    private WaitDialog waitDialog;

    private RxActionHandler() {
        waitDialog = new WaitDialog((Frame) null, true);
    }

    public void execute(Execution<?> execution) {
        ActionContext context = execution.context;
        Observable<?> observable = execution.observable;
        context.addPropertyChangeListener(waitDialog);
        context.setStartTime(System.currentTimeMillis());
        observable = observable.observeOn(SwingSchedulers.edt());
        observable.subscribe(next -> {
            // final value is ignored
        }, exception -> {
            context.setWaitDialogVisible(false);
            Throwable toLog = exception;
            if (exception instanceof RxActionHandlerException)
            {
                toLog = exception.getCause() == null ? exception : exception.getCause();
                GuiComponentUtils.showError(
                        ((RxActionHandlerException) exception).getText(),
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
        return new Execution<>(new ActionContext(id, component), Observable.just(object));
    }

    public <T> Execution<T> newExecution(ActionContext context, T object) {
        return new Execution<>(context, Observable.just(object));
    }

    public class Execution<T> {

        protected final ActionContext context;
        protected final Observable<T> observable;

        protected Execution(ActionContext context, Observable<T> observable) {
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

        public <U> Execution<U> addSplitObservable(BiFunction<ActionContext, T, Observable<U>> function) {
            return new Execution<>(context,
                    observable.filter(item -> !context.isCancelled()).flatMap(t -> function.apply(context, t)));
        }

        public Execution<T> onEdt() {
            return new Execution<>(context, observable.compose(SwingObservable.observeOnEdt()));
        }

        public Execution<T> onBackground() {
            return new Execution<>(context, observable.observeOn(Schedulers.computation()));
        }

        public void execute() {
            RxActionHandler.this.execute(this);
        }


        public Execution<T> setMessage(String message) {
            context.setWaitMessage(message);
            context.setProgress(0);
            context.setShowProgress(true);
            context.setWaitDialogVisible(true);
            return this;
        }

        public Execution<T> logTime() {
            context.setLogTime(true);
            return this;
        }

        public <Y> BatchExecution<Y> split(Function<T, Collection<Y>> mapping, int chunkSize) {
            return new BatchExecution<>(context, observable
                    .filter(item -> !context.isCancelled())
                    .flatMap(t -> {
                        Collection<Y> allItems = mapping.apply(t);
                        context.setAttribute("total", allItems.size());
                        context.setAttribute("current", 0);
                        return Observable.fromIterable(allItems).buffer(chunkSize);
                    }));
        }
    }

    public class BatchExecution<T> extends Execution<List<T>> {

        protected BatchExecution(ActionContext context, Observable<List<T>> observable) {
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

        private void checkException() throws Exception {
            Exception exception = context.getAttribute("exception", Exception.class);
            if (exception != null) {
                throw exception;
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

    public static class RxActionHandlerException extends RuntimeException {

        private static final long serialVersionUID = 1L;

        private final String text;

        public RxActionHandlerException(String text, String message, Throwable cause) {
            super(message, cause);
            this.text = text;
        }

        public RxActionHandlerException(String text, String message) {
            super(message);
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }
}
