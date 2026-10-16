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

   public int getEntityId() {
      return this.entityId;
   }

   public void setEntityId(int id) {
      this.entityId = id;
   }

   public void onKillCommand() {
      this.setDead();
   }

   public Entity(World worldIn) {
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
      this.worldObj = worldIn;
      this.setPosition(0.0, 0.0, 0.0);
      if (worldIn != null) {
         this.dimension = worldIn.provider.getDimensionId();
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
   public boolean equals(Object object) {
      return object instanceof Entity ? ((Entity)object).entityId == this.entityId : false;
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

   public void setSize(float width, float height) {
      if (width != this.width || height != this.height) {
         float f = this.width;
         this.width = width;
         this.height = height;
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
         if (this.width > f && !this.firstUpdate && !this.worldObj.isRemote) {
            this.moveEntity(f - this.width, 0.0, f - this.width);
         }
      }
   }

   public void setRotation(float yaw, float pitch) {
      this.rotationYaw = yaw % 360.0F;
      this.rotationPitch = pitch % 360.0F;
   }

   public void setPosition(double x, double y, double z) {
      this.posX = x;
      this.posY = y;
      this.posZ = z;
      float f = this.width / 2.0F;
      float g = this.height;
      this.setEntityBoundingBox(new AxisAlignedBB(x - f, y, z - f, x + f, y + g, z + f));
   }

   public void setAngles(float yaw, float pitch) {
      float f = this.rotationPitch;
      float g = this.rotationYaw;
      this.rotationYaw = (float)(this.rotationYaw + yaw * 0.15);
      this.rotationPitch = (float)(this.rotationPitch - pitch * 0.15);
      this.rotationPitch = MathHelper.clamp_float(this.rotationPitch, -90.0F, 90.0F);
      this.prevRotationPitch = this.prevRotationPitch + (this.rotationPitch - f);
      this.prevRotationYaw = this.prevRotationYaw + (this.rotationYaw - g);
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

   public void setFire(int seconds) {
      int i = seconds * 20;
      i = EnchantmentProtection.getFireTimeForEntity(this, i);
      if (this.fire < i) {
         this.fire = i;
      }
   }

   public void extinguish() {
      this.fire = 0;
   }

   public void kill() {
      this.setDead();
   }

   public boolean isOffsetPositionInLiquid(double x, double y, double z) {
      AxisAlignedBB axisAlignedBB = this.getEntityBoundingBox().offset(x, y, z);
      return this.isLiquidPresentInAABB(axisAlignedBB);
   }

   private boolean isLiquidPresentInAABB(AxisAlignedBB bb) {
      return this.worldObj.getCollidingBoundingBoxes(this, bb).isEmpty() && !this.worldObj.isAnyLiquid(bb);
   }

   public void moveEntity(double x, double y, double z) {
      if (this.noClip) {
         this.setEntityBoundingBox(this.getEntityBoundingBox().offset(x, y, z));
         this.resetPositionToBB();
      } else {
         this.worldObj.theProfiler.startSection("move");
         double d = this.posX;
         double e = this.posY;
         double d2 = this.posZ;
         if (this.isInWeb) {
            this.isInWeb = false;
            x *= 0.25;
            y *= 0.05F;
            z *= 0.25;
            this.motionX = 0.0;
            this.motionY = 0.0;
            this.motionZ = 0.0;
         }

         double d3 = x;
         double d4 = y;
         double d5 = z;
         boolean bl = this.onGround && this.isSneaking() && this instanceof EntityPlayer;
         SafeWalkEvent safeWalkEvent = new SafeWalkEvent();
         Astolfo.INSTANCE.getEventBus().post(safeWalkEvent);
         if (bl || this instanceof EntityPlayer && safeWalkEvent.isCancelled() && (this.onGround || !safeWalkEvent.isSafe())) {
            double d6 = 0.05;

            while (x != 0.0 && this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox().offset(x, -1.0, 0.0)).isEmpty()) {
               if (x < d6 && x >= -d6) {
                  x = 0.0;
               } else if (x > 0.0) {
                  x -= d6;
               } else {
                  x += d6;
               }

               d3 = x;
            }

            for (; z != 0.0 && this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox().offset(0.0, -1.0, z)).isEmpty(); d5 = z) {
               if (z < d6 && z >= -d6) {
                  z = 0.0;
               } else if (z > 0.0) {
                  z -= d6;
               } else {
                  z += d6;
               }
            }

            for (; x != 0.0 && z != 0.0 && this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox().offset(x, -1.0, z)).isEmpty(); d5 = z) {
               if (x < d6 && x >= -d6) {
                  x = 0.0;
               } else if (x > 0.0) {
                  x -= d6;
               } else {
                  x += d6;
               }

               d3 = x;
               if (z < d6 && z >= -d6) {
                  z = 0.0;
               } else if (z > 0.0) {
                  z -= d6;
               } else {
                  z += d6;
               }
            }
         }

         List list = this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox().addCoord(x, y, z));
         AxisAlignedBB axisAlignedBB = this.getEntityBoundingBox();

         for (Object object : list) {
            AxisAlignedBB axisAlignedBB2 = (AxisAlignedBB)object;
            y = axisAlignedBB2.calculateYOffset(this.getEntityBoundingBox(), y);
         }

         this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0, y, 0.0));
         boolean bl2 = this.onGround || d4 != y && d4 < 0.0;

         for (Object object2 : list) {
            AxisAlignedBB axisAlignedBB3 = (AxisAlignedBB)object2;
            x = axisAlignedBB3.A(this.getEntityBoundingBox(), x);
         }

         this.setEntityBoundingBox(this.getEntityBoundingBox().offset(x, 0.0, 0.0));

         for (Object object3 : list) {
            AxisAlignedBB axisAlignedBB4 = (AxisAlignedBB)object3;
            z = axisAlignedBB4.v(this.getEntityBoundingBox(), z);
         }

         this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0, 0.0, z));
         if (this.stepHeight > 0.0F && bl2 && (d3 != x || d5 != z)) {
            if (this instanceof EntityPlayerSP) {
               StepEvent stepEvent = new StepEvent(StepState.PRE, this, x(this, d3, d5, x, y, z, axisAlignedBB));
               Astolfo.INSTANCE.getEventBus().post(stepEvent);
               if (stepEvent.isCancelled()) {
                  return;
               }
            }

            double d7 = x;
            double d8 = y;
            double d9 = z;
            AxisAlignedBB axisAlignedBB5 = this.getEntityBoundingBox();
            this.setEntityBoundingBox(axisAlignedBB);
            y = this.stepHeight;
            List list2 = this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox().addCoord(d3, y, d5));
            AxisAlignedBB axisAlignedBB6 = this.getEntityBoundingBox();
            AxisAlignedBB axisAlignedBB7 = axisAlignedBB6.addCoord(d3, 0.0, d5);
            double d10 = y;

            for (Object object4 : list2) {
               AxisAlignedBB axisAlignedBB8 = (AxisAlignedBB)object4;
               d10 = axisAlignedBB8.calculateYOffset(axisAlignedBB7, d10);
            }

            axisAlignedBB6 = axisAlignedBB6.offset(0.0, d10, 0.0);
            double d11 = d3;

            for (Object object5 : list2) {
               AxisAlignedBB axisAlignedBB9 = (AxisAlignedBB)object5;
               d11 = axisAlignedBB9.A(axisAlignedBB6, d11);
            }

            axisAlignedBB6 = axisAlignedBB6.offset(d11, 0.0, 0.0);
            double d12 = d5;

            for (Object object6 : list2) {
               AxisAlignedBB axisAlignedBB10 = (AxisAlignedBB)object6;
               d12 = axisAlignedBB10.v(axisAlignedBB6, d12);
            }

            axisAlignedBB6 = axisAlignedBB6.offset(0.0, 0.0, d12);
            AxisAlignedBB axisAlignedBB11 = this.getEntityBoundingBox();
            double d13 = y;

            for (Object object7 : list2) {
               AxisAlignedBB axisAlignedBB12 = (AxisAlignedBB)object7;
               d13 = axisAlignedBB12.calculateYOffset(axisAlignedBB11, d13);
            }

            axisAlignedBB11 = axisAlignedBB11.offset(0.0, d13, 0.0);
            double d14 = d3;

            for (Object object8 : list2) {
               AxisAlignedBB axisAlignedBB13 = (AxisAlignedBB)object8;
               d14 = axisAlignedBB13.A(axisAlignedBB11, d14);
            }

            axisAlignedBB11 = axisAlignedBB11.offset(d14, 0.0, 0.0);
            double d15 = d5;

            for (Object object9 : list2) {
               AxisAlignedBB axisAlignedBB14 = (AxisAlignedBB)object9;
               d15 = axisAlignedBB14.v(axisAlignedBB11, d15);
            }

            axisAlignedBB11 = axisAlignedBB11.offset(0.0, 0.0, d15);
            double d16 = d11 * d11 + d12 * d12;
            double d17 = d14 * d14 + d15 * d15;
            if (d16 > d17) {
               x = d11;
               z = d12;
               y = -d10;
               this.setEntityBoundingBox(axisAlignedBB6);
            } else {
               x = d14;
               z = d15;
               y = -d13;
               this.setEntityBoundingBox(axisAlignedBB11);
            }

            for (Object object10 : list2) {
               AxisAlignedBB axisAlignedBB15 = (AxisAlignedBB)object10;
               y = axisAlignedBB15.calculateYOffset(this.getEntityBoundingBox(), y);
            }

            this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0, y, 0.0));
            if (d7 * d7 + d9 * d9 >= x * x + z * z) {
               x = d7;
               y = d8;
               z = d9;
               this.setEntityBoundingBox(axisAlignedBB5);
            }

            StepEvent stepEvent2 = new StepEvent(StepState.POST, this, null);
            Astolfo.INSTANCE.getEventBus().post(stepEvent2);
         }

         this.worldObj.theProfiler.endSection();
         this.worldObj.theProfiler.startSection("rest");
         this.resetPositionToBB();
         this.isCollidedHorizontally = d3 != x || d5 != z;
         this.isCollidedVertically = d4 != y;
         this.onGround = this.isCollidedVertically && d4 < 0.0;
         this.isCollided = this.isCollidedHorizontally || this.isCollidedVertically;
         int i = MathHelper.floor_double(this.posX);
         int j = MathHelper.floor_double(this.posY - 0.2F);
         int k = MathHelper.floor_double(this.posZ);
         BlockPos blockPos = new BlockPos(i, j, k);
         Block block2 = this.worldObj.getBlockState(blockPos).getBlock();
         if (block2.getMaterial() == Material.air) {
            Block block3 = this.worldObj.getBlockState(blockPos.down()).getBlock();
            if (block3 instanceof BlockFence || block3 instanceof BlockWall || block3 instanceof BlockFenceGate) {
               block2 = block3;
               blockPos = blockPos.down();
            }
         }

         this.updateFallState(y, this.onGround, block2, blockPos);
         if (d3 != x) {
            this.motionX = 0.0;
         }

         if (d5 != z) {
            this.motionZ = 0.0;
         }

         if (d4 != y) {
            block2.onLanded(this.worldObj, this);
         }

         if (this.canTriggerWalking() && !bl && this.ridingEntity == null) {
            double d18 = this.posX - d;
            double d19 = this.posY - e;
            double d20 = this.posZ - d2;
            if (block2 != Blocks.ladder) {
               d19 = 0.0;
            }

            if (block2 != null && this.onGround) {
               block2.onEntityCollidedWithBlock(this.worldObj, blockPos, this);
            }

            this.distanceWalkedModified = (float)(this.distanceWalkedModified + MathHelper.sqrt_double(d18 * d18 + d20 * d20) * 0.6);
            this.distanceWalkedOnStepModified = (float)(
               this.distanceWalkedOnStepModified + MathHelper.sqrt_double(d18 * d18 + d19 * d19 + d20 * d20) * 0.6
            );
            if (this.distanceWalkedOnStepModified > this.nextStepDistance && block2.getMaterial() != Material.air) {
               this.nextStepDistance = (int)this.distanceWalkedOnStepModified + 1;
               if (this.isInWater()) {
                  float f = MathHelper.sqrt_double(this.motionX * this.motionX * 0.2F + this.motionY * this.motionY + this.motionZ * this.motionZ * 0.2F)
                     * 0.35F;
                  if (f > 1.0F) {
                     f = 1.0F;
                  }

                  this.playSound(this.getSwimSound(), f, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
               }

               this.playStepSound(blockPos, block2);
            }
         }

         try {
            this.doBlockCollisions();
         } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Checking entity block collision");
            CrashReportCategory crashReportCategory = crashReport.makeCategory("Entity being checked for collision");
            this.addEntityCrashInfo(crashReportCategory);
            throw new ReportedException(crashReport);
         }

         boolean bl3 = this.G();
         if (this.worldObj.a(this.getEntityBoundingBox().contract(0.001, 0.001, 0.001))) {
            this.dealFireDamage(1);
            if (!bl3) {
               this.fire++;
               if (this.fire == 0) {
                  this.setFire(8);
               }
            }
         } else if (this.fire <= 0) {
            this.fire = -this.fireResistance;
         }

         if (bl3 && this.fire > 0) {
            this.playSound("random.fizz", 0.7F, 1.6F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
            this.fire = -this.fireResistance;
         }

         this.worldObj.theProfiler.endSection();
      }
   }

   public static Vec3 x(Entity entity, double d, double d2, double d3, double d4, double d5, AxisAlignedBB axisAlignedBB) {
      AxisAlignedBB axisAlignedBB2 = entity.getEntityBoundingBox();
      double e = d3;
      double d6 = d5;
      AxisAlignedBB axisAlignedBB3 = axisAlignedBB2;
      axisAlignedBB2 = axisAlignedBB;
      d4 = 2.0;
      List list = entity.worldObj.getCollidingBoundingBoxes(entity, axisAlignedBB2.addCoord(d, d4, d2));
      AxisAlignedBB axisAlignedBB4 = axisAlignedBB2;
      AxisAlignedBB axisAlignedBB5 = axisAlignedBB4.addCoord(d, 0.0, d2);
      double d7 = d4;
      int i = 0;

      for (int j = list.size(); i < j; i++) {
         d7 = ((AxisAlignedBB)list.get(i)).calculateYOffset(axisAlignedBB5, d7);
      }

      axisAlignedBB4 = axisAlignedBB4.offset(0.0, d7, 0.0);
      double d8 = d;
      int k = 0;

      for (int l = list.size(); k < l; k++) {
         d8 = ((AxisAlignedBB)list.get(k)).A(axisAlignedBB4, d8);
      }

      axisAlignedBB4 = axisAlignedBB4.offset(d8, 0.0, 0.0);
      double d9 = d2;
      int m = 0;

      for (int n = list.size(); m < n; m++) {
         d9 = ((AxisAlignedBB)list.get(m)).v(axisAlignedBB4, d9);
      }

      axisAlignedBB4 = axisAlignedBB4.offset(0.0, 0.0, d9);
      AxisAlignedBB axisAlignedBB6 = axisAlignedBB2;
      double d10 = d4;
      int i2 = 0;

      for (int i3 = list.size(); i2 < i3; i2++) {
         d10 = ((AxisAlignedBB)list.get(i2)).calculateYOffset(axisAlignedBB6, d10);
      }

      axisAlignedBB6 = axisAlignedBB6.offset(0.0, d10, 0.0);
      double d11 = d;
      int i4 = 0;

      for (int i5 = list.size(); i4 < i5; i4++) {
         d11 = ((AxisAlignedBB)list.get(i4)).A(axisAlignedBB6, d11);
      }

      axisAlignedBB6 = axisAlignedBB6.offset(d11, 0.0, 0.0);
      double d12 = d2;
      int i6 = 0;

      for (int i7 = list.size(); i6 < i7; i6++) {
         d12 = ((AxisAlignedBB)list.get(i6)).v(axisAlignedBB6, d12);
      }

      axisAlignedBB6 = axisAlignedBB6.offset(0.0, 0.0, d12);
      double d13 = d8 * d8 + d9 * d9;
      double d14 = d11 * d11 + d12 * d12;
      if (d13 > d14) {
         d3 = d8;
         d5 = d9;
         d4 = -d7;
         axisAlignedBB2 = axisAlignedBB4;
      } else {
         d3 = d11;
         d5 = d12;
         d4 = -d10;
         axisAlignedBB2 = axisAlignedBB6;
      }

      int i8 = 0;

      for (int i9 = list.size(); i8 < i9; i8++) {
         d4 = ((AxisAlignedBB)list.get(i8)).calculateYOffset(axisAlignedBB2, d4);
      }

      axisAlignedBB2 = axisAlignedBB2.offset(0.0, d4, 0.0);
      if (e * e + d6 * d6 >= d3 * d3 + d5 * d5) {
         axisAlignedBB2 = axisAlignedBB3;
      }

      return new Vec3(
         axisAlignedBB2.minX - entity.getEntityBoundingBox().minX, axisAlignedBB2.minY - entity.getEntityBoundingBox().minY, axisAlignedBB2.minZ - entity.getEntityBoundingBox().minZ
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
      BlockPos blockPos = new BlockPos(this.getEntityBoundingBox().minX + 0.001, this.getEntityBoundingBox().minY + 0.001, this.getEntityBoundingBox().minZ + 0.001);
      BlockPos blockPos2 = new BlockPos(this.getEntityBoundingBox().maxX - 0.001, this.getEntityBoundingBox().maxY - 0.001, this.getEntityBoundingBox().maxZ - 0.001);
      if (this.worldObj.isAreaLoaded(blockPos, blockPos2)) {
         for (int i = blockPos.getX(); i <= blockPos2.getX(); i++) {
            for (int j = blockPos.getY(); j <= blockPos2.getY(); j++) {
               for (int k = blockPos.getZ(); k <= blockPos2.getZ(); k++) {
                  BlockPos blockPos3 = new BlockPos(i, j, k);
                  IBlockState iBlockState = this.worldObj.getBlockState(blockPos3);

                  try {
                     iBlockState.getBlock().onEntityCollidedWithBlock(this.worldObj, blockPos3, iBlockState, this);
                  } catch (Throwable throwable) {
                     CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Colliding entity with block");
                     CrashReportCategory crashReportCategory = crashReport.makeCategory("Block being collided with");
                     CrashReportCategory.addBlockInfo(crashReportCategory, blockPos3, iBlockState);
                     throw new ReportedException(crashReport);
                  }
               }
            }
         }
      }
   }

   public void playStepSound(BlockPos pos, Block blockIn) {
      Block.SoundType soundType = blockIn.stepSound;
      if (this.worldObj.getBlockState(pos.up()).getBlock() == Blocks.snow_layer) {
         soundType = Blocks.snow_layer.stepSound;
         this.playSound(soundType.getStepSound(), soundType.getVolume() * 0.15F, soundType.getFrequency());
      } else if (!blockIn.getMaterial().isLiquid()) {
         this.playSound(soundType.getStepSound(), soundType.getVolume() * 0.15F, soundType.getFrequency());
      }
   }

   public void playSound(String name, float volume, float pitch) {
      if (!this.isSilent()) {
         this.worldObj.playSoundAtEntity(this, name, volume, pitch);
      }
   }

   public boolean isSilent() {
      return this.dataWatcher.getWatchableObjectByte(4) == 1;
   }

   public void setSilent(boolean isSilent) {
      this.dataWatcher.updateObject(4, (byte)(isSilent ? 1 : 0));
   }

   public boolean canTriggerWalking() {
      return true;
   }

   public void updateFallState(double y, boolean onGroundIn, Block blockIn, BlockPos pos) {
      if (onGroundIn) {
         if (this.fallDistance > 0.0F) {
            if (blockIn != null) {
               blockIn.onFallenUpon(this.worldObj, pos, this, this.fallDistance);
            } else {
               this.fall(this.fallDistance, 1.0F);
            }

            this.fallDistance = 0.0F;
         }
      } else if (y < 0.0) {
         this.fallDistance = (float)(this.fallDistance - y);
      }
   }

   public AxisAlignedBB getCollisionBoundingBox() {
      return null;
   }

   public void dealFireDamage(int amount) {
      if (!this.isImmuneToFire) {
         this.attackEntityFrom(DamageSource.inFire, amount);
      }
   }

   public final boolean isImmuneToFire() {
      return this.isImmuneToFire;
   }

   public void fall(float distance, float damageMultiplier) {
      if (this.riddenByEntity != null) {
         this.riddenByEntity.fall(distance, damageMultiplier);
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
      float f = MathHelper.sqrt_double(this.motionX * this.motionX * 0.2F + this.motionY * this.motionY + this.motionZ * this.motionZ * 0.2F) * 0.2F;
      if (f > 1.0F) {
         f = 1.0F;
      }

      this.playSound(this.getSplashSound(), f, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
      float g = MathHelper.floor_double(this.getEntityBoundingBox().minY);

      for (int i = 0; i < 1.0F + this.width * 20.0F; i++) {
         float h = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
         float f2 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
         this.worldObj
            .spawnParticle(
               EnumParticleTypes.WATER_BUBBLE,
               this.posX + h,
               g + 1.0F,
               this.posZ + f2,
               this.motionX,
               this.motionY - this.rand.nextFloat() * 0.2F,
               this.motionZ
            );
      }

      for (int j = 0; j < 1.0F + this.width * 20.0F; j++) {
         float f3 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
         float f4 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
         this.worldObj
            .spawnParticle(EnumParticleTypes.WATER_SPLASH, this.posX + f3, g + 1.0F, this.posZ + f4, this.motionX, this.motionY, this.motionZ);
      }
   }

   public void spawnRunningParticles() {
      if (this.isSprinting() && !this.isInWater()) {
         this.createRunningParticles();
      }
   }

   public void createRunningParticles() {
      int i = MathHelper.floor_double(this.posX);
      int j = MathHelper.floor_double(this.posY - 0.2F);
      int k = MathHelper.floor_double(this.posZ);
      BlockPos blockPos = new BlockPos(i, j, k);
      IBlockState iBlockState = this.worldObj.getBlockState(blockPos);
      Block block = iBlockState.getBlock();
      if (block.getRenderType() != -1) {
         this.worldObj
            .spawnParticle(
               EnumParticleTypes.BLOCK_CRACK,
               this.posX + (this.rand.nextFloat() - 0.5) * this.width,
               this.getEntityBoundingBox().minY + 0.1,
               this.posZ + (this.rand.nextFloat() - 0.5) * this.width,
               -this.motionX * 4.0,
               1.5,
               -this.motionZ * 4.0,
               Block.getStateId(iBlockState)
            );
      }
   }

   public String getSplashSound() {
      return "game.neutral.swim.splash";
   }

   public boolean isInsideOfMaterial(Material materialIn) {
      double d = this.posY + this.getEyeHeight();
      BlockPos blockPos = new BlockPos(this.posX, d, this.posZ);
      IBlockState iBlockState = this.worldObj.getBlockState(blockPos);
      Block block = iBlockState.getBlock();
      if (block.getMaterial() == materialIn) {
         float f = BlockLiquid.getLiquidHeightPercent(iBlockState.getBlock().getMetaFromState(iBlockState)) - 0.11111111F;
         float g = blockPos.getY() + 1 - f;
         boolean bl = d < g;
         return !bl && this instanceof EntityPlayer ? false : bl;
      } else {
         return false;
      }
   }

   public boolean isInLava() {
      return this.worldObj.isMaterialInBB(this.getEntityBoundingBox().expand(-0.1F, -0.4F, -0.1F), Material.lava);
   }

   public void moveFlying(float strafe, float forward, float friction) {
      if (this.equals(Minecraft.getMinecraft().thePlayer)) {
         StrafeEvent strafeEvent = new StrafeEvent(strafe, forward, friction);
         Astolfo.INSTANCE.getEventBus().post(strafeEvent);
         if (strafeEvent.isCancelled()) {
            return;
         }
      }

      float f = strafe * strafe + forward * forward;
      if (f >= 1.0E-4F) {
         f = MathHelper.sqrt_float(f);
         if (f < 1.0F) {
            f = 1.0F;
         }

         f = friction / f;
         strafe *= f;
         forward *= f;
         float g = MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F);
         float h = MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F);
         this.motionX += strafe * h - forward * g;
         this.motionZ += forward * h + strafe * g;
      }
   }

   public int getBrightnessForRender(float partialTicks) {
      BlockPos blockPos = new BlockPos(this.posX, this.posY + this.getEyeHeight(), this.posZ);
      return this.worldObj.isBlockLoaded(blockPos) ? this.worldObj.getCombinedLight(blockPos, 0) : 0;
   }

   public float getBrightness(float partialTicks) {
      BlockPos blockPos = new BlockPos(this.posX, this.posY + this.getEyeHeight(), this.posZ);
      return this.worldObj.isBlockLoaded(blockPos) ? this.worldObj.getLightBrightness(blockPos) : 0.0F;
   }

   public void setWorld(World worldIn) {
      this.worldObj = worldIn;
   }

   public void setPositionAndRotation(double x, double y, double z, float yaw, float pitch) {
      this.prevPosX = this.posX = x;
      this.prevPosY = this.posY = y;
      this.prevPosZ = this.posZ = z;
      this.prevRotationYaw = this.rotationYaw = yaw;
      this.prevRotationPitch = this.rotationPitch = pitch;
      double d = this.prevRotationYaw - yaw;
      if (d < -180.0) {
         this.prevRotationYaw += 360.0F;
      }

      if (d >= 180.0) {
         this.prevRotationYaw -= 360.0F;
      }

      this.setPosition(this.posX, this.posY, this.posZ);
      this.setRotation(yaw, pitch);
   }

   public void moveToBlockPosAndAngles(BlockPos pos, float rotationYawIn, float rotationPitchIn) {
      this.setLocationAndAngles(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, rotationYawIn, rotationPitchIn);
   }

   public void setLocationAndAngles(double x, double y, double z, float yaw, float pitch) {
      this.lastTickPosX = this.prevPosX = this.posX = x;
      this.lastTickPosY = this.prevPosY = this.posY = y;
      this.lastTickPosZ = this.prevPosZ = this.posZ = z;
      this.rotationYaw = yaw;
      this.rotationPitch = pitch;
      this.setPosition(this.posX, this.posY, this.posZ);
   }

   public float getDistanceToEntity(Entity entityIn) {
      float f = (float)(this.posX - entityIn.posX);
      float g = (float)(this.posY - entityIn.posY);
      float h = (float)(this.posZ - entityIn.posZ);
      return MathHelper.sqrt_float(f * f + g * g + h * h);
   }

   public double getDistanceSq(double x, double y, double z) {
      double d = this.posX - x;
      double e = this.posY - y;
      double d2 = this.posZ - z;
      return d * d + e * e + d2 * d2;
   }

   public double getDistanceSq(BlockPos pos) {
      return pos.distanceSq(this.posX, this.posY, this.posZ);
   }

   public double getDistanceSqToCenter(BlockPos pos) {
      return pos.distanceSqToCenter(this.posX, this.posY, this.posZ);
   }

   public double getDistance(double x, double y, double z) {
      double d = this.posX - x;
      double e = this.posY - y;
      double d2 = this.posZ - z;
      return MathHelper.sqrt_double(d * d + e * e + d2 * d2);
   }

   public double getDistanceSqToEntity(Entity entityIn) {
      double d = this.posX - entityIn.posX;
      double e = this.posY - entityIn.posY;
      double d2 = this.posZ - entityIn.posZ;
      return d * d + e * e + d2 * d2;
   }

   public void onCollideWithPlayer(EntityPlayer entityIn) {
   }

   public void applyEntityCollision(Entity entityIn) {
      if (entityIn.riddenByEntity != this && entityIn.ridingEntity != this && !entityIn.noClip && !this.noClip) {
         double d = entityIn.posX - this.posX;
         double e = entityIn.posZ - this.posZ;
         double d2 = MathHelper.abs_max(d, e);
         if (d2 >= 0.01F) {
            d2 = MathHelper.sqrt_double(d2);
            d /= d2;
            e /= d2;
            double d3 = 1.0 / d2;
            if (d3 > 1.0) {
               d3 = 1.0;
            }

            d *= d3;
            e *= d3;
            d *= 0.05F;
            e *= 0.05F;
            d *= 1.0F - this.entityCollisionReduction;
            e *= 1.0F - this.entityCollisionReduction;
            if (this.riddenByEntity == null) {
               this.addVelocity(-d, 0.0, -e);
            }

            if (entityIn.riddenByEntity == null) {
               entityIn.addVelocity(d, 0.0, e);
            }
         }
      }
   }

   public void addVelocity(double x, double y, double z) {
      this.motionX += x;
      this.motionY += y;
      this.motionZ += z;
      this.isAirBorne = true;
   }

   public void setBeenAttacked() {
      this.velocityChanged = true;
   }

   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isEntityInvulnerable(source)) {
         return false;
      }

      this.setBeenAttacked();
      return false;
   }

   public Vec3 getLook(float partialTicks) {
      if (partialTicks == 1.0F) {
         return this.getVectorForRotation(this.rotationPitch, this.rotationYaw);
      }

      float f = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * partialTicks;
      float g = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * partialTicks;
      return this.getVectorForRotation(f, g);
   }

   public final Vec3 getVectorForRotation(float pitch, float yaw) {
      float f = MathHelper.cos(-yaw * (float) (Math.PI / 180.0) - (float) Math.PI);
      float g = MathHelper.sin(-yaw * (float) (Math.PI / 180.0) - (float) Math.PI);
      float h = -MathHelper.cos(-pitch * (float) (Math.PI / 180.0));
      float f2 = MathHelper.sin(-pitch * (float) (Math.PI / 180.0));
      return new Vec3(g * h, f2, f * h);
   }

   public Vec3 getPositionEyes(float partialTicks) {
      if (partialTicks == 1.0F) {
         return new Vec3(this.posX, this.posY + this.getEyeHeight(), this.posZ);
      }

      double d = this.prevPosX + (this.posX - this.prevPosX) * partialTicks;
      double e = this.prevPosY + (this.posY - this.prevPosY) * partialTicks + this.getEyeHeight();
      double d2 = this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks;
      return new Vec3(d, e, d2);
   }

   public MovingObjectPosition rayTrace(double blockReachDistance, float partialTicks) {
      Vec3 vec3 = this.getPositionEyes(partialTicks);
      Vec3 vec32 = this.getLook(partialTicks);
      Vec3 vec33 = vec3.addVector(vec32.xCoord * blockReachDistance, vec32.yCoord * blockReachDistance, vec32.zCoord * blockReachDistance);
      return this.worldObj.rayTraceBlocks(vec3, vec33, false, false, true);
   }

   public boolean canBeCollidedWith() {
      return false;
   }

   public boolean canBePushed() {
      return false;
   }

   public void addToPlayerScore(Entity entityIn, int amount) {
   }

   public boolean isInRangeToRender3d(double x, double y, double z) {
      double d = this.posX - x;
      double e = this.posY - y;
      double d2 = this.posZ - z;
      double d3 = d * d + e * e + d2 * d2;
      if (ModuleManager.noRender.isEnabled()) {
         return !Minecraft.getMinecraft().thePlayer.C(this) && NoRender.visGraph.getValue() && !(this instanceof EntityPlayer)
            ? false
            : !NoRender.smart.getValue() || this.isInRangeToRenderDist(d3 * 10.0);
      } else {
         return this.isInRangeToRenderDist(d3);
      }
   }

   public boolean isInRangeToRenderDist(double distance) {
      double d = this.getEntityBoundingBox().getAverageEdgeLength();
      if (Double.isNaN(d)) {
         d = 1.0;
      }

      d = d * 64.0 * this.renderDistanceWeight;
      return distance < d * d;
   }

   public boolean writeMountToNBT(NBTTagCompound tagCompund) {
      String string = this.getEntityString();
      if (!this.isDead && string != null) {
         tagCompund.setString("id", string);
         this.writeToNBT(tagCompund);
         return true;
      } else {
         return false;
      }
   }

   public boolean writeToNBTOptional(NBTTagCompound tagCompund) {
      String string = this.getEntityString();
      if (!this.isDead && string != null && this.riddenByEntity == null) {
         tagCompund.setString("id", string);
         this.writeToNBT(tagCompund);
         return true;
      } else {
         return false;
      }
   }

   public void writeToNBT(NBTTagCompound tagCompund) {
      try {
         tagCompund.setTag("Pos", this.newDoubleNBTList(this.posX, this.posY, this.posZ));
         tagCompund.setTag("Motion", this.newDoubleNBTList(this.motionX, this.motionY, this.motionZ));
         tagCompund.setTag("Rotation", this.newFloatNBTList(this.rotationYaw, this.rotationPitch));
         tagCompund.setFloat("FallDistance", this.fallDistance);
         tagCompund.setShort("Fire", (short)this.fire);
         tagCompund.setShort("Air", (short)this.getAir());
         tagCompund.setBoolean("OnGround", this.onGround);
         tagCompund.setInteger("Dimension", this.dimension);
         tagCompund.setBoolean("Invulnerable", this.invulnerable);
         tagCompund.setInteger("PortalCooldown", this.timeUntilPortal);
         tagCompund.setLong("UUIDMost", this.getUniqueID().getMostSignificantBits());
         tagCompund.setLong("UUIDLeast", this.getUniqueID().getLeastSignificantBits());
         if (this.getCustomNameTag() != null && this.getCustomNameTag().length() > 0) {
            tagCompund.setString("CustomName", this.getCustomNameTag());
            tagCompund.setBoolean("CustomNameVisible", this.getAlwaysRenderNameTag());
         }

         this.cmdResultStats.writeStatsToNBT(tagCompund);
         if (this.isSilent()) {
            tagCompund.setBoolean("Silent", this.isSilent());
         }

         this.writeEntityToNBT(tagCompund);
         if (this.ridingEntity != null) {
            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            if (this.ridingEntity.writeMountToNBT(nbtTagCompound)) {
               tagCompund.setTag("Riding", nbtTagCompound);
            }
         }
      } catch (Throwable throwable) {
         CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Saving entity NBT");
         CrashReportCategory crashReportCategory = crashReport.makeCategory("Entity being saved");
         this.addEntityCrashInfo(crashReportCategory);
         throw new ReportedException(crashReport);
      }
   }

   public void readFromNBT(NBTTagCompound tagCompund) {
      try {
         NBTTagList nbtTagList = tagCompund.getTagList("Pos", 6);
         NBTTagList nbtTagList2 = tagCompund.getTagList("Motion", 6);
         NBTTagList nbtTagList3 = tagCompund.getTagList("Rotation", 5);
         this.motionX = nbtTagList2.getDoubleAt(0);
         this.motionY = nbtTagList2.getDoubleAt(1);
         this.motionZ = nbtTagList2.getDoubleAt(2);
         if (Math.abs(this.motionX) > 10.0) {
            this.motionX = 0.0;
         }

         if (Math.abs(this.motionY) > 10.0) {
            this.motionY = 0.0;
         }

         if (Math.abs(this.motionZ) > 10.0) {
            this.motionZ = 0.0;
         }

         this.prevPosX = this.lastTickPosX = this.posX = nbtTagList.getDoubleAt(0);
         this.prevPosY = this.lastTickPosY = this.posY = nbtTagList.getDoubleAt(1);
         this.prevPosZ = this.lastTickPosZ = this.posZ = nbtTagList.getDoubleAt(2);
         this.prevRotationYaw = this.rotationYaw = nbtTagList3.getFloatAt(0);
         this.prevRotationPitch = this.rotationPitch = nbtTagList3.getFloatAt(1);
         this.setRotationYawHead(this.rotationYaw);
         this.setRenderYawOffset(this.rotationYaw);
         this.fallDistance = tagCompund.getFloat("FallDistance");
         this.fire = tagCompund.getShort("Fire");
         this.setAir(tagCompund.getShort("Air"));
         this.onGround = tagCompund.getBoolean("OnGround");
         this.dimension = tagCompund.getInteger("Dimension");
         this.invulnerable = tagCompund.getBoolean("Invulnerable");
         this.timeUntilPortal = tagCompund.getInteger("PortalCooldown");
         if (tagCompund.hasKey("UUIDMost", 4) && tagCompund.hasKey("UUIDLeast", 4)) {
            this.entityUniqueID = new UUID(tagCompund.getLong("UUIDMost"), tagCompund.getLong("UUIDLeast"));
         } else if (tagCompund.hasKey("UUID", 8)) {
            this.entityUniqueID = UUID.fromString(tagCompund.getString("UUID"));
         }

         this.setPosition(this.posX, this.posY, this.posZ);
         this.setRotation(this.rotationYaw, this.rotationPitch);
         if (tagCompund.hasKey("CustomName", 8) && tagCompund.getString("CustomName").length() > 0) {
            this.setCustomNameTag(tagCompund.getString("CustomName"));
         }

         this.setAlwaysRenderNameTag(tagCompund.getBoolean("CustomNameVisible"));
         this.cmdResultStats.readStatsFromNBT(tagCompund);
         this.setSilent(tagCompund.getBoolean("Silent"));
         this.readEntityFromNBT(tagCompund);
         if (this.shouldSetPosAfterLoading()) {
            this.setPosition(this.posX, this.posY, this.posZ);
         }
      } catch (Throwable throwable) {
         CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Loading entity NBT");
         CrashReportCategory crashReportCategory = crashReport.makeCategory("Entity being loaded");
         this.addEntityCrashInfo(crashReportCategory);
         throw new ReportedException(crashReport);
      }
   }

   public boolean shouldSetPosAfterLoading() {
      return true;
   }

   public final String getEntityString() {
      return EntityList.getEntityString(this);
   }

   public abstract void readEntityFromNBT(NBTTagCompound tagCompund);

   public abstract void writeEntityToNBT(NBTTagCompound tagCompound);

   public void onChunkLoad() {
   }

   public NBTTagList newDoubleNBTList(double... numbers) {
      NBTTagList nbtTagList = new NBTTagList();

      for (double d : numbers) {
         nbtTagList.appendTag(new NBTTagDouble(d));
      }

      return nbtTagList;
   }

   public NBTTagList newFloatNBTList(float... numbers) {
      NBTTagList nbtTagList = new NBTTagList();

      for (float f : numbers) {
         nbtTagList.appendTag(new NBTTagFloat(f));
      }

      return nbtTagList;
   }

   public EntityItem dropItem(Item itemIn, int size) {
      return this.dropItemWithOffset(itemIn, size, 0.0F);
   }

   public EntityItem dropItemWithOffset(Item itemIn, int size, float offsetY) {
      return this.entityDropItem(new ItemStack(itemIn, size, 0), offsetY);
   }

   public EntityItem entityDropItem(ItemStack itemStackIn, float offsetY) {
      if (itemStackIn.stackSize != 0 && itemStackIn.getItem() != null) {
         EntityItem entityItem = new EntityItem(this.worldObj, this.posX, this.posY + offsetY, this.posZ, itemStackIn);
         entityItem.setDefaultPickupDelay();
         this.worldObj.spawnEntityInWorld(entityItem);
         return entityItem;
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

      BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);

      for (int i = 0; i < 8; i++) {
         int j = MathHelper.floor_double(this.posY + ((i >> 0) % 2 - 0.5F) * 0.1F + this.getEyeHeight());
         int k = MathHelper.floor_double(this.posX + ((i >> 1) % 2 - 0.5F) * this.width * 0.8F);
         int l = MathHelper.floor_double(this.posZ + ((i >> 2) % 2 - 0.5F) * this.width * 0.8F);
         if (mutableBlockPos.getX() != k || mutableBlockPos.getY() != j || mutableBlockPos.getZ() != l) {
            mutableBlockPos.set(k, j, l);
            if (this.worldObj.getBlockState(mutableBlockPos).getBlock().isVisuallyOpaque()) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean interactFirst(EntityPlayer playerIn) {
      return false;
   }

   public AxisAlignedBB getCollisionBox(Entity entityIn) {
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

            double d = this.entityRiderYawDelta * 0.5;
            double e = this.entityRiderPitchDelta * 0.5;
            float f = 10.0F;
            if (d > f) {
               d = f;
            }

            if (d < -f) {
               d = -f;
            }

            if (e > f) {
               e = f;
            }

            if (e < -f) {
               e = -f;
            }

            this.entityRiderYawDelta -= d;
            this.entityRiderPitchDelta -= e;
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

   public void mountEntity(Entity entityIn) {
      this.entityRiderPitchDelta = 0.0;
      this.entityRiderYawDelta = 0.0;
      if (entityIn == null) {
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

         if (entityIn != null) {
            for (Entity entity = entityIn.ridingEntity; entity != null; entity = entity.ridingEntity) {
               if (entity == this) {
                  return;
               }
            }
         }

         this.ridingEntity = entityIn;
         entityIn.riddenByEntity = this;
      }
   }

   public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean bl) {
      this.setPosition(x, y, z);
      this.setRotation(yaw, pitch);
      List list = this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox().contract(0.03125, 0.0, 0.03125));
      if (!list.isEmpty()) {
         double d = 0.0;

         for (Object object : list) {
            AxisAlignedBB axisAlignedBB = (AxisAlignedBB)object;
            if (axisAlignedBB.maxY > d) {
               d = axisAlignedBB.maxY;
            }
         }

         y += d - this.getEntityBoundingBox().minY;
         this.setPosition(x, y, z);
      }
   }

   public float getCollisionBorderSize() {
      if (!(this instanceof EntityPlayer)) {
         return 0.1F;
      }

      HitboxEvent hitboxEvent = new HitboxEvent(this);
      Astolfo.INSTANCE.getEventBus().post(hitboxEvent);
      return hitboxEvent.borderSize;
   }

   public Vec3 getLookVec() {
      return null;
   }

   public void setPortal(BlockPos pos) {
      if (this.timeUntilPortal > 0) {
         this.timeUntilPortal = this.getPortalCooldown();
      } else {
         if (!this.worldObj.isRemote && !pos.equals(this.lastPortalPos)) {
            this.lastPortalPos = pos;
            BlockPattern.PatternHelper patternHelper = Blocks.portal.func_181089_f(this.worldObj, pos);
            double d = patternHelper.getFinger().getAxis() == EnumFacing.Axis.X ? patternHelper.getPos().getZ() : patternHelper.getPos().getX();
            double e = patternHelper.getFinger().getAxis() == EnumFacing.Axis.X ? this.posZ : this.posX;
            e = Math.abs(
               MathHelper.func_181160_c(
                  e - (patternHelper.getFinger().rotateY().getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE ? 1 : 0), d, d - patternHelper.func_181118_d()
               )
            );
            double d2 = MathHelper.func_181160_c(this.posY - 1.0, patternHelper.getPos().getY(), patternHelper.getPos().getY() - patternHelper.func_181119_e());
            this.lastPortalVec = new Vec3(e, d2, 0.0);
            this.teleportDirection = patternHelper.getFinger();
         }

         this.inPortal = true;
      }
   }

   public int getPortalCooldown() {
      return 300;
   }

   public void setVelocity(double x, double y, double z) {
      this.motionX = x;
      this.motionY = y;
      this.motionZ = z;
   }

   public void handleStatusUpdate(byte id) {
   }

   public void performHurtAnimation() {
   }

   public ItemStack[] getInventory() {
      return null;
   }

   public void setCurrentItemOrArmor(int slotIn, ItemStack stack) {
   }

   public boolean isBurning() {
      boolean bl = this.worldObj != null && this.worldObj.isRemote;
      return !this.isImmuneToFire && (this.fire > 0 || bl && this.getFlag(0));
   }

   public boolean E() {
      return this.ridingEntity != null;
   }

   public boolean isSneaking() {
      return this.getFlag(1);
   }

   public void v(boolean bl) {
      this.setFlag(1, bl);
   }

   public boolean isSprinting() {
      return this.getFlag(3);
   }

   public void setSprinting(boolean sprinting) {
      this.setFlag(3, sprinting);
   }

   public boolean isInvisible() {
      return this.getFlag(5);
   }

   public boolean isInvisibleToPlayer(EntityPlayer player) {
      return player.isSpectator() ? false : this.isInvisible();
   }

   public void setInvisible(boolean invisible) {
      this.setFlag(5, invisible);
   }

   public boolean m() {
      return this.getFlag(4);
   }

   public void setEating(boolean eating) {
      this.setFlag(4, eating);
   }

   public boolean getFlag(int flag) {
      return (this.dataWatcher.getWatchableObjectByte(0) & 1 << flag) != 0;
   }

   public void setFlag(int flag, boolean set) {
      byte b = this.dataWatcher.getWatchableObjectByte(0);
      if (set) {
         this.dataWatcher.updateObject(0, (byte)(b | 1 << flag));
      } else {
         this.dataWatcher.updateObject(0, (byte)(b & ~(1 << flag)));
      }
   }

   public int getAir() {
      return this.dataWatcher.getWatchableObjectShort(1);
   }

   public void setAir(int air) {
      this.dataWatcher.updateObject(1, (short)air);
   }

   public void onStruckByLightning(EntityLightningBolt lightningBolt) {
      this.attackEntityFrom(DamageSource.lightningBolt, 5.0F);
      this.fire++;
      if (this.fire == 0) {
         this.setFire(8);
      }
   }

   public void onKillEntity(EntityLivingBase entityLivingIn) {
   }

   public boolean pushOutOfBlocks(double x, double y, double z) {
      BlockPos blockPos = new BlockPos(x, y, z);
      double d = x - blockPos.getX();
      double e = y - blockPos.getY();
      double d2 = z - blockPos.getZ();
      List list = this.worldObj.getCollisionBoxes(this.getEntityBoundingBox());
      if (list.isEmpty() && !this.worldObj.isBlockFullCube(blockPos)) {
         return false;
      }

      byte b = 3;
      double d3 = 9999.0;
      if (!this.worldObj.isBlockFullCube(blockPos.west()) && d < d3) {
         d3 = d;
         b = 0;
      }

      if (!this.worldObj.isBlockFullCube(blockPos.east()) && 1.0 - d < d3) {
         d3 = 1.0 - d;
         b = 1;
      }

      if (!this.worldObj.isBlockFullCube(blockPos.up()) && 1.0 - e < d3) {
         d3 = 1.0 - e;
         b = 3;
      }

      if (!this.worldObj.isBlockFullCube(blockPos.north()) && d2 < d3) {
         d3 = d2;
         b = 4;
      }

      if (!this.worldObj.isBlockFullCube(blockPos.south()) && 1.0 - d2 < d3) {
         d3 = 1.0 - d2;
         b = 5;
      }

      float f = this.rand.nextFloat() * 0.2F + 0.1F;
      if (b == 0) {
         this.motionX = -f;
      }

      if (b == 1) {
         this.motionX = f;
      }

      if (b == 3) {
         this.motionY = f;
      }

      if (b == 4) {
         this.motionZ = -f;
      }

      if (b == 5) {
         this.motionZ = f;
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

      String string = EntityList.getEntityString(this);
      if (string == null) {
         string = "generic";
      }

      return StatCollector.translateToLocal("entity." + string + ".name");
   }

   public Entity[] getParts() {
      return null;
   }

   public boolean isEntityEqual(Entity entityIn) {
      return this == entityIn;
   }

   public float getRotationYawHead() {
      return 0.0F;
   }

   public void setRotationYawHead(float rotation) {
   }

   public void setRenderYawOffset(float offset) {
   }

   public boolean canAttackWithItem() {
      return true;
   }

   public boolean hitByEntity(Entity entityIn) {
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

   public boolean isEntityInvulnerable(DamageSource source) {
      return this.invulnerable && source != DamageSource.outOfWorld && !source.isCreativePlayer();
   }

   public void z(Entity entity) {
      this.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch);
   }

   public void copyDataFromOld(Entity entityIn) {
      NBTTagCompound nbtTagCompound = new NBTTagCompound();
      entityIn.writeToNBT(nbtTagCompound);
      this.readFromNBT(nbtTagCompound);
      this.timeUntilPortal = entityIn.timeUntilPortal;
      this.lastPortalPos = entityIn.lastPortalPos;
      this.lastPortalVec = entityIn.lastPortalVec;
      this.teleportDirection = entityIn.teleportDirection;
   }

   public void travelToDimension(int dimensionId) {
      if (!this.worldObj.isRemote && !this.isDead) {
         this.worldObj.theProfiler.startSection("changeDimension");
         MinecraftServer minecraftServer = MinecraftServer.getServer();
         int i = this.dimension;
         WorldServer worldServer = minecraftServer.worldServerForDimension(i);
         WorldServer worldServer2 = minecraftServer.worldServerForDimension(dimensionId);
         this.dimension = dimensionId;
         if (i == 1 && dimensionId == 1) {
            worldServer2 = minecraftServer.worldServerForDimension(0);
            this.dimension = 0;
         }

         this.worldObj.removeEntity(this);
         this.isDead = false;
         this.worldObj.theProfiler.startSection("reposition");
         minecraftServer.getConfigurationManager().transferEntityToWorld(this, i, worldServer, worldServer2);
         this.worldObj.theProfiler.endStartSection("reloading");
         Entity entity = EntityList.createEntityByName(EntityList.getEntityString(this), worldServer2);
         if (entity != null) {
            entity.copyDataFromOld(this);
            if (i == 1 && dimensionId == 1) {
               BlockPos blockPos = this.worldObj.getTopSolidOrLiquidBlock(worldServer2.getSpawnPoint());
               entity.moveToBlockPosAndAngles(blockPos, entity.rotationYaw, entity.rotationPitch);
            }

            worldServer2.spawnEntityInWorld(entity);
         }

         this.isDead = true;
         this.worldObj.theProfiler.endSection();
         worldServer.resetUpdateEntityTick();
         worldServer2.resetUpdateEntityTick();
         this.worldObj.theProfiler.endSection();
      }
   }

   public float getExplosionResistance(Explosion explosionIn, World worldIn, BlockPos pos, IBlockState blockStateIn) {
      return blockStateIn.getBlock().getExplosionResistance(this);
   }

   public boolean verifyExplosion(Explosion explosionIn, World worldIn, BlockPos pos, IBlockState blockStateIn, float f) {
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

   public void addEntityCrashInfo(CrashReportCategory category) {
      category.addCrashSectionCallable("Entity Type", new Callable<String>() {
         public String call() throws Exception {
            return EntityList.getEntityString(Entity.this) + " (" + Entity.this.getClass().getCanonicalName() + ")";
         }
      });
      category.addCrashSection("Entity ID", this.entityId);
      category.addCrashSectionCallable("Entity Name", new Callable<String>() {
         public String call() throws Exception {
            return Entity.this.getName();
         }
      });
      category.addCrashSection("Entity's Exact location", String.format("%.2f, %.2f, %.2f", this.posX, this.posY, this.posZ));
      category.addCrashSection(
         "Entity's Block location",
         CrashReportCategory.getCoordinateInfo(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ))
      );
      category.addCrashSection("Entity's Momentum", String.format("%.2f, %.2f, %.2f", this.motionX, this.motionY, this.motionZ));
      category.addCrashSectionCallable("Entity's Rider", new Callable<String>() {
         public String call() throws Exception {
            return Entity.this.riddenByEntity.toString();
         }
      });
      category.addCrashSectionCallable("Entity's Vehicle", new Callable<String>() {
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
      ChatComponentText chatComponentText = new ChatComponentText(this.getName());
      chatComponentText.getChatStyle().setChatHoverEvent(this.getHoverEvent());
      chatComponentText.getChatStyle().setInsertion(this.getUniqueID().toString());
      return chatComponentText;
   }

   public void setCustomNameTag(String name) {
      this.dataWatcher.updateObject(2, name);
   }

   public String getCustomNameTag() {
      return this.dataWatcher.getWatchableObjectString(2);
   }

   public boolean hasCustomName() {
      return this.dataWatcher.getWatchableObjectString(2).length() > 0;
   }

   public void setAlwaysRenderNameTag(boolean alwaysRenderNameTag) {
      this.dataWatcher.updateObject(3, (byte)(alwaysRenderNameTag ? 1 : 0));
   }

   public boolean getAlwaysRenderNameTag() {
      return this.dataWatcher.getWatchableObjectByte(3) == 1;
   }

   public void setPositionAndUpdate(double x, double y, double z) {
      this.setLocationAndAngles(x, y, z, this.rotationYaw, this.rotationPitch);
   }

   public boolean getAlwaysRenderNameTagForRender() {
      return this.getAlwaysRenderNameTag();
   }

   public void onDataWatcherUpdate(int dataID) {
   }

   public EnumFacing getHorizontalFacing() {
      return EnumFacing.getHorizontal(MathHelper.floor_double(this.rotationYaw * 4.0F / 360.0F + 0.5) & 3);
   }

   public HoverEvent getHoverEvent() {
      NBTTagCompound nbtTagCompound = new NBTTagCompound();
      String string = EntityList.getEntityString(this);
      nbtTagCompound.setString("id", this.getUniqueID().toString());
      if (string != null) {
         nbtTagCompound.setString("type", string);
      }

      nbtTagCompound.setString("name", this.getName());
      return new HoverEvent(HoverEvent.Action.SHOW_ENTITY, new ChatComponentText(nbtTagCompound.toString()));
   }

   public boolean isSpectatedByPlayer(EntityPlayerMP player) {
      return true;
   }

   public AxisAlignedBB getEntityBoundingBox() {
      return this.boundingBox;
   }

   public void setEntityBoundingBox(AxisAlignedBB bb) {
      this.boundingBox = bb;
   }

   public float getEyeHeight() {
      return this.height * 0.85F;
   }

   public boolean isOutsideBorder() {
      return this.isOutsideBorder;
   }

   public void setOutsideBorder(boolean outsideBorder) {
      this.isOutsideBorder = outsideBorder;
   }

   public boolean replaceItemInInventory(int inventorySlot, ItemStack itemStackIn) {
      return false;
   }

   @Override
   public void addChatMessage(IChatComponent component) {
   }

   @Override
   public boolean canCommandSenderUseCommand(int permLevel, String commandName) {
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
   public void setCommandStat(CommandResultStats.Type typeIn, int amount) {
      this.cmdResultStats.setCommandStatScore(this, typeIn, amount);
   }

   public CommandResultStats getCommandStats() {
      return this.cmdResultStats;
   }

   public void T_void(Entity entity) {
      this.cmdResultStats.addAllStats(entity.getCommandStats());
   }

   public NBTTagCompound getNBTTagCompound() {
      return null;
   }

   public void clientUpdateEntityNBT(NBTTagCompound compound) {
   }

   public boolean interactAt(EntityPlayer player, Vec3 targetVec3) {
      return false;
   }

   public boolean isImmuneToExplosions() {
      return false;
   }

   public void applyEnchantments(EntityLivingBase entityLivingBaseIn, Entity entityIn) {
      if (entityIn instanceof EntityLivingBase) {
         EnchantmentHelper.applyThornEnchantments((EntityLivingBase)entityIn, entityLivingBaseIn);
      }

      EnchantmentHelper.applyArthropodEnchantments(entityLivingBaseIn, entityIn);
   }
}
