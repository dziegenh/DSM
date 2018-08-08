package de.uos.se.prom.dsmproject.gui;

import com.airhacks.afterburner.injection.Injector;
import de.uos.se.prom.dsmproject.bl.ArtifactEditor;
import de.uos.se.prom.dsmproject.entity.Artifact;
import de.uos.se.prom.dsmproject.gui.artifactProperties.ArtifactPropertiesDialog;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javax.inject.Inject;

/**
 *
 * @author dziegenhagen
 */
public class DragDropHandler {
//
//    @Inject
//    ArtifactEditor artEd;

    private Node target;

    public DragDropHandler(Node target) {
        this.target = target;

        target.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* data is dragged over the target */
//                System.out.println("onDragOver");

                /* accept it only if it is  not dragged from the same node 
                 * and if it has a string data */
                if (event.getGestureSource() != target
                        && event.getDragboard().hasString()) {
                    /* allow for both copying and moving, whatever user chooses */
                    event.acceptTransferModes(TransferMode.COPY, TransferMode.MOVE);
                }

                event.consume();
            }
        });

        target.setOnDragEntered(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* the drag-and-drop gesture entered the target */
//                System.out.println("onDragEntered");
                /* show to the user that it is an actual gesture target */
                if (event.getGestureSource() != target
                        && event.getDragboard().hasString()) {
//                    System.out.println("accept drag content");
//                    target.setFill(Color.GREEN);
                }

                event.consume();
            }
        });
//
        target.setOnDragExited(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {

//                System.out.println("Bye drag!");
                event.consume();

            }
        });

        target.setOnDragDropped(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                /* data dropped */
                System.out.println("onDragDropped");
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasString()) {
//                    artEd.
                    Artifact artifact = new Artifact(null, db.getString());

                    // Show Dialog to specify missing artifact data
                    ArtifactPropertiesDialog dialog = new ArtifactPropertiesDialog();
                    Injector.injectMembers(ArtifactPropertiesDialog.class, dialog);
                    dialog.createEditorDialog(artifact);

//                    System.out.println("Add new Artifact: '" + db.getString() + "'");
//                    target.setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);

                event.consume();
            }
        });

    }
}
