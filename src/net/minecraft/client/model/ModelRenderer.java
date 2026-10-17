package net.minecraft.client.model;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.src.ModelSprite;
import org.lwjgl.opengl.GL11;

public class ModelRenderer {
   public float textureWidth;
   public float textureHeight;
   private int textureOffsetX;
   private int textureOffsetY;
   public float rotationPointX;
   public float rotationPointY;
   public float rotationPointZ;
   public float rotateAngleX;
   public float rotateAngleY;
   public float rotateAngleZ;
   private boolean compiled;
   private int displayList;
   public boolean mirror;
   public boolean showModel;
   public boolean isHidden;
   public List<ModelBox> cubeList;
   public List<ModelRenderer> childModels;
   public final String boxName;
   private ModelBase baseModel;
   public float offsetX;
   public float offsetY;
   public float offsetZ;
   public List spriteList = new ArrayList();
   public boolean mirrorV = false;
   public float savedScale;

   public ModelRenderer(ModelBase model, String boxNameIn) {
      this.textureWidth = 64.0F;
      this.textureHeight = 32.0F;
      this.showModel = true;
      this.cubeList = Lists.newArrayList();
      this.baseModel = model;
      model.boxList.add(this);
      this.boxName = boxNameIn;
      this.setTextureSize(model.textureWidth, model.textureHeight);
   }

   public ModelRenderer(ModelBase model) {
      this(model, (String)null);
   }

   public ModelRenderer(ModelBase model, int texOffX, int texOffY) {
      this(model);
      this.setTextureOffset(texOffX, texOffY);
   }

   public void addChild(ModelRenderer renderer) {
      if (this.childModels == null) {
         this.childModels = Lists.newArrayList();
      }

      this.childModels.add(renderer);
   }

   public ModelRenderer setTextureOffset(int x, int y) {
      this.textureOffsetX = x;
      this.textureOffsetY = y;
      return this;
   }

   public ModelRenderer addBox(String partName, float offX, float offY, float offZ, int width, int height, int depth) {
      partName = this.boxName + "." + partName;
      TextureOffset textureOffset = this.baseModel.getTextureOffset(partName);
      this.setTextureOffset(textureOffset.textureOffsetX, textureOffset.textureOffsetY);
      this.cubeList.add(new ModelBox(this, this.textureOffsetX, this.textureOffsetY, offX, offY, offZ, width, height, depth, 0.0F).setBoxName(partName));
      return this;
   }

   public ModelRenderer addBox(float offX, float offY, float offZ, int width, int height, int depth) {
      this.cubeList.add(new ModelBox(this, this.textureOffsetX, this.textureOffsetY, offX, offY, offZ, width, height, depth, 0.0F));
      return this;
   }

   public ModelRenderer addBox(float f, float g, float h, int i, int j, int k, boolean bl) {
      this.cubeList.add(new ModelBox(this, this.textureOffsetX, this.textureOffsetY, f, g, h, i, j, k, 0.0F, bl));
      return this;
   }

   public void addBox(float f, float g, float h, int width, int height, int depth, float scaleFactor) {
      this.cubeList.add(new ModelBox(this, this.textureOffsetX, this.textureOffsetY, f, g, h, width, height, depth, scaleFactor));
   }

   public void setRotationPoint(float rotationPointXIn, float rotationPointYIn, float rotationPointZIn) {
      this.rotationPointX = rotationPointXIn;
      this.rotationPointY = rotationPointYIn;
      this.rotationPointZ = rotationPointZIn;
   }

