package de.uos.se.prom.dsmproject.gui.dsm.dsmcell;

import de.uos.se.prom.dsmproject.bl.ProjectEditor;
import de.uos.se.prom.dsmproject.bl.event.Event;
import de.uos.se.prom.dsmproject.bl.event.EventBus;
import de.uos.se.prom.dsmproject.bl.event.EventListener;
import de.uos.se.prom.dsmproject.bl.events.DependenciesWeightedChanged;
import de.uos.se.prom.dsmproject.entity.Artifact;
import de.uos.se.prom.dsmproject.entity.Dependency;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javax.inject.Inject;

/**
 *
 * @author dziegenhagen
 */
public class DsmCellPresenter implements Initializable {

    private final static String DEPENDENCY_INDICATOR = "X";

    @FXML
    TextField text;

    @Inject
    ProjectEditor projectEditor;

    private Artifact sourceArtifact;
    private Artifact targetArtifact;

    private boolean weighted;

    @Inject
    EventBus eventBus;

    EventListener<Event> weightedListener;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        final boolean weighted = projectEditor.getCurrentProject().areDependenciesWeighted();
        setWeighted(weighted);
        weightedListener = (event) -> {
            setWeighted(weighted);
        };

        eventBus.addListener(DependenciesWeightedChanged.TOPIC, weightedListener);
    }

    public void setWeighted(boolean weighted) {
        this.weighted = weighted;

        text.setEditable(false);

        /**
         * Weighted cells allow number input by the user.
         */
        if (weighted) {
            text.setOnMouseClicked((event) -> {
                if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
                    text.setEditable(true);
                }
            });

        } /**
         * Non-weighted cells allow toggling the dependency status.
         */
        else {
            text.setOnMouseClicked((event) -> {
                if (text.getText().equals(DEPENDENCY_INDICATOR)) {
                    projectEditor.removeDepenendency(sourceArtifact, targetArtifact);
                } else {
                    projectEditor.addDepenendency(sourceArtifact, targetArtifact, 1.0);
                }
            });
        }
    }

    public void setArtifacts(Artifact source, Artifact target) {
        this.sourceArtifact = source;
        this.targetArtifact = target;

        if (source.equals(target)) {
            text.setDisable(true);
        }

        Dependency dependency = projectEditor.getDependency(source, target);
        if (null != dependency) {
            dependencyAdded(dependency.getWeight());
        }

        text.setTooltip(new Tooltip(source.getName() + " - " + target.getName()));
    }

    public void dependencyAdded(double weight) {
        if (weighted) {
            text.setText("" + weight);
        } else {
            text.setText(DEPENDENCY_INDICATOR);
        }

    }

    public void dependencyDeleted() {
        text.clear();
    }

    public void destroy() {
        eventBus.removeListener(DependenciesWeightedChanged.TOPIC, weightedListener);
    }

}
