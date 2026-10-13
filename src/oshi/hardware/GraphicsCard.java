package oshi.hardware;

import oshi.annotation.concurrent.Immutable;

@Immutable
public interface GraphicsCard {
   String E();

   String S();

   String L();

   String e();

   long getVRam();
}
