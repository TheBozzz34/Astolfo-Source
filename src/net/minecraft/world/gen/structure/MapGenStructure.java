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
   private static final long d = 110697862744374L;

   public abstract String getStructureName();

   @Override
   public final void recursiveGenerate(World var1, final int var2, final int var3, int var4, int var5, ChunkPrimer var6) {
      this.initializeStructureData(var1);
      if (!this.structureLongMap.containsItem(ChunkCoordIntPair.chunkXZ2Int(var2, var3))) {
         this.rand.nextInt();

         try {
            if (this.canSpawnStructureAtCoords(var2, var3)) {
               StructureStart var10 = this.getStructureStart(var2, var3);
               this.structureMap.put(ChunkCoordIntPair.chunkXZ2Int(var2, var3), var10);
               this.structureLongMap.add(ChunkCoordIntPair.chunkXZ2Int(var2, var3), var10);
               this.setStructureStart(var2, var3, var10);
            }
         } catch (Throwable var13) {
            CrashReport var11 = CrashReport.makeCrashReport(var13, "Exception preparing structure feature");
            CrashReportCategory var12 = var11.makeCategory("Feature being prepared");
            var12.addCrashSectionCallable("Is feature chunk", new Callable<String>() {
               private static final long a = 40201466015239L;

               public String call() throws Exception {
                  return MapGenStructure.this.canSpawnStructureAtCoords(var2, var3) ? "True" : "False";
               }
            });
            var12.addCrashSection("Chunk location", String.format("%d,%d", var2, var3));
            var12.addCrashSectionCallable("Chunk pos hash", new Callable<String>() {
               public String call() throws Exception {
                  return String.valueOf(ChunkCoordIntPair.chunkXZ2Int(var2, var3));
               }
            });
            var12.addCrashSectionCallable("Structure type", new Callable<String>() {
               public String call() throws Exception {
                  return MapGenStructure.this.getClass().getCanonicalName();
               }
            });
            throw new ReportedException(var11);
         }
      }
   }

   public boolean generateStructure(World var1, Random var2, ChunkCoordIntPair var3) {
      this.initializeStructureData(var1);
      int var7 = (var3.chunkXPos << 4) + 8;
      int var8 = (var3.chunkZPos << 4) + 8;
      boolean var9 = false;

      for (Object var11 : this.structureMap.values()) {
         StructureStart var12 = (StructureStart)var11;
         if (var12.isSizeableStructure() && var12.func_175788_a(var3) && var12.getBoundingBox().intersectsWith(var7, var8, var7 + 15, var8 + 15)) {
            var12.generateStructure(var1, var2, new StructureBoundingBox(var7, var8, var7 + 15, var8 + 15));
            var12.func_175787_b(var3);
            var9 = true;
            this.setStructureStart(var12.getChunkPosX(), var12.getChunkPosZ(), var12);
         }
      }

      return var9;
   }

   public boolean func_175795_b(BlockPos var1) {
      this.initializeStructureData(this.worldObj);
      return this.func_175797_c(var1) != null;
   }

   public StructureStart func_175797_c(BlockPos var1) {
      for (Object var6 : this.structureMap.values()) {
         StructureStart var7 = (StructureStart)var6;
         if (var7.isSizeableStructure() && var7.getBoundingBox().isVecInside(var1)) {
            for (StructureComponent var9 : var7.getComponents()) {
               if (var9.getBoundingBox().isVecInside(var1)) {
                  return var7;
               }
            }
         }
      }

      return null;
   }

   public boolean isPositionInStructure(World var1, BlockPos var2) {
      this.initializeStructureData(var1);

      for (Object var7 : this.structureMap.values()) {
         StructureStart var8 = (StructureStart)var7;
         if (var8.isSizeableStructure() && var8.getBoundingBox().isVecInside(var2)) {
            return true;
         }
      }

      return false;
   }

   public BlockPos getClosestStrongholdPos(World var1, BlockPos var2) {
      this.worldObj = var1;
      this.initializeStructureData(var1);
      this.rand.setSeed(var1.getSeed());
      long var6 = this.rand.nextLong();
      long var8 = this.rand.nextLong();
      long var10 = (var2.getX() >> 4) * var6;
      long var12 = (var2.getZ() >> 4) * var8;
      this.rand.setSeed(var10 ^ var12 ^ var1.getSeed());
      this.recursiveGenerate(var1, var2.getX() >> 4, var2.getZ() >> 4, 0, 0, (ChunkPrimer)null);
      double var14 = Double.MAX_VALUE;
      BlockPos var16 = null;

      for (Object var18 : this.structureMap.values()) {
         StructureStart var19 = (StructureStart)var18;
         if (var19.isSizeableStructure()) {
            StructureComponent var20 = var19.getComponents().get(0);
            BlockPos var21 = var20.getBoundingBoxCenter();
            double var22 = var21.distanceSq(var2);
            if (var22 < var14) {
               var14 = var22;
               var16 = var21;
            }
         }
      }

      if (var16 != null) {
         return var16;
      }

      List var24 = this.getCoordList();
      if (var24 != null) {
         BlockPos var25 = null;

         for (Object var27 : var24) {
            double var28 = ((BlockPos)var27).distanceSq(var2);
            if (var28 < var14) {
               var14 = var28;
               var25 = (BlockPos)var27;
            }
         }

         return var25;
      } else {
         return null;
      }
   }

   public List<BlockPos> getCoordList() {
      return null;
   }

   private void initializeStructureData(World var1) {
      if (this.structureData == null) {
         if (Reflector.ForgeWorld_getPerWorldStorage.exists()) {
            MapStorage var5 = (MapStorage)Reflector.call(var1, Reflector.ForgeWorld_getPerWorldStorage);
            this.structureData = (MapGenStructureData)var5.loadData(MapGenStructureData.class, this.getStructureName());
         } else {
            this.structureData = (MapGenStructureData)var1.loadItemData(MapGenStructureData.class, this.getStructureName());
         }

         if (this.structureData == null) {
            this.structureData = new MapGenStructureData(this.getStructureName());
            if (Reflector.ForgeWorld_getPerWorldStorage.exists()) {
               MapStorage var13 = (MapStorage)Reflector.call(var1, Reflector.ForgeWorld_getPerWorldStorage);
               var13.setData(this.getStructureName(), this.structureData);
            } else {
               var1.setItemData(this.getStructureName(), this.structureData);
            }
         } else {
            NBTTagCompound var14 = this.structureData.getTagCompound();

            for (String var7 : var14.getKeySet()) {
               NBTBase var8 = var14.getTag(var7);
               if (var8.getId() == 10) {
                  NBTTagCompound var9 = (NBTTagCompound)var8;
                  if (var9.hasKey("ChunkX") && var9.hasKey("ChunkZ")) {
                     int var10 = var9.getInteger("ChunkX");
                     int var11 = var9.getInteger("ChunkZ");
                     StructureStart var12 = MapGenStructureIO.getStructureStart(var9, var1);
                     if (var12 != null) {
                        this.structureMap.put(ChunkCoordIntPair.chunkXZ2Int(var10, var11), var12);
                        this.structureLongMap.add(ChunkCoordIntPair.chunkXZ2Int(var10, var11), var12);
                     }
                  }
               }
            }
         }
      }
   }

   private void setStructureStart(int var1, int var2, StructureStart var3) {
      this.structureData.writeInstance(var3.writeStructureComponentsToNBT(var1, var2), var1, var2);
      this.structureData.markDirty();
   }

   public abstract boolean canSpawnStructureAtCoords(int var1, int var2);

   public abstract StructureStart getStructureStart(int var1, int var2);
}
