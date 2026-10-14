package bypass.auth.net.scramble;

import java.nio.ByteBuffer;

public final class FirstPairSwapScrambler implements Scrambler {
   private static void O(byte[] bytes, int i, int j) {
      int k = j / 2;
      if (k > 1) {
         O(bytes, i, k);
      } else if (k == 1) {
         byte b = bytes[i + k];
         bytes[i + k] = bytes[i];
         bytes[i] = b;
      }
   }

   @Override
   public void B(ByteBuffer byteBuffer) {
      byte[] bytes = byteBuffer.array();
      O(bytes, 0, bytes.length);
   }
}
