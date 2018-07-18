package de.uos.se.prom.dsmproject.bl.events;

import de.uos.se.prom.dsmproject.bl.event.Event;
import de.uos.se.prom.dsmproject.entity.Project;

/**
 *
 * @author dziegenhagen
 */
public class ProjectCreated extends Event {

    public final static String TOPIC = "ProjectCreated";

    private Project project;

    public ProjectCreated(Project project) {
        this.project = project;
    }

    public Project getProject() {
        return project;
    }

    @Override
    public String getTopic() {
        return TOPIC;
    }

}
