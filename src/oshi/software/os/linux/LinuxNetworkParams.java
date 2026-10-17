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
      int i = LIBC.getaddrinfo(string, null, addrinfo, pointerByReference);
      if (i > 0) {
         return "";
      }

      CLibrary.Addrinfo addrinfo2 = new CLibrary.Addrinfo(pointerByReference.getValue());
      String string2 = addrinfo2.ai_canonname.trim();
      LIBC.freeaddrinfo(pointerByReference.getValue());
      return string2;
   }

   @Override
   public String getHostName() {
      byte[] bytes = new byte[256];
      return 0 != LibC.INSTANCE.gethostname(bytes, bytes.length) ? super.getHostName() : Native.toString(bytes);
   }

   @Override
   public String getIpv4DefaultGateway() {
      List list = ExecutingCommand.runNative("route -A inet -n");
      if (list.size() <= 2) {
         return "";
      }

      String string = "";
      int i = Integer.MAX_VALUE;

      for (int j = 2; j < list.size(); j++) {
         String[] strings = ParseUtil.whitespaces.split((CharSequence)list.get(j));
         if (strings.length > 4 && strings[0].equals("0.0.0.0")) {
            boolean bl = strings[3].indexOf(71) != -1;
            int k = ParseUtil.parseIntOrDefault(strings[4], Integer.MAX_VALUE);
            if (bl && k < i) {
               i = k;
               string = strings[1];
            }
         }
      }

      return string;
   }

   @Override
   public String getIpv6DefaultGateway() {
      List list = ExecutingCommand.runNative("route -A inet6 -n");
      if (list.size() <= 2) {
         return "";
      }

      String string = "";
      int i = Integer.MAX_VALUE;

      for (int j = 2; j < list.size(); j++) {
         String[] strings = ParseUtil.whitespaces.split((CharSequence)list.get(j));
         if (strings.length > 3 && strings[0].equals("::/0")) {
            boolean bl = strings[2].indexOf(71) != -1;
            int k = ParseUtil.parseIntOrDefault(strings[3], Integer.MAX_VALUE);
            if (bl && k < i) {
               i = k;
               string = strings[1];
            }
         }
      }

      return string;
   }
}
