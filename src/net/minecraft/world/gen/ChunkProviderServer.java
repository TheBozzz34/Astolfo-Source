package net.minecraft.world.gen;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.LongHashMap;
import net.minecraft.util.ReportedException;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkProviderServer implements IChunkProvider {
   private static final Logger logger = LogManager.getLogger();
   private Set<Long> droppedChunksSet = Collections.newSetFromMap(new ConcurrentHashMap<>());
   private Chunk dummyChunk;
   private IChunkProvider serverChunkGenerator;
   private IChunkLoader chunkLoader;
   public boolean chunkLoadOverride = true;
   private LongHashMap<Chunk> id2ChunkMap = new LongHashMap<>();
   private List<Chunk> loadedChunks = Lists.newArrayList();
   private WorldServer worldObj;

   public ChunkProviderServer(WorldServer worldServer, IChunkLoader iChunkLoader, IChunkProvider iChunkProvider) {
      this.dummyChunk = new EmptyChunk(worldServer, 0, 0);
      this.worldObj = worldServer;
      this.chunkLoader = iChunkLoader;
      this.serverChunkGenerator = iChunkProvider;
   }

   @Override
   public boolean chunkExists(int x, int z) {
      return this.id2ChunkMap.containsItem(ChunkCoordIntPair.chunkXZ2Int(x, z));
   }

   public List<Chunk> func_152380_a() {
      return this.loadedChunks;
   }

   public void dropChunk(int x, int z) {
      if (this.worldObj.provider.canRespawnHere()) {
         if (!this.worldObj.isSpawnChunk(x, z)) {
            this.droppedChunksSet.add(ChunkCoordIntPair.chunkXZ2Int(x, z));
         }
      } else {
         this.droppedChunksSet.add(ChunkCoordIntPair.chunkXZ2Int(x, z));
      }
   }

   public void unloadAllChunks() {
      for (Chunk chunk : this.loadedChunks) {
         this.dropChunk(chunk.xPosition, chunk.zPosition);
      }
   }

   public Chunk loadChunk(int chunkX, int chunkZ) {
      long l = ChunkCoordIntPair.chunkXZ2Int(chunkX, chunkZ);
      this.droppedChunksSet.remove(l);
      Chunk chunk2 = this.id2ChunkMap.getValueByKey(l);
      if (chunk2 == null) {
         chunk2 = this.loadChunkFromFile(chunkX, chunkZ);
         if (chunk2 == null) {
            if (this.serverChunkGenerator == null) {
               chunk2 = this.dummyChunk;
            } else {
               try {
                  chunk2 = this.serverChunkGenerator.provideChunk(chunkX, chunkZ);
               } catch (Throwable throwable) {
                  CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Exception generating new chunk");
                  CrashReportCategory crashReportCategory = crashReport.makeCategory("Chunk to be generated");
                  crashReportCategory.addCrashSection("Location", String.format("%d,%d", chunkX, chunkZ));
                  crashReportCategory.addCrashSection("Position hash", l);
                  crashReportCategory.addCrashSection("Generator", this.serverChunkGenerator.makeString());
                  throw new ReportedException(crashReport);
               }
            }
         }

         this.id2ChunkMap.add(l, chunk2);
         this.loadedChunks.add(chunk2);
         chunk2.onChunkLoad();
         chunk2.populateChunk(this, this, chunkX, chunkZ);
      }

      return chunk2;
   }

   @Override
   public Chunk provideChunk(int x, int z) {
      Chunk chunk = this.id2ChunkMap.getValueByKey(ChunkCoordIntPair.chunkXZ2Int(x, z));
      return chunk == null ? (!this.worldObj.isFindingSpawnPoint() && !this.chunkLoadOverride ? this.dummyChunk : this.loadChunk(x, z)) : chunk;
   }

   private Chunk loadChunkFromFile(int x, int z) {
      if (this.chunkLoader == null) {
         return null;
      }

      try {
         Chunk chunk2 = this.chunkLoader.loadChunk(this.worldObj, x, z);
         if (chunk2 != null) {
            chunk2.setLastSaveTime(this.worldObj.getTotalWorldTime());
            if (this.serverChunkGenerator != null) {
               this.serverChunkGenerator.recreateStructures(chunk2, x, z);
            }
         }

         return chunk2;
      } catch (Exception exception) {
         logger.error("Couldn't load chunk", exception);
         return null;
      }
   }

   private void saveChunkExtraData(Chunk chunkIn) {
      if (this.chunkLoader != null) {
         try {
            this.chunkLoader.saveExtraChunkData(this.worldObj, chunkIn);
         } catch (Exception exception) {
            logger.error("Couldn't save entities", exception);
         }
      }
   }

   private void saveChunkData(Chunk chunkIn) {
      if (this.chunkLoader != null) {
         try {
            chunkIn.setLastSaveTime(this.worldObj.getTotalWorldTime());
            this.chunkLoader.saveChunk(this.worldObj, chunkIn);
         } catch (IOException ioException) {
            logger.error("Couldn't save chunk", ioException);
         } catch (MinecraftException minecraftException) {
            logger.error("Couldn't save chunk; already in use by another instance of Minecraft?", minecraftException);
         }
      }
   }

   @Override
   public void populate(IChunkProvider chunkProvider, int x, int z) {
      Chunk chunk = this.provideChunk(x, z);
      if (!chunk.isTerrainPopulated()) {
         chunk.func_150809_p();
         if (this.serverChunkGenerator != null) {
            this.serverChunkGenerator.populate(chunkProvider, x, z);
            chunk.setChunkModified();
         }
      }
   }

   @Override
   public boolean populateChunk(IChunkProvider chunkProvider, Chunk chunkIn, int x, int z) {
      if (this.serverChunkGenerator != null && this.serverChunkGenerator.populateChunk(chunkProvider, chunkIn, x, z)) {
         Chunk chunk = this.provideChunk(x, z);
         chunk.setChunkModified();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean saveChunks(boolean saveAllChunks, IProgressUpdate progressCallback) {
      int i = 0;
      ArrayList arrayList = Lists.newArrayList(this.loadedChunks);

      for (int j = 0; j < arrayList.size(); j++) {
         Chunk chunk = (Chunk)arrayList.get(j);
         if (saveAllChunks) {
            this.saveChunkExtraData(chunk);
         }

         if (chunk.needsSaving(saveAllChunks)) {
            this.saveChunkData(chunk);
            chunk.setModified(false);
            if (++i == 24 && !saveAllChunks) {
               return false;
            }
         }
      }

      return true;
   }

   @Override
   public void saveExtraData() {
      if (this.chunkLoader != null) {
         this.chunkLoader.saveExtraData();
      }
   }

   @Override
   public boolean unloadQueuedChunks() {
      if (!this.worldObj.disableLevelSaving) {
         for (int i = 0; i < 100; i++) {
            if (!this.droppedChunksSet.isEmpty()) {
               Long long2 = this.droppedChunksSet.iterator().next();
               Chunk chunk = this.id2ChunkMap.getValueByKey(long2);
               if (chunk != null) {
                  chunk.onChunkUnload();
                  this.saveChunkData(chunk);
                  this.saveChunkExtraData(chunk);
                  this.id2ChunkMap.remove(long2);
                  this.loadedChunks.remove(chunk);
               }

               this.droppedChunksSet.remove(long2);
            }
         }

         if (this.chunkLoader != null) {
            this.chunkLoader.chunkTick();
         }
      }

      return this.serverChunkGenerator.unloadQueuedChunks();
   }

   @Override
   public boolean canSave() {
      return !this.worldObj.disableLevelSaving;
   }

   @Override
   public String makeString() {
      return "ServerChunkCache: " + this.id2ChunkMap.getNumHashElements() + " Drop: " + this.droppedChunksSet.size();
   }

   @Override
   public List<BiomeGenBase.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
      return this.serverChunkGenerator.getPossibleCreatures(creatureType, pos);
   }

   @Override
   public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position) {
      return this.serverChunkGenerator.getStrongholdGen(worldIn, structureName, position);
   }

   @Override
   public int getLoadedChunkCount() {
      return this.id2ChunkMap.getNumHashElements();
   }

   @Override
   public void recreateStructures(Chunk chunkIn, int x, int z) {
   }

   @Override
   public Chunk provideChunk(BlockPos blockPosIn) {
      return this.provideChunk(blockPosIn.getX() >> 4, blockPosIn.getZ() >> 4);
   }
}
