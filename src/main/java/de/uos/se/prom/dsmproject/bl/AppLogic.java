package de.uos.se.prom.dsmproject.bl;

import de.uos.se.prom.dsmproject.bl.event.EventBus;
import de.uos.se.prom.dsmproject.bl.events.AppClosing;
import de.uos.se.prom.dsmproject.bl.events.AutoAdjustDsmChanged;
import de.uos.se.prom.dsmproject.bl.events.AutoSortChanged;
import javax.inject.Inject;

/**
 * Contains actions which are performed at application start.
 *
 * @author dziegenhagen
 */
public class AppLogic {

    @Inject
    ProjectController projectController;

    @Inject
    SortController sortController;

    @Inject
    ViewEditor viewEditor;

    @Inject
    EventBus eventBus;

    public void postGui() {

        eventBus.fireEvent(new AutoAdjustDsmChanged(true));
        eventBus.fireEvent(new AutoSortChanged(true));

        // TODO store and read value from propoerties !!
//        if (dsmFile.exists()) {
//            projectController.loadProject(dsmFile);
//        } else {
            projectController.newProject();
//        }
    }

    public void preClose() {
        eventBus.fireEvent(new AppClosing());
    }
}
