package de.uos.se.prom.dsmproject.gui.dsm.artifactlabel;

import com.airhacks.afterburner.injection.Injector;
import de.uos.se.prom.dsmproject.bl.ArtifactEditor;
import de.uos.se.prom.dsmproject.bl.SelectionController;
import de.uos.se.prom.dsmproject.bl.ViewEditor;
import de.uos.se.prom.dsmproject.entity.Artifact;
import de.uos.se.prom.dsmproject.entity.Artifacttype;
import de.uos.se.prom.dsmproject.gui.artifactProperties.ArtifactPropertiesDialog;
import java.util.List;
import java.util.Set;
import javafx.event.ActionEvent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Contextmenu for the system elements treeview.F
 *
 * @author dziegenhagen
 */
public class ArtifactContextmenu extends ContextMenu {

    @Inject
    SelectionController selectionController;

    @Inject
    ArtifactEditor artifactEditor;

    @Inject
    ViewEditor viewEditor;

    private final MenuItem editItem = new MenuItem("Edit");
    private final MenuItem hidetypeItem = new MenuItem("Hide type(s)");
    private final MenuItem deleteItem = new MenuItem("Delete");

    @PostConstruct
    void createItems() {

        editItem.setOnAction((event) -> {
            ArtifactPropertiesDialog dialog = new ArtifactPropertiesDialog();
            Injector.injectMembers(ArtifactPropertiesDialog.class, dialog);
            List<Artifact> selection = selectionController.getSelection();

            if (1 == selection.size()) {
                dialog.createEditorDialog(selection.get(0));
            } else if (1 < selection.size()) {
                dialog.createMultiEditorDialog(selection);
            }

        });

        hidetypeItem.setOnAction((event) -> {
            Set<Artifacttype> selection = selectionController.getSelectedTypes();
            viewEditor.addHiddentypes(selection);
        });

        deleteItem.setOnAction((ActionEvent e) -> {
            List<Artifact> selection = selectionController.getSelection();
            if (1 == selection.size()) {
                artifactEditor.deleteArtifact(selection.get(0));
            } else if (1 < selection.size()) {
                artifactEditor.deleteArtifacts(selection);
            }
        });

        getItems().addAll(editItem, hidetypeItem, deleteItem);

    }
}
