package de.uos.se.prom.dsmproject.bl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.*;


import java.util.List;
import java.util.logging.Level;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import com.airhacks.afterburner.injection.Injector;
import com.sun.istack.internal.logging.Logger;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.GenericType;

import de.uos.se.prom.dsmproject.bl.event.EventBus;
import de.uos.se.prom.dsmproject.bl.events.ArtifactAdded;
import de.uos.se.prom.dsmproject.bl.events.LiveModeExited;
import de.uos.se.prom.dsmproject.bl.events.LiveModeStarted;
import de.uos.se.prom.dsmproject.bl.events.ProjectCreated;
import de.uos.se.prom.dsmproject.bl.events.ProjectLoaded;
import de.uos.se.prom.dsmproject.bl.events.TimestampChanged;
import de.uos.se.prom.dsmproject.da.ProjectAccess;
import de.uos.se.prom.dsmproject.da.entity.PersistedProject;
import de.uos.se.prom.dsmproject.da.entity.PersistedTypesFactory;
import de.uos.se.prom.dsmproject.da.onlineEntity.ProjectList;
import de.uos.se.prom.dsmproject.entity.Project;

/**
 * 
 * @author Markus Mohr
 *
 */
public class ServerController {
	
	@Inject
	private ProjectEditor projectEditor;
	
	@Inject
	private EventBus eventBus;
	
	@Inject
	private ProjectAccess projectAccess;
	
	//Injection is needed because here the OnlineEventListener is instantiated
	@Inject
	private OnlineEventListener onlineEventListener;
	
	//URLs from Webserver
	public static String HTTP_HOST = "localhost";
	public static String HTTP_PORT = "8080";
	public static String HTTP_SERVLET_CONTEXT = "/dsm_webserver";
	//public static String HTTP_SERVER = "http://" + HTTP_HOST + ":8080/dsm_webserver";
	public static String HTTP_XML = "/xml";
	public static String HTTP_ARTIFACT = "/artifact";
	public static String HTTP_DEPENDENCY = "/dependency";
	public static String HTTP_ADD = "/add";
	public static String HTTP_DELETE = "/delete";
	public static String HTTP_EDIT = "/edit";
	
	//Currently loaded Project as File
	private File loadedProjectFile;
	
	//Currently loaded Project as Project class
	private Project loadedProject;
	
	//Filename of Currently loaded Project from Server
	private String loadedProjectFilename = "";
	
	//HTTP Client
	private Client client = ClientBuilder.newClient();
	
	//Cookie for Session Management has to be public because OnlineEventListener must have Access to Cookie
	public static Cookie COOKIE;
	
	//User Agent for HTTP Request Header
	private String user_agent = "DSMTool";
	
	//Server Timestamp - Default Value is 0
	private String serverTimestamp = "0";
	
	@PostConstruct
	public void initialize() {
		//Listener because when New Project Created loadedProject etc must be reseted
		eventBus.addListener(ProjectCreated.TOPIC, (event) ->{
        	resetServerController();
        	//System.out.println("serverController reseted");
        });
		
		//Add Listener for changed Timestamps in Live Mode
		eventBus.addListener(TimestampChanged.TOPIC, (event) -> {
			this.serverTimestamp = ((TimestampChanged) event).getTimestamp();
			System.out.println("New TS: " + this.serverTimestamp);
		});
		
	}
	
	/**
	 * Set the HTTP Host
	 * @param host
	 */
	public static void setHost(String host) {
		HTTP_HOST = host;
	}
	
	/**
	 * Set the HTTP Port
	 * @param port
	 */
	public static void setPort(String port) {
		HTTP_PORT = port;
	}
	
	/**
	 * Test Connection
	 * @return True if Connection can be established.
	 */
	public boolean testConnection() {
		//Create Web Target
		WebTarget target = client.target(buildServerString());
		
		try {
			//Try to Connect
			Response response = target.request().header("user-agent", user_agent).get();
			//Return True only if Response Code is HTTP 200
			if(response.getStatus() == 200) return true;
			
		}catch(Exception e) {
			//Exception occured return false
			return false;
		}
		return false;
	}
	
