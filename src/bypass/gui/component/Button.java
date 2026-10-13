package bypass.gui.component;

import bypass.render.RenderUtil;
import java.awt.Color;
import net.minecraft.client.gui.GuiMainMenu;

public abstract class Button extends Component {
   private static final long c = 81323577334946L;

   public Button(String var1, float var2, float var3, int var4, int var5) {
      super(var1, var2, var3, var4, var5);
   }

   @Override
   public void l(int var1) {
      this.G();
   }

   public abstract void G();

   @Override
   public void render(int var1, int var2) {
      long var3 = c ^ 104425031287504L;
      long var5 = var3 ^ 8972929756414L;
      int var7 = client.getSidebarGui().Z();
      int var8 = client.getSidebarGui().T();
      int var9 = client.getSidebarGui().F();
      RenderUtil.drawRect(this.M() + 1.0F, this.E_float() + 1.0F, this.M() + this.x() - 2.0F, this.E_float() + this.Y() - 2.0F, var7);
      RenderUtil.drawRect(this.M() + 2.0F, this.E_float() + 2.0F, this.M() + this.x() - 3.0F, this.E_float() + this.Y() - 3.0F, var9);
      GuiMainMenu.c
         .drawString(
            this.J(), this.M() + (this.x() - GuiMainMenu.c.getStringWidth(this.J())) / 2 + 1.0F, this.E_float() + this.Y() / 2 - 3.0F, Color.WHITE.getRGB()
         );
   }
}
