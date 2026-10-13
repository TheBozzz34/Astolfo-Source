package shadersmod.client;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.LayeredTexture;
import net.minecraft.client.renderer.texture.Stitcher;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import shadersmod.common.SMCLog;

public class ShadersTex {
   public static final int initialBufferSize = 1048576;
   public static ByteBuffer byteBuffer = BufferUtils.createByteBuffer(4194304);
   public static IntBuffer intBuffer = byteBuffer.asIntBuffer();
   public static int[] intArray = new int[1048576];
   public static final int defBaseTexColor = 0;
   public static final int defNormTexColor = -8421377;
   public static final int defSpecTexColor = 0;
   public static Map<Integer, MultiTexID> multiTexMap = new HashMap<>();
   public static TextureMap updatingTextureMap = null;
   public static TextureAtlasSprite updatingSprite = null;
   public static MultiTexID updatingTex = null;
   public static MultiTexID boundTex = null;
   public static int updatingPage = 0;
   public static String iconName = null;
   public static IResourceManager resManager = null;
   public static ResourceLocation resLocation = null;
   public static int imageSize = 0;
   private static final long a = 115708160086331L;

   public static IntBuffer getIntBuffer(int var0) {
      if (intBuffer.capacity() < var0) {
         int var3 = roundUpPOT(var0);
         byteBuffer = BufferUtils.createByteBuffer(var3 * 4);
         intBuffer = byteBuffer.asIntBuffer();
      }

      return intBuffer;
   }

   public static int[] getIntArray(int var0) {
      if (intArray == null) {
         intArray = new int[1048576];
      }

      if (intArray.length < var0) {
         intArray = new int[roundUpPOT(var0)];
      }

      return intArray;
   }

   public static int roundUpPOT(int var0) {
      int var3 = var0 - 1;
      var3 |= var3 >> 1;
      var3 |= var3 >> 2;
      var3 |= var3 >> 4;
      var3 |= var3 >> 8;
      var3 |= var3 >> 16;
      return var3 + 1;
   }

   public static int log2(int var0) {
      int var4 = 0;
      if ((var0 & -65536) != 0) {
         var4 += 16;
         var0 >>= 16;
      }

      if ((var0 & 0xFF00) != 0) {
         var4 += 8;
         var0 >>= 8;
      }

      if ((var0 & 240) != 0) {
         var4 += 4;
         var0 >>= 4;
      }

      if ((var0 & 6) != 0) {
         var4 += 2;
         var0 >>= 2;
      }

      if ((var0 & 2) != 0) {
         var4++;
      }

      return var4;
   }

   public static IntBuffer fillIntBuffer(int var0, int var1) {
      int[] var2 = getIntArray(var0);
      IntBuffer var3 = getIntBuffer(var0);
      Arrays.fill(intArray, 0, var0, var1);
      intBuffer.put(intArray, 0, var0);
      return intBuffer;
   }

   public static int[] createAIntImage(int var0) {
      int[] var3 = new int[var0 * 3];
      Arrays.fill(var3, 0, var0, 0);
      Arrays.fill(var3, var0, var0 * 2, -8421377);
      Arrays.fill(var3, var0 * 2, var0 * 3, 0);
      return var3;
   }

   public static int[] createAIntImage(int var0, int var1) {
      int[] var4 = new int[var0 * 3];
      Arrays.fill(var4, 0, var0, var1);
      Arrays.fill(var4, var0, var0 * 2, -8421377);
      Arrays.fill(var4, var0 * 2, var0 * 3, 0);
      return var4;
   }

   public static MultiTexID getMultiTexID(AbstractTexture var0) {
      MultiTexID var3 = var0.multiTex;
      if (var3 == null) {
         int var4 = var0.getGlTextureId();
         var3 = multiTexMap.get(var4);
         if (var3 == null) {
            var3 = new MultiTexID(var4, GL11.glGenTextures(), GL11.glGenTextures());
            multiTexMap.put(var4, var3);
         }

         var0.multiTex = var3;
      }

      return var3;
   }

