package de.uos.se.prom.dsmproject.gui.typeVisibility;

import de.uos.se.prom.dsmproject.bl.ArtifactEditor;
import de.uos.se.prom.dsmproject.bl.ViewEditor;
import de.uos.se.prom.dsmproject.entity.Artifacttype;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import javax.inject.Inject;

public class TypeVisibilityPresenter implements Initializable {

    @FXML
    VBox checkboxContainer;

    @Inject
    ArtifactEditor artifactEditor;

    @Inject
    ViewEditor viewEditor;

    private List<CheckBox> checkBoxes = new LinkedList<>();

    private List<Artifacttype> oldHiddenTypes;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Collection<Artifacttype> artifacttypes = artifactEditor.getArtifacttypes();
        oldHiddenTypes = viewEditor.getHiddenTypes();

        for (Artifacttype artifacttype : artifacttypes) {
            CheckBox checkBox = new CheckBox(artifacttype.getName());
            checkBox.setUserData(artifacttype);
            if (oldHiddenTypes.contains(artifacttype)) {
                checkBox.setSelected(true);
            }
            this.checkBoxes.add(checkBox);
            this.checkboxContainer.getChildren().add(checkBox);
        }
    }

    boolean hiddenTypesChanged() {
        List<Artifacttype> newHiddenTypes = getHiddenTypes();
        boolean sameSize = (newHiddenTypes.size() == oldHiddenTypes.size());
        if (!sameSize) {
            return true;
        }

        return !newHiddenTypes.containsAll(oldHiddenTypes);
    }

    List<Artifacttype> getHiddenTypes() {
        List<Artifacttype> newHiddenTypes = new LinkedList<>();
        for (CheckBox checkBox : checkBoxes) {
            if (checkBox.isSelected()) {
                newHiddenTypes.add((Artifacttype) checkBox.getUserData());
            }
        }

        return newHiddenTypes;
    }

}
