package net.minecraft.util;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CryptManager {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final long a = 16981134938495L;

   public static SecretKey createNewSharedKey() {
      try {
         KeyGenerator var2 = KeyGenerator.getInstance("AES");
         var2.init(128);
         return var2.generateKey();
      } catch (NoSuchAlgorithmException var3) {
         throw new Error(var3);
      }
   }

   public static KeyPair generateKeyPair() {
      try {
         KeyPairGenerator var2 = KeyPairGenerator.getInstance("RSA");
         var2.initialize(1024);
         return var2.generateKeyPair();
      } catch (NoSuchAlgorithmException var3) {
         var3.printStackTrace();
         LOGGER.error("Key pair generation failed!");
         return null;
      }
   }

   public static byte[] getServerIdHash(String var0, PublicKey var1, SecretKey var2) {
      try {
         return digestOperation("SHA-1", var0.getBytes("ISO_8859_1"), var2.getEncoded(), var1.getEncoded());
      } catch (UnsupportedEncodingException var6) {
         var6.printStackTrace();
         return null;
      }
   }

   private static byte[] digestOperation(String var0, byte[]... var1) {
      try {
         MessageDigest var5 = MessageDigest.getInstance(var0);

         for (byte[] var9 : var1) {
            var5.update(var9);
         }

         return var5.digest();
      } catch (NoSuchAlgorithmException var10) {
         var10.printStackTrace();
         return null;
      }
   }

   public static PublicKey decodePublicKey(byte[] var0) {
      try {
         X509EncodedKeySpec var3 = new X509EncodedKeySpec(var0);
         KeyFactory var4 = KeyFactory.getInstance("RSA");
         return var4.generatePublic(var3);
      } catch (NoSuchAlgorithmException var5) {
      } catch (InvalidKeySpecException var6) {
      }

      LOGGER.error("Public key reconstitute failed!");
      return null;
   }

   public static SecretKey decryptSharedKey(PrivateKey var0, byte[] var1) {
      return new SecretKeySpec(decryptData(var0, var1), "AES");
   }

   public static byte[] encryptData(Key var0, byte[] var1) {
      return cipherOperation(1, var0, var1);
   }

   public static byte[] decryptData(Key var0, byte[] var1) {
      return cipherOperation(2, var0, var1);
   }

   private static byte[] cipherOperation(int var0, Key var1, byte[] var2) {
      try {
         return createTheCipherInstance(var0, var1.getAlgorithm(), var1).doFinal(var2);
      } catch (IllegalBlockSizeException var6) {
         var6.printStackTrace();
      } catch (BadPaddingException var7) {
         var7.printStackTrace();
      }

      LOGGER.error("Cipher data failed!");
      return null;
   }

   private static Cipher createTheCipherInstance(int var0, String var1, Key var2) {
      try {
         Cipher var5 = Cipher.getInstance(var1);
         var5.init(var0, var2);
         return var5;
      } catch (InvalidKeyException var6) {
         var6.printStackTrace();
      } catch (NoSuchAlgorithmException var7) {
         var7.printStackTrace();
      } catch (NoSuchPaddingException var8) {
         var8.printStackTrace();
      }

      LOGGER.error("Cipher creation failed!");
      return null;
   }

   public static Cipher createNetCipherInstance(int var0, Key var1) {
      try {
         Cipher var4 = Cipher.getInstance("AES/CFB8/NoPadding");
         var4.init(var0, var1, new IvParameterSpec(var1.getEncoded()));
         return var4;
      } catch (GeneralSecurityException var5) {
         throw new RuntimeException(var5);
      }
   }
}
