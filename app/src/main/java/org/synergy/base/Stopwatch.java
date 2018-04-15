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
/*
 * 
 *
 * Copyright (c) 2005, Corey Goldberg

 * StopWatch.java is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
*/


package org.synergy.base;



public class Stopwatch {
	
	private double mark;
	private boolean triggered;
	private boolean stopped;
	
	public Stopwatch (boolean triggered) {
	    this.mark = 0.0;
        this.triggered = triggered;
        this.stopped = triggered;

        if (!this.triggered) {
            mark = getClock ();
        }

	}

    private double getClock () {
        return (double) System.currentTimeMillis () / 1000.0;
    }


    public double reset () {
        if (this.stopped) {
            final double dt = this.mark;
            this.mark = 0.0;
            return dt;
        } else {
            final double t = getClock ();
            final double dt = t - this.mark;
            this.mark = dt;
            return dt;
        }
    }

    public void stop () {
        if (this.stopped) {
            return;
        }

        // save the elapsed time
        this.mark = getClock () - this.mark;
        this.stopped = true;
    }

    public void start () {
        this.triggered = false;
        if (!this.stopped) {
            return;
        }

        // set the mark such that it reports the time elapsed at stop ()
        this.mark = getClock () - this.mark;
        this.stopped = false;
    }

    public void setTrigger () {
        stop ();
        this.triggered = true;
    }

    public double getTime () {
        if (this.triggered) {
            final double dt = this.mark;
            start ();
            return dt;
        } else if (this.stopped) {
            return this.mark;
        } else {
            return getClock () - this.mark;
        }
    }

    public double getTimeNoStart () {
        if (this.stopped) {
            return this.mark;
        } else {
            return getClock () - this.mark;
        }
    }


	public boolean isStopped () {
        return this.stopped;
    }
}
