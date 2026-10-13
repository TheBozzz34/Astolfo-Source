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
   private static final long a = 7143608497093L;

   public ChunkProviderServer(WorldServer var1, IChunkLoader var2, IChunkProvider var3) {
      this.dummyChunk = new EmptyChunk(var1, 0, 0);
      this.worldObj = var1;
      this.chunkLoader = var2;
      this.serverChunkGenerator = var3;
   }

   @Override
   public boolean chunkExists(int var1, int var2) {
      return this.id2ChunkMap.containsItem(ChunkCoordIntPair.chunkXZ2Int(var1, var2));
   }

   public List<Chunk> func_152380_a() {
      return this.loadedChunks;
   }

   public void dropChunk(int var1, int var2) {
      if (this.worldObj.provider.canRespawnHere()) {
         if (!this.worldObj.isSpawnChunk(var1, var2)) {
            this.droppedChunksSet.add(ChunkCoordIntPair.chunkXZ2Int(var1, var2));
         }
      } else {
         this.droppedChunksSet.add(ChunkCoordIntPair.chunkXZ2Int(var1, var2));
      }
   }

   public void unloadAllChunks() {
      for (Chunk var5 : this.loadedChunks) {
         this.dropChunk(var5.xPosition, var5.zPosition);
      }
   }

   public Chunk loadChunk(int var1, int var2) {
      long var6 = ChunkCoordIntPair.chunkXZ2Int(var1, var2);
      this.droppedChunksSet.remove(var6);
      Chunk var8 = this.id2ChunkMap.getValueByKey(var6);
      if (var8 == null) {
         var8 = this.loadChunkFromFile(var1, var2);
         if (var8 == null) {
            if (this.serverChunkGenerator == null) {
               var8 = this.dummyChunk;
            } else {
               try {
                  var8 = this.serverChunkGenerator.provideChunk(var1, var2);
               } catch (Throwable var12) {
                  CrashReport var10 = CrashReport.makeCrashReport(var12, "Exception generating new chunk");
                  CrashReportCategory var11 = var10.makeCategory("Chunk to be generated");
                  var11.addCrashSection("Location", String.format("%d,%d", var1, var2));
                  var11.addCrashSection("Position hash", var6);
                  var11.addCrashSection("Generator", this.serverChunkGenerator.makeString());
                  throw new ReportedException(var10);
               }
            }
         }

         this.id2ChunkMap.add(var6, var8);
         this.loadedChunks.add(var8);
         var8.onChunkLoad();
         var8.populateChunk(this, this, var1, var2);
      }

      return var8;
   }

   @Override
   public Chunk provideChunk(int var1, int var2) {
      Chunk var6 = this.id2ChunkMap.getValueByKey(ChunkCoordIntPair.chunkXZ2Int(var1, var2));
      return var6 == null ? (!this.worldObj.v() && !this.chunkLoadOverride ? this.dummyChunk : this.loadChunk(var1, var2)) : var6;
   }

   private Chunk loadChunkFromFile(int var1, int var2) {
      if (this.chunkLoader == null) {
         return null;
      }

      try {
         Chunk var6 = this.chunkLoader.loadChunk(this.worldObj, var1, var2);
         if (var6 != null) {
            var6.setLastSaveTime(this.worldObj.getTotalWorldTime());
            if (this.serverChunkGenerator != null) {
               this.serverChunkGenerator.recreateStructures(var6, var1, var2);
            }
         }

         return var6;
      } catch (Exception var7) {
         logger.error("Couldn't load chunk", var7);
         return null;
      }
   }

   private void saveChunkExtraData(Chunk var1) {
      if (this.chunkLoader != null) {
         try {
            this.chunkLoader.saveExtraChunkData(this.worldObj, var1);
         } catch (Exception var6) {
            logger.error("Couldn't save entities", var6);
         }
      }
   }

   private void saveChunkData(Chunk var1) {
      if (this.chunkLoader != null) {
         try {
            var1.setLastSaveTime(this.worldObj.getTotalWorldTime());
            this.chunkLoader.saveChunk(this.worldObj, var1);
         } catch (IOException var6) {
            logger.error("Couldn't save chunk", var6);
         } catch (MinecraftException var7) {
            logger.error("Couldn't save chunk; already in use by another instance of Minecraft?", var7);
         }
      }
   }

   @Override
   public void populate(IChunkProvider var1, int var2, int var3) {
      Chunk var7 = this.provideChunk(var2, var3);
      if (!var7.isTerrainPopulated()) {
         var7.func_150809_p();
         if (this.serverChunkGenerator != null) {
            this.serverChunkGenerator.populate(var1, var2, var3);
            var7.setChunkModified();
         }
      }
   }

   @Override
   public boolean populateChunk(IChunkProvider var1, Chunk var2, int var3, int var4) {
      if (this.serverChunkGenerator != null && this.serverChunkGenerator.populateChunk(var1, var2, var3, var4)) {
         Chunk var8 = this.provideChunk(var3, var4);
         var8.setChunkModified();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean saveChunks(boolean var1, IProgressUpdate var2) {
      int var6 = 0;
      ArrayList var7 = Lists.newArrayList(this.loadedChunks);

      for (int var8 = 0; var8 < var7.size(); var8++) {
         Chunk var9 = (Chunk)var7.get(var8);
         if (var1) {
            this.saveChunkExtraData(var9);
         }

         if (var9.needsSaving(var1)) {
            this.saveChunkData(var9);
            var9.setModified(false);
            if (++var6 == 24 && !var1) {
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
         for (int var4 = 0; var4 < 100; var4++) {
            if (!this.droppedChunksSet.isEmpty()) {
               Long var5 = this.droppedChunksSet.iterator().next();
               Chunk var6 = this.id2ChunkMap.getValueByKey(var5);
               if (var6 != null) {
                  var6.onChunkUnload();
                  this.saveChunkData(var6);
                  this.saveChunkExtraData(var6);
                  this.id2ChunkMap.remove(var5);
                  this.loadedChunks.remove(var6);
               }

               this.droppedChunksSet.remove(var5);
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
   public List<BiomeGenBase.SpawnListEntry> getPossibleCreatures(EnumCreatureType var1, BlockPos var2) {
      return this.serverChunkGenerator.getPossibleCreatures(var1, var2);
   }

   @Override
   public BlockPos getStrongholdGen(World var1, String var2, BlockPos var3) {
      return this.serverChunkGenerator.getStrongholdGen(var1, var2, var3);
   }

   @Override
   public int getLoadedChunkCount() {
      return this.id2ChunkMap.getNumHashElements();
   }

   @Override
   public void recreateStructures(Chunk var1, int var2, int var3) {
   }

   @Override
   public Chunk provideChunk(BlockPos var1) {
      return this.provideChunk(var1.getX() >> 4, var1.getZ() >> 4);
   }
}
