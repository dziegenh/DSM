package de.uos.se.prom.dsmproject.gui.dsm;

import com.sun.istack.internal.logging.Logger;
import de.uos.se.prom.dsmproject.bl.ProjectController;
import de.uos.se.prom.dsmproject.bl.ProjectEditor;
import de.uos.se.prom.dsmproject.bl.SortController;
import de.uos.se.prom.dsmproject.bl.event.Event;
import de.uos.se.prom.dsmproject.bl.event.EventBus;
import de.uos.se.prom.dsmproject.bl.events.ArtifactAdded;
import de.uos.se.prom.dsmproject.bl.events.ArtifactDeleted;
import de.uos.se.prom.dsmproject.bl.events.ArtifactEdited;
import de.uos.se.prom.dsmproject.bl.events.DependencyAdded;
import de.uos.se.prom.dsmproject.bl.events.DependencyDeleted;
import de.uos.se.prom.dsmproject.bl.events.HiddenTypesChanged;
import de.uos.se.prom.dsmproject.bl.events.ProjectCreated;
import de.uos.se.prom.dsmproject.bl.events.ProjectLoaded;
import de.uos.se.prom.dsmproject.bl.events.SelectionChanged;
import de.uos.se.prom.dsmproject.bl.events.SortingChanged;
import de.uos.se.prom.dsmproject.entity.Artifact;
import de.uos.se.prom.dsmproject.entity.Artifacttype;
import de.uos.se.prom.dsmproject.entity.Dependency;
import de.uos.se.prom.dsmproject.entity.Project;
import de.uos.se.prom.dsmproject.entity.View;
import de.uos.se.prom.dsmproject.gui.dsm.collabel.CollabelView;
import de.uos.se.prom.dsmproject.gui.dsm.dsmcell.DsmCellPresenter;
import de.uos.se.prom.dsmproject.gui.dsm.dsmcell.DsmCellView;
import de.uos.se.prom.dsmproject.gui.dsm.rowlabel.RowlabelView;
import de.uos.se.prom.dsmproject.gui.dsm.typelabel.TypelabelView;
import de.uos.se.prom.dsmproject.gui.events.DsmSizeChanged;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javax.inject.Inject;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class DsmPresenter implements Initializable {

    @Inject
    ProjectController projectController;

    @Inject
    ProjectEditor projectEditor;

    @Inject
    SortController sortController;

    @Inject
    EventBus eventBus;

    @FXML
    GridPane cellGrid;

    HashMap<Artifact, ArtifactWrapper> artifactWrappers = new HashMap<>();

    HashMap<ArtifactWrapper, List<DsmCellView>> artifactCells = new HashMap<>();
    private final List<Artifact> sortedArtifacts = new LinkedList<>();

    private final HashMap<Pair<ArtifactWrapper, ArtifactWrapper>, DsmCellView> sourceTargetCells = new HashMap<>();

    /**
     * Stores the artifact's labels etc.
     */
    private final HashMap<ArtifactWrapper, ArtifactComponents> artifactComponents = new HashMap<>();

    private List<Artifact> pendingSorting;

    Logger logger = Logger.getLogger(DsmPresenter.class);

    private List<Artifacttype> hiddenTypes = new LinkedList<>();

    MutablePair<ArtifactWrapper, ArtifactWrapper> highlighted = new MutablePair();

    private final int typeLabelsColumn = 0;
    private final int rowLabelsColumn = 1;
    private final int cellsStartRow = 1;
    private final int cellsStartColumn = 1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // Position the column artifact labels at the bottom of the cells 
        RowConstraints labelRowConstraints = new RowConstraints();
        labelRowConstraints.setValignment(VPos.BOTTOM);
        cellGrid.getRowConstraints().add(labelRowConstraints);
        
        eventBus.addListener(DependencyAdded.TOPIC, (event) -> {
            DependencyAdded realEvent = (DependencyAdded) event;
            Dependency dependency = realEvent.getDependency();
            DsmCellView dsmCell = getCellForDependency(dependency);
            dsmCell.getRealPresenter().dependencyAdded(dependency.getWeight());
        });
        eventBus.addListener(DependencyDeleted.TOPIC, (event) -> {
            DependencyDeleted realEvent = (DependencyDeleted) event;
            Dependency dependency = realEvent.getDependency();
            DsmCellView dsmCell = getCellForDependency(dependency);
            dsmCell.getRealPresenter().dependencyDeleted();
        });

        eventBus.addListener(ArtifactAdded.TOPIC, (event) -> {
            ArtifactAdded realEvent = (ArtifactAdded) event;
            Artifact artifact = realEvent.getArtifact();

            // the artifact may already be added if the add-event reached the sorter before this listener 
            if (sortedArtifacts.contains(artifact)) {
                return;
            }

            addArtifact(artifact, true);
        });

        eventBus.addListener(ArtifactDeleted.TOPIC, (event) -> {
            ArtifactDeleted realEvent = (ArtifactDeleted) event;
            deleteArtifact(realEvent.getArtifact(), true);
        });

        eventBus.addListener(SortingChanged.TOPIC, (event) -> {
            SortingChanged realEvent = (SortingChanged) event;
            applySorting(realEvent.getSortedArtifacts());
        });

        eventBus.addListener(SelectionChanged.TOPIC, (event) -> {
            SelectionChanged realEvent = (SelectionChanged) event;

            List<Artifact> selectedArtifacts = realEvent.getAdded();
            List<Artifact> deselectedArtifacts = realEvent.getRemoved();

            for (Artifact deselectedArtifact : deselectedArtifacts) {
                ArtifactWrapper wrapper = this.artifactWrappers.get(deselectedArtifact);
                if (sortedArtifacts.contains(deselectedArtifact)) {
                    ArtifactComponents components = this.artifactComponents.get(wrapper);
                    components.columnLabelView.getRealPresenter().setUnselected();
                    components.rowLabelView.getRealPresenter().setUnselected();
                }
            }

            for (Artifact selectedArtifact : selectedArtifacts) {
                ArtifactWrapper wrapper = this.artifactWrappers.get(selectedArtifact);
                if (sortedArtifacts.contains(selectedArtifact)) {
                    ArtifactComponents components = this.artifactComponents.get(wrapper);
                    components.columnLabelView.getRealPresenter().setSelected();
                    components.rowLabelView.getRealPresenter().setSelected();
                }
            }
        });

        eventBus.addListener(ProjectCreated.TOPIC, (event) -> {
            ProjectCreated realEvent = (ProjectCreated) event;
            applyProjectElements(realEvent.getProject());
        });

        eventBus.addListener(ProjectLoaded.TOPIC, (event) -> {
            ProjectLoaded realEvent = (ProjectLoaded) event;
            applyProjectElements(realEvent.getProject());
        });

        eventBus.addListener(ArtifactEdited.TOPIC, (event) -> {

            ArtifactEdited realEvent = (ArtifactEdited) event;
            Artifact before = realEvent.getBefore();
            Artifact after = realEvent.getAfter();

            applyArtifactChanges(before, after);
        });

        eventBus.addListener(HiddenTypesChanged.TOPIC, (Event event) -> {
            HiddenTypesChanged realEvent = (HiddenTypesChanged) event;
            Collection<Artifacttype> hidden = realEvent.getHidden();
            Collection<Artifacttype> shown = realEvent.getShown();

            hiddenTypes.addAll(hidden);
            hiddenTypes.removeAll(shown);

            boolean sizeChanged = (hidden.size() != shown.size());

            // apply sorting only if there is not already a sort request pending.
            if (null == pendingSorting) {
                applySorting(true);
            } else {
                createOrUpdateTypeLabels();
            }

        });

    }

    private void applyArtifactChanges(Artifact before, Artifact after) {
        ArtifactWrapper wrapper = this.artifactWrappers.remove(before);
        wrapper.setArtifact(after);
        this.artifactWrappers.put(after, wrapper);

        ArtifactComponents components = this.artifactComponents.get(wrapper);

        CollabelView colLabel = components.columnLabelView;
        if (null != colLabel) {
            colLabel.getRealPresenter().setArtifact(after);
        }

        RowlabelView rowLabel = components.rowLabelView;
        if (null != rowLabel) {
            rowLabel.getRealPresenter().setArtifact(after);
        }

        this.sortedArtifacts.remove(before);
        this.sortedArtifacts.add(after);

        components.typeLabel.getRealPresenter().setType(after.getType());

        LinkedList<Pair<ArtifactWrapper, ArtifactWrapper>> sourceTargetKeys = new LinkedList<>(this.sourceTargetCells.keySet());
        for (Pair<ArtifactWrapper, ArtifactWrapper> sourceTargetKey : sourceTargetKeys) {
            boolean isEdited = sourceTargetKey.getLeft().equals(wrapper) || sourceTargetKey.getRight().equals(wrapper);

            if (isEdited) {
                DsmCellView cell = sourceTargetCells.get(sourceTargetKey);
                final Artifact leftArtifact = sourceTargetKey.getLeft().getArtifact();
                final Artifact rightArtifact = sourceTargetKey.getRight().getArtifact();
                cell.getRealPresenter().setArtifacts(leftArtifact, rightArtifact);
            }
        }

        fireDsmSizeChanged();
    }

    private void fireDsmSizeChanged() {
        Platform.runLater(() -> {
            eventBus.fireEvent(new DsmSizeChanged());
        });
    }

    private DsmCellView getCellForDependency(Dependency dependency) {
        Artifact source = dependency.getSource();
        Artifact target = dependency.getTarget();
        ArtifactWrapper sourceWrapper = this.artifactWrappers.get(source);
        ArtifactWrapper targetWrapper = this.artifactWrappers.get(target);

        DsmCellView dsmCell = sourceTargetCells.get(new ImmutablePair<>(sourceWrapper, targetWrapper));
        return dsmCell;
    }

    private void applyProjectElements(Project project) {
        List<Artifact> artifacts = project.getArtifacts();
        boolean sameSize = artifacts.size() == this.sortedArtifacts.size();
        boolean sameArtifacts = false;

        // Cancel if the new artifacts have already been added.
        if (sameSize) {
            sameArtifacts = artifacts.containsAll(this.sortedArtifacts);
            if (sameArtifacts) {
                return;
            }
        }

        clearDsm();

        for (Artifact newArtifact : artifacts) {
            addArtifact(newArtifact, false);
        }

        View activeView = project.getActiveView();
        this.hiddenTypes = new LinkedList<>();
        if (null != activeView) {
            this.hiddenTypes.addAll(activeView.getHiddenTypes());
        }

        this.highlighted.setLeft(null);
        this.highlighted.setRight(null);

        // delete out-of-use artifacts
//        Collection<Artifact> deletedArtifacts = CollectionUtils.subtract(this.sortedArtifacts, artifacts);
//        for (Artifact deletedArtifact : deletedArtifacts) {
//            deleteArtifact(deletedArtifact, false);
//        }
//
//        // add missing artifacts
//        Collection<Artifact> newArtifacts = CollectionUtils.subtract(artifacts, this.sortedArtifacts);
//        for (Artifact newArtifact : newArtifacts) {
//            addArtifact(newArtifact, false);
//        }
        fireDsmSizeChanged();
    }

    private void addArtifact(Artifact artifact, boolean callListeners) {
        if (sortedArtifacts.contains(artifact)) {
            return;
        }

        ArtifactWrapper wrapper = new ArtifactWrapper(artifact);
        this.artifactWrappers.put(artifact, wrapper);

        ArtifactComponents components = new ArtifactComponents();

        // the new row will be inserted as the last grid row. Plus 1 because of the first row containing the labels.
        int row = sortedArtifacts.size() + cellsStartRow;
        int column = cellsStartColumn + 1;

        sortedArtifacts.add(artifact);

        components.rowLabelView = createAndAddRowLabel(wrapper, row);

        boolean weighted = projectEditor.getCurrentProject().areDependenciesWeighted();
        LinkedList<DsmCellView> cells = new LinkedList<>();
        for (Artifact anArtifact : sortedArtifacts) {
            ArtifactWrapper aWrapper = artifactWrappers.get(anArtifact);

            DsmCellView createdCell = createAndAddCell(wrapper, aWrapper, weighted, column, row);
            cells.add(createdCell);

            if (!artifact.equals(anArtifact)) {
                createdCell = createAndAddCell(aWrapper, wrapper, weighted, row, column);
                artifactCells.get(aWrapper).add(createdCell);
            } else {
                components.columnLabelView = createAndAddColumnLabel(wrapper, column);
            }

            column++;
        }

        components.typeLabel = new TypelabelView();
        components.typeLabel.getRealPresenter().setType(artifact.getType());
        cellGrid.add(components.typeLabel.getView(), this.typeLabelsColumn, row);

        artifactCells.put(wrapper, cells);
        artifactComponents.put(wrapper, components);

        if (callListeners) {
            fireDsmSizeChanged();
        }

        applyPendingSorting();
    }

    private CollabelView createAndAddColumnLabel(ArtifactWrapper wrapper, int column) {
        CollabelView collabelView = new CollabelView();
        collabelView.getRealPresenter().setArtifact(wrapper.getArtifact());
        collabelView.getRealPresenter().setNumber(column);
        final Parent view = collabelView.getView();
        cellGrid.add(view, column, 0);
        view.setOnMouseEntered((event) -> {
            setHighlightedColumn(wrapper);
        });
        return collabelView;
    }

    private RowlabelView createAndAddRowLabel(ArtifactWrapper wrapper, int row) {
        RowlabelView rowlabelView = new RowlabelView();
        rowlabelView.getRealPresenter().setArtifact(wrapper.getArtifact());
        rowlabelView.getRealPresenter().setNumber(row);
        final Parent view = rowlabelView.getView();
        cellGrid.add(view, rowLabelsColumn, row);
        view.setOnMouseEntered((event) -> {
            setHighlightedRow(wrapper);
        });
        return rowlabelView;
    }

    private DsmCellView createAndAddCell(ArtifactWrapper artifact, ArtifactWrapper anArtifact, boolean weighted, int row, int column) {
        DsmCellView theCell = createCell(artifact, anArtifact, weighted);
        this.sourceTargetCells.put(new ImmutablePair<>(artifact, anArtifact), theCell);
        Parent view = theCell.getView();
        view.managedProperty().bind(view.visibleProperty());
        cellGrid.add(view, row, column);

        view.setOnMouseEntered((event) -> {
            setHighlighted(anArtifact, artifact);
        });

        return theCell;
    }

    private DsmCellView createCell(ArtifactWrapper source, ArtifactWrapper target, boolean weighted) {
        DsmCellView cellView = new DsmCellView();
        final DsmCellPresenter cellPresenter = cellView.getRealPresenter();
        cellPresenter.setArtifacts(source.getArtifact(), target.getArtifact());
        cellPresenter.setWeighted(weighted);
        return cellView;
    }

    private void deleteArtifact(Artifact artifact, boolean callSizeListeners) {
        final ObservableList<Node> cells = cellGrid.getChildren();

        ArtifactWrapper wrapper = this.artifactWrappers.get(artifact);

        List<DsmCellView> dsmCells = this.artifactCells.get(wrapper);
        if (null != dsmCells) {
            for (DsmCellView dsmCell : dsmCells) {
                cells.remove(dsmCell.getView());
                dsmCell.getRealPresenter().destroy();
            }
            this.artifactCells.remove(wrapper);
        }

        ArtifactComponents components = this.artifactComponents.get(wrapper);

        cells.remove(components.rowLabelView.getView());
        cells.remove(components.columnLabelView.getView());
        cells.remove(components.typeLabel.getView());
        components.typeLabel.getRealPresenter().destroy();

        this.sortedArtifacts.remove(artifact);

        List<Pair<ArtifactWrapper, ArtifactWrapper>> toBeDeleted = new LinkedList<>();
        this.sourceTargetCells.keySet().forEach((pair) -> {
            ArtifactWrapper source = pair.getLeft();
            ArtifactWrapper target = pair.getRight();
            if (source.equals(wrapper) || target.equals(wrapper)) {
                toBeDeleted.add(pair);
            }
        });

        toBeDeleted.forEach((pair) -> {
            DsmCellView toRemove = this.sourceTargetCells.remove(pair);
            cells.remove(toRemove.getView());
        });

        Optional.ofNullable(highlighted.getLeft()).ifPresent((t) -> {
            if (t.equals(wrapper)) {
                highlighted.setLeft(null);
            }
        });
        Optional.ofNullable(highlighted.getRight()).ifPresent((t) -> {
            if (t.equals(wrapper)) {
                highlighted.setRight(null);
            }
        });

        if (callSizeListeners) {
            fireDsmSizeChanged();

        }

        if (!applyPendingSorting()) {
            createOrUpdateTypeLabels();
        }
    }

    private boolean applyPendingSorting() {
        if (null != pendingSorting && !pendingSorting.isEmpty()) {
            this.applySorting(pendingSorting);
            return true;
        }

        return false;
    }

    private void applySorting(List<Artifact> sortedArtifacts) {

        if (!sortedArtifacts.containsAll(this.sortedArtifacts) || !this.sortedArtifacts.containsAll(sortedArtifacts)) {
            this.pendingSorting = sortedArtifacts;
            return;
        }

        boolean dsmSizeChanged = this.sortedArtifacts.size() != sortedArtifacts.size();

        this.sortedArtifacts.clear();
        this.sortedArtifacts.addAll(sortedArtifacts);

        applySorting(dsmSizeChanged);
    }

    void createOrUpdateTypeLabels() {
        String lastTypeName = "";

        TypelabelView lastTypelabel = null;

        for (Artifact artifact : sortedArtifacts) {
            final Artifacttype type = artifact.getType();

            ArtifactWrapper wrapper = artifactWrappers.get(artifact);
            ArtifactComponents components = artifactComponents.get(wrapper);
            final TypelabelView typeLabel = components.typeLabel;
            cellGrid.getChildren().remove(typeLabel.getView());

            // don't add labels for hidden artifacts
            if (hiddenTypes.contains(type)) {
                continue;
            }

            // add label for artifact type
            final String typeName = type.getName();
            if (!typeName.equals(lastTypeName)) {
                typeLabel.setVisible(true);

                int i = components.rowNr;
                cellGrid.add(typeLabel.getView(), typeLabelsColumn, cellsStartRow + i);

                typeLabel.getRealPresenter().clearArtifacts();
                typeLabel.getRealPresenter().addArtifact(wrapper);
                lastTypelabel = typeLabel;
                lastTypeName = typeName;
            } else {
                typeLabel.setVisible(false);
                lastTypelabel.getRealPresenter().addArtifact(wrapper);
            }
        }

    }

    private void applySorting(boolean dsmSizeChanged) {

        if (this.sortedArtifacts.size() > 1) {

            ObservableList<Node> gridCells = cellGrid.getChildren();

            // create an map of artifacts and their positions (=index) in order to reduce list searches.
            HashMap<ArtifactWrapper, Integer> indexMap = new HashMap<>();
            int i = 1;
            for (Artifact artifact : sortedArtifacts) {

                ArtifactWrapper wrapper = this.artifactWrappers.get(artifact);

                ArtifactComponents components = this.artifactComponents.get(wrapper);

                // move artifact's row & col labels
                CollabelView colLabel = components.columnLabelView;
                gridCells.remove(colLabel.getView());
                RowlabelView rowLabel = components.rowLabelView;
                gridCells.remove(rowLabel.getView());

                // don't add labels for hidden artifacts
                if (hiddenTypes.contains(artifact.getType())) {
                    continue;
                }

                indexMap.put(wrapper, i);
                cellGrid.add(colLabel.getView(), cellsStartColumn + i, 0);
                cellGrid.add(rowLabel.getView(), rowLabelsColumn, cellsStartRow + i);

                // update label numbers
                components.rowNr = i;
                colLabel.getRealPresenter().setNumber(i);
                rowLabel.getRealPresenter().setNumber(i);

                i++;
            }

            sourceTargetCells.forEach((pair, dsmCell) -> {
                gridCells.remove(dsmCell.getView());

                // add cells if they are indexed (=visible)
                if (indexMap.containsKey(pair.getLeft()) && indexMap.containsKey(pair.getRight())) {
                    int sourceIdx = indexMap.get(pair.getLeft()); //  = row nr.
                    int targetIdx = indexMap.get(pair.getRight()); // = column nr.
                    cellGrid.add(dsmCell.getView(), cellsStartColumn + targetIdx, cellsStartRow + sourceIdx);
                }
            });

        }

        createOrUpdateTypeLabels();

        if (dsmSizeChanged) {
            fireDsmSizeChanged();
        }

        this.pendingSorting = null;
    }

    private void clearDsm() {
        this.sortedArtifacts.clear();
        this.sourceTargetCells.clear();
        for (ArtifactComponents value : artifactComponents.values()) {
            value.typeLabel.getRealPresenter().destroy();
        }
        this.artifactComponents.clear();
        for (List<DsmCellView> value : artifactCells.values()) {
            for (DsmCellView dsmCellView : value) {
                dsmCellView.getRealPresenter().destroy();
            }
        }
        this.artifactCells.clear();
        this.cellGrid.getChildren().clear();
    }

    private void setHighlighted(ArtifactWrapper columnArtifact, ArtifactWrapper rowArtifact) {
        setHighlightedColumn(columnArtifact);
        setHighlightedRow(rowArtifact);
    }

    private void setHighlightedColumn(ArtifactWrapper artifact) {
        boolean columnAlreadyHighlighted = false;

        ArtifactWrapper currentColumn = highlighted.getLeft();

        if (null != currentColumn) {
            columnAlreadyHighlighted = currentColumn.equals(artifact);
            if (!columnAlreadyHighlighted) {
                ArtifactComponents currentColumnComponents = this.artifactComponents.get(currentColumn);
                currentColumnComponents.columnLabelView.getView().getStyleClass().remove("highlighted");
            }
        }

        if (null != artifact && !columnAlreadyHighlighted) {
            ArtifactComponents components = this.artifactComponents.get(artifact);
            components.columnLabelView.getView().getStyleClass().add("highlighted");
            highlighted.setLeft(artifact);
        }
    }

    private void setHighlightedRow(ArtifactWrapper artifact) {

        boolean rowAlreadyHighlighted = false;

        ArtifactWrapper currentRow = highlighted.getRight();

        try {
            if (null != currentRow) {
                rowAlreadyHighlighted = currentRow.equals(artifact);
                if (!rowAlreadyHighlighted) {
                    ArtifactComponents currentRowComponents = this.artifactComponents.get(currentRow);
                    currentRowComponents.rowLabelView.getView().getStyleClass().remove("highlighted");
                }
            }
        } catch (Exception ex) {
            logger.log(Level.INFO, null, ex);
        }

        if (null != artifact && !rowAlreadyHighlighted) {
            ArtifactComponents components = this.artifactComponents.get(artifact);
            components.rowLabelView.getView().getStyleClass().add("highlighted");
            highlighted.setRight(artifact);
        }

    }

}
