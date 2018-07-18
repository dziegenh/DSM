package de.uos.se.prom.dsmproject.gui.artifactProperties;

import de.uos.se.prom.dsmproject.bl.ArtifactEditor;
import de.uos.se.prom.dsmproject.entity.Artifacttype;
import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javax.inject.Inject;

/**
 *
 * @author dziegenhagen
 */
public abstract class ArtifacttypePresenter implements Initializable {

    @Inject
    protected ArtifactEditor artifactEditor;

    @FXML
    protected ComboBox inputType;

    protected Artifacttype selectedType;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Collection<Artifacttype> artifacttypes = artifactEditor.getArtifacttypes();
        this.inputType.getItems().addAll(artifacttypes);
    }

    @FXML
    void onSelectType(ActionEvent event) {
        final Object selectedItem = inputType.getSelectionModel().getSelectedItem();
        if (null != selectedItem && selectedItem instanceof Artifacttype) {
            this.selectedType = (Artifacttype) selectedItem;
        } else {
            this.selectedType = null;
        }
    }

    Artifacttype getType() {
        if (null == selectedType) {
            final String typeName = inputType.getEditor().getText();
            selectedType = new Artifacttype(typeName);
        }
        return selectedType;
    }

}
