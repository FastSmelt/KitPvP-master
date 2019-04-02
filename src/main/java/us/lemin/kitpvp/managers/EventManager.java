package us.lemin.kitpvp.managers;

import us.lemin.kitpvp.KitPvPPlugin;
import us.lemin.kitpvp.events.Event;
import us.lemin.kitpvp.events.EventType;
import us.lemin.kitpvp.events.impl.BracketsEvent;
import us.lemin.kitpvp.events.impl.SumoEvent;

import java.util.EnumMap;
import java.util.Map;

public class EventManager {
    private final Map<EventType, Event> availableEvents = new EnumMap<>(EventType.class);

    public EventManager(KitPvPPlugin plugin) {
        availableEvents.put(EventType.SUMO, new SumoEvent(plugin));
        availableEvents.put(EventType.BRACKETS, new BracketsEvent(plugin));
    }

    public boolean isEventActive(EventType type) {
        return getEventByType(type).isActive();
    }

    public Event getEventByType(EventType type) {
        return availableEvents.get(type);
    }

    public Map<EventType, Event> getAvailableEvents() {
        return availableEvents;
    }
}
