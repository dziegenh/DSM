package de.uos.se.prom.dsmproject.bl.events;

import de.uos.se.prom.dsmproject.bl.event.Event;

/**
 *
 * @author dziegenhagen
 */
public class AutoAdjustDsmChanged extends Event {

    public final static String TOPIC = "AutoAdjustDsmChanged";

    public String getTopic() {
        return TOPIC;
    }

    private final boolean autoAdjust;

    public AutoAdjustDsmChanged(boolean autoAdjust) {
        this.autoAdjust = autoAdjust;
    }

    public boolean isAutoAdjust() {
        return autoAdjust;
    }

}
