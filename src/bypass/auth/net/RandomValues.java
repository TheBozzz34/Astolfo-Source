package bypass.auth.net;

import java.util.concurrent.ThreadLocalRandom;

public final class RandomValues {
   private static final long a = 136084739936287L;

   public static byte y() {
      return -128;
   }

   public static byte x() {
      return 127;
   }

   public static int H() {
      return Integer.MIN_VALUE;
   }

   public static int n() {
      return ThreadLocalRandom.current().nextInt();
   }

   public static short X() {
      return (short)ThreadLocalRandom.current().nextInt(-32768, 32768);
   }

   public static short k(short s) {
      return (short)ThreadLocalRandom.current().nextInt(-32768, s);
   }

   public static byte c(byte b) {
      return (byte)ThreadLocalRandom.current().nextInt(-128, b);
   }

   public static byte S() {
      return (byte)ThreadLocalRandom.current().nextInt(-128, 128);
   }
}
