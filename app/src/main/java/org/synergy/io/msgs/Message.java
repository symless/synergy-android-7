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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.synergy.base.Log;
import org.synergy.io.MessageDataOutputStream;

/*public interface Message <T> {
	
	private Message <T>;
	public Message<T> create (MessageHeader header); 
	
}*/

/**
 * Writing:
 *   Write to a ByteArrayOutputStream using a DataOutputStream
 *   Create the header
 */
public abstract class Message {
	
	private MessageType type;
	protected MessageHeader header;
	private ByteArrayOutputStream data;
	protected MessageDataOutputStream dataStream;
	
	/**
	 * Constants for reading messages
	 */
	protected static final int BYTE_SIZE = 1;
	protected static final int SHORT_SIZE = 2;
	protected static final int INT_SIZE = 4;
	
	
	/**
	 * Generate a message header 
	 */
	//public abstract MessageHeader generateHeader ();
	
	/**
	 * This constructor is called when a message is read in.
	 * The header information is therefore not important
	 */
	protected Message () {
		this.type = null;
		this.header = null;
		this.data = new ByteArrayOutputStream();
		this.dataStream = new MessageDataOutputStream(this.data);
	}
	
	/*
	 * Create a message 
	 */
	public Message (MessageType type) {
		this.type = type;
		this.header = new MessageHeader (this.getType ());
		this.data = new ByteArrayOutputStream();
		this.dataStream = new MessageDataOutputStream(this.data);
	}
	
	/**
	 * Create a message with a message header
	 */
	public Message (MessageHeader header) {
		this.header = header;
		this.type = header.getType ();
		this.data = new ByteArrayOutputStream();
		this.dataStream = new MessageDataOutputStream(this.data);
	}
	
	/**
	 * Get the message type 
	 */
	public MessageType getType () {
		return this.type;
	}
	

	/**
	 * Get the bytes for this message
	 * @return message data in bytes
	 */
	protected byte [] getBytes () {
		return data.toByteArray();
	}

    /**
      * Writes the message data to the byte array stream
      *
      */
    protected void writeData () throws IOException {
        throw new IOException ("Invalid message. Subclass-ed messages must implement writeData");
    } 


    /**
      * Write the message header and the message data
      */
    public final void write (DataOutputStream dout) throws IOException {
    	Log.debug2 ("Sending: " + this.toString ());
    	
        // Write the message data to the byte array.  
    	//  Subclasses MUST override this function 
        writeData ();
        
        // Set the message header size based on how much data
        //  has been written to the byte array stream
        header.setDataSize (dataStream.size ());
        Log.debug2 ("Sending Header: " + header);
                
        // Write out the header and the message data
        header.write (dout);
        data.writeTo (dout);
        
        dout.flush ();
    }
}
