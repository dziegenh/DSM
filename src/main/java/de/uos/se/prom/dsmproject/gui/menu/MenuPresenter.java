package de.uos.se.prom.dsmproject.gui.menu;

import com.airhacks.afterburner.injection.Injector;
import de.uos.se.prom.dsmproject.bl.ArtifactEditor;
import de.uos.se.prom.dsmproject.bl.ProjectController;
import de.uos.se.prom.dsmproject.bl.ProjectEditor;
import de.uos.se.prom.dsmproject.bl.SelectionController;
import de.uos.se.prom.dsmproject.bl.ViewEditor;
import de.uos.se.prom.dsmproject.bl.application.ExceptionHandler;
import de.uos.se.prom.dsmproject.bl.event.EventBus;
import de.uos.se.prom.dsmproject.bl.events.AdjustDsmViewport;
import de.uos.se.prom.dsmproject.bl.events.AutoAdjustDsmChanged;
import de.uos.se.prom.dsmproject.bl.events.AutoSortChanged;
import de.uos.se.prom.dsmproject.bl.export.Exporter;
import de.uos.se.prom.dsmproject.entity.DsmSorting;
import de.uos.se.prom.dsmproject.gui.EnumTranslator;
import de.uos.se.prom.dsmproject.gui.about.AboutView;
import de.uos.se.prom.dsmproject.gui.artifactProperties.ArtifactPropertiesDialog;
import de.uos.se.prom.dsmproject.gui.typeVisibility.TypeVisibilityDialog;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Dialog;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javax.inject.Inject;

public class MenuPresenter implements Initializable {

    @Inject
    ExceptionHandler exceptionHandler;

    @Inject
    ProjectController projectController;

    @Inject
    ProjectEditor projectEditor;

    @Inject
    ArtifactEditor artifactEditor;

    @Inject
    SelectionController selectionController;

    @Inject
    ViewEditor viewController;

    @FXML
    AnchorPane menuAnchorPane;

    @FXML
    MenuItem mi_saveProject;

    @FXML
    CheckMenuItem mi_autoAdjust;

    @FXML
    CheckMenuItem mi_autoSort;

    @FXML
    private Menu menuViewSort;

    @Inject
    EventBus eventBus;

    /**
     * True if the current project has unsaved changes. Used to show a confirm
     * dialog when these changes would be discarded on menu actions.
     */
    private boolean unsavedChanges = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mi_saveProject.disableProperty().bind(projectController.getHasProjectFileProperty().not());

        // Create Sorting menu
        EnumTranslator enumTranslator = new EnumTranslator();
        DsmSorting[] sortings = DsmSorting.values();
        for (DsmSorting sorting : sortings) {
            String sortingText = enumTranslator.translateDsmSorting(sorting);
            MenuItem sortingMenuItem = new MenuItem(sortingText);
            sortingMenuItem.setOnAction((event) -> {
                viewController.setSorting(sorting);
            });
            menuViewSort.getItems().add(sortingMenuItem);
        }

        // Update "saved asterisk" in window title when saved state changes
        projectEditor.getHasUnsavedChanges().addListener((observable, oldValue, newValue) -> {
            unsavedChanges = newValue;
        });

        eventBus.addListener(AutoAdjustDsmChanged.TOPIC, (event) -> {
            AutoAdjustDsmChanged realEvent = (AutoAdjustDsmChanged) event;
            mi_autoAdjust.setSelected(realEvent.isAutoAdjust());
        });

