package net.minecraft.client.shader;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.util.JsonException;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

public class ShaderGroup {
   public Framebuffer mainFramebuffer;
   private IResourceManager resourceManager;
   private String shaderGroupName;
   private final List<Shader> listShaders = Lists.newArrayList();
   private final Map<String, Framebuffer> mapFramebuffers = Maps.newHashMap();
   private final List<Framebuffer> listFramebuffers = Lists.newArrayList();
   private Matrix4f projectionMatrix;
   private int mainFramebufferWidth;
   private int mainFramebufferHeight;
   private float field_148036_j;
   private float field_148037_k;

   public ShaderGroup(TextureManager textureManager, IResourceManager iResourceManager, Framebuffer framebuffer, ResourceLocation resourceLocation) throws JsonException, IOException, JsonSyntaxException {
      this.resourceManager = iResourceManager;
      this.mainFramebuffer = framebuffer;
      this.field_148036_j = 0.0F;
      this.field_148037_k = 0.0F;
      this.mainFramebufferWidth = framebuffer.framebufferWidth;
      this.mainFramebufferHeight = framebuffer.framebufferHeight;
      this.shaderGroupName = resourceLocation.toString();
      this.resetProjectionMatrix();
      this.parseGroup(textureManager, resourceLocation);
   }

   public void parseGroup(TextureManager textureManager, ResourceLocation resourceLocation) throws JsonException, IOException, JsonSyntaxException {
      JsonParser jsonParser = new JsonParser();
      InputStream inputStream = null;

      try {
         IResource iResource = this.resourceManager.getResource(resourceLocation);
         inputStream = iResource.getInputStream();
         JsonObject jsonObject = jsonParser.parse(IOUtils.toString(inputStream, Charsets.UTF_8)).getAsJsonObject();
         if (JsonUtils.isJsonArray(jsonObject, "targets")) {
            JsonArray jsonArray = jsonObject.getAsJsonArray("targets");
            int i = 0;

            for (JsonElement jsonElement : jsonArray) {
               try {
                  this.initTarget(jsonElement);
               } catch (Exception exception) {
                  JsonException jsonException = JsonException.func_151379_a(exception);
                  jsonException.func_151380_a("targets[" + i + "]");
                  throw jsonException;
               }

               i++;
            }
         }

         if (JsonUtils.isJsonArray(jsonObject, "passes")) {
            JsonArray jsonArray2 = jsonObject.getAsJsonArray("passes");
            int j = 0;

            for (JsonElement jsonElement2 : jsonArray2) {
               try {
                  this.parsePass(textureManager, jsonElement2);
               } catch (Exception exception2) {
                  JsonException jsonException2 = JsonException.func_151379_a(exception2);
                  jsonException2.func_151380_a("passes[" + j + "]");
                  throw jsonException2;
               }

               j++;
            }
         }
      } catch (Exception exception3) {
         JsonException jsonException3 = JsonException.func_151379_a(exception3);
         jsonException3.func_151381_b(resourceLocation.getResourcePath());
         throw jsonException3;
      } finally {
         IOUtils.closeQuietly(inputStream);
      }
   }

   private void initTarget(JsonElement jsonElement) throws JsonException {
      if (JsonUtils.isString(jsonElement)) {
         this.addFramebuffer(jsonElement.getAsString(), this.mainFramebufferWidth, this.mainFramebufferHeight);
      } else {
         JsonObject jsonObject = JsonUtils.getJsonObject(jsonElement, "target");
         String string = JsonUtils.getString(jsonObject, "name");
         int i = JsonUtils.getInt(jsonObject, "width", this.mainFramebufferWidth);
         int j = JsonUtils.getInt(jsonObject, "height", this.mainFramebufferHeight);
         if (this.mapFramebuffers.containsKey(string)) {
            throw new JsonException(string + " is already defined");
         }

         this.addFramebuffer(string, i, j);
      }
   }

