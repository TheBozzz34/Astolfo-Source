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
   public static final OneArgFunction showGuiFunction = new ShowGui();
   public static final VarArgFunction stringFunction = new StringFunction();
   public static final VarArgFunction worldToScreenFunction = new WorldToScreen();
   public static final VarArgFunction stringShadowFunction = new StringShadow();
   public static final OneArgFunction getStringWidthFunction = new GetStringWidth();
   public static final OneArgFunction scaleFunction = new Scale();
   public static final OneArgFunction lineWidthFunction = new LineWidth();
   public static final VarArgFunction playerFunction = new Player();
   public static final VarArgFunction playerHeadFunction = new PlayerHead();
   public static final VarArgFunction rectFunction = new Rect();
   public static final VarArgFunction drawImageFunction = new DrawImage();
   public static final VarArgFunction lineFunction = new Line();
   public static final VarArgFunction circleFunction = new Circle();
   private static final ResourceLocation FALLBACK_IMAGE_TEXTURE = new ResourceLocation("client/da.png");

   @Override
   public LuaValue call(LuaValue luaValue, LuaValue luaValue2) {
      LuaTable luaTable = LuaValue.tableOf();
      luaTable.set("rect", rectFunction);
      luaTable.set("line", lineFunction);
      luaTable.set("player_head", playerHeadFunction);
      luaTable.set("circle", circleFunction);
      luaTable.set("player", playerFunction);
      luaTable.set("get_string_width", getStringWidthFunction);
      luaTable.set("string", stringFunction);
      luaTable.set("show_gui", showGuiFunction);
      luaTable.set("scale", scaleFunction);
      luaTable.set("line_width", lineWidthFunction);
      luaTable.set("string_shadow", stringShadowFunction);
      luaTable.set("world_to_screen", worldToScreenFunction);
      luaTable.set("draw_image", drawImageFunction);
      luaValue2.set("render", luaTable);
      return luaTable;
   }

   public static ResourceLocation getOrDownloadImageTexture(String string, boolean bl) {
      String string2 = string.replaceAll("[^a-zA-Z0-9]", "");
      ResourceLocation resourceLocation = new ResourceLocation("script_img/" + string2);
      TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
      ITextureObject iTextureObject = textureManager.getTexture(resourceLocation);
      if (iTextureObject == null) {
         iTextureObject = new ThreadDownloadImageData(null, string, FALLBACK_IMAGE_TEXTURE, new ImageBufferDownload(true));
         textureManager.loadTexture(resourceLocation, iTextureObject);
      }

      return resourceLocation;
   }
}
