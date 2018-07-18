package de.uos.se.prom.dsmproject.gui.dsm.dsmcell;

import com.airhacks.afterburner.views.FXMLView;

/**
 *
 * @author dziegenhagen
 */
public class DsmCellView extends FXMLView {
    
    public DsmCellPresenter getRealPresenter() {
        return (DsmCellPresenter) super.getPresenter();
    }
    
}
