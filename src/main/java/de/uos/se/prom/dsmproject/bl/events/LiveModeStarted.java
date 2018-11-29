package de.uos.se.prom.dsmproject.bl.events;

import de.uos.se.prom.dsmproject.bl.event.Event;

public class LiveModeStarted extends Event {
	
	public final static String TOPIC = "LiveModeStarted";
	
	//Filename of Currently loaded Project from Server
	private String loadedProjectFilename = "";
	
	public LiveModeStarted(String projectFilename) {
		this.loadedProjectFilename = projectFilename;
	}
	
	/**
	 * Get Filename of Loaded Project for HTTP Requests
	 * @return
	 */
	public String getProjectFilename() {
		return this.loadedProjectFilename;
	}
	
	@Override
	public String getTopic() {
		return TOPIC;
	}
	
	
}
