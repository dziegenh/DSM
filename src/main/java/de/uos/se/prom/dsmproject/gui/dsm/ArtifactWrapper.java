package de.uos.se.prom.dsmproject.gui.dsm;

import de.uos.se.prom.dsmproject.entity.Artifact;

/**
 *
 * @author dziegenhagen
 */
public class ArtifactWrapper {

    private static int NEXT_ID = 0;

    private int id;

    private Artifact artifact;

    public ArtifactWrapper(Artifact artifact) {
        this.artifact = artifact;
        this.id = NEXT_ID++;
    }

    public Artifact getArtifact() {
        return artifact;
    }

    public void setArtifact(Artifact artifact) {
        this.artifact = artifact;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + this.id;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ArtifactWrapper other = (ArtifactWrapper) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

}