        eventBus.addListener(AutoSortChanged.TOPIC, (event) -> {
            AutoSortChanged realEvent = (AutoSortChanged) event;
            mi_autoSort.setSelected(realEvent.isAutoSort());
        });
    }

    @FXML
    void onEditTypeVisibility(ActionEvent event) {
        TypeVisibilityDialog dialog = new TypeVisibilityDialog();
        Injector.injectMembers(TypeVisibilityDialog.class, dialog);
        dialog.createEditorDialog();
    }

    @FXML
    public void onNewProject(ActionEvent event) {
        if (!discardChanges()) {
            return;
        }

        projectController.newProject();
    }

    @FXML
    public void onLoadProject(ActionEvent event) {

        if (!discardChanges()) {
            return;
        }

        Window window = getWindow();
        FileChooser fileChooser = createDsmFileChooser();
        File selectedFile = fileChooser.showOpenDialog(window);

        if (selectedFile != null) {
            this.projectController.loadProject(selectedFile);
        }
    }

    @FXML
    public void onSaveProject(ActionEvent event) {
        projectController.saveProject();
    }

    @FXML
    public void onExit(ActionEvent event) {
        if (!discardChanges()) {
            return;
        }

        getWindow().fireEvent(new WindowEvent(getWindow(), WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    @FXML
    public void onCreateRandomProject(ActionEvent event) {
        if (!discardChanges()) {
            return;
        }

        projectController.createRandomProject();
    }

    @FXML
    public void onSaveProjectAs(ActionEvent event) {
        Window window = getWindow();
        FileChooser fileChooser = createDsmFileChooser();
        File selectedFile = fileChooser.showSaveDialog(window);

        if (selectedFile != null) {
            try {
                projectController.saveProjectAs(selectedFile);

            } catch (Exception ex) {
                this.exceptionHandler.exceptionCatched(ex);
            }
        }
    }

    @FXML
    public void onExportCsv(ActionEvent event) {
        Window window = getWindow();
        FileChooser fileChooser = createFileChooser("Select CSV file", "CSV File", "*.csv");
        File selectedFile = fileChooser.showSaveDialog(window);

        if (selectedFile != null) {
            try {
                Exporter exporter = new Exporter();
                Injector.injectMembers(Exporter.class, exporter);
                exporter.exportAsCsv(selectedFile);

            } catch (Exception ex) {
                this.exceptionHandler.exceptionCatched(ex);
            }
        }
    }

    @FXML
    public void onNewArtifact(ActionEvent event) {
        ArtifactPropertiesDialog dialog = new ArtifactPropertiesDialog();
        Injector.injectMembers(ArtifactPropertiesDialog.class, dialog);
        dialog.createEditorDialog(null);
    }

    @FXML
    public void onChangeSorting(ActionEvent event) {
    }

    @FXML
    void onAdjustViewport(ActionEvent event) {
        eventBus.fireEvent(new AdjustDsmViewport());

    }

    @FXML
    void onAutoAdjust(ActionEvent event) {
        final boolean autoAdjust = mi_autoAdjust.isSelected();
        eventBus.fireEvent(new AutoAdjustDsmChanged(autoAdjust));
    }

    @FXML
    void onAutoSort(ActionEvent event) {
        final boolean autoSort = mi_autoSort.isSelected();
        eventBus.fireEvent(new AutoSortChanged(autoSort));
    }

    @FXML
    void onExtractMeta(ActionEvent event) {
        if (!discardChanges()) {
            return;
        }

        projectController.createMetaProject();
    }

    private Window getWindow() {
        Window window = menuAnchorPane.getScene().getWindow();
        return window;
    }

    private FileChooser createDsmFileChooser() {
        final String title = "Choose Project File";
        final String fileDescription = "DSM Project Files";
        final String fileExtension = "*.dsm";

        return createFileChooser(title, fileDescription, fileExtension);
    }

    private FileChooser createFileChooser(final String title, final String fileDescription, final String fileExtension) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);

        fileChooser.getExtensionFilters().addAll(new ExtensionFilter(fileDescription, fileExtension),
                new ExtensionFilter("All Files", "*.*"));
        return fileChooser;
    }

    // TODO old stuff below - check what can be used here
    @FXML
    void onEditDelete(ActionEvent event) {
        // TODO
    }

    @FXML
    void onEditSelectAll(ActionEvent event) {
        selectionController.selectAll();
    }

    @FXML
    void onZoomIn(ActionEvent event) {
        // TODO
//        this.zoomer.zoomIn();
    }

    @FXML
    void onZoomOut(ActionEvent event) {
        // TODO
//        this.zoomer.zoomOut();
    }

    @FXML
    void onResetZoom(ActionEvent event) {
        // TODO
//        this.zoomer.resetZoom();
    }

    @FXML
    void onSimConfig(ActionEvent event) {

    }

    @FXML
    void onHelpAbout(ActionEvent event) {
        Dialog<Object> dialog = new Dialog<>();

        dialog.setTitle("About");
        dialog.setResizable(false);

        // TODO add/set content
        dialog.getDialogPane().setContent(new AboutView().getView());
        // Add button to dialog
        ButtonType buttonTypeOk = new ButtonType("Close", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);

        dialog.showAndWait();
    }

    /**
     * If the project has unsaved changes, a confirm dialog will be shown.
     *
     * @return True if there are no unsaved changes or if the changes should be
     * discarded.
     */
    boolean discardChanges() {
        if (!unsavedChanges) {
            return true;
        }

        Alert alert = new Alert(AlertType.CONFIRMATION, "The project contains unsaved changes, which will be discarded.\nDo you really want to proceed?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait();

        return alert.getResult() == ButtonType.YES;
    }

}
