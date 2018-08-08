package de.uos.se.prom.dsmproject.gui.app;

import de.uos.se.prom.dsmproject.MainApp;
import de.uos.se.prom.dsmproject.bl.ProjectController;
import de.uos.se.prom.dsmproject.bl.ProjectEditor;
import de.uos.se.prom.dsmproject.bl.application.ExceptionHandler;
import de.uos.se.prom.dsmproject.bl.application.IExceptionCatchedListener;
import de.uos.se.prom.dsmproject.bl.event.EventBus;
import de.uos.se.prom.dsmproject.bl.events.AdjustDsmViewport;
import de.uos.se.prom.dsmproject.bl.events.AutoAdjustDsmChanged;
import de.uos.se.prom.dsmproject.bl.events.ProjectCreated;
import de.uos.se.prom.dsmproject.bl.events.ProjectLoaded;
import de.uos.se.prom.dsmproject.entity.Project;
import de.uos.se.prom.dsmproject.gui.DragDropHandler;
import de.uos.se.prom.dsmproject.gui.dg.DgView;
import de.uos.se.prom.dsmproject.gui.dsm.DsmView;
import de.uos.se.prom.dsmproject.gui.events.DsmSizeChanged;
import de.uos.se.prom.dsmproject.gui.menu.MenuView;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import javax.inject.Inject;

/**
 * Presenter for the main app contents.
 *
 * @author dziegenh
 */
public class AppPresenter implements Initializable {

    private static final double INITIAL_DIVIDER_POSITION = .25;

    @FXML
    private ScrollPane poolContainer;

    @FXML
    SplitPane mainSplitPane;

    @FXML
    AnchorPane appAnchorPane;

    @Inject
    ProjectController projectController;

    @Inject
    ProjectEditor projectEditor;

    @Inject
    ExceptionHandler exceptionHandler;

    @Inject
    EventBus eventBus;

    DsmView dsmView;

    // TODO get initial value from properties
    boolean autoAdjustDsm = false;

    String windowTitle = "";
    boolean unsavedChanges = false;

    private void setWindowTitle(Project project) {
        windowTitle = MainApp.DEFAULT_WINDOW_TITLE + " - " + project.getName();
        updateWindowTitle();
    }

    private void setSavedState(boolean saved) {
        this.unsavedChanges = saved;
        updateWindowTitle();
    }

    private void updateWindowTitle() {
        Window window = getWindow();
        if (window instanceof Stage) {
            String savedIndicator = unsavedChanges ? "*" : "";
            ((Stage) window).setTitle(windowTitle + savedIndicator);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        this.exceptionHandler.addExceptionCatchedListener(new IExceptionCatchedListener() {

            @Override
            public void exceptionCatched(Exception exception) {
                exception.printStackTrace();
            }
        });

        appAnchorPane.getChildren().add(new MenuView().getView());

        dsmView = new DsmView();
        poolContainer.setContent(dsmView.getView());
        mainSplitPane.getItems().add(new DgView().getView());
        mainSplitPane.setDividerPosition(0, INITIAL_DIVIDER_POSITION);

        // Update "saved asterisk" in window title when saved state changes
        projectEditor.getHasUnsavedChanges().addListener((observable, oldValue, newValue) -> {
            setSavedState(newValue);
        });

        eventBus.addListener(ProjectCreated.TOPIC, (event) -> {
            ProjectCreated realEvent = (ProjectCreated) event;
            setWindowTitle(realEvent.getProject());
        });
        eventBus.addListener(ProjectLoaded.TOPIC, (event) -> {
            ProjectLoaded realEvent = (ProjectLoaded) event;
            setWindowTitle(realEvent.getProject());
        });

        eventBus.addListener(AdjustDsmViewport.TOPIC, (event) -> {
            applyDsmViewportAdjustment();
        });
        eventBus.addListener(AutoAdjustDsmChanged.TOPIC, (event) -> {
            AutoAdjustDsmChanged realEvent = (AutoAdjustDsmChanged) event;
            autoAdjustDsm = realEvent.isAutoAdjust();
            if (autoAdjustDsm) {
                applyDsmViewportAdjustment();
            }
        });

        eventBus.addListener(DsmSizeChanged.TOPIC, (event) -> {
            if (autoAdjustDsm) {
                applyDsmViewportAdjustment();
            }
        });

        
        // Create handler for Drag-and-Drop-Actions
        DragDropHandler dragDropHandler = new DragDropHandler(mainSplitPane);
    }

    private Window getWindow() {
        Window window = mainSplitPane.getScene().getWindow();
        return window;
    }

    void applyDsmViewportAdjustment() {
        // Additional spacing is necessary when the scrollbar is visible
        double additionalViewportSpacing = 20;

        // compute width of the DSM inc. spacing
        Bounds dsmBounds = dsmView.getView().lookup("#cellGrid").getLayoutBounds();
        double dsmWidth = dsmBounds.getWidth() + additionalViewportSpacing;

        // Copmute divider pos. (=the ratio between DSM-width and splitpane-width)
        Bounds splitpaneBounds = mainSplitPane.getLayoutBounds();
        double dsmRatio = dsmWidth / splitpaneBounds.getWidth();

        mainSplitPane.setDividerPositions(dsmRatio);
    }

}
