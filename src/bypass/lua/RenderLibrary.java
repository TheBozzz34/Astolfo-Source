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
   public LuaValue call(LuaValue var1, LuaValue var2) {
      LuaTable var7 = LuaValue.tableOf();
      var7.set("rect", q);
      var7.set("line", D);
      var7.set("player_head", u);
      var7.set("circle", H);
      var7.set("player", W);
      var7.set("get_string_width", o);
      var7.set("string", Z);
      var7.set("show_gui", e);
      var7.set("scale", P);
      var7.set("line_width", r);
      var7.set("string_shadow", c);
      var7.set("world_to_screen", C);
      var7.set("draw_image", f);
      var2.set("render", var7);
      return var7;
   }

   public static ResourceLocation m(String var0, boolean var1) {
      String var4 = var0.replaceAll("[^a-zA-Z0-9]", "");
      ResourceLocation var5 = new ResourceLocation("script_img/" + var4);
      TextureManager var6 = Minecraft.getMinecraft().getTextureManager();
      ITextureObject var7 = var6.getTexture(var5);
      if (var7 == null) {
         var7 = new ThreadDownloadImageData(null, var0, E, new ImageBufferDownload(true));
         var6.loadTexture(var5, var7);
      }

      return var5;
   }
}
