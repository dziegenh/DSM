package de.uos.se.prom.dsmproject.bl;

import java.util.List;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "projectList")
public class ProjectList {
	
    private List<String> projects;
    
   
    @XmlElement(name = "project")
	public List<String> getProjects() {
        return projects;
    }
 
    public void setProjects(List<String> project) {
        this.projects = project;
    }

}
