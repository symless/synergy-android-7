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

/*
 * @author Shaun Patterson
 * 
 * For lack of a better name...
 * 
 * This class holds an event, a data id, and the type of 
 *  event
 */
public class EventData {

	public enum Type {
		NONE,
		SYSTEM,
		USER
	}
	
	private Type type;
	private Event event;
	private Integer dataID;
	
	// None
	public EventData () {
		this.type = Type.NONE;
		this.dataID = -1;
		this.event = null;
	}
	
	public EventData (Type type, Event event, Integer dataID) {
		this.type = type;
		this.event = event;
		this.dataID = dataID;
	}

	public Type getType() {
		return type;
	}

	public Integer getDataID() {
		return dataID;
	}

	public Event getEvent() {
		return event;
	}
	
}
