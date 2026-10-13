package net.minecraft.client.player.inventory;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.LockCode;

public class ContainerLocalMenu extends InventoryBasic implements ILockableContainer {
   private String guiID;
   private Map<Integer, Integer> field_174895_b = Maps.newHashMap();
   private static final long a = 13462063080658L;

   public ContainerLocalMenu(String var1, IChatComponent var2, int var3) {
      super(var2, var3);
      this.guiID = var1;
   }

   @Override
   public int getField(int var1) {
      return this.field_174895_b.containsKey(var1) ? this.field_174895_b.get(var1) : 0;
   }

   @Override
   public void setField(int var1, int var2) {
      this.field_174895_b.put(var1, var2);
   }

   @Override
   public int getFieldCount() {
      return this.field_174895_b.size();
   }

   @Override
   public boolean isLocked() {
      return false;
   }

   @Override
   public void setLockCode(LockCode var1) {
   }

   @Override
   public LockCode getLockCode() {
      return LockCode.EMPTY_CODE;
   }

   @Override
   public String getGuiID() {
      return this.guiID;
   }

   @Override
   public Container createContainer(InventoryPlayer var1, EntityPlayer var2) {
      throw new UnsupportedOperationException();
   }
}
