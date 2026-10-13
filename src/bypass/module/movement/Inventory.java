package bypass.module.movement;

import bypass.event.EventHandler;
import bypass.event.EventState;
import bypass.event.Listener;
import bypass.event.network.PacketDirection;
import bypass.event.network.PacketEvent;
import bypass.event.player.UpdateEvent;
import bypass.gui.clickgui.defaultgui.DefaultClickGuiScreen;
import bypass.gui.clickgui.sidebar.SidebarClickGuiScreen;
import bypass.gui.clickgui.transparent.TransparentClickGuiScreen;
import bypass.module.Category;
import bypass.module.Module;
import bypass.setting.BooleanSetting;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public final class Inventory extends Module {
   private final int[] J = new int[]{
      mc.gameSettings.keyBindForward.getKeyCode(),
      mc.gameSettings.keyBindBack.getKeyCode(),
      mc.gameSettings.keyBindRight.getKeyCode(),
      mc.gameSettings.keyBindLeft.getKeyCode(),
      mc.gameSettings.keyBindJump.getKeyCode()
   };
   public static final BooleanSetting percentage = new BooleanSetting("Percentage", "shows percentage of each item", true);
   private final BooleanSetting inventory = new BooleanSetting("Inventory+", "lets u have items in the craft thing", false);
   private final BooleanSetting silent = new BooleanSetting("Silent", "Pranks the server", false);
   @EventHandler(UpdateEvent.class)
   private Listener<UpdateEvent> onUpdate = var1x -> {
      if (var1x.getState() == EventState.PRE) {
         if (!(mc.currentScreen instanceof GuiContainer)
            && !(mc.currentScreen instanceof DefaultClickGuiScreen)
            && !(mc.currentScreen instanceof TransparentClickGuiScreen)
            && !(mc.currentScreen instanceof SidebarClickGuiScreen)
            && !(mc.currentScreen instanceof GuiInventory)) {
            return;
         }

         if (Mouse.isButtonDown(2)) {
            Mouse.setGrabbed(true);
            mc.inGameHasFocus = true;
         } else {
            Mouse.setGrabbed(false);
            mc.inGameHasFocus = false;
         }

         for (int var8 : this.J) {
            KeyBinding.setKeyBindState(var8, !Keyboard.areRepeatEventsEnabled() && Keyboard.isKeyDown(var8));
         }

         if (this.inventory.getValue() && mc.currentScreen instanceof GuiInventory) {
            mc.playerController.u();
         }
      }
   };
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = var1x -> {
      if (var1x.getDirection().equals(PacketDirection.OUTGOING)) {
         if (this.inventory.getValue() && var1x.getPacket() instanceof C0DPacketCloseWindow) {
            var1x.cancel();
         }

         if (this.silent.getValue() && var1x.getPacket() instanceof C16PacketClientStatus) {
            C16PacketClientStatus var5 = (C16PacketClientStatus)var1x.getPacket();
            if (var5.getStatus() == C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT) {
               var1x.cancel();
            }
         }
      }
   };
   private static final long c = 97520652034366L;

   public Inventory(Category var1) {
      super(var1);
      this.addSettings(percentage, this.inventory, this.silent);
      this.setHidden(false);
   }
}
