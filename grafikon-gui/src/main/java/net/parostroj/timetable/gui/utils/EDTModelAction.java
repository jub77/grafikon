package net.parostroj.timetable.gui.utils;

import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.SwingWorker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Model action with chunks of work executed in EDT thread.
 * 
 * @author jub
 */
abstract public class EDTModelAction<T> extends SwingWorker<Void, T>{
    
    private static final Logger LOG = LoggerFactory.getLogger(EDTModelAction.class);

    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();
    private boolean chunksFinished = false;
    private long time;
    
    protected String actionName;
    
    public EDTModelAction(String actionName) {
        this.actionName = actionName;
    }
    
    public EDTModelAction() {
        this("Action");
    }

    @Override
    protected Void doInBackground() throws Exception {
        time = System.currentTimeMillis();
        lock.lock();
        try {
            while (prepareItems()) {
                while (!chunksFinished) {
                    condition.await();
                }
            }
        } finally {
            lock.unlock();
        }
        return null;
    }
    
    protected abstract void itemsFinished();
    
    protected abstract boolean prepareItems() throws Exception;
    
    protected void addItems(T... chunks) {
        if (chunks != null && chunks.length > 0)
            chunksFinished = false;
        this.publish(chunks);
    }
    
    @Override
    protected void done() {
        time = System.currentTimeMillis() - time;
        LOG.debug("{} finished in {}ms", actionName, time);
        this.itemsFinished();
    }
    
    @Override
    protected void process(List<T> chunks) {
        LOG.trace("Process chunk of items. Size: " + chunks.size());
        lock.lock();
        try {
            for (T item : chunks) {
                this.processItem(item);
            }
        } catch (Exception e) {
            LOG.error("Error processing item: " + e.getMessage(), e);
        } finally {
            chunksFinished = true;
            condition.signalAll();
            lock.unlock();
        }
    }
    
    protected abstract void processItem(T item) throws Exception;
}
