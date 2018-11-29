package de.uos.se.prom.dsmproject.bl;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


import de.uos.se.prom.dsmproject.bl.event.Event;
import de.uos.se.prom.dsmproject.bl.event.EventBus;
import de.uos.se.prom.dsmproject.bl.event.EventListener;
import de.uos.se.prom.dsmproject.bl.events.ArtifactAdded;
import de.uos.se.prom.dsmproject.bl.events.ArtifactDeleted;
import de.uos.se.prom.dsmproject.bl.events.ArtifactEdited;
import de.uos.se.prom.dsmproject.bl.events.DependencyAdded;
import de.uos.se.prom.dsmproject.bl.events.DependencyDeleted;
import de.uos.se.prom.dsmproject.bl.events.LiveModeExited;
import de.uos.se.prom.dsmproject.bl.events.LiveModeStarted;
import de.uos.se.prom.dsmproject.bl.events.TimestampChanged;
import de.uos.se.prom.dsmproject.da.entity.PersistedArtifact;
import de.uos.se.prom.dsmproject.da.entity.PersistedDependency;
import de.uos.se.prom.dsmproject.da.entity.PersistedTypesFactory;
import de.uos.se.prom.dsmproject.da.onlineEntity.PersistedArtifactDeleted;
import de.uos.se.prom.dsmproject.da.onlineEntity.PersistedArtifactEdited;
import de.uos.se.prom.dsmproject.da.onlineEntity.PersistedDependencyDeleted;
import de.uos.se.prom.dsmproject.entity.Artifact;
import de.uos.se.prom.dsmproject.entity.Dependency;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class OnlineEventListener implements EventListener<Event>{
	
	@Inject
	EventBus eventBus;

	//URIs for HTTP Requests
	private String http_xml;
	private String http_artifact;
	private String http_dependency;
	private String http_add;
	private String http_delete;
	private String http_edit;
	
	//HTTP Client
	private Client client = ClientBuilder.newClient();
	
	//Projectname needed for HTTP Requests
	String projectname = "";
	
	//Indicates whether Live Mode is started to prevent that LiveMode can started multiple times
	boolean liveModeStarted = false;
	
	@PostConstruct
	public void initialize() {
		
		//Listen on LiveModeStarted Event
		eventBus.addListener(LiveModeStarted.TOPIC, (event) ->{
			startLiveMode();
        	this.projectname = ((LiveModeStarted) event).getProjectFilename();
        	
        });
		
		//Listen on LiveModeExited Event
		eventBus.addListener(LiveModeExited.TOPIC, (event) ->{
			exitLiveMode("Leaved Live Mode!");
		});

        
		//Get URIs from ServerController Reason is only for better legibility
		this.http_xml = ServerController.HTTP_XML;
		this.http_artifact = ServerController.HTTP_ARTIFACT;
		this.http_dependency = ServerController.HTTP_DEPENDENCY;
		this.http_add = ServerController.HTTP_ADD;
		this.http_delete = ServerController.HTTP_DELETE;
		this.http_edit = ServerController.HTTP_EDIT;
		
	}
	
	@Override
	public void eventOccured(Event event) {
		if(event.getClass() == ArtifactAdded.class) {
			onArtifactAdded((ArtifactAdded) event);	
			System.out.println("Art added");	
		}
		
		if(event.getClass() == ArtifactEdited.class) {
			onArtifactEdited((ArtifactEdited) event);	
			System.out.println("Art edited");	
		}
		
		if(event.getClass() == ArtifactDeleted.class) {
			onArtifactDeleted((ArtifactDeleted) event);	
			System.out.println("Art deleted");	
		}
		
		if(event.getClass() == DependencyAdded.class) {
			onDependencyAdded((DependencyAdded) event);	
			System.out.println("Dep added");	
		}
		
		if(event.getClass() == DependencyDeleted.class) {
			onDependencyDeleted((DependencyDeleted) event);	
			System.out.println("Dep deleted");	
		}
	}
	

	/**
	 * Method that is executed when new Artifact is added
	 * @param event
	 */
	private void onArtifactAdded(ArtifactAdded event){
		Artifact newArtifact = event.getArtifact();
		System.out.println("Artifact: " + newArtifact.getName());
		
		//Create Persisted Artifact 
		PersistedArtifact pArtifact = new PersistedTypesFactory().create(newArtifact);
    	
		//Create WebTarget
    	WebTarget target = client.target(ServerController.buildServerString()).path(http_xml + "/" + projectname + http_artifact + http_add);
    	//WebTarget target = client.target(ServerController.buildServerString()).path(http_xml + "/" + projectname);

    	//Make Request
    	Response response = target.request(MediaType.APPLICATION_XML).cookie(ServerController.COOKIE).post(Entity.entity(pArtifact, MediaType.APPLICATION_XML));
    	if(response.getCookies().get("JSESSIONID") != null){
			ServerController.COOKIE = response.getCookies().get("JSESSIONID");
		}
    	
    	//If Response Status is not 200, probably Session is expired or File deleted on Server or anything else
    	if(response.getStatus() != 200) {
    		exitLiveMode("Probably your Session is expired or you are not allowed to be in Live Mode (or an other Error occured)! Your Last Changes are not send to Server! Try to send your Complete Project to Server!");
    	}
    	
    	//Update Timestamp
    	else {
    		String timestamp = response.readEntity(String.class);
    		if(!timestamp.isEmpty()) {
    			eventBus.fireEvent(new TimestampChanged(timestamp));
    		}
    	}
		
	}
	
	/**
	 * Method that is executed when an Artifact is changed
	 * @param event
	 */
	private void onArtifactEdited(ArtifactEdited event) {
		Artifact artifactBefore = event.getBefore();
		Artifact artifactAfter = event.getAfter();
		
		//Create Persisted Artifacts
		PersistedArtifact pArtifactBefore = new PersistedTypesFactory().create(artifactBefore);
		PersistedArtifact pArtifactAfter = new PersistedTypesFactory().create(artifactAfter);
		
		System.out.println(pArtifactBefore.name);
		System.out.println(pArtifactAfter.name);
		
		
		//Create PersistedArtifactEdited
		PersistedArtifactEdited pArtifactEdited = new PersistedArtifactEdited(pArtifactBefore, pArtifactAfter);
		
		//Create WebTarget
    	WebTarget target = client.target(ServerController.buildServerString()).path(http_xml + "/" + projectname + http_artifact + http_edit);
		
    	//Make Request
    	Response response = target.request(MediaType.APPLICATION_XML).cookie(ServerController.COOKIE).post(Entity.entity(pArtifactEdited, MediaType.APPLICATION_XML));
    	if(response.getCookies().get("JSESSIONID") != null){
			ServerController.COOKIE = response.getCookies().get("JSESSIONID");
		}
    	
    	//If Response Status is not 200, probably Session is expired or File deleted on Server or anything else
    	if(response.getStatus() != 200) {
    		exitLiveMode("Probably your Session is expired or you are not allowed to be in Live Mode (or an other Error occured)! Your Last Changes are not send to Server! Try to send your Complete Project to Server!");
    	}
    	
    	//Update Timestamp
    	else {
    		String timestamp = response.readEntity(String.class);
    		if(!timestamp.isEmpty()) {
    			eventBus.fireEvent(new TimestampChanged(timestamp));
    		}
    	}
		
		
	}
	
	
	/**
	 * Method that is executed when an Artifact is deleted
	 * @param event
	 */
	private void onArtifactDeleted(ArtifactDeleted event) {
		Artifact artifactDeleted = event.getArtifact();
		
		//Create Persisted Artifact
		PersistedArtifact pArtifact = new PersistedTypesFactory().create(artifactDeleted);
		
		//Create PersistedArtifactDeleted
		PersistedArtifactDeleted pArtifactDeleted = new PersistedArtifactDeleted(pArtifact);
		
		//Create WebTarget
    	WebTarget target = client.target(ServerController.buildServerString()).path(http_xml + "/" + projectname + http_artifact + http_delete);
		
    	//Make Request
    	Response response = target.request(MediaType.APPLICATION_XML).cookie(ServerController.COOKIE).post(Entity.entity(pArtifactDeleted, MediaType.APPLICATION_XML));
    	if(response.getCookies().get("JSESSIONID") != null){
			ServerController.COOKIE = response.getCookies().get("JSESSIONID");
		}
    	
    	//If Response Status is not 200, probably Session is expired or File deleted on Server or anything else
    	if(response.getStatus() != 200) {
    		exitLiveMode("Probably your Session is expired or you are not allowed to be in Live Mode (or an other Error occured)! Your Last Changes are not send to Server! Try to send your Complete Project to Server!");
    	}
    	
    	//Update Timestamp
    	else {
    		String timestamp = response.readEntity(String.class);
    		if(!timestamp.isEmpty()) {
    			eventBus.fireEvent(new TimestampChanged(timestamp));
    		}
    	}
		
	}
	
	/**
	 * Method that is executed when an Dependency is added
	 * @param event
	 */
	private void onDependencyAdded(DependencyAdded event) {
		Dependency dependencyAdded = event.getDependency();
		
		//Create Persisted Dependency
		PersistedDependency pDependency = new PersistedTypesFactory().create(dependencyAdded);
		
		//Create WebTarget
    	WebTarget target = client.target(ServerController.buildServerString()).path(http_xml + "/" + projectname + http_dependency + http_add);
		
    	//Make Request
    	Response response = target.request(MediaType.APPLICATION_XML).cookie(ServerController.COOKIE).post(Entity.entity(pDependency, MediaType.APPLICATION_XML));
    	if(response.getCookies().get("JSESSIONID") != null){
			ServerController.COOKIE = response.getCookies().get("JSESSIONID");
		}
    	
    	//If Response Status is not 200, probably Session is expired or File deleted on Server or anything else
    	if(response.getStatus() != 200) {
    		exitLiveMode("Probably your Session is expired or you are not allowed to be in Live Mode (or an other Error occured)! Your Last Changes are not send to Server! Try to send your Complete Project to Server!");
    	}
    	
    	//Update Timestamp
    	else {
    		String timestamp = response.readEntity(String.class);
    		if(!timestamp.isEmpty()) {
    			eventBus.fireEvent(new TimestampChanged(timestamp));
    		}
    	}
	}
	
	
	/**
	 * Method that is executed when an Dependency is deleted
	 * @param event
	 */
	private void onDependencyDeleted(DependencyDeleted event) {
		
		Dependency dependencyDeleted = event.getDependency();
		
		//Create Persisted Dependency
		PersistedDependency pDependency = new PersistedTypesFactory().create(dependencyDeleted);
		
		//Create PersistedDependencyDeleted
		PersistedDependencyDeleted pDependencyDeleted = new PersistedDependencyDeleted(pDependency);
		
		//Create WebTarget
    	WebTarget target = client.target(ServerController.buildServerString()).path(http_xml + "/" + projectname + http_dependency + http_delete);
		
    	//Make Request
    	Response response = target.request(MediaType.APPLICATION_XML).cookie(ServerController.COOKIE).post(Entity.entity(pDependencyDeleted, MediaType.APPLICATION_XML));
    	if(response.getCookies().get("JSESSIONID") != null){
			ServerController.COOKIE = response.getCookies().get("JSESSIONID");
		}
    	
    	//If Response Status is not 200, probably Session is expired or File deleted on Server or anything else
    	if(response.getStatus() != 200) {
    		exitLiveMode("Probably your Session is expired or you are not allowed to be in Live Mode (or an other Error occured)! Your Last Changes are not send to Server! Try to send your Complete Project to Server!");
    	}
    	
    	//Update Timestamp
    	else {
    		String timestamp = response.readEntity(String.class);
    		if(!timestamp.isEmpty()) {
    			eventBus.fireEvent(new TimestampChanged(timestamp));
    		}
    	}
	}
	
	/**
	 * Method is executed when Live Mode is exited
	 * @param message
	 */
	private void exitLiveMode(String message) {
		
		if(!message.isEmpty()) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Exiting Live Mode");
			alert.setHeaderText("");
			alert.setContentText(message);
			alert.showAndWait();
		}
		
		//Remove Listener from all Topics
		eventBus.removeListener(ArtifactAdded.TOPIC, this);
		eventBus.removeListener(ArtifactEdited.TOPIC, this);
		eventBus.removeListener(ArtifactDeleted.TOPIC, this);
		eventBus.removeListener(DependencyAdded.TOPIC, this);
		eventBus.removeListener(DependencyDeleted.TOPIC, this);
		
		//Set LiveModeStarted to false
		liveModeStarted = false;
		
		//Reset Projectname
		this.projectname = "";
			
	}
	
	/**
	 * Method is executed when Live Mode is started
	 */
	private void startLiveMode() {
		/*
		 * Without this check, Live Mode can started multiple times and every time Listeners are added.
		 * This causes in multiple HTTP Requests for only one Change and as a result the Project on Webserver
		 * has for example 3 times the same artifact in XML File	
		 */
		if(!liveModeStarted) {
			liveModeStarted = true;
			//Add Listener for Events
			eventBus.addListener(ArtifactAdded.TOPIC, this);
			eventBus.addListener(ArtifactEdited.TOPIC, this);
			eventBus.addListener(ArtifactDeleted.TOPIC, this);
			eventBus.addListener(DependencyAdded.TOPIC, this);
			eventBus.addListener(DependencyDeleted.TOPIC, this);
		}
		
	}
}
