package net.minecraft.world.chunk.storage;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.NibbleArray;

public class ChunkLoader {
   public static ChunkLoader.AnvilConverterData load(NBTTagCompound nbt) {
      int i = nbt.getInteger("xPos");
      int j = nbt.getInteger("zPos");
      ChunkLoader.AnvilConverterData anvilConverterData = new ChunkLoader.AnvilConverterData(i, j);
      anvilConverterData.blocks = nbt.getByteArray("Blocks");
      anvilConverterData.data = new NibbleArrayReader(nbt.getByteArray("Data"), 7);
      anvilConverterData.skyLight = new NibbleArrayReader(nbt.getByteArray("SkyLight"), 7);
      anvilConverterData.blockLight = new NibbleArrayReader(nbt.getByteArray("BlockLight"), 7);
      anvilConverterData.heightmap = nbt.getByteArray("HeightMap");
      anvilConverterData.terrainPopulated = nbt.getBoolean("TerrainPopulated");
      anvilConverterData.entities = nbt.getTagList("Entities", 10);
      anvilConverterData.tileEntities = nbt.getTagList("TileEntities", 10);
      anvilConverterData.tileTicks = nbt.getTagList("TileTicks", 10);

      try {
         anvilConverterData.lastUpdated = nbt.getLong("LastUpdate");
      } catch (ClassCastException classCastException) {
         anvilConverterData.lastUpdated = nbt.getInteger("LastUpdate");
      }

      return anvilConverterData;
   }

   public static void convertToAnvilFormat(ChunkLoader.AnvilConverterData anvilConverterData, NBTTagCompound compound, WorldChunkManager worldChunkManager) {
      compound.setInteger("xPos", anvilConverterData.x);
      compound.setInteger("zPos", anvilConverterData.z);
      compound.setLong("LastUpdate", anvilConverterData.lastUpdated);
      int[] ints = new int[anvilConverterData.heightmap.length];

      for (int i = 0; i < anvilConverterData.heightmap.length; i++) {
         ints[i] = anvilConverterData.heightmap[i];
      }

      compound.setIntArray("HeightMap", ints);
      compound.setBoolean("TerrainPopulated", anvilConverterData.terrainPopulated);
      NBTTagList nbtTagList = new NBTTagList();

      for (int j = 0; j < 8; j++) {
         boolean bl = true;

         for (int k = 0; k < 16 && bl; k++) {
            for (int l = 0; l < 16 && bl; l++) {
               for (int m = 0; m < 16; m++) {
                  int n = k << 11 | m << 7 | l + (j << 4);
                  byte b = anvilConverterData.blocks[n];
                  if (b != 0) {
                     bl = false;
                     break;
                  }
               }
            }
         }

         if (!bl) {
            byte[] bytes = new byte[4096];
            NibbleArray nibbleArray = new NibbleArray();
            NibbleArray nibbleArray2 = new NibbleArray();
            NibbleArray nibbleArray3 = new NibbleArray();

            for (int i2 = 0; i2 < 16; i2++) {
               for (int i3 = 0; i3 < 16; i3++) {
                  for (int i4 = 0; i4 < 16; i4++) {
                     int i5 = i2 << 11 | i4 << 7 | i3 + (j << 4);
                     byte b2 = anvilConverterData.blocks[i5];
                     bytes[i3 << 8 | i4 << 4 | i2] = (byte)(b2 & 0xFF);
                     nibbleArray.set(i2, i3, i4, anvilConverterData.data.get(i2, i3 + (j << 4), i4));
                     nibbleArray2.set(i2, i3, i4, anvilConverterData.skyLight.get(i2, i3 + (j << 4), i4));
                     nibbleArray3.set(i2, i3, i4, anvilConverterData.blockLight.get(i2, i3 + (j << 4), i4));
                  }
               }
            }

            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            nbtTagCompound.setByte("Y", (byte)(j & 0xFF));
            nbtTagCompound.setByteArray("Blocks", bytes);
            nbtTagCompound.setByteArray("Data", nibbleArray.getData());
            nbtTagCompound.setByteArray("SkyLight", nibbleArray2.getData());
            nbtTagCompound.setByteArray("BlockLight", nibbleArray3.getData());
            nbtTagList.appendTag(nbtTagCompound);
         }
      }

      compound.setTag("Sections", nbtTagList);
      byte[] bytes2 = new byte[256];
      BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

      for (int i6 = 0; i6 < 16; i6++) {
         for (int i7 = 0; i7 < 16; i7++) {
            mutableBlockPos.set(anvilConverterData.x << 4 | i6, 0, anvilConverterData.z << 4 | i7);
            bytes2[i7 << 4 | i6] = (byte)(worldChunkManager.getBiomeGenerator(mutableBlockPos, BiomeGenBase.field_180279_ad).biomeID & 0xFF);
         }
      }

      compound.setByteArray("Biomes", bytes2);
      compound.setTag("Entities", anvilConverterData.entities);
      compound.setTag("TileEntities", anvilConverterData.tileEntities);
      if (anvilConverterData.tileTicks != null) {
         compound.setTag("TileTicks", anvilConverterData.tileTicks);
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

      public AnvilConverterData(int xIn, int zIn) {
         this.x = xIn;
         this.z = zIn;
      }
   }
}
