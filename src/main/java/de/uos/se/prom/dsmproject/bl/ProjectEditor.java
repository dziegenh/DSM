package de.uos.se.prom.dsmproject.bl;

import de.uos.se.prom.dsmproject.bl.application.ExceptionHandler;
import de.uos.se.prom.dsmproject.bl.event.EventBus;
import de.uos.se.prom.dsmproject.bl.events.ArtifactDeleted;
import de.uos.se.prom.dsmproject.bl.events.DependenciesWeightedChanged;
import de.uos.se.prom.dsmproject.bl.events.DependencyAdded;
import de.uos.se.prom.dsmproject.bl.events.DependencyDeleted;
import de.uos.se.prom.dsmproject.bl.events.ProjectCreated;
import de.uos.se.prom.dsmproject.bl.events.ProjectLoaded;
import de.uos.se.prom.dsmproject.bl.events.ProjectRenamed;
import de.uos.se.prom.dsmproject.bl.events.ProjectSaved;
import de.uos.se.prom.dsmproject.bl.events.ProjectSavedAs;
import de.uos.se.prom.dsmproject.bl.exceptions.ArtifactAlreadyExisting;
import de.uos.se.prom.dsmproject.entity.Artifact;
import de.uos.se.prom.dsmproject.entity.Dependency;
import de.uos.se.prom.dsmproject.entity.Project;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

public class ProjectEditor {

    /**
     * Contains a list of dependend artifacts (=value) for each key artifact.
     */
    final HashMap<Artifact, List<Dependency>> artifactTargetDependencies;

    /**
     * Contains a list of artifacts on which the key artifact depends on.
     */
    final HashMap<Artifact, List<Dependency>> artifactSourceDependencies;

    @Inject
    ExceptionHandler exceptionHandler;

    @Inject
    EventBus eventBus;

    /**
     * The currently active project.
     */
    Project currentProject;

    SimpleBooleanProperty hasUnsavedChanges = new SimpleBooleanProperty();

    @PostConstruct
    public void initialize() {
        eventBus.addListener(ProjectCreated.TOPIC, (event) -> {
            ProjectCreated realEvent = (ProjectCreated) event;
            applyProjectData(realEvent.getProject());
            hasUnsavedChanges.set(false);
        });
        eventBus.addListener(ProjectLoaded.TOPIC, (event) -> {
            ProjectLoaded realEvent = (ProjectLoaded) event;
            applyProjectData(realEvent.getProject());
            hasUnsavedChanges.set(false);

        });
        eventBus.addListener(ProjectSaved.TOPIC, (event) -> {
            hasUnsavedChanges.set(false);
        });
        eventBus.addListener(ProjectSavedAs.TOPIC, (event) -> {
            hasUnsavedChanges.set(false);
        });
    }

    public ProjectEditor() {
        this.artifactTargetDependencies = new HashMap<>();
        this.artifactSourceDependencies = new HashMap<>();
    }

    /**
     * Returns a copy of the current artifact list.
     *
     * @return
     */
    public List<Artifact> getArtifacts() {
        return new LinkedList<>(this.artifactSourceDependencies.keySet());
    }

    public void rename(String name) {
        this.currentProject.setName(name);
        this.eventBus.fireEvent(new ProjectRenamed(name));
        hasUnsavedChanges.set(true);
    }

    public void setWeightingDependencies(boolean weighted) {
        this.currentProject.setDependenciesWeighted(weighted);
        eventBus.fireEvent(new DependenciesWeightedChanged(weighted));
        hasUnsavedChanges.set(true);
    }

    boolean addArtifact(Artifact artifact) {
        if (artifactSourceDependencies.containsKey(artifact)) {
            exceptionHandler.exceptionCatched(new ArtifactAlreadyExisting(artifact));
            return false;

        }

        this.artifactSourceDependencies.put(artifact, new LinkedList<>());
        this.artifactTargetDependencies.put(artifact, new LinkedList<>());
        currentProject.addArtifact(artifact);

        hasUnsavedChanges.set(true);

        return true;
    }

    public void addDepenendency(Artifact sourceArtifact, Artifact targetArtifact, double weight) {
        this.addDependency(new Dependency(sourceArtifact, targetArtifact, weight));
    }

    public void addDependency(Dependency dependency) {
        Artifact source = dependency.getSource();
        Artifact target = dependency.getTarget();

        this.artifactSourceDependencies.get(target).add(dependency);
        this.artifactTargetDependencies.get(source).add(dependency);

        currentProject.addDependency(dependency);

        eventBus.fireEvent(new DependencyAdded(dependency));

        hasUnsavedChanges.set(true);
    }

