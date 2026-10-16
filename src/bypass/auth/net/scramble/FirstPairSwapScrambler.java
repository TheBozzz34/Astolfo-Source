package bypass.auth.net.scramble;

import java.nio.ByteBuffer;

public final class FirstPairSwapScrambler implements Scrambler {
   private static void swapFirstPair(byte[] bytes, int i, int j) {
      int k = j / 2;
      if (k > 1) {
         swapFirstPair(bytes, i, k);
      } else if (k == 1) {
         byte b = bytes[i + k];
         bytes[i + k] = bytes[i];
         bytes[i] = b;
      }
   }

   @Override
   public void scramble(ByteBuffer byteBuffer) {
      byte[] bytes = byteBuffer.array();
      swapFirstPair(bytes, 0, bytes.length);
   }
}
