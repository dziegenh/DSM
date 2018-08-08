/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package de.uos.se.prom.dsmproject.gui.dsm.rowlabel;

import de.uos.se.prom.dsmproject.gui.dsm.artifactlabel.ArtifactLabelView;

/**
 *
 * @author dziegenhagen
 */
public class RowlabelView extends ArtifactLabelView {
    
    public RowlabelPresenter getRealPresenter() {
        return (RowlabelPresenter) super.getPresenter();
    }
    
}
