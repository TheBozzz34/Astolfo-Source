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

   @Override
   public void onKillCommand() {
      this.attackEntityFrom(DamageSource.outOfWorld, Float.MAX_VALUE);
   }

   public EntityLivingBase(World worldIn) {
      super(worldIn);
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
   public void updateFallState(double y, boolean onGroundIn, Block blockIn, BlockPos pos) {
      if (!this.isInWater()) {
         this.handleWaterMovement();
      }

      if (!this.worldObj.isRemote && this.fallDistance > 3.0F && onGroundIn) {
         IBlockState iBlockState = this.worldObj.getBlockState(pos);
         Block block = iBlockState.getBlock();
         float f = MathHelper.ceiling_float_int(this.fallDistance - 3.0F);
         if (block.getMaterial() != Material.air) {
            double d = Math.min(0.2F + f / 15.0F, 10.0F);
            if (d > 2.5) {
               d = 2.5;
            }

            int i = (int)(150.0 * d);
            ((WorldServer)this.worldObj)
               .spawnParticle(EnumParticleTypes.BLOCK_DUST, this.posX, this.posY, this.posZ, i, 0.0, 0.0, 0.0, 0.15F, Block.getStateId(iBlockState));
         }
      }

      super.updateFallState(y, onGroundIn, blockIn, pos);
   }

   public boolean canBreatheUnderwater() {
      return false;
   }

   @Override
   public void onEntityUpdate() {
      this.prevSwingProgress = this.swingProgress;
      super.onEntityUpdate();
      this.worldObj.theProfiler.startSection("livingEntityBaseTick");
      boolean bl = this instanceof EntityPlayer;
      if (this.isEntityAlive()) {
         if (this.isEntityInsideOpaqueBlock()) {
            this.attackEntityFrom(DamageSource.inWall, 1.0F);
         } else if (bl && !this.worldObj.getWorldBorder().contains(this.getEntityBoundingBox())) {
            double d = this.worldObj.getWorldBorder().getClosestDistance(this) + this.worldObj.getWorldBorder().getDamageBuffer();
            if (d < 0.0) {
               this.attackEntityFrom(DamageSource.inWall, Math.max(1, MathHelper.floor_double(-d * this.worldObj.getWorldBorder().getDamageAmount())));
            }
         }
      }

      if (this.isImmuneToFire() || this.worldObj.isRemote) {
         this.extinguish();
      }

      boolean bl2 = bl && ((EntityPlayer)this).capabilities.disableDamage;
      if (this.isEntityAlive()) {
         if (this.isInsideOfMaterial(Material.water)) {
            if (!this.canBreatheUnderwater() && !this.isPotionActive(Potion.waterBreathing.id) && !bl2) {
               this.setAir(this.decreaseAirSupply(this.getAir()));
               if (this.getAir() == -20) {
                  this.setAir(0);

                  for (int i = 0; i < 8; i++) {
                     float f = this.rand.nextFloat() - this.rand.nextFloat();
                     float g = this.rand.nextFloat() - this.rand.nextFloat();
                     float h = this.rand.nextFloat() - this.rand.nextFloat();
                     this.worldObj
                        .spawnParticle(
                           EnumParticleTypes.WATER_BUBBLE, this.posX + f, this.posY + g, this.posZ + h, this.motionX, this.motionY, this.motionZ
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
            int i = this.getExperiencePoints(this.attackingPlayer);

            while (i > 0) {
               int j = EntityXPOrb.getXPSplit(i);
               i -= j;
               this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, j));
            }
         }

         this.setDead();

         for (int k = 0; k < 20; k++) {
            double d = this.rand.nextGaussian() * 0.02;
            double e = this.rand.nextGaussian() * 0.02;
            double d2 = this.rand.nextGaussian() * 0.02;
            this.worldObj
               .spawnParticle(
                  EnumParticleTypes.EXPLOSION_NORMAL,
                  this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width,
                  this.posY + this.rand.nextFloat() * this.height,
                  this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width,
                  d,
                  e,
                  d2
               );
         }
      }
   }

   public boolean canDropLoot() {
      return !this.isChild();
   }

   public int decreaseAirSupply(int i) {
      int j = EnchantmentHelper.getRespiration(this);
      return j > 0 && this.rand.nextInt(j + 1) > 0 ? i : i - 1;
   }

   public int getExperiencePoints(EntityPlayer player) {
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

   public void setRevengeTarget(EntityLivingBase livingBase) {
      this.entityLivingToAttack = livingBase;
      this.revengeTimer = this.ticksExisted;
   }

   public EntityLivingBase getLastAttacker() {
      return this.lastAttacker;
   }

   public int getLastAttackerTime() {
      return this.lastAttackerTime;
   }

   public void setLastAttacker(Entity entityIn) {
      if (entityIn instanceof EntityLivingBase) {
         this.lastAttacker = (EntityLivingBase)entityIn;
      } else {
         this.lastAttacker = null;
      }

      this.lastAttackerTime = this.ticksExisted;
   }

   public int s() {
      return this.entityAge;
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      tagCompound.setFloat("HealF", this.getHealth());
      tagCompound.setShort("Health", (short)Math.ceil(this.getHealth()));
      tagCompound.setShort("HurtTime", (short)this.hurtTime);
      tagCompound.setInteger("HurtByTimestamp", this.revengeTimer);
      tagCompound.setShort("DeathTime", (short)this.deathTime);
      tagCompound.setFloat("AbsorptionAmount", this.getAbsorptionAmount());

      for (ItemStack itemStack : this.getInventory()) {
         if (itemStack != null) {
            this.attributeMap.removeAttributeModifiers(itemStack.getAttributeModifiers());
         }
      }

      tagCompound.setTag("Attributes", SharedMonsterAttributes.writeBaseAttributeMapToNBT(this.getAttributeMap()));

      for (ItemStack itemStack2 : this.getInventory()) {
         if (itemStack2 != null) {
            this.attributeMap.applyAttributeModifiers(itemStack2.getAttributeModifiers());
         }
      }

      if (!this.activePotionsMap.isEmpty()) {
         NBTTagList nbtTagList = new NBTTagList();

         for (PotionEffect potionEffect : this.activePotionsMap.values()) {
            nbtTagList.appendTag(potionEffect.writeCustomPotionEffectToNBT(new NBTTagCompound()));
         }

         tagCompound.setTag("ActiveEffects", nbtTagList);
      }
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      this.setAbsorptionAmount(tagCompund.getFloat("AbsorptionAmount"));
      if (tagCompund.hasKey("Attributes", 9) && this.worldObj != null && !this.worldObj.isRemote) {
         SharedMonsterAttributes.setAttributeModifiers(this.getAttributeMap(), tagCompund.getTagList("Attributes", 10));
      }

      if (tagCompund.hasKey("ActiveEffects", 9)) {
         NBTTagList nbtTagList = tagCompund.getTagList("ActiveEffects", 10);

         for (int i = 0; i < nbtTagList.tagCount(); i++) {
            NBTTagCompound nbtTagCompound = nbtTagList.getCompoundTagAt(i);
            PotionEffect potionEffect = PotionEffect.readCustomPotionEffectFromNBT(nbtTagCompound);
            if (potionEffect != null) {
               this.activePotionsMap.put(potionEffect.getPotionID(), potionEffect);
            }
         }
      }

      if (tagCompund.hasKey("HealF", 99)) {
         this.setHealth(tagCompund.getFloat("HealF"));
      } else {
         NBTBase nbtBase = tagCompund.getTag("Health");
         if (nbtBase == null) {
            this.setHealth(this.getMaxHealth());
         } else if (nbtBase.getId() == 5) {
            this.setHealth(((NBTTagFloat)nbtBase).getFloat());
         } else if (nbtBase.getId() == 2) {
            this.setHealth(((NBTTagShort)nbtBase).getShort());
         }
      }

      this.hurtTime = tagCompund.getShort("HurtTime");
      this.deathTime = tagCompund.getShort("DeathTime");
      this.revengeTimer = tagCompund.getInteger("HurtByTimestamp");
   }

   public void updatePotionEffects() {
      Iterator iterator2 = this.activePotionsMap.keySet().iterator();

      while (iterator2.hasNext()) {
         Integer integer = (Integer)iterator2.next();
         PotionEffect potionEffect = this.activePotionsMap.get(integer);
         if (!potionEffect.onUpdate(this)) {
            if (!this.worldObj.isRemote) {
               iterator2.remove();
               this.onFinishedPotionEffect(potionEffect);
            }
         } else if (potionEffect.getDuration() % 600 == 0) {
            this.onChangedPotionEffect(potionEffect, false);
         }
      }

      if (this.potionsNeedUpdate) {
         if (!this.worldObj.isRemote) {
            this.updatePotionMetadata();
         }

         this.potionsNeedUpdate = false;
      }

      int i = this.dataWatcher.getWatchableObjectInt(7);
      boolean bl = this.dataWatcher.getWatchableObjectByte(8) > 0;
      if (i > 0) {
         boolean bl2 = false;
         if (!this.isInvisible()) {
            bl2 = this.rand.nextBoolean();
         } else {
            bl2 = this.rand.nextInt(15) == 0;
         }

         if (bl) {
            bl2 &= this.rand.nextInt(5) == 0;
         }

         if (bl2 && i > 0) {
            double d = (i >> 16 & 0xFF) / 255.0;
            double e = (i >> 8 & 0xFF) / 255.0;
            double d2 = (i >> 0 & 0xFF) / 255.0;
            this.worldObj
               .spawnParticle(
                  bl ? EnumParticleTypes.SPELL_MOB_AMBIENT : EnumParticleTypes.SPELL_MOB,
                  this.posX + (this.rand.nextDouble() - 0.5) * this.width,
                  this.posY + this.rand.nextDouble() * this.height,
                  this.posZ + (this.rand.nextDouble() - 0.5) * this.width,
                  d,
                  e,
                  d2
               );
         }
      }
   }

   public void updatePotionMetadata() {
      if (this.activePotionsMap.isEmpty()) {
         this.resetPotionEffectMetadata();
         this.setInvisible(false);
      } else {
         int i = PotionHelper.calcPotionLiquidColor(this.activePotionsMap.values());
         this.dataWatcher.updateObject(8, (byte)(PotionHelper.getAreAmbient(this.activePotionsMap.values()) ? 1 : 0));
         this.dataWatcher.updateObject(7, i);
         this.setInvisible(this.isPotionActive(Potion.invisibility.id));
      }
   }

   public void resetPotionEffectMetadata() {
      this.dataWatcher.updateObject(8, (byte)0);
      this.dataWatcher.updateObject(7, 0);
   }

   public void clearActivePotions() {
      Iterator iterator2 = this.activePotionsMap.keySet().iterator();

      while (iterator2.hasNext()) {
         Integer integer = (Integer)iterator2.next();
         PotionEffect potionEffect = this.activePotionsMap.get(integer);
         if (!this.worldObj.isRemote) {
            iterator2.remove();
            this.onFinishedPotionEffect(potionEffect);
         }
      }
   }

   public Collection<PotionEffect> getActivePotionEffects() {
      return this.activePotionsMap.values();
   }

   public boolean isPotionActive(int potionId) {
      return this.activePotionsMap.containsKey(potionId);
   }

   public boolean isPotionActive(Potion potionIn) {
      return this.activePotionsMap.containsKey(potionIn.id);
   }

   public PotionEffect getActivePotionEffect(Potion potionIn) {
      return this.activePotionsMap.get(potionIn.id);
   }

   public void addPotionEffect(PotionEffect potioneffectIn) {
      if (this.isPotionApplicable(potioneffectIn)) {
         if (this.activePotionsMap.containsKey(potioneffectIn.getPotionID())) {
            this.activePotionsMap.get(potioneffectIn.getPotionID()).combine(potioneffectIn);
            this.onChangedPotionEffect(this.activePotionsMap.get(potioneffectIn.getPotionID()), true);
         } else {
            this.activePotionsMap.put(potioneffectIn.getPotionID(), potioneffectIn);
            this.onNewPotionEffect(potioneffectIn);
         }
      }
   }

   public boolean isPotionApplicable(PotionEffect potioneffectIn) {
      if (this.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD) {
         int i = potioneffectIn.getPotionID();
         if (i == Potion.regeneration.id || i == Potion.poison.id) {
            return false;
         }
      }

      return true;
   }

   public boolean V() {
      return this.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD;
   }

   public void removePotionEffectClient(int potionId) {
      this.activePotionsMap.remove(potionId);
   }

   public void removePotionEffect(int potionId) {
      PotionEffect potionEffect = this.activePotionsMap.remove(potionId);
      if (potionEffect != null) {
         this.onFinishedPotionEffect(potionEffect);
      }
   }

   public void onNewPotionEffect(PotionEffect id) {
      this.potionsNeedUpdate = true;
      if (!this.worldObj.isRemote) {
         Potion.potionTypes[id.getPotionID()].applyAttributesModifiersToEntity(this, this.getAttributeMap(), id.getAmplifier());
      }
   }

   public void onChangedPotionEffect(PotionEffect id, boolean bl) {
      this.potionsNeedUpdate = true;
      if (bl && !this.worldObj.isRemote) {
         Potion.potionTypes[id.getPotionID()].removeAttributesModifiersFromEntity(this, this.getAttributeMap(), id.getAmplifier());
         Potion.potionTypes[id.getPotionID()].applyAttributesModifiersToEntity(this, this.getAttributeMap(), id.getAmplifier());
      }
   }

   public void onFinishedPotionEffect(PotionEffect effect) {
      this.potionsNeedUpdate = true;
      if (!this.worldObj.isRemote) {
         Potion.potionTypes[effect.getPotionID()].removeAttributesModifiersFromEntity(this, this.getAttributeMap(), effect.getAmplifier());
      }
   }

   public void heal(float healAmount) {
      float f = this.getHealth();
      if (f > 0.0F) {
         this.setHealth(f + healAmount);
      }
   }

   public final float getHealth() {
      return this.dataWatcher.getWatchableObjectFloat(6);
   }

   public void setHealth(float health) {
      this.dataWatcher.updateObject(6, MathHelper.clamp_float(health, 0.0F, this.getMaxHealth()));
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isEntityInvulnerable(source)) {
         return false;
      }

      if (this.worldObj.isRemote) {
         return false;
      }

      this.entityAge = 0;
      if (this.getHealth() <= 0.0F) {
         return false;
      }

      if (source.isFireDamage() && this.isPotionActive(Potion.fireResistance)) {
         return false;
      }

      if ((source == DamageSource.anvil || source == DamageSource.fallingBlock) && this.getEquipmentInSlot(4) != null) {
         this.getEquipmentInSlot(4).damageItem((int)(amount * 4.0F + this.rand.nextFloat() * amount * 2.0F), this);
         amount *= 0.75F;
      }

      this.limbSwingAmount = 1.5F;
      boolean bl = true;
      if (this.hurtResistantTime > this.maxHurtResistantTime / 2.0F) {
         if (amount <= this.lastDamage) {
            return false;
         }

         this.damageEntity(source, amount - this.lastDamage);
         this.lastDamage = amount;
         bl = false;
      } else {
         this.lastDamage = amount;
         this.hurtResistantTime = this.maxHurtResistantTime;
         this.damageEntity(source, amount);
         this.hurtTime = this.maxHurtTime = 10;
      }

      this.attackedAtYaw = 0.0F;
      Entity entity = source.getEntity();
      if (entity != null) {
         if (entity instanceof EntityLivingBase) {
            this.setRevengeTarget((EntityLivingBase)entity);
         }

         if (entity instanceof EntityPlayer) {
            this.recentlyHit = 100;
            this.attackingPlayer = (EntityPlayer)entity;
         } else if (entity instanceof EntityWolf) {
            EntityWolf entityWolf = (EntityWolf)entity;
            if (entityWolf.isTamed()) {
               this.recentlyHit = 100;
               this.attackingPlayer = null;
            }
         }
      }

      if (bl) {
         this.worldObj.setEntityState(this, (byte)2);
         if (source != DamageSource.drown) {
            this.setBeenAttacked();
         }

         if (entity != null) {
            double d = entity.posX - this.posX;

            double e;
            for (e = entity.posZ - this.posZ; d * d + e * e < 1.0E-4; e = (Math.random() - Math.random()) * 0.01) {
               d = (Math.random() - Math.random()) * 0.01;
            }

            this.attackedAtYaw = (float)(MathHelper.atan2(e, d) * 180.0 / Math.PI - this.rotationYaw);
            this.knockBack(entity, amount, d, e);
         } else {
            this.attackedAtYaw = (int)(Math.random() * 2.0) * 180;
         }
      }

      if (this.getHealth() <= 0.0F) {
         String string = this.getDeathSound();
         if (bl && string != null) {
            this.playSound(string, this.getSoundVolume(), this.getSoundPitch());
         }

         this.onDeath(source);
      } else {
         String string2 = this.getHurtSound();
         if (bl && string2 != null) {
            this.playSound(string2, this.getSoundVolume(), this.getSoundPitch());
         }
      }

      return true;
   }

   public void renderBrokenItemStack(ItemStack stack) {
      this.playSound("random.break", 0.8F, 0.8F + this.worldObj.rand.nextFloat() * 0.4F);

      for (int i = 0; i < 5; i++) {
         Vec3 vec3 = new Vec3((this.rand.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
         vec3 = vec3.rotatePitch(-this.rotationPitch * (float) Math.PI / 180.0F);
         vec3 = vec3.rotateYaw(-this.rotationYaw * (float) Math.PI / 180.0F);
         double d = -this.rand.nextFloat() * 0.6 - 0.3;
         Vec3 vec32 = new Vec3((this.rand.nextFloat() - 0.5) * 0.3, d, 0.6);
         vec32 = vec32.rotatePitch(-this.rotationPitch * (float) Math.PI / 180.0F);
         vec32 = vec32.rotateYaw(-this.rotationYaw * (float) Math.PI / 180.0F);
         vec32 = vec32.addVector(this.posX, this.posY + this.getEyeHeight(), this.posZ);
         this.worldObj
            .spawnParticle(
               EnumParticleTypes.ITEM_CRACK,
               vec32.xCoord,
               vec32.yCoord,
               vec32.zCoord,
               vec3.xCoord,
               vec3.yCoord + 0.05,
               vec3.zCoord,
               Item.getIdFromItem(stack.getItem())
            );
      }
   }

   public void onDeath(DamageSource cause) {
      Entity entity = cause.getEntity();
      EntityLivingBase entityLivingBase = this.getAttackingEntity();
      if (this.scoreValue >= 0 && entityLivingBase != null) {
         entityLivingBase.addToPlayerScore(this, this.scoreValue);
      }

      if (entity != null) {
         entity.onKillEntity(this);
      }

      this.dead = true;
      this.getCombatTracker().reset();
      if (!this.worldObj.isRemote) {
         int i = 0;
         if (entity instanceof EntityPlayer) {
            i = EnchantmentHelper.getLootingModifier((EntityLivingBase)entity);
         }

         if (this.canDropLoot() && this.worldObj.getGameRules().getBoolean("doMobLoot")) {
            this.dropFewItems(this.recentlyHit > 0, i);
            this.dropEquipment(this.recentlyHit > 0, i);
            if (this.recentlyHit > 0 && this.rand.nextFloat() < 0.025F + i * 0.01F) {
               this.addRandomDrop();
            }
         }
      }

      this.worldObj.setEntityState(this, (byte)3);
   }

   public void dropEquipment(boolean wasRecentlyHit, int lootingModifier) {
   }

   public void knockBack(Entity entityIn, float f, double d, double d2) {
      if (this.rand.nextDouble() >= this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).getAttributeValue()) {
         this.isAirBorne = true;
         float g = MathHelper.sqrt_double(d * d + d2 * d2);
         float h = 0.4F;
         this.motionX /= 2.0;
         this.motionY /= 2.0;
         this.motionZ /= 2.0;
         this.motionX -= d / g * h;
         this.motionY += h;
         this.motionZ -= d2 / g * h;
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

   public void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
   }

   public boolean isOnLadder() {
      int i = MathHelper.floor_double(this.posX);
      int j = MathHelper.floor_double(this.getEntityBoundingBox().minY);
      int k = MathHelper.floor_double(this.posZ);
      Block block = this.worldObj.getBlockState(new BlockPos(i, j, k)).getBlock();
      return (block == Blocks.ladder || block == Blocks.vine) && (!(this instanceof EntityPlayer) || !((EntityPlayer)this).isSpectator());
   }

   @Override
   public boolean isEntityAlive() {
      return !this.isDead && this.getHealth() > 0.0F;
   }

   @Override
   public void fall(float distance, float damageMultiplier) {
      super.fall(distance, damageMultiplier);
      PotionEffect potionEffect = this.getActivePotionEffect(Potion.jump);
      float f = potionEffect != null ? potionEffect.getAmplifier() + 1 : 0.0F;
      int i = MathHelper.ceiling_float_int((distance - 3.0F - f) * damageMultiplier);
      if (i > 0) {
         this.playSound(this.getFallSoundString(i), 1.0F, 1.0F);
         this.attackEntityFrom(DamageSource.fall, i);
         int j = MathHelper.floor_double(this.posX);
         int k = MathHelper.floor_double(this.posY - 0.2F);
         int l = MathHelper.floor_double(this.posZ);
         Block block = this.worldObj.getBlockState(new BlockPos(j, k, l)).getBlock();
         if (block.getMaterial() != Material.air) {
            Block.SoundType soundType = block.stepSound;
            this.playSound(soundType.getStepSound(), soundType.getVolume() * 0.5F, soundType.getFrequency() * 0.75F);
         }
      }
   }

   public String getFallSoundString(int damageValue) {
      return damageValue > 4 ? "game.neutral.hurt.fall.big" : "game.neutral.hurt.fall.small";
   }

   @Override
   public void performHurtAnimation() {
      this.hurtTime = this.maxHurtTime = 10;
      this.attackedAtYaw = 0.0F;
   }

   public int getTotalArmorValue() {
      int i = 0;

      for (ItemStack itemStack : this.getInventory()) {
         if (itemStack != null && itemStack.getItem() instanceof ItemArmor) {
            int j = ((ItemArmor)itemStack.getItem()).damageReduceAmount;
            i += j;
         }
      }

      return i;
   }

   public void damageArmor(float f) {
   }

   public float applyArmorCalculations(DamageSource source, float damage) {
      if (!source.isUnblockable()) {
         int i = 25 - this.getTotalArmorValue();
         float f = damage * i;
         this.damageArmor(damage);
         damage = f / 25.0F;
      }

      return damage;
   }

   public float applyPotionDamageCalculations(DamageSource source, float damage) {
      if (source.isDamageAbsolute()) {
         return damage;
      }

      if (this.isPotionActive(Potion.resistance) && source != DamageSource.outOfWorld) {
         int i = (this.getActivePotionEffect(Potion.resistance).getAmplifier() + 1) * 5;
         int j = 25 - i;
         float f = damage * j;
         damage = f / 25.0F;
      }

      if (damage <= 0.0F) {
         return 0.0F;
      }

      int k = EnchantmentHelper.getEnchantmentModifierDamage(this.getInventory(), source);
      if (k > 20) {
         k = 20;
      }

      if (k > 0 && k <= 20) {
         int l = 25 - k;
         float g = damage * l;
         damage = g / 25.0F;
      }

      return damage;
   }

   public void damageEntity(DamageSource damageSrc, float damageAmount) {
      if (!this.isEntityInvulnerable(damageSrc)) {
         damageAmount = this.applyArmorCalculations(damageSrc, damageAmount);
         damageAmount = this.applyPotionDamageCalculations(damageSrc, damageAmount);
         float f = damageAmount;
         damageAmount = Math.max(damageAmount - this.getAbsorptionAmount(), 0.0F);
         this.setAbsorptionAmount(this.getAbsorptionAmount() - (f - damageAmount));
         if (damageAmount != 0.0F) {
            float g = this.getHealth();
            this.setHealth(g - damageAmount);
            this.getCombatTracker().trackDamage(damageSrc, g, damageAmount);
            this.setAbsorptionAmount(this.getAbsorptionAmount() - damageAmount);
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

   public final void setArrowCountInEntity(int count) {
      this.dataWatcher.updateObject(9, (byte)count);
   }

   private int getArmSwingAnimationEnd() {
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
   public void handleStatusUpdate(byte id) {
      if (id == 2) {
         this.limbSwingAmount = 1.5F;
         this.hurtResistantTime = this.maxHurtResistantTime;
         this.hurtTime = this.maxHurtTime = 10;
         this.attackedAtYaw = 0.0F;
         String string = this.getHurtSound();
         if (string != null) {
            this.playSound(this.getHurtSound(), this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
         }

         this.attackEntityFrom(DamageSource.generic, 0.0F);
      } else if (id == 3) {
         String string2 = this.getDeathSound();
         if (string2 != null) {
            this.playSound(this.getDeathSound(), this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
         }

         this.setHealth(0.0F);
         this.onDeath(DamageSource.generic);
      } else {
         super.handleStatusUpdate(id);
      }
   }

   @Override
   public void kill() {
      this.attackEntityFrom(DamageSource.outOfWorld, 4.0F);
   }

   public void L() {
      int i = this.getArmSwingAnimationEnd();
      if (this.Lp) {
         this.swingProgressInt++;
         if (this.swingProgressInt >= i) {
            this.swingProgressInt = 0;
            this.Lp = false;
         }
      } else {
         this.swingProgressInt = 0;
      }

      this.swingProgress = (float)this.swingProgressInt / i;
   }

   public IAttributeInstance getEntityAttribute(IAttribute attribute) {
      return this.getAttributeMap().getAttributeInstance(attribute);
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

   public abstract ItemStack getEquipmentInSlot(int slotIn);

   public abstract ItemStack getCurrentArmor(int slotIn);

   @Override
   public abstract void setCurrentItemOrArmor(int slotIn, ItemStack stack);

   @Override
   public void setSprinting(boolean sprinting) {
      super.setSprinting(sprinting);
      IAttributeInstance iAttributeInstance = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
      if (iAttributeInstance.getModifier(sprintingSpeedBoostModifierUUID) != null) {
         iAttributeInstance.removeModifier(sprintingSpeedBoostModifier);
      }

      if (sprinting) {
         iAttributeInstance.applyModifier(sprintingSpeedBoostModifier);
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

   public void dismountEntity(Entity entityIn) {
      double d = entityIn.posX;
      double e = entityIn.getEntityBoundingBox().minY + entityIn.height;
      double d2 = entityIn.posZ;
      byte b = 1;

      for (int i = -b; i <= b; i++) {
         for (int j = -b; j < b; j++) {
            if (i != 0 || j != 0) {
               int k = (int)(this.posX + i);
               int l = (int)(this.posZ + j);
               AxisAlignedBB axisAlignedBB = this.getEntityBoundingBox().offset(i, 1.0, j);
               if (this.worldObj.getCollisionBoxes(axisAlignedBB).isEmpty()) {
                  if (World.doesBlockHaveSolidTopSurface(this.worldObj, new BlockPos(k, (int)this.posY, l))) {
                     this.setPositionAndUpdate(this.posX + i, this.posY + 1.0, this.posZ + j);
                     return;
                  }

                  if (World.doesBlockHaveSolidTopSurface(this.worldObj, new BlockPos(k, (int)this.posY - 1, l))
                     || this.worldObj.getBlockState(new BlockPos(k, (int)this.posY - 1, l)).getBlock().getMaterial() == Material.water) {
                     d = this.posX + i;
                     e = this.posY + 1.0;
                     d2 = this.posZ + j;
                  }
               }
            }
         }
      }

      this.setPositionAndUpdate(d, e, d2);
   }

   @Override
   public boolean getAlwaysRenderNameTagForRender() {
      return false;
   }

   public float getJumpUpwardsMotion() {
      return 0.42F;
   }

   public void jump() {
      this.motionY = this.getJumpUpwardsMotion();
      if (this.isPotionActive(Potion.jump)) {
         this.motionY = this.motionY + (this.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F;
      }

      if (this.isSprinting()) {
         float f = this.rotationYaw * (float) (Math.PI / 180.0);
         this.motionX = this.motionX - MathHelper.sin(f) * 0.2F;
         this.motionZ = this.motionZ + MathHelper.cos(f) * 0.2F;
      }

      this.isAirBorne = true;
      if (this.isSprinting()) {
         float g = (float)Math.toRadians(this.rotationYaw);
         boolean bl = Astolfo.INSTANCE.getModuleManager().get(Sprint.class).isEnabled();
         if (bl && Sprint.omnidirectional.getValue()) {
            this.motionX = this.motionX + MathHelper.sin(g) * 0.2F;
            this.motionZ = this.motionZ - MathHelper.cos(g) * 0.2F;
            float h = this.rotationYaw;
            if (this.moveForward < 0.0F) {
               h += 180.0F;
            }

            if (this.moveStrafing > 0.0F) {
               h -= 90.0F * (this.moveForward > 0.0F ? 0.5F : (this.moveForward < 0.0F ? -0.5F : 1.0F));
            }

            if (this.moveStrafing < 0.0F) {
               h += 90.0F * (this.moveForward > 0.0F ? 0.5F : (this.moveForward < 0.0F ? -0.5F : 1.0F));
            }

            g = (float)Math.toRadians(h);
            this.motionX = this.motionX - MathHelper.sin(g) * 0.2F;
            this.motionZ = this.motionZ + MathHelper.cos(g) * 0.2F;
         }
      }
   }

   public void R() {
      this.motionY += 0.04F;
   }

   public void handleJumpLava() {
      this.motionY += 0.04F;
   }

   public void moveEntityWithHeading(float strafe, float forward) {
      if (this.C()) {
         if (!this.isInWater() || this instanceof EntityPlayer && ((EntityPlayer)this).capabilities.isFlying) {
            if (!this.isInLava() || this instanceof EntityPlayer && ((EntityPlayer)this).capabilities.isFlying) {
               float f = 0.91F;
               if (this.onGround) {
                  f = this.worldObj
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

               float g = 0.16277136F / (f * f * f);
               float h;
               if (this.onGround) {
                  h = this.getAIMoveSpeed() * g;
               } else {
                  h = this.jumpMovementFactor;
               }

               this.moveFlying(strafe, forward, h);
               f = 0.91F;
               if (this.onGround) {
                  f = this.worldObj
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
                  float f2 = 0.15F;
                  if (!Astolfo.INSTANCE.getModuleManager().get(NoSlow.class).isEnabled() || !NoSlow.ladderFix.getValue()) {
                     this.motionX = MathHelper.clamp_double(this.motionX, -f2, f2);
                     this.motionZ = MathHelper.clamp_double(this.motionZ, -f2, f2);
                  }

                  this.fallDistance = 0.0F;
                  if (this.motionY < -0.15) {
                     this.motionY = -0.15;
                  }

                  boolean bl = this.isSneaking() && this instanceof EntityPlayer;
                  if (bl && this.motionY < 0.0) {
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
               this.motionX *= f;
               this.motionZ *= f;
            } else {
               double d = this.posY;
               this.moveFlying(strafe, forward, 0.02F);
               this.moveEntity(this.motionX, this.motionY, this.motionZ);
               this.motionX *= 0.5;
               this.motionY *= 0.5;
               this.motionZ *= 0.5;
               this.motionY -= 0.02;
               if (this.isCollidedHorizontally && this.isOffsetPositionInLiquid(this.motionX, this.motionY + 0.6F - this.posY + d, this.motionZ)) {
                  this.motionY = 0.3F;
               }
            }
         } else {
            double e = this.posY;
            float f3 = 0.8F;
            float f4 = 0.02F;
            float f5 = EnchantmentHelper.getDepthStriderModifier(this);
            if (f5 > 3.0F) {
               f5 = 3.0F;
            }

            if (!this.onGround) {
               f5 *= 0.5F;
            }

            if (f5 > 0.0F) {
               f3 += (0.54600006F - f3) * f5 / 3.0F;
               f4 += (this.getAIMoveSpeed() * 1.0F - f4) * f5 / 3.0F;
            }

            this.moveFlying(strafe, forward, f4);
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= f3;
            this.motionY *= 0.8F;
            this.motionZ *= f3;
            this.motionY -= 0.02;
            if (this.isCollidedHorizontally && this.isOffsetPositionInLiquid(this.motionX, this.motionY + 0.6F - this.posY + e, this.motionZ)) {
               this.motionY = 0.3F;
            }
         }
      }

      this.prevLimbSwingAmount = this.limbSwingAmount;
      double d2 = this.posX - this.prevPosX;
      double d3 = this.posZ - this.prevPosZ;
      float f6 = MathHelper.sqrt_double(d2 * d2 + d3 * d3) * 4.0F;
      if (f6 > 1.0F) {
         f6 = 1.0F;
      }

      this.limbSwingAmount = this.limbSwingAmount + (f6 - this.limbSwingAmount) * 0.4F;
      this.limbSwing = this.limbSwing + this.limbSwingAmount;
   }

   public float getAIMoveSpeed() {
      return this.landMovementFactor;
   }

   public void setAIMoveSpeed(float speedIn) {
      this.landMovementFactor = speedIn;
   }

   public boolean attackEntityAsMob(Entity entityIn) {
      this.setLastAttacker(entityIn);
      return false;
   }

   public boolean isPlayerSleeping() {
      return false;
   }

   @Override
   public void onUpdate() {
      super.onUpdate();
      if (!this.worldObj.isRemote) {
         int i = this.getArrowCountInEntity();
         if (i > 0) {
            if (this.arrowHitTimer <= 0) {
               this.arrowHitTimer = 20 * (30 - i);
            }

            this.arrowHitTimer--;
            if (this.arrowHitTimer <= 0) {
               this.setArrowCountInEntity(i - 1);
            }
         }

         for (int j = 0; j < 5; j++) {
            ItemStack itemStack = this.previousEquipment[j];
            ItemStack itemStack2 = this.getEquipmentInSlot(j);
            if (!ItemStack.areItemStacksEqual(itemStack2, itemStack)) {
               ((WorldServer)this.worldObj).getEntityTracker().sendToAllTrackingEntity(this, new S04PacketEntityEquipment(this.getEntityId(), j, itemStack2));
               if (itemStack != null) {
                  this.attributeMap.removeAttributeModifiers(itemStack.getAttributeModifiers());
               }

               if (itemStack2 != null) {
                  this.attributeMap.applyAttributeModifiers(itemStack2.getAttributeModifiers());
               }

               this.previousEquipment[j] = itemStack2 == null ? null : itemStack2.copy();
            }
         }

         if (this.ticksExisted % 20 == 0) {
            this.getCombatTracker().reset();
         }
      }

      this.onLivingUpdate();
      double d = this.posX - this.prevPosX;
      double e = this.posZ - this.prevPosZ;
      float f = (float)(d * d + e * e);
      float g = this.renderYawOffset;
      float h = 0.0F;
      this.prevOnGroundSpeedFactor = this.onGroundSpeedFactor;
      float f2 = 0.0F;
      if (f > 0.0025000002F) {
         f2 = 1.0F;
         h = (float)Math.sqrt(f) * 3.0F;
         g = (float)MathHelper.atan2(e, d) * 180.0F / (float) Math.PI - 90.0F;
      }

      if (this.swingProgress > 0.0F) {
         g = this.rotationYaw;
      }

      if (!this.onGround) {
         f2 = 0.0F;
      }

      this.onGroundSpeedFactor = this.onGroundSpeedFactor + (f2 - this.onGroundSpeedFactor) * 0.3F;
      this.worldObj.theProfiler.startSection("headTurn");
      h = this.updateDistance(g, h);
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
      this.movedDistance += h;
   }

   public float updateDistance(float f, float g) {
      float h = MathHelper.wrapAngleTo180_float(f - this.renderYawOffset);
      this.renderYawOffset += h * 0.3F;
      float f2 = this.rotationYaw;
      if (this instanceof EntityPlayerSP) {
         RotationYawEvent rotationYawEvent = new RotationYawEvent(f2);
         Astolfo.INSTANCE.getEventBus().post(rotationYawEvent);
         f2 = rotationYawEvent.getYaw();
      }

      float f3 = MathHelper.wrapAngleTo180_float(f2 - this.renderYawOffset);
      boolean bl = f3 < -90.0F || f3 >= 90.0F;
      if (f3 < -75.0F) {
         f3 = -75.0F;
      }

      if (f3 >= 75.0F) {
         f3 = 75.0F;
      }

      this.renderYawOffset = f2 - f3;
      if (f3 * f3 > 2500.0F) {
         this.renderYawOffset += f3 * 0.2F;
      }

      if (bl) {
         g *= -1.0F;
      }

      return g;
   }

   public void onLivingUpdate() {
      if (this.jumpTicks > 0) {
         this.jumpTicks--;
      }

      if (this.newPosRotationIncrements > 0) {
         double d = this.posX + (this.newPosX - this.posX) / this.newPosRotationIncrements;
         double e = this.posY + (this.newPosY - this.posY) / this.newPosRotationIncrements;
         double d2 = this.posZ + (this.newPosZ - this.posZ) / this.newPosRotationIncrements;
         double d3 = MathHelper.wrapAngleTo180_double(this.newRotationYaw - this.rotationYaw);
         this.rotationYaw = (float)(this.rotationYaw + d3 / this.newPosRotationIncrements);
         this.rotationPitch = (float)(this.rotationPitch + (this.newRotationPitch - this.rotationPitch) / this.newPosRotationIncrements);
         this.newPosRotationIncrements--;
         this.setPosition(d, e, d2);
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
      if (RiderGod.isExactly1122Protocol() && this instanceof EntityPlayerSP && this.ridingEntity != null && this.ridingEntity instanceof EntityHorse) {
         EntityHorse entityHorse = (EntityHorse)this.ridingEntity;
         entityHorse.O(this.moveStrafing, this.moveForward, this.randomYawVelocity);
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
      List list = this.worldObj
         .getEntitiesInAABBexcluding(
            this, this.getEntityBoundingBox().expand(0.2F, 0.0, 0.2F), Predicates.and(EntitySelectors.NOT_SPECTATING, new Predicate<Entity>() {
               public boolean apply(Entity entity) {
                  return entity.canBePushed();
               }
            })
         );
      if (!list.isEmpty()) {
         for (int i = 0; i < list.size(); i++) {
            Entity entity2 = (Entity)list.get(i);
            this.collideWithEntity(entity2);
         }
      }
   }

   public void collideWithEntity(Entity entityIn) {
      entityIn.applyEntityCollision(this);
   }

   @Override
   public void mountEntity(Entity entityIn) {
      if (this.ridingEntity != null && entityIn == null) {
         if (!this.worldObj.isRemote) {
            this.dismountEntity(this.ridingEntity);
         }

         if (this.ridingEntity != null) {
            this.ridingEntity.riddenByEntity = null;
         }

         this.ridingEntity = null;
      } else {
         super.mountEntity(entityIn);
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
   public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean bl) {
      this.newPosX = x;
      this.newPosY = y;
      this.newPosZ = z;
      this.newRotationYaw = yaw;
      this.newRotationPitch = pitch;
      this.newPosRotationIncrements = posRotationIncrements;
   }

   public void j(boolean bl) {
      this.Lr = bl;
   }

   public void onItemPickup(Entity entity, int i) {
      if (!entity.isDead && !this.worldObj.isRemote) {
         EntityTracker entityTracker = ((WorldServer)this.worldObj).getEntityTracker();
         if (entity instanceof EntityItem) {
            entityTracker.sendToAllTrackingEntity(entity, new S0DPacketCollectItem(entity.getEntityId(), this.getEntityId()));
         }

         if (entity instanceof EntityArrow) {
            entityTracker.sendToAllTrackingEntity(entity, new S0DPacketCollectItem(entity.getEntityId(), this.getEntityId()));
         }

         if (entity instanceof EntityXPOrb) {
            entityTracker.sendToAllTrackingEntity(entity, new S0DPacketCollectItem(entity.getEntityId(), this.getEntityId()));
         }
      }
   }

   public boolean C(Entity entity) {
      return this.worldObj
            .rayTraceBlocks(
               new Vec3(this.posX, this.posY + this.getEyeHeight(), this.posZ), new Vec3(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ)
            )
         == null;
   }

   @Override
   public Vec3 getLookVec() {
      return this.getLook(1.0F);
   }

   @Override
   public Vec3 getLook(float partialTicks) {
      if (this instanceof EntityPlayerSP) {
         return super.getLook(partialTicks);
      }

      if (partialTicks == 1.0F) {
         return this.getVectorForRotation(this.rotationPitch, this.rotationYawHead);
      }

      float f = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * partialTicks;
      float g = this.prevRotationYawHead + (this.rotationYawHead - this.prevRotationYawHead) * partialTicks;
      return this.getVectorForRotation(f, g);
   }

   public float getSwingProgress(float partialTickTime) {
      float f = this.swingProgress - this.prevSwingProgress;
      if (f < 0.0F) {
         f++;
      }

      return this.prevSwingProgress + f * partialTickTime;
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
   public void setRotationYawHead(float rotation) {
      this.rotationYawHead = rotation;
   }

   @Override
   public void setRenderYawOffset(float offset) {
      this.renderYawOffset = offset;
   }

   public float getAbsorptionAmount() {
      return this.absorptionAmount;
   }

   public void setAbsorptionAmount(float amount) {
      if (amount < 0.0F) {
         amount = 0.0F;
      }

      this.absorptionAmount = amount;
   }

   public Team getTeam() {
      return this.worldObj.getScoreboard().getPlayersTeam(this.getUniqueID().toString());
   }

   public boolean isOnSameTeam(EntityLivingBase otherEntity) {
      return this.isOnTeam(otherEntity.getTeam());
   }

   public boolean isOnTeam(Team teamIn) {
      return this.getTeam() != null ? this.getTeam().isSameTeam(teamIn) : false;
   }

   public void sendEnterCombat() {
   }

   public void sendEndCombat() {
   }

   public void l() {
      this.potionsNeedUpdate = true;
   }
}
