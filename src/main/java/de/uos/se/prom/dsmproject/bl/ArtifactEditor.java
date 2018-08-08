package de.uos.se.prom.dsmproject.bl;

import de.uos.se.prom.dsmproject.bl.event.EventBus;
import de.uos.se.prom.dsmproject.bl.events.ArtifactAdded;
import de.uos.se.prom.dsmproject.bl.events.ArtifactEdited;
import de.uos.se.prom.dsmproject.bl.events.ProjectCreated;
import de.uos.se.prom.dsmproject.bl.events.ProjectLoaded;
import de.uos.se.prom.dsmproject.entity.Artifact;
import de.uos.se.prom.dsmproject.entity.Artifacttype;
import de.uos.se.prom.dsmproject.entity.Project;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

public class ArtifactEditor {

    HashMap<String, Artifacttype> nameToTypes = new HashMap<>();

    private Artifacttype lastUsedType;

    @Inject
    ProjectEditor projectEditor;

    HashMap<Artifacttype, List<Artifact>> typeToArtifacts = new HashMap<>();

    @Inject
    SortController sortController;

    @Inject
    EventBus eventBus;

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

    public boolean addArtifact(String name, Artifacttype type) {
        if (hasArtifact(name)) {
            return false;
        }

        addArtifacttype(type);

        Artifact artifact = new Artifact(type, name);
        projectEditor.addArtifact(artifact);

        lastUsedType = type;

        handleArtifacttype(artifact);

        eventBus.fireEvent(new ArtifactAdded(artifact));

        return true;
    }

    private void addArtifacttype(Artifacttype type) {
        String typeName = type.getName();
        if (!this.nameToTypes.containsKey(typeName)) {
            nameToTypes.put(typeName, type);
        }
        if (!this.typeToArtifacts.containsKey(type)) {
            typeToArtifacts.put(type, new LinkedList<>());
        }
    }

    public void deleteArtifact(Artifact artifact) {
        if (!hasArtifact(artifact.getName())) {
            return;
        }

        this.typeToArtifacts.get(artifact.getType()).remove(artifact);

        projectEditor.removeArtifact(artifact);
    }

    void applyProjectData(Project project) {
        List<Artifact> artifacts = project.getArtifacts();
        this.nameToTypes.clear();
        this.typeToArtifacts.clear();
        this.lastUsedType = null;
        artifacts.forEach((artifact) -> {
            handleArtifacttype(artifact);
            // TODO the next line could also be done by handleArtifacttype() !
            addArtifacttype(artifact.getType());
        });
    }

    private void handleArtifacttype(Artifact artifact) {
        final Artifacttype type = artifact.getType();
        if (this.typeToArtifacts.containsKey(type)) {
            this.typeToArtifacts.get(type).add(artifact);
        } else {
            List<Artifact> artifactList = new LinkedList<>();
            artifactList.add(artifact);
            this.typeToArtifacts.put(type, artifactList);
        }
    }

    public Collection<Artifacttype> getArtifacttypes() {
        return this.nameToTypes.values();
    }

    public void editArtifact(Artifact artifact, String newName, Artifacttype newType) {
        addArtifacttype(newType);
        Artifact newArtifact = new Artifact(newType, newName);

        Artifacttype oldType = artifact.getType();
        if (!oldType.equals(newType)) {
            typeToArtifacts.get(artifact.getType()).remove(artifact);
            handleArtifacttype(newArtifact);
        }

        // project editor must apply the changes before the listeners do
        projectEditor.artifactEdited(artifact, newArtifact);

        lastUsedType = newType;

        eventBus.fireEvent(new ArtifactEdited(artifact, newArtifact));
    }

    public boolean hasArtifact(String name) {
        Project currentProject = projectEditor.getCurrentProject();
        List<Artifact> artifacts = currentProject.getArtifacts();
        for (Artifact artifact : artifacts) {
            if (artifact.getName().equals(name)) {
                return true;
            }
        }

        return false;
    }

    public void setTypeFor(List<Artifact> artifacts, Artifacttype type) {
        artifacts = new LinkedList<>(artifacts);
        for (Artifact artifact : artifacts) {
            editArtifact(artifact, artifact.getName(), type);
        }
    }

    public void deleteArtifacts(List<Artifact> artifacts) {
        artifacts = new LinkedList<>(artifacts);
        for (Artifact artifact : artifacts) {
            deleteArtifact(artifact);
        }
    }

    public Artifacttype getLastUsedType() {
        return lastUsedType;
    }

    public List<Artifact> getArtifactsForType(Artifacttype type) {
        return this.typeToArtifacts.get(type);
    }

    public Artifact getArtifactByName(String name) {
        for (Artifact artifact : projectEditor.getArtifacts()) {
            if (artifact.getName().equals(name)) {
                return artifact;
            }
        }

        return null;
    }
}
