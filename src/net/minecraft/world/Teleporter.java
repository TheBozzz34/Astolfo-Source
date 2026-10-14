package net.minecraft.world;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.LongHashMap;
import net.minecraft.util.MathHelper;

public class Teleporter {
   private final WorldServer worldServerInstance;
   private final Random random;
   private final LongHashMap<Teleporter.PortalPosition> destinationCoordinateCache = new LongHashMap<>();
   private final List<Long> destinationCoordinateKeys = Lists.newArrayList();

   public Teleporter(WorldServer worldIn) {
      this.worldServerInstance = worldIn;
      this.random = new Random(worldIn.getSeed());
   }

   public void placeInPortal(Entity entityIn, float rotationYaw) {
      if (this.worldServerInstance.provider.getDimensionId() != 1) {
         if (!this.placeInExistingPortal(entityIn, rotationYaw)) {
            this.makePortal(entityIn);
            this.placeInExistingPortal(entityIn, rotationYaw);
         }
      } else {
         int i = MathHelper.floor_double(entityIn.posX);
         int j = MathHelper.floor_double(entityIn.posY) - 1;
         int k = MathHelper.floor_double(entityIn.posZ);
         byte b = 1;
         byte b2 = 0;

         for (int l = -2; l <= 2; l++) {
            for (int m = -2; m <= 2; m++) {
               for (int n = -1; n < 3; n++) {
                  int i2 = i + m * b + l * b2;
                  int i3 = j + n;
                  int i4 = k + m * b2 - l * b;
                  boolean bl = n < 0;
                  this.worldServerInstance
                     .setBlockState(new BlockPos(i2, i3, i4), bl ? Blocks.obsidian.getDefaultState() : Blocks.air.getDefaultState());
               }
            }
         }

         entityIn.setLocationAndAngles(i, j, k, entityIn.rotationYaw, 0.0F);
         entityIn.motionX = entityIn.motionY = entityIn.motionZ = 0.0;
      }
   }

   public boolean placeInExistingPortal(Entity entityIn, float rotationYaw) {
      double d = -1.0;
      int i = MathHelper.floor_double(entityIn.posX);
      int j = MathHelper.floor_double(entityIn.posZ);
      boolean bl = true;
      BlockPos blockPos = BlockPos.ORIGIN;
      long l = ChunkCoordIntPair.chunkXZ2Int(i, j);
      if (this.destinationCoordinateCache.containsItem(l)) {
         Teleporter.PortalPosition portalPosition = this.destinationCoordinateCache.getValueByKey(l);
         d = 0.0;
         blockPos = portalPosition;
         portalPosition.lastUpdateTime = this.worldServerInstance.getTotalWorldTime();
         bl = false;
      } else {
         BlockPos blockPos2 = new BlockPos(entityIn);

         for (int k = -128; k <= 128; k++) {
            for (int m = -128; m <= 128; m++) {
               BlockPos blockPos3 = blockPos2.add(k, this.worldServerInstance.getActualHeight() - 1 - blockPos2.getY(), m);

               while (blockPos3.getY() >= 0) {
                  BlockPos blockPos4 = blockPos3.down();
                  if (this.worldServerInstance.getBlockState(blockPos3).getBlock() == Blocks.portal) {
                     while (this.worldServerInstance.getBlockState(blockPos4 = blockPos3.down()).getBlock() == Blocks.portal) {
                        blockPos3 = blockPos4;
                     }

                     double e = blockPos3.distanceSq(blockPos2);
                     if (d < 0.0 || e < d) {
                        d = e;
                        blockPos = blockPos3;
                     }
                  }

                  blockPos3 = blockPos4;
               }
            }
         }
      }

      if (d >= 0.0) {
         if (bl) {
            this.destinationCoordinateCache.add(l, new Teleporter.PortalPosition(blockPos, this.worldServerInstance.getTotalWorldTime()));
            this.destinationCoordinateKeys.add(l);
         }

         double d2 = blockPos.getX() + 0.5;
         double d3 = blockPos.getY() + 0.5;
         double d4 = blockPos.getZ() + 0.5;
         BlockPattern.PatternHelper patternHelper = Blocks.portal.func_181089_f(this.worldServerInstance, blockPos);
         boolean bl2 = patternHelper.getFinger().rotateY().getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE;
         double d5 = patternHelper.getFinger().getAxis() == EnumFacing.Axis.X ? patternHelper.getPos().getZ() : patternHelper.getPos().getX();
         d3 = patternHelper.getPos().getY() + 1 - entityIn.func_181014_aG().yCoord * patternHelper.func_181119_e();
         if (bl2) {
            d5++;
         }

         if (patternHelper.getFinger().getAxis() == EnumFacing.Axis.X) {
            d4 = d5 + (1.0 - entityIn.func_181014_aG().xCoord) * patternHelper.func_181118_d() * patternHelper.getFinger().rotateY().getAxisDirection().getOffset();
         } else {
            d2 = d5 + (1.0 - entityIn.func_181014_aG().xCoord) * patternHelper.func_181118_d() * patternHelper.getFinger().rotateY().getAxisDirection().getOffset();
         }

         float f = 0.0F;
         float g = 0.0F;
         float h = 0.0F;
         float f2 = 0.0F;
         if (patternHelper.getFinger().getOpposite() == entityIn.getTeleportDirection()) {
            f = 1.0F;
            g = 1.0F;
         } else if (patternHelper.getFinger().getOpposite() == entityIn.getTeleportDirection().getOpposite()) {
            f = -1.0F;
            g = -1.0F;
         } else if (patternHelper.getFinger().getOpposite() == entityIn.getTeleportDirection().rotateY()) {
            h = 1.0F;
            f2 = -1.0F;
         } else {
            h = -1.0F;
            f2 = 1.0F;
         }

         double d6 = entityIn.motionX;
         double d7 = entityIn.motionZ;
         entityIn.motionX = d6 * f + d7 * f2;
         entityIn.motionZ = d6 * h + d7 * g;
         entityIn.rotationYaw = rotationYaw
            - entityIn.getTeleportDirection().getOpposite().getHorizontalIndex() * 90
            + patternHelper.getFinger().getHorizontalIndex() * 90;
         entityIn.setLocationAndAngles(d2, d3, d4, entityIn.rotationYaw, entityIn.rotationPitch);
         return true;
      } else {
         return false;
      }
   }

