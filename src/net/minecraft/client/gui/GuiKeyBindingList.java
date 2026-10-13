package net.minecraft.client.gui;

import java.util.Arrays;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumChatFormatting;
import org.apache.commons.lang3.ArrayUtils;

public class GuiKeyBindingList extends GuiListExtended {
   private final GuiControls field_148191_k;
   private final Minecraft mc;
   private final GuiListExtended.IGuiListEntry[] listEntries;
   private int maxListLabelWidth = 0;
   private static final long d = 71108590503191L;

   public GuiKeyBindingList(GuiControls var1, Minecraft var2) {
      super(var2, var1.width, var1.height, 63, var1.height - 32, 20);
      this.field_148191_k = var1;
      this.mc = var2;
      KeyBinding[] var6 = ArrayUtils.clone(var2.gameSettings.keyBindings);
      this.listEntries = new GuiListExtended.IGuiListEntry[var6.length + KeyBinding.getKeybinds().size()];
      Arrays.sort(var6);
      int var7 = 0;
      String var8 = null;

      for (KeyBinding var12 : var6) {
         String var13 = var12.getKeyCategory();
         if (!var13.equals(var8)) {
            var8 = var13;
            this.listEntries[var7++] = new GuiKeyBindingList.CategoryEntry(var13);
         }

         int var14 = var2.fontRendererObj.getStringWidth(I18n.format(var12.getKeyDescription()));
         if (var14 > this.maxListLabelWidth) {
            this.maxListLabelWidth = var14;
         }

         this.listEntries[var7++] = new GuiKeyBindingList.KeyEntry(var12);
      }
   }

   @Override
   public int getSize() {
      return this.listEntries.length;
   }

   @Override
   public GuiListExtended.IGuiListEntry getListEntry(int var1) {
      return this.listEntries[var1];
   }

   @Override
   public int getScrollBarX() {
      return super.getScrollBarX() + 15;
   }

   @Override
   public int getListWidth() {
      return super.getListWidth() + 32;
   }

   public class CategoryEntry implements GuiListExtended.IGuiListEntry {
      private final String labelText;
      private final int labelWidth;
      private static final long a = 112253178002106L;
      private static final long b = 6829617502444584959L;

      public CategoryEntry(String var2) {
         this.labelText = I18n.format(var2);
         this.labelWidth = GuiKeyBindingList.this.mc.fontRendererObj.getStringWidth(this.labelText);
      }

      @Override
      public void drawEntry(int var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8) {
         GuiKeyBindingList.this.mc
            .fontRendererObj
            .D(
               this.labelText,
               GuiKeyBindingList.this.mc.currentScreen.width / 2 - this.labelWidth / 2,
               var3 + var5 - GuiKeyBindingList.this.mc.fontRendererObj.FONT_HEIGHT - 1,
               (int)b
            );
      }

      @Override
      public boolean mousePressed(int var1, int var2, int var3, int var4, int var5, int var6) {
         return false;
      }

      @Override
      public void mouseReleased(int var1, int var2, int var3, int var4, int var5, int var6) {
      }

      @Override
      public void setSelected(int var1, int var2, int var3) {
      }
   }

   public class KeyEntry implements GuiListExtended.IGuiListEntry {
      private final KeyBinding keybinding;
      private final String keyDesc;
      private final GuiButton btnChangeKeyBinding;
      private final GuiButton btnReset;
      private static final long a = 21296537949425L;

      public KeyEntry(KeyBinding var2) {
         this.keybinding = var2;
         this.keyDesc = I18n.format(var2.getKeyDescription());
         this.btnChangeKeyBinding = new GuiButton(0, 0, 0, 75, 20, I18n.format(var2.getKeyDescription()));
         this.btnReset = new GuiButton(0, 0, 0, 50, 20, I18n.format("controls.reset"));
      }

      @Override
      public void drawEntry(int var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8) {
         boolean var12 = GuiKeyBindingList.this.field_148191_k.buttonId == this.keybinding;
         GuiKeyBindingList.this.mc
            .fontRendererObj
            .D(
               this.keyDesc,
               var2 + 90 - GuiKeyBindingList.this.maxListLabelWidth,
               var3 + var5 / 2 - GuiKeyBindingList.this.mc.fontRendererObj.FONT_HEIGHT / 2,
               16777215
            );
         this.btnReset.xPosition = var2 + 190;
         this.btnReset.yPosition = var3;
         this.btnReset.enabled = this.keybinding.getKeyCode() != this.keybinding.getKeyCodeDefault();
         this.btnReset.drawButton(GuiKeyBindingList.this.mc, var6, var7);
         this.btnChangeKeyBinding.xPosition = var2 + 105;
         this.btnChangeKeyBinding.yPosition = var3;
         this.btnChangeKeyBinding.displayString = GameSettings.getKeyDisplayString(this.keybinding.getKeyCode());
         boolean var13 = false;
         if (this.keybinding.getKeyCode() != 0) {
            for (KeyBinding var17 : GuiKeyBindingList.this.mc.gameSettings.keyBindings) {
               if (var17 != this.keybinding && var17.getKeyCode() == this.keybinding.getKeyCode()) {
                  var13 = true;
                  break;
               }
            }
         }

         if (var12) {
            this.btnChangeKeyBinding.displayString = EnumChatFormatting.WHITE
               + "> "
               + EnumChatFormatting.YELLOW
               + this.btnChangeKeyBinding.displayString
               + EnumChatFormatting.WHITE
               + " <";
         } else if (var13) {
            this.btnChangeKeyBinding.displayString = EnumChatFormatting.RED + this.btnChangeKeyBinding.displayString;
         }

         this.btnChangeKeyBinding.drawButton(GuiKeyBindingList.this.mc, var6, var7);
      }

      @Override
      public boolean mousePressed(int var1, int var2, int var3, int var4, int var5, int var6) {
         if (this.btnChangeKeyBinding.mousePressed(GuiKeyBindingList.this.mc, var2, var3)) {
            GuiKeyBindingList.this.field_148191_k.buttonId = this.keybinding;
            return true;
         } else if (this.btnReset.mousePressed(GuiKeyBindingList.this.mc, var2, var3)) {
            GuiKeyBindingList.this.mc.gameSettings.setOptionKeyBinding(this.keybinding, this.keybinding.getKeyCodeDefault());
            KeyBinding.resetKeyBindingArrayAndHash();
            return true;
         } else {
            return false;
         }
      }

      @Override
      public void mouseReleased(int var1, int var2, int var3, int var4, int var5, int var6) {
         this.btnChangeKeyBinding.mouseReleased(var2, var3);
         this.btnReset.mouseReleased(var2, var3);
      }

      @Override
      public void setSelected(int var1, int var2, int var3) {
      }
   }
}
