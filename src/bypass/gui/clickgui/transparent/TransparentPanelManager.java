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
   private static final long b = 101563399775410L;

   @Override
   public void load() {
   }

   @Override
   public void a() {
   }

   public List<JsonObject> n() {
      ArrayList var5 = new ArrayList();
      JsonObject var6 = new JsonObject();

      for (Element var8 : this.getList()) {
         if (var8 instanceof CategoryPanel) {
            CategoryPanel var9 = (CategoryPanel)var8;
            JsonObject var10 = new JsonObject();
            var10.addProperty("state", var9.s_boolean());
            var10.addProperty("x", var9.M());
            var10.addProperty("y", var9.E_float());
            var6.add(var9.J(), var10);
         }
      }

      var5.add(var6);
      return var5;
   }

   public String u() {
      return String.format("%s%s%s%s.json", Astolfo.INSTANCE.getDirectory().getAbsolutePath(), "", File.separator, "panels");
   }
}
