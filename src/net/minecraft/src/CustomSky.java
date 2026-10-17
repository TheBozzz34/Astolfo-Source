package net.minecraft.src;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class CustomSky {
   private static CustomSkyLayer[][] worldSkyLayers = (CustomSkyLayer[][])null;

   public static void reset() {
      worldSkyLayers = (CustomSkyLayer[][])null;
   }

   public static void update() {
      reset();
      if (Config.isCustomSky()) {
         worldSkyLayers = readCustomSkies();
      }
   }

   private static CustomSkyLayer[][] readCustomSkies() {
      CustomSkyLayer[][] customSkyLayerss = new CustomSkyLayer[10][0];
      String string = "mcpatcher/sky/world";
      int i = -1;

      for (int j = 0; j < customSkyLayerss.length; j++) {
         String string2 = string + j + "/sky";
         ArrayList arrayList = new ArrayList();

         for (int k = 1; k < 1000; k++) {
            String string3 = string2 + k + ".properties";

            try {
               ResourceLocation resourceLocation = new ResourceLocation(string3);
               InputStream inputStream = Config.getResourceStream(resourceLocation);
               if (inputStream == null) {
                  break;
               }

               Properties properties2 = new Properties();
               properties2.load(inputStream);
               inputStream.close();
               Config.dbg("CustomSky properties: " + string3);
               String string4 = string2 + k + ".png";
               CustomSkyLayer customSkyLayer = new CustomSkyLayer(properties2, string4);
               if (customSkyLayer.isValid(string3)) {
                  ResourceLocation resourceLocation2 = new ResourceLocation(customSkyLayer.source);
                  ITextureObject iTextureObject = TextureUtils.getTexture(resourceLocation2);
                  if (iTextureObject == null) {
                     Config.log("CustomSky: Texture not found: " + resourceLocation2);
                  } else {
                     customSkyLayer.textureId = iTextureObject.getGlTextureId();
                     arrayList.add(customSkyLayer);
                     inputStream.close();
                  }
               }
            } catch (FileNotFoundException fileNotFoundException) {
               break;
            } catch (IOException ioException) {
               ioException.printStackTrace();
            }
         }

         if (arrayList.size() > 0) {
            CustomSkyLayer[] customSkyLayers = (net.minecraft.src.CustomSkyLayer[])(arrayList.toArray(new CustomSkyLayer[arrayList.size()]));
            customSkyLayerss[j] = customSkyLayers;
            i = j;
         }
      }

      if (i < 0) {
         return (CustomSkyLayer[][])null;
      }

      int l = i + 1;
      CustomSkyLayer[][] customSkyLayerss2 = new CustomSkyLayer[l][0];

      for (int m = 0; m < customSkyLayerss2.length; m++) {
         customSkyLayerss2[m] = customSkyLayerss[m];
      }

      return customSkyLayerss2;
   }

   public static void renderSky(World world, TextureManager textureManager, float f, float g) {
      if (worldSkyLayers != null && Config.getGameSettings().renderDistanceChunks >= 8) {
         int i = world.provider.getDimensionId();
         if (i >= 0 && i < worldSkyLayers.length) {
            CustomSkyLayer[] customSkyLayers = worldSkyLayers[i];
            if (customSkyLayers != null) {
               long l = world.getWorldTime();
               int j = (int)(l % 24000L);

               for (int k = 0; k < customSkyLayers.length; k++) {
                  CustomSkyLayer customSkyLayer = customSkyLayers[k];
                  if (customSkyLayer.isActive(world, j)) {
                     customSkyLayer.render(j, f, g);
                  }
               }

               Blender.clearBlend(g);
            }
         }
      }
   }

   public static boolean hasSkyLayers(World world) {
      if (worldSkyLayers == null) {
         return false;
      } else if (Config.getGameSettings().renderDistanceChunks < 8) {
         return false;
      } else {
         int i = world.provider.getDimensionId();
         if (i >= 0 && i < worldSkyLayers.length) {
            CustomSkyLayer[] customSkyLayers = worldSkyLayers[i];
            return customSkyLayers == null ? false : customSkyLayers.length > 0;
         } else {
            return false;
         }
      }
   }
}
