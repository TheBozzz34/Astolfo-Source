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

   public int findChunksForSpawning(WorldServer worldServerIn, boolean spawnHostileMobs, boolean spawnPeacefulMobs, boolean bl) {
      long l = a ^ 98560144167614L;
      if (!spawnHostileMobs && !spawnPeacefulMobs) {
         return 0;
      }

      this.eligibleChunksForSpawning.clear();
      int i = 0;

      for (EntityPlayer entityPlayer : worldServerIn.playerEntities) {
         if (!entityPlayer.isSpectator()) {
            int j = MathHelper.floor_double(entityPlayer.posX / 16.0);
            int k = MathHelper.floor_double(entityPlayer.posZ / 16.0);
            long m = 3408594515062740785L ^ l;
            byte b = 8;

            for (int n = -b; n <= b; n++) {
               for (int i2 = -b; i2 <= b; i2++) {
                  boolean bl2 = n == -b || n == b || i2 == -b || i2 == b;
                  ChunkCoordIntPair chunkCoordIntPair = new ChunkCoordIntPair(n + j, i2 + k);
                  if (!this.eligibleChunksForSpawning.contains(chunkCoordIntPair)) {
                     i++;
                     if (!bl2 && worldServerIn.getWorldBorder().contains(chunkCoordIntPair)) {
                        this.eligibleChunksForSpawning.add(chunkCoordIntPair);
                     }
                  }
               }
            }
         }
      }

      int i3 = 0;
      BlockPos blockPos = worldServerIn.getSpawnPoint();

      for (EnumCreatureType enumCreatureType : EnumCreatureType.values()) {
         if ((!enumCreatureType.getPeacefulCreature() || spawnPeacefulMobs) && (enumCreatureType.getPeacefulCreature() || spawnHostileMobs) && (!enumCreatureType.getAnimal() || bl)) {
            int i4 = worldServerIn.countEntities(enumCreatureType.getCreatureClass());
            int i5 = enumCreatureType.getMaxNumberOfCreature() * i / MOB_COUNT_DIV;
            if (i4 <= i5) {
               label121:
               for (ChunkCoordIntPair chunkCoordIntPair2 : this.eligibleChunksForSpawning) {
                  BlockPos blockPos2 = getRandomChunkPosition(worldServerIn, chunkCoordIntPair2.chunkXPos, chunkCoordIntPair2.chunkZPos);
                  int i6 = blockPos2.getX();
                  int i7 = blockPos2.getY();
                  int i8 = blockPos2.getZ();
                  Block block = worldServerIn.getBlockState(blockPos2).getBlock();
                  if (!block.isNormalCube()) {
                     int i9 = 0;

                     for (int i10 = 0; i10 < 3; i10++) {
                        int i11 = i6;
                        int i12 = i7;
                        long l2 = 3006838497115828214L ^ l;
                        int i13 = i8;
                        byte b2 = 6;
                        BiomeGenBase.SpawnListEntry spawnListEntry = null;
                        IEntityLivingData iEntityLivingData = null;

                        for (int i14 = 0; i14 < 4; i14++) {
                           i11 += worldServerIn.rand.nextInt(b2) - worldServerIn.rand.nextInt(b2);
                           i12 += worldServerIn.rand.nextInt(1) - worldServerIn.rand.nextInt(1);
                           i13 += worldServerIn.rand.nextInt(b2) - worldServerIn.rand.nextInt(b2);
                           BlockPos blockPos3 = new BlockPos(i11, i12, i13);
                           float f = i11 + 0.5F;
                           float g = i13 + 0.5F;
                           if (!worldServerIn.isAnyPlayerWithinRangeAt(f, i12, g, 24.0) && blockPos.distanceSq(f, i12, g) >= 576.0) {
                              if (spawnListEntry == null) {
                                 spawnListEntry = worldServerIn.getSpawnListEntryForTypeAt(enumCreatureType, blockPos3);
                                 if (spawnListEntry == null) {
                                    break;
                                 }
                              }

                              if (worldServerIn.canCreatureTypeSpawnHere(enumCreatureType, spawnListEntry, blockPos3)
                                 && canCreatureTypeSpawnAtLocation(EntitySpawnPlacementRegistry.getPlacementForEntity(spawnListEntry.entityClass), worldServerIn, blockPos3)
                                 )
                               {
                                 EntityLiving entityLiving;
                                 try {
                                    entityLiving = spawnListEntry.entityClass.getConstructor(World.class).newInstance(worldServerIn);
                                 } catch (Exception exception) {
                                    exception.printStackTrace();
                                    return i3;
                                 }

                                 entityLiving.setLocationAndAngles(f, i12, g, worldServerIn.rand.nextFloat() * 360.0F, 0.0F);
                                 if (entityLiving.getCanSpawnHere() && entityLiving.isNotColliding()) {
                                    iEntityLivingData = entityLiving.onInitialSpawn(worldServerIn.getDifficultyForLocation(new BlockPos(entityLiving)), iEntityLivingData);
                                    if (entityLiving.isNotColliding()) {
                                       i9++;
                                       worldServerIn.spawnEntityInWorld(entityLiving);
                                    }

                                    if (i9 >= entityLiving.getMaxSpawnedInChunk()) {
                                       continue label121;
                                    }
                                 }

                                 i3 += i9;
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      return i3;
   }

   public static BlockPos getRandomChunkPosition(World worldIn, int x, int z) {
      Chunk chunk = worldIn.getChunkFromChunkCoords(x, z);
      int i = x * 16 + worldIn.rand.nextInt(16);
      int j = z * 16 + worldIn.rand.nextInt(16);
      int k = MathHelper.roundUp(chunk.getHeight(new BlockPos(i, 0, j)) + 1, 16);
      int l = worldIn.rand.nextInt(k > 0 ? k : chunk.getTopFilledSegment() + 16 - 1);
      return new BlockPos(i, l, j);
   }

   public static boolean canCreatureTypeSpawnAtLocation(EntityLiving.SpawnPlacementType spawnPlacementTypeIn, World worldIn, BlockPos pos) {
      if (!worldIn.getWorldBorder().contains(pos)) {
         return false;
      }

      Block block = worldIn.getBlockState(pos).getBlock();
      if (spawnPlacementTypeIn == EntityLiving.SpawnPlacementType.IN_WATER) {
         return block.getMaterial().isLiquid()
            && worldIn.getBlockState(pos.down()).getBlock().getMaterial().isLiquid()
            && !worldIn.getBlockState(pos.up()).getBlock().isNormalCube();
      }

      BlockPos blockPos = pos.down();
      if (!World.doesBlockHaveSolidTopSurface(worldIn, blockPos)) {
         return false;
      }

      Block block2 = worldIn.getBlockState(blockPos).getBlock();
      boolean bl = block2 != Blocks.bedrock && block2 != Blocks.barrier;
      return bl && !block.isNormalCube() && !block.getMaterial().isLiquid() && !worldIn.getBlockState(pos.up()).getBlock().isNormalCube();
   }

   public static void performWorldGenSpawning(World worldIn, BiomeGenBase biomeIn, int i, int j, int k, int l, Random randomIn) {
      List list = biomeIn.getSpawnableList(EnumCreatureType.CREATURE);
      if (!list.isEmpty()) {
         while (randomIn.nextFloat() < biomeIn.getSpawningChance()) {
            BiomeGenBase.SpawnListEntry spawnListEntry = (net.minecraft.world.biome.BiomeGenBase.SpawnListEntry)(WeightedRandom.getRandomItem(worldIn.rand, list));
            int m = spawnListEntry.minGroupCount + randomIn.nextInt(1 + spawnListEntry.maxGroupCount - spawnListEntry.minGroupCount);
            IEntityLivingData iEntityLivingData = null;
            int n = i + randomIn.nextInt(k);
            int i2 = j + randomIn.nextInt(l);
            int i3 = n;
            int i4 = i2;

            for (int i5 = 0; i5 < m; i5++) {
               boolean bl = false;

               for (int i6 = 0; !bl && i6 < 4; i6++) {
                  BlockPos blockPos = worldIn.getTopSolidOrLiquidBlock(new BlockPos(n, 0, i2));
                  if (canCreatureTypeSpawnAtLocation(EntityLiving.SpawnPlacementType.ON_GROUND, worldIn, blockPos)) {
                     EntityLiving entityLiving;
                     try {
                        entityLiving = spawnListEntry.entityClass.getConstructor(World.class).newInstance(worldIn);
                     } catch (Exception exception) {
                        exception.printStackTrace();
                        continue;
                     }

                     entityLiving.setLocationAndAngles(n + 0.5F, blockPos.getY(), i2 + 0.5F, randomIn.nextFloat() * 360.0F, 0.0F);
                     worldIn.spawnEntityInWorld(entityLiving);
                     iEntityLivingData = entityLiving.onInitialSpawn(worldIn.getDifficultyForLocation(new BlockPos(entityLiving)), iEntityLivingData);
                     bl = true;
                  }

                  n += randomIn.nextInt(5) - randomIn.nextInt(5);

                  for (i2 += randomIn.nextInt(5) - randomIn.nextInt(5);
                     n < i || n >= i + k || i2 < j || i2 >= j + k;
                     i2 = i4 + randomIn.nextInt(5) - randomIn.nextInt(5)
                  ) {
                     n = i3 + randomIn.nextInt(5) - randomIn.nextInt(5);
                  }
               }
            }
         }
      }
   }
}
