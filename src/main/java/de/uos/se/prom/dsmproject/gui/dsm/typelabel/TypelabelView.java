package de.uos.se.prom.dsmproject.gui.dsm.typelabel;

import com.airhacks.afterburner.views.FXMLView;

/**
 *
 * @author dziegenhagen
 */
public class TypelabelView extends FXMLView {

    public TypelabelPresenter getRealPresenter() {
        return (TypelabelPresenter) super.getPresenter();
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof TypelabelView)) {
            return false;
        }

        TypelabelView other = (TypelabelView) obj;
        return other.getRealPresenter().getId() == getRealPresenter().getId();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        return getRealPresenter().getId() * hash;
    }

}
