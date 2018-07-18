package de.uos.se.prom.dsmproject.bl.events;

import de.uos.se.prom.dsmproject.bl.event.Event;
import de.uos.se.prom.dsmproject.entity.Artifact;
import java.util.List;

/**
 *
 * @author dziegenhagen
 */
public class SelectionChanged extends Event {

    public final static String TOPIC = "SelectionChanged";

    @Override
    public String getTopic() {
        return TOPIC;
    }

    private final List<Artifact> added;
    private final List<Artifact> removed;

    public SelectionChanged(List<Artifact> added, List<Artifact> removed) {
        this.added = added;
        this.removed = removed;
    }

    public List<Artifact> getAdded() {
        return added;
    }

    public List<Artifact> getRemoved() {
        return removed;
    }

}
