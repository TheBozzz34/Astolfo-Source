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
   private static final List<Waypoint> waypoints = new ArrayList<>();
   private final NumberSetting<Float> scale = new NumberSetting<>("Scale", "Scale of waypoints", 0.5F, 0.1F, 3.0F);
   @EventHandler(value = RenderEvent.class, priority = EventPriority.LOWEST)
   private Listener<RenderEvent> onRender = renderEvent -> {
      if (renderEvent.getType() == RenderType.WORLD) {
         RenderManager renderManager = mc.getRenderManager();
         waypoints.forEach(waypoint -> {
            String string = mc.getCurrentServerData() == null ? "SinglePlayer" : mc.getCurrentServerData().serverIP;
            if (mc.thePlayer.dimension == Waypoint.dimensionOf(waypoint) && string.equals(waypoint.getServer())) {
               this.renderWaypoint(mc, waypoint, waypoint.getX() - renderManager.viewerPosX, waypoint.getY() - renderManager.viewerPosY, waypoint.getZ() - renderManager.viewerPosZ);
            }
         });
      }
   };
   public static final boolean ASSERTIONS_DISABLED = !Waypoints.class.desiredAssertionStatus();

   public Waypoints(Category category) {
      super(category);
      this.setHidden(false);
      this.addSettings(this.scale);
   }

   private void renderWaypoint(Minecraft minecraft, Waypoint waypoint, double d, double e, double d2) {
      double d3 = minecraft.thePlayer.getDistance(waypoint.getX(), waypoint.getY(), waypoint.getZ());
      double d4 = minecraft.gameSettings.renderDistanceChunks * 12.8;
      double d5 = Math.sqrt(d * d + d2 * d2 + e * e);
      if (d5 > d4) {
         double d6 = d4 / d5;
         d3 *= d6;
         d *= d6;
         e *= d6;
         d2 *= d6;
      }

      double d7 = d3 / 3.0 <= 2.0 ? 2.0 : d3 / 3.0;
      double d8 = 0.016666668 * d7;
      double d9 = 2.0 + d7 / 6.0 + 0.325F;
      GlStateManager.pushMatrix();
      RenderHelper.enableStandardItemLighting();
      GlStateManager.translate(d, e + d9, d2);
      GL11.glScalef(this.scale.getValue(), this.scale.getValue(), this.scale.getValue());
      GL11.glNormal3f(0.0F, 1.0F, 0.0F);
      if (minecraft.gameSettings.thirdPersonView == 2) {
         GlStateManager.rotate(-minecraft.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(minecraft.getRenderManager().playerViewX, -1.0F, 0.0F, 0.0F);
      } else {
         GlStateManager.rotate(-minecraft.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(minecraft.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
      }

      GlStateManager.scale(-d8, -d8, d8);
      GlStateManager.disableLighting();
      GlStateManager.enableDepth();
      GlStateManager.depthMask(false);
      GlStateManager.disableDepth();
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      String string = "§7" + Math.round(minecraft.thePlayer.getDistance(waypoint.getX(), waypoint.getY(), waypoint.getZ())) + "§f";
      Gui.drawRect(
         -(minecraft.fontRendererObj.getStringWidth(waypoint.getName() + " [" + string + "]") / 2) - 2,
         -2,
         minecraft.fontRendererObj.getStringWidth(waypoint.getName() + " [" + string + "]") / 2 + 2,
         9,
         new Color(0, 0, 0, 50).getRGB()
      );
      minecraft.fontRendererObj
         .drawStringWithShadow(
            waypoint.getName() + " [" + string + "]",
            -(minecraft.fontRendererObj.getStringWidth(waypoint.getName() + " [" + string + "]") / 2.0F) + 0.5F,
            -0.5F,
            -1
         );
      GlStateManager.enableDepth();
      GlStateManager.depthMask(true);
      GlStateManager.enableLighting();
      GlStateManager.disableBlend();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      RenderHelper.disableStandardItemLighting();
      GlStateManager.popMatrix();
   }

   public static void addWaypoint(String string, double d, double e, double d2) {
      waypoints.add(
         new Waypoint(string, mc.getCurrentServerData() == null ? "SinglePlayer" : mc.getCurrentServerData().serverIP, mc.thePlayer.dimension, d, e, d2)
      );
   }

   public static void save() {
      JsonObject jsonObject = new JsonObject();
      waypoints.forEach(waypoint -> {
         JsonObject jsonObject2 = new JsonObject();
         jsonObject2.addProperty("server", waypoint.getServer());
         jsonObject2.addProperty("dimension", waypoint.getDimension());
         jsonObject2.addProperty("x", waypoint.getX());
         jsonObject2.addProperty("y", waypoint.getY());
         jsonObject2.addProperty("z", waypoint.getZ());
         jsonObject.add(waypoint.getName(), jsonObject2);
      });
      Gson gson = new GsonBuilder().setPrettyPrinting().create();

      try {
         FileWriter fileWriter = new FileWriter(String.format("%s/%s.json", client.getDirectory().getAbsolutePath(), "waypointed"));
         gson.toJson(jsonObject, fileWriter);
         fileWriter.close();
      } catch (IOException ioException) {
         ioException.printStackTrace();
      }
   }

   public static void load() {
      try {
         File[] files = client.getDirectory().getAbsoluteFile().listFiles((file, string) -> string.equals("waypointed.json"));
         assert files != null;
         if (files.length == 0) {
            return;
         }

         JsonReader jsonReader = new JsonReader(new FileReader(files[0]));
         Gson gson = new Gson();
         Type type = new WaypointListType().getType();

         for (Object object : (List)gson.fromJson(jsonReader, type)) {
            JsonObject jsonObject = (JsonObject)object;
            jsonObject.getAsJsonObject("points");
            jsonObject.entrySet()
               .forEach(
                  entry -> {
                     JsonObject jsonObject2 = entry.getValue().getAsJsonObject();
                     waypoints.add(
                        new Waypoint(
                           entry.getKey(),
                           jsonObject2.get("server").getAsString(),
                           jsonObject2.get("dimension").getAsInt(),
                           jsonObject2.get("x").getAsDouble(),
                           jsonObject2.get("y").getAsDouble(),
                           jsonObject2.get("z").getAsDouble()
                        )
                     );
                  }
               );
         }
      } catch (FileNotFoundException fileNotFoundException) {
         fileNotFoundException.printStackTrace();
      }
   }

   public static void removeWaypoint(String string) {
      if (findWaypoint(string).isPresent()) {
         waypoints.remove(findWaypoint(string).get());
      }
   }

   public static Optional<Waypoint> findWaypoint(String string) {
      for (Waypoint waypoint : waypoints) {
         if (waypoint.getName().equalsIgnoreCase(string)) {
            return Optional.of(waypoint);
         }
      }

      return Optional.empty();
   }
}