   public static void deleteTextures(AbstractTexture var0, int var1) {
      MultiTexID var5 = var0.multiTex;
      if (var5 != null) {
         var0.multiTex = null;
         multiTexMap.remove(var5.base);
         GlStateManager.deleteTexture(var5.norm);
         GlStateManager.deleteTexture(var5.spec);
         if (var5.base != var1) {
            SMCLog.warning("Error : MultiTexID.base mismatch: " + var5.base + ", texid: " + var1);
            GlStateManager.deleteTexture(var5.base);
         }
      }
   }

   public static void bindNSTextures(int var0, int var1) {
      long var2 = a ^ 28025472818474L;
      if (Shaders.isRenderingWorld && GlStateManager.getActiveTextureUnit() == 33984) {
         GlStateManager.setActiveTexture(33986);
         GlStateManager.bindTexture(var0);
         long var10000 = 5977265796643902783L ^ var2;
         GlStateManager.setActiveTexture(33987);
         GlStateManager.bindTexture(var1);
         var10000 = 7174659571251472928L ^ var2;
         GlStateManager.setActiveTexture(33984);
      }
   }

   public static void bindNSTextures(MultiTexID var0) {
      bindNSTextures(var0.norm, var0.spec);
   }

   public static void bindTextures(int var0, int var1, int var2) {
      long var3 = a ^ 37801774780013L;
      if (Shaders.isRenderingWorld && GlStateManager.getActiveTextureUnit() == 33984) {
         GlStateManager.setActiveTexture(33986);
         GlStateManager.bindTexture(var1);
         long var10000 = 8224667203623637601L ^ var3;
         GlStateManager.setActiveTexture(33987);
         GlStateManager.bindTexture(var2);
         var10000 = 7174719922672335207L ^ var3;
         GlStateManager.setActiveTexture(33984);
      }

      GlStateManager.bindTexture(var0);
   }

   public static void bindTextures(MultiTexID var0) {
      boundTex = var0;
      if (Shaders.isRenderingWorld && GlStateManager.getActiveTextureUnit() == 33984) {
         if (Shaders.configNormalMap) {
            GlStateManager.setActiveTexture(33986);
            GlStateManager.bindTexture(var0.norm);
         }

         if (Shaders.configSpecularMap) {
            GlStateManager.setActiveTexture(33987);
            GlStateManager.bindTexture(var0.spec);
         }

         GlStateManager.setActiveTexture(33984);
      }

      GlStateManager.bindTexture(var0.base);
   }

   public static void bindTexture(ITextureObject var0) {
      int var4 = var0.getGlTextureId();
      if (var0 instanceof TextureMap) {
         Shaders.atlasSizeX = ((TextureMap)var0).atlasWidth;
         Shaders.atlasSizeY = ((TextureMap)var0).atlasHeight;
         bindTextures(var0.getMultiTexID());
      } else {
         Shaders.atlasSizeX = 0;
         Shaders.atlasSizeY = 0;
         bindTextures(var0.getMultiTexID());
      }
   }

   public static void bindTextureMapForUpdateAndRender(TextureManager var0, ResourceLocation var1) {
      TextureMap var2 = (TextureMap)var0.getTexture(var1);
      Shaders.atlasSizeX = var2.atlasWidth;
      Shaders.atlasSizeY = var2.atlasHeight;
      bindTextures(updatingTex = var2.getMultiTexID());
   }

   public static void bindTextures(int var0) {
      MultiTexID var1 = multiTexMap.get(var0);
      bindTextures(var1);
   }

   public static void initDynamicTexture(int var0, int var1, int var2, DynamicTexture var3) {
      MultiTexID var6 = var3.getMultiTexID();
      int[] var7 = var3.getTextureData();
      int var8 = var1 * var2;
      Arrays.fill(var7, var8, var8 * 2, -8421377);
      Arrays.fill(var7, var8 * 2, var8 * 3, 0);
      TextureUtil.allocateTexture(var6.base, var1, var2);
      TextureUtil.setTextureBlurMipmap(false, false);
      TextureUtil.setTextureClamped(false);
      TextureUtil.allocateTexture(var6.norm, var1, var2);
      TextureUtil.setTextureBlurMipmap(false, false);
      TextureUtil.setTextureClamped(false);
      TextureUtil.allocateTexture(var6.spec, var1, var2);
      TextureUtil.setTextureBlurMipmap(false, false);
      TextureUtil.setTextureClamped(false);
      GlStateManager.bindTexture(var6.base);
   }

