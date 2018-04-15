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

/**
 * @author Shaun Patterson
 */
public class Event {

    public enum Flags {
        NONE (0x00),
        DELIVER_IMMEDIATELY (0x01),
        DONTFREEDATA (0x02);

        private int value;
        Flags (int value) {
            this.value = value;
        }
        
        public int getValue () {
        	return value;
        }

    }
    
    private EventType type;
    private Object target;
    private Object data;
    private Event.Flags flags;
    private String name = "";
    
    public Event () {
        this (EventType.UNKNOWN, null, null, Flags.NONE);
    }
    
    public Event (EventType type) {
    	this (type, null, null, Flags.NONE);
    }
    
    public Event (EventType type, Object target) {
    	this (type, target, null, Flags.NONE);
    }

    public Event (EventType type, Object target, Object data) {
    	this (type, target, data, Flags.NONE);
    }
    
    public Event (EventType type, Object target, Object data, Flags flags) {
    	this.type = type;
    	this.target = target;
    	this.data = data;
    	this.flags = flags;
    }
    
    public static void deleteData (final Event event) {
        // TODO
    }

	public EventType getType() {
		return type;
	}
	
	public Object getTarget() {
		return target;
	}

	public Object getData() {
		return data;
	}

	public Event.Flags getFlags() {
		return flags;
	}
	
	public String getName () {
		return name;
	}
	
	public String toString () {
		return "Event:" + type.toString () + ":" + target;
	}
}
