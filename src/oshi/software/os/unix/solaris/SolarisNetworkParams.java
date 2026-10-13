package oshi.software.os.unix.solaris;

import com.sun.jna.Native;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.jna.platform.unix.solaris.SolarisLibc;
import oshi.software.common.AbstractNetworkParams;
import oshi.util.ExecutingCommand;

@ThreadSafe
public final class SolarisNetworkParams extends AbstractNetworkParams {
   private static final SolarisLibc LIBC = SolarisLibc.INSTANCE;
   private static final long b = 114702184419316L;
   private static final long i = 235472922032996608L;

   @Override
   public String getHostName() {
      byte[] var3 = new byte[(int)i];
      return 0 != LIBC.gethostname(var3, var3.length) ? super.getHostName() : Native.toString(var3);
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
