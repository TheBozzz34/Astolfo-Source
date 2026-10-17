package oshi.software.os.unix.solaris;

import oshi.software.common.AbstractOSVersionInfoEx;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@Deprecated
public class SolarisOSVersionInfoEx extends AbstractOSVersionInfoEx {
   public SolarisOSVersionInfoEx() {
      String string = ExecutingCommand.getFirstAnswer("uname -rv");
      String[] strings = ParseUtil.whitespaces.split(string);
      this.setVersion(strings[0]);
      if (strings.length > 1) {
         this.setBuildNumber(strings[1]);
      }

      this.setCodeName("Solaris");
   }
}
