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

   public BlockRendererDispatcher(BlockModelShapes blockModelShapesIn, GameSettings gameSettingsIn) {
      this.blockModelShapes = blockModelShapesIn;
      this.gameSettings = gameSettingsIn;
   }

   public BlockModelShapes getBlockModelShapes() {
      return this.blockModelShapes;
   }

   public void renderBlockDamage(IBlockState state, BlockPos pos, TextureAtlasSprite texture, IBlockAccess blockAccess) {
      Block block = state.getBlock();
      int i = block.getRenderType();
      if (i == 3) {
         state = block.getActualState(state, blockAccess, pos);
         IBakedModel iBakedModel = this.blockModelShapes.getModelForState(state);
         if (Reflector.ISmartBlockModel.isInstance(iBakedModel)) {
            IBlockState iBlockState = (IBlockState)Reflector.call(block, Reflector.ForgeBlock_getExtendedState, state, blockAccess, pos);

            for (EnumWorldBlockLayer enumWorldBlockLayer : EnumWorldBlockLayer.values()) {
               if (Reflector.callBoolean(block, Reflector.ForgeBlock_canRenderInLayer, enumWorldBlockLayer)) {
                  Reflector.callVoid(Reflector.ForgeHooksClient_setRenderLayer, enumWorldBlockLayer);
                  IBakedModel iBakedModel2 = (IBakedModel)Reflector.call(iBakedModel, Reflector.ISmartBlockModel_handleBlockState, iBlockState);
                  IBakedModel iBakedModel3 = new SimpleBakedModel.Builder(iBakedModel2, texture).makeBakedModel();
                  this.blockModelRenderer.renderModel(blockAccess, iBakedModel3, state, pos, Tessellator.getInstance().getWorldRenderer());
               }
            }

            return;
         }

         IBakedModel iBakedModel4 = new SimpleBakedModel.Builder(iBakedModel, texture).makeBakedModel();
         this.blockModelRenderer.renderModel(blockAccess, iBakedModel4, state, pos, Tessellator.getInstance().getWorldRenderer());
      }
   }

   public boolean renderBlock(IBlockState state, BlockPos pos, IBlockAccess blockAccess, WorldRenderer worldRendererIn) {
      if (ModuleManager.xray.isEnabled() && !Xray.isTranslucent() && !Xray.isEnabledOre(state.getBlock())) {
         return false;
      }

      try {
         int i = state.getBlock().getRenderType();
         if (i == -1) {
            return false;
         }

         switch (i) {
            case 1:
               if (Config.isShaders()) {
                  SVertexBuilder.pushEntity(state, pos, blockAccess, worldRendererIn);
               }

               boolean bl = this.fluidRenderer.renderFluid(blockAccess, state, pos, worldRendererIn);
               if (Config.isShaders()) {
                  SVertexBuilder.s(worldRendererIn);
               }

               return bl;
            case 2:
               return false;
            case 3:
               IBakedModel iBakedModel = this.getModelFromBlockState(state, blockAccess, pos);
               if (Config.isShaders()) {
                  SVertexBuilder.pushEntity(state, pos, blockAccess, worldRendererIn);
               }

               boolean bl2 = this.blockModelRenderer.renderModel(blockAccess, iBakedModel, state, pos, worldRendererIn);
               if (Config.isShaders()) {
                  SVertexBuilder.s(worldRendererIn);
               }

               return bl2;
            default:
               return false;
         }
      } catch (Throwable throwable) {
         CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Tesselating block in world");
         CrashReportCategory crashReportCategory = crashReport.makeCategory("Block being tesselated");
         CrashReportCategory.addBlockInfo(crashReportCategory, pos, state.getBlock(), state.getBlock().getMetaFromState(state));
         throw new ReportedException(crashReport);
      }
   }

   public BlockModelRenderer getBlockModelRenderer() {
      return this.blockModelRenderer;
   }

   private IBakedModel getBakedModel(IBlockState state, BlockPos pos) {
      IBakedModel iBakedModel = this.blockModelShapes.getModelForState(state);
      if (pos != null && this.gameSettings.allowBlockAlternatives && iBakedModel instanceof WeightedBakedModel) {
         iBakedModel = ((WeightedBakedModel)iBakedModel).getAlternativeModel(MathHelper.getPositionRandom(pos));
      }

      return iBakedModel;
   }

   public IBakedModel getModelFromBlockState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      Block block = state.getBlock();
      if (worldIn.getWorldType() != WorldType.DEBUG_WORLD) {
         try {
            state = block.getActualState(state, worldIn, pos);
         } catch (Exception exception) {
         }
      }

      IBakedModel iBakedModel = this.blockModelShapes.getModelForState(state);
      if (pos != null && this.gameSettings.allowBlockAlternatives && iBakedModel instanceof WeightedBakedModel) {
         iBakedModel = ((WeightedBakedModel)iBakedModel).getAlternativeModel(MathHelper.getPositionRandom(pos));
      }

      if (Reflector.ISmartBlockModel.isInstance(iBakedModel)) {
         IBlockState iBlockState = (IBlockState)Reflector.call(block, Reflector.ForgeBlock_getExtendedState, state, worldIn, pos);
         iBakedModel = (IBakedModel)Reflector.call(iBakedModel, Reflector.ISmartBlockModel_handleBlockState, iBlockState);
      }

      return iBakedModel;
   }

   public void renderBlockBrightness(IBlockState state, float brightness) {
      int i = state.getBlock().getRenderType();
      if (i != -1) {
         switch (i) {
            case 1:
            default:
               break;
            case 2:
               this.chestRenderer.renderChestBrightness(state.getBlock(), brightness);
               break;
            case 3:
               IBakedModel iBakedModel = this.getBakedModel(state, (BlockPos)null);
               this.blockModelRenderer.renderModelBrightness(iBakedModel, state, brightness, true);
         }
      }
   }

   public boolean isRenderTypeChest(Block block, int i) {
      if (block == null) {
         return false;
      }

      int j = block.getRenderType();
      return j == 3 ? false : j == 2;
   }

   @Override
   public void onResourceManagerReload(IResourceManager resourceManager) {
      this.fluidRenderer.initAtlasSprites();
   }
}
