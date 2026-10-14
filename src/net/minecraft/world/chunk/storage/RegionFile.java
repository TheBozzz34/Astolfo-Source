package net.minecraft.world.chunk.storage;

import com.google.common.collect.Lists;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;
import net.minecraft.server.MinecraftServer;

public class RegionFile {
   private static final byte[] emptySector = new byte[4096];
   private final File fileName;
   private RandomAccessFile dataFile;
   private final int[] offsets = new int[1024];
   private final int[] chunkTimestamps = new int[1024];
   private List<Boolean> sectorFree;
   private int sizeDelta;
   private long lastModified;

   public RegionFile(File fileNameIn) {
      this.fileName = fileNameIn;
      this.sizeDelta = 0;

      try {
         if (fileNameIn.exists()) {
            this.lastModified = fileNameIn.lastModified();
         }

         this.dataFile = new RandomAccessFile(fileNameIn, "rw");
         if (this.dataFile.length() < 4096L) {
            for (int i = 0; i < 1024; i++) {
               this.dataFile.writeInt(0);
            }

            for (int j = 0; j < 1024; j++) {
               this.dataFile.writeInt(0);
            }

            this.sizeDelta += 8192;
         }

         if ((this.dataFile.length() & 4095L) != 0L) {
            for (int k = 0; k < (this.dataFile.length() & 4095L); k++) {
               this.dataFile.write(0);
            }
         }

         int l = (int)this.dataFile.length() / 4096;
         this.sectorFree = Lists.newArrayListWithCapacity(l);

         for (int m = 0; m < l; m++) {
            this.sectorFree.add(true);
         }

         this.sectorFree.set(0, false);
         this.sectorFree.set(1, false);
         this.dataFile.seek(0L);

         for (int n = 0; n < 1024; n++) {
            int i2 = this.dataFile.readInt();
            this.offsets[n] = i2;
            if (i2 != 0 && (i2 >> 8) + (i2 & 0xFF) <= this.sectorFree.size()) {
               for (int i3 = 0; i3 < (i2 & 0xFF); i3++) {
                  this.sectorFree.set((i2 >> 8) + i3, false);
               }
            }
         }

         for (int i4 = 0; i4 < 1024; i4++) {
            int i5 = this.dataFile.readInt();
            this.chunkTimestamps[i4] = i5;
         }
      } catch (IOException ioException) {
         ioException.printStackTrace();
      }
   }

   public synchronized DataInputStream getChunkDataInputStream(int x, int z) {
      if (this.outOfBounds(x, z)) {
         return null;
      }

      try {
         int i = this.getOffset(x, z);
         if (i == 0) {
            return null;
         } else {
            int j = i >> 8;
            int k = i & 0xFF;
            if (j + k > this.sectorFree.size()) {
               return null;
            } else {
               this.dataFile.seek(j * 4096);
               int l = this.dataFile.readInt();
               if (l > 4096 * k) {
                  return null;
               } else if (l <= 0) {
                  return null;
               } else {
                  byte b = this.dataFile.readByte();
                  if (b == 1) {
                     byte[] bytes = new byte[l - 1];
                     this.dataFile.read(bytes);
                     return new DataInputStream(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(bytes))));
                  } else if (b == 2) {
                     byte[] bytes2 = new byte[l - 1];
                     this.dataFile.read(bytes2);
                     return new DataInputStream(new BufferedInputStream(new InflaterInputStream(new ByteArrayInputStream(bytes2))));
                  } else {
                     return null;
                  }
               }
            }
         }
      } catch (IOException ioException) {
         return null;
      }
   }

   public DataOutputStream getChunkDataOutputStream(int x, int z) {
      return this.outOfBounds(x, z) ? null : new DataOutputStream(new DeflaterOutputStream(new RegionFile.ChunkBuffer(x, z)));
   }

   public synchronized void write(int x, int z, byte[] data, int length) {
      try {
         int i = this.getOffset(x, z);
         int j = i >> 8;
         int k = i & 0xFF;
         int l = (length + 5) / 4096 + 1;
         if (l >= 256) {
            return;
         }

         if (j != 0 && k == l) {
            this.write(j, data, length);
         } else {
            for (int m = 0; m < k; m++) {
               this.sectorFree.set(j + m, true);
            }

            int n = this.sectorFree.indexOf(true);
            int i2 = 0;
            if (n != -1) {
               for (int i3 = n; i3 < this.sectorFree.size(); i3++) {
                  if (i2 != 0) {
                     if (this.sectorFree.get(i3)) {
                        i2++;
                     } else {
                        i2 = 0;
                     }
                  } else if (this.sectorFree.get(i3)) {
                     n = i3;
                     i2 = 1;
                  }

                  if (i2 >= l) {
                     break;
                  }
               }
            }

            if (i2 >= l) {
               j = n;
               this.setOffset(x, z, n << 8 | l);

               for (int i4 = 0; i4 < l; i4++) {
                  this.sectorFree.set(j + i4, false);
               }

               this.write(j, data, length);
            } else {
               this.dataFile.seek(this.dataFile.length());
               j = this.sectorFree.size();

               for (int i5 = 0; i5 < l; i5++) {
                  this.dataFile.write(emptySector);
                  this.sectorFree.add(false);
               }

               this.sizeDelta += 4096 * l;
               this.write(j, data, length);
               this.setOffset(x, z, j << 8 | l);
            }
         }

         this.setChunkTimestamp(x, z, (int)(MinecraftServer.getCurrentTimeMillis() / 1000L));
      } catch (IOException ioException) {
         ioException.printStackTrace();
      }
   }

   private void write(int sectorNumber, byte[] data, int length) throws IOException {
      this.dataFile.seek(sectorNumber * 4096);
      this.dataFile.writeInt(length + 1);
      this.dataFile.writeByte(2);
      this.dataFile.write(data, 0, length);
   }

   private boolean outOfBounds(int x, int z) {
      return x < 0 || x >= 32 || z < 0 || z >= 32;
   }

   private int getOffset(int x, int z) {
      return this.offsets[x + z * 32];
   }

   public boolean isChunkSaved(int x, int z) {
      return this.getOffset(x, z) != 0;
   }

   private void setOffset(int x, int z, int offset) throws IOException {
      this.offsets[x + z * 32] = offset;
      this.dataFile.seek((x + z * 32) * 4);
      this.dataFile.writeInt(offset);
   }

   private void setChunkTimestamp(int x, int z, int timestamp) throws IOException {
      this.chunkTimestamps[x + z * 32] = timestamp;
      this.dataFile.seek(4096 + (x + z * 32) * 4);
      this.dataFile.writeInt(timestamp);
   }

   public void close() throws IOException {
      if (this.dataFile != null) {
         this.dataFile.close();
      }
   }

   public class ChunkBuffer extends ByteArrayOutputStream {
      private int chunkX;
      private int chunkZ;

      public ChunkBuffer(int x, int z) {
         super(8096);
         this.chunkX = x;
         this.chunkZ = z;
      }

      @Override
      public void close() throws IOException {
         RegionFile.this.write(this.chunkX, this.chunkZ, this.buf, this.count);
      }
   }
}
