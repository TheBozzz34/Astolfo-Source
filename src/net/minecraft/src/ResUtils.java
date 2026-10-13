package net.minecraft.src;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.minecraft.client.resources.AbstractResourcePack;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.util.ResourceLocation;

public class ResUtils {
   private static final long a = 80758993885400L;

   public static String[] collectFiles(String var0, String var1) {
      return collectFiles(new String[]{var0}, new String[]{var1});
   }

   public static String[] collectFiles(String[] var0, String[] var1) {
      LinkedHashSet var5 = new LinkedHashSet();
      IResourcePack[] var6 = Config.getResourcePacks();

      for (int var7 = 0; var7 < var6.length; var7++) {
         IResourcePack var8 = var6[var7];
         String[] var9 = collectFiles(var8, var0, var1, (String[])null);
         var5.addAll(Arrays.asList(var9));
      }

      return (java.lang.String[])(var5.toArray(new String[var5.size()]));
   }

   public static String[] collectFiles(IResourcePack var0, String var1, String var2, String[] var3) {
      return collectFiles(var0, new String[]{var1}, new String[]{var2}, var3);
   }

   public static String[] collectFiles(IResourcePack var0, String[] var1, String[] var2) {
      return collectFiles(var0, var1, var2, (String[])null);
   }

   public static String[] collectFiles(IResourcePack var0, String[] var1, String[] var2, String[] var3) {
      if (var0 instanceof DefaultResourcePack) {
         return collectFilesFixed(var0, var3);
      }

      if (!(var0 instanceof AbstractResourcePack)) {
         return new String[0];
      }

      AbstractResourcePack var7 = (AbstractResourcePack)var0;
      File var8 = var7.resourcePackFile;
      return var8 == null
         ? new String[0]
         : (var8.isDirectory() ? collectFilesFolder(var8, "", var1, var2) : (var8.isFile() ? collectFilesZIP(var8, var1, var2) : new String[0]));
   }

   private static String[] collectFilesFixed(IResourcePack var0, String[] var1) {
      if (var1 == null) {
         return new String[0];
      }

      ArrayList var5 = new ArrayList();

      for (int var6 = 0; var6 < var1.length; var6++) {
         String var7 = var1[var6];
         ResourceLocation var8 = new ResourceLocation(var7);
         if (var0.resourceExists(var8)) {
            var5.add(var7);
         }
      }

      return (java.lang.String[])(var5.toArray(new String[var5.size()]));
   }

   private static String[] collectFilesFolder(File var0, String var1, String[] var2, String[] var3) {
      ArrayList var7 = new ArrayList();
      String var8 = "assets/minecraft/";
      File[] var9 = var0.listFiles();
      if (var9 == null) {
         return new String[0];
      }

      for (int var10 = 0; var10 < var9.length; var10++) {
         File var11 = var9[var10];
         if (var11.isFile()) {
            String var16 = var1 + var11.getName();
            if (var16.startsWith(var8)) {
               var16 = var16.substring(var8.length());
               if (StrUtils.startsWith(var16, var2) && StrUtils.endsWith(var16, var3)) {
                  var7.add(var16);
               }
            }
         } else if (var11.isDirectory()) {
            String var12 = var1 + var11.getName() + "/";
            String[] var13 = collectFilesFolder(var11, var12, var2, var3);

            for (int var14 = 0; var14 < var13.length; var14++) {
               String var15 = var13[var14];
               var7.add(var15);
            }
         }
      }

      return (java.lang.String[])(var7.toArray(new String[var7.size()]));
   }

   private static String[] collectFilesZIP(File var0, String[] var1, String[] var2) {
      ArrayList var6 = new ArrayList();
      String var7 = "assets/minecraft/";

      try {
         ZipFile var8 = new ZipFile(var0);
         Enumeration var9 = var8.entries();

         while (var9.hasMoreElements()) {
            ZipEntry var10 = (ZipEntry)var9.nextElement();
            String var11 = var10.getName();
            if (var11.startsWith(var7)) {
               var11 = var11.substring(var7.length());
               if (StrUtils.startsWith(var11, var1) && StrUtils.endsWith(var11, var2)) {
                  var6.add(var11);
               }
            }
         }

         var8.close();
         return (java.lang.String[])(var6.toArray(new String[var6.size()]));
      } catch (IOException var12) {
         var12.printStackTrace();
         return new String[0];
      }
   }
}
