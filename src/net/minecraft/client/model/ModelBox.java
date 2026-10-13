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
   private static final long a = 57466946435308L;

   public ModelBox(ModelRenderer var1, int var2, int var3, float var4, float var5, float var6, int var7, int var8, int var9, float var10) {
      this(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var1.mirror);
   }

   public ModelBox(ModelRenderer var1, int var2, int var3, float var4, float var5, float var6, int var7, int var8, int var9, float var10, boolean var11) {
      this.posX1 = var4;
      this.posY1 = var5;
      this.posZ1 = var6;
      this.posX2 = var4 + var7;
      this.posY2 = var5 + var8;
      this.posZ2 = var6 + var9;
      this.vertexPositions = new PositionTextureVertex[8];
      this.quadList = new TexturedQuad[6];
      float var14 = var4 + var7;
      float var15 = var5 + var8;
      float var16 = var6 + var9;
      var4 -= var10;
      var5 -= var10;
      var6 -= var10;
      var14 += var10;
      var15 += var10;
      var16 += var10;
      if (var11) {
         float var17 = var14;
         var14 = var4;
         var4 = var17;
      }

      PositionTextureVertex var32 = new PositionTextureVertex(var4, var5, var6, 0.0F, 0.0F);
      PositionTextureVertex var18 = new PositionTextureVertex(var14, var5, var6, 0.0F, 8.0F);
      PositionTextureVertex var19 = new PositionTextureVertex(var14, var15, var6, 8.0F, 8.0F);
      PositionTextureVertex var20 = new PositionTextureVertex(var4, var15, var6, 8.0F, 0.0F);
      PositionTextureVertex var21 = new PositionTextureVertex(var4, var5, var16, 0.0F, 0.0F);
      PositionTextureVertex var22 = new PositionTextureVertex(var14, var5, var16, 0.0F, 8.0F);
      PositionTextureVertex var23 = new PositionTextureVertex(var14, var15, var16, 8.0F, 8.0F);
      PositionTextureVertex var24 = new PositionTextureVertex(var4, var15, var16, 8.0F, 0.0F);
      this.vertexPositions[0] = var32;
      this.vertexPositions[1] = var18;
      this.vertexPositions[2] = var19;
      this.vertexPositions[3] = var20;
      this.vertexPositions[4] = var21;
      this.vertexPositions[5] = var22;
      this.vertexPositions[6] = var23;
      this.vertexPositions[7] = var24;
      this.quadList[0] = new TexturedQuad(
         new PositionTextureVertex[]{var22, var18, var19, var23},
         var2 + var9 + var7,
         var3 + var9,
         var2 + var9 + var7 + var9,
         var3 + var9 + var8,
         var1.textureWidth,
         var1.textureHeight
      );
      this.quadList[1] = new TexturedQuad(
         new PositionTextureVertex[]{var32, var21, var24, var20}, var2, var3 + var9, var2 + var9, var3 + var9 + var8, var1.textureWidth, var1.textureHeight
      );
      this.quadList[2] = new TexturedQuad(
         new PositionTextureVertex[]{var22, var21, var32, var18}, var2 + var9, var3, var2 + var9 + var7, var3 + var9, var1.textureWidth, var1.textureHeight
      );
      this.quadList[3] = new TexturedQuad(
         new PositionTextureVertex[]{var19, var20, var24, var23},
         var2 + var9 + var7,
         var3 + var9,
         var2 + var9 + var7 + var7,
         var3,
         var1.textureWidth,
         var1.textureHeight
      );
      this.quadList[4] = new TexturedQuad(
         new PositionTextureVertex[]{var18, var32, var20, var19},
         var2 + var9,
         var3 + var9,
         var2 + var9 + var7,
         var3 + var9 + var8,
         var1.textureWidth,
         var1.textureHeight
      );
      this.quadList[5] = new TexturedQuad(
         new PositionTextureVertex[]{var21, var22, var23, var24},
         var2 + var9 + var7 + var9,
         var3 + var9,
         var2 + var9 + var7 + var9 + var7,
         var3 + var9 + var8,
         var1.textureWidth,
         var1.textureHeight
      );
      if (var11) {
         for (int var25 = 0; var25 < this.quadList.length; var25++) {
            this.quadList[var25].flipFace();
         }
      }
   }

   public void render(WorldRenderer var1, float var2) {
      for (int var6 = 0; var6 < this.quadList.length; var6++) {
         this.quadList[var6].draw(var1, var2);
      }
   }

   public ModelBox setBoxName(String var1) {
      this.boxName = var1;
      return this;
   }
}
