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

   public static void update() {
      propertiesByIndex = new NaturalProperties[0];
      if (Config.isNaturalTextures()) {
         String string = "optifine/natural.properties";

         try {
            ResourceLocation resourceLocation = new ResourceLocation(string);
            if (!Config.hasResource(resourceLocation)) {
               Config.dbg("NaturalTextures: configuration \"" + string + "\" not found");
               return;
            }

            boolean bl = Config.isFromDefaultResourcePack(resourceLocation);
            InputStream inputStream = Config.getResourceStream(resourceLocation);
            ArrayList arrayList = new ArrayList(256);
            String string2 = Config.readInputStream(inputStream);
            inputStream.close();
            String[] strings = Config.tokenize(string2, "\n\r");
            if (bl) {
               Config.dbg("Natural Textures: Parsing default configuration \"" + string + "\"");
               Config.dbg("Natural Textures: Valid only for textures from default resource pack");
            } else {
               Config.dbg("Natural Textures: Parsing configuration \"" + string + "\"");
            }

            TextureMap textureMap = TextureUtils.getTextureMapBlocks();

            for (int i = 0; i < strings.length; i++) {
               String string3 = strings[i].trim();
               if (!string3.startsWith("#")) {
                  String[] strings2 = Config.tokenize(string3, "=");
                  if (strings2.length != 2) {
                     Config.warn("Natural Textures: Invalid \"" + string + "\" line: " + string3);
                  } else {
                     String string4 = strings2[0].trim();
                     String string5 = strings2[1].trim();
                     TextureAtlasSprite textureAtlasSprite = textureMap.getSpriteSafe("minecraft:blocks/" + string4);
                     if (textureAtlasSprite == null) {
                        Config.warn("Natural Textures: Texture not found: \"" + string + "\" line: " + string3);
                     } else {
                        int j = textureAtlasSprite.getIndexInMap();
                        if (j < 0) {
                           Config.warn("Natural Textures: Invalid \"" + string + "\" line: " + string3);
                        } else {
                           if (bl && !Config.isFromDefaultResourcePack(new ResourceLocation("textures/blocks/" + string4 + ".png"))) {
                              return;
                           }

                           NaturalProperties naturalProperties = new NaturalProperties(string5);
                           if (naturalProperties.isValid()) {
                              while (arrayList.size() <= j) {
                                 arrayList.add(null);
                              }

                              arrayList.set(j, naturalProperties);
                              Config.dbg("NaturalTextures: " + string4 + " = " + string5);
                           }
                        }
                     }
                  }
               }
            }

            propertiesByIndex = (net.minecraft.src.NaturalProperties[])(arrayList.toArray(new NaturalProperties[arrayList.size()]));
         } catch (FileNotFoundException fileNotFoundException) {
            Config.warn("NaturalTextures: configuration \"" + string + "\" not found");
            return;
         } catch (Exception exception) {
            exception.printStackTrace();
         }
      }
   }

   public static BakedQuad getNaturalTexture(BlockPos blockPosIn, BakedQuad quad) {
      TextureAtlasSprite textureAtlasSprite = quad.getSprite();
      if (textureAtlasSprite == null) {
         return quad;
      }

      NaturalProperties naturalProperties = getNaturalProperties(textureAtlasSprite);
      if (naturalProperties == null) {
         return quad;
      }

      int i = ConnectedTextures.getSide(quad.getFace());
      int j = Config.getRandom(blockPosIn, i);
      int k = 0;
      boolean bl = false;
      if (naturalProperties.rotation > 1) {
         k = j & 3;
      }

      if (naturalProperties.rotation == 2) {
         k = k / 2 * 2;
      }

      if (naturalProperties.flip) {
         bl = (j & 4) != 0;
      }

      return naturalProperties.getQuad(quad, k, bl);
   }

   public static NaturalProperties getNaturalProperties(TextureAtlasSprite icon) {
      if (!(icon instanceof TextureAtlasSprite)) {
         return null;
      }

      int i = icon.getIndexInMap();
      return i >= 0 && i < propertiesByIndex.length ? propertiesByIndex[i] : null;
   }
}
