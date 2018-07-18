package de.uos.se.prom.dsmproject.bl;

import de.uos.se.prom.dsmproject.bl.application.ExceptionHandler;
import de.uos.se.prom.dsmproject.bl.event.EventBus;
import de.uos.se.prom.dsmproject.bl.events.ProjectCreated;
import de.uos.se.prom.dsmproject.bl.events.ProjectLoaded;
import de.uos.se.prom.dsmproject.bl.events.ProjectSaved;
import de.uos.se.prom.dsmproject.bl.events.ProjectSavedAs;
import de.uos.se.prom.dsmproject.da.ProjectAccess;
import de.uos.se.prom.dsmproject.entity.Artifact;
import de.uos.se.prom.dsmproject.entity.Artifacttype;
import de.uos.se.prom.dsmproject.entity.Dependency;
import de.uos.se.prom.dsmproject.entity.Project;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javax.inject.Inject;

public class ProjectController {

    @Inject
    private ProjectAccess projectAccess;

    @Inject
    private ProjectEditor projectEditor;

    @Inject
    ExceptionHandler exceptionHandler;

    /**
     * The save file of the current project
     */
    private java.io.File projectFile;

    /**
     * Indicates whether the current project has been saved as a file.
     */
    private final javafx.beans.property.BooleanProperty hasProjectFileProperty;

    @Inject
    EventBus eventBus;

    public ProjectController() {

        this.hasProjectFileProperty = new SimpleBooleanProperty();

    }

    /**
     * Uses data acces layer to load a project. Replaces the currently active
     * project.
     */
    public void loadProject(File file) {
        Project loaded = null;

        try {
            loaded = projectAccess.loadProject(file);
        } catch (Exception ex) {
            exceptionHandler.exceptionCatched(ex);
            return;
        }

        this.projectFile = file;
        hasProjectFileProperty.setValue(Boolean.TRUE);
        eventBus.fireEvent(new ProjectLoaded(loaded, file));
    }

    /**
     * Replaces the current project by an empty one.
     */
    public void newProject() {

        Project project = new Project("New", true);
        project.setDependenciesWeighted(false);

        this.projectFile = null;

        hasProjectFileProperty.setValue(Boolean.FALSE);

        eventBus.fireEvent(new ProjectCreated(project));
    }

    /**
     * Uses data acces layer to save the current project.
     */
    public void saveProject() {
        try {
            projectAccess.saveProject(projectEditor.getCurrentProject(), this.projectFile);
        } catch (Exception ex) {
            exceptionHandler.exceptionCatched(ex);
            return;
        }

        eventBus.fireEvent(new ProjectSaved());
    }

    public void saveProjectAs(java.io.File file) {
        try {

            projectAccess.saveProject(projectEditor.getCurrentProject(), file);
        } catch (Exception ex) {
            exceptionHandler.exceptionCatched(ex);
            return;
        }

        // only if successful!
        this.projectFile = file;
        hasProjectFileProperty.setValue(Boolean.TRUE);

        eventBus.fireEvent(new ProjectSavedAs(file));
    }

    public BooleanProperty getHasProjectFileProperty() {
        return hasProjectFileProperty;
    }

    public void createRandomProject() {
        Project project = new DummyProjectFactory().DEV_createRandomDummyProject();
        this.projectFile = null;
        hasProjectFileProperty.setValue(Boolean.FALSE);
        eventBus.fireEvent(new ProjectCreated(project));
    }

    public void createMetaProject() {
        List<Artifact> artifacts = projectEditor.getArtifacts();

        final String currentName = projectEditor.getCurrentProject().getName();
        Project metaProject = new Project("Metamodel for " + currentName, true);
        metaProject.setDependenciesWeighted(false);

        Artifacttype metatype = new Artifacttype("Meta");

        HashMap<Artifacttype, Set<Artifacttype>> typeDependencies = new HashMap<>();
        HashMap<Artifacttype, Artifact> metaArtifacts = new HashMap<>();

        for (Artifact artifact : artifacts) {
            final Artifacttype type = artifact.getType();
            if (!metaArtifacts.containsKey(type)) {
                Artifact typeArtifact = new Artifact(metatype, type.getName());
                metaProject.addArtifact(typeArtifact);
                metaArtifacts.put(type, typeArtifact);
            }

            Set<Artifacttype> dependendTypes = new HashSet<>();

            List<Dependency> artifactDependencies = projectEditor.getDependenciesFrom(artifact);
            for (Dependency dependency : artifactDependencies) {
                Artifact dependendArtifact = dependency.getTarget();
                Artifacttype dependendType = dependendArtifact.getType();
                dependendTypes.add(dependendType);
            }

            Set<Artifacttype> allDependendTypes = typeDependencies.get(type);
            if (null == allDependendTypes) {
                allDependendTypes = dependendTypes;
            } else {
                allDependendTypes.addAll(dependendTypes);
            }

            typeDependencies.put(type, allDependendTypes);
        }

        for (Map.Entry<Artifacttype, Set<Artifacttype>> entry : typeDependencies.entrySet()) {
            Artifacttype typeArtifact = entry.getKey();
            Set<Artifacttype> dependendTypeArtifacts = entry.getValue();
            for (Artifacttype dependendTypeArtifact : dependendTypeArtifacts) {
                Artifact source = metaArtifacts.get(typeArtifact);
                Artifact target = metaArtifacts.get(dependendTypeArtifact);
                Dependency typeArtifactDependency = new Dependency(source, target, 1.);
                metaProject.addDependency(typeArtifactDependency);
            }
        }

        this.projectFile = null;
        hasProjectFileProperty.setValue(Boolean.FALSE);
        eventBus.fireEvent(new ProjectCreated(metaProject));
    }

}
