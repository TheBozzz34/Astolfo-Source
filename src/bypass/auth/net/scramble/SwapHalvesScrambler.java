package bypass.auth.net.scramble;

import java.nio.ByteBuffer;

public final class SwapHalvesScrambler implements Scrambler {
   private static final long a = 34959906320801L;

   @Override
   public void B(ByteBuffer byteBuffer) {
      byte[] bytes = byteBuffer.array();
      int i = bytes.length / 2;

      for (int j = 0; j < i; j++) {
         byte b = bytes[j + i];
         bytes[j + i] = bytes[j];
         bytes[j] = b;
      }
   }
}
