package de.uos.se.prom.dsmproject.bl.events;

import de.uos.se.prom.dsmproject.bl.event.Event;

/**
 *
 * @author dziegenhagen
 */
public class AutoSortChanged extends Event {

    public final static String TOPIC = "AutoSortChanged";

    public String getTopic() {
        return TOPIC;
    }

    private final boolean autoSort;

    public AutoSortChanged(boolean autoSort) {
        this.autoSort = autoSort;
    }

    public boolean isAutoSort() {
        return autoSort;
    }

}
