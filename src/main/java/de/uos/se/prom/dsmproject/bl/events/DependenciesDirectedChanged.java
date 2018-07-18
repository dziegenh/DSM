package de.uos.se.prom.dsmproject.bl.events;

import de.uos.se.prom.dsmproject.bl.event.Event;

/**
 *
 * @author dziegenhagen
 */
public class DependenciesDirectedChanged extends Event {

    public final static String TOPIC = "DependenciesDirectedChanged";

    private final boolean directed;

    public DependenciesDirectedChanged(boolean directed) {
        this.directed = directed;
    }

    public boolean isDirected() {
        return directed;
    }

    public String getTopic() {
        return TOPIC;
    }

}