   public static void updateDynamicTexture(int var0, int[] var1, int var2, int var3, DynamicTexture var4) {
      MultiTexID var5 = var4.getMultiTexID();
      GlStateManager.bindTexture(var5.base);
      updateDynTexSubImage1(var1, var2, var3, 0, 0, 0);
      GlStateManager.bindTexture(var5.norm);
      updateDynTexSubImage1(var1, var2, var3, 0, 0, 1);
      GlStateManager.bindTexture(var5.spec);
      updateDynTexSubImage1(var1, var2, var3, 0, 0, 2);
      GlStateManager.bindTexture(var5.base);
   }

   public static void updateDynTexSubImage1(int[] var0, int var1, int var2, int var3, int var4, int var5) {
      int var8 = var1 * var2;
      IntBuffer var9 = getIntBuffer(var8);
      ((Buffer)var9).clear();
      int var10 = var5 * var8;
      if (var0.length >= var10 + var8) {
         ((Buffer)var9.put(var0, var10, var8)).position(0).limit(var8);
         GL11.glTexSubImage2D(3553, 0, var3, var4, var1, var2, 32993, 33639, var9);
         ((Buffer)var9).clear();
      }
   }

   public static ITextureObject createDefaultTexture() {
      DynamicTexture var0 = new DynamicTexture(1, 1);
      var0.getTextureData()[0] = -1;
      var0.updateDynamicTexture();
      return var0;
   }

   public static void allocateTextureMap(int var0, int var1, int var2, int var3, Stitcher var4, TextureMap var5) {
      SMCLog.info("allocateTextureMap " + var1 + " " + var2 + " " + var3 + " ");
      updatingTextureMap = var5;
      var5.atlasWidth = var2;
      var5.atlasHeight = var3;
      MultiTexID var9 = getMultiTexID(var5);
      updatingTex = var9;
      TextureUtil.allocateTextureImpl(var9.base, var1, var2, var3);
      if (Shaders.configNormalMap) {
         TextureUtil.allocateTextureImpl(var9.norm, var1, var2, var3);
      }

      if (Shaders.configSpecularMap) {
         TextureUtil.allocateTextureImpl(var9.spec, var1, var2, var3);
      }

      GlStateManager.bindTexture(var0);
   }

   public static TextureAtlasSprite setSprite(TextureAtlasSprite var0) {
      updatingSprite = var0;
      return var0;
   }

   public static String setIconName(String var0) {
      iconName = var0;
      return var0;
   }

   public static void uploadTexSubForLoadAtlas(int[][] var0, int var1, int var2, int var3, int var4, boolean var5, boolean var6) {
      TextureUtil.uploadTextureMipmap(var0, var1, var2, var3, var4, var5, var6);
      boolean var10 = false;
      if (Shaders.configNormalMap) {
         int[][] var11 = readImageAndMipmaps(iconName + "_n", var1, var2, var0.length, var10, -8421377);
         GlStateManager.bindTexture(updatingTex.norm);
         TextureUtil.uploadTextureMipmap(var11, var1, var2, var3, var4, var5, var6);
      }

      if (Shaders.configSpecularMap) {
         int[][] var12 = readImageAndMipmaps(iconName + "_s", var1, var2, var0.length, var10, 0);
         GlStateManager.bindTexture(updatingTex.spec);
         TextureUtil.uploadTextureMipmap(var12, var1, var2, var3, var4, var5, var6);
      }

      GlStateManager.bindTexture(updatingTex.base);
   }

   public static int[][] readImageAndMipmaps(String var0, int var1, int var2, int var3, boolean var4, int var5) {
      int[][] var9 = new int[var3][];
      int[] var10;
      var9[0] = var10 = new int[var1 * var2];
      boolean var11 = false;
      BufferedImage var12 = readImage(updatingTextureMap.completeResourceLocation(new ResourceLocation(var0), 0));
      if (var12 != null) {
         int var13 = var12.getWidth();
         int var14 = var12.getHeight();
         if (var13 + (var4 ? 16 : 0) == var1) {
            var11 = true;
            var12.getRGB(0, 0, var13, var13, var10, 0, var13);
         }
      }

      if (!var11) {
         Arrays.fill(var10, var5);
      }

      GlStateManager.bindTexture(updatingTex.spec);
      return genMipmapsSimple(var9.length - 1, var1, var9);
   }

