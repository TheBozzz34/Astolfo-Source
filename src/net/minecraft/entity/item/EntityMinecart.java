package net.minecraft.entity.item;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.BlockRailPowered;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityMinecartCommandBlock;
import net.minecraft.entity.ai.EntityMinecartMobSpawner;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.IWorldNameable;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public abstract class EntityMinecart extends Entity implements IWorldNameable {
   private boolean isInReverse;
   private String entityName;
   private static final int[][][] matrix = new int[][][]{
      {{0, 0, -1}, {0, 0, 1}},
      {{-1, 0, 0}, {1, 0, 0}},
      {{-1, -1, 0}, {1, 0, 0}},
      {{-1, 0, 0}, {1, -1, 0}},
      {{0, 0, -1}, {0, -1, 1}},
      {{0, -1, -1}, {0, 0, 1}},
      {{0, 0, 1}, {1, 0, 0}},
      {{0, 0, 1}, {-1, 0, 0}},
      {{0, 0, -1}, {-1, 0, 0}},
      {{0, 0, -1}, {1, 0, 0}}
   };
   private int turnProgress;
   private double minecartX;
   private double minecartY;
   private double minecartZ;
   private double minecartYaw;
   private double minecartPitch;
   private double velocityX;
   private double velocityY;
   private double velocityZ;
   private static final long bb = 122433007979174L;

   public EntityMinecart(World var1) {
      super(var1);
      this.preventEntitySpawning = true;
      this.setSize(0.98F, 0.7F);
   }

   public static EntityMinecart getMinecart(World var0, double var1, double var3, double var5, EntityMinecart.EnumMinecartType var7) {
      switch (var7) {
         case CHEST:
            return new EntityMinecartChest(var0, var1, var3, var5);
         case FURNACE:
            return new EntityMinecartFurnace(var0, var1, var3, var5);
         case TNT:
            return new EntityMinecartTNT(var0, var1, var3, var5);
         case SPAWNER:
            return new EntityMinecartMobSpawner(var0, var1, var3, var5);
         case HOPPER:
            return new EntityMinecartHopper(var0, var1, var3, var5);
         case COMMAND_BLOCK:
            return new EntityMinecartCommandBlock(var0, var1, var3, var5);
         default:
            return new EntityMinecartEmpty(var0, var1, var3, var5);
      }
   }

   @Override
   public boolean canTriggerWalking() {
      return false;
   }

   @Override
   public void entityInit() {
      this.dataWatcher.addObject(17, new Integer(0));
      this.dataWatcher.addObject(18, new Integer(1));
      this.dataWatcher.addObject(19, new Float(0.0F));
      this.dataWatcher.addObject(20, new Integer(0));
      this.dataWatcher.addObject(21, new Integer(6));
      this.dataWatcher.addObject(22, (byte)0);
   }

   @Override
   public AxisAlignedBB getCollisionBox(Entity var1) {
      return var1.canBePushed() ? var1.getEntityBoundingBox() : null;
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox() {
      return null;
   }

   @Override
   public boolean canBePushed() {
      return true;
   }

   public EntityMinecart(World var1, double var2, double var4, double var6) {
      this(var1);
      this.setPosition(var2, var4, var6);
      this.motionX = 0.0;
      this.motionY = 0.0;
      this.motionZ = 0.0;
      this.prevPosX = var2;
      this.prevPosY = var4;
      this.prevPosZ = var6;
   }

   @Override
   public double getMountedYOffset() {
      return 0.0;
   }

   @Override
   public boolean attackEntityFrom(DamageSource var1, float var2) {
      if (this.worldObj.isRemote || this.isDead) {
         return true;
      }

      if (this.isEntityInvulnerable(var1)) {
         return false;
      }

      this.setRollingDirection(-this.getRollingDirection());
      this.setRollingAmplitude(10);
      this.setBeenAttacked();
      this.setDamage(this.getDamage() + var2 * 10.0F);
      boolean var6 = var1.getEntity() instanceof EntityPlayer && ((EntityPlayer)var1.getEntity()).capabilities.isCreativeMode;
      if (var6 || this.getDamage() > 40.0F) {
         if (this.riddenByEntity != null) {
            this.riddenByEntity.mountEntity((Entity)null);
         }

         if (var6 && !this.hasCustomName()) {
            this.setDead();
         } else {
            this.killMinecart(var1);
         }
      }

      return true;
   }

   public void killMinecart(DamageSource var1) {
      this.setDead();
      if (this.worldObj.getGameRules().getBoolean("doEntityDrops")) {
         ItemStack var5 = new ItemStack(Items.minecart, 1);
         if (this.entityName != null) {
            var5.setStackDisplayName(this.entityName);
         }

         this.entityDropItem(var5, 0.0F);
      }
   }

   @Override
   public void performHurtAnimation() {
      this.setRollingDirection(-this.getRollingDirection());
      this.setRollingAmplitude(10);
      this.setDamage(this.getDamage() + this.getDamage() * 10.0F);
   }

   @Override
   public boolean canBeCollidedWith() {
      return !this.isDead;
   }

   @Override
   public void setDead() {
      super.setDead();
   }

   @Override
   public void onUpdate() {
      if (this.getRollingAmplitude() > 0) {
         this.setRollingAmplitude(this.getRollingAmplitude() - 1);
      }

      if (this.getDamage() > 0.0F) {
         this.setDamage(this.getDamage() - 1.0F);
      }

      if (this.posY < -64.0) {
         this.kill();
      }

      if (!this.worldObj.isRemote && this.worldObj instanceof WorldServer) {
         this.worldObj.theProfiler.startSection("portal");
         MinecraftServer var4 = ((WorldServer)this.worldObj).getMinecraftServer();
         int var5 = this.getMaxInPortalTime();
         if (this.inPortal) {
            if (var4.getAllowNether()) {
               if (this.ridingEntity == null && this.portalCounter++ >= var5) {
                  this.portalCounter = var5;
                  this.timeUntilPortal = this.getPortalCooldown();
                  byte var6;
                  if (this.worldObj.provider.getDimensionId() == -1) {
                     var6 = 0;
                  } else {
                     var6 = -1;
                  }

                  this.travelToDimension(var6);
               }

               this.inPortal = false;
            }
         } else {
            if (this.portalCounter > 0) {
               this.portalCounter -= 4;
            }

            if (this.portalCounter < 0) {
               this.portalCounter = 0;
            }
         }

         if (this.timeUntilPortal > 0) {
            this.timeUntilPortal--;
         }

         this.worldObj.theProfiler.endSection();
      }

      if (this.worldObj.isRemote) {
         if (this.turnProgress > 0) {
            double var17 = this.posX + (this.minecartX - this.posX) / this.turnProgress;
            double var20 = this.posY + (this.minecartY - this.posY) / this.turnProgress;
            double var8 = this.posZ + (this.minecartZ - this.posZ) / this.turnProgress;
            double var10 = MathHelper.wrapAngleTo180_double(this.minecartYaw - this.rotationYaw);
            this.rotationYaw = (float)(this.rotationYaw + var10 / this.turnProgress);
            this.rotationPitch = (float)(this.rotationPitch + (this.minecartPitch - this.rotationPitch) / this.turnProgress);
            this.turnProgress--;
            this.setPosition(var17, var20, var8);
            this.setRotation(this.rotationYaw, this.rotationPitch);
         } else {
            this.setPosition(this.posX, this.posY, this.posZ);
            this.setRotation(this.rotationYaw, this.rotationPitch);
         }
      } else {
         this.prevPosX = this.posX;
         this.prevPosY = this.posY;
         this.prevPosZ = this.posZ;
         this.motionY -= 0.04F;
         int var18 = MathHelper.floor_double(this.posX);
         int var19 = MathHelper.floor_double(this.posY);
         int var21 = MathHelper.floor_double(this.posZ);
         if (BlockRailBase.isRailBlock(this.worldObj, new BlockPos(var18, var19 - 1, var21))) {
            var19--;
         }

         BlockPos var7 = new BlockPos(var18, var19, var21);
         IBlockState var22 = this.worldObj.getBlockState(var7);
         if (BlockRailBase.isRailBlock(var22)) {
            this.func_180460_a(var7, var22);
            if (var22.getBlock() == Blocks.activator_rail) {
               this.onActivatorRailPass(var18, var19, var21, var22.getValue(BlockRailPowered.POWERED));
            }
         } else {
            this.moveDerailedMinecart();
         }

         this.doBlockCollisions();
         this.rotationPitch = 0.0F;
         double var9 = this.prevPosX - this.posX;
         double var11 = this.prevPosZ - this.posZ;
         if (var9 * var9 + var11 * var11 > 0.001) {
            this.rotationYaw = (float)(MathHelper.atan2(var11, var9) * 180.0 / Math.PI);
            if (this.isInReverse) {
               this.rotationYaw += 180.0F;
            }
         }

         double var13 = MathHelper.wrapAngleTo180_float(this.rotationYaw - this.prevRotationYaw);
         if (var13 < -170.0 || var13 >= 170.0) {
            this.rotationYaw += 180.0F;
            this.isInReverse = !this.isInReverse;
         }

         this.setRotation(this.rotationYaw, this.rotationPitch);

         for (Entity var16 : this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().expand(0.2F, 0.0, 0.2F))) {
            if (var16 != this.riddenByEntity && var16.canBePushed() && var16 instanceof EntityMinecart) {
               var16.applyEntityCollision(this);
            }
         }

         if (this.riddenByEntity != null && this.riddenByEntity.isDead) {
            if (this.riddenByEntity.ridingEntity == this) {
               this.riddenByEntity.ridingEntity = null;
            }

            this.riddenByEntity = null;
         }

         this.handleWaterMovement();
      }
   }

   public double getMaximumSpeed() {
      return 0.4;
   }

   public void onActivatorRailPass(int var1, int var2, int var3, boolean var4) {
   }

   public void moveDerailedMinecart() {
      double var4 = this.getMaximumSpeed();
      this.motionX = MathHelper.clamp_double(this.motionX, -var4, var4);
      this.motionZ = MathHelper.clamp_double(this.motionZ, -var4, var4);
      if (this.onGround) {
         this.motionX *= 0.5;
         this.motionY *= 0.5;
         this.motionZ *= 0.5;
      }

      this.moveEntity(this.motionX, this.motionY, this.motionZ);
      if (!this.onGround) {
         this.motionX *= 0.95F;
         this.motionY *= 0.95F;
         this.motionZ *= 0.95F;
      }
   }

   public void func_180460_a(BlockPos var1, IBlockState var2) {
      this.fallDistance = 0.0F;
      Vec3 var7 = this.func_70489_a(this.posX, this.posY, this.posZ);
      this.posY = var1.getY();
      boolean var8 = false;
      boolean var9 = false;
      BlockRailBase var10 = (BlockRailBase)var2.getBlock();
      if (var10 == Blocks.golden_rail) {
         var8 = var2.getValue(BlockRailPowered.POWERED);
         var9 = !var8;
      }

      BlockRailBase.EnumRailDirection var13 = var2.getValue(var10.getShapeProperty());
      switch (var13) {
         case ASCENDING_EAST:
            this.motionX -= 0.0078125;
            this.posY++;
            break;
         case ASCENDING_WEST:
            this.motionX += 0.0078125;
            this.posY++;
            break;
         case ASCENDING_NORTH:
            this.motionZ += 0.0078125;
            this.posY++;
            break;
         case ASCENDING_SOUTH:
            this.motionZ -= 0.0078125;
            this.posY++;
      }

      int[][] var14 = matrix[var13.getMetadata()];
      double var15 = var14[1][0] - var14[0][0];
      double var17 = var14[1][2] - var14[0][2];
      double var19 = Math.sqrt(var15 * var15 + var17 * var17);
      double var21 = this.motionX * var15 + this.motionZ * var17;
      if (var21 < 0.0) {
         var15 = -var15;
         var17 = -var17;
      }

      double var23 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
      if (var23 > 2.0) {
         var23 = 2.0;
      }

      this.motionX = var23 * var15 / var19;
      this.motionZ = var23 * var17 / var19;
      if (this.riddenByEntity instanceof EntityLivingBase) {
         double var25 = ((EntityLivingBase)this.riddenByEntity).moveForward;
         if (var25 > 0.0) {
            double var27 = -Math.sin(this.riddenByEntity.rotationYaw * (float) Math.PI / 180.0F);
            double var29 = Math.cos(this.riddenByEntity.rotationYaw * (float) Math.PI / 180.0F);
            double var31 = this.motionX * this.motionX + this.motionZ * this.motionZ;
            if (var31 < 0.01) {
               this.motionX += var27 * 0.1;
               this.motionZ += var29 * 0.1;
               var9 = false;
            }
         }
      }

      if (var9) {
         double var52 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
         if (var52 < 0.03) {
            this.motionX *= 0.0;
            this.motionY *= 0.0;
            this.motionZ *= 0.0;
         } else {
            this.motionX *= 0.5;
            this.motionY *= 0.0;
            this.motionZ *= 0.5;
         }
      }

      double var53 = 0.0;
      double var55 = var1.getX() + 0.5 + var14[0][0] * 0.5;
      double var56 = var1.getZ() + 0.5 + var14[0][2] * 0.5;
      double var57 = var1.getX() + 0.5 + var14[1][0] * 0.5;
      double var33 = var1.getZ() + 0.5 + var14[1][2] * 0.5;
      var15 = var57 - var55;
      var17 = var33 - var56;
      if (var15 == 0.0) {
         this.posX = var1.getX() + 0.5;
         var53 = this.posZ - var1.getZ();
      } else if (var17 == 0.0) {
         this.posZ = var1.getZ() + 0.5;
         var53 = this.posX - var1.getX();
      } else {
         double var35 = this.posX - var55;
         double var37 = this.posZ - var56;
         var53 = (var35 * var15 + var37 * var17) * 2.0;
      }

      this.posX = var55 + var15 * var53;
      this.posZ = var56 + var17 * var53;
      this.setPosition(this.posX, this.posY, this.posZ);
      double var58 = this.motionX;
      double var60 = this.motionZ;
      if (this.riddenByEntity != null) {
         var58 *= 0.75;
         var60 *= 0.75;
      }

      double var39 = this.getMaximumSpeed();
      var58 = MathHelper.clamp_double(var58, -var39, var39);
      var60 = MathHelper.clamp_double(var60, -var39, var39);
      this.moveEntity(var58, 0.0, var60);
      if (var14[0][1] != 0
         && MathHelper.floor_double(this.posX) - var1.getX() == var14[0][0]
         && MathHelper.floor_double(this.posZ) - var1.getZ() == var14[0][2]) {
         this.setPosition(this.posX, this.posY + var14[0][1], this.posZ);
      } else if (var14[1][1] != 0
         && MathHelper.floor_double(this.posX) - var1.getX() == var14[1][0]
         && MathHelper.floor_double(this.posZ) - var1.getZ() == var14[1][2]) {
         this.setPosition(this.posX, this.posY + var14[1][1], this.posZ);
      }

      this.applyDrag();
      Vec3 var41 = this.func_70489_a(this.posX, this.posY, this.posZ);
      if (var41 != null && var7 != null) {
         double var42 = (var7.yCoord - var41.yCoord) * 0.05;
         var23 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
         if (var23 > 0.0) {
            this.motionX = this.motionX / var23 * (var23 + var42);
            this.motionZ = this.motionZ / var23 * (var23 + var42);
         }

         this.setPosition(this.posX, var41.yCoord, this.posZ);
      }

      int var62 = MathHelper.floor_double(this.posX);
      int var43 = MathHelper.floor_double(this.posZ);
      if (var62 != var1.getX() || var43 != var1.getZ()) {
         var23 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
         this.motionX = var23 * (var62 - var1.getX());
         this.motionZ = var23 * (var43 - var1.getZ());
      }

      if (var8) {
         double var44 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
         if (var44 > 0.01) {
            double var46 = 0.06;
            this.motionX = this.motionX + this.motionX / var44 * var46;
            this.motionZ = this.motionZ + this.motionZ / var44 * var46;
         } else if (var13 == BlockRailBase.EnumRailDirection.EAST_WEST) {
            if (this.worldObj.getBlockState(var1.west()).getBlock().isNormalCube()) {
               this.motionX = 0.02;
            } else if (this.worldObj.getBlockState(var1.east()).getBlock().isNormalCube()) {
               this.motionX = -0.02;
            }
         } else if (var13 == BlockRailBase.EnumRailDirection.NORTH_SOUTH) {
            if (this.worldObj.getBlockState(var1.north()).getBlock().isNormalCube()) {
               this.motionZ = 0.02;
            } else if (this.worldObj.getBlockState(var1.south()).getBlock().isNormalCube()) {
               this.motionZ = -0.02;
            }
         }
      }
   }

   public void applyDrag() {
      if (this.riddenByEntity != null) {
         this.motionX *= 0.997F;
         this.motionY *= 0.0;
         this.motionZ *= 0.997F;
      } else {
         this.motionX *= 0.96F;
         this.motionY *= 0.0;
         this.motionZ *= 0.96F;
      }
   }

   @Override
   public void setPosition(double var1, double var3, double var5) {
      this.posX = var1;
      this.posY = var3;
      this.posZ = var5;
      float var7 = this.width / 2.0F;
      float var8 = this.height;
      this.setEntityBoundingBox(new AxisAlignedBB(var1 - var7, var3, var5 - var7, var1 + var7, var3 + var8, var5 + var7));
   }

   public Vec3 func_70495_a(double var1, double var3, double var5, double var7) {
      int var12 = MathHelper.floor_double(var1);
      int var13 = MathHelper.floor_double(var3);
      int var14 = MathHelper.floor_double(var5);
      if (BlockRailBase.isRailBlock(this.worldObj, new BlockPos(var12, var13 - 1, var14))) {
         var13--;
      }

      IBlockState var15 = this.worldObj.getBlockState(new BlockPos(var12, var13, var14));
      if (BlockRailBase.isRailBlock(var15)) {
         BlockRailBase.EnumRailDirection var16 = var15.getValue(((BlockRailBase)var15.getBlock()).getShapeProperty());
         var3 = var13;
         if (var16.isAscending()) {
            var3 = var13 + 1;
         }

         int[][] var17 = matrix[var16.getMetadata()];
         double var18 = var17[1][0] - var17[0][0];
         double var20 = var17[1][2] - var17[0][2];
         double var22 = Math.sqrt(var18 * var18 + var20 * var20);
         var18 /= var22;
         var20 /= var22;
         var1 += var18 * var7;
         var5 += var20 * var7;
         if (var17[0][1] != 0 && MathHelper.floor_double(var1) - var12 == var17[0][0] && MathHelper.floor_double(var5) - var14 == var17[0][2]) {
            var3 += var17[0][1];
         } else if (var17[1][1] != 0 && MathHelper.floor_double(var1) - var12 == var17[1][0] && MathHelper.floor_double(var5) - var14 == var17[1][2]) {
            var3 += var17[1][1];
         }

         return this.func_70489_a(var1, var3, var5);
      } else {
         return null;
      }
   }

   public Vec3 func_70489_a(double var1, double var3, double var5) {
      int var10 = MathHelper.floor_double(var1);
      int var11 = MathHelper.floor_double(var3);
      int var12 = MathHelper.floor_double(var5);
      if (BlockRailBase.isRailBlock(this.worldObj, new BlockPos(var10, var11 - 1, var12))) {
         var11--;
      }

      IBlockState var13 = this.worldObj.getBlockState(new BlockPos(var10, var11, var12));
      if (BlockRailBase.isRailBlock(var13)) {
         BlockRailBase.EnumRailDirection var14 = var13.getValue(((BlockRailBase)var13.getBlock()).getShapeProperty());
         int[][] var15 = matrix[var14.getMetadata()];
         double var16 = 0.0;
         double var18 = var10 + 0.5 + var15[0][0] * 0.5;
         double var20 = var11 + 0.0625 + var15[0][1] * 0.5;
         double var22 = var12 + 0.5 + var15[0][2] * 0.5;
         double var24 = var10 + 0.5 + var15[1][0] * 0.5;
         double var26 = var11 + 0.0625 + var15[1][1] * 0.5;
         double var28 = var12 + 0.5 + var15[1][2] * 0.5;
         double var30 = var24 - var18;
         double var32 = (var26 - var20) * 2.0;
         double var34 = var28 - var22;
         if (var30 == 0.0) {
            var1 = var10 + 0.5;
            var16 = var5 - var12;
         } else if (var34 == 0.0) {
            var5 = var12 + 0.5;
            var16 = var1 - var10;
         } else {
            double var36 = var1 - var18;
            double var38 = var5 - var22;
            var16 = (var36 * var30 + var38 * var34) * 2.0;
         }

         var1 = var18 + var30 * var16;
         var3 = var20 + var32 * var16;
         var5 = var22 + var34 * var16;
         if (var32 < 0.0) {
            var3++;
         }

         if (var32 > 0.0) {
            var3 += 0.5;
         }

         return new Vec3(var1, var3, var5);
      } else {
         return null;
      }
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound var1) {
      if (var1.getBoolean("CustomDisplayTile")) {
         int var5 = var1.getInteger("DisplayData");
         if (var1.hasKey("DisplayTile", 8)) {
            Block var6 = Block.getBlockFromName(var1.getString("DisplayTile"));
            if (var6 == null) {
               this.func_174899_a(Blocks.air.getDefaultState());
            } else {
               this.func_174899_a(var6.getStateFromMeta(var5));
            }
         } else {
            Block var7 = Block.getBlockById(var1.getInteger("DisplayTile"));
            if (var7 == null) {
               this.func_174899_a(Blocks.air.getDefaultState());
            } else {
               this.func_174899_a(var7.getStateFromMeta(var5));
            }
         }

         this.setDisplayTileOffset(var1.getInteger("DisplayOffset"));
      }

      if (var1.hasKey("CustomName", 8) && var1.getString("CustomName").length() > 0) {
         this.entityName = var1.getString("CustomName");
      }
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound var1) {
      if (this.hasDisplayTile()) {
         var1.setBoolean("CustomDisplayTile", true);
         IBlockState var5 = this.getDisplayTile();
         ResourceLocation var6 = Block.blockRegistry.getNameForObject(var5.getBlock());
         var1.setString("DisplayTile", var6 == null ? "" : var6.toString());
         var1.setInteger("DisplayData", var5.getBlock().getMetaFromState(var5));
         var1.setInteger("DisplayOffset", this.getDisplayTileOffset());
      }

      if (this.entityName != null && this.entityName.length() > 0) {
         var1.setString("CustomName", this.entityName);
      }
   }

   @Override
   public void applyEntityCollision(Entity var1) {
      if (!this.worldObj.isRemote && !var1.noClip && !this.noClip && var1 != this.riddenByEntity) {
         if (var1 instanceof EntityLivingBase
            && !(var1 instanceof EntityPlayer)
            && !(var1 instanceof EntityIronGolem)
            && this.getMinecartType() == EntityMinecart.EnumMinecartType.RIDEABLE
            && this.motionX * this.motionX + this.motionZ * this.motionZ > 0.01
            && this.riddenByEntity == null
            && var1.ridingEntity == null) {
            var1.mountEntity(this);
         }

         double var5 = var1.posX - this.posX;
         double var7 = var1.posZ - this.posZ;
         double var9 = var5 * var5 + var7 * var7;
         if (var9 >= 1.0E-4F) {
            var9 = MathHelper.sqrt_double(var9);
            var5 /= var9;
            var7 /= var9;
            double var11 = 1.0 / var9;
            if (var11 > 1.0) {
               var11 = 1.0;
            }

            var5 *= var11;
            var7 *= var11;
            var5 *= 0.1F;
            var7 *= 0.1F;
            var5 *= 1.0F - this.entityCollisionReduction;
            var7 *= 1.0F - this.entityCollisionReduction;
            var5 *= 0.5;
            var7 *= 0.5;
            if (var1 instanceof EntityMinecart) {
               double var13 = var1.posX - this.posX;
               double var15 = var1.posZ - this.posZ;
               Vec3 var17 = new Vec3(var13, 0.0, var15).normalize();
               Vec3 var18 = new Vec3(
                     MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F), 0.0, MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F)
                  )
                  .normalize();
               double var19 = Math.abs(var17.dotProduct(var18));
               if (var19 < 0.8F) {
                  return;
               }

               double var21 = var1.motionX + this.motionX;
               double var23 = var1.motionZ + this.motionZ;
               if (((EntityMinecart)var1).getMinecartType() == EntityMinecart.EnumMinecartType.FURNACE
                  && this.getMinecartType() != EntityMinecart.EnumMinecartType.FURNACE) {
                  this.motionX *= 0.2F;
                  this.motionZ *= 0.2F;
                  this.addVelocity(var1.motionX - var5, 0.0, var1.motionZ - var7);
                  var1.motionX *= 0.95F;
                  var1.motionZ *= 0.95F;
               } else if (((EntityMinecart)var1).getMinecartType() != EntityMinecart.EnumMinecartType.FURNACE
                  && this.getMinecartType() == EntityMinecart.EnumMinecartType.FURNACE) {
                  var1.motionX *= 0.2F;
                  var1.motionZ *= 0.2F;
                  var1.addVelocity(this.motionX + var5, 0.0, this.motionZ + var7);
                  this.motionX *= 0.95F;
                  this.motionZ *= 0.95F;
               } else {
                  var21 /= 2.0;
                  var23 /= 2.0;
                  this.motionX *= 0.2F;
                  this.motionZ *= 0.2F;
                  this.addVelocity(var21 - var5, 0.0, var23 - var7);
                  var1.motionX *= 0.2F;
                  var1.motionZ *= 0.2F;
                  var1.addVelocity(var21 + var5, 0.0, var23 + var7);
               }
            } else {
               this.addVelocity(-var5, 0.0, -var7);
               var1.addVelocity(var5 / 4.0, 0.0, var7 / 4.0);
            }
         }
      }
   }

   @Override
   public void setPositionAndRotation2(double var1, double var3, double var5, float var7, float var8, int var9, boolean var10) {
      this.minecartX = var1;
      this.minecartY = var3;
      this.minecartZ = var5;
      this.minecartYaw = var7;
      this.minecartPitch = var8;
      this.turnProgress = var9 + 2;
      this.motionX = this.velocityX;
      this.motionY = this.velocityY;
      this.motionZ = this.velocityZ;
   }

   @Override
   public void setVelocity(double var1, double var3, double var5) {
      this.velocityX = this.motionX = var1;
      this.velocityY = this.motionY = var3;
      this.velocityZ = this.motionZ = var5;
   }

   public void setDamage(float var1) {
      this.dataWatcher.updateObject(19, var1);
   }

   public float getDamage() {
      return this.dataWatcher.getWatchableObjectFloat(19);
   }

   public void setRollingAmplitude(int var1) {
      this.dataWatcher.updateObject(17, var1);
   }

   public int getRollingAmplitude() {
      return this.dataWatcher.getWatchableObjectInt(17);
   }

   public void setRollingDirection(int var1) {
      this.dataWatcher.updateObject(18, var1);
   }

   public int getRollingDirection() {
      return this.dataWatcher.getWatchableObjectInt(18);
   }

   public abstract EntityMinecart.EnumMinecartType getMinecartType();

   public IBlockState getDisplayTile() {
      return !this.hasDisplayTile() ? this.getDefaultDisplayTile() : Block.getStateById(this.getDataWatcher().getWatchableObjectInt(20));
   }

   public IBlockState getDefaultDisplayTile() {
      return Blocks.air.getDefaultState();
   }

   public int getDisplayTileOffset() {
      return !this.hasDisplayTile() ? this.getDefaultDisplayTileOffset() : this.getDataWatcher().getWatchableObjectInt(21);
   }

   public int getDefaultDisplayTileOffset() {
      return 6;
   }

   public void func_174899_a(IBlockState var1) {
      this.getDataWatcher().updateObject(20, Block.getStateId(var1));
      this.setHasDisplayTile(true);
   }

   public void setDisplayTileOffset(int var1) {
      this.getDataWatcher().updateObject(21, var1);
      this.setHasDisplayTile(true);
   }

   public boolean hasDisplayTile() {
      return this.getDataWatcher().getWatchableObjectByte(22) == 1;
   }

   public void setHasDisplayTile(boolean var1) {
      this.getDataWatcher().updateObject(22, (byte)(var1 ? 1 : 0));
   }

   @Override
   public void setCustomNameTag(String var1) {
      this.entityName = var1;
   }

   @Override
   public String getName() {
      return this.entityName != null ? this.entityName : super.getName();
   }

   @Override
   public boolean hasCustomName() {
      return this.entityName != null;
   }

   @Override
   public String getCustomNameTag() {
      return this.entityName;
   }

   @Override
   public IChatComponent getDisplayName() {
      if (this.hasCustomName()) {
         ChatComponentText var4 = new ChatComponentText(this.entityName);
         var4.getChatStyle().setChatHoverEvent(this.getHoverEvent());
         var4.getChatStyle().setInsertion(this.getUniqueID().toString());
         return var4;
      } else {
         ChatComponentTranslation var3 = new ChatComponentTranslation(this.getName());
         var3.getChatStyle().setChatHoverEvent(this.getHoverEvent());
         var3.getChatStyle().setInsertion(this.getUniqueID().toString());
         return var3;
      }
   }

   public enum EnumMinecartType {
      RIDEABLE(0, "MinecartRideable"),
      CHEST(1, "MinecartChest"),
      FURNACE(2, "MinecartFurnace"),
      TNT(3, "MinecartTNT"),
      SPAWNER(4, "MinecartSpawner"),
      HOPPER(5, "MinecartHopper"),
      COMMAND_BLOCK(6, "MinecartCommandBlock");

      private static final Map<Integer, EntityMinecart.EnumMinecartType> ID_LOOKUP = Maps.newHashMap();
      private final int networkID;
      private final String name;
      private static final long a = 102531001790621L;

      EnumMinecartType(int var3, String var4) {
         this.networkID = var3;
         this.name = var4;
      }

      public int getNetworkID() {
         return this.networkID;
      }

      public String getName() {
         return this.name;
      }

      public static EntityMinecart.EnumMinecartType byNetworkID(int var0) {
         EntityMinecart.EnumMinecartType var4 = ID_LOOKUP.get(var0);
         return var4 == null ? RIDEABLE : var4;
      }

      static {
         for (EntityMinecart.EnumMinecartType var25 : values()) {
            ID_LOOKUP.put(var25.getNetworkID(), var25);
         }
      }
   }
}
