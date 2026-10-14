package oshi.software.os.unix.solaris;

import oshi.software.common.AbstractOSVersionInfoEx;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@Deprecated
public class SolarisOSVersionInfoEx extends AbstractOSVersionInfoEx {
   private static final long b = 22969604460943L;

   public SolarisOSVersionInfoEx() {
      String string = ExecutingCommand.getFirstAnswer("uname -rv");
      String[] strings = ParseUtil.Q.split(string);
      this.n(strings[0]);
      if (strings.length > 1) {
         this.C(strings[1]);
      }

      this.I("Solaris");
   }
}