	/**
	 * Build Server String with Servlet Context
	 * @return Server String
	 */
	//public because OnlineEventListener needs Access too
	public static String buildServerString() {
		return ("http://" + HTTP_HOST + ":" + HTTP_PORT + HTTP_SERVLET_CONTEXT);
	}
	
	/**
	 * Get Project List from Server
	 * @return Projectlist
	 */
	public List<String> getProjectList() {
		WebTarget target = client.target(buildServerString()).path(HTTP_XML);
		
		try {
			
			//Do Request and Set Cookie for Session Management
			Response response = target.request(MediaType.APPLICATION_XML).cookie(COOKIE).header("user-agent", user_agent).get();
			if(response.getCookies().get("JSESSIONID") != null){
				COOKIE = response.getCookies().get("JSESSIONID");
			}
			
			//read ProjectList to get ProjectList Object
			//ProjectList projectlist = response.readEntity(new GenericType<ProjectList>() {});
			ProjectList projectlist = response.readEntity(ProjectList.class);

			
			//get Projects as String List
			List<String> projects = projectlist.getProjects();
			
			return projects;
		}
		catch(Exception e) {
			//no connection to server or any other Exception return null
			return null;
		}
		
	}
	
	/**
	 * Get Project from Server
	 * @param projectname
	 * @return HTTP Code or 0 if Exception occurs (possibly could not connect to server)
	 */
	public int getProject(String projectname, boolean live) {
		
		if(projectname.isEmpty()) {
			return 0;
		}
		
		//create WebTarget
		WebTarget target = client.target(buildServerString()).path(HTTP_XML + "/" + projectname);
		
		try {
			
			//Do Request and Set Cookie for Session Management
			Response response = target.request(MediaType.APPLICATION_XML).cookie(COOKIE).header("user-agent", user_agent).get();
			if(response.getCookies().get("JSESSIONID") != null){
				COOKIE = response.getCookies().get("JSESSIONID");
			}
			
			//System.out.println(response.getStringHeaders());
			//HTTP Status of Response
			int resp_status = response.getStatus();
			
			//if succesfully received
			if(resp_status == 200) {
				
				//Set Filename of Project
				this.loadedProjectFilename = projectname;
				
				//Read Entity as File
				loadedProjectFile = response.readEntity(File.class);
				
				//Read as Project 
				loadedProject = projectAccess.loadProject(loadedProjectFile);
				
				//Get serverTimestamp
				this.serverTimestamp = projectAccess.getProjectTimestamp(loadedProjectFile);
				System.out.println("Timestamp: " + this.serverTimestamp);
				
				//Load Project in NormalMode or LiveMode
				if(live) loadProjectLive();
				
				else loadProject();
			}
			
			//Return HTTP Code
			return resp_status;
			
		}catch(Exception e) {		
			//if no connection to server or any other Exception return 0
			e.printStackTrace();
			return 0;
		}
	
	}
	
	/**
	 * Get the Status (Read/Write Access) of the Project
	 * @param projectname
	 * @return Read/Write Status of Project. If there was a failure return empty String
	 */
	public String getProjectStatus(String projectname) {
		//create WebTarget
		WebTarget target = client.target(buildServerString()).path(HTTP_XML + "/" + projectname + "/status");
		
		try {
			//Do Request and Set Cookie for Session Management
			Response response = target.request().cookie(COOKIE).header("user-agent", user_agent).get();
			if(response.getCookies().get("JSESSIONID") != null){
				COOKIE = response.getCookies().get("JSESSIONID");
			}
			
			//Read Response as String
			return response.readEntity(String.class);
			
		}catch(Exception e) {
			e.printStackTrace();
			return "";
		}
		
		
	}
	
