package oshi.software.os.unix.freebsd;

import oshi.software.common.AbstractOSVersionInfoEx;
import oshi.util.platform.unix.freebsd.BsdSysctlUtil;

@Deprecated
public class FreeBsdOSVersionInfoEx extends AbstractOSVersionInfoEx {
   public FreeBsdOSVersionInfoEx() {
      this.setVersion(BsdSysctlUtil.sysctl("kern.osrelease", ""));
      String string = BsdSysctlUtil.sysctl("kern.version", "");
      String string2 = BsdSysctlUtil.sysctl("kern.ostype", "FreeBSD");
      this.setBuildNumber(string.split(":")[0].replace(string2, "").replace(this.getVersion(), "").trim());
      this.setCodeName("");
   }
}
