package de.uos.se.prom.dsmproject.bl.application;

import java.util.LinkedList;
import java.util.List;

/**
 * Exception Delegation.
 *
 * @author dziegenh
 */
public class ExceptionHandler {

    private List<IExceptionCatchedListener> listeners = new LinkedList<>();

    /**
     * Adds a listener which is informed if any exception is catched.
     *
     * @param listener
     */
    public void addExceptionCatchedListener(IExceptionCatchedListener listener) {
        this.listeners.add(listener);
    }

    public void removeExceptionCatchedListener(IExceptionCatchedListener listener) {
        this.listeners.remove(listener);
    }

    /**
     * Deligates an exception to the listeners.
     *
     * @param exception
     */
    public void exceptionCatched(Exception exception) {
        for (IExceptionCatchedListener listener : listeners) {
            listener.exceptionCatched(exception);
        }
    }

}
