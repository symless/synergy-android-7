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
package org.synergy.io;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MessageDataInputStream extends DataInputStream {

	public MessageDataInputStream (InputStream in) {
		super (in);
	}
	
	/**
	 * Read in a string.  First reads in the string length and then the string
	 */
	public String readString () throws IOException {
		int stringLength = readInt ();
		
		// Read in the bytes and convert to a string
		byte [] stringBytes = new byte [stringLength];
		read (stringBytes, 0, stringBytes.length);
		return new String (stringBytes);
	}
	
	
	/**
	 * Read an expected string from the stream
	 * @throws IOException if expected string is not read
	 */
	public void readExpectedString (String expectedString) throws IOException {
		byte [] stringBytes = new byte [expectedString.length()];
		
		// Read in the bytes and convert to a string
		read (stringBytes, 0, stringBytes.length);
		String readString = new String (stringBytes);
		
		if (readString.equals(expectedString) == false) {
			throw new IOException ("Expected string " + expectedString + " not found.  Found: " + readString);
		}
	}
	
	
}
