package de.uos.se.prom.dsmproject.bl.events;

import de.uos.se.prom.dsmproject.bl.event.Event;

/**
 *
 * @author dziegenhagen
 */
public class AppClosing extends Event {

    public final static String TOPIC = "AppClosing";

    @Override
    public String getTopic() {
        return TOPIC;
    }

}
