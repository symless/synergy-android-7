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

import java.util.Timer;
import java.util.TimerTask;

public class EventQueueTimer { 

	// Do it once?
	boolean oneShot;
	
	// The target for the event
	Object target;
	
	// What will run when the timer is fired from the Event Queue
	EventJobInterface job;
	
	Timer timer;
		
	public EventQueueTimer (double timeout, boolean oneShot, Object target, EventJobInterface job) {
		if (job == null || target == null) {
			throw new IllegalArgumentException ("Target and job must not be null");
		}
		
		this.oneShot = oneShot;
		this.target = target;
		this.job = job;
		
		this.timer = new Timer ();
		timer.schedule (new TimerEventTask (), (long)(timeout * 1000.0));
	}

    /**
      * Cancel a running timer
      */
    public void cancel () {
        if (timer != null) {
            timer.cancel ();
        }
    }
	
	/**
	 * This is the actual task the timer will perform. 
	 *  This task will take the EventJob and create a new
	 *  timer event and put it onto the EventQueue.  The Event Queue will
	 *  actually handle the dispatching of the job
	 *  
	 *  Hmm. Scratch that?  Just run the damn thing?
	 *  
	 */
	private class TimerEventTask extends TimerTask {
		public void run () {
			job.run (new Event (EventType.TIMER, target));
			/*Log.debug ("Timer fired");
			
			//EventQueue.getInstance ().adoptHandler (EventType.TIMER, target, job);
			EventQueue.getInstance ().addEvent (new Event (EventType.TIMER, target)); //, null, Event.Flags.DELIVER_IMMEDIATELY));
			*/
			
			if (!oneShot) {
				timer.cancel ();
			}
		}
	}
	
	
 /*   private EventQueueTimer timer;
    private double timeout;
    private double time;
    private Object target;
    private boolean oneShot;

    public EventQueueTimer (Timer timer, double timeout, double initialTime,
            Object target, boolean oneShot) {

        this.timer = timer;
        this.timeout = timeout;
        this.target = target;
        this.oneShot = oneShot;
        this.time = initialTime;

        // TODO assert (timeout > 0.0)
    }

    public void reset () {
        time = timeout;
    }

    public boolean isOneShot () {
        return oneShot;
    }

    public void subtractTime (double dt) {
        time -= dt;
    }

    public double getTime () {
        return time;
    }


    public EventQueueTimer getTimer () {
        return timer;
    }

    public Object getTarget () {
        return target;
    }

    public void fillEvent (TimerEvent event) {
        event.setTimer (timer);
        event.setCount (0);

        if (time <= 0.0) {
            event.setCount (new Integer ((int)((timeout - time) / timeout)));
        }
    }
    
    public int compareTo (Object o) {
        /*if (!o.instanceof (Timer)) {
            return -1;
        }
        *
        EventQueueTimer rhs = (EventQueueTimer) o;

        if (time < rhs.getTime ()) {
            return -1;
        } else if (time == rhs.getTime ()) {
            return 0;
        } else {
            return 1;
        }
    } */

}
