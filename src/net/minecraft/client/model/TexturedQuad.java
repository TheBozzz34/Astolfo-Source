package net.minecraft.client.model;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.src.Config;
import net.minecraft.util.Vec3;
import shadersmod.client.SVertexFormat;

public class TexturedQuad {
   public PositionTextureVertex[] vertexPositions;
   public int nVertices;
   private boolean invertNormal;
   private static final long a = 55687692980217L;

   public TexturedQuad(PositionTextureVertex[] var1) {
      this.vertexPositions = var1;
      this.nVertices = var1.length;
   }

   public TexturedQuad(PositionTextureVertex[] var1, int var2, int var3, int var4, int var5, float var6, float var7) {
      this(var1);
      float var8 = 0.0F / var6;
      float var9 = 0.0F / var7;
      var1[0] = var1[0].setTexturePosition(var4 / var6 - var8, var3 / var7 + var9);
      var1[1] = var1[1].setTexturePosition(var2 / var6 + var8, var3 / var7 + var9);
      var1[2] = var1[2].setTexturePosition(var2 / var6 + var8, var5 / var7 - var9);
      var1[3] = var1[3].setTexturePosition(var4 / var6 - var8, var5 / var7 - var9);
   }

   public void flipFace() {
      PositionTextureVertex[] var3 = new PositionTextureVertex[this.vertexPositions.length];

      for (int var4 = 0; var4 < this.vertexPositions.length; var4++) {
         var3[var4] = this.vertexPositions[this.vertexPositions.length - var4 - 1];
      }

      this.vertexPositions = var3;
   }

   public void draw(WorldRenderer var1, float var2) {
      Vec3 var6 = this.vertexPositions[1].vector3D.subtractReverse(this.vertexPositions[0].vector3D);
      Vec3 var7 = this.vertexPositions[1].vector3D.subtractReverse(this.vertexPositions[2].vector3D);
      Vec3 var8 = var7.crossProduct(var6).normalize();
      float var9 = (float)var8.xCoord;
      float var10 = (float)var8.yCoord;
      float var11 = (float)var8.zCoord;
      if (this.invertNormal) {
         var9 = -var9;
         var10 = -var10;
         var11 = -var11;
      }

      if (Config.isShaders()) {
         var1.begin(7, SVertexFormat.defVertexFormatTextured);
      } else {
         var1.begin(7, DefaultVertexFormats.b);
      }

      for (int var12 = 0; var12 < 4; var12++) {
         PositionTextureVertex var13 = this.vertexPositions[var12];
         var1.pos(var13.vector3D.xCoord * var2, var13.vector3D.yCoord * var2, var13.vector3D.zCoord * var2)
            .tex(var13.texturePositionX, var13.texturePositionY)
            .normal(var9, var10, var11)
            .endVertex();
      }

      Tessellator.getInstance().draw();
   }
}
