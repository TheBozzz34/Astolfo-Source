package net.minecraft.client.player.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.IInteractionObject;

public class LocalBlockIntercommunication implements IInteractionObject {
   private String guiID;
   private IChatComponent displayName;
   private static final long a = 88185268112468L;

   public LocalBlockIntercommunication(String var1, IChatComponent var2) {
      this.guiID = var1;
      this.displayName = var2;
   }

   @Override
   public Container createContainer(InventoryPlayer var1, EntityPlayer var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public String getName() {
      return this.displayName.getUnformattedText();
   }

   @Override
   public boolean hasCustomName() {
      return true;
   }

   @Override
   public String getGuiID() {
      return this.guiID;
   }

   @Override
   public IChatComponent getDisplayName() {
      return this.displayName;
   }
}
