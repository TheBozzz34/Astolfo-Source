package oshi.software.os.unix.solaris;

import oshi.software.common.AbstractOSVersionInfoEx;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@Deprecated
public class SolarisOSVersionInfoEx extends AbstractOSVersionInfoEx {
   private static final long b = 22969604460943L;

   public SolarisOSVersionInfoEx() {
      String var4 = ExecutingCommand.getFirstAnswer("uname -rv");
      String[] var5 = ParseUtil.Q.split(var4);
      this.n(var5[0]);
      if (var5.length > 1) {
         this.C(var5[1]);
      }

      this.I("Solaris");
   }
}
