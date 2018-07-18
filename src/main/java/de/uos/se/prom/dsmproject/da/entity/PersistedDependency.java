package de.uos.se.prom.dsmproject.da.entity;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author dziegenhagen
 */
@XmlRootElement
public class PersistedDependency {

    @XmlAttribute
    public String sourceArtifactName;

    @XmlAttribute
    public String targetArtifactName;

    @XmlAttribute
    public double weight;

}
