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
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Describes a message header
 * 
 * Size = 
 *  length of message type +
 *  message data size (see Message.write)
 */
public class MessageHeader {
	private final static int MESSAGE_TYPE_SIZE = 4;
	
	private Integer size;
	private Integer dataSize;
	private MessageType type;
	
	public MessageHeader (MessageType type) {
		this.type = type;
		this.size = type.getValue ().length ();
		this.dataSize = null;  // User must set
	}
	
	public MessageHeader (String type) {
		this.type = MessageType.fromString (type);
		this.size = this.type.getValue ().length ();
		this.dataSize = null;  // User must set
	}
	
	/**
	 * Read in a message header
	 * @param din Data input stream from socket
	 */
	public MessageHeader (DataInputStream din) throws IOException {
		int messageSize = din.readInt ();
		
		
		byte messageTypeBytes [] = new byte [MESSAGE_TYPE_SIZE];
        din.read (messageTypeBytes, 0, MESSAGE_TYPE_SIZE);
        this.type = MessageType.fromString (new String (messageTypeBytes));
		this.size = MESSAGE_TYPE_SIZE;
        this.dataSize = messageSize - this.size;
	}
	
	/**
	 * Set the size of the DATA passed along with this message
	 * @param dataSize
	 */
	public void setDataSize (int dataSize) {
		this.dataSize = dataSize;
	}
	
	/**
	 * Get the size of the data in the message
	 */
	public int getDataSize () {
		return this.dataSize;
	}
	
	/**
	 * Get the message type for the message this header describes
	 * @return
	 */
	public MessageType getType () {
		return type;
	}
	
	public void write (DataOutputStream dout) throws IOException {
		if (dataSize == null) {
			throw new IOException ("Message header size is null");
		}
		
		dout.writeInt(size + dataSize);
		dout.write (type.getValue ().getBytes("UTF8"));
		
	}

	public String toString () {
		return "MessageHeader:" + size + ":" + dataSize + ":" + type;
	}
	
}
