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
   private static final long a = 81778826221374L;
   private static final long g = -670197618969673722L;

   public static IBakedModel getLeavesModel(IBakedModel var0) {
      if (!Config.isTreesSmart()) {
         return var0;
      }

      List var4 = var0.getGeneralQuads();
      return var4 == generalQuadsCullAcacia
         ? modelLeavesDoubleAcacia
         : (
            var4 == generalQuadsCullBirch
               ? modelLeavesDoubleBirch
               : (
                  var4 == generalQuadsCullDarkOak
                     ? modelLeavesDoubleDarkOak
                     : (
                        var4 == generalQuadsCullJungle
                           ? modelLeavesDoubleJungle
                           : (var4 == generalQuadsCullOak ? modelLeavesDoubleOak : (var4 == generalQuadsCullSpruce ? modelLeavesDoubleSpruce : var0))
                     )
               )
         );
   }

   public static void updateLeavesModels() {
      ArrayList var3 = new ArrayList();
      modelLeavesCullAcacia = getModelCull("acacia", var3);
      modelLeavesCullBirch = getModelCull("birch", var3);
      modelLeavesCullDarkOak = getModelCull("dark_oak", var3);
      modelLeavesCullJungle = getModelCull("jungle", var3);
      modelLeavesCullOak = getModelCull("oak", var3);
      modelLeavesCullSpruce = getModelCull("spruce", var3);
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
      if (var3.size() > 0) {
         Config.dbg("Enable face culling: " + Config.arrayToString(var3.toArray()));
      }
   }

   private static List getGeneralQuadsSafe(IBakedModel var0) {
      return var0 == null ? null : var0.getGeneralQuads();
   }

   public static IBakedModel getModelCull(String var0, List var1) {
      ModelManager var5 = Config.getModelManager();
      if (var5 == null) {
         return null;
      }

      ResourceLocation var6 = new ResourceLocation("blockstates/" + var0 + "_leaves.json");
      if (Config.getDefiningResourcePack(var6) != Config.getDefaultResourcePack()) {
         return null;
      }

      ResourceLocation var7 = new ResourceLocation("models/block/" + var0 + "_leaves.json");
      if (Config.getDefiningResourcePack(var7) != Config.getDefaultResourcePack()) {
         return null;
      }

      ModelResourceLocation var8 = new ModelResourceLocation(var0 + "_leaves", "normal");
      IBakedModel var9 = var5.getModel(var8);
      if (var9 != null && var9 != var5.getMissingModel()) {
         List var10 = var9.getGeneralQuads();
         if (var10.size() == 0) {
            return var9;
         }

         if (var10.size() != (int)g) {
            return null;
         }

         for (Object var12 : var10) {
            List var13 = var9.getFaceQuads(((BakedQuad)var12).getFace());
            if (var13.size() > 0) {
               return null;
            }

            var13.add(var12);
         }

         var10.clear();
         var1.add(var0 + "_leaves");
         return var9;
      } else {
         return null;
      }
   }

   private static IBakedModel getModelDoubleFace(IBakedModel var0) {
      if (var0 == null) {
         return null;
      }

      if (var0.getGeneralQuads().size() > 0) {
         Config.warn("SmartLeaves: Model is not cube, general quads: " + var0.getGeneralQuads().size() + ", model: " + var0);
         return var0;
      }

      EnumFacing[] var4 = EnumFacing.VALUES;

      for (int var5 = 0; var5 < var4.length; var5++) {
         EnumFacing var6 = var4[var5];
         List var7 = var0.getFaceQuads(var6);
         if (var7.size() != 1) {
            Config.warn("SmartLeaves: Model is not cube, side: " + var6 + ", quads: " + var7.size() + ", model: " + var0);
            return var0;
         }
      }

      IBakedModel var15 = ModelUtils.duplicateModel(var0);
      List[] var16 = new List[var4.length];

      for (int var17 = 0; var17 < var4.length; var17++) {
         EnumFacing var8 = var4[var17];
         List var9 = var15.getFaceQuads(var8);
         BakedQuad var10 = (BakedQuad)var9.get(0);
         BakedQuad var11 = new BakedQuad((int[])var10.getVertexData().clone(), var10.getTintIndex(), var10.getFace(), var10.getSprite());
         int[] var12 = var11.getVertexData();
         int[] var13 = (int[])var12.clone();
         int var14 = var12.length / 4;
         System.arraycopy(var12, 0 * var14, var13, 3 * var14, var14);
         System.arraycopy(var12, 1 * var14, var13, 2 * var14, var14);
         System.arraycopy(var12, 2 * var14, var13, 1 * var14, var14);
         System.arraycopy(var12, 3 * var14, var13, 0 * var14, var14);
         System.arraycopy(var13, 0, var12, 0, var13.length);
         var9.add(var11);
      }

      return var15;
   }
}
