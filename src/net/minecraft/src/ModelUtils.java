package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.util.EnumFacing;

public class ModelUtils {
   public static void dbgModel(IBakedModel model) {
      if (model != null) {
         Config.dbg(
            "Model: "
               + model
               + ", ao: "
               + model.isAmbientOcclusion()
               + ", gui3d: "
               + model.isGui3d()
               + ", builtIn: "
               + model.isBuiltInRenderer()
               + ", particle: "
               + model.getParticleTexture()
         );
         EnumFacing[] enumFacings = EnumFacing.VALUES;

         for (int i = 0; i < enumFacings.length; i++) {
            EnumFacing enumFacing = enumFacings[i];
            List list = model.getFaceQuads(enumFacing);
            dbgQuads(enumFacing.getName(), list, "  ");
         }

         List list2 = model.getGeneralQuads();
         dbgQuads("General", list2, "  ");
      }
   }

   private static void dbgQuads(String name, List quads, String prefix) {
      for (Object object : quads) {
         dbgQuad(name, (BakedQuad)object, prefix);
      }
   }

   public static void dbgQuad(String name, BakedQuad quad, String prefix) {
      Config.dbg(
         prefix
            + "Quad: "
            + quad.getClass().getName()
            + ", type: "
            + name
            + ", face: "
            + quad.getFace()
            + ", tint: "
            + quad.getTintIndex()
            + ", sprite: "
            + quad.getSprite()
      );
      dbgVertexData(quad.getVertexData(), "  " + prefix);
   }

   public static void dbgVertexData(int[] vd, String prefix) {
      int i = vd.length / 4;
      Config.dbg(prefix + "Length: " + vd.length + ", step: " + i);

      for (int j = 0; j < 4; j++) {
         int k = j * i;
         float f = Float.intBitsToFloat(vd[k + 0]);
         float g = Float.intBitsToFloat(vd[k + 1]);
         float h = Float.intBitsToFloat(vd[k + 2]);
         int l = vd[k + 3];
         float f2 = Float.intBitsToFloat(vd[k + 4]);
         float f3 = Float.intBitsToFloat(vd[k + 5]);
         Config.dbg(prefix + j + " xyz: " + f + "," + g + "," + h + " col: " + l + " u,v: " + f2 + "," + f3);
      }
   }

   public static IBakedModel duplicateModel(IBakedModel model) {
      List list = duplicateQuadList(model.getGeneralQuads());
      EnumFacing[] enumFacings = EnumFacing.VALUES;
      ArrayList arrayList = new ArrayList();

      for (int i = 0; i < enumFacings.length; i++) {
         EnumFacing enumFacing = enumFacings[i];
         List list2 = model.getFaceQuads(enumFacing);
         List list3 = duplicateQuadList(list2);
         arrayList.add(list3);
      }

      return new SimpleBakedModel(list, arrayList, model.isAmbientOcclusion(), model.isGui3d(), model.getParticleTexture(), model.getItemCameraTransforms());
   }

   public static List duplicateQuadList(List list) {
      ArrayList arrayList = new ArrayList();

      for (Object object : list) {
         BakedQuad bakedQuad = duplicateQuad((BakedQuad)object);
         arrayList.add(bakedQuad);
      }

      return arrayList;
   }

   public static BakedQuad duplicateQuad(BakedQuad quad) {
      return new BakedQuad((int[])quad.getVertexData().clone(), quad.getTintIndex(), quad.getFace(), quad.getSprite());
   }
}
