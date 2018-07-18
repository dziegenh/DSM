package de.uos.se.prom.dsmproject.gui.dsm.artifactlabel;

import com.airhacks.afterburner.injection.Injector;
import de.uos.se.prom.dsmproject.bl.SelectionController;
import de.uos.se.prom.dsmproject.entity.Artifact;
import de.uos.se.prom.dsmproject.gui.artifactProperties.ArtifactPropertiesDialog;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javax.inject.Inject;

/**
 *
 * @author dziegenhagen
 */
public abstract class ArtifactLabelPresenter implements Initializable {

    Artifact artifact;

    @Inject
    SelectionController selectionController;

    @FXML
    protected Label nameLabel;

    @FXML
    protected Label nrLabel;

    @FXML
    private AnchorPane rootPane;

    protected ResourceBundle resources = null;
    private boolean selected;

    public void setArtifact(Artifact artifact) {
        this.artifact = artifact;
    }

    public void setNumber(int nr) {
        this.nrLabel.setText("" + nr);
    }

    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
        rootPane.setOnMouseClicked((MouseEvent event) -> {
            boolean isPrimMouse = event.getButton().equals(MouseButton.PRIMARY);
            boolean isSecMouse = event.getButton().equals(MouseButton.SECONDARY);

            boolean isCtrlDown = event.isControlDown();

            if (event.getClickCount() == 2 && isPrimMouse) {
                ArtifactPropertiesDialog dialog = new ArtifactPropertiesDialog();
                Injector.injectMembers(ArtifactPropertiesDialog.class, dialog);
                dialog.createEditorDialog(artifact);
                event.consume();

            } else if (event.getClickCount() == 1) {
                if (isPrimMouse) {
                    if (selected) {
                        if (isCtrlDown) {
                            selectionController.removeFromSelection(artifact);
                        } else {
                            selectionController.clear();
                        }
                    } else {
                        if (isCtrlDown) {
                            selectionController.addToSelection(artifact);
                        } else {
                            selectionController.select(artifact);
                        }
                    }
                } else if (isSecMouse) {
                    if (!selected) {
                        selectionController.select(artifact);
                    }
                }
            }
        });

        // Use the same menu singleton instance for all labels
        ArtifactContextmenu cmenu = Injector.instantiateModelOrService(ArtifactContextmenu.class);
        nameLabel.setContextMenu(cmenu);
        nrLabel.setContextMenu(cmenu);
    }

    public void setUnselected() {
        selected = false;
//        rootPane.getStyleClass().remove("selectedLabel");
        // TODO use CSS class
        rootPane.setStyle("-fx-background-color: transparent;");
    }

    public void setSelected() {
        selected = true;
//        rootPane.getStyleClass().add("selectedLabel");
        // TODO use CSS class
        rootPane.setStyle("-fx-background-color: #99f;");

    }

}
