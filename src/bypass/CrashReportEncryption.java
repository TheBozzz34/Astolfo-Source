package bypass;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

public final class CrashReportEncryption {
   private static final char[] C = "0123456789ABCDEF".toCharArray();
   private static final long a = 40326595087952L;

   public static String O(String var0) {
      if (var0 != null && !var0.isEmpty()) {
         try {
            SecretKeySpec var4 = new SecretKeySpec("7NW3vp3p3gM3cZJt".getBytes(), CrashReportEncryptionStrings.R("Qvsf"));
            Cipher var8 = Cipher.getInstance(CrashReportEncryptionStrings.R("QvsfDE7YSCgtExfElroMypnpEXM="));
            var8.init(1, var4);
            byte[] var6 = var8.doFinal(var0.getBytes());
            return DatatypeConverter.printBase64Binary(var6);
         } catch (NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException var7) {
            InternalError var5 = new InternalError();
            var5.setStackTrace(new StackTraceElement[0]);
            throw var5;
         }
      } else {
         return var0;
      }
   }

   public static String D(String var0) {
      return H(var0, "7NW3vp3p3gM3cZJt");
   }

   public static String H(String var0, String var1) {
      if (var0 != null && !var0.isEmpty()) {
         try {
            SecretKeySpec var5 = new SecretKeySpec(var1.getBytes(), CrashReportEncryptionStrings.R("Qvsf"));
            Cipher var9 = Cipher.getInstance(CrashReportEncryptionStrings.R("QvsfDE7YSCgtExfElroMypnpEXM="));
            var9.init(2, var5);
            byte[] var7 = var9.doFinal(DatatypeConverter.parseBase64Binary(var0));
            return new String(var7);
         } catch (NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException var8) {
            NullPointerException var6 = new NullPointerException();
            var6.setStackTrace(new StackTraceElement[0]);
            throw var6;
         }
      } else {
         return var0;
      }
   }

   public static String P(byte[] var0) {
      char[] var4 = new char[var0.length * 2];

      for (int var5 = 0; var5 < var0.length; var5++) {
         int var6 = var0[var5] & 255;
         var4[var5 * 2] = C[var6 >>> 4];
         var4[var5 * 2 + 1] = C[var6 & 15];
      }

      return new String(var4);
   }

   public static String h(String var0) {
      StringBuilder var4 = new StringBuilder();
      char var5 = var0.charAt(0);
      int var6 = 1;

      for (int var7 = 1; var7 < var0.length(); var7++) {
         char var8 = var0.charAt(var7);
         if (var8 == var5) {
            var6++;
         } else {
            var4.append(var5).append(var6);
            var6 = 1;
         }

         var5 = var8;
      }

      var4.append(var5).append(var6);
      return var4.toString();
   }

   public static String H(String var0) {
      StringBuilder var4 = new StringBuilder();

      for (int var5 = 0; var5 < var0.length(); var5 += 2) {
         int var6 = Integer.valueOf("" + var0.charAt(var5 + 1));

         for (int var7 = 0; var7 < var6; var7++) {
            var4.append(var0.charAt(var5));
         }
      }

      return var4.toString();
   }
}
