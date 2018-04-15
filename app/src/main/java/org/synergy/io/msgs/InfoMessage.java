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
package org.synergy.io.msgs;

import java.io.IOException;

public class InfoMessage extends Message {
	private static final MessageType MESSAGE_TYPE = MessageType.DINFO;

	short screenX;
	short screenY;
	short screenWidth;
	short screenHeight;
	short unknown; // TODO: I haven't figured out what this is used for yet
	short cursorX;
	short cursorY;
	

    public InfoMessage (int screenX, int screenY, int screenWidth, int screenHeight,
    					 int cursorX, int cursorY) {
    	super (MESSAGE_TYPE);

    	this.screenX = (short) screenX;
    	this.screenY = (short) screenY;
    	this.screenWidth = (short) screenWidth;
    	this.screenHeight = (short) screenHeight;
    	this.unknown = 0; // TODO: see above
    	this.cursorX = (short) cursorX;
    	this.cursorY = (short) cursorY;
    }

    @Override
    protected final void writeData () throws IOException {
        dataStream.writeShort (screenX);
        dataStream.writeShort (screenY);
        dataStream.writeShort (screenWidth);
        dataStream.writeShort (screenHeight);
        dataStream.writeShort (unknown);
        dataStream.writeShort (cursorX);
        dataStream.writeShort (cursorY);
    }
    
    @Override
    public final String toString () {
    	return "InfoMessage:" +
                screenX + ":" + 
                screenY + ":" + 
                screenWidth + ":" + 
                screenHeight + ":" + 
                unknown + ":" + 
                cursorX + ":" + 
                cursorY;
    	
    }
}
