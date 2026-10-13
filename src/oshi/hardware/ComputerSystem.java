package oshi.hardware;

import oshi.annotation.concurrent.Immutable;

@Immutable
public interface ComputerSystem {
   String V();

   String z();

   String k();

   Firmware getFirmware();

   Baseboard getBaseboard();
}
