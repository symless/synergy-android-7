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
package org.synergy.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.synergy.base.Event;
import org.synergy.base.EventJobInterface;
import org.synergy.base.EventQueue;
import org.synergy.base.EventQueueTimer;
import org.synergy.base.EventType;
import org.synergy.base.Log;
import org.synergy.io.Stream;
import org.synergy.io.msgs.ClipboardDataMessage;
import org.synergy.io.msgs.ClipboardMessage;
import org.synergy.io.msgs.EnterMessage;
import org.synergy.io.msgs.InfoMessage;
import org.synergy.io.msgs.KeepAliveMessage;
import org.synergy.io.msgs.KeyDownMessage;
import org.synergy.io.msgs.KeyRepeatMessage;
import org.synergy.io.msgs.KeyUpMessage;
import org.synergy.io.msgs.LeaveMessage;
import org.synergy.io.msgs.MessageHeader;
import org.synergy.io.msgs.MouseDownMessage;
import org.synergy.io.msgs.MouseUpMessage;
import org.synergy.io.msgs.MouseWheelMessage;
import org.synergy.io.msgs.ResetOptionsMessage;
import org.synergy.io.msgs.ScreenSaverMessage;
import org.synergy.io.msgs.SetOptionsMessage;

public class ServerProxy {
    private static final double KEEP_ALIVE_UNTIL_DEATH = 3.0;
    private static final double KEEP_ALIVE_RATE = 3.0;

	/**
	 * Enumeration and Interface for parsing function
	 */
	private enum Result {
		OKAY, UNKNOWN, DISCONNECT
	};

	// To define what should parse and process messages
	private interface Parser {
		public Result parse () throws IOException;
	}

	private Client client;
	private Stream stream;

	private Integer seqNum;

	private Parser parser;

	// TODO KeyModifierTable

	private double keepAliveAlarm;
	private EventQueueTimer keepAliveAlarmTimer;

	private DataInputStream din;
	private DataOutputStream dout;

	public ServerProxy (Client client, Stream stream) {
		this.client = client;
		this.stream = stream;

		this.seqNum = 0;

		this.keepAliveAlarm = 0.0;
		this.keepAliveAlarmTimer = null;

		assert (client != null);
		assert (stream != null);

		// TODO Key modifier table

		// handle data on stream
		EventQueue.getInstance ().adoptHandler (EventType.STREAM_INPUT_READY, stream.getEventTarget (),
				new EventJobInterface () {
					public void run (Event event) {
						handleData ();
					}
				});

		// send heartbeat
        setKeepAliveRate (KEEP_ALIVE_RATE);
        
		parser = new Parser () {
			public Result parse () throws IOException {
				return parseHandshakeMessage ();
			}
		};

	}

	protected enum EResult {
		OKAY, UNKNOWN, DISCONNECT;
	}

    /**
      * Handle messages before handshake is complete
      */
	protected Result parseHandshakeMessage () throws IOException {
		// Read the header
		MessageHeader header = new MessageHeader (din);
		//Log.debug ("Received Header: " + header);

		switch (header.getType ()) {
		case QINFO:
			//queryInfo (new QueryInfoMessage ());
			queryInfo ();
			break;
		case CINFOACK:
			infoAcknowledgment ();
			break;
		case DSETOPTIONS:
			SetOptionsMessage setOptionsMessage = new SetOptionsMessage (header, din);

			setOptions (setOptionsMessage);

			// handshake is complete
			Log.debug ("Handshake is complete");
			parser = new Parser () {
				public Result parse () throws IOException {
					return parseMessage ();
				}
			};

			client.handshakeComplete ();
			break;
		case CRESETOPTIONS:
			resetOptions (new ResetOptionsMessage (din));
			break;
		default:
			return Result.UNKNOWN;
		}
		
		return Result.OKAY;
	}

    /**
      * Handle messages after the handshake is complete
      */
	byte[] messageDataBuffer = new byte[256];
	protected Result parseMessage () throws IOException {
		// Read the header
		MessageHeader header = new MessageHeader (din);
		
		// NOTE: as this is currently designed an improperly consumed message
		// will break the handling of the next message,  The message data should
		// be fully read into a buffer and that buffer passed into the message
		// for handling...
		
        switch (header.getType ()) {
        case DMOUSEMOVE:
        	// Cut right to the chase with mouse movements since
        	//  they are the most abundant
            short ax = din.readShort ();
    		short ay = din.readShort ();
            client.mouseMove (ax, ay);	
            break;

        case DMOUSERELMOVE:
            short rx = din.readShort ();
    		short ry = din.readShort ();
            client.relMouseMove(rx, ry);	
            break;

        case DMOUSEWHEEL:
			mouseWheel (new MouseWheelMessage (din));
            break;

		case DKEYDOWN:
		    keyDown (new KeyDownMessage(din));
            break;

		case DKEYUP:
			keyUp (new KeyUpMessage(din));
            break;
		
        case DKEYREPEAT:
			keyRepeat (new KeyRepeatMessage (din));
            break;

        case DMOUSEDOWN:
            mouseDown (new MouseDownMessage (din));
            break;

        case DMOUSEUP:
            mouseUp (new MouseUpMessage (din));
            break;

		case CKEEPALIVE:
			// echo keep alives and reset alarm
            new KeepAliveMessage ().write (dout);
			resetKeepAliveAlarm ();
			break;

		case CNOOP:
			// accept and discard no-op
			break;

        case CENTER:
			enter (new EnterMessage (header, din));
			break;

        case CLEAVE:
			leave (new LeaveMessage (din));
			break;

		case CCLIPBOARD:
			grabClipboard (new ClipboardMessage (din));
			break;

		case CSCREENSAVER:
			byte screenSaverOnFlag = din.readByte();
			screensaver (new ScreenSaverMessage (din, screenSaverOnFlag));
			break;

		case QINFO:
			queryInfo ();
			break;

		case CINFOACK:
			//infoAcknowledgment (new InfoAckMessage (din));
			infoAcknowledgment ();
			
			break;

        case DCLIPBOARD:
			setClipboard (new ClipboardDataMessage (header, din));
			break;

		case CRESETOPTIONS:
			resetOptions (new ResetOptionsMessage (din));
			break;

        case DSETOPTIONS:
			SetOptionsMessage setOptionsMessage = new SetOptionsMessage (header, din);
			setOptions (setOptionsMessage);
			break;

		case CCLOSE:
			// server wants us to hangup
			Log.debug1 ("recv close");
			// client.disconnect (null);
			return Result.DISCONNECT;

        case EBAD:
			Log.error ("server disconnected due to a protocol error");
			// client.disconnect("server reported a protocol error");
			return Result.DISCONNECT;

        default: 
			return Result.UNKNOWN;
		}

		return Result.OKAY;

	}

