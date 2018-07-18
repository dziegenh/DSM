package de.uos.se.prom.dsmproject.da.entity;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author dziegenhagen
 */
@XmlRootElement
public class PersistedArtifact {

    @XmlAttribute
    public String name;

    @XmlAttribute
    public String typeName;

}
