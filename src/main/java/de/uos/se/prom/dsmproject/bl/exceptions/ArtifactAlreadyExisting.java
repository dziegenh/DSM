package de.uos.se.prom.dsmproject.bl.exceptions;

import de.uos.se.prom.dsmproject.entity.Artifact;

/**
 *
 * @author dziegenhagen
 */
public class ArtifactAlreadyExisting extends Exception {

    public ArtifactAlreadyExisting(Artifact artifact) {
        super("Artifact already existing: " + artifact.toString());
    }

}
