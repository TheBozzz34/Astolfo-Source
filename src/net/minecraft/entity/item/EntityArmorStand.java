package net.minecraft.entity.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Rotations;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class EntityArmorStand extends EntityLivingBase {
   private static final Rotations DEFAULT_HEAD_ROTATION = new Rotations(0.0F, 0.0F, 0.0F);
   private static final Rotations DEFAULT_BODY_ROTATION = new Rotations(0.0F, 0.0F, 0.0F);
   private static final Rotations DEFAULT_LEFTARM_ROTATION = new Rotations(-10.0F, 0.0F, -10.0F);
   private static final Rotations DEFAULT_RIGHTARM_ROTATION = new Rotations(-15.0F, 0.0F, 10.0F);
   private static final Rotations DEFAULT_LEFTLEG_ROTATION = new Rotations(-1.0F, 0.0F, -1.0F);
   private static final Rotations DEFAULT_RIGHTLEG_ROTATION = new Rotations(1.0F, 0.0F, 1.0F);
   private final ItemStack[] contents = new ItemStack[5];
   private boolean canInteract;
   private long punchCooldown;
   private int disabledSlots;
   private boolean field_181028_bj;
   private Rotations headRotation = DEFAULT_HEAD_ROTATION;
   private Rotations bodyRotation = DEFAULT_BODY_ROTATION;
   private Rotations leftArmRotation = DEFAULT_LEFTARM_ROTATION;
   private Rotations rightArmRotation = DEFAULT_RIGHTARM_ROTATION;
   private Rotations leftLegRotation = DEFAULT_LEFTLEG_ROTATION;
   private Rotations rightLegRotation = DEFAULT_RIGHTLEG_ROTATION;
   private static final long ab = 14146458160560L;

   public EntityArmorStand(World var1) {
      super(var1);
      this.setSilent(true);
      this.noClip = this.hasNoGravity();
      this.setSize(0.5F, 1.975F);
   }

   public EntityArmorStand(World var1, double var2, double var4, double var6) {
      this(var1);
      this.setPosition(var2, var4, var6);
   }

   @Override
   public boolean C() {
      return super.C() && !this.hasNoGravity();
   }

   @Override
   public void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(10, (byte)0);
      this.dataWatcher.addObject(11, DEFAULT_HEAD_ROTATION);
      this.dataWatcher.addObject(12, DEFAULT_BODY_ROTATION);
      this.dataWatcher.addObject(13, DEFAULT_LEFTARM_ROTATION);
      this.dataWatcher.addObject(14, DEFAULT_RIGHTARM_ROTATION);
      this.dataWatcher.addObject(15, DEFAULT_LEFTLEG_ROTATION);
      this.dataWatcher.addObject(16, DEFAULT_RIGHTLEG_ROTATION);
   }

   @Override
   public ItemStack getHeldItem() {
      return this.contents[0];
   }

   @Override
   public ItemStack getEquipmentInSlot(int var1) {
      return this.contents[var1];
   }

   @Override
   public ItemStack getCurrentArmor(int var1) {
      return this.contents[var1 + 1];
   }

   @Override
   public void setCurrentItemOrArmor(int var1, ItemStack var2) {
      this.contents[var1] = var2;
   }

   @Override
   public ItemStack[] getInventory() {
      return this.contents;
   }

   @Override
   public boolean replaceItemInInventory(int var1, ItemStack var2) {
      int var6;
      if (var1 == 99) {
         var6 = 0;
      } else {
         var6 = var1 - 100 + 1;
         if (var6 < 0 || var6 >= this.contents.length) {
            return false;
         }
      }

      if (var2 == null || EntityLiving.getArmorPosition(var2) == var6 || var6 == 4 && var2.getItem() instanceof ItemBlock) {
         this.setCurrentItemOrArmor(var6, var2);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound var1) {
      super.writeEntityToNBT(var1);
      NBTTagList var5 = new NBTTagList();

      for (int var6 = 0; var6 < this.contents.length; var6++) {
         NBTTagCompound var7 = new NBTTagCompound();
         if (this.contents[var6] != null) {
            this.contents[var6].writeToNBT(var7);
         }

         var5.appendTag(var7);
      }

      var1.setTag("Equipment", var5);
      if (this.getAlwaysRenderNameTag() && (this.getCustomNameTag() == null || this.getCustomNameTag().length() == 0)) {
         var1.setBoolean("CustomNameVisible", this.getAlwaysRenderNameTag());
      }

      var1.setBoolean("Invisible", this.isInvisible());
      var1.setBoolean("Small", this.isSmall());
      var1.setBoolean("ShowArms", this.getShowArms());
      var1.setInteger("DisabledSlots", this.disabledSlots);
      var1.setBoolean("NoGravity", this.hasNoGravity());
      var1.setBoolean("NoBasePlate", this.hasNoBasePlate());
      if (this.hasMarker()) {
         var1.setBoolean("Marker", this.hasMarker());
      }

      var1.setTag("Pose", this.readPoseFromNBT());
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound var1) {
      super.readEntityFromNBT(var1);
      if (var1.hasKey("Equipment", 9)) {
         NBTTagList var5 = var1.getTagList("Equipment", 10);

         for (int var6 = 0; var6 < this.contents.length; var6++) {
            this.contents[var6] = ItemStack.loadItemStackFromNBT(var5.getCompoundTagAt(var6));
         }
      }

      this.setInvisible(var1.getBoolean("Invisible"));
      this.setSmall(var1.getBoolean("Small"));
      this.setShowArms(var1.getBoolean("ShowArms"));
      this.disabledSlots = var1.getInteger("DisabledSlots");
      this.setNoGravity(var1.getBoolean("NoGravity"));
      this.setNoBasePlate(var1.getBoolean("NoBasePlate"));
      this.setMarker(var1.getBoolean("Marker"));
      this.field_181028_bj = !this.hasMarker();
      this.noClip = this.hasNoGravity();
      NBTTagCompound var7 = var1.getCompoundTag("Pose");
      this.writePoseToNBT(var7);
   }

   private void writePoseToNBT(NBTTagCompound var1) {
      NBTTagList var5 = var1.getTagList("Head", 5);
      if (var5.tagCount() > 0) {
         this.setHeadRotation(new Rotations(var5));
      } else {
         this.setHeadRotation(DEFAULT_HEAD_ROTATION);
      }

      NBTTagList var6 = var1.getTagList("Body", 5);
      if (var6.tagCount() > 0) {
         this.setBodyRotation(new Rotations(var6));
      } else {
         this.setBodyRotation(DEFAULT_BODY_ROTATION);
      }

      NBTTagList var7 = var1.getTagList("LeftArm", 5);
      if (var7.tagCount() > 0) {
         this.setLeftArmRotation(new Rotations(var7));
      } else {
         this.setLeftArmRotation(DEFAULT_LEFTARM_ROTATION);
      }

      NBTTagList var8 = var1.getTagList("RightArm", 5);
      if (var8.tagCount() > 0) {
         this.setRightArmRotation(new Rotations(var8));
      } else {
         this.setRightArmRotation(DEFAULT_RIGHTARM_ROTATION);
      }

      NBTTagList var9 = var1.getTagList("LeftLeg", 5);
      if (var9.tagCount() > 0) {
         this.setLeftLegRotation(new Rotations(var9));
      } else {
         this.setLeftLegRotation(DEFAULT_LEFTLEG_ROTATION);
      }

      NBTTagList var10 = var1.getTagList("RightLeg", 5);
      if (var10.tagCount() > 0) {
         this.setRightLegRotation(new Rotations(var10));
      } else {
         this.setRightLegRotation(DEFAULT_RIGHTLEG_ROTATION);
      }
   }

   private NBTTagCompound readPoseFromNBT() {
      NBTTagCompound var4 = new NBTTagCompound();
      if (!DEFAULT_HEAD_ROTATION.equals(this.headRotation)) {
         var4.setTag("Head", this.headRotation.writeToNBT());
      }

      if (!DEFAULT_BODY_ROTATION.equals(this.bodyRotation)) {
         var4.setTag("Body", this.bodyRotation.writeToNBT());
      }

      if (!DEFAULT_LEFTARM_ROTATION.equals(this.leftArmRotation)) {
         var4.setTag("LeftArm", this.leftArmRotation.writeToNBT());
      }

      if (!DEFAULT_RIGHTARM_ROTATION.equals(this.rightArmRotation)) {
         var4.setTag("RightArm", this.rightArmRotation.writeToNBT());
      }

      if (!DEFAULT_LEFTLEG_ROTATION.equals(this.leftLegRotation)) {
         var4.setTag("LeftLeg", this.leftLegRotation.writeToNBT());
      }

      if (!DEFAULT_RIGHTLEG_ROTATION.equals(this.rightLegRotation)) {
         var4.setTag("RightLeg", this.rightLegRotation.writeToNBT());
      }

      return var4;
   }

   @Override
   public boolean canBePushed() {
      return false;
   }

   @Override
   public void collideWithEntity(Entity var1) {
   }

   @Override
   public void collideWithNearbyEntities() {
      List var4 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox());
      if (var4 != null && !var4.isEmpty()) {
         for (int var5 = 0; var5 < var4.size(); var5++) {
            Entity var6 = (Entity)var4.get(var5);
            if (var6 instanceof EntityMinecart
               && ((EntityMinecart)var6).getMinecartType() == EntityMinecart.EnumMinecartType.RIDEABLE
               && this.getDistanceSqToEntity(var6) <= 0.2) {
               var6.applyEntityCollision(this);
            }
         }
      }
   }

   @Override
   public boolean interactAt(EntityPlayer var1, Vec3 var2) {
      if (this.hasMarker()) {
         return false;
      }

      if (!this.worldObj.isRemote && !var1.isSpectator()) {
         byte var6 = 0;
         ItemStack var7 = var1.getCurrentEquippedItem();
         boolean var8 = var7 != null;
         if (var8 && var7.getItem() instanceof ItemArmor) {
            ItemArmor var9 = (ItemArmor)var7.getItem();
            if (var9.armorType == 3) {
               var6 = 1;
            } else if (var9.armorType == 2) {
               var6 = 2;
            } else if (var9.armorType == 1) {
               var6 = 3;
            } else if (var9.armorType == 0) {
               var6 = 4;
            }
         }

         if (var8 && (var7.getItem() == Items.skull || var7.getItem() == Item.getItemFromBlock(Blocks.pumpkin))) {
            var6 = 4;
         }

         double var22 = 0.1;
         byte var17 = 0;
         boolean var18 = this.isSmall();
         double var19 = var18 ? var2.yCoord * 2.0 : var2.yCoord;
         if (var19 >= 0.1 && var19 < 0.1 + (var18 ? 0.8 : 0.45) && this.contents[1] != null) {
            var17 = 1;
         } else if (var19 >= 0.9 + (var18 ? 0.3 : 0.0) && var19 < 0.9 + (var18 ? 1.0 : 0.7) && this.contents[3] != null) {
            var17 = 3;
         } else if (var19 >= 0.4 && var19 < 0.4 + (var18 ? 1.0 : 0.8) && this.contents[2] != null) {
            var17 = 2;
         } else if (var19 >= 1.6 && this.contents[4] != null) {
            var17 = 4;
         }

         boolean var21 = this.contents[var17] != null;
         if ((this.disabledSlots & 1 << var17) != 0 || (this.disabledSlots & 1 << var6) != 0) {
            var17 = var6;
            if ((this.disabledSlots & 1 << var6) != 0) {
               if ((this.disabledSlots & 1) != 0) {
                  return true;
               }

               var17 = 0;
            }
         }

         if (var8 && var6 == 0 && !this.getShowArms()) {
            return true;
         }

         if (var8) {
            this.func_175422_a(var1, var6);
         } else if (var21) {
            this.func_175422_a(var1, var17);
         }

         return true;
      } else {
         return true;
      }
   }

   private void func_175422_a(EntityPlayer var1, int var2) {
      ItemStack var6 = this.contents[var2];
      if ((var6 == null || (this.disabledSlots & 1 << var2 + 8) == 0) && (var6 != null || (this.disabledSlots & 1 << var2 + 16) == 0)) {
         int var7 = var1.inventory.currentItem;
         ItemStack var8 = var1.inventory.getStackInSlot(var7);
         if (var1.capabilities.isCreativeMode && (var6 == null || var6.getItem() == Item.getItemFromBlock(Blocks.air)) && var8 != null) {
            ItemStack var10 = var8.copy();
            var10.stackSize = 1;
            this.setCurrentItemOrArmor(var2, var10);
         } else if (var8 == null || var8.stackSize <= 1) {
            this.setCurrentItemOrArmor(var2, var8);
            var1.inventory.setInventorySlotContents(var7, var6);
         } else if (var6 == null) {
            ItemStack var9 = var8.copy();
            var9.stackSize = 1;
            this.setCurrentItemOrArmor(var2, var9);
            var8.stackSize--;
         }
      }
   }

   @Override
   public boolean attackEntityFrom(DamageSource var1, float var2) {
      if (this.worldObj.isRemote) {
         return false;
      }

      if (DamageSource.outOfWorld.equals(var1)) {
         this.setDead();
         return false;
      }

      if (this.isEntityInvulnerable(var1) || this.canInteract || this.hasMarker()) {
         return false;
      }

      if (var1.isExplosion()) {
         this.dropContents();
         this.setDead();
         return false;
      }

      if (DamageSource.inFire.equals(var1)) {
         if (!this.isBurning()) {
            this.setFire(5);
         } else {
            this.damageArmorStand(0.15F);
         }

         return false;
      } else {
         if (DamageSource.onFire.equals(var1) && this.getHealth() > 0.5F) {
            this.damageArmorStand(4.0F);
            return false;
         }

         boolean var6 = "arrow".equals(var1.getDamageType());
         boolean var7 = "player".equals(var1.getDamageType());
         if (!var7 && !var6) {
            return false;
         }

         if (var1.getSourceOfDamage() instanceof EntityArrow) {
            var1.getSourceOfDamage().setDead();
         }

         if (var1.getEntity() instanceof EntityPlayer && !((EntityPlayer)var1.getEntity()).capabilities.allowEdit) {
            return false;
         }

         if (var1.isCreativePlayer()) {
            this.playParticles();
            this.setDead();
            return false;
         }

         long var8 = this.worldObj.getTotalWorldTime();
         if (var8 - this.punchCooldown > 5L && !var6) {
            this.punchCooldown = var8;
         } else {
            this.dropBlock();
            this.playParticles();
            this.setDead();
         }

         return false;
      }
   }

   @Override
   public boolean isInRangeToRenderDist(double var1) {
      double var6 = this.getEntityBoundingBox().getAverageEdgeLength() * 4.0;
      if (Double.isNaN(var6) || var6 == 0.0) {
         var6 = 4.0;
      }

      var6 *= 64.0;
      return var1 < var6 * var6;
   }

   private void playParticles() {
      if (this.worldObj instanceof WorldServer) {
         ((WorldServer)this.worldObj)
            .spawnParticle(
               EnumParticleTypes.BLOCK_DUST,
               this.posX,
               this.posY + this.height / 1.5,
               this.posZ,
               10,
               this.width / 4.0F,
               this.height / 4.0F,
               this.width / 4.0F,
               0.05,
               Block.getStateId(Blocks.planks.getDefaultState())
            );
      }
   }

   private void damageArmorStand(float var1) {
      float var5 = this.getHealth();
      var5 -= var1;
      if (var5 <= 0.5F) {
         this.dropContents();
         this.setDead();
      } else {
         this.setHealth(var5);
      }
   }

   private void dropBlock() {
      Block.spawnAsEntity(this.worldObj, new BlockPos(this), new ItemStack(Items.armor_stand));
      this.dropContents();
   }

   private void dropContents() {
      for (int var4 = 0; var4 < this.contents.length; var4++) {
         if (this.contents[var4] != null && this.contents[var4].stackSize > 0) {
            if (this.contents[var4] != null) {
               Block.spawnAsEntity(this.worldObj, new BlockPos(this).up(), this.contents[var4]);
            }

            this.contents[var4] = null;
         }
      }
   }

   @Override
   public float updateDistance(float var1, float var2) {
      this.prevRenderYawOffset = this.prevRotationYaw;
      this.renderYawOffset = this.rotationYaw;
      return 0.0F;
   }

   @Override
   public float getEyeHeight() {
      return this.isChild() ? this.height * 0.5F : this.height * 0.9F;
   }

   @Override
   public void moveEntityWithHeading(float var1, float var2) {
      if (!this.hasNoGravity()) {
         super.moveEntityWithHeading(var1, var2);
      }
   }

   @Override
   public void onUpdate() {
      super.onUpdate();
      Rotations var4 = this.dataWatcher.getWatchableObjectRotations(11);
      if (!this.headRotation.equals(var4)) {
         this.setHeadRotation(var4);
      }

      Rotations var5 = this.dataWatcher.getWatchableObjectRotations(12);
      if (!this.bodyRotation.equals(var5)) {
         this.setBodyRotation(var5);
      }

      Rotations var6 = this.dataWatcher.getWatchableObjectRotations(13);
      if (!this.leftArmRotation.equals(var6)) {
         this.setLeftArmRotation(var6);
      }

      Rotations var7 = this.dataWatcher.getWatchableObjectRotations(14);
      if (!this.rightArmRotation.equals(var7)) {
         this.setRightArmRotation(var7);
      }

      Rotations var8 = this.dataWatcher.getWatchableObjectRotations(15);
      if (!this.leftLegRotation.equals(var8)) {
         this.setLeftLegRotation(var8);
      }

      Rotations var9 = this.dataWatcher.getWatchableObjectRotations(16);
      if (!this.rightLegRotation.equals(var9)) {
         this.setRightLegRotation(var9);
      }

      boolean var10 = this.hasMarker();
      if (!this.field_181028_bj && var10) {
         this.func_181550_a(false);
      } else {
         if (!this.field_181028_bj || var10) {
            return;
         }

         this.func_181550_a(true);
      }

      this.field_181028_bj = var10;
   }

   private void func_181550_a(boolean var1) {
      double var5 = this.posX;
      double var7 = this.posY;
      double var9 = this.posZ;
      if (var1) {
         this.setSize(0.5F, 1.975F);
      } else {
         this.setSize(0.0F, 0.0F);
      }

      this.setPosition(var5, var7, var9);
   }

   @Override
   public void updatePotionMetadata() {
      this.setInvisible(this.canInteract);
   }

   @Override
   public void setInvisible(boolean var1) {
      this.canInteract = var1;
      super.setInvisible(var1);
   }

   @Override
   public boolean isChild() {
      return this.isSmall();
   }

   @Override
   public void onKillCommand() {
      this.setDead();
   }

   @Override
   public boolean isImmuneToExplosions() {
      return this.isInvisible();
   }

   private void setSmall(boolean var1) {
      byte var5 = this.dataWatcher.getWatchableObjectByte(10);
      if (var1) {
         var5 = (byte)(var5 | 1);
      } else {
         var5 = (byte)(var5 & -2);
      }

      this.dataWatcher.updateObject(10, var5);
   }

   public boolean isSmall() {
      return (this.dataWatcher.getWatchableObjectByte(10) & 1) != 0;
   }

   private void setNoGravity(boolean var1) {
      byte var5 = this.dataWatcher.getWatchableObjectByte(10);
      if (var1) {
         var5 = (byte)(var5 | 2);
      } else {
         var5 = (byte)(var5 & -3);
      }

      this.dataWatcher.updateObject(10, var5);
   }

   public boolean hasNoGravity() {
      return (this.dataWatcher.getWatchableObjectByte(10) & 2) != 0;
   }

   private void setShowArms(boolean var1) {
      byte var5 = this.dataWatcher.getWatchableObjectByte(10);
      if (var1) {
         var5 = (byte)(var5 | 4);
      } else {
         var5 = (byte)(var5 & -5);
      }

      this.dataWatcher.updateObject(10, var5);
   }

   public boolean getShowArms() {
      return (this.dataWatcher.getWatchableObjectByte(10) & 4) != 0;
   }

   private void setNoBasePlate(boolean var1) {
      byte var5 = this.dataWatcher.getWatchableObjectByte(10);
      if (var1) {
         var5 = (byte)(var5 | 8);
      } else {
         var5 = (byte)(var5 & -9);
      }

      this.dataWatcher.updateObject(10, var5);
   }

   public boolean hasNoBasePlate() {
      return (this.dataWatcher.getWatchableObjectByte(10) & 8) != 0;
   }

   private void setMarker(boolean var1) {
      byte var5 = this.dataWatcher.getWatchableObjectByte(10);
      if (var1) {
         var5 = (byte)(var5 | 16);
      } else {
         var5 = (byte)(var5 & -17);
      }

      this.dataWatcher.updateObject(10, var5);
   }

   public boolean hasMarker() {
      return (this.dataWatcher.getWatchableObjectByte(10) & 16) != 0;
   }

   public void setHeadRotation(Rotations var1) {
      this.headRotation = var1;
      this.dataWatcher.updateObject(11, var1);
   }

   public void setBodyRotation(Rotations var1) {
      this.bodyRotation = var1;
      this.dataWatcher.updateObject(12, var1);
   }

   public void setLeftArmRotation(Rotations var1) {
      this.leftArmRotation = var1;
      this.dataWatcher.updateObject(13, var1);
   }

   public void setRightArmRotation(Rotations var1) {
      this.rightArmRotation = var1;
      this.dataWatcher.updateObject(14, var1);
   }

   public void setLeftLegRotation(Rotations var1) {
      this.leftLegRotation = var1;
      this.dataWatcher.updateObject(15, var1);
   }

   public void setRightLegRotation(Rotations var1) {
      this.rightLegRotation = var1;
      this.dataWatcher.updateObject(16, var1);
   }

   public Rotations getHeadRotation() {
      return this.headRotation;
   }

   public Rotations getBodyRotation() {
      return this.bodyRotation;
   }

   public Rotations getLeftArmRotation() {
      return this.leftArmRotation;
   }

   public Rotations getRightArmRotation() {
      return this.rightArmRotation;
   }

   public Rotations getLeftLegRotation() {
      return this.leftLegRotation;
   }

   public Rotations getRightLegRotation() {
      return this.rightLegRotation;
   }

   @Override
   public boolean canBeCollidedWith() {
      return super.canBeCollidedWith() && !this.hasMarker();
   }
}
