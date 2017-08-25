package net.parostroj.timetable.gui.actions.execution;

import java.awt.Component;
import java.awt.Frame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.akarnokd.rxjava2.swing.SwingObservable;
import hu.akarnokd.rxjava2.swing.SwingSchedulers;
import io.reactivex.Observable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.BiFunction;
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

    public void execute(ActionContext context, Observable<?> observable) {
        context.addPropertyChangeListener(waitDialog);
        context.setStartTime(System.currentTimeMillis());
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

    public <T> RxActionHandlerBuilder<T> newBuilder(String id, Component component, T object) {
        return new RxActionHandlerBuilder<>(new ActionContext(id, component), Observable.just(object));
    }

    public <T> RxActionHandlerBuilder<T> newBuilder(ActionContext context, T object) {
        return new RxActionHandlerBuilder<>(context, Observable.just(object));
    }

    public class RxActionHandlerBuilder<T> {

        private final ActionContext context;
        private final Observable<T> observable;

        private RxActionHandlerBuilder(ActionContext context, Observable<T> observable) {
            this.context = context;
            this.observable = observable;
        }

        public RxActionHandlerBuilder<T> addConsumer(BiConsumer<ActionContext, T> consumer) {
            return new RxActionHandlerBuilder<>(context, observable.filter(item -> !context.isCancelled()).doOnNext(t -> {
                consumer.accept(context, t);
            }));
        }

        public <U> RxActionHandlerBuilder<U> addAction(BiFunction<ActionContext, T, U> function) {
            return new RxActionHandlerBuilder<>(context, observable.filter(item -> !context.isCancelled()).map(t -> {
                return function.apply(context, t);
            }));
        }

        public <U> RxActionHandlerBuilder<U> addSplitAction(BiFunction<ActionContext, T, ? extends Iterable<U>> function) {
            return new RxActionHandlerBuilder<>(context, observable.filter(item -> !context.isCancelled()).flatMapIterable(t -> {
                return function.apply(context, t);
            }));
        }

        public <U> RxActionHandlerBuilder<U> addSplitObservable(BiFunction<ActionContext, T, Observable<U>> function) {
            return new RxActionHandlerBuilder<>(context, observable.filter(item -> !context.isCancelled()).flatMap(t -> {
                return function.apply(context, t);
            }));
        }

        public RxActionHandlerBuilder<T> onEdt() {
            return new RxActionHandlerBuilder<>(context, observable.compose(SwingObservable.observeOnEdt()));
        }

        public RxActionHandlerBuilder<T> onBackground() {
            return new RxActionHandlerBuilder<>(context, observable.observeOn(Schedulers.computation()));
        }

        public void execute() {
            RxActionHandler.this.execute(context, observable.observeOn(SwingSchedulers.edt()));
        }
    }

    public static class RxActionHandlerException extends RuntimeException {

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
