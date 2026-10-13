package bypass.module.visuals;

import bypass.event.EventHandler;
import bypass.event.EventPriority;
import bypass.event.Listener;
import bypass.event.render.RenderEvent;
import bypass.event.render.RenderType;
import bypass.module.Category;
import bypass.module.Module;
import bypass.setting.NumberSetting;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import org.lwjgl.opengl.GL11;

public final class Waypoints extends Module {
   private static final List<Waypoint> h = new ArrayList<>();
   private final NumberSetting<Float> scale = new NumberSetting<>("Scale", "Scale of waypoints", 0.5F, 0.1F, 3.0F);
   @EventHandler(value = RenderEvent.class, priority = EventPriority.LOWEST)
   private Listener<RenderEvent> onRender = var1x -> {
      if (var1x.getType() == RenderType.WORLD) {
         RenderManager var4 = mc.getRenderManager();
         h.forEach(var2 -> {
            String var6 = mc.getCurrentServerData() == null ? "SinglePlayer" : mc.getCurrentServerData().serverIP;
            if (mc.thePlayer.dimension == Waypoint.L(var2) && var6.equals(var2.z())) {
               this.o(mc, var2, var2.c() - var4.viewerPosX, var2.I() - var4.viewerPosY, var2.K() - var4.viewerPosZ);
            }
         });
      }
   };
   public static final boolean W = !Waypoints.class.desiredAssertionStatus();
   private static final long c = 98607734995140L;

   public Waypoints(Category var1) {
      super(var1);
      this.setHidden(false);
      this.addSettings(this.scale);
   }

   private void o(Minecraft var1, Waypoint var2, double var3, double var5, double var7) {
      long var9 = c ^ 94832676877468L;
      double var12 = var1.thePlayer.getDistance(var2.c(), var2.I(), var2.K());
      double var14 = var1.gameSettings.renderDistanceChunks * 12.8;
      double var16 = Math.sqrt(var3 * var3 + var7 * var7 + var5 * var5);
      if (var16 > var14) {
         double var18 = var14 / var16;
         var12 *= var18;
         var3 *= var18;
         var5 *= var18;
         var7 *= var18;
      }

      double var20 = var12 / 3.0 <= 2.0 ? 2.0 : var12 / 3.0;
      double var22 = 0.016666668 * var20;
      double var24 = 2.0 + var20 / 6.0 + 0.325F;
      GlStateManager.pushMatrix();
      RenderHelper.enableStandardItemLighting();
      GlStateManager.translate(var3, var5 + var24, var7);
      GL11.glScalef(this.scale.getValue(), this.scale.getValue(), this.scale.getValue());
      GL11.glNormal3f(0.0F, 1.0F, 0.0F);
      if (var1.gameSettings.thirdPersonView == 2) {
         GlStateManager.rotate(-var1.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(var1.getRenderManager().playerViewX, -1.0F, 0.0F, 0.0F);
      } else {
         GlStateManager.rotate(-var1.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(var1.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
      }

      GlStateManager.scale(-var22, -var22, var22);
      GlStateManager.disableLighting();
      long var10000 = 9013024031471692415L ^ var9;
      GlStateManager.enableDepth();
      GlStateManager.depthMask(false);
      GlStateManager.disableDepth();
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      String var26 = "§7" + Math.round(var1.thePlayer.getDistance(var2.c(), var2.I(), var2.K())) + "§f";
      Gui.drawRect(
         -(var1.fontRendererObj.getStringWidth(var2.r() + " [" + var26 + "]") / 2) - 2,
         -2,
         var1.fontRendererObj.getStringWidth(var2.r() + " [" + var26 + "]") / 2 + 2,
         9,
         new Color(0, 0, 0, 50).getRGB()
      );
      var1.fontRendererObj
         .drawStringWithShadow(var2.r() + " [" + var26 + "]", -(var1.fontRendererObj.getStringWidth(var2.r() + " [" + var26 + "]") / 2.0F) + 0.5F, -0.5F, -1);
      GlStateManager.enableDepth();
      GlStateManager.depthMask(true);
      GlStateManager.enableLighting();
      GlStateManager.disableBlend();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      RenderHelper.disableStandardItemLighting();
      GlStateManager.popMatrix();
   }

   public static void X(String var0, double var1, double var3, double var5) {
      h.add(
         new Waypoint(var0, mc.getCurrentServerData() == null ? "SinglePlayer" : mc.getCurrentServerData().serverIP, mc.thePlayer.dimension, var1, var3, var5)
      );
   }

   public static void save() {
      JsonObject var2 = new JsonObject();
      h.forEach(var1 -> {
         JsonObject var4x = new JsonObject();
         var4x.addProperty("server", var1.z());
         var4x.addProperty("dimension", var1.k());
         var4x.addProperty("x", var1.c());
         var4x.addProperty("y", var1.I());
         var4x.addProperty("z", var1.K());
         var2.add(var1.r(), var4x);
      });
      Gson var3 = new GsonBuilder().setPrettyPrinting().create();

      try {
         FileWriter var4 = new FileWriter(String.format("%s/%s.json", client.getDirectory().getAbsolutePath(), "waypointed"));
         var3.toJson(var2, var4);
         var4.close();
      } catch (IOException var6) {
         var6.printStackTrace();
      }
   }

   public static void load() {
      try {
         File[] var4 = client.getDirectory().getAbsoluteFile().listFiles((var0, var1) -> var1.equals("waypointed.json"));
         assert var4 != null;
         if (var4.length == 0) {
            return;
         }

         JsonReader var3 = new JsonReader(new FileReader(var4[0]));
         Gson var5 = new Gson();
         Type var6 = new WaypointListType().getType();

         for (Object var9$raw : (List)var5.fromJson(var3, var6)) {
            JsonObject var9 = (JsonObject)var9$raw;
            var9.getAsJsonObject("points");
            var9.entrySet()
               .forEach(
                  var0 -> {
                     JsonObject var3x = var0.getValue().getAsJsonObject();
                     h.add(
                        new Waypoint(
                           var0.getKey(),
                           var3x.get("server").getAsString(),
                           var3x.get("dimension").getAsInt(),
                           var3x.get("x").getAsDouble(),
                           var3x.get("y").getAsDouble(),
                           var3x.get("z").getAsDouble()
                        )
                     );
                  }
               );
         }
      } catch (FileNotFoundException var10) {
         var10.printStackTrace();
      }
   }

   public static void G(String var0) {
      if (U(var0).isPresent()) {
         h.remove(U(var0).get());
      }
   }

   public static Optional<Waypoint> U(String var0) {
      for (Waypoint var5 : h) {
         if (var5.r().equalsIgnoreCase(var0)) {
            return Optional.of(var5);
         }
      }

      return Optional.empty();
   }
}
