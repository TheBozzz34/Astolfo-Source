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
         double d = this.levels * 10 + 10;
         byte b = 0;
         if (this.levels >= 4 && this.primaryEffect == this.secondaryEffect) {
            b = 1;
         }

         int i = this.pos.getX();
         int j = this.pos.getY();
         int k = this.pos.getZ();
         AxisAlignedBB axisAlignedBB = new AxisAlignedBB(i, j, k, i + 1, j + 1, k + 1)
            .expand(d, d, d)
            .addCoord(0.0, this.worldObj.getHeight(), 0.0);
         List list = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, axisAlignedBB);

         for (Object object : list) {
            EntityPlayer entityPlayer = (EntityPlayer)object;
            entityPlayer.addPotionEffect(new PotionEffect(this.primaryEffect, 180, b, true, true));
         }

         if (this.levels >= 4 && this.primaryEffect != this.secondaryEffect && this.secondaryEffect > 0) {
            for (Object object2 : list) {
               EntityPlayer entityPlayer2 = (EntityPlayer)object2;
               entityPlayer2.addPotionEffect(new PotionEffect(this.secondaryEffect, 180, 0, true, true));
            }
         }
      }
   }

   private void updateSegmentColors() {
      int i = this.levels;
      int j = this.pos.getX();
      int k = this.pos.getY();
      int l = this.pos.getZ();
      this.levels = 0;
      this.beamSegments.clear();
      this.isComplete = true;
      TileEntityBeacon.BeamSegment beamSegment = new TileEntityBeacon.BeamSegment(EntitySheep.getDyeRgb(EnumDyeColor.WHITE));
      this.beamSegments.add(beamSegment);
      boolean bl = true;
      BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

      for (int m = k + 1; m < 256; m++) {
         IBlockState iBlockState = this.worldObj.getBlockState(mutableBlockPos.set(j, m, l));
         float[] floats;
         if (iBlockState.getBlock() == Blocks.stained_glass) {
            floats = EntitySheep.getDyeRgb(iBlockState.getValue(BlockStainedGlass.COLOR));
         } else {
            if (iBlockState.getBlock() != Blocks.stained_glass_pane) {
               if (iBlockState.getBlock().getLightOpacity() >= 15 && iBlockState.getBlock() != Blocks.bedrock) {
                  this.isComplete = false;
                  this.beamSegments.clear();
                  break;
               }

               beamSegment.incrementHeight();
               continue;
            }

            floats = EntitySheep.getDyeRgb(iBlockState.getValue(BlockStainedGlassPane.COLOR));
         }

         if (!bl) {
            floats = new float[]{(beamSegment.getColors()[0] + floats[0]) / 2.0F, (beamSegment.getColors()[1] + floats[1]) / 2.0F, (beamSegment.getColors()[2] + floats[2]) / 2.0F};
         }

         if (Arrays.equals(floats, beamSegment.getColors())) {
            beamSegment.incrementHeight();
         } else {
            beamSegment = new TileEntityBeacon.BeamSegment(floats);
            this.beamSegments.add(beamSegment);
         }

         bl = false;
      }

      if (this.isComplete) {
         for (int n = 1; n <= 4; this.levels = n++) {
            int i2 = k - n;
            if (i2 < 0) {
               break;
            }

            boolean bl2 = true;

            for (int i3 = j - n; i3 <= j + n && bl2; i3++) {
               for (int i4 = l - n; i4 <= l + n; i4++) {
                  Block block = this.worldObj.getBlockState(new BlockPos(i3, i2, i4)).getBlock();
                  if (block != Blocks.emerald_block && block != Blocks.gold_block && block != Blocks.diamond_block && block != Blocks.iron_block) {
                     bl2 = false;
                     break;
                  }
               }
            }

            if (!bl2) {
               break;
            }
         }

         if (this.levels == 0) {
            this.isComplete = false;
         }
      }

      if (!this.worldObj.isRemote && this.levels == 4 && i < this.levels) {
         for (EntityPlayer entityPlayer : this.worldObj
            .getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(j, k, l, j, k - 4, l).expand(10.0, 5.0, 10.0))) {
            entityPlayer.triggerAchievement(AchievementList.fullBeacon);
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

      int i = (int)(this.worldObj.getTotalWorldTime() - this.beamRenderCounter);
      this.beamRenderCounter = this.worldObj.getTotalWorldTime();
      if (i > 1) {
         this.field_146014_j -= i / 40.0F;
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
      NBTTagCompound nbtTagCompound = new NBTTagCompound();
      this.writeToNBT(nbtTagCompound);
      return new S35PacketUpdateTileEntity(this.pos, 3, nbtTagCompound);
   }

   @Override
   public double getMaxRenderDistanceSquared() {
      return 65536.0;
   }

   private int func_183001_h(int i) {
      if (i >= 0 && i < Potion.potionTypes.length && Potion.potionTypes[i] != null) {
         Potion potion = Potion.potionTypes[i];
         return potion != Potion.moveSpeed
               && potion != Potion.digSpeed
               && potion != Potion.resistance
               && potion != Potion.jump
               && potion != Potion.damageBoost
               && potion != Potion.regeneration
            ? 0
            : i;
      } else {
         return 0;
      }
   }

   @Override
   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      this.primaryEffect = this.func_183001_h(compound.getInteger("Primary"));
      this.secondaryEffect = this.func_183001_h(compound.getInteger("Secondary"));
      this.levels = compound.getInteger("Levels");
   }

   @Override
   public void writeToNBT(NBTTagCompound compound) {
      super.writeToNBT(compound);
      compound.setInteger("Primary", this.primaryEffect);
      compound.setInteger("Secondary", this.secondaryEffect);
      compound.setInteger("Levels", this.levels);
   }

   @Override
   public int getSizeInventory() {
      return 1;
   }

   @Override
   public ItemStack getStackInSlot(int index) {
      return index == 0 ? this.payment : null;
   }

   @Override
   public ItemStack decrStackSize(int index, int count) {
      if (index != 0 || this.payment == null) {
         return null;
      } else if (count >= this.payment.stackSize) {
         ItemStack itemStack = this.payment;
         this.payment = null;
         return itemStack;
      } else {
         this.payment.stackSize -= count;
         return new ItemStack(this.payment.getItem(), count, this.payment.getMetadata());
      }
   }

   @Override
   public ItemStack removeStackFromSlot(int index) {
      if (index == 0 && this.payment != null) {
         ItemStack itemStack = this.payment;
         this.payment = null;
         return itemStack;
      } else {
         return null;
      }
   }

   @Override
   public void setInventorySlotContents(int index, ItemStack stack) {
      if (index == 0) {
         this.payment = stack;
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

   public void setName(String name) {
      this.customName = name;
   }

   @Override
   public int getInventoryStackLimit() {
      return 1;
   }

   @Override
   public boolean isUseableByPlayer(EntityPlayer player) {
      return this.worldObj.getTileEntity(this.pos) != this
         ? false
         : player.getDistanceSq(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5) <= 64.0;
   }

   @Override
   public void openInventory(EntityPlayer player) {
   }

   @Override
   public void closeInventory(EntityPlayer player) {
   }

   @Override
   public boolean isItemValidForSlot(int index, ItemStack stack) {
      return stack.getItem() == Items.emerald || stack.getItem() == Items.diamond || stack.getItem() == Items.gold_ingot || stack.getItem() == Items.iron_ingot;
   }

   @Override
   public String getGuiID() {
      return "minecraft:beacon";
   }

   @Override
   public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
      return new ContainerBeacon(playerInventory, this);
   }

   @Override
   public int getField(int id) {
      switch (id) {
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
   public void setField(int id, int value) {
      switch (id) {
         case 0:
            this.levels = value;
            break;
         case 1:
            this.primaryEffect = this.func_183001_h(value);
            break;
         case 2:
            this.secondaryEffect = this.func_183001_h(value);
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
   public boolean receiveClientEvent(int id, int typeIn) {
      if (id == 1) {
         this.updateBeacon();
         return true;
      } else {
         return super.receiveClientEvent(id, typeIn);
      }
   }

   public static class BeamSegment {
      private final float[] colors;
      private int height;

      public BeamSegment(float[] floats) {
         this.colors = floats;
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
