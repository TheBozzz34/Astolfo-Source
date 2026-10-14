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
   public static NBTTagCompound readCompressed(InputStream is) throws IOException {
      DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(new GZIPInputStream(is)));

      NBTTagCompound nbtTagCompound;
      try {
         nbtTagCompound = read(dataInputStream, NBTSizeTracker.INFINITE);
      } finally {
         dataInputStream.close();
      }

      return nbtTagCompound;
   }

   public static void writeCompressed(NBTTagCompound nbtTagCompound, OutputStream outputStream) throws IOException {
      DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(outputStream)));

      try {
         write(nbtTagCompound, dataOutputStream);
      } finally {
         dataOutputStream.close();
      }
   }

   public static void safeWrite(NBTTagCompound nbtTagCompound, File file) throws IOException {
      File file2 = new File(file.getAbsolutePath() + "_tmp");
      if (file2.exists()) {
         file2.delete();
      }

      write(nbtTagCompound, file2);
      if (file.exists()) {
         file.delete();
      }

      if (file.exists()) {
         throw new IOException("Failed to delete " + file);
      }

      file2.renameTo(file);
   }

   public static void write(NBTTagCompound nbtTagCompound, File file) throws IOException {
      DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file));

      try {
         write(nbtTagCompound, dataOutputStream);
      } finally {
         dataOutputStream.close();
      }
   }

   public static NBTTagCompound read(File file) throws IOException {
      if (!file.exists()) {
         return null;
      }

      DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));

      NBTTagCompound nbtTagCompound;
      try {
         nbtTagCompound = read(dataInputStream, NBTSizeTracker.INFINITE);
      } finally {
         dataInputStream.close();
      }

      return nbtTagCompound;
   }

   public static NBTTagCompound read(DataInputStream inputStream) throws IOException {
      return read(inputStream, NBTSizeTracker.INFINITE);
   }

   public static NBTTagCompound read(DataInput dataInput, NBTSizeTracker nbtSizeTracker) throws IOException {
      NBTBase nbtBase = func_152455_a(dataInput, 0, nbtSizeTracker);
      if (nbtBase instanceof NBTTagCompound) {
         return (NBTTagCompound)nbtBase;
      } else {
         throw new IOException("Root tag must be a named compound tag");
      }
   }

   public static void write(NBTTagCompound nbtTagCompound, DataOutput dataOutput) throws IOException {
      writeTag(nbtTagCompound, dataOutput);
   }

   private static void writeTag(NBTBase nbtBase, DataOutput dataOutput) throws IOException {
      dataOutput.writeByte(nbtBase.getId());
      if (nbtBase.getId() != 0) {
         dataOutput.writeUTF("");
         nbtBase.write(dataOutput);
      }
   }

   private static NBTBase func_152455_a(DataInput dataInput, int i, NBTSizeTracker nbtSizeTracker) throws IOException {
      byte b = dataInput.readByte();
      if (b == 0) {
         return new NBTTagEnd();
      }

      dataInput.readUTF();
      NBTBase nbtBase = NBTBase.createNewByType(b);

      try {
         nbtBase.read(dataInput, i, nbtSizeTracker);
         return nbtBase;
      } catch (IOException ioException) {
         CrashReport crashReport = CrashReport.makeCrashReport(ioException, "Loading NBT data");
         CrashReportCategory crashReportCategory = crashReport.makeCategory("NBT Tag");
         crashReportCategory.addCrashSection("Tag name", "[UNNAMED TAG]");
         crashReportCategory.addCrashSection("Tag type", b);
         throw new ReportedException(crashReport);
      }
   }
}
