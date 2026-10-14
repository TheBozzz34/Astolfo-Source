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
   public static String[] collectFiles(String prefix, String suffix) {
      return collectFiles(new String[]{prefix}, new String[]{suffix});
   }

   public static String[] collectFiles(String[] prefixes, String[] suffixes) {
      LinkedHashSet linkedHashSet = new LinkedHashSet();
      IResourcePack[] iResourcePacks = Config.getResourcePacks();

      for (int i = 0; i < iResourcePacks.length; i++) {
         IResourcePack iResourcePack = iResourcePacks[i];
         String[] strings = collectFiles(iResourcePack, prefixes, suffixes, (String[])null);
         linkedHashSet.addAll(Arrays.asList(strings));
      }

      return (java.lang.String[])(linkedHashSet.toArray(new String[linkedHashSet.size()]));
   }

   public static String[] collectFiles(IResourcePack rp, String prefix, String suffix, String[] defaultPaths) {
      return collectFiles(rp, new String[]{prefix}, new String[]{suffix}, defaultPaths);
   }

   public static String[] collectFiles(IResourcePack rp, String[] prefixes, String[] suffixes) {
      return collectFiles(rp, prefixes, suffixes, (String[])null);
   }

   public static String[] collectFiles(IResourcePack rp, String[] prefixes, String[] suffixes, String[] defaultPaths) {
      if (rp instanceof DefaultResourcePack) {
         return collectFilesFixed(rp, defaultPaths);
      }

      if (!(rp instanceof AbstractResourcePack)) {
         return new String[0];
      }

      AbstractResourcePack abstractResourcePack = (AbstractResourcePack)rp;
      File file = abstractResourcePack.resourcePackFile;
      return file == null
         ? new String[0]
         : (file.isDirectory() ? collectFilesFolder(file, "", prefixes, suffixes) : (file.isFile() ? collectFilesZIP(file, prefixes, suffixes) : new String[0]));
   }

   private static String[] collectFilesFixed(IResourcePack rp, String[] paths) {
      if (paths == null) {
         return new String[0];
      }

      ArrayList arrayList = new ArrayList();

      for (int i = 0; i < paths.length; i++) {
         String string = paths[i];
         ResourceLocation resourceLocation = new ResourceLocation(string);
         if (rp.resourceExists(resourceLocation)) {
            arrayList.add(string);
         }
      }

      return (java.lang.String[])(arrayList.toArray(new String[arrayList.size()]));
   }

   private static String[] collectFilesFolder(File tpFile, String basePath, String[] prefixes, String[] suffixes) {
      ArrayList arrayList = new ArrayList();
      String string = "assets/minecraft/";
      File[] files = tpFile.listFiles();
      if (files == null) {
         return new String[0];
      }

      for (int i = 0; i < files.length; i++) {
         File file = files[i];
         if (file.isFile()) {
            String string2 = basePath + file.getName();
            if (string2.startsWith(string)) {
               string2 = string2.substring(string.length());
               if (StrUtils.startsWith(string2, prefixes) && StrUtils.endsWith(string2, suffixes)) {
                  arrayList.add(string2);
               }
            }
         } else if (file.isDirectory()) {
            String string3 = basePath + file.getName() + "/";
            String[] strings = collectFilesFolder(file, string3, prefixes, suffixes);

            for (int j = 0; j < strings.length; j++) {
               String string4 = strings[j];
               arrayList.add(string4);
            }
         }
      }

      return (java.lang.String[])(arrayList.toArray(new String[arrayList.size()]));
   }

   private static String[] collectFilesZIP(File tpFile, String[] prefixes, String[] suffixes) {
      ArrayList arrayList = new ArrayList();
      String string = "assets/minecraft/";

      try {
         ZipFile zipFile = new ZipFile(tpFile);
         Enumeration enumeration = zipFile.entries();

         while (enumeration.hasMoreElements()) {
            ZipEntry zipEntry = (ZipEntry)enumeration.nextElement();
            String string2 = zipEntry.getName();
            if (string2.startsWith(string)) {
               string2 = string2.substring(string.length());
               if (StrUtils.startsWith(string2, prefixes) && StrUtils.endsWith(string2, suffixes)) {
                  arrayList.add(string2);
               }
            }
         }

         zipFile.close();
         return (java.lang.String[])(arrayList.toArray(new String[arrayList.size()]));
      } catch (IOException ioException) {
         ioException.printStackTrace();
         return new String[0];
      }
   }
}
