package bypass.util;

import java.math.BigInteger;

public class FnvHash {
   private static final BigInteger m = new BigInteger("811c9dc5", 16);
   private static final BigInteger y = new BigInteger("cbf29ce484222325", 16);
   private static final BigInteger X = new BigInteger("01000193", 16);
   private static final BigInteger B = new BigInteger("100000001b3", 16);
   private static final BigInteger r = new BigInteger("2").pow(32);
   private static final BigInteger q = new BigInteger("2").pow(64);

   public BigInteger X(byte[] bytes) {
      BigInteger bigInteger = m;

      for (byte b : bytes) {
         bigInteger = bigInteger.multiply(X).mod(r);
         bigInteger = bigInteger.xor(BigInteger.valueOf(b & 0xFF));
      }

      return bigInteger;
   }

   public BigInteger o(byte[] bytes) {
      BigInteger bigInteger = y;

      for (byte b : bytes) {
         bigInteger = bigInteger.multiply(B).mod(q);
         bigInteger = bigInteger.xor(BigInteger.valueOf(b & 0xFF));
      }

      return bigInteger;
   }

   public BigInteger h(byte[] bytes) {
      BigInteger bigInteger = m;

      for (byte b : bytes) {
         bigInteger = bigInteger.xor(BigInteger.valueOf(b & 0xFF));
         bigInteger = bigInteger.multiply(X).mod(r);
      }

      return bigInteger;
   }

   public BigInteger s(byte[] bytes) {
      BigInteger bigInteger = y;
      int i = 0;

      for (int j = bytes.length; i < j; i++) {
         byte b = bytes[i];
         bigInteger = bigInteger.xor(BigInteger.valueOf(b & 0xFF));
         bigInteger = bigInteger.multiply(B).mod(q);
      }

      return bigInteger;
   }
}
