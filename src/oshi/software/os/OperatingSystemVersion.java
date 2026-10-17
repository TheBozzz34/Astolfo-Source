package oshi.software.os;

import oshi.annotation.concurrent.NotThreadSafe;

@Deprecated
@NotThreadSafe
public interface OperatingSystemVersion {
   String getVersion();

   void setVersion(String string);

   String getCodeName();

   void setCodeName(String string);

   String getBuildNumber();

   void setBuildNumber(String string);
}