   public static BufferedImage readImage(ResourceLocation var0) {
      try {
         if (!Config.hasResource(var0)) {
            return null;
         }

         InputStream var4 = Config.getResourceStream(var0);
         if (var4 == null) {
            return null;
         }

         BufferedImage var5 = ImageIO.read(var4);
         var4.close();
         return var5;
      } catch (IOException var6) {
         return null;
      }
   }

   public static int[][] genMipmapsSimple(int var0, int var1, int[][] var2) {
      for (int var6 = 1; var6 <= var0; var6++) {
         if (var2[var6] == null) {
            int var7 = var1 >> var6;
            int var8 = var7 * 2;
            int[] var9 = var2[var6 - 1];
            int[] var10 = var2[var6] = new int[var7 * var7];

            for (int var11 = 0; var11 < var7; var11++) {
               for (int var12 = 0; var12 < var7; var12++) {
                  int var13 = var11 * 2 * var8 + var12 * 2;
                  var10[var11 * var7 + var12] = blend4Simple(var9[var13], var9[var13 + 1], var9[var13 + var8], var9[var13 + var8 + 1]);
               }
            }
         }
      }

      return var2;
   }

   public static void uploadTexSub(int[][] var0, int var1, int var2, int var3, int var4, boolean var5, boolean var6) {
      TextureUtil.uploadTextureMipmap(var0, var1, var2, var3, var4, var5, var6);
      if (Shaders.configNormalMap || Shaders.configSpecularMap) {
         if (Shaders.configNormalMap) {
            GlStateManager.bindTexture(updatingTex.norm);
            uploadTexSub1(var0, var1, var2, var3, var4, 1);
         }

         if (Shaders.configSpecularMap) {
            GlStateManager.bindTexture(updatingTex.spec);
            uploadTexSub1(var0, var1, var2, var3, var4, 2);
         }

         GlStateManager.bindTexture(updatingTex.base);
      }
   }

   public static void uploadTexSub1(int[][] var0, int var1, int var2, int var3, int var4, int var5) {
      int var9 = var1 * var2;
      IntBuffer var10 = getIntBuffer(var9);
      int var11 = var0.length;
      int var12 = 0;
      int var13 = var1;
      int var14 = var2;
      int var15 = var3;
      int var16 = var4;

      while (var13 > 0 && var14 > 0 && var12 < var11) {
         int var17 = var13 * var14;
         int[] var18 = var0[var12];
         ((Buffer)var10).clear();
         if (var18.length >= var17 * (var5 + 1)) {
            ((Buffer)var10.put(var18, var17 * var5, var17)).position(0).limit(var17);
            GL11.glTexSubImage2D(3553, var12, var15, var16, var13, var14, 32993, 33639, var10);
         }

         var13 >>= 1;
         var14 >>= 1;
         var15 >>= 1;
         var16 >>= 1;
         var12++;
      }

      ((Buffer)var10).clear();
   }

   public static int n(int var0, int var1, int var2, int var3) {
      int var7 = var0 >>> 24 & 0xFF;
      int var8 = var1 >>> 24 & 0xFF;
      int var9 = var2 >>> 24 & 0xFF;
      int var10 = var3 >>> 24 & 0xFF;
      int var11 = var7 + var8 + var9 + var10;
      int var12 = (var11 + 2) / 4;
      int var13;
      if (var11 != 0) {
         var13 = var11;
      } else {
         var13 = 4;
         var7 = 1;
         var8 = 1;
         var9 = 1;
         var10 = 1;
      }

      int var14 = (var13 + 1) / 2;
      return var12 << 24
         | ((var0 >>> 16 & 0xFF) * var7 + (var1 >>> 16 & 0xFF) * var8 + (var2 >>> 16 & 0xFF) * var9 + (var3 >>> 16 & 0xFF) * var10 + var14) / var13 << 16
         | ((var0 >>> 8 & 0xFF) * var7 + (var1 >>> 8 & 0xFF) * var8 + (var2 >>> 8 & 0xFF) * var9 + (var3 >>> 8 & 0xFF) * var10 + var14) / var13 << 8
         | ((var0 >>> 0 & 0xFF) * var7 + (var1 >>> 0 & 0xFF) * var8 + (var2 >>> 0 & 0xFF) * var9 + (var3 >>> 0 & 0xFF) * var10 + var14) / var13 << 0;
   }

