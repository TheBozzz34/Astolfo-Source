package net.minecraft.entity.passive;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIEatGrass;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class EntitySheep extends EntityAnimal {
   private final InventoryCrafting inventoryCrafting = new InventoryCrafting(new Container() {
      @Override
      public boolean canInteractWith(EntityPlayer var1) {
         return false;
      }
   }, 2, 1);
   private static final Map<EnumDyeColor, float[]> DYE_TO_RGB = Maps.newEnumMap(EnumDyeColor.class);
   private int sheepTimer;
   private EntityAIEatGrass entityAIEatGrass = new EntityAIEatGrass(this);
   private static final long gb = 2364259734577L;

   public static float[] getDyeRgb(EnumDyeColor var0) {
      return DYE_TO_RGB.get(var0);
   }

   public EntitySheep(World var1) {
      super(var1);
      this.setSize(0.9F, 1.3F);
      ((PathNavigateGround)this.getNavigator()).setAvoidsWater(true);
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(1, new EntityAIPanic(this, 1.25));
      this.tasks.addTask(2, new EntityAIMate(this, 1.0));
      this.tasks.addTask(3, new EntityAITempt(this, 1.1, Items.wheat, false));
      this.tasks.addTask(4, new EntityAIFollowParent(this, 1.1));
      this.tasks.addTask(5, this.entityAIEatGrass);
      this.tasks.addTask(6, new EntityAIWander(this, 1.0));
      this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
      this.tasks.addTask(8, new EntityAILookIdle(this));
      this.inventoryCrafting.setInventorySlotContents(0, new ItemStack(Items.dye, 1, 0));
      this.inventoryCrafting.setInventorySlotContents(1, new ItemStack(Items.dye, 1, 0));
   }

   @Override
   public void updateAITasks() {
      this.sheepTimer = this.entityAIEatGrass.getEatingGrassTimer();
      super.updateAITasks();
   }

   @Override
   public void onLivingUpdate() {
      if (this.worldObj.isRemote) {
         this.sheepTimer = Math.max(0, this.sheepTimer - 1);
      }

      super.onLivingUpdate();
   }

   @Override
   public void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(8.0);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.23F);
   }

   @Override
   public void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(16, new Byte((byte)0));
   }

   @Override
   public void dropFewItems(boolean var1, int var2) {
      if (!this.getSheared()) {
         this.entityDropItem(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, this.getFleeceColor().getMetadata()), 0.0F);
      }

      int var6 = this.rand.nextInt(2) + 1 + this.rand.nextInt(1 + var2);

      for (int var7 = 0; var7 < var6; var7++) {
         if (this.isBurning()) {
            this.dropItem(Items.cooked_mutton, 1);
         } else {
            this.dropItem(Items.mutton, 1);
         }
      }
   }

   @Override
   public Item getDropItem() {
      return Item.getItemFromBlock(Blocks.wool);
   }

   @Override
   public void handleStatusUpdate(byte var1) {
      if (var1 == 10) {
         this.sheepTimer = 40;
      } else {
         super.handleStatusUpdate(var1);
      }
   }

   public float getHeadRotationPointY(float var1) {
      return this.sheepTimer <= 0
         ? 0.0F
         : (
            this.sheepTimer >= 4 && this.sheepTimer <= 36
               ? 1.0F
               : (this.sheepTimer < 4 ? (this.sheepTimer - var1) / 4.0F : -(this.sheepTimer - 40 - var1) / 4.0F)
         );
   }

   public float getHeadRotationAngleX(float var1) {
      if (this.sheepTimer > 4 && this.sheepTimer <= 36) {
         float var5 = (this.sheepTimer - 4 - var1) / 32.0F;
         return (float) (Math.PI / 5) + 0.2199115F * MathHelper.sin(var5 * 28.7F);
      } else {
         return this.sheepTimer > 0 ? (float) (Math.PI / 5) : this.rotationPitch / (180.0F / (float)Math.PI);
      }
   }

   @Override
   public boolean interact(EntityPlayer var1) {
      ItemStack var5 = var1.inventory.getCurrentItem();
      if (var5 != null && var5.getItem() == Items.shears && !this.getSheared() && !this.isChild()) {
         if (!this.worldObj.isRemote) {
            this.setSheared(true);
            int var6 = 1 + this.rand.nextInt(3);

            for (int var7 = 0; var7 < var6; var7++) {
               EntityItem var8 = this.entityDropItem(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, this.getFleeceColor().getMetadata()), 1.0F);
               var8.motionY = var8.motionY + this.rand.nextFloat() * 0.05F;
               var8.motionX = var8.motionX + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F;
               var8.motionZ = var8.motionZ + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F;
            }
         }

         var5.damageItem(1, var1);
         this.playSound("mob.sheep.shear", 1.0F, 1.0F);
      }

      return super.interact(var1);
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound var1) {
      super.writeEntityToNBT(var1);
      var1.setBoolean("Sheared", this.getSheared());
      var1.setByte("Color", (byte)this.getFleeceColor().getMetadata());
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound var1) {
      super.readEntityFromNBT(var1);
      this.setSheared(var1.getBoolean("Sheared"));
      this.setFleeceColor(EnumDyeColor.byMetadata(var1.getByte("Color")));
   }

   @Override
   public String getLivingSound() {
      return "mob.sheep.say";
   }

   @Override
   public String getHurtSound() {
      return "mob.sheep.say";
   }

   @Override
   public String getDeathSound() {
      return "mob.sheep.say";
   }

   @Override
   public void playStepSound(BlockPos var1, Block var2) {
      this.playSound("mob.sheep.step", 0.15F, 1.0F);
   }

   public EnumDyeColor getFleeceColor() {
      return EnumDyeColor.byMetadata(this.dataWatcher.getWatchableObjectByte(16) & 15);
   }

   public void setFleeceColor(EnumDyeColor var1) {
      byte var4 = this.dataWatcher.getWatchableObjectByte(16);
      this.dataWatcher.updateObject(16, (byte)(var4 & 240 | var1.getMetadata() & 15));
   }

   public boolean getSheared() {
      return (this.dataWatcher.getWatchableObjectByte(16) & 16) != 0;
   }

   public void setSheared(boolean var1) {
      byte var5 = this.dataWatcher.getWatchableObjectByte(16);
      if (var1) {
         this.dataWatcher.updateObject(16, (byte)(var5 | 16));
      } else {
         this.dataWatcher.updateObject(16, (byte)(var5 & -17));
      }
   }

   public static EnumDyeColor getRandomSheepColor(Random var0) {
      int var4 = var0.nextInt(100);
      return var4 < 5
         ? EnumDyeColor.BLACK
         : (
            var4 < 10
               ? EnumDyeColor.GRAY
               : (var4 < 15 ? EnumDyeColor.SILVER : (var4 < 18 ? EnumDyeColor.BROWN : (var0.nextInt(500) == 0 ? EnumDyeColor.PINK : EnumDyeColor.WHITE)))
         );
   }

   public EntitySheep createChild(EntityAgeable var1) {
      EntitySheep var2 = (EntitySheep)var1;
      EntitySheep var3 = new EntitySheep(this.worldObj);
      var3.setFleeceColor(this.getDyeColorMixFromParents(this, var2));
      return var3;
   }

   @Override
   public void eatGrassBonus() {
      this.setSheared(false);
      if (this.isChild()) {
         this.addGrowth(60);
      }
   }

   @Override
   public IEntityLivingData onInitialSpawn(DifficultyInstance var1, IEntityLivingData var2) {
      var2 = super.onInitialSpawn(var1, var2);
      this.setFleeceColor(getRandomSheepColor(this.worldObj.rand));
      return var2;
   }

   private EnumDyeColor getDyeColorMixFromParents(EntityAnimal var1, EntityAnimal var2) {
      int var6 = ((EntitySheep)var1).getFleeceColor().getDyeDamage();
      int var7 = ((EntitySheep)var2).getFleeceColor().getDyeDamage();
      this.inventoryCrafting.getStackInSlot(0).setItemDamage(var6);
      this.inventoryCrafting.getStackInSlot(1).setItemDamage(var7);
      ItemStack var8 = CraftingManager.getInstance().findMatchingRecipe(this.inventoryCrafting, ((EntitySheep)var1).worldObj);
      int var9;
      if (var8 != null && var8.getItem() == Items.dye) {
         var9 = var8.getMetadata();
      } else {
         var9 = this.worldObj.rand.nextBoolean() ? var6 : var7;
      }

      return EnumDyeColor.byDyeDamage(var9);
   }

   @Override
   public float getEyeHeight() {
      return 0.95F * this.height;
   }

   static {
      DYE_TO_RGB.put(EnumDyeColor.WHITE, new float[]{1.0F, 1.0F, 1.0F});
      DYE_TO_RGB.put(EnumDyeColor.ORANGE, new float[]{0.85F, 0.5F, 0.2F});
      DYE_TO_RGB.put(EnumDyeColor.MAGENTA, new float[]{0.7F, 0.3F, 0.85F});
      DYE_TO_RGB.put(EnumDyeColor.LIGHT_BLUE, new float[]{0.4F, 0.6F, 0.85F});
      DYE_TO_RGB.put(EnumDyeColor.YELLOW, new float[]{0.9F, 0.9F, 0.2F});
      DYE_TO_RGB.put(EnumDyeColor.LIME, new float[]{0.5F, 0.8F, 0.1F});
      DYE_TO_RGB.put(EnumDyeColor.PINK, new float[]{0.95F, 0.5F, 0.65F});
      DYE_TO_RGB.put(EnumDyeColor.GRAY, new float[]{0.3F, 0.3F, 0.3F});
      DYE_TO_RGB.put(EnumDyeColor.SILVER, new float[]{0.6F, 0.6F, 0.6F});
      DYE_TO_RGB.put(EnumDyeColor.CYAN, new float[]{0.3F, 0.5F, 0.6F});
      DYE_TO_RGB.put(EnumDyeColor.PURPLE, new float[]{0.5F, 0.25F, 0.7F});
      DYE_TO_RGB.put(EnumDyeColor.BLUE, new float[]{0.2F, 0.3F, 0.7F});
      DYE_TO_RGB.put(EnumDyeColor.BROWN, new float[]{0.4F, 0.3F, 0.2F});
      DYE_TO_RGB.put(EnumDyeColor.GREEN, new float[]{0.4F, 0.5F, 0.2F});
      DYE_TO_RGB.put(EnumDyeColor.RED, new float[]{0.6F, 0.2F, 0.2F});
      DYE_TO_RGB.put(EnumDyeColor.BLACK, new float[]{0.1F, 0.1F, 0.1F});
   }
}
