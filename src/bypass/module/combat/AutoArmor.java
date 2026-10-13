package bypass.module.combat;

import bypass.event.EventHandler;
import bypass.event.EventState;
import bypass.event.Listener;
import bypass.event.network.PacketDirection;
import bypass.event.network.PacketEvent;
import bypass.event.player.MotionEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.setting.BooleanSetting;
import bypass.setting.NumberSetting;
import bypass.util.InventoryUtil;
import bypass.util.Stopwatch;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.util.EnumChatFormatting;

public final class AutoArmor extends Module {
   private final Stopwatch z = new Stopwatch();
   private int f;
   private final NumberSetting<Integer> delay = new NumberSetting<>("Delay", "Delay in milliseconds between moving each item", 250, 0, 1000, "ms");
   private final BooleanSetting drop = new BooleanSetting("Drop", "Drops old armor", true);
   private final BooleanSetting inventory = new BooleanSetting("Inventory", "Only changes armour if in inventory", false);
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = var1x -> {
      if (var1x.getDirection() == PacketDirection.OUTGOING && var1x.getPacket() instanceof C0EPacketClickWindow) {
         this.z.reset();
      }
   };
   @EventHandler(MotionEvent.class)
   private Listener<MotionEvent> onMotion = var1x -> {
      long var2 = c ^ 2471658156914L;
      if (var1x.getState() == EventState.PRE) {
         if (mc.currentScreen instanceof GuiContainer && !(mc.currentScreen instanceof GuiInventory)) {
            return;
         }

         if (!(mc.currentScreen instanceof GuiInventory) && this.inventory.getValue()) {
            return;
         }

         ArmorSlot var5 = null;
         int var6 = -1;
         long var10000 = 3949560276703916607L ^ var2;
         int var7 = 0;

         for (int var8 = 9; var8 <= 44; var8++) {
            ItemStack var9 = mc.thePlayer.inventoryContainer.getSlot(var8).getStack();
            if (var9 != null && var9.getItem() instanceof ItemArmor) {
               ItemArmor var10 = (ItemArmor)var9.getItem();
               ArmorSlot var11 = N(var10.armorType);
               if (var11 != null && var10.armorType == this.f) {
                  int var12 = InventoryUtil.getArmorScore(var9);
                  Slot var13 = mc.thePlayer.inventoryContainer.getSlot(var11.slot);
                  int var14 = InventoryUtil.getArmorScore(var13.getStack());
                  if (var12 != var14) {
                     if (var12 > var14 && var12 > var7) {
                        var5 = var11;
                        var6 = var8;
                        var7 = var12;
                     }
                  } else if (var9.hasEffect() && !var13.getStack().hasEffect()) {
                     var5 = var11;
                     var6 = var8;
                     var7 = var12;
                  }
               }
            }
         }

         if (var6 >= 0 && this.z.hasElapsed(this.delay.getValue().intValue())) {
            ItemStack var15 = mc.thePlayer.inventoryContainer.getSlot(var5.slot).getStack();
            if (var15 == null) {
               mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, var6, 0, 1, mc.thePlayer);
            } else if (KillAura.target == null) {
               if (this.drop.getValue()) {
                  InventoryUtil.dropSlot(var5.slot, 0);
               } else if (!c()) {
                  mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, var5.slot, 0, 1, mc.thePlayer);
               } else {
                  mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, var5.slot, 0, 4, mc.thePlayer);
               }
            }
         }

         if (this.f < 3) {
            this.f++;
         } else {
            this.f = 0;
         }
      }
   };
   private static final long c = 109552791208972L;

   public AutoArmor(Category var1) {
      super(var1);
      this.addSettings(this.delay, this.drop, this.inventory);
   }

   @Override
   public void p() {
      this.setDisplayName("Auto Armor " + EnumChatFormatting.GRAY + this.delay.getValue());
      super.p();
   }

   @Override
   public void onDisable() {
      this.z.reset();
      super.onDisable();
   }

   public static boolean c() {
      boolean var3 = true;

      for (int var4 = 0; var4 < 35; var4++) {
         if (mc.thePlayer.inventory.getStackInSlot(var4) == null) {
            var3 = false;
         }
      }

      return var3;
   }

   public static ArmorSlot N(int var0) {
      for (ArmorSlot var7 : ArmorSlot.values()) {
         if (ArmorSlot.z(var7) == var0) {
            return var7;
         }
      }

      return null;
   }
}
