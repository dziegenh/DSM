package de.uos.se.prom.dsmproject.gui.dg;

import com.airhacks.afterburner.views.FXMLView;

/**
 *
 * @author dziegenhagen
 */
public class DgView extends FXMLView {

    public DgPresenter getRealPresenter() {
        return (DgPresenter) super.getPresenter();
    }

}
