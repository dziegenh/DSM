package de.uos.se.prom.dsmproject.bl.events;

import de.uos.se.prom.dsmproject.bl.event.Event;

/**
 *
 * @author dziegenhagen
 */
public class ProjectRenamed extends Event {

    public final static String TOPIC = "ProjectRenamed";

    private final String newName;

    public ProjectRenamed(String newName) {
        this.newName = newName;
    }

    public String getNewName() {
        return newName;
    }

    @Override
    public String getTopic() {
        return TOPIC;
    }

}
