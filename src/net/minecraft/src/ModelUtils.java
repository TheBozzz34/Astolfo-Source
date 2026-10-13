package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.util.EnumFacing;

public class ModelUtils {
   private static final long a = 136646981529561L;

   public static void dbgModel(IBakedModel var0) {
      if (var0 != null) {
         Config.dbg(
            "Model: "
               + var0
               + ", ao: "
               + var0.isAmbientOcclusion()
               + ", gui3d: "
               + var0.isGui3d()
               + ", builtIn: "
               + var0.isBuiltInRenderer()
               + ", particle: "
               + var0.getParticleTexture()
         );
         EnumFacing[] var4 = EnumFacing.VALUES;

         for (int var5 = 0; var5 < var4.length; var5++) {
            EnumFacing var6 = var4[var5];
            List var7 = var0.getFaceQuads(var6);
            dbgQuads(var6.getName(), var7, "  ");
         }

         List var8 = var0.getGeneralQuads();
         dbgQuads("General", var8, "  ");
      }
   }

   private static void dbgQuads(String var0, List var1, String var2) {
      for (Object var7 : var1) {
         dbgQuad(var0, (BakedQuad)var7, var2);
      }
   }

   public static void dbgQuad(String var0, BakedQuad var1, String var2) {
      Config.dbg(
         var2
            + "Quad: "
            + var1.getClass().getName()
            + ", type: "
            + var0
            + ", face: "
            + var1.getFace()
            + ", tint: "
            + var1.getTintIndex()
            + ", sprite: "
            + var1.getSprite()
      );
      dbgVertexData(var1.getVertexData(), "  " + var2);
   }

   public static void dbgVertexData(int[] var0, String var1) {
      int var5 = var0.length / 4;
      Config.dbg(var1 + "Length: " + var0.length + ", step: " + var5);

      for (int var6 = 0; var6 < 4; var6++) {
         int var7 = var6 * var5;
         float var8 = Float.intBitsToFloat(var0[var7 + 0]);
         float var9 = Float.intBitsToFloat(var0[var7 + 1]);
         float var10 = Float.intBitsToFloat(var0[var7 + 2]);
         int var11 = var0[var7 + 3];
         float var12 = Float.intBitsToFloat(var0[var7 + 4]);
         float var13 = Float.intBitsToFloat(var0[var7 + 5]);
         Config.dbg(var1 + var6 + " xyz: " + var8 + "," + var9 + "," + var10 + " col: " + var11 + " u,v: " + var12 + "," + var13);
      }
   }

   public static IBakedModel duplicateModel(IBakedModel var0) {
      List var4 = duplicateQuadList(var0.getGeneralQuads());
      EnumFacing[] var5 = EnumFacing.VALUES;
      ArrayList var6 = new ArrayList();

      for (int var7 = 0; var7 < var5.length; var7++) {
         EnumFacing var8 = var5[var7];
         List var9 = var0.getFaceQuads(var8);
         List var10 = duplicateQuadList(var9);
         var6.add(var10);
      }

      return new SimpleBakedModel(var4, var6, var0.isAmbientOcclusion(), var0.isGui3d(), var0.getParticleTexture(), var0.getItemCameraTransforms());
   }

   public static List duplicateQuadList(List var0) {
      ArrayList var4 = new ArrayList();

      for (Object var6 : var0) {
         BakedQuad var7 = duplicateQuad((BakedQuad)var6);
         var4.add(var7);
      }

      return var4;
   }

   public static BakedQuad duplicateQuad(BakedQuad var0) {
      return new BakedQuad((int[])var0.getVertexData().clone(), var0.getTintIndex(), var0.getFace(), var0.getSprite());
   }
}
