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

public class HelloBackMessage extends Message {
    private static final MessageType MESSAGE_TYPE = MessageType.HELLOBACK;

    // Protocol version and screen name
    private int majorVersion;
    private int minorVersion;
    private String name;

    public HelloBackMessage (int majorVersion, int minorVersion, String name) {
        super (MESSAGE_TYPE);

        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.name = name;
    }

    @Override
    protected final void writeData () throws IOException {
        dataStream.writeShort (majorVersion);
        dataStream.writeShort (minorVersion);
        dataStream.writeString (name);
    }
}
