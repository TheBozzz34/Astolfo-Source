package oshi.jna.platform.mac;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Structure;
import com.sun.jna.platform.mac.IOKit.IOConnect;
import com.sun.jna.ptr.NativeLongByReference;

public interface IOKit extends com.sun.jna.platform.mac.IOKit {
   IOKit INSTANCE = Native.load("IOKit", IOKit.class);

   int IOConnectCallStructMethod(IOConnect var1, int var2, Structure var3, NativeLong var4, Structure var5, NativeLongByReference var6);
}