   public static int blend4Simple(int var0, int var1, int var2, int var3) {
      return ((var0 >>> 24 & 0xFF) + (var1 >>> 24 & 0xFF) + (var2 >>> 24 & 0xFF) + (var3 >>> 24 & 0xFF) + 2) / 4 << 24
         | ((var0 >>> 16 & 0xFF) + (var1 >>> 16 & 0xFF) + (var2 >>> 16 & 0xFF) + (var3 >>> 16 & 0xFF) + 2) / 4 << 16
         | ((var0 >>> 8 & 0xFF) + (var1 >>> 8 & 0xFF) + (var2 >>> 8 & 0xFF) + (var3 >>> 8 & 0xFF) + 2) / 4 << 8
         | ((var0 >>> 0 & 0xFF) + (var1 >>> 0 & 0xFF) + (var2 >>> 0 & 0xFF) + (var3 >>> 0 & 0xFF) + 2) / 4 << 0;
   }

   public static void f(int[] var0, int var1, int var2, int var3) {
      Math.min(var2, var3);
      int var7 = var1;
      int var8 = var2;
      int var9 = var3;
      int var10 = 0;
      int var11 = 0;
      int var12 = 0;
      int var13 = 0;

      while (var8 > 1 && var9 > 1) {
         var10 = var7 + var8 * var9;
         var11 = var8 / 2;
         var12 = var9 / 2;

         for (int var14 = 0; var14 < var12; var14++) {
            int var15 = var10 + var14 * var11;
            int var16 = var7 + var14 * 2 * var8;

            for (int var17 = 0; var17 < var11; var17++) {
               var0[var15 + var17] = n(var0[var16 + var17 * 2], var0[var16 + var17 * 2 + 1], var0[var16 + var8 + var17 * 2], var0[var16 + var8 + var17 * 2 + 1]);
            }
         }

         var13++;
         var8 = var11;
         var9 = var12;
         var7 = var10;
      }

      while (var13 > 0) {
         var8 = var2 >> --var13;
         var9 = var3 >> var13;
         var7 = var10 - var8 * var9;
         int var22 = var7;

         for (int var23 = 0; var23 < var9; var23++) {
            for (int var24 = 0; var24 < var8; var24++) {
               if (var0[var22] == 0) {
                  var0[var22] = var0[var10 + var23 / 2 * var11 + var24 / 2] & 16777215;
               }

               var22++;
            }
         }

         var10 = var7;
         var11 = var8;
      }
   }

   public static void Z(int[] var0, int var1, int var2, int var3) {
      Math.min(var2, var3);
      int var7 = var1;
      int var8 = var2;
      int var9 = var3;
      int var10 = 0;
      int var11 = 0;
      int var12 = 0;
      int var13 = 0;

      while (var8 > 1 && var9 > 1) {
         var10 = var7 + var8 * var9;
         var11 = var8 / 2;
         var12 = var9 / 2;

         for (int var14 = 0; var14 < var12; var14++) {
            int var15 = var10 + var14 * var11;
            int var16 = var7 + var14 * 2 * var8;

            for (int var17 = 0; var17 < var11; var17++) {
               var0[var15 + var17] = blend4Simple(
                  var0[var16 + var17 * 2], var0[var16 + var17 * 2 + 1], var0[var16 + var8 + var17 * 2], var0[var16 + var8 + var17 * 2 + 1]
               );
            }
         }

         var13++;
         var8 = var11;
         var9 = var12;
         var7 = var10;
      }

      while (var13 > 0) {
         var8 = var2 >> --var13;
         var9 = var3 >> var13;
         var7 = var10 - var8 * var9;
         int var22 = var7;

         for (int var23 = 0; var23 < var9; var23++) {
            for (int var24 = 0; var24 < var8; var24++) {
               if (var0[var22] == 0) {
                  var0[var22] = var0[var10 + var23 / 2 * var11 + var24 / 2] & 16777215;
               }

               var22++;
            }
         }

         var10 = var7;
         var11 = var8;
      }
   }

