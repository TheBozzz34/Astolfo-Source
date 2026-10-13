package net.minecraft.client.gui;

import bypass.Astolfo;
import com.google.common.collect.Lists;
import java.awt.Color;
import java.io.IOException;
import java.util.List;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class GuiChat extends GuiScreen {
   private static final Logger logger = LogManager.getLogger();
   private String historyBuffer = "";
   private int sentHistoryCursor = -1;
   private boolean playerNamesFound;
   private boolean waitingOnAutocomplete;
   private int autocompleteIndex;
   private List<String> foundPlayerNames = Lists.newArrayList();
   public GuiTextField inputField;
   private String defaultInputFieldText = "";
   private static final long a = 94312828732271L;

   public GuiChat() {
   }

   public GuiChat(String var1) {
      this.defaultInputFieldText = var1;
   }

   @Override
   public void initGui() {
      Keyboard.enableRepeatEvents(true);
      this.sentHistoryCursor = this.mc.ingameGUI.getChatGUI().getSentMessages().size();
      this.inputField = new GuiTextField(0, this.fontRendererObj, 4, this.height - 12, this.width - 4, 12);
      this.inputField.setMaxStringLength(100);
      this.inputField.setEnableBackgroundDrawing(false);
      this.inputField.setFocused(true);
      this.inputField.setText(this.defaultInputFieldText);
      this.inputField.setCanLoseFocus(false);
   }

   @Override
   public void onGuiClosed() {
      Keyboard.enableRepeatEvents(false);
      this.mc.ingameGUI.getChatGUI().resetScroll();
   }

   @Override
   public void updateScreen() {
      this.inputField.updateCursorCounter();
   }

   @Override
   public void keyTyped(char var1, int var2) throws IOException {
      this.waitingOnAutocomplete = false;
      if (var2 == 15) {
         this.autocompletePlayerNames();
      } else {
         this.playerNamesFound = false;
      }

      if (var2 == 1) {
         this.mc.displayGuiScreen((GuiScreen)null);
      } else if (var2 == 28 || var2 == 156) {
         String var6 = this.inputField.getText().trim();
         if (var6.length() > 0) {
            this.sendChatMessage(var6);
         }

         this.mc.displayGuiScreen((GuiScreen)null);
      } else if (var2 == 200) {
         this.getSentHistory(-1);
      } else if (var2 == 208) {
         this.getSentHistory(1);
      } else if (var2 == 201) {
         this.mc.ingameGUI.getChatGUI().scroll(this.mc.ingameGUI.getChatGUI().getLineCount() - 1);
      } else if (var2 == 209) {
         this.mc.ingameGUI.getChatGUI().scroll(-this.mc.ingameGUI.getChatGUI().getLineCount() + 1);
      } else {
         this.inputField.textboxKeyTyped(var1, var2);
      }
   }

   @Override
   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      int var4 = Mouse.getEventDWheel();
      if (var4 != 0) {
         if (var4 > 1) {
            var4 = 1;
         }

         if (var4 < -1) {
            var4 = -1;
         }

         if (!isShiftKeyDown()) {
            var4 *= 7;
         }

         this.mc.ingameGUI.getChatGUI().scroll(var4);
      }
   }

   @Override
   public void mouseClicked(int var1, int var2, int var3) throws IOException {
      if (var3 == 0) {
         IChatComponent var7 = this.mc.ingameGUI.getChatGUI().getChatComponent(Mouse.getX(), Mouse.getY());
         if (this.handleComponentClick(var7)) {
            return;
         }
      }

      this.inputField.mouseClicked(var1, var2, var3);
      super.mouseClicked(var1, var2, var3);
   }

   @Override
   public void setText(String var1, boolean var2) {
      if (var2) {
         this.inputField.setText(var1);
      } else {
         this.inputField.writeText(var1);
      }
   }

   public void autocompletePlayerNames() {
      if (this.playerNamesFound) {
         this.inputField.deleteFromCursor(this.inputField.func_146197_a(-1, this.inputField.getCursorPosition(), false) - this.inputField.getCursorPosition());
         if (this.autocompleteIndex >= this.foundPlayerNames.size()) {
            this.autocompleteIndex = 0;
         }
      } else {
         int var4 = this.inputField.func_146197_a(-1, this.inputField.getCursorPosition(), false);
         this.foundPlayerNames.clear();
         this.autocompleteIndex = 0;
         String var5 = this.inputField.getText().substring(var4).toLowerCase();
         String var6 = this.inputField.getText().substring(0, this.inputField.getCursorPosition());
         this.sendAutocompleteRequest(var6, var5);
         if (this.foundPlayerNames.isEmpty()) {
            return;
         }

         this.playerNamesFound = true;
         this.inputField.deleteFromCursor(var4 - this.inputField.getCursorPosition());
      }

      if (this.foundPlayerNames.size() > 1) {
         StringBuilder var7 = new StringBuilder();

         for (String var9 : this.foundPlayerNames) {
            if (var7.length() > 0) {
               var7.append(", ");
            }

            var7.append(var9);
         }

         this.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new ChatComponentText(var7.toString()), 1);
      }

      this.inputField.writeText(this.foundPlayerNames.get(this.autocompleteIndex++));
   }

   private void sendAutocompleteRequest(String var1, String var2) {
      if (var1.length() >= 1) {
         BlockPos var6 = null;
         if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            var6 = this.mc.objectMouseOver.getBlockPos();
         }

         this.mc.thePlayer.sendQueue.addToSendQueue(new C14PacketTabComplete(var1, var6));
         this.waitingOnAutocomplete = true;
      }
   }

   public void getSentHistory(int var1) {
      int var5 = this.sentHistoryCursor + var1;
      int var6 = this.mc.ingameGUI.getChatGUI().getSentMessages().size();
      var5 = MathHelper.clamp_int(var5, 0, var6);
      if (var5 != this.sentHistoryCursor) {
         if (var5 == var6) {
            this.sentHistoryCursor = var6;
            this.inputField.setText(this.historyBuffer);
         } else {
            if (this.sentHistoryCursor == var6) {
               this.historyBuffer = this.inputField.getText();
            }

            this.inputField.setText(this.mc.ingameGUI.getChatGUI().getSentMessages().get(var5));
            this.sentHistoryCursor = var5;
         }
      }
   }

   @Override
   public void drawScreen(int var1, int var2, float var3) {
      if (this.inputField.getText().startsWith("-")) {
         GlStateManager.enableBlend();
         drawRect(
            1,
            this.height - 15,
            this.width - 1,
            this.height - 1,
            new Color(
                  Astolfo.INSTANCE.getColorSetting().getValue().getRed(),
                  Astolfo.INSTANCE.getColorSetting().getValue().getGreen(),
                  Astolfo.INSTANCE.getColorSetting().getValue().getBlue(),
                  120
               )
               .getRGB()
         );
         GlStateManager.disableBlend();
      }

      drawRect(2, this.height - 14, this.width - 2, this.height - 2, Integer.MIN_VALUE);
      this.inputField.drawTextBox();
      IChatComponent var7 = this.mc.ingameGUI.getChatGUI().getChatComponent(Mouse.getX(), Mouse.getY());
      if (var7 != null && var7.getChatStyle().getChatHoverEvent() != null) {
         this.handleComponentHover(var7, var1, var2);
      }

      super.drawScreen(var1, var2, var3);
   }

   public void onAutocompleteResponse(String[] var1) {
      if (this.waitingOnAutocomplete) {
         this.playerNamesFound = false;
         this.foundPlayerNames.clear();

         for (String var8 : var1) {
            if (var8.length() > 0) {
               this.foundPlayerNames.add(var8);
            }
         }

         String var9 = this.inputField.getText().substring(this.inputField.func_146197_a(-1, this.inputField.getCursorPosition(), false));
         String var10 = StringUtils.getCommonPrefix(var1);
         if (var10.length() > 0 && !var9.equalsIgnoreCase(var10)) {
            this.inputField
               .deleteFromCursor(this.inputField.func_146197_a(-1, this.inputField.getCursorPosition(), false) - this.inputField.getCursorPosition());
            this.inputField.writeText(var10);
         } else if (this.foundPlayerNames.size() > 0) {
            this.playerNamesFound = true;
            this.autocompletePlayerNames();
         }
      }
   }

   @Override
   public boolean doesGuiPauseGame() {
      return false;
   }
}
