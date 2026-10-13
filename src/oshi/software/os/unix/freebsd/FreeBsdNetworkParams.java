package oshi.software.os.unix.freebsd;

import com.sun.jna.Native;
import com.sun.jna.ptr.PointerByReference;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.jna.platform.unix.CLibrary;
import oshi.jna.platform.unix.freebsd.FreeBsdLibc;
import oshi.software.common.AbstractNetworkParams;
import oshi.util.ExecutingCommand;

@ThreadSafe
public final class FreeBsdNetworkParams extends AbstractNetworkParams {
   private static final FreeBsdLibc LIBC = FreeBsdLibc.INSTANCE;
   private static final long b = 41215666105544L;

   @Override
   public String getDomainName() {
      CLibrary.Addrinfo var3 = new CLibrary.Addrinfo();
      var3.ai_flags = 2;
      String var4 = this.getHostName();
      PointerByReference var5 = new PointerByReference();
      int var6 = LIBC.getaddrinfo(var4, null, var3, var5);
      if (var6 > 0) {
         return "";
      }

      CLibrary.Addrinfo var7 = new CLibrary.Addrinfo(var5.getValue());
      String var8 = var7.ai_canonname.trim();
      LIBC.freeaddrinfo(var5.getValue());
      return var8;
   }

   @Override
   public String getHostName() {
      byte[] var3 = new byte[256];
      return 0 != LIBC.gethostname(var3, var3.length) ? super.getHostName() : Native.toString(var3);
   }

   @Override
   public String getIpv4DefaultGateway() {
      return searchGateway(ExecutingCommand.runNative("route -4 get default"));
   }

   @Override
   public String getIpv6DefaultGateway() {
      return searchGateway(ExecutingCommand.runNative("route -6 get default"));
   }
}
