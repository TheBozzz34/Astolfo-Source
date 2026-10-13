package net.minecraft.tileentity;

import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.ResourceLocation;

public class TileEntityFlowerPot extends TileEntity {
   private Item flowerPotItem;
   private int flowerPotData;
   private static final long a = 128503974742488L;
   private static final long g = -882684580409114616L;

   public TileEntityFlowerPot() {
   }

   public TileEntityFlowerPot(Item var1, int var2) {
      this.flowerPotItem = var1;
      this.flowerPotData = var2;
   }

   @Override
   public void writeToNBT(NBTTagCompound var1) {
      super.writeToNBT(var1);
      ResourceLocation var5 = Item.itemRegistry.getNameForObject(this.flowerPotItem);
      var1.setString("Item", var5 == null ? "" : var5.toString());
      var1.setInteger("Data", this.flowerPotData);
   }

   @Override
   public void readFromNBT(NBTTagCompound var1) {
      super.readFromNBT(var1);
      if (var1.hasKey("Item", (int)g)) {
         this.flowerPotItem = Item.getByNameOrId(var1.getString("Item"));
      } else {
         this.flowerPotItem = Item.getItemById(var1.getInteger("Item"));
      }

      this.flowerPotData = var1.getInteger("Data");
   }

   @Override
   public Packet getDescriptionPacket() {
      NBTTagCompound var3 = new NBTTagCompound();
      this.writeToNBT(var3);
      var3.removeTag("Item");
      var3.setInteger("Item", Item.getIdFromItem(this.flowerPotItem));
      return new S35PacketUpdateTileEntity(this.pos, 5, var3);
   }

   public void setFlowerPotData(Item var1, int var2) {
      this.flowerPotItem = var1;
      this.flowerPotData = var2;
   }

   public Item getFlowerPotItem() {
      return this.flowerPotItem;
   }

   public int getFlowerPotData() {
      return this.flowerPotData;
   }
}
