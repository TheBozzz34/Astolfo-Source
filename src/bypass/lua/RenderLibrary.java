package bypass.lua;

import bypass.ClientAccess;
import bypass.lua.render.Circle;
import bypass.lua.render.DrawImage;
import bypass.lua.render.GetStringWidth;
import bypass.lua.render.Line;
import bypass.lua.render.LineWidth;
import bypass.lua.render.Player;
import bypass.lua.render.PlayerHead;
import bypass.lua.render.Rect;
import bypass.lua.render.Scale;
import bypass.lua.render.ShowGui;
import bypass.lua.render.StringFunction;
import bypass.lua.render.StringShadow;
import bypass.lua.render.WorldToScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

public class RenderLibrary extends TwoArgFunction implements ClientAccess {
   public static final OneArgFunction e = new ShowGui();
   public static final VarArgFunction Z = new StringFunction();
   public static final VarArgFunction C = new WorldToScreen();
   public static final VarArgFunction c = new StringShadow();
   public static final OneArgFunction o = new GetStringWidth();
   public static final OneArgFunction P = new Scale();
   public static final OneArgFunction r = new LineWidth();
   public static final VarArgFunction W = new Player();
   public static final VarArgFunction u = new PlayerHead();
   public static final VarArgFunction q = new Rect();
   public static final VarArgFunction f = new DrawImage();
   public static final VarArgFunction D = new Line();
   public static final VarArgFunction H = new Circle();
   private static final ResourceLocation E = new ResourceLocation("client/da.png");
   private static final long b = 140597798818334L;

   @Override
   public LuaValue call(LuaValue luaValue, LuaValue luaValue2) {
      LuaTable luaTable = LuaValue.tableOf();
      luaTable.set("rect", q);
      luaTable.set("line", D);
      luaTable.set("player_head", u);
      luaTable.set("circle", H);
      luaTable.set("player", W);
      luaTable.set("get_string_width", o);
      luaTable.set("string", Z);
      luaTable.set("show_gui", e);
      luaTable.set("scale", P);
      luaTable.set("line_width", r);
      luaTable.set("string_shadow", c);
      luaTable.set("world_to_screen", C);
      luaTable.set("draw_image", f);
      luaValue2.set("render", luaTable);
      return luaTable;
   }

   public static ResourceLocation m(String string, boolean bl) {
      String string2 = string.replaceAll("[^a-zA-Z0-9]", "");
      ResourceLocation resourceLocation = new ResourceLocation("script_img/" + string2);
      TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
      ITextureObject iTextureObject = textureManager.getTexture(resourceLocation);
      if (iTextureObject == null) {
         iTextureObject = new ThreadDownloadImageData(null, string, E, new ImageBufferDownload(true));
         textureManager.loadTexture(resourceLocation, iTextureObject);
      }

      return resourceLocation;
   }
}
