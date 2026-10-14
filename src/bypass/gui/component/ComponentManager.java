package bypass.gui.component;

import bypass.gui.clickgui.defaultgui.ConfigsPanel;
import bypass.gui.clickgui.defaultgui.ScriptsPanel;
import bypass.manager.Manager;
import java.util.Arrays;

public abstract class ComponentManager extends Manager<Element> {
   public void F(int i, int j) {
      for (Element element : this.getList()) {
         if (element instanceof Component) {
            Component component = (Component)element;
            if (component.u_boolean()) {
               component.T(i, j);
            }
         }
      }
   }

   public void p(int i, int j) {
      for (Element element : this.getList()) {
         if (!(element instanceof ConfigsPanel) && !(element instanceof ScriptsPanel)) {
            element.render(i, j);
         }
      }
   }

   public void Q(boolean bl) {
      for (Element element : this.getList()) {
         if (element instanceof Component) {
            Component component = (Component)element;
            if (component.c_boolean()) {
               component.x(bl);
            }
         }
      }
   }

   public void addElements(Element... elements) {
      this.getList().addAll(Arrays.asList(elements));
   }
}
