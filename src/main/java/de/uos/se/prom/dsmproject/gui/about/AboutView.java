/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package de.uos.se.prom.dsmproject.gui.about;

import com.airhacks.afterburner.views.FXMLView;

/**
 *
 * @author dziegenhagen
 */
public class AboutView extends FXMLView {
    
    public AboutPresenter getRealPresenter() {
        return (AboutPresenter) super.getPresenter();
    }
    
}
