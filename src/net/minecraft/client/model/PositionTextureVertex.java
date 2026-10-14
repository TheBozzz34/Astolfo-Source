package net.minecraft.client.model;

import net.minecraft.util.Vec3;

public class PositionTextureVertex {
   public Vec3 vector3D;
   public float texturePositionX;
   public float texturePositionY;

   public PositionTextureVertex(float f, float g, float h, float f2, float f3) {
      this(new Vec3(f, g, h), f2, f3);
   }

   public PositionTextureVertex setTexturePosition(float f, float g) {
      return new PositionTextureVertex(this, f, g);
   }

   public PositionTextureVertex(PositionTextureVertex textureVertex, float texturePositionXIn, float texturePositionYIn) {
      this.vector3D = textureVertex.vector3D;
      this.texturePositionX = texturePositionXIn;
      this.texturePositionY = texturePositionYIn;
   }

   public PositionTextureVertex(Vec3 vector3DIn, float texturePositionXIn, float texturePositionYIn) {
      this.vector3D = vector3DIn;
      this.texturePositionX = texturePositionXIn;
      this.texturePositionY = texturePositionYIn;
   }
}
