package de.uos.se.prom.dsmproject.gui;

import com.airhacks.afterburner.injection.Injector;
import de.uos.se.prom.dsmproject.entity.Artifact;
import de.uos.se.prom.dsmproject.gui.artifactProperties.ArtifactPropertiesDialog;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

/**
 *
 * @author dziegenhagen
 */
public class DragDropHandler {

    private final Node target;

    public DragDropHandler(Node target) {
        this.target = target;

        target.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                // accept dragged data if:
                // - it is  dragged from another node 
                // - and it is string data
                if (event.getGestureSource() != target
                        && event.getDragboard().hasString()) {
                    event.acceptTransferModes(TransferMode.COPY, TransferMode.MOVE);
                }

                event.consume();
            }
        });

        target.setOnDragEntered(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                if (event.getGestureSource() != target
                        && event.getDragboard().hasString()) {
                    // TODO (or remove method :)
                }

                event.consume();
            }
        });

        target.setOnDragExited(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {

                // TODO (or remove method :)
                event.consume();

            }
        });

        target.setOnDragDropped(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {

                // if there is a string data on dragboard, read it and use it
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasString()) {
                    Artifact artifact = new Artifact(null, db.getString());

                    // Show Dialog to specify missing artifact data
                    ArtifactPropertiesDialog dialog = new ArtifactPropertiesDialog();
                    Injector.injectMembers(ArtifactPropertiesDialog.class, dialog);
                    dialog.createEditorDialog(artifact);

                    success = true;
                }

                // let the source know whether the string was successfully transferred and used
                event.setDropCompleted(success);

                event.consume();
            }
        });

    }
}
