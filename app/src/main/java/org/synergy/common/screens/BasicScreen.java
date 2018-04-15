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
package org.synergy.common.screens;

import java.util.Arrays;

import org.synergy.base.Log;
import org.synergy.injection.Injection;

import android.graphics.Point;
import android.graphics.Rect;

public class BasicScreen implements ScreenInterface {
	
	private final int[] buttonToKeyDownID;
	
	// Keep track of the mouse cursor since I cannot find a way of
	//  determining the current mouse position
	private int mouseX = -1;
	private int mouseY = -1;
	
	// Screen dimensions
	private int width;
	private int height;
	
    public BasicScreen () {
    	
    	// the keyUp/Down/Repeat button parameter appears to be the low-level
    	// keyboard scan code (*shouldn't be* more than 256 of these, but I speak
    	// from anecdotal experience, not as an expert...
    	buttonToKeyDownID = new int[256];
    	Arrays.fill(buttonToKeyDownID, -1);
    }

    /**
     * Set the shape of the screen -- set from the initializing activity
     * @param width
     * @param height
     */
    public void setShape (int width, int height) {
    	this.width = width;
    	this.height = height;
    }
    
	@Override
    public Rect getShape () {
    	return new Rect (0, 0, width, height);
    }

	@Override
    public void enable () {
	}

	@Override
	public void disable () {
	}

	@Override
	public void enter (int toggleMask) {
		allKeysUp ();
		
	}
	
	@Override
	public boolean leave () {
		allKeysUp ();
		return true;
	}
    
	private void allKeysUp () {
		// TODO Auto-generated method stub
	}

	
	@Override
	public void keyDown (int id, int mask, int button) {
		// 1) 'button - 1' appears to be the low-level keyboard scan code
		// 2) 'id' does not appear to be conserved between server keyDown
		// and keyUp event broadcasts as the 'id' on *most* keyUp events
		// appears to be set to 0.  'button' does appear to be conserved
		// so we store the keyDown 'id' using this event so that we can
		// pull out the 'id' used for keyDown for proper keyUp handling 
		if (button < buttonToKeyDownID.length) {
			buttonToKeyDownID[button] = id;
		} else {
			Log.note ("found keyDown button parameter > " + buttonToKeyDownID.length + ", may not be able to properly handle keyUp event.");
		}
		Injection.keydown(id, mask);
	}
	
	@Override
	public void keyUp (int id, int mask, int button) {
		if (button < buttonToKeyDownID.length) {
			int keyDownID = buttonToKeyDownID[button];
			if (keyDownID > -1) {
				id = keyDownID;
			}
		} else {
			Log.note ("found keyUp button parameter > " + buttonToKeyDownID.length + ", may not be able to properly handle keyUp event.");
		}
		Injection.keyup(id, mask);
	}

	@Override
	public void keyRepeat (int keyEventID, int mask, int button) {
	}

	@Override
	public void mouseDown (int buttonID) {
		Injection.mousedown (buttonID);
	}
	
	@Override
	public void mouseUp (int buttonID) {
		Injection.mouseup (buttonID);
	}
		
	@Override
	public final void mouseMove (int x, int y) {
		Log.debug2 ("mouseMove: " + x + ", " + y);

		// this state appears to signal a screen exit, use this to
		// flag mouse position reinitialization for next call
		// to this method.
        if (x == width && y == height) {
        	clearMousePosition(true);
        	return;
        }
        
        if (mouseX < 0 || mouseY < 0) {
        	Injection.movemouse (-width, -height);
        	Injection.movemouse(x, y);
        	mouseX = x;
        	mouseY = y;
        } else {
	        int dx = x - mouseX;
	    	int dy = y - mouseY; 
		  	Injection.movemouse (dx, dy);
	    	// Adjust 'known' cursor position
	        mouseX += dx;
	        mouseY += dy;
        }
	}
	
	@Override
	public void mouseRelativeMove (int x, int y) {
		Injection.movemouse (x, y);
	}

	@Override
	public void mouseWheel (int x, int y) {
		Injection.mousewheel(x, y);
	}
	
	private void clearMousePosition(boolean inject) {
		mouseX = -1; 
	    mouseY = -1;
	    if (inject) {
	    	// moving to height/width will hide mouse pointer
	    	Injection.movemouse(width, height);
	    }
	}

	@Override
	public Point getCursorPos () {
		return new Point (0,0);
	}

	@Override
	public Object getEventTarget () {
		return this;
	}

}
