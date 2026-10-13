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
   private static final long a = 111075183169828L;

   public ShaderGroup(TextureManager var1, IResourceManager var2, Framebuffer var3, ResourceLocation var4) throws JsonException, IOException, JsonSyntaxException {
      this.resourceManager = var2;
      this.mainFramebuffer = var3;
      this.field_148036_j = 0.0F;
      this.field_148037_k = 0.0F;
      this.mainFramebufferWidth = var3.framebufferWidth;
      this.mainFramebufferHeight = var3.framebufferHeight;
      this.shaderGroupName = var4.toString();
      this.resetProjectionMatrix();
      this.parseGroup(var1, var4);
   }

   public void parseGroup(TextureManager var1, ResourceLocation var2) throws JsonException, IOException, JsonSyntaxException {
      JsonParser var6 = new JsonParser();
      InputStream var7 = null;

      try {
         IResource var8 = this.resourceManager.getResource(var2);
         var7 = var8.getInputStream();
         JsonObject var25 = var6.parse(IOUtils.toString(var7, Charsets.UTF_8)).getAsJsonObject();
         if (JsonUtils.isJsonArray(var25, "targets")) {
            JsonArray var10 = var25.getAsJsonArray("targets");
            int var11 = 0;

            for (JsonElement var13 : var10) {
               try {
                  this.initTarget(var13);
               } catch (Exception var22) {
                  JsonException var15 = JsonException.func_151379_a(var22);
                  var15.func_151380_a("targets[" + var11 + "]");
                  throw var15;
               }

               var11++;
            }
         }

         if (JsonUtils.isJsonArray(var25, "passes")) {
            JsonArray var26 = var25.getAsJsonArray("passes");
            int var27 = 0;

            for (JsonElement var29 : var26) {
               try {
                  this.parsePass(var1, var29);
               } catch (Exception var21) {
                  JsonException var30 = JsonException.func_151379_a(var21);
                  var30.func_151380_a("passes[" + var27 + "]");
                  throw var30;
               }

               var27++;
            }
         }
      } catch (Exception var23) {
         JsonException var9 = JsonException.func_151379_a(var23);
         var9.func_151381_b(var2.getResourcePath());
         throw var9;
      } finally {
         IOUtils.closeQuietly(var7);
      }
   }

   private void initTarget(JsonElement var1) throws JsonException {
      if (JsonUtils.isString(var1)) {
         this.addFramebuffer(var1.getAsString(), this.mainFramebufferWidth, this.mainFramebufferHeight);
      } else {
         JsonObject var5 = JsonUtils.getJsonObject(var1, "target");
         String var6 = JsonUtils.getString(var5, "name");
         int var7 = JsonUtils.getInt(var5, "width", this.mainFramebufferWidth);
         int var8 = JsonUtils.getInt(var5, "height", this.mainFramebufferHeight);
         if (this.mapFramebuffers.containsKey(var6)) {
            throw new JsonException(var6 + " is already defined");
         }

         this.addFramebuffer(var6, var7, var8);
      }
   }

   private void parsePass(TextureManager var1, JsonElement var2) throws JsonException, IOException {
      JsonObject var6 = JsonUtils.getJsonObject(var2, "pass");
      String var7 = JsonUtils.getString(var6, "name");
      String var8 = JsonUtils.getString(var6, "intarget");
      String var9 = JsonUtils.getString(var6, "outtarget");
      Framebuffer var10 = this.getFramebuffer(var8);
      Framebuffer var11 = this.getFramebuffer(var9);
      if (var10 == null) {
         throw new JsonException("Input target '" + var8 + "' does not exist");
      }

      if (var11 == null) {
         throw new JsonException("Output target '" + var9 + "' does not exist");
      }

      Shader var12 = this.addShader(var7, var10, var11);
      JsonArray var13 = JsonUtils.getJsonArray(var6, "auxtargets", (JsonArray)null);
      if (var13 != null) {
         int var14 = 0;

         for (JsonElement var16 : var13) {
            try {
               JsonObject var17 = JsonUtils.getJsonObject(var16, "auxtarget");
               String var33 = JsonUtils.getString(var17, "name");
               String var19 = JsonUtils.getString(var17, "id");
               Framebuffer var20 = this.getFramebuffer(var19);
               if (var20 == null) {
                  ResourceLocation var21 = new ResourceLocation("textures/effect/" + var19 + ".png");

                  try {
                     this.resourceManager.getResource(var21);
                  } catch (FileNotFoundException var27) {
                     throw new JsonException("Render target or texture '" + var19 + "' does not exist");
                  }

                  var1.bindTexture(var21);
                  ITextureObject var22 = var1.getTexture(var21);
                  int var23 = JsonUtils.getInt(var17, "width");
                  int var24 = JsonUtils.getInt(var17, "height");
                  boolean var25 = JsonUtils.getBoolean(var17, "bilinear");
                  if (var25) {
                     GL11.glTexParameteri(3553, 10241, 9729);
                     GL11.glTexParameteri(3553, 10240, 9729);
                  } else {
                     GL11.glTexParameteri(3553, 10241, 9728);
                     GL11.glTexParameteri(3553, 10240, 9728);
                  }

                  var12.addAuxFramebuffer(var33, var22.getGlTextureId(), var23, var24);
               } else {
                  var12.addAuxFramebuffer(var33, var20, var20.framebufferTextureWidth, var20.framebufferTextureHeight);
               }
            } catch (Exception var28) {
               JsonException var18 = JsonException.func_151379_a(var28);
               var18.func_151380_a("auxtargets[" + var14 + "]");
               throw var18;
            }

            var14++;
         }
      }

      JsonArray var29 = JsonUtils.getJsonArray(var6, "uniforms", (JsonArray)null);
      if (var29 != null) {
         int var30 = 0;

         for (JsonElement var32 : var29) {
            try {
               this.initUniform(var32);
            } catch (Exception var26) {
               JsonException var34 = JsonException.func_151379_a(var26);
               var34.func_151380_a("uniforms[" + var30 + "]");
               throw var34;
            }

            var30++;
         }
      }
   }

   private void initUniform(JsonElement var1) throws JsonException {
      JsonObject var5 = JsonUtils.getJsonObject(var1, "uniform");
      String var6 = JsonUtils.getString(var5, "name");
      ShaderUniform var7 = this.listShaders.get(this.listShaders.size() - 1).getShaderManager().getShaderUniform(var6);
      if (var7 == null) {
         throw new JsonException("Uniform '" + var6 + "' does not exist");
      }

      float[] var8 = new float[4];
      int var9 = 0;

      for (JsonElement var11 : JsonUtils.getJsonArray(var5, "values")) {
         try {
            var8[var9] = JsonUtils.getFloat(var11, "value");
         } catch (Exception var14) {
            JsonException var13 = JsonException.func_151379_a(var14);
            var13.func_151380_a("values[" + var9 + "]");
            throw var13;
         }

         var9++;
      }

      switch (var9) {
         case 0:
         default:
            break;
         case 1:
            var7.set(var8[0]);
            break;
         case 2:
            var7.set(var8[0], var8[1]);
            break;
         case 3:
            var7.set(var8[0], var8[1], var8[2]);
            break;
         case 4:
            var7.set(var8[0], var8[1], var8[2], var8[3]);
      }
   }

   public Framebuffer getFramebufferRaw(String var1) {
      return this.mapFramebuffers.get(var1);
   }

   public void addFramebuffer(String var1, int var2, int var3) {
      Framebuffer var7 = new Framebuffer(var2, var3, true);
      var7.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
      this.mapFramebuffers.put(var1, var7);
      if (var2 == this.mainFramebufferWidth && var3 == this.mainFramebufferHeight) {
         this.listFramebuffers.add(var7);
      }
   }

   public void deleteShaderGroup() {
      for (Framebuffer var5 : this.mapFramebuffers.values()) {
         var5.deleteFramebuffer();
      }

      for (Shader var7 : this.listShaders) {
         var7.deleteShader();
      }

      this.listShaders.clear();
   }

   public Shader addShader(String var1, Framebuffer var2, Framebuffer var3) throws JsonException, IOException {
      Shader var4 = new Shader(this.resourceManager, var1, var2, var3);
      this.listShaders.add(this.listShaders.size(), var4);
      return var4;
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

   public void createBindFramebuffers(int var1, int var2) {
      this.mainFramebufferWidth = this.mainFramebuffer.framebufferTextureWidth;
      this.mainFramebufferHeight = this.mainFramebuffer.framebufferTextureHeight;
      this.resetProjectionMatrix();

      for (Shader var7 : this.listShaders) {
         var7.setProjectionMatrix(this.projectionMatrix);
      }

      for (Framebuffer var9 : this.listFramebuffers) {
         var9.createBindFramebuffer(var1, var2);
      }
   }

   public void t(float var1) {
      if (var1 < this.field_148037_k) {
         this.field_148036_j = this.field_148036_j + (1.0F - this.field_148037_k);
         this.field_148036_j += var1;
      } else {
         this.field_148036_j = this.field_148036_j + (var1 - this.field_148037_k);
      }

      this.field_148037_k = var1;

      while (this.field_148036_j > 20.0F) {
         this.field_148036_j -= 20.0F;
      }

      for (Shader var6 : this.listShaders) {
         var6.loadShader(this.field_148036_j / 20.0F);
      }
   }

   public void o(float var1) {
      if (var1 < this.field_148037_k) {
         this.field_148036_j = this.field_148036_j + (1.0F - this.field_148037_k);
         this.field_148036_j += var1;
      } else {
         this.field_148036_j = this.field_148036_j + (var1 - this.field_148037_k);
      }

      this.field_148037_k = var1;

      while (this.field_148036_j > 20.0F) {
         this.field_148036_j -= 20.0F;
      }

      for (Shader var6 : this.listShaders) {
         var6.Q(this.field_148036_j / 20.0F);
      }
   }

   public final String getShaderGroupName() {
      return this.shaderGroupName;
   }

   private Framebuffer getFramebuffer(String var1) {
      return var1 == null ? null : (var1.equals("minecraft:main") ? this.mainFramebuffer : this.mapFramebuffers.get(var1));
   }

   public List<Shader> O() {
      return this.listShaders;
   }
}