   public void render(float f) {
      if (!this.isHidden && this.showModel) {
         if (!this.compiled) {
            this.compileDisplayList(f);
         }

         GlStateManager.translate(this.offsetX, this.offsetY, this.offsetZ);
         if (this.rotateAngleX == 0.0F && this.rotateAngleY == 0.0F && this.rotateAngleZ == 0.0F) {
            if (this.rotationPointX == 0.0F && this.rotationPointY == 0.0F && this.rotationPointZ == 0.0F) {
               GlStateManager.callList(this.displayList);
               if (this.childModels != null) {
                  for (int i = 0; i < this.childModels.size(); i++) {
                     this.childModels.get(i).render(f);
                  }
               }
            } else {
               GlStateManager.translate(this.rotationPointX * f, this.rotationPointY * f, this.rotationPointZ * f);
               GlStateManager.callList(this.displayList);
               if (this.childModels != null) {
                  for (int j = 0; j < this.childModels.size(); j++) {
                     this.childModels.get(j).render(f);
                  }
               }

               GlStateManager.translate(-this.rotationPointX * f, -this.rotationPointY * f, -this.rotationPointZ * f);
            }
         } else {
            GlStateManager.pushMatrix();
            GlStateManager.translate(this.rotationPointX * f, this.rotationPointY * f, this.rotationPointZ * f);
            if (this.rotateAngleZ != 0.0F) {
               GlStateManager.rotate(this.rotateAngleZ * (180.0F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
            }

            if (this.rotateAngleY != 0.0F) {
               GlStateManager.rotate(this.rotateAngleY * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
            }

            if (this.rotateAngleX != 0.0F) {
               GlStateManager.rotate(this.rotateAngleX * (180.0F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
            }

            GlStateManager.callList(this.displayList);
            if (this.childModels != null) {
               for (int k = 0; k < this.childModels.size(); k++) {
                  this.childModels.get(k).render(f);
               }
            }

            GlStateManager.popMatrix();
         }

         GlStateManager.translate(-this.offsetX, -this.offsetY, -this.offsetZ);
      }
   }

   public void renderWithRotation(float f) {
      if (!this.isHidden && this.showModel) {
         if (!this.compiled) {
            this.compileDisplayList(f);
         }

         GlStateManager.pushMatrix();
         GlStateManager.translate(this.rotationPointX * f, this.rotationPointY * f, this.rotationPointZ * f);
         if (this.rotateAngleY != 0.0F) {
            GlStateManager.rotate(this.rotateAngleY * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
         }

         if (this.rotateAngleX != 0.0F) {
            GlStateManager.rotate(this.rotateAngleX * (180.0F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
         }

         if (this.rotateAngleZ != 0.0F) {
            GlStateManager.rotate(this.rotateAngleZ * (180.0F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
         }

         GlStateManager.callList(this.displayList);
         GlStateManager.popMatrix();
      }
   }

   public void postRender(float scale) {
      if (!this.isHidden && this.showModel) {
         if (!this.compiled) {
            this.compileDisplayList(scale);
         }

         if (this.rotateAngleX != 0.0F || this.rotateAngleY != 0.0F || this.rotateAngleZ != 0.0F) {
            GlStateManager.translate(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);
            if (this.rotateAngleZ != 0.0F) {
               GlStateManager.rotate(this.rotateAngleZ * (180.0F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
            }

            if (this.rotateAngleY != 0.0F) {
               GlStateManager.rotate(this.rotateAngleY * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
            }

            if (this.rotateAngleX != 0.0F) {
               GlStateManager.rotate(this.rotateAngleX * (180.0F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
            }
         } else if (this.rotationPointX != 0.0F || this.rotationPointY != 0.0F || this.rotationPointZ != 0.0F) {
            GlStateManager.translate(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);
         }
      }
   }

   private void compileDisplayList(float scale) {
      if (this.displayList == 0) {
         this.savedScale = scale;
         this.displayList = GLAllocation.generateDisplayLists(1);
      }

      GL11.glNewList(this.displayList, 4864);
      WorldRenderer worldRenderer = Tessellator.getInstance().getWorldRenderer();

      for (int i = 0; i < this.cubeList.size(); i++) {
         this.cubeList.get(i).render(worldRenderer, scale);
      }

      for (int j = 0; j < this.spriteList.size(); j++) {
         ModelSprite modelSprite = (ModelSprite)this.spriteList.get(j);
         modelSprite.render(Tessellator.getInstance(), scale);
      }

      GL11.glEndList();
      this.compiled = true;
   }

   public ModelRenderer setTextureSize(int textureWidthIn, int textureHeightIn) {
      this.textureWidth = textureWidthIn;
      this.textureHeight = textureHeightIn;
      return this;
   }

   public void addSprite(float f, float g, float h, int i, int j, int k, float f2) {
      this.spriteList.add(new ModelSprite(this, this.textureOffsetX, this.textureOffsetY, f, g, h, i, j, k, f2));
   }

   public boolean getCompiled() {
      return this.compiled;
   }

   public int getDisplayList() {
      return this.displayList;
   }

   public void resetDisplayList() {
      if (this.compiled) {
         this.compiled = false;
         this.compileDisplayList(this.savedScale);
      }
   }
}
