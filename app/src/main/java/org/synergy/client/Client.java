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
import org.synergy.base.EventTarget;
import org.synergy.base.EventType;
import org.synergy.base.Log;
import org.synergy.common.screens.ScreenInterface;
import org.synergy.io.Stream;
import org.synergy.io.StreamFilterFactoryInterface;
import org.synergy.io.msgs.EnterMessage;
import org.synergy.io.msgs.HelloBackMessage;
import org.synergy.io.msgs.HelloMessage;
import org.synergy.io.msgs.LeaveMessage;
import org.synergy.net.DataSocketInterface;
import org.synergy.net.NetworkAddress;
import org.synergy.net.SocketFactoryInterface;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.widget.Toast;

public class Client implements EventTarget {
	
	private final Context context;
	private String name;
	private NetworkAddress serverAddress;
	private Stream stream;
	private SocketFactoryInterface socketFactory;
	private StreamFilterFactoryInterface streamFilterFactory;
	private ScreenInterface screen;
	
	private int mouseX;
	private int mouseY;
	
	private ServerProxy server;

	public Client (final Context context, final String name, final NetworkAddress serverAddress,
			SocketFactoryInterface socketFactory, StreamFilterFactoryInterface streamFilterFactory,
			ScreenInterface screen) {
		
		this.context = context;
		this.name = name;
		this.serverAddress = serverAddress;
		this.socketFactory = socketFactory;
		this.streamFilterFactory = streamFilterFactory;
		this.screen = screen;
		
        assert (socketFactory != null);
        assert (screen != null);

        // TODO register suspend / resume event handlers
	}
	
	public void finalize () throws Throwable {
	    // TODO
	}
	
	public void connect () {
        if (stream != null) {
            Log.info ("stream != null");
            return;
        }

		try {
			serverAddress.resolve ();
			
			if (serverAddress.getAddress () != null) {
				Log.debug ("Connecting to: '" +
						serverAddress.getHostname () + "': " +
						serverAddress.getAddress () + ":" +
						serverAddress.getPort ());
			}
			
            // create the socket
	        DataSocketInterface socket = socketFactory.create ();
    
            // filter socket messages, including a packetizing filter
            stream = socket;
            if (streamFilterFactory != null) {
                // TODO stream = streamFilterFactory.create (stream, true);
            }

            // connect
            Log.debug ("connecting to server");

            setupConnecting ();
            setupTimer ();

            socket.connect (serverAddress);
            
            //final Toast toast = Toast.makeText(context, "Connected to " + serverAddress.getHostname()
            //        + ":" + serverAddress.getPort(), Toast.LENGTH_SHORT);
            //toast.show();
		} catch (IOException e) {
			final String errorMessage = "Failed to connect to " + serverAddress.getHostname()
					+ ":" + serverAddress.getPort();
			Log.error(errorMessage);
			//final Toast toast = Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT);
			//toast.show();
		}
	}
	
	public void disconnect (String msg) {
		cleanupTimer ();
		cleanupScreen ();
		cleanupConnecting ();
		if (msg != null) {
			sendConnectionFailedEvent (msg);
		} else {
			sendEvent (EventType.CLIENT_DISCONNECTED, null);
		}
	}
		
    private void setupConnecting () {
        assert (stream != null);

        EventQueue.getInstance().adoptHandler(EventType.SOCKET_CONNECTED, stream.getEventTarget(),
        		new EventJobInterface () { 
        			public void run (Event event) {
        				handleConnected();
        			}});
        
        EventJobInterface job = EventQueue.getInstance().getHandler(EventType.SOCKET_CONNECTED, stream.getEventTarget());
        
        EventQueue.getInstance().adoptHandler(EventType.SOCKET_CONNECT_FAILED, stream.getEventTarget(),
        		new EventJobInterface () {
        			public void run (Event event) {
        				handleConnectionFailed();
        			}});
    }

    private void cleanupConnecting () {
        if (stream != null) {
            EventQueue.getInstance().removeHandler (EventType.SOCKET_CONNECTED, stream.getEventTarget ());
            EventQueue.getInstance().removeHandler (EventType.SOCKET_CONNECT_FAILED, stream.getEventTarget ());
        }
    }

    private void setupTimer () {
        // TODO
        //assert (timer == null);


    }
    
    private void handleConnected () {
    	Log.debug1 ("connected; wait for hello");

        cleanupConnecting ();
        setupConnection ();

        // TODO Clipboard
    }
    
    private void handleConnectionFailed () {
        // TODO
    }
    
    private void handleDisconnected () {
    	// TODO
    }

    private void handleHello () {
        Log.debug ("handling hello");

        try {
            // Read in the Hello Message
            DataInputStream din = new DataInputStream (stream.getInputStream ());
            HelloMessage helloMessage = new HelloMessage (din);

            Log.debug1 ("Read hello message: " + helloMessage);
            
            // TODO check versions
            
            // say hello back
            DataOutputStream dout = new DataOutputStream (stream.getOutputStream ());

            // Grab the hostname
            new HelloBackMessage (1, 3, name).write (dout);

            setupScreen ();
            cleanupTimer ();

            // make sure we process any remaining messages later. we won't 
            //  receive another event for already pending messages so we fake
            //  one
            if (stream.isReady ()) {
                // TODO, So far this event does nothing -- I think
                EventQueue.getInstance ().addEvent (new Event (EventType.STREAM_INPUT_READY, stream.getEventTarget ()));
            }

        } catch (Exception e) {
            e.printStackTrace ();
        }
    }

