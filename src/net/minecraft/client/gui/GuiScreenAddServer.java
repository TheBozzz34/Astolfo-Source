package net.minecraft.client.gui;

import com.google.common.base.Predicate;
import java.io.IOException;
import java.net.IDN;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;

public class GuiScreenAddServer extends GuiScreen {
   private final GuiScreen parentScreen;
   private final ServerData serverData;
   private GuiTextField serverIPField;
   private GuiTextField serverNameField;
   private GuiButton serverResourcePacks;
   private Predicate<String> field_181032_r = new Predicate<String>() {
      private static final long a = 15103075447856L;

      public boolean apply(String var1) {
         if (var1.length() == 0) {
            return true;
         }

         String[] var5 = var1.split(":");
         if (var5.length == 0) {
            return true;
         }

         try {
            String var6 = IDN.toASCII(var5[0]);
            return true;
         } catch (IllegalArgumentException var7) {
            return false;
         }
      }
   };
   private static final long b = 66651723247614L;

   public GuiScreenAddServer(GuiScreen var1, ServerData var2) {
      this.parentScreen = var1;
      this.serverData = var2;
   }

   @Override
   public void updateScreen() {
      this.serverNameField.updateCursorCounter();
      this.serverIPField.updateCursorCounter();
   }

   @Override
   public void initGui() {
      Keyboard.enableRepeatEvents(true);
      this.buttonList.clear();
      this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + 18, I18n.format("addServer.add")));
      this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + 18, I18n.format("gui.cancel")));
      this.buttonList
         .add(
            this.serverResourcePacks = new GuiButton(
               2,
               this.width / 2 - 100,
               this.height / 4 + 72,
               I18n.format("addServer.resourcePack") + ": " + this.serverData.getResourceMode().getMotd().getFormattedText()
            )
         );
      this.serverNameField = new GuiTextField(0, this.fontRendererObj, this.width / 2 - 100, 66, 200, 20);
      this.serverNameField.setFocused(true);
      this.serverNameField.setText(this.serverData.serverName);
      this.serverIPField = new GuiTextField(1, this.fontRendererObj, this.width / 2 - 100, 106, 200, 20);
      this.serverIPField.setMaxStringLength(128);
      this.serverIPField.setText(this.serverData.serverIP);
      this.serverIPField.setValidator(this.field_181032_r);
      this.buttonList.get(0).enabled = this.serverIPField.getText().length() > 0
         && this.serverIPField.getText().split(":").length > 0
         && this.serverNameField.getText().length() > 0;
   }

   @Override
   public void onGuiClosed() {
      Keyboard.enableRepeatEvents(false);
   }

   @Override
   public void actionPerformed(GuiButton var1) throws IOException {
      if (var1.enabled) {
         if (var1.id == 2) {
            this.serverData
               .setResourceMode(
                  ServerData.ServerResourceMode.values()[(this.serverData.getResourceMode().ordinal() + 1) % ServerData.ServerResourceMode.values().length]
               );
            this.serverResourcePacks.displayString = I18n.format("addServer.resourcePack")
               + ": "
               + this.serverData.getResourceMode().getMotd().getFormattedText();
         } else if (var1.id == 1) {
            this.parentScreen.confirmClicked(false, 0);
         } else if (var1.id == 0) {
            this.serverData.serverName = this.serverNameField.getText();
            this.serverData.serverIP = this.serverIPField.getText();
            this.parentScreen.confirmClicked(true, 0);
         }
      }
   }

   @Override
   public void keyTyped(char var1, int var2) throws IOException {
      this.serverNameField.textboxKeyTyped(var1, var2);
      this.serverIPField.textboxKeyTyped(var1, var2);
      if (var2 == 15) {
         this.serverNameField.setFocused(!this.serverNameField.isFocused());
         this.serverIPField.setFocused(!this.serverIPField.isFocused());
      }

      if (var2 == 28 || var2 == 156) {
         this.actionPerformed(this.buttonList.get(0));
      }

      this.buttonList.get(0).enabled = this.serverIPField.getText().length() > 0
         && this.serverIPField.getText().split(":").length > 0
         && this.serverNameField.getText().length() > 0;
   }

   @Override
   public void mouseClicked(int var1, int var2, int var3) throws IOException {
      super.mouseClicked(var1, var2, var3);
      this.serverIPField.mouseClicked(var1, var2, var3);
      this.serverNameField.mouseClicked(var1, var2, var3);
   }

   @Override
   public void drawScreen(int var1, int var2, float var3) {
      this.drawDefaultBackground();
      this.d(this.fontRendererObj, I18n.format("addServer.title"), this.width / 2, 17, 16777215);
      this.I(this.fontRendererObj, I18n.format("addServer.enterName"), this.width / 2 - 100, 53, 10526880);
      this.I(this.fontRendererObj, I18n.format("addServer.enterIp"), this.width / 2 - 100, 94, 10526880);
      this.serverNameField.drawTextBox();
      this.serverIPField.drawTextBox();
      super.drawScreen(var1, var2, var3);
   }
}
