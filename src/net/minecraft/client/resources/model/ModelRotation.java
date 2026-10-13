package net.minecraft.client.resources.model;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.src.Reflector;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.model.IModelPart;
import net.minecraftforge.client.model.IModelState;
import net.minecraftforge.client.model.ITransformation;
import net.minecraftforge.client.model.TRSRTransformation;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public enum ModelRotation implements IModelState, ITransformation {
   X0_Y0("X0_Y0", 0, 0, 0),
   X0_Y90("X0_Y90", 1, 0, 90),
   X0_Y180("X0_Y180", 2, 0, 180),
   X0_Y270("X0_Y270", 3, 0, 270),
   X90_Y0("X90_Y0", 4, 90, 0),
   X90_Y90("X90_Y90", 5, 90, 90),
   X90_Y180("X90_Y180", 6, 90, 180),
   X90_Y270("X90_Y270", 7, 90, 270),
   X180_Y0("X180_Y0", 8, 180, 0),
   X180_Y90("X180_Y90", 9, 180, 90),
   X180_Y180("X180_Y180", 10, 180, 180),
   X180_Y270("X180_Y270", 11, 180, 270),
   X270_Y0("X270_Y0", 12, 270, 0),
   X270_Y90("X270_Y90", 13, 270, 90),
   X270_Y180("X270_Y180", 14, 270, 180),
   X270_Y270("X270_Y270", 15, 270, 270);

   private static final Map<Integer, ModelRotation> mapRotations = Maps.newHashMap();
   private final int combinedXY;
   private final Matrix4f matrix4d;
   private final int quartersX;
   private final int quartersY;
   private static final ModelRotation[] u = new ModelRotation[]{
      X0_Y0, X0_Y90, X0_Y180, X0_Y270, X90_Y0, X90_Y90, X90_Y180, X90_Y270, X180_Y0, X180_Y90, X180_Y180, X180_Y270, X270_Y0, X270_Y90, X270_Y180, X270_Y270
   };
   private static final long a = 70556392233673L;

   private static int combineXY(int var0, int var1) {
      return var0 * 360 + var1;
   }

   ModelRotation(String var3, int var4, int var5, int var6) {
      this.combinedXY = combineXY(var5, var6);
      this.matrix4d = new Matrix4f();
      Matrix4f var9 = new Matrix4f();
      var9.setIdentity();
      Matrix4f.rotate(-var5 * (float) (Math.PI / 180.0), new Vector3f(1.0F, 0.0F, 0.0F), var9, var9);
      this.quartersX = MathHelper.abs_int(var5 / 90);
      Matrix4f var10 = new Matrix4f();
      var10.setIdentity();
      Matrix4f.rotate(-var6 * (float) (Math.PI / 180.0), new Vector3f(0.0F, 1.0F, 0.0F), var10, var10);
      this.quartersY = MathHelper.abs_int(var6 / 90);
      Matrix4f.mul(var10, var9, this.matrix4d);
   }

   public Matrix4f getMatrix4d() {
      return this.matrix4d;
   }

   public EnumFacing s(EnumFacing var1) {
      EnumFacing var5 = var1;

      for (int var6 = 0; var6 < this.quartersX; var6++) {
         var5 = var5.rotateAround(EnumFacing.Axis.X);
      }

      if (var5.getAxis() != EnumFacing.Axis.Y) {
         for (int var7 = 0; var7 < this.quartersY; var7++) {
            var5 = var5.rotateAround(EnumFacing.Axis.Y);
         }
      }

      return var5;
   }

   public int g(EnumFacing var1, int var2) {
      int var6 = var2;
      if (var1.getAxis() == EnumFacing.Axis.X) {
         var6 = (var2 + this.quartersX) % 4;
      }

      EnumFacing var7 = var1;

      for (int var8 = 0; var8 < this.quartersX; var8++) {
         var7 = var7.rotateAround(EnumFacing.Axis.X);
      }

      if (var7.getAxis() == EnumFacing.Axis.Y) {
         var6 = (var6 + this.quartersY) % 4;
      }

      return var6;
   }

   public static ModelRotation getModelRotation(int var0, int var1) {
      return mapRotations.get(combineXY(MathHelper.normalizeAngle(var0, 360), MathHelper.normalizeAngle(var1, 360)));
   }

   @Override
   public Optional<TRSRTransformation> apply(Optional<? extends IModelPart> var1) {
      return (Optional<TRSRTransformation>)Reflector.call(Reflector.ForgeHooksClient_applyTransform, this.m(), var1);
   }

   @Override
   public javax.vecmath.Matrix4f m() {
      return Reflector.ForgeHooksClient_getMatrix.exists()
         ? (javax.vecmath.Matrix4f)Reflector.call(Reflector.ForgeHooksClient_getMatrix, this)
         : new javax.vecmath.Matrix4f();
   }

   @Override
   public EnumFacing E(EnumFacing var1) {
      return this.s(var1);
   }

   @Override
   public int x(EnumFacing var1, int var2) {
      return this.g(var1, var2);
   }

   static {
      long var20 = a ^ 98101457055251L;
      long var10000 = 1501666566874362761L ^ var20;

      for (ModelRotation var25 : values()) {
         mapRotations.put(var25.combinedXY, var25);
      }
   }
}
