package net.minecraft.client.gui;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IntHashMap;

public class GuiPageButtonList extends GuiListExtended {
   private final List<GuiPageButtonList.GuiEntry> field_178074_u = Lists.newArrayList();
   private final IntHashMap<Gui> field_178073_v = new IntHashMap<>();
   private final List<GuiTextField> field_178072_w = Lists.newArrayList();
   private final GuiPageButtonList.GuiListEntry[][] field_178078_x;
   private int field_178077_y;
   private GuiPageButtonList.GuiResponder field_178076_z;
   private Gui field_178075_A;

   public GuiPageButtonList(
      Minecraft mcIn,
      int widthIn,
      int heightIn,
      int topIn,
      int bottomIn,
      int slotHeightIn,
      GuiPageButtonList.GuiResponder guiResponder,
      GuiPageButtonList.GuiListEntry[]... guiListEntrys
   ) {
      super(mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
      this.field_178076_z = guiResponder;
      this.field_178078_x = guiListEntrys;
      this.field_148163_i = false;
      this.func_178069_s();
      this.func_178055_t();
   }

   private void func_178069_s() {
      for (GuiPageButtonList.GuiListEntry[] guiListEntrys : this.field_178078_x) {
         for (int i = 0; i < guiListEntrys.length; i += 2) {
            GuiPageButtonList.GuiListEntry guiListEntry = guiListEntrys[i];
            GuiPageButtonList.GuiListEntry guiListEntry2 = i < guiListEntrys.length - 1 ? guiListEntrys[i + 1] : null;
            Gui gui = this.func_178058_a(guiListEntry, 0, guiListEntry2 == null);
            Gui gui2 = this.func_178058_a(guiListEntry2, 160, guiListEntry == null);
            GuiPageButtonList.GuiEntry guiEntry = new GuiPageButtonList.GuiEntry(gui, gui2);
            this.field_178074_u.add(guiEntry);
            if (guiListEntry != null && gui != null) {
               this.field_178073_v.addKey(guiListEntry.func_178935_b(), gui);
               if (gui instanceof GuiTextField) {
                  this.field_178072_w.add((GuiTextField)gui);
               }
            }

            if (guiListEntry2 != null && gui2 != null) {
               this.field_178073_v.addKey(guiListEntry2.func_178935_b(), gui2);
               if (gui2 instanceof GuiTextField) {
                  this.field_178072_w.add((GuiTextField)gui2);
               }
            }
         }
      }
   }

   private void func_178055_t() {
      this.field_178074_u.clear();

      for (int i = 0; i < this.field_178078_x[this.field_178077_y].length; i += 2) {
         GuiPageButtonList.GuiListEntry guiListEntry = this.field_178078_x[this.field_178077_y][i];
         GuiPageButtonList.GuiListEntry guiListEntry2 = i < this.field_178078_x[this.field_178077_y].length - 1
            ? this.field_178078_x[this.field_178077_y][i + 1]
            : null;
         Gui gui = this.field_178073_v.lookup(guiListEntry.func_178935_b());
         Gui gui2 = guiListEntry2 != null ? this.field_178073_v.lookup(guiListEntry2.func_178935_b()) : null;
         GuiPageButtonList.GuiEntry guiEntry = new GuiPageButtonList.GuiEntry(gui, gui2);
         this.field_178074_u.add(guiEntry);
      }
   }

   public void func_181156_c(int i) {
      if (i != this.field_178077_y) {
         int j = this.field_178077_y;
         this.field_178077_y = i;
         this.func_178055_t();
         this.func_178060_e(j, i);
         this.amountScrolled = 0.0F;
      }
   }

   public int func_178059_e() {
      return this.field_178077_y;
   }

   public int func_178057_f() {
      return this.field_178078_x.length;
   }

   public Gui func_178056_g() {
      return this.field_178075_A;
   }

   public void func_178071_h() {
      if (this.field_178077_y > 0) {
         this.func_181156_c(this.field_178077_y - 1);
      }
   }

   public void func_178064_i() {
      if (this.field_178077_y < this.field_178078_x.length - 1) {
         this.func_181156_c(this.field_178077_y + 1);
      }
   }

   public Gui func_178061_c(int i) {
      return this.field_178073_v.lookup(i);
   }

   private void func_178060_e(int i, int j) {
      for (GuiPageButtonList.GuiListEntry guiListEntry : this.field_178078_x[i]) {
         if (guiListEntry != null) {
            this.func_178066_a(this.field_178073_v.lookup(guiListEntry.func_178935_b()), false);
         }
      }

      for (GuiPageButtonList.GuiListEntry guiListEntry2 : this.field_178078_x[j]) {
         if (guiListEntry2 != null) {
            this.func_178066_a(this.field_178073_v.lookup(guiListEntry2.func_178935_b()), true);
         }
      }
   }

   private void func_178066_a(Gui gui, boolean bl) {
      if (gui instanceof GuiButton) {
         ((GuiButton)gui).visible = bl;
      } else if (gui instanceof GuiTextField) {
         ((GuiTextField)gui).setVisible(bl);
      } else if (gui instanceof GuiLabel) {
         ((GuiLabel)gui).visible = bl;
      }
   }

   private Gui func_178058_a(GuiPageButtonList.GuiListEntry guiListEntry, int i, boolean bl) {
      return guiListEntry instanceof GuiPageButtonList.GuiSlideEntry
         ? this.func_178067_a(this.width / 2 - 155 + i, 0, (GuiPageButtonList.GuiSlideEntry)guiListEntry)
         : (
            guiListEntry instanceof GuiPageButtonList.GuiButtonEntry
               ? this.func_178065_a(this.width / 2 - 155 + i, 0, (GuiPageButtonList.GuiButtonEntry)guiListEntry)
               : (
                  guiListEntry instanceof GuiPageButtonList.EditBoxEntry
                     ? this.func_178068_a(this.width / 2 - 155 + i, 0, (GuiPageButtonList.EditBoxEntry)guiListEntry)
                     : (
                        guiListEntry instanceof GuiPageButtonList.GuiLabelEntry
                           ? this.func_178063_a(this.width / 2 - 155 + i, 0, (GuiPageButtonList.GuiLabelEntry)guiListEntry, bl)
                           : null
                     )
               )
         );
   }

   public void func_181155_a(boolean bl) {
      for (GuiPageButtonList.GuiEntry guiEntry : this.field_178074_u) {
         if (guiEntry.field_178029_b instanceof GuiButton) {
            ((GuiButton)guiEntry.field_178029_b).enabled = bl;
         }

         if (guiEntry.field_178030_c instanceof GuiButton) {
            ((GuiButton)guiEntry.field_178030_c).enabled = bl;
         }
      }
   }

   @Override
   public boolean mouseClicked(int mouseX, int mouseY, int mouseEvent) {
      boolean bl = super.mouseClicked(mouseX, mouseY, mouseEvent);
      int i = this.getSlotIndexFromScreenCoords(mouseX, mouseY);
      if (i >= 0) {
         GuiPageButtonList.GuiEntry guiEntry = this.getListEntry(i);
         if (this.field_178075_A != guiEntry.field_178028_d && this.field_178075_A != null && this.field_178075_A instanceof GuiTextField) {
            ((GuiTextField)this.field_178075_A).setFocused(false);
         }

         this.field_178075_A = guiEntry.field_178028_d;
      }

      return bl;
   }

   private GuiSlider func_178067_a(int i, int j, GuiPageButtonList.GuiSlideEntry guiSlideEntry) {
      GuiSlider guiSlider = new GuiSlider(
         this.field_178076_z,
         guiSlideEntry.func_178935_b(),
         i,
         j,
         guiSlideEntry.func_178936_c(),
         guiSlideEntry.func_178943_e(),
         guiSlideEntry.func_178944_f(),
         guiSlideEntry.func_178942_g(),
         guiSlideEntry.func_178945_a()
      );
      guiSlider.visible = guiSlideEntry.func_178934_d();
      return guiSlider;
   }

   private GuiListButton func_178065_a(int i, int j, GuiPageButtonList.GuiButtonEntry guiButtonEntry) {
      GuiListButton guiListButton = new GuiListButton(
         this.field_178076_z, guiButtonEntry.func_178935_b(), i, j, guiButtonEntry.func_178936_c(), guiButtonEntry.func_178940_a()
      );
      guiListButton.visible = guiButtonEntry.func_178934_d();
      return guiListButton;
   }

   private GuiTextField func_178068_a(int i, int j, GuiPageButtonList.EditBoxEntry editBoxEntry) {
      GuiTextField guiTextField = new GuiTextField(editBoxEntry.func_178935_b(), this.mc.fontRendererObj, i, j, 150, 20);
      guiTextField.setText(editBoxEntry.func_178936_c());
      guiTextField.func_175207_a(this.field_178076_z);
      guiTextField.setVisible(editBoxEntry.func_178934_d());
      guiTextField.setValidator(editBoxEntry.func_178950_a());
      return guiTextField;
   }

   private GuiLabel func_178063_a(int i, int j, GuiPageButtonList.GuiLabelEntry guiLabelEntry, boolean bl) {
      GuiLabel guiLabel;
      if (bl) {
         guiLabel = new GuiLabel(this.mc.fontRendererObj, guiLabelEntry.func_178935_b(), i, j, this.width - i * 2, 20, -1);
      } else {
         guiLabel = new GuiLabel(this.mc.fontRendererObj, guiLabelEntry.func_178935_b(), i, j, 150, 20, -1);
      }

      guiLabel.visible = guiLabelEntry.func_178934_d();
      guiLabel.func_175202_a(guiLabelEntry.func_178936_c());
      guiLabel.setCentered();
      return guiLabel;
   }

   public void func_178062_a(char c, int i) {
      if (this.field_178075_A instanceof GuiTextField) {
         GuiTextField guiTextField = (GuiTextField)this.field_178075_A;
         if (!GuiScreen.isKeyComboCtrlV(i)) {
            if (i == 15) {
               guiTextField.setFocused(false);
               int j = this.field_178072_w.indexOf(this.field_178075_A);
               if (GuiScreen.isShiftKeyDown()) {
                  if (j == 0) {
                     j = this.field_178072_w.size() - 1;
                  } else {
                     j--;
                  }
               } else if (j == this.field_178072_w.size() - 1) {
                  j = 0;
               } else {
                  j++;
               }

               this.field_178075_A = this.field_178072_w.get(j);
               guiTextField = (GuiTextField)this.field_178075_A;
               guiTextField.setFocused(true);
               int k = guiTextField.yPosition + this.slotHeight;
               int l = guiTextField.yPosition;
               if (k > this.bottom) {
                  this.amountScrolled = this.amountScrolled + (k - this.bottom);
               } else if (l < this.top) {
                  this.amountScrolled = l;
               }
            } else {
               guiTextField.textboxKeyTyped(c, i);
            }
         } else {
            String string = GuiScreen.getClipboardString();
            String[] strings = string.split(";");
            int m = this.field_178072_w.indexOf(this.field_178075_A);
            int n = m;

            for (String string2 : strings) {
               this.field_178072_w.get(n).setText(string2);
               if (n == this.field_178072_w.size() - 1) {
                  n = 0;
               } else {
                  n++;
               }

               if (n == m) {
                  break;
               }
            }
         }
      }
   }

   public GuiPageButtonList.GuiEntry getListEntry(int index) {
      return this.field_178074_u.get(index);
   }

   @Override
   public int getSize() {
      return this.field_178074_u.size();
   }

   @Override
   public int getListWidth() {
      return 400;
   }

   @Override
   public int getScrollBarX() {
      return super.getScrollBarX() + 32;
   }

   public static class EditBoxEntry extends GuiPageButtonList.GuiListEntry {
      private final Predicate<String> field_178951_a;

      public EditBoxEntry(int i, String string, boolean bl, Predicate<String> predicate) {
         super(i, string, bl);
         this.field_178951_a = Objects.firstNonNull(predicate, Predicates.alwaysTrue());
      }

      public Predicate<String> func_178950_a() {
         return this.field_178951_a;
      }
   }

   public static class GuiButtonEntry extends GuiPageButtonList.GuiListEntry {
      private final boolean field_178941_a;

      public GuiButtonEntry(int i, String string, boolean bl, boolean bl2) {
         super(i, string, bl);
         this.field_178941_a = bl2;
      }

      public boolean func_178940_a() {
         return this.field_178941_a;
      }
   }

   public static class GuiEntry implements GuiListExtended.IGuiListEntry {
      private final Minecraft field_178031_a = Minecraft.getMinecraft();
      private final Gui field_178029_b;
      private final Gui field_178030_c;
      private Gui field_178028_d;

      public GuiEntry(Gui gui, Gui gui2) {
         this.field_178029_b = gui;
         this.field_178030_c = gui2;
      }

      public Gui func_178022_a() {
         return this.field_178029_b;
      }

      public Gui func_178021_b() {
         return this.field_178030_c;
      }

      @Override
      public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeightIn, int mouseXIn, int mouseYIn, boolean isSelected) {
         this.func_178017_a(this.field_178029_b, y, mouseXIn, mouseYIn, false);
         this.func_178017_a(this.field_178030_c, y, mouseXIn, mouseYIn, false);
      }

      private void func_178017_a(Gui gui, int i, int j, int k, boolean bl) {
         if (gui != null) {
            if (gui instanceof GuiButton) {
               this.func_178024_a((GuiButton)gui, i, j, k, bl);
            } else if (gui instanceof GuiTextField) {
               this.func_178027_a((GuiTextField)gui, i, bl);
            } else if (gui instanceof GuiLabel) {
               this.func_178025_a((GuiLabel)gui, i, j, k, bl);
            }
         }
      }

      private void func_178024_a(GuiButton guiButton, int i, int j, int k, boolean bl) {
         guiButton.yPosition = i;
         if (!bl) {
            guiButton.drawButton(this.field_178031_a, j, k);
         }
      }

      private void func_178027_a(GuiTextField guiTextField, int i, boolean bl) {
         guiTextField.yPosition = i;
         if (!bl) {
            guiTextField.drawTextBox();
         }
      }

      private void func_178025_a(GuiLabel guiLabel, int i, int j, int k, boolean bl) {
         guiLabel.field_146174_h = i;
         if (!bl) {
            guiLabel.drawLabel(this.field_178031_a, j, k);
         }
      }

      @Override
      public void setSelected(int i, int j, int k) {
         this.func_178017_a(this.field_178029_b, k, 0, 0, true);
         this.func_178017_a(this.field_178030_c, k, 0, 0, true);
      }

      @Override
      public boolean mousePressed(int slotIndex, int i, int j, int k, int l, int m) {
         boolean bl = this.func_178026_a(this.field_178029_b, i, j, k);
         boolean bl2 = this.func_178026_a(this.field_178030_c, i, j, k);
         return bl || bl2;
      }

      private boolean func_178026_a(Gui gui, int i, int j, int k) {
         if (gui == null) {
            return false;
         }

         if (gui instanceof GuiButton) {
            return this.func_178023_a((GuiButton)gui, i, j, k);
         }

         if (gui instanceof GuiTextField) {
            this.func_178018_a((GuiTextField)gui, i, j, k);
         }

         return false;
      }

      private boolean func_178023_a(GuiButton guiButton, int i, int j, int k) {
         boolean bl = guiButton.mousePressed(this.field_178031_a, i, j);
         if (bl) {
            this.field_178028_d = guiButton;
         }

         return bl;
      }

      private void func_178018_a(GuiTextField guiTextField, int i, int j, int k) {
         guiTextField.mouseClicked(i, j, k);
         if (guiTextField.isFocused()) {
            this.field_178028_d = guiTextField;
         }
      }

      @Override
      public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
         this.func_178016_b(this.field_178029_b, x, y, mouseEvent);
         this.func_178016_b(this.field_178030_c, x, y, mouseEvent);
      }

