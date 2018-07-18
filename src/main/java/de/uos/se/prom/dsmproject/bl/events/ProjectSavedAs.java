package de.uos.se.prom.dsmproject.bl.events;

import de.uos.se.prom.dsmproject.bl.event.Event;
import java.io.File;

/**
 *
 * @author dziegenhagen
 */
public class ProjectSavedAs extends Event {

    public final static String TOPIC = "ProjectSavedAs";

    private final File file;

    public ProjectSavedAs(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    @Override
    public String getTopic() {
        return TOPIC;
    }

}