	private void handleData () {
		Log.debug ("handle data called");

		try {
			this.din = new DataInputStream (stream.getInputStream ());
			// this.dout = new DataOutputStream (stream.getOutputStream ());
			// this.oout = new ObjectOutputStream (stream.getOutputStream());

			while (true) {
				switch (parser.parse ()) {
				case OKAY:
					break;
				case UNKNOWN:
					Log.error ("invalid message from server");
					return;
				case DISCONNECT:
					return;
				}
			}
		} catch (IOException e) {
			e.printStackTrace ();
			// TODO
		}
	}

	private void resetKeepAliveAlarm () {
        if (keepAliveAlarmTimer != null) {
            keepAliveAlarmTimer.cancel ();
            keepAliveAlarmTimer = null;
        }
        
        if (keepAliveAlarm > 0.0) {
            keepAliveAlarmTimer = new EventQueueTimer (keepAliveAlarm, true, this, 
                    new EventJobInterface () {
                        public void run (Event event) {
                            handleKeepAliveAlarm ();
                        }});
        }
	}

	private void setKeepAliveRate (double rate) {
        keepAliveAlarm = rate * KEEP_ALIVE_UNTIL_DEATH;
        resetKeepAliveAlarm ();
	}

    private void handleKeepAliveAlarm () {
        Log.note ("server is dead");
        client.disconnect ("server is not responding");
    }

	private void queryInfo () {
		ClientInfo info = new ClientInfo (client.getShape (), client.getCursorPos ());
		sendInfo (info);
	}

	private void sendInfo (ClientInfo info) {
		try {
			dout = new DataOutputStream (stream.getOutputStream ());
            
			new InfoMessage (info.getScreenPosition ().left,
                            info.getScreenPosition ().top,
                            info.getScreenPosition ().right,
                            info.getScreenPosition ().bottom,
                            info.getCursorPos ().x,
                            info.getCursorPos ().y).write (dout);
            
		} catch (Exception e) {
			// TODO
			e.printStackTrace ();
		}
	}

	private void infoAcknowledgment () {
		Log.debug ("recv info acknowledgment");
	}

	public void onInfoChanged () {
		// send info update
		queryInfo ();
	}

	private void enter (EnterMessage enterMessage) {
		Log.debug1 ("Screen entered: " + enterMessage);

        seqNum = enterMessage.getSequenceNumber ();
        client.enter (enterMessage);
	}

	private void leave (LeaveMessage leaveMessage) {
		Log.debug1 ("Screen left: " + leaveMessage);
		client.leave (leaveMessage);
	}

	private void keyUp (KeyUpMessage keyUpMessage) {
        Log.debug1 (keyUpMessage.toString ());
        
        try {
    		client.keyUp (keyUpMessage.getID(), keyUpMessage.getMask(), keyUpMessage.getButton());
        } catch (Exception e) {
        }
	}

    private void keyDown (KeyDownMessage keyDownMessage) {
        Log.info (keyDownMessage.toString ());
        
        client.keyDown (keyDownMessage.getID(), keyDownMessage.getMask(), keyDownMessage.getButton());
        
    }

	private void keyRepeat (KeyRepeatMessage keyRepeatMessage) {
        Log.debug1 (keyRepeatMessage.toString ());
        
        try {
            client.keyRepeat (keyRepeatMessage.getID (), keyRepeatMessage.getMask(), keyRepeatMessage.getButton());
        } catch (Exception e) {
        }
	}

	private void mouseDown (MouseDownMessage mouseDownMessage) {
        Log.debug (mouseDownMessage.toString ());
        client.mouseDown (mouseDownMessage.getButtonID ());
	}

	private void mouseUp (MouseUpMessage mouseUpMessage) {
        Log.debug (mouseUpMessage.toString ());
        client.mouseUp (mouseUpMessage.getButtonID ());
	}

	private void mouseWheel (MouseWheelMessage mouseWheelMessage) {
		client.mouseWheel(mouseWheelMessage.getXDelta(), mouseWheelMessage.getYDelta());
	}

	private void resetOptions (ResetOptionsMessage resetOptionsMessage) {
	}

	private void setOptions (SetOptionsMessage setOptionsMessage) {
	}

	private void screensaver (ScreenSaverMessage screenSaverMessage) {
	}

	private void grabClipboard (ClipboardMessage clipboardMessage) {
	}

	private void setClipboard (ClipboardDataMessage clipboardDataMessage) {
		Log.debug1 ("Setting clipboard: " + clipboardDataMessage);
	}

}