   public static boolean isSemiTransparent(int[] var0, int var1, int var2) {
      int var6 = var1 * var2;
      if (var0[0] >>> 24 == 255 && var0[var6 - 1] == 0) {
         return true;
      }

      for (int var7 = 0; var7 < var6; var7++) {
         int var8 = var0[var7] >>> 24;
         if (var8 != 0 && var8 != 255) {
            return true;
         }
      }

      return false;
   }

   public static void updateSubTex1(int[] var0, int var1, int var2, int var3, int var4) {
      int var8 = 0;
      int var9 = var1;
      int var10 = var2;
      int var11 = var3;

      for (int var12 = var4; var9 > 0 && var10 > 0; var12 /= 2) {
         GL11.glCopyTexSubImage2D(3553, var8, var11, var12, 0, 0, var9, var10);
         var8++;
         var9 /= 2;
         var10 /= 2;
         var11 /= 2;
      }
   }

   public static void setupTexture(MultiTexID var0, int[] var1, int var2, int var3, boolean var4, boolean var5) {
      long var6 = a ^ 63368218621672L;
      int var9 = var4 ? 9729 : 9728;
      int var10 = var5 ? 10496 : 10497;
      int var11 = var2 * var3;
      long var10000 = 9105653145016864870L ^ var6;
      IntBuffer var12 = getIntBuffer(var11);
      ((Buffer)var12).clear();
      ((Buffer)var12.put(var1, 0, var11)).position(0).limit(var11);
      GlStateManager.bindTexture(var0.base);
      GL11.glTexImage2D(3553, 0, 6408, var2, var3, 0, 32993, 33639, var12);
      GL11.glTexParameteri(3553, 10241, var9);
      GL11.glTexParameteri(3553, 10240, var9);
      GL11.glTexParameteri(3553, 10242, var10);
      GL11.glTexParameteri(3553, 10243, var10);
      ((Buffer)var12.put(var1, var11, var11)).position(0).limit(var11);
      var10000 = 9105653145016864870L ^ var6;
      GlStateManager.bindTexture(var0.norm);
      GL11.glTexImage2D(3553, 0, 6408, var2, var3, 0, 32993, 33639, var12);
      GL11.glTexParameteri(3553, 10241, var9);
      GL11.glTexParameteri(3553, 10240, var9);
      GL11.glTexParameteri(3553, 10242, var10);
      GL11.glTexParameteri(3553, 10243, var10);
      ((Buffer)var12.put(var1, var11 * 2, var11)).position(0).limit(var11);
      var10000 = 9105653145016864870L ^ var6;
      GlStateManager.bindTexture(var0.spec);
      GL11.glTexImage2D(3553, 0, 6408, var2, var3, 0, 32993, 33639, var12);
      GL11.glTexParameteri(3553, 10241, var9);
      GL11.glTexParameteri(3553, 10240, var9);
      GL11.glTexParameteri(3553, 10242, var10);
      GL11.glTexParameteri(3553, 10243, var10);
      GlStateManager.bindTexture(var0.base);
   }

   public static void updateSubImage(MultiTexID var0, int[] var1, int var2, int var3, int var4, int var5, boolean var6, boolean var7) {
      int var11 = var2 * var3;
      IntBuffer var12 = getIntBuffer(var11);
      ((Buffer)var12).clear();
      var12.put(var1, 0, var11);
      ((Buffer)var12).position(0).limit(var11);
      GlStateManager.bindTexture(var0.base);
      GL11.glTexParameteri(3553, 10241, 9728);
      GL11.glTexParameteri(3553, 10240, 9728);
      GL11.glTexParameteri(3553, 10242, 10497);
      GL11.glTexParameteri(3553, 10243, 10497);
      GL11.glTexSubImage2D(3553, 0, var4, var5, var2, var3, 32993, 33639, var12);
      if (var1.length == var11 * 3) {
         ((Buffer)var12).clear();
         ((Buffer)var12.put(var1, var11, var11)).position(0);
         ((Buffer)var12).position(0).limit(var11);
      }

      GlStateManager.bindTexture(var0.norm);
      GL11.glTexParameteri(3553, 10241, 9728);
      GL11.glTexParameteri(3553, 10240, 9728);
      GL11.glTexParameteri(3553, 10242, 10497);
      GL11.glTexParameteri(3553, 10243, 10497);
      GL11.glTexSubImage2D(3553, 0, var4, var5, var2, var3, 32993, 33639, var12);
      if (var1.length == var11 * 3) {
         ((Buffer)var12).clear();
         var12.put(var1, var11 * 2, var11);
         ((Buffer)var12).position(0).limit(var11);
      }

      GlStateManager.bindTexture(var0.spec);
      GL11.glTexParameteri(3553, 10241, 9728);
      GL11.glTexParameteri(3553, 10240, 9728);
      GL11.glTexParameteri(3553, 10242, 10497);
      GL11.glTexParameteri(3553, 10243, 10497);
      GL11.glTexSubImage2D(3553, 0, var4, var5, var2, var3, 32993, 33639, var12);
      GlStateManager.setActiveTexture(33984);
   }

