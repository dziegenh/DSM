package de.uos.se.prom.dsmproject.gui.artifactProperties;

import de.uos.se.prom.dsmproject.entity.Artifact;
import de.uos.se.prom.dsmproject.entity.Artifacttype;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

/**
 * Set artifact properties like name and type. FA06, FA13
 */
public class ArtifactPropertiesPresenter extends ArtifacttypePresenter {

    @FXML
    private TextField inputName;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        Artifacttype lastUsedType = artifactEditor.getLastUsedType();
        if (null != lastUsedType) {
            this.inputType.getSelectionModel().select(lastUsedType);
            this.selectedType = lastUsedType;
        }
    }

    void setArtifact(Artifact artifact) {
        if (null == artifact) {
            return;
        }

        this.inputName.setText(artifact.getName());
        this.inputType.getSelectionModel().select(artifact.getType());
        this.selectedType = artifact.getType();
    }

    String getName() {
        return inputName.getText().trim();
    }

}
