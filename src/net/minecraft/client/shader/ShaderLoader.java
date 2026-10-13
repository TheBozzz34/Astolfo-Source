package net.minecraft.client.shader;

import com.google.common.collect.Maps;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Map;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.util.JsonException;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.BufferUtils;

public class ShaderLoader {
   private final ShaderLoader.ShaderType shaderType;
   private final String shaderFilename;
   private int shader;
   private int shaderAttachCount = 0;
   private static final long a = 61273118313009L;
   private static final long f = 5788503952270065664L;

   private ShaderLoader(ShaderLoader.ShaderType var1, int var2, String var3) {
      this.shaderType = var1;
      this.shader = var2;
      this.shaderFilename = var3;
   }

   public void attachShader(ShaderManager var1) {
      this.shaderAttachCount++;
      OpenGlHelper.glAttachShader(var1.getProgram(), this.shader);
   }

   public void deleteShader(ShaderManager var1) {
      this.shaderAttachCount--;
      if (this.shaderAttachCount <= 0) {
         OpenGlHelper.glDeleteShader(this.shader);
         this.shaderType.getLoadedShaders().remove(this.shaderFilename);
      }
   }

   public String getShaderFilename() {
      return this.shaderFilename;
   }

   public static ShaderLoader loadShader(IResourceManager var0, ShaderLoader.ShaderType var1, String var2) throws IOException {
      ShaderLoader var6 = var1.getLoadedShaders().get(var2);
      if (var6 == null) {
         ResourceLocation var7 = new ResourceLocation("shaders/program/" + var2 + var1.getShaderExtension());
         BufferedInputStream var8 = new BufferedInputStream(var0.getResource(var7).getInputStream());
         byte[] var9 = toByteArray(var8);
         ByteBuffer var10 = BufferUtils.createByteBuffer(var9.length);
         var10.put(var9);
         ((Buffer)var10).position(0);
         int var11 = OpenGlHelper.glCreateShader(var1.getShaderMode());
         OpenGlHelper.glShaderSource(var11, var10);
         OpenGlHelper.glCompileShader(var11);
         if (OpenGlHelper.glGetShaderi(var11, OpenGlHelper.GL_COMPILE_STATUS) == 0) {
            String var12 = StringUtils.trim(OpenGlHelper.glGetShaderInfoLog(var11, (int)f));
            JsonException var13 = new JsonException("Couldn't compile " + var1.getShaderName() + " program: " + var12);
            var13.func_151381_b(var7.getResourcePath());
            throw var13;
         }

         var6 = new ShaderLoader(var1, var11, var2);
         var1.getLoadedShaders().put(var2, var6);
      }

      return var6;
   }

   public static byte[] toByteArray(BufferedInputStream var0) throws IOException {
      byte[] var1;
      try {
         var1 = IOUtils.toByteArray(var0);
      } finally {
         var0.close();
      }

      return var1;
   }

   public enum ShaderType {
      VERTEX("vertex", ".vsh", OpenGlHelper.GL_VERTEX_SHADER),
      FRAGMENT("fragment", ".fsh", OpenGlHelper.GL_FRAGMENT_SHADER);

      private final String shaderName;
      private final String shaderExtension;
      private final int shaderMode;
      private final Map<String, ShaderLoader> loadedShaders = Maps.newHashMap();

      ShaderType(String var3, String var4, int var5) {
         this.shaderName = var3;
         this.shaderExtension = var4;
         this.shaderMode = var5;
      }

      public String getShaderName() {
         return this.shaderName;
      }

      public String getShaderExtension() {
         return this.shaderExtension;
      }

      public int getShaderMode() {
         return this.shaderMode;
      }

      public Map<String, ShaderLoader> getLoadedShaders() {
         return this.loadedShaders;
      }
   }
}
