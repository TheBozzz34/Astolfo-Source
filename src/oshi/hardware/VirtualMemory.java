package oshi.hardware;

import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public interface VirtualMemory {
   long getSwapTotal();

   long getSwapUsed();

   long L();

   long I();
}
