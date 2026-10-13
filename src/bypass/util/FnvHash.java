package bypass.util;

import java.math.BigInteger;

public class FnvHash {
   private static final BigInteger m = new BigInteger("811c9dc5", 16);
   private static final BigInteger y = new BigInteger("cbf29ce484222325", 16);
   private static final BigInteger X = new BigInteger("01000193", 16);
   private static final BigInteger B = new BigInteger("100000001b3", 16);
   private static final BigInteger r = new BigInteger("2").pow(32);
   private static final BigInteger q = new BigInteger("2").pow(64);
   private static final long a = 8022724535161L;

   public BigInteger X(byte[] var1) {
      BigInteger var5 = m;

      for (byte var9 : var1) {
         var5 = var5.multiply(X).mod(r);
         var5 = var5.xor(BigInteger.valueOf(var9 & 0xFF));
      }

      return var5;
   }

   public BigInteger o(byte[] var1) {
      BigInteger var5 = y;

      for (byte var9 : var1) {
         var5 = var5.multiply(B).mod(q);
         var5 = var5.xor(BigInteger.valueOf(var9 & 0xFF));
      }

      return var5;
   }

   public BigInteger h(byte[] var1) {
      BigInteger var6 = m;

      for (byte var10 : var1) {
         var6 = var6.xor(BigInteger.valueOf(var10 & 0xFF));
         var6 = var6.multiply(X).mod(r);
      }

      return var6;
   }

   public BigInteger s(byte[] var1) {
      BigInteger var5 = y;
      int var6 = 0;

      for (int var7 = var1.length; var6 < var7; var6++) {
         byte var8 = var1[var6];
         var5 = var5.xor(BigInteger.valueOf(var8 & 0xFF));
         var5 = var5.multiply(B).mod(q);
      }

      return var5;
   }
}
