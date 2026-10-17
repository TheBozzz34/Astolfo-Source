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

   public EntityMinecart(World worldIn) {
      super(worldIn);
      this.preventEntitySpawning = true;
      this.setSize(0.98F, 0.7F);
   }

   public static EntityMinecart getMinecart(World worldIn, double x, double y, double z, EntityMinecart.EnumMinecartType typeIn) {
      switch (typeIn) {
         case CHEST:
            return new EntityMinecartChest(worldIn, x, y, z);
         case FURNACE:
            return new EntityMinecartFurnace(worldIn, x, y, z);
         case TNT:
            return new EntityMinecartTNT(worldIn, x, y, z);
         case SPAWNER:
            return new EntityMinecartMobSpawner(worldIn, x, y, z);
         case HOPPER:
            return new EntityMinecartHopper(worldIn, x, y, z);
         case COMMAND_BLOCK:
            return new EntityMinecartCommandBlock(worldIn, x, y, z);
         default:
            return new EntityMinecartEmpty(worldIn, x, y, z);
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
   public AxisAlignedBB getCollisionBox(Entity entityIn) {
      return entityIn.canBePushed() ? entityIn.getEntityBoundingBox() : null;
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox() {
      return null;
   }

   @Override
   public boolean canBePushed() {
      return true;
   }

   public EntityMinecart(World worldIn, double x, double y, double z) {
      this(worldIn);
      this.setPosition(x, y, z);
      this.motionX = 0.0;
      this.motionY = 0.0;
      this.motionZ = 0.0;
      this.prevPosX = x;
      this.prevPosY = y;
      this.prevPosZ = z;
   }

   @Override
   public double getMountedYOffset() {
      return 0.0;
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.worldObj.isRemote || this.isDead) {
         return true;
      }

      if (this.isEntityInvulnerable(source)) {
         return false;
      }

      this.setRollingDirection(-this.getRollingDirection());
      this.setRollingAmplitude(10);
      this.setBeenAttacked();
      this.setDamage(this.getDamage() + amount * 10.0F);
      boolean bl = source.getEntity() instanceof EntityPlayer && ((EntityPlayer)source.getEntity()).capabilities.isCreativeMode;
      if (bl || this.getDamage() > 40.0F) {
         if (this.riddenByEntity != null) {
            this.riddenByEntity.mountEntity((Entity)null);
         }

         if (bl && !this.hasCustomName()) {
            this.setDead();
         } else {
            this.killMinecart(source);
         }
      }

      return true;
   }

   public void killMinecart(DamageSource source) {
      this.setDead();
      if (this.worldObj.getGameRules().getBoolean("doEntityDrops")) {
         ItemStack itemStack = new ItemStack(Items.minecart, 1);
         if (this.entityName != null) {
            itemStack.setStackDisplayName(this.entityName);
         }

         this.entityDropItem(itemStack, 0.0F);
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
         MinecraftServer minecraftServer = ((WorldServer)this.worldObj).getMinecraftServer();
         int i = this.getMaxInPortalTime();
         if (this.inPortal) {
            if (minecraftServer.getAllowNether()) {
               if (this.ridingEntity == null && this.portalCounter++ >= i) {
                  this.portalCounter = i;
                  this.timeUntilPortal = this.getPortalCooldown();
                  byte b;
                  if (this.worldObj.provider.getDimensionId() == -1) {
                     b = 0;
                  } else {
                     b = -1;
                  }

                  this.travelToDimension(b);
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
            double d = this.posX + (this.minecartX - this.posX) / this.turnProgress;
            double e = this.posY + (this.minecartY - this.posY) / this.turnProgress;
            double d2 = this.posZ + (this.minecartZ - this.posZ) / this.turnProgress;
            double d3 = MathHelper.wrapAngleTo180_double(this.minecartYaw - this.rotationYaw);
            this.rotationYaw = (float)(this.rotationYaw + d3 / this.turnProgress);
            this.rotationPitch = (float)(this.rotationPitch + (this.minecartPitch - this.rotationPitch) / this.turnProgress);
            this.turnProgress--;
            this.setPosition(d, e, d2);
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
         int j = MathHelper.floor_double(this.posX);
         int k = MathHelper.floor_double(this.posY);
         int l = MathHelper.floor_double(this.posZ);
         if (BlockRailBase.isRailBlock(this.worldObj, new BlockPos(j, k - 1, l))) {
            k--;
         }

         BlockPos blockPos = new BlockPos(j, k, l);
         IBlockState iBlockState = this.worldObj.getBlockState(blockPos);
         if (BlockRailBase.isRailBlock(iBlockState)) {
            this.func_180460_a(blockPos, iBlockState);
            if (iBlockState.getBlock() == Blocks.activator_rail) {
               this.onActivatorRailPass(j, k, l, iBlockState.getValue(BlockRailPowered.POWERED));
            }
         } else {
            this.moveDerailedMinecart();
         }

         this.doBlockCollisions();
         this.rotationPitch = 0.0F;
         double d4 = this.prevPosX - this.posX;
         double d5 = this.prevPosZ - this.posZ;
         if (d4 * d4 + d5 * d5 > 0.001) {
            this.rotationYaw = (float)(MathHelper.atan2(d5, d4) * 180.0 / Math.PI);
            if (this.isInReverse) {
               this.rotationYaw += 180.0F;
            }
         }

         double d6 = MathHelper.wrapAngleTo180_float(this.rotationYaw - this.prevRotationYaw);
         if (d6 < -170.0 || d6 >= 170.0) {
            this.rotationYaw += 180.0F;
            this.isInReverse = !this.isInReverse;
         }

         this.setRotation(this.rotationYaw, this.rotationPitch);

         for (Entity entity : this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().expand(0.2F, 0.0, 0.2F))) {
            if (entity != this.riddenByEntity && entity.canBePushed() && entity instanceof EntityMinecart) {
               entity.applyEntityCollision(this);
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

   public void onActivatorRailPass(int x, int y, int z, boolean receivingPower) {
   }

   public void moveDerailedMinecart() {
      double d = this.getMaximumSpeed();
      this.motionX = MathHelper.clamp_double(this.motionX, -d, d);
      this.motionZ = MathHelper.clamp_double(this.motionZ, -d, d);
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

   public void func_180460_a(BlockPos blockPos, IBlockState iBlockState) {
      this.fallDistance = 0.0F;
      Vec3 vec3 = this.func_70489_a(this.posX, this.posY, this.posZ);
      this.posY = blockPos.getY();
      boolean bl = false;
      boolean bl2 = false;
      BlockRailBase blockRailBase = (BlockRailBase)iBlockState.getBlock();
      if (blockRailBase == Blocks.golden_rail) {
         bl = iBlockState.getValue(BlockRailPowered.POWERED);
         bl2 = !bl;
      }

      BlockRailBase.EnumRailDirection enumRailDirection = iBlockState.getValue(blockRailBase.getShapeProperty());
      switch (enumRailDirection) {
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

      int[][] intss = matrix[enumRailDirection.getMetadata()];
      double d = intss[1][0] - intss[0][0];
      double e = intss[1][2] - intss[0][2];
      double d2 = Math.sqrt(d * d + e * e);
      double d3 = this.motionX * d + this.motionZ * e;
      if (d3 < 0.0) {
         d = -d;
         e = -e;
      }

      double d4 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
      if (d4 > 2.0) {
         d4 = 2.0;
      }

      this.motionX = d4 * d / d2;
      this.motionZ = d4 * e / d2;
      if (this.riddenByEntity instanceof EntityLivingBase) {
         double d5 = ((EntityLivingBase)this.riddenByEntity).moveForward;
         if (d5 > 0.0) {
            double d6 = -Math.sin(this.riddenByEntity.rotationYaw * (float) Math.PI / 180.0F);
            double d7 = Math.cos(this.riddenByEntity.rotationYaw * (float) Math.PI / 180.0F);
            double d8 = this.motionX * this.motionX + this.motionZ * this.motionZ;
            if (d8 < 0.01) {
               this.motionX += d6 * 0.1;
               this.motionZ += d7 * 0.1;
               bl2 = false;
            }
         }
      }

      if (bl2) {
         double d9 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
         if (d9 < 0.03) {
            this.motionX *= 0.0;
            this.motionY *= 0.0;
            this.motionZ *= 0.0;
         } else {
            this.motionX *= 0.5;
            this.motionY *= 0.0;
            this.motionZ *= 0.5;
         }
      }

      double d10 = 0.0;
      double d11 = blockPos.getX() + 0.5 + intss[0][0] * 0.5;
      double d12 = blockPos.getZ() + 0.5 + intss[0][2] * 0.5;
      double d13 = blockPos.getX() + 0.5 + intss[1][0] * 0.5;
      double d14 = blockPos.getZ() + 0.5 + intss[1][2] * 0.5;
      d = d13 - d11;
      e = d14 - d12;
      if (d == 0.0) {
         this.posX = blockPos.getX() + 0.5;
         d10 = this.posZ - blockPos.getZ();
      } else if (e == 0.0) {
         this.posZ = blockPos.getZ() + 0.5;
         d10 = this.posX - blockPos.getX();
      } else {
         double d15 = this.posX - d11;
         double d16 = this.posZ - d12;
         d10 = (d15 * d + d16 * e) * 2.0;
      }

      this.posX = d11 + d * d10;
      this.posZ = d12 + e * d10;
      this.setPosition(this.posX, this.posY, this.posZ);
      double d17 = this.motionX;
      double d18 = this.motionZ;
      if (this.riddenByEntity != null) {
         d17 *= 0.75;
         d18 *= 0.75;
      }

      double d19 = this.getMaximumSpeed();
      d17 = MathHelper.clamp_double(d17, -d19, d19);
      d18 = MathHelper.clamp_double(d18, -d19, d19);
      this.moveEntity(d17, 0.0, d18);
      if (intss[0][1] != 0
         && MathHelper.floor_double(this.posX) - blockPos.getX() == intss[0][0]
         && MathHelper.floor_double(this.posZ) - blockPos.getZ() == intss[0][2]) {
         this.setPosition(this.posX, this.posY + intss[0][1], this.posZ);
      } else if (intss[1][1] != 0
         && MathHelper.floor_double(this.posX) - blockPos.getX() == intss[1][0]
         && MathHelper.floor_double(this.posZ) - blockPos.getZ() == intss[1][2]) {
         this.setPosition(this.posX, this.posY + intss[1][1], this.posZ);
      }

      this.applyDrag();
      Vec3 vec32 = this.func_70489_a(this.posX, this.posY, this.posZ);
      if (vec32 != null && vec3 != null) {
         double d20 = (vec3.yCoord - vec32.yCoord) * 0.05;
         d4 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
         if (d4 > 0.0) {
            this.motionX = this.motionX / d4 * (d4 + d20);
            this.motionZ = this.motionZ / d4 * (d4 + d20);
         }

         this.setPosition(this.posX, vec32.yCoord, this.posZ);
      }

      int i = MathHelper.floor_double(this.posX);
      int j = MathHelper.floor_double(this.posZ);
      if (i != blockPos.getX() || j != blockPos.getZ()) {
         d4 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
         this.motionX = d4 * (i - blockPos.getX());
         this.motionZ = d4 * (j - blockPos.getZ());
      }

      if (bl) {
         double d21 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
         if (d21 > 0.01) {
            double d22 = 0.06;
            this.motionX = this.motionX + this.motionX / d21 * d22;
            this.motionZ = this.motionZ + this.motionZ / d21 * d22;
         } else if (enumRailDirection == BlockRailBase.EnumRailDirection.EAST_WEST) {
            if (this.worldObj.getBlockState(blockPos.west()).getBlock().isNormalCube()) {
               this.motionX = 0.02;
            } else if (this.worldObj.getBlockState(blockPos.east()).getBlock().isNormalCube()) {
               this.motionX = -0.02;
            }
         } else if (enumRailDirection == BlockRailBase.EnumRailDirection.NORTH_SOUTH) {
            if (this.worldObj.getBlockState(blockPos.north()).getBlock().isNormalCube()) {
               this.motionZ = 0.02;
            } else if (this.worldObj.getBlockState(blockPos.south()).getBlock().isNormalCube()) {
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
   public void setPosition(double x, double y, double z) {
      this.posX = x;
      this.posY = y;
      this.posZ = z;
      float f = this.width / 2.0F;
      float g = this.height;
      this.setEntityBoundingBox(new AxisAlignedBB(x - f, y, z - f, x + f, y + g, z + f));
   }

   public Vec3 func_70495_a(double d, double e, double d2, double d3) {
      int i = MathHelper.floor_double(d);
      int j = MathHelper.floor_double(e);
      int k = MathHelper.floor_double(d2);
      if (BlockRailBase.isRailBlock(this.worldObj, new BlockPos(i, j - 1, k))) {
         j--;
      }

      IBlockState iBlockState = this.worldObj.getBlockState(new BlockPos(i, j, k));
      if (BlockRailBase.isRailBlock(iBlockState)) {
         BlockRailBase.EnumRailDirection enumRailDirection = iBlockState.getValue(((BlockRailBase)iBlockState.getBlock()).getShapeProperty());
         e = j;
         if (enumRailDirection.isAscending()) {
            e = j + 1;
         }

         int[][] intss = matrix[enumRailDirection.getMetadata()];
         double d4 = intss[1][0] - intss[0][0];
         double d5 = intss[1][2] - intss[0][2];
         double d6 = Math.sqrt(d4 * d4 + d5 * d5);
         d4 /= d6;
         d5 /= d6;
         d += d4 * d3;
         d2 += d5 * d3;
         if (intss[0][1] != 0 && MathHelper.floor_double(d) - i == intss[0][0] && MathHelper.floor_double(d2) - k == intss[0][2]) {
            e += intss[0][1];
         } else if (intss[1][1] != 0 && MathHelper.floor_double(d) - i == intss[1][0] && MathHelper.floor_double(d2) - k == intss[1][2]) {
            e += intss[1][1];
         }

         return this.func_70489_a(d, e, d2);
      } else {
         return null;
      }
   }

   public Vec3 func_70489_a(double d, double e, double d2) {
      int i = MathHelper.floor_double(d);
      int j = MathHelper.floor_double(e);
      int k = MathHelper.floor_double(d2);
      if (BlockRailBase.isRailBlock(this.worldObj, new BlockPos(i, j - 1, k))) {
         j--;
      }

      IBlockState iBlockState = this.worldObj.getBlockState(new BlockPos(i, j, k));
      if (BlockRailBase.isRailBlock(iBlockState)) {
         BlockRailBase.EnumRailDirection enumRailDirection = iBlockState.getValue(((BlockRailBase)iBlockState.getBlock()).getShapeProperty());
         int[][] intss = matrix[enumRailDirection.getMetadata()];
         double d3 = 0.0;
         double d4 = i + 0.5 + intss[0][0] * 0.5;
         double d5 = j + 0.0625 + intss[0][1] * 0.5;
         double d6 = k + 0.5 + intss[0][2] * 0.5;
         double d7 = i + 0.5 + intss[1][0] * 0.5;
         double d8 = j + 0.0625 + intss[1][1] * 0.5;
         double d9 = k + 0.5 + intss[1][2] * 0.5;
         double d10 = d7 - d4;
         double d11 = (d8 - d5) * 2.0;
         double d12 = d9 - d6;
         if (d10 == 0.0) {
            d = i + 0.5;
            d3 = d2 - k;
         } else if (d12 == 0.0) {
            d2 = k + 0.5;
            d3 = d - i;
         } else {
            double d13 = d - d4;
            double d14 = d2 - d6;
            d3 = (d13 * d10 + d14 * d12) * 2.0;
         }

         d = d4 + d10 * d3;
         e = d5 + d11 * d3;
         d2 = d6 + d12 * d3;
         if (d11 < 0.0) {
            e++;
         }

         if (d11 > 0.0) {
            e += 0.5;
         }

         return new Vec3(d, e, d2);
      } else {
         return null;
      }
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      if (tagCompund.getBoolean("CustomDisplayTile")) {
         int i = tagCompund.getInteger("DisplayData");
         if (tagCompund.hasKey("DisplayTile", 8)) {
            Block block = Block.getBlockFromName(tagCompund.getString("DisplayTile"));
            if (block == null) {
               this.func_174899_a(Blocks.air.getDefaultState());
            } else {
               this.func_174899_a(block.getStateFromMeta(i));
            }
         } else {
            Block block2 = Block.getBlockById(tagCompund.getInteger("DisplayTile"));
            if (block2 == null) {
               this.func_174899_a(Blocks.air.getDefaultState());
            } else {
               this.func_174899_a(block2.getStateFromMeta(i));
            }
         }

         this.setDisplayTileOffset(tagCompund.getInteger("DisplayOffset"));
      }

      if (tagCompund.hasKey("CustomName", 8) && tagCompund.getString("CustomName").length() > 0) {
         this.entityName = tagCompund.getString("CustomName");
      }
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      if (this.hasDisplayTile()) {
         tagCompound.setBoolean("CustomDisplayTile", true);
         IBlockState iBlockState = this.getDisplayTile();
         ResourceLocation resourceLocation = Block.blockRegistry.getNameForObject(iBlockState.getBlock());
         tagCompound.setString("DisplayTile", resourceLocation == null ? "" : resourceLocation.toString());
         tagCompound.setInteger("DisplayData", iBlockState.getBlock().getMetaFromState(iBlockState));
         tagCompound.setInteger("DisplayOffset", this.getDisplayTileOffset());
      }

      if (this.entityName != null && this.entityName.length() > 0) {
         tagCompound.setString("CustomName", this.entityName);
      }
   }

   @Override
   public void applyEntityCollision(Entity entityIn) {
      if (!this.worldObj.isRemote && !entityIn.noClip && !this.noClip && entityIn != this.riddenByEntity) {
         if (entityIn instanceof EntityLivingBase
            && !(entityIn instanceof EntityPlayer)
            && !(entityIn instanceof EntityIronGolem)
            && this.getMinecartType() == EntityMinecart.EnumMinecartType.RIDEABLE
            && this.motionX * this.motionX + this.motionZ * this.motionZ > 0.01
            && this.riddenByEntity == null
            && entityIn.ridingEntity == null) {
            entityIn.mountEntity(this);
         }

         double d = entityIn.posX - this.posX;
         double e = entityIn.posZ - this.posZ;
         double d2 = d * d + e * e;
         if (d2 >= 1.0E-4F) {
            d2 = MathHelper.sqrt_double(d2);
            d /= d2;
            e /= d2;
            double d3 = 1.0 / d2;
            if (d3 > 1.0) {
               d3 = 1.0;
            }

            d *= d3;
            e *= d3;
            d *= 0.1F;
            e *= 0.1F;
            d *= 1.0F - this.entityCollisionReduction;
            e *= 1.0F - this.entityCollisionReduction;
            d *= 0.5;
            e *= 0.5;
            if (entityIn instanceof EntityMinecart) {
               double d4 = entityIn.posX - this.posX;
               double d5 = entityIn.posZ - this.posZ;
               Vec3 vec3 = new Vec3(d4, 0.0, d5).normalize();
               Vec3 vec32 = new Vec3(
                     MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F), 0.0, MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F)
                  )
                  .normalize();
               double d6 = Math.abs(vec3.dotProduct(vec32));
               if (d6 < 0.8F) {
                  return;
               }

               double d7 = entityIn.motionX + this.motionX;
               double d8 = entityIn.motionZ + this.motionZ;
               if (((EntityMinecart)entityIn).getMinecartType() == EntityMinecart.EnumMinecartType.FURNACE
                  && this.getMinecartType() != EntityMinecart.EnumMinecartType.FURNACE) {
                  this.motionX *= 0.2F;
                  this.motionZ *= 0.2F;
                  this.addVelocity(entityIn.motionX - d, 0.0, entityIn.motionZ - e);
                  entityIn.motionX *= 0.95F;
                  entityIn.motionZ *= 0.95F;
               } else if (((EntityMinecart)entityIn).getMinecartType() != EntityMinecart.EnumMinecartType.FURNACE
                  && this.getMinecartType() == EntityMinecart.EnumMinecartType.FURNACE) {
                  entityIn.motionX *= 0.2F;
                  entityIn.motionZ *= 0.2F;
                  entityIn.addVelocity(this.motionX + d, 0.0, this.motionZ + e);
                  this.motionX *= 0.95F;
                  this.motionZ *= 0.95F;
               } else {
                  d7 /= 2.0;
                  d8 /= 2.0;
                  this.motionX *= 0.2F;
                  this.motionZ *= 0.2F;
                  this.addVelocity(d7 - d, 0.0, d8 - e);
                  entityIn.motionX *= 0.2F;
                  entityIn.motionZ *= 0.2F;
                  entityIn.addVelocity(d7 + d, 0.0, d8 + e);
               }
            } else {
               this.addVelocity(-d, 0.0, -e);
               entityIn.addVelocity(d / 4.0, 0.0, e / 4.0);
            }
         }
      }
   }

   @Override
   public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean bl) {
      this.minecartX = x;
      this.minecartY = y;
      this.minecartZ = z;
      this.minecartYaw = yaw;
      this.minecartPitch = pitch;
      this.turnProgress = posRotationIncrements + 2;
      this.motionX = this.velocityX;
      this.motionY = this.velocityY;
      this.motionZ = this.velocityZ;
   }

   @Override
   public void setVelocity(double x, double y, double z) {
      this.velocityX = this.motionX = x;
      this.velocityY = this.motionY = y;
      this.velocityZ = this.motionZ = z;
   }

   public void setDamage(float f) {
      this.dataWatcher.updateObject(19, f);
   }

   public float getDamage() {
      return this.dataWatcher.getWatchableObjectFloat(19);
   }

   public void setRollingAmplitude(int i) {
      this.dataWatcher.updateObject(17, i);
   }

   public int getRollingAmplitude() {
      return this.dataWatcher.getWatchableObjectInt(17);
   }

   public void setRollingDirection(int i) {
      this.dataWatcher.updateObject(18, i);
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

   public void func_174899_a(IBlockState iBlockState) {
      this.getDataWatcher().updateObject(20, Block.getStateId(iBlockState));
      this.setHasDisplayTile(true);
   }

   public void setDisplayTileOffset(int i) {
      this.getDataWatcher().updateObject(21, i);
      this.setHasDisplayTile(true);
   }

   public boolean hasDisplayTile() {
      return this.getDataWatcher().getWatchableObjectByte(22) == 1;
   }

   public void setHasDisplayTile(boolean bl) {
      this.getDataWatcher().updateObject(22, (byte)(bl ? 1 : 0));
   }

   @Override
   public void setCustomNameTag(String name) {
      this.entityName = name;
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
         ChatComponentText chatComponentText = new ChatComponentText(this.entityName);
         chatComponentText.getChatStyle().setChatHoverEvent(this.getHoverEvent());
         chatComponentText.getChatStyle().setInsertion(this.getUniqueID().toString());
         return chatComponentText;
      } else {
         ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation(this.getName());
         chatComponentTranslation.getChatStyle().setChatHoverEvent(this.getHoverEvent());
         chatComponentTranslation.getChatStyle().setInsertion(this.getUniqueID().toString());
         return chatComponentTranslation;
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

      EnumMinecartType(int networkID, String name) {
         this.networkID = networkID;
         this.name = name;
      }

      public int getNetworkID() {
         return this.networkID;
      }

      public String getName() {
         return this.name;
      }

      public static EntityMinecart.EnumMinecartType byNetworkID(int id) {
         EntityMinecart.EnumMinecartType enumMinecartType = ID_LOOKUP.get(id);
         return enumMinecartType == null ? RIDEABLE : enumMinecartType;
      }

      static {
         for (EntityMinecart.EnumMinecartType enumMinecartType : values()) {
            ID_LOOKUP.put(enumMinecartType.getNetworkID(), enumMinecartType);
         }
      }
   }
}
