package net.minecraft.world;

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;

public final class SpawnerAnimals {
   private static final int MOB_COUNT_DIV = (int)Math.pow(17.0, 2.0);
   private final Set<ChunkCoordIntPair> eligibleChunksForSpawning = Sets.newHashSet();
   private static final long a = 92894341218988L;

   public int findChunksForSpawning(WorldServer var1, boolean var2, boolean var3, boolean var4) {
      long var5 = a ^ 98560144167614L;
      if (!var2 && !var3) {
         return 0;
      }

      this.eligibleChunksForSpawning.clear();
      int var8 = 0;

      for (EntityPlayer var10 : var1.playerEntities) {
         if (!var10.isSpectator()) {
            int var11 = MathHelper.floor_double(var10.posX / 16.0);
            int var12 = MathHelper.floor_double(var10.posZ / 16.0);
            long var10000 = 3408594515062740785L ^ var5;
            byte var13 = 8;

            for (int var14 = -var13; var14 <= var13; var14++) {
               for (int var15 = -var13; var15 <= var13; var15++) {
                  boolean var16 = var14 == -var13 || var14 == var13 || var15 == -var13 || var15 == var13;
                  ChunkCoordIntPair var17 = new ChunkCoordIntPair(var14 + var11, var15 + var12);
                  if (!this.eligibleChunksForSpawning.contains(var17)) {
                     var8++;
                     if (!var16 && var1.getWorldBorder().contains(var17)) {
                        this.eligibleChunksForSpawning.add(var17);
                     }
                  }
               }
            }
         }
      }

      int var39 = 0;
      BlockPos var40 = var1.getSpawnPoint();

      for (EnumCreatureType var44 : EnumCreatureType.values()) {
         if ((!var44.getPeacefulCreature() || var3) && (var44.getPeacefulCreature() || var2) && (!var44.getAnimal() || var4)) {
            int var45 = var1.countEntities(var44.getCreatureClass());
            int var46 = var44.getMaxNumberOfCreature() * var8 / MOB_COUNT_DIV;
            if (var45 <= var46) {
               label121:
               for (ChunkCoordIntPair var18 : this.eligibleChunksForSpawning) {
                  BlockPos var19 = getRandomChunkPosition(var1, var18.chunkXPos, var18.chunkZPos);
                  int var20 = var19.getX();
                  int var21 = var19.getY();
                  int var22 = var19.getZ();
                  Block var23 = var1.getBlockState(var19).getBlock();
                  if (!var23.isNormalCube()) {
                     int var24 = 0;

                     for (int var25 = 0; var25 < 3; var25++) {
                        int var26 = var20;
                        int var27 = var21;
                        long var48 = 3006838497115828214L ^ var5;
                        int var28 = var22;
                        byte var29 = 6;
                        BiomeGenBase.SpawnListEntry var30 = null;
                        IEntityLivingData var31 = null;

                        for (int var32 = 0; var32 < 4; var32++) {
                           var26 += var1.rand.nextInt(var29) - var1.rand.nextInt(var29);
                           var27 += var1.rand.nextInt(1) - var1.rand.nextInt(1);
                           var28 += var1.rand.nextInt(var29) - var1.rand.nextInt(var29);
                           BlockPos var33 = new BlockPos(var26, var27, var28);
                           float var34 = var26 + 0.5F;
                           float var35 = var28 + 0.5F;
                           if (!var1.isAnyPlayerWithinRangeAt(var34, var27, var35, 24.0) && var40.distanceSq(var34, var27, var35) >= 576.0) {
                              if (var30 == null) {
                                 var30 = var1.getSpawnListEntryForTypeAt(var44, var33);
                                 if (var30 == null) {
                                    break;
                                 }
                              }

                              if (var1.canCreatureTypeSpawnHere(var44, var30, var33)
                                 && canCreatureTypeSpawnAtLocation(EntitySpawnPlacementRegistry.getPlacementForEntity(var30.entityClass), var1, var33)) {
                                 EntityLiving var36;
                                 try {
                                    var36 = var30.entityClass.getConstructor(World.class).newInstance(var1);
                                 } catch (Exception var38) {
                                    var38.printStackTrace();
                                    return var39;
                                 }

                                 var36.setLocationAndAngles(var34, var27, var35, var1.rand.nextFloat() * 360.0F, 0.0F);
                                 if (var36.getCanSpawnHere() && var36.isNotColliding()) {
                                    var31 = var36.onInitialSpawn(var1.getDifficultyForLocation(new BlockPos(var36)), var31);
                                    if (var36.isNotColliding()) {
                                       var24++;
                                       var1.spawnEntityInWorld(var36);
                                    }

                                    if (var24 >= var36.getMaxSpawnedInChunk()) {
                                       continue label121;
                                    }
                                 }

                                 var39 += var24;
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      return var39;
   }

   public static BlockPos getRandomChunkPosition(World var0, int var1, int var2) {
      Chunk var6 = var0.getChunkFromChunkCoords(var1, var2);
      int var7 = var1 * 16 + var0.rand.nextInt(16);
      int var8 = var2 * 16 + var0.rand.nextInt(16);
      int var9 = MathHelper.roundUp(var6.getHeight(new BlockPos(var7, 0, var8)) + 1, 16);
      int var10 = var0.rand.nextInt(var9 > 0 ? var9 : var6.getTopFilledSegment() + 16 - 1);
      return new BlockPos(var7, var10, var8);
   }

   public static boolean canCreatureTypeSpawnAtLocation(EntityLiving.SpawnPlacementType var0, World var1, BlockPos var2) {
      if (!var1.getWorldBorder().contains(var2)) {
         return false;
      }

      Block var6 = var1.getBlockState(var2).getBlock();
      if (var0 == EntityLiving.SpawnPlacementType.IN_WATER) {
         return var6.getMaterial().isLiquid()
            && var1.getBlockState(var2.down()).getBlock().getMaterial().isLiquid()
            && !var1.getBlockState(var2.up()).getBlock().isNormalCube();
      }

      BlockPos var7 = var2.down();
      if (!World.doesBlockHaveSolidTopSurface(var1, var7)) {
         return false;
      }

      Block var8 = var1.getBlockState(var7).getBlock();
      boolean var9 = var8 != Blocks.bedrock && var8 != Blocks.barrier;
      return var9 && !var6.isNormalCube() && !var6.getMaterial().isLiquid() && !var1.getBlockState(var2.up()).getBlock().isNormalCube();
   }

   public static void performWorldGenSpawning(World var0, BiomeGenBase var1, int var2, int var3, int var4, int var5, Random var6) {
      List var10 = var1.getSpawnableList(EnumCreatureType.CREATURE);
      if (!var10.isEmpty()) {
         while (var6.nextFloat() < var1.getSpawningChance()) {
            BiomeGenBase.SpawnListEntry var11 = (net.minecraft.world.biome.BiomeGenBase.SpawnListEntry)(WeightedRandom.getRandomItem(var0.rand, var10));
            int var12 = var11.minGroupCount + var6.nextInt(1 + var11.maxGroupCount - var11.minGroupCount);
            IEntityLivingData var13 = null;
            int var14 = var2 + var6.nextInt(var4);
            int var15 = var3 + var6.nextInt(var5);
            int var16 = var14;
            int var17 = var15;

            for (int var18 = 0; var18 < var12; var18++) {
               boolean var19 = false;

               for (int var20 = 0; !var19 && var20 < 4; var20++) {
                  BlockPos var21 = var0.getTopSolidOrLiquidBlock(new BlockPos(var14, 0, var15));
                  if (canCreatureTypeSpawnAtLocation(EntityLiving.SpawnPlacementType.ON_GROUND, var0, var21)) {
                     EntityLiving var22;
                     try {
                        var22 = var11.entityClass.getConstructor(World.class).newInstance(var0);
                     } catch (Exception var24) {
                        var24.printStackTrace();
                        continue;
                     }

                     var22.setLocationAndAngles(var14 + 0.5F, var21.getY(), var15 + 0.5F, var6.nextFloat() * 360.0F, 0.0F);
                     var0.spawnEntityInWorld(var22);
                     var13 = var22.onInitialSpawn(var0.getDifficultyForLocation(new BlockPos(var22)), var13);
                     var19 = true;
                  }

                  var14 += var6.nextInt(5) - var6.nextInt(5);

                  for (var15 += var6.nextInt(5) - var6.nextInt(5);
                     var14 < var2 || var14 >= var2 + var4 || var15 < var3 || var15 >= var3 + var4;
                     var15 = var17 + var6.nextInt(5) - var6.nextInt(5)
                  ) {
                     var14 = var16 + var6.nextInt(5) - var6.nextInt(5);
                  }
               }
            }
         }
      }
   }
}
