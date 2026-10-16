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
   private static final char[] hexChars = "0123456789ABCDEF".toCharArray();

   public static String encrypt(String string) {
      if (string != null && !string.isEmpty()) {
         try {
            SecretKeySpec secretKeySpec = new SecretKeySpec("7NW3vp3p3gM3cZJt".getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(1, secretKeySpec);
            byte[] bytes = cipher.doFinal(string.getBytes());
            return DatatypeConverter.printBase64Binary(bytes);
         } catch (NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException value) {
            InternalError internalError = new InternalError();
            internalError.setStackTrace(new StackTraceElement[0]);
            throw internalError;
         }
      } else {
         return string;
      }
   }

   public static String decrypt(String string) {
      return decryptWithKey(string, "7NW3vp3p3gM3cZJt");
   }

   public static String decryptWithKey(String string, String string2) {
      if (string != null && !string.isEmpty()) {
         try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(string2.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(2, secretKeySpec);
            byte[] bytes = cipher.doFinal(DatatypeConverter.parseBase64Binary(string));
            return new String(bytes);
         } catch (NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException value) {
            NullPointerException nullPointerException = new NullPointerException();
            nullPointerException.setStackTrace(new StackTraceElement[0]);
            throw nullPointerException;
         }
      } else {
         return string;
      }
   }

   public static String bytesToHex(byte[] bytes) {
      char[] chars = new char[bytes.length * 2];

      for (int i = 0; i < bytes.length; i++) {
         int j = bytes[i] & 255;
         chars[i * 2] = hexChars[j >>> 4];
         chars[i * 2 + 1] = hexChars[j & 15];
      }

      return new String(chars);
   }

   public static String runLengthEncode(String string) {
      StringBuilder stringBuilder = new StringBuilder();
      char c = string.charAt(0);
      int i = 1;

      for (int j = 1; j < string.length(); j++) {
         char c2 = string.charAt(j);
         if (c2 == c) {
            i++;
         } else {
            stringBuilder.append(c).append(i);
            i = 1;
         }

         c = c2;
      }

      stringBuilder.append(c).append(i);
      return stringBuilder.toString();
   }

   public static String runLengthDecode(String string) {
      StringBuilder stringBuilder = new StringBuilder();

      for (int i = 0; i < string.length(); i += 2) {
         int j = Integer.valueOf("" + string.charAt(i + 1));

         for (int k = 0; k < j; k++) {
            stringBuilder.append(string.charAt(i));
         }
      }

      return stringBuilder.toString();
   }
}
