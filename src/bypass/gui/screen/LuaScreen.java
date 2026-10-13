package bypass.gui.screen;

import bypass.ClientAccess;
import java.io.IOException;
import net.minecraft.client.gui.GuiScreen;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

public class LuaScreen extends GuiScreen implements ClientAccess {
   private final LuaTable K;
   private static final long b = 96183474392458L;

   public LuaScreen(LuaTable var1) {
      this.K = var1;
   }

   @Override
   public void drawScreen(int var1, int var2, float var3) {
      LuaTable var7 = LuaValue.tableOf();
      var7.set("mouse_x", var1);
      var7.set("mouse_y", var2);
      var7.set("partial_ticks", var3);
      this.X("on_render_screen", var7);
      super.drawScreen(var1, var2, var3);
   }

   @Override
   public void mouseClicked(int var1, int var2, int var3) throws IOException {
      LuaTable var6 = LuaValue.tableOf();
      var6.set("mouse_button", var3);
      var6.set("mouse_x", var1);
      var6.set("mouse_y", var2);
      this.X("on_click", var6);
      super.mouseClicked(var1, var2, var3);
   }

   @Override
   public void mouseClickMove(int var1, int var2, int var3, long var4) {
      LuaTable var10 = LuaValue.tableOf();
      var10.set("mouse_button", var3);
      var10.set("mouse_x", var1);
      var10.set("mouse_y", var2);
      var10.set("time_since", var4);
      this.X("on_drag_click", var10);
      super.mouseClickMove(var1, var2, var3, var4);
   }

   @Override
   public void mouseReleased(int var1, int var2, int var3) {
      this.v("on_mouse_released");
      super.mouseReleased(var1, var2, var3);
   }

   @Override
   public void keyTyped(char var1, int var2) throws IOException {
      LuaTable var5 = LuaValue.tableOf();
      var5.set("typed_char", String.valueOf(var1));
      var5.set("key_code", var2);
      this.X("on_key_typed", var5);
      super.keyTyped(var1, var2);
   }

   @Override
   public void onGuiClosed() {
      this.v("on_gui_close");
      super.onGuiClosed();
   }

   @Override
   public boolean doesGuiPauseGame() {
      return false;
   }

   private LuaValue v(String var1) {
      LuaValue var5 = this.K.get(var1);
      if (!var5.isnil()) {
         try {
            return var5.call();
         } catch (LuaError var7) {
            var7.printStackTrace();
            chat.f(var7.getMessage());
         }
      }

      return null;
   }

   private LuaValue X(String var1, LuaValue var2) {
      LuaValue var6 = this.K.get(var1);
      if (!var6.isnil()) {
         try {
            return var6.call(var2);
         } catch (LuaError var8) {
            var8.printStackTrace();
            chat.f(var8.getMessage());
         }
      }

      return null;
   }
}
