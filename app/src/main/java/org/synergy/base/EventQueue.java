/*
 * synergy -- mouse and keyboard sharing utility
 * Copyright (C) 2010 Shaun Patterson
 * Copyright (C) 2010 The Synergy Project
 * Copyright (C) 2009 The Synergy+ Project
 * Copyright (C) 2002 Chris Schoeneman
 * 
 * This package is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * found in the file COPYING that should have accompanied this file.
 * 
 * This package is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.synergy.base;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.synergy.base.exceptions.InvalidMessageException;

public class EventQueue implements EventQueueInterface {

    // buffer of events
    EventQueueBuffer buffer;

    // saved events
    private Map <Integer, Event> events;
    private LinkedList <Integer> oldEventIDs;

    // event handlers
    private Map <Object, Map <EventType, EventJobInterface>> handlers;
	
	
	private static Object sync = new Object ();
	private static EventQueue eventQueue;
	public static EventQueue getInstance () {
		synchronized (sync) {
			if (eventQueue == null) {
				eventQueue = new EventQueue ();
			}
			return eventQueue;
		}
	}
	private EventQueue () {
        this.buffer = new SimpleEventQueueBuffer ();
        
        new HashMap <Integer, String> ();
        this.events = new HashMap <Integer, Event> ();
        this.oldEventIDs = new LinkedList<Integer>();
        
        this.handlers = new HashMap <Object, Map <EventType, EventJobInterface>> ();
    }

    private static void interrupt () {
    	// TODO: Todo?
    }
    
    public synchronized void adoptBuffer (EventQueueBuffer eventQueueBuffer) {
        // discard old buffer and old events
        events.clear ();
        oldEventIDs.clear ();

        // use new buffer
        buffer = eventQueueBuffer;
        if (buffer == null) {
            buffer = new SimpleEventQueueBuffer ();
        }
	}
	
    /**
     * Get an event from the event queue
     * 
     * TODO: The SimpleEventQueueBuffer has not been tested... and is rarely used
     *  in the client
     * 
     * @event Event to get
     * @timeout milliseconds to wait, < 0.0 is infinite
     */
	public Event getEvent (final Event event, final double timeout) 
        throws InvalidMessageException 
    {
		EventData eventData = null; 
		
		try {
			if (timeout < 0.0) {
				// Infinite timeout, retry forever
				eventData = buffer.getEvent ();
			} else {
				eventData = buffer.getEvent (timeout);
			}
		} catch (InterruptedException e) {
			e.printStackTrace ();
			return null;
		}
		
        switch (eventData.getType ()) {
        case NONE:
        	if (timeout < 0.0) { 
        		// Client is not expecting a NONE type.  
        		// Just try again
        		return getEvent (event, timeout);
        	} else {
        		return null;
        	}
        case SYSTEM:
            return eventData.getEvent ();
        case USER:
            return removeEvent (eventData.getDataID ());
        default:
            throw new InvalidMessageException ("Invalid message type: " + eventData.getType ());
        }
	}
    
	/**
	 * Dispatch an event
	 */
    public boolean dispatchEvent (final Event event) {
    	Log.note (event.toString ());
    	
        Object target = event.getTarget ();
        
        EventJobInterface job = getHandler (event.getType (), target);
        if (job == null) {
            Log.debug ("job is null for Event: " + event);
            job = getHandler (EventType.UNKNOWN, target);
        } else {
        	Log.debug ("running job");
            job.run (event);
        }

    	return false;
    }

    /**
     * Add an event to the queue
     */
    public void addEvent (final Event event) {
    	Log.debug5 ("addEvent");
    	
        // discard bogus Integers
		switch (event.getType ()) {
		case UNKNOWN:
		case SYSTEM:
		case TIMER:
			Log.debug ("Bogus event discarded");
		    return;
		default:
		    break;
		}

        if (event.getFlags ().equals (Event.Flags.DELIVER_IMMEDIATELY) == true) {
            dispatchEvent (event);

            // TODO: Questionable
            Event.deleteData (event);
        } else {
	        // store the event's data locally
	        Integer eventID = saveEvent (event);
	
	        // add it
	        try {
	        	buffer.addEvent (eventID);
	        } catch (InterruptedException e) {
	            // failed to send event
	            // TODO Log error?
	            removeEvent (eventID);
	            // TODO Questionable
	            Event.deleteData (event);
	        }
        }
    }

    /**
     * Link an event to an EventJobInterface
     */
    public synchronized void adoptHandler (EventType type, Object target, EventJobInterface handler) {
        // set/replace current handler
    	Map <EventType, EventJobInterface> handlerMap = handlers.get (target);
    	
    	if (handlerMap == null) {
    		// First handler for this event type
    		handlerMap = new HashMap <EventType, EventJobInterface> ();
    		handlers.put (target, handlerMap);
    	}
        handlers.get (target).put (type, handler);

    }

    /**
     * Remove a handler for a particular event
     */
    public synchronized void removeHandler (EventType type, Object target) {
        if (handlers.containsKey (target)) {
            handlers.get (target).remove (type);
        }
    }

    /**
     * Remove all handlers for a target
     */
    public void removeHandlers (Object target) {
        handlers.remove (target);
    }


    
    public EventJobInterface getHandler (EventType type, Object target) {
        if (handlers.containsKey (target)) {
        	if (handlers.get(target).containsKey (type)) {
        		return handlers.get (target).get (type);
        	} else {
        		return null;
        	}
        } else {
            return null;
        }
    }
    
    
    private synchronized Integer saveEvent (final Event event) {
    	Log.debug ("Old EventIDs Size: " + oldEventIDs.size ());
    	
        // choose id
        Integer id;
        if (oldEventIDs.isEmpty () == false) {
            // reuse an old id
            id = oldEventIDs.remove (oldEventIDs.size () - 1);
        } else {
            id = Integer.valueOf (events.size ());
        }

        // save data
        Log.debug ("Saving event data: " + id);
        events.put (id, event);

        return id;
    }

    private synchronized Event removeEvent (Integer eventID) {
        Event removedEvent = events.remove (eventID);

        if (removedEvent == null) {
            return new Event ();
        } else {
            // push the old id for reuse
            oldEventIDs.addLast (eventID);

            return removedEvent;
        }
    }

    public boolean isEmpty () {
        return (buffer.isEmpty () && getNextTimerTimeout () != 0.0);
    }
	
    private double getNextTimerTimeout () {
        return 0.0;
    }
}
