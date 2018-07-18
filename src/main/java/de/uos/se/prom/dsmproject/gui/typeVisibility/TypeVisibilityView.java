package de.uos.se.prom.dsmproject.gui.typeVisibility;

import com.airhacks.afterburner.views.FXMLView;

/**
 *
 * @author dziegenhagen
 */
public class TypeVisibilityView extends FXMLView {

    TypeVisibilityPresenter getRealPresenter() {
        return (TypeVisibilityPresenter) getPresenter();
    }

}
