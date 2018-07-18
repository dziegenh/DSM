package de.uos.se.prom.dsmproject.bl.event;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.logging.Logger;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

/**
 *
 * @author dziegenhagen
 */
public class EventBus {

    HashMap<String, List<EventListener>> listeners = new HashMap<>();

    LinkedList<Event> eventQueue = new LinkedList<>();

    private boolean isFiring = false;

    Logger logger = Logger.getLogger(EventBus.class);

    public void fireEvent(Event event) {
        this.eventQueue.add(event);

        if (!isFiring) {
            fire();
        }
    }

    private void fire() {
        isFiring = true;

        while (!eventQueue.isEmpty()) {

            Event event = eventQueue.pop();
            String topic = event.getTopic();

            logger.log(Level.INFO, "Firing event for topic: " + topic);

            if (listeners.containsKey(topic)) {

                List<EventListener> topicListeners = new LinkedList<>(this.listeners.get(topic));

                for (EventListener listener : topicListeners) {
                    listener.eventOccured(event);
                }
            }
        }

        isFiring = false;
    }

    public void addListener(@NotNull String topic, EventListener<? extends Event> listener) {
        if (this.listeners.containsKey(topic)) {
            this.listeners.get(topic).add(listener);
        } else {
            List<EventListener> list = new LinkedList<>();
            list.add(listener);
            this.listeners.put(topic, list);
        }
    }

    public void removeListener(String topic, EventListener<Event> eventListener) {
        logger.log(Level.INFO, "Remove Listener for " + topic);
        this.listeners.get(topic).remove(eventListener);
    }

}
