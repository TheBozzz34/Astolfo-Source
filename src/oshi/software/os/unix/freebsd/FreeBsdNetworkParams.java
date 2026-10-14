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

   @Override
   public String getDomainName() {
      CLibrary.Addrinfo addrinfo = new CLibrary.Addrinfo();
      addrinfo.ai_flags = 2;
      String string = this.getHostName();
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
      return 0 != LIBC.gethostname(bytes, bytes.length) ? super.getHostName() : Native.toString(bytes);
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
