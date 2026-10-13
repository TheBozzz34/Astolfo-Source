package net.minecraft.entity;

import bypass.Astolfo;
import bypass.event.player.RotationYawEvent;
import bypass.module.exploits.RiderGod;
import bypass.module.movement.NoSlow;
import bypass.module.movement.Sprint;
import bypass.module.visuals.Animations;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.ServersideAttributeMap;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.network.play.server.S04PacketEntityEquipment;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.network.play.server.S0DPacketCollectItem;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.CombatTracker;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public abstract class EntityLivingBase extends Entity {
   private static final UUID sprintingSpeedBoostModifierUUID = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");
   private static final AttributeModifier sprintingSpeedBoostModifier = new AttributeModifier(sprintingSpeedBoostModifierUUID, "Sprinting speed boost", 0.3F, 2)
      .setSaved(false);
   private BaseAttributeMap attributeMap;
   private final CombatTracker _combatTracker = new CombatTracker(this);
   public final Map<Integer, PotionEffect> activePotionsMap = Maps.newHashMap();
   private final ItemStack[] previousEquipment = new ItemStack[5];
   public boolean Lp;
   public int swingProgressInt;
   public int arrowHitTimer;
   public int hurtTime;
   public int maxHurtTime;
   public float attackedAtYaw;
   public int deathTime;
   public float prevSwingProgress;
   public float swingProgress;
   public float prevLimbSwingAmount;
   public float limbSwingAmount;
   public float limbSwing;
   public int maxHurtResistantTime = 20;
   public float prevCameraPitch;
   public float cameraPitch;
   public float randomUnused2;
   public float randomUnused1;
   public float renderYawOffset;
   public float prevRenderYawOffset;
   public float rotationYawHead;
   public float prevRotationYawHead;
   public float jumpMovementFactor = 0.02F;
   public EntityPlayer attackingPlayer;
   public int recentlyHit;
   public boolean dead;
   public int entityAge;
   public float prevOnGroundSpeedFactor;
   public float onGroundSpeedFactor;
   public float movedDistance;
   public float prevMovedDistance;
   public float unused180;
   public int scoreValue;
   public float lastDamage;
   public boolean Lr;
   public float moveStrafing;
   public float moveForward;
   public float randomYawVelocity;
   public int newPosRotationIncrements;
   public double newPosX;
   public double newPosY;
   public double newPosZ;
   public double newRotationYaw;
   public double newRotationPitch;
   private boolean potionsNeedUpdate = true;
   private EntityLivingBase entityLivingToAttack;
   private int revengeTimer;
   private EntityLivingBase lastAttacker;
   private int lastAttackerTime;
   private float landMovementFactor;
   public int jumpTicks;
   private float absorptionAmount;
   private boolean Ls;
   private static final long fb = 102761715617941L;

   @Override
   public void onKillCommand() {
      this.attackEntityFrom(DamageSource.outOfWorld, Float.MAX_VALUE);
   }

   public EntityLivingBase(World var1) {
      super(var1);
      this.applyEntityAttributes();
      this.setHealth(this.getMaxHealth());
      this.preventEntitySpawning = true;
      this.randomUnused1 = (float)((Math.random() + 1.0) * 0.01F);
      this.setPosition(this.posX, this.posY, this.posZ);
      this.randomUnused2 = (float)Math.random() * 12398.0F;
      this.rotationYaw = (float)(Math.random() * Math.PI * 2.0);
      this.rotationYawHead = this.rotationYaw;
      this.stepHeight = 0.6F;
   }

   @Override
   public void entityInit() {
      this.dataWatcher.addObject(7, 0);
      this.dataWatcher.addObject(8, (byte)0);
      this.dataWatcher.addObject(9, (byte)0);
      this.dataWatcher.addObject(6, 1.0F);
   }

   public void applyEntityAttributes() {
      this.getAttributeMap().registerAttribute(SharedMonsterAttributes.maxHealth);
      this.getAttributeMap().registerAttribute(SharedMonsterAttributes.knockbackResistance);
      this.getAttributeMap().registerAttribute(SharedMonsterAttributes.movementSpeed);
   }

   @Override
   public void updateFallState(double var1, boolean var3, Block var4, BlockPos var5) {
      if (!this.isInWater()) {
         this.handleWaterMovement();
      }

      if (!this.worldObj.isRemote && this.fallDistance > 3.0F && var3) {
         IBlockState var9 = this.worldObj.getBlockState(var5);
         Block var10 = var9.getBlock();
         float var11 = MathHelper.ceiling_float_int(this.fallDistance - 3.0F);
         if (var10.getMaterial() != Material.air) {
            double var12 = Math.min(0.2F + var11 / 15.0F, 10.0F);
            if (var12 > 2.5) {
               var12 = 2.5;
            }

            int var14 = (int)(150.0 * var12);
            ((WorldServer)this.worldObj)
               .spawnParticle(EnumParticleTypes.BLOCK_DUST, this.posX, this.posY, this.posZ, var14, 0.0, 0.0, 0.0, 0.15F, Block.getStateId(var9));
         }
      }

      super.updateFallState(var1, var3, var4, var5);
   }

   public boolean canBreatheUnderwater() {
      return false;
   }

   @Override
   public void onEntityUpdate() {
      this.prevSwingProgress = this.swingProgress;
      super.onEntityUpdate();
      this.worldObj.theProfiler.startSection("livingEntityBaseTick");
      boolean var4 = this instanceof EntityPlayer;
      if (this.isEntityAlive()) {
         if (this.isEntityInsideOpaqueBlock()) {
            this.attackEntityFrom(DamageSource.inWall, 1.0F);
         } else if (var4 && !this.worldObj.getWorldBorder().contains(this.getEntityBoundingBox())) {
            double var5 = this.worldObj.getWorldBorder().getClosestDistance(this) + this.worldObj.getWorldBorder().getDamageBuffer();
            if (var5 < 0.0) {
               this.attackEntityFrom(DamageSource.inWall, Math.max(1, MathHelper.floor_double(-var5 * this.worldObj.getWorldBorder().getDamageAmount())));
            }
         }
      }

      if (this.isImmuneToFire() || this.worldObj.isRemote) {
         this.extinguish();
      }

      boolean var10 = var4 && ((EntityPlayer)this).capabilities.disableDamage;
      if (this.isEntityAlive()) {
         if (this.isInsideOfMaterial(Material.water)) {
            if (!this.canBreatheUnderwater() && !this.isPotionActive(Potion.waterBreathing.id) && !var10) {
               this.setAir(this.decreaseAirSupply(this.getAir()));
               if (this.getAir() == -20) {
                  this.setAir(0);

                  for (int var6 = 0; var6 < 8; var6++) {
                     float var7 = this.rand.nextFloat() - this.rand.nextFloat();
                     float var8 = this.rand.nextFloat() - this.rand.nextFloat();
                     float var9 = this.rand.nextFloat() - this.rand.nextFloat();
                     this.worldObj
                        .spawnParticle(
                           EnumParticleTypes.WATER_BUBBLE, this.posX + var7, this.posY + var8, this.posZ + var9, this.motionX, this.motionY, this.motionZ
                        );
                  }

                  this.attackEntityFrom(DamageSource.drown, 2.0F);
               }
            }

            if (!this.worldObj.isRemote && this.E() && this.ridingEntity instanceof EntityLivingBase) {
               this.mountEntity((Entity)null);
            }
         } else {
            this.setAir(300);
         }
      }

      if (this.isEntityAlive() && this.G()) {
         this.extinguish();
      }

      this.prevCameraPitch = this.cameraPitch;
      if (this.hurtTime > 0) {
         this.hurtTime--;
      }

      if (this.hurtResistantTime > 0 && !(this instanceof EntityPlayerMP)) {
         this.hurtResistantTime--;
      }

      if (this.getHealth() <= 0.0F) {
         this.onDeathUpdate();
      }

      if (this.recentlyHit > 0) {
         this.recentlyHit--;
      } else {
         this.attackingPlayer = null;
      }

      if (this.lastAttacker != null && !this.lastAttacker.isEntityAlive()) {
         this.lastAttacker = null;
      }

      if (this.entityLivingToAttack != null) {
         if (!this.entityLivingToAttack.isEntityAlive()) {
            this.setRevengeTarget((EntityLivingBase)null);
         } else if (this.ticksExisted - this.revengeTimer > 100) {
            this.setRevengeTarget((EntityLivingBase)null);
         }
      }

      this.updatePotionEffects();
      this.prevMovedDistance = this.movedDistance;
      this.prevRenderYawOffset = this.renderYawOffset;
      this.prevRotationYawHead = this.rotationYawHead;
      this.prevRotationYaw = this.rotationYaw;
      this.prevRotationPitch = this.rotationPitch;
      this.worldObj.theProfiler.endSection();
   }

   public boolean isChild() {
      return false;
   }

   public void onDeathUpdate() {
      this.deathTime++;
      if (this.deathTime == 20) {
         if (!this.worldObj.isRemote && (this.recentlyHit > 0 || this.isPlayer()) && this.canDropLoot() && this.worldObj.getGameRules().getBoolean("doMobLoot")
            )
          {
            int var4 = this.getExperiencePoints(this.attackingPlayer);

            while (var4 > 0) {
               int var5 = EntityXPOrb.getXPSplit(var4);
               var4 -= var5;
               this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, var5));
            }
         }

         this.setDead();

         for (int var11 = 0; var11 < 20; var11++) {
            double var12 = this.rand.nextGaussian() * 0.02;
            double var7 = this.rand.nextGaussian() * 0.02;
            double var9 = this.rand.nextGaussian() * 0.02;
            this.worldObj
               .spawnParticle(
                  EnumParticleTypes.EXPLOSION_NORMAL,
                  this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width,
                  this.posY + this.rand.nextFloat() * this.height,
                  this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width,
                  var12,
                  var7,
                  var9
               );
         }
      }
   }

   public boolean canDropLoot() {
      return !this.isChild();
   }

   public int decreaseAirSupply(int var1) {
      int var5 = EnchantmentHelper.getRespiration(this);
      return var5 > 0 && this.rand.nextInt(var5 + 1) > 0 ? var1 : var1 - 1;
   }

   public int getExperiencePoints(EntityPlayer var1) {
      return 0;
   }

   public boolean isPlayer() {
      return false;
   }

   public Random getRNG() {
      return this.rand;
   }

   public EntityLivingBase getAITarget() {
      return this.entityLivingToAttack;
   }

   public int getRevengeTimer() {
      return this.revengeTimer;
   }

   public void setRevengeTarget(EntityLivingBase var1) {
      this.entityLivingToAttack = var1;
      this.revengeTimer = this.ticksExisted;
   }

   public EntityLivingBase getLastAttacker() {
      return this.lastAttacker;
   }

   public int getLastAttackerTime() {
      return this.lastAttackerTime;
   }

   public void setLastAttacker(Entity var1) {
      if (var1 instanceof EntityLivingBase) {
         this.lastAttacker = (EntityLivingBase)var1;
      } else {
         this.lastAttacker = null;
      }

      this.lastAttackerTime = this.ticksExisted;
   }

   public int s() {
      return this.entityAge;
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound var1) {
      var1.setFloat("HealF", this.getHealth());
      var1.setShort("Health", (short)Math.ceil(this.getHealth()));
      var1.setShort("HurtTime", (short)this.hurtTime);
      var1.setInteger("HurtByTimestamp", this.revengeTimer);
      var1.setShort("DeathTime", (short)this.deathTime);
      var1.setFloat("AbsorptionAmount", this.getAbsorptionAmount());

      for (ItemStack var8 : this.getInventory()) {
         if (var8 != null) {
            this.attributeMap.removeAttributeModifiers(var8.getAttributeModifiers());
         }
      }

      var1.setTag("Attributes", SharedMonsterAttributes.writeBaseAttributeMapToNBT(this.getAttributeMap()));

      for (ItemStack var15 : this.getInventory()) {
         if (var15 != null) {
            this.attributeMap.applyAttributeModifiers(var15.getAttributeModifiers());
         }
      }

      if (!this.activePotionsMap.isEmpty()) {
         NBTTagList var10 = new NBTTagList();

         for (PotionEffect var14 : this.activePotionsMap.values()) {
            var10.appendTag(var14.writeCustomPotionEffectToNBT(new NBTTagCompound()));
         }

         var1.setTag("ActiveEffects", var10);
      }
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound var1) {
      this.setAbsorptionAmount(var1.getFloat("AbsorptionAmount"));
      if (var1.hasKey("Attributes", 9) && this.worldObj != null && !this.worldObj.isRemote) {
         SharedMonsterAttributes.setAttributeModifiers(this.getAttributeMap(), var1.getTagList("Attributes", 10));
      }

      if (var1.hasKey("ActiveEffects", 9)) {
         NBTTagList var5 = var1.getTagList("ActiveEffects", 10);

         for (int var6 = 0; var6 < var5.tagCount(); var6++) {
            NBTTagCompound var7 = var5.getCompoundTagAt(var6);
            PotionEffect var8 = PotionEffect.readCustomPotionEffectFromNBT(var7);
            if (var8 != null) {
               this.activePotionsMap.put(var8.getPotionID(), var8);
            }
         }
      }

      if (var1.hasKey("HealF", 99)) {
         this.setHealth(var1.getFloat("HealF"));
      } else {
         NBTBase var9 = var1.getTag("Health");
         if (var9 == null) {
            this.setHealth(this.getMaxHealth());
         } else if (var9.getId() == 5) {
            this.setHealth(((NBTTagFloat)var9).getFloat());
         } else if (var9.getId() == 2) {
            this.setHealth(((NBTTagShort)var9).getShort());
         }
      }

      this.hurtTime = var1.getShort("HurtTime");
      this.deathTime = var1.getShort("DeathTime");
      this.revengeTimer = var1.getInteger("HurtByTimestamp");
   }

   public void updatePotionEffects() {
      Iterator var4 = this.activePotionsMap.keySet().iterator();

      while (var4.hasNext()) {
         Integer var5 = (Integer)var4.next();
         PotionEffect var6 = this.activePotionsMap.get(var5);
         if (!var6.onUpdate(this)) {
            if (!this.worldObj.isRemote) {
               var4.remove();
               this.onFinishedPotionEffect(var6);
            }
         } else if (var6.getDuration() % 600 == 0) {
            this.onChangedPotionEffect(var6, false);
         }
      }

      if (this.potionsNeedUpdate) {
         if (!this.worldObj.isRemote) {
            this.updatePotionMetadata();
         }

         this.potionsNeedUpdate = false;
      }

      int var14 = this.dataWatcher.getWatchableObjectInt(7);
      boolean var15 = this.dataWatcher.getWatchableObjectByte(8) > 0;
      if (var14 > 0) {
         boolean var7 = false;
         if (!this.isInvisible()) {
            var7 = this.rand.nextBoolean();
         } else {
            var7 = this.rand.nextInt(15) == 0;
         }

         if (var15) {
            var7 &= this.rand.nextInt(5) == 0;
         }

         if (var7 && var14 > 0) {
            double var8 = (var14 >> 16 & 0xFF) / 255.0;
            double var10 = (var14 >> 8 & 0xFF) / 255.0;
            double var12 = (var14 >> 0 & 0xFF) / 255.0;
            this.worldObj
               .spawnParticle(
                  var15 ? EnumParticleTypes.SPELL_MOB_AMBIENT : EnumParticleTypes.SPELL_MOB,
                  this.posX + (this.rand.nextDouble() - 0.5) * this.width,
                  this.posY + this.rand.nextDouble() * this.height,
                  this.posZ + (this.rand.nextDouble() - 0.5) * this.width,
                  var8,
                  var10,
                  var12
               );
         }
      }
   }

   public void updatePotionMetadata() {
      if (this.activePotionsMap.isEmpty()) {
         this.resetPotionEffectMetadata();
         this.setInvisible(false);
      } else {
         int var4 = PotionHelper.calcPotionLiquidColor(this.activePotionsMap.values());
         this.dataWatcher.updateObject(8, (byte)(PotionHelper.getAreAmbient(this.activePotionsMap.values()) ? 1 : 0));
         this.dataWatcher.updateObject(7, var4);
         this.setInvisible(this.isPotionActive(Potion.invisibility.id));
      }
   }

   public void resetPotionEffectMetadata() {
      this.dataWatcher.updateObject(8, (byte)0);
      this.dataWatcher.updateObject(7, 0);
   }

   public void clearActivePotions() {
      Iterator var4 = this.activePotionsMap.keySet().iterator();

      while (var4.hasNext()) {
         Integer var5 = (Integer)var4.next();
         PotionEffect var6 = this.activePotionsMap.get(var5);
         if (!this.worldObj.isRemote) {
            var4.remove();
            this.onFinishedPotionEffect(var6);
         }
      }
   }

   public Collection<PotionEffect> getActivePotionEffects() {
      return this.activePotionsMap.values();
   }

   public boolean isPotionActive(int var1) {
      return this.activePotionsMap.containsKey(var1);
   }

   public boolean isPotionActive(Potion var1) {
      return this.activePotionsMap.containsKey(var1.id);
   }

   public PotionEffect getActivePotionEffect(Potion var1) {
      return this.activePotionsMap.get(var1.id);
   }

   public void addPotionEffect(PotionEffect var1) {
      if (this.isPotionApplicable(var1)) {
         if (this.activePotionsMap.containsKey(var1.getPotionID())) {
            this.activePotionsMap.get(var1.getPotionID()).combine(var1);
            this.onChangedPotionEffect(this.activePotionsMap.get(var1.getPotionID()), true);
         } else {
            this.activePotionsMap.put(var1.getPotionID(), var1);
            this.onNewPotionEffect(var1);
         }
      }
   }

   public boolean isPotionApplicable(PotionEffect var1) {
      if (this.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD) {
         int var5 = var1.getPotionID();
         if (var5 == Potion.regeneration.id || var5 == Potion.poison.id) {
            return false;
         }
      }

      return true;
   }

   public boolean V() {
      return this.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD;
   }

   public void removePotionEffectClient(int var1) {
      this.activePotionsMap.remove(var1);
   }

   public void removePotionEffect(int var1) {
      PotionEffect var4 = this.activePotionsMap.remove(var1);
      if (var4 != null) {
         this.onFinishedPotionEffect(var4);
      }
   }

   public void onNewPotionEffect(PotionEffect var1) {
      this.potionsNeedUpdate = true;
      if (!this.worldObj.isRemote) {
         Potion.potionTypes[var1.getPotionID()].applyAttributesModifiersToEntity(this, this.getAttributeMap(), var1.getAmplifier());
      }
   }

   public void onChangedPotionEffect(PotionEffect var1, boolean var2) {
      this.potionsNeedUpdate = true;
      if (var2 && !this.worldObj.isRemote) {
         Potion.potionTypes[var1.getPotionID()].removeAttributesModifiersFromEntity(this, this.getAttributeMap(), var1.getAmplifier());
         Potion.potionTypes[var1.getPotionID()].applyAttributesModifiersToEntity(this, this.getAttributeMap(), var1.getAmplifier());
      }
   }

   public void onFinishedPotionEffect(PotionEffect var1) {
      this.potionsNeedUpdate = true;
      if (!this.worldObj.isRemote) {
         Potion.potionTypes[var1.getPotionID()].removeAttributesModifiersFromEntity(this, this.getAttributeMap(), var1.getAmplifier());
      }
   }

   public void heal(float var1) {
      float var4 = this.getHealth();
      if (var4 > 0.0F) {
         this.setHealth(var4 + var1);
      }
   }

   public final float getHealth() {
      return this.dataWatcher.getWatchableObjectFloat(6);
   }

   public void setHealth(float var1) {
      this.dataWatcher.updateObject(6, MathHelper.clamp_float(var1, 0.0F, this.getMaxHealth()));
   }

   @Override
   public boolean attackEntityFrom(DamageSource var1, float var2) {
      if (this.isEntityInvulnerable(var1)) {
         return false;
      }

      if (this.worldObj.isRemote) {
         return false;
      }

      this.entityAge = 0;
      if (this.getHealth() <= 0.0F) {
         return false;
      }

      if (var1.isFireDamage() && this.isPotionActive(Potion.fireResistance)) {
         return false;
      }

      if ((var1 == DamageSource.anvil || var1 == DamageSource.fallingBlock) && this.getEquipmentInSlot(4) != null) {
         this.getEquipmentInSlot(4).damageItem((int)(var2 * 4.0F + this.rand.nextFloat() * var2 * 2.0F), this);
         var2 *= 0.75F;
      }

      this.limbSwingAmount = 1.5F;
      boolean var6 = true;
      if (this.hurtResistantTime > this.maxHurtResistantTime / 2.0F) {
         if (var2 <= this.lastDamage) {
            return false;
         }

         this.damageEntity(var1, var2 - this.lastDamage);
         this.lastDamage = var2;
         var6 = false;
      } else {
         this.lastDamage = var2;
         this.hurtResistantTime = this.maxHurtResistantTime;
         this.damageEntity(var1, var2);
         this.hurtTime = this.maxHurtTime = 10;
      }

      this.attackedAtYaw = 0.0F;
      Entity var7 = var1.getEntity();
      if (var7 != null) {
         if (var7 instanceof EntityLivingBase) {
            this.setRevengeTarget((EntityLivingBase)var7);
         }

         if (var7 instanceof EntityPlayer) {
            this.recentlyHit = 100;
            this.attackingPlayer = (EntityPlayer)var7;
         } else if (var7 instanceof EntityWolf) {
            EntityWolf var8 = (EntityWolf)var7;
            if (var8.isTamed()) {
               this.recentlyHit = 100;
               this.attackingPlayer = null;
            }
         }
      }

      if (var6) {
         this.worldObj.setEntityState(this, (byte)2);
         if (var1 != DamageSource.drown) {
            this.setBeenAttacked();
         }

         if (var7 != null) {
            double var12 = var7.posX - this.posX;

            double var10;
            for (var10 = var7.posZ - this.posZ; var12 * var12 + var10 * var10 < 1.0E-4; var10 = (Math.random() - Math.random()) * 0.01) {
               var12 = (Math.random() - Math.random()) * 0.01;
            }

            this.attackedAtYaw = (float)(MathHelper.atan2(var10, var12) * 180.0 / Math.PI - this.rotationYaw);
            this.knockBack(var7, var2, var12, var10);
         } else {
            this.attackedAtYaw = (int)(Math.random() * 2.0) * 180;
         }
      }

      if (this.getHealth() <= 0.0F) {
         String var13 = this.getDeathSound();
         if (var6 && var13 != null) {
            this.playSound(var13, this.getSoundVolume(), this.getSoundPitch());
         }

         this.onDeath(var1);
      } else {
         String var14 = this.getHurtSound();
         if (var6 && var14 != null) {
            this.playSound(var14, this.getSoundVolume(), this.getSoundPitch());
         }
      }

      return true;
   }

   public void renderBrokenItemStack(ItemStack var1) {
      this.playSound("random.break", 0.8F, 0.8F + this.worldObj.rand.nextFloat() * 0.4F);

      for (int var5 = 0; var5 < 5; var5++) {
         Vec3 var6 = new Vec3((this.rand.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
         var6 = var6.rotatePitch(-this.rotationPitch * (float) Math.PI / 180.0F);
         var6 = var6.rotateYaw(-this.rotationYaw * (float) Math.PI / 180.0F);
         double var7 = -this.rand.nextFloat() * 0.6 - 0.3;
         Vec3 var9 = new Vec3((this.rand.nextFloat() - 0.5) * 0.3, var7, 0.6);
         var9 = var9.rotatePitch(-this.rotationPitch * (float) Math.PI / 180.0F);
         var9 = var9.rotateYaw(-this.rotationYaw * (float) Math.PI / 180.0F);
         var9 = var9.addVector(this.posX, this.posY + this.getEyeHeight(), this.posZ);
         this.worldObj
            .spawnParticle(
               EnumParticleTypes.ITEM_CRACK,
               var9.xCoord,
               var9.yCoord,
               var9.zCoord,
               var6.xCoord,
               var6.yCoord + 0.05,
               var6.zCoord,
               Item.getIdFromItem(var1.getItem())
            );
      }
   }

   public void onDeath(DamageSource var1) {
      Entity var5 = var1.getEntity();
      EntityLivingBase var6 = this.getAttackingEntity();
      if (this.scoreValue >= 0 && var6 != null) {
         var6.addToPlayerScore(this, this.scoreValue);
      }

      if (var5 != null) {
         var5.onKillEntity(this);
      }

      this.dead = true;
      this.getCombatTracker().reset();
      if (!this.worldObj.isRemote) {
         int var7 = 0;
         if (var5 instanceof EntityPlayer) {
            var7 = EnchantmentHelper.getLootingModifier((EntityLivingBase)var5);
         }

         if (this.canDropLoot() && this.worldObj.getGameRules().getBoolean("doMobLoot")) {
            this.dropFewItems(this.recentlyHit > 0, var7);
            this.dropEquipment(this.recentlyHit > 0, var7);
            if (this.recentlyHit > 0 && this.rand.nextFloat() < 0.025F + var7 * 0.01F) {
               this.addRandomDrop();
            }
         }
      }

      this.worldObj.setEntityState(this, (byte)3);
   }

   public void dropEquipment(boolean var1, int var2) {
   }

   public void knockBack(Entity var1, float var2, double var3, double var5) {
      if (this.rand.nextDouble() >= this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).getAttributeValue()) {
         this.isAirBorne = true;
         float var10 = MathHelper.sqrt_double(var3 * var3 + var5 * var5);
         float var11 = 0.4F;
         this.motionX /= 2.0;
         this.motionY /= 2.0;
         this.motionZ /= 2.0;
         this.motionX -= var3 / var10 * var11;
         this.motionY += var11;
         this.motionZ -= var5 / var10 * var11;
         if (this.motionY > 0.4F) {
            this.motionY = 0.4F;
         }
      }
   }

   public String getHurtSound() {
      return "game.neutral.hurt";
   }

   public String getDeathSound() {
      return "game.neutral.die";
   }

   public void addRandomDrop() {
   }

   public void dropFewItems(boolean var1, int var2) {
   }

   public boolean isOnLadder() {
      int var4 = MathHelper.floor_double(this.posX);
      int var5 = MathHelper.floor_double(this.getEntityBoundingBox().minY);
      int var6 = MathHelper.floor_double(this.posZ);
      Block var7 = this.worldObj.getBlockState(new BlockPos(var4, var5, var6)).getBlock();
      return (var7 == Blocks.ladder || var7 == Blocks.vine) && (!(this instanceof EntityPlayer) || !((EntityPlayer)this).isSpectator());
   }

   @Override
   public boolean isEntityAlive() {
      return !this.isDead && this.getHealth() > 0.0F;
   }

   @Override
   public void fall(float var1, float var2) {
      super.fall(var1, var2);
      PotionEffect var6 = this.getActivePotionEffect(Potion.jump);
      float var7 = var6 != null ? var6.getAmplifier() + 1 : 0.0F;
      int var8 = MathHelper.ceiling_float_int((var1 - 3.0F - var7) * var2);
      if (var8 > 0) {
         this.playSound(this.getFallSoundString(var8), 1.0F, 1.0F);
         this.attackEntityFrom(DamageSource.fall, var8);
         int var9 = MathHelper.floor_double(this.posX);
         int var10 = MathHelper.floor_double(this.posY - 0.2F);
         int var11 = MathHelper.floor_double(this.posZ);
         Block var12 = this.worldObj.getBlockState(new BlockPos(var9, var10, var11)).getBlock();
         if (var12.getMaterial() != Material.air) {
            Block.SoundType var13 = var12.stepSound;
            this.playSound(var13.getStepSound(), var13.getVolume() * 0.5F, var13.getFrequency() * 0.75F);
         }
      }
   }

   public String getFallSoundString(int var1) {
      return var1 > 4 ? "game.neutral.hurt.fall.big" : "game.neutral.hurt.fall.small";
   }

   @Override
   public void performHurtAnimation() {
      this.hurtTime = this.maxHurtTime = 10;
      this.attackedAtYaw = 0.0F;
   }

   public int getTotalArmorValue() {
      int var4 = 0;

      for (ItemStack var8 : this.getInventory()) {
         if (var8 != null && var8.getItem() instanceof ItemArmor) {
            int var9 = ((ItemArmor)var8.getItem()).damageReduceAmount;
            var4 += var9;
         }
      }

      return var4;
   }

   public void damageArmor(float var1) {
   }

   public float applyArmorCalculations(DamageSource var1, float var2) {
      if (!var1.isUnblockable()) {
         int var6 = 25 - this.getTotalArmorValue();
         float var7 = var2 * var6;
         this.damageArmor(var2);
         var2 = var7 / 25.0F;
      }

      return var2;
   }

   public float applyPotionDamageCalculations(DamageSource var1, float var2) {
      if (var1.isDamageAbsolute()) {
         return var2;
      }

      if (this.isPotionActive(Potion.resistance) && var1 != DamageSource.outOfWorld) {
         int var6 = (this.getActivePotionEffect(Potion.resistance).getAmplifier() + 1) * 5;
         int var7 = 25 - var6;
         float var8 = var2 * var7;
         var2 = var8 / 25.0F;
      }

      if (var2 <= 0.0F) {
         return 0.0F;
      }

      int var9 = EnchantmentHelper.getEnchantmentModifierDamage(this.getInventory(), var1);
      if (var9 > 20) {
         var9 = 20;
      }

      if (var9 > 0 && var9 <= 20) {
         int var10 = 25 - var9;
         float var11 = var2 * var10;
         var2 = var11 / 25.0F;
      }

      return var2;
   }

   public void damageEntity(DamageSource var1, float var2) {
      if (!this.isEntityInvulnerable(var1)) {
         var2 = this.applyArmorCalculations(var1, var2);
         var2 = this.applyPotionDamageCalculations(var1, var2);
         float var6 = var2;
         var2 = Math.max(var2 - this.getAbsorptionAmount(), 0.0F);
         this.setAbsorptionAmount(this.getAbsorptionAmount() - (var6 - var2));
         if (var2 != 0.0F) {
            float var7 = this.getHealth();
            this.setHealth(var7 - var2);
            this.getCombatTracker().trackDamage(var1, var7, var2);
            this.setAbsorptionAmount(this.getAbsorptionAmount() - var2);
         }
      }
   }

   public CombatTracker getCombatTracker() {
      return this._combatTracker;
   }

   public EntityLivingBase getAttackingEntity() {
      return this._combatTracker.func_94550_c() != null
         ? this._combatTracker.func_94550_c()
         : (this.attackingPlayer != null ? this.attackingPlayer : (this.entityLivingToAttack != null ? this.entityLivingToAttack : null));
   }

   public final float getMaxHealth() {
      return (float)this.getEntityAttribute(SharedMonsterAttributes.maxHealth).getAttributeValue();
   }

   public final int getArrowCountInEntity() {
      return this.dataWatcher.getWatchableObjectByte(9);
   }

   public final void setArrowCountInEntity(int var1) {
      this.dataWatcher.updateObject(9, (byte)var1);
   }

   private int getArmSwingAnimationEnd() {
      long var1 = fb ^ 30317504845315L;
      long var3 = var1 ^ 124876313361189L;
      if (this instanceof EntityPlayerSP && Astolfo.INSTANCE.getModuleManager().get(Animations.class).isEnabled()) {
         return Animations.speed.getValue();
      } else {
         return this.isPotionActive(Potion.digSpeed)
            ? 6 - (1 + this.getActivePotionEffect(Potion.digSpeed).getAmplifier()) * 1
            : (this.isPotionActive(Potion.digSlowdown) ? 6 + (1 + this.getActivePotionEffect(Potion.digSlowdown).getAmplifier()) * 2 : 6);
      }
   }

   public void swingItem() {
      if (!this.Lp || this.swingProgressInt >= this.getArmSwingAnimationEnd() / 2 || this.swingProgressInt < 0) {
         this.swingProgressInt = -1;
         this.Lp = true;
         if (this.worldObj instanceof WorldServer) {
            ((WorldServer)this.worldObj).getEntityTracker().sendToAllTrackingEntity(this, new S0BPacketAnimation(this, 0));
         }
      }
   }

   @Override
   public void handleStatusUpdate(byte var1) {
      if (var1 == 2) {
         this.limbSwingAmount = 1.5F;
         this.hurtResistantTime = this.maxHurtResistantTime;
         this.hurtTime = this.maxHurtTime = 10;
         this.attackedAtYaw = 0.0F;
         String var5 = this.getHurtSound();
         if (var5 != null) {
            this.playSound(this.getHurtSound(), this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
         }

         this.attackEntityFrom(DamageSource.generic, 0.0F);
      } else if (var1 == 3) {
         String var6 = this.getDeathSound();
         if (var6 != null) {
            this.playSound(this.getDeathSound(), this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
         }

         this.setHealth(0.0F);
         this.onDeath(DamageSource.generic);
      } else {
         super.handleStatusUpdate(var1);
      }
   }

   @Override
   public void kill() {
      this.attackEntityFrom(DamageSource.outOfWorld, 4.0F);
   }

   public void L() {
      int var4 = this.getArmSwingAnimationEnd();
      if (this.Lp) {
         this.swingProgressInt++;
         if (this.swingProgressInt >= var4) {
            this.swingProgressInt = 0;
            this.Lp = false;
         }
      } else {
         this.swingProgressInt = 0;
      }

      this.swingProgress = (float)this.swingProgressInt / var4;
   }

   public IAttributeInstance getEntityAttribute(IAttribute var1) {
      return this.getAttributeMap().getAttributeInstance(var1);
   }

   public BaseAttributeMap getAttributeMap() {
      if (this.attributeMap == null) {
         this.attributeMap = new ServersideAttributeMap();
      }

      return this.attributeMap;
   }

   public EnumCreatureAttribute getCreatureAttribute() {
      return EnumCreatureAttribute.UNDEFINED;
   }

   public abstract ItemStack getHeldItem();

   public abstract ItemStack getEquipmentInSlot(int var1);

   public abstract ItemStack getCurrentArmor(int var1);

   @Override
   public abstract void setCurrentItemOrArmor(int var1, ItemStack var2);

   @Override
   public void setSprinting(boolean var1) {
      super.setSprinting(var1);
      IAttributeInstance var5 = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
      if (var5.getModifier(sprintingSpeedBoostModifierUUID) != null) {
         var5.removeModifier(sprintingSpeedBoostModifier);
      }

      if (var1) {
         var5.applyModifier(sprintingSpeedBoostModifier);
      }
   }

   @Override
   public abstract ItemStack[] getInventory();

   public float getSoundVolume() {
      return 1.0F;
   }

   public float getSoundPitch() {
      return this.isChild() ? (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.5F : (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F;
   }

   public boolean isMovementBlocked() {
      return this.getHealth() <= 0.0F;
   }

   public void dismountEntity(Entity var1) {
      double var5 = var1.posX;
      double var7 = var1.getEntityBoundingBox().minY + var1.height;
      double var9 = var1.posZ;
      byte var11 = 1;

      for (int var12 = -var11; var12 <= var11; var12++) {
         for (int var13 = -var11; var13 < var11; var13++) {
            if (var12 != 0 || var13 != 0) {
               int var14 = (int)(this.posX + var12);
               int var15 = (int)(this.posZ + var13);
               AxisAlignedBB var16 = this.getEntityBoundingBox().offset(var12, 1.0, var13);
               if (this.worldObj.getCollisionBoxes(var16).isEmpty()) {
                  if (World.doesBlockHaveSolidTopSurface(this.worldObj, new BlockPos(var14, (int)this.posY, var15))) {
                     this.setPositionAndUpdate(this.posX + var12, this.posY + 1.0, this.posZ + var13);
                     return;
                  }

                  if (World.doesBlockHaveSolidTopSurface(this.worldObj, new BlockPos(var14, (int)this.posY - 1, var15))
                     || this.worldObj.getBlockState(new BlockPos(var14, (int)this.posY - 1, var15)).getBlock().getMaterial() == Material.water) {
                     var5 = this.posX + var12;
                     var7 = this.posY + 1.0;
                     var9 = this.posZ + var13;
                  }
               }
            }
         }
      }

      this.setPositionAndUpdate(var5, var7, var9);
   }

   @Override
   public boolean getAlwaysRenderNameTagForRender() {
      return false;
   }

   public float getJumpUpwardsMotion() {
      return 0.42F;
   }

   public void jump() {
      long var1 = fb ^ 107812693659344L;
      long var3 = var1 ^ 8863891448822L;
      this.motionY = this.getJumpUpwardsMotion();
      if (this.isPotionActive(Potion.jump)) {
         this.motionY = this.motionY + (this.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F;
      }

      if (this.isSprinting()) {
         float var6 = this.rotationYaw * (float) (Math.PI / 180.0);
         this.motionX = this.motionX - MathHelper.sin(var6) * 0.2F;
         this.motionZ = this.motionZ + MathHelper.cos(var6) * 0.2F;
      }

      this.isAirBorne = true;
      if (this.isSprinting()) {
         float var9 = (float)Math.toRadians(this.rotationYaw);
         boolean var7 = Astolfo.INSTANCE.getModuleManager().get(Sprint.class).isEnabled();
         if (var7 && Sprint.omnidirectional.getValue()) {
            this.motionX = this.motionX + MathHelper.sin(var9) * 0.2F;
            this.motionZ = this.motionZ - MathHelper.cos(var9) * 0.2F;
            float var8 = this.rotationYaw;
            if (this.moveForward < 0.0F) {
               var8 += 180.0F;
            }

            if (this.moveStrafing > 0.0F) {
               var8 -= 90.0F * (this.moveForward > 0.0F ? 0.5F : (this.moveForward < 0.0F ? -0.5F : 1.0F));
            }

            if (this.moveStrafing < 0.0F) {
               var8 += 90.0F * (this.moveForward > 0.0F ? 0.5F : (this.moveForward < 0.0F ? -0.5F : 1.0F));
            }

            var9 = (float)Math.toRadians(var8);
            this.motionX = this.motionX - MathHelper.sin(var9) * 0.2F;
            this.motionZ = this.motionZ + MathHelper.cos(var9) * 0.2F;
         }
      }
   }

   public void R() {
      this.motionY += 0.04F;
   }

   public void handleJumpLava() {
      this.motionY += 0.04F;
   }

   public void moveEntityWithHeading(float var1, float var2) {
      long var3 = fb ^ 62942906234037L;
      long var5 = var3 ^ 91529406716307L;
      if (this.C()) {
         if (!this.isInWater() || this instanceof EntityPlayer && ((EntityPlayer)this).capabilities.isFlying) {
            if (!this.isInLava() || this instanceof EntityPlayer && ((EntityPlayer)this).capabilities.isFlying) {
               float var14 = 0.91F;
               if (this.onGround) {
                  var14 = this.worldObj
                        .getBlockState(
                           new BlockPos(
                              MathHelper.floor_double(this.posX),
                              MathHelper.floor_double(this.getEntityBoundingBox().minY) - 1,
                              MathHelper.floor_double(this.posZ)
                           )
                        )
                        .getBlock()
                        .slipperiness
                     * 0.91F;
               }

               float var9 = 0.16277136F / (var14 * var14 * var14);
               float var17;
               if (this.onGround) {
                  var17 = this.getAIMoveSpeed() * var9;
               } else {
                  var17 = this.jumpMovementFactor;
               }

               this.moveFlying(var1, var2, var17);
               var14 = 0.91F;
               if (this.onGround) {
                  var14 = this.worldObj
                        .getBlockState(
                           new BlockPos(
                              MathHelper.floor_double(this.posX),
                              MathHelper.floor_double(this.getEntityBoundingBox().minY) - 1,
                              MathHelper.floor_double(this.posZ)
                           )
                        )
                        .getBlock()
                        .slipperiness
                     * 0.91F;
               }

               if (this.isOnLadder()) {
                  float var19 = 0.15F;
                  if (!Astolfo.INSTANCE.getModuleManager().get(NoSlow.class).isEnabled() || !NoSlow.ladderFix.getValue()) {
                     this.motionX = MathHelper.clamp_double(this.motionX, -var19, var19);
                     this.motionZ = MathHelper.clamp_double(this.motionZ, -var19, var19);
                  }

                  this.fallDistance = 0.0F;
                  if (this.motionY < -0.15) {
                     this.motionY = -0.15;
                  }

                  boolean var20 = this.isSneaking() && this instanceof EntityPlayer;
                  if (var20 && this.motionY < 0.0) {
                     this.motionY = 0.0;
                  }
               }

               this.moveEntity(this.motionX, this.motionY, this.motionZ);
               if (this.isCollidedHorizontally && this.isOnLadder()) {
                  this.motionY = 0.2;
               }

               if (this.worldObj.isRemote
                  && (
                     !this.worldObj.isBlockLoaded(new BlockPos((int)this.posX, 0, (int)this.posZ))
                        || !this.worldObj.getChunkFromBlockCoords(new BlockPos((int)this.posX, 0, (int)this.posZ)).isLoaded()
                  )) {
                  if (this.posY > 0.0) {
                     this.motionY = -0.1;
                  } else {
                     this.motionY = 0.0;
                  }
               } else {
                  this.motionY -= 0.08;
               }

               this.motionY *= 0.98F;
               this.motionX *= var14;
               this.motionZ *= var14;
            } else {
               double var13 = this.posY;
               this.moveFlying(var1, var2, 0.02F);
               this.moveEntity(this.motionX, this.motionY, this.motionZ);
               this.motionX *= 0.5;
               this.motionY *= 0.5;
               this.motionZ *= 0.5;
               this.motionY -= 0.02;
               if (this.isCollidedHorizontally && this.isOffsetPositionInLiquid(this.motionX, this.motionY + 0.6F - this.posY + var13, this.motionZ)) {
                  this.motionY = 0.3F;
               }
            }
         } else {
            double var8 = this.posY;
            float var10 = 0.8F;
            float var11 = 0.02F;
            float var12 = EnchantmentHelper.getDepthStriderModifier(this);
            if (var12 > 3.0F) {
               var12 = 3.0F;
            }

            if (!this.onGround) {
               var12 *= 0.5F;
            }

            if (var12 > 0.0F) {
               var10 += (0.54600006F - var10) * var12 / 3.0F;
               var11 += (this.getAIMoveSpeed() * 1.0F - var11) * var12 / 3.0F;
            }

            this.moveFlying(var1, var2, var11);
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= var10;
            this.motionY *= 0.8F;
            this.motionZ *= var10;
            this.motionY -= 0.02;
            if (this.isCollidedHorizontally && this.isOffsetPositionInLiquid(this.motionX, this.motionY + 0.6F - this.posY + var8, this.motionZ)) {
               this.motionY = 0.3F;
            }
         }
      }

      this.prevLimbSwingAmount = this.limbSwingAmount;
      double var16 = this.posX - this.prevPosX;
      double var18 = this.posZ - this.prevPosZ;
      float var21 = MathHelper.sqrt_double(var16 * var16 + var18 * var18) * 4.0F;
      if (var21 > 1.0F) {
         var21 = 1.0F;
      }

      this.limbSwingAmount = this.limbSwingAmount + (var21 - this.limbSwingAmount) * 0.4F;
      this.limbSwing = this.limbSwing + this.limbSwingAmount;
   }

   public float getAIMoveSpeed() {
      return this.landMovementFactor;
   }

   public void setAIMoveSpeed(float var1) {
      this.landMovementFactor = var1;
   }

   public boolean attackEntityAsMob(Entity var1) {
      this.setLastAttacker(var1);
      return false;
   }

   public boolean isPlayerSleeping() {
      return false;
   }

   @Override
   public void onUpdate() {
      super.onUpdate();
      if (!this.worldObj.isRemote) {
         int var4 = this.getArrowCountInEntity();
         if (var4 > 0) {
            if (this.arrowHitTimer <= 0) {
               this.arrowHitTimer = 20 * (30 - var4);
            }

            this.arrowHitTimer--;
            if (this.arrowHitTimer <= 0) {
               this.setArrowCountInEntity(var4 - 1);
            }
         }

         for (int var5 = 0; var5 < 5; var5++) {
            ItemStack var6 = this.previousEquipment[var5];
            ItemStack var7 = this.getEquipmentInSlot(var5);
            if (!ItemStack.areItemStacksEqual(var7, var6)) {
               ((WorldServer)this.worldObj).getEntityTracker().sendToAllTrackingEntity(this, new S04PacketEntityEquipment(this.getEntityId(), var5, var7));
               if (var6 != null) {
                  this.attributeMap.removeAttributeModifiers(var6.getAttributeModifiers());
               }

               if (var7 != null) {
                  this.attributeMap.applyAttributeModifiers(var7.getAttributeModifiers());
               }

               this.previousEquipment[var5] = var7 == null ? null : var7.copy();
            }
         }

         if (this.ticksExisted % 20 == 0) {
            this.getCombatTracker().reset();
         }
      }

      this.onLivingUpdate();
      double var12 = this.posX - this.prevPosX;
      double var13 = this.posZ - this.prevPosZ;
      float var8 = (float)(var12 * var12 + var13 * var13);
      float var9 = this.renderYawOffset;
      float var10 = 0.0F;
      this.prevOnGroundSpeedFactor = this.onGroundSpeedFactor;
      float var11 = 0.0F;
      if (var8 > 0.0025000002F) {
         var11 = 1.0F;
         var10 = (float)Math.sqrt(var8) * 3.0F;
         var9 = (float)MathHelper.atan2(var13, var12) * 180.0F / (float) Math.PI - 90.0F;
      }

      if (this.swingProgress > 0.0F) {
         var9 = this.rotationYaw;
      }

      if (!this.onGround) {
         var11 = 0.0F;
      }

      this.onGroundSpeedFactor = this.onGroundSpeedFactor + (var11 - this.onGroundSpeedFactor) * 0.3F;
      this.worldObj.theProfiler.startSection("headTurn");
      var10 = this.updateDistance(var9, var10);
      this.worldObj.theProfiler.endSection();
      this.worldObj.theProfiler.startSection("rangeChecks");

      while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
         this.prevRotationYaw -= 360.0F;
      }

      while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
         this.prevRotationYaw += 360.0F;
      }

      while (this.renderYawOffset - this.prevRenderYawOffset < -180.0F) {
         this.prevRenderYawOffset -= 360.0F;
      }

      while (this.renderYawOffset - this.prevRenderYawOffset >= 180.0F) {
         this.prevRenderYawOffset += 360.0F;
      }

      while (this.rotationPitch - this.prevRotationPitch < -180.0F) {
         this.prevRotationPitch -= 360.0F;
      }

      while (this.rotationPitch - this.prevRotationPitch >= 180.0F) {
         this.prevRotationPitch += 360.0F;
      }

      while (this.rotationYawHead - this.prevRotationYawHead < -180.0F) {
         this.prevRotationYawHead -= 360.0F;
      }

      while (this.rotationYawHead - this.prevRotationYawHead >= 180.0F) {
         this.prevRotationYawHead += 360.0F;
      }

      this.worldObj.theProfiler.endSection();
      this.movedDistance += var10;
   }

   public float updateDistance(float var1, float var2) {
      float var6 = MathHelper.wrapAngleTo180_float(var1 - this.renderYawOffset);
      this.renderYawOffset += var6 * 0.3F;
      float var7 = this.rotationYaw;
      if (this instanceof EntityPlayerSP) {
         RotationYawEvent var8 = new RotationYawEvent(var7);
         Astolfo.INSTANCE.getEventBus().post(var8);
         var7 = var8.getYaw();
      }

      float var10 = MathHelper.wrapAngleTo180_float(var7 - this.renderYawOffset);
      boolean var9 = var10 < -90.0F || var10 >= 90.0F;
      if (var10 < -75.0F) {
         var10 = -75.0F;
      }

      if (var10 >= 75.0F) {
         var10 = 75.0F;
      }

      this.renderYawOffset = var7 - var10;
      if (var10 * var10 > 2500.0F) {
         this.renderYawOffset += var10 * 0.2F;
      }

      if (var9) {
         var2 *= -1.0F;
      }

      return var2;
   }

   public void onLivingUpdate() {
      if (this.jumpTicks > 0) {
         this.jumpTicks--;
      }

      if (this.newPosRotationIncrements > 0) {
         double var4 = this.posX + (this.newPosX - this.posX) / this.newPosRotationIncrements;
         double var6 = this.posY + (this.newPosY - this.posY) / this.newPosRotationIncrements;
         double var8 = this.posZ + (this.newPosZ - this.posZ) / this.newPosRotationIncrements;
         double var10 = MathHelper.wrapAngleTo180_double(this.newRotationYaw - this.rotationYaw);
         this.rotationYaw = (float)(this.rotationYaw + var10 / this.newPosRotationIncrements);
         this.rotationPitch = (float)(this.rotationPitch + (this.newRotationPitch - this.rotationPitch) / this.newPosRotationIncrements);
         this.newPosRotationIncrements--;
         this.setPosition(var4, var6, var8);
         this.setRotation(this.rotationYaw, this.rotationPitch);
      } else if (!this.C()) {
         this.motionX *= 0.98;
         this.motionY *= 0.98;
         this.motionZ *= 0.98;
      }

      if (Math.abs(this.motionX) < 0.005) {
         this.motionX = 0.0;
      }

      if (Math.abs(this.motionY) < 0.005) {
         this.motionY = 0.0;
      }

      if (Math.abs(this.motionZ) < 0.005) {
         this.motionZ = 0.0;
      }

      this.worldObj.theProfiler.startSection("ai");
      if (this.isMovementBlocked()) {
         this.Lr = false;
         this.moveStrafing = 0.0F;
         this.moveForward = 0.0F;
         this.randomYawVelocity = 0.0F;
      } else if (this.C()) {
         this.worldObj.theProfiler.startSection("newAi");
         this.updateEntityActionState();
         this.worldObj.theProfiler.endSection();
      }

      this.worldObj.theProfiler.endSection();
      this.worldObj.theProfiler.startSection("jump");
      if (this.Lr) {
         if (this.isInWater()) {
            this.R();
         } else if (this.isInLava()) {
            this.handleJumpLava();
         } else if (this.onGround && this.jumpTicks == 0) {
            this.jump();
            this.jumpTicks = 10;
         }
      } else {
         this.jumpTicks = 0;
      }

      this.worldObj.theProfiler.endSection();
      this.worldObj.theProfiler.startSection("travel");
      this.moveStrafing *= 0.98F;
      this.moveForward *= 0.98F;
      this.randomYawVelocity *= 0.9F;
      if (RiderGod.S() && this instanceof EntityPlayerSP && this.ridingEntity != null && this.ridingEntity instanceof EntityHorse) {
         EntityHorse var12 = (EntityHorse)this.ridingEntity;
         var12.O(this.moveStrafing, this.moveForward, this.randomYawVelocity);
      } else {
         this.moveEntityWithHeading(this.moveStrafing, this.moveForward);
      }

      this.worldObj.theProfiler.endSection();
      this.worldObj.theProfiler.startSection("push");
      if (!this.worldObj.isRemote) {
         this.collideWithNearbyEntities();
      }

      this.worldObj.theProfiler.endSection();
   }

   public void updateEntityActionState() {
   }

   public void collideWithNearbyEntities() {
      List var4 = this.worldObj
         .getEntitiesInAABBexcluding(
            this, this.getEntityBoundingBox().expand(0.2F, 0.0, 0.2F), Predicates.and(EntitySelectors.NOT_SPECTATING, new Predicate<Entity>() {
               public boolean apply(Entity var1) {
                  return var1.canBePushed();
               }
            })
         );
      if (!var4.isEmpty()) {
         for (int var5 = 0; var5 < var4.size(); var5++) {
            Entity var6 = (Entity)var4.get(var5);
            this.collideWithEntity(var6);
         }
      }
   }

   public void collideWithEntity(Entity var1) {
      var1.applyEntityCollision(this);
   }

   @Override
   public void mountEntity(Entity var1) {
      if (this.ridingEntity != null && var1 == null) {
         if (!this.worldObj.isRemote) {
            this.dismountEntity(this.ridingEntity);
         }

         if (this.ridingEntity != null) {
            this.ridingEntity.riddenByEntity = null;
         }

         this.ridingEntity = null;
      } else {
         super.mountEntity(var1);
      }
   }

   @Override
   public void updateRidden() {
      super.updateRidden();
      this.prevOnGroundSpeedFactor = this.onGroundSpeedFactor;
      this.onGroundSpeedFactor = 0.0F;
      this.fallDistance = 0.0F;
   }

   @Override
   public void setPositionAndRotation2(double var1, double var3, double var5, float var7, float var8, int var9, boolean var10) {
      this.newPosX = var1;
      this.newPosY = var3;
      this.newPosZ = var5;
      this.newRotationYaw = var7;
      this.newRotationPitch = var8;
      this.newPosRotationIncrements = var9;
   }

   public void j(boolean var1) {
      this.Lr = var1;
   }

   public void onItemPickup(Entity var1, int var2) {
      if (!var1.isDead && !this.worldObj.isRemote) {
         EntityTracker var6 = ((WorldServer)this.worldObj).getEntityTracker();
         if (var1 instanceof EntityItem) {
            var6.sendToAllTrackingEntity(var1, new S0DPacketCollectItem(var1.getEntityId(), this.getEntityId()));
         }

         if (var1 instanceof EntityArrow) {
            var6.sendToAllTrackingEntity(var1, new S0DPacketCollectItem(var1.getEntityId(), this.getEntityId()));
         }

         if (var1 instanceof EntityXPOrb) {
            var6.sendToAllTrackingEntity(var1, new S0DPacketCollectItem(var1.getEntityId(), this.getEntityId()));
         }
      }
   }

   public boolean C(Entity var1) {
      return this.worldObj
            .rayTraceBlocks(new Vec3(this.posX, this.posY + this.getEyeHeight(), this.posZ), new Vec3(var1.posX, var1.posY + var1.getEyeHeight(), var1.posZ))
         == null;
   }

   @Override
   public Vec3 getLookVec() {
      return this.getLook(1.0F);
   }

   @Override
   public Vec3 getLook(float var1) {
      if (this instanceof EntityPlayerSP) {
         return super.getLook(var1);
      }

      if (var1 == 1.0F) {
         return this.getVectorForRotation(this.rotationPitch, this.rotationYawHead);
      }

      float var5 = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * var1;
      float var6 = this.prevRotationYawHead + (this.rotationYawHead - this.prevRotationYawHead) * var1;
      return this.getVectorForRotation(var5, var6);
   }

   public float getSwingProgress(float var1) {
      float var5 = this.swingProgress - this.prevSwingProgress;
      if (var5 < 0.0F) {
         var5++;
      }

      return this.prevSwingProgress + var5 * var1;
   }

   public boolean C() {
      return !this.worldObj.isRemote;
   }

   @Override
   public boolean canBeCollidedWith() {
      return !this.isDead;
   }

   @Override
   public boolean canBePushed() {
      return !this.isDead;
   }

   @Override
   public void setBeenAttacked() {
      this.velocityChanged = this.rand.nextDouble() >= this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).getAttributeValue();
   }

   @Override
   public float getRotationYawHead() {
      return this.rotationYawHead;
   }

   @Override
   public void setRotationYawHead(float var1) {
      this.rotationYawHead = var1;
   }

   @Override
   public void setRenderYawOffset(float var1) {
      this.renderYawOffset = var1;
   }

   public float getAbsorptionAmount() {
      return this.absorptionAmount;
   }

   public void setAbsorptionAmount(float var1) {
      if (var1 < 0.0F) {
         var1 = 0.0F;
      }

      this.absorptionAmount = var1;
   }

   public Team getTeam() {
      return this.worldObj.getScoreboard().getPlayersTeam(this.getUniqueID().toString());
   }

   public boolean isOnSameTeam(EntityLivingBase var1) {
      return this.isOnTeam(var1.getTeam());
   }

   public boolean isOnTeam(Team var1) {
      return this.getTeam() != null ? this.getTeam().isSameTeam(var1) : false;
   }

   public void sendEnterCombat() {
   }

   public void sendEndCombat() {
   }

   public void l() {
      this.potionsNeedUpdate = true;
   }
}
