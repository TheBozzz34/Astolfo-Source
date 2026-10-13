package net.minecraft.nbt;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;

public class CompressedStreamTools {
   private static final long a = 67467306098877L;

   public static NBTTagCompound readCompressed(InputStream var0) throws IOException {
      DataInputStream var1 = new DataInputStream(new BufferedInputStream(new GZIPInputStream(var0)));

      NBTTagCompound var2;
      try {
         var2 = read(var1, NBTSizeTracker.INFINITE);
      } finally {
         var1.close();
      }

      return var2;
   }

   public static void writeCompressed(NBTTagCompound var0, OutputStream var1) throws IOException {
      DataOutputStream var2 = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(var1)));

      try {
         write(var0, var2);
      } finally {
         var2.close();
      }
   }

   public static void safeWrite(NBTTagCompound var0, File var1) throws IOException {
      File var5 = new File(var1.getAbsolutePath() + "_tmp");
      if (var5.exists()) {
         var5.delete();
      }

      write(var0, var5);
      if (var1.exists()) {
         var1.delete();
      }

      if (var1.exists()) {
         throw new IOException("Failed to delete " + var1);
      }

      var5.renameTo(var1);
   }

   public static void write(NBTTagCompound var0, File var1) throws IOException {
      DataOutputStream var2 = new DataOutputStream(new FileOutputStream(var1));

      try {
         write(var0, var2);
      } finally {
         var2.close();
      }
   }

   public static NBTTagCompound read(File var0) throws IOException {
      if (!var0.exists()) {
         return null;
      }

      DataInputStream var3 = new DataInputStream(new FileInputStream(var0));

      NBTTagCompound var4;
      try {
         var4 = read(var3, NBTSizeTracker.INFINITE);
      } finally {
         var3.close();
      }

      return var4;
   }

   public static NBTTagCompound read(DataInputStream var0) throws IOException {
      return read(var0, NBTSizeTracker.INFINITE);
   }

   public static NBTTagCompound read(DataInput var0, NBTSizeTracker var1) throws IOException {
      NBTBase var5 = func_152455_a(var0, 0, var1);
      if (var5 instanceof NBTTagCompound) {
         return (NBTTagCompound)var5;
      } else {
         throw new IOException("Root tag must be a named compound tag");
      }
   }

   public static void write(NBTTagCompound var0, DataOutput var1) throws IOException {
      writeTag(var0, var1);
   }

   private static void writeTag(NBTBase var0, DataOutput var1) throws IOException {
      var1.writeByte(var0.getId());
      if (var0.getId() != 0) {
         var1.writeUTF("");
         var0.write(var1);
      }
   }

   private static NBTBase func_152455_a(DataInput var0, int var1, NBTSizeTracker var2) throws IOException {
      byte var6 = var0.readByte();
      if (var6 == 0) {
         return new NBTTagEnd();
      }

      var0.readUTF();
      NBTBase var7 = NBTBase.createNewByType(var6);

      try {
         var7.read(var0, var1, var2);
         return var7;
      } catch (IOException var11) {
         CrashReport var9 = CrashReport.makeCrashReport(var11, "Loading NBT data");
         CrashReportCategory var10 = var9.makeCategory("NBT Tag");
         var10.addCrashSection("Tag name", "[UNNAMED TAG]");
         var10.addCrashSection("Tag type", var6);
         throw new ReportedException(var9);
      }
   }
}
