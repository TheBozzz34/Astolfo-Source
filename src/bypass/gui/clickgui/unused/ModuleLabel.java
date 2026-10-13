package bypass.gui.clickgui.unused;

import bypass.gui.component.Container;
import bypass.module.Module;
import java.awt.Color;

public class ModuleLabel extends Container {
   private Module f;
   private static final long d = 59776296588272L;

   public ModuleLabel(Module var1, int var2, int var3, int var4, int var5) {
      super(var1.getName(), var2, var3, var4, var5);
      this.f = var1;
   }

   @Override
   public void l(int var1) {
   }

   @Override
   public void render(int var1, int var2) {
      client.getCustomFontRenderer().drawString(this.f.getName(), this.M(), this.E_float(), new Color(97, 95, 97).getRGB());
   }
}
