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

   public static IntBuffer getIntBuffer(int size) {
      if (intBuffer.capacity() < size) {
         int i = roundUpPOT(size);
         byteBuffer = BufferUtils.createByteBuffer(i * 4);
         intBuffer = byteBuffer.asIntBuffer();
      }

      return intBuffer;
   }

   public static int[] getIntArray(int size) {
      if (intArray == null) {
         intArray = new int[1048576];
      }

      if (intArray.length < size) {
         intArray = new int[roundUpPOT(size)];
      }

      return intArray;
   }

   public static int roundUpPOT(int x) {
      int i = x - 1;
      i |= i >> 1;
      i |= i >> 2;
      i |= i >> 4;
      i |= i >> 8;
      i |= i >> 16;
      return i + 1;
   }

   public static int log2(int x) {
      int i = 0;
      if ((x & -65536) != 0) {
         i += 16;
         x >>= 16;
      }

      if ((x & 0xFF00) != 0) {
         i += 8;
         x >>= 8;
      }

      if ((x & 240) != 0) {
         i += 4;
         x >>= 4;
      }

      if ((x & 6) != 0) {
         i += 2;
         x >>= 2;
      }

      if ((x & 2) != 0) {
         i++;
      }

      return i;
   }

   public static IntBuffer fillIntBuffer(int size, int value) {
      int[] ints = getIntArray(size);
      IntBuffer intBuffer2 = getIntBuffer(size);
      Arrays.fill(intArray, 0, size, value);
      intBuffer.put(intArray, 0, size);
      return intBuffer;
   }

   public static int[] createAIntImage(int size) {
      int[] ints = new int[size * 3];
      Arrays.fill(ints, 0, size, 0);
      Arrays.fill(ints, size, size * 2, -8421377);
      Arrays.fill(ints, size * 2, size * 3, 0);
      return ints;
   }

   public static int[] createAIntImage(int size, int color) {
      int[] ints = new int[size * 3];
      Arrays.fill(ints, 0, size, color);
      Arrays.fill(ints, size, size * 2, -8421377);
      Arrays.fill(ints, size * 2, size * 3, 0);
      return ints;
   }

   public static MultiTexID getMultiTexID(AbstractTexture tex) {
      MultiTexID multiTexID = tex.multiTex;
      if (multiTexID == null) {
         int i = tex.getGlTextureId();
         multiTexID = multiTexMap.get(i);
         if (multiTexID == null) {
            multiTexID = new MultiTexID(i, GL11.glGenTextures(), GL11.glGenTextures());
            multiTexMap.put(i, multiTexID);
         }

         tex.multiTex = multiTexID;
      }

      return multiTexID;
   }

   public static void deleteTextures(AbstractTexture atex, int texid) {
      MultiTexID multiTexID = atex.multiTex;
      if (multiTexID != null) {
         atex.multiTex = null;
         multiTexMap.remove(multiTexID.base);
         GlStateManager.deleteTexture(multiTexID.norm);
         GlStateManager.deleteTexture(multiTexID.spec);
         if (multiTexID.base != texid) {
            SMCLog.warning("Error : MultiTexID.base mismatch: " + multiTexID.base + ", texid: " + texid);
            GlStateManager.deleteTexture(multiTexID.base);
         }
      }
   }

   public static void bindNSTextures(int normTex, int specTex) {
      if (Shaders.isRenderingWorld && GlStateManager.getActiveTextureUnit() == 33984) {
         GlStateManager.setActiveTexture(33986);
         GlStateManager.bindTexture(normTex);
         GlStateManager.setActiveTexture(33987);
         GlStateManager.bindTexture(specTex);
         GlStateManager.setActiveTexture(33984);
      }
   }

   public static void bindNSTextures(MultiTexID multiTex) {
      bindNSTextures(multiTex.norm, multiTex.spec);
   }

   public static void bindTextures(int baseTex, int normTex, int specTex) {
      if (Shaders.isRenderingWorld && GlStateManager.getActiveTextureUnit() == 33984) {
         GlStateManager.setActiveTexture(33986);
         GlStateManager.bindTexture(normTex);
         GlStateManager.setActiveTexture(33987);
         GlStateManager.bindTexture(specTex);
         GlStateManager.setActiveTexture(33984);
      }

      GlStateManager.bindTexture(baseTex);
   }

   public static void bindTextures(MultiTexID multiTex) {
      boundTex = multiTex;
      if (Shaders.isRenderingWorld && GlStateManager.getActiveTextureUnit() == 33984) {
         if (Shaders.configNormalMap) {
            GlStateManager.setActiveTexture(33986);
            GlStateManager.bindTexture(multiTex.norm);
         }

         if (Shaders.configSpecularMap) {
            GlStateManager.setActiveTexture(33987);
            GlStateManager.bindTexture(multiTex.spec);
         }

         GlStateManager.setActiveTexture(33984);
      }

      GlStateManager.bindTexture(multiTex.base);
   }

   public static void bindTexture(ITextureObject tex) {
      int i = tex.getGlTextureId();
      if (tex instanceof TextureMap) {
         Shaders.atlasSizeX = ((TextureMap)tex).atlasWidth;
         Shaders.atlasSizeY = ((TextureMap)tex).atlasHeight;
         bindTextures(tex.getMultiTexID());
      } else {
         Shaders.atlasSizeX = 0;
         Shaders.atlasSizeY = 0;
         bindTextures(tex.getMultiTexID());
      }
   }

   public static void bindTextureMapForUpdateAndRender(TextureManager tm, ResourceLocation resLoc) {
      TextureMap tex = (TextureMap)tm.getTexture(resLoc);
      Shaders.atlasSizeX = tex.atlasWidth;
      Shaders.atlasSizeY = tex.atlasHeight;
      bindTextures(updatingTex = tex.getMultiTexID());
   }

   public static void bindTextures(int baseTex) {
      MultiTexID multiTex = multiTexMap.get(baseTex);
      bindTextures(multiTex);
   }

   public static void initDynamicTexture(int texID, int width, int height, DynamicTexture tex) {
      MultiTexID multiTexID = tex.getMultiTexID();
      int[] ints = tex.getTextureData();
      int i = width * height;
      Arrays.fill(ints, i, i * 2, -8421377);
      Arrays.fill(ints, i * 2, i * 3, 0);
      TextureUtil.allocateTexture(multiTexID.base, width, height);
      TextureUtil.setTextureBlurMipmap(false, false);
      TextureUtil.setTextureClamped(false);
      TextureUtil.allocateTexture(multiTexID.norm, width, height);
      TextureUtil.setTextureBlurMipmap(false, false);
      TextureUtil.setTextureClamped(false);
      TextureUtil.allocateTexture(multiTexID.spec, width, height);
      TextureUtil.setTextureBlurMipmap(false, false);
      TextureUtil.setTextureClamped(false);
      GlStateManager.bindTexture(multiTexID.base);
   }

   public static void updateDynamicTexture(int texID, int[] src, int width, int height, DynamicTexture tex) {
      MultiTexID multiTex = tex.getMultiTexID();
      GlStateManager.bindTexture(multiTex.base);
      updateDynTexSubImage1(src, width, height, 0, 0, 0);
      GlStateManager.bindTexture(multiTex.norm);
      updateDynTexSubImage1(src, width, height, 0, 0, 1);
      GlStateManager.bindTexture(multiTex.spec);
      updateDynTexSubImage1(src, width, height, 0, 0, 2);
      GlStateManager.bindTexture(multiTex.base);
   }

   public static void updateDynTexSubImage1(int[] src, int width, int height, int posX, int posY, int page) {
      int i = width * height;
      IntBuffer intBuffer = getIntBuffer(i);
      ((Buffer)intBuffer).clear();
      int j = page * i;
      if (src.length >= j + i) {
         ((Buffer)intBuffer.put(src, j, i)).position(0).limit(i);
         GL11.glTexSubImage2D(3553, 0, posX, posY, width, height, 32993, 33639, intBuffer);
         ((Buffer)intBuffer).clear();
      }
   }

   public static ITextureObject createDefaultTexture() {
      DynamicTexture tex = new DynamicTexture(1, 1);
      tex.getTextureData()[0] = -1;
      tex.updateDynamicTexture();
      return tex;
   }

   public static void allocateTextureMap(int texID, int mipmapLevels, int width, int height, Stitcher stitcher, TextureMap tex) {
      SMCLog.info("allocateTextureMap " + mipmapLevels + " " + width + " " + height + " ");
      updatingTextureMap = tex;
      tex.atlasWidth = width;
      tex.atlasHeight = height;
      MultiTexID multiTexID = getMultiTexID(tex);
      updatingTex = multiTexID;
      TextureUtil.allocateTextureImpl(multiTexID.base, mipmapLevels, width, height);
      if (Shaders.configNormalMap) {
         TextureUtil.allocateTextureImpl(multiTexID.norm, mipmapLevels, width, height);
      }

      if (Shaders.configSpecularMap) {
         TextureUtil.allocateTextureImpl(multiTexID.spec, mipmapLevels, width, height);
      }

      GlStateManager.bindTexture(texID);
   }

   public static TextureAtlasSprite setSprite(TextureAtlasSprite tas) {
      updatingSprite = tas;
      return tas;
   }

   public static String setIconName(String name) {
      iconName = name;
      return name;
   }

   public static void uploadTexSubForLoadAtlas(int[][] data, int width, int height, int xoffset, int yoffset, boolean linear, boolean clamp) {
      TextureUtil.uploadTextureMipmap(data, width, height, xoffset, yoffset, linear, clamp);
      boolean bl = false;
      if (Shaders.configNormalMap) {
         int[][] intss = readImageAndMipmaps(iconName + "_n", width, height, data.length, bl, -8421377);
         GlStateManager.bindTexture(updatingTex.norm);
         TextureUtil.uploadTextureMipmap(intss, width, height, xoffset, yoffset, linear, clamp);
      }

      if (Shaders.configSpecularMap) {
         int[][] intss2 = readImageAndMipmaps(iconName + "_s", width, height, data.length, bl, 0);
         GlStateManager.bindTexture(updatingTex.spec);
         TextureUtil.uploadTextureMipmap(intss2, width, height, xoffset, yoffset, linear, clamp);
      }

      GlStateManager.bindTexture(updatingTex.base);
   }

   public static int[][] readImageAndMipmaps(String name, int width, int height, int numLevels, boolean border, int defColor) {
      int[][] intss = new int[numLevels][];
      int[] ints;
      intss[0] = ints = new int[width * height];
      boolean bl = false;
      BufferedImage bufferedImage = readImage(updatingTextureMap.completeResourceLocation(new ResourceLocation(name), 0));
      if (bufferedImage != null) {
         int i = bufferedImage.getWidth();
         int j = bufferedImage.getHeight();
         if (i + (border ? 16 : 0) == width) {
            bl = true;
            bufferedImage.getRGB(0, 0, i, i, ints, 0, i);
         }
      }

      if (!bl) {
         Arrays.fill(ints, defColor);
      }

      GlStateManager.bindTexture(updatingTex.spec);
      return genMipmapsSimple(intss.length - 1, width, intss);
   }

   public static BufferedImage readImage(ResourceLocation resLoc) {
      try {
         if (!Config.hasResource(resLoc)) {
            return null;
         }

         InputStream inputStream = Config.getResourceStream(resLoc);
         if (inputStream == null) {
            return null;
         }

         BufferedImage bufferedImage = ImageIO.read(inputStream);
         inputStream.close();
         return bufferedImage;
      } catch (IOException ioException) {
         return null;
      }
   }

   public static int[][] genMipmapsSimple(int maxLevel, int width, int[][] data) {
      for (int i = 1; i <= maxLevel; i++) {
         if (data[i] == null) {
            int j = width >> i;
            int k = j * 2;
            int[] ints = data[i - 1];
            int[] ints2 = data[i] = new int[j * j];

            for (int l = 0; l < j; l++) {
               for (int m = 0; m < j; m++) {
                  int n = l * 2 * k + m * 2;
                  ints2[l * j + m] = blend4Simple(ints[n], ints[n + 1], ints[n + k], ints[n + k + 1]);
               }
            }
         }
      }

      return data;
   }

   public static void uploadTexSub(int[][] data, int width, int height, int xoffset, int yoffset, boolean linear, boolean clamp) {
      TextureUtil.uploadTextureMipmap(data, width, height, xoffset, yoffset, linear, clamp);
      if (Shaders.configNormalMap || Shaders.configSpecularMap) {
         if (Shaders.configNormalMap) {
            GlStateManager.bindTexture(updatingTex.norm);
            uploadTexSub1(data, width, height, xoffset, yoffset, 1);
         }

         if (Shaders.configSpecularMap) {
            GlStateManager.bindTexture(updatingTex.spec);
            uploadTexSub1(data, width, height, xoffset, yoffset, 2);
         }

         GlStateManager.bindTexture(updatingTex.base);
      }
   }

   public static void uploadTexSub1(int[][] src, int width, int height, int posX, int posY, int page) {
      int i = width * height;
      IntBuffer intBuffer = getIntBuffer(i);
      int j = src.length;
      int k = 0;
      int l = width;
      int m = height;
      int n = posX;
      int i2 = posY;

      while (l > 0 && m > 0 && k < j) {
         int i3 = l * m;
         int[] ints = src[k];
         ((Buffer)intBuffer).clear();
         if (ints.length >= i3 * (page + 1)) {
            ((Buffer)intBuffer.put(ints, i3 * page, i3)).position(0).limit(i3);
            GL11.glTexSubImage2D(3553, k, n, i2, l, m, 32993, 33639, intBuffer);
         }

         l >>= 1;
         m >>= 1;
         n >>= 1;
         i2 >>= 1;
         k++;
      }

      ((Buffer)intBuffer).clear();
   }

   public static int blend4Alpha(int c0, int c1, int c2, int c3) {
      int i = c0 >>> 24 & 0xFF;
      int j = c1 >>> 24 & 0xFF;
      int k = c2 >>> 24 & 0xFF;
      int l = c3 >>> 24 & 0xFF;
      int m = i + j + k + l;
      int n = (m + 2) / 4;
      int i2;
      if (m != 0) {
         i2 = m;
      } else {
         i2 = 4;
         i = 1;
         j = 1;
         k = 1;
         l = 1;
      }

      int i3 = (i2 + 1) / 2;
      return n << 24
         | ((c0 >>> 16 & 0xFF) * i + (c1 >>> 16 & 0xFF) * j + (c2 >>> 16 & 0xFF) * k + (c3 >>> 16 & 0xFF) * l + i3) / i2 << 16
         | ((c0 >>> 8 & 0xFF) * i + (c1 >>> 8 & 0xFF) * j + (c2 >>> 8 & 0xFF) * k + (c3 >>> 8 & 0xFF) * l + i3) / i2 << 8
         | ((c0 >>> 0 & 0xFF) * i + (c1 >>> 0 & 0xFF) * j + (c2 >>> 0 & 0xFF) * k + (c3 >>> 0 & 0xFF) * l + i3) / i2 << 0;
   }

   public static int blend4Simple(int c0, int c1, int c2, int c3) {
      return ((c0 >>> 24 & 0xFF) + (c1 >>> 24 & 0xFF) + (c2 >>> 24 & 0xFF) + (c3 >>> 24 & 0xFF) + 2) / 4 << 24
         | ((c0 >>> 16 & 0xFF) + (c1 >>> 16 & 0xFF) + (c2 >>> 16 & 0xFF) + (c3 >>> 16 & 0xFF) + 2) / 4 << 16
         | ((c0 >>> 8 & 0xFF) + (c1 >>> 8 & 0xFF) + (c2 >>> 8 & 0xFF) + (c3 >>> 8 & 0xFF) + 2) / 4 << 8
         | ((c0 >>> 0 & 0xFF) + (c1 >>> 0 & 0xFF) + (c2 >>> 0 & 0xFF) + (c3 >>> 0 & 0xFF) + 2) / 4 << 0;
   }

   public static void genMipmapAlpha(int[] aint, int offset, int width, int height) {
      Math.min(width, height);
      int i = offset;
      int j = width;
      int k = height;
      int l = 0;
      int m = 0;
      int n = 0;
      int i2 = 0;

      while (j > 1 && k > 1) {
         l = i + j * k;
         m = j / 2;
         n = k / 2;

         for (int i3 = 0; i3 < n; i3++) {
            int i4 = l + i3 * m;
            int i5 = i + i3 * 2 * j;

            for (int i6 = 0; i6 < m; i6++) {
               aint[i4 + i6] = blend4Alpha(
                  aint[i5 + i6 * 2], aint[i5 + i6 * 2 + 1], aint[i5 + j + i6 * 2], aint[i5 + j + i6 * 2 + 1]
               );
            }
         }

         i2++;
         j = m;
         k = n;
         i = l;
      }

      while (i2 > 0) {
         j = width >> --i2;
         k = height >> i2;
         i = l - j * k;
         int i7 = i;

         for (int i8 = 0; i8 < k; i8++) {
            for (int i9 = 0; i9 < j; i9++) {
               if (aint[i7] == 0) {
                  aint[i7] = aint[l + i8 / 2 * m + i9 / 2] & 16777215;
               }

               i7++;
            }
         }

         l = i;
         m = j;
      }
   }

   public static void genMipmapSimple(int[] aint, int offset, int width, int height) {
      Math.min(width, height);
      int i = offset;
      int j = width;
      int k = height;
      int l = 0;
      int m = 0;
      int n = 0;
      int i2 = 0;

      while (j > 1 && k > 1) {
         l = i + j * k;
         m = j / 2;
         n = k / 2;

         for (int i3 = 0; i3 < n; i3++) {
            int i4 = l + i3 * m;
            int i5 = i + i3 * 2 * j;

            for (int i6 = 0; i6 < m; i6++) {
               aint[i4 + i6] = blend4Simple(
                  aint[i5 + i6 * 2], aint[i5 + i6 * 2 + 1], aint[i5 + j + i6 * 2], aint[i5 + j + i6 * 2 + 1]
               );
            }
         }

         i2++;
         j = m;
         k = n;
         i = l;
      }

      while (i2 > 0) {
         j = width >> --i2;
         k = height >> i2;
         i = l - j * k;
         int i7 = i;

         for (int i8 = 0; i8 < k; i8++) {
            for (int i9 = 0; i9 < j; i9++) {
               if (aint[i7] == 0) {
                  aint[i7] = aint[l + i8 / 2 * m + i9 / 2] & 16777215;
               }

               i7++;
            }
         }

         l = i;
         m = j;
      }
   }

   public static boolean isSemiTransparent(int[] aint, int width, int height) {
      int i = width * height;
      if (aint[0] >>> 24 == 255 && aint[i - 1] == 0) {
         return true;
      }

      for (int j = 0; j < i; j++) {
         int k = aint[j] >>> 24;
         if (k != 0 && k != 255) {
            return true;
         }
      }

      return false;
   }

   public static void updateSubTex1(int[] src, int width, int height, int posX, int posY) {
      int i = 0;
      int j = width;
      int k = height;
      int l = posX;

      for (int m = posY; j > 0 && k > 0; m /= 2) {
         GL11.glCopyTexSubImage2D(3553, i, l, m, 0, 0, j, k);
         i++;
         j /= 2;
         k /= 2;
         l /= 2;
      }
   }

   public static void setupTexture(MultiTexID multiTex, int[] src, int width, int height, boolean linear, boolean clamp) {
      int i = linear ? 9729 : 9728;
      int j = clamp ? 10496 : 10497;
      int k = width * height;
      IntBuffer intBuffer = getIntBuffer(k);
      ((Buffer)intBuffer).clear();
      ((Buffer)intBuffer.put(src, 0, k)).position(0).limit(k);
      GlStateManager.bindTexture(multiTex.base);
      GL11.glTexImage2D(3553, 0, 6408, width, height, 0, 32993, 33639, intBuffer);
      GL11.glTexParameteri(3553, 10241, i);
      GL11.glTexParameteri(3553, 10240, i);
      GL11.glTexParameteri(3553, 10242, j);
      GL11.glTexParameteri(3553, 10243, j);
      ((Buffer)intBuffer.put(src, k, k)).position(0).limit(k);
      GlStateManager.bindTexture(multiTex.norm);
      GL11.glTexImage2D(3553, 0, 6408, width, height, 0, 32993, 33639, intBuffer);
      GL11.glTexParameteri(3553, 10241, i);
      GL11.glTexParameteri(3553, 10240, i);
      GL11.glTexParameteri(3553, 10242, j);
      GL11.glTexParameteri(3553, 10243, j);
      ((Buffer)intBuffer.put(src, k * 2, k)).position(0).limit(k);
      GlStateManager.bindTexture(multiTex.spec);
      GL11.glTexImage2D(3553, 0, 6408, width, height, 0, 32993, 33639, intBuffer);
      GL11.glTexParameteri(3553, 10241, i);
      GL11.glTexParameteri(3553, 10240, i);
      GL11.glTexParameteri(3553, 10242, j);
      GL11.glTexParameteri(3553, 10243, j);
      GlStateManager.bindTexture(multiTex.base);
   }

   public static void updateSubImage(MultiTexID multiTex, int[] src, int width, int height, int posX, int posY, boolean linear, boolean clamp) {
      int i = width * height;
      IntBuffer intBuffer = getIntBuffer(i);
      ((Buffer)intBuffer).clear();
      intBuffer.put(src, 0, i);
      ((Buffer)intBuffer).position(0).limit(i);
      GlStateManager.bindTexture(multiTex.base);
      GL11.glTexParameteri(3553, 10241, 9728);
      GL11.glTexParameteri(3553, 10240, 9728);
      GL11.glTexParameteri(3553, 10242, 10497);
      GL11.glTexParameteri(3553, 10243, 10497);
      GL11.glTexSubImage2D(3553, 0, posX, posY, width, height, 32993, 33639, intBuffer);
      if (src.length == i * 3) {
         ((Buffer)intBuffer).clear();
         ((Buffer)intBuffer.put(src, i, i)).position(0);
         ((Buffer)intBuffer).position(0).limit(i);
      }

      GlStateManager.bindTexture(multiTex.norm);
      GL11.glTexParameteri(3553, 10241, 9728);
      GL11.glTexParameteri(3553, 10240, 9728);
      GL11.glTexParameteri(3553, 10242, 10497);
      GL11.glTexParameteri(3553, 10243, 10497);
      GL11.glTexSubImage2D(3553, 0, posX, posY, width, height, 32993, 33639, intBuffer);
      if (src.length == i * 3) {
         ((Buffer)intBuffer).clear();
         intBuffer.put(src, i * 2, i);
         ((Buffer)intBuffer).position(0).limit(i);
      }

      GlStateManager.bindTexture(multiTex.spec);
      GL11.glTexParameteri(3553, 10241, 9728);
      GL11.glTexParameteri(3553, 10240, 9728);
      GL11.glTexParameteri(3553, 10242, 10497);
      GL11.glTexParameteri(3553, 10243, 10497);
      GL11.glTexSubImage2D(3553, 0, posX, posY, width, height, 32993, 33639, intBuffer);
      GlStateManager.setActiveTexture(33984);
   }

   public static ResourceLocation getNSMapLocation(ResourceLocation location, String mapName) {
      String string = location.getResourcePath();
      String[] strings = string.split(".png");
      String string2 = strings[0];
      return new ResourceLocation(location.getResourceDomain(), string2 + "_" + mapName + ".png");
   }

   public static void loadNSMap(IResourceManager manager, ResourceLocation location, int width, int height, int[] aint) {
      if (Shaders.configNormalMap) {
         loadNSMap1(manager, getNSMapLocation(location, "n"), width, height, aint, width * height, -8421377);
      }

      if (Shaders.configSpecularMap) {
         loadNSMap1(manager, getNSMapLocation(location, "s"), width, height, aint, width * height * 2, 0);
      }
   }

   public static void loadNSMap1(IResourceManager manager, ResourceLocation location, int width, int height, int[] aint, int offset, int defaultColor) {
      boolean bl = false;

      try {
         IResource iResource = manager.getResource(location);
         BufferedImage bufferedImage = ImageIO.read(iResource.getInputStream());
         if (bufferedImage != null && bufferedImage.getWidth() == width && bufferedImage.getHeight() == height) {
            bufferedImage.getRGB(0, 0, width, height, aint, offset, width);
            bl = true;
         }
      } catch (IOException ioException) {
      }

      if (!bl) {
         Arrays.fill(aint, offset, offset + width * height, defaultColor);
      }
   }

   public static int loadSimpleTexture(
      int textureID,
      BufferedImage bufferedimage,
      boolean linear,
      boolean clamp,
      IResourceManager resourceManager,
      ResourceLocation location,
      MultiTexID multiTex
   ) {
      int width = bufferedimage.getWidth();
      int height = bufferedimage.getHeight();
      int size = width * height;
      int[] aint = getIntArray(size * 3);
      bufferedimage.getRGB(0, 0, width, height, aint, 0, width);
      loadNSMap(resourceManager, location, width, height, aint);
      setupTexture(multiTex, aint, width, height, linear, clamp);
      return textureID;
   }

   public static void mergeImage(int[] aint, int dstoff, int srcoff, int size) {
   }

   public static int blendColor(int color1, int color2, int factor1) {
      int i = 255 - factor1;
      return ((color1 >>> 24 & 0xFF) * factor1 + (color2 >>> 24 & 0xFF) * i) / 255 << 24
         | ((color1 >>> 16 & 0xFF) * factor1 + (color2 >>> 16 & 0xFF) * i) / 255 << 16
         | ((color1 >>> 8 & 0xFF) * factor1 + (color2 >>> 8 & 0xFF) * i) / 255 << 8
         | ((color1 >>> 0 & 0xFF) * factor1 + (color2 >>> 0 & 0xFF) * i) / 255 << 0;
   }

   public static void loadLayeredTexture(LayeredTexture tex, IResourceManager manager, List list) {
      int i = 0;
      int j = 0;
      int k = 0;
      int[] ints = null;

      for (Object object : list) {
         if (object != null) {
            try {
               ResourceLocation resourceLocation = new ResourceLocation((String)object);
               InputStream inputStream = manager.getResource(resourceLocation).getInputStream();
               BufferedImage bufferedImage = ImageIO.read(inputStream);
               if (k == 0) {
                  i = bufferedImage.getWidth();
                  j = bufferedImage.getHeight();
                  k = i * j;
                  ints = createAIntImage(k, 0);
               }

               int[] ints2 = getIntArray(k * 3);
               bufferedImage.getRGB(0, 0, i, j, ints2, 0, i);
               loadNSMap(manager, resourceLocation, i, j, ints2);

               for (int l = 0; l < k; l++) {
                  int m = ints2[l] >>> 24 & 0xFF;
                  ints[k * 0 + l] = blendColor(ints2[k * 0 + l], ints[k * 0 + l], m);
                  ints[k * 1 + l] = blendColor(ints2[k * 1 + l], ints[k * 1 + l], m);
                  ints[k * 2 + l] = blendColor(ints2[k * 2 + l], ints[k * 2 + l], m);
               }
            } catch (IOException ioException) {
               ioException.printStackTrace();
            }
         }
      }

      setupTexture(tex.getMultiTexID(), ints, i, j, false, false);
   }

   public static void updateTextureMinMagFilter() {
      TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
      ITextureObject iTextureObject = textureManager.getTexture(TextureMap.locationBlocksTexture);
      if (iTextureObject != null) {
         MultiTexID multiTexID = iTextureObject.getMultiTexID();
         GlStateManager.bindTexture(multiTexID.base);
         GL11.glTexParameteri(3553, 10241, Shaders.texMinFilValue[Shaders.configTexMinFilB]);
         GL11.glTexParameteri(3553, 10240, Shaders.texMagFilValue[Shaders.configTexMagFilB]);
         GlStateManager.bindTexture(multiTexID.norm);
         GL11.glTexParameteri(3553, 10241, Shaders.texMinFilValue[Shaders.configTexMinFilN]);
         GL11.glTexParameteri(3553, 10240, Shaders.texMagFilValue[Shaders.configTexMagFilN]);
         GlStateManager.bindTexture(multiTexID.spec);
         GL11.glTexParameteri(3553, 10241, Shaders.texMinFilValue[Shaders.configTexMinFilS]);
         GL11.glTexParameteri(3553, 10240, Shaders.texMagFilValue[Shaders.configTexMagFilS]);
         GlStateManager.bindTexture(0);
      }
   }

   public static IResource loadResource(IResourceManager manager, ResourceLocation location) throws IOException {
      resManager = manager;
      resLocation = location;
      return manager.getResource(location);
   }

   public static int[] loadAtlasSprite(BufferedImage bufferedimage, int startX, int startY, int w, int h, int[] aint, int offset, int scansize) {
      imageSize = w * h;
      bufferedimage.getRGB(startX, startY, w, h, aint, offset, scansize);
      loadNSMap(resManager, resLocation, w, h, aint);
      return aint;
   }

   public static int[][] getFrameTexData(int[][] src, int width, int height, int frameIndex) {
      int i = src.length;
      int[][] intss = new int[i][];

      for (int j = 0; j < i; j++) {
         int[] ints = src[j];
         if (ints != null) {
            int k = (width >> j) * (height >> j);
            int[] ints2 = new int[k * 3];
            intss[j] = ints2;
            int l = ints.length / 3;
            int m = k * frameIndex;
            int n = 0;
            System.arraycopy(ints, m, ints2, n, k);
            m += l;
            n += k;
            System.arraycopy(ints, m, ints2, n, k);
            m += l;
            n += k;
            System.arraycopy(ints, m, ints2, n, k);
         }
      }

      return intss;
   }

   public static int[][] prepareAF(TextureAtlasSprite tas, int[][] src, int width, int height) {
      return src;
   }

   public static void fixTransparentColor(TextureAtlasSprite tas, int[] aint) {
   }
}
