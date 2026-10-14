package bypass.gui.clickgui.unused;

import bypass.gui.component.Container;
import bypass.module.Module;
import java.awt.Color;

public class ModuleLabel extends Container {
   private Module f;
   private static final long d = 59776296588272L;

   public ModuleLabel(Module module, int i, int j, int k, int l) {
      super(module.getName(), i, j, k, l);
      this.f = module;
   }

   @Override
   public void l(int i) {
   }

   @Override
   public void render(int i, int j) {
      client.getCustomFontRenderer().drawString(this.f.getName(), this.M(), this.E_float(), new Color(97, 95, 97).getRGB());
   }
}
