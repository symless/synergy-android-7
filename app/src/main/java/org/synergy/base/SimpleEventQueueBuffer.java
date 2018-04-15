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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.*;
import java.util.concurrent.TimeUnit;

public class SimpleEventQueueBuffer implements EventQueueBuffer {
	private BlockingQueue <Integer> queue;
	
	public SimpleEventQueueBuffer () {
		// TODO: NOTE: This WAS a LinkedBlockingDeque but Android does not support that
		// 
		// Need to reevaluate the workings there and make sure everything is going to work
		queue = new LinkedBlockingQueue <Integer> ();
	}
	
    public EventData getEvent () throws InterruptedException {
    	Integer dataID = queue.take ();
        return new EventData (EventData.Type.USER, null, dataID);
    }
    
    public EventData getEvent (double timeout) throws InterruptedException {
    	Integer dataID = queue.poll ((long)(timeout * 1000.0), TimeUnit.MILLISECONDS);
        return new EventData (EventData.Type.USER, null, dataID);
    }

	public void addEvent (Integer dataID) throws InterruptedException {
		queue.put (dataID);
	}

	public boolean isEmpty () {
		return queue.isEmpty ();
	}
}
