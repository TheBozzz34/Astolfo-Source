package net.minecraft.world.chunk.storage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.storage.IThreadedFileIO;
import net.minecraft.world.storage.ThreadedFileIOBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AnvilChunkLoader implements IChunkLoader, IThreadedFileIO {
   private static final Logger logger = LogManager.getLogger();
   private Map<ChunkCoordIntPair, NBTTagCompound> chunksToRemove = new ConcurrentHashMap<>();
   private Set<ChunkCoordIntPair> pendingAnvilChunksCoordinates = Collections.newSetFromMap(new ConcurrentHashMap<>());
   private final File chunkSaveLocation;
   private boolean field_183014_e = false;
   private static final long a = 126365843584747L;

   public AnvilChunkLoader(File var1) {
      this.chunkSaveLocation = var1;
   }

   @Override
   public Chunk loadChunk(World var1, int var2, int var3) throws IOException {
      ChunkCoordIntPair var7 = new ChunkCoordIntPair(var2, var3);
      NBTTagCompound var8 = this.chunksToRemove.get(var7);
      if (var8 == null) {
         DataInputStream var9 = RegionFileCache.getChunkInputStream(this.chunkSaveLocation, var2, var3);
         if (var9 == null) {
            return null;
         }

         var8 = CompressedStreamTools.read(var9);
      }

      return this.checkedReadChunkFromNBT(var1, var2, var3, var8);
   }

   public Chunk checkedReadChunkFromNBT(World var1, int var2, int var3, NBTTagCompound var4) {
      if (!var4.hasKey("Level", 10)) {
         logger.error("Chunk file at " + var2 + "," + var3 + " is missing level data, skipping");
         return null;
      }

      NBTTagCompound var8 = var4.getCompoundTag("Level");
      if (!var8.hasKey("Sections", 9)) {
         logger.error("Chunk file at " + var2 + "," + var3 + " is missing block data, skipping");
         return null;
      }

      Chunk var9 = this.readChunkFromNBT(var1, var8);
      if (!var9.isAtLocation(var2, var3)) {
         logger.error(
            "Chunk file at "
               + var2
               + ","
               + var3
               + " is in the wrong location; relocating. (Expected "
               + var2
               + ", "
               + var3
               + ", got "
               + var9.xPosition
               + ", "
               + var9.zPosition
               + ")"
         );
         var8.setInteger("xPos", var2);
         var8.setInteger("zPos", var3);
         var9 = this.readChunkFromNBT(var1, var8);
      }

      return var9;
   }

   @Override
   public void saveChunk(World var1, Chunk var2) throws MinecraftException, IOException {
      var1.checkSessionLock();

      try {
         NBTTagCompound var5 = new NBTTagCompound();
         NBTTagCompound var6 = new NBTTagCompound();
         var5.setTag("Level", var6);
         this.writeChunkToNBT(var2, var1, var6);
         this.addChunkToPending(var2.getChunkCoordIntPair(), var5);
      } catch (Exception var7) {
         logger.error("Failed to save chunk", var7);
      }
   }

   public void addChunkToPending(ChunkCoordIntPair var1, NBTTagCompound var2) {
      if (!this.pendingAnvilChunksCoordinates.contains(var1)) {
         this.chunksToRemove.put(var1, var2);
      }

      ThreadedFileIOBase.getThreadedIOInstance().queueIO(this);
   }

   @Override
   public boolean writeNextIO() {
      if (this.chunksToRemove.isEmpty()) {
         if (this.field_183014_e) {
            logger.info("ThreadedAnvilChunkStorage ({}): All chunks are saved", new Object[]{this.chunkSaveLocation.getName()});
         }

         return false;
      } else {
         ChunkCoordIntPair var4 = this.chunksToRemove.keySet().iterator().next();

         boolean var5;
         try {
            this.pendingAnvilChunksCoordinates.add(var4);
            NBTTagCompound var6 = this.chunksToRemove.remove(var4);
            if (var6 != null) {
               try {
                  this.func_183013_b(var4, var6);
               } catch (Exception var11) {
                  logger.error("Failed to save chunk", var11);
               }
            }

            var5 = true;
         } finally {
            this.pendingAnvilChunksCoordinates.remove(var4);
         }

         return var5;
      }
   }

   private void func_183013_b(ChunkCoordIntPair var1, NBTTagCompound var2) throws IOException {
      DataOutputStream var3 = RegionFileCache.getChunkOutputStream(this.chunkSaveLocation, var1.chunkXPos, var1.chunkZPos);
      CompressedStreamTools.write(var2, var3);
      var3.close();
   }

   @Override
   public void saveExtraChunkData(World var1, Chunk var2) throws IOException {
   }

   @Override
   public void chunkTick() {
   }

   @Override
   public void saveExtraData() {
      try {
         this.field_183014_e = true;

         while (true) {
            if (this.writeNextIO()) {
            }
         }
      } finally {
         this.field_183014_e = false;
      }
   }

   private void writeChunkToNBT(Chunk var1, World var2, NBTTagCompound var3) {
      var3.setByte("V", (byte)1);
      var3.setInteger("xPos", var1.xPosition);
      var3.setInteger("zPos", var1.zPosition);
      var3.setLong("LastUpdate", var2.getTotalWorldTime());
      var3.setIntArray("HeightMap", var1.getHeightMap());
      var3.setBoolean("TerrainPopulated", var1.isTerrainPopulated());
      var3.setBoolean("LightPopulated", var1.isLightPopulated());
      var3.setLong("InhabitedTime", var1.getInhabitedTime());
      ExtendedBlockStorage[] var7 = var1.getBlockStorageArray();
      NBTTagList var8 = new NBTTagList();
      boolean var9 = !var2.provider.getHasNoSky();

      for (ExtendedBlockStorage var13 : var7) {
         if (var13 != null) {
            NBTTagCompound var14 = new NBTTagCompound();
            var14.setByte("Y", (byte)(var13.getYLocation() >> 4 & 0xFF));
            byte[] var15 = new byte[var13.getData().length];
            NibbleArray var16 = new NibbleArray();
            NibbleArray var17 = null;

            for (int var18 = 0; var18 < var13.getData().length; var18++) {
               char var19 = var13.getData()[var18];
               int var20 = var18 & 15;
               int var21 = var18 >> 8 & 15;
               int var22 = var18 >> 4 & 15;
               if (var19 >> '\f' != 0) {
                  if (var17 == null) {
                     var17 = new NibbleArray();
                  }

                  var17.set(var20, var21, var22, var19 >> '\f');
               }

               var15[var18] = (byte)(var19 >> 4 & 0xFF);
               var16.set(var20, var21, var22, var19 & 15);
            }

            var14.setByteArray("Blocks", var15);
            var14.setByteArray("Data", var16.getData());
            if (var17 != null) {
               var14.setByteArray("Add", var17.getData());
            }

            var14.setByteArray("BlockLight", var13.getBlocklightArray().getData());
            if (var9) {
               var14.setByteArray("SkyLight", var13.getSkylightArray().getData());
            } else {
               var14.setByteArray("SkyLight", new byte[var13.getBlocklightArray().getData().length]);
            }

            var8.appendTag(var14);
         }
      }

      var3.setTag("Sections", var8);
      var3.setByteArray("Biomes", var1.getBiomeArray());
      var1.setHasEntities(false);
      NBTTagList var23 = new NBTTagList();

      for (int var24 = 0; var24 < var1.getEntityLists().length; var24++) {
         for (Entity var29 : var1.getEntityLists()[var24]) {
            NBTTagCompound var32 = new NBTTagCompound();
            if (var29.writeToNBTOptional(var32)) {
               var1.setHasEntities(true);
               var23.appendTag(var32);
            }
         }
      }

      var3.setTag("Entities", var23);
      NBTTagList var25 = new NBTTagList();

      for (TileEntity var30 : var1.getTileEntityMap().values()) {
         NBTTagCompound var33 = new NBTTagCompound();
         var30.writeToNBT(var33);
         var25.appendTag(var33);
      }

      var3.setTag("TileEntities", var25);
      List var28 = var2.getPendingBlockUpdates(var1, false);
      if (var28 != null) {
         long var31 = var2.getTotalWorldTime();
         NBTTagList var34 = new NBTTagList();

         for (Object var36$raw : var28) {
            NextTickListEntry var36 = (NextTickListEntry)var36$raw;
            NBTTagCompound var37 = new NBTTagCompound();
            ResourceLocation var38 = Block.blockRegistry.getNameForObject(var36.getBlock());
            var37.setString("i", var38 == null ? "" : var38.toString());
            var37.setInteger("x", var36.position.getX());
            var37.setInteger("y", var36.position.getY());
            var37.setInteger("z", var36.position.getZ());
            var37.setInteger("t", (int)(var36.scheduledTime - var31));
            var37.setInteger("p", var36.priority);
            var34.appendTag(var37);
         }

         var3.setTag("TileTicks", var34);
      }
   }

   private Chunk readChunkFromNBT(World var1, NBTTagCompound var2) {
      int var6 = var2.getInteger("xPos");
      int var7 = var2.getInteger("zPos");
      Chunk var8 = new Chunk(var1, var6, var7);
      var8.setHeightMap(var2.getIntArray("HeightMap"));
      var8.setTerrainPopulated(var2.getBoolean("TerrainPopulated"));
      var8.setLightPopulated(var2.getBoolean("LightPopulated"));
      var8.setInhabitedTime(var2.getLong("InhabitedTime"));
      NBTTagList var9 = var2.getTagList("Sections", 10);
      byte var10 = 16;
      ExtendedBlockStorage[] var11 = new ExtendedBlockStorage[var10];
      boolean var12 = !var1.provider.getHasNoSky();

      for (int var13 = 0; var13 < var9.tagCount(); var13++) {
         NBTTagCompound var14 = var9.getCompoundTagAt(var13);
         byte var15 = var14.getByte("Y");
         ExtendedBlockStorage var16 = new ExtendedBlockStorage(var15 << 4, var12);
         byte[] var17 = var14.getByteArray("Blocks");
         NibbleArray var18 = new NibbleArray(var14.getByteArray("Data"));
         NibbleArray var19 = var14.hasKey("Add", 7) ? new NibbleArray(var14.getByteArray("Add")) : null;
         char[] var20 = new char[var17.length];

         for (int var21 = 0; var21 < var20.length; var21++) {
            int var22 = var21 & 15;
            int var23 = var21 >> 8 & 15;
            int var24 = var21 >> 4 & 15;
            int var25 = var19 != null ? var19.get(var22, var23, var24) : 0;
            var20[var21] = (char)(var25 << 12 | (var17[var21] & 255) << 4 | var18.get(var22, var23, var24));
         }

         var16.setData(var20);
         var16.setBlocklightArray(new NibbleArray(var14.getByteArray("BlockLight")));
         if (var12) {
            var16.setSkylightArray(new NibbleArray(var14.getByteArray("SkyLight")));
         }

         var16.removeInvalidBlocks();
         var11[var15] = var16;
      }

      var8.setStorageArrays(var11);
      if (var2.hasKey("Biomes", 7)) {
         var8.setBiomeArray(var2.getByteArray("Biomes"));
      }

      NBTTagList var26 = var2.getTagList("Entities", 10);
      if (var26 != null) {
         for (int var27 = 0; var27 < var26.tagCount(); var27++) {
            NBTTagCompound var29 = var26.getCompoundTagAt(var27);
            Entity var32 = EntityList.createEntityFromNBT(var29, var1);
            var8.setHasEntities(true);
            if (var32 != null) {
               var8.addEntity(var32);
               Entity var35 = var32;

               for (NBTTagCompound var38 = var29; var38.hasKey("Riding", 10); var38 = var38.getCompoundTag("Riding")) {
                  Entity var40 = EntityList.createEntityFromNBT(var38.getCompoundTag("Riding"), var1);
                  if (var40 != null) {
                     var8.addEntity(var40);
                     var35.mountEntity(var40);
                  }

                  var35 = var40;
               }
            }
         }
      }

      NBTTagList var28 = var2.getTagList("TileEntities", 10);
      if (var28 != null) {
         for (int var30 = 0; var30 < var28.tagCount(); var30++) {
            NBTTagCompound var33 = var28.getCompoundTagAt(var30);
            TileEntity var36 = TileEntity.createAndLoadEntity(var33);
            if (var36 != null) {
               var8.addTileEntity(var36);
            }
         }
      }

      if (var2.hasKey("TileTicks", 9)) {
         NBTTagList var31 = var2.getTagList("TileTicks", 10);
         if (var31 != null) {
            for (int var34 = 0; var34 < var31.tagCount(); var34++) {
               NBTTagCompound var37 = var31.getCompoundTagAt(var34);
               Block var39;
               if (var37.hasKey("i", 8)) {
                  var39 = Block.getBlockFromName(var37.getString("i"));
               } else {
                  var39 = Block.getBlockById(var37.getInteger("i"));
               }

               var1.scheduleBlockUpdate(
                  new BlockPos(var37.getInteger("x"), var37.getInteger("y"), var37.getInteger("z")), var39, var37.getInteger("t"), var37.getInteger("p")
               );
            }
         }
      }

      return var8;
   }
}
