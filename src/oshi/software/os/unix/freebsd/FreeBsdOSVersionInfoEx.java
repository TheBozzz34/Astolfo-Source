package oshi.software.os.unix.freebsd;

import oshi.software.common.AbstractOSVersionInfoEx;
import oshi.util.platform.unix.freebsd.BsdSysctlUtil;

@Deprecated
public class FreeBsdOSVersionInfoEx extends AbstractOSVersionInfoEx {
   private static final long b = 112576163377192L;

   public FreeBsdOSVersionInfoEx() {
      this.n(BsdSysctlUtil.sysctl("kern.osrelease", ""));
      String var3 = BsdSysctlUtil.sysctl("kern.version", "");
      String var4 = BsdSysctlUtil.sysctl("kern.ostype", "FreeBSD");
      this.C(var3.split(":")[0].replace(var4, "").replace(this.getVersion(), "").trim());
      this.I("");
   }
}
