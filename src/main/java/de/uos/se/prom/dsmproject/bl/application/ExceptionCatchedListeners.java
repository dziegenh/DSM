package de.uos.se.prom.dsmproject.bl.application;

/**
 *
 * @author dziegenh
 */
public interface ExceptionCatchedListeners {

    /**
     * Called if any exception was catched.
     *
     * @param exception
     */
    public void exceptionCatched(Exception exception);

}
