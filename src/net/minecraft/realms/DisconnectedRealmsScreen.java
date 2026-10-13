package net.minecraft.realms;

import java.util.List;
import net.minecraft.util.IChatComponent;

public class DisconnectedRealmsScreen extends RealmsScreen {
   private String title;
   private IChatComponent reason;
   private List<String> lines;
   private final RealmsScreen parent;
   private int textHeight;
   private static final long a = 118242549628469L;

   public DisconnectedRealmsScreen(RealmsScreen var1, String var2, IChatComponent var3) {
      this.parent = var1;
      this.title = getLocalizedString(var2);
      this.reason = var3;
   }

   @Override
   public void init() {
      Realms.setConnectedToRealms(false);
      this.u();
      this.lines = this.fontSplit(this.reason.getFormattedText(), this.t() - 50);
      this.textHeight = this.lines.size() * this.s();
      this.c(newButton(0, this.t() / 2 - 100, this.o() / 2 + this.textHeight / 2 + this.s(), getLocalizedString("gui.back")));
   }

   @Override
   public void keyPressed(char var1, int var2) {
      if (var2 == 1) {
         Realms.setScreen(this.parent);
      }
   }

   @Override
   public void buttonClicked(RealmsButton var1) {
      if (var1.id() == 0) {
         Realms.setScreen(this.parent);
      }
   }

   @Override
   public void render(int var1, int var2, float var3) {
      this.Q();
      this.D(this.title, this.t() / 2, this.o() / 2 - this.textHeight / 2 - this.s() * 2, 11184810);
      int var7 = this.o() / 2 - this.textHeight / 2;
      if (this.lines != null) {
         for (String var9 : this.lines) {
            this.D(var9, this.t() / 2, var7, 16777215);
            var7 += this.s();
         }
      }

      super.render(var1, var2, var3);
   }
}
