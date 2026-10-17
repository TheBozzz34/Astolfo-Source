package net.minecraft.entity.projectile;

import java.util.Arrays;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemFishFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.WeightedRandomFishable;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class EntityFishHook extends Entity {
   private static final List<WeightedRandomFishable> JUNK = Arrays.asList(
      new WeightedRandomFishable(new ItemStack(Items.leather_boots), 10).setMaxDamagePercent(0.9F),
      new WeightedRandomFishable(new ItemStack(Items.leather), 10),
      new WeightedRandomFishable(new ItemStack(Items.bone), 10),
      new WeightedRandomFishable(new ItemStack(Items.potionitem), 10),
      new WeightedRandomFishable(new ItemStack(Items.string), 5),
      new WeightedRandomFishable(new ItemStack(Items.fishing_rod), 2).setMaxDamagePercent(0.9F),
      new WeightedRandomFishable(new ItemStack(Items.bowl), 10),
      new WeightedRandomFishable(new ItemStack(Items.stick), 5),
      new WeightedRandomFishable(new ItemStack(Items.dye, 10, EnumDyeColor.BLACK.getDyeDamage()), 1),
      new WeightedRandomFishable(new ItemStack(Blocks.tripwire_hook), 10),
      new WeightedRandomFishable(new ItemStack(Items.rotten_flesh), 10)
   );
   private static final List<WeightedRandomFishable> TREASURE = Arrays.asList(
      new WeightedRandomFishable(new ItemStack(Blocks.waterlily), 1),
      new WeightedRandomFishable(new ItemStack(Items.name_tag), 1),
      new WeightedRandomFishable(new ItemStack(Items.saddle), 1),
      new WeightedRandomFishable(new ItemStack(Items.bow), 1).setMaxDamagePercent(0.25F).setEnchantable(),
      new WeightedRandomFishable(new ItemStack(Items.fishing_rod), 1).setMaxDamagePercent(0.25F).setEnchantable(),
      new WeightedRandomFishable(new ItemStack(Items.book), 1).setEnchantable()
   );
   private static final List<WeightedRandomFishable> FISH = Arrays.asList(
      new WeightedRandomFishable(new ItemStack(Items.fish, 1, ItemFishFood.FishType.COD.getMetadata()), 60),
      new WeightedRandomFishable(new ItemStack(Items.fish, 1, ItemFishFood.FishType.SALMON.getMetadata()), 25),
      new WeightedRandomFishable(new ItemStack(Items.fish, 1, ItemFishFood.FishType.CLOWNFISH.getMetadata()), 2),
      new WeightedRandomFishable(new ItemStack(Items.fish, 1, ItemFishFood.FishType.PUFFERFISH.getMetadata()), 13)
   );
   private int xTile = -1;
   private int yTile = -1;
   private int zTile = -1;
   private Block inTile;
   private boolean inGround;
   public int shake;
   public EntityPlayer angler;
   private int ticksInGround;
   private int ticksInAir;
   private int ticksCatchable;
   private int ticksCaughtDelay;
   private int ticksCatchableDelay;
   private float fishApproachAngle;
   public Entity caughtEntity;
   private int fishPosRotationIncrements;
   private double fishX;
   private double fishY;
   private double fishZ;
   private double fishYaw;
   private double fishPitch;
   private double clientMotionX;
   private double clientMotionY;
   private double clientMotionZ;

   public static List<WeightedRandomFishable> func_174855_j() {
      return FISH;
   }

   public EntityFishHook(World worldIn) {
      super(worldIn);
      this.setSize(0.25F, 0.25F);
      this.ignoreFrustumCheck = true;
   }

   public EntityFishHook(World worldIn, double x, double y, double z, EntityPlayer anglerIn) {
      this(worldIn);
      this.setPosition(x, y, z);
      this.ignoreFrustumCheck = true;
      this.angler = anglerIn;
      anglerIn.fishEntity = this;
   }

   public EntityFishHook(World worldIn, EntityPlayer fishingPlayer) {
      super(worldIn);
      this.ignoreFrustumCheck = true;
      this.angler = fishingPlayer;
      this.angler.fishEntity = this;
      this.setSize(0.25F, 0.25F);
      this.setLocationAndAngles(
         fishingPlayer.posX, fishingPlayer.posY + fishingPlayer.getEyeHeight(), fishingPlayer.posZ, fishingPlayer.rotationYaw, fishingPlayer.rotationPitch
      );
      this.posX = this.posX - MathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI) * 0.16F;
      this.posY -= 0.1F;
      this.posZ = this.posZ - MathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI) * 0.16F;
      this.setPosition(this.posX, this.posY, this.posZ);
      float f = 0.4F;
      this.motionX = -MathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI) * f;
      this.motionZ = MathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI) * f;
      this.motionY = -MathHelper.sin(this.rotationPitch / 180.0F * (float) Math.PI) * f;
      this.handleHookCasting(this.motionX, this.motionY, this.motionZ, 1.5F, 1.0F);
   }

   @Override
   public void entityInit() {
   }

   @Override
   public boolean isInRangeToRenderDist(double distance) {
      double d = this.getEntityBoundingBox().getAverageEdgeLength() * 4.0;
      if (Double.isNaN(d)) {
         d = 4.0;
      }

      d *= 64.0;
      return distance < d * d;
   }

   public void handleHookCasting(double d, double e, double d2, float f, float g) {
      float h = MathHelper.sqrt_double(d * d + e * e + d2 * d2);
      d /= h;
      e /= h;
      d2 /= h;
      d += this.rand.nextGaussian() * 0.0075F * g;
      e += this.rand.nextGaussian() * 0.0075F * g;
      d2 += this.rand.nextGaussian() * 0.0075F * g;
      d *= f;
      e *= f;
      d2 *= f;
      this.motionX = d;
      this.motionY = e;
      this.motionZ = d2;
      float f2 = MathHelper.sqrt_double(d * d + d2 * d2);
      this.prevRotationYaw = this.rotationYaw = (float)(MathHelper.atan2(d, d2) * 180.0 / Math.PI);
      this.prevRotationPitch = this.rotationPitch = (float)(MathHelper.atan2(e, f2) * 180.0 / Math.PI);
      this.ticksInGround = 0;
   }

   @Override
   public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean bl) {
      this.fishX = x;
      this.fishY = y;
      this.fishZ = z;
      this.fishYaw = yaw;
      this.fishPitch = pitch;
      this.fishPosRotationIncrements = posRotationIncrements;
      this.motionX = this.clientMotionX;
      this.motionY = this.clientMotionY;
      this.motionZ = this.clientMotionZ;
   }

   @Override
   public void setVelocity(double x, double y, double z) {
      this.clientMotionX = this.motionX = x;
      this.clientMotionY = this.motionY = y;
      this.clientMotionZ = this.motionZ = z;
   }

   @Override
   public void onUpdate() {
      super.onUpdate();
      if (this.fishPosRotationIncrements > 0) {
         double d = this.posX + (this.fishX - this.posX) / this.fishPosRotationIncrements;
         double e = this.posY + (this.fishY - this.posY) / this.fishPosRotationIncrements;
         double d2 = this.posZ + (this.fishZ - this.posZ) / this.fishPosRotationIncrements;
         double d3 = MathHelper.wrapAngleTo180_double(this.fishYaw - this.rotationYaw);
         this.rotationYaw = (float)(this.rotationYaw + d3 / this.fishPosRotationIncrements);
         this.rotationPitch = (float)(this.rotationPitch + (this.fishPitch - this.rotationPitch) / this.fishPosRotationIncrements);
         this.fishPosRotationIncrements--;
         this.setPosition(d, e, d2);
         this.setRotation(this.rotationYaw, this.rotationPitch);
      } else {
         if (!this.worldObj.isRemote) {
            ItemStack itemStack = this.angler.getCurrentEquippedItem();
            if (this.angler.isDead
               || !this.angler.isEntityAlive()
               || itemStack == null
               || itemStack.getItem() != Items.fishing_rod
               || this.getDistanceSqToEntity(this.angler) > 1024.0) {
               this.setDead();
               this.angler.fishEntity = null;
               return;
            }

            if (this.caughtEntity != null) {
               if (!this.caughtEntity.isDead) {
                  this.posX = this.caughtEntity.posX;
                  double d4 = this.caughtEntity.height;
                  this.posY = this.caughtEntity.getEntityBoundingBox().minY + d4 * 0.8;
                  this.posZ = this.caughtEntity.posZ;
                  return;
               }

               this.caughtEntity = null;
            }
         }

         if (this.shake > 0) {
            this.shake--;
         }

         if (this.inGround) {
            if (this.worldObj.getBlockState(new BlockPos(this.xTile, this.yTile, this.zTile)).getBlock() == this.inTile) {
               this.ticksInGround++;
               if (this.ticksInGround == 1200) {
                  this.setDead();
               }

               return;
            }

            this.inGround = false;
            this.motionX = this.motionX * (this.rand.nextFloat() * 0.2F);
            this.motionY = this.motionY * (this.rand.nextFloat() * 0.2F);
            this.motionZ = this.motionZ * (this.rand.nextFloat() * 0.2F);
            this.ticksInGround = 0;
            this.ticksInAir = 0;
         } else {
            this.ticksInAir++;
         }

         Vec3 vec3 = new Vec3(this.posX, this.posY, this.posZ);
         Vec3 vec32 = new Vec3(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
         MovingObjectPosition movingObjectPosition = this.worldObj.rayTraceBlocks(vec3, vec32);
         vec3 = new Vec3(this.posX, this.posY, this.posZ);
         vec32 = new Vec3(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
         if (movingObjectPosition != null) {
            vec32 = new Vec3(movingObjectPosition.hitVec.xCoord, movingObjectPosition.hitVec.yCoord, movingObjectPosition.hitVec.zCoord);
         }

         Entity entity = null;
         List list = this.worldObj
            .getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0, 1.0, 1.0));
         double d5 = 0.0;

         for (int i = 0; i < list.size(); i++) {
            Entity entity2 = (Entity)list.get(i);
            if (entity2.canBeCollidedWith() && (entity2 != this.angler || this.ticksInAir >= 5)) {
               float f = 0.3F;
               AxisAlignedBB axisAlignedBB = entity2.getEntityBoundingBox().expand(f, f, f);
               MovingObjectPosition movingObjectPosition2 = axisAlignedBB.calculateIntercept(vec3, vec32);
               if (movingObjectPosition2 != null) {
                  double d6 = vec3.squareDistanceTo(movingObjectPosition2.hitVec);
                  if (d6 < d5 || d5 == 0.0) {
                     entity = entity2;
                     d5 = d6;
                  }
               }
            }
         }

         if (entity != null) {
            movingObjectPosition = new MovingObjectPosition(entity);
         }

         if (movingObjectPosition != null) {
            if (movingObjectPosition.entityHit != null) {
               if (movingObjectPosition.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.angler), 0.0F)) {
                  this.caughtEntity = movingObjectPosition.entityHit;
               }
            } else {
               this.inGround = true;
            }
         }

         if (!this.inGround) {
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            float g = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.rotationYaw = (float)(MathHelper.atan2(this.motionX, this.motionZ) * 180.0 / Math.PI);
            this.rotationPitch = (float)(MathHelper.atan2(this.motionY, g) * 180.0 / Math.PI);

            while (this.rotationPitch - this.prevRotationPitch < -180.0F) {
               this.prevRotationPitch -= 360.0F;
            }

            while (this.rotationPitch - this.prevRotationPitch >= 180.0F) {
               this.prevRotationPitch += 360.0F;
            }

            while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
               this.prevRotationYaw -= 360.0F;
            }

            while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
               this.prevRotationYaw += 360.0F;
            }

            this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
            this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
            float h = 0.92F;
            if (this.onGround || this.isCollidedHorizontally) {
               h = 0.5F;
            }

            byte b = 5;
            double d7 = 0.0;

            for (int j = 0; j < b; j++) {
               AxisAlignedBB axisAlignedBB2 = this.getEntityBoundingBox();
               double d8 = axisAlignedBB2.maxY - axisAlignedBB2.minY;
               double d9 = axisAlignedBB2.minY + d8 * j / b;
               double d10 = axisAlignedBB2.minY + d8 * (j + 1) / b;
               AxisAlignedBB axisAlignedBB3 = new AxisAlignedBB(axisAlignedBB2.minX, d9, axisAlignedBB2.minZ, axisAlignedBB2.maxX, d10, axisAlignedBB2.maxZ);
               if (this.worldObj.isAABBInMaterial(axisAlignedBB3, Material.water)) {
                  d7 += 1.0 / b;
               }
            }

            if (!this.worldObj.isRemote && d7 > 0.0) {
               WorldServer worldServer = (WorldServer)this.worldObj;
               int k = 1;
               BlockPos blockPos = new BlockPos(this).up();
               if (this.rand.nextFloat() < 0.25F && this.worldObj.isRainingAt(blockPos)) {
                  k = 2;
               }

               if (this.rand.nextFloat() < 0.5F && !this.worldObj.canSeeSky(blockPos)) {
                  k--;
               }

               if (this.ticksCatchable > 0) {
                  this.ticksCatchable--;
                  if (this.ticksCatchable <= 0) {
                     this.ticksCaughtDelay = 0;
                     this.ticksCatchableDelay = 0;
                  }
               } else if (this.ticksCatchableDelay > 0) {
                  this.ticksCatchableDelay -= k;
                  if (this.ticksCatchableDelay <= 0) {
                     this.motionY -= 0.2F;
                     this.playSound("random.splash", 0.25F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
                     float f2 = MathHelper.floor_double(this.getEntityBoundingBox().minY);
                     worldServer.spawnParticle(
                        EnumParticleTypes.WATER_BUBBLE, this.posX, f2 + 1.0F, this.posZ, (int)(1.0F + this.width * 20.0F), this.width, 0.0, this.width, 0.2F
                     );
                     worldServer.spawnParticle(
                        EnumParticleTypes.WATER_WAKE, this.posX, f2 + 1.0F, this.posZ, (int)(1.0F + this.width * 20.0F), this.width, 0.0, this.width, 0.2F
                     );
                     this.ticksCatchable = MathHelper.getRandomIntegerInRange(this.rand, 10, 30);
                  } else {
                     this.fishApproachAngle = (float)(this.fishApproachAngle + this.rand.nextGaussian() * 4.0);
                     float f3 = this.fishApproachAngle * (float) (Math.PI / 180.0);
                     float f4 = MathHelper.sin(f3);
                     float f5 = MathHelper.cos(f3);
                     double d11 = this.posX + f4 * this.ticksCatchableDelay * 0.1F;
                     double d12 = MathHelper.floor_double(this.getEntityBoundingBox().minY) + 1.0F;
                     double d13 = this.posZ + f5 * this.ticksCatchableDelay * 0.1F;
                     Block block = worldServer.getBlockState(new BlockPos((int)d11, (int)d12 - 1, (int)d13)).getBlock();
                     if (block == Blocks.water || block == Blocks.flowing_water) {
                        if (this.rand.nextFloat() < 0.15F) {
                           worldServer.spawnParticle(EnumParticleTypes.WATER_BUBBLE, d11, d12 - 0.1F, d13, 1, f4, 0.1, f5, 0.0);
                        }

                        float f6 = f4 * 0.04F;
                        float f7 = f5 * 0.04F;
                        worldServer.spawnParticle(EnumParticleTypes.WATER_WAKE, d11, d12, d13, 0, f7, 0.01, -f6, 1.0);
                        worldServer.spawnParticle(EnumParticleTypes.WATER_WAKE, d11, d12, d13, 0, -f7, 0.01, f6, 1.0);
                     }
                  }
               } else if (this.ticksCaughtDelay > 0) {
                  this.ticksCaughtDelay -= k;
                  float f8 = 0.15F;
                  if (this.ticksCaughtDelay < 20) {
                     f8 = (float)(f8 + (20 - this.ticksCaughtDelay) * 0.05);
                  } else if (this.ticksCaughtDelay < 40) {
                     f8 = (float)(f8 + (40 - this.ticksCaughtDelay) * 0.02);
                  } else if (this.ticksCaughtDelay < 60) {
                     f8 = (float)(f8 + (60 - this.ticksCaughtDelay) * 0.01);
                  }

                  if (this.rand.nextFloat() < f8) {
                     float f9 = MathHelper.randomFloatClamp(this.rand, 0.0F, 360.0F) * (float) (Math.PI / 180.0);
                     float f10 = MathHelper.randomFloatClamp(this.rand, 25.0F, 60.0F);
                     double d14 = this.posX + MathHelper.sin(f9) * f10 * 0.1F;
                     double d15 = MathHelper.floor_double(this.getEntityBoundingBox().minY) + 1.0F;
                     double d16 = this.posZ + MathHelper.cos(f9) * f10 * 0.1F;
                     Block block2 = worldServer.getBlockState(new BlockPos((int)d14, (int)d15 - 1, (int)d16)).getBlock();
                     if (block2 == Blocks.water || block2 == Blocks.flowing_water) {
                        worldServer.spawnParticle(EnumParticleTypes.WATER_SPLASH, d14, d15, d16, 2 + this.rand.nextInt(2), 0.1F, 0.0, 0.1F, 0.0);
                     }
                  }

                  if (this.ticksCaughtDelay <= 0) {
                     this.fishApproachAngle = MathHelper.randomFloatClamp(this.rand, 0.0F, 360.0F);
                     this.ticksCatchableDelay = MathHelper.getRandomIntegerInRange(this.rand, 20, 80);
                  }
               } else {
                  this.ticksCaughtDelay = MathHelper.getRandomIntegerInRange(this.rand, 100, 900);
                  this.ticksCaughtDelay = this.ticksCaughtDelay - EnchantmentHelper.getLureModifier(this.angler) * 20 * 5;
               }

               if (this.ticksCatchable > 0) {
                  this.motionY = this.motionY - this.rand.nextFloat() * this.rand.nextFloat() * this.rand.nextFloat() * 0.2;
               }
            }

            double d17 = d7 * 2.0 - 1.0;
            this.motionY += 0.04F * d17;
            if (d7 > 0.0) {
               h = (float)(h * 0.9);
               this.motionY *= 0.8;
            }

            this.motionX *= h;
            this.motionY *= h;
            this.motionZ *= h;
            this.setPosition(this.posX, this.posY, this.posZ);
         }
      }
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      tagCompound.setShort("xTile", (short)this.xTile);
      tagCompound.setShort("yTile", (short)this.yTile);
      tagCompound.setShort("zTile", (short)this.zTile);
      ResourceLocation resourceLocation = Block.blockRegistry.getNameForObject(this.inTile);
      tagCompound.setString("inTile", resourceLocation == null ? "" : resourceLocation.toString());
      tagCompound.setByte("shake", (byte)this.shake);
      tagCompound.setByte("inGround", (byte)(this.inGround ? 1 : 0));
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      this.xTile = tagCompund.getShort("xTile");
      this.yTile = tagCompund.getShort("yTile");
      this.zTile = tagCompund.getShort("zTile");
      if (tagCompund.hasKey("inTile", 8)) {
         this.inTile = Block.getBlockFromName(tagCompund.getString("inTile"));
      } else {
         this.inTile = Block.getBlockById(tagCompund.getByte("inTile") & 255);
      }

      this.shake = tagCompund.getByte("shake") & 255;
      this.inGround = tagCompund.getByte("inGround") == 1;
   }

   public int handleHookRetraction() {
      if (this.worldObj.isRemote) {
         return 0;
      }

      byte b = 0;
      if (this.caughtEntity != null) {
         double d = this.angler.posX - this.posX;
         double e = this.angler.posY - this.posY;
         double d2 = this.angler.posZ - this.posZ;
         double d3 = MathHelper.sqrt_double(d * d + e * e + d2 * d2);
         double d4 = 0.1;
         this.caughtEntity.motionX += d * d4;
         this.caughtEntity.motionY = this.caughtEntity.motionY + (e * d4 + MathHelper.sqrt_double(d3) * 0.08);
         this.caughtEntity.motionZ += d2 * d4;
         b = 3;
      } else if (this.ticksCatchable > 0) {
         EntityItem entityItem = new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, this.getFishingResult());
         double d5 = this.angler.posX - this.posX;
         double d6 = this.angler.posY - this.posY;
         double d7 = this.angler.posZ - this.posZ;
         double d8 = MathHelper.sqrt_double(d5 * d5 + d6 * d6 + d7 * d7);
         double d9 = 0.1;
         entityItem.motionX = d5 * d9;
         entityItem.motionY = d6 * d9 + MathHelper.sqrt_double(d8) * 0.08;
         entityItem.motionZ = d7 * d9;
         this.worldObj.spawnEntityInWorld(entityItem);
         this.angler
            .worldObj
            .spawnEntityInWorld(
               new EntityXPOrb(this.angler.worldObj, this.angler.posX, this.angler.posY + 0.5, this.angler.posZ + 0.5, this.rand.nextInt(6) + 1)
            );
         b = 1;
      }

      if (this.inGround) {
         b = 2;
      }

      this.setDead();
      this.angler.fishEntity = null;
      return b;
   }

   private ItemStack getFishingResult() {
      float f = this.worldObj.rand.nextFloat();
      int i = EnchantmentHelper.getLuckOfSeaModifier(this.angler);
      int j = EnchantmentHelper.getLureModifier(this.angler);
      float g = 0.1F - i * 0.025F - j * 0.01F;
      float h = 0.05F + i * 0.01F - j * 0.01F;
      g = MathHelper.clamp_float(g, 0.0F, 1.0F);
      h = MathHelper.clamp_float(h, 0.0F, 1.0F);
      if (f < g) {
         this.angler.triggerAchievement(StatList.junkFishedStat);
         return WeightedRandom.getRandomItem(this.rand, JUNK).getItemStack(this.rand);
      } else {
         f -= g;
         if (f < h) {
            this.angler.triggerAchievement(StatList.treasureFishedStat);
            return WeightedRandom.getRandomItem(this.rand, TREASURE).getItemStack(this.rand);
         } else {
            float f2 = f - h;
            this.angler.triggerAchievement(StatList.fishCaughtStat);
            return WeightedRandom.getRandomItem(this.rand, FISH).getItemStack(this.rand);
         }
      }
   }

   @Override
   public void setDead() {
      super.setDead();
      if (this.angler != null) {
         this.angler.fishEntity = null;
      }
   }
}
