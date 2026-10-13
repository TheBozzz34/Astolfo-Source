package net.minecraft.entity;

import java.util.UUID;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityJumpHelper;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.ai.EntitySenses;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.src.BlockPosM;
import net.minecraft.src.Config;
import net.minecraft.src.Reflector;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;

public abstract class EntityLiving extends EntityLivingBase {
   public int livingSoundTime;
   public int experienceValue;
   private EntityLookHelper lookHelper;
   public EntityMoveHelper moveHelper;
   public EntityJumpHelper jumpHelper;
   public EntityBodyHelper bodyHelper;
   public PathNavigate navigator;
   public final EntityAITasks tasks;
   public final EntityAITasks targetTasks;
   private EntityLivingBase attackTarget;
   private EntitySenses senses;
   private ItemStack[] equipment = new ItemStack[5];
   public float[] equipmentDropChances = new float[5];
   private boolean canPickUpLoot;
   private boolean persistenceRequired;
   private boolean isLeashed;
   private Entity leashedToEntity;
   private NBTTagCompound leashNBTTag;
   public int randomMobsId = 0;
   public BiomeGenBase spawnBiome = null;
   public BlockPos spawnPosition = null;
   private static final long cb = 72390919354266L;

   public EntityLiving(World var1) {
      super(var1);
      this.tasks = new EntityAITasks(var1 != null && var1.theProfiler != null ? var1.theProfiler : null);
      this.targetTasks = new EntityAITasks(var1 != null && var1.theProfiler != null ? var1.theProfiler : null);
      this.lookHelper = new EntityLookHelper(this);
      this.moveHelper = new EntityMoveHelper(this);
      this.jumpHelper = new EntityJumpHelper(this);
      this.bodyHelper = new EntityBodyHelper(this);
      this.navigator = this.getNewNavigator(var1);
      this.senses = new EntitySenses(this);

      for (int var5 = 0; var5 < this.equipmentDropChances.length; var5++) {
         this.equipmentDropChances[var5] = 0.085F;
      }

      UUID var8 = this.getUniqueID();
      long var6 = var8.getLeastSignificantBits();
      this.randomMobsId = (int)(var6 & 2147483647L);
   }

