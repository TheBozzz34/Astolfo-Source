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
   private static final long a = 13186635971862L;

   public static void v() {
      worldSkyLayers = (CustomSkyLayer[][])null;
   }

   public static void L() {
      v();
      if (Config.L()) {
         worldSkyLayers = readCustomSkies();
      }
   }

   private static CustomSkyLayer[][] readCustomSkies() {
      CustomSkyLayer[][] var3 = new CustomSkyLayer[10][0];
      String var4 = "mcpatcher/sky/world";
      int var5 = -1;

      for (int var6 = 0; var6 < var3.length; var6++) {
         String var7 = var4 + var6 + "/sky";
         ArrayList var8 = new ArrayList();

         for (int var9 = 1; var9 < 1000; var9++) {
            String var10 = var7 + var9 + ".properties";

            try {
               ResourceLocation var11 = new ResourceLocation(var10);
               InputStream var12 = Config.getResourceStream(var11);
               if (var12 == null) {
                  break;
               }

               Properties var13 = new Properties();
               var13.load(var12);
               var12.close();
               Config.dbg("CustomSky properties: " + var10);
               String var14 = var7 + var9 + ".png";
               CustomSkyLayer var15 = new CustomSkyLayer(var13, var14);
               if (var15.isValid(var10)) {
                  ResourceLocation var16 = new ResourceLocation(var15.l);
                  ITextureObject var17 = TextureUtils.getTexture(var16);
                  if (var17 == null) {
                     Config.log("CustomSky: Texture not found: " + var16);
                  } else {
                     var15.d = var17.getGlTextureId();
                     var8.add(var15);
                     var12.close();
                  }
               }
            } catch (FileNotFoundException var18) {
               break;
            } catch (IOException var19) {
               var19.printStackTrace();
            }
         }

         if (var8.size() > 0) {
            CustomSkyLayer[] var23 = (net.minecraft.src.CustomSkyLayer[])(var8.toArray(new CustomSkyLayer[var8.size()]));
            var3[var6] = var23;
            var5 = var6;
         }
      }

      if (var5 < 0) {
         return (CustomSkyLayer[][])null;
      }

      int var20 = var5 + 1;
      CustomSkyLayer[][] var21 = new CustomSkyLayer[var20][0];

      for (int var22 = 0; var22 < var21.length; var22++) {
         var21[var22] = var3[var22];
      }

      return var21;
   }

   public static void J(World var0, TextureManager var1, float var2, float var3) {
      if (worldSkyLayers != null && Config.getGameSettings().renderDistanceChunks >= 8) {
         int var7 = var0.provider.getDimensionId();
         if (var7 >= 0 && var7 < worldSkyLayers.length) {
            CustomSkyLayer[] var8 = worldSkyLayers[var7];
            if (var8 != null) {
               long var9 = var0.getWorldTime();
               int var11 = (int)(var9 % 24000L);

               for (int var12 = 0; var12 < var8.length; var12++) {
                  CustomSkyLayer var13 = var8[var12];
                  if (var13.isActive(var0, var11)) {
                     var13.s(var11, var2, var3);
                  }
               }

               Blender.clearBlend(var3);
            }
         }
      }
   }

   public static boolean hasSkyLayers(World var0) {
      if (worldSkyLayers == null) {
         return false;
      } else if (Config.getGameSettings().renderDistanceChunks < 8) {
         return false;
      } else {
         int var4 = var0.provider.getDimensionId();
         if (var4 >= 0 && var4 < worldSkyLayers.length) {
            CustomSkyLayer[] var5 = worldSkyLayers[var4];
            return var5 == null ? false : var5.length > 0;
         } else {
            return false;
         }
      }
   }
}