    private void handleOutputError () {
    }

    
	private void setupConnection () {
        assert (stream != null);

        EventQueue.getInstance().adoptHandler(EventType.SOCKET_DISCONNECTED, stream.getEventTarget(),
        		new EventJobInterface () { public void run (Event event) {
        				handleDisconnected ();
        			}});

        EventQueue.getInstance().adoptHandler(EventType.STREAM_INPUT_READY, stream.getEventTarget(),
        		new EventJobInterface () { public void run (Event event) {
        				handleHello ();
        			}});

        EventQueue.getInstance().adoptHandler(EventType.STREAM_OUTPUT_ERROR, stream.getEventTarget(),
        		new EventJobInterface () { public void run (Event event) {
        				handleDisconnected ();
        			}});
        EventQueue.getInstance().adoptHandler(EventType.STREAM_INPUT_SHUTDOWN, stream.getEventTarget(),
        		new EventJobInterface () { public void run (Event event) {
        				handleDisconnected ();
        			}});
        EventQueue.getInstance().adoptHandler(EventType.STREAM_OUTPUT_SHUTDOWN, stream.getEventTarget(),
        		new EventJobInterface () { public void run (Event event) {
        				handleDisconnected ();
        			}});
    }

    private void setupScreen () {
        assert (server == null);
        assert (screen == null);

        server = new ServerProxy (this, stream);
        
        EventQueue.getInstance().adoptHandler(EventType.SHAPE_CHANGED, getEventTarget (),
        		new EventJobInterface () { public void run (Event event) {
        				handleShapeChanged ();
        			}});
        // TODO Clipboard
//        EventQueue.getInstance().adoptHandler(Stream.getInputShutdownEvent(), stream.getEventTarget(),
//        		new EventJobInterface () { public void run (Event event) {
//        				handleDisconnected ();
//        			}});
    }

    private void cleanupTimer () {
        // TODO
    }
    
    private void cleanupScreen () {
    	// TODO/
    }

    public Object getEventTarget () {
        return screen.getEventTarget ();
    }

    private void handleShapeChanged () {
        Log.debug ("resolution changed");
        server.onInfoChanged ();
    }

    public Rect getShape () {
        return screen.getShape ();
    }

    public Point getCursorPos () {
        return screen.getCursorPos ();
    }
	

    public void handshakeComplete () {
        screen.enable ();
        sendEvent (EventType.CLIENT_CONNECTED, "");
    }

    private void sendEvent (EventType type, Object data) {
        EventQueue.getInstance ().addEvent (new Event (type, data));
    }

    private void sendConnectionFailedEvent (String msg) {
    	// TODO
    }
    

    /*
    private Integer getConnectedEvent () {
        connectedEvent = Event.registerTypeOnce (connectedEvent, "Client.connected");
        return connectedEvent;
    }

    private Integer getConnectionFailedEvent () {
        connectionFailedEvent = Event.registerTypeOnce (connectionFailedEvent, "Client.failed");
        return connectionFailedEvent;
    }

    private Integer getDisconnectedEvent () {
        disconnectedEvent = Event.registerTypeOnce (disconnectedEvent, "Client.disconnected");
        return disconnectedEvent;
    }
    */


    public void enter (EnterMessage enterMessage) {
    	mouseX = enterMessage.getX ();
    	mouseY = enterMessage.getY ();
        screen.mouseMove (mouseX, mouseY);
        screen.enter ((int)enterMessage.getMask ());
    }
    
    public void leave (LeaveMessage leaveMessage) {
    	// Since I don't know how to hide the cursor, tuck it away out of sight
    	screen.mouseMove (screen.getShape ().right, screen.getShape ().bottom);
    }

    public void mouseMove (int x, int y) {
    	screen.mouseMove (x, y);    	
    }

    public void mouseDown (int buttonID) {
        screen.mouseDown (buttonID);
    }

    public void mouseUp (int buttonID) {
        screen.mouseUp (buttonID);
    }
    
    public void relMouseMove (int x, int y) {
    	screen.mouseRelativeMove (x, y);
    }
    
    public void mouseWheel (int x, int y) {
    	screen.mouseWheel(x, y);
    }
    
    /**
     * @param keyEventID A VK_ defined in KeyEvent
     */
    public void keyDown (int keyEventID, int mask, int button) {
       screen.keyDown (keyEventID, mask, button);
    }
    
    /**
     * @param keyEventID A VK_ defined in KeyEvent
     */
    public void keyRepeat (int keyEventID, int mask, int button) {
    	screen.keyDown (keyEventID, mask, button);
    }
    
    /**
     * @param keyEventID A VK_ defined in KeyEvent
     */
   public void keyUp (int keyEventID, int mask, int button) {
   	screen.keyUp (keyEventID, mask, button);
   }

}