   public static ResourceLocation getNSMapLocation(ResourceLocation var0, String var1) {
      String var4 = var0.getResourcePath();
      String[] var5 = var4.split(".png");
      String var6 = var5[0];
      return new ResourceLocation(var0.getResourceDomain(), var6 + "_" + var1 + ".png");
   }

   public static void loadNSMap(IResourceManager var0, ResourceLocation var1, int var2, int var3, int[] var4) {
      if (Shaders.configNormalMap) {
         loadNSMap1(var0, getNSMapLocation(var1, "n"), var2, var3, var4, var2 * var3, -8421377);
      }

      if (Shaders.configSpecularMap) {
         loadNSMap1(var0, getNSMapLocation(var1, "s"), var2, var3, var4, var2 * var3 * 2, 0);
      }
   }

   public static void loadNSMap1(IResourceManager var0, ResourceLocation var1, int var2, int var3, int[] var4, int var5, int var6) {
      boolean var10 = false;

      try {
         IResource var11 = var0.getResource(var1);
         BufferedImage var12 = ImageIO.read(var11.getInputStream());
         if (var12 != null && var12.getWidth() == var2 && var12.getHeight() == var3) {
            var12.getRGB(0, 0, var2, var3, var4, var5, var2);
            var10 = true;
         }
      } catch (IOException var13) {
      }

      if (!var10) {
         Arrays.fill(var4, var5, var5 + var2 * var3, var6);
      }
   }

   public static int loadSimpleTexture(int var0, BufferedImage var1, boolean var2, boolean var3, IResourceManager var4, ResourceLocation var5, MultiTexID var6) {
      int var7 = var1.getWidth();
      int var8 = var1.getHeight();
      int var9 = var7 * var8;
      int[] var10 = getIntArray(var9 * 3);
      var1.getRGB(0, 0, var7, var8, var10, 0, var7);
      loadNSMap(var4, var5, var7, var8, var10);
      setupTexture(var6, var10, var7, var8, var2, var3);
      return var0;
   }

   public static void N(int[] var0, int var1, int var2, int var3) {
   }

   public static int blendColor(int var0, int var1, int var2) {
      int var5 = 255 - var2;
      return ((var0 >>> 24 & 0xFF) * var2 + (var1 >>> 24 & 0xFF) * var5) / 255 << 24
         | ((var0 >>> 16 & 0xFF) * var2 + (var1 >>> 16 & 0xFF) * var5) / 255 << 16
         | ((var0 >>> 8 & 0xFF) * var2 + (var1 >>> 8 & 0xFF) * var5) / 255 << 8
         | ((var0 >>> 0 & 0xFF) * var2 + (var1 >>> 0 & 0xFF) * var5) / 255 << 0;
   }

