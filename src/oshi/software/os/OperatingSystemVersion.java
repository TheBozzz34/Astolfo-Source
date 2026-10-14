package oshi.software.os;

import oshi.annotation.concurrent.NotThreadSafe;

@Deprecated
@NotThreadSafe
public interface OperatingSystemVersion {
   String getVersion();

   void n(String string);

   String getCodeName();

   void I(String string);

   String getBuildNumber();

   void C(String string);
}
