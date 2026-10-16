package bypass.module;

import bypass.ClientAccess;
import bypass.auth.DisconnectHandler;
import bypass.gui.animation.Animation;
import bypass.gui.animation.Easing;
import bypass.gui.animation.TimedAnimation;
import bypass.module.hidden.GUI;
import bypass.module.visuals.HUD;
import bypass.setting.BooleanSetting;
import bypass.setting.Setting;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;

public class Module extends AbstractModule implements Toggleable, ClientAccess {
   private boolean enabled;
   public LuaValue script;
   public LuaValue registeredScriptTable;
   private int moduleColor = Color.WHITE.getRGB();
   private int key;
   private final ArrayList<Setting<?>> settings = new ArrayList<>();
   public Animation animation = new TimedAnimation().setDuration(250).setEasing(Easing.INERTIA);
   private final BooleanSetting hide = new BooleanSetting("Hide", "", true);

   public Module(String string, Category category) {
      super(string, category);
      this.addSettings(this.hide);
   }

   public Module(Category category) {
      super(null, category);
      this.addSettings(this.hide);
   }

   @Override
   public void toggle() {
      this.setEnabled(!this.enabled);
   }

   public ArrayList<Setting<?>> getSettings() {
      return this.settings;
   }

   public void addSettings(Setting<?>... settings) {
      this.settings.addAll(Arrays.asList(settings));
   }

   @Override
   public void setEnabled(boolean bl) {
      boolean bl2 = this.enabled;
      if (DisconnectHandler.disconnected && !(this instanceof GUI)) {
         if (this.enabled) {
            this.enabled = false;
            this.onDisable();
         }
      } else {
         if (this.enabled = bl) {
            if (bl2 != bl) {
               this.onEnable();
            }
         } else if (bl2 != bl) {
            this.onDisable();
         }
      }
   }

   public int getModuleColor() {
      return this.moduleColor;
   }

   public void setModuleColor(int i) {
      this.moduleColor = i;
   }

   public boolean isHidden() {
      return this.hide.getValue();
   }

   public void setHidden(boolean bl) {
      this.hide.set(bl);
   }

   @Override
   public int getKey() {
      return this.key;
   }

   @Override
   public void setKey(int i) {
      this.key = i;
   }

   @Override
   public boolean isEnabled() {
      return this.enabled;
   }

   public void updateDisplayName() {
   }

   public LuaValue callScript(String string, LuaValue luaValue) {
      LuaValue luaValue2 = this.script.get(string);
      if (!luaValue2.isnil()) {
         try {
            return luaValue2.call(luaValue);
         } catch (LuaError luaError) {
            if (HUD.isDevelopment) {
               luaError.printStackTrace();
               chat.printError(luaError.getMessage());
            }
         }
      }

      return null;
   }

   public LuaValue callScript(String string) {
      LuaValue luaValue = this.script.get(string);
      if (!luaValue.isnil()) {
         try {
            return luaValue.call();
         } catch (LuaError luaError) {
            if (HUD.isDevelopment) {
               luaError.printStackTrace();
               chat.printError(luaError.getMessage());
            }
         }
      }

      return null;
   }

   @Override
   public void onEnable() {
      if (!DisconnectHandler.disconnected) {
         client.getEventBus().subscribe(this);
      }

      this.animation.forwards().start();
   }

   @Override
   public void onDisable() {
      client.getEventBus().unsubscribe(this);
      this.animation.backwards().start();
   }
}
