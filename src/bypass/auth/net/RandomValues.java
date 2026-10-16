package bypass.auth.net;

import java.util.concurrent.ThreadLocalRandom;

public final class RandomValues {
   public static byte minByte() {
      return -128;
   }

   public static byte maxByte() {
      return 127;
   }

   public static int minInt() {
      return Integer.MIN_VALUE;
   }

   public static int randomInt() {
      return ThreadLocalRandom.current().nextInt();
   }

   public static short randomShort() {
      return (short)ThreadLocalRandom.current().nextInt(-32768, 32768);
   }

   public static short randomShortBelow(short s) {
      return (short)ThreadLocalRandom.current().nextInt(-32768, s);
   }

   public static byte randomByteBelow(byte b) {
      return (byte)ThreadLocalRandom.current().nextInt(-128, b);
   }

   public static byte randomByte() {
      return (byte)ThreadLocalRandom.current().nextInt(-128, 128);
   }
}
