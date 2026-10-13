package net.hypixel.api.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class Utilities {
   private static final long a = 92237956713096L;

   public static ZonedDateTime getDateTime(long var0) {
      return Instant.ofEpochMilli(var0).atZone(ZoneId.of("America/New_York"));
   }
}
