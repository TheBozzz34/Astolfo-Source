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

   public EntityLiving(World worldIn) {
      super(worldIn);
      this.tasks = new EntityAITasks(worldIn != null && worldIn.theProfiler != null ? worldIn.theProfiler : null);
      this.targetTasks = new EntityAITasks(worldIn != null && worldIn.theProfiler != null ? worldIn.theProfiler : null);
      this.lookHelper = new EntityLookHelper(this);
      this.moveHelper = new EntityMoveHelper(this);
      this.jumpHelper = new EntityJumpHelper(this);
      this.bodyHelper = new EntityBodyHelper(this);
      this.navigator = this.getNewNavigator(worldIn);
      this.senses = new EntitySenses(this);

      for (int i = 0; i < this.equipmentDropChances.length; i++) {
         this.equipmentDropChances[i] = 0.085F;
      }

      UUID uuid = this.getUniqueID();
      long l = uuid.getLeastSignificantBits();
      this.randomMobsId = (int)(l & 2147483647L);
   }

   @Override
   public void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getAttributeMap().registerAttribute(SharedMonsterAttributes.followRange).setBaseValue(16.0);
   }

   public PathNavigate getNewNavigator(World worldIn) {
      return new PathNavigateGround(this, worldIn);
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

   public void setAttackTarget(EntityLivingBase entitylivingbaseIn) {
      this.attackTarget = entitylivingbaseIn;
      Reflector.callVoid(Reflector.ForgeHooks_onLivingSetAttackTarget, this, entitylivingbaseIn);
   }

   public boolean canAttackClass(Class<? extends EntityLivingBase> cls) {
      return cls != EntityGhast.class;
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
      String string = this.getLivingSound();
      if (string != null) {
         this.playSound(string, this.getSoundVolume(), this.getSoundPitch());
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
   public int getExperiencePoints(EntityPlayer player) {
      if (this.experienceValue > 0) {
         int i = this.experienceValue;
         ItemStack[] itemStacks = this.getInventory();

         for (int j = 0; j < itemStacks.length; j++) {
            if (itemStacks[j] != null && this.equipmentDropChances[j] <= 1.0F) {
               i += 1 + this.rand.nextInt(3);
            }
         }

         return i;
      } else {
         return this.experienceValue;
      }
   }

   public void spawnExplosionParticle() {
      if (this.worldObj.isRemote) {
         for (int i = 0; i < 20; i++) {
            double d = this.rand.nextGaussian() * 0.02;
            double e = this.rand.nextGaussian() * 0.02;
            double d2 = this.rand.nextGaussian() * 0.02;
            double d3 = 10.0;
            this.worldObj
               .spawnParticle(
                  EnumParticleTypes.EXPLOSION_NORMAL,
                  this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width - d * d3,
                  this.posY + this.rand.nextFloat() * this.height - e * d3,
                  this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width - d2 * d3,
                  d,
                  e,
                  d2
               );
         }
      } else {
         this.worldObj.setEntityState(this, (byte)20);
      }
   }

   @Override
   public void handleStatusUpdate(byte id) {
      if (id == 20) {
         this.spawnExplosionParticle();
      } else {
         super.handleStatusUpdate(id);
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
   public float updateDistance(float f, float g) {
      this.bodyHelper.updateRenderAngles();
      return g;
   }

   public String getLivingSound() {
      return null;
   }

   public Item getDropItem() {
      return null;
   }

   @Override
   public void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
      Item item = this.getDropItem();
      if (item != null) {
         int i = this.rand.nextInt(3);
         if (lootingModifier > 0) {
            i += this.rand.nextInt(lootingModifier + 1);
         }

         for (int j = 0; j < i; j++) {
            this.dropItem(item, 1);
         }
      }
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      super.writeEntityToNBT(tagCompound);
      tagCompound.setBoolean("CanPickUpLoot", this.canPickUpLoot());
      tagCompound.setBoolean("PersistenceRequired", this.persistenceRequired);
      NBTTagList nbtTagList = new NBTTagList();

      for (int i = 0; i < this.equipment.length; i++) {
         NBTTagCompound nbtTagCompound = new NBTTagCompound();
         if (this.equipment[i] != null) {
            this.equipment[i].writeToNBT(nbtTagCompound);
         }

         nbtTagList.appendTag(nbtTagCompound);
      }

      tagCompound.setTag("Equipment", nbtTagList);
      NBTTagList nbtTagList2 = new NBTTagList();

      for (int j = 0; j < this.equipmentDropChances.length; j++) {
         nbtTagList2.appendTag(new NBTTagFloat(this.equipmentDropChances[j]));
      }

      tagCompound.setTag("DropChances", nbtTagList2);
      tagCompound.setBoolean("Leashed", this.isLeashed);
      if (this.leashedToEntity != null) {
         NBTTagCompound nbtTagCompound2 = new NBTTagCompound();
         if (this.leashedToEntity instanceof EntityLivingBase) {
            nbtTagCompound2.setLong("UUIDMost", this.leashedToEntity.getUniqueID().getMostSignificantBits());
            nbtTagCompound2.setLong("UUIDLeast", this.leashedToEntity.getUniqueID().getLeastSignificantBits());
         } else if (this.leashedToEntity instanceof EntityHanging) {
            BlockPos blockPos = ((EntityHanging)this.leashedToEntity).getHangingPosition();
            nbtTagCompound2.setInteger("X", blockPos.getX());
            nbtTagCompound2.setInteger("Y", blockPos.getY());
            nbtTagCompound2.setInteger("Z", blockPos.getZ());
         }

         tagCompound.setTag("Leash", nbtTagCompound2);
      }

      if (this.isAIDisabled()) {
         tagCompound.setBoolean("NoAI", this.isAIDisabled());
      }
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      super.readEntityFromNBT(tagCompund);
      if (tagCompund.hasKey("CanPickUpLoot", 1)) {
         this.setCanPickUpLoot(tagCompund.getBoolean("CanPickUpLoot"));
      }

      this.persistenceRequired = tagCompund.getBoolean("PersistenceRequired");
      if (tagCompund.hasKey("Equipment", 9)) {
         NBTTagList nbtTagList = tagCompund.getTagList("Equipment", 10);

         for (int i = 0; i < this.equipment.length; i++) {
            this.equipment[i] = ItemStack.loadItemStackFromNBT(nbtTagList.getCompoundTagAt(i));
         }
      }

      if (tagCompund.hasKey("DropChances", 9)) {
         NBTTagList nbtTagList2 = tagCompund.getTagList("DropChances", 5);

         for (int j = 0; j < nbtTagList2.tagCount(); j++) {
            this.equipmentDropChances[j] = nbtTagList2.getFloatAt(j);
         }
      }

      this.isLeashed = tagCompund.getBoolean("Leashed");
      if (this.isLeashed && tagCompund.hasKey("Leash", 10)) {
         this.leashNBTTag = tagCompund.getCompoundTag("Leash");
      }

      this.setNoAI(tagCompund.getBoolean("NoAI"));
   }

   public void setMoveForward(float f) {
      this.moveForward = f;
   }

   @Override
   public void setAIMoveSpeed(float speedIn) {
      super.setAIMoveSpeed(speedIn);
      this.setMoveForward(speedIn);
   }

   @Override
   public void onLivingUpdate() {
      super.onLivingUpdate();
      this.worldObj.theProfiler.startSection("looting");
      if (!this.worldObj.isRemote && this.canPickUpLoot() && !this.dead && this.worldObj.getGameRules().getBoolean("mobGriefing")) {
         for (EntityItem entityItem : this.worldObj.getEntitiesWithinAABB(EntityItem.class, this.getEntityBoundingBox().expand(1.0, 0.0, 1.0))) {
            if (!entityItem.isDead && entityItem.getEntityItem() != null && !entityItem.cannotPickup()) {
               this.updateEquipmentIfNeeded(entityItem);
            }
         }
      }

      this.worldObj.theProfiler.endSection();
   }

   public void updateEquipmentIfNeeded(EntityItem itemEntity) {
      ItemStack itemStack = itemEntity.getEntityItem();
      int i = getArmorPosition(itemStack);
      if (i > -1) {
         boolean bl = true;
         ItemStack itemStack2 = this.getEquipmentInSlot(i);
         if (itemStack2 != null) {
            if (i == 0) {
               if (itemStack.getItem() instanceof ItemSword && !(itemStack2.getItem() instanceof ItemSword)) {
                  bl = true;
               } else if (itemStack.getItem() instanceof ItemSword && itemStack2.getItem() instanceof ItemSword) {
                  ItemSword itemSword = (ItemSword)itemStack.getItem();
                  ItemSword itemSword2 = (ItemSword)itemStack2.getItem();
                  if (itemSword.getDamageVsEntity() != itemSword2.getDamageVsEntity()) {
                     bl = itemSword.getDamageVsEntity() > itemSword2.getDamageVsEntity();
                  } else {
                     bl = itemStack.getMetadata() > itemStack2.getMetadata() || itemStack.hasTagCompound() && !itemStack2.hasTagCompound();
                  }
               } else if (itemStack.getItem() instanceof ItemBow && itemStack2.getItem() instanceof ItemBow) {
                  bl = itemStack.hasTagCompound() && !itemStack2.hasTagCompound();
               } else {
                  bl = false;
               }
            } else if (itemStack.getItem() instanceof ItemArmor && !(itemStack2.getItem() instanceof ItemArmor)) {
               bl = true;
            } else if (itemStack.getItem() instanceof ItemArmor && itemStack2.getItem() instanceof ItemArmor) {
               ItemArmor itemArmor = (ItemArmor)itemStack.getItem();
               ItemArmor itemArmor2 = (ItemArmor)itemStack2.getItem();
               if (itemArmor.damageReduceAmount != itemArmor2.damageReduceAmount) {
                  bl = itemArmor.damageReduceAmount > itemArmor2.damageReduceAmount;
               } else {
                  bl = itemStack.getMetadata() > itemStack2.getMetadata() || itemStack.hasTagCompound() && !itemStack2.hasTagCompound();
               }
            } else {
               bl = false;
            }
         }

         if (bl && this.func_175448_a(itemStack)) {
            if (itemStack2 != null && this.rand.nextFloat() - 0.1F < this.equipmentDropChances[i]) {
               this.entityDropItem(itemStack2, 0.0F);
            }

            if (itemStack.getItem() == Items.diamond && itemEntity.getThrower() != null) {
               EntityPlayer entityPlayer = this.worldObj.getPlayerEntityByName(itemEntity.getThrower());
               if (entityPlayer != null) {
                  entityPlayer.triggerAchievement(AchievementList.diamondsToYou);
               }
            }

            this.setCurrentItemOrArmor(i, itemStack);
            this.equipmentDropChances[i] = 2.0F;
            this.persistenceRequired = true;
            this.onItemPickup(itemEntity, 1);
            itemEntity.setDead();
         }
      }
   }

   public boolean func_175448_a(ItemStack stack) {
      return true;
   }

   public boolean canDespawn() {
      return true;
   }

   public void despawnEntity() {
      Object object = null;
      Object object2 = Reflector.getFieldValue(Reflector.Event_Result_DEFAULT);
      Object object3 = Reflector.getFieldValue(Reflector.Event_Result_DENY);
      if (this.persistenceRequired) {
         this.entityAge = 0;
      } else if ((this.entityAge & 31) != 31 || (object = Reflector.call(Reflector.ForgeEventFactory_canEntityDespawn, this)) == object2) {
         EntityPlayer entityPlayer = this.worldObj.getClosestPlayerToEntity(this, -1.0);
         if (entityPlayer != null) {
            double d = entityPlayer.posX - this.posX;
            double e = entityPlayer.posY - this.posY;
            double d2 = entityPlayer.posZ - this.posZ;
            double d3 = d * d + e * e + d2 * d2;
            if (this.canDespawn() && d3 > 16384.0) {
               this.setDead();
            }

            if (this.entityAge > 600 && this.rand.nextInt(800) == 0 && d3 > 1024.0 && this.canDespawn()) {
               this.setDead();
            } else if (d3 < 1024.0) {
               this.entityAge = 0;
            }
         }
      } else if (object == object3) {
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

   public void faceEntity(Entity entityIn, float f, float g) {
      double d = entityIn.posX - this.posX;
      double e = entityIn.posZ - this.posZ;
      double d2;
      if (entityIn instanceof EntityLivingBase) {
         EntityLivingBase entityLivingBase = (EntityLivingBase)entityIn;
         d2 = entityLivingBase.posY + entityLivingBase.getEyeHeight() - (this.posY + this.getEyeHeight());
      } else {
         d2 = (entityIn.getEntityBoundingBox().minY + entityIn.getEntityBoundingBox().maxY) / 2.0 - (this.posY + this.getEyeHeight());
      }

      double d3 = MathHelper.sqrt_double(d * d + e * e);
      float h = (float)(MathHelper.atan2(e, d) * 180.0 / Math.PI) - 90.0F;
      float f2 = (float)(-(MathHelper.atan2(d2, d3) * 180.0 / Math.PI));
      this.rotationPitch = this.updateRotation(this.rotationPitch, f2, g);
      this.rotationYaw = this.updateRotation(this.rotationYaw, h, f);
   }

   private float updateRotation(float f, float g, float h) {
      float f2 = MathHelper.wrapAngleTo180_float(g - f);
      if (f2 > h) {
         f2 = h;
      }

      if (f2 < -h) {
         f2 = -h;
      }

      return f + f2;
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

      int i = (int)(this.getHealth() - this.getMaxHealth() * 0.33F);
      i -= (3 - this.worldObj.getDifficulty().getDifficultyId()) * 4;
      if (i < 0) {
         i = 0;
      }

      return i + 3;
   }

   @Override
   public ItemStack getHeldItem() {
      return this.equipment[0];
   }

   @Override
   public ItemStack getEquipmentInSlot(int slotIn) {
      return this.equipment[slotIn];
   }

   @Override
   public ItemStack getCurrentArmor(int slotIn) {
      return this.equipment[slotIn + 1];
   }

   @Override
   public void setCurrentItemOrArmor(int slotIn, ItemStack stack) {
      this.equipment[slotIn] = stack;
   }

   @Override
   public ItemStack[] getInventory() {
      return this.equipment;
   }

   @Override
   public void dropEquipment(boolean wasRecentlyHit, int lootingModifier) {
      for (int i = 0; i < this.getInventory().length; i++) {
         ItemStack itemStack = this.getEquipmentInSlot(i);
         boolean bl = this.equipmentDropChances[i] > 1.0F;
         if (itemStack != null && (wasRecentlyHit || bl) && this.rand.nextFloat() - lootingModifier * 0.01F < this.equipmentDropChances[i]) {
            if (!bl && itemStack.isItemStackDamageable()) {
               int j = Math.max(itemStack.getMaxDamage() - 25, 1);
               int k = itemStack.getMaxDamage() - this.rand.nextInt(this.rand.nextInt(j) + 1);
               if (k > j) {
                  k = j;
               }

               if (k < 1) {
                  k = 1;
               }

               itemStack.setItemDamage(k);
            }

            this.entityDropItem(itemStack, 0.0F);
         }
      }
   }

   public void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
      if (this.rand.nextFloat() < 0.15F * difficulty.getClampedAdditionalDifficulty()) {
         int i = this.rand.nextInt(2);
         float f = this.worldObj.getDifficulty() == EnumDifficulty.HARD ? 0.1F : 0.25F;
         if (this.rand.nextFloat() < 0.095F) {
            i++;
         }

         if (this.rand.nextFloat() < 0.095F) {
            i++;
         }

         if (this.rand.nextFloat() < 0.095F) {
            i++;
         }

         for (int j = 3; j >= 0; j--) {
            ItemStack itemStack = this.getCurrentArmor(j);
            if (j < 3 && this.rand.nextFloat() < f) {
               break;
            }

            if (itemStack == null) {
               Item item = getArmorItemForSlot(j + 1, i);
               if (item != null) {
                  this.setCurrentItemOrArmor(j + 1, new ItemStack(item));
               }
            }
         }
      }
   }

   public static int getArmorPosition(ItemStack stack) {
      if (stack.getItem() == Item.getItemFromBlock(Blocks.pumpkin) || stack.getItem() == Items.skull) {
         return 4;
      }

      if (stack.getItem() instanceof ItemArmor) {
         switch (((ItemArmor)stack.getItem()).armorType) {
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

   public static Item getArmorItemForSlot(int armorSlot, int itemTier) {
      switch (armorSlot) {
         case 4:
            if (itemTier == 0) {
               return Items.leather_helmet;
            } else if (itemTier == 1) {
               return Items.golden_helmet;
            } else if (itemTier == 2) {
               return Items.chainmail_helmet;
            } else if (itemTier == 3) {
               return Items.iron_helmet;
            } else if (itemTier == 4) {
               return Items.diamond_helmet;
            }
         case 3:
            if (itemTier == 0) {
               return Items.leather_chestplate;
            } else if (itemTier == 1) {
               return Items.golden_chestplate;
            } else if (itemTier == 2) {
               return Items.chainmail_chestplate;
            } else if (itemTier == 3) {
               return Items.iron_chestplate;
            } else if (itemTier == 4) {
               return Items.diamond_chestplate;
            }
         case 2:
            if (itemTier == 0) {
               return Items.leather_leggings;
            } else if (itemTier == 1) {
               return Items.golden_leggings;
            } else if (itemTier == 2) {
               return Items.chainmail_leggings;
            } else if (itemTier == 3) {
               return Items.iron_leggings;
            } else if (itemTier == 4) {
               return Items.diamond_leggings;
            }
         case 1:
            if (itemTier == 0) {
               return Items.leather_boots;
            } else if (itemTier == 1) {
               return Items.golden_boots;
            } else if (itemTier == 2) {
               return Items.chainmail_boots;
            } else if (itemTier == 3) {
               return Items.iron_boots;
            } else if (itemTier == 4) {
               return Items.diamond_boots;
            }
         default:
            return null;
      }
   }

   public void setEnchantmentBasedOnDifficulty(DifficultyInstance difficulty) {
      float f = difficulty.getClampedAdditionalDifficulty();
      if (this.getHeldItem() != null && this.rand.nextFloat() < 0.25F * f) {
         EnchantmentHelper.addRandomEnchantment(this.rand, this.getHeldItem(), (int)(5.0F + f * this.rand.nextInt(18)));
      }

      for (int i = 0; i < 4; i++) {
         ItemStack itemStack = this.getCurrentArmor(i);
         if (itemStack != null && this.rand.nextFloat() < 0.5F * f) {
            EnchantmentHelper.addRandomEnchantment(this.rand, itemStack, (int)(5.0F + f * this.rand.nextInt(18)));
         }
      }
   }

   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
      this.getEntityAttribute(SharedMonsterAttributes.followRange)
         .applyModifier(new AttributeModifier("Random spawn bonus", this.rand.nextGaussian() * 0.05, 1));
      return livingdata;
   }

   public boolean canBeSteered() {
      return false;
   }

   public void enablePersistence() {
      this.persistenceRequired = true;
   }

   public void setEquipmentDropChance(int slotIn, float chance) {
      this.equipmentDropChances[slotIn] = chance;
   }

   public boolean canPickUpLoot() {
      return this.canPickUpLoot;
   }

   public void setCanPickUpLoot(boolean canPickup) {
      this.canPickUpLoot = canPickup;
   }

   public boolean isNoDespawnRequired() {
      return this.persistenceRequired;
   }

   @Override
   public final boolean interactFirst(EntityPlayer playerIn) {
      if (this.getLeashed() && this.getLeashedToEntity() == playerIn) {
         this.clearLeashed(true, !playerIn.capabilities.isCreativeMode);
         return true;
      }

      ItemStack itemStack = playerIn.inventory.getCurrentItem();
      if (itemStack != null && itemStack.getItem() == Items.lead && this.allowLeashing()) {
         if (!(this instanceof EntityTameable) || !((EntityTameable)this).isTamed()) {
            this.setLeashedToEntity(playerIn, true);
            itemStack.stackSize--;
            return true;
         }

         if (((EntityTameable)this).isOwner(playerIn)) {
            this.setLeashedToEntity(playerIn, true);
            itemStack.stackSize--;
            return true;
         }
      }

      return this.interact(playerIn) ? true : super.interactFirst(playerIn);
   }

   public boolean interact(EntityPlayer player) {
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

   public void clearLeashed(boolean sendPacket, boolean dropLead) {
      if (this.isLeashed) {
         this.isLeashed = false;
         this.leashedToEntity = null;
         if (!this.worldObj.isRemote && dropLead) {
            this.dropItem(Items.lead, 1);
         }

         if (!this.worldObj.isRemote && sendPacket && this.worldObj instanceof WorldServer) {
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

   public void setLeashedToEntity(Entity entityIn, boolean sendAttachNotification) {
      this.isLeashed = true;
      this.leashedToEntity = entityIn;
      if (!this.worldObj.isRemote && sendAttachNotification && this.worldObj instanceof WorldServer) {
         ((WorldServer)this.worldObj).getEntityTracker().sendToAllTrackingEntity(this, new S1BPacketEntityAttach(1, this, this.leashedToEntity));
      }
   }

   private void recreateLeash() {
      if (this.isLeashed && this.leashNBTTag != null) {
         if (this.leashNBTTag.hasKey("UUIDMost", 4) && this.leashNBTTag.hasKey("UUIDLeast", 4)) {
            UUID uuid = new UUID(this.leashNBTTag.getLong("UUIDMost"), this.leashNBTTag.getLong("UUIDLeast"));

            for (EntityLivingBase entityLivingBase : this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().expand(10.0, 10.0, 10.0))) {
               if (entityLivingBase.getUniqueID().equals(uuid)) {
                  this.leashedToEntity = entityLivingBase;
                  break;
               }
            }
         } else if (this.leashNBTTag.hasKey("X", 99) && this.leashNBTTag.hasKey("Y", 99) && this.leashNBTTag.hasKey("Z", 99)) {
            BlockPos blockPos = new BlockPos(this.leashNBTTag.getInteger("X"), this.leashNBTTag.getInteger("Y"), this.leashNBTTag.getInteger("Z"));
            EntityLeashKnot entityLeashKnot = EntityLeashKnot.getKnotForPosition(this.worldObj, blockPos);
            if (entityLeashKnot == null) {
               entityLeashKnot = EntityLeashKnot.createKnot(this.worldObj, blockPos);
            }

            this.leashedToEntity = entityLeashKnot;
         } else {
            this.clearLeashed(false, true);
         }
      }

      this.leashNBTTag = null;
   }

   @Override
   public boolean replaceItemInInventory(int inventorySlot, ItemStack itemStackIn) {
      int i;
      if (inventorySlot == 99) {
         i = 0;
      } else {
         i = inventorySlot - 100 + 1;
         if (i < 0 || i >= this.equipment.length) {
            return false;
         }
      }

      if (itemStackIn != null && getArmorPosition(itemStackIn) != i && (i != 4 || !(itemStackIn.getItem() instanceof ItemBlock))) {
         return false;
      }

      this.setCurrentItemOrArmor(i, itemStackIn);
      return true;
   }

   @Override
   public boolean isServerWorld() {
      return super.isServerWorld() && !this.isAIDisabled();
   }

   public void setNoAI(boolean disable) {
      this.dataWatcher.updateObject(15, (byte)(disable ? 1 : 0));
   }

   public boolean isAIDisabled() {
      return this.dataWatcher.getWatchableObjectByte(15) != 0;
   }

   @Override
   public boolean isEntityInsideOpaqueBlock() {
      if (this.noClip) {
         return false;
      }

      BlockPosM blockPosM = new BlockPosM(0, 0, 0);

      for (int i = 0; i < 8; i++) {
         double d = this.posX + ((i >> 0) % 2 - 0.5F) * this.width * 0.8F;
         double e = this.posY + ((i >> 1) % 2 - 0.5F) * 0.1F;
         double d2 = this.posZ + ((i >> 2) % 2 - 0.5F) * this.width * 0.8F;
         blockPosM.setXyz(d, e + this.getEyeHeight(), d2);
         if (this.worldObj.getBlockState(blockPosM).getBlock().isVisuallyOpaque()) {
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

      World world = this.getEntityWorld();
      if (world == null) {
         return false;
      }

      if (world.playerEntities.size() != 1) {
         return false;
      }

      Entity entity = world.playerEntities.get(0);
      double d = Math.max(Math.abs(this.posX - entity.posX) - 16.0, 0.0);
      double e = Math.max(Math.abs(this.posZ - entity.posZ) - 16.0, 0.0);
      double d2 = d * d + e * e;
      return !this.isInRangeToRenderDist(d2);
   }

   private void onUpdateMinimal() {
      this.entityAge++;
      if (this instanceof EntityMob) {
         float f = this.getBrightness(1.0F);
         if (f > 0.5F) {
            this.entityAge += 2;
         }
      }

      this.despawnEntity();
   }

   public enum SpawnPlacementType {
      ON_GROUND("ON_GROUND", 0),
      IN_AIR("IN_AIR", 1),
      IN_WATER("IN_WATER", 2);

      private static final EntityLiving.SpawnPlacementType[] VALUES = new EntityLiving.SpawnPlacementType[]{ON_GROUND, IN_AIR, IN_WATER};

      SpawnPlacementType(String string2, int j) {
      }
   }
}