      private void func_178016_b(Gui gui, int i, int j, int k) {
         if (gui != null && gui instanceof GuiButton) {
            this.func_178019_b((GuiButton)gui, i, j, k);
         }
      }

      private void func_178019_b(GuiButton guiButton, int i, int j, int k) {
         guiButton.mouseReleased(i, j);
      }
   }

   public static class GuiLabelEntry extends GuiPageButtonList.GuiListEntry {
      public GuiLabelEntry(int i, String string, boolean bl) {
         super(i, string, bl);
      }
   }

   public static class GuiListEntry {
      private final int field_178939_a;
      private final String field_178937_b;
      private final boolean field_178938_c;

      public GuiListEntry(int i, String string, boolean bl) {
         this.field_178939_a = i;
         this.field_178937_b = string;
         this.field_178938_c = bl;
      }

      public int func_178935_b() {
         return this.field_178939_a;
      }

      public String func_178936_c() {
         return this.field_178937_b;
      }

      public boolean func_178934_d() {
         return this.field_178938_c;
      }
   }

   public interface GuiResponder {
      void func_175321_a(int i, boolean bl);

      void onTick(int id, float value);

      void func_175319_a(int i, String string);
   }

   public static class GuiSlideEntry extends GuiPageButtonList.GuiListEntry {
      private final GuiSlider.FormatHelper field_178949_a;
      private final float field_178947_b;
      private final float field_178948_c;
      private final float field_178946_d;

      public GuiSlideEntry(int i, String string, boolean bl, GuiSlider.FormatHelper formatHelper, float f, float g, float h) {
         super(i, string, bl);
         this.field_178949_a = formatHelper;
         this.field_178947_b = f;
         this.field_178948_c = g;
         this.field_178946_d = h;
      }

      public GuiSlider.FormatHelper func_178945_a() {
         return this.field_178949_a;
      }

      public float func_178943_e() {
         return this.field_178947_b;
      }

      public float func_178944_f() {
         return this.field_178948_c;
      }

      public float func_178942_g() {
         return this.field_178946_d;
      }
   }
}
