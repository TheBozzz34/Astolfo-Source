package net.minecraft.realms;

import java.util.List;
import net.minecraft.util.IChatComponent;

public class DisconnectedRealmsScreen extends RealmsScreen {
   private String title;
   private IChatComponent reason;
   private List<String> lines;
   private final RealmsScreen parent;
   private int textHeight;

   public DisconnectedRealmsScreen(RealmsScreen parentIn, String unlocalizedTitle, IChatComponent reasonIn) {
      this.parent = parentIn;
      this.title = getLocalizedString(unlocalizedTitle);
      this.reason = reasonIn;
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
   public void keyPressed(char c, int i) {
      if (i == 1) {
         Realms.setScreen(this.parent);
      }
   }

   @Override
   public void buttonClicked(RealmsButton realmsButton) {
      if (realmsButton.id() == 0) {
         Realms.setScreen(this.parent);
      }
   }

   @Override
   public void render(int i, int j, float f) {
      this.Q();
      this.D(this.title, this.t() / 2, this.o() / 2 - this.textHeight / 2 - this.s() * 2, 11184810);
      int k = this.o() / 2 - this.textHeight / 2;
      if (this.lines != null) {
         for (String string : this.lines) {
            this.D(string, this.t() / 2, k, 16777215);
            k += this.s();
         }
      }

      super.render(i, j, f);
   }
}
