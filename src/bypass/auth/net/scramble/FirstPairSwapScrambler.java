package bypass.auth.net.scramble;

import java.nio.ByteBuffer;

public final class FirstPairSwapScrambler implements Scrambler {
   private static final long a = 9773263911674L;

   private static void O(byte[] var0, int var1, int var2) {
      int var6 = var2 / 2;
      if (var6 > 1) {
         O(var0, var1, var6);
      } else if (var6 == 1) {
         byte var7 = var0[var1 + var6];
         var0[var1 + var6] = var0[var1];
         var0[var1] = var7;
      }
   }

   @Override
   public void B(ByteBuffer var1) {
      byte[] var2 = var1.array();
      O(var2, 0, var2.length);
   }
}
