package net.minecraft.src;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;

public class NaturalTextures {
   private static NaturalProperties[] propertiesByIndex = new NaturalProperties[0];
   private static final long a = 128262247427347L;
   private static final long e = -3662600461938065152L;

   public static void update() {
      propertiesByIndex = new NaturalProperties[0];
      if (Config.isNaturalTextures()) {
         String var3 = "optifine/natural.properties";

         try {
            ResourceLocation var4 = new ResourceLocation(var3);
            if (!Config.hasResource(var4)) {
               Config.dbg("NaturalTextures: configuration \"" + var3 + "\" not found");
               return;
            }

            boolean var5 = Config.isFromDefaultResourcePack(var4);
            InputStream var6 = Config.getResourceStream(var4);
            ArrayList var7 = new ArrayList((int)e);
            String var8 = Config.readInputStream(var6);
            var6.close();
            String[] var9 = Config.tokenize(var8, "\n\r");
            if (var5) {
               Config.dbg("Natural Textures: Parsing default configuration \"" + var3 + "\"");
               Config.dbg("Natural Textures: Valid only for textures from default resource pack");
            } else {
               Config.dbg("Natural Textures: Parsing configuration \"" + var3 + "\"");
            }

            TextureMap var10 = TextureUtils.getTextureMapBlocks();

            for (int var11 = 0; var11 < var9.length; var11++) {
               String var12 = var9[var11].trim();
               if (!var12.startsWith("#")) {
                  String[] var13 = Config.tokenize(var12, "=");
                  if (var13.length != 2) {
                     Config.warn("Natural Textures: Invalid \"" + var3 + "\" line: " + var12);
                  } else {
                     String var14 = var13[0].trim();
                     String var15 = var13[1].trim();
                     TextureAtlasSprite var16 = var10.getSpriteSafe("minecraft:blocks/" + var14);
                     if (var16 == null) {
                        Config.warn("Natural Textures: Texture not found: \"" + var3 + "\" line: " + var12);
                     } else {
                        int var17 = var16.getIndexInMap();
                        if (var17 < 0) {
                           Config.warn("Natural Textures: Invalid \"" + var3 + "\" line: " + var12);
                        } else {
                           if (var5 && !Config.isFromDefaultResourcePack(new ResourceLocation("textures/blocks/" + var14 + ".png"))) {
                              return;
                           }

                           NaturalProperties var18 = new NaturalProperties(var15);
                           if (var18.isValid()) {
                              while (var7.size() <= var17) {
                                 var7.add(null);
                              }

                              var7.set(var17, var18);
                              Config.dbg("NaturalTextures: " + var14 + " = " + var15);
                           }
                        }
                     }
                  }
               }
            }

            propertiesByIndex = (net.minecraft.src.NaturalProperties[])(var7.toArray(new NaturalProperties[var7.size()]));
         } catch (FileNotFoundException var19) {
            Config.warn("NaturalTextures: configuration \"" + var3 + "\" not found");
            return;
         } catch (Exception var20) {
            var20.printStackTrace();
         }
      }
   }

   public static BakedQuad getNaturalTexture(BlockPos var0, BakedQuad var1) {
      TextureAtlasSprite var5 = var1.getSprite();
      if (var5 == null) {
         return var1;
      }

      NaturalProperties var6 = getNaturalProperties(var5);
      if (var6 == null) {
         return var1;
      }

      int var7 = ConnectedTextures.getSide(var1.getFace());
      int var8 = Config.getRandom(var0, var7);
      int var9 = 0;
      boolean var10 = false;
      if (var6.rotation > 1) {
         var9 = var8 & 3;
      }

      if (var6.rotation == 2) {
         var9 = var9 / 2 * 2;
      }

      if (var6.flip) {
         var10 = (var8 & 4) != 0;
      }

      return var6.getQuad(var1, var9, var10);
   }

   public static NaturalProperties getNaturalProperties(TextureAtlasSprite var0) {
      if (!(var0 instanceof TextureAtlasSprite)) {
         return null;
      }

      int var4 = var0.getIndexInMap();
      return var4 >= 0 && var4 < propertiesByIndex.length ? propertiesByIndex[var4] : null;
   }
}
