package de.uos.se.prom.dsmproject.bl.events;

import de.uos.se.prom.dsmproject.bl.event.Event;
import de.uos.se.prom.dsmproject.entity.Artifact;

/**
 *
 * @author dziegenhagen
 */
public class ArtifactAdded extends Event {

    public final static String TOPIC = "ArtifactAdded";

    private Artifact artifact;

    public ArtifactAdded(Artifact artifact) {
        this.artifact = artifact;
    }

    public Artifact getArtifact() {
        return artifact;
    }

    @Override
    public String getTopic() {
        return TOPIC;
    }

}
