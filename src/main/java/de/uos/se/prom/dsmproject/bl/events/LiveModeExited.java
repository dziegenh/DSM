package de.uos.se.prom.dsmproject.bl.events;

import de.uos.se.prom.dsmproject.bl.event.Event;

/**
 * This Event is executed when Live Mode is exited
 * @author Markus Mohr
 *
 */

public class LiveModeExited extends Event {
	
	public final static String TOPIC = "LiveModeExited";
	
	
	@Override
	public String getTopic() {
		return TOPIC;
	}

}
