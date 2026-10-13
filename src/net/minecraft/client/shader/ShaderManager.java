package net.minecraft.client.shader;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.util.JsonBlendingMode;
import net.minecraft.client.util.JsonException;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ShaderManager {
   private static final Logger logger = LogManager.getLogger();
   private static final ShaderDefault defaultShaderUniform = new ShaderDefault();
   private static ShaderManager staticShaderManager = null;
   private static int currentProgram = -1;
   private static boolean field_148000_e = true;
   private final Map<String, Object> shaderSamplers = Maps.newHashMap();
   private final List<String> samplerNames = Lists.newArrayList();
   private final List<Integer> shaderSamplerLocations = Lists.newArrayList();
   private final List<ShaderUniform> shaderUniforms = Lists.newArrayList();
   private final List<Integer> shaderUniformLocations = Lists.newArrayList();
   private final Map<String, ShaderUniform> mappedShaderUniforms = Maps.newHashMap();
   private final int program;
   private final String programFilename;
   private final boolean useFaceCulling;
   private boolean isDirty;
   private final JsonBlendingMode field_148016_p;
   private final List<Integer> attribLocations;
   private final List<String> attributes;
   private final ShaderLoader vertexShaderLoader;
   private final ShaderLoader fragmentShaderLoader;
   private static final long a = 114777707189823L;

   public ShaderManager(IResourceManager var1, String var2) throws JsonException, IOException {
      JsonParser var6 = new JsonParser();
      ResourceLocation var7 = new ResourceLocation("shaders/program/" + var2 + ".json");
      this.programFilename = var2;
      InputStream var8 = null;

      try {
         var8 = var1.getResource(var7).getInputStream();
         JsonObject var9 = var6.parse(IOUtils.toString(var8, Charsets.UTF_8)).getAsJsonObject();
         String var31 = JsonUtils.getString(var9, "vertex");
         String var11 = JsonUtils.getString(var9, "fragment");
         JsonArray var12 = JsonUtils.getJsonArray(var9, "samplers", (JsonArray)null);
         if (var12 != null) {
            int var13 = 0;

            for (JsonElement var15 : var12) {
               try {
                  this.parseSampler(var15);
               } catch (Exception var28) {
                  JsonException var17 = JsonException.func_151379_a(var28);
                  var17.func_151380_a("samplers[" + var13 + "]");
                  throw var17;
               }

               var13++;
            }
         }

         JsonArray var32 = JsonUtils.getJsonArray(var9, "attributes", (JsonArray)null);
         if (var32 != null) {
            int var33 = 0;
            this.attribLocations = Lists.newArrayListWithCapacity(var32.size());
            this.attributes = Lists.newArrayListWithCapacity(var32.size());

            for (JsonElement var16 : var32) {
               try {
                  this.attributes.add(JsonUtils.getString(var16, "attribute"));
               } catch (Exception var27) {
                  JsonException var18 = JsonException.func_151379_a(var27);
                  var18.func_151380_a("attributes[" + var33 + "]");
                  throw var18;
               }

               var33++;
            }
         } else {
            this.attribLocations = null;
            this.attributes = null;
         }

         JsonArray var34 = JsonUtils.getJsonArray(var9, "uniforms", (JsonArray)null);
         if (var34 != null) {
            int var36 = 0;

            for (JsonElement var40 : var34) {
               try {
                  this.parseUniform(var40);
               } catch (Exception var26) {
                  JsonException var19 = JsonException.func_151379_a(var26);
                  var19.func_151380_a("uniforms[" + var36 + "]");
                  throw var19;
               }

               var36++;
            }
         }

         this.field_148016_p = JsonBlendingMode.func_148110_a(JsonUtils.getJsonObject(var9, "blend", (JsonObject)null));
         this.useFaceCulling = JsonUtils.getBoolean(var9, "cull", true);
         this.vertexShaderLoader = ShaderLoader.loadShader(var1, ShaderLoader.ShaderType.VERTEX, var31);
         this.fragmentShaderLoader = ShaderLoader.loadShader(var1, ShaderLoader.ShaderType.FRAGMENT, var11);
         this.program = ShaderLinkHelper.getStaticShaderLinkHelper().createProgram();
         ShaderLinkHelper.getStaticShaderLinkHelper().linkProgram(this);
         this.setupUniforms();
         if (this.attributes != null) {
            for (String var39 : this.attributes) {
               int var41 = OpenGlHelper.glGetAttribLocation(this.program, var39);
               this.attribLocations.add(var41);
            }
         }
      } catch (Exception var29) {
         JsonException var10 = JsonException.func_151379_a(var29);
         var10.func_151381_b(var7.getResourcePath());
         throw var10;
      } finally {
         IOUtils.closeQuietly(var8);
      }

      this.markDirty();
   }

   public void deleteShader() {
      ShaderLinkHelper.getStaticShaderLinkHelper().deleteShader(this);
   }

   public void endShader() {
      OpenGlHelper.glUseProgram(0);
      currentProgram = -1;
      staticShaderManager = null;
      field_148000_e = true;

      for (int var4 = 0; var4 < this.shaderSamplerLocations.size(); var4++) {
         if (this.shaderSamplers.get(this.samplerNames.get(var4)) != null) {
            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit + var4);
            GlStateManager.bindTexture(0);
         }
      }
   }

   public void useShader() {
      this.isDirty = false;
      staticShaderManager = this;
      this.field_148016_p.func_148109_a();
      if (this.program != currentProgram) {
         OpenGlHelper.glUseProgram(this.program);
         currentProgram = this.program;
      }

      if (this.useFaceCulling) {
         GlStateManager.enableCull();
      } else {
         GlStateManager.disableCull();
      }

      for (int var4 = 0; var4 < this.shaderSamplerLocations.size(); var4++) {
         if (this.shaderSamplers.get(this.samplerNames.get(var4)) != null) {
            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit + var4);
            GlStateManager.enableTexture2D();
            Object var5 = this.shaderSamplers.get(this.samplerNames.get(var4));
            int var6 = -1;
            if (var5 instanceof Framebuffer) {
               var6 = ((Framebuffer)var5).framebufferTexture;
            } else if (var5 instanceof ITextureObject) {
               var6 = ((ITextureObject)var5).getGlTextureId();
            } else if (var5 instanceof Integer) {
               var6 = (Integer)var5;
            }

            if (var6 != -1) {
               GlStateManager.bindTexture(var6);
               OpenGlHelper.glUniform1i(OpenGlHelper.glGetUniformLocation(this.program, this.samplerNames.get(var4)), var4);
            }
         }
      }

      for (ShaderUniform var8 : this.shaderUniforms) {
         var8.upload();
      }
   }

   public void markDirty() {
      this.isDirty = true;
   }

   public ShaderUniform getShaderUniform(String var1) {
      return this.mappedShaderUniforms.containsKey(var1) ? this.mappedShaderUniforms.get(var1) : null;
   }

   public ShaderUniform getShaderUniformOrDefault(String var1) {
      return this.mappedShaderUniforms.containsKey(var1) ? this.mappedShaderUniforms.get(var1) : defaultShaderUniform;
   }

   private void setupUniforms() {
      int var4 = 0;

      for (int var5 = 0; var4 < this.samplerNames.size(); var5++) {
         String var6 = this.samplerNames.get(var4);
         int var7 = OpenGlHelper.glGetUniformLocation(this.program, var6);
         if (var7 == -1) {
            logger.warn("Shader " + this.programFilename + "could not find sampler named " + var6 + " in the specified shader program.");
            this.shaderSamplers.remove(var6);
            this.samplerNames.remove(var5);
            var5--;
         } else {
            this.shaderSamplerLocations.add(var7);
         }

         var4++;
      }

      for (ShaderUniform var10 : this.shaderUniforms) {
         String var11 = var10.getShaderName();
         int var8 = OpenGlHelper.glGetUniformLocation(this.program, var11);
         if (var8 == -1) {
            logger.warn("Could not find uniform named " + var11 + " in the specified shader program.");
         } else {
            this.shaderUniformLocations.add(var8);
            var10.setUniformLocation(var8);
            this.mappedShaderUniforms.put(var11, var10);
         }
      }
   }

   private void parseSampler(JsonElement var1) throws JsonException {
      JsonObject var5 = JsonUtils.getJsonObject(var1, "sampler");
      String var6 = JsonUtils.getString(var5, "name");
      if (!JsonUtils.isString(var5, "file")) {
         this.shaderSamplers.put(var6, null);
         this.samplerNames.add(var6);
      } else {
         this.samplerNames.add(var6);
      }
   }

   public void addSamplerTexture(String var1, Object var2) {
      if (this.shaderSamplers.containsKey(var1)) {
         this.shaderSamplers.remove(var1);
      }

      this.shaderSamplers.put(var1, var2);
      this.markDirty();
   }

   private void parseUniform(JsonElement var1) throws JsonException {
      JsonObject var5 = JsonUtils.getJsonObject(var1, "uniform");
      String var6 = JsonUtils.getString(var5, "name");
      int var7 = ShaderUniform.parseType(JsonUtils.getString(var5, "type"));
      int var8 = JsonUtils.getInt(var5, "count");
      float[] var9 = new float[Math.max(var8, 16)];
      JsonArray var10 = JsonUtils.getJsonArray(var5, "values");
      if (var10.size() != var8 && var10.size() > 1) {
         throw new JsonException("Invalid amount of values specified (expected " + var8 + ", found " + var10.size() + ")");
      }

      int var11 = 0;

      for (JsonElement var13 : var10) {
         try {
            var9[var11] = JsonUtils.getFloat(var13, "value");
         } catch (Exception var16) {
            JsonException var15 = JsonException.func_151379_a(var16);
            var15.func_151380_a("values[" + var11 + "]");
            throw var15;
         }

         var11++;
      }

      if (var8 > 1 && var10.size() == 1) {
         while (var11 < var8) {
            var9[var11] = var9[0];
            var11++;
         }
      }

      int var17 = var8 > 1 && var8 <= 4 && var7 < 8 ? var8 - 1 : 0;
      ShaderUniform var18 = new ShaderUniform(var6, var7 + var17, var8, this);
      if (var7 <= 3) {
         var18.set((int)var9[0], (int)var9[1], (int)var9[2], (int)var9[3]);
      } else if (var7 <= 7) {
         var18.func_148092_b(var9[0], var9[1], var9[2], var9[3]);
      } else {
         var18.set(var9);
      }

      this.shaderUniforms.add(var18);
   }

   public ShaderLoader getVertexShaderLoader() {
      return this.vertexShaderLoader;
   }

   public ShaderLoader getFragmentShaderLoader() {
      return this.fragmentShaderLoader;
   }

   public int getProgram() {
      return this.program;
   }
}