    public final Project getCurrentProject() {
        return currentProject;
    }

    /**
     * Returns a list of artifacts, which are dependend from the given one.
     */
    public List<Dependency> getDependenciesFrom(Artifact artifact) {
        return this.artifactTargetDependencies.get(artifact);

    }

    /**
     * Returns a list of artifacts, on which the given one depends.
     */
    public List<Dependency> getDependenciesTo(Artifact artifact) {
        return this.artifactSourceDependencies.get(artifact);

    }

    public void removeDepenendency(Artifact source, Artifact target) {
        this.removeDepenendency(new Dependency(source, target, 0));
    }

    public void removeDepenendency(Dependency dependency) {
        final Artifact source = dependency.getSource();
        final Artifact target = dependency.getTarget();

        // TODO check if this really removes the dep from the map's list !!!
        List<Dependency> dependencies = this.artifactSourceDependencies.get(target);
        if (null != dependencies) {
            Dependency[] depArray = dependencies.toArray(new Dependency[]{});
            for (Dependency removeCandidate : depArray) {
                if (removeCandidate.equals(dependency)) {
                    dependencies.remove(dependency);
                }
            }
        }

        dependencies = this.artifactTargetDependencies.get(source);
        if (null != dependencies) {
            Dependency[] depArray = dependencies.toArray(new Dependency[]{});
            for (Dependency removeCandidate : depArray) {
                if (removeCandidate.equals(dependency)) {
                    dependencies.remove(dependency);
                }
            }
        }

        this.currentProject.removeDependency(dependency);

        eventBus.fireEvent(new DependencyDeleted(dependency));

        hasUnsavedChanges.set(true);
    }

    void removeArtifact(Artifact artifact) {
        List<Dependency> dependenciesFrom = new LinkedList<>(this.getDependenciesFrom(artifact));
        for (Dependency dependency : dependenciesFrom) {
            removeDepenendency(dependency);
        }

        List<Dependency> dependenciesTo = new LinkedList<>(getDependenciesTo(artifact));
        for (Dependency dependency : dependenciesTo) {
            removeDepenendency(dependency);
        }

        this.artifactSourceDependencies.remove(artifact);
        this.artifactTargetDependencies.remove(artifact);

        this.currentProject.removeArtifact(artifact);

        this.eventBus.fireEvent(new ArtifactDeleted(artifact));

        hasUnsavedChanges.set(true);
    }

    private void applyProjectData(Project project) {
        currentProject = project;

        this.artifactSourceDependencies.clear();
        this.artifactTargetDependencies.clear();

        for (Artifact artifact : project.getArtifacts()) {
            artifactSourceDependencies.put(artifact, new LinkedList<>());
            artifactTargetDependencies.put(artifact, new LinkedList<>());
        }

        for (Dependency dependency : project.getDependencies()) {
            final Artifact target = dependency.getTarget();
            List<Dependency> sources = artifactSourceDependencies.get(target);
            sources.add(dependency);

            final Artifact source = dependency.getSource();
            List<Dependency> targets = artifactTargetDependencies.get(source);
            targets.add(dependency);
        }
    }

    public Dependency getDependency(Artifact source, Artifact target) {
        List<Dependency> targetDependencies = this.artifactTargetDependencies.get(source);

        if (null == targetDependencies) {
            return null;
        }

        for (Dependency aDep : targetDependencies) {
            if (aDep.getTarget().equals(target)) {
                return aDep;
            }
        }

        return null;
    }

    public ReadOnlyBooleanProperty getHasUnsavedChanges() {
        return this.hasUnsavedChanges;
    }

    void artifactEdited(Artifact before, Artifact after) {

        // edit artifact in source dependencies list
        {
            List<Dependency> artSourceDeps = artifactSourceDependencies.remove(before);
            List<Dependency> newArtSourceDeps = new LinkedList<>();
            for (Dependency artSourceDep : artSourceDeps) {
                artSourceDep.setTarget(after);
                newArtSourceDeps.add(artSourceDep);
            }
            artifactSourceDependencies.put(after, newArtSourceDeps);
        }

        // edit artifact in target dependencies list
        {
            List<Dependency> artTargetDeps = artifactTargetDependencies.remove(before);
            List<Dependency> newArtTargetDeps = new LinkedList<>();
            for (Dependency artTargetDep : artTargetDeps) {
                artTargetDep.setSource(after);
                newArtTargetDeps.add(artTargetDep);
            }
            artifactTargetDependencies.put(after, newArtTargetDeps);
        }

        currentProject.removeArtifact(before);
        currentProject.addArtifact(after);

        this.hasUnsavedChanges.set(true);
    }

}
