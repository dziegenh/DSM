package de.uos.se.prom.dsmproject.gui.dsm;

import com.airhacks.afterburner.views.FXMLView;

/**
 *
 * @author dziegenhagen
 */
public class DsmView extends FXMLView {

    public DsmPresenter getRealPresenter() {
        return (DsmPresenter) getPresenter();
    }

}
