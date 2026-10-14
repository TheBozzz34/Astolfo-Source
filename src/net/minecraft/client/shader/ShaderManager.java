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

   public ShaderManager(IResourceManager resourceManager, String programName) throws JsonException, IOException {
      JsonParser jsonParser = new JsonParser();
      ResourceLocation resourceLocation = new ResourceLocation("shaders/program/" + programName + ".json");
      this.programFilename = programName;
      InputStream inputStream = null;

      try {
         inputStream = resourceManager.getResource(resourceLocation).getInputStream();
         JsonObject jsonObject = jsonParser.parse(IOUtils.toString(inputStream, Charsets.UTF_8)).getAsJsonObject();
         String string = JsonUtils.getString(jsonObject, "vertex");
         String string2 = JsonUtils.getString(jsonObject, "fragment");
         JsonArray jsonArray = JsonUtils.getJsonArray(jsonObject, "samplers", (JsonArray)null);
         if (jsonArray != null) {
            int i = 0;

            for (JsonElement jsonElement : jsonArray) {
               try {
                  this.parseSampler(jsonElement);
               } catch (Exception exception) {
                  JsonException jsonException = JsonException.func_151379_a(exception);
                  jsonException.func_151380_a("samplers[" + i + "]");
                  throw jsonException;
               }

               i++;
            }
         }

         JsonArray jsonArray2 = JsonUtils.getJsonArray(jsonObject, "attributes", (JsonArray)null);
         if (jsonArray2 != null) {
            int j = 0;
            this.attribLocations = Lists.newArrayListWithCapacity(jsonArray2.size());
            this.attributes = Lists.newArrayListWithCapacity(jsonArray2.size());

            for (JsonElement jsonElement2 : jsonArray2) {
               try {
                  this.attributes.add(JsonUtils.getString(jsonElement2, "attribute"));
               } catch (Exception exception2) {
                  JsonException jsonException2 = JsonException.func_151379_a(exception2);
                  jsonException2.func_151380_a("attributes[" + j + "]");
                  throw jsonException2;
               }

               j++;
            }
         } else {
            this.attribLocations = null;
            this.attributes = null;
         }

         JsonArray jsonArray3 = JsonUtils.getJsonArray(jsonObject, "uniforms", (JsonArray)null);
         if (jsonArray3 != null) {
            int k = 0;

            for (JsonElement jsonElement3 : jsonArray3) {
               try {
                  this.parseUniform(jsonElement3);
               } catch (Exception exception3) {
                  JsonException jsonException3 = JsonException.func_151379_a(exception3);
                  jsonException3.func_151380_a("uniforms[" + k + "]");
                  throw jsonException3;
               }

               k++;
            }
         }

         this.field_148016_p = JsonBlendingMode.func_148110_a(JsonUtils.getJsonObject(jsonObject, "blend", (JsonObject)null));
         this.useFaceCulling = JsonUtils.getBoolean(jsonObject, "cull", true);
         this.vertexShaderLoader = ShaderLoader.loadShader(resourceManager, ShaderLoader.ShaderType.VERTEX, string);
         this.fragmentShaderLoader = ShaderLoader.loadShader(resourceManager, ShaderLoader.ShaderType.FRAGMENT, string2);
         this.program = ShaderLinkHelper.getStaticShaderLinkHelper().createProgram();
         ShaderLinkHelper.getStaticShaderLinkHelper().linkProgram(this);
         this.setupUniforms();
         if (this.attributes != null) {
            for (String string3 : this.attributes) {
               int l = OpenGlHelper.glGetAttribLocation(this.program, string3);
               this.attribLocations.add(l);
            }
         }
      } catch (Exception exception4) {
         JsonException jsonException4 = JsonException.func_151379_a(exception4);
         jsonException4.func_151381_b(resourceLocation.getResourcePath());
         throw jsonException4;
      } finally {
         IOUtils.closeQuietly(inputStream);
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

      for (int i = 0; i < this.shaderSamplerLocations.size(); i++) {
         if (this.shaderSamplers.get(this.samplerNames.get(i)) != null) {
            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit + i);
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

      for (int i = 0; i < this.shaderSamplerLocations.size(); i++) {
         if (this.shaderSamplers.get(this.samplerNames.get(i)) != null) {
            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit + i);
            GlStateManager.enableTexture2D();
            Object object = this.shaderSamplers.get(this.samplerNames.get(i));
            int j = -1;
            if (object instanceof Framebuffer) {
               j = ((Framebuffer)object).framebufferTexture;
            } else if (object instanceof ITextureObject) {
               j = ((ITextureObject)object).getGlTextureId();
            } else if (object instanceof Integer) {
               j = (Integer)object;
            }

            if (j != -1) {
               GlStateManager.bindTexture(j);
               OpenGlHelper.glUniform1i(OpenGlHelper.glGetUniformLocation(this.program, this.samplerNames.get(i)), i);
            }
         }
      }

      for (ShaderUniform shaderUniform : this.shaderUniforms) {
         shaderUniform.upload();
      }
   }

   public void markDirty() {
      this.isDirty = true;
   }

   public ShaderUniform getShaderUniform(String string) {
      return this.mappedShaderUniforms.containsKey(string) ? this.mappedShaderUniforms.get(string) : null;
   }

   public ShaderUniform getShaderUniformOrDefault(String string) {
      return this.mappedShaderUniforms.containsKey(string) ? this.mappedShaderUniforms.get(string) : defaultShaderUniform;
   }

   private void setupUniforms() {
      int i = 0;

      for (int j = 0; i < this.samplerNames.size(); j++) {
         String string = this.samplerNames.get(i);
         int k = OpenGlHelper.glGetUniformLocation(this.program, string);
         if (k == -1) {
            logger.warn("Shader " + this.programFilename + "could not find sampler named " + string + " in the specified shader program.");
            this.shaderSamplers.remove(string);
            this.samplerNames.remove(j);
            j--;
         } else {
            this.shaderSamplerLocations.add(k);
         }

         i++;
      }

      for (ShaderUniform shaderUniform : this.shaderUniforms) {
         String string2 = shaderUniform.getShaderName();
         int l = OpenGlHelper.glGetUniformLocation(this.program, string2);
         if (l == -1) {
            logger.warn("Could not find uniform named " + string2 + " in the specified shader program.");
         } else {
            this.shaderUniformLocations.add(l);
            shaderUniform.setUniformLocation(l);
            this.mappedShaderUniforms.put(string2, shaderUniform);
         }
      }
   }

   private void parseSampler(JsonElement jsonElement) throws JsonException {
      JsonObject jsonObject = JsonUtils.getJsonObject(jsonElement, "sampler");
      String string = JsonUtils.getString(jsonObject, "name");
      if (!JsonUtils.isString(jsonObject, "file")) {
         this.shaderSamplers.put(string, null);
         this.samplerNames.add(string);
      } else {
         this.samplerNames.add(string);
      }
   }

   public void addSamplerTexture(String string, Object object) {
      if (this.shaderSamplers.containsKey(string)) {
         this.shaderSamplers.remove(string);
      }

      this.shaderSamplers.put(string, object);
      this.markDirty();
   }

   private void parseUniform(JsonElement jsonElement) throws JsonException {
      JsonObject jsonObject = JsonUtils.getJsonObject(jsonElement, "uniform");
      String string = JsonUtils.getString(jsonObject, "name");
      int i = ShaderUniform.parseType(JsonUtils.getString(jsonObject, "type"));
      int j = JsonUtils.getInt(jsonObject, "count");
      float[] floats = new float[Math.max(j, 16)];
      JsonArray jsonArray = JsonUtils.getJsonArray(jsonObject, "values");
      if (jsonArray.size() != j && jsonArray.size() > 1) {
         throw new JsonException("Invalid amount of values specified (expected " + j + ", found " + jsonArray.size() + ")");
      }

      int k = 0;

      for (JsonElement jsonElement2 : jsonArray) {
         try {
            floats[k] = JsonUtils.getFloat(jsonElement2, "value");
         } catch (Exception exception) {
            JsonException jsonException = JsonException.func_151379_a(exception);
            jsonException.func_151380_a("values[" + k + "]");
            throw jsonException;
         }

         k++;
      }

      if (j > 1 && jsonArray.size() == 1) {
         while (k < j) {
            floats[k] = floats[0];
            k++;
         }
      }

      int l = j > 1 && j <= 4 && i < 8 ? j - 1 : 0;
      ShaderUniform shaderUniform = new ShaderUniform(string, i + l, j, this);
      if (i <= 3) {
         shaderUniform.set((int)floats[0], (int)floats[1], (int)floats[2], (int)floats[3]);
      } else if (i <= 7) {
         shaderUniform.func_148092_b(floats[0], floats[1], floats[2], floats[3]);
      } else {
         shaderUniform.set(floats);
      }

      this.shaderUniforms.add(shaderUniform);
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
