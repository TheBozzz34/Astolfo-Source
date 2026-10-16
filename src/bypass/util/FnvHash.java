package bypass.util;

import java.math.BigInteger;

public class FnvHash {
   private static final BigInteger FNV32_OFFSET_BASIS = new BigInteger("811c9dc5", 16);
   private static final BigInteger FNV64_OFFSET_BASIS = new BigInteger("cbf29ce484222325", 16);
   private static final BigInteger FNV32_PRIME = new BigInteger("01000193", 16);
   private static final BigInteger FNV64_PRIME = new BigInteger("100000001b3", 16);
   private static final BigInteger MOD_32 = new BigInteger("2").pow(32);
   private static final BigInteger MOD_64 = new BigInteger("2").pow(64);

   public BigInteger fnv1Hash32(byte[] bytes) {
      BigInteger bigInteger = FNV32_OFFSET_BASIS;

      for (byte b : bytes) {
         bigInteger = bigInteger.multiply(FNV32_PRIME).mod(MOD_32);
         bigInteger = bigInteger.xor(BigInteger.valueOf(b & 0xFF));
      }

      return bigInteger;
   }

   public BigInteger fnv1Hash64(byte[] bytes) {
      BigInteger bigInteger = FNV64_OFFSET_BASIS;

      for (byte b : bytes) {
         bigInteger = bigInteger.multiply(FNV64_PRIME).mod(MOD_64);
         bigInteger = bigInteger.xor(BigInteger.valueOf(b & 0xFF));
      }

      return bigInteger;
   }

   public BigInteger fnv1aHash32(byte[] bytes) {
      BigInteger bigInteger = FNV32_OFFSET_BASIS;

      for (byte b : bytes) {
         bigInteger = bigInteger.xor(BigInteger.valueOf(b & 0xFF));
         bigInteger = bigInteger.multiply(FNV32_PRIME).mod(MOD_32);
      }

      return bigInteger;
   }

   public BigInteger fnv1aHash64(byte[] bytes) {
      BigInteger bigInteger = FNV64_OFFSET_BASIS;
      int i = 0;

      for (int j = bytes.length; i < j; i++) {
         byte b = bytes[i];
         bigInteger = bigInteger.xor(BigInteger.valueOf(b & 0xFF));
         bigInteger = bigInteger.multiply(FNV64_PRIME).mod(MOD_64);
      }

      return bigInteger;
   }
}
