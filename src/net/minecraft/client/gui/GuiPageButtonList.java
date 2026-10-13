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
   private static final long c = 133067077315965L;

   public GuiPageButtonList(
      Minecraft var1, int var2, int var3, int var4, int var5, int var6, GuiPageButtonList.GuiResponder var7, GuiPageButtonList.GuiListEntry[]... var8
   ) {
      super(var1, var2, var3, var4, var5, var6);
      this.field_178076_z = var7;
      this.field_178078_x = var8;
      this.field_148163_i = false;
      this.func_178069_s();
      this.func_178055_t();
   }

   private void func_178069_s() {
      for (GuiPageButtonList.GuiListEntry[] var7 : this.field_178078_x) {
         for (int var8 = 0; var8 < var7.length; var8 += 2) {
            GuiPageButtonList.GuiListEntry var9 = var7[var8];
            GuiPageButtonList.GuiListEntry var10 = var8 < var7.length - 1 ? var7[var8 + 1] : null;
            Gui var11 = this.func_178058_a(var9, 0, var10 == null);
            Gui var12 = this.func_178058_a(var10, 160, var9 == null);
            GuiPageButtonList.GuiEntry var13 = new GuiPageButtonList.GuiEntry(var11, var12);
            this.field_178074_u.add(var13);
            if (var9 != null && var11 != null) {
               this.field_178073_v.addKey(var9.func_178935_b(), var11);
               if (var11 instanceof GuiTextField) {
                  this.field_178072_w.add((GuiTextField)var11);
               }
            }

            if (var10 != null && var12 != null) {
               this.field_178073_v.addKey(var10.func_178935_b(), var12);
               if (var12 instanceof GuiTextField) {
                  this.field_178072_w.add((GuiTextField)var12);
               }
            }
         }
      }
   }

   private void func_178055_t() {
      this.field_178074_u.clear();

      for (int var4 = 0; var4 < this.field_178078_x[this.field_178077_y].length; var4 += 2) {
         GuiPageButtonList.GuiListEntry var5 = this.field_178078_x[this.field_178077_y][var4];
         GuiPageButtonList.GuiListEntry var6 = var4 < this.field_178078_x[this.field_178077_y].length - 1
            ? this.field_178078_x[this.field_178077_y][var4 + 1]
            : null;
         Gui var7 = this.field_178073_v.lookup(var5.func_178935_b());
         Gui var8 = var6 != null ? this.field_178073_v.lookup(var6.func_178935_b()) : null;
         GuiPageButtonList.GuiEntry var9 = new GuiPageButtonList.GuiEntry(var7, var8);
         this.field_178074_u.add(var9);
      }
   }

   public void func_181156_c(int var1) {
      if (var1 != this.field_178077_y) {
         int var5 = this.field_178077_y;
         this.field_178077_y = var1;
         this.func_178055_t();
         this.func_178060_e(var5, var1);
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

   public void I() {
      if (this.field_178077_y > 0) {
         this.func_181156_c(this.field_178077_y - 1);
      }
   }

   public void H() {
      if (this.field_178077_y < this.field_178078_x.length - 1) {
         this.func_181156_c(this.field_178077_y + 1);
      }
   }

   public Gui func_178061_c(int var1) {
      return this.field_178073_v.lookup(var1);
   }

   private void func_178060_e(int var1, int var2) {
      for (GuiPageButtonList.GuiListEntry var9 : this.field_178078_x[var1]) {
         if (var9 != null) {
            this.func_178066_a(this.field_178073_v.lookup(var9.func_178935_b()), false);
         }
      }

      for (GuiPageButtonList.GuiListEntry var13 : this.field_178078_x[var2]) {
         if (var13 != null) {
            this.func_178066_a(this.field_178073_v.lookup(var13.func_178935_b()), true);
         }
      }
   }

   private void func_178066_a(Gui var1, boolean var2) {
      if (var1 instanceof GuiButton) {
         ((GuiButton)var1).visible = var2;
      } else if (var1 instanceof GuiTextField) {
         ((GuiTextField)var1).setVisible(var2);
      } else if (var1 instanceof GuiLabel) {
         ((GuiLabel)var1).visible = var2;
      }
   }

   private Gui func_178058_a(GuiPageButtonList.GuiListEntry var1, int var2, boolean var3) {
      return var1 instanceof GuiPageButtonList.GuiSlideEntry
         ? this.func_178067_a(this.width / 2 - 155 + var2, 0, (GuiPageButtonList.GuiSlideEntry)var1)
         : (
            var1 instanceof GuiPageButtonList.GuiButtonEntry
               ? this.func_178065_a(this.width / 2 - 155 + var2, 0, (GuiPageButtonList.GuiButtonEntry)var1)
               : (
                  var1 instanceof GuiPageButtonList.EditBoxEntry
                     ? this.func_178068_a(this.width / 2 - 155 + var2, 0, (GuiPageButtonList.EditBoxEntry)var1)
                     : (
                        var1 instanceof GuiPageButtonList.GuiLabelEntry
                           ? this.func_178063_a(this.width / 2 - 155 + var2, 0, (GuiPageButtonList.GuiLabelEntry)var1, var3)
                           : null
                     )
               )
         );
   }

   public void func_181155_a(boolean var1) {
      for (GuiPageButtonList.GuiEntry var6 : this.field_178074_u) {
         if (var6.field_178029_b instanceof GuiButton) {
            ((GuiButton)var6.field_178029_b).enabled = var1;
         }

         if (var6.field_178030_c instanceof GuiButton) {
            ((GuiButton)var6.field_178030_c).enabled = var1;
         }
      }
   }

   @Override
   public boolean mouseClicked(int var1, int var2, int var3) {
      boolean var7 = super.mouseClicked(var1, var2, var3);
      int var8 = this.getSlotIndexFromScreenCoords(var1, var2);
      if (var8 >= 0) {
         GuiPageButtonList.GuiEntry var9 = this.getListEntry(var8);
         if (this.field_178075_A != var9.field_178028_d && this.field_178075_A != null && this.field_178075_A instanceof GuiTextField) {
            ((GuiTextField)this.field_178075_A).setFocused(false);
         }

         this.field_178075_A = var9.field_178028_d;
      }

      return var7;
   }

   private GuiSlider func_178067_a(int var1, int var2, GuiPageButtonList.GuiSlideEntry var3) {
      GuiSlider var4 = new GuiSlider(
         this.field_178076_z,
         var3.func_178935_b(),
         var1,
         var2,
         var3.func_178936_c(),
         var3.func_178943_e(),
         var3.func_178944_f(),
         var3.func_178942_g(),
         var3.func_178945_a()
      );
      var4.visible = var3.func_178934_d();
      return var4;
   }

   private GuiListButton func_178065_a(int var1, int var2, GuiPageButtonList.GuiButtonEntry var3) {
      GuiListButton var4 = new GuiListButton(this.field_178076_z, var3.func_178935_b(), var1, var2, var3.func_178936_c(), var3.func_178940_a());
      var4.visible = var3.func_178934_d();
      return var4;
   }

   private GuiTextField func_178068_a(int var1, int var2, GuiPageButtonList.EditBoxEntry var3) {
      GuiTextField var6 = new GuiTextField(var3.func_178935_b(), this.mc.fontRendererObj, var1, var2, 150, 20);
      var6.setText(var3.func_178936_c());
      var6.func_175207_a(this.field_178076_z);
      var6.setVisible(var3.func_178934_d());
      var6.setValidator(var3.func_178950_a());
      return var6;
   }

   private GuiLabel func_178063_a(int var1, int var2, GuiPageButtonList.GuiLabelEntry var3, boolean var4) {
      GuiLabel var8;
      if (var4) {
         var8 = new GuiLabel(this.mc.fontRendererObj, var3.func_178935_b(), var1, var2, this.width - var1 * 2, 20, -1);
      } else {
         var8 = new GuiLabel(this.mc.fontRendererObj, var3.func_178935_b(), var1, var2, 150, 20, -1);
      }

      var8.visible = var3.func_178934_d();
      var8.func_175202_a(var3.func_178936_c());
      var8.setCentered();
      return var8;
   }

   public void func_178062_a(char var1, int var2) {
      if (this.field_178075_A instanceof GuiTextField) {
         GuiTextField var6 = (GuiTextField)this.field_178075_A;
         if (!GuiScreen.isKeyComboCtrlV(var2)) {
            if (var2 == 15) {
               var6.setFocused(false);
               int var7 = this.field_178072_w.indexOf(this.field_178075_A);
               if (GuiScreen.isShiftKeyDown()) {
                  if (var7 == 0) {
                     var7 = this.field_178072_w.size() - 1;
                  } else {
                     var7--;
                  }
               } else if (var7 == this.field_178072_w.size() - 1) {
                  var7 = 0;
               } else {
                  var7++;
               }

               this.field_178075_A = this.field_178072_w.get(var7);
               var6 = (GuiTextField)this.field_178075_A;
               var6.setFocused(true);
               int var8 = var6.yPosition + this.slotHeight;
               int var9 = var6.yPosition;
               if (var8 > this.bottom) {
                  this.amountScrolled = this.amountScrolled + (var8 - this.bottom);
               } else if (var9 < this.top) {
                  this.amountScrolled = var9;
               }
            } else {
               var6.textboxKeyTyped(var1, var2);
            }
         } else {
            String var17 = GuiScreen.getClipboardString();
            String[] var18 = var17.split(";");
            int var19 = this.field_178072_w.indexOf(this.field_178075_A);
            int var10 = var19;

            for (String var14 : var18) {
               this.field_178072_w.get(var10).setText(var14);
               if (var10 == this.field_178072_w.size() - 1) {
                  var10 = 0;
               } else {
                  var10++;
               }

               if (var10 == var19) {
                  break;
               }
            }
         }
      }
   }

   public GuiPageButtonList.GuiEntry getListEntry(int var1) {
      return this.field_178074_u.get(var1);
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

      public EditBoxEntry(int var1, String var2, boolean var3, Predicate<String> var4) {
         super(var1, var2, var3);
         this.field_178951_a = Objects.firstNonNull(var4, Predicates.alwaysTrue());
      }

      public Predicate<String> func_178950_a() {
         return this.field_178951_a;
      }
   }

   public static class GuiButtonEntry extends GuiPageButtonList.GuiListEntry {
      private final boolean field_178941_a;

      public GuiButtonEntry(int var1, String var2, boolean var3, boolean var4) {
         super(var1, var2, var3);
         this.field_178941_a = var4;
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
      private static final long a = 22572772591413L;

      public GuiEntry(Gui var1, Gui var2) {
         this.field_178029_b = var1;
         this.field_178030_c = var2;
      }

      public Gui X() {
         return this.field_178029_b;
      }

      public Gui Q() {
         return this.field_178030_c;
      }

      @Override
      public void drawEntry(int var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8) {
         this.func_178017_a(this.field_178029_b, var3, var6, var7, false);
         this.func_178017_a(this.field_178030_c, var3, var6, var7, false);
      }

      private void func_178017_a(Gui var1, int var2, int var3, int var4, boolean var5) {
         if (var1 != null) {
            if (var1 instanceof GuiButton) {
               this.func_178024_a((GuiButton)var1, var2, var3, var4, var5);
            } else if (var1 instanceof GuiTextField) {
               this.func_178027_a((GuiTextField)var1, var2, var5);
            } else if (var1 instanceof GuiLabel) {
               this.func_178025_a((GuiLabel)var1, var2, var3, var4, var5);
            }
         }
      }

      private void func_178024_a(GuiButton var1, int var2, int var3, int var4, boolean var5) {
         var1.yPosition = var2;
         if (!var5) {
            var1.drawButton(this.field_178031_a, var3, var4);
         }
      }

      private void func_178027_a(GuiTextField var1, int var2, boolean var3) {
         var1.yPosition = var2;
         if (!var3) {
            var1.drawTextBox();
         }
      }

      private void func_178025_a(GuiLabel var1, int var2, int var3, int var4, boolean var5) {
         var1.field_146174_h = var2;
         if (!var5) {
            var1.drawLabel(this.field_178031_a, var3, var4);
         }
      }

      @Override
      public void setSelected(int var1, int var2, int var3) {
         this.func_178017_a(this.field_178029_b, var3, 0, 0, true);
         this.func_178017_a(this.field_178030_c, var3, 0, 0, true);
      }

      @Override
      public boolean mousePressed(int var1, int var2, int var3, int var4, int var5, int var6) {
         boolean var10 = this.func_178026_a(this.field_178029_b, var2, var3, var4);
         boolean var11 = this.func_178026_a(this.field_178030_c, var2, var3, var4);
         return var10 || var11;
      }

      private boolean func_178026_a(Gui var1, int var2, int var3, int var4) {
         if (var1 == null) {
            return false;
         }

         if (var1 instanceof GuiButton) {
            return this.func_178023_a((GuiButton)var1, var2, var3, var4);
         }

         if (var1 instanceof GuiTextField) {
            this.func_178018_a((GuiTextField)var1, var2, var3, var4);
         }

         return false;
      }

      private boolean func_178023_a(GuiButton var1, int var2, int var3, int var4) {
         boolean var8 = var1.mousePressed(this.field_178031_a, var2, var3);
         if (var8) {
            this.field_178028_d = var1;
         }

         return var8;
      }

      private void func_178018_a(GuiTextField var1, int var2, int var3, int var4) {
         var1.mouseClicked(var2, var3, var4);
         if (var1.isFocused()) {
            this.field_178028_d = var1;
         }
      }

      @Override
      public void mouseReleased(int var1, int var2, int var3, int var4, int var5, int var6) {
         this.func_178016_b(this.field_178029_b, var2, var3, var4);
         this.func_178016_b(this.field_178030_c, var2, var3, var4);
      }

      private void func_178016_b(Gui var1, int var2, int var3, int var4) {
         if (var1 != null && var1 instanceof GuiButton) {
            this.func_178019_b((GuiButton)var1, var2, var3, var4);
         }
      }

      private void func_178019_b(GuiButton var1, int var2, int var3, int var4) {
         var1.mouseReleased(var2, var3);
      }
   }

   public static class GuiLabelEntry extends GuiPageButtonList.GuiListEntry {
      public GuiLabelEntry(int var1, String var2, boolean var3) {
         super(var1, var2, var3);
      }
   }

   public static class GuiListEntry {
      private final int field_178939_a;
      private final String field_178937_b;
      private final boolean field_178938_c;

      public GuiListEntry(int var1, String var2, boolean var3) {
         this.field_178939_a = var1;
         this.field_178937_b = var2;
         this.field_178938_c = var3;
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
      void func_175321_a(int var1, boolean var2);

      void onTick(int var1, float var2);

      void func_175319_a(int var1, String var2);
   }

   public static class GuiSlideEntry extends GuiPageButtonList.GuiListEntry {
      private final GuiSlider.FormatHelper field_178949_a;
      private final float field_178947_b;
      private final float field_178948_c;
      private final float field_178946_d;

      public GuiSlideEntry(int var1, String var2, boolean var3, GuiSlider.FormatHelper var4, float var5, float var6, float var7) {
         super(var1, var2, var3);
         this.field_178949_a = var4;
         this.field_178947_b = var5;
         this.field_178948_c = var6;
         this.field_178946_d = var7;
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