   private void parsePass(TextureManager textureManager, JsonElement jsonElement) throws JsonException, IOException {
      JsonObject jsonObject = JsonUtils.getJsonObject(jsonElement, "pass");
      String string = JsonUtils.getString(jsonObject, "name");
      String string2 = JsonUtils.getString(jsonObject, "intarget");
      String string3 = JsonUtils.getString(jsonObject, "outtarget");
      Framebuffer framebuffer = this.getFramebuffer(string2);
      Framebuffer framebuffer2 = this.getFramebuffer(string3);
      if (framebuffer == null) {
         throw new JsonException("Input target '" + string2 + "' does not exist");
      }

      if (framebuffer2 == null) {
         throw new JsonException("Output target '" + string3 + "' does not exist");
      }

      Shader shader = this.addShader(string, framebuffer, framebuffer2);
      JsonArray jsonArray = JsonUtils.getJsonArray(jsonObject, "auxtargets", (JsonArray)null);
      if (jsonArray != null) {
         int i = 0;

         for (JsonElement jsonElement2 : jsonArray) {
            try {
               JsonObject jsonObject2 = JsonUtils.getJsonObject(jsonElement2, "auxtarget");
               String string4 = JsonUtils.getString(jsonObject2, "name");
               String string5 = JsonUtils.getString(jsonObject2, "id");
               Framebuffer framebuffer3 = this.getFramebuffer(string5);
               if (framebuffer3 == null) {
                  ResourceLocation resourceLocation = new ResourceLocation("textures/effect/" + string5 + ".png");

                  try {
                     this.resourceManager.getResource(resourceLocation);
                  } catch (FileNotFoundException fileNotFoundException) {
                     throw new JsonException("Render target or texture '" + string5 + "' does not exist");
                  }

                  textureManager.bindTexture(resourceLocation);
                  ITextureObject iTextureObject = textureManager.getTexture(resourceLocation);
                  int j = JsonUtils.getInt(jsonObject2, "width");
                  int k = JsonUtils.getInt(jsonObject2, "height");
                  boolean bl = JsonUtils.getBoolean(jsonObject2, "bilinear");
                  if (bl) {
                     GL11.glTexParameteri(3553, 10241, 9729);
                     GL11.glTexParameteri(3553, 10240, 9729);
                  } else {
                     GL11.glTexParameteri(3553, 10241, 9728);
                     GL11.glTexParameteri(3553, 10240, 9728);
                  }

                  shader.addAuxFramebuffer(string4, iTextureObject.getGlTextureId(), j, k);
               } else {
                  shader.addAuxFramebuffer(string4, framebuffer3, framebuffer3.framebufferTextureWidth, framebuffer3.framebufferTextureHeight);
               }
            } catch (Exception exception) {
               JsonException jsonException = JsonException.func_151379_a(exception);
               jsonException.func_151380_a("auxtargets[" + i + "]");
               throw jsonException;
            }

            i++;
         }
      }

      JsonArray jsonArray2 = JsonUtils.getJsonArray(jsonObject, "uniforms", (JsonArray)null);
      if (jsonArray2 != null) {
         int l = 0;

         for (JsonElement jsonElement3 : jsonArray2) {
            try {
               this.initUniform(jsonElement3);
            } catch (Exception exception2) {
               JsonException jsonException2 = JsonException.func_151379_a(exception2);
               jsonException2.func_151380_a("uniforms[" + l + "]");
               throw jsonException2;
            }

            l++;
         }
      }
   }

   private void initUniform(JsonElement jsonElement) throws JsonException {
      JsonObject jsonObject = JsonUtils.getJsonObject(jsonElement, "uniform");
      String string = JsonUtils.getString(jsonObject, "name");
      ShaderUniform shaderUniform = this.listShaders.get(this.listShaders.size() - 1).getShaderManager().getShaderUniform(string);
      if (shaderUniform == null) {
         throw new JsonException("Uniform '" + string + "' does not exist");
      }

      float[] floats = new float[4];
      int i = 0;

      for (JsonElement jsonElement2 : JsonUtils.getJsonArray(jsonObject, "values")) {
         try {
            floats[i] = JsonUtils.getFloat(jsonElement2, "value");
         } catch (Exception exception) {
            JsonException jsonException = JsonException.func_151379_a(exception);
            jsonException.func_151380_a("values[" + i + "]");
            throw jsonException;
         }

         i++;
      }

      switch (i) {
         case 0:
         default:
            break;
         case 1:
            shaderUniform.set(floats[0]);
            break;
         case 2:
            shaderUniform.set(floats[0], floats[1]);
            break;
         case 3:
            shaderUniform.set(floats[0], floats[1], floats[2]);
            break;
         case 4:
            shaderUniform.set(floats[0], floats[1], floats[2], floats[3]);
      }
   }

