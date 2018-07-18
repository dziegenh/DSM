package de.uos.se.prom.dsmproject.da.entity;

import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author dziegenhagen
 */
@XmlRootElement
public class PersistedProject {

    public List<PersistedArtifact> artifacts;

    public List<PersistedArtifacttype> artifacttypes;

    public List<PersistedDependency> dependencies;

    public List<PersistedView> views;

    @XmlAttribute
    public boolean directed;

    @XmlAttribute
    public boolean dependenciesWeighted;

    @XmlAttribute
    public String name;

    @XmlAttribute
    public String activeViewName;

}
