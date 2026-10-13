package oshi.hardware;

import oshi.annotation.concurrent.Immutable;

@Immutable
public interface Baseboard {
   String p();

   String g();

   String W();

   String A();
}
