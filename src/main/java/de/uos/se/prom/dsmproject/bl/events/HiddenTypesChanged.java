package de.uos.se.prom.dsmproject.bl.events;

import de.uos.se.prom.dsmproject.bl.event.Event;
import de.uos.se.prom.dsmproject.entity.Artifacttype;
import java.util.Collection;

/**
 *
 * @author dziegenhagen
 */
public class HiddenTypesChanged extends Event {

    public final static String TOPIC = "HiddenTypesChanged";

    private Collection<Artifacttype> hidden;
    private Collection<Artifacttype> shown;

    public HiddenTypesChanged(Collection<Artifacttype> hidden, Collection<Artifacttype> shown) {
        this.hidden = hidden;
        this.shown = shown;
    }

    public Collection<Artifacttype> getHidden() {
        return hidden;
    }

    public Collection<Artifacttype> getShown() {
        return shown;
    }

    @Override
    public String getTopic() {
        return TOPIC;
    }

}
