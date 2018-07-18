package de.uos.se.prom.dsmproject.bl;

import de.uos.se.prom.dsmproject.bl.event.EventBus;
import de.uos.se.prom.dsmproject.bl.events.ArtifactAdded;
import de.uos.se.prom.dsmproject.bl.events.ArtifactDeleted;
import de.uos.se.prom.dsmproject.bl.events.ArtifactEdited;
import de.uos.se.prom.dsmproject.bl.events.AutoSortChanged;
import de.uos.se.prom.dsmproject.bl.events.SortingChanged;
import de.uos.se.prom.dsmproject.bl.sorting.SortAlgorithmController;
import de.uos.se.prom.dsmproject.entity.Artifact;
import de.uos.se.prom.dsmproject.entity.DsmSorting;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

public class SortController {

    /**
     * If enabled, the controller will apply the currently selected algorithm
     * each time a project or artifact event occures.
     */
    private boolean autosort = true;

    private List<Artifact> sortedArtifacts = new LinkedList<>();

    @Inject
    private SortAlgorithmController algorithmController;

    private DsmSorting selectedAlgorithm;

    @Inject
    private ProjectEditor projectEditor;

    @Inject
    EventBus eventBus;

    @PostConstruct
    public void initialize() {

        eventBus.addListener(ArtifactEdited.TOPIC, (event) -> {
            ArtifactEdited realEvent = (ArtifactEdited) event;
            this.artifactEdited(realEvent.getBefore(), realEvent.getAfter());
        });

        eventBus.addListener(ArtifactAdded.TOPIC, (event) -> {
            ArtifactAdded realEvent = (ArtifactAdded) event;
            sortedArtifacts.add(realEvent.getArtifact());
            if (autosort) {
                applyAlgorithm();
            }
        });

        eventBus.addListener(ArtifactDeleted.TOPIC, (event) -> {
            ArtifactDeleted realEvent = (ArtifactDeleted) event;
            sortedArtifacts.remove(realEvent.getArtifact());
        });

        eventBus.addListener(AutoSortChanged.TOPIC, (event) -> {
            AutoSortChanged realEvent = (AutoSortChanged) event;
            setAutosort(realEvent.isAutoSort());
        });

    }

    void selectAlgorithm(DsmSorting algorithm) {
        this.selectedAlgorithm = algorithm;
    }

    public void applyAlgorithm() {
        if (null == selectedAlgorithm) {
            return;
        }

        List<Artifact> artifacts = projectEditor.getArtifacts();
        this.sortedArtifacts = this.algorithmController.applyAlgorithm(artifacts, selectedAlgorithm);

        eventBus.fireEvent(new SortingChanged(sortedArtifacts));
    }

    private void artifactEdited(Artifact before, Artifact after) {
        sortedArtifacts.remove(before);
        sortedArtifacts.add(after);

        if (autosort) {
            applyAlgorithm();
        }
    }

    public void setAutosort(boolean autosort) {
        this.autosort = autosort;

        if (autosort) {
            applyAlgorithm();
        }
    }

    public List<Artifact> getSortedArtifacts() {
        return sortedArtifacts;
    }

}
