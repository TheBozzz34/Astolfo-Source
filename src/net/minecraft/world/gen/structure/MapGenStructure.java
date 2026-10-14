package net.minecraft.world.gen.structure;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.src.Reflector;
import net.minecraft.util.BlockPos;
import net.minecraft.util.LongHashMap;
import net.minecraft.util.ReportedException;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.storage.MapStorage;

public abstract class MapGenStructure extends MapGenBase {
   private MapGenStructureData structureData;
   public Map<Long, StructureStart> structureMap = Maps.newHashMap();
   private LongHashMap structureLongMap = new LongHashMap();

   public abstract String getStructureName();

   @Override
   public final void recursiveGenerate(World worldIn, final int chunkX, final int chunkZ, int i, int j, ChunkPrimer chunkPrimerIn) {
      this.initializeStructureData(worldIn);
      if (!this.structureLongMap.containsItem(ChunkCoordIntPair.chunkXZ2Int(chunkX, chunkZ))) {
         this.rand.nextInt();

         try {
            if (this.canSpawnStructureAtCoords(chunkX, chunkZ)) {
               StructureStart structureStart = this.getStructureStart(chunkX, chunkZ);
               this.structureMap.put(ChunkCoordIntPair.chunkXZ2Int(chunkX, chunkZ), structureStart);
               this.structureLongMap.add(ChunkCoordIntPair.chunkXZ2Int(chunkX, chunkZ), structureStart);
               this.setStructureStart(chunkX, chunkZ, structureStart);
            }
         } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Exception preparing structure feature");
            CrashReportCategory crashReportCategory = crashReport.makeCategory("Feature being prepared");
            crashReportCategory.addCrashSectionCallable("Is feature chunk", new Callable<String>() {
               public String call() throws Exception {
                  return MapGenStructure.this.canSpawnStructureAtCoords(chunkX, chunkZ) ? "True" : "False";
               }
            });
            crashReportCategory.addCrashSection("Chunk location", String.format("%d,%d", chunkX, chunkZ));
            crashReportCategory.addCrashSectionCallable("Chunk pos hash", new Callable<String>() {
               public String call() throws Exception {
                  return String.valueOf(ChunkCoordIntPair.chunkXZ2Int(chunkX, chunkZ));
               }
            });
            crashReportCategory.addCrashSectionCallable("Structure type", new Callable<String>() {
               public String call() throws Exception {
                  return MapGenStructure.this.getClass().getCanonicalName();
               }
            });
            throw new ReportedException(crashReport);
         }
      }
   }

   public boolean generateStructure(World worldIn, Random randomIn, ChunkCoordIntPair chunkCoord) {
      this.initializeStructureData(worldIn);
      int i = (chunkCoord.chunkXPos << 4) + 8;
      int j = (chunkCoord.chunkZPos << 4) + 8;
      boolean bl = false;

      for (Object object : this.structureMap.values()) {
         StructureStart structureStart = (StructureStart)object;
         if (structureStart.isSizeableStructure() && structureStart.func_175788_a(chunkCoord) && structureStart.getBoundingBox().intersectsWith(i, j, i + 15, j + 15)) {
            structureStart.generateStructure(worldIn, randomIn, new StructureBoundingBox(i, j, i + 15, j + 15));
            structureStart.func_175787_b(chunkCoord);
            bl = true;
            this.setStructureStart(structureStart.getChunkPosX(), structureStart.getChunkPosZ(), structureStart);
         }
      }

      return bl;
   }

   public boolean func_175795_b(BlockPos pos) {
      this.initializeStructureData(this.worldObj);
      return this.func_175797_c(pos) != null;
   }

   public StructureStart func_175797_c(BlockPos pos) {
      for (Object object : this.structureMap.values()) {
         StructureStart structureStart = (StructureStart)object;
         if (structureStart.isSizeableStructure() && structureStart.getBoundingBox().isVecInside(pos)) {
            for (StructureComponent structureComponent : structureStart.getComponents()) {
               if (structureComponent.getBoundingBox().isVecInside(pos)) {
                  return structureStart;
               }
            }
         }
      }

      return null;
   }

   public boolean isPositionInStructure(World worldIn, BlockPos pos) {
      this.initializeStructureData(worldIn);

      for (Object object : this.structureMap.values()) {
         StructureStart structureStart = (StructureStart)object;
         if (structureStart.isSizeableStructure() && structureStart.getBoundingBox().isVecInside(pos)) {
            return true;
         }
      }

      return false;
   }

   public BlockPos getClosestStrongholdPos(World worldIn, BlockPos pos) {
      this.worldObj = worldIn;
      this.initializeStructureData(worldIn);
      this.rand.setSeed(worldIn.getSeed());
      long l = this.rand.nextLong();
      long m = this.rand.nextLong();
      long n = (pos.getX() >> 4) * l;
      long l2 = (pos.getZ() >> 4) * m;
      this.rand.setSeed(n ^ l2 ^ worldIn.getSeed());
      this.recursiveGenerate(worldIn, pos.getX() >> 4, pos.getZ() >> 4, 0, 0, (ChunkPrimer)null);
      double d = Double.MAX_VALUE;
      BlockPos blockPos = null;

      for (Object object : this.structureMap.values()) {
         StructureStart structureStart = (StructureStart)object;
         if (structureStart.isSizeableStructure()) {
            StructureComponent structureComponent = structureStart.getComponents().get(0);
            BlockPos blockPos2 = structureComponent.getBoundingBoxCenter();
            double e = blockPos2.distanceSq(pos);
            if (e < d) {
               d = e;
               blockPos = blockPos2;
            }
         }
      }

      if (blockPos != null) {
         return blockPos;
      }

      List list = this.getCoordList();
      if (list != null) {
         BlockPos blockPos3 = null;

         for (Object object2 : list) {
            double d2 = ((BlockPos)object2).distanceSq(pos);
            if (d2 < d) {
               d = d2;
               blockPos3 = (BlockPos)object2;
            }
         }

         return blockPos3;
      } else {
         return null;
      }
   }

   public List<BlockPos> getCoordList() {
      return null;
   }

   private void initializeStructureData(World worldIn) {
      if (this.structureData == null) {
         if (Reflector.ForgeWorld_getPerWorldStorage.exists()) {
            MapStorage mapStorage = (MapStorage)Reflector.call(worldIn, Reflector.ForgeWorld_getPerWorldStorage);
            this.structureData = (MapGenStructureData)mapStorage.loadData(MapGenStructureData.class, this.getStructureName());
         } else {
            this.structureData = (MapGenStructureData)worldIn.loadItemData(MapGenStructureData.class, this.getStructureName());
         }

         if (this.structureData == null) {
            this.structureData = new MapGenStructureData(this.getStructureName());
            if (Reflector.ForgeWorld_getPerWorldStorage.exists()) {
               MapStorage mapStorage2 = (MapStorage)Reflector.call(worldIn, Reflector.ForgeWorld_getPerWorldStorage);
               mapStorage2.setData(this.getStructureName(), this.structureData);
            } else {
               worldIn.setItemData(this.getStructureName(), this.structureData);
            }
         } else {
            NBTTagCompound nbtTagCompound = this.structureData.getTagCompound();

            for (String string : nbtTagCompound.getKeySet()) {
               NBTBase nbtBase = nbtTagCompound.getTag(string);
               if (nbtBase.getId() == 10) {
                  NBTTagCompound nbtTagCompound2 = (NBTTagCompound)nbtBase;
                  if (nbtTagCompound2.hasKey("ChunkX") && nbtTagCompound2.hasKey("ChunkZ")) {
                     int i = nbtTagCompound2.getInteger("ChunkX");
                     int j = nbtTagCompound2.getInteger("ChunkZ");
                     StructureStart structureStart = MapGenStructureIO.getStructureStart(nbtTagCompound2, worldIn);
                     if (structureStart != null) {
                        this.structureMap.put(ChunkCoordIntPair.chunkXZ2Int(i, j), structureStart);
                        this.structureLongMap.add(ChunkCoordIntPair.chunkXZ2Int(i, j), structureStart);
                     }
                  }
               }
            }
         }
      }
   }

   private void setStructureStart(int chunkX, int chunkZ, StructureStart startIn) {
      this.structureData.writeInstance(startIn.writeStructureComponentsToNBT(chunkX, chunkZ), chunkX, chunkZ);
      this.structureData.markDirty();
   }

   public abstract boolean canSpawnStructureAtCoords(int chunkX, int chunkZ);

   public abstract StructureStart getStructureStart(int chunkX, int chunkZ);
}
