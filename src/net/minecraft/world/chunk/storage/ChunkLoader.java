package net.minecraft.world.chunk.storage;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.NibbleArray;

public class ChunkLoader {
   private static final long a = 111326364152383L;

   public static ChunkLoader.AnvilConverterData load(NBTTagCompound var0) {
      int var3 = var0.getInteger("xPos");
      int var4 = var0.getInteger("zPos");
      ChunkLoader.AnvilConverterData var5 = new ChunkLoader.AnvilConverterData(var3, var4);
      var5.blocks = var0.getByteArray("Blocks");
      var5.data = new NibbleArrayReader(var0.getByteArray("Data"), 7);
      var5.skyLight = new NibbleArrayReader(var0.getByteArray("SkyLight"), 7);
      var5.blockLight = new NibbleArrayReader(var0.getByteArray("BlockLight"), 7);
      var5.heightmap = var0.getByteArray("HeightMap");
      var5.terrainPopulated = var0.getBoolean("TerrainPopulated");
      var5.entities = var0.getTagList("Entities", 10);
      var5.tileEntities = var0.getTagList("TileEntities", 10);
      var5.tileTicks = var0.getTagList("TileTicks", 10);

      try {
         var5.lastUpdated = var0.getLong("LastUpdate");
      } catch (ClassCastException var7) {
         var5.lastUpdated = var0.getInteger("LastUpdate");
      }

      return var5;
   }

   public static void convertToAnvilFormat(ChunkLoader.AnvilConverterData var0, NBTTagCompound var1, WorldChunkManager var2) {
      var1.setInteger("xPos", var0.x);
      var1.setInteger("zPos", var0.z);
      var1.setLong("LastUpdate", var0.lastUpdated);
      int[] var6 = new int[var0.heightmap.length];

      for (int var7 = 0; var7 < var0.heightmap.length; var7++) {
         var6[var7] = var0.heightmap[var7];
      }

      var1.setIntArray("HeightMap", var6);
      var1.setBoolean("TerrainPopulated", var0.terrainPopulated);
      NBTTagList var19 = new NBTTagList();

      for (int var8 = 0; var8 < 8; var8++) {
         boolean var9 = true;

         for (int var10 = 0; var10 < 16 && var9; var10++) {
            for (int var11 = 0; var11 < 16 && var9; var11++) {
               for (int var12 = 0; var12 < 16; var12++) {
                  int var13 = var10 << 11 | var12 << 7 | var11 + (var8 << 4);
                  byte var14 = var0.blocks[var13];
                  if (var14 != 0) {
                     var9 = false;
                     break;
                  }
               }
            }
         }

         if (!var9) {
            byte[] var22 = new byte[4096];
            NibbleArray var24 = new NibbleArray();
            NibbleArray var26 = new NibbleArray();
            NibbleArray var27 = new NibbleArray();

            for (int var28 = 0; var28 < 16; var28++) {
               for (int var15 = 0; var15 < 16; var15++) {
                  for (int var16 = 0; var16 < 16; var16++) {
                     int var17 = var28 << 11 | var16 << 7 | var15 + (var8 << 4);
                     byte var18 = var0.blocks[var17];
                     var22[var15 << 8 | var16 << 4 | var28] = (byte)(var18 & 0xFF);
                     var24.set(var28, var15, var16, var0.data.get(var28, var15 + (var8 << 4), var16));
                     var26.set(var28, var15, var16, var0.skyLight.get(var28, var15 + (var8 << 4), var16));
                     var27.set(var28, var15, var16, var0.blockLight.get(var28, var15 + (var8 << 4), var16));
                  }
               }
            }

            NBTTagCompound var29 = new NBTTagCompound();
            var29.setByte("Y", (byte)(var8 & 0xFF));
            var29.setByteArray("Blocks", var22);
            var29.setByteArray("Data", var24.getData());
            var29.setByteArray("SkyLight", var26.getData());
            var29.setByteArray("BlockLight", var27.getData());
            var19.appendTag(var29);
         }
      }

      var1.setTag("Sections", var19);
      byte[] var20 = new byte[256];
      BlockPos.MutableBlockPos var21 = new BlockPos.MutableBlockPos();

      for (int var23 = 0; var23 < 16; var23++) {
         for (int var25 = 0; var25 < 16; var25++) {
            var21.set(var0.x << 4 | var23, 0, var0.z << 4 | var25);
            var20[var25 << 4 | var23] = (byte)(var2.getBiomeGenerator(var21, BiomeGenBase.field_180279_ad).biomeID & 0xFF);
         }
      }

      var1.setByteArray("Biomes", var20);
      var1.setTag("Entities", var0.entities);
      var1.setTag("TileEntities", var0.tileEntities);
      if (var0.tileTicks != null) {
         var1.setTag("TileTicks", var0.tileTicks);
      }
   }

   public static class AnvilConverterData {
      public long lastUpdated;
      public boolean terrainPopulated;
      public byte[] heightmap;
      public NibbleArrayReader blockLight;
      public NibbleArrayReader skyLight;
      public NibbleArrayReader data;
      public byte[] blocks;
      public NBTTagList entities;
      public NBTTagList tileEntities;
      public NBTTagList tileTicks;
      public final int x;
      public final int z;

      public AnvilConverterData(int var1, int var2) {
         this.x = var1;
         this.z = var2;
      }
   }
}
