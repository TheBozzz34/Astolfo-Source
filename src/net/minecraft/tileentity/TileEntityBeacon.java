package net.minecraft.tileentity;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerBeacon;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ITickable;

public class TileEntityBeacon extends TileEntityLockable implements ITickable, IInventory {
   public static final Potion[][] effectsList = new Potion[][]{
      {Potion.moveSpeed, Potion.digSpeed}, {Potion.resistance, Potion.jump}, {Potion.damageBoost}, {Potion.regeneration}
   };
   private final List<TileEntityBeacon.BeamSegment> beamSegments = Lists.newArrayList();
   private long beamRenderCounter;
   private float field_146014_j;
   private boolean isComplete;
   private int levels = -1;
   private int primaryEffect;
   private int secondaryEffect;
   private ItemStack payment;
   private String customName;
   private static final long c = 133922871835393L;

   @Override
   public void update() {
      if (this.worldObj.getTotalWorldTime() % 80L == 0L) {
         this.updateBeacon();
      }
   }

   public void updateBeacon() {
      this.updateSegmentColors();
      this.addEffectsToPlayers();
   }

   private void addEffectsToPlayers() {
      if (this.isComplete && this.levels > 0 && !this.worldObj.isRemote && this.primaryEffect > 0) {
         double var4 = this.levels * 10 + 10;
         byte var6 = 0;
         if (this.levels >= 4 && this.primaryEffect == this.secondaryEffect) {
            var6 = 1;
         }

         int var7 = this.pos.getX();
         int var8 = this.pos.getY();
         int var9 = this.pos.getZ();
         AxisAlignedBB var10 = new AxisAlignedBB(var7, var8, var9, var7 + 1, var8 + 1, var9 + 1)
            .expand(var4, var4, var4)
            .addCoord(0.0, this.worldObj.getHeight(), 0.0);
         List var11 = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, var10);

         for (Object var13$raw : var11) {
            EntityPlayer var13 = (EntityPlayer)var13$raw;
            var13.addPotionEffect(new PotionEffect(this.primaryEffect, 180, var6, true, true));
         }

         if (this.levels >= 4 && this.primaryEffect != this.secondaryEffect && this.secondaryEffect > 0) {
            for (Object var15$raw : var11) {
               EntityPlayer var15 = (EntityPlayer)var15$raw;
               var15.addPotionEffect(new PotionEffect(this.secondaryEffect, 180, 0, true, true));
            }
         }
      }
   }

   private void updateSegmentColors() {
      int var4 = this.levels;
      int var5 = this.pos.getX();
      int var6 = this.pos.getY();
      int var7 = this.pos.getZ();
      this.levels = 0;
      this.beamSegments.clear();
      this.isComplete = true;
      TileEntityBeacon.BeamSegment var8 = new TileEntityBeacon.BeamSegment(EntitySheep.getDyeRgb(EnumDyeColor.WHITE));
      this.beamSegments.add(var8);
      boolean var9 = true;
      BlockPos.MutableBlockPos var10 = new BlockPos.MutableBlockPos();

      for (int var11 = var6 + 1; var11 < 256; var11++) {
         IBlockState var12 = this.worldObj.getBlockState(var10.set(var5, var11, var7));
         float[] var13;
         if (var12.getBlock() == Blocks.stained_glass) {
            var13 = EntitySheep.getDyeRgb(var12.getValue(BlockStainedGlass.COLOR));
         } else {
            if (var12.getBlock() != Blocks.stained_glass_pane) {
               if (var12.getBlock().getLightOpacity() >= 15 && var12.getBlock() != Blocks.bedrock) {
                  this.isComplete = false;
                  this.beamSegments.clear();
                  break;
               }

               var8.incrementHeight();
               continue;
            }

            var13 = EntitySheep.getDyeRgb(var12.getValue(BlockStainedGlassPane.COLOR));
         }

         if (!var9) {
            var13 = new float[]{(var8.getColors()[0] + var13[0]) / 2.0F, (var8.getColors()[1] + var13[1]) / 2.0F, (var8.getColors()[2] + var13[2]) / 2.0F};
         }

         if (Arrays.equals(var13, var8.getColors())) {
            var8.incrementHeight();
         } else {
            var8 = new TileEntityBeacon.BeamSegment(var13);
            this.beamSegments.add(var8);
         }

         var9 = false;
      }

      if (this.isComplete) {
         for (int var17 = 1; var17 <= 4; this.levels = var17++) {
            int var19 = var6 - var17;
            if (var19 < 0) {
               break;
            }

            boolean var21 = true;

            for (int var14 = var5 - var17; var14 <= var5 + var17 && var21; var14++) {
               for (int var15 = var7 - var17; var15 <= var7 + var17; var15++) {
                  Block var16 = this.worldObj.getBlockState(new BlockPos(var14, var19, var15)).getBlock();
                  if (var16 != Blocks.emerald_block && var16 != Blocks.gold_block && var16 != Blocks.diamond_block && var16 != Blocks.iron_block) {
                     var21 = false;
                     break;
                  }
               }
            }

            if (!var21) {
               break;
            }
         }

         if (this.levels == 0) {
            this.isComplete = false;
         }
      }

      if (!this.worldObj.isRemote && this.levels == 4 && var4 < this.levels) {
         for (EntityPlayer var20 : this.worldObj
            .getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(var5, var6, var7, var5, var6 - 4, var7).expand(10.0, 5.0, 10.0))) {
            var20.triggerAchievement(AchievementList.fullBeacon);
         }
      }
   }

   public List<TileEntityBeacon.BeamSegment> getBeamSegments() {
      return this.beamSegments;
   }

   public float shouldBeamRender() {
      if (!this.isComplete) {
         return 0.0F;
      }

      int var4 = (int)(this.worldObj.getTotalWorldTime() - this.beamRenderCounter);
      this.beamRenderCounter = this.worldObj.getTotalWorldTime();
      if (var4 > 1) {
         this.field_146014_j -= var4 / 40.0F;
         if (this.field_146014_j < 0.0F) {
            this.field_146014_j = 0.0F;
         }
      }

      this.field_146014_j += 0.025F;
      if (this.field_146014_j > 1.0F) {
         this.field_146014_j = 1.0F;
      }

      return this.field_146014_j;
   }

   @Override
   public Packet getDescriptionPacket() {
      NBTTagCompound var1 = new NBTTagCompound();
      this.writeToNBT(var1);
      return new S35PacketUpdateTileEntity(this.pos, 3, var1);
   }

   @Override
   public double getMaxRenderDistanceSquared() {
      return 65536.0;
   }

   private int func_183001_h(int var1) {
      if (var1 >= 0 && var1 < Potion.potionTypes.length && Potion.potionTypes[var1] != null) {
         Potion var5 = Potion.potionTypes[var1];
         return var5 != Potion.moveSpeed
               && var5 != Potion.digSpeed
               && var5 != Potion.resistance
               && var5 != Potion.jump
               && var5 != Potion.damageBoost
               && var5 != Potion.regeneration
            ? 0
            : var1;
      } else {
         return 0;
      }
   }

   @Override
   public void readFromNBT(NBTTagCompound var1) {
      super.readFromNBT(var1);
      this.primaryEffect = this.func_183001_h(var1.getInteger("Primary"));
      this.secondaryEffect = this.func_183001_h(var1.getInteger("Secondary"));
      this.levels = var1.getInteger("Levels");
   }

   @Override
   public void writeToNBT(NBTTagCompound var1) {
      super.writeToNBT(var1);
      var1.setInteger("Primary", this.primaryEffect);
      var1.setInteger("Secondary", this.secondaryEffect);
      var1.setInteger("Levels", this.levels);
   }

   @Override
   public int getSizeInventory() {
      return 1;
   }

   @Override
   public ItemStack getStackInSlot(int var1) {
      return var1 == 0 ? this.payment : null;
   }

   @Override
   public ItemStack decrStackSize(int var1, int var2) {
      if (var1 != 0 || this.payment == null) {
         return null;
      } else if (var2 >= this.payment.stackSize) {
         ItemStack var5 = this.payment;
         this.payment = null;
         return var5;
      } else {
         this.payment.stackSize -= var2;
         return new ItemStack(this.payment.getItem(), var2, this.payment.getMetadata());
      }
   }

   @Override
   public ItemStack removeStackFromSlot(int var1) {
      if (var1 == 0 && this.payment != null) {
         ItemStack var5 = this.payment;
         this.payment = null;
         return var5;
      } else {
         return null;
      }
   }

   @Override
   public void setInventorySlotContents(int var1, ItemStack var2) {
      if (var1 == 0) {
         this.payment = var2;
      }
   }

   @Override
   public String getName() {
      return this.hasCustomName() ? this.customName : "container.beacon";
   }

   @Override
   public boolean hasCustomName() {
      return this.customName != null && this.customName.length() > 0;
   }

   public void setName(String var1) {
      this.customName = var1;
   }

   @Override
   public int getInventoryStackLimit() {
      return 1;
   }

   @Override
   public boolean isUseableByPlayer(EntityPlayer var1) {
      return this.worldObj.getTileEntity(this.pos) != this
         ? false
         : var1.getDistanceSq(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5) <= 64.0;
   }

   @Override
   public void openInventory(EntityPlayer var1) {
   }

   @Override
   public void closeInventory(EntityPlayer var1) {
   }

   @Override
   public boolean isItemValidForSlot(int var1, ItemStack var2) {
      return var2.getItem() == Items.emerald || var2.getItem() == Items.diamond || var2.getItem() == Items.gold_ingot || var2.getItem() == Items.iron_ingot;
   }

   @Override
   public String getGuiID() {
      return "minecraft:beacon";
   }

   @Override
   public Container createContainer(InventoryPlayer var1, EntityPlayer var2) {
      return new ContainerBeacon(var1, this);
   }

   @Override
   public int getField(int var1) {
      switch (var1) {
         case 0:
            return this.levels;
         case 1:
            return this.primaryEffect;
         case 2:
            return this.secondaryEffect;
         default:
            return 0;
      }
   }

   @Override
   public void setField(int var1, int var2) {
      switch (var1) {
         case 0:
            this.levels = var2;
            break;
         case 1:
            this.primaryEffect = this.func_183001_h(var2);
            break;
         case 2:
            this.secondaryEffect = this.func_183001_h(var2);
      }
   }

   @Override
   public int getFieldCount() {
      return 3;
   }

   @Override
   public void clear() {
      this.payment = null;
   }

   @Override
   public boolean receiveClientEvent(int var1, int var2) {
      if (var1 == 1) {
         this.updateBeacon();
         return true;
      } else {
         return super.receiveClientEvent(var1, var2);
      }
   }

   public static class BeamSegment {
      private final float[] colors;
      private int height;

      public BeamSegment(float[] var1) {
         this.colors = var1;
         this.height = 1;
      }

      public void incrementHeight() {
         this.height++;
      }

      public float[] getColors() {
         return this.colors;
      }

      public int getHeight() {
         return this.height;
      }
   }
}
