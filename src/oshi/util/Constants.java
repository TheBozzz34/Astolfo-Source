package oshi.util;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class Constants {
   public static final String N = "unknown";
   public static final String c = "/sys/devices/virtual/dmi/id/";
   public static final OffsetDateTime UNIX_EPOCH = OffsetDateTime.ofInstant(Instant.EPOCH, ZoneOffset.UTC);

   private Constants() {
      throw new AssertionError();
   }
}
