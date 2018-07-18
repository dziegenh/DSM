package de.uos.se.prom.dsmproject.bl.events;

import de.uos.se.prom.dsmproject.bl.event.Event;

/**
 *
 * @author dziegenhagen
 */
public class DependenciesWeightedChanged extends Event {

    public final static String TOPIC = "DependenciesWeightedChanged";

    private final boolean weighted;

    public DependenciesWeightedChanged(boolean weighted) {
        this.weighted = weighted;
    }

    public boolean isWeighted() {
        return weighted;
    }

    public String getTopic() {
        return TOPIC;
    }

}
