package de.uos.se.prom.dsmproject.bl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.*;


import java.util.List;
import java.util.logging.Level;

import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import com.sun.istack.internal.logging.Logger;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.GenericType;

import de.uos.se.prom.dsmproject.bl.event.EventBus;
import de.uos.se.prom.dsmproject.bl.events.ArtifactAdded;
import de.uos.se.prom.dsmproject.da.ProjectAccess;
import de.uos.se.prom.dsmproject.entity.Project;

/**
 * 
 * @author Markus Mohr
 *
 */
public class ServerController {
	

	

	
	private String http_server = "http://localhost:8080/dsm_webserver";
	private String http_xml = "/xml";
	
	//Currently loaded Project
	private File loadedProject;
	
	//HTTP Client
	private Client client = ClientBuilder.newClient();
	
	//Cookie for Session Management
	private Cookie cookie;
	
	//User Agent for HTTP Request Header
	private String user_agent = "DSMTool";
	

	
	/**
	 * Get Project List from Server
	 * @return Projectlist
	 */
	public List<String> getProjectList() {
		
		WebTarget target = client.target(http_server).path(http_xml);
		
		try {
			
			//Do Request and Set Cookie for Session Management
			Response response = target.request(MediaType.APPLICATION_XML).cookie(cookie).header("user-agent", user_agent).get();
			if(response.getCookies().get("JSESSIONID") != null){
				cookie = response.getCookies().get("JSESSIONID");
			}
			
			//read ProjectList to get ProjectList Object
			ProjectList projectlist = response.readEntity(new GenericType<ProjectList>() {});
			
			//get Projects as String List
			List<String> projects = projectlist.getProjects();

			//return ProjectList
			return projects;
		}
		catch(Exception e) {
			//no connection to server or any other Exception return null
			e.printStackTrace();
			return null;
		}
		
	}
	
	/**
	 * Get Project from Server
	 * @param projectname
	 * @return HTTP Code or 0 if Exception occurs (possibly could not connect to server)
	 */
	public int getProject(String projectname) {
		
		return 0;
	
	}
	
	/**
	 * Send the complete Project
	 * @param projectname
	 * @return
	 */
	public boolean sendProject(String projectname) {	
		
		return true;
	}
	
	/**
	 * Clear the loadedProject
	 */
	public void clearloadedProject() {
		loadedProject = null;
	}
	
	/**
	 * Load Project in normal Mode
	 * 
	 * @return only true, if there is a received Project
	 */
	public boolean loadProject() {
		
		//Load Project
		return true;
	}
	
	
	/**
	 * Load Project in Live Mode
	 * @return
	 */
	public boolean loadProjectLive() {
		
		System.out.println("Project loaded in Live Mode");
		
		//Listener muss auf Einzelaktionen hören und an Webserver senden
		
		return true;
	}
	
	
	/**
	 * Exit Live Mode
	 * @return true if successful otherwise false
	 */
	public boolean exitLiveMode() {
		
		//To Do: Online Event Listener von allen relevanten Topics entfernen
		return true;
	}
	

}
