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

   public LuaScreen(LuaTable luaTable) {
      this.K = luaTable;
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      LuaTable luaTable = LuaValue.tableOf();
      luaTable.set("mouse_x", mouseX);
      luaTable.set("mouse_y", mouseY);
      luaTable.set("partial_ticks", partialTicks);
      this.X("on_render_screen", luaTable);
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   @Override
   public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      LuaTable luaTable = LuaValue.tableOf();
      luaTable.set("mouse_button", mouseButton);
      luaTable.set("mouse_x", mouseX);
      luaTable.set("mouse_y", mouseY);
      this.X("on_click", luaTable);
      super.mouseClicked(mouseX, mouseY, mouseButton);
   }

   @Override
   public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
      LuaTable luaTable = LuaValue.tableOf();
      luaTable.set("mouse_button", clickedMouseButton);
      luaTable.set("mouse_x", mouseX);
      luaTable.set("mouse_y", mouseY);
      luaTable.set("time_since", timeSinceLastClick);
      this.X("on_drag_click", luaTable);
      super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
   }

   @Override
   public void mouseReleased(int mouseX, int mouseY, int state) {
      this.v("on_mouse_released");
      super.mouseReleased(mouseX, mouseY, state);
   }

   @Override
   public void keyTyped(char typedChar, int keyCode) throws IOException {
      LuaTable luaTable = LuaValue.tableOf();
      luaTable.set("typed_char", String.valueOf(typedChar));
      luaTable.set("key_code", keyCode);
      this.X("on_key_typed", luaTable);
      super.keyTyped(typedChar, keyCode);
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

   private LuaValue v(String string) {
      LuaValue luaValue = this.K.get(string);
      if (!luaValue.isnil()) {
         try {
            return luaValue.call();
         } catch (LuaError luaError) {
            luaError.printStackTrace();
            chat.f(luaError.getMessage());
         }
      }

      return null;
   }

   private LuaValue X(String string, LuaValue luaValue) {
      LuaValue luaValue2 = this.K.get(string);
      if (!luaValue2.isnil()) {
         try {
            return luaValue2.call(luaValue);
         } catch (LuaError luaError) {
            luaError.printStackTrace();
            chat.f(luaError.getMessage());
         }
      }

      return null;
   }
}
