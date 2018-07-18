package de.uos.se.prom.dsmproject.gui.events;

import de.uos.se.prom.dsmproject.bl.event.Event;

/**
 *
 * @author dziegenhagen
 */
public class DsmSizeChanged extends Event {

    public final static String TOPIC = "DsmSizeChanged";

    @Override
    public String getTopic() {
        return TOPIC;
    }

}
