package bypass.gui.component;

import bypass.render.RenderUtil;
import java.awt.Color;
import net.minecraft.client.gui.GuiMainMenu;

public abstract class Button extends Component {
   private static final long c = 81323577334946L;

   public Button(String string, float f, float g, int i, int j) {
      super(string, f, g, i, j);
   }

   @Override
   public void l(int i) {
      this.G();
   }

   public abstract void G();

   @Override
   public void render(int i, int j) {
      long l = c ^ 104425031287504L;
      long m = l ^ 8972929756414L;
      int k = client.getSidebarGui().Z();
      int n = client.getSidebarGui().T();
      int i2 = client.getSidebarGui().F();
      RenderUtil.drawRect(this.M() + 1.0F, this.E_float() + 1.0F, this.M() + this.x() - 2.0F, this.E_float() + this.Y() - 2.0F, k);
      RenderUtil.drawRect(this.M() + 2.0F, this.E_float() + 2.0F, this.M() + this.x() - 3.0F, this.E_float() + this.Y() - 3.0F, i2);
      GuiMainMenu.c
         .drawString(
            this.J(), this.M() + (this.x() - GuiMainMenu.c.getStringWidth(this.J())) / 2 + 1.0F, this.E_float() + this.Y() / 2 - 3.0F, Color.WHITE.getRGB()
         );
   }
}
