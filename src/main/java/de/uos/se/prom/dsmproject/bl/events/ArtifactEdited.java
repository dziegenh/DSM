package de.uos.se.prom.dsmproject.bl.events;

import de.uos.se.prom.dsmproject.bl.event.Event;
import de.uos.se.prom.dsmproject.entity.Artifact;

/**
 *
 * @author dziegenhagen
 */
public class ArtifactEdited extends Event {

    public final static String TOPIC = "ArtifactEdited";

    public String getTopic() {
        return TOPIC;
    }

    Artifact before, after;

    public ArtifactEdited(Artifact before, Artifact after) {
        this.before = before;
        this.after = after;
    }

    public Artifact getAfter() {
        return after;
    }

    public Artifact getBefore() {
        return before;
    }

}
