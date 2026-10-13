package bypass.gui.component;

import bypass.gui.clickgui.defaultgui.ConfigsPanel;
import bypass.gui.clickgui.defaultgui.ScriptsPanel;
import bypass.manager.Manager;
import java.util.Arrays;

public abstract class ComponentManager extends Manager<Element> {
   private static final long a = 116201125173732L;

   public void F(int var1, int var2) {
      for (Element var7 : this.getList()) {
         if (var7 instanceof Component) {
            Component var8 = (Component)var7;
            if (var8.u_boolean()) {
               var8.T(var1, var2);
            }
         }
      }
   }

   public void p(int var1, int var2) {
      for (Element var7 : this.getList()) {
         if (!(var7 instanceof ConfigsPanel) && !(var7 instanceof ScriptsPanel)) {
            var7.render(var1, var2);
         }
      }
   }

   public void Q(boolean var1) {
      for (Element var7 : this.getList()) {
         if (var7 instanceof Component) {
            Component var8 = (Component)var7;
            if (var8.c_boolean()) {
               var8.x(var1);
            }
         }
      }
   }

   public void addElements(Element... var1) {
      this.getList().addAll(Arrays.asList(var1));
   }
}