   public boolean makePortal(Entity entityIn) {
      byte b = 16;
      double d = -1.0;
      int i = MathHelper.floor_double(entityIn.posX);
      int j = MathHelper.floor_double(entityIn.posY);
      int k = MathHelper.floor_double(entityIn.posZ);
      int l = i;
      int m = j;
      int n = k;
      int i2 = 0;
      int i3 = this.random.nextInt(4);
      BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

      for (int i4 = i - b; i4 <= i + b; i4++) {
         double e = i4 + 0.5 - entityIn.posX;

         for (int i5 = k - b; i5 <= k + b; i5++) {
            double d2 = i5 + 0.5 - entityIn.posZ;

            label294:
            for (int i6 = this.worldServerInstance.getActualHeight() - 1; i6 >= 0; i6--) {
               if (this.worldServerInstance.isAirBlock(mutableBlockPos.set(i4, i6, i5))) {
                  while (i6 > 0 && this.worldServerInstance.isAirBlock(mutableBlockPos.set(i4, i6 - 1, i5))) {
                     i6--;
                  }

                  for (int i7 = i3; i7 < i3 + 4; i7++) {
                     int i8 = i7 % 2;
                     int i9 = 1 - i8;
                     if (i7 % 4 >= 2) {
                        i8 = -i8;
                        i9 = -i9;
                     }

                     for (int i10 = 0; i10 < 3; i10++) {
                        for (int i11 = 0; i11 < 4; i11++) {
                           for (int i12 = -1; i12 < 4; i12++) {
                              int i13 = i4 + (i11 - 1) * i8 + i10 * i9;
                              int i14 = i6 + i12;
                              int i15 = i5 + (i11 - 1) * i9 - i10 * i8;
                              mutableBlockPos.set(i13, i14, i15);
                              if (i12 < 0 && !this.worldServerInstance.getBlockState(mutableBlockPos).getBlock().getMaterial().isSolid()
                                 || i12 >= 0 && !this.worldServerInstance.isAirBlock(mutableBlockPos)) {
                                 continue label294;
                              }
                           }
                        }
                     }

                     double d3 = i6 + 0.5 - entityIn.posY;
                     double d4 = e * e + d3 * d3 + d2 * d2;
                     if (d < 0.0 || d4 < d) {
                        d = d4;
                        l = i4;
                        m = i6;
                        n = i5;
                        i2 = i7 % 4;
                     }
                  }
               }
            }
         }
      }

      if (d < 0.0) {
         for (int i16 = i - b; i16 <= i + b; i16++) {
            double d5 = i16 + 0.5 - entityIn.posX;

            for (int i17 = k - b; i17 <= k + b; i17++) {
               double d6 = i17 + 0.5 - entityIn.posZ;

               label231:
               for (int i18 = this.worldServerInstance.getActualHeight() - 1; i18 >= 0; i18--) {
                  if (this.worldServerInstance.isAirBlock(mutableBlockPos.set(i16, i18, i17))) {
                     while (i18 > 0 && this.worldServerInstance.isAirBlock(mutableBlockPos.set(i16, i18 - 1, i17))) {
                        i18--;
                     }

                     for (int i19 = i3; i19 < i3 + 2; i19++) {
                        int i20 = i19 % 2;
                        int i21 = 1 - i20;

                        for (int i22 = 0; i22 < 4; i22++) {
                           for (int i23 = -1; i23 < 4; i23++) {
                              int i24 = i16 + (i22 - 1) * i20;
                              int i25 = i18 + i23;
                              int i26 = i17 + (i22 - 1) * i21;
                              mutableBlockPos.set(i24, i25, i26);
                              if (i23 < 0 && !this.worldServerInstance.getBlockState(mutableBlockPos).getBlock().getMaterial().isSolid()
                                 || i23 >= 0 && !this.worldServerInstance.isAirBlock(mutableBlockPos)) {
                                 continue label231;
                              }
                           }
                        }

                        double d7 = i18 + 0.5 - entityIn.posY;
                        double d8 = d5 * d5 + d7 * d7 + d6 * d6;
                        if (d < 0.0 || d8 < d) {
                           d = d8;
                           l = i16;
                           m = i18;
                           n = i17;
                           i2 = i19 % 2;
                        }
                     }
                  }
               }
            }
         }
      }

      int i27 = l;
      int i28 = m;
      int i29 = n;
      int i30 = i2 % 2;
      int i31 = 1 - i30;
      if (i2 % 4 >= 2) {
         i30 = -i30;
         i31 = -i31;
      }

      if (d < 0.0) {
         m = MathHelper.clamp_int(m, 70, this.worldServerInstance.getActualHeight() - 10);
         i28 = m;

         for (int i32 = -1; i32 <= 1; i32++) {
            for (int i33 = 1; i33 < 3; i33++) {
               for (int i34 = -1; i34 < 3; i34++) {
                  int i35 = i27 + (i33 - 1) * i30 + i32 * i31;
                  int i36 = i28 + i34;
                  int i37 = i29 + (i33 - 1) * i31 - i32 * i30;
                  boolean bl = i34 < 0;
                  this.worldServerInstance
                     .setBlockState(new BlockPos(i35, i36, i37), bl ? Blocks.obsidian.getDefaultState() : Blocks.air.getDefaultState());
               }
            }
         }
      }

      IBlockState iBlockState = Blocks.portal.getDefaultState().withProperty(BlockPortal.AXIS, i30 != 0 ? EnumFacing.Axis.X : EnumFacing.Axis.Z);

      for (int i38 = 0; i38 < 4; i38++) {
         for (int i39 = 0; i39 < 4; i39++) {
            for (int i40 = -1; i40 < 4; i40++) {
               int i41 = i27 + (i39 - 1) * i30;
               int i42 = i28 + i40;
               int i43 = i29 + (i39 - 1) * i31;
               boolean bl2 = i39 == 0 || i39 == 3 || i40 == -1 || i40 == 3;
               this.worldServerInstance.setBlockState(new BlockPos(i41, i42, i43), bl2 ? Blocks.obsidian.getDefaultState() : iBlockState, 2);
            }
         }

         for (int i44 = 0; i44 < 4; i44++) {
            for (int i45 = -1; i45 < 4; i45++) {
               int i46 = i27 + (i44 - 1) * i30;
               int i47 = i28 + i45;
               int i48 = i29 + (i44 - 1) * i31;
               BlockPos blockPos = new BlockPos(i46, i47, i48);
               this.worldServerInstance.notifyNeighborsOfStateChange(blockPos, this.worldServerInstance.getBlockState(blockPos).getBlock());
            }
         }
      }

      return true;
   }

   public void removeStalePortalLocations(long worldTime) {
      if (worldTime % 100L == 0L) {
         Iterator iterator2 = this.destinationCoordinateKeys.iterator();
         long l = worldTime - 300L;

         while (iterator2.hasNext()) {
            Long long2 = (Long)iterator2.next();
            Teleporter.PortalPosition portalPosition = this.destinationCoordinateCache.getValueByKey(long2);
            if (portalPosition == null || portalPosition.lastUpdateTime < l) {
               iterator2.remove();
               this.destinationCoordinateCache.remove(long2);
            }
         }
      }
   }

   public class PortalPosition extends BlockPos {
      public long lastUpdateTime;

      public PortalPosition(BlockPos pos, long lastUpdate) {
         super(pos.getX(), pos.getY(), pos.getZ());
         this.lastUpdateTime = lastUpdate;
      }
   }
}
