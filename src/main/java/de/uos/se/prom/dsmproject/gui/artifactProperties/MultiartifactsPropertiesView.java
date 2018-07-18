package de.uos.se.prom.dsmproject.gui.artifactProperties;

import com.airhacks.afterburner.views.FXMLView;

/**
 *
 * @author dziegenhagen
 */
public class MultiartifactsPropertiesView extends FXMLView {

    MultiartifactsPropertiesPresenter getRealPresenter() {
        return (MultiartifactsPropertiesPresenter) this.getPresenter();
    }

}
