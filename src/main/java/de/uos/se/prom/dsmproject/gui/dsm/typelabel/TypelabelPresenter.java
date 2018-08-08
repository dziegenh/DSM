package de.uos.se.prom.dsmproject.gui.dsm.typelabel;

import de.uos.se.prom.dsmproject.bl.SelectionController;
import de.uos.se.prom.dsmproject.bl.event.Event;
import de.uos.se.prom.dsmproject.bl.event.EventBus;
import de.uos.se.prom.dsmproject.bl.event.EventListener;
import de.uos.se.prom.dsmproject.bl.events.SelectionChanged;
import de.uos.se.prom.dsmproject.entity.Artifact;
import de.uos.se.prom.dsmproject.entity.Artifacttype;
import de.uos.se.prom.dsmproject.gui.dsm.ArtifactWrapper;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javax.inject.Inject;

/**
 *
 * @author dziegenhagen
 */
public class TypelabelPresenter implements Initializable {

    @Inject
    SelectionController selectionController;

    @Inject
    EventBus eventBus;

    @FXML
    private Label typelabel;

    @FXML
    private AnchorPane rootPane;

    int id;
    List<ArtifactWrapper> forArtifacts = new LinkedList<>();

    boolean isSelected = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        eventListener = (event) -> {
            SelectionChanged realEvent = (SelectionChanged) event;
            // this type is selected if the global selection contains each of this type's artifacts
            this.isSelected = selectionController.getSelection().containsAll(getArtifacts());
            applySelectedState();
        };
        eventBus.addListener(SelectionChanged.TOPIC, eventListener);
    }
    private EventListener<Event> eventListener;

    public void setType(Artifacttype type) {
        if (null == type) {
            this.typelabel.setText(null);
        } else {
            this.typelabel.setText(type.getName());
        }
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void clearArtifacts() {
        this.forArtifacts.clear();
    }

    public void addArtifact(ArtifactWrapper wrapper) {
        this.forArtifacts.add(wrapper);
    }

    @FXML
    void onMouseClicked(MouseEvent event) {
        List<Artifact> artifacts = getArtifacts();

        if (!isSelected) {
            if (event.isControlDown()) {
                selectionController.addToSelection(artifacts);
            } else {
                selectionController.select(artifacts);
            }
        } else {
            if (event.isControlDown()) {
                selectionController.removeFromSelection(artifacts);
            } else {
                selectionController.clear();
            }
        }

        // additional actions will be performed when the selection event is received; see listener
    }

    private List<Artifact> getArtifacts() {
        List<Artifact> artifacts = new LinkedList<>();
        for (ArtifactWrapper wrapper : forArtifacts) {
            artifacts.add(wrapper.getArtifact());
        }
        return artifacts;
    }

    private void applySelectedState() {
        // TODO use CSS class
        if (isSelected) {
            rootPane.setStyle("-fx-background-color: #99f;");
        } else {
            rootPane.setStyle("-fx-background-color: transparent;");

        }
    }

    public void destroy() {
        this.eventBus.removeListener(SelectionChanged.TOPIC, eventListener);
    }

}
