package de.uos.se.prom.dsmproject.da.entity;

import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author dziegenhagen
 */
@XmlRootElement
public class PersistedView {

    public List<String> hiddenTypesNames;

    @XmlAttribute
    public String dsmSortingName;

    @XmlAttribute
    public String name;

}
