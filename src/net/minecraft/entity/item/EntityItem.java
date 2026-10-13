package net.minecraft.entity.item;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityItem extends Entity {
   private static final Logger logger = LogManager.getLogger();
   private int age;
   private int delayBeforeCanPickup;
   private int health = 5;
   private String thrower;
   private String owner;
   public float hoverStart = (float)(Math.random() * Math.PI * 2.0);
   private static final long ab = 110901665805688L;

   public EntityItem(World var1, double var2, double var4, double var6) {
      super(var1);
      this.setSize(0.25F, 0.25F);
      this.setPosition(var2, var4, var6);
      this.rotationYaw = (float)(Math.random() * 360.0);
      this.motionX = (float)(Math.random() * 0.2F - 0.1F);
      this.motionY = 0.2F;
      this.motionZ = (float)(Math.random() * 0.2F - 0.1F);
   }

   public EntityItem(World var1, double var2, double var4, double var6, ItemStack var8) {
      this(var1, var2, var4, var6);
      this.setEntityItemStack(var8);
   }

   @Override
   public boolean canTriggerWalking() {
      return false;
   }

   public EntityItem(World var1) {
      super(var1);
      this.setSize(0.25F, 0.25F);
      this.setEntityItemStack(new ItemStack(Blocks.air, 0));
   }

   @Override
   public void entityInit() {
      this.getDataWatcher().addObjectByDataType(10, 5);
   }

   @Override
   public void onUpdate() {
      if (this.getEntityItem() == null) {
         this.setDead();
      } else {
         super.onUpdate();
         if (this.delayBeforeCanPickup > 0 && this.delayBeforeCanPickup != 32767) {
            this.delayBeforeCanPickup--;
         }

         this.prevPosX = this.posX;
         this.prevPosY = this.posY;
         this.prevPosZ = this.posZ;
         this.motionY -= 0.04F;
         this.noClip = this.pushOutOfBlocks(this.posX, (this.getEntityBoundingBox().minY + this.getEntityBoundingBox().maxY) / 2.0, this.posZ);
         this.moveEntity(this.motionX, this.motionY, this.motionZ);
         boolean var4 = (int)this.prevPosX != (int)this.posX || (int)this.prevPosY != (int)this.posY || (int)this.prevPosZ != (int)this.posZ;
         if (var4 || this.ticksExisted % 25 == 0) {
            if (this.worldObj.getBlockState(new BlockPos(this)).getBlock().getMaterial() == Material.lava) {
               this.motionY = 0.2F;
               this.motionX = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F;
               this.motionZ = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F;
               this.playSound("random.fizz", 0.4F, 2.0F + this.rand.nextFloat() * 0.4F);
            }

            if (!this.worldObj.isRemote) {
               this.searchForOtherItemsNearby();
            }
         }

         float var5 = 0.98F;
         if (this.onGround) {
            var5 = this.worldObj
                  .getBlockState(
                     new BlockPos(
                        MathHelper.floor_double(this.posX), MathHelper.floor_double(this.getEntityBoundingBox().minY) - 1, MathHelper.floor_double(this.posZ)
                     )
                  )
                  .getBlock()
                  .slipperiness
               * 0.98F;
         }

         this.motionX *= var5;
         this.motionY *= 0.98F;
         this.motionZ *= var5;
         if (this.onGround) {
            this.motionY *= -0.5;
         }

         if (this.age != -32768) {
            this.age++;
         }

         this.handleWaterMovement();
         if (!this.worldObj.isRemote && this.age >= 6000) {
            this.setDead();
         }
      }
   }

   private void searchForOtherItemsNearby() {
      for (EntityItem var5 : this.worldObj.getEntitiesWithinAABB(EntityItem.class, this.getEntityBoundingBox().expand(0.5, 0.0, 0.5))) {
         this.combineItems(var5);
      }
   }

   private boolean combineItems(EntityItem var1) {
      if (var1 == this) {
         return false;
      }

      if (var1.isEntityAlive() && this.isEntityAlive()) {
         ItemStack var5 = this.getEntityItem();
         ItemStack var6 = var1.getEntityItem();
         if (this.delayBeforeCanPickup == 32767 || var1.delayBeforeCanPickup == 32767) {
            return false;
         }

         if (this.age != -32768 && var1.age != -32768) {
            if (var6.getItem() != var5.getItem()) {
               return false;
            }

            if (var6.hasTagCompound() ^ var5.hasTagCompound()) {
               return false;
            }

            if (var6.hasTagCompound() && !var6.getTagCompound().equals(var5.getTagCompound())) {
               return false;
            }

            if (var6.getItem() == null) {
               return false;
            }

            if (var6.getItem().getHasSubtypes() && var6.getMetadata() != var5.getMetadata()) {
               return false;
            }

            if (var6.stackSize < var5.stackSize) {
               return var1.combineItems(this);
            }

            if (var6.stackSize + var5.stackSize > var6.getMaxStackSize()) {
               return false;
            }

            var6.stackSize = var6.stackSize + var5.stackSize;
            var1.delayBeforeCanPickup = Math.max(var1.delayBeforeCanPickup, this.delayBeforeCanPickup);
            var1.age = Math.min(var1.age, this.age);
            var1.setEntityItemStack(var6);
            this.setDead();
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public void setAgeToCreativeDespawnTime() {
      this.age = 4800;
   }

   @Override
   public boolean handleWaterMovement() {
      if (this.worldObj.handleMaterialAcceleration(this.getEntityBoundingBox(), Material.water, this)) {
         if (!this.inWater && !this.firstUpdate) {
            this.resetHeight();
         }

         this.inWater = true;
      } else {
         this.inWater = false;
      }

      return this.inWater;
   }

   @Override
   public void dealFireDamage(int var1) {
      this.attackEntityFrom(DamageSource.inFire, var1);
   }

   @Override
   public boolean attackEntityFrom(DamageSource var1, float var2) {
      if (this.isEntityInvulnerable(var1)) {
         return false;
      }

      if (this.getEntityItem() != null && this.getEntityItem().getItem() == Items.nether_star && var1.isExplosion()) {
         return false;
      }

      this.setBeenAttacked();
      this.health = (int)(this.health - var2);
      if (this.health <= 0) {
         this.setDead();
      }

      return false;
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound var1) {
      var1.setShort("Health", (byte)this.health);
      var1.setShort("Age", (short)this.age);
      var1.setShort("PickupDelay", (short)this.delayBeforeCanPickup);
      if (this.getThrower() != null) {
         var1.setString("Thrower", this.thrower);
      }

      if (this.getOwner() != null) {
         var1.setString("Owner", this.owner);
      }

      if (this.getEntityItem() != null) {
         var1.setTag("Item", this.getEntityItem().writeToNBT(new NBTTagCompound()));
      }
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound var1) {
      this.health = var1.getShort("Health") & 255;
      this.age = var1.getShort("Age");
      if (var1.hasKey("PickupDelay")) {
         this.delayBeforeCanPickup = var1.getShort("PickupDelay");
      }

      if (var1.hasKey("Owner")) {
         this.owner = var1.getString("Owner");
      }

      if (var1.hasKey("Thrower")) {
         this.thrower = var1.getString("Thrower");
      }

      NBTTagCompound var5 = var1.getCompoundTag("Item");
      this.setEntityItemStack(ItemStack.loadItemStackFromNBT(var5));
      if (this.getEntityItem() == null) {
         this.setDead();
      }
   }

   @Override
   public void onCollideWithPlayer(EntityPlayer var1) {
      if (!this.worldObj.isRemote) {
         ItemStack var5 = this.getEntityItem();
         int var6 = var5.stackSize;
         if (this.delayBeforeCanPickup == 0
            && (this.owner == null || 6000 - this.age <= 200 || this.owner.equals(var1.getName()))
            && var1.inventory.addItemStackToInventory(var5)) {
            if (var5.getItem() == Item.getItemFromBlock(Blocks.log)) {
               var1.triggerAchievement(AchievementList.mineWood);
            }

            if (var5.getItem() == Item.getItemFromBlock(Blocks.log2)) {
               var1.triggerAchievement(AchievementList.mineWood);
            }

            if (var5.getItem() == Items.leather) {
               var1.triggerAchievement(AchievementList.killCow);
            }

            if (var5.getItem() == Items.diamond) {
               var1.triggerAchievement(AchievementList.diamonds);
            }

            if (var5.getItem() == Items.blaze_rod) {
               var1.triggerAchievement(AchievementList.blazeRod);
            }

            if (var5.getItem() == Items.diamond && this.getThrower() != null) {
               EntityPlayer var7 = this.worldObj.getPlayerEntityByName(this.getThrower());
               if (var7 != null && var7 != var1) {
                  var7.triggerAchievement(AchievementList.diamondsToYou);
               }
            }

            if (!this.isSilent()) {
               this.worldObj.playSoundAtEntity(var1, "random.pop", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            }

            var1.onItemPickup(this, var6);
            if (var5.stackSize <= 0) {
               this.setDead();
            }
         }
      }
   }

   @Override
   public String getName() {
      return this.hasCustomName() ? this.getCustomNameTag() : StatCollector.translateToLocal("item." + this.getEntityItem().getUnlocalizedName());
   }

   @Override
   public boolean canAttackWithItem() {
      return false;
   }

   @Override
   public void travelToDimension(int var1) {
      super.travelToDimension(var1);
      if (!this.worldObj.isRemote) {
         this.searchForOtherItemsNearby();
      }
   }

   public ItemStack getEntityItem() {
      ItemStack var4 = this.getDataWatcher().getWatchableObjectItemStack(10);
      if (var4 == null) {
         if (this.worldObj != null) {
            logger.error("Item entity " + this.getEntityId() + " has no item?!");
         }

         return new ItemStack(Blocks.stone);
      } else {
         return var4;
      }
   }

   public void setEntityItemStack(ItemStack var1) {
      this.getDataWatcher().updateObject(10, var1);
      this.getDataWatcher().setObjectWatched(10);
   }

   public String getOwner() {
      return this.owner;
   }

   public void setOwner(String var1) {
      this.owner = var1;
   }

   public String getThrower() {
      return this.thrower;
   }

   public void setThrower(String var1) {
      this.thrower = var1;
   }

   public int getAge() {
      return this.age;
   }

   public void setDefaultPickupDelay() {
      this.delayBeforeCanPickup = 10;
   }

   public void setNoPickupDelay() {
      this.delayBeforeCanPickup = 0;
   }

   public void setInfinitePickupDelay() {
      this.delayBeforeCanPickup = 32767;
   }

   public void setPickupDelay(int var1) {
      this.delayBeforeCanPickup = var1;
   }

   public boolean cannotPickup() {
      return this.delayBeforeCanPickup > 0;
   }

   public void setNoDespawn() {
      this.age = -6000;
   }

   public void func_174870_v() {
      this.setInfinitePickupDelay();
      this.age = 5999;
   }
}
