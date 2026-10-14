package net.hypixel.api.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class Utilities {
   private static final long a = 92237956713096L;

   public static ZonedDateTime getDateTime(long timeStamp) {
      return Instant.ofEpochMilli(timeStamp).atZone(ZoneId.of("America/New_York"));
   }
}
