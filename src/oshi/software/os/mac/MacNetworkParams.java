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

   @Override
   public String getDomainName() {
      CLibrary.Addrinfo addrinfo = new CLibrary.Addrinfo();
      addrinfo.ai_flags = 2;
      String string = "";

      try {
         string = InetAddress.getLocalHost().getHostName();
      } catch (UnknownHostException unknownHostException) {
         return "";
      }

      PointerByReference pointerByReference = new PointerByReference();
      int i = SYS.getaddrinfo(string, null, addrinfo, pointerByReference);
      if (i > 0) {
         return "";
      }

      CLibrary.Addrinfo addrinfo2 = new CLibrary.Addrinfo(pointerByReference.getValue());
      String string2 = addrinfo2.ai_canonname.trim();
      SYS.freeaddrinfo(pointerByReference.getValue());
      return string2;
   }

   @Override
   public String getHostName() {
      byte[] bytes = new byte[256];
      return 0 != SYS.gethostname(bytes, bytes.length) ? super.getHostName() : Native.toString(bytes);
   }

   @Override
   public String getIpv4DefaultGateway() {
      return searchGateway(ExecutingCommand.runNative("route -n get default"));
   }

   @Override
   public String getIpv6DefaultGateway() {
      List list = ExecutingCommand.runNative("netstat -nr");
      boolean bl = false;

      for (Object object : list) {
         String string = (String)object;
         if (bl && string.startsWith("default")) {
            String[] strings = ParseUtil.whitespaces.split(string);
            if (strings.length > 2 && strings[2].contains("G")) {
               return strings[1].split("%")[0];
            }
         } else if (string.startsWith("Internet6:")) {
            bl = true;
         }
      }

      return "";
   }
}
