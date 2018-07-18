package de.uos.se.prom.dsmproject.bl.events;

import de.uos.se.prom.dsmproject.bl.event.Event;

/**
 *
 * @author dziegenhagen
 */
public class AdjustDsmViewport extends Event {

    public final static String TOPIC = "AdjustDsmViewport";

    @Override
    public String getTopic() {
        return TOPIC;
    }

}
