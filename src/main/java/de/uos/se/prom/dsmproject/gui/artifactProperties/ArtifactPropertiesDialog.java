package de.uos.se.prom.dsmproject.gui.artifactProperties;

import com.sun.istack.internal.logging.Logger;
import de.uos.se.prom.dsmproject.bl.ArtifactEditor;
import de.uos.se.prom.dsmproject.entity.Artifact;
import de.uos.se.prom.dsmproject.entity.Artifacttype;
import java.util.LinkedList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javax.inject.Inject;

/**
 * Launcher for attribute edit dialogs.
 *
 * @author dziegenh
 */
public class ArtifactPropertiesDialog {

    @Inject
    ArtifactEditor artifactEditor;

    public void createEditorDialog() {
        this.createEditorDialog(null);
    }

    Logger logger = Logger.getLogger(ArtifactPropertiesDialog.class);

    /**
     * Creates and shows a dialog for the given elements.
     *
     * @param elements
     */
    public void createEditorDialog(Artifact artifact) {
        Dialog dialog = createDialog(artifact);
        dialog.showAndWait();
    }

    public void createMultiEditorDialog(List<Artifact> artifacts) {
        Dialog<Boolean> dialog = createMultiDialog(artifacts);
        dialog.showAndWait();
    }

    private Dialog<Boolean> createDialog(Artifact artifact) {

        Dialog<Boolean> dialog = new Dialog<>();

        String preTitle = null == artifact ? "Create" : "Edit";
        dialog.setTitle(preTitle + " Artifact");
        dialog.setResizable(false);

        // create panel containing attribute fields
        ArtifactPropertiesView view = new ArtifactPropertiesView();
        view.getRealPresenter().setArtifact(artifact);
        dialog.getDialogPane().setContent(view.getView());

        // Add button to dialog
        ButtonType buttonTypeOk = new ButtonType("Ok", ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(buttonTypeOk, buttonTypeCancel);

        final Button btOk = (Button) dialog.getDialogPane().lookupButton(buttonTypeOk);
        btOk.addEventFilter(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                boolean closeDialog = false;

                String name = view.getRealPresenter().getName();
                Artifacttype type = view.getRealPresenter().getType();

                if (name.trim().isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Artifact name can not be empty.", ButtonType.OK);
                    alert.show();

                } else {

                    if (null == artifact) {

                        if (artifactEditor.hasArtifact(name)) {
                            alertNameExists(name);
                        } else {
                            artifactEditor.addArtifact(name, type);
                            closeDialog = true;
                        }

                    } else {
                        if (!artifact.getName().equals(name) && artifactEditor.hasArtifact(name)) {
                            alertNameExists(name);
                        } else {
                            artifactEditor.editArtifact(artifact, name, type);
                            closeDialog = true;
                        }

                    }
                }

                if (!closeDialog) {
                    // prevent the dialog to close
                    event.consume();
                }
            }
        });

        return dialog;
    }

    private Dialog<Boolean> createMultiDialog(List<Artifact> artifacts) {

        Dialog<Boolean> dialog = new Dialog<>();

        dialog.setTitle("Edit multiple Artifacts");
        dialog.setResizable(false);

        // create panel containing attribute fields
        MultiartifactsPropertiesView view = new MultiartifactsPropertiesView();
        view.getRealPresenter().setArtifacts(new LinkedList<>(artifacts));
        dialog.getDialogPane().setContent(view.getView());

        // Add button to dialog
        ButtonType buttonTypeOk = new ButtonType("Ok", ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(buttonTypeOk, buttonTypeCancel);

        final Button btOk = (Button) dialog.getDialogPane().lookupButton(buttonTypeOk);
        btOk.addEventFilter(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                final MultiartifactsPropertiesPresenter presenter = view.getRealPresenter();
                // perform editing only if there were changes
                if (!presenter.selectedTypeIsCommonType()) {
                    Artifacttype type = presenter.getType();
                    artifactEditor.setTypeFor(artifacts, type);
                }
            }
        });

        return dialog;
    }

    private void alertNameExists(String name) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "An artifact named '" + name + "' already exists.", ButtonType.OK);
        alert.show();
    }

}
