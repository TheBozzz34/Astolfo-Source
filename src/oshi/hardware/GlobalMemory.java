package oshi.hardware;

import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public interface GlobalMemory {
   long getTotal();

   long getAvailable();

   long getPageSize();

   VirtualMemory getVirtualMemory();

   PhysicalMemory[] getPhysicalMemory();
}
