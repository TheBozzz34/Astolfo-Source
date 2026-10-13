package bypass.auth.net.scramble;

import java.nio.ByteBuffer;

public final class SwapHalvesScrambler implements Scrambler {
   private static final long a = 34959906320801L;

   @Override
   public void B(ByteBuffer var1) {
      byte[] var5 = var1.array();
      int var6 = var5.length / 2;

      for (int var7 = 0; var7 < var6; var7++) {
         byte var8 = var5[var7 + var6];
         var5[var7 + var6] = var5[var7];
         var5[var7] = var8;
      }
   }
}
