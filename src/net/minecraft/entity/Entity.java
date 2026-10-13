package net.minecraft.entity;

import bypass.Astolfo;
import bypass.event.player.HitboxEvent;
import bypass.event.player.SafeWalkEvent;
import bypass.event.player.StepEvent;
import bypass.event.player.StepState;
import bypass.event.player.StrafeEvent;
import bypass.module.ModuleManager;
import bypass.module.visuals.NoRender;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommandSender;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.event.HoverEvent;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ReportedException;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public abstract class Entity implements ICommandSender {
   private static final AxisAlignedBB ZERO_AABB = new AxisAlignedBB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
   private static int nextEntityID;
   private int entityId;
   public double renderDistanceWeight;
   public boolean preventEntitySpawning;
   public Entity riddenByEntity;
   public Entity ridingEntity;
   public boolean forceSpawn;
   public World worldObj;
   public double prevPosX;
   public double prevPosY;
   public double prevPosZ;
   public double posX;
   public double posY;
   public double posZ;
   public double motionX;
   public double motionY;
   public double motionZ;
   public float rotationYaw;
   public float rotationPitch;
   public float prevRotationYaw;
   public float prevRotationPitch;
   private AxisAlignedBB boundingBox;
   public boolean onGround;
   public boolean isCollidedHorizontally;
   public boolean isCollidedVertically;
   public boolean isCollided;
   public boolean velocityChanged;
   public boolean isInWeb;
   private boolean isOutsideBorder;
   public boolean isDead;
   public float width;
   public float height;
   public float prevDistanceWalkedModified;
   public float distanceWalkedModified;
   public float distanceWalkedOnStepModified;
   public float fallDistance;
   private int nextStepDistance;
   public double lastTickPosX;
   public double lastTickPosY;
   public double lastTickPosZ;
   public float stepHeight;
   public boolean noClip;
   public float entityCollisionReduction;
   public Random rand;
   public int ticksExisted;
   public int fireResistance;
   private int fire;
   public boolean inWater;
   public int hurtResistantTime;
   public boolean firstUpdate;
   public boolean isImmuneToFire;
   public DataWatcher dataWatcher;
   private double entityRiderPitchDelta;
   private double entityRiderYawDelta;
   public boolean addedToChunk;
   public int chunkCoordX;
   public int chunkCoordY;
   public int chunkCoordZ;
   public int serverPosX;
   public int serverPosY;
   public int serverPosZ;
   public boolean ignoreFrustumCheck;
   public boolean isAirBorne;
   public int timeUntilPortal;
   public boolean inPortal;
   public int portalCounter;
   public int dimension;
   public BlockPos lastPortalPos;
   public Vec3 lastPortalVec;
   public EnumFacing teleportDirection;
   private boolean invulnerable;
   public UUID entityUniqueID;
   private final CommandResultStats cmdResultStats;
   private static final long db = 135335006354497L;

   public int getEntityId() {
      return this.entityId;
   }

   public void setEntityId(int var1) {
      this.entityId = var1;
   }

   public void onKillCommand() {
      this.setDead();
   }

   public Entity(World var1) {
      this.entityId = nextEntityID++;
      this.renderDistanceWeight = 1.0;
      this.boundingBox = ZERO_AABB;
      this.width = 0.6F;
      this.height = 1.8F;
      this.nextStepDistance = 1;
      this.rand = new Random();
      this.fireResistance = 1;
      this.firstUpdate = true;
      this.entityUniqueID = MathHelper.getRandomUuid(this.rand);
      this.cmdResultStats = new CommandResultStats();
      this.worldObj = var1;
      this.setPosition(0.0, 0.0, 0.0);
      if (var1 != null) {
         this.dimension = var1.provider.getDimensionId();
      }

      this.dataWatcher = new DataWatcher(this);
      this.dataWatcher.addObject(0, (byte)0);
      this.dataWatcher.addObject(1, (short)300);
      this.dataWatcher.addObject(3, (byte)0);
      this.dataWatcher.addObject(2, "");
      this.dataWatcher.addObject(4, (byte)0);
      this.entityInit();
   }

   public abstract void entityInit();

   public DataWatcher getDataWatcher() {
      return this.dataWatcher;
   }

   @Override
   public boolean equals(Object var1) {
      return var1 instanceof Entity ? ((Entity)var1).entityId == this.entityId : false;
   }

   @Override
   public int hashCode() {
      return this.entityId;
   }

   public void preparePlayerToSpawn() {
      if (this.worldObj != null) {
         while (true) {
            if (this.posY > 0.0 && this.posY < 256.0) {
               this.setPosition(this.posX, this.posY, this.posZ);
               if (!this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox()).isEmpty()) {
                  this.posY++;
                  continue;
               }
            }

            this.motionX = this.motionY = this.motionZ = 0.0;
            this.rotationPitch = 0.0F;
            break;
         }
      }
   }

   public void setDead() {
      this.isDead = true;
   }

   public void setSize(float var1, float var2) {
      if (var1 != this.width || var2 != this.height) {
         float var6 = this.width;
         this.width = var1;
         this.height = var2;
         this.setEntityBoundingBox(
            new AxisAlignedBB(
               this.getEntityBoundingBox().minX,
               this.getEntityBoundingBox().minY,
               this.getEntityBoundingBox().minZ,
               this.getEntityBoundingBox().minX + this.width,
               this.getEntityBoundingBox().minY + this.height,
               this.getEntityBoundingBox().minZ + this.width
            )
         );
         if (this.width > var6 && !this.firstUpdate && !this.worldObj.isRemote) {
            this.moveEntity(var6 - this.width, 0.0, var6 - this.width);
         }
      }
   }

   public void setRotation(float var1, float var2) {
      this.rotationYaw = var1 % 360.0F;
      this.rotationPitch = var2 % 360.0F;
   }

   public void setPosition(double var1, double var3, double var5) {
      this.posX = var1;
      this.posY = var3;
      this.posZ = var5;
      float var7 = this.width / 2.0F;
      float var8 = this.height;
      this.setEntityBoundingBox(new AxisAlignedBB(var1 - var7, var3, var5 - var7, var1 + var7, var3 + var8, var5 + var7));
   }

   public void setAngles(float var1, float var2) {
      float var3 = this.rotationPitch;
      float var4 = this.rotationYaw;
      this.rotationYaw = (float)(this.rotationYaw + var1 * 0.15);
      this.rotationPitch = (float)(this.rotationPitch - var2 * 0.15);
      this.rotationPitch = MathHelper.clamp_float(this.rotationPitch, -90.0F, 90.0F);
      this.prevRotationPitch = this.prevRotationPitch + (this.rotationPitch - var3);
      this.prevRotationYaw = this.prevRotationYaw + (this.rotationYaw - var4);
   }

   public void onUpdate() {
      this.onEntityUpdate();
   }

   public void onEntityUpdate() {
      this.worldObj.theProfiler.startSection("entityBaseTick");
      if (this.ridingEntity != null && this.ridingEntity.isDead) {
         this.ridingEntity = null;
      }

      this.prevDistanceWalkedModified = this.distanceWalkedModified;
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      this.prevRotationPitch = this.rotationPitch;
      this.prevRotationYaw = this.rotationYaw;
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

      this.spawnRunningParticles();
      this.handleWaterMovement();
      if (this.worldObj.isRemote) {
         this.fire = 0;
      } else if (this.fire > 0) {
         if (this.isImmuneToFire) {
            this.fire -= 4;
            if (this.fire < 0) {
               this.fire = 0;
            }
         } else {
            if (this.fire % 20 == 0) {
               this.attackEntityFrom(DamageSource.onFire, 1.0F);
            }

            this.fire--;
         }
      }

      if (this.isInLava()) {
         this.setOnFireFromLava();
         this.fallDistance *= 0.5F;
      }

      if (this.posY < -64.0) {
         this.kill();
      }

      if (!this.worldObj.isRemote) {
         this.setFlag(0, this.fire > 0);
      }

      this.firstUpdate = false;
      this.worldObj.theProfiler.endSection();
   }

   public int getMaxInPortalTime() {
      return 0;
   }

   public void setOnFireFromLava() {
      if (!this.isImmuneToFire) {
         this.attackEntityFrom(DamageSource.lava, 4.0F);
         this.setFire(15);
      }
   }

   public void setFire(int var1) {
      int var5 = var1 * 20;
      var5 = EnchantmentProtection.getFireTimeForEntity(this, var5);
      if (this.fire < var5) {
         this.fire = var5;
      }
   }

   public void extinguish() {
      this.fire = 0;
   }

   public void kill() {
      this.setDead();
   }

   public boolean isOffsetPositionInLiquid(double var1, double var3, double var5) {
      AxisAlignedBB var7 = this.getEntityBoundingBox().offset(var1, var3, var5);
      return this.isLiquidPresentInAABB(var7);
   }

   private boolean isLiquidPresentInAABB(AxisAlignedBB var1) {
      return this.worldObj.getCollidingBoundingBoxes(this, var1).isEmpty() && !this.worldObj.isAnyLiquid(var1);
   }

   public void moveEntity(double var1, double var3, double var5) {
      if (this.noClip) {
         this.setEntityBoundingBox(this.getEntityBoundingBox().offset(var1, var3, var5));
         this.resetPositionToBB();
      } else {
         this.worldObj.theProfiler.startSection("move");
         double var10 = this.posX;
         double var12 = this.posY;
         double var14 = this.posZ;
         if (this.isInWeb) {
            this.isInWeb = false;
            var1 *= 0.25;
            var3 *= 0.05F;
            var5 *= 0.25;
            this.motionX = 0.0;
            this.motionY = 0.0;
            this.motionZ = 0.0;
         }

         double var16 = var1;
         double var18 = var3;
         double var20 = var5;
         boolean var26 = this.onGround && this.isSneaking() && this instanceof EntityPlayer;
         SafeWalkEvent var27 = new SafeWalkEvent();
         Astolfo.INSTANCE.getEventBus().post(var27);
         if (var26 || this instanceof EntityPlayer && var27.isCancelled() && (this.onGround || !var27.isSafe())) {
            double var28 = 0.05;

            while (var1 != 0.0 && this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox().offset(var1, -1.0, 0.0)).isEmpty()) {
               if (var1 < var28 && var1 >= -var28) {
                  var1 = 0.0;
               } else if (var1 > 0.0) {
                  var1 -= var28;
               } else {
                  var1 += var28;
               }

               var16 = var1;
            }

            for (; var5 != 0.0 && this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox().offset(0.0, -1.0, var5)).isEmpty(); var20 = var5) {
               if (var5 < var28 && var5 >= -var28) {
                  var5 = 0.0;
               } else if (var5 > 0.0) {
                  var5 -= var28;
               } else {
                  var5 += var28;
               }
            }

            for (;
               var1 != 0.0 && var5 != 0.0 && this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox().offset(var1, -1.0, var5)).isEmpty();
               var20 = var5
            ) {
               if (var1 < var28 && var1 >= -var28) {
                  var1 = 0.0;
               } else if (var1 > 0.0) {
                  var1 -= var28;
               } else {
                  var1 += var28;
               }

               var16 = var1;
               if (var5 < var28 && var5 >= -var28) {
                  var5 = 0.0;
               } else if (var5 > 0.0) {
                  var5 -= var28;
               } else {
                  var5 += var28;
               }
            }
         }

         List var62 = this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox().addCoord(var1, var3, var5));
         AxisAlignedBB var29 = this.getEntityBoundingBox();

         for (Object var31$raw : var62) {
            AxisAlignedBB var31 = (AxisAlignedBB)var31$raw;
            var3 = var31.calculateYOffset(this.getEntityBoundingBox(), var3);
         }

         this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0, var3, 0.0));
         boolean var63 = this.onGround || var18 != var3 && var18 < 0.0;

         for (Object var32$raw : var62) {
            AxisAlignedBB var32 = (AxisAlignedBB)var32$raw;
            var1 = var32.A(this.getEntityBoundingBox(), var1);
         }

         this.setEntityBoundingBox(this.getEntityBoundingBox().offset(var1, 0.0, 0.0));

         for (Object var69$raw : var62) {
            AxisAlignedBB var69 = (AxisAlignedBB)var69$raw;
            var5 = var69.v(this.getEntityBoundingBox(), var5);
         }

         this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0, 0.0, var5));
         if (this.stepHeight > 0.0F && var63 && (var16 != var1 || var20 != var5)) {
            if (this instanceof EntityPlayerSP) {
               StepEvent var66 = new StepEvent(StepState.PRE, this, x(this, var16, var20, var1, var3, var5, var29));
               Astolfo.INSTANCE.getEventBus().post(var66);
               if (var66.isCancelled()) {
                  return;
               }
            }

            double var67 = var1;
            double var33 = var3;
            double var35 = var5;
            AxisAlignedBB var37 = this.getEntityBoundingBox();
            this.setEntityBoundingBox(var29);
            var3 = this.stepHeight;
            List var38 = this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox().addCoord(var16, var3, var20));
            AxisAlignedBB var39 = this.getEntityBoundingBox();
            AxisAlignedBB var40 = var39.addCoord(var16, 0.0, var20);
            double var41 = var3;

            for (Object var44$raw : var38) {
               AxisAlignedBB var44 = (AxisAlignedBB)var44$raw;
               var41 = var44.calculateYOffset(var40, var41);
            }

            var39 = var39.offset(0.0, var41, 0.0);
            double var82 = var16;

            for (Object var46$raw : var38) {
               AxisAlignedBB var46 = (AxisAlignedBB)var46$raw;
               var82 = var46.A(var39, var82);
            }

            var39 = var39.offset(var82, 0.0, 0.0);
            double var83 = var20;

            for (Object var48$raw : var38) {
               AxisAlignedBB var48 = (AxisAlignedBB)var48$raw;
               var83 = var48.v(var39, var83);
            }

            var39 = var39.offset(0.0, 0.0, var83);
            AxisAlignedBB var84 = this.getEntityBoundingBox();
            double var88 = var3;

            for (Object var51$raw : var38) {
               AxisAlignedBB var51 = (AxisAlignedBB)var51$raw;
               var88 = var51.calculateYOffset(var84, var88);
            }

            var84 = var84.offset(0.0, var88, 0.0);
            double var89 = var16;

            for (Object var53$raw : var38) {
               AxisAlignedBB var53 = (AxisAlignedBB)var53$raw;
               var89 = var53.A(var84, var89);
            }

            var84 = var84.offset(var89, 0.0, 0.0);
            double var90 = var20;

            for (Object var55$raw : var38) {
               AxisAlignedBB var55 = (AxisAlignedBB)var55$raw;
               var90 = var55.v(var84, var90);
            }

            var84 = var84.offset(0.0, 0.0, var90);
            double var91 = var82 * var82 + var83 * var83;
            double var56 = var89 * var89 + var90 * var90;
            if (var91 > var56) {
               var1 = var82;
               var5 = var83;
               var3 = -var41;
               this.setEntityBoundingBox(var39);
            } else {
               var1 = var89;
               var5 = var90;
               var3 = -var88;
               this.setEntityBoundingBox(var84);
            }

            for (Object var59$raw : var38) {
               AxisAlignedBB var59 = (AxisAlignedBB)var59$raw;
               var3 = var59.calculateYOffset(this.getEntityBoundingBox(), var3);
            }

            this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0, var3, 0.0));
            if (var67 * var67 + var35 * var35 >= var1 * var1 + var5 * var5) {
               var1 = var67;
               var3 = var33;
               var5 = var35;
               this.setEntityBoundingBox(var37);
            }

            StepEvent var92 = new StepEvent(StepState.POST, this, null);
            Astolfo.INSTANCE.getEventBus().post(var92);
         }

         this.worldObj.theProfiler.endSection();
         this.worldObj.theProfiler.startSection("rest");
         this.resetPositionToBB();
         this.isCollidedHorizontally = var16 != var1 || var20 != var5;
         this.isCollidedVertically = var18 != var3;
         this.onGround = this.isCollidedVertically && var18 < 0.0;
         this.isCollided = this.isCollidedHorizontally || this.isCollidedVertically;
         int var68 = MathHelper.floor_double(this.posX);
         int var70 = MathHelper.floor_double(this.posY - 0.2F);
         int var71 = MathHelper.floor_double(this.posZ);
         BlockPos var34 = new BlockPos(var68, var70, var71);
         Block var72 = this.worldObj.getBlockState(var34).getBlock();
         if (var72.getMaterial() == Material.air) {
            Block var36 = this.worldObj.getBlockState(var34.down()).getBlock();
            if (var36 instanceof BlockFence || var36 instanceof BlockWall || var36 instanceof BlockFenceGate) {
               var72 = var36;
               var34 = var34.down();
            }
         }

         this.updateFallState(var3, this.onGround, var72, var34);
         if (var16 != var1) {
            this.motionX = 0.0;
         }

         if (var20 != var5) {
            this.motionZ = 0.0;
         }

         if (var18 != var3) {
            var72.onLanded(this.worldObj, this);
         }

         if (this.canTriggerWalking() && !var26 && this.ridingEntity == null) {
            double var73 = this.posX - var10;
            double var76 = this.posY - var12;
            double var81 = this.posZ - var14;
            if (var72 != Blocks.ladder) {
               var76 = 0.0;
            }

            if (var72 != null && this.onGround) {
               var72.onEntityCollidedWithBlock(this.worldObj, var34, this);
            }

            this.distanceWalkedModified = (float)(this.distanceWalkedModified + MathHelper.sqrt_double(var73 * var73 + var81 * var81) * 0.6);
            this.distanceWalkedOnStepModified = (float)(
               this.distanceWalkedOnStepModified + MathHelper.sqrt_double(var73 * var73 + var76 * var76 + var81 * var81) * 0.6
            );
            if (this.distanceWalkedOnStepModified > this.nextStepDistance && var72.getMaterial() != Material.air) {
               this.nextStepDistance = (int)this.distanceWalkedOnStepModified + 1;
               if (this.isInWater()) {
                  float var42 = MathHelper.sqrt_double(this.motionX * this.motionX * 0.2F + this.motionY * this.motionY + this.motionZ * this.motionZ * 0.2F)
                     * 0.35F;
                  if (var42 > 1.0F) {
                     var42 = 1.0F;
                  }

                  this.playSound(this.getSwimSound(), var42, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
               }

               this.playStepSound(var34, var72);
            }
         }

         try {
            this.doBlockCollisions();
         } catch (Throwable var60) {
            CrashReport var75 = CrashReport.makeCrashReport(var60, "Checking entity block collision");
            CrashReportCategory var77 = var75.makeCategory("Entity being checked for collision");
            this.addEntityCrashInfo(var77);
            throw new ReportedException(var75);
         }

         boolean var74 = this.G();
         if (this.worldObj.a(this.getEntityBoundingBox().contract(0.001, 0.001, 0.001))) {
            this.dealFireDamage(1);
            if (!var74) {
               this.fire++;
               if (this.fire == 0) {
                  this.setFire(8);
               }
            }
         } else if (this.fire <= 0) {
            this.fire = -this.fireResistance;
         }

         if (var74 && this.fire > 0) {
            this.playSound("random.fizz", 0.7F, 1.6F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
            this.fire = -this.fireResistance;
         }

         this.worldObj.theProfiler.endSection();
      }
   }

   public static Vec3 x(Entity var0, double var1, double var3, double var5, double var7, double var9, AxisAlignedBB var11) {
      AxisAlignedBB var16 = var0.getEntityBoundingBox();
      double var17 = var5;
      double var21 = var9;
      AxisAlignedBB var23 = var16;
      var16 = var11;
      var7 = 2.0;
      List var24 = var0.worldObj.getCollidingBoundingBoxes(var0, var16.addCoord(var1, var7, var3));
      AxisAlignedBB var25 = var16;
      AxisAlignedBB var26 = var25.addCoord(var1, 0.0, var3);
      double var27 = var7;
      int var29 = 0;

      for (int var30 = var24.size(); var29 < var30; var29++) {
         var27 = ((AxisAlignedBB)var24.get(var29)).calculateYOffset(var26, var27);
      }

      var25 = var25.offset(0.0, var27, 0.0);
      double var62 = var1;
      int var32 = 0;

      for (int var33 = var24.size(); var32 < var33; var32++) {
         var62 = ((AxisAlignedBB)var24.get(var32)).A(var25, var62);
      }

      var25 = var25.offset(var62, 0.0, 0.0);
      double var63 = var3;
      int var35 = 0;

      for (int var36 = var24.size(); var35 < var36; var35++) {
         var63 = ((AxisAlignedBB)var24.get(var35)).v(var25, var63);
      }

      var25 = var25.offset(0.0, 0.0, var63);
      AxisAlignedBB var64 = var16;
      double var37 = var7;
      int var39 = 0;

      for (int var40 = var24.size(); var39 < var40; var39++) {
         var37 = ((AxisAlignedBB)var24.get(var39)).calculateYOffset(var64, var37);
      }

      var64 = var64.offset(0.0, var37, 0.0);
      double var68 = var1;
      int var42 = 0;

      for (int var43 = var24.size(); var42 < var43; var42++) {
         var68 = ((AxisAlignedBB)var24.get(var42)).A(var64, var68);
      }

      var64 = var64.offset(var68, 0.0, 0.0);
      double var69 = var3;
      int var45 = 0;

      for (int var46 = var24.size(); var45 < var46; var45++) {
         var69 = ((AxisAlignedBB)var24.get(var45)).v(var64, var69);
      }

      var64 = var64.offset(0.0, 0.0, var69);
      double var70 = var62 * var62 + var63 * var63;
      double var48 = var68 * var68 + var69 * var69;
      if (var70 > var48) {
         var5 = var62;
         var9 = var63;
         var7 = -var27;
         var16 = var25;
      } else {
         var5 = var68;
         var9 = var69;
         var7 = -var37;
         var16 = var64;
      }

      int var50 = 0;

      for (int var51 = var24.size(); var50 < var51; var50++) {
         var7 = ((AxisAlignedBB)var24.get(var50)).calculateYOffset(var16, var7);
      }

      var16 = var16.offset(0.0, var7, 0.0);
      if (var17 * var17 + var21 * var21 >= var5 * var5 + var9 * var9) {
         var16 = var23;
      }

      return new Vec3(
         var16.minX - var0.getEntityBoundingBox().minX, var16.minY - var0.getEntityBoundingBox().minY, var16.minZ - var0.getEntityBoundingBox().minZ
      );
   }

   private void resetPositionToBB() {
      this.posX = (this.getEntityBoundingBox().minX + this.getEntityBoundingBox().maxX) / 2.0;
      this.posY = this.getEntityBoundingBox().minY;
      this.posZ = (this.getEntityBoundingBox().minZ + this.getEntityBoundingBox().maxZ) / 2.0;
   }

   public String getSwimSound() {
      return "game.neutral.swim";
   }

   public void doBlockCollisions() {
      BlockPos var4 = new BlockPos(this.getEntityBoundingBox().minX + 0.001, this.getEntityBoundingBox().minY + 0.001, this.getEntityBoundingBox().minZ + 0.001);
      BlockPos var5 = new BlockPos(this.getEntityBoundingBox().maxX - 0.001, this.getEntityBoundingBox().maxY - 0.001, this.getEntityBoundingBox().maxZ - 0.001);
      if (this.worldObj.isAreaLoaded(var4, var5)) {
         for (int var6 = var4.getX(); var6 <= var5.getX(); var6++) {
            for (int var7 = var4.getY(); var7 <= var5.getY(); var7++) {
               for (int var8 = var4.getZ(); var8 <= var5.getZ(); var8++) {
                  BlockPos var9 = new BlockPos(var6, var7, var8);
                  IBlockState var10 = this.worldObj.getBlockState(var9);

                  try {
                     var10.getBlock().onEntityCollidedWithBlock(this.worldObj, var9, var10, this);
                  } catch (Throwable var14) {
                     CrashReport var12 = CrashReport.makeCrashReport(var14, "Colliding entity with block");
                     CrashReportCategory var13 = var12.makeCategory("Block being collided with");
                     CrashReportCategory.addBlockInfo(var13, var9, var10);
                     throw new ReportedException(var12);
                  }
               }
            }
         }
      }
   }

   public void playStepSound(BlockPos var1, Block var2) {
      Block.SoundType var6 = var2.stepSound;
      if (this.worldObj.getBlockState(var1.up()).getBlock() == Blocks.snow_layer) {
         var6 = Blocks.snow_layer.stepSound;
         this.playSound(var6.getStepSound(), var6.getVolume() * 0.15F, var6.getFrequency());
      } else if (!var2.getMaterial().isLiquid()) {
         this.playSound(var6.getStepSound(), var6.getVolume() * 0.15F, var6.getFrequency());
      }
   }

   public void playSound(String var1, float var2, float var3) {
      if (!this.isSilent()) {
         this.worldObj.playSoundAtEntity(this, var1, var2, var3);
      }
   }

   public boolean isSilent() {
      return this.dataWatcher.getWatchableObjectByte(4) == 1;
   }

   public void setSilent(boolean var1) {
      this.dataWatcher.updateObject(4, (byte)(var1 ? 1 : 0));
   }

   public boolean canTriggerWalking() {
      return true;
   }

   public void updateFallState(double var1, boolean var3, Block var4, BlockPos var5) {
      if (var3) {
         if (this.fallDistance > 0.0F) {
            if (var4 != null) {
               var4.onFallenUpon(this.worldObj, var5, this, this.fallDistance);
            } else {
               this.fall(this.fallDistance, 1.0F);
            }

            this.fallDistance = 0.0F;
         }
      } else if (var1 < 0.0) {
         this.fallDistance = (float)(this.fallDistance - var1);
      }
   }

   public AxisAlignedBB getCollisionBoundingBox() {
      return null;
   }

   public void dealFireDamage(int var1) {
      if (!this.isImmuneToFire) {
         this.attackEntityFrom(DamageSource.inFire, var1);
      }
   }

   public final boolean isImmuneToFire() {
      return this.isImmuneToFire;
   }

   public void fall(float var1, float var2) {
      if (this.riddenByEntity != null) {
         this.riddenByEntity.fall(var1, var2);
      }
   }

   public boolean G() {
      return this.inWater
         || this.worldObj.isRainingAt(new BlockPos(this.posX, this.posY, this.posZ))
         || this.worldObj.isRainingAt(new BlockPos(this.posX, this.posY + this.height, this.posZ));
   }

   public boolean isInWater() {
      return this.inWater;
   }

   public boolean handleWaterMovement() {
      if (this.worldObj.handleMaterialAcceleration(this.getEntityBoundingBox().expand(0.0, -0.4F, 0.0).contract(0.001, 0.001, 0.001), Material.water, this)) {
         if (!this.inWater && !this.firstUpdate) {
            this.resetHeight();
         }

         this.fallDistance = 0.0F;
         this.inWater = true;
         this.fire = 0;
      } else {
         this.inWater = false;
      }

      return this.inWater;
   }

   public void resetHeight() {
      float var4 = MathHelper.sqrt_double(this.motionX * this.motionX * 0.2F + this.motionY * this.motionY + this.motionZ * this.motionZ * 0.2F) * 0.2F;
      if (var4 > 1.0F) {
         var4 = 1.0F;
      }

      this.playSound(this.getSplashSound(), var4, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
      float var5 = MathHelper.floor_double(this.getEntityBoundingBox().minY);

      for (int var6 = 0; var6 < 1.0F + this.width * 20.0F; var6++) {
         float var7 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
         float var8 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
         this.worldObj
            .spawnParticle(
               EnumParticleTypes.WATER_BUBBLE,
               this.posX + var7,
               var5 + 1.0F,
               this.posZ + var8,
               this.motionX,
               this.motionY - this.rand.nextFloat() * 0.2F,
               this.motionZ
            );
      }

      for (int var9 = 0; var9 < 1.0F + this.width * 20.0F; var9++) {
         float var10 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
         float var11 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
         this.worldObj
            .spawnParticle(EnumParticleTypes.WATER_SPLASH, this.posX + var10, var5 + 1.0F, this.posZ + var11, this.motionX, this.motionY, this.motionZ);
      }
   }

   public void spawnRunningParticles() {
      if (this.isSprinting() && !this.isInWater()) {
         this.createRunningParticles();
      }
   }

   public void createRunningParticles() {
      int var3 = MathHelper.floor_double(this.posX);
      int var4 = MathHelper.floor_double(this.posY - 0.2F);
      int var5 = MathHelper.floor_double(this.posZ);
      BlockPos var6 = new BlockPos(var3, var4, var5);
      IBlockState var7 = this.worldObj.getBlockState(var6);
      Block var8 = var7.getBlock();
      if (var8.getRenderType() != -1) {
         this.worldObj
            .spawnParticle(
               EnumParticleTypes.BLOCK_CRACK,
               this.posX + (this.rand.nextFloat() - 0.5) * this.width,
               this.getEntityBoundingBox().minY + 0.1,
               this.posZ + (this.rand.nextFloat() - 0.5) * this.width,
               -this.motionX * 4.0,
               1.5,
               -this.motionZ * 4.0,
               Block.getStateId(var7)
            );
      }
   }

   public String getSplashSound() {
      return "game.neutral.swim.splash";
   }

   public boolean isInsideOfMaterial(Material var1) {
      double var5 = this.posY + this.getEyeHeight();
      BlockPos var7 = new BlockPos(this.posX, var5, this.posZ);
      IBlockState var8 = this.worldObj.getBlockState(var7);
      Block var9 = var8.getBlock();
      if (var9.getMaterial() == var1) {
         float var10 = BlockLiquid.getLiquidHeightPercent(var8.getBlock().getMetaFromState(var8)) - 0.11111111F;
         float var11 = var7.getY() + 1 - var10;
         boolean var12 = var5 < var11;
         return !var12 && this instanceof EntityPlayer ? false : var12;
      } else {
         return false;
      }
   }

   public boolean isInLava() {
      return this.worldObj.isMaterialInBB(this.getEntityBoundingBox().expand(-0.1F, -0.4F, -0.1F), Material.lava);
   }

   public void moveFlying(float var1, float var2, float var3) {
      if (this.equals(Minecraft.getMinecraft().thePlayer)) {
         StrafeEvent var7 = new StrafeEvent(var1, var2, var3);
         Astolfo.INSTANCE.getEventBus().post(var7);
         if (var7.isCancelled()) {
            return;
         }
      }

      float var12 = var1 * var1 + var2 * var2;
      if (var12 >= 1.0E-4F) {
         var12 = MathHelper.sqrt_float(var12);
         if (var12 < 1.0F) {
            var12 = 1.0F;
         }

         var12 = var3 / var12;
         var1 *= var12;
         var2 *= var12;
         float var8 = MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F);
         float var9 = MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F);
         this.motionX += var1 * var9 - var2 * var8;
         this.motionZ += var2 * var9 + var1 * var8;
      }
   }

   public int getBrightnessForRender(float var1) {
      BlockPos var5 = new BlockPos(this.posX, this.posY + this.getEyeHeight(), this.posZ);
      return this.worldObj.isBlockLoaded(var5) ? this.worldObj.getCombinedLight(var5, 0) : 0;
   }

   public float getBrightness(float var1) {
      BlockPos var5 = new BlockPos(this.posX, this.posY + this.getEyeHeight(), this.posZ);
      return this.worldObj.isBlockLoaded(var5) ? this.worldObj.getLightBrightness(var5) : 0.0F;
   }

   public void setWorld(World var1) {
      this.worldObj = var1;
   }

   public void setPositionAndRotation(double var1, double var3, double var5, float var7, float var8) {
      this.prevPosX = this.posX = var1;
      this.prevPosY = this.posY = var3;
      this.prevPosZ = this.posZ = var5;
      this.prevRotationYaw = this.rotationYaw = var7;
      this.prevRotationPitch = this.rotationPitch = var8;
      double var12 = this.prevRotationYaw - var7;
      if (var12 < -180.0) {
         this.prevRotationYaw += 360.0F;
      }

      if (var12 >= 180.0) {
         this.prevRotationYaw -= 360.0F;
      }

      this.setPosition(this.posX, this.posY, this.posZ);
      this.setRotation(var7, var8);
   }

   public void moveToBlockPosAndAngles(BlockPos var1, float var2, float var3) {
      this.setLocationAndAngles(var1.getX() + 0.5, var1.getY(), var1.getZ() + 0.5, var2, var3);
   }

   public void setLocationAndAngles(double var1, double var3, double var5, float var7, float var8) {
      this.lastTickPosX = this.prevPosX = this.posX = var1;
      this.lastTickPosY = this.prevPosY = this.posY = var3;
      this.lastTickPosZ = this.prevPosZ = this.posZ = var5;
      this.rotationYaw = var7;
      this.rotationPitch = var8;
      this.setPosition(this.posX, this.posY, this.posZ);
   }

   public float getDistanceToEntity(Entity var1) {
      float var2 = (float)(this.posX - var1.posX);
      float var3 = (float)(this.posY - var1.posY);
      float var4 = (float)(this.posZ - var1.posZ);
      return MathHelper.sqrt_float(var2 * var2 + var3 * var3 + var4 * var4);
   }

   public double getDistanceSq(double var1, double var3, double var5) {
      double var7 = this.posX - var1;
      double var9 = this.posY - var3;
      double var11 = this.posZ - var5;
      return var7 * var7 + var9 * var9 + var11 * var11;
   }

   public double getDistanceSq(BlockPos var1) {
      return var1.distanceSq(this.posX, this.posY, this.posZ);
   }

   public double getDistanceSqToCenter(BlockPos var1) {
      return var1.distanceSqToCenter(this.posX, this.posY, this.posZ);
   }

   public double getDistance(double var1, double var3, double var5) {
      double var7 = this.posX - var1;
      double var9 = this.posY - var3;
      double var11 = this.posZ - var5;
      return MathHelper.sqrt_double(var7 * var7 + var9 * var9 + var11 * var11);
   }

   public double getDistanceSqToEntity(Entity var1) {
      double var2 = this.posX - var1.posX;
      double var4 = this.posY - var1.posY;
      double var6 = this.posZ - var1.posZ;
      return var2 * var2 + var4 * var4 + var6 * var6;
   }

   public void onCollideWithPlayer(EntityPlayer var1) {
   }

   public void applyEntityCollision(Entity var1) {
      if (var1.riddenByEntity != this && var1.ridingEntity != this && !var1.noClip && !this.noClip) {
         double var5 = var1.posX - this.posX;
         double var7 = var1.posZ - this.posZ;
         double var9 = MathHelper.abs_max(var5, var7);
         if (var9 >= 0.01F) {
            var9 = MathHelper.sqrt_double(var9);
            var5 /= var9;
            var7 /= var9;
            double var11 = 1.0 / var9;
            if (var11 > 1.0) {
               var11 = 1.0;
            }

            var5 *= var11;
            var7 *= var11;
            var5 *= 0.05F;
            var7 *= 0.05F;
            var5 *= 1.0F - this.entityCollisionReduction;
            var7 *= 1.0F - this.entityCollisionReduction;
            if (this.riddenByEntity == null) {
               this.addVelocity(-var5, 0.0, -var7);
            }

            if (var1.riddenByEntity == null) {
               var1.addVelocity(var5, 0.0, var7);
            }
         }
      }
   }

   public void addVelocity(double var1, double var3, double var5) {
      this.motionX += var1;
      this.motionY += var3;
      this.motionZ += var5;
      this.isAirBorne = true;
   }

   public void setBeenAttacked() {
      this.velocityChanged = true;
   }

   public boolean attackEntityFrom(DamageSource var1, float var2) {
      if (this.isEntityInvulnerable(var1)) {
         return false;
      }

      this.setBeenAttacked();
      return false;
   }

   public Vec3 getLook(float var1) {
      if (var1 == 1.0F) {
         return this.getVectorForRotation(this.rotationPitch, this.rotationYaw);
      }

      float var5 = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * var1;
      float var6 = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * var1;
      return this.getVectorForRotation(var5, var6);
   }

   public final Vec3 getVectorForRotation(float var1, float var2) {
      float var3 = MathHelper.cos(-var2 * (float) (Math.PI / 180.0) - (float) Math.PI);
      float var4 = MathHelper.sin(-var2 * (float) (Math.PI / 180.0) - (float) Math.PI);
      float var5 = -MathHelper.cos(-var1 * (float) (Math.PI / 180.0));
      float var6 = MathHelper.sin(-var1 * (float) (Math.PI / 180.0));
      return new Vec3(var4 * var5, var6, var3 * var5);
   }

   public Vec3 getPositionEyes(float var1) {
      if (var1 == 1.0F) {
         return new Vec3(this.posX, this.posY + this.getEyeHeight(), this.posZ);
      }

      double var4 = this.prevPosX + (this.posX - this.prevPosX) * var1;
      double var6 = this.prevPosY + (this.posY - this.prevPosY) * var1 + this.getEyeHeight();
      double var8 = this.prevPosZ + (this.posZ - this.prevPosZ) * var1;
      return new Vec3(var4, var6, var8);
   }

   public MovingObjectPosition rayTrace(double var1, float var3) {
      Vec3 var4 = this.getPositionEyes(var3);
      Vec3 var5 = this.getLook(var3);
      Vec3 var6 = var4.addVector(var5.xCoord * var1, var5.yCoord * var1, var5.zCoord * var1);
      return this.worldObj.rayTraceBlocks(var4, var6, false, false, true);
   }

   public boolean canBeCollidedWith() {
      return false;
   }

   public boolean canBePushed() {
      return false;
   }

   public void addToPlayerScore(Entity var1, int var2) {
   }

   public boolean isInRangeToRender3d(double var1, double var3, double var5) {
      double var10 = this.posX - var1;
      double var12 = this.posY - var3;
      double var14 = this.posZ - var5;
      double var16 = var10 * var10 + var12 * var12 + var14 * var14;
      if (ModuleManager.I.isEnabled()) {
         return !Minecraft.getMinecraft().thePlayer.C(this) && NoRender.visGraph.getValue() && !(this instanceof EntityPlayer)
            ? false
            : !NoRender.smart.getValue() || this.isInRangeToRenderDist(var16 * 10.0);
      } else {
         return this.isInRangeToRenderDist(var16);
      }
   }

   public boolean isInRangeToRenderDist(double var1) {
      double var6 = this.getEntityBoundingBox().getAverageEdgeLength();
      if (Double.isNaN(var6)) {
         var6 = 1.0;
      }

      var6 = var6 * 64.0 * this.renderDistanceWeight;
      return var1 < var6 * var6;
   }

   public boolean writeMountToNBT(NBTTagCompound var1) {
      String var5 = this.getEntityString();
      if (!this.isDead && var5 != null) {
         var1.setString("id", var5);
         this.writeToNBT(var1);
         return true;
      } else {
         return false;
      }
   }

   public boolean writeToNBTOptional(NBTTagCompound var1) {
      String var5 = this.getEntityString();
      if (!this.isDead && var5 != null && this.riddenByEntity == null) {
         var1.setString("id", var5);
         this.writeToNBT(var1);
         return true;
      } else {
         return false;
      }
   }

   public void writeToNBT(NBTTagCompound var1) {
      try {
         var1.setTag("Pos", this.newDoubleNBTList(this.posX, this.posY, this.posZ));
         var1.setTag("Motion", this.newDoubleNBTList(this.motionX, this.motionY, this.motionZ));
         var1.setTag("Rotation", this.newFloatNBTList(this.rotationYaw, this.rotationPitch));
         var1.setFloat("FallDistance", this.fallDistance);
         var1.setShort("Fire", (short)this.fire);
         var1.setShort("Air", (short)this.getAir());
         var1.setBoolean("OnGround", this.onGround);
         var1.setInteger("Dimension", this.dimension);
         var1.setBoolean("Invulnerable", this.invulnerable);
         var1.setInteger("PortalCooldown", this.timeUntilPortal);
         var1.setLong("UUIDMost", this.getUniqueID().getMostSignificantBits());
         var1.setLong("UUIDLeast", this.getUniqueID().getLeastSignificantBits());
         if (this.getCustomNameTag() != null && this.getCustomNameTag().length() > 0) {
            var1.setString("CustomName", this.getCustomNameTag());
            var1.setBoolean("CustomNameVisible", this.getAlwaysRenderNameTag());
         }

         this.cmdResultStats.writeStatsToNBT(var1);
         if (this.isSilent()) {
            var1.setBoolean("Silent", this.isSilent());
         }

         this.writeEntityToNBT(var1);
         if (this.ridingEntity != null) {
            NBTTagCompound var5 = new NBTTagCompound();
            if (this.ridingEntity.writeMountToNBT(var5)) {
               var1.setTag("Riding", var5);
            }
         }
      } catch (Throwable var8) {
         CrashReport var6 = CrashReport.makeCrashReport(var8, "Saving entity NBT");
         CrashReportCategory var7 = var6.makeCategory("Entity being saved");
         this.addEntityCrashInfo(var7);
         throw new ReportedException(var6);
      }
   }

   public void readFromNBT(NBTTagCompound var1) {
      try {
         NBTTagList var5 = var1.getTagList("Pos", 6);
         NBTTagList var9 = var1.getTagList("Motion", 6);
         NBTTagList var10 = var1.getTagList("Rotation", 5);
         this.motionX = var9.getDoubleAt(0);
         this.motionY = var9.getDoubleAt(1);
         this.motionZ = var9.getDoubleAt(2);
         if (Math.abs(this.motionX) > 10.0) {
            this.motionX = 0.0;
         }

         if (Math.abs(this.motionY) > 10.0) {
            this.motionY = 0.0;
         }

         if (Math.abs(this.motionZ) > 10.0) {
            this.motionZ = 0.0;
         }

         this.prevPosX = this.lastTickPosX = this.posX = var5.getDoubleAt(0);
         this.prevPosY = this.lastTickPosY = this.posY = var5.getDoubleAt(1);
         this.prevPosZ = this.lastTickPosZ = this.posZ = var5.getDoubleAt(2);
         this.prevRotationYaw = this.rotationYaw = var10.getFloatAt(0);
         this.prevRotationPitch = this.rotationPitch = var10.getFloatAt(1);
         this.setRotationYawHead(this.rotationYaw);
         this.setRenderYawOffset(this.rotationYaw);
         this.fallDistance = var1.getFloat("FallDistance");
         this.fire = var1.getShort("Fire");
         this.setAir(var1.getShort("Air"));
         this.onGround = var1.getBoolean("OnGround");
         this.dimension = var1.getInteger("Dimension");
         this.invulnerable = var1.getBoolean("Invulnerable");
         this.timeUntilPortal = var1.getInteger("PortalCooldown");
         if (var1.hasKey("UUIDMost", 4) && var1.hasKey("UUIDLeast", 4)) {
            this.entityUniqueID = new UUID(var1.getLong("UUIDMost"), var1.getLong("UUIDLeast"));
         } else if (var1.hasKey("UUID", 8)) {
            this.entityUniqueID = UUID.fromString(var1.getString("UUID"));
         }

         this.setPosition(this.posX, this.posY, this.posZ);
         this.setRotation(this.rotationYaw, this.rotationPitch);
         if (var1.hasKey("CustomName", 8) && var1.getString("CustomName").length() > 0) {
            this.setCustomNameTag(var1.getString("CustomName"));
         }

         this.setAlwaysRenderNameTag(var1.getBoolean("CustomNameVisible"));
         this.cmdResultStats.readStatsFromNBT(var1);
         this.setSilent(var1.getBoolean("Silent"));
         this.readEntityFromNBT(var1);
         if (this.shouldSetPosAfterLoading()) {
            this.setPosition(this.posX, this.posY, this.posZ);
         }
      } catch (Throwable var8) {
         CrashReport var6 = CrashReport.makeCrashReport(var8, "Loading entity NBT");
         CrashReportCategory var7 = var6.makeCategory("Entity being loaded");
         this.addEntityCrashInfo(var7);
         throw new ReportedException(var6);
      }
   }

   public boolean shouldSetPosAfterLoading() {
      return true;
   }

   public final String getEntityString() {
      return EntityList.getEntityString(this);
   }

   public abstract void readEntityFromNBT(NBTTagCompound var1);

   public abstract void writeEntityToNBT(NBTTagCompound var1);

   public void onChunkLoad() {
   }

   public NBTTagList newDoubleNBTList(double... var1) {
      NBTTagList var5 = new NBTTagList();

      for (double var9 : var1) {
         var5.appendTag(new NBTTagDouble(var9));
      }

      return var5;
   }

   public NBTTagList newFloatNBTList(float... var1) {
      NBTTagList var5 = new NBTTagList();

      for (float var9 : var1) {
         var5.appendTag(new NBTTagFloat(var9));
      }

      return var5;
   }

   public EntityItem dropItem(Item var1, int var2) {
      return this.dropItemWithOffset(var1, var2, 0.0F);
   }

   public EntityItem dropItemWithOffset(Item var1, int var2, float var3) {
      return this.entityDropItem(new ItemStack(var1, var2, 0), var3);
   }

   public EntityItem entityDropItem(ItemStack var1, float var2) {
      if (var1.stackSize != 0 && var1.getItem() != null) {
         EntityItem var6 = new EntityItem(this.worldObj, this.posX, this.posY + var2, this.posZ, var1);
         var6.setDefaultPickupDelay();
         this.worldObj.spawnEntityInWorld(var6);
         return var6;
      } else {
         return null;
      }
   }

   public boolean isEntityAlive() {
      return !this.isDead;
   }

   public boolean isEntityInsideOpaqueBlock() {
      if (this.noClip) {
         return false;
      }

      BlockPos.MutableBlockPos var4 = new BlockPos.MutableBlockPos(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);

      for (int var5 = 0; var5 < 8; var5++) {
         int var6 = MathHelper.floor_double(this.posY + ((var5 >> 0) % 2 - 0.5F) * 0.1F + this.getEyeHeight());
         int var7 = MathHelper.floor_double(this.posX + ((var5 >> 1) % 2 - 0.5F) * this.width * 0.8F);
         int var8 = MathHelper.floor_double(this.posZ + ((var5 >> 2) % 2 - 0.5F) * this.width * 0.8F);
         if (var4.getX() != var7 || var4.getY() != var6 || var4.getZ() != var8) {
            var4.set(var7, var6, var8);
            if (this.worldObj.getBlockState(var4).getBlock().isVisuallyOpaque()) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean interactFirst(EntityPlayer var1) {
      return false;
   }

   public AxisAlignedBB getCollisionBox(Entity var1) {
      return null;
   }

   public void updateRidden() {
      if (this.ridingEntity.isDead) {
         this.ridingEntity = null;
      } else {
         this.motionX = 0.0;
         this.motionY = 0.0;
         this.motionZ = 0.0;
         this.onUpdate();
         if (this.ridingEntity != null) {
            this.ridingEntity.updateRiderPosition();
            this.entityRiderYawDelta = this.entityRiderYawDelta + (this.ridingEntity.rotationYaw - this.ridingEntity.prevRotationYaw);
            this.entityRiderPitchDelta = this.entityRiderPitchDelta + (this.ridingEntity.rotationPitch - this.ridingEntity.prevRotationPitch);

            while (this.entityRiderYawDelta >= 180.0) {
               this.entityRiderYawDelta -= 360.0;
            }

            while (this.entityRiderYawDelta < -180.0) {
               this.entityRiderYawDelta += 360.0;
            }

            while (this.entityRiderPitchDelta >= 180.0) {
               this.entityRiderPitchDelta -= 360.0;
            }

            while (this.entityRiderPitchDelta < -180.0) {
               this.entityRiderPitchDelta += 360.0;
            }

            double var4 = this.entityRiderYawDelta * 0.5;
            double var6 = this.entityRiderPitchDelta * 0.5;
            float var8 = 10.0F;
            if (var4 > var8) {
               var4 = var8;
            }

            if (var4 < -var8) {
               var4 = -var8;
            }

            if (var6 > var8) {
               var6 = var8;
            }

            if (var6 < -var8) {
               var6 = -var8;
            }

            this.entityRiderYawDelta -= var4;
            this.entityRiderPitchDelta -= var6;
         }
      }
   }

   public void updateRiderPosition() {
      if (this.riddenByEntity != null) {
         this.riddenByEntity.setPosition(this.posX, this.posY + this.getMountedYOffset() + this.riddenByEntity.getYOffset(), this.posZ);
      }
   }

   public double getYOffset() {
      return 0.0;
   }

   public double getMountedYOffset() {
      return this.height * 0.75;
   }

   public void mountEntity(Entity var1) {
      this.entityRiderPitchDelta = 0.0;
      this.entityRiderYawDelta = 0.0;
      if (var1 == null) {
         if (this.ridingEntity != null) {
            this.setLocationAndAngles(
               this.ridingEntity.posX,
               this.ridingEntity.getEntityBoundingBox().minY + this.ridingEntity.height,
               this.ridingEntity.posZ,
               this.rotationYaw,
               this.rotationPitch
            );
            this.ridingEntity.riddenByEntity = null;
         }

         this.ridingEntity = null;
      } else {
         if (this.ridingEntity != null) {
            this.ridingEntity.riddenByEntity = null;
         }

         if (var1 != null) {
            for (Entity var5 = var1.ridingEntity; var5 != null; var5 = var5.ridingEntity) {
               if (var5 == this) {
                  return;
               }
            }
         }

         this.ridingEntity = var1;
         var1.riddenByEntity = this;
      }
   }

   public void setPositionAndRotation2(double var1, double var3, double var5, float var7, float var8, int var9, boolean var10) {
      this.setPosition(var1, var3, var5);
      this.setRotation(var7, var8);
      List var14 = this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox().contract(0.03125, 0.0, 0.03125));
      if (!var14.isEmpty()) {
         double var15 = 0.0;

         for (Object var18$raw : var14) {
            AxisAlignedBB var18 = (AxisAlignedBB)var18$raw;
            if (var18.maxY > var15) {
               var15 = var18.maxY;
            }
         }

         var3 += var15 - this.getEntityBoundingBox().minY;
         this.setPosition(var1, var3, var5);
      }
   }

   public float getCollisionBorderSize() {
      if (!(this instanceof EntityPlayer)) {
         return 0.1F;
      }

      HitboxEvent var3 = new HitboxEvent(this);
      Astolfo.INSTANCE.getEventBus().post(var3);
      return var3.borderSize;
   }

   public Vec3 getLookVec() {
      return null;
   }

   public void setPortal(BlockPos var1) {
      if (this.timeUntilPortal > 0) {
         this.timeUntilPortal = this.getPortalCooldown();
      } else {
         if (!this.worldObj.isRemote && !var1.equals(this.lastPortalPos)) {
            this.lastPortalPos = var1;
            BlockPattern.PatternHelper var5 = Blocks.portal.func_181089_f(this.worldObj, var1);
            double var6 = var5.getFinger().getAxis() == EnumFacing.Axis.X ? var5.getPos().getZ() : var5.getPos().getX();
            double var8 = var5.getFinger().getAxis() == EnumFacing.Axis.X ? this.posZ : this.posX;
            var8 = Math.abs(
               MathHelper.func_181160_c(
                  var8 - (var5.getFinger().rotateY().getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE ? 1 : 0), var6, var6 - var5.func_181118_d()
               )
            );
            double var10 = MathHelper.func_181160_c(this.posY - 1.0, var5.getPos().getY(), var5.getPos().getY() - var5.func_181119_e());
            this.lastPortalVec = new Vec3(var8, var10, 0.0);
            this.teleportDirection = var5.getFinger();
         }

         this.inPortal = true;
      }
   }

   public int getPortalCooldown() {
      return 300;
   }

   public void setVelocity(double var1, double var3, double var5) {
      this.motionX = var1;
      this.motionY = var3;
      this.motionZ = var5;
   }

   public void handleStatusUpdate(byte var1) {
   }

   public void performHurtAnimation() {
   }

   public ItemStack[] getInventory() {
      return null;
   }

   public void setCurrentItemOrArmor(int var1, ItemStack var2) {
   }

   public boolean isBurning() {
      boolean var4 = this.worldObj != null && this.worldObj.isRemote;
      return !this.isImmuneToFire && (this.fire > 0 || var4 && this.getFlag(0));
   }

   public boolean E() {
      return this.ridingEntity != null;
   }

   public boolean isSneaking() {
      return this.getFlag(1);
   }

   public void v(boolean var1) {
      this.setFlag(1, var1);
   }

   public boolean isSprinting() {
      return this.getFlag(3);
   }

   public void setSprinting(boolean var1) {
      this.setFlag(3, var1);
   }

   public boolean isInvisible() {
      return this.getFlag(5);
   }

   public boolean isInvisibleToPlayer(EntityPlayer var1) {
      return var1.isSpectator() ? false : this.isInvisible();
   }

   public void setInvisible(boolean var1) {
      this.setFlag(5, var1);
   }

   public boolean m() {
      return this.getFlag(4);
   }

   public void setEating(boolean var1) {
      this.setFlag(4, var1);
   }

   public boolean getFlag(int var1) {
      return (this.dataWatcher.getWatchableObjectByte(0) & 1 << var1) != 0;
   }

   public void setFlag(int var1, boolean var2) {
      byte var6 = this.dataWatcher.getWatchableObjectByte(0);
      if (var2) {
         this.dataWatcher.updateObject(0, (byte)(var6 | 1 << var1));
      } else {
         this.dataWatcher.updateObject(0, (byte)(var6 & ~(1 << var1)));
      }
   }

   public int getAir() {
      return this.dataWatcher.getWatchableObjectShort(1);
   }

   public void setAir(int var1) {
      this.dataWatcher.updateObject(1, (short)var1);
   }

   public void onStruckByLightning(EntityLightningBolt var1) {
      this.attackEntityFrom(DamageSource.lightningBolt, 5.0F);
      this.fire++;
      if (this.fire == 0) {
         this.setFire(8);
      }
   }

   public void onKillEntity(EntityLivingBase var1) {
   }

   public boolean pushOutOfBlocks(double var1, double var3, double var5) {
      BlockPos var10 = new BlockPos(var1, var3, var5);
      double var11 = var1 - var10.getX();
      double var13 = var3 - var10.getY();
      double var15 = var5 - var10.getZ();
      List var17 = this.worldObj.getCollisionBoxes(this.getEntityBoundingBox());
      if (var17.isEmpty() && !this.worldObj.isBlockFullCube(var10)) {
         return false;
      }

      byte var18 = 3;
      double var19 = 9999.0;
      if (!this.worldObj.isBlockFullCube(var10.west()) && var11 < var19) {
         var19 = var11;
         var18 = 0;
      }

      if (!this.worldObj.isBlockFullCube(var10.east()) && 1.0 - var11 < var19) {
         var19 = 1.0 - var11;
         var18 = 1;
      }

      if (!this.worldObj.isBlockFullCube(var10.up()) && 1.0 - var13 < var19) {
         var19 = 1.0 - var13;
         var18 = 3;
      }

      if (!this.worldObj.isBlockFullCube(var10.north()) && var15 < var19) {
         var19 = var15;
         var18 = 4;
      }

      if (!this.worldObj.isBlockFullCube(var10.south()) && 1.0 - var15 < var19) {
         var19 = 1.0 - var15;
         var18 = 5;
      }

      float var21 = this.rand.nextFloat() * 0.2F + 0.1F;
      if (var18 == 0) {
         this.motionX = -var21;
      }

      if (var18 == 1) {
         this.motionX = var21;
      }

      if (var18 == 3) {
         this.motionY = var21;
      }

      if (var18 == 4) {
         this.motionZ = -var21;
      }

      if (var18 == 5) {
         this.motionZ = var21;
      }

      return true;
   }

   public void setInWeb() {
      this.isInWeb = true;
      this.fallDistance = 0.0F;
   }

   @Override
   public String getName() {
      if (this.hasCustomName()) {
         return this.getCustomNameTag();
      }

      String var4 = EntityList.getEntityString(this);
      if (var4 == null) {
         var4 = "generic";
      }

      return StatCollector.translateToLocal("entity." + var4 + ".name");
   }

   public Entity[] getParts() {
      return null;
   }

   public boolean isEntityEqual(Entity var1) {
      return this == var1;
   }

   public float getRotationYawHead() {
      return 0.0F;
   }

   public void setRotationYawHead(float var1) {
   }

   public void setRenderYawOffset(float var1) {
   }

   public boolean canAttackWithItem() {
      return true;
   }

   public boolean hitByEntity(Entity var1) {
      return false;
   }

   @Override
   public String toString() {
      return String.format(
         "%s['%s'/%d, l='%s', x=%.2f, y=%.2f, z=%.2f]",
         this.getClass().getSimpleName(),
         this.getName(),
         this.entityId,
         this.worldObj == null ? "~NULL~" : this.worldObj.getWorldInfo().getWorldName(),
         this.posX,
         this.posY,
         this.posZ
      );
   }

   public boolean isEntityInvulnerable(DamageSource var1) {
      return this.invulnerable && var1 != DamageSource.outOfWorld && !var1.isCreativePlayer();
   }

   public void z(Entity var1) {
      this.setLocationAndAngles(var1.posX, var1.posY, var1.posZ, var1.rotationYaw, var1.rotationPitch);
   }

   public void copyDataFromOld(Entity var1) {
      NBTTagCompound var2 = new NBTTagCompound();
      var1.writeToNBT(var2);
      this.readFromNBT(var2);
      this.timeUntilPortal = var1.timeUntilPortal;
      this.lastPortalPos = var1.lastPortalPos;
      this.lastPortalVec = var1.lastPortalVec;
      this.teleportDirection = var1.teleportDirection;
   }

   public void travelToDimension(int var1) {
      if (!this.worldObj.isRemote && !this.isDead) {
         this.worldObj.theProfiler.startSection("changeDimension");
         MinecraftServer var5 = MinecraftServer.getServer();
         int var6 = this.dimension;
         WorldServer var7 = var5.worldServerForDimension(var6);
         WorldServer var8 = var5.worldServerForDimension(var1);
         this.dimension = var1;
         if (var6 == 1 && var1 == 1) {
            var8 = var5.worldServerForDimension(0);
            this.dimension = 0;
         }

         this.worldObj.removeEntity(this);
         this.isDead = false;
         this.worldObj.theProfiler.startSection("reposition");
         var5.getConfigurationManager().transferEntityToWorld(this, var6, var7, var8);
         this.worldObj.theProfiler.endStartSection("reloading");
         Entity var9 = EntityList.createEntityByName(EntityList.getEntityString(this), var8);
         if (var9 != null) {
            var9.copyDataFromOld(this);
            if (var6 == 1 && var1 == 1) {
               BlockPos var10 = this.worldObj.getTopSolidOrLiquidBlock(var8.getSpawnPoint());
               var9.moveToBlockPosAndAngles(var10, var9.rotationYaw, var9.rotationPitch);
            }

            var8.spawnEntityInWorld(var9);
         }

         this.isDead = true;
         this.worldObj.theProfiler.endSection();
         var7.resetUpdateEntityTick();
         var8.resetUpdateEntityTick();
         this.worldObj.theProfiler.endSection();
      }
   }

   public float getExplosionResistance(Explosion var1, World var2, BlockPos var3, IBlockState var4) {
      return var4.getBlock().getExplosionResistance(this);
   }

   public boolean verifyExplosion(Explosion var1, World var2, BlockPos var3, IBlockState var4, float var5) {
      return true;
   }

   public int getMaxFallHeight() {
      return 3;
   }

   public Vec3 func_181014_aG() {
      return this.lastPortalVec;
   }

   public EnumFacing getTeleportDirection() {
      return this.teleportDirection;
   }

   public boolean doesEntityNotTriggerPressurePlate() {
      return false;
   }

   public void addEntityCrashInfo(CrashReportCategory var1) {
      var1.addCrashSectionCallable("Entity Type", new Callable<String>() {
         private static final long a = 100458081021378L;

         public String call() throws Exception {
            return EntityList.getEntityString(Entity.this) + " (" + Entity.this.getClass().getCanonicalName() + ")";
         }
      });
      var1.addCrashSection("Entity ID", this.entityId);
      var1.addCrashSectionCallable("Entity Name", new Callable<String>() {
         public String call() throws Exception {
            return Entity.this.getName();
         }
      });
      var1.addCrashSection("Entity's Exact location", String.format("%.2f, %.2f, %.2f", this.posX, this.posY, this.posZ));
      var1.addCrashSection(
         "Entity's Block location",
         CrashReportCategory.getCoordinateInfo(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ))
      );
      var1.addCrashSection("Entity's Momentum", String.format("%.2f, %.2f, %.2f", this.motionX, this.motionY, this.motionZ));
      var1.addCrashSectionCallable("Entity's Rider", new Callable<String>() {
         public String call() throws Exception {
            return Entity.this.riddenByEntity.toString();
         }
      });
      var1.addCrashSectionCallable("Entity's Vehicle", new Callable<String>() {
         public String call() throws Exception {
            return Entity.this.ridingEntity.toString();
         }
      });
   }

   public boolean canRenderOnFire() {
      return this.isBurning();
   }

   public UUID getUniqueID() {
      return this.entityUniqueID;
   }

   public boolean isPushedByWater() {
      return true;
   }

   @Override
   public IChatComponent getDisplayName() {
      ChatComponentText var1 = new ChatComponentText(this.getName());
      var1.getChatStyle().setChatHoverEvent(this.getHoverEvent());
      var1.getChatStyle().setInsertion(this.getUniqueID().toString());
      return var1;
   }

   public void setCustomNameTag(String var1) {
      this.dataWatcher.updateObject(2, var1);
   }

   public String getCustomNameTag() {
      return this.dataWatcher.getWatchableObjectString(2);
   }

   public boolean hasCustomName() {
      return this.dataWatcher.getWatchableObjectString(2).length() > 0;
   }

   public void setAlwaysRenderNameTag(boolean var1) {
      this.dataWatcher.updateObject(3, (byte)(var1 ? 1 : 0));
   }

   public boolean getAlwaysRenderNameTag() {
      return this.dataWatcher.getWatchableObjectByte(3) == 1;
   }

   public void setPositionAndUpdate(double var1, double var3, double var5) {
      this.setLocationAndAngles(var1, var3, var5, this.rotationYaw, this.rotationPitch);
   }

   public boolean getAlwaysRenderNameTagForRender() {
      return this.getAlwaysRenderNameTag();
   }

   public void onDataWatcherUpdate(int var1) {
   }

   public EnumFacing getHorizontalFacing() {
      return EnumFacing.getHorizontal(MathHelper.floor_double(this.rotationYaw * 4.0F / 360.0F + 0.5) & 3);
   }

   public HoverEvent getHoverEvent() {
      NBTTagCompound var4 = new NBTTagCompound();
      String var5 = EntityList.getEntityString(this);
      var4.setString("id", this.getUniqueID().toString());
      if (var5 != null) {
         var4.setString("type", var5);
      }

      var4.setString("name", this.getName());
      return new HoverEvent(HoverEvent.Action.SHOW_ENTITY, new ChatComponentText(var4.toString()));
   }

   public boolean isSpectatedByPlayer(EntityPlayerMP var1) {
      return true;
   }

   public AxisAlignedBB getEntityBoundingBox() {
      return this.boundingBox;
   }

   public void setEntityBoundingBox(AxisAlignedBB var1) {
      this.boundingBox = var1;
   }

   public float getEyeHeight() {
      return this.height * 0.85F;
   }

   public boolean isOutsideBorder() {
      return this.isOutsideBorder;
   }

   public void setOutsideBorder(boolean var1) {
      this.isOutsideBorder = var1;
   }

   public boolean replaceItemInInventory(int var1, ItemStack var2) {
      return false;
   }

   @Override
   public void addChatMessage(IChatComponent var1) {
   }

   @Override
   public boolean canCommandSenderUseCommand(int var1, String var2) {
      return true;
   }

   @Override
   public BlockPos getPosition() {
      return new BlockPos(this.posX, this.posY + 0.5, this.posZ);
   }

   @Override
   public Vec3 getPositionVector() {
      return new Vec3(this.posX, this.posY, this.posZ);
   }

   @Override
   public World getEntityWorld() {
      return this.worldObj;
   }

   @Override
   public Entity getCommandSenderEntity() {
      return this;
   }

   @Override
   public boolean sendCommandFeedback() {
      return false;
   }

   @Override
   public void setCommandStat(CommandResultStats.Type var1, int var2) {
      this.cmdResultStats.setCommandStatScore(this, var1, var2);
   }

   public CommandResultStats getCommandStats() {
      return this.cmdResultStats;
   }

   public void T_void(Entity var1) {
      this.cmdResultStats.addAllStats(var1.getCommandStats());
   }

   public NBTTagCompound getNBTTagCompound() {
      return null;
   }

   public void clientUpdateEntityNBT(NBTTagCompound var1) {
   }

   public boolean interactAt(EntityPlayer var1, Vec3 var2) {
      return false;
   }

   public boolean isImmuneToExplosions() {
      return false;
   }

   public void applyEnchantments(EntityLivingBase var1, Entity var2) {
      if (var2 instanceof EntityLivingBase) {
         EnchantmentHelper.applyThornEnchantments((EntityLivingBase)var2, var1);
      }

      EnchantmentHelper.applyArthropodEnchantments(var1, var2);
   }
}
