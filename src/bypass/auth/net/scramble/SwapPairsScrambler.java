package bypass.auth.net.scramble;

import java.nio.ByteBuffer;

public final class SwapPairsScrambler implements Scrambler {
   private static final long a = 41730414129629L;

   private static void h(byte[] var0, int var1, int var2) {
      byte var3 = var0[var1];
      var0[var1] = var0[var2];
      var0[var2] = var3;
   }

   @Override
   public void B(ByteBuffer var1) {
      byte[] var5 = var1.array();
      byte var6 = 0;
      int var7 = var5.length;
      int var8 = var7 - 1;

      for (int var9 = var7 - 2; var6 != var8; var6 += var6 == var9 ? 1 : 2) {
         h(var5, var6, var6 + 1);
      }
   }
}
