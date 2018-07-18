package de.uos.se.prom.dsmproject.bl.events;

import de.uos.se.prom.dsmproject.bl.event.Event;
import de.uos.se.prom.dsmproject.entity.Dependency;

/**
 *
 * @author dziegenhagen
 */
public class DependencyAdded extends Event {

    public final static String TOPIC = "DependencyAdded";

    private final Dependency dependency;

    public DependencyAdded(Dependency dependency) {
        this.dependency = dependency;
    }

    public Dependency getDependency() {
        return dependency;
    }

    @Override
    public String getTopic() {
        return TOPIC;
    }

}
