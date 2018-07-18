package de.uos.se.prom.dsmproject.gui.dg;

import de.uos.se.prom.dsmproject.bl.ArtifactEditor;
import de.uos.se.prom.dsmproject.bl.ProjectEditor;
import de.uos.se.prom.dsmproject.bl.SelectionController;
import de.uos.se.prom.dsmproject.bl.event.EventBus;
import de.uos.se.prom.dsmproject.bl.event.EventListener;
import de.uos.se.prom.dsmproject.bl.events.AppClosing;
import de.uos.se.prom.dsmproject.bl.events.ArtifactAdded;
import de.uos.se.prom.dsmproject.bl.events.ArtifactDeleted;
import de.uos.se.prom.dsmproject.bl.events.ArtifactEdited;
import de.uos.se.prom.dsmproject.bl.events.DependenciesDirectedChanged;
import de.uos.se.prom.dsmproject.bl.events.DependencyAdded;
import de.uos.se.prom.dsmproject.bl.events.DependencyDeleted;
import de.uos.se.prom.dsmproject.bl.events.HiddenTypesChanged;
import de.uos.se.prom.dsmproject.bl.events.ProjectCreated;
import de.uos.se.prom.dsmproject.bl.events.ProjectLoaded;
import de.uos.se.prom.dsmproject.bl.events.SelectionChanged;
import de.uos.se.prom.dsmproject.entity.Artifact;
import de.uos.se.prom.dsmproject.entity.Artifacttype;
import de.uos.se.prom.dsmproject.entity.Dependency;
import de.uos.se.prom.dsmproject.entity.Project;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javax.inject.Inject;
import javax.swing.SwingUtilities;

public class DgPresenter implements Initializable {

    Graph graph;

    @FXML
    AnchorPane rootPane;

    @Inject
    ArtifactEditor artifactEditor;

    @Inject
    ProjectEditor projectEditor;

    @Inject
    EventBus eventBus;

    @Inject
    SelectionController selectionController;

    @FXML
    private SwingNode graphWrapper;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        graph = new Graph();

        graph.addGraphSelectionListener(new Graph.IGraphSelectionListener() {
            @Override
            public void artifactSelected(String name) {
                Artifact selected = artifactEditor.getArtifactByName(name);
                selectionController.select(selected);
            }

            @Override
            public void selectionCleared() {
                selectionController.clear();
            }
        });

        EventListener artifactAdded = (EventListener<ArtifactAdded>) (ArtifactAdded event) -> {
            graph.addArtifact(event.getArtifact());
        };
        EventListener artifactDeleted = (EventListener<ArtifactDeleted>) (ArtifactDeleted event) -> {
            graph.removeArtifact(event.getArtifact());
        };
        EventListener artifactEdited = (EventListener<ArtifactEdited>) (ArtifactEdited event) -> {
            graph.updateArtifact(event.getBefore(), event.getAfter());
        };
        EventListener dependencyAdded = (EventListener<DependencyAdded>) (DependencyAdded event) -> {
            graph.addDependency(event.getDependency());
        };
        EventListener dependencyDeleted = (EventListener<DependencyDeleted>) (DependencyDeleted event) -> {
            graph.removeDependency(event.getDependency());
        };
        EventListener dependenciesDirectedChanged = (EventListener<DependenciesDirectedChanged>) (DependenciesDirectedChanged event) -> {
            graph.setDependenciesDirected(event.isDirected());
        };
        EventListener projectLoaded = (EventListener<ProjectLoaded>) (ProjectLoaded event) -> {
            graph.clear();
            applyProjectData(event.getProject());
        };
        EventListener projectCreated = (EventListener<ProjectCreated>) (ProjectCreated event) -> {
            graph.clear();
            applyProjectData(event.getProject());
        };
        EventListener hiddenTypesChanged = (EventListener<HiddenTypesChanged>) (HiddenTypesChanged event) -> {

            for (Artifacttype shownTypes : event.getShown()) {
                final List<Artifact> artifactsForType = artifactEditor.getArtifactsForType(shownTypes);

                List<Dependency> dependencies = new LinkedList<>();
                for (Artifact shownArtifact : artifactsForType) {
                    dependencies.addAll(projectEditor.getDependenciesFrom(shownArtifact));
                    dependencies.addAll(projectEditor.getDependenciesTo(shownArtifact));
                }
                graph.showArtifacts(artifactsForType, dependencies);

            }
            for (Artifacttype hiddenTypes : event.getHidden()) {
                List<Artifact> artifacts = artifactEditor.getArtifactsForType(hiddenTypes);
                graph.hideArtifacts(artifacts);
            }
        };

        EventListener selectionChanged = (EventListener<SelectionChanged>) (SelectionChanged event) -> {
            graph.selectionChanged(event.getAdded(), event.getRemoved());
        };

        EventListener appClosing = (EventListener<AppClosing>) (AppClosing event) -> {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    graph.close();
                }
            });
        };

        eventBus.addListener(ArtifactAdded.TOPIC, artifactAdded);
        eventBus.addListener(ArtifactDeleted.TOPIC, artifactDeleted);
        eventBus.addListener(ArtifactEdited.TOPIC, artifactEdited);
        eventBus.addListener(DependencyAdded.TOPIC, dependencyAdded);
        eventBus.addListener(DependencyDeleted.TOPIC, dependencyDeleted);
        eventBus.addListener(DependenciesDirectedChanged.TOPIC, dependenciesDirectedChanged);
        eventBus.addListener(ProjectLoaded.TOPIC, projectLoaded);
        eventBus.addListener(ProjectCreated.TOPIC, projectCreated);
        eventBus.addListener(HiddenTypesChanged.TOPIC, hiddenTypesChanged);
        eventBus.addListener(SelectionChanged.TOPIC, selectionChanged);
        eventBus.addListener(AppClosing.TOPIC, appClosing);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                graphWrapper.setContent(graph.getView());
            }
        });
    }

    private void applyProjectData(Project project) {
        graph.addArtifacts(project.getArtifacts());
        graph.addDependencies(project.getDependencies());

    }

}
