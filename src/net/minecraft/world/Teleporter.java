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
   private static final long a = 136533044238811L;

   public Teleporter(WorldServer var1) {
      this.worldServerInstance = var1;
      this.random = new Random(var1.getSeed());
   }

   public void placeInPortal(Entity var1, float var2) {
      long var3 = a ^ 96545537550479L;
      if (this.worldServerInstance.provider.getDimensionId() != 1) {
         if (!this.placeInExistingPortal(var1, var2)) {
            this.makePortal(var1);
            this.placeInExistingPortal(var1, var2);
         }
      } else {
         int var6 = MathHelper.floor_double(var1.posX);
         int var7 = MathHelper.floor_double(var1.posY) - 1;
         long var10000 = 4919567686191631466L ^ var3;
         int var8 = MathHelper.floor_double(var1.posZ);
         byte var9 = 1;
         byte var10 = 0;

         for (int var11 = -2; var11 <= 2; var11++) {
            for (int var12 = -2; var12 <= 2; var12++) {
               for (int var13 = -1; var13 < 3; var13++) {
                  int var14 = var6 + var12 * var9 + var11 * var10;
                  int var15 = var7 + var13;
                  int var16 = var8 + var12 * var10 - var11 * var9;
                  boolean var17 = var13 < 0;
                  this.worldServerInstance
                     .setBlockState(new BlockPos(var14, var15, var16), var17 ? Blocks.obsidian.getDefaultState() : Blocks.air.getDefaultState());
               }
            }
         }

         var1.setLocationAndAngles(var6, var7, var8, var1.rotationYaw, 0.0F);
         var1.motionX = var1.motionY = var1.motionZ = 0.0;
      }
   }

   public boolean placeInExistingPortal(Entity var1, float var2) {
      double var7 = -1.0;
      int var9 = MathHelper.floor_double(var1.posX);
      int var10 = MathHelper.floor_double(var1.posZ);
      boolean var11 = true;
      BlockPos var12 = BlockPos.ORIGIN;
      long var13 = ChunkCoordIntPair.chunkXZ2Int(var9, var10);
      if (this.destinationCoordinateCache.containsItem(var13)) {
         Teleporter.PortalPosition var15 = this.destinationCoordinateCache.getValueByKey(var13);
         var7 = 0.0;
         var12 = var15;
         var15.lastUpdateTime = this.worldServerInstance.getTotalWorldTime();
         var11 = false;
      } else {
         BlockPos var33 = new BlockPos(var1);

         for (int var16 = -128; var16 <= 128; var16++) {
            for (int var18 = -128; var18 <= 128; var18++) {
               BlockPos var19 = var33.add(var16, this.worldServerInstance.getActualHeight() - 1 - var33.getY(), var18);

               while (var19.getY() >= 0) {
                  BlockPos var17 = var19.down();
                  if (this.worldServerInstance.getBlockState(var19).getBlock() == Blocks.portal) {
                     while (this.worldServerInstance.getBlockState(var17 = var19.down()).getBlock() == Blocks.portal) {
                        var19 = var17;
                     }

                     double var20 = var19.distanceSq(var33);
                     if (var7 < 0.0 || var20 < var7) {
                        var7 = var20;
                        var12 = var19;
                     }
                  }

                  var19 = var17;
               }
            }
         }
      }

      if (var7 >= 0.0) {
         if (var11) {
            this.destinationCoordinateCache.add(var13, new Teleporter.PortalPosition(var12, this.worldServerInstance.getTotalWorldTime()));
            this.destinationCoordinateKeys.add(var13);
         }

         double var34 = var12.getX() + 0.5;
         double var35 = var12.getY() + 0.5;
         double var37 = var12.getZ() + 0.5;
         BlockPattern.PatternHelper var21 = Blocks.portal.func_181089_f(this.worldServerInstance, var12);
         boolean var22 = var21.getFinger().rotateY().getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE;
         double var23 = var21.getFinger().getAxis() == EnumFacing.Axis.X ? var21.getPos().getZ() : var21.getPos().getX();
         var35 = var21.getPos().getY() + 1 - var1.func_181014_aG().yCoord * var21.func_181119_e();
         if (var22) {
            var23++;
         }

         if (var21.getFinger().getAxis() == EnumFacing.Axis.X) {
            var37 = var23 + (1.0 - var1.func_181014_aG().xCoord) * var21.func_181118_d() * var21.getFinger().rotateY().getAxisDirection().getOffset();
         } else {
            var34 = var23 + (1.0 - var1.func_181014_aG().xCoord) * var21.func_181118_d() * var21.getFinger().rotateY().getAxisDirection().getOffset();
         }

         float var25 = 0.0F;
         float var26 = 0.0F;
         float var27 = 0.0F;
         float var28 = 0.0F;
         if (var21.getFinger().getOpposite() == var1.getTeleportDirection()) {
            var25 = 1.0F;
            var26 = 1.0F;
         } else if (var21.getFinger().getOpposite() == var1.getTeleportDirection().getOpposite()) {
            var25 = -1.0F;
            var26 = -1.0F;
         } else if (var21.getFinger().getOpposite() == var1.getTeleportDirection().rotateY()) {
            var27 = 1.0F;
            var28 = -1.0F;
         } else {
            var27 = -1.0F;
            var28 = 1.0F;
         }

         double var29 = var1.motionX;
         double var31 = var1.motionZ;
         var1.motionX = var29 * var25 + var31 * var28;
         var1.motionZ = var29 * var27 + var31 * var26;
         var1.rotationYaw = var2 - var1.getTeleportDirection().getOpposite().getHorizontalIndex() * 90 + var21.getFinger().getHorizontalIndex() * 90;
         var1.setLocationAndAngles(var34, var35, var37, var1.rotationYaw, var1.rotationPitch);
         return true;
      } else {
         return false;
      }
   }

   public boolean makePortal(Entity var1) {
      byte var5 = 16;
      double var6 = -1.0;
      int var8 = MathHelper.floor_double(var1.posX);
      int var9 = MathHelper.floor_double(var1.posY);
      int var10 = MathHelper.floor_double(var1.posZ);
      int var11 = var8;
      int var12 = var9;
      int var13 = var10;
      int var14 = 0;
      int var15 = this.random.nextInt(4);
      BlockPos.MutableBlockPos var16 = new BlockPos.MutableBlockPos();

      for (int var17 = var8 - var5; var17 <= var8 + var5; var17++) {
         double var18 = var17 + 0.5 - var1.posX;

         for (int var20 = var10 - var5; var20 <= var10 + var5; var20++) {
            double var21 = var20 + 0.5 - var1.posZ;

            label294:
            for (int var23 = this.worldServerInstance.getActualHeight() - 1; var23 >= 0; var23--) {
               if (this.worldServerInstance.isAirBlock(var16.set(var17, var23, var20))) {
                  while (var23 > 0 && this.worldServerInstance.isAirBlock(var16.set(var17, var23 - 1, var20))) {
                     var23--;
                  }

                  for (int var24 = var15; var24 < var15 + 4; var24++) {
                     int var25 = var24 % 2;
                     int var26 = 1 - var25;
                     if (var24 % 4 >= 2) {
                        var25 = -var25;
                        var26 = -var26;
                     }

                     for (int var27 = 0; var27 < 3; var27++) {
                        for (int var28 = 0; var28 < 4; var28++) {
                           for (int var29 = -1; var29 < 4; var29++) {
                              int var30 = var17 + (var28 - 1) * var25 + var27 * var26;
                              int var31 = var23 + var29;
                              int var32 = var20 + (var28 - 1) * var26 - var27 * var25;
                              var16.set(var30, var31, var32);
                              if (var29 < 0 && !this.worldServerInstance.getBlockState(var16).getBlock().getMaterial().isSolid()
                                 || var29 >= 0 && !this.worldServerInstance.isAirBlock(var16)) {
                                 continue label294;
                              }
                           }
                        }
                     }

                     double var58 = var23 + 0.5 - var1.posY;
                     double var68 = var18 * var18 + var58 * var58 + var21 * var21;
                     if (var6 < 0.0 || var68 < var6) {
                        var6 = var68;
                        var11 = var17;
                        var12 = var23;
                        var13 = var20;
                        var14 = var24 % 4;
                     }
                  }
               }
            }
         }
      }

      if (var6 < 0.0) {
         for (int var34 = var8 - var5; var34 <= var8 + var5; var34++) {
            double var36 = var34 + 0.5 - var1.posX;

            for (int var38 = var10 - var5; var38 <= var10 + var5; var38++) {
               double var40 = var38 + 0.5 - var1.posZ;

               label231:
               for (int var43 = this.worldServerInstance.getActualHeight() - 1; var43 >= 0; var43--) {
                  if (this.worldServerInstance.isAirBlock(var16.set(var34, var43, var38))) {
                     while (var43 > 0 && this.worldServerInstance.isAirBlock(var16.set(var34, var43 - 1, var38))) {
                        var43--;
                     }

                     for (int var46 = var15; var46 < var15 + 2; var46++) {
                        int var50 = var46 % 2;
                        int var54 = 1 - var50;

                        for (int var59 = 0; var59 < 4; var59++) {
                           for (int var64 = -1; var64 < 4; var64++) {
                              int var69 = var34 + (var59 - 1) * var50;
                              int var73 = var43 + var64;
                              int var74 = var38 + (var59 - 1) * var54;
                              var16.set(var69, var73, var74);
                              if (var64 < 0 && !this.worldServerInstance.getBlockState(var16).getBlock().getMaterial().isSolid()
                                 || var64 >= 0 && !this.worldServerInstance.isAirBlock(var16)) {
                                 continue label231;
                              }
                           }
                        }

                        double var60 = var43 + 0.5 - var1.posY;
                        double var70 = var36 * var36 + var60 * var60 + var40 * var40;
                        if (var6 < 0.0 || var70 < var6) {
                           var6 = var70;
                           var11 = var34;
                           var12 = var43;
                           var13 = var38;
                           var14 = var46 % 2;
                        }
                     }
                  }
               }
            }
         }
      }

      int var35 = var11;
      int var37 = var12;
      int var19 = var13;
      int var39 = var14 % 2;
      int var41 = 1 - var39;
      if (var14 % 4 >= 2) {
         var39 = -var39;
         var41 = -var41;
      }

      if (var6 < 0.0) {
         var12 = MathHelper.clamp_int(var12, 70, this.worldServerInstance.getActualHeight() - 10);
         var37 = var12;

         for (int var22 = -1; var22 <= 1; var22++) {
            for (int var44 = 1; var44 < 3; var44++) {
               for (int var47 = -1; var47 < 3; var47++) {
                  int var51 = var35 + (var44 - 1) * var39 + var22 * var41;
                  int var55 = var37 + var47;
                  int var61 = var19 + (var44 - 1) * var41 - var22 * var39;
                  boolean var65 = var47 < 0;
                  this.worldServerInstance
                     .setBlockState(new BlockPos(var51, var55, var61), var65 ? Blocks.obsidian.getDefaultState() : Blocks.air.getDefaultState());
               }
            }
         }
      }

      IBlockState var42 = Blocks.portal.getDefaultState().withProperty(BlockPortal.AXIS, var39 != 0 ? EnumFacing.Axis.X : EnumFacing.Axis.Z);

      for (int var45 = 0; var45 < 4; var45++) {
         for (int var48 = 0; var48 < 4; var48++) {
            for (int var52 = -1; var52 < 4; var52++) {
               int var56 = var35 + (var48 - 1) * var39;
               int var62 = var37 + var52;
               int var66 = var19 + (var48 - 1) * var41;
               boolean var71 = var48 == 0 || var48 == 3 || var52 == -1 || var52 == 3;
               this.worldServerInstance.setBlockState(new BlockPos(var56, var62, var66), var71 ? Blocks.obsidian.getDefaultState() : var42, 2);
            }
         }

         for (int var49 = 0; var49 < 4; var49++) {
            for (int var53 = -1; var53 < 4; var53++) {
               int var57 = var35 + (var49 - 1) * var39;
               int var63 = var37 + var53;
               int var67 = var19 + (var49 - 1) * var41;
               BlockPos var72 = new BlockPos(var57, var63, var67);
               this.worldServerInstance.notifyNeighborsOfStateChange(var72, this.worldServerInstance.getBlockState(var72).getBlock());
            }
         }
      }

      return true;
   }

   public void removeStalePortalLocations(long var1) {
      if (var1 % 100L == 0L) {
         Iterator var6 = this.destinationCoordinateKeys.iterator();
         long var7 = var1 - 300L;

         while (var6.hasNext()) {
            Long var9 = (Long)var6.next();
            Teleporter.PortalPosition var10 = this.destinationCoordinateCache.getValueByKey(var9);
            if (var10 == null || var10.lastUpdateTime < var7) {
               var6.remove();
               this.destinationCoordinateCache.remove(var9);
            }
         }
      }
   }

   public class PortalPosition extends BlockPos {
      public long lastUpdateTime;

      public PortalPosition(BlockPos var2, long var3) {
         super(var2.getX(), var2.getY(), var2.getZ());
         this.lastUpdateTime = var3;
      }
   }
}
