package net.minecraft.entity.passive;

import com.google.common.base.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAIRunAroundLikeCrazy;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.AnimalChest;
import net.minecraft.inventory.IInvBasic;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.Potion;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class EntityHorse extends EntityAnimal implements IInvBasic {
   private static final Predicate<Entity> horseBreedingSelector = new Predicate<Entity>() {
      public boolean apply(Entity entity) {
         return entity instanceof EntityHorse && ((EntityHorse)entity).isBreeding();
      }
   };
   private static final IAttribute horseJumpStrength = new RangedAttribute((IAttribute)null, "horse.jumpStrength", 0.7, 0.0, 2.0)
      .setDescription("Jump Strength")
      .setShouldWatch(true);
   private static final String[] horseArmorTextures = new String[]{
      null,
      "textures/entity/horse/armor/horse_armor_iron.png",
      "textures/entity/horse/armor/horse_armor_gold.png",
      "textures/entity/horse/armor/horse_armor_diamond.png"
   };
   private static final String[] HORSE_ARMOR_TEXTURES_ABBR = new String[]{"", "meo", "goo", "dio"};
   private static final int[] armorValues = new int[]{0, 5, 7, 11};
   private static final String[] horseTextures = new String[]{
      "textures/entity/horse/horse_white.png",
      "textures/entity/horse/horse_creamy.png",
      "textures/entity/horse/horse_chestnut.png",
      "textures/entity/horse/horse_brown.png",
      "textures/entity/horse/horse_black.png",
      "textures/entity/horse/horse_gray.png",
      "textures/entity/horse/horse_darkbrown.png"
   };
   private static final String[] HORSE_TEXTURES_ABBR = new String[]{"hwh", "hcr", "hch", "hbr", "hbl", "hgr", "hdb"};
   private static final String[] horseMarkingTextures = new String[]{
      null,
      "textures/entity/horse/horse_markings_white.png",
      "textures/entity/horse/horse_markings_whitefield.png",
      "textures/entity/horse/horse_markings_whitedots.png",
      "textures/entity/horse/horse_markings_blackdots.png"
   };
   private static final String[] HORSE_MARKING_TEXTURES_ABBR = new String[]{"", "wo_", "wmo", "wdo", "bdo"};
   private int eatingHaystackCounter;
   private int openMouthCounter;
   private int jumpRearingCounter;
   public int field_110278_bp;
   public int field_110279_bq;
   public boolean horseJumping;
   private AnimalChest horseChest;
   private boolean hasReproduced;
   public int temper;
   public float jumpPower;
   private boolean field_110294_bI;
   private float headLean;
   private float prevHeadLean;
   private float rearingAmount;
   private float prevRearingAmount;
   private float mouthOpenness;
   private float prevMouthOpenness;
   private int gallopTime;
   private String texturePrefix;
   private String[] horseTexturesArray = new String[3];
   private boolean field_175508_bO = false;

   public EntityHorse(World worldIn) {
      super(worldIn);
      this.setSize(1.4F, 1.6F);
      this.isImmuneToFire = false;
      this.setChested(false);
      ((PathNavigateGround)this.getNavigator()).setAvoidsWater(true);
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(1, new EntityAIPanic(this, 1.2));
      this.tasks.addTask(1, new EntityAIRunAroundLikeCrazy(this, 1.2));
      this.tasks.addTask(2, new EntityAIMate(this, 1.0));
      this.tasks.addTask(4, new EntityAIFollowParent(this, 1.0));
      this.tasks.addTask(6, new EntityAIWander(this, 0.7));
      this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
      this.tasks.addTask(8, new EntityAILookIdle(this));
      this.initHorseChest();
   }

   @Override
   public void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(16, 0);
      this.dataWatcher.addObject(19, (byte)0);
      this.dataWatcher.addObject(20, 0);
      this.dataWatcher.addObject(21, String.valueOf(""));
      this.dataWatcher.addObject(22, 0);
   }

   public void setHorseType(int typeIn) {
      this.dataWatcher.updateObject(19, (byte)typeIn);
      this.resetTexturePrefix();
   }

   public int getHorseType() {
      return this.dataWatcher.getWatchableObjectByte(19);
   }

   public void setHorseVariant(int variant) {
      this.dataWatcher.updateObject(20, variant);
      this.resetTexturePrefix();
   }

   public int getHorseVariant() {
      return this.dataWatcher.getWatchableObjectInt(20);
   }

   @Override
   public String getName() {
      if (this.hasCustomName()) {
         return this.getCustomNameTag();
      }

      int i = this.getHorseType();
      switch (i) {
         case 0:
         default:
            return StatCollector.translateToLocal("entity.horse.name");
         case 1:
            return StatCollector.translateToLocal("entity.donkey.name");
         case 2:
            return StatCollector.translateToLocal("entity.mule.name");
         case 3:
            return StatCollector.translateToLocal("entity.zombiehorse.name");
         case 4:
            return StatCollector.translateToLocal("entity.skeletonhorse.name");
      }
   }

   private boolean getHorseWatchableBoolean(int i) {
      return (this.dataWatcher.getWatchableObjectInt(16) & i) != 0;
   }

   private void setHorseWatchableBoolean(int i, boolean bl) {
      int j = this.dataWatcher.getWatchableObjectInt(16);
      if (bl) {
         this.dataWatcher.updateObject(16, j | i);
      } else {
         this.dataWatcher.updateObject(16, j & ~i);
      }
   }

   public boolean isAdultHorse() {
      return !this.isChild();
   }

   public boolean isTame() {
      return this.getHorseWatchableBoolean(2);
   }

   public boolean func_110253_bW() {
      return this.isAdultHorse();
   }

   public String getOwnerId() {
      return this.dataWatcher.getWatchableObjectString(21);
   }

   public void setOwnerId(String id) {
      this.dataWatcher.updateObject(21, id);
   }

   public float getHorseSize() {
      return 0.5F;
   }

   @Override
   public void setScaleForAge(boolean bl) {
      if (bl) {
         this.setScale(this.getHorseSize());
      } else {
         this.setScale(1.0F);
      }
   }

   public boolean isHorseJumping() {
      return this.horseJumping;
   }

   public void setHorseTamed(boolean tamed) {
      this.setHorseWatchableBoolean(2, tamed);
   }

   public void setHorseJumping(boolean jumping) {
      this.horseJumping = jumping;
   }

   @Override
   public boolean allowLeashing() {
      return !this.isUndead() && super.allowLeashing();
   }

   @Override
   public void func_142017_o(float f) {
      if (f > 6.0F && this.isEatingHaystack()) {
         this.setEatingHaystack(false);
      }
   }

   public boolean isChested() {
      return this.getHorseWatchableBoolean(8);
   }

   public int getHorseArmorIndexSynced() {
      return this.dataWatcher.getWatchableObjectInt(22);
   }

   private int getHorseArmorIndex(ItemStack itemStackIn) {
      if (itemStackIn == null) {
         return 0;
      }

      Item item = itemStackIn.getItem();
      return item == Items.iron_horse_armor ? 1 : (item == Items.golden_horse_armor ? 2 : (item == Items.diamond_horse_armor ? 3 : 0));
   }

   public boolean isEatingHaystack() {
      return this.getHorseWatchableBoolean(32);
   }

   public boolean isRearing() {
      return this.getHorseWatchableBoolean(64);
   }

   public boolean isBreeding() {
      return this.getHorseWatchableBoolean(16);
   }

   public boolean getHasReproduced() {
      return this.hasReproduced;
   }

   public void setHorseArmorStack(ItemStack itemStackIn) {
      this.dataWatcher.updateObject(22, this.getHorseArmorIndex(itemStackIn));
      this.resetTexturePrefix();
   }

   public void setBreeding(boolean breeding) {
      this.setHorseWatchableBoolean(16, breeding);
   }

   public void setChested(boolean chested) {
      this.setHorseWatchableBoolean(8, chested);
   }

   public void setHasReproduced(boolean hasReproducedIn) {
      this.hasReproduced = hasReproducedIn;
   }

   public void setHorseSaddled(boolean saddled) {
      this.setHorseWatchableBoolean(4, saddled);
   }

   public int getTemper() {
      return this.temper;
   }

   public void setTemper(int temperIn) {
      this.temper = temperIn;
   }

   public int increaseTemper(int i) {
      int j = MathHelper.clamp_int(this.getTemper() + i, 0, this.getMaxTemper());
      this.setTemper(j);
      return j;
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      Entity entity = source.getEntity();
      return this.riddenByEntity != null && this.riddenByEntity.equals(entity) ? false : super.attackEntityFrom(source, amount);
   }

   @Override
   public int getTotalArmorValue() {
      return armorValues[this.getHorseArmorIndexSynced()];
   }

   @Override
   public boolean canBePushed() {
      return this.riddenByEntity == null;
   }

   public boolean prepareChunkForSpawn() {
      int i = MathHelper.floor_double(this.posX);
      int j = MathHelper.floor_double(this.posZ);
      this.worldObj.getBiomeGenForCoords(new BlockPos(i, 0, j));
      return true;
   }

   public void dropChests() {
      if (!this.worldObj.isRemote && this.isChested()) {
         this.dropItem(Item.getItemFromBlock(Blocks.chest), 1);
         this.setChested(false);
      }
   }

   private void func_110266_cB() {
      this.openHorseMouth();
      if (!this.isSilent()) {
         this.worldObj.playSoundAtEntity(this, "eating", 1.0F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
      }
   }

   @Override
   public void fall(float distance, float damageMultiplier) {
      if (distance > 1.0F) {
         this.playSound("mob.horse.land", 0.4F, 1.0F);
      }

      int i = MathHelper.ceiling_float_int((distance * 0.5F - 3.0F) * damageMultiplier);
      if (i > 0) {
         this.attackEntityFrom(DamageSource.fall, i);
         if (this.riddenByEntity != null) {
            this.riddenByEntity.attackEntityFrom(DamageSource.fall, i);
         }

         Block block = this.worldObj.getBlockState(new BlockPos(this.posX, this.posY - 0.2 - this.prevRotationYaw, this.posZ)).getBlock();
         if (block.getMaterial() != Material.air && !this.isSilent()) {
            Block.SoundType soundType = block.stepSound;
            this.worldObj.playSoundAtEntity(this, soundType.getStepSound(), soundType.getVolume() * 0.5F, soundType.getFrequency() * 0.75F);
         }
      }
   }

   private int getChestSize() {
      int i = this.getHorseType();
      return this.isChested() && (i == 1 || i == 2) ? 17 : 2;
   }

   private void initHorseChest() {
      AnimalChest animalChest = this.horseChest;
      this.horseChest = new AnimalChest("HorseChest", this.getChestSize());
      this.horseChest.setCustomName(this.getName());
      if (animalChest != null) {
         animalChest.removeInventoryChangeListener(this);
         int i = Math.min(animalChest.getSizeInventory(), this.horseChest.getSizeInventory());

         for (int j = 0; j < i; j++) {
            ItemStack itemStack = animalChest.getStackInSlot(j);
            if (itemStack != null) {
               this.horseChest.setInventorySlotContents(j, itemStack.copy());
            }
         }
      }

      this.horseChest.addInventoryChangeListener(this);
      this.updateHorseSlots();
   }

   private void updateHorseSlots() {
      if (!this.worldObj.isRemote) {
         this.setHorseSaddled(this.horseChest.getStackInSlot(0) != null);
         if (this.canWearArmor()) {
            this.setHorseArmorStack(this.horseChest.getStackInSlot(1));
         }
      }
   }

   public void O(float f, float g, float h) {
      EntityPlayerSP entityPlayerSP = Minecraft.getMinecraft().thePlayer;
      this.rotationYaw = entityPlayerSP.rotationYaw;
      this.prevRotationYaw = this.rotationYaw;
      this.rotationPitch = entityPlayerSP.rotationPitch * 0.5F;
      this.setRotation(this.rotationYaw, this.rotationPitch);
      this.renderYawOffset = this.rotationYaw;
      this.rotationYawHead = this.renderYawOffset;
      f = entityPlayerSP.moveStrafing * 0.5F;
      h = entityPlayerSP.moveForward;
      if (h <= 0.0F) {
         h *= 0.25F;
         this.gallopTime = 0;
      }

      if (this.onGround && this.jumpPower == 0.0F && this.isRearing()) {
         f = 0.0F;
         h = 0.0F;
      }

      if (this.jumpPower > 0.0F && !this.isHorseJumping() && this.onGround) {
         this.motionY = this.getHorseJumpStrength() * this.jumpPower;
         this.setHorseJumping(true);
         this.isAirBorne = true;
         if (h > 0.0F) {
            float f2 = MathHelper.sin(this.rotationYaw * (float) (Math.PI / 180.0));
            float f3 = MathHelper.cos(this.rotationYaw * (float) (Math.PI / 180.0));
            this.motionX = this.motionX + -0.4F * f2 * this.jumpPower;
            this.motionZ = this.motionZ + 0.4F * f3 * this.jumpPower;
         }

         this.jumpPower = 0.0F;
      }

      this.jumpMovementFactor = this.getAIMoveSpeed() * 0.1F;
      this.setAIMoveSpeed((float)this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue());
      super.moveEntityWithHeading(f, g);
      if (this.onGround) {
         this.jumpPower = 0.0F;
         this.setHorseJumping(false);
      }

      this.prevLimbSwingAmount = this.limbSwingAmount;
      double d = this.posX - this.prevPosX;
      double e = this.posZ - this.prevPosZ;
      float f4 = MathHelper.sqrt_double(d * d + e * e) * 4.0F;
      if (f4 > 1.0F) {
         f4 = 1.0F;
      }

      this.limbSwingAmount = this.limbSwingAmount + (f4 - this.limbSwingAmount) * 0.4F;
      this.limbSwing = this.limbSwing + this.limbSwingAmount;
   }

   @Override
   public void onInventoryChanged(InventoryBasic inventoryBasic) {
      int i = this.getHorseArmorIndexSynced();
      boolean bl = this.isHorseSaddled();
      this.updateHorseSlots();
      if (this.ticksExisted > 20) {
         if (i == 0 && i != this.getHorseArmorIndexSynced()) {
            this.playSound("mob.horse.armor", 0.5F, 1.0F);
         } else if (i != this.getHorseArmorIndexSynced()) {
            this.playSound("mob.horse.armor", 0.5F, 1.0F);
         }

         if (!bl && this.isHorseSaddled()) {
            this.playSound("mob.horse.leather", 0.5F, 1.0F);
         }
      }
   }

   @Override
   public boolean getCanSpawnHere() {
      this.prepareChunkForSpawn();
      return super.getCanSpawnHere();
   }

   public EntityHorse getClosestHorse(Entity entityIn, double distance) {
      double d = Double.MAX_VALUE;
      Entity entity = null;

      for (Entity entity2 : this.worldObj
         .getEntitiesInAABBexcluding(entityIn, entityIn.getEntityBoundingBox().addCoord(distance, distance, distance), horseBreedingSelector)) {
         double e = entity2.getDistanceSq(entityIn.posX, entityIn.posY, entityIn.posZ);
         if (e < d) {
            entity = entity2;
            d = e;
         }
      }

      return (EntityHorse)entity;
   }

   public double getHorseJumpStrength() {
      return this.getEntityAttribute(horseJumpStrength).getAttributeValue();
   }

   @Override
   public String getDeathSound() {
      this.openHorseMouth();
      int i = this.getHorseType();
      return i == 3
         ? "mob.horse.zombie.death"
         : (i == 4 ? "mob.horse.skeleton.death" : (i != 1 && i != 2 ? "mob.horse.death" : "mob.horse.donkey.death"));
   }

   @Override
   public Item getDropItem() {
      boolean bl = this.rand.nextInt(4) == 0;
      int i = this.getHorseType();
      return i == 4 ? Items.bone : (i == 3 ? (bl ? null : Items.rotten_flesh) : Items.leather);
   }

   @Override
   public String getHurtSound() {
      this.openHorseMouth();
      if (this.rand.nextInt(3) == 0) {
         this.makeHorseRear();
      }

      int i = this.getHorseType();
      return i == 3 ? "mob.horse.zombie.hit" : (i == 4 ? "mob.horse.skeleton.hit" : (i != 1 && i != 2 ? "mob.horse.hit" : "mob.horse.donkey.hit"));
   }

   public boolean isHorseSaddled() {
      return this.getHorseWatchableBoolean(4);
   }

   @Override
   public String getLivingSound() {
      this.openHorseMouth();
      if (this.rand.nextInt(10) == 0 && !this.isMovementBlocked()) {
         this.makeHorseRear();
      }

      int i = this.getHorseType();
      return i == 3
         ? "mob.horse.zombie.idle"
         : (i == 4 ? "mob.horse.skeleton.idle" : (i != 1 && i != 2 ? "mob.horse.idle" : "mob.horse.donkey.idle"));
   }

   public String getAngrySoundName() {
      this.openHorseMouth();
      this.makeHorseRear();
      int i = this.getHorseType();
      return i != 3 && i != 4 ? (i != 1 && i != 2 ? "mob.horse.angry" : "mob.horse.donkey.angry") : null;
   }

   @Override
   public void playStepSound(BlockPos pos, Block blockIn) {
      Block.SoundType soundType = blockIn.stepSound;
      if (this.worldObj.getBlockState(pos.up()).getBlock() == Blocks.snow_layer) {
         soundType = Blocks.snow_layer.stepSound;
      }

      if (!blockIn.getMaterial().isLiquid()) {
         int i = this.getHorseType();
         if (this.riddenByEntity != null && i != 1 && i != 2) {
            this.gallopTime++;
            if (this.gallopTime > 5 && this.gallopTime % 3 == 0) {
               this.playSound("mob.horse.gallop", soundType.getVolume() * 0.15F, soundType.getFrequency());
               if (i == 0 && this.rand.nextInt(10) == 0) {
                  this.playSound("mob.horse.breathe", soundType.getVolume() * 0.6F, soundType.getFrequency());
               }
            } else if (this.gallopTime <= 5) {
               this.playSound("mob.horse.wood", soundType.getVolume() * 0.15F, soundType.getFrequency());
            }
         } else if (soundType == Block.soundTypeWood) {
            this.playSound("mob.horse.wood", soundType.getVolume() * 0.15F, soundType.getFrequency());
         } else {
            this.playSound("mob.horse.soft", soundType.getVolume() * 0.15F, soundType.getFrequency());
         }
      }
   }

   @Override
   public void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getAttributeMap().registerAttribute(horseJumpStrength);
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(53.0);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.225F);
   }

   @Override
   public int getMaxSpawnedInChunk() {
      return 6;
   }

   public int getMaxTemper() {
      return 100;
   }

   @Override
   public float getSoundVolume() {
      return 0.8F;
   }

   @Override
   public int getTalkInterval() {
      return 400;
   }

   public boolean func_110239_cn() {
      return this.getHorseType() == 0 || this.getHorseArmorIndexSynced() > 0;
   }

   private void resetTexturePrefix() {
      this.texturePrefix = null;
   }

   public boolean func_175507_cI() {
      return this.field_175508_bO;
   }

   private void setHorseTexturePaths() {
      this.texturePrefix = "horse/";
      this.horseTexturesArray[0] = null;
      this.horseTexturesArray[1] = null;
      this.horseTexturesArray[2] = null;
      int i = this.getHorseType();
      int j = this.getHorseVariant();
      if (i == 0) {
         int k = j & 0xFF;
         int l = (j & 0xFF00) >> 8;
         if (k >= horseTextures.length) {
            this.field_175508_bO = false;
            return;
         }

         this.horseTexturesArray[0] = horseTextures[k];
         this.texturePrefix = this.texturePrefix + HORSE_TEXTURES_ABBR[k];
         if (l >= horseMarkingTextures.length) {
            this.field_175508_bO = false;
            return;
         }

         this.horseTexturesArray[1] = horseMarkingTextures[l];
         this.texturePrefix = this.texturePrefix + HORSE_MARKING_TEXTURES_ABBR[l];
      } else {
         this.horseTexturesArray[0] = "";
         this.texturePrefix = this.texturePrefix + "_" + i + "_";
      }

      int m = this.getHorseArmorIndexSynced();
      if (m >= horseArmorTextures.length) {
         this.field_175508_bO = false;
      } else {
         this.horseTexturesArray[2] = horseArmorTextures[m];
         this.texturePrefix = this.texturePrefix + HORSE_ARMOR_TEXTURES_ABBR[m];
         this.field_175508_bO = true;
      }
   }

   public String getHorseTexture() {
      if (this.texturePrefix == null) {
         this.setHorseTexturePaths();
      }

      return this.texturePrefix;
   }

   public String[] getVariantTexturePaths() {
      if (this.texturePrefix == null) {
         this.setHorseTexturePaths();
      }

      return this.horseTexturesArray;
   }

   public void openGUI(EntityPlayer playerEntity) {
      if (!this.worldObj.isRemote && (this.riddenByEntity == null || this.riddenByEntity == playerEntity) && this.isTame()) {
         this.horseChest.setCustomName(this.getName());
         playerEntity.displayGUIHorse(this, this.horseChest);
      }
   }

   @Override
   public boolean interact(EntityPlayer player) {
      ItemStack itemStack = player.inventory.getCurrentItem();
      if (itemStack != null && itemStack.getItem() == Items.spawn_egg) {
         return super.interact(player);
      }

      if (!this.isTame() && this.isUndead()) {
         return false;
      }

      if (this.isTame() && this.isAdultHorse() && player.isSneaking()) {
         this.openGUI(player);
         return true;
      }

      if (this.func_110253_bW() && this.riddenByEntity != null) {
         return super.interact(player);
      }

      if (itemStack != null) {
         boolean bl = false;
         if (this.canWearArmor()) {
            byte b = -1;
            if (itemStack.getItem() == Items.iron_horse_armor) {
               b = 1;
            } else if (itemStack.getItem() == Items.golden_horse_armor) {
               b = 2;
            } else if (itemStack.getItem() == Items.diamond_horse_armor) {
               b = 3;
            }

            if (b >= 0) {
               if (!this.isTame()) {
                  this.makeHorseRearWithSound();
                  return true;
               }

               this.openGUI(player);
               return true;
            }
         }

         if (!bl && !this.isUndead()) {
            float f = 0.0F;
            short s = 0;
            byte b2 = 0;
            if (itemStack.getItem() == Items.wheat) {
               f = 2.0F;
               s = 20;
               b2 = 3;
            } else if (itemStack.getItem() == Items.sugar) {
               f = 1.0F;
               s = 30;
               b2 = 3;
            } else if (Block.getBlockFromItem(itemStack.getItem()) == Blocks.hay_block) {
               f = 20.0F;
               s = 180;
            } else if (itemStack.getItem() == Items.apple) {
               f = 3.0F;
               s = 60;
               b2 = 3;
            } else if (itemStack.getItem() == Items.golden_carrot) {
               f = 4.0F;
               s = 60;
               b2 = 5;
               if (this.isTame() && this.getGrowingAge() == 0) {
                  bl = true;
                  this.setInLove(player);
               }
            } else if (itemStack.getItem() == Items.golden_apple) {
               f = 10.0F;
               s = 240;
               b2 = 10;
               if (this.isTame() && this.getGrowingAge() == 0) {
                  bl = true;
                  this.setInLove(player);
               }
            }

            if (this.getHealth() < this.getMaxHealth() && f > 0.0F) {
               this.heal(f);
               bl = true;
            }

            if (!this.isAdultHorse() && s > 0) {
               this.addGrowth(s);
               bl = true;
            }

            if (b2 > 0 && (bl || !this.isTame()) && b2 < this.getMaxTemper()) {
               bl = true;
               this.increaseTemper(b2);
            }

            if (bl) {
               this.func_110266_cB();
            }
         }

         if (!this.isTame() && !bl) {
            if (itemStack != null && itemStack.interactWithEntity(player, this)) {
               return true;
            }

            this.makeHorseRearWithSound();
            return true;
         }

         if (!bl && this.canCarryChest() && !this.isChested() && itemStack.getItem() == Item.getItemFromBlock(Blocks.chest)) {
            this.setChested(true);
            this.playSound("mob.chickenplop", 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            bl = true;
            this.initHorseChest();
         }

         if (!bl && this.func_110253_bW() && !this.isHorseSaddled() && itemStack.getItem() == Items.saddle) {
            this.openGUI(player);
            return true;
         }

         if (bl) {
            if (!player.capabilities.isCreativeMode && --itemStack.stackSize == 0) {
               player.inventory.setInventorySlotContents(player.inventory.currentItem, (ItemStack)null);
            }

            return true;
         }
      }

      if (!this.func_110253_bW() || this.riddenByEntity != null) {
         return super.interact(player);
      }

      if (itemStack != null && itemStack.interactWithEntity(player, this)) {
         return true;
      }

      this.mountTo(player);
      return true;
   }

   private void mountTo(EntityPlayer player) {
      player.rotationYaw = this.rotationYaw;
      player.rotationPitch = this.rotationPitch;
      this.setEatingHaystack(false);
      this.setRearing(false);
      if (!this.worldObj.isRemote) {
         player.mountEntity(this);
      }
   }

   public boolean canWearArmor() {
      return this.getHorseType() == 0;
   }

   public boolean canCarryChest() {
      int i = this.getHorseType();
      return i == 2 || i == 1;
   }

   @Override
   public boolean isMovementBlocked() {
      return this.riddenByEntity != null && this.isHorseSaddled() ? true : this.isEatingHaystack() || this.isRearing();
   }

   public boolean isUndead() {
      int i = this.getHorseType();
      return i == 3 || i == 4;
   }

   public boolean isSterile() {
      return this.isUndead() || this.getHorseType() == 2;
   }

   @Override
   public boolean isBreedingItem(ItemStack stack) {
      return false;
   }

   private void func_110210_cH() {
      this.field_110278_bp = 1;
   }

   @Override
   public void onDeath(DamageSource cause) {
      super.onDeath(cause);
      if (!this.worldObj.isRemote) {
         this.dropChestItems();
      }
   }

   @Override
   public void onLivingUpdate() {
      if (this.rand.nextInt(200) == 0) {
         this.func_110210_cH();
      }

      super.onLivingUpdate();
      if (!this.worldObj.isRemote) {
         if (this.rand.nextInt(900) == 0 && this.deathTime == 0) {
            this.heal(1.0F);
         }

         if (!this.isEatingHaystack()
            && this.riddenByEntity == null
            && this.rand.nextInt(300) == 0
            && this.worldObj
                  .getBlockState(new BlockPos(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY) - 1, MathHelper.floor_double(this.posZ)))
                  .getBlock()
               == Blocks.grass) {
            this.setEatingHaystack(true);
         }

         if (this.isEatingHaystack() && ++this.eatingHaystackCounter > 50) {
            this.eatingHaystackCounter = 0;
            this.setEatingHaystack(false);
         }

         if (this.isBreeding() && !this.isAdultHorse() && !this.isEatingHaystack()) {
            EntityHorse entityHorse = this.getClosestHorse(this, 16.0);
            if (entityHorse != null && this.getDistanceSqToEntity(entityHorse) > 4.0) {
               this.navigator.getPathToEntityLiving(entityHorse);
            }
         }
      }
   }

   @Override
   public void onUpdate() {
      super.onUpdate();
      if (this.worldObj.isRemote && this.dataWatcher.hasObjectChanged()) {
         this.dataWatcher.func_111144_e();
         this.resetTexturePrefix();
      }

      if (this.openMouthCounter > 0 && ++this.openMouthCounter > 30) {
         this.openMouthCounter = 0;
         this.setHorseWatchableBoolean(128, false);
      }

      if (!this.worldObj.isRemote && this.jumpRearingCounter > 0 && ++this.jumpRearingCounter > 20) {
         this.jumpRearingCounter = 0;
         this.setRearing(false);
      }

      if (this.field_110278_bp > 0 && ++this.field_110278_bp > 8) {
         this.field_110278_bp = 0;
      }

      if (this.field_110279_bq > 0) {
         this.field_110279_bq++;
         if (this.field_110279_bq > 300) {
            this.field_110279_bq = 0;
         }
      }

      this.prevHeadLean = this.headLean;
      if (this.isEatingHaystack()) {
         this.headLean = this.headLean + ((1.0F - this.headLean) * 0.4F + 0.05F);
         if (this.headLean > 1.0F) {
            this.headLean = 1.0F;
         }
      } else {
         this.headLean = this.headLean + ((0.0F - this.headLean) * 0.4F - 0.05F);
         if (this.headLean < 0.0F) {
            this.headLean = 0.0F;
         }
      }

      this.prevRearingAmount = this.rearingAmount;
      if (this.isRearing()) {
         this.prevHeadLean = this.headLean = 0.0F;
         this.rearingAmount = this.rearingAmount + ((1.0F - this.rearingAmount) * 0.4F + 0.05F);
         if (this.rearingAmount > 1.0F) {
            this.rearingAmount = 1.0F;
         }
      } else {
         this.field_110294_bI = false;
         this.rearingAmount = this.rearingAmount + ((0.8F * this.rearingAmount * this.rearingAmount * this.rearingAmount - this.rearingAmount) * 0.6F - 0.05F);
         if (this.rearingAmount < 0.0F) {
            this.rearingAmount = 0.0F;
         }
      }

      this.prevMouthOpenness = this.mouthOpenness;
      if (this.getHorseWatchableBoolean(128)) {
         this.mouthOpenness = this.mouthOpenness + ((1.0F - this.mouthOpenness) * 0.7F + 0.05F);
         if (this.mouthOpenness > 1.0F) {
            this.mouthOpenness = 1.0F;
         }
      } else {
         this.mouthOpenness = this.mouthOpenness + ((0.0F - this.mouthOpenness) * 0.7F - 0.05F);
         if (this.mouthOpenness < 0.0F) {
            this.mouthOpenness = 0.0F;
         }
      }
   }

   private void openHorseMouth() {
      if (!this.worldObj.isRemote) {
         this.openMouthCounter = 1;
         this.setHorseWatchableBoolean(128, true);
      }
   }

   private boolean canMate() {
      return this.riddenByEntity == null
         && this.ridingEntity == null
         && this.isTame()
         && this.isAdultHorse()
         && !this.isSterile()
         && this.getHealth() >= this.getMaxHealth()
         && this.isInLove();
   }

   @Override
   public void setEating(boolean eating) {
      this.setHorseWatchableBoolean(32, eating);
   }

   public void setEatingHaystack(boolean bl) {
      this.setEating(bl);
   }

   public void setRearing(boolean rearing) {
      if (rearing) {
         this.setEatingHaystack(false);
      }

      this.setHorseWatchableBoolean(64, rearing);
   }

   private void makeHorseRear() {
      if (!this.worldObj.isRemote) {
         this.jumpRearingCounter = 1;
         this.setRearing(true);
      }
   }

   public void makeHorseRearWithSound() {
      this.makeHorseRear();
      String string = this.getAngrySoundName();
      if (string != null) {
         this.playSound(string, this.getSoundVolume(), this.getSoundPitch());
      }
   }

   public void dropChestItems() {
      this.dropItemsInChest(this, this.horseChest);
      this.dropChests();
   }

   private void dropItemsInChest(Entity entityIn, AnimalChest animalChestIn) {
      if (animalChestIn != null && !this.worldObj.isRemote) {
         for (int i = 0; i < animalChestIn.getSizeInventory(); i++) {
            ItemStack itemStack = animalChestIn.getStackInSlot(i);
            if (itemStack != null) {
               this.entityDropItem(itemStack, 0.0F);
            }
         }
      }
   }

   public boolean setTamedBy(EntityPlayer player) {
      this.setOwnerId(player.getUniqueID().toString());
      this.setHorseTamed(true);
      return true;
   }

   @Override
   public void moveEntityWithHeading(float strafe, float forward) {
      if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityLivingBase && this.isHorseSaddled()) {
         this.prevRotationYaw = this.rotationYaw = this.riddenByEntity.rotationYaw;
         this.rotationPitch = this.riddenByEntity.rotationPitch * 0.5F;
         this.setRotation(this.rotationYaw, this.rotationPitch);
         this.rotationYawHead = this.renderYawOffset = this.rotationYaw;
         strafe = ((EntityLivingBase)this.riddenByEntity).moveStrafing * 0.5F;
         forward = ((EntityLivingBase)this.riddenByEntity).moveForward;
         if (forward <= 0.0F) {
            forward *= 0.25F;
            this.gallopTime = 0;
         }

         if (this.onGround && this.jumpPower == 0.0F && this.isRearing() && !this.field_110294_bI) {
            strafe = 0.0F;
            forward = 0.0F;
         }

         if (this.jumpPower > 0.0F && !this.isHorseJumping() && this.onGround) {
            this.motionY = this.getHorseJumpStrength() * this.jumpPower;
            if (this.isPotionActive(Potion.jump)) {
               this.motionY = this.motionY + (this.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F;
            }

            this.setHorseJumping(true);
            this.isAirBorne = true;
            if (forward > 0.0F) {
               float f = MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F);
               float g = MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F);
               this.motionX = this.motionX + -0.4F * f * this.jumpPower;
               this.motionZ = this.motionZ + 0.4F * g * this.jumpPower;
               this.playSound("mob.horse.jump", 0.4F, 1.0F);
            }

            this.jumpPower = 0.0F;
         }

         this.stepHeight = 1.0F;
         this.jumpMovementFactor = this.getAIMoveSpeed() * 0.1F;
         if (!this.worldObj.isRemote) {
            this.setAIMoveSpeed((float)this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue());
            super.moveEntityWithHeading(strafe, forward);
         }

         if (this.onGround) {
            this.jumpPower = 0.0F;
            this.setHorseJumping(false);
         }

         this.prevLimbSwingAmount = this.limbSwingAmount;
         double d = this.posX - this.prevPosX;
         double e = this.posZ - this.prevPosZ;
         float h = MathHelper.sqrt_double(d * d + e * e) * 4.0F;
         if (h > 1.0F) {
            h = 1.0F;
         }

         this.limbSwingAmount = this.limbSwingAmount + (h - this.limbSwingAmount) * 0.4F;
         this.limbSwing = this.limbSwing + this.limbSwingAmount;
      } else {
         this.stepHeight = 0.5F;
         this.jumpMovementFactor = 0.02F;
         super.moveEntityWithHeading(strafe, forward);
      }
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      super.writeEntityToNBT(tagCompound);
      tagCompound.setBoolean("EatingHaystack", this.isEatingHaystack());
      tagCompound.setBoolean("ChestedHorse", this.isChested());
      tagCompound.setBoolean("HasReproduced", this.getHasReproduced());
      tagCompound.setBoolean("Bred", this.isBreeding());
      tagCompound.setInteger("Type", this.getHorseType());
      tagCompound.setInteger("Variant", this.getHorseVariant());
      tagCompound.setInteger("Temper", this.getTemper());
      tagCompound.setBoolean("Tame", this.isTame());
      tagCompound.setString("OwnerUUID", this.getOwnerId());
      if (this.isChested()) {
         NBTTagList nbtTagList = new NBTTagList();

         for (int i = 2; i < this.horseChest.getSizeInventory(); i++) {
            ItemStack itemStack = this.horseChest.getStackInSlot(i);
            if (itemStack != null) {
               NBTTagCompound nbtTagCompound = new NBTTagCompound();
               nbtTagCompound.setByte("Slot", (byte)i);
               itemStack.writeToNBT(nbtTagCompound);
               nbtTagList.appendTag(nbtTagCompound);
            }
         }

         tagCompound.setTag("Items", nbtTagList);
      }

      if (this.horseChest.getStackInSlot(1) != null) {
         tagCompound.setTag("ArmorItem", this.horseChest.getStackInSlot(1).writeToNBT(new NBTTagCompound()));
      }

      if (this.horseChest.getStackInSlot(0) != null) {
         tagCompound.setTag("SaddleItem", this.horseChest.getStackInSlot(0).writeToNBT(new NBTTagCompound()));
      }
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      super.readEntityFromNBT(tagCompund);
      this.setEatingHaystack(tagCompund.getBoolean("EatingHaystack"));
      this.setBreeding(tagCompund.getBoolean("Bred"));
      this.setChested(tagCompund.getBoolean("ChestedHorse"));
      this.setHasReproduced(tagCompund.getBoolean("HasReproduced"));
      this.setHorseType(tagCompund.getInteger("Type"));
      this.setHorseVariant(tagCompund.getInteger("Variant"));
      this.setTemper(tagCompund.getInteger("Temper"));
      this.setHorseTamed(tagCompund.getBoolean("Tame"));
      String string = "";
      if (tagCompund.hasKey("OwnerUUID", 8)) {
         string = tagCompund.getString("OwnerUUID");
      } else {
         String string2 = tagCompund.getString("Owner");
         string = PreYggdrasilConverter.getStringUUIDFromName(string2);
      }

      if (string.length() > 0) {
         this.setOwnerId(string);
      }

      IAttributeInstance iAttributeInstance = this.getAttributeMap().getAttributeInstanceByName("Speed");
      if (iAttributeInstance != null) {
         this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(iAttributeInstance.getBaseValue() * 0.25);
      }

      if (this.isChested()) {
         NBTTagList nbtTagList = tagCompund.getTagList("Items", 10);
         this.initHorseChest();

         for (int i = 0; i < nbtTagList.tagCount(); i++) {
            NBTTagCompound nbtTagCompound = nbtTagList.getCompoundTagAt(i);
            int j = nbtTagCompound.getByte("Slot") & 255;
            if (j >= 2 && j < this.horseChest.getSizeInventory()) {
               this.horseChest.setInventorySlotContents(j, ItemStack.loadItemStackFromNBT(nbtTagCompound));
            }
         }
      }

      if (tagCompund.hasKey("ArmorItem", 10)) {
         ItemStack itemStack = ItemStack.loadItemStackFromNBT(tagCompund.getCompoundTag("ArmorItem"));
         if (itemStack != null && isArmorItem(itemStack.getItem())) {
            this.horseChest.setInventorySlotContents(1, itemStack);
         }
      }

      if (tagCompund.hasKey("SaddleItem", 10)) {
         ItemStack itemStack2 = ItemStack.loadItemStackFromNBT(tagCompund.getCompoundTag("SaddleItem"));
         if (itemStack2 != null && itemStack2.getItem() == Items.saddle) {
            this.horseChest.setInventorySlotContents(0, itemStack2);
         }
      } else if (tagCompund.getBoolean("Saddle")) {
         this.horseChest.setInventorySlotContents(0, new ItemStack(Items.saddle));
      }

      this.updateHorseSlots();
   }

   @Override
   public boolean canMateWith(EntityAnimal otherAnimal) {
      if (otherAnimal == this) {
         return false;
      } else if (otherAnimal.getClass() != this.getClass()) {
         return false;
      } else {
         EntityHorse entityHorse = (EntityHorse)otherAnimal;
         if (this.canMate() && entityHorse.canMate()) {
            int i = this.getHorseType();
            int j = entityHorse.getHorseType();
            return i == j || i == 0 && j == 1 || i == 1 && j == 0;
         } else {
            return false;
         }
      }
   }

   @Override
   public EntityAgeable createChild(EntityAgeable ageable) {
      EntityHorse entityHorse = (EntityHorse)ageable;
      EntityHorse entityHorse2 = new EntityHorse(this.worldObj);
      int i = this.getHorseType();
      int j = entityHorse.getHorseType();
      int k = 0;
      if (i == j) {
         k = i;
      } else if (i == 0 && j == 1 || i == 1 && j == 0) {
         k = 2;
      }

      if (k == 0) {
         int l = this.rand.nextInt(9);
         int m;
         if (l < 4) {
            m = this.getHorseVariant() & 0xFF;
         } else if (l < 8) {
            m = entityHorse.getHorseVariant() & 0xFF;
         } else {
            m = this.rand.nextInt(7);
         }

         int n = this.rand.nextInt(5);
         if (n < 2) {
            m |= this.getHorseVariant() & 0xFF00;
         } else if (n < 4) {
            m |= entityHorse.getHorseVariant() & 0xFF00;
         } else {
            m |= this.rand.nextInt(5) << 8 & 0xFF00;
         }

         entityHorse2.setHorseVariant(m);
      }

      entityHorse2.setHorseType(k);
      double d = this.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue()
         + ageable.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue()
         + this.getModifiedMaxHealth();
      entityHorse2.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(d / 3.0);
      double e = this.getEntityAttribute(horseJumpStrength).getBaseValue()
         + ageable.getEntityAttribute(horseJumpStrength).getBaseValue()
         + this.getModifiedJumpStrength();
      entityHorse2.getEntityAttribute(horseJumpStrength).setBaseValue(e / 3.0);
      double d2 = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getBaseValue()
         + ageable.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getBaseValue()
         + this.getModifiedMovementSpeed();
      entityHorse2.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(d2 / 3.0);
      return entityHorse2;
   }

   @Override
   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
      livingdata = super.onInitialSpawn(difficulty, livingdata);
      int i = 0;
      int j = 0;
      if (livingdata instanceof EntityHorse.GroupData) {
         i = ((EntityHorse.GroupData)livingdata).horseType;
         j = ((EntityHorse.GroupData)livingdata).horseVariant & 0xFF | this.rand.nextInt(5) << 8;
      } else {
         if (this.rand.nextInt(10) == 0) {
            i = 1;
         } else {
            int k = this.rand.nextInt(7);
            int l = this.rand.nextInt(5);
            i = 0;
            j = k | l << 8;
         }

         livingdata = new EntityHorse.GroupData(i, j);
      }

      this.setHorseType(i);
      this.setHorseVariant(j);
      if (this.rand.nextInt(5) == 0) {
         this.setGrowingAge(-24000);
      }

      if (i != 4 && i != 3) {
         this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(this.getModifiedMaxHealth());
         if (i == 0) {
            this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(this.getModifiedMovementSpeed());
         } else {
            this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.175F);
         }
      } else {
         this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(15.0);
         this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.2F);
      }

      if (i != 2 && i != 1) {
         this.getEntityAttribute(horseJumpStrength).setBaseValue(this.getModifiedJumpStrength());
      } else {
         this.getEntityAttribute(horseJumpStrength).setBaseValue(0.5);
      }

      this.setHealth(this.getMaxHealth());
      return livingdata;
   }

   public float getGrassEatingAmount(float f) {
      return this.prevHeadLean + (this.headLean - this.prevHeadLean) * f;
   }

   public float getRearingAmount(float f) {
      return this.prevRearingAmount + (this.rearingAmount - this.prevRearingAmount) * f;
   }

   public float getMouthOpennessAngle(float f) {
      return this.prevMouthOpenness + (this.mouthOpenness - this.prevMouthOpenness) * f;
   }

   public void setJumpPower(int jumpPowerIn) {
      if (this.isHorseSaddled()) {
         if (jumpPowerIn < 0) {
            jumpPowerIn = 0;
         } else {
            this.field_110294_bI = true;
            this.makeHorseRear();
         }

         if (jumpPowerIn >= 90) {
            this.jumpPower = 1.0F;
         } else {
            this.jumpPower = 0.4F + 0.4F * jumpPowerIn / 90.0F;
         }
      }
   }

   public void spawnHorseParticles(boolean bl) {
      EnumParticleTypes enumParticleTypes = bl ? EnumParticleTypes.HEART : EnumParticleTypes.SMOKE_NORMAL;

      for (int i = 0; i < 7; i++) {
         double d = this.rand.nextGaussian() * 0.02;
         double e = this.rand.nextGaussian() * 0.02;
         double d2 = this.rand.nextGaussian() * 0.02;
         this.worldObj
            .spawnParticle(
               enumParticleTypes,
               this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width,
               this.posY + 0.5 + this.rand.nextFloat() * this.height,
               this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width,
               d,
               e,
               d2
            );
      }
   }

   @Override
   public void handleStatusUpdate(byte id) {
      if (id == 7) {
         this.spawnHorseParticles(true);
      } else if (id == 6) {
         this.spawnHorseParticles(false);
      } else {
         super.handleStatusUpdate(id);
      }
   }

   @Override
   public void updateRiderPosition() {
      super.updateRiderPosition();
      if (this.prevRearingAmount > 0.0F) {
         float f = MathHelper.sin(this.renderYawOffset * (float) Math.PI / 180.0F);
         float g = MathHelper.cos(this.renderYawOffset * (float) Math.PI / 180.0F);
         float h = 0.7F * this.prevRearingAmount;
         float f2 = 0.15F * this.prevRearingAmount;
         this.riddenByEntity
            .setPosition(this.posX + h * f, this.posY + this.getMountedYOffset() + this.riddenByEntity.getYOffset() + f2, this.posZ - h * g);
         if (this.riddenByEntity instanceof EntityLivingBase) {
            ((EntityLivingBase)this.riddenByEntity).renderYawOffset = this.renderYawOffset;
         }
      }
   }

   private float getModifiedMaxHealth() {
      return 15.0F + this.rand.nextInt(8) + this.rand.nextInt(9);
   }

   private double getModifiedJumpStrength() {
      return 0.4F + this.rand.nextDouble() * 0.2 + this.rand.nextDouble() * 0.2 + this.rand.nextDouble() * 0.2;
   }

   private double getModifiedMovementSpeed() {
      return (0.45F + this.rand.nextDouble() * 0.3 + this.rand.nextDouble() * 0.3 + this.rand.nextDouble() * 0.3) * 0.25;
   }

   public static boolean isArmorItem(Item item) {
      return item == Items.iron_horse_armor || item == Items.golden_horse_armor || item == Items.diamond_horse_armor;
   }

   @Override
   public boolean isOnLadder() {
      return false;
   }

   @Override
   public float getEyeHeight() {
      return this.height;
   }

   @Override
   public boolean replaceItemInInventory(int inventorySlot, ItemStack itemStackIn) {
      if (inventorySlot == 499 && this.canCarryChest()) {
         if (itemStackIn == null && this.isChested()) {
            this.setChested(false);
            this.initHorseChest();
            return true;
         }

         if (itemStackIn != null && itemStackIn.getItem() == Item.getItemFromBlock(Blocks.chest) && !this.isChested()) {
            this.setChested(true);
            this.initHorseChest();
            return true;
         }
      }

      int i = inventorySlot - 400;
      if (i >= 0 && i < 2 && i < this.horseChest.getSizeInventory()) {
         if (i == 0 && itemStackIn != null && itemStackIn.getItem() != Items.saddle) {
            return false;
         }

         if (i == 1 && (itemStackIn != null && !isArmorItem(itemStackIn.getItem()) || !this.canWearArmor())) {
            return false;
         }

         this.horseChest.setInventorySlotContents(i, itemStackIn);
         this.updateHorseSlots();
         return true;
      } else {
         int j = inventorySlot - 500 + 2;
         if (j >= 2 && j < this.horseChest.getSizeInventory()) {
            this.horseChest.setInventorySlotContents(j, itemStackIn);
            return true;
         } else {
            return false;
         }
      }
   }

   public static class GroupData implements IEntityLivingData {
      public int horseType;
      public int horseVariant;

      public GroupData(int typeIn, int variant) {
         this.horseType = typeIn;
         this.horseVariant = variant;
      }
   }
}
