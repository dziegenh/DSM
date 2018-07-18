package de.uos.se.prom.dsmproject.gui.dsm.collabel;

import de.uos.se.prom.dsmproject.entity.Artifact;
import de.uos.se.prom.dsmproject.gui.dsm.artifactlabel.ArtifactLabelPresenter;

/**
 *
 * @author dziegenhagen
 */
public class CollabelPresenter extends ArtifactLabelPresenter {

    @Override
    public void setArtifact(Artifact artifact) {
        super.setArtifact(artifact);
        this.nameLabel.setText(artifact.getName());
    }

}