   @Override
   public void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getAttributeMap().registerAttribute(SharedMonsterAttributes.followRange).setBaseValue(16.0);
   }

   public PathNavigate getNewNavigator(World var1) {
      return new PathNavigateGround(this, var1);
   }

   public EntityLookHelper getLookHelper() {
      return this.lookHelper;
   }

   public EntityMoveHelper getMoveHelper() {
      return this.moveHelper;
   }

   public EntityJumpHelper getJumpHelper() {
      return this.jumpHelper;
   }

   public PathNavigate getNavigator() {
      return this.navigator;
   }

   public EntitySenses getEntitySenses() {
      return this.senses;
   }

   public EntityLivingBase getAttackTarget() {
      return this.attackTarget;
   }

   public void setAttackTarget(EntityLivingBase var1) {
      this.attackTarget = var1;
      Reflector.callVoid(Reflector.ForgeHooks_onLivingSetAttackTarget, this, var1);
   }

   public boolean canAttackClass(Class<? extends EntityLivingBase> var1) {
      return var1 != EntityGhast.class;
   }

   public void eatGrassBonus() {
   }

   @Override
   public void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(15, (byte)0);
   }

   public int getTalkInterval() {
      return 80;
   }

   public void playLivingSound() {
      String var3 = this.getLivingSound();
      if (var3 != null) {
         this.playSound(var3, this.getSoundVolume(), this.getSoundPitch());
      }
   }

   @Override
   public void onEntityUpdate() {
      super.onEntityUpdate();
      this.worldObj.theProfiler.startSection("mobBaseTick");
      if (this.isEntityAlive() && this.rand.nextInt(1000) < this.livingSoundTime++) {
         this.livingSoundTime = -this.getTalkInterval();
         this.playLivingSound();
      }

      this.worldObj.theProfiler.endSection();
   }

   @Override
   public int getExperiencePoints(EntityPlayer var1) {
      if (this.experienceValue > 0) {
         int var5 = this.experienceValue;
         ItemStack[] var6 = this.getInventory();

         for (int var7 = 0; var7 < var6.length; var7++) {
            if (var6[var7] != null && this.equipmentDropChances[var7] <= 1.0F) {
               var5 += 1 + this.rand.nextInt(3);
            }
         }

         return var5;
      } else {
         return this.experienceValue;
      }
   }

   public void spawnExplosionParticle() {
      if (this.worldObj.isRemote) {
         for (int var4 = 0; var4 < 20; var4++) {
            double var5 = this.rand.nextGaussian() * 0.02;
            double var7 = this.rand.nextGaussian() * 0.02;
            double var9 = this.rand.nextGaussian() * 0.02;
            double var11 = 10.0;
            this.worldObj
               .spawnParticle(
                  EnumParticleTypes.EXPLOSION_NORMAL,
                  this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width - var5 * var11,
                  this.posY + this.rand.nextFloat() * this.height - var7 * var11,
                  this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width - var9 * var11,
                  var5,
                  var7,
                  var9
               );
         }
      } else {
         this.worldObj.setEntityState(this, (byte)20);
      }
   }

   @Override
   public void handleStatusUpdate(byte var1) {
      if (var1 == 20) {
         this.spawnExplosionParticle();
      } else {
         super.handleStatusUpdate(var1);
      }
   }

   @Override
   public void onUpdate() {
      if (Config.isSmoothWorld() && this.canSkipUpdate()) {
         this.onUpdateMinimal();
      } else {
         super.onUpdate();
         if (!this.worldObj.isRemote) {
            this.updateLeashedState();
         }
      }
   }

   @Override
   public float updateDistance(float var1, float var2) {
      this.bodyHelper.updateRenderAngles();
      return var2;
   }

   public String getLivingSound() {
      return null;
   }

   public Item getDropItem() {
      return null;
   }

   @Override
   public void dropFewItems(boolean var1, int var2) {
      Item var6 = this.getDropItem();
      if (var6 != null) {
         int var7 = this.rand.nextInt(3);
         if (var2 > 0) {
            var7 += this.rand.nextInt(var2 + 1);
         }

         for (int var8 = 0; var8 < var7; var8++) {
            this.dropItem(var6, 1);
         }
      }
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound var1) {
      super.writeEntityToNBT(var1);
      var1.setBoolean("CanPickUpLoot", this.canPickUpLoot());
      var1.setBoolean("PersistenceRequired", this.persistenceRequired);
      NBTTagList var5 = new NBTTagList();

      for (int var6 = 0; var6 < this.equipment.length; var6++) {
         NBTTagCompound var7 = new NBTTagCompound();
         if (this.equipment[var6] != null) {
            this.equipment[var6].writeToNBT(var7);
         }

         var5.appendTag(var7);
      }

      var1.setTag("Equipment", var5);
      NBTTagList var9 = new NBTTagList();

      for (int var10 = 0; var10 < this.equipmentDropChances.length; var10++) {
         var9.appendTag(new NBTTagFloat(this.equipmentDropChances[var10]));
      }

      var1.setTag("DropChances", var9);
      var1.setBoolean("Leashed", this.isLeashed);
      if (this.leashedToEntity != null) {
         NBTTagCompound var11 = new NBTTagCompound();
         if (this.leashedToEntity instanceof EntityLivingBase) {
            var11.setLong("UUIDMost", this.leashedToEntity.getUniqueID().getMostSignificantBits());
            var11.setLong("UUIDLeast", this.leashedToEntity.getUniqueID().getLeastSignificantBits());
         } else if (this.leashedToEntity instanceof EntityHanging) {
            BlockPos var8 = ((EntityHanging)this.leashedToEntity).getHangingPosition();
            var11.setInteger("X", var8.getX());
            var11.setInteger("Y", var8.getY());
            var11.setInteger("Z", var8.getZ());
         }

         var1.setTag("Leash", var11);
      }

      if (this.isAIDisabled()) {
         var1.setBoolean("NoAI", this.isAIDisabled());
      }
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound var1) {
      super.readEntityFromNBT(var1);
      if (var1.hasKey("CanPickUpLoot", 1)) {
         this.setCanPickUpLoot(var1.getBoolean("CanPickUpLoot"));
      }

      this.persistenceRequired = var1.getBoolean("PersistenceRequired");
      if (var1.hasKey("Equipment", 9)) {
         NBTTagList var5 = var1.getTagList("Equipment", 10);

         for (int var6 = 0; var6 < this.equipment.length; var6++) {
            this.equipment[var6] = ItemStack.loadItemStackFromNBT(var5.getCompoundTagAt(var6));
         }
      }

      if (var1.hasKey("DropChances", 9)) {
         NBTTagList var7 = var1.getTagList("DropChances", 5);

         for (int var8 = 0; var8 < var7.tagCount(); var8++) {
            this.equipmentDropChances[var8] = var7.getFloatAt(var8);
         }
      }

      this.isLeashed = var1.getBoolean("Leashed");
      if (this.isLeashed && var1.hasKey("Leash", 10)) {
         this.leashNBTTag = var1.getCompoundTag("Leash");
      }

      this.setNoAI(var1.getBoolean("NoAI"));
   }

   public void setMoveForward(float var1) {
      this.moveForward = var1;
   }

   @Override
   public void setAIMoveSpeed(float var1) {
      super.setAIMoveSpeed(var1);
      this.setMoveForward(var1);
   }

   @Override
   public void onLivingUpdate() {
      super.onLivingUpdate();
      this.worldObj.theProfiler.startSection("looting");
      if (!this.worldObj.isRemote && this.canPickUpLoot() && !this.dead && this.worldObj.getGameRules().getBoolean("mobGriefing")) {
         for (EntityItem var5 : this.worldObj.getEntitiesWithinAABB(EntityItem.class, this.getEntityBoundingBox().expand(1.0, 0.0, 1.0))) {
            if (!var5.isDead && var5.getEntityItem() != null && !var5.cannotPickup()) {
               this.updateEquipmentIfNeeded(var5);
            }
         }
      }

      this.worldObj.theProfiler.endSection();
   }

   public void updateEquipmentIfNeeded(EntityItem var1) {
      ItemStack var5 = var1.getEntityItem();
      int var6 = getArmorPosition(var5);
      if (var6 > -1) {
         boolean var7 = true;
         ItemStack var8 = this.getEquipmentInSlot(var6);
         if (var8 != null) {
            if (var6 == 0) {
               if (var5.getItem() instanceof ItemSword && !(var8.getItem() instanceof ItemSword)) {
                  var7 = true;
               } else if (var5.getItem() instanceof ItemSword && var8.getItem() instanceof ItemSword) {
                  ItemSword var11 = (ItemSword)var5.getItem();
                  ItemSword var13 = (ItemSword)var8.getItem();
                  if (var11.Q() != var13.Q()) {
                     var7 = var11.Q() > var13.Q();
                  } else {
                     var7 = var5.getMetadata() > var8.getMetadata() || var5.hasTagCompound() && !var8.hasTagCompound();
                  }
               } else if (var5.getItem() instanceof ItemBow && var8.getItem() instanceof ItemBow) {
                  var7 = var5.hasTagCompound() && !var8.hasTagCompound();
               } else {
                  var7 = false;
               }
            } else if (var5.getItem() instanceof ItemArmor && !(var8.getItem() instanceof ItemArmor)) {
               var7 = true;
            } else if (var5.getItem() instanceof ItemArmor && var8.getItem() instanceof ItemArmor) {
               ItemArmor var9 = (ItemArmor)var5.getItem();
               ItemArmor var10 = (ItemArmor)var8.getItem();
               if (var9.damageReduceAmount != var10.damageReduceAmount) {
                  var7 = var9.damageReduceAmount > var10.damageReduceAmount;
               } else {
                  var7 = var5.getMetadata() > var8.getMetadata() || var5.hasTagCompound() && !var8.hasTagCompound();
               }
            } else {
               var7 = false;
            }
         }

         if (var7 && this.func_175448_a(var5)) {
            if (var8 != null && this.rand.nextFloat() - 0.1F < this.equipmentDropChances[var6]) {
               this.entityDropItem(var8, 0.0F);
            }

            if (var5.getItem() == Items.diamond && var1.getThrower() != null) {
               EntityPlayer var12 = this.worldObj.getPlayerEntityByName(var1.getThrower());
               if (var12 != null) {
                  var12.triggerAchievement(AchievementList.diamondsToYou);
               }
            }

            this.setCurrentItemOrArmor(var6, var5);
            this.equipmentDropChances[var6] = 2.0F;
            this.persistenceRequired = true;
            this.onItemPickup(var1, 1);
            var1.setDead();
         }
      }
   }

   public boolean func_175448_a(ItemStack var1) {
      return true;
   }

   public boolean canDespawn() {
      return true;
   }

   public void despawnEntity() {
      Object var4 = null;
      Object var5 = Reflector.getFieldValue(Reflector.Event_Result_DEFAULT);
      Object var6 = Reflector.getFieldValue(Reflector.Event_Result_DENY);
      if (this.persistenceRequired) {
         this.entityAge = 0;
      } else if ((this.entityAge & 31) != 31 || (var4 = Reflector.call(Reflector.ForgeEventFactory_canEntityDespawn, this)) == var5) {
         EntityPlayer var7 = this.worldObj.getClosestPlayerToEntity(this, -1.0);
         if (var7 != null) {
            double var8 = var7.posX - this.posX;
            double var10 = var7.posY - this.posY;
            double var12 = var7.posZ - this.posZ;
            double var14 = var8 * var8 + var10 * var10 + var12 * var12;
            if (this.canDespawn() && var14 > 16384.0) {
               this.setDead();
            }

            if (this.entityAge > 600 && this.rand.nextInt(800) == 0 && var14 > 1024.0 && this.canDespawn()) {
               this.setDead();
            } else if (var14 < 1024.0) {
               this.entityAge = 0;
            }
         }
      } else if (var4 == var6) {
         this.entityAge = 0;
      } else {
         this.setDead();
      }
   }

   @Override
   public final void updateEntityActionState() {
      this.entityAge++;
      this.worldObj.theProfiler.startSection("checkDespawn");
      this.despawnEntity();
      this.worldObj.theProfiler.endSection();
      this.worldObj.theProfiler.startSection("sensing");
      this.senses.clearSensingCache();
      this.worldObj.theProfiler.endSection();
      this.worldObj.theProfiler.startSection("targetSelector");
      this.targetTasks.onUpdateTasks();
      this.worldObj.theProfiler.endSection();
      this.worldObj.theProfiler.startSection("goalSelector");
      this.tasks.onUpdateTasks();
      this.worldObj.theProfiler.endSection();
      this.worldObj.theProfiler.startSection("navigation");
      this.navigator.onUpdateNavigation();
      this.worldObj.theProfiler.endSection();
      this.worldObj.theProfiler.startSection("mob tick");
      this.updateAITasks();
      this.worldObj.theProfiler.endSection();
      this.worldObj.theProfiler.startSection("controls");
      this.worldObj.theProfiler.startSection("move");
      this.moveHelper.onUpdateMoveHelper();
      this.worldObj.theProfiler.endStartSection("look");
      this.lookHelper.onUpdateLook();
      this.worldObj.theProfiler.endStartSection("jump");
      this.jumpHelper.doJump();
      this.worldObj.theProfiler.endSection();
      this.worldObj.theProfiler.endSection();
   }

   public void updateAITasks() {
   }

   public int getVerticalFaceSpeed() {
      return 40;
   }

   public void faceEntity(Entity var1, float var2, float var3) {
      double var7 = var1.posX - this.posX;
      double var9 = var1.posZ - this.posZ;
      double var11;
      if (var1 instanceof EntityLivingBase) {
         EntityLivingBase var13 = (EntityLivingBase)var1;
         var11 = var13.posY + var13.getEyeHeight() - (this.posY + this.getEyeHeight());
      } else {
         var11 = (var1.getEntityBoundingBox().minY + var1.getEntityBoundingBox().maxY) / 2.0 - (this.posY + this.getEyeHeight());
      }

      double var17 = MathHelper.sqrt_double(var7 * var7 + var9 * var9);
      float var15 = (float)(MathHelper.atan2(var9, var7) * 180.0 / Math.PI) - 90.0F;
      float var16 = (float)(-(MathHelper.atan2(var11, var17) * 180.0 / Math.PI));
      this.rotationPitch = this.updateRotation(this.rotationPitch, var16, var3);
      this.rotationYaw = this.updateRotation(this.rotationYaw, var15, var2);
   }

   private float updateRotation(float var1, float var2, float var3) {
      float var7 = MathHelper.wrapAngleTo180_float(var2 - var1);
      if (var7 > var3) {
         var7 = var3;
      }

      if (var7 < -var3) {
         var7 = -var3;
      }

      return var1 + var7;
   }

   public boolean getCanSpawnHere() {
      return true;
   }

   public boolean isNotColliding() {
      return this.worldObj.checkNoEntityCollision(this.getEntityBoundingBox(), this)
         && this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox()).isEmpty()
         && !this.worldObj.isAnyLiquid(this.getEntityBoundingBox());
   }

   public float getRenderSizeModifier() {
      return 1.0F;
   }

   public int getMaxSpawnedInChunk() {
      return 4;
   }

   @Override
   public int getMaxFallHeight() {
      if (this.getAttackTarget() == null) {
         return 3;
      }

      int var4 = (int)(this.getHealth() - this.getMaxHealth() * 0.33F);
      var4 -= (3 - this.worldObj.getDifficulty().getDifficultyId()) * 4;
      if (var4 < 0) {
         var4 = 0;
      }

      return var4 + 3;
   }

   @Override
   public ItemStack getHeldItem() {
      return this.equipment[0];
   }

   @Override
   public ItemStack getEquipmentInSlot(int var1) {
      return this.equipment[var1];
   }

   @Override
   public ItemStack getCurrentArmor(int var1) {
      return this.equipment[var1 + 1];
   }

   @Override
   public void setCurrentItemOrArmor(int var1, ItemStack var2) {
      this.equipment[var1] = var2;
   }

   @Override
   public ItemStack[] getInventory() {
      return this.equipment;
   }

   @Override
   public void dropEquipment(boolean var1, int var2) {
      for (int var6 = 0; var6 < this.getInventory().length; var6++) {
         ItemStack var7 = this.getEquipmentInSlot(var6);
         boolean var8 = this.equipmentDropChances[var6] > 1.0F;
         if (var7 != null && (var1 || var8) && this.rand.nextFloat() - var2 * 0.01F < this.equipmentDropChances[var6]) {
            if (!var8 && var7.isItemStackDamageable()) {
               int var9 = Math.max(var7.getMaxDamage() - 25, 1);
               int var10 = var7.getMaxDamage() - this.rand.nextInt(this.rand.nextInt(var9) + 1);
               if (var10 > var9) {
                  var10 = var9;
               }

               if (var10 < 1) {
                  var10 = 1;
               }

               var7.setItemDamage(var10);
            }

            this.entityDropItem(var7, 0.0F);
         }
      }
   }

   public void setEquipmentBasedOnDifficulty(DifficultyInstance var1) {
      if (this.rand.nextFloat() < 0.15F * var1.getClampedAdditionalDifficulty()) {
         int var5 = this.rand.nextInt(2);
         float var6 = this.worldObj.getDifficulty() == EnumDifficulty.HARD ? 0.1F : 0.25F;
         if (this.rand.nextFloat() < 0.095F) {
            var5++;
         }

         if (this.rand.nextFloat() < 0.095F) {
            var5++;
         }

         if (this.rand.nextFloat() < 0.095F) {
            var5++;
         }

         for (int var7 = 3; var7 >= 0; var7--) {
            ItemStack var8 = this.getCurrentArmor(var7);
            if (var7 < 3 && this.rand.nextFloat() < var6) {
               break;
            }

            if (var8 == null) {
               Item var9 = getArmorItemForSlot(var7 + 1, var5);
               if (var9 != null) {
                  this.setCurrentItemOrArmor(var7 + 1, new ItemStack(var9));
               }
            }
         }
      }
   }

   public static int getArmorPosition(ItemStack var0) {
      if (var0.getItem() == Item.getItemFromBlock(Blocks.pumpkin) || var0.getItem() == Items.skull) {
         return 4;
      }

      if (var0.getItem() instanceof ItemArmor) {
         switch (((ItemArmor)var0.getItem()).armorType) {
            case 0:
               return 4;
            case 1:
               return 3;
            case 2:
               return 2;
            case 3:
               return 1;
         }
      }

      return 0;
   }

   public static Item getArmorItemForSlot(int var0, int var1) {
      switch (var0) {
         case 4:
            if (var1 == 0) {
               return Items.leather_helmet;
            } else if (var1 == 1) {
               return Items.golden_helmet;
            } else if (var1 == 2) {
               return Items.chainmail_helmet;
            } else if (var1 == 3) {
               return Items.iron_helmet;
            } else if (var1 == 4) {
               return Items.diamond_helmet;
            }
         case 3:
            if (var1 == 0) {
               return Items.leather_chestplate;
            } else if (var1 == 1) {
               return Items.golden_chestplate;
            } else if (var1 == 2) {
               return Items.chainmail_chestplate;
            } else if (var1 == 3) {
               return Items.iron_chestplate;
            } else if (var1 == 4) {
               return Items.diamond_chestplate;
            }
         case 2:
            if (var1 == 0) {
               return Items.leather_leggings;
            } else if (var1 == 1) {
               return Items.golden_leggings;
            } else if (var1 == 2) {
               return Items.chainmail_leggings;
            } else if (var1 == 3) {
               return Items.iron_leggings;
            } else if (var1 == 4) {
               return Items.diamond_leggings;
            }
         case 1:
            if (var1 == 0) {
               return Items.leather_boots;
            } else if (var1 == 1) {
               return Items.golden_boots;
            } else if (var1 == 2) {
               return Items.chainmail_boots;
            } else if (var1 == 3) {
               return Items.iron_boots;
            } else if (var1 == 4) {
               return Items.diamond_boots;
            }
         default:
            return null;
      }
   }

   public void setEnchantmentBasedOnDifficulty(DifficultyInstance var1) {
      float var5 = var1.getClampedAdditionalDifficulty();
      if (this.getHeldItem() != null && this.rand.nextFloat() < 0.25F * var5) {
         EnchantmentHelper.addRandomEnchantment(this.rand, this.getHeldItem(), (int)(5.0F + var5 * this.rand.nextInt(18)));
      }

      for (int var6 = 0; var6 < 4; var6++) {
         ItemStack var7 = this.getCurrentArmor(var6);
         if (var7 != null && this.rand.nextFloat() < 0.5F * var5) {
            EnchantmentHelper.addRandomEnchantment(this.rand, var7, (int)(5.0F + var5 * this.rand.nextInt(18)));
         }
      }
   }

   public IEntityLivingData onInitialSpawn(DifficultyInstance var1, IEntityLivingData var2) {
      this.getEntityAttribute(SharedMonsterAttributes.followRange)
         .applyModifier(new AttributeModifier("Random spawn bonus", this.rand.nextGaussian() * 0.05, 1));
      return var2;
   }

   public boolean canBeSteered() {
      return false;
   }

   public void enablePersistence() {
      this.persistenceRequired = true;
   }

   public void setEquipmentDropChance(int var1, float var2) {
      this.equipmentDropChances[var1] = var2;
   }

   public boolean canPickUpLoot() {
      return this.canPickUpLoot;
   }

   public void setCanPickUpLoot(boolean var1) {
      this.canPickUpLoot = var1;
   }

   public boolean isNoDespawnRequired() {
      return this.persistenceRequired;
   }

   @Override
   public final boolean interactFirst(EntityPlayer var1) {
      if (this.getLeashed() && this.getLeashedToEntity() == var1) {
         this.clearLeashed(true, !var1.capabilities.isCreativeMode);
         return true;
      }

      ItemStack var5 = var1.inventory.getCurrentItem();
      if (var5 != null && var5.getItem() == Items.lead && this.allowLeashing()) {
         if (!(this instanceof EntityTameable) || !((EntityTameable)this).isTamed()) {
            this.setLeashedToEntity(var1, true);
            var5.stackSize--;
            return true;
         }

         if (((EntityTameable)this).V(var1)) {
            this.setLeashedToEntity(var1, true);
            var5.stackSize--;
            return true;
         }
      }

      return this.interact(var1) ? true : super.interactFirst(var1);
   }

   public boolean interact(EntityPlayer var1) {
      return false;
   }

   public void updateLeashedState() {
      if (this.leashNBTTag != null) {
         this.recreateLeash();
      }

      if (this.isLeashed) {
         if (!this.isEntityAlive()) {
            this.clearLeashed(true, true);
         }

         if (this.leashedToEntity == null || this.leashedToEntity.isDead) {
            this.clearLeashed(true, true);
         }
      }
   }

   public void clearLeashed(boolean var1, boolean var2) {
      if (this.isLeashed) {
         this.isLeashed = false;
         this.leashedToEntity = null;
         if (!this.worldObj.isRemote && var2) {
            this.dropItem(Items.lead, 1);
         }

         if (!this.worldObj.isRemote && var1 && this.worldObj instanceof WorldServer) {
            ((WorldServer)this.worldObj).getEntityTracker().sendToAllTrackingEntity(this, new S1BPacketEntityAttach(1, this, (Entity)null));
         }
      }
   }

   public boolean allowLeashing() {
      return !this.getLeashed() && !(this instanceof IMob);
   }

   public boolean getLeashed() {
      return this.isLeashed;
   }

   public Entity getLeashedToEntity() {
      return this.leashedToEntity;
   }

   public void setLeashedToEntity(Entity var1, boolean var2) {
      this.isLeashed = true;
      this.leashedToEntity = var1;
      if (!this.worldObj.isRemote && var2 && this.worldObj instanceof WorldServer) {
         ((WorldServer)this.worldObj).getEntityTracker().sendToAllTrackingEntity(this, new S1BPacketEntityAttach(1, this, this.leashedToEntity));
      }
   }

   private void recreateLeash() {
      if (this.isLeashed && this.leashNBTTag != null) {
         if (this.leashNBTTag.hasKey("UUIDMost", 4) && this.leashNBTTag.hasKey("UUIDLeast", 4)) {
            UUID var7 = new UUID(this.leashNBTTag.getLong("UUIDMost"), this.leashNBTTag.getLong("UUIDLeast"));

            for (EntityLivingBase var6 : this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().expand(10.0, 10.0, 10.0))) {
               if (var6.getUniqueID().equals(var7)) {
                  this.leashedToEntity = var6;
                  break;
               }
            }
         } else if (this.leashNBTTag.hasKey("X", 99) && this.leashNBTTag.hasKey("Y", 99) && this.leashNBTTag.hasKey("Z", 99)) {
            BlockPos var4 = new BlockPos(this.leashNBTTag.getInteger("X"), this.leashNBTTag.getInteger("Y"), this.leashNBTTag.getInteger("Z"));
            EntityLeashKnot var5 = EntityLeashKnot.getKnotForPosition(this.worldObj, var4);
            if (var5 == null) {
               var5 = EntityLeashKnot.createKnot(this.worldObj, var4);
            }

            this.leashedToEntity = var5;
         } else {
            this.clearLeashed(false, true);
         }
      }

      this.leashNBTTag = null;
   }

   @Override
   public boolean replaceItemInInventory(int var1, ItemStack var2) {
      int var6;
      if (var1 == 99) {
         var6 = 0;
      } else {
         var6 = var1 - 100 + 1;
         if (var6 < 0 || var6 >= this.equipment.length) {
            return false;
         }
      }

      if (var2 != null && getArmorPosition(var2) != var6 && (var6 != 4 || !(var2.getItem() instanceof ItemBlock))) {
         return false;
      }

      this.setCurrentItemOrArmor(var6, var2);
      return true;
   }

   @Override
   public boolean C() {
      return super.C() && !this.isAIDisabled();
   }

   public void setNoAI(boolean var1) {
      this.dataWatcher.updateObject(15, (byte)(var1 ? 1 : 0));
   }

   public boolean isAIDisabled() {
      return this.dataWatcher.getWatchableObjectByte(15) != 0;
   }

   @Override
   public boolean isEntityInsideOpaqueBlock() {
      if (this.noClip) {
         return false;
      }

      BlockPosM var4 = new BlockPosM(0, 0, 0);

      for (int var5 = 0; var5 < 8; var5++) {
         double var6 = this.posX + ((var5 >> 0) % 2 - 0.5F) * this.width * 0.8F;
         double var8 = this.posY + ((var5 >> 1) % 2 - 0.5F) * 0.1F;
         double var10 = this.posZ + ((var5 >> 2) % 2 - 0.5F) * this.width * 0.8F;
         var4.setXyz(var6, var8 + this.getEyeHeight(), var10);
         if (this.worldObj.getBlockState(var4).getBlock().isVisuallyOpaque()) {
            return true;
         }
      }

      return false;
   }

   private boolean canSkipUpdate() {
      if (this.isChild()) {
         return false;
      }

      if (this.hurtTime > 0) {
         return false;
      }

      if (this.ticksExisted < 20) {
         return false;
      }

      World var4 = this.getEntityWorld();
      if (var4 == null) {
         return false;
      }

      if (var4.playerEntities.size() != 1) {
         return false;
      }

      Entity var5 = var4.playerEntities.get(0);
      double var6 = Math.max(Math.abs(this.posX - var5.posX) - 16.0, 0.0);
      double var8 = Math.max(Math.abs(this.posZ - var5.posZ) - 16.0, 0.0);
      double var10 = var6 * var6 + var8 * var8;
      return !this.isInRangeToRenderDist(var10);
   }

   private void onUpdateMinimal() {
      this.entityAge++;
      if (this instanceof EntityMob) {
         float var4 = this.getBrightness(1.0F);
         if (var4 > 0.5F) {
            this.entityAge += 2;
         }
      }

      this.despawnEntity();
   }

   public enum SpawnPlacementType {
      ON_GROUND("ON_GROUND", 0),
      IN_AIR("IN_AIR", 1),
      IN_WATER("IN_WATER", 2);

      private static final EntityLiving.SpawnPlacementType[] c = new EntityLiving.SpawnPlacementType[]{ON_GROUND, IN_AIR, IN_WATER};

      SpawnPlacementType(String var3, int var4) {
      }
   }
}
