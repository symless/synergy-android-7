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
package org.synergy.net;

import java.net.InetAddress;
import java.net.UnknownHostException;

/** 
 * @author Shaun Patterson
 */
public class NetworkAddress {

    private InetAddress address;
    private String hostname = "";
    private int port = 0;
    

    public NetworkAddress () {
    }

    public NetworkAddress (int port) {
    }

    public NetworkAddress (final String hostname, final int port) {
        this.hostname = hostname;
        this.port = port;
    }

    protected void finalize () throws Throwable {
        super.finalize ();

        // TODO close address
    }

    public void resolve () throws UnknownHostException {
        if (address != null) {
            // Discard previous address

            address = null;
        }

        if (hostname == null) {
        	System.out.println ("Hostname is empty");
        } else {
            address = InetAddress.getByName (hostname);
        }
    }

    public boolean isValid () {
    	return true;
    }

    public InetAddress getAddress () {
        return address;
    }

    public int getPort () {
    	return port;
    }

    public String getHostname () {
    	return hostname;
    }

    private void checkPort () {
    }

    boolean equalTo (NetworkAddress address) {
    	return false;
    }  
}