	/**
	 * Send the complete Project
	 * @param projectname
	 * @return
	 */
	public int sendProject(String projectname) {
	
		WebTarget target = client.target(buildServerString()).path(HTTP_XML + "/" + projectname);
		
		/** Send as PersistedProject
		try {
			
			//get current Project
			Project project = projectEditor.getCurrentProject();
			
			//create PersistedProject
			PersistedProject pProject = new PersistedTypesFactory().create(project);
			
			//make request
			Response response = target.request(MediaType.APPLICATION_XML).cookie(cookie).header("user-agent", user_agent).put(Entity.entity(pProject, MediaType.APPLICATION_XML));
			if(response.getCookies().get("JSESSIONID") != null){
				cookie = response.getCookies().get("JSESSIONID");
			}
			
			//HTTP Status of Response
			int resp_status = response.getStatus();
			
			return resp_status;
			
		}catch(Exception e) {
			e.printStackTrace();
			return 0;
		}
		
		*/
		
		try {
			//create temporary File
			File tmp = File.createTempFile("DSMProject", "tmp");
			tmp.deleteOnExit();
			
			//get current Project from Project Editor
			Project project = projectEditor.currentProject;
			
			//Save Project with Timestamp in temporary File
			projectAccess.saveProject(project, tmp, serverTimestamp);
			
			//Make Request
			Response response = target.request(MediaType.APPLICATION_XML).cookie(COOKIE).header("user-agent",user_agent).put(Entity.entity(tmp, MediaType.APPLICATION_XML));
			if(response.getStatus() != 200) {
				return response.getStatus();
			}
			
			if(response.getCookies().get("JSESSIONID") != null){
				COOKIE = response.getCookies().get("JSESSIONID");
			}
			
			//Set new Timestamp read it from Response Body
			String newTimestamp = response.readEntity(String.class);
			if(!newTimestamp.isEmpty()) {
				this.serverTimestamp = newTimestamp;
				System.out.println("serverTimestamp new: " + serverTimestamp);
			}
			
			//HTTP Status of Response
			int resp_status = response.getStatus();
			return resp_status;
			
		} catch (Exception e) {
			// something wrong with file access
			e.printStackTrace();
			return 0;
		} 
		


	}
	
	/**
	 * Reset the ServerController
	 */
	public void resetServerController() {
		loadedProject = null;
		loadedProjectFile = null;
		loadedProjectFilename = "";
		serverTimestamp = "0";
		
	}
	
	/**
	 * Load Project in normal Mode
	 * 
	 * @return only true, if there is a received Project
	 */
	public boolean loadProject() {
		
		if (loadedProject == null) {
			return false;
		}
		else {
			
			//projectController.loadProject(loadedProject);
			
			/*
			 * Fire own Event because with ProjectController the Option hasProjectFileProperty is set 
			 * and this is not true when receiving Project from Server. The given File is only a temporarily File.
			 * Other Option could be to receive only the <PersistedProject>...</PersistedProject> but this tool is designed only for 
			 * load complete serialized Projects with <DsmProject>...</DsmProject>
			 */
			 eventBus.fireEvent(new ProjectLoaded(loadedProject, loadedProjectFile));
			return true;
		}	
	}
	
	
	/**
	 * Load Project in Live Mode
	 * @return
	 */
	public boolean loadProjectLive() {
		
		System.out.println("Project loaded in Live Mode");
		
		/*
		//Add Listener for changed Timestamps in Live Mode
		eventBus.addListener(TimestampChanged.TOPIC, (event) -> {
            this.serverTimestamp = ((TimestampChanged) event).getTimestamp();
        });
		*/
		
		//Fire Event Live Mode Started and load Project
		eventBus.fireEvent(new LiveModeStarted(this.loadedProjectFilename));
		loadProject();
		
		return true;
	}
	
	
	/**
	 * Exit Live Mode
	 * @return true if successful otherwise false
	 */
	public boolean exitLiveMode() {
		
		//Fire Event LiveModeExited
		eventBus.fireEvent(new LiveModeExited());
		
		return true;
	}
	
	/**
	 * Get the Filename of received and loaded Project
	 * @return
	 */
	public String getLoadedProjectFilename() {
		return loadedProjectFilename;
	}




		

}
