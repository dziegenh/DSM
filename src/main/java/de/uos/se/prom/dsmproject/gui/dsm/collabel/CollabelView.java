/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package de.uos.se.prom.dsmproject.gui.dsm.collabel;

import de.uos.se.prom.dsmproject.gui.dsm.artifactlabel.ArtifactLabelView;

/**
 *
 * @author dziegenhagen
 */
public class CollabelView extends ArtifactLabelView {
    
    public CollabelPresenter getRealPresenter() {
        return (CollabelPresenter) super.getPresenter();
    }
    
}
