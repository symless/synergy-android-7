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

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Data Output Stream for messages
 * 
 * This class was specifically designed for writing strings in messages. 
 * 
 * Strings are written as: String length (int) + String 
 *  * 
 * @author Shaun Patterson
 */

public class MessageDataOutputStream extends DataOutputStream {
	
	public MessageDataOutputStream (OutputStream out) {
		super (out);
	}
	
	
	/**
	 * Writes the string length and the string. 
	 *   
	 * @param str to write
	 * @throws IOException
	 */
	public void writeString (String str) throws IOException {
		super.writeInt (str.length());
		super.write(str.getBytes ("UTF8"));
	}
	
	
	
	
}
