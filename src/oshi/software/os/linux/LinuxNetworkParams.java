package oshi.software.os.linux;

import com.sun.jna.Native;
import com.sun.jna.platform.linux.LibC;
import com.sun.jna.ptr.PointerByReference;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.jna.platform.linux.LinuxLibc;
import oshi.jna.platform.unix.CLibrary;
import oshi.software.common.AbstractNetworkParams;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@ThreadSafe
public final class LinuxNetworkParams extends AbstractNetworkParams {
   private static final LinuxLibc LIBC = LinuxLibc.INSTANCE;
   private static final long c = 92836762438379L;

   @Override
   public String getDomainName() {
      CLibrary.Addrinfo var3 = new CLibrary.Addrinfo();
      var3.ai_flags = 2;
      String var4 = "";

      try {
         var4 = InetAddress.getLocalHost().getHostName();
      } catch (UnknownHostException var9) {
         return "";
      }

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
      return 0 != LibC.INSTANCE.gethostname(var3, var3.length) ? super.getHostName() : Native.toString(var3);
   }

   @Override
   public String getIpv4DefaultGateway() {
      List var4 = ExecutingCommand.runNative("route -A inet -n");
      if (var4.size() <= 2) {
         return "";
      }

      String var5 = "";
      int var6 = Integer.MAX_VALUE;

      for (int var7 = 2; var7 < var4.size(); var7++) {
         String[] var8 = ParseUtil.Q.split((CharSequence)var4.get(var7));
         if (var8.length > 4 && var8[0].equals("0.0.0.0")) {
            boolean var9 = var8[3].indexOf(71) != -1;
            int var10 = ParseUtil.parseIntOrDefault(var8[4], Integer.MAX_VALUE);
            if (var9 && var10 < var6) {
               var6 = var10;
               var5 = var8[1];
            }
         }
      }

      return var5;
   }

   @Override
   public String getIpv6DefaultGateway() {
      List var4 = ExecutingCommand.runNative("route -A inet6 -n");
      if (var4.size() <= 2) {
         return "";
      }

      String var5 = "";
      int var6 = Integer.MAX_VALUE;

      for (int var7 = 2; var7 < var4.size(); var7++) {
         String[] var8 = ParseUtil.Q.split((CharSequence)var4.get(var7));
         if (var8.length > 3 && var8[0].equals("::/0")) {
            boolean var9 = var8[2].indexOf(71) != -1;
            int var10 = ParseUtil.parseIntOrDefault(var8[3], Integer.MAX_VALUE);
            if (var9 && var10 < var6) {
               var6 = var10;
               var5 = var8[1];
            }
         }
      }

      return var5;
   }
}
