package de.uos.se.prom.dsmproject.bl.events;

import de.uos.se.prom.dsmproject.bl.event.Event;

/**
 *
 * @author dziegenhagen
 */
public class ProjectSaved extends Event {

    public final static String TOPIC = "ProjectSaved";

    @Override
    public String getTopic() {
        return TOPIC;
    }

}
