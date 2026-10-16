package bypass.auth.net;

public final class XorMasks {
   private static final byte MASK_45 = 45;
   private static final byte MASK_22 = 22;
   private static final byte MASK_72 = 72;
   private static final byte MASK_14 = 14;
   private static final byte MASK_99 = 99;
   private static final byte MASK_33 = 33;

   public static byte xor72(byte b) {
      return (byte)(b ^ 72);
   }

   public static byte xor45(byte b) {
      return (byte)(b ^ 45);
   }

   public static byte xor22(byte b) {
      return (byte)(b ^ 22);
   }

   public static byte xor14(byte b) {
      return (byte)(b ^ 14);
   }

   public static byte xor99(byte b) {
      return (byte)(b ^ 99);
   }

   public static byte xor33(byte b) {
      return (byte)(b ^ 33);
   }
}
