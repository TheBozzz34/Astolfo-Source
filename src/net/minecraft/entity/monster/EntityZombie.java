package net.minecraft.entity.monster;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIBreakDoor;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityZombie extends EntityMob {
   public static final IAttribute reinforcementChance = new RangedAttribute((IAttribute)null, "zombie.spawnReinforcements", 0.0, 0.0, 1.0)
      .setDescription("Spawn Reinforcements Chance");
   private static final UUID babySpeedBoostUUID = UUID.fromString("B9766B59-9566-4402-BC1F-2EE2A276D836");
   private static final AttributeModifier babySpeedBoostModifier = new AttributeModifier(babySpeedBoostUUID, "Baby speed boost", 0.5, 1);
   private final EntityAIBreakDoor breakDoor = new EntityAIBreakDoor(this);
   private int conversionTime;
   private boolean isBreakDoorsTaskSet = false;
   private float zombieWidth = -1.0F;
   private float zombieHeight;
   private static final long gb = 22908641317214L;

   public EntityZombie(World var1) {
      super(var1);
      ((PathNavigateGround)this.getNavigator()).setBreakDoors(true);
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.0, false));
      this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1.0));
      this.tasks.addTask(7, new EntityAIWander(this, 1.0));
      this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.tasks.addTask(8, new EntityAILookIdle(this));
      this.applyEntityAI();
      this.setSize(0.6F, 1.95F);
   }

   public void applyEntityAI() {
      this.tasks.addTask(4, new EntityAIAttackOnCollide(this, EntityVillager.class, 1.0, true));
      this.tasks.addTask(4, new EntityAIAttackOnCollide(this, EntityIronGolem.class, 1.0, true));
      this.tasks.addTask(6, new EntityAIMoveThroughVillage(this, 1.0, false));
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, EntityPigZombie.class));
      this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
      this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityVillager.class, false));
      this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityIronGolem.class, true));
   }

   @Override
   public void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(35.0);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.23F);
      this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(3.0);
      this.getAttributeMap().registerAttribute(reinforcementChance).setBaseValue(this.rand.nextDouble() * 0.1F);
   }

   @Override
   public void entityInit() {
      super.entityInit();
      this.getDataWatcher().addObject(12, (byte)0);
      this.getDataWatcher().addObject(13, (byte)0);
      this.getDataWatcher().addObject(14, (byte)0);
   }

   @Override
   public int getTotalArmorValue() {
      int var4 = super.getTotalArmorValue() + 2;
      if (var4 > 20) {
         var4 = 20;
      }

      return var4;
   }

   public boolean isBreakDoorsTaskSet() {
      return this.isBreakDoorsTaskSet;
   }

   public void setBreakDoorsAItask(boolean var1) {
      if (this.isBreakDoorsTaskSet != var1) {
         this.isBreakDoorsTaskSet = var1;
         if (var1) {
            this.tasks.addTask(1, this.breakDoor);
         } else {
            this.tasks.removeTask(this.breakDoor);
         }
      }
   }

   @Override
   public boolean isChild() {
      return this.getDataWatcher().getWatchableObjectByte(12) == 1;
   }

   @Override
   public int getExperiencePoints(EntityPlayer var1) {
      if (this.isChild()) {
         this.experienceValue = (int)(this.experienceValue * 2.5F);
      }

      return super.getExperiencePoints(var1);
   }

   public void setChild(boolean var1) {
      this.getDataWatcher().updateObject(12, (byte)(var1 ? 1 : 0));
      if (this.worldObj != null && !this.worldObj.isRemote) {
         IAttributeInstance var5 = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
         var5.removeModifier(babySpeedBoostModifier);
         if (var1) {
            var5.applyModifier(babySpeedBoostModifier);
         }
      }

      this.setChildSize(var1);
   }

   public boolean isVillager() {
      return this.getDataWatcher().getWatchableObjectByte(13) == 1;
   }

   public void setVillager(boolean var1) {
      this.getDataWatcher().updateObject(13, (byte)(var1 ? 1 : 0));
   }

   @Override
   public void onLivingUpdate() {
      if (this.worldObj.isDaytime() && !this.worldObj.isRemote && !this.isChild()) {
         float var4 = this.getBrightness(1.0F);
         BlockPos var5 = new BlockPos(this.posX, Math.round(this.posY), this.posZ);
         if (var4 > 0.5F && this.rand.nextFloat() * 30.0F < (var4 - 0.4F) * 2.0F && this.worldObj.canSeeSky(var5)) {
            boolean var6 = true;
            ItemStack var7 = this.getEquipmentInSlot(4);
            if (var7 != null) {
               if (var7.isItemStackDamageable()) {
                  var7.setItemDamage(var7.getItemDamage() + this.rand.nextInt(2));
                  if (var7.getItemDamage() >= var7.getMaxDamage()) {
                     this.renderBrokenItemStack(var7);
                     this.setCurrentItemOrArmor(4, (ItemStack)null);
                  }
               }

               var6 = false;
            }

            if (var6) {
               this.setFire(8);
            }
         }
      }

      if (this.E() && this.getAttackTarget() != null && this.ridingEntity instanceof EntityChicken) {
         ((EntityLiving)this.ridingEntity).getNavigator().setPath(this.getNavigator().getPath(), 1.5);
      }

      super.onLivingUpdate();
   }

   @Override
   public boolean attackEntityFrom(DamageSource var1, float var2) {
      if (super.attackEntityFrom(var1, var2)) {
         EntityLivingBase var6 = this.getAttackTarget();
         if (var6 == null && var1.getEntity() instanceof EntityLivingBase) {
            var6 = (EntityLivingBase)var1.getEntity();
         }

         if (var6 != null
            && this.worldObj.getDifficulty() == EnumDifficulty.HARD
            && this.rand.nextFloat() < this.getEntityAttribute(reinforcementChance).getAttributeValue()) {
            int var7 = MathHelper.floor_double(this.posX);
            int var8 = MathHelper.floor_double(this.posY);
            int var9 = MathHelper.floor_double(this.posZ);
            EntityZombie var10 = new EntityZombie(this.worldObj);

            for (int var11 = 0; var11 < 50; var11++) {
               int var12 = var7 + MathHelper.getRandomIntegerInRange(this.rand, 7, 40) * MathHelper.getRandomIntegerInRange(this.rand, -1, 1);
               int var13 = var8 + MathHelper.getRandomIntegerInRange(this.rand, 7, 40) * MathHelper.getRandomIntegerInRange(this.rand, -1, 1);
               int var14 = var9 + MathHelper.getRandomIntegerInRange(this.rand, 7, 40) * MathHelper.getRandomIntegerInRange(this.rand, -1, 1);
               if (World.doesBlockHaveSolidTopSurface(this.worldObj, new BlockPos(var12, var13 - 1, var14))
                  && this.worldObj.getLightFromNeighbors(new BlockPos(var12, var13, var14)) < 10) {
                  var10.setPosition(var12, var13, var14);
                  if (!this.worldObj.isAnyPlayerWithinRangeAt(var12, var13, var14, 7.0)
                     && this.worldObj.checkNoEntityCollision(var10.getEntityBoundingBox(), var10)
                     && this.worldObj.getCollidingBoundingBoxes(var10, var10.getEntityBoundingBox()).isEmpty()
                     && !this.worldObj.isAnyLiquid(var10.getEntityBoundingBox())) {
                     this.worldObj.spawnEntityInWorld(var10);
                     var10.setAttackTarget(var6);
                     var10.onInitialSpawn(this.worldObj.getDifficultyForLocation(new BlockPos(var10)), (IEntityLivingData)null);
                     this.getEntityAttribute(reinforcementChance).applyModifier(new AttributeModifier("Zombie reinforcement caller charge", -0.05F, 0));
                     var10.getEntityAttribute(reinforcementChance).applyModifier(new AttributeModifier("Zombie reinforcement callee charge", -0.05F, 0));
                     break;
                  }
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public void onUpdate() {
      if (!this.worldObj.isRemote && this.isConverting()) {
         int var4 = this.getConversionTimeBoost();
         this.conversionTime -= var4;
         if (this.conversionTime <= 0) {
            this.convertToVillager();
         }
      }

      super.onUpdate();
   }

   @Override
   public boolean attackEntityAsMob(Entity var1) {
      boolean var5 = super.attackEntityAsMob(var1);
      if (var5) {
         int var6 = this.worldObj.getDifficulty().getDifficultyId();
         if (this.getHeldItem() == null && this.isBurning() && this.rand.nextFloat() < var6 * 0.3F) {
            var1.setFire(2 * var6);
         }
      }

      return var5;
   }

   @Override
   public String getLivingSound() {
      return "mob.zombie.say";
   }

   @Override
   public String getHurtSound() {
      return "mob.zombie.hurt";
   }

   @Override
   public String getDeathSound() {
      return "mob.zombie.death";
   }

   @Override
   public void playStepSound(BlockPos var1, Block var2) {
      this.playSound("mob.zombie.step", 0.15F, 1.0F);
   }

   @Override
   public Item getDropItem() {
      return Items.rotten_flesh;
   }

   @Override
   public EnumCreatureAttribute getCreatureAttribute() {
      return EnumCreatureAttribute.UNDEAD;
   }

   @Override
   public void addRandomDrop() {
      switch (this.rand.nextInt(3)) {
         case 0:
            this.dropItem(Items.iron_ingot, 1);
            break;
         case 1:
            this.dropItem(Items.carrot, 1);
            break;
         case 2:
            this.dropItem(Items.potato, 1);
      }
   }

   @Override
   public void setEquipmentBasedOnDifficulty(DifficultyInstance var1) {
      super.setEquipmentBasedOnDifficulty(var1);
      if (this.rand.nextFloat() < (this.worldObj.getDifficulty() == EnumDifficulty.HARD ? 0.05F : 0.01F)) {
         int var5 = this.rand.nextInt(3);
         if (var5 == 0) {
            this.setCurrentItemOrArmor(0, new ItemStack(Items.iron_sword));
         } else {
            this.setCurrentItemOrArmor(0, new ItemStack(Items.iron_shovel));
         }
      }
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound var1) {
      super.writeEntityToNBT(var1);
      if (this.isChild()) {
         var1.setBoolean("IsBaby", true);
      }

      if (this.isVillager()) {
         var1.setBoolean("IsVillager", true);
      }

      var1.setInteger("ConversionTime", this.isConverting() ? this.conversionTime : -1);
      var1.setBoolean("CanBreakDoors", this.isBreakDoorsTaskSet());
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound var1) {
      super.readEntityFromNBT(var1);
      if (var1.getBoolean("IsBaby")) {
         this.setChild(true);
      }

      if (var1.getBoolean("IsVillager")) {
         this.setVillager(true);
      }

      if (var1.hasKey("ConversionTime", 99) && var1.getInteger("ConversionTime") > -1) {
         this.startConversion(var1.getInteger("ConversionTime"));
      }

      this.setBreakDoorsAItask(var1.getBoolean("CanBreakDoors"));
   }

   @Override
   public void onKillEntity(EntityLivingBase var1) {
      super.onKillEntity(var1);
      if ((this.worldObj.getDifficulty() == EnumDifficulty.NORMAL || this.worldObj.getDifficulty() == EnumDifficulty.HARD) && var1 instanceof EntityVillager) {
         if (this.worldObj.getDifficulty() != EnumDifficulty.HARD && this.rand.nextBoolean()) {
            return;
         }

         EntityLiving var5 = (EntityLiving)var1;
         EntityZombie var6 = new EntityZombie(this.worldObj);
         var6.z(var1);
         this.worldObj.removeEntity(var1);
         var6.onInitialSpawn(this.worldObj.getDifficultyForLocation(new BlockPos(var6)), (IEntityLivingData)null);
         var6.setVillager(true);
         if (var1.isChild()) {
            var6.setChild(true);
         }

         var6.setNoAI(var5.isAIDisabled());
         if (var5.hasCustomName()) {
            var6.setCustomNameTag(var5.getCustomNameTag());
            var6.setAlwaysRenderNameTag(var5.getAlwaysRenderNameTag());
         }

         this.worldObj.spawnEntityInWorld(var6);
         this.worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1016, new BlockPos((int)this.posX, (int)this.posY, (int)this.posZ), 0);
      }
   }

   @Override
   public float getEyeHeight() {
      float var3 = 1.74F;
      if (this.isChild()) {
         var3 = (float)(var3 - 0.81);
      }

      return var3;
   }

   @Override
   public boolean func_175448_a(ItemStack var1) {
      return var1.getItem() == Items.egg && this.isChild() && this.E() ? false : super.func_175448_a(var1);
   }

   @Override
   public IEntityLivingData onInitialSpawn(DifficultyInstance var1, IEntityLivingData var2) {
      var2 = super.onInitialSpawn(var1, var2);
      float var6 = var1.getClampedAdditionalDifficulty();
      this.setCanPickUpLoot(this.rand.nextFloat() < 0.55F * var6);
      if (var2 == null) {
         var2 = new EntityZombie.GroupData(this.worldObj.rand.nextFloat() < 0.05F, this.worldObj.rand.nextFloat() < 0.05F);
      }

      if (var2 instanceof EntityZombie.GroupData) {
         EntityZombie.GroupData var7 = (EntityZombie.GroupData)var2;
         if (var7.isVillager) {
            this.setVillager(true);
         }

         if (var7.isChild) {
            this.setChild(true);
            if (this.worldObj.rand.nextFloat() < 0.05) {
               List var8 = this.worldObj
                  .getEntitiesWithinAABB(EntityChicken.class, this.getEntityBoundingBox().expand(5.0, 3.0, 5.0), EntitySelectors.IS_STANDALONE);
               if (!var8.isEmpty()) {
                  EntityChicken var9 = (EntityChicken)var8.get(0);
                  var9.setChickenJockey(true);
                  this.mountEntity(var9);
               }
            } else if (this.worldObj.rand.nextFloat() < 0.05) {
               EntityChicken var13 = new EntityChicken(this.worldObj);
               var13.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, 0.0F);
               var13.onInitialSpawn(var1, (IEntityLivingData)null);
               var13.setChickenJockey(true);
               this.worldObj.spawnEntityInWorld(var13);
               this.mountEntity(var13);
            }
         }
      }

      this.setBreakDoorsAItask(this.rand.nextFloat() < var6 * 0.1F);
      this.setEquipmentBasedOnDifficulty(var1);
      this.setEnchantmentBasedOnDifficulty(var1);
      if (this.getEquipmentInSlot(4) == null) {
         Calendar var11 = this.worldObj.getCurrentDate();
         if (var11.get(2) + 1 == 10 && var11.get(5) == 31 && this.rand.nextFloat() < 0.25F) {
            this.setCurrentItemOrArmor(4, new ItemStack(this.rand.nextFloat() < 0.1F ? Blocks.lit_pumpkin : Blocks.pumpkin));
            this.equipmentDropChances[4] = 0.0F;
         }
      }

      this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance)
         .applyModifier(new AttributeModifier("Random spawn bonus", this.rand.nextDouble() * 0.05F, 0));
      double var12 = this.rand.nextDouble() * 1.5 * var6;
      if (var12 > 1.0) {
         this.getEntityAttribute(SharedMonsterAttributes.followRange).applyModifier(new AttributeModifier("Random zombie-spawn bonus", var12, 2));
      }

      if (this.rand.nextFloat() < var6 * 0.05F) {
         this.getEntityAttribute(reinforcementChance).applyModifier(new AttributeModifier("Leader zombie bonus", this.rand.nextDouble() * 0.25 + 0.5, 0));
         this.getEntityAttribute(SharedMonsterAttributes.maxHealth)
            .applyModifier(new AttributeModifier("Leader zombie bonus", this.rand.nextDouble() * 3.0 + 1.0, 2));
         this.setBreakDoorsAItask(true);
      }

      return var2;
   }

   @Override
   public boolean interact(EntityPlayer var1) {
      ItemStack var5 = var1.getCurrentEquippedItem();
      if (var5 != null && var5.getItem() == Items.golden_apple && var5.getMetadata() == 0 && this.isVillager() && this.isPotionActive(Potion.weakness)) {
         if (!var1.capabilities.isCreativeMode) {
            var5.stackSize--;
         }

         if (var5.stackSize <= 0) {
            var1.inventory.setInventorySlotContents(var1.inventory.currentItem, (ItemStack)null);
         }

         if (!this.worldObj.isRemote) {
            this.startConversion(this.rand.nextInt(2401) + 3600);
         }

         return true;
      } else {
         return false;
      }
   }

   public void startConversion(int var1) {
      this.conversionTime = var1;
      this.getDataWatcher().updateObject(14, (byte)1);
      this.removePotionEffect(Potion.weakness.id);
      this.addPotionEffect(new PotionEffect(Potion.damageBoost.id, var1, Math.min(this.worldObj.getDifficulty().getDifficultyId() - 1, 0)));
      this.worldObj.setEntityState(this, (byte)16);
   }

   @Override
   public void handleStatusUpdate(byte var1) {
      if (var1 == 16) {
         if (!this.isSilent()) {
            this.worldObj
               .playSound(
                  this.posX + 0.5,
                  this.posY + 0.5,
                  this.posZ + 0.5,
                  "mob.zombie.remedy",
                  1.0F + this.rand.nextFloat(),
                  this.rand.nextFloat() * 0.7F + 0.3F,
                  false
               );
         }
      } else {
         super.handleStatusUpdate(var1);
      }
   }

   @Override
   public boolean canDespawn() {
      return !this.isConverting();
   }

   public boolean isConverting() {
      return this.getDataWatcher().getWatchableObjectByte(14) == 1;
   }

   public void convertToVillager() {
      EntityVillager var4 = new EntityVillager(this.worldObj);
      var4.z(this);
      var4.onInitialSpawn(this.worldObj.getDifficultyForLocation(new BlockPos(var4)), (IEntityLivingData)null);
      var4.setLookingForHome();
      if (this.isChild()) {
         var4.setGrowingAge(-24000);
      }

      this.worldObj.removeEntity(this);
      var4.setNoAI(this.isAIDisabled());
      if (this.hasCustomName()) {
         var4.setCustomNameTag(this.getCustomNameTag());
         var4.setAlwaysRenderNameTag(this.getAlwaysRenderNameTag());
      }

      this.worldObj.spawnEntityInWorld(var4);
      var4.addPotionEffect(new PotionEffect(Potion.confusion.id, 200, 0));
      this.worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1017, new BlockPos((int)this.posX, (int)this.posY, (int)this.posZ), 0);
   }

   public int getConversionTimeBoost() {
      int var4 = 1;
      if (this.rand.nextFloat() < 0.01F) {
         int var5 = 0;
         BlockPos.MutableBlockPos var6 = new BlockPos.MutableBlockPos();

         for (int var7 = (int)this.posX - 4; var7 < (int)this.posX + 4 && var5 < 14; var7++) {
            for (int var8 = (int)this.posY - 4; var8 < (int)this.posY + 4 && var5 < 14; var8++) {
               for (int var9 = (int)this.posZ - 4; var9 < (int)this.posZ + 4 && var5 < 14; var9++) {
                  Block var10 = this.worldObj.getBlockState(var6.set(var7, var8, var9)).getBlock();
                  if (var10 == Blocks.iron_bars || var10 == Blocks.bed) {
                     if (this.rand.nextFloat() < 0.3F) {
                        var4++;
                     }

                     var5++;
                  }
               }
            }
         }
      }

      return var4;
   }

   public void setChildSize(boolean var1) {
      this.multiplySize(var1 ? 0.5F : 1.0F);
   }

   @Override
   public final void setSize(float var1, float var2) {
      boolean var6 = this.zombieWidth > 0.0F && this.zombieHeight > 0.0F;
      this.zombieWidth = var1;
      this.zombieHeight = var2;
      if (!var6) {
         this.multiplySize(1.0F);
      }
   }

   public final void multiplySize(float var1) {
      super.setSize(this.zombieWidth * var1, this.zombieHeight * var1);
   }

   @Override
   public double getYOffset() {
      return this.isChild() ? 0.0 : -0.35;
   }

   @Override
   public void onDeath(DamageSource var1) {
      super.onDeath(var1);
      if (var1.getEntity() instanceof EntityCreeper
         && !(this instanceof EntityPigZombie)
         && ((EntityCreeper)var1.getEntity()).getPowered()
         && ((EntityCreeper)var1.getEntity()).isAIEnabled()) {
         ((EntityCreeper)var1.getEntity()).func_175493_co();
         this.entityDropItem(new ItemStack(Items.skull, 1, 2), 0.0F);
      }
   }

   public class GroupData implements IEntityLivingData {
      public boolean isChild = false;
      public boolean isVillager = false;

      public GroupData(boolean var2, boolean var3) {
         this.isChild = var2;
         this.isVillager = var3;
      }
   }
}
