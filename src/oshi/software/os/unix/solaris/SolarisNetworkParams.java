package oshi.software.os.unix.solaris;

import com.sun.jna.Native;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.jna.platform.unix.solaris.SolarisLibc;
import oshi.software.common.AbstractNetworkParams;
import oshi.util.ExecutingCommand;

@ThreadSafe
public final class SolarisNetworkParams extends AbstractNetworkParams {
   private static final SolarisLibc LIBC = SolarisLibc.INSTANCE;

   @Override
   public String getHostName() {
      byte[] bytes = new byte[256];
      return 0 != LIBC.gethostname(bytes, bytes.length) ? super.getHostName() : Native.toString(bytes);
   }

   @Override
   public String getIpv4DefaultGateway() {
      return searchGateway(ExecutingCommand.runNative("route get -inet default"));
   }

   @Override
   public String getIpv6DefaultGateway() {
      return searchGateway(ExecutingCommand.runNative("route get -inet6 default"));
   }
}
