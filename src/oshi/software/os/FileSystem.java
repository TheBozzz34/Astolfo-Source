package oshi.software.os;

import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public interface FileSystem {
   OSFileStore[] getFileStores();

   OSFileStore[] getFileStores(boolean var1);

   long getOpenFileDescriptors();

   long getMaxFileDescriptors();
}
