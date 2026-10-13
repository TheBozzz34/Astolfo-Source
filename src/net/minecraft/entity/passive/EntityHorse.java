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
      private static final long a = 24586163153127L;

      public boolean apply(Entity var1) {
         return var1 instanceof EntityHorse && ((EntityHorse)var1).isBreeding();
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
   private static final long gb = 75937292940380L;

   public EntityHorse(World var1) {
      super(var1);
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

   public void setHorseType(int var1) {
      this.dataWatcher.updateObject(19, (byte)var1);
      this.resetTexturePrefix();
   }

   public int getHorseType() {
      return this.dataWatcher.getWatchableObjectByte(19);
   }

   public void setHorseVariant(int var1) {
      this.dataWatcher.updateObject(20, var1);
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

      int var4 = this.getHorseType();
      switch (var4) {
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

   private boolean getHorseWatchableBoolean(int var1) {
      return (this.dataWatcher.getWatchableObjectInt(16) & var1) != 0;
   }

   private void setHorseWatchableBoolean(int var1, boolean var2) {
      int var6 = this.dataWatcher.getWatchableObjectInt(16);
      if (var2) {
         this.dataWatcher.updateObject(16, var6 | var1);
      } else {
         this.dataWatcher.updateObject(16, var6 & ~var1);
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

   public void setOwnerId(String var1) {
      this.dataWatcher.updateObject(21, var1);
   }

   public float getHorseSize() {
      return 0.5F;
   }

   @Override
   public void setScaleForAge(boolean var1) {
      if (var1) {
         this.setScale(this.getHorseSize());
      } else {
         this.setScale(1.0F);
      }
   }

   public boolean TC() {
      return this.horseJumping;
   }

   public void setHorseTamed(boolean var1) {
      this.setHorseWatchableBoolean(2, var1);
   }

   public void N(boolean var1) {
      this.horseJumping = var1;
   }

   @Override
   public boolean allowLeashing() {
      return !this.isUndead() && super.allowLeashing();
   }

   @Override
   public void func_142017_o(float var1) {
      if (var1 > 6.0F && this.isEatingHaystack()) {
         this.setEatingHaystack(false);
      }
   }

   public boolean isChested() {
      return this.getHorseWatchableBoolean(8);
   }

   public int getHorseArmorIndexSynced() {
      return this.dataWatcher.getWatchableObjectInt(22);
   }

   private int getHorseArmorIndex(ItemStack var1) {
      if (var1 == null) {
         return 0;
      }

      Item var5 = var1.getItem();
      return var5 == Items.iron_horse_armor ? 1 : (var5 == Items.golden_horse_armor ? 2 : (var5 == Items.diamond_horse_armor ? 3 : 0));
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

   public void setHorseArmorStack(ItemStack var1) {
      this.dataWatcher.updateObject(22, this.getHorseArmorIndex(var1));
      this.resetTexturePrefix();
   }

   public void setBreeding(boolean var1) {
      this.setHorseWatchableBoolean(16, var1);
   }

   public void setChested(boolean var1) {
      this.setHorseWatchableBoolean(8, var1);
   }

   public void setHasReproduced(boolean var1) {
      this.hasReproduced = var1;
   }

   public void setHorseSaddled(boolean var1) {
      this.setHorseWatchableBoolean(4, var1);
   }

   public int getTemper() {
      return this.temper;
   }

   public void setTemper(int var1) {
      this.temper = var1;
   }

   public int increaseTemper(int var1) {
      int var2 = MathHelper.clamp_int(this.getTemper() + var1, 0, this.getMaxTemper());
      this.setTemper(var2);
      return var2;
   }

   @Override
   public boolean attackEntityFrom(DamageSource var1, float var2) {
      Entity var6 = var1.getEntity();
      return this.riddenByEntity != null && this.riddenByEntity.equals(var6) ? false : super.attackEntityFrom(var1, var2);
   }

   @Override
   public int getTotalArmorValue() {
      return armorValues[this.getHorseArmorIndexSynced()];
   }

   @Override
   public boolean canBePushed() {
      return this.riddenByEntity == null;
   }

   public boolean Tx() {
      int var1 = MathHelper.floor_double(this.posX);
      int var2 = MathHelper.floor_double(this.posZ);
      this.worldObj.getBiomeGenForCoords(new BlockPos(var1, 0, var2));
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
   public void fall(float var1, float var2) {
      if (var1 > 1.0F) {
         this.playSound("mob.horse.land", 0.4F, 1.0F);
      }

      int var6 = MathHelper.ceiling_float_int((var1 * 0.5F - 3.0F) * var2);
      if (var6 > 0) {
         this.attackEntityFrom(DamageSource.fall, var6);
         if (this.riddenByEntity != null) {
            this.riddenByEntity.attackEntityFrom(DamageSource.fall, var6);
         }

         Block var7 = this.worldObj.getBlockState(new BlockPos(this.posX, this.posY - 0.2 - this.prevRotationYaw, this.posZ)).getBlock();
         if (var7.getMaterial() != Material.air && !this.isSilent()) {
            Block.SoundType var8 = var7.stepSound;
            this.worldObj.playSoundAtEntity(this, var8.getStepSound(), var8.getVolume() * 0.5F, var8.getFrequency() * 0.75F);
         }
      }
   }

   private int getChestSize() {
      int var4 = this.getHorseType();
      return this.isChested() && (var4 == 1 || var4 == 2) ? 17 : 2;
   }

   private void initHorseChest() {
      AnimalChest var4 = this.horseChest;
      this.horseChest = new AnimalChest("HorseChest", this.getChestSize());
      this.horseChest.setCustomName(this.getName());
      if (var4 != null) {
         var4.removeInventoryChangeListener(this);
         int var5 = Math.min(var4.getSizeInventory(), this.horseChest.getSizeInventory());

         for (int var6 = 0; var6 < var5; var6++) {
            ItemStack var7 = var4.getStackInSlot(var6);
            if (var7 != null) {
               this.horseChest.setInventorySlotContents(var6, var7.copy());
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

   public void O(float var1, float var2, float var3) {
      EntityPlayerSP var7 = Minecraft.getMinecraft().thePlayer;
      this.rotationYaw = var7.rotationYaw;
      this.prevRotationYaw = this.rotationYaw;
      this.rotationPitch = var7.rotationPitch * 0.5F;
      this.setRotation(this.rotationYaw, this.rotationPitch);
      this.renderYawOffset = this.rotationYaw;
      this.rotationYawHead = this.renderYawOffset;
      var1 = var7.moveStrafing * 0.5F;
      var3 = var7.moveForward;
      if (var3 <= 0.0F) {
         var3 *= 0.25F;
         this.gallopTime = 0;
      }

      if (this.onGround && this.jumpPower == 0.0F && this.isRearing()) {
         var1 = 0.0F;
         var3 = 0.0F;
      }

      if (this.jumpPower > 0.0F && !this.TC() && this.onGround) {
         this.motionY = this.getHorseJumpStrength() * this.jumpPower;
         this.N(true);
         this.isAirBorne = true;
         if (var3 > 0.0F) {
            float var8 = MathHelper.sin(this.rotationYaw * (float) (Math.PI / 180.0));
            float var9 = MathHelper.cos(this.rotationYaw * (float) (Math.PI / 180.0));
            this.motionX = this.motionX + -0.4F * var8 * this.jumpPower;
            this.motionZ = this.motionZ + 0.4F * var9 * this.jumpPower;
         }

         this.jumpPower = 0.0F;
      }

      this.jumpMovementFactor = this.getAIMoveSpeed() * 0.1F;
      this.setAIMoveSpeed((float)this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue());
      super.moveEntityWithHeading(var1, var2);
      if (this.onGround) {
         this.jumpPower = 0.0F;
         this.N(false);
      }

      this.prevLimbSwingAmount = this.limbSwingAmount;
      double var15 = this.posX - this.prevPosX;
      double var10 = this.posZ - this.prevPosZ;
      float var12 = MathHelper.sqrt_double(var15 * var15 + var10 * var10) * 4.0F;
      if (var12 > 1.0F) {
         var12 = 1.0F;
      }

      this.limbSwingAmount = this.limbSwingAmount + (var12 - this.limbSwingAmount) * 0.4F;
      this.limbSwing = this.limbSwing + this.limbSwingAmount;
   }

   @Override
   public void onInventoryChanged(InventoryBasic var1) {
      int var5 = this.getHorseArmorIndexSynced();
      boolean var6 = this.isHorseSaddled();
      this.updateHorseSlots();
      if (this.ticksExisted > 20) {
         if (var5 == 0 && var5 != this.getHorseArmorIndexSynced()) {
            this.playSound("mob.horse.armor", 0.5F, 1.0F);
         } else if (var5 != this.getHorseArmorIndexSynced()) {
            this.playSound("mob.horse.armor", 0.5F, 1.0F);
         }

         if (!var6 && this.isHorseSaddled()) {
            this.playSound("mob.horse.leather", 0.5F, 1.0F);
         }
      }
   }

   @Override
   public boolean getCanSpawnHere() {
      this.Tx();
      return super.getCanSpawnHere();
   }

   public EntityHorse getClosestHorse(Entity var1, double var2) {
      double var7 = Double.MAX_VALUE;
      Entity var9 = null;

      for (Entity var11 : this.worldObj.getEntitiesInAABBexcluding(var1, var1.getEntityBoundingBox().addCoord(var2, var2, var2), horseBreedingSelector)) {
         double var12 = var11.getDistanceSq(var1.posX, var1.posY, var1.posZ);
         if (var12 < var7) {
            var9 = var11;
            var7 = var12;
         }
      }

      return (EntityHorse)var9;
   }

   public double getHorseJumpStrength() {
      return this.getEntityAttribute(horseJumpStrength).getAttributeValue();
   }

   @Override
   public String getDeathSound() {
      this.openHorseMouth();
      int var4 = this.getHorseType();
      return var4 == 3
         ? "mob.horse.zombie.death"
         : (var4 == 4 ? "mob.horse.skeleton.death" : (var4 != 1 && var4 != 2 ? "mob.horse.death" : "mob.horse.donkey.death"));
   }

   @Override
   public Item getDropItem() {
      boolean var4 = this.rand.nextInt(4) == 0;
      int var5 = this.getHorseType();
      return var5 == 4 ? Items.bone : (var5 == 3 ? (var4 ? null : Items.rotten_flesh) : Items.leather);
   }

   @Override
   public String getHurtSound() {
      this.openHorseMouth();
      if (this.rand.nextInt(3) == 0) {
         this.makeHorseRear();
      }

      int var4 = this.getHorseType();
      return var4 == 3 ? "mob.horse.zombie.hit" : (var4 == 4 ? "mob.horse.skeleton.hit" : (var4 != 1 && var4 != 2 ? "mob.horse.hit" : "mob.horse.donkey.hit"));
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

      int var4 = this.getHorseType();
      return var4 == 3
         ? "mob.horse.zombie.idle"
         : (var4 == 4 ? "mob.horse.skeleton.idle" : (var4 != 1 && var4 != 2 ? "mob.horse.idle" : "mob.horse.donkey.idle"));
   }

   public String getAngrySoundName() {
      this.openHorseMouth();
      this.makeHorseRear();
      int var4 = this.getHorseType();
      return var4 != 3 && var4 != 4 ? (var4 != 1 && var4 != 2 ? "mob.horse.angry" : "mob.horse.donkey.angry") : null;
   }

   @Override
   public void playStepSound(BlockPos var1, Block var2) {
      Block.SoundType var6 = var2.stepSound;
      if (this.worldObj.getBlockState(var1.up()).getBlock() == Blocks.snow_layer) {
         var6 = Blocks.snow_layer.stepSound;
      }

      if (!var2.getMaterial().isLiquid()) {
         int var7 = this.getHorseType();
         if (this.riddenByEntity != null && var7 != 1 && var7 != 2) {
            this.gallopTime++;
            if (this.gallopTime > 5 && this.gallopTime % 3 == 0) {
               this.playSound("mob.horse.gallop", var6.getVolume() * 0.15F, var6.getFrequency());
               if (var7 == 0 && this.rand.nextInt(10) == 0) {
                  this.playSound("mob.horse.breathe", var6.getVolume() * 0.6F, var6.getFrequency());
               }
            } else if (this.gallopTime <= 5) {
               this.playSound("mob.horse.wood", var6.getVolume() * 0.15F, var6.getFrequency());
            }
         } else if (var6 == Block.soundTypeWood) {
            this.playSound("mob.horse.wood", var6.getVolume() * 0.15F, var6.getFrequency());
         } else {
            this.playSound("mob.horse.soft", var6.getVolume() * 0.15F, var6.getFrequency());
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
      int var4 = this.getHorseType();
      int var5 = this.getHorseVariant();
      if (var4 == 0) {
         int var6 = var5 & 0xFF;
         int var7 = (var5 & 0xFF00) >> 8;
         if (var6 >= horseTextures.length) {
            this.field_175508_bO = false;
            return;
         }

         this.horseTexturesArray[0] = horseTextures[var6];
         this.texturePrefix = this.texturePrefix + HORSE_TEXTURES_ABBR[var6];
         if (var7 >= horseMarkingTextures.length) {
            this.field_175508_bO = false;
            return;
         }

         this.horseTexturesArray[1] = horseMarkingTextures[var7];
         this.texturePrefix = this.texturePrefix + HORSE_MARKING_TEXTURES_ABBR[var7];
      } else {
         this.horseTexturesArray[0] = "";
         this.texturePrefix = this.texturePrefix + "_" + var4 + "_";
      }

      int var8 = this.getHorseArmorIndexSynced();
      if (var8 >= horseArmorTextures.length) {
         this.field_175508_bO = false;
      } else {
         this.horseTexturesArray[2] = horseArmorTextures[var8];
         this.texturePrefix = this.texturePrefix + HORSE_ARMOR_TEXTURES_ABBR[var8];
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

   public void openGUI(EntityPlayer var1) {
      if (!this.worldObj.isRemote && (this.riddenByEntity == null || this.riddenByEntity == var1) && this.isTame()) {
         this.horseChest.setCustomName(this.getName());
         var1.displayGUIHorse(this, this.horseChest);
      }
   }

   @Override
   public boolean interact(EntityPlayer var1) {
      ItemStack var5 = var1.inventory.getCurrentItem();
      if (var5 != null && var5.getItem() == Items.spawn_egg) {
         return super.interact(var1);
      }

      if (!this.isTame() && this.isUndead()) {
         return false;
      }

      if (this.isTame() && this.isAdultHorse() && var1.isSneaking()) {
         this.openGUI(var1);
         return true;
      }

      if (this.func_110253_bW() && this.riddenByEntity != null) {
         return super.interact(var1);
      }

      if (var5 != null) {
         boolean var6 = false;
         if (this.canWearArmor()) {
            byte var7 = -1;
            if (var5.getItem() == Items.iron_horse_armor) {
               var7 = 1;
            } else if (var5.getItem() == Items.golden_horse_armor) {
               var7 = 2;
            } else if (var5.getItem() == Items.diamond_horse_armor) {
               var7 = 3;
            }

            if (var7 >= 0) {
               if (!this.isTame()) {
                  this.makeHorseRearWithSound();
                  return true;
               }

               this.openGUI(var1);
               return true;
            }
         }

         if (!var6 && !this.isUndead()) {
            float var10 = 0.0F;
            short var8 = 0;
            byte var9 = 0;
            if (var5.getItem() == Items.wheat) {
               var10 = 2.0F;
               var8 = 20;
               var9 = 3;
            } else if (var5.getItem() == Items.sugar) {
               var10 = 1.0F;
               var8 = 30;
               var9 = 3;
            } else if (Block.getBlockFromItem(var5.getItem()) == Blocks.hay_block) {
               var10 = 20.0F;
               var8 = 180;
            } else if (var5.getItem() == Items.apple) {
               var10 = 3.0F;
               var8 = 60;
               var9 = 3;
            } else if (var5.getItem() == Items.golden_carrot) {
               var10 = 4.0F;
               var8 = 60;
               var9 = 5;
               if (this.isTame() && this.getGrowingAge() == 0) {
                  var6 = true;
                  this.setInLove(var1);
               }
            } else if (var5.getItem() == Items.golden_apple) {
               var10 = 10.0F;
               var8 = 240;
               var9 = 10;
               if (this.isTame() && this.getGrowingAge() == 0) {
                  var6 = true;
                  this.setInLove(var1);
               }
            }

            if (this.getHealth() < this.getMaxHealth() && var10 > 0.0F) {
               this.heal(var10);
               var6 = true;
            }

            if (!this.isAdultHorse() && var8 > 0) {
               this.addGrowth(var8);
               var6 = true;
            }

            if (var9 > 0 && (var6 || !this.isTame()) && var9 < this.getMaxTemper()) {
               var6 = true;
               this.increaseTemper(var9);
            }

            if (var6) {
               this.func_110266_cB();
            }
         }

         if (!this.isTame() && !var6) {
            if (var5 != null && var5.interactWithEntity(var1, this)) {
               return true;
            }

            this.makeHorseRearWithSound();
            return true;
         }

         if (!var6 && this.canCarryChest() && !this.isChested() && var5.getItem() == Item.getItemFromBlock(Blocks.chest)) {
            this.setChested(true);
            this.playSound("mob.chickenplop", 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            var6 = true;
            this.initHorseChest();
         }

         if (!var6 && this.func_110253_bW() && !this.isHorseSaddled() && var5.getItem() == Items.saddle) {
            this.openGUI(var1);
            return true;
         }

         if (var6) {
            if (!var1.capabilities.isCreativeMode && --var5.stackSize == 0) {
               var1.inventory.setInventorySlotContents(var1.inventory.currentItem, (ItemStack)null);
            }

            return true;
         }
      }

      if (!this.func_110253_bW() || this.riddenByEntity != null) {
         return super.interact(var1);
      }

      if (var5 != null && var5.interactWithEntity(var1, this)) {
         return true;
      }

      this.mountTo(var1);
      return true;
   }

   private void mountTo(EntityPlayer var1) {
      var1.rotationYaw = this.rotationYaw;
      var1.rotationPitch = this.rotationPitch;
      this.setEatingHaystack(false);
      this.setRearing(false);
      if (!this.worldObj.isRemote) {
         var1.mountEntity(this);
      }
   }

   public boolean canWearArmor() {
      return this.getHorseType() == 0;
   }

   public boolean canCarryChest() {
      int var4 = this.getHorseType();
      return var4 == 2 || var4 == 1;
   }

   @Override
   public boolean isMovementBlocked() {
      return this.riddenByEntity != null && this.isHorseSaddled() ? true : this.isEatingHaystack() || this.isRearing();
   }

   public boolean isUndead() {
      int var4 = this.getHorseType();
      return var4 == 3 || var4 == 4;
   }

   public boolean isSterile() {
      return this.isUndead() || this.getHorseType() == 2;
   }

   @Override
   public boolean isBreedingItem(ItemStack var1) {
      return false;
   }

   private void func_110210_cH() {
      this.field_110278_bp = 1;
   }

   @Override
   public void onDeath(DamageSource var1) {
      super.onDeath(var1);
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
            EntityHorse var4 = this.getClosestHorse(this, 16.0);
            if (var4 != null && this.getDistanceSqToEntity(var4) > 4.0) {
               this.navigator.getPathToEntityLiving(var4);
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
   public void setEating(boolean var1) {
      this.setHorseWatchableBoolean(32, var1);
   }

   public void setEatingHaystack(boolean var1) {
      this.setEating(var1);
   }

   public void setRearing(boolean var1) {
      if (var1) {
         this.setEatingHaystack(false);
      }

      this.setHorseWatchableBoolean(64, var1);
   }

   private void makeHorseRear() {
      if (!this.worldObj.isRemote) {
         this.jumpRearingCounter = 1;
         this.setRearing(true);
      }
   }

   public void makeHorseRearWithSound() {
      this.makeHorseRear();
      String var3 = this.getAngrySoundName();
      if (var3 != null) {
         this.playSound(var3, this.getSoundVolume(), this.getSoundPitch());
      }
   }

   public void dropChestItems() {
      this.dropItemsInChest(this, this.horseChest);
      this.dropChests();
   }

   private void dropItemsInChest(Entity var1, AnimalChest var2) {
      if (var2 != null && !this.worldObj.isRemote) {
         for (int var6 = 0; var6 < var2.getSizeInventory(); var6++) {
            ItemStack var7 = var2.getStackInSlot(var6);
            if (var7 != null) {
               this.entityDropItem(var7, 0.0F);
            }
         }
      }
   }

   public boolean setTamedBy(EntityPlayer var1) {
      this.setOwnerId(var1.getUniqueID().toString());
      this.setHorseTamed(true);
      return true;
   }

   @Override
   public void moveEntityWithHeading(float var1, float var2) {
      if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityLivingBase && this.isHorseSaddled()) {
         this.prevRotationYaw = this.rotationYaw = this.riddenByEntity.rotationYaw;
         this.rotationPitch = this.riddenByEntity.rotationPitch * 0.5F;
         this.setRotation(this.rotationYaw, this.rotationPitch);
         this.rotationYawHead = this.renderYawOffset = this.rotationYaw;
         var1 = ((EntityLivingBase)this.riddenByEntity).moveStrafing * 0.5F;
         var2 = ((EntityLivingBase)this.riddenByEntity).moveForward;
         if (var2 <= 0.0F) {
            var2 *= 0.25F;
            this.gallopTime = 0;
         }

         if (this.onGround && this.jumpPower == 0.0F && this.isRearing() && !this.field_110294_bI) {
            var1 = 0.0F;
            var2 = 0.0F;
         }

         if (this.jumpPower > 0.0F && !this.TC() && this.onGround) {
            this.motionY = this.getHorseJumpStrength() * this.jumpPower;
            if (this.isPotionActive(Potion.jump)) {
               this.motionY = this.motionY + (this.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F;
            }

            this.N(true);
            this.isAirBorne = true;
            if (var2 > 0.0F) {
               float var6 = MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F);
               float var7 = MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F);
               this.motionX = this.motionX + -0.4F * var6 * this.jumpPower;
               this.motionZ = this.motionZ + 0.4F * var7 * this.jumpPower;
               this.playSound("mob.horse.jump", 0.4F, 1.0F);
            }

            this.jumpPower = 0.0F;
         }

         this.stepHeight = 1.0F;
         this.jumpMovementFactor = this.getAIMoveSpeed() * 0.1F;
         if (!this.worldObj.isRemote) {
            this.setAIMoveSpeed((float)this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue());
            super.moveEntityWithHeading(var1, var2);
         }

         if (this.onGround) {
            this.jumpPower = 0.0F;
            this.N(false);
         }

         this.prevLimbSwingAmount = this.limbSwingAmount;
         double var13 = this.posX - this.prevPosX;
         double var8 = this.posZ - this.prevPosZ;
         float var10 = MathHelper.sqrt_double(var13 * var13 + var8 * var8) * 4.0F;
         if (var10 > 1.0F) {
            var10 = 1.0F;
         }

         this.limbSwingAmount = this.limbSwingAmount + (var10 - this.limbSwingAmount) * 0.4F;
         this.limbSwing = this.limbSwing + this.limbSwingAmount;
      } else {
         this.stepHeight = 0.5F;
         this.jumpMovementFactor = 0.02F;
         super.moveEntityWithHeading(var1, var2);
      }
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound var1) {
      super.writeEntityToNBT(var1);
      var1.setBoolean("EatingHaystack", this.isEatingHaystack());
      var1.setBoolean("ChestedHorse", this.isChested());
      var1.setBoolean("HasReproduced", this.getHasReproduced());
      var1.setBoolean("Bred", this.isBreeding());
      var1.setInteger("Type", this.getHorseType());
      var1.setInteger("Variant", this.getHorseVariant());
      var1.setInteger("Temper", this.getTemper());
      var1.setBoolean("Tame", this.isTame());
      var1.setString("OwnerUUID", this.getOwnerId());
      if (this.isChested()) {
         NBTTagList var5 = new NBTTagList();

         for (int var6 = 2; var6 < this.horseChest.getSizeInventory(); var6++) {
            ItemStack var7 = this.horseChest.getStackInSlot(var6);
            if (var7 != null) {
               NBTTagCompound var8 = new NBTTagCompound();
               var8.setByte("Slot", (byte)var6);
               var7.writeToNBT(var8);
               var5.appendTag(var8);
            }
         }

         var1.setTag("Items", var5);
      }

      if (this.horseChest.getStackInSlot(1) != null) {
         var1.setTag("ArmorItem", this.horseChest.getStackInSlot(1).writeToNBT(new NBTTagCompound()));
      }

      if (this.horseChest.getStackInSlot(0) != null) {
         var1.setTag("SaddleItem", this.horseChest.getStackInSlot(0).writeToNBT(new NBTTagCompound()));
      }
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound var1) {
      super.readEntityFromNBT(var1);
      this.setEatingHaystack(var1.getBoolean("EatingHaystack"));
      this.setBreeding(var1.getBoolean("Bred"));
      this.setChested(var1.getBoolean("ChestedHorse"));
      this.setHasReproduced(var1.getBoolean("HasReproduced"));
      this.setHorseType(var1.getInteger("Type"));
      this.setHorseVariant(var1.getInteger("Variant"));
      this.setTemper(var1.getInteger("Temper"));
      this.setHorseTamed(var1.getBoolean("Tame"));
      String var5 = "";
      if (var1.hasKey("OwnerUUID", 8)) {
         var5 = var1.getString("OwnerUUID");
      } else {
         String var6 = var1.getString("Owner");
         var5 = PreYggdrasilConverter.getStringUUIDFromName(var6);
      }

      if (var5.length() > 0) {
         this.setOwnerId(var5);
      }

      IAttributeInstance var12 = this.getAttributeMap().getAttributeInstanceByName("Speed");
      if (var12 != null) {
         this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(var12.getBaseValue() * 0.25);
      }

      if (this.isChested()) {
         NBTTagList var7 = var1.getTagList("Items", 10);
         this.initHorseChest();

         for (int var8 = 0; var8 < var7.tagCount(); var8++) {
            NBTTagCompound var9 = var7.getCompoundTagAt(var8);
            int var10 = var9.getByte("Slot") & 255;
            if (var10 >= 2 && var10 < this.horseChest.getSizeInventory()) {
               this.horseChest.setInventorySlotContents(var10, ItemStack.loadItemStackFromNBT(var9));
            }
         }
      }

      if (var1.hasKey("ArmorItem", 10)) {
         ItemStack var13 = ItemStack.loadItemStackFromNBT(var1.getCompoundTag("ArmorItem"));
         if (var13 != null && isArmorItem(var13.getItem())) {
            this.horseChest.setInventorySlotContents(1, var13);
         }
      }

      if (var1.hasKey("SaddleItem", 10)) {
         ItemStack var14 = ItemStack.loadItemStackFromNBT(var1.getCompoundTag("SaddleItem"));
         if (var14 != null && var14.getItem() == Items.saddle) {
            this.horseChest.setInventorySlotContents(0, var14);
         }
      } else if (var1.getBoolean("Saddle")) {
         this.horseChest.setInventorySlotContents(0, new ItemStack(Items.saddle));
      }

      this.updateHorseSlots();
   }

   @Override
   public boolean canMateWith(EntityAnimal var1) {
      if (var1 == this) {
         return false;
      } else if (var1.getClass() != this.getClass()) {
         return false;
      } else {
         EntityHorse var5 = (EntityHorse)var1;
         if (this.canMate() && var5.canMate()) {
            int var6 = this.getHorseType();
            int var7 = var5.getHorseType();
            return var6 == var7 || var6 == 0 && var7 == 1 || var6 == 1 && var7 == 0;
         } else {
            return false;
         }
      }
   }

   @Override
   public EntityAgeable createChild(EntityAgeable var1) {
      EntityHorse var5 = (EntityHorse)var1;
      EntityHorse var6 = new EntityHorse(this.worldObj);
      int var7 = this.getHorseType();
      int var8 = var5.getHorseType();
      int var9 = 0;
      if (var7 == var8) {
         var9 = var7;
      } else if (var7 == 0 && var8 == 1 || var7 == 1 && var8 == 0) {
         var9 = 2;
      }

      if (var9 == 0) {
         int var10 = this.rand.nextInt(9);
         int var11;
         if (var10 < 4) {
            var11 = this.getHorseVariant() & 0xFF;
         } else if (var10 < 8) {
            var11 = var5.getHorseVariant() & 0xFF;
         } else {
            var11 = this.rand.nextInt(7);
         }

         int var12 = this.rand.nextInt(5);
         if (var12 < 2) {
            var11 |= this.getHorseVariant() & 0xFF00;
         } else if (var12 < 4) {
            var11 |= var5.getHorseVariant() & 0xFF00;
         } else {
            var11 |= this.rand.nextInt(5) << 8 & 0xFF00;
         }

         var6.setHorseVariant(var11);
      }

      var6.setHorseType(var9);
      double var16 = this.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue()
         + var1.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue()
         + this.getModifiedMaxHealth();
      var6.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(var16 / 3.0);
      double var18 = this.getEntityAttribute(horseJumpStrength).getBaseValue()
         + var1.getEntityAttribute(horseJumpStrength).getBaseValue()
         + this.getModifiedJumpStrength();
      var6.getEntityAttribute(horseJumpStrength).setBaseValue(var18 / 3.0);
      double var14 = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getBaseValue()
         + var1.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getBaseValue()
         + this.getModifiedMovementSpeed();
      var6.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(var14 / 3.0);
      return var6;
   }

   @Override
   public IEntityLivingData onInitialSpawn(DifficultyInstance var1, IEntityLivingData var2) {
      var2 = super.onInitialSpawn(var1, var2);
      int var6 = 0;
      int var7 = 0;
      if (var2 instanceof EntityHorse.GroupData) {
         var6 = ((EntityHorse.GroupData)var2).horseType;
         var7 = ((EntityHorse.GroupData)var2).horseVariant & 0xFF | this.rand.nextInt(5) << 8;
      } else {
         if (this.rand.nextInt(10) == 0) {
            var6 = 1;
         } else {
            int var8 = this.rand.nextInt(7);
            int var9 = this.rand.nextInt(5);
            var6 = 0;
            var7 = var8 | var9 << 8;
         }

         var2 = new EntityHorse.GroupData(var6, var7);
      }

      this.setHorseType(var6);
      this.setHorseVariant(var7);
      if (this.rand.nextInt(5) == 0) {
         this.setGrowingAge(-24000);
      }

      if (var6 != 4 && var6 != 3) {
         this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(this.getModifiedMaxHealth());
         if (var6 == 0) {
            this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(this.getModifiedMovementSpeed());
         } else {
            this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.175F);
         }
      } else {
         this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(15.0);
         this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.2F);
      }

      if (var6 != 2 && var6 != 1) {
         this.getEntityAttribute(horseJumpStrength).setBaseValue(this.getModifiedJumpStrength());
      } else {
         this.getEntityAttribute(horseJumpStrength).setBaseValue(0.5);
      }

      this.setHealth(this.getMaxHealth());
      return var2;
   }

   public float getGrassEatingAmount(float var1) {
      return this.prevHeadLean + (this.headLean - this.prevHeadLean) * var1;
   }

   public float getRearingAmount(float var1) {
      return this.prevRearingAmount + (this.rearingAmount - this.prevRearingAmount) * var1;
   }

   public float getMouthOpennessAngle(float var1) {
      return this.prevMouthOpenness + (this.mouthOpenness - this.prevMouthOpenness) * var1;
   }

   public void setJumpPower(int var1) {
      if (this.isHorseSaddled()) {
         if (var1 < 0) {
            var1 = 0;
         } else {
            this.field_110294_bI = true;
            this.makeHorseRear();
         }

         if (var1 >= 90) {
            this.jumpPower = 1.0F;
         } else {
            this.jumpPower = 0.4F + 0.4F * var1 / 90.0F;
         }
      }
   }

   public void spawnHorseParticles(boolean var1) {
      EnumParticleTypes var5 = var1 ? EnumParticleTypes.HEART : EnumParticleTypes.SMOKE_NORMAL;

      for (int var6 = 0; var6 < 7; var6++) {
         double var7 = this.rand.nextGaussian() * 0.02;
         double var9 = this.rand.nextGaussian() * 0.02;
         double var11 = this.rand.nextGaussian() * 0.02;
         this.worldObj
            .spawnParticle(
               var5,
               this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width,
               this.posY + 0.5 + this.rand.nextFloat() * this.height,
               this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width,
               var7,
               var9,
               var11
            );
      }
   }

   @Override
   public void handleStatusUpdate(byte var1) {
      if (var1 == 7) {
         this.spawnHorseParticles(true);
      } else if (var1 == 6) {
         this.spawnHorseParticles(false);
      } else {
         super.handleStatusUpdate(var1);
      }
   }

   @Override
   public void updateRiderPosition() {
      super.updateRiderPosition();
      if (this.prevRearingAmount > 0.0F) {
         float var4 = MathHelper.sin(this.renderYawOffset * (float) Math.PI / 180.0F);
         float var5 = MathHelper.cos(this.renderYawOffset * (float) Math.PI / 180.0F);
         float var6 = 0.7F * this.prevRearingAmount;
         float var7 = 0.15F * this.prevRearingAmount;
         this.riddenByEntity
            .setPosition(this.posX + var6 * var4, this.posY + this.getMountedYOffset() + this.riddenByEntity.getYOffset() + var7, this.posZ - var6 * var5);
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

   public static boolean isArmorItem(Item var0) {
      return var0 == Items.iron_horse_armor || var0 == Items.golden_horse_armor || var0 == Items.diamond_horse_armor;
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
   public boolean replaceItemInInventory(int var1, ItemStack var2) {
      if (var1 == 499 && this.canCarryChest()) {
         if (var2 == null && this.isChested()) {
            this.setChested(false);
            this.initHorseChest();
            return true;
         }

         if (var2 != null && var2.getItem() == Item.getItemFromBlock(Blocks.chest) && !this.isChested()) {
            this.setChested(true);
            this.initHorseChest();
            return true;
         }
      }

      int var6 = var1 - 400;
      if (var6 >= 0 && var6 < 2 && var6 < this.horseChest.getSizeInventory()) {
         if (var6 == 0 && var2 != null && var2.getItem() != Items.saddle) {
            return false;
         }

         if (var6 == 1 && (var2 != null && !isArmorItem(var2.getItem()) || !this.canWearArmor())) {
            return false;
         }

         this.horseChest.setInventorySlotContents(var6, var2);
         this.updateHorseSlots();
         return true;
      } else {
         int var7 = var1 - 500 + 2;
         if (var7 >= 2 && var7 < this.horseChest.getSizeInventory()) {
            this.horseChest.setInventorySlotContents(var7, var2);
            return true;
         } else {
            return false;
         }
      }
   }

   public static class GroupData implements IEntityLivingData {
      public int horseType;
      public int horseVariant;

      public GroupData(int var1, int var2) {
         this.horseType = var1;
         this.horseVariant = var2;
      }
   }
}
