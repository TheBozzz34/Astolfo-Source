package net.minecraft.client.renderer;

import bypass.module.ModuleManager;
import bypass.module.visuals.Xray;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.client.resources.model.WeightedBakedModel;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.src.Config;
import net.minecraft.src.Reflector;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import shadersmod.client.SVertexBuilder;

public class BlockRendererDispatcher implements IResourceManagerReloadListener {
   private BlockModelShapes blockModelShapes;
   private final GameSettings gameSettings;
   private final BlockModelRenderer blockModelRenderer = new BlockModelRenderer();
   private final ChestRenderer chestRenderer = new ChestRenderer();
   private final BlockFluidRenderer fluidRenderer = new BlockFluidRenderer();
   private static final long a = 83134089747619L;

   public BlockRendererDispatcher(BlockModelShapes var1, GameSettings var2) {
      this.blockModelShapes = var1;
      this.gameSettings = var2;
   }

   public BlockModelShapes getBlockModelShapes() {
      return this.blockModelShapes;
   }

   public void renderBlockDamage(IBlockState var1, BlockPos var2, TextureAtlasSprite var3, IBlockAccess var4) {
      Block var8 = var1.getBlock();
      int var9 = var8.getRenderType();
      if (var9 == 3) {
         var1 = var8.getActualState(var1, var4, var2);
         IBakedModel var10 = this.blockModelShapes.getModelForState(var1);
         if (Reflector.ISmartBlockModel.isInstance(var10)) {
            IBlockState var19 = (IBlockState)Reflector.call(var8, Reflector.ForgeBlock_getExtendedState, var1, var4, var2);

            for (EnumWorldBlockLayer var15 : EnumWorldBlockLayer.values()) {
               if (Reflector.callBoolean(var8, Reflector.ForgeBlock_canRenderInLayer, var15)) {
                  Reflector.callVoid(Reflector.ForgeHooksClient_setRenderLayer, var15);
                  IBakedModel var16 = (IBakedModel)Reflector.call(var10, Reflector.ISmartBlockModel_handleBlockState, var19);
                  IBakedModel var17 = new SimpleBakedModel.Builder(var16, var3).makeBakedModel();
                  this.blockModelRenderer.renderModel(var4, var17, var1, var2, Tessellator.getInstance().getWorldRenderer());
               }
            }

            return;
         }

         IBakedModel var11 = new SimpleBakedModel.Builder(var10, var3).makeBakedModel();
         this.blockModelRenderer.renderModel(var4, var11, var1, var2, Tessellator.getInstance().getWorldRenderer());
      }
   }

   public boolean renderBlock(IBlockState var1, BlockPos var2, IBlockAccess var3, WorldRenderer var4) {
      if (ModuleManager.s.isEnabled() && !Xray.isTranslucent() && !Xray.isEnabledOre(var1.getBlock())) {
         return false;
      }

      try {
         int var8 = var1.getBlock().getRenderType();
         if (var8 == -1) {
            return false;
         }

         switch (var8) {
            case 1:
               if (Config.isShaders()) {
                  SVertexBuilder.pushEntity(var1, var2, var3, var4);
               }

               boolean var13 = this.fluidRenderer.renderFluid(var3, var1, var2, var4);
               if (Config.isShaders()) {
                  SVertexBuilder.s(var4);
               }

               return var13;
            case 2:
               return false;
            case 3:
               IBakedModel var14 = this.getModelFromBlockState(var1, var3, var2);
               if (Config.isShaders()) {
                  SVertexBuilder.pushEntity(var1, var2, var3, var4);
               }

               boolean var11 = this.blockModelRenderer.renderModel(var3, var14, var1, var2, var4);
               if (Config.isShaders()) {
                  SVertexBuilder.s(var4);
               }

               return var11;
            default:
               return false;
         }
      } catch (Throwable var12) {
         CrashReport var9 = CrashReport.makeCrashReport(var12, "Tesselating block in world");
         CrashReportCategory var10 = var9.makeCategory("Block being tesselated");
         CrashReportCategory.addBlockInfo(var10, var2, var1.getBlock(), var1.getBlock().getMetaFromState(var1));
         throw new ReportedException(var9);
      }
   }

   public BlockModelRenderer getBlockModelRenderer() {
      return this.blockModelRenderer;
   }

   private IBakedModel getBakedModel(IBlockState var1, BlockPos var2) {
      IBakedModel var6 = this.blockModelShapes.getModelForState(var1);
      if (var2 != null && this.gameSettings.allowBlockAlternatives && var6 instanceof WeightedBakedModel) {
         var6 = ((WeightedBakedModel)var6).getAlternativeModel(MathHelper.getPositionRandom(var2));
      }

      return var6;
   }

   public IBakedModel getModelFromBlockState(IBlockState var1, IBlockAccess var2, BlockPos var3) {
      Block var7 = var1.getBlock();
      if (var2.getWorldType() != WorldType.DEBUG_WORLD) {
         try {
            var1 = var7.getActualState(var1, var2, var3);
         } catch (Exception var10) {
         }
      }

      IBakedModel var8 = this.blockModelShapes.getModelForState(var1);
      if (var3 != null && this.gameSettings.allowBlockAlternatives && var8 instanceof WeightedBakedModel) {
         var8 = ((WeightedBakedModel)var8).getAlternativeModel(MathHelper.getPositionRandom(var3));
      }

      if (Reflector.ISmartBlockModel.isInstance(var8)) {
         IBlockState var9 = (IBlockState)Reflector.call(var7, Reflector.ForgeBlock_getExtendedState, var1, var2, var3);
         var8 = (IBakedModel)Reflector.call(var8, Reflector.ISmartBlockModel_handleBlockState, var9);
      }

      return var8;
   }

   public void renderBlockBrightness(IBlockState var1, float var2) {
      int var6 = var1.getBlock().getRenderType();
      if (var6 != -1) {
         switch (var6) {
            case 1:
            default:
               break;
            case 2:
               this.chestRenderer.renderChestBrightness(var1.getBlock(), var2);
               break;
            case 3:
               IBakedModel var7 = this.getBakedModel(var1, (BlockPos)null);
               this.blockModelRenderer.renderModelBrightness(var7, var1, var2, true);
         }
      }
   }

   public boolean isRenderTypeChest(Block var1, int var2) {
      if (var1 == null) {
         return false;
      }

      int var6 = var1.getRenderType();
      return var6 == 3 ? false : var6 == 2;
   }

   @Override
   public void onResourceManagerReload(IResourceManager var1) {
      this.fluidRenderer.initAtlasSprites();
   }
}
