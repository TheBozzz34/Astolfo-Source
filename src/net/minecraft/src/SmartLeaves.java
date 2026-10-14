package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class SmartLeaves {
   private static IBakedModel modelLeavesCullAcacia = null;
   private static IBakedModel modelLeavesCullBirch = null;
   private static IBakedModel modelLeavesCullDarkOak = null;
   private static IBakedModel modelLeavesCullJungle = null;
   private static IBakedModel modelLeavesCullOak = null;
   private static IBakedModel modelLeavesCullSpruce = null;
   private static List generalQuadsCullAcacia = null;
   private static List generalQuadsCullBirch = null;
   private static List generalQuadsCullDarkOak = null;
   private static List generalQuadsCullJungle = null;
   private static List generalQuadsCullOak = null;
   private static List generalQuadsCullSpruce = null;
   private static IBakedModel modelLeavesDoubleAcacia = null;
   private static IBakedModel modelLeavesDoubleBirch = null;
   private static IBakedModel modelLeavesDoubleDarkOak = null;
   private static IBakedModel modelLeavesDoubleJungle = null;
   private static IBakedModel modelLeavesDoubleOak = null;
   private static IBakedModel modelLeavesDoubleSpruce = null;

   public static IBakedModel getLeavesModel(IBakedModel model) {
      if (!Config.isTreesSmart()) {
         return model;
      }

      List list = model.getGeneralQuads();
      return list == generalQuadsCullAcacia
         ? modelLeavesDoubleAcacia
         : (
            list == generalQuadsCullBirch
               ? modelLeavesDoubleBirch
               : (
                  list == generalQuadsCullDarkOak
                     ? modelLeavesDoubleDarkOak
                     : (
                        list == generalQuadsCullJungle
                           ? modelLeavesDoubleJungle
                           : (list == generalQuadsCullOak ? modelLeavesDoubleOak : (list == generalQuadsCullSpruce ? modelLeavesDoubleSpruce : model))
                     )
               )
         );
   }

   public static void updateLeavesModels() {
      ArrayList arrayList = new ArrayList();
      modelLeavesCullAcacia = getModelCull("acacia", arrayList);
      modelLeavesCullBirch = getModelCull("birch", arrayList);
      modelLeavesCullDarkOak = getModelCull("dark_oak", arrayList);
      modelLeavesCullJungle = getModelCull("jungle", arrayList);
      modelLeavesCullOak = getModelCull("oak", arrayList);
      modelLeavesCullSpruce = getModelCull("spruce", arrayList);
      generalQuadsCullAcacia = getGeneralQuadsSafe(modelLeavesCullAcacia);
      generalQuadsCullBirch = getGeneralQuadsSafe(modelLeavesCullBirch);
      generalQuadsCullDarkOak = getGeneralQuadsSafe(modelLeavesCullDarkOak);
      generalQuadsCullJungle = getGeneralQuadsSafe(modelLeavesCullJungle);
      generalQuadsCullOak = getGeneralQuadsSafe(modelLeavesCullOak);
      generalQuadsCullSpruce = getGeneralQuadsSafe(modelLeavesCullSpruce);
      modelLeavesDoubleAcacia = getModelDoubleFace(modelLeavesCullAcacia);
      modelLeavesDoubleBirch = getModelDoubleFace(modelLeavesCullBirch);
      modelLeavesDoubleDarkOak = getModelDoubleFace(modelLeavesCullDarkOak);
      modelLeavesDoubleJungle = getModelDoubleFace(modelLeavesCullJungle);
      modelLeavesDoubleOak = getModelDoubleFace(modelLeavesCullOak);
      modelLeavesDoubleSpruce = getModelDoubleFace(modelLeavesCullSpruce);
      if (arrayList.size() > 0) {
         Config.dbg("Enable face culling: " + Config.arrayToString(arrayList.toArray()));
      }
   }

   private static List getGeneralQuadsSafe(IBakedModel model) {
      return model == null ? null : model.getGeneralQuads();
   }

   public static IBakedModel getModelCull(String typeIn, List updatedTypes) {
      ModelManager modelManager = Config.getModelManager();
      if (modelManager == null) {
         return null;
      }

      ResourceLocation resourceLocation = new ResourceLocation("blockstates/" + typeIn + "_leaves.json");
      if (Config.getDefiningResourcePack(resourceLocation) != Config.getDefaultResourcePack()) {
         return null;
      }

      ResourceLocation resourceLocation2 = new ResourceLocation("models/block/" + typeIn + "_leaves.json");
      if (Config.getDefiningResourcePack(resourceLocation2) != Config.getDefaultResourcePack()) {
         return null;
      }

      ModelResourceLocation modelResourceLocation = new ModelResourceLocation(typeIn + "_leaves", "normal");
      IBakedModel iBakedModel = modelManager.getModel(modelResourceLocation);
      if (iBakedModel != null && iBakedModel != modelManager.getMissingModel()) {
         List list = iBakedModel.getGeneralQuads();
         if (list.size() == 0) {
            return iBakedModel;
         }

         if (list.size() != 6) {
            return null;
         }

         for (Object object : list) {
            List list2 = iBakedModel.getFaceQuads(((BakedQuad)object).getFace());
            if (list2.size() > 0) {
               return null;
            }

            list2.add(object);
         }

         list.clear();
         updatedTypes.add(typeIn + "_leaves");
         return iBakedModel;
      } else {
         return null;
      }
   }

   private static IBakedModel getModelDoubleFace(IBakedModel model) {
      if (model == null) {
         return null;
      }

      if (model.getGeneralQuads().size() > 0) {
         Config.warn("SmartLeaves: Model is not cube, general quads: " + model.getGeneralQuads().size() + ", model: " + model);
         return model;
      }

      EnumFacing[] enumFacings = EnumFacing.VALUES;

      for (int i = 0; i < enumFacings.length; i++) {
         EnumFacing enumFacing = enumFacings[i];
         List list = model.getFaceQuads(enumFacing);
         if (list.size() != 1) {
            Config.warn("SmartLeaves: Model is not cube, side: " + enumFacing + ", quads: " + list.size() + ", model: " + model);
            return model;
         }
      }

      IBakedModel iBakedModel = ModelUtils.duplicateModel(model);
      List[] lists = new List[enumFacings.length];

      for (int j = 0; j < enumFacings.length; j++) {
         EnumFacing enumFacing2 = enumFacings[j];
         List list2 = iBakedModel.getFaceQuads(enumFacing2);
         BakedQuad bakedQuad = (BakedQuad)list2.get(0);
         BakedQuad bakedQuad2 = new BakedQuad((int[])bakedQuad.getVertexData().clone(), bakedQuad.getTintIndex(), bakedQuad.getFace(), bakedQuad.getSprite());
         int[] ints = bakedQuad2.getVertexData();
         int[] ints2 = (int[])ints.clone();
         int k = ints.length / 4;
         System.arraycopy(ints, 0 * k, ints2, 3 * k, k);
         System.arraycopy(ints, 1 * k, ints2, 2 * k, k);
         System.arraycopy(ints, 2 * k, ints2, 1 * k, k);
         System.arraycopy(ints, 3 * k, ints2, 0 * k, k);
         System.arraycopy(ints2, 0, ints, 0, ints2.length);
         list2.add(bakedQuad2);
      }

      return iBakedModel;
   }
}
