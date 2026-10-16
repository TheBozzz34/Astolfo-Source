package bypass.auth.net.scramble;

import java.nio.ByteBuffer;

public final class RecursiveSwapScrambler implements Scrambler {
   private static void swapRecursive(byte[] bytes, int i, int j) {
      int k = j / 2;
      if (k > 1) {
         swapRecursive(bytes, i, k);
         swapRecursive(bytes, i + k, j - k);
      } else if (k == 1) {
         byte b = bytes[i + k];
         bytes[i + k] = bytes[i];
         bytes[i] = b;
      }
   }

   @Override
   public void scramble(ByteBuffer byteBuffer) {
      byte[] bytes = byteBuffer.array();
      swapRecursive(bytes, 0, bytes.length);
   }
}
