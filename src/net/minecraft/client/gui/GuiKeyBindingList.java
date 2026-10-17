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

   public GuiKeyBindingList(GuiControls controls, Minecraft mcIn) {
      super(mcIn, controls.width, controls.height, 63, controls.height - 32, 20);
      this.field_148191_k = controls;
      this.mc = mcIn;
      KeyBinding[] keyBindings2 = ArrayUtils.clone(mcIn.gameSettings.keyBindings);
      this.listEntries = new GuiListExtended.IGuiListEntry[keyBindings2.length + KeyBinding.getKeybinds().size()];
      Arrays.sort(keyBindings2);
      int i = 0;
      String string = null;

      for (KeyBinding keyBinding : keyBindings2) {
         String string2 = keyBinding.getKeyCategory();
         if (!string2.equals(string)) {
            string = string2;
            this.listEntries[i++] = new GuiKeyBindingList.CategoryEntry(string2);
         }

         int j = mcIn.fontRendererObj.getStringWidth(I18n.format(keyBinding.getKeyDescription()));
         if (j > this.maxListLabelWidth) {
            this.maxListLabelWidth = j;
         }

         this.listEntries[i++] = new GuiKeyBindingList.KeyEntry(keyBinding);
      }
   }

   @Override
   public int getSize() {
      return this.listEntries.length;
   }

   @Override
   public GuiListExtended.IGuiListEntry getListEntry(int index) {
      return this.listEntries[index];
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

      public CategoryEntry(String string) {
         this.labelText = I18n.format(string);
         this.labelWidth = GuiKeyBindingList.this.mc.fontRendererObj.getStringWidth(this.labelText);
      }

      @Override
      public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeightIn, int mouseXIn, int mouseYIn, boolean isSelected) {
         GuiKeyBindingList.this.mc
            .fontRendererObj
            .drawStringNoShadow(
               this.labelText,
               GuiKeyBindingList.this.mc.currentScreen.width / 2 - this.labelWidth / 2,
               y + slotHeightIn - GuiKeyBindingList.this.mc.fontRendererObj.FONT_HEIGHT - 1,
               16777215
            );
      }

      @Override
      public boolean mousePressed(int slotIndex, int i, int j, int k, int l, int m) {
         return false;
      }

      @Override
      public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
      }

      @Override
      public void setSelected(int i, int j, int k) {
      }
   }

   public class KeyEntry implements GuiListExtended.IGuiListEntry {
      private final KeyBinding keybinding;
      private final String keyDesc;
      private final GuiButton btnChangeKeyBinding;
      private final GuiButton btnReset;

      public KeyEntry(KeyBinding keyBinding) {
         this.keybinding = keyBinding;
         this.keyDesc = I18n.format(keyBinding.getKeyDescription());
         this.btnChangeKeyBinding = new GuiButton(0, 0, 0, 75, 20, I18n.format(keyBinding.getKeyDescription()));
         this.btnReset = new GuiButton(0, 0, 0, 50, 20, I18n.format("controls.reset"));
      }

      @Override
      public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeightIn, int mouseXIn, int mouseYIn, boolean isSelected) {
         boolean bl = GuiKeyBindingList.this.field_148191_k.buttonId == this.keybinding;
         GuiKeyBindingList.this.mc
            .fontRendererObj
            .drawStringNoShadow(
               this.keyDesc,
               x + 90 - GuiKeyBindingList.this.maxListLabelWidth,
               y + slotHeightIn / 2 - GuiKeyBindingList.this.mc.fontRendererObj.FONT_HEIGHT / 2,
               16777215
            );
         this.btnReset.xPosition = x + 190;
         this.btnReset.yPosition = y;
         this.btnReset.enabled = this.keybinding.getKeyCode() != this.keybinding.getKeyCodeDefault();
         this.btnReset.drawButton(GuiKeyBindingList.this.mc, mouseXIn, mouseYIn);
         this.btnChangeKeyBinding.xPosition = x + 105;
         this.btnChangeKeyBinding.yPosition = y;
         this.btnChangeKeyBinding.displayString = GameSettings.getKeyDisplayString(this.keybinding.getKeyCode());
         boolean bl2 = false;
         if (this.keybinding.getKeyCode() != 0) {
            for (KeyBinding keyBinding : GuiKeyBindingList.this.mc.gameSettings.keyBindings) {
               if (keyBinding != this.keybinding && keyBinding.getKeyCode() == this.keybinding.getKeyCode()) {
                  bl2 = true;
                  break;
               }
            }
         }

         if (bl) {
            this.btnChangeKeyBinding.displayString = EnumChatFormatting.WHITE
               + "> "
               + EnumChatFormatting.YELLOW
               + this.btnChangeKeyBinding.displayString
               + EnumChatFormatting.WHITE
               + " <";
         } else if (bl2) {
            this.btnChangeKeyBinding.displayString = EnumChatFormatting.RED + this.btnChangeKeyBinding.displayString;
         }

         this.btnChangeKeyBinding.drawButton(GuiKeyBindingList.this.mc, mouseXIn, mouseYIn);
      }

      @Override
      public boolean mousePressed(int slotIndex, int i, int j, int k, int l, int m) {
         if (this.btnChangeKeyBinding.mousePressed(GuiKeyBindingList.this.mc, i, j)) {
            GuiKeyBindingList.this.field_148191_k.buttonId = this.keybinding;
            return true;
         } else if (this.btnReset.mousePressed(GuiKeyBindingList.this.mc, i, j)) {
            GuiKeyBindingList.this.mc.gameSettings.setOptionKeyBinding(this.keybinding, this.keybinding.getKeyCodeDefault());
            KeyBinding.resetKeyBindingArrayAndHash();
            return true;
         } else {
            return false;
         }
      }

      @Override
      public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
         this.btnChangeKeyBinding.mouseReleased(x, y);
         this.btnReset.mouseReleased(x, y);
      }

      @Override
      public void setSelected(int i, int j, int k) {
      }
   }
}
