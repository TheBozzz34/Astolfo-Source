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

   public EntityArmorStand(World worldIn) {
      super(worldIn);
      this.setSilent(true);
      this.noClip = this.hasNoGravity();
      this.setSize(0.5F, 1.975F);
   }

   public EntityArmorStand(World worldIn, double posX, double posY, double posZ) {
      this(worldIn);
      this.setPosition(posX, posY, posZ);
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
   public ItemStack getEquipmentInSlot(int slotIn) {
      return this.contents[slotIn];
   }

   @Override
   public ItemStack getCurrentArmor(int slotIn) {
      return this.contents[slotIn + 1];
   }

   @Override
   public void setCurrentItemOrArmor(int slotIn, ItemStack stack) {
      this.contents[slotIn] = stack;
   }

   @Override
   public ItemStack[] getInventory() {
      return this.contents;
   }

   @Override
   public boolean replaceItemInInventory(int inventorySlot, ItemStack itemStackIn) {
      int i;
      if (inventorySlot == 99) {
         i = 0;
      } else {
         i = inventorySlot - 100 + 1;
         if (i < 0 || i >= this.contents.length) {
            return false;
         }
      }

      if (itemStackIn == null || EntityLiving.getArmorPosition(itemStackIn) == i || i == 4 && itemStackIn.getItem() instanceof ItemBlock) {
         this.setCurrentItemOrArmor(i, itemStackIn);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      super.writeEntityToNBT(tagCompound);
      NBTTagList nbtTagList = new NBTTagList();

      for (int i = 0; i < this.contents.length; i++) {
         NBTTagCompound nbtTagCompound = new NBTTagCompound();
         if (this.contents[i] != null) {
            this.contents[i].writeToNBT(nbtTagCompound);
         }

         nbtTagList.appendTag(nbtTagCompound);
      }

      tagCompound.setTag("Equipment", nbtTagList);
      if (this.getAlwaysRenderNameTag() && (this.getCustomNameTag() == null || this.getCustomNameTag().length() == 0)) {
         tagCompound.setBoolean("CustomNameVisible", this.getAlwaysRenderNameTag());
      }

      tagCompound.setBoolean("Invisible", this.isInvisible());
      tagCompound.setBoolean("Small", this.isSmall());
      tagCompound.setBoolean("ShowArms", this.getShowArms());
      tagCompound.setInteger("DisabledSlots", this.disabledSlots);
      tagCompound.setBoolean("NoGravity", this.hasNoGravity());
      tagCompound.setBoolean("NoBasePlate", this.hasNoBasePlate());
      if (this.hasMarker()) {
         tagCompound.setBoolean("Marker", this.hasMarker());
      }

      tagCompound.setTag("Pose", this.readPoseFromNBT());
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      super.readEntityFromNBT(tagCompund);
      if (tagCompund.hasKey("Equipment", 9)) {
         NBTTagList nbtTagList = tagCompund.getTagList("Equipment", 10);

         for (int i = 0; i < this.contents.length; i++) {
            this.contents[i] = ItemStack.loadItemStackFromNBT(nbtTagList.getCompoundTagAt(i));
         }
      }

      this.setInvisible(tagCompund.getBoolean("Invisible"));
      this.setSmall(tagCompund.getBoolean("Small"));
      this.setShowArms(tagCompund.getBoolean("ShowArms"));
      this.disabledSlots = tagCompund.getInteger("DisabledSlots");
      this.setNoGravity(tagCompund.getBoolean("NoGravity"));
      this.setNoBasePlate(tagCompund.getBoolean("NoBasePlate"));
      this.setMarker(tagCompund.getBoolean("Marker"));
      this.field_181028_bj = !this.hasMarker();
      this.noClip = this.hasNoGravity();
      NBTTagCompound nbtTagCompound = tagCompund.getCompoundTag("Pose");
      this.writePoseToNBT(nbtTagCompound);
   }

   private void writePoseToNBT(NBTTagCompound tagCompound) {
      NBTTagList nbtTagList = tagCompound.getTagList("Head", 5);
      if (nbtTagList.tagCount() > 0) {
         this.setHeadRotation(new Rotations(nbtTagList));
      } else {
         this.setHeadRotation(DEFAULT_HEAD_ROTATION);
      }

      NBTTagList nbtTagList2 = tagCompound.getTagList("Body", 5);
      if (nbtTagList2.tagCount() > 0) {
         this.setBodyRotation(new Rotations(nbtTagList2));
      } else {
         this.setBodyRotation(DEFAULT_BODY_ROTATION);
      }

      NBTTagList nbtTagList3 = tagCompound.getTagList("LeftArm", 5);
      if (nbtTagList3.tagCount() > 0) {
         this.setLeftArmRotation(new Rotations(nbtTagList3));
      } else {
         this.setLeftArmRotation(DEFAULT_LEFTARM_ROTATION);
      }

      NBTTagList nbtTagList4 = tagCompound.getTagList("RightArm", 5);
      if (nbtTagList4.tagCount() > 0) {
         this.setRightArmRotation(new Rotations(nbtTagList4));
      } else {
         this.setRightArmRotation(DEFAULT_RIGHTARM_ROTATION);
      }

      NBTTagList nbtTagList5 = tagCompound.getTagList("LeftLeg", 5);
      if (nbtTagList5.tagCount() > 0) {
         this.setLeftLegRotation(new Rotations(nbtTagList5));
      } else {
         this.setLeftLegRotation(DEFAULT_LEFTLEG_ROTATION);
      }

      NBTTagList nbtTagList6 = tagCompound.getTagList("RightLeg", 5);
      if (nbtTagList6.tagCount() > 0) {
         this.setRightLegRotation(new Rotations(nbtTagList6));
      } else {
         this.setRightLegRotation(DEFAULT_RIGHTLEG_ROTATION);
      }
   }

   private NBTTagCompound readPoseFromNBT() {
      NBTTagCompound nbtTagCompound = new NBTTagCompound();
      if (!DEFAULT_HEAD_ROTATION.equals(this.headRotation)) {
         nbtTagCompound.setTag("Head", this.headRotation.writeToNBT());
      }

      if (!DEFAULT_BODY_ROTATION.equals(this.bodyRotation)) {
         nbtTagCompound.setTag("Body", this.bodyRotation.writeToNBT());
      }

      if (!DEFAULT_LEFTARM_ROTATION.equals(this.leftArmRotation)) {
         nbtTagCompound.setTag("LeftArm", this.leftArmRotation.writeToNBT());
      }

      if (!DEFAULT_RIGHTARM_ROTATION.equals(this.rightArmRotation)) {
         nbtTagCompound.setTag("RightArm", this.rightArmRotation.writeToNBT());
      }

      if (!DEFAULT_LEFTLEG_ROTATION.equals(this.leftLegRotation)) {
         nbtTagCompound.setTag("LeftLeg", this.leftLegRotation.writeToNBT());
      }

      if (!DEFAULT_RIGHTLEG_ROTATION.equals(this.rightLegRotation)) {
         nbtTagCompound.setTag("RightLeg", this.rightLegRotation.writeToNBT());
      }

      return nbtTagCompound;
   }

   @Override
   public boolean canBePushed() {
      return false;
   }

   @Override
   public void collideWithEntity(Entity entityIn) {
   }

   @Override
   public void collideWithNearbyEntities() {
      List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox());
      if (list != null && !list.isEmpty()) {
         for (int i = 0; i < list.size(); i++) {
            Entity entity = (Entity)list.get(i);
            if (entity instanceof EntityMinecart
               && ((EntityMinecart)entity).getMinecartType() == EntityMinecart.EnumMinecartType.RIDEABLE
               && this.getDistanceSqToEntity(entity) <= 0.2) {
               entity.applyEntityCollision(this);
            }
         }
      }
   }

   @Override
   public boolean interactAt(EntityPlayer player, Vec3 targetVec3) {
      if (this.hasMarker()) {
         return false;
      }

      if (!this.worldObj.isRemote && !player.isSpectator()) {
         byte b = 0;
         ItemStack itemStack = player.getCurrentEquippedItem();
         boolean bl = itemStack != null;
         if (bl && itemStack.getItem() instanceof ItemArmor) {
            ItemArmor itemArmor = (ItemArmor)itemStack.getItem();
            if (itemArmor.armorType == 3) {
               b = 1;
            } else if (itemArmor.armorType == 2) {
               b = 2;
            } else if (itemArmor.armorType == 1) {
               b = 3;
            } else if (itemArmor.armorType == 0) {
               b = 4;
            }
         }

         if (bl && (itemStack.getItem() == Items.skull || itemStack.getItem() == Item.getItemFromBlock(Blocks.pumpkin))) {
            b = 4;
         }

         double d = 0.1;
         byte b2 = 0;
         boolean bl2 = this.isSmall();
         double e = bl2 ? targetVec3.yCoord * 2.0 : targetVec3.yCoord;
         if (e >= 0.1 && e < 0.1 + (bl2 ? 0.8 : 0.45) && this.contents[1] != null) {
            b2 = 1;
         } else if (e >= 0.9 + (bl2 ? 0.3 : 0.0) && e < 0.9 + (bl2 ? 1.0 : 0.7) && this.contents[3] != null) {
            b2 = 3;
         } else if (e >= 0.4 && e < 0.4 + (bl2 ? 1.0 : 0.8) && this.contents[2] != null) {
            b2 = 2;
         } else if (e >= 1.6 && this.contents[4] != null) {
            b2 = 4;
         }

         boolean bl3 = this.contents[b2] != null;
         if ((this.disabledSlots & 1 << b2) != 0 || (this.disabledSlots & 1 << b) != 0) {
            b2 = b;
            if ((this.disabledSlots & 1 << b) != 0) {
               if ((this.disabledSlots & 1) != 0) {
                  return true;
               }

               b2 = 0;
            }
         }

         if (bl && b == 0 && !this.getShowArms()) {
            return true;
         }

         if (bl) {
            this.func_175422_a(player, b);
         } else if (bl3) {
            this.func_175422_a(player, b2);
         }

         return true;
      } else {
         return true;
      }
   }

   private void func_175422_a(EntityPlayer entityPlayer, int i) {
      ItemStack itemStack = this.contents[i];
      if ((itemStack == null || (this.disabledSlots & 1 << i + 8) == 0) && (itemStack != null || (this.disabledSlots & 1 << i + 16) == 0)) {
         int j = entityPlayer.inventory.currentItem;
         ItemStack itemStack2 = entityPlayer.inventory.getStackInSlot(j);
         if (entityPlayer.capabilities.isCreativeMode && (itemStack == null || itemStack.getItem() == Item.getItemFromBlock(Blocks.air)) && itemStack2 != null) {
            ItemStack itemStack3 = itemStack2.copy();
            itemStack3.stackSize = 1;
            this.setCurrentItemOrArmor(i, itemStack3);
         } else if (itemStack2 == null || itemStack2.stackSize <= 1) {
            this.setCurrentItemOrArmor(i, itemStack2);
            entityPlayer.inventory.setInventorySlotContents(j, itemStack);
         } else if (itemStack == null) {
            ItemStack itemStack4 = itemStack2.copy();
            itemStack4.stackSize = 1;
            this.setCurrentItemOrArmor(i, itemStack4);
            itemStack2.stackSize--;
         }
      }
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.worldObj.isRemote) {
         return false;
      }

      if (DamageSource.outOfWorld.equals(source)) {
         this.setDead();
         return false;
      }

      if (this.isEntityInvulnerable(source) || this.canInteract || this.hasMarker()) {
         return false;
      }

      if (source.isExplosion()) {
         this.dropContents();
         this.setDead();
         return false;
      }

      if (DamageSource.inFire.equals(source)) {
         if (!this.isBurning()) {
            this.setFire(5);
         } else {
            this.damageArmorStand(0.15F);
         }

         return false;
      } else {
         if (DamageSource.onFire.equals(source) && this.getHealth() > 0.5F) {
            this.damageArmorStand(4.0F);
            return false;
         }

         boolean bl = "arrow".equals(source.getDamageType());
         boolean bl2 = "player".equals(source.getDamageType());
         if (!bl2 && !bl) {
            return false;
         }

         if (source.getSourceOfDamage() instanceof EntityArrow) {
            source.getSourceOfDamage().setDead();
         }

         if (source.getEntity() instanceof EntityPlayer && !((EntityPlayer)source.getEntity()).capabilities.allowEdit) {
            return false;
         }

         if (source.isCreativePlayer()) {
            this.playParticles();
            this.setDead();
            return false;
         }

         long l = this.worldObj.getTotalWorldTime();
         if (l - this.punchCooldown > 5L && !bl) {
            this.punchCooldown = l;
         } else {
            this.dropBlock();
            this.playParticles();
            this.setDead();
         }

         return false;
      }
   }

   @Override
   public boolean isInRangeToRenderDist(double distance) {
      double d = this.getEntityBoundingBox().getAverageEdgeLength() * 4.0;
      if (Double.isNaN(d) || d == 0.0) {
         d = 4.0;
      }

      d *= 64.0;
      return distance < d * d;
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

   private void damageArmorStand(float f) {
      float g = this.getHealth();
      g -= f;
      if (g <= 0.5F) {
         this.dropContents();
         this.setDead();
      } else {
         this.setHealth(g);
      }
   }

   private void dropBlock() {
      Block.spawnAsEntity(this.worldObj, new BlockPos(this), new ItemStack(Items.armor_stand));
      this.dropContents();
   }

   private void dropContents() {
      for (int i = 0; i < this.contents.length; i++) {
         if (this.contents[i] != null && this.contents[i].stackSize > 0) {
            if (this.contents[i] != null) {
               Block.spawnAsEntity(this.worldObj, new BlockPos(this).up(), this.contents[i]);
            }

            this.contents[i] = null;
         }
      }
   }

   @Override
   public float updateDistance(float f, float g) {
      this.prevRenderYawOffset = this.prevRotationYaw;
      this.renderYawOffset = this.rotationYaw;
      return 0.0F;
   }

   @Override
   public float getEyeHeight() {
      return this.isChild() ? this.height * 0.5F : this.height * 0.9F;
   }

   @Override
   public void moveEntityWithHeading(float strafe, float forward) {
      if (!this.hasNoGravity()) {
         super.moveEntityWithHeading(strafe, forward);
      }
   }

   @Override
   public void onUpdate() {
      super.onUpdate();
      Rotations rotations = this.dataWatcher.getWatchableObjectRotations(11);
      if (!this.headRotation.equals(rotations)) {
         this.setHeadRotation(rotations);
      }

      Rotations rotations2 = this.dataWatcher.getWatchableObjectRotations(12);
      if (!this.bodyRotation.equals(rotations2)) {
         this.setBodyRotation(rotations2);
      }

      Rotations rotations3 = this.dataWatcher.getWatchableObjectRotations(13);
      if (!this.leftArmRotation.equals(rotations3)) {
         this.setLeftArmRotation(rotations3);
      }

      Rotations rotations4 = this.dataWatcher.getWatchableObjectRotations(14);
      if (!this.rightArmRotation.equals(rotations4)) {
         this.setRightArmRotation(rotations4);
      }

      Rotations rotations5 = this.dataWatcher.getWatchableObjectRotations(15);
      if (!this.leftLegRotation.equals(rotations5)) {
         this.setLeftLegRotation(rotations5);
      }

      Rotations rotations6 = this.dataWatcher.getWatchableObjectRotations(16);
      if (!this.rightLegRotation.equals(rotations6)) {
         this.setRightLegRotation(rotations6);
      }

      boolean bl = this.hasMarker();
      if (!this.field_181028_bj && bl) {
         this.func_181550_a(false);
      } else {
         if (!this.field_181028_bj || bl) {
            return;
         }

         this.func_181550_a(true);
      }

      this.field_181028_bj = bl;
   }

   private void func_181550_a(boolean bl) {
      double d = this.posX;
      double e = this.posY;
      double d2 = this.posZ;
      if (bl) {
         this.setSize(0.5F, 1.975F);
      } else {
         this.setSize(0.0F, 0.0F);
      }

      this.setPosition(d, e, d2);
   }

   @Override
   public void updatePotionMetadata() {
      this.setInvisible(this.canInteract);
   }

   @Override
   public void setInvisible(boolean invisible) {
      this.canInteract = invisible;
      super.setInvisible(invisible);
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

   private void setSmall(boolean bl) {
      byte b = this.dataWatcher.getWatchableObjectByte(10);
      if (bl) {
         b = (byte)(b | 1);
      } else {
         b = (byte)(b & -2);
      }

      this.dataWatcher.updateObject(10, b);
   }

   public boolean isSmall() {
      return (this.dataWatcher.getWatchableObjectByte(10) & 1) != 0;
   }

   private void setNoGravity(boolean bl) {
      byte b = this.dataWatcher.getWatchableObjectByte(10);
      if (bl) {
         b = (byte)(b | 2);
      } else {
         b = (byte)(b & -3);
      }

      this.dataWatcher.updateObject(10, b);
   }

   public boolean hasNoGravity() {
      return (this.dataWatcher.getWatchableObjectByte(10) & 2) != 0;
   }

   private void setShowArms(boolean bl) {
      byte b = this.dataWatcher.getWatchableObjectByte(10);
      if (bl) {
         b = (byte)(b | 4);
      } else {
         b = (byte)(b & -5);
      }

      this.dataWatcher.updateObject(10, b);
   }

   public boolean getShowArms() {
      return (this.dataWatcher.getWatchableObjectByte(10) & 4) != 0;
   }

   private void setNoBasePlate(boolean bl) {
      byte b = this.dataWatcher.getWatchableObjectByte(10);
      if (bl) {
         b = (byte)(b | 8);
      } else {
         b = (byte)(b & -9);
      }

      this.dataWatcher.updateObject(10, b);
   }

   public boolean hasNoBasePlate() {
      return (this.dataWatcher.getWatchableObjectByte(10) & 8) != 0;
   }

   private void setMarker(boolean bl) {
      byte b = this.dataWatcher.getWatchableObjectByte(10);
      if (bl) {
         b = (byte)(b | 16);
      } else {
         b = (byte)(b & -17);
      }

      this.dataWatcher.updateObject(10, b);
   }

   public boolean hasMarker() {
      return (this.dataWatcher.getWatchableObjectByte(10) & 16) != 0;
   }

   public void setHeadRotation(Rotations rotations) {
      this.headRotation = rotations;
      this.dataWatcher.updateObject(11, rotations);
   }

   public void setBodyRotation(Rotations rotations) {
      this.bodyRotation = rotations;
      this.dataWatcher.updateObject(12, rotations);
   }

   public void setLeftArmRotation(Rotations rotations) {
      this.leftArmRotation = rotations;
      this.dataWatcher.updateObject(13, rotations);
   }

   public void setRightArmRotation(Rotations rotations) {
      this.rightArmRotation = rotations;
      this.dataWatcher.updateObject(14, rotations);
   }

   public void setLeftLegRotation(Rotations rotations) {
      this.leftLegRotation = rotations;
      this.dataWatcher.updateObject(15, rotations);
   }

   public void setRightLegRotation(Rotations rotations) {
      this.rightLegRotation = rotations;
      this.dataWatcher.updateObject(16, rotations);
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
