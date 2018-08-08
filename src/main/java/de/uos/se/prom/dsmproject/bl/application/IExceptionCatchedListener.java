package de.uos.se.prom.dsmproject.bl.application;

/**
 *
 * @author dziegenh
 */
public interface IExceptionCatchedListener {

    /**
     * Deligates an exception to the listeners.
     *
     * @param exception
     */
    public void exceptionCatched(Exception exception);

}
