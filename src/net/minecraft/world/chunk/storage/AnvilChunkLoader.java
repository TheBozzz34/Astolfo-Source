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

   public AnvilChunkLoader(File chunkSaveLocationIn) {
      this.chunkSaveLocation = chunkSaveLocationIn;
   }

   @Override
   public Chunk loadChunk(World worldIn, int x, int z) throws IOException {
      ChunkCoordIntPair chunkCoordIntPair = new ChunkCoordIntPair(x, z);
      NBTTagCompound nbtTagCompound = this.chunksToRemove.get(chunkCoordIntPair);
      if (nbtTagCompound == null) {
         DataInputStream dataInputStream = RegionFileCache.getChunkInputStream(this.chunkSaveLocation, x, z);
         if (dataInputStream == null) {
            return null;
         }

         nbtTagCompound = CompressedStreamTools.read(dataInputStream);
      }

      return this.checkedReadChunkFromNBT(worldIn, x, z, nbtTagCompound);
   }

   public Chunk checkedReadChunkFromNBT(World worldIn, int x, int z, NBTTagCompound nbtTagCompound) {
      if (!nbtTagCompound.hasKey("Level", 10)) {
         logger.error("Chunk file at " + x + "," + z + " is missing level data, skipping");
         return null;
      }

      NBTTagCompound nbtTagCompound2 = nbtTagCompound.getCompoundTag("Level");
      if (!nbtTagCompound2.hasKey("Sections", 9)) {
         logger.error("Chunk file at " + x + "," + z + " is missing block data, skipping");
         return null;
      }

      Chunk chunk = this.readChunkFromNBT(worldIn, nbtTagCompound2);
      if (!chunk.isAtLocation(x, z)) {
         logger.error(
            "Chunk file at "
               + x
               + ","
               + z
               + " is in the wrong location; relocating. (Expected "
               + x
               + ", "
               + z
               + ", got "
               + chunk.xPosition
               + ", "
               + chunk.zPosition
               + ")"
         );
         nbtTagCompound2.setInteger("xPos", x);
         nbtTagCompound2.setInteger("zPos", z);
         chunk = this.readChunkFromNBT(worldIn, nbtTagCompound2);
      }

      return chunk;
   }

   @Override
   public void saveChunk(World worldIn, Chunk chunkIn) throws MinecraftException, IOException {
      worldIn.checkSessionLock();

      try {
         NBTTagCompound nbtTagCompound = new NBTTagCompound();
         NBTTagCompound nbtTagCompound2 = new NBTTagCompound();
         nbtTagCompound.setTag("Level", nbtTagCompound2);
         this.writeChunkToNBT(chunkIn, worldIn, nbtTagCompound2);
         this.addChunkToPending(chunkIn.getChunkCoordIntPair(), nbtTagCompound);
      } catch (Exception exception) {
         logger.error("Failed to save chunk", exception);
      }
   }

   public void addChunkToPending(ChunkCoordIntPair chunkCoordIntPair, NBTTagCompound nbtTagCompound) {
      if (!this.pendingAnvilChunksCoordinates.contains(chunkCoordIntPair)) {
         this.chunksToRemove.put(chunkCoordIntPair, nbtTagCompound);
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
         ChunkCoordIntPair chunkCoordIntPair = this.chunksToRemove.keySet().iterator().next();

         boolean bl;
         try {
            this.pendingAnvilChunksCoordinates.add(chunkCoordIntPair);
            NBTTagCompound nbtTagCompound = this.chunksToRemove.remove(chunkCoordIntPair);
            if (nbtTagCompound != null) {
               try {
                  this.func_183013_b(chunkCoordIntPair, nbtTagCompound);
               } catch (Exception exception) {
                  logger.error("Failed to save chunk", exception);
               }
            }

            bl = true;
         } finally {
            this.pendingAnvilChunksCoordinates.remove(chunkCoordIntPair);
         }

         return bl;
      }
   }

   private void func_183013_b(ChunkCoordIntPair chunkCoordIntPair, NBTTagCompound nbtTagCompound) throws IOException {
      DataOutputStream dataOutputStream = RegionFileCache.getChunkOutputStream(this.chunkSaveLocation, chunkCoordIntPair.chunkXPos, chunkCoordIntPair.chunkZPos);
      CompressedStreamTools.write(nbtTagCompound, dataOutputStream);
      dataOutputStream.close();
   }

   @Override
   public void saveExtraChunkData(World worldIn, Chunk chunkIn) throws IOException {
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

   private void writeChunkToNBT(Chunk chunkIn, World worldIn, NBTTagCompound nbtTagCompound) {
      nbtTagCompound.setByte("V", (byte)1);
      nbtTagCompound.setInteger("xPos", chunkIn.xPosition);
      nbtTagCompound.setInteger("zPos", chunkIn.zPosition);
      nbtTagCompound.setLong("LastUpdate", worldIn.getTotalWorldTime());
      nbtTagCompound.setIntArray("HeightMap", chunkIn.getHeightMap());
      nbtTagCompound.setBoolean("TerrainPopulated", chunkIn.isTerrainPopulated());
      nbtTagCompound.setBoolean("LightPopulated", chunkIn.isLightPopulated());
      nbtTagCompound.setLong("InhabitedTime", chunkIn.getInhabitedTime());
      ExtendedBlockStorage[] extendedBlockStorages = chunkIn.getBlockStorageArray();
      NBTTagList nbtTagList = new NBTTagList();
      boolean bl = !worldIn.provider.getHasNoSky();

      for (ExtendedBlockStorage extendedBlockStorage : extendedBlockStorages) {
         if (extendedBlockStorage != null) {
            NBTTagCompound nbtTagCompound2 = new NBTTagCompound();
            nbtTagCompound2.setByte("Y", (byte)(extendedBlockStorage.getYLocation() >> 4 & 0xFF));
            byte[] bytes = new byte[extendedBlockStorage.getData().length];
            NibbleArray nibbleArray = new NibbleArray();
            NibbleArray nibbleArray2 = null;

            for (int j = 0; j < extendedBlockStorage.getData().length; j++) {
               char c = extendedBlockStorage.getData()[j];
               int k = j & 15;
               int l = j >> 8 & 15;
               int m = j >> 4 & 15;
               if (c >> '\f' != 0) {
                  if (nibbleArray2 == null) {
                     nibbleArray2 = new NibbleArray();
                  }

                  nibbleArray2.set(k, l, m, c >> '\f');
               }

               bytes[j] = (byte)(c >> 4 & 0xFF);
               nibbleArray.set(k, l, m, c & 15);
            }

            nbtTagCompound2.setByteArray("Blocks", bytes);
            nbtTagCompound2.setByteArray("Data", nibbleArray.getData());
            if (nibbleArray2 != null) {
               nbtTagCompound2.setByteArray("Add", nibbleArray2.getData());
            }

            nbtTagCompound2.setByteArray("BlockLight", extendedBlockStorage.getBlocklightArray().getData());
            if (bl) {
               nbtTagCompound2.setByteArray("SkyLight", extendedBlockStorage.getSkylightArray().getData());
            } else {
               nbtTagCompound2.setByteArray("SkyLight", new byte[extendedBlockStorage.getBlocklightArray().getData().length]);
            }

            nbtTagList.appendTag(nbtTagCompound2);
         }
      }

      nbtTagCompound.setTag("Sections", nbtTagList);
      nbtTagCompound.setByteArray("Biomes", chunkIn.getBiomeArray());
      chunkIn.setHasEntities(false);
      NBTTagList nbtTagList2 = new NBTTagList();

      for (int n = 0; n < chunkIn.getEntityLists().length; n++) {
         for (Entity entity : chunkIn.getEntityLists()[n]) {
            NBTTagCompound nbtTagCompound3 = new NBTTagCompound();
            if (entity.writeToNBTOptional(nbtTagCompound3)) {
               chunkIn.setHasEntities(true);
               nbtTagList2.appendTag(nbtTagCompound3);
            }
         }
      }

      nbtTagCompound.setTag("Entities", nbtTagList2);
      NBTTagList nbtTagList3 = new NBTTagList();

      for (TileEntity tileEntity : chunkIn.getTileEntityMap().values()) {
         NBTTagCompound nbtTagCompound4 = new NBTTagCompound();
         tileEntity.writeToNBT(nbtTagCompound4);
         nbtTagList3.appendTag(nbtTagCompound4);
      }

      nbtTagCompound.setTag("TileEntities", nbtTagList3);
      List list = worldIn.getPendingBlockUpdates(chunkIn, false);
      if (list != null) {
         long l2 = worldIn.getTotalWorldTime();
         NBTTagList nbtTagList4 = new NBTTagList();

         for (Object object : list) {
            NextTickListEntry nextTickListEntry = (NextTickListEntry)object;
            NBTTagCompound nbtTagCompound5 = new NBTTagCompound();
            ResourceLocation resourceLocation = Block.blockRegistry.getNameForObject(nextTickListEntry.getBlock());
            nbtTagCompound5.setString("i", resourceLocation == null ? "" : resourceLocation.toString());
            nbtTagCompound5.setInteger("x", nextTickListEntry.position.getX());
            nbtTagCompound5.setInteger("y", nextTickListEntry.position.getY());
            nbtTagCompound5.setInteger("z", nextTickListEntry.position.getZ());
            nbtTagCompound5.setInteger("t", (int)(nextTickListEntry.scheduledTime - l2));
            nbtTagCompound5.setInteger("p", nextTickListEntry.priority);
            nbtTagList4.appendTag(nbtTagCompound5);
         }

         nbtTagCompound.setTag("TileTicks", nbtTagList4);
      }
   }

   private Chunk readChunkFromNBT(World worldIn, NBTTagCompound nbtTagCompound) {
      int j = nbtTagCompound.getInteger("xPos");
      int k = nbtTagCompound.getInteger("zPos");
      Chunk chunk = new Chunk(worldIn, j, k);
      chunk.setHeightMap(nbtTagCompound.getIntArray("HeightMap"));
      chunk.setTerrainPopulated(nbtTagCompound.getBoolean("TerrainPopulated"));
      chunk.setLightPopulated(nbtTagCompound.getBoolean("LightPopulated"));
      chunk.setInhabitedTime(nbtTagCompound.getLong("InhabitedTime"));
      NBTTagList nbtTagList = nbtTagCompound.getTagList("Sections", 10);
      byte b = 16;
      ExtendedBlockStorage[] extendedBlockStorages = new ExtendedBlockStorage[b];
      boolean bl = !worldIn.provider.getHasNoSky();

      for (int l = 0; l < nbtTagList.tagCount(); l++) {
         NBTTagCompound nbtTagCompound2 = nbtTagList.getCompoundTagAt(l);
         byte b2 = nbtTagCompound2.getByte("Y");
         ExtendedBlockStorage extendedBlockStorage = new ExtendedBlockStorage(b2 << 4, bl);
         byte[] bytes = nbtTagCompound2.getByteArray("Blocks");
         NibbleArray nibbleArray = new NibbleArray(nbtTagCompound2.getByteArray("Data"));
         NibbleArray nibbleArray2 = nbtTagCompound2.hasKey("Add", 7) ? new NibbleArray(nbtTagCompound2.getByteArray("Add")) : null;
         char[] chars = new char[bytes.length];

         for (int m = 0; m < chars.length; m++) {
            int n = m & 15;
            int i2 = m >> 8 & 15;
            int i3 = m >> 4 & 15;
            int i4 = nibbleArray2 != null ? nibbleArray2.get(n, i2, i3) : 0;
            chars[m] = (char)(i4 << 12 | (bytes[m] & 255) << 4 | nibbleArray.get(n, i2, i3));
         }

         extendedBlockStorage.setData(chars);
         extendedBlockStorage.setBlocklightArray(new NibbleArray(nbtTagCompound2.getByteArray("BlockLight")));
         if (bl) {
            extendedBlockStorage.setSkylightArray(new NibbleArray(nbtTagCompound2.getByteArray("SkyLight")));
         }

         extendedBlockStorage.removeInvalidBlocks();
         extendedBlockStorages[b2] = extendedBlockStorage;
      }

      chunk.setStorageArrays(extendedBlockStorages);
      if (nbtTagCompound.hasKey("Biomes", 7)) {
         chunk.setBiomeArray(nbtTagCompound.getByteArray("Biomes"));
      }

      NBTTagList nbtTagList2 = nbtTagCompound.getTagList("Entities", 10);
      if (nbtTagList2 != null) {
         for (int i5 = 0; i5 < nbtTagList2.tagCount(); i5++) {
            NBTTagCompound nbtTagCompound3 = nbtTagList2.getCompoundTagAt(i5);
            Entity entity = EntityList.createEntityFromNBT(nbtTagCompound3, worldIn);
            chunk.setHasEntities(true);
            if (entity != null) {
               chunk.addEntity(entity);
               Entity entity2 = entity;

               for (NBTTagCompound nbtTagCompound4 = nbtTagCompound3; nbtTagCompound4.hasKey("Riding", 10); nbtTagCompound4 = nbtTagCompound4.getCompoundTag("Riding")) {
                  Entity entity3 = EntityList.createEntityFromNBT(nbtTagCompound4.getCompoundTag("Riding"), worldIn);
                  if (entity3 != null) {
                     chunk.addEntity(entity3);
                     entity2.mountEntity(entity3);
                  }

                  entity2 = entity3;
               }
            }
         }
      }

      NBTTagList nbtTagList3 = nbtTagCompound.getTagList("TileEntities", 10);
      if (nbtTagList3 != null) {
         for (int i6 = 0; i6 < nbtTagList3.tagCount(); i6++) {
            NBTTagCompound nbtTagCompound5 = nbtTagList3.getCompoundTagAt(i6);
            TileEntity tileEntity = TileEntity.createAndLoadEntity(nbtTagCompound5);
            if (tileEntity != null) {
               chunk.addTileEntity(tileEntity);
            }
         }
      }

      if (nbtTagCompound.hasKey("TileTicks", 9)) {
         NBTTagList nbtTagList4 = nbtTagCompound.getTagList("TileTicks", 10);
         if (nbtTagList4 != null) {
            for (int i7 = 0; i7 < nbtTagList4.tagCount(); i7++) {
               NBTTagCompound nbtTagCompound6 = nbtTagList4.getCompoundTagAt(i7);
               Block block;
               if (nbtTagCompound6.hasKey("i", 8)) {
                  block = Block.getBlockFromName(nbtTagCompound6.getString("i"));
               } else {
                  block = Block.getBlockById(nbtTagCompound6.getInteger("i"));
               }

               worldIn.scheduleBlockUpdate(
                  new BlockPos(nbtTagCompound6.getInteger("x"), nbtTagCompound6.getInteger("y"), nbtTagCompound6.getInteger("z")), block, nbtTagCompound6.getInteger("t"), nbtTagCompound6.getInteger("p")
               );
            }
         }
      }

      return chunk;
   }
}
