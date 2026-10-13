package bypass.auth.net;

public final class XorMasks {
   private static final byte T = 45;
   private static final byte u = 22;
   private static final byte i = 72;
   private static final byte p = 14;
   private static final byte V = 99;
   private static final byte B = 33;
   private static final long a = 114941214252859L;

   public static byte m(byte var0) {
      return (byte)(var0 ^ 72);
   }

   public static byte e(byte var0) {
      return (byte)(var0 ^ 45);
   }

   public static byte W(byte var0) {
      return (byte)(var0 ^ 22);
   }

   public static byte L(byte var0) {
      return (byte)(var0 ^ 14);
   }

   public static byte Z(byte var0) {
      return (byte)(var0 ^ 99);
   }

   public static byte C(byte var0) {
      return (byte)(var0 ^ 33);
   }

   static {
      long var0 = a ^ 21331223225858L;
      long var10000 = var0 ^ 8514064284078194092L;
      var10000 = var0 ^ 2460136048495960219L;
      var10000 = var0 ^ 5911462281177442192L;
      var10000 = var0 ^ 3742263885249019097L;
      var10000 = var0 ^ 1892368278199275216L;
      var10000 = var0 ^ 4861844347580305118L;
   }
}
