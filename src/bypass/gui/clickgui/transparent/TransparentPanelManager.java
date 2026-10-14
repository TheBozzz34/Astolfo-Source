package bypass.gui.clickgui.transparent;

import bypass.Astolfo;
import bypass.gui.component.ComponentManager;
import bypass.gui.component.Element;
import bypass.manager.Loadable;
import com.google.gson.JsonObject;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TransparentPanelManager extends ComponentManager implements Loadable {
   @Override
   public void load() {
   }

   @Override
   public void a() {
   }

   public List<JsonObject> n() {
      ArrayList arrayList = new ArrayList();
      JsonObject jsonObject = new JsonObject();

      for (Element element : this.getList()) {
         if (element instanceof CategoryPanel) {
            CategoryPanel categoryPanel = (CategoryPanel)element;
            JsonObject jsonObject2 = new JsonObject();
            jsonObject2.addProperty("state", categoryPanel.s_boolean());
            jsonObject2.addProperty("x", categoryPanel.M());
            jsonObject2.addProperty("y", categoryPanel.E_float());
            jsonObject.add(categoryPanel.J(), jsonObject2);
         }
      }

      arrayList.add(jsonObject);
      return arrayList;
   }

   public String u() {
      return String.format("%s%s%s%s.json", Astolfo.INSTANCE.getDirectory().getAbsolutePath(), "", File.separator, "panels");
   }
}
