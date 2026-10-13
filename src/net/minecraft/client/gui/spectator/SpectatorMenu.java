package net.minecraft.client.gui.spectator;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiSpectator;
import net.minecraft.client.gui.spectator.categories.SpectatorDetails;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class SpectatorMenu {
   private static final ISpectatorMenuObject field_178655_b = new SpectatorMenu.EndSpectatorObject();
   private static final ISpectatorMenuObject field_178656_c = new SpectatorMenu.MoveMenuObject(-1, true);
   private static final ISpectatorMenuObject field_178653_d = new SpectatorMenu.MoveMenuObject(1, true);
   private static final ISpectatorMenuObject field_178654_e = new SpectatorMenu.MoveMenuObject(1, false);
   public static final ISpectatorMenuObject field_178657_a = new ISpectatorMenuObject() {
      @Override
      public void func_178661_a(SpectatorMenu var1) {
      }

      @Override
      public IChatComponent getSpectatorName() {
         return new ChatComponentText("");
      }

      @Override
      public void func_178663_a(float var1, int var2) {
      }

      @Override
      public boolean func_178662_A_() {
         return false;
      }
   };
   private final ISpectatorMenuRecipient field_178651_f;
   private final List<SpectatorDetails> field_178652_g = Lists.newArrayList();
   private ISpectatorMenuView field_178659_h = new BaseSpectatorGroup();
   private int field_178660_i = -1;
   private int field_178658_j;
   private static final long a = 52534856511781L;

   public SpectatorMenu(ISpectatorMenuRecipient var1) {
      this.field_178651_f = var1;
   }

   public ISpectatorMenuObject func_178643_a(int var1) {
      int var5 = var1 + this.field_178658_j * 6;
      return this.field_178658_j > 0 && var1 == 0
         ? field_178656_c
         : (
            var1 == 7
               ? (var5 < this.field_178659_h.func_178669_a().size() ? field_178653_d : field_178654_e)
               : (
                  var1 == 8
                     ? field_178655_b
                     : (
                        var5 >= 0 && var5 < this.field_178659_h.func_178669_a().size()
                           ? Objects.firstNonNull(this.field_178659_h.func_178669_a().get(var5), field_178657_a)
                           : field_178657_a
                     )
               )
         );
   }

   public List<ISpectatorMenuObject> func_178642_a() {
      ArrayList var4 = Lists.newArrayList();

      for (int var5 = 0; var5 <= 8; var5++) {
         var4.add(this.func_178643_a(var5));
      }

      return var4;
   }

   public ISpectatorMenuObject func_178645_b() {
      return this.func_178643_a(this.field_178660_i);
   }

   public ISpectatorMenuView func_178650_c() {
      return this.field_178659_h;
   }

   public void func_178644_b(int var1) {
      ISpectatorMenuObject var5 = this.func_178643_a(var1);
      if (var5 != field_178657_a) {
         if (this.field_178660_i == var1 && var5.func_178662_A_()) {
            var5.func_178661_a(this);
         } else {
            this.field_178660_i = var1;
         }
      }
   }

   public void func_178641_d() {
      this.field_178651_f.func_175257_a(this);
   }

   public int func_178648_e() {
      return this.field_178660_i;
   }

   public void func_178647_a(ISpectatorMenuView var1) {
      this.field_178652_g.add(this.func_178646_f());
      this.field_178659_h = var1;
      this.field_178660_i = -1;
      this.field_178658_j = 0;
   }

   public SpectatorDetails func_178646_f() {
      return new SpectatorDetails(this.field_178659_h, this.func_178642_a(), this.field_178660_i);
   }

   public static class EndSpectatorObject implements ISpectatorMenuObject {
      private static final long a = 132235007661659L;

      public EndSpectatorObject() {
      }

      @Override
      public void func_178661_a(SpectatorMenu var1) {
         var1.func_178641_d();
      }

      @Override
      public IChatComponent getSpectatorName() {
         return new ChatComponentText("Close menu");
      }

      @Override
      public void func_178663_a(float var1, int var2) {
         Minecraft.getMinecraft().getTextureManager().bindTexture(GuiSpectator.field_175269_a);
         Gui.drawModalRectWithCustomSizedTexture(0, 0, 128.0F, 0.0F, 16, 16, 256.0F, 256.0F);
      }

      @Override
      public boolean func_178662_A_() {
         return true;
      }
   }

   public static class MoveMenuObject implements ISpectatorMenuObject {
      private final int field_178666_a;
      private final boolean field_178665_b;
      private static final long a = 6691103535830L;

      public MoveMenuObject(int var1, boolean var2) {
         this.field_178666_a = var1;
         this.field_178665_b = var2;
      }

      @Override
      public void func_178661_a(SpectatorMenu var1) {
         var1.field_178658_j = this.field_178666_a;
      }

      @Override
      public IChatComponent getSpectatorName() {
         return this.field_178666_a < 0 ? new ChatComponentText("Previous Page") : new ChatComponentText("Next Page");
      }

      @Override
      public void func_178663_a(float var1, int var2) {
         Minecraft.getMinecraft().getTextureManager().bindTexture(GuiSpectator.field_175269_a);
         if (this.field_178666_a < 0) {
            Gui.drawModalRectWithCustomSizedTexture(0, 0, 144.0F, 0.0F, 16, 16, 256.0F, 256.0F);
         } else {
            Gui.drawModalRectWithCustomSizedTexture(0, 0, 160.0F, 0.0F, 16, 16, 256.0F, 256.0F);
         }
      }

      @Override
      public boolean func_178662_A_() {
         return this.field_178665_b;
      }
   }
}
