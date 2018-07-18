/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package de.uos.se.prom.dsmproject.gui.dsm.rowlabel;

import de.uos.se.prom.dsmproject.gui.dsm.artifactlabel.ArtifactLabelPresenter;
import de.uos.se.prom.dsmproject.entity.Artifact;
import javafx.fxml.Initializable;

/**
 *
 * @author dziegenhagen
 */
public class RowlabelPresenter extends ArtifactLabelPresenter implements Initializable {

    @Override
    public void setArtifact(Artifact artifact) {
        super.setArtifact(artifact);
        this.nameLabel.setText(artifact.getName());
    }

}
