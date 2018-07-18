package de.uos.se.prom.dsmproject.gui.dsm.artifactlabel;

import com.airhacks.afterburner.views.FXMLView;

/**
 *
 * @author dziegenhagen
 */
public abstract class ArtifactLabelView extends FXMLView {
    
        public ArtifactLabelPresenter getRealPresenter() {
        return (ArtifactLabelPresenter) super.getPresenter();
    }

}
