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

import java.io.DataInputStream;
import java.io.IOException;

import org.synergy.io.MessageDataInputStream;

public class ClipboardDataMessage extends Message {
	public static final MessageType MESSAGE_TYPE = MessageType.DCLIPBOARD;
	
	private byte id;
	private int sequenceNumber;
	private String data;
		
	public ClipboardDataMessage (MessageHeader header, DataInputStream din) throws IOException {
		super (header);
		
		MessageDataInputStream mdin = new MessageDataInputStream(din);
		
		id = mdin.readByte();
		sequenceNumber = mdin.readInt();
		data = mdin.readString ();
	}
	
	public String toString () {
		return "ClipboardDataMessage:" + id + ":" + sequenceNumber + ":" + data;
	}
	
}
