package de.uos.se.prom.dsmproject.bl;

import de.uos.se.prom.dsmproject.bl.event.Event;
import de.uos.se.prom.dsmproject.bl.event.EventBus;
import de.uos.se.prom.dsmproject.bl.event.EventListener;
import de.uos.se.prom.dsmproject.bl.events.ArtifactDeleted;
import de.uos.se.prom.dsmproject.bl.events.ArtifactEdited;
import de.uos.se.prom.dsmproject.bl.events.ProjectCreated;
import de.uos.se.prom.dsmproject.bl.events.ProjectLoaded;
import de.uos.se.prom.dsmproject.bl.events.SelectionChanged;
import de.uos.se.prom.dsmproject.entity.Artifact;
import de.uos.se.prom.dsmproject.entity.Artifacttype;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.apache.commons.collections4.CollectionUtils;

/**
 *
 * @author dziegenhagen
 */
public class SelectionController {

    @Inject
    EventBus eventBus;

    @Inject
    ProjectEditor projectEditor;

    List<Artifact> selection = new LinkedList<>();

    @PostConstruct
    public void initialize() {
        eventBus.addListener(ArtifactEdited.TOPIC, (event) -> {
            ArtifactEdited realEvent = (ArtifactEdited) event;
            Artifact before = realEvent.getBefore();
            Artifact after = realEvent.getAfter();
            if (selection.contains(before)) {
                selection.remove(before);
                selection.add(after);
            }
        });

        eventBus.addListener(ArtifactDeleted.TOPIC, (event) -> {
            ArtifactDeleted realEvent = (ArtifactDeleted) event;
            this.selection.remove(realEvent.getArtifact());
        });

        EventListener<? extends Event> projectChangedHandler = (Event event) -> {
            this.selection.clear();
        };

        eventBus.addListener(ProjectCreated.TOPIC, projectChangedHandler);
        eventBus.addListener(ProjectLoaded.TOPIC, projectChangedHandler);
    }

    public void select(Artifact artifact) {
        LinkedList<Artifact> deselect = new LinkedList<>(selection);

        this.selection.clear();
        this.selection.add(artifact);

        fireSelectionChanged(selection, deselect);
    }

    public void select(List<Artifact> artifacts) {
        LinkedList<Artifact> deselect = new LinkedList<>(selection);

        this.selection.clear();
        this.selection.addAll(artifacts);

        fireSelectionChanged(selection, deselect);
    }

    public void addToSelection(List<Artifact> artifacts) {
        this.selection.addAll(artifacts);

        fireSelectionChanged(artifacts, new LinkedList<>());
    }

    public void addToSelection(Artifact artifact) {
        LinkedList<Artifact> added = new LinkedList<>();
        added.add(artifact);

        this.selection.add(artifact);

        fireSelectionChanged(added, new LinkedList<>());
    }

    public void removeFromSelection(Artifact artifact) {
        if (this.selection.contains(artifact)) {
            this.selection.remove(artifact);

            LinkedList<Artifact> removed = new LinkedList<>();
            removed.add(artifact);

            fireSelectionChanged(new LinkedList<>(), removed);
        }
    }

    public void removeFromSelection(List<Artifact> artifacts) {
        LinkedList<Artifact> removed = new LinkedList<>();

        for (Artifact artifact : artifacts) {

            if (this.selection.contains(artifact)) {
                this.selection.remove(artifact);
                removed.add(artifact);
            }
        }

        fireSelectionChanged(new LinkedList<>(), removed);
    }

    public void clear() {
        LinkedList<Artifact> deselect = new LinkedList<>(selection);
        this.selection.clear();
        fireSelectionChanged(new LinkedList<>(), deselect);
    }

    private void fireSelectionChanged(List<Artifact> added, List<Artifact> removed) {
        eventBus.fireEvent(new SelectionChanged(added, removed));
    }

    public List<Artifact> getSelection() {
        return selection;
    }

    public Set<Artifacttype> getSelectedTypes() {
        Set<Artifacttype> types = new HashSet<>();
        for (Artifact artifact : selection) {
            types.add(artifact.getType());
        }
        return types;
    }

    public void selectAll() {
        List<Artifact> allArtifacts = projectEditor.getArtifacts();
        Collection<Artifact> added = CollectionUtils.subtract(allArtifacts, selection);
        this.selection = allArtifacts;
        fireSelectionChanged(new LinkedList<>(added), new LinkedList<>());
    }

}
