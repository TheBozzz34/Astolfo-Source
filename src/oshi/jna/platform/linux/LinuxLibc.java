package oshi.jna.platform.linux;

import com.sun.jna.Native;
import com.sun.jna.platform.linux.LibC;
import oshi.jna.platform.unix.CLibrary;

public interface LinuxLibc extends LibC, CLibrary {
   LinuxLibc INSTANCE = Native.load("c", LinuxLibc.class);
}
