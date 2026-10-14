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

   public TexturedQuad(PositionTextureVertex[] vertices) {
      this.vertexPositions = vertices;
      this.nVertices = vertices.length;
   }

   public TexturedQuad(
      PositionTextureVertex[] vertices, int texcoordU1, int texcoordV1, int texcoordU2, int texcoordV2, float textureWidth, float textureHeight
   ) {
      this(vertices);
      float f = 0.0F / textureWidth;
      float g = 0.0F / textureHeight;
      vertices[0] = vertices[0].setTexturePosition(texcoordU2 / textureWidth - f, texcoordV1 / textureHeight + g);
      vertices[1] = vertices[1].setTexturePosition(texcoordU1 / textureWidth + f, texcoordV1 / textureHeight + g);
      vertices[2] = vertices[2].setTexturePosition(texcoordU1 / textureWidth + f, texcoordV2 / textureHeight - g);
      vertices[3] = vertices[3].setTexturePosition(texcoordU2 / textureWidth - f, texcoordV2 / textureHeight - g);
   }

   public void flipFace() {
      PositionTextureVertex[] positionTextureVertexs = new PositionTextureVertex[this.vertexPositions.length];

      for (int i = 0; i < this.vertexPositions.length; i++) {
         positionTextureVertexs[i] = this.vertexPositions[this.vertexPositions.length - i - 1];
      }

      this.vertexPositions = positionTextureVertexs;
   }

   public void draw(WorldRenderer renderer, float scale) {
      Vec3 vec3 = this.vertexPositions[1].vector3D.subtractReverse(this.vertexPositions[0].vector3D);
      Vec3 vec32 = this.vertexPositions[1].vector3D.subtractReverse(this.vertexPositions[2].vector3D);
      Vec3 vec33 = vec32.crossProduct(vec3).normalize();
      float f = (float)vec33.xCoord;
      float g = (float)vec33.yCoord;
      float h = (float)vec33.zCoord;
      if (this.invertNormal) {
         f = -f;
         g = -g;
         h = -h;
      }

      if (Config.isShaders()) {
         renderer.begin(7, SVertexFormat.defVertexFormatTextured);
      } else {
         renderer.begin(7, DefaultVertexFormats.b);
      }

      for (int i = 0; i < 4; i++) {
         PositionTextureVertex positionTextureVertex = this.vertexPositions[i];
         renderer.pos(positionTextureVertex.vector3D.xCoord * scale, positionTextureVertex.vector3D.yCoord * scale, positionTextureVertex.vector3D.zCoord * scale)
            .tex(positionTextureVertex.texturePositionX, positionTextureVertex.texturePositionY)
            .normal(f, g, h)
            .endVertex();
      }

      Tessellator.getInstance().draw();
   }
}
