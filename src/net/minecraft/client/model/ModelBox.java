package net.minecraft.client.model;

import net.minecraft.client.renderer.WorldRenderer;

public class ModelBox {
   private PositionTextureVertex[] vertexPositions;
   private TexturedQuad[] quadList;
   public final float posX1;
   public final float posY1;
   public final float posZ1;
   public final float posX2;
   public final float posY2;
   public final float posZ2;
   public String boxName;

   public ModelBox(ModelRenderer renderer, int i, int j, float f, float g, float h, int k, int l, int m, float f2) {
      this(renderer, i, j, f, g, h, k, l, m, f2, renderer.mirror);
   }

   public ModelBox(ModelRenderer renderer, int textureX, int textureY, float f, float g, float h, int i, int j, int k, float f2, boolean bl) {
      this.posX1 = f;
      this.posY1 = g;
      this.posZ1 = h;
      this.posX2 = f + i;
      this.posY2 = g + j;
      this.posZ2 = h + k;
      this.vertexPositions = new PositionTextureVertex[8];
      this.quadList = new TexturedQuad[6];
      float f3 = f + i;
      float f4 = g + j;
      float f5 = h + k;
      f -= f2;
      g -= f2;
      h -= f2;
      f3 += f2;
      f4 += f2;
      f5 += f2;
      if (bl) {
         float f6 = f3;
         f3 = f;
         f = f6;
      }

      PositionTextureVertex positionTextureVertex = new PositionTextureVertex(f, g, h, 0.0F, 0.0F);
      PositionTextureVertex positionTextureVertex2 = new PositionTextureVertex(f3, g, h, 0.0F, 8.0F);
      PositionTextureVertex positionTextureVertex3 = new PositionTextureVertex(f3, f4, h, 8.0F, 8.0F);
      PositionTextureVertex positionTextureVertex4 = new PositionTextureVertex(f, f4, h, 8.0F, 0.0F);
      PositionTextureVertex positionTextureVertex5 = new PositionTextureVertex(f, g, f5, 0.0F, 0.0F);
      PositionTextureVertex positionTextureVertex6 = new PositionTextureVertex(f3, g, f5, 0.0F, 8.0F);
      PositionTextureVertex positionTextureVertex7 = new PositionTextureVertex(f3, f4, f5, 8.0F, 8.0F);
      PositionTextureVertex positionTextureVertex8 = new PositionTextureVertex(f, f4, f5, 8.0F, 0.0F);
      this.vertexPositions[0] = positionTextureVertex;
      this.vertexPositions[1] = positionTextureVertex2;
      this.vertexPositions[2] = positionTextureVertex3;
      this.vertexPositions[3] = positionTextureVertex4;
      this.vertexPositions[4] = positionTextureVertex5;
      this.vertexPositions[5] = positionTextureVertex6;
      this.vertexPositions[6] = positionTextureVertex7;
      this.vertexPositions[7] = positionTextureVertex8;
      this.quadList[0] = new TexturedQuad(
         new PositionTextureVertex[]{positionTextureVertex6, positionTextureVertex2, positionTextureVertex3, positionTextureVertex7},
         textureX + k + i,
         textureY + k,
         textureX + k + i + k,
         textureY + k + j,
         renderer.textureWidth,
         renderer.textureHeight
      );
      this.quadList[1] = new TexturedQuad(
         new PositionTextureVertex[]{positionTextureVertex, positionTextureVertex5, positionTextureVertex8, positionTextureVertex4},
         textureX,
         textureY + k,
         textureX + k,
         textureY + k + j,
         renderer.textureWidth,
         renderer.textureHeight
      );
      this.quadList[2] = new TexturedQuad(
         new PositionTextureVertex[]{positionTextureVertex6, positionTextureVertex5, positionTextureVertex, positionTextureVertex2},
         textureX + k,
         textureY,
         textureX + k + i,
         textureY + k,
         renderer.textureWidth,
         renderer.textureHeight
      );
      this.quadList[3] = new TexturedQuad(
         new PositionTextureVertex[]{positionTextureVertex3, positionTextureVertex4, positionTextureVertex8, positionTextureVertex7},
         textureX + k + i,
         textureY + k,
         textureX + k + i + i,
         textureY,
         renderer.textureWidth,
         renderer.textureHeight
      );
      this.quadList[4] = new TexturedQuad(
         new PositionTextureVertex[]{positionTextureVertex2, positionTextureVertex, positionTextureVertex4, positionTextureVertex3},
         textureX + k,
         textureY + k,
         textureX + k + i,
         textureY + k + j,
         renderer.textureWidth,
         renderer.textureHeight
      );
      this.quadList[5] = new TexturedQuad(
         new PositionTextureVertex[]{positionTextureVertex5, positionTextureVertex6, positionTextureVertex7, positionTextureVertex8},
         textureX + k + i + k,
         textureY + k,
         textureX + k + i + k + i,
         textureY + k + j,
         renderer.textureWidth,
         renderer.textureHeight
      );
      if (bl) {
         for (int l = 0; l < this.quadList.length; l++) {
            this.quadList[l].flipFace();
         }
      }
   }

   public void render(WorldRenderer renderer, float scale) {
      for (int i = 0; i < this.quadList.length; i++) {
         this.quadList[i].draw(renderer, scale);
      }
   }

   public ModelBox setBoxName(String name) {
      this.boxName = name;
      return this;
   }
}
