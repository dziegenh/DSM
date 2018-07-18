package de.uos.se.prom.dsmproject.bl;

import de.uos.se.prom.dsmproject.bl.event.EventBus;
import de.uos.se.prom.dsmproject.bl.events.HiddenTypesChanged;
import de.uos.se.prom.dsmproject.bl.events.ProjectCreated;
import de.uos.se.prom.dsmproject.bl.events.ProjectLoaded;
import de.uos.se.prom.dsmproject.entity.Artifacttype;
import de.uos.se.prom.dsmproject.entity.DsmSorting;
import de.uos.se.prom.dsmproject.entity.Project;
import de.uos.se.prom.dsmproject.entity.View;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.apache.commons.collections4.CollectionUtils;

public class ViewEditor {

    private final HashMap<String, View> nameToViews = new HashMap<>();

    @Inject
    private SortController sortController;

    @Inject
    EventBus eventBus;

    private View activeView;

    public ViewEditor() {
        activeView = new View();
    }

    @PostConstruct
    public void initialize() {

        eventBus.addListener(ProjectCreated.TOPIC, (event) -> {
            ProjectCreated realEvent = (ProjectCreated) event;
            applyProjectData(realEvent.getProject());

        });
        eventBus.addListener(ProjectLoaded.TOPIC, (event) -> {
            ProjectLoaded realEvent = (ProjectLoaded) event;
            applyProjectData(realEvent.getProject());
        });
    }

    public void saveActiveView(String name) {
        activeView.setName(name);
        nameToViews.put(name, activeView);
        activeView = new View(activeView);
    }

    public void setActive(String viewname) {
        View view = nameToViews.get(viewname);
        if (null != view) {
            activeView = view;
        }
    }

    public void delete(String viewname) {
        this.nameToViews.remove(viewname);
    }

    public void setSorting(DsmSorting dsmSorting) {
        activeView.setSorting(dsmSorting);
        sortController.selectAlgorithm(dsmSorting);
        sortController.applyAlgorithm();
    }

    public void setHiddenTypes(List<Artifacttype> types) {
        List<Artifacttype> before = this.activeView.getHiddenTypes();

        this.activeView.setHiddenTypes(types);

        Collection<Artifacttype> addedHiddenTypes = CollectionUtils.subtract(types, before);
        Collection<Artifacttype> removedHiddenTypes = CollectionUtils.subtract(before, types);

        eventBus.fireEvent(new HiddenTypesChanged(addedHiddenTypes, removedHiddenTypes));
    }

    private void applyProjectData(Project project) {
        this.nameToViews.clear();
        final List<View> views = project.getViews();

        if (!views.isEmpty()) {
            for (View view : views) {
                this.nameToViews.put(view.getName(), view);
            }
            this.activeView = nameToViews.get(project.getActiveViewName());

        } else {
            this.activeView = new View();
            this.activeView.setSorting(DsmSorting.ARTIFACT_NAME);
        }

        setSorting(activeView.getSorting());
    }

    public List<Artifacttype> getHiddenTypes() {
        return this.activeView.getHiddenTypes();
    }

    public void addHiddentypes(Collection<Artifacttype> types) {
        LinkedList<Artifacttype> currentlyHidden = new LinkedList<>(activeView.getHiddenTypes());
        currentlyHidden.addAll(types);
        this.setHiddenTypes(currentlyHidden);
    }
}
