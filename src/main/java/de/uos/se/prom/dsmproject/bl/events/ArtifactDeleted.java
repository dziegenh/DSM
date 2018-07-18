package de.uos.se.prom.dsmproject.bl.events;

import de.uos.se.prom.dsmproject.bl.event.Event;
import de.uos.se.prom.dsmproject.entity.Artifact;

/**
 *
 * @author dziegenhagen
 */
public class ArtifactDeleted extends Event {

    public final static String TOPIC = "ArtifactDeleted";

    private Artifact artifact;

    public ArtifactDeleted(Artifact artifact) {
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
