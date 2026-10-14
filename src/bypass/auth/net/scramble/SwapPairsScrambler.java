package bypass.auth.net.scramble;

import java.nio.ByteBuffer;

public final class SwapPairsScrambler implements Scrambler {
   private static void h(byte[] bytes, int i, int j) {
      byte b = bytes[i];
      bytes[i] = bytes[j];
      bytes[j] = b;
   }

   @Override
   public void B(ByteBuffer byteBuffer) {
      byte[] bytes = byteBuffer.array();
      byte b = 0;
      int i = bytes.length;
      int j = i - 1;

      for (int k = i - 2; b != j; b += b == k ? 1 : 2) {
         h(bytes, b, b + 1);
      }
   }
}
