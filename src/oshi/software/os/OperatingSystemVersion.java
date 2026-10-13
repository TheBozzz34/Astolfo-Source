package oshi.software.os;

import oshi.annotation.concurrent.NotThreadSafe;

@Deprecated
@NotThreadSafe
public interface OperatingSystemVersion {
   String getVersion();

   void n(String var1);

   String getCodeName();

   void I(String var1);

   String getBuildNumber();

   void C(String var1);
}
