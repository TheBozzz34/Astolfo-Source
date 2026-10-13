package oshi.software.os.mac;

import com.sun.jna.Native;
import com.sun.jna.ptr.PointerByReference;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.jna.platform.mac.SystemB;
import oshi.jna.platform.unix.CLibrary;
import oshi.software.common.AbstractNetworkParams;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@ThreadSafe
public final class MacNetworkParams extends AbstractNetworkParams {
   private static final SystemB SYS = SystemB.INSTANCE;
   private static final long b = 136860002131676L;

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
      int var6 = SYS.getaddrinfo(var4, null, var3, var5);
      if (var6 > 0) {
         return "";
      }

      CLibrary.Addrinfo var7 = new CLibrary.Addrinfo(var5.getValue());
      String var8 = var7.ai_canonname.trim();
      SYS.freeaddrinfo(var5.getValue());
      return var8;
   }

   @Override
   public String getHostName() {
      byte[] var3 = new byte[256];
      return 0 != SYS.gethostname(var3, var3.length) ? super.getHostName() : Native.toString(var3);
   }

   @Override
   public String getIpv4DefaultGateway() {
      return searchGateway(ExecutingCommand.runNative("route -n get default"));
   }

   @Override
   public String getIpv6DefaultGateway() {
      List var4 = ExecutingCommand.runNative("netstat -nr");
      boolean var5 = false;

      for (Object var7$raw : var4) {
         String var7 = (String)var7$raw;
         if (var5 && var7.startsWith("default")) {
            String[] var8 = ParseUtil.Q.split(var7);
            if (var8.length > 2 && var8[2].contains("G")) {
               return var8[1].split("%")[0];
            }
         } else if (var7.startsWith("Internet6:")) {
            var5 = true;
         }
      }

      return "";
   }
}