   public Framebuffer getFramebufferRaw(String string) {
      return this.mapFramebuffers.get(string);
   }

   public void addFramebuffer(String string, int i, int j) {
      Framebuffer framebuffer = new Framebuffer(i, j, true);
      framebuffer.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
      this.mapFramebuffers.put(string, framebuffer);
      if (i == this.mainFramebufferWidth && j == this.mainFramebufferHeight) {
         this.listFramebuffers.add(framebuffer);
      }
   }

   public void deleteShaderGroup() {
      for (Framebuffer framebuffer : this.mapFramebuffers.values()) {
         framebuffer.deleteFramebuffer();
      }

      for (Shader shader : this.listShaders) {
         shader.deleteShader();
      }

      this.listShaders.clear();
   }

   public Shader addShader(String string, Framebuffer framebuffer, Framebuffer framebuffer2) throws JsonException, IOException {
      Shader shader = new Shader(this.resourceManager, string, framebuffer, framebuffer2);
      this.listShaders.add(this.listShaders.size(), shader);
      return shader;
   }

   private void resetProjectionMatrix() {
      this.projectionMatrix = new Matrix4f();
      this.projectionMatrix.setIdentity();
      this.projectionMatrix.m00 = 2.0F / this.mainFramebuffer.framebufferTextureWidth;
      this.projectionMatrix.m11 = 2.0F / -this.mainFramebuffer.framebufferTextureHeight;
      this.projectionMatrix.m22 = -0.0020001999F;
      this.projectionMatrix.m33 = 1.0F;
      this.projectionMatrix.m03 = -1.0F;
      this.projectionMatrix.m13 = 1.0F;
      this.projectionMatrix.m23 = -1.0001999F;
   }

   public void createBindFramebuffers(int width, int height) {
      this.mainFramebufferWidth = this.mainFramebuffer.framebufferTextureWidth;
      this.mainFramebufferHeight = this.mainFramebuffer.framebufferTextureHeight;
      this.resetProjectionMatrix();

      for (Shader shader : this.listShaders) {
         shader.setProjectionMatrix(this.projectionMatrix);
      }

      for (Framebuffer framebuffer : this.listFramebuffers) {
         framebuffer.createBindFramebuffer(width, height);
      }
   }

   public void loadShaderGroup(float partialTicks) {
      if (partialTicks < this.field_148037_k) {
         this.field_148036_j = this.field_148036_j + (1.0F - this.field_148037_k);
         this.field_148036_j += partialTicks;
      } else {
         this.field_148036_j = this.field_148036_j + (partialTicks - this.field_148037_k);
      }

      this.field_148037_k = partialTicks;

      while (this.field_148036_j > 20.0F) {
         this.field_148036_j -= 20.0F;
      }

      for (Shader shader : this.listShaders) {
         shader.loadShader(this.field_148036_j / 20.0F);
      }
   }

   public void loadShaderGroupBlended(float f) {
      if (f < this.field_148037_k) {
         this.field_148036_j = this.field_148036_j + (1.0F - this.field_148037_k);
         this.field_148036_j += f;
      } else {
         this.field_148036_j = this.field_148036_j + (f - this.field_148037_k);
      }

      this.field_148037_k = f;

      while (this.field_148036_j > 20.0F) {
         this.field_148036_j -= 20.0F;
      }

      for (Shader shader : this.listShaders) {
         shader.renderBlended(this.field_148036_j / 20.0F);
      }
   }

   public final String getShaderGroupName() {
      return this.shaderGroupName;
   }

   private Framebuffer getFramebuffer(String string) {
      return string == null ? null : (string.equals("minecraft:main") ? this.mainFramebuffer : this.mapFramebuffers.get(string));
   }

   public List<Shader> getShaders() {
      return this.listShaders;
   }
}
