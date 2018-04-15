package org.synergy.injection;

import java.io.DataOutputStream;

import org.synergy.base.Log;

public final class Injection {

	static {
		System.loadLibrary ("synergy-jni");
	}
	
	/**
	 * Functions imported from synergy-jni library
	 */
	public static final native void start (String deviceName);
	public static native void stop ();
	public static native void keydown (int key, int mask);
	public static native void keyup (int key, int mask);
	public static final native void movemouse (final int x, final int y);
	public static native void mousedown (int buttonId);
	public static native void mouseup (int buttonId);
	public static native void mousewheel (int x, int y);
	
	private Injection () { }
	
	public static void setPermissionsForInputDevice () {
		Log.debug ("Starting injection");
		try {
			Process process = Runtime.getRuntime ().exec ("su");
			DataOutputStream dout = new DataOutputStream (process.getOutputStream ());
			dout.writeBytes ("chmod 666 /dev/uinput");
			dout.flush ();
			dout.close ();
			process.waitFor ();
			Log.debug ("Access to /dev/uinput granted");
		} catch (Exception e) {
			e.printStackTrace ();
		}
	}

	public static void startInjection (String deviceName) {
		start (deviceName);
	}

	public static void stopInjection () {
		stop ();
	}
}
