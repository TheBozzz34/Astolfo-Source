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
   public LuaValue o;
   private int U = Color.WHITE.getRGB();
   private int key;
   private final ArrayList<Setting<?>> settings = new ArrayList<>();
   public Animation animation = new TimedAnimation().setDuration(250).setEasing(Easing.INERTIA);
   private final BooleanSetting hide = new BooleanSetting("Hide", "", true);
   private static final long bb = 63546949332023L;

   public Module(String var1, Category var2) {
      super(var1, var2);
      this.addSettings(this.hide);
   }

   public Module(Category var1) {
      super(null, var1);
      this.addSettings(this.hide);
   }

   @Override
   public void toggle() {
      this.setEnabled(!this.enabled);
   }

   public ArrayList<Setting<?>> getSettings() {
      return this.settings;
   }

   public void addSettings(Setting<?>... var1) {
      this.settings.addAll(Arrays.asList(var1));
   }

   @Override
   public void setEnabled(boolean var1) {
      boolean var5 = this.enabled;
      if (DisconnectHandler.disconnected && !(this instanceof GUI)) {
         if (this.enabled) {
            this.enabled = false;
            this.onDisable();
         }
      } else {
         if (this.enabled = var1) {
            if (var5 != var1) {
               this.onEnable();
            }
         } else if (var5 != var1) {
            this.onDisable();
         }
      }
   }

   public int f_int() {
      return this.U;
   }

   public void B(int var1) {
      this.U = var1;
   }

   public boolean isHidden() {
      return this.hide.getValue();
   }

   public void setHidden(boolean var1) {
      this.hide.set(var1);
   }

   @Override
   public int getKey() {
      return this.key;
   }

   @Override
   public void setKey(int var1) {
      this.key = var1;
   }

   @Override
   public boolean isEnabled() {
      return this.enabled;
   }

   public void p() {
   }

   public LuaValue callScript(String var1, LuaValue var2) {
      LuaValue var6 = this.script.get(var1);
      if (!var6.isnil()) {
         try {
            return var6.call(var2);
         } catch (LuaError var8) {
            if (HUD.isDevelopment) {
               var8.printStackTrace();
               chat.f(var8.getMessage());
            }
         }
      }

      return null;
   }

   public LuaValue callScript(String var1) {
      LuaValue var5 = this.script.get(var1);
      if (!var5.isnil()) {
         try {
            return var5.call();
         } catch (LuaError var7) {
            if (HUD.isDevelopment) {
               var7.printStackTrace();
               chat.f(var7.getMessage());
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
