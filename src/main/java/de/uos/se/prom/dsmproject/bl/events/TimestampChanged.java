package de.uos.se.prom.dsmproject.bl.events;

import de.uos.se.prom.dsmproject.bl.event.Event;

public class TimestampChanged extends Event {
	
	public final static String TOPIC = "TimestampChanged";
	
	//Timestamp
	private String timestamp = "";
	
	public TimestampChanged(String timestamp) {
		this.timestamp = timestamp;
	}
	
	public String getTimestamp() {
		return this.timestamp;
	}
	
	@Override
	public String getTopic() {
		return TOPIC;
	}

}
