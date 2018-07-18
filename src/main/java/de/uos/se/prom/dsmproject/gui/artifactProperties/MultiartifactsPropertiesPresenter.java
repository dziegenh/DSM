package de.uos.se.prom.dsmproject.gui.artifactProperties;

import de.uos.se.prom.dsmproject.entity.Artifact;
import de.uos.se.prom.dsmproject.entity.Artifacttype;
import java.util.List;

/**
 *
 * @author dziegenhagen
 */
public class MultiartifactsPropertiesPresenter extends ArtifacttypePresenter {

    private Artifacttype commonType;

    void setArtifacts(List<Artifact> artifacts) {
        if (artifacts.isEmpty()) {
            return;
        }

        commonType = getCommonType(artifacts);

        if (null != commonType) {
            super.selectedType = commonType;
            this.inputType.getSelectionModel().select(commonType);
        }
    }

    boolean selectedTypeIsCommonType() {
        if (null == commonType || null == selectedType) {
            return false;
        }

        return selectedType.equals(commonType);
    }

    Artifacttype getCommonType(List<Artifact> artifacts) {
        if (artifacts.isEmpty()) {
            return null;
        }

        Artifacttype commonType = artifacts.remove(0).getType();

        for (Artifact artifact : artifacts) {
            if (!artifact.getType().equals(commonType)) {
                return null;
            }
        }

        return commonType;
    }

}
