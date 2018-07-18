package de.uos.se.prom.dsmproject.bl.events;

import de.uos.se.prom.dsmproject.bl.event.Event;
import de.uos.se.prom.dsmproject.entity.Artifact;
import java.util.List;

/**
 *
 * @author dziegenhagen
 */
public class SortingChanged extends Event {

    public final static String TOPIC = "SortingChanged";

    @Override
    public String getTopic() {
        return TOPIC;
    }

    List<Artifact> sortedArtifacts;

    public SortingChanged(List<Artifact> sortedArtifacts) {
        this.sortedArtifacts = sortedArtifacts;
    }

    public List<Artifact> getSortedArtifacts() {
        return sortedArtifacts;
    }

}
