package oshi.jna.platform.unix.solaris;

import com.sun.jna.Native;
import oshi.jna.platform.unix.CLibrary;

public interface SolarisLibc extends CLibrary {
   SolarisLibc INSTANCE = Native.load("c", SolarisLibc.class);
}
