package de.uos.se.prom.dsmproject.bl.events;

import de.uos.se.prom.dsmproject.bl.event.Event;
import de.uos.se.prom.dsmproject.entity.Project;
import java.io.File;

/**
 *
 * @author dziegenhagen
 */
public class ProjectLoaded extends Event {

    public final static String TOPIC = "ProjectLoaded";

    private final Project project;

    private final File file;

    public ProjectLoaded(Project project, File file) {
        this.project = project;
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public Project getProject() {
        return project;
    }

    @Override
    public String getTopic() {
        return TOPIC;
    }

}