   public static void loadLayeredTexture(LayeredTexture var0, IResourceManager var1, List var2) {
      int var6 = 0;
      int var7 = 0;
      int var8 = 0;
      int[] var9 = null;

      for (Object var11 : var2) {
         if (var11 != null) {
            try {
               ResourceLocation var12 = new ResourceLocation((String)var11);
               InputStream var13 = var1.getResource(var12).getInputStream();
               BufferedImage var14 = ImageIO.read(var13);
               if (var8 == 0) {
                  var6 = var14.getWidth();
                  var7 = var14.getHeight();
                  var8 = var6 * var7;
                  var9 = createAIntImage(var8, 0);
               }

               int[] var15 = getIntArray(var8 * 3);
               var14.getRGB(0, 0, var6, var7, var15, 0, var6);
               loadNSMap(var1, var12, var6, var7, var15);

               for (int var16 = 0; var16 < var8; var16++) {
                  int var17 = var15[var16] >>> 24 & 0xFF;
                  var9[var8 * 0 + var16] = blendColor(var15[var8 * 0 + var16], var9[var8 * 0 + var16], var17);
                  var9[var8 * 1 + var16] = blendColor(var15[var8 * 1 + var16], var9[var8 * 1 + var16], var17);
                  var9[var8 * 2 + var16] = blendColor(var15[var8 * 2 + var16], var9[var8 * 2 + var16], var17);
               }
            } catch (IOException var18) {
               var18.printStackTrace();
            }
         }
      }

      setupTexture(var0.getMultiTexID(), var9, var6, var7, false, false);
   }

   public static void updateTextureMinMagFilter() {
      long var0 = a ^ 63330937206196L;
      TextureManager var3 = Minecraft.getMinecraft().getTextureManager();
      ITextureObject var4 = var3.getTexture(TextureMap.locationBlocksTexture);
      if (var4 != null) {
         MultiTexID var5 = var4.getMultiTexID();
         GlStateManager.bindTexture(var5.base);
         long var10000 = 9105653313357796154L ^ var0;
         GL11.glTexParameteri(3553, 10241, Shaders.texMinFilValue[Shaders.configTexMinFilB]);
         GL11.glTexParameteri(3553, 10240, Shaders.texMagFilValue[Shaders.configTexMagFilB]);
         GlStateManager.bindTexture(var5.norm);
         var10000 = 9105653313357796154L ^ var0;
         GL11.glTexParameteri(3553, 10241, Shaders.texMinFilValue[Shaders.configTexMinFilN]);
         GL11.glTexParameteri(3553, 10240, Shaders.texMagFilValue[Shaders.ub]);
         GlStateManager.bindTexture(var5.spec);
         var10000 = 9105653313357796154L ^ var0;
         GL11.glTexParameteri(3553, 10241, Shaders.texMinFilValue[Shaders.configTexMinFilS]);
         GL11.glTexParameteri(3553, 10240, Shaders.texMagFilValue[Shaders.L]);
         GlStateManager.bindTexture(0);
      }
   }

   public static IResource loadResource(IResourceManager var0, ResourceLocation var1) throws IOException {
      resManager = var0;
      resLocation = var1;
      return var0.getResource(var1);
   }

   public static int[] loadAtlasSprite(BufferedImage var0, int var1, int var2, int var3, int var4, int[] var5, int var6, int var7) {
      imageSize = var3 * var4;
      var0.getRGB(var1, var2, var3, var4, var5, var6, var7);
      loadNSMap(resManager, resLocation, var3, var4, var5);
      return var5;
   }

   public static int[][] getFrameTexData(int[][] var0, int var1, int var2, int var3) {
      int var7 = var0.length;
      int[][] var8 = new int[var7][];

      for (int var9 = 0; var9 < var7; var9++) {
         int[] var10 = var0[var9];
         if (var10 != null) {
            int var11 = (var1 >> var9) * (var2 >> var9);
            int[] var12 = new int[var11 * 3];
            var8[var9] = var12;
            int var13 = var10.length / 3;
            int var14 = var11 * var3;
            int var15 = 0;
            System.arraycopy(var10, var14, var12, var15, var11);
            var14 += var13;
            var15 += var11;
            System.arraycopy(var10, var14, var12, var15, var11);
            var14 += var13;
            var15 += var11;
            System.arraycopy(var10, var14, var12, var15, var11);
         }
      }

      return var8;
   }

   public static int[][] prepareAF(TextureAtlasSprite var0, int[][] var1, int var2, int var3) {
      return var1;
   }

   public static void fixTransparentColor(TextureAtlasSprite var0, int[] var1) {
   }

   static {
      long var20 = a ^ 40851684065142L;
      long var10000 = var20 ^ 8366248186743340471L;
      var10000 = var20 ^ 8586458847900479132L;
      var10000 = 738699630547068525L ^ var20;
   }
}
