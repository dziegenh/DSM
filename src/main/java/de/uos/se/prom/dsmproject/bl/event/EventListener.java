package de.uos.se.prom.dsmproject.bl.event;

/**
 *
 * @author dziegenhagen
 */
public interface EventListener<E extends Event> {

    void eventOccured(E event);
    
}
