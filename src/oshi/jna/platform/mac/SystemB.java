package oshi.jna.platform.mac;

import com.sun.jna.Native;
import oshi.jna.platform.unix.CLibrary;

public interface SystemB extends CLibrary, com.sun.jna.platform.mac.SystemB {
   SystemB INSTANCE = Native.load("System", SystemB.class);
}
