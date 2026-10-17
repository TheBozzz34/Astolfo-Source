package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;

public class GuiOptionsRowList extends GuiListExtended {
   private final List<GuiOptionsRowList.Row> field_148184_k = Lists.newArrayList();

   public GuiOptionsRowList(Minecraft mcIn, int i, int j, int k, int l, int m, GameSettings.Options... optionss) {
      super(mcIn, i, j, k, l, m);
      this.field_148163_i = false;

      for (int n = 0; n < optionss.length; n += 2) {
         GameSettings.Options options = optionss[n];
         GameSettings.Options options2 = n < optionss.length - 1 ? optionss[n + 1] : null;
         GuiButton guiButton = this.func_148182_a(mcIn, i / 2 - 155, 0, options);
         GuiButton guiButton2 = this.func_148182_a(mcIn, i / 2 - 155 + 160, 0, options2);
         this.field_148184_k.add(new GuiOptionsRowList.Row(guiButton, guiButton2));
      }
   }

   private GuiButton func_148182_a(Minecraft mcIn, int i, int j, GameSettings.Options options) {
      if (options == null) {
         return null;
      }

      int k = options.returnEnumOrdinal();
      return options.getEnumFloat()
         ? new GuiOptionSlider(k, i, j, options)
         : new GuiOptionButton(k, i, j, options, mcIn.gameSettings.getKeyBinding(options));
   }

   public GuiOptionsRowList.Row getListEntry(int index) {
      return this.field_148184_k.get(index);
   }

   @Override
   public int getSize() {
      return this.field_148184_k.size();
   }

   @Override
   public int getListWidth() {
      return 400;
   }

   @Override
   public int getScrollBarX() {
      return super.getScrollBarX() + 32;
   }

   public static class Row implements GuiListExtended.IGuiListEntry {
      private final Minecraft field_148325_a = Minecraft.getMinecraft();
      private final GuiButton field_148323_b;
      private final GuiButton field_148324_c;

      public Row(GuiButton guiButton, GuiButton guiButton2) {
         this.field_148323_b = guiButton;
         this.field_148324_c = guiButton2;
      }

      @Override
      public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeightIn, int mouseXIn, int mouseYIn, boolean isSelected) {
         if (this.field_148323_b != null) {
            this.field_148323_b.yPosition = y;
            this.field_148323_b.drawButton(this.field_148325_a, mouseXIn, mouseYIn);
         }

         if (this.field_148324_c != null) {
            this.field_148324_c.yPosition = y;
            this.field_148324_c.drawButton(this.field_148325_a, mouseXIn, mouseYIn);
         }
      }

      @Override
      public boolean mousePressed(int slotIndex, int i, int j, int k, int l, int m) {
         if (this.field_148323_b.mousePressed(this.field_148325_a, i, j)) {
            if (this.field_148323_b instanceof GuiOptionButton) {
               this.field_148325_a.gameSettings.setOptionValue(((GuiOptionButton)this.field_148323_b).returnEnumOptions(), 1);
               this.field_148323_b.displayString = this.field_148325_a.gameSettings.getKeyBinding(GameSettings.Options.getEnumOptions(this.field_148323_b.id));
            }

            return true;
         } else if (this.field_148324_c != null && this.field_148324_c.mousePressed(this.field_148325_a, i, j)) {
            if (this.field_148324_c instanceof GuiOptionButton) {
               this.field_148325_a.gameSettings.setOptionValue(((GuiOptionButton)this.field_148324_c).returnEnumOptions(), 1);
               this.field_148324_c.displayString = this.field_148325_a.gameSettings.getKeyBinding(GameSettings.Options.getEnumOptions(this.field_148324_c.id));
            }

            return true;
         } else {
            return false;
         }
      }

      @Override
      public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
         if (this.field_148323_b != null) {
            this.field_148323_b.mouseReleased(x, y);
         }

         if (this.field_148324_c != null) {
            this.field_148324_c.mouseReleased(x, y);
         }
      }

      @Override
      public void setSelected(int i, int j, int k) {
      }
   }
}
