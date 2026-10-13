package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.play.client.C0BPacketEntityAction;

public class GuiSleepMP extends GuiChat {
   private static final long b = 107945530625452L;

   @Override
   public void initGui() {
      super.initGui();
      this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height - 40, I18n.format("multiplayer.stopSleeping")));
   }

   @Override
   public void keyTyped(char var1, int var2) throws IOException {
      if (var2 == 1) {
         this.wakeFromSleep();
      } else if (var2 != 28 && var2 != 156) {
         super.keyTyped(var1, var2);
      } else {
         String var6 = this.inputField.getText().trim();
         if (!var6.isEmpty()) {
            this.mc.thePlayer.sendChatMessage(var6);
         }

         this.inputField.setText("");
         this.mc.ingameGUI.getChatGUI().resetScroll();
      }
   }

   @Override
   public void actionPerformed(GuiButton var1) throws IOException {
      if (var1.id == 1) {
         this.wakeFromSleep();
      } else {
         super.actionPerformed(var1);
      }
   }

   private void wakeFromSleep() {
      NetHandlerPlayClient var1 = this.mc.thePlayer.sendQueue;
      var1.addToSendQueue(new C0BPacketEntityAction(this.mc.thePlayer, C0BPacketEntityAction.Action.STOP_SLEEPING));
   }
}
