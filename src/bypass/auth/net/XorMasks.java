package bypass.auth.net;

public final class XorMasks {
   private static final byte T = 45;
   private static final byte u = 22;
   private static final byte i = 72;
   private static final byte p = 14;
   private static final byte V = 99;
   private static final byte B = 33;
   private static final long a = 114941214252859L;

   public static byte m(byte b) {
      return (byte)(b ^ 72);
   }

   public static byte e(byte b) {
      return (byte)(b ^ 45);
   }

   public static byte W(byte b) {
      return (byte)(b ^ 22);
   }

   public static byte L(byte b) {
      return (byte)(b ^ 14);
   }

   public static byte Z(byte b) {
      return (byte)(b ^ 99);
   }

   public static byte C(byte b) {
      return (byte)(b ^ 33);
   }

   static {
      long l = a ^ 21331223225858L;
      long m = l ^ 8514064284078194092L;
      m = l ^ 2460136048495960219L;
      m = l ^ 5911462281177442192L;
      m = l ^ 3742263885249019097L;
      m = l ^ 1892368278199275216L;
      m = l ^ 4861844347580305118L;
   }
}
