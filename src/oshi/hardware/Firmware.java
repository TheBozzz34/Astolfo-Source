package oshi.hardware;

import oshi.annotation.concurrent.Immutable;

@Immutable
public interface Firmware {
   String R();

   String getName();

   String getDescription();

   String P();

   String getReleaseDate();
}
