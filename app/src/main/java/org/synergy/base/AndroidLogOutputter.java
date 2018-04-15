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

public class AndroidLogOutputter implements LogOutputterInterface {

    public AndroidLogOutputter () {
    }

    public void open (final String title) {
        // Do nothing
    }

    public void close () {
        // Do nothing
    }

    public void show (final boolean showIfEmpty) {
        // Do nothing
    }

    public boolean write (Log.Level level, final String tag, final String message) {
    	switch (level) {
    	case PRINT:
    		android.util.Log.v (tag, message);
    		break;
    	case FATAL:
    	case ERROR:
    		android.util.Log.e (tag, message);
    		break;
    	case WARNING:
    		android.util.Log.w (tag, message);
    		break;
    	case NOTE:
    	case INFO:
    		android.util.Log.i (tag, message);
    		break;
    	case DEBUG:
    	case DEBUG1:
    	case DEBUG2:
    	case DEBUG3:
    	case DEBUG4:
    	case DEBUG5:
    		android.util.Log.d (tag, message);
    		break;
    	}
    	
    	return true; // wtf
    }
	
    public void flush () {
        System.out.flush ();
    }
	
}
