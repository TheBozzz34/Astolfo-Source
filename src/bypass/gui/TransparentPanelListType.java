package bypass.gui;

import bypass.gui.clickgui.transparent.TransparentPanelManager;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;

public class TransparentPanelListType extends TypeToken<ArrayList<JsonObject>> {
   public final TransparentPanelManager d;

   public TransparentPanelListType(TransparentPanelManager transparentPanelManager) {
      this.d = transparentPanelManager;
   }
}
