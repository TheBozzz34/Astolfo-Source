package net.minecraft.src;

import com.google.common.base.Optional;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import javax.vecmath.Matrix4f;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.model.ModelBanner;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBat;
import net.minecraft.client.model.ModelBlaze;
import net.minecraft.client.model.ModelBook;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.model.ModelDragon;
import net.minecraft.client.model.ModelEnderCrystal;
import net.minecraft.client.model.ModelEnderMite;
import net.minecraft.client.model.ModelGhast;
import net.minecraft.client.model.ModelGuardian;
import net.minecraft.client.model.ModelHorse;
import net.minecraft.client.model.ModelHumanoidHead;
import net.minecraft.client.model.ModelLeashKnot;
import net.minecraft.client.model.ModelMagmaCube;
import net.minecraft.client.model.ModelOcelot;
import net.minecraft.client.model.ModelRabbit;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelSign;
import net.minecraft.client.model.ModelSilverfish;
import net.minecraft.client.model.ModelSkeletonHead;
import net.minecraft.client.model.ModelSlime;
import net.minecraft.client.model.ModelSquid;
import net.minecraft.client.model.ModelWitch;
import net.minecraft.client.model.ModelWither;
import net.minecraft.client.model.ModelWolf;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.entity.RenderBoat;
import net.minecraft.client.renderer.entity.RenderLeashKnot;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderMinecart;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.tileentity.RenderEnderCrystal;
import net.minecraft.client.renderer.tileentity.RenderItemFrame;
import net.minecraft.client.renderer.tileentity.RenderWitherSkull;
import net.minecraft.client.renderer.tileentity.TileEntityBannerRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityChestRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityEnchantmentTableRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityEnderChestRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySignRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemRecord;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.LongHashMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.property.IUnlistedProperty;

public class Reflector {
   private static boolean logForge = logEntry("*** Reflector Forge ***");
   public static ReflectorClass Attributes = new ReflectorClass("net.minecraftforge.client.model.Attributes");
   public static ReflectorField Attributes_DEFAULT_BAKED_FORMAT = new ReflectorField(Attributes, "DEFAULT_BAKED_FORMAT");
   public static ReflectorClass BetterFoliageClient = new ReflectorClass("mods.betterfoliage.client.BetterFoliageClient");
   public static ReflectorClass BlamingTransformer = new ReflectorClass("net.minecraftforge.fml.common.asm.transformers.BlamingTransformer");
   public static ReflectorMethod BlamingTransformer_onCrash = new ReflectorMethod(BlamingTransformer, "onCrash");
   public static ReflectorClass ChunkWatchEvent_UnWatch = new ReflectorClass("net.minecraftforge.event.world.ChunkWatchEvent$UnWatch");
   public static ReflectorConstructor ChunkWatchEvent_UnWatch_Constructor = new ReflectorConstructor(
      ChunkWatchEvent_UnWatch, new Class[]{ChunkCoordIntPair.class, EntityPlayerMP.class}
   );
   public static ReflectorClass CoreModManager = new ReflectorClass("net.minecraftforge.fml.relauncher.CoreModManager");
   public static ReflectorMethod CoreModManager_onCrash = new ReflectorMethod(CoreModManager, "onCrash");
   public static ReflectorClass DimensionManager = new ReflectorClass("net.minecraftforge.common.DimensionManager");
   public static ReflectorMethod DimensionManager_createProviderFor = new ReflectorMethod(DimensionManager, "createProviderFor");
   public static ReflectorMethod DimensionManager_getStaticDimensionIDs = new ReflectorMethod(DimensionManager, "getStaticDimensionIDs");
   public static ReflectorClass DrawScreenEvent_Pre = new ReflectorClass("net.minecraftforge.client.event.GuiScreenEvent$DrawScreenEvent$Pre");
   public static ReflectorConstructor DrawScreenEvent_Pre_Constructor = new ReflectorConstructor(
      DrawScreenEvent_Pre, new Class[]{GuiScreen.class, int.class, int.class, float.class}
   );
   public static ReflectorClass DrawScreenEvent_Post = new ReflectorClass("net.minecraftforge.client.event.GuiScreenEvent$DrawScreenEvent$Post");
   public static ReflectorConstructor DrawScreenEvent_Post_Constructor = new ReflectorConstructor(
      DrawScreenEvent_Post, new Class[]{GuiScreen.class, int.class, int.class, float.class}
   );
   public static ReflectorClass EntityViewRenderEvent_CameraSetup = new ReflectorClass("net.minecraftforge.client.event.EntityViewRenderEvent$CameraSetup");
   public static ReflectorConstructor EntityViewRenderEvent_CameraSetup_Constructor = new ReflectorConstructor(
      EntityViewRenderEvent_CameraSetup, new Class[]{EntityRenderer.class, Entity.class, Block.class, double.class, float.class, float.class, float.class}
   );
   public static ReflectorField EntityViewRenderEvent_CameraSetup_yaw = new ReflectorField(EntityViewRenderEvent_CameraSetup, "yaw");
   public static ReflectorField EntityViewRenderEvent_CameraSetup_pitch = new ReflectorField(EntityViewRenderEvent_CameraSetup, "pitch");
   public static ReflectorField EntityViewRenderEvent_CameraSetup_roll = new ReflectorField(EntityViewRenderEvent_CameraSetup, "roll");
   public static ReflectorClass EntityViewRenderEvent_FogColors = new ReflectorClass("net.minecraftforge.client.event.EntityViewRenderEvent$FogColors");
   public static ReflectorConstructor EntityViewRenderEvent_FogColors_Constructor = new ReflectorConstructor(
      EntityViewRenderEvent_FogColors, new Class[]{EntityRenderer.class, Entity.class, Block.class, double.class, float.class, float.class, float.class}
   );
   public static ReflectorField EntityViewRenderEvent_FogColors_red = new ReflectorField(EntityViewRenderEvent_FogColors, "red");
   public static ReflectorField EntityViewRenderEvent_FogColors_green = new ReflectorField(EntityViewRenderEvent_FogColors, "green");
   public static ReflectorField EntityViewRenderEvent_FogColors_blue = new ReflectorField(EntityViewRenderEvent_FogColors, "blue");
   public static ReflectorClass Event = new ReflectorClass("net.minecraftforge.fml.common.eventhandler.Event");
   public static ReflectorMethod Event_isCanceled = new ReflectorMethod(Event, "isCanceled");
   public static ReflectorClass EventBus = new ReflectorClass("net.minecraftforge.fml.common.eventhandler.EventBus");
   public static ReflectorMethod EventBus_post = new ReflectorMethod(EventBus, "post");
   public static ReflectorClass Event_Result = new ReflectorClass("net.minecraftforge.fml.common.eventhandler.Event$Result");
   public static ReflectorField Event_Result_DENY = new ReflectorField(Event_Result, "DENY");
   public static ReflectorField Event_Result_ALLOW = new ReflectorField(Event_Result, "ALLOW");
   public static ReflectorField Event_Result_DEFAULT = new ReflectorField(Event_Result, "DEFAULT");
   public static ReflectorClass ExtendedBlockState = new ReflectorClass("net.minecraftforge.common.property.ExtendedBlockState");
   public static ReflectorConstructor ExtendedBlockState_Constructor = new ReflectorConstructor(
      ExtendedBlockState, new Class[]{Block.class, IProperty[].class, IUnlistedProperty[].class}
   );
   public static ReflectorClass FMLClientHandler = new ReflectorClass("net.minecraftforge.fml.client.FMLClientHandler");
   public static ReflectorMethod FMLClientHandler_instance = new ReflectorMethod(FMLClientHandler, "instance");
   public static ReflectorMethod FMLClientHandler_isLoading = new ReflectorMethod(FMLClientHandler, "isLoading");
   public static ReflectorMethod FMLClientHandler_trackBrokenTexture = new ReflectorMethod(FMLClientHandler, "trackBrokenTexture");
   public static ReflectorMethod FMLClientHandler_trackMissingTexture = new ReflectorMethod(FMLClientHandler, "trackMissingTexture");
   public static ReflectorClass FMLCommonHandler = new ReflectorClass("net.minecraftforge.fml.common.FMLCommonHandler");
   public static ReflectorMethod FMLCommonHandler_callFuture = new ReflectorMethod(FMLCommonHandler, "callFuture");
   public static ReflectorMethod FMLCommonHandler_enhanceCrashReport = new ReflectorMethod(FMLCommonHandler, "enhanceCrashReport");
   public static ReflectorMethod FMLCommonHandler_getBrandings = new ReflectorMethod(FMLCommonHandler, "getBrandings");
   public static ReflectorMethod FMLCommonHandler_handleServerAboutToStart = new ReflectorMethod(FMLCommonHandler, "handleServerAboutToStart");
   public static ReflectorMethod FMLCommonHandler_handleServerStarting = new ReflectorMethod(FMLCommonHandler, "handleServerStarting");
   public static ReflectorMethod FMLCommonHandler_instance = new ReflectorMethod(FMLCommonHandler, "instance");
   public static ReflectorClass Nn = new ReflectorClass(BiomeGenBase.class);
   public static ReflectorMethod ForgeBiome_getWaterColorMultiplier = new ReflectorMethod(Nn, "getWaterColorMultiplier");
   public static ReflectorClass NY = new ReflectorClass(Block.class);
   public static ReflectorMethod ForgeBlock_addDestroyEffects = new ReflectorMethod(NY, "addDestroyEffects");
   public static ReflectorMethod ForgeBlock_addHitEffects = new ReflectorMethod(NY, "addHitEffects");
   public static ReflectorMethod ForgeBlock_canCreatureSpawn = new ReflectorMethod(NY, "canCreatureSpawn");
   public static ReflectorMethod ForgeBlock_canRenderInLayer = new ReflectorMethod(NY, "canRenderInLayer", new Class[]{EnumWorldBlockLayer.class});
   public static ReflectorMethod ForgeBlock_doesSideBlockRendering = new ReflectorMethod(NY, "doesSideBlockRendering");
   public static ReflectorMethod ForgeBlock_getBedDirection = new ReflectorMethod(NY, "getBedDirection");
   public static ReflectorMethod ForgeBlock_getExtendedState = new ReflectorMethod(NY, "getExtendedState");
   public static ReflectorMethod ForgeBlock_getLightOpacity = new ReflectorMethod(NY, "getLightOpacity");
   public static ReflectorMethod ForgeBlock_getLightValue = new ReflectorMethod(NY, "getLightValue");
   public static ReflectorMethod ForgeBlock_hasTileEntity = new ReflectorMethod(NY, "hasTileEntity", new Class[]{IBlockState.class});
   public static ReflectorMethod ForgeBlock_isAir = new ReflectorMethod(NY, "isAir");
   public static ReflectorMethod ForgeBlock_isBed = new ReflectorMethod(NY, "isBed");
   public static ReflectorMethod ForgeBlock_isBedFoot = new ReflectorMethod(NY, "isBedFoot");
   public static ReflectorMethod ForgeBlock_isSideSolid = new ReflectorMethod(NY, "isSideSolid");
   public static ReflectorClass BA = new ReflectorClass(Entity.class);
   public static ReflectorMethod ForgeEntity_canRiderInteract = new ReflectorMethod(BA, "canRiderInteract");
   public static ReflectorField ForgeEntity_captureDrops = new ReflectorField(BA, "captureDrops");
   public static ReflectorField ForgeEntity_capturedDrops = new ReflectorField(BA, "capturedDrops");
   public static ReflectorMethod B9 = new ReflectorMethod(BA, "shouldRenderInPass");
   public static ReflectorMethod ForgeEntity_shouldRiderSit = new ReflectorMethod(BA, "shouldRiderSit");
   public static ReflectorClass ForgeEventFactory = new ReflectorClass("net.minecraftforge.event.ForgeEventFactory");
   public static ReflectorMethod ForgeEventFactory_canEntityDespawn = new ReflectorMethod(ForgeEventFactory, "canEntityDespawn");
   public static ReflectorMethod ForgeEventFactory_canEntitySpawn = new ReflectorMethod(ForgeEventFactory, "canEntitySpawn");
   public static ReflectorMethod ForgeEventFactory_renderBlockOverlay = new ReflectorMethod(ForgeEventFactory, "renderBlockOverlay");
   public static ReflectorMethod ForgeEventFactory_renderFireOverlay = new ReflectorMethod(ForgeEventFactory, "renderFireOverlay");
   public static ReflectorMethod ForgeEventFactory_renderWaterOverlay = new ReflectorMethod(ForgeEventFactory, "renderWaterOverlay");
   public static ReflectorClass ForgeHooks = new ReflectorClass("net.minecraftforge.common.ForgeHooks");
   public static ReflectorMethod ForgeHooks_onLivingAttack = new ReflectorMethod(ForgeHooks, "onLivingAttack");
   public static ReflectorMethod ForgeHooks_onLivingDeath = new ReflectorMethod(ForgeHooks, "onLivingDeath");
   public static ReflectorMethod ForgeHooks_onLivingDrops = new ReflectorMethod(ForgeHooks, "onLivingDrops");
   public static ReflectorMethod ForgeHooks_onLivingFall = new ReflectorMethod(ForgeHooks, "onLivingFall");
   public static ReflectorMethod ForgeHooks_onLivingHurt = new ReflectorMethod(ForgeHooks, "onLivingHurt");
   public static ReflectorMethod ForgeHooks_onLivingJump = new ReflectorMethod(ForgeHooks, "onLivingJump");
   public static ReflectorMethod ForgeHooks_onLivingSetAttackTarget = new ReflectorMethod(ForgeHooks, "onLivingSetAttackTarget");
   public static ReflectorMethod ForgeHooks_onLivingUpdate = new ReflectorMethod(ForgeHooks, "onLivingUpdate");
   public static ReflectorClass ForgeHooksClient = new ReflectorClass("net.minecraftforge.client.ForgeHooksClient");
   public static ReflectorMethod ForgeHooksClient_applyTransform = new ReflectorMethod(
      ForgeHooksClient, "applyTransform", new Class[]{Matrix4f.class, Optional.class}
   );
   public static ReflectorMethod ForgeHooksClient_dispatchRenderLast = new ReflectorMethod(ForgeHooksClient, "dispatchRenderLast");
   public static ReflectorMethod ForgeHooksClient_drawScreen = new ReflectorMethod(ForgeHooksClient, "drawScreen");
   public static ReflectorMethod ForgeHooksClient_fillNormal = new ReflectorMethod(ForgeHooksClient, "fillNormal");
   public static ReflectorMethod ForgeHooksClient_handleCameraTransforms = new ReflectorMethod(ForgeHooksClient, "handleCameraTransforms");
   public static ReflectorMethod ForgeHooksClient_getArmorModel = new ReflectorMethod(ForgeHooksClient, "getArmorModel");
   public static ReflectorMethod ForgeHooksClient_getArmorTexture = new ReflectorMethod(ForgeHooksClient, "getArmorTexture");
   public static ReflectorMethod ForgeHooksClient_getFogDensity = new ReflectorMethod(ForgeHooksClient, "getFogDensity");
   public static ReflectorMethod ForgeHooksClient_getFOVModifier = new ReflectorMethod(ForgeHooksClient, "getFOVModifier");
   public static ReflectorMethod ForgeHooksClient_getMatrix = new ReflectorMethod(ForgeHooksClient, "getMatrix", new Class[]{ModelRotation.class});
   public static ReflectorMethod ForgeHooksClient_getOffsetFOV = new ReflectorMethod(ForgeHooksClient, "getOffsetFOV");
   public static ReflectorMethod ForgeHooksClient_loadEntityShader = new ReflectorMethod(ForgeHooksClient, "loadEntityShader");
   public static ReflectorMethod ForgeHooksClient_onDrawBlockHighlight = new ReflectorMethod(ForgeHooksClient, "onDrawBlockHighlight");
   public static ReflectorMethod ForgeHooksClient_onFogRender = new ReflectorMethod(ForgeHooksClient, "onFogRender");
   public static ReflectorMethod ForgeHooksClient_onTextureStitchedPre = new ReflectorMethod(ForgeHooksClient, "onTextureStitchedPre");
   public static ReflectorMethod ForgeHooksClient_onTextureStitchedPost = new ReflectorMethod(ForgeHooksClient, "onTextureStitchedPost");
   public static ReflectorMethod ForgeHooksClient_orientBedCamera = new ReflectorMethod(ForgeHooksClient, "orientBedCamera");
   public static ReflectorMethod ForgeHooksClient_putQuadColor = new ReflectorMethod(ForgeHooksClient, "putQuadColor");
   public static ReflectorMethod ForgeHooksClient_renderFirstPersonHand = new ReflectorMethod(ForgeHooksClient, "renderFirstPersonHand");
   public static ReflectorMethod ForgeHooksClient_renderMainMenu = new ReflectorMethod(ForgeHooksClient, "renderMainMenu");
   public static ReflectorMethod ForgeHooksClient_setRenderLayer = new ReflectorMethod(ForgeHooksClient, "setRenderLayer");
   public static ReflectorMethod ForgeHooksClient_setRenderPass = new ReflectorMethod(ForgeHooksClient, "setRenderPass");
   public static ReflectorMethod ForgeHooksClient_transform = new ReflectorMethod(ForgeHooksClient, "transform");
   public static ReflectorClass D = new ReflectorClass(Item.class);
   public static ReflectorMethod ForgeItem_getDurabilityForDisplay = new ReflectorMethod(D, "getDurabilityForDisplay");
   public static ReflectorMethod ForgeItem_getModel = new ReflectorMethod(D, "getModel");
   public static ReflectorMethod ForgeItem_onEntitySwing = new ReflectorMethod(D, "onEntitySwing");
   public static ReflectorMethod ForgeItem_shouldCauseReequipAnimation = new ReflectorMethod(D, "shouldCauseReequipAnimation");
   public static ReflectorMethod ForgeItem_showDurabilityBar = new ReflectorMethod(D, "showDurabilityBar");
   public static ReflectorClass uz = new ReflectorClass(ItemRecord.class);
   public static ReflectorMethod ForgeItemRecord_getRecordResource = new ReflectorMethod(uz, "getRecordResource", new Class[]{String.class});
   public static ReflectorClass ForgeModContainer = new ReflectorClass("net.minecraftforge.common.ForgeModContainer");
   public static ReflectorField ForgeModContainer_forgeLightPipelineEnabled = new ReflectorField(ForgeModContainer, "forgeLightPipelineEnabled");
   public static ReflectorClass w = new ReflectorClass(PotionEffect.class);
   public static ReflectorMethod ForgePotionEffect_isCurativeItem = new ReflectorMethod(w, "isCurativeItem");
   public static ReflectorClass kc = new ReflectorClass(TileEntity.class);
   public static ReflectorMethod ForgeTileEntity_canRenderBreaking = new ReflectorMethod(kc, "canRenderBreaking");
   public static ReflectorMethod ForgeTileEntity_getRenderBoundingBox = new ReflectorMethod(kc, "getRenderBoundingBox");
   public static ReflectorMethod ForgeTileEntity_hasFastRenderer = new ReflectorMethod(kc, "hasFastRenderer");
   public static ReflectorMethod kC = new ReflectorMethod(kc, "shouldRenderInPass");
   public static ReflectorClass Bl = new ReflectorClass(TileEntityRendererDispatcher.class);
   public static ReflectorMethod ForgeTileEntityRendererDispatcher_preDrawBatch = new ReflectorMethod(Bl, "preDrawBatch");
   public static ReflectorMethod ForgeTileEntityRendererDispatcher_drawBatch = new ReflectorMethod(Bl, "drawBatch");
   public static ReflectorClass O = new ReflectorClass(VertexFormatElement.EnumUsage.class);
   public static ReflectorMethod ForgeVertexFormatElementEnumUseage_preDraw = new ReflectorMethod(O, "preDraw");
   public static ReflectorMethod ForgeVertexFormatElementEnumUseage_postDraw = new ReflectorMethod(O, "postDraw");
   public static ReflectorClass NX = new ReflectorClass(World.class);
   public static ReflectorMethod ForgeWorld_countEntities = new ReflectorMethod(NX, "countEntities", new Class[]{EnumCreatureType.class, boolean.class});
   public static ReflectorMethod ForgeWorld_getPerWorldStorage = new ReflectorMethod(NX, "getPerWorldStorage");
   public static ReflectorClass NE = new ReflectorClass(WorldProvider.class);
   public static ReflectorMethod ForgeWorldProvider_getCloudRenderer = new ReflectorMethod(NE, "getCloudRenderer");
   public static ReflectorMethod ForgeWorldProvider_getSkyRenderer = new ReflectorMethod(NE, "getSkyRenderer");
   public static ReflectorMethod ForgeWorldProvider_getWeatherRenderer = new ReflectorMethod(NE, "getWeatherRenderer");
   public static ReflectorClass GuiModList = new ReflectorClass("net.minecraftforge.fml.client.GuiModList");
   public static ReflectorConstructor GuiModList_Constructor = new ReflectorConstructor(GuiModList, new Class[]{GuiScreen.class});
   public static ReflectorClass IColoredBakedQuad = new ReflectorClass("net.minecraftforge.client.model.IColoredBakedQuad");
   public static ReflectorClass IExtendedBlockState = new ReflectorClass("net.minecraftforge.common.property.IExtendedBlockState");
   public static ReflectorMethod IExtendedBlockState_getClean = new ReflectorMethod(IExtendedBlockState, "getClean");
   public static ReflectorClass IRenderHandler = new ReflectorClass("net.minecraftforge.client.IRenderHandler");
   public static ReflectorMethod IRenderHandler_render = new ReflectorMethod(IRenderHandler, "render");
   public static ReflectorClass ISmartBlockModel = new ReflectorClass("net.minecraftforge.client.model.ISmartBlockModel");
   public static ReflectorMethod ISmartBlockModel_handleBlockState = new ReflectorMethod(ISmartBlockModel, "handleBlockState");
   public static ReflectorClass ItemModelMesherForge = new ReflectorClass("net.minecraftforge.client.ItemModelMesherForge");
   public static ReflectorConstructor ItemModelMesherForge_Constructor = new ReflectorConstructor(ItemModelMesherForge, new Class[]{ModelManager.class});
   public static ReflectorClass Launch = new ReflectorClass("net.minecraft.launchwrapper.Launch");
   public static ReflectorField Launch_blackboard = new ReflectorField(Launch, "blackboard");
   public static ReflectorClass LightUtil = new ReflectorClass("net.minecraftforge.client.model.pipeline.LightUtil");
   public static ReflectorField LightUtil_itemConsumer = new ReflectorField(LightUtil, "itemConsumer");
   public static ReflectorMethod LightUtil_putBakedQuad = new ReflectorMethod(LightUtil, "putBakedQuad");
   public static ReflectorMethod LightUtil_renderQuadColor = new ReflectorMethod(LightUtil, "renderQuadColor");
   public static ReflectorField LightUtil_tessellator = new ReflectorField(LightUtil, "tessellator");
   public static ReflectorClass MinecraftForge = new ReflectorClass("net.minecraftforge.common.MinecraftForge");
   public static ReflectorField MinecraftForge_EVENT_BUS = new ReflectorField(MinecraftForge, "EVENT_BUS");
   public static ReflectorClass MinecraftForgeClient = new ReflectorClass("net.minecraftforge.client.MinecraftForgeClient");
   public static ReflectorMethod MinecraftForgeClient_getRenderPass = new ReflectorMethod(MinecraftForgeClient, "getRenderPass");
   public static ReflectorMethod MinecraftForgeClient_onRebuildChunk = new ReflectorMethod(MinecraftForgeClient, "onRebuildChunk");
   public static ReflectorClass ModelLoader = new ReflectorClass("net.minecraftforge.client.model.ModelLoader");
   public static ReflectorMethod ModelLoader_onRegisterItems = new ReflectorMethod(ModelLoader, "onRegisterItems");
   public static ReflectorClass RenderBlockOverlayEvent_OverlayType = new ReflectorClass("net.minecraftforge.client.event.RenderBlockOverlayEvent$OverlayType");
   public static ReflectorField RenderBlockOverlayEvent_OverlayType_BLOCK = new ReflectorField(RenderBlockOverlayEvent_OverlayType, "BLOCK");
   public static ReflectorClass RenderingRegistry = new ReflectorClass("net.minecraftforge.fml.client.registry.RenderingRegistry");
   public static ReflectorMethod RenderingRegistry_loadEntityRenderers = new ReflectorMethod(
      RenderingRegistry, "loadEntityRenderers", new Class[]{RenderManager.class, Map.class}
   );
   public static ReflectorClass RenderItemInFrameEvent = new ReflectorClass("net.minecraftforge.client.event.RenderItemInFrameEvent");
   public static ReflectorConstructor RenderItemInFrameEvent_Constructor = new ReflectorConstructor(
      RenderItemInFrameEvent, new Class[]{EntityItemFrame.class, RenderItemFrame.class}
   );
   public static ReflectorClass RenderLivingEvent_Pre = new ReflectorClass("net.minecraftforge.client.event.RenderLivingEvent$Pre");
   public static ReflectorConstructor RenderLivingEvent_Pre_Constructor = new ReflectorConstructor(
      RenderLivingEvent_Pre, new Class[]{EntityLivingBase.class, RendererLivingEntity.class, double.class, double.class, double.class}
   );
   public static ReflectorClass RenderLivingEvent_Post = new ReflectorClass("net.minecraftforge.client.event.RenderLivingEvent$Post");
   public static ReflectorConstructor RenderLivingEvent_Post_Constructor = new ReflectorConstructor(
      RenderLivingEvent_Post, new Class[]{EntityLivingBase.class, RendererLivingEntity.class, double.class, double.class, double.class}
   );
   public static ReflectorClass RenderLivingEvent_Specials_Pre = new ReflectorClass("net.minecraftforge.client.event.RenderLivingEvent$Specials$Pre");
   public static ReflectorConstructor RenderLivingEvent_Specials_Pre_Constructor = new ReflectorConstructor(
      RenderLivingEvent_Specials_Pre, new Class[]{EntityLivingBase.class, RendererLivingEntity.class, double.class, double.class, double.class}
   );
   public static ReflectorClass RenderLivingEvent_Specials_Post = new ReflectorClass("net.minecraftforge.client.event.RenderLivingEvent$Specials$Post");
   public static ReflectorConstructor RenderLivingEvent_Specials_Post_Constructor = new ReflectorConstructor(
      RenderLivingEvent_Specials_Post, new Class[]{EntityLivingBase.class, RendererLivingEntity.class, double.class, double.class, double.class}
   );
   public static ReflectorClass SplashScreen = new ReflectorClass("net.minecraftforge.fml.client.SplashProgress");
   public static ReflectorClass WorldEvent_Load = new ReflectorClass("net.minecraftforge.event.world.WorldEvent$Load");
   public static ReflectorConstructor WorldEvent_Load_Constructor = new ReflectorConstructor(WorldEvent_Load, new Class[]{World.class});
   private static boolean logVanilla = logEntry("*** Reflector Vanilla ***");
   public static ReflectorClass uC = new ReflectorClass(ChunkProviderClient.class);
   public static ReflectorField kI = new ReflectorField(uC, LongHashMap.class);
   public static ReflectorClass Nt = new ReflectorClass(GuiMainMenu.class);
   public static ReflectorField k7 = new ReflectorField(Nt, String.class);
   public static ReflectorClass BB = new ReflectorClass(Minecraft.class);
   public static ReflectorField Minecraft_defaultResourcePack = new ReflectorField(BB, DefaultResourcePack.class);
   public static ReflectorClass Bn = new ReflectorClass(ModelHumanoidHead.class);
   public static ReflectorField kS = new ReflectorField(Bn, ModelRenderer.class);
   public static ReflectorClass kR = new ReflectorClass(ModelBat.class);
   public static ReflectorFields ModelBat_ModelRenderers = new ReflectorFields(kR, ModelRenderer.class, 6);
   public static ReflectorClass H = new ReflectorClass(ModelBlaze.class);
   public static ReflectorField B2 = new ReflectorField(H, ModelRenderer.class);
   public static ReflectorField k = new ReflectorField(H, ModelRenderer[].class);
   public static ReflectorClass ND = new ReflectorClass(ModelDragon.class);
   public static ReflectorFields ModelDragon_ModelRenderers = new ReflectorFields(ND, ModelRenderer.class, 12);
   public static ReflectorClass BF = new ReflectorClass(ModelEnderCrystal.class);
   public static ReflectorFields ModelEnderCrystal_ModelRenderers = new ReflectorFields(BF, ModelRenderer.class, 3);
   public static ReflectorClass B_ = new ReflectorClass(RenderEnderCrystal.class);
   public static ReflectorField BT = new ReflectorField(B_, ModelBase.class, 0);
   public static ReflectorClass uI = new ReflectorClass(ModelEnderMite.class);
   public static ReflectorField F = new ReflectorField(uI, ModelRenderer[].class);
   public static ReflectorClass kp = new ReflectorClass(ModelGhast.class);
   public static ReflectorField NC = new ReflectorField(kp, ModelRenderer.class);
   public static ReflectorField kN = new ReflectorField(kp, ModelRenderer[].class);
   public static ReflectorClass Bs = new ReflectorClass(ModelGuardian.class);
   public static ReflectorField f = new ReflectorField(Bs, ModelRenderer.class, 0);
   public static ReflectorField N4 = new ReflectorField(Bs, ModelRenderer.class, 1);
   public static ReflectorField k6 = new ReflectorField(Bs, ModelRenderer[].class, 0);
   public static ReflectorField BD = new ReflectorField(Bs, ModelRenderer[].class, 1);
   public static ReflectorClass NK = new ReflectorClass(ModelHorse.class);
   public static ReflectorFields ModelHorse_ModelRenderers = new ReflectorFields(NK, ModelRenderer.class, 39);
   public static ReflectorClass NF = new ReflectorClass(RenderLeashKnot.class);
   public static ReflectorField B5 = new ReflectorField(NF, ModelLeashKnot.class);
   public static ReflectorClass ky = new ReflectorClass(ModelMagmaCube.class);
   public static ReflectorField ui = new ReflectorField(ky, ModelRenderer.class);
   public static ReflectorField kZ = new ReflectorField(ky, ModelRenderer[].class);
   public static ReflectorClass U = new ReflectorClass(ModelOcelot.class);
   public static ReflectorFields ModelOcelot_ModelRenderers = new ReflectorFields(U, ModelRenderer.class, 8);
   public static ReflectorClass u6 = new ReflectorClass(ModelRabbit.class);
   public static ReflectorFields ModelRabbit_renderers = new ReflectorFields(u6, ModelRenderer.class, 12);
   public static ReflectorClass BI = new ReflectorClass(ModelSilverfish.class);
   public static ReflectorField kY = new ReflectorField(BI, ModelRenderer[].class, 0);
   public static ReflectorField Bv = new ReflectorField(BI, ModelRenderer[].class, 1);
   public static ReflectorClass uq = new ReflectorClass(ModelSlime.class);
   public static ReflectorFields ModelSlime_ModelRenderers = new ReflectorFields(uq, ModelRenderer.class, 4);
   public static ReflectorClass Na = new ReflectorClass(ModelSquid.class);
   public static ReflectorField kd = new ReflectorField(Na, ModelRenderer.class);
   public static ReflectorField Nl = new ReflectorField(Na, ModelRenderer[].class);
   public static ReflectorClass Nb = new ReflectorClass(ModelWitch.class);
   public static ReflectorField BZ = new ReflectorField(Nb, ModelRenderer.class, 0);
   public static ReflectorField Bm = new ReflectorField(Nb, ModelRenderer.class, 1);
   public static ReflectorClass kT = new ReflectorClass(ModelWither.class);
   public static ReflectorField I = new ReflectorField(kT, ModelRenderer[].class, 0);
   public static ReflectorField Nw = new ReflectorField(kT, ModelRenderer[].class, 1);
   public static ReflectorClass BY = new ReflectorClass(ModelWolf.class);
   public static ReflectorField kk = new ReflectorField(BY, ModelRenderer.class, 6);
   public static ReflectorField N_ = new ReflectorField(BY, ModelRenderer.class, 7);
   public static ReflectorClass OptiFineClassTransformer = new ReflectorClass("optifine.OptiFineClassTransformer");
   public static ReflectorField OptiFineClassTransformer_instance = new ReflectorField(OptiFineClassTransformer, "instance");
   public static ReflectorMethod OptiFineClassTransformer_getOptiFineResource = new ReflectorMethod(OptiFineClassTransformer, "getOptiFineResource");
   public static ReflectorClass BM = new ReflectorClass(RenderBoat.class);
   public static ReflectorField uv = new ReflectorField(BM, ModelBase.class);
   public static ReflectorClass ke = new ReflectorClass(RenderMinecart.class);
   public static ReflectorField kb = new ReflectorField(ke, ModelBase.class);
   public static ReflectorClass kQ = new ReflectorClass(RenderWitherSkull.class);
   public static ReflectorField uB = new ReflectorField(kQ, ModelSkeletonHead.class);
   public static ReflectorClass kV = new ReflectorClass(ResourcePackRepository.class);
   public static ReflectorField ResourcePackRepository_repositoryEntries = new ReflectorField(kV, List.class, 1);
   public static ReflectorClass ut = new ReflectorClass(TileEntityBannerRenderer.class);
   public static ReflectorField km = new ReflectorField(ut, ModelBanner.class);
   public static ReflectorClass kf = new ReflectorClass(TileEntityChestRenderer.class);
   public static ReflectorField Nz = new ReflectorField(kf, ModelChest.class, 0);
   public static ReflectorField Nx = new ReflectorField(kf, ModelChest.class, 1);
   public static ReflectorClass p = new ReflectorClass(TileEntityEnchantmentTableRenderer.class);
   public static ReflectorField BJ = new ReflectorField(p, ModelBook.class);
   public static ReflectorClass C = new ReflectorClass(TileEntityEnderChestRenderer.class);
   public static ReflectorField z = new ReflectorField(C, ModelChest.class);
   public static ReflectorClass u = new ReflectorClass(TileEntitySignRenderer.class);
   public static ReflectorField NN = new ReflectorField(u, ModelSign.class);
   public static ReflectorClass T = new ReflectorClass(TileEntitySkullRenderer.class);
   public static ReflectorField k9 = new ReflectorField(T, ModelSkeletonHead.class, 0);
   public static ReflectorField Nf = new ReflectorField(T, ModelSkeletonHead.class, 1);
   public static ReflectorField NW = new ReflectorField(D, "delegate");
   public static ReflectorClass Bh = new ReflectorClass(ModelBlock.class);
   public static ReflectorField ko = new ReflectorField(Bh, Map.class);
   public static ReflectorField um = new ReflectorField(Bh, ResourceLocation.class);
   public static ReflectorMethod BW = new ReflectorMethod(ModelLoader, "getInventoryVariant");
   private static final long ab = 78019759156624L;

   public static void callVoid(ReflectorMethod refMethod, Object... params) {
      try {
         Method method = refMethod.getTargetMethod();
         if (method == null) {
            return;
         }

         method.invoke(null, params);
      } catch (Throwable throwable) {
         handleException(throwable, null, refMethod, params);
      }
   }

   public static boolean callBoolean(ReflectorMethod refMethod, Object... params) {
      try {
         Method method = refMethod.getTargetMethod();
         if (method == null) {
            return false;
         }

         Boolean boolean2 = (Boolean)method.invoke(null, params);
         return boolean2;
      } catch (Throwable throwable) {
         handleException(throwable, null, refMethod, params);
         return false;
      }
   }

   public static int callInt(ReflectorMethod refMethod, Object... params) {
      try {
         Method method = refMethod.getTargetMethod();
         if (method == null) {
            return 0;
         }

         Integer integer = (Integer)method.invoke(null, params);
         return integer;
      } catch (Throwable throwable) {
         handleException(throwable, null, refMethod, params);
         return 0;
      }
   }

   public static float callFloat(ReflectorMethod refMethod, Object... params) {
      try {
         Method method = refMethod.getTargetMethod();
         if (method == null) {
            return 0.0F;
         }

         Float float2 = (Float)method.invoke(null, params);
         return float2;
      } catch (Throwable throwable) {
         handleException(throwable, null, refMethod, params);
         return 0.0F;
      }
   }

   public static double callDouble(ReflectorMethod refMethod, Object... params) {
      try {
         Method method = refMethod.getTargetMethod();
         if (method == null) {
            return 0.0;
         }

         Double double2 = (Double)method.invoke(null, params);
         return double2;
      } catch (Throwable throwable) {
         handleException(throwable, null, refMethod, params);
         return 0.0;
      }
   }

   public static String callString(ReflectorMethod refMethod, Object... params) {
      try {
         Method method = refMethod.getTargetMethod();
         return method == null ? null : (String)method.invoke(null, params);
      } catch (Throwable throwable) {
         handleException(throwable, null, refMethod, params);
         return null;
      }
   }

   public static Object call(ReflectorMethod refMethod, Object... params) {
      try {
         Method method = refMethod.getTargetMethod();
         return method == null ? null : method.invoke(null, params);
      } catch (Throwable throwable) {
         handleException(throwable, null, refMethod, params);
         return null;
      }
   }

   public static void callVoid(Object obj, ReflectorMethod refMethod, Object... params) {
      try {
         if (obj == null) {
            return;
         }

         Method method = refMethod.getTargetMethod();
         if (method == null) {
            return;
         }

         method.invoke(obj, params);
      } catch (Throwable throwable) {
         handleException(throwable, obj, refMethod, params);
      }
   }

   public static boolean callBoolean(Object obj, ReflectorMethod refMethod, Object... params) {
      try {
         Method method = refMethod.getTargetMethod();
         if (method == null) {
            return false;
         }

         Boolean boolean2 = (Boolean)method.invoke(obj, params);
         return boolean2;
      } catch (Throwable throwable) {
         handleException(throwable, obj, refMethod, params);
         return false;
      }
   }

   public static int callInt(Object obj, ReflectorMethod refMethod, Object... params) {
      try {
         Method method = refMethod.getTargetMethod();
         if (method == null) {
            return 0;
         }

         Integer integer = (Integer)method.invoke(obj, params);
         return integer;
      } catch (Throwable throwable) {
         handleException(throwable, obj, refMethod, params);
         return 0;
      }
   }

   public static float callFloat(Object obj, ReflectorMethod refMethod, Object... params) {
      try {
         Method method = refMethod.getTargetMethod();
         if (method == null) {
            return 0.0F;
         }

         Float float2 = (Float)method.invoke(obj, params);
         return float2;
      } catch (Throwable throwable) {
         handleException(throwable, obj, refMethod, params);
         return 0.0F;
      }
   }

   public static double callDouble(Object obj, ReflectorMethod refMethod, Object... params) {
      try {
         Method method = refMethod.getTargetMethod();
         if (method == null) {
            return 0.0;
         }

         Double double2 = (Double)method.invoke(obj, params);
         return double2;
      } catch (Throwable throwable) {
         handleException(throwable, obj, refMethod, params);
         return 0.0;
      }
   }

   public static String callString(Object obj, ReflectorMethod refMethod, Object... params) {
      try {
         Method method = refMethod.getTargetMethod();
         return method == null ? null : (String)method.invoke(obj, params);
      } catch (Throwable throwable) {
         handleException(throwable, obj, refMethod, params);
         return null;
      }
   }

   public static Object call(Object obj, ReflectorMethod refMethod, Object... params) {
      try {
         Method method = refMethod.getTargetMethod();
         return method == null ? null : method.invoke(obj, params);
      } catch (Throwable throwable) {
         handleException(throwable, obj, refMethod, params);
         return null;
      }
   }

   public static Object getFieldValue(ReflectorField refField) {
      return getFieldValue(null, refField);
   }

   public static Object getFieldValue(Object obj, ReflectorField refField) {
      try {
         Field field = refField.getTargetField();
         return field == null ? null : field.get(obj);
      } catch (Throwable throwable) {
         throwable.printStackTrace();
         return null;
      }
   }

   public static Object getFieldValue(ReflectorFields refFields, int index) {
      ReflectorField reflectorField = refFields.getReflectorField(index);
      return reflectorField == null ? null : getFieldValue(reflectorField);
   }

   public static Object getFieldValue(Object obj, ReflectorFields refFields, int index) {
      ReflectorField reflectorField = refFields.getReflectorField(index);
      return reflectorField == null ? null : getFieldValue(obj, reflectorField);
   }

   public static float getFieldValueFloat(Object obj, ReflectorField refField, float def) {
      Object object = getFieldValue(obj, refField);
      if (!(object instanceof Float)) {
         return def;
      }

      Float float2 = (Float)object;
      return float2;
   }

   public static boolean setFieldValue(ReflectorField refField, Object value) {
      return setFieldValue(null, refField, value);
   }

   public static boolean setFieldValue(Object obj, ReflectorField refField, Object value) {
      try {
         Field field = refField.getTargetField();
         if (field == null) {
            return false;
         }

         field.set(obj, value);
         return true;
      } catch (Throwable throwable) {
         throwable.printStackTrace();
         return false;
      }
   }

   public static boolean postForgeBusEvent(ReflectorConstructor constr, Object... params) {
      Object object = newInstance(constr, params);
      return object == null ? false : postForgeBusEvent(object);
   }

   public static boolean postForgeBusEvent(Object event) {
      if (event == null) {
         return false;
      }

      Object object = getFieldValue(MinecraftForge_EVENT_BUS);
      if (object == null) {
         return false;
      }

      Object object2 = call(object, EventBus_post, event);
      if (!(object2 instanceof Boolean)) {
         return false;
      }

      Boolean boolean2 = (Boolean)object2;
      return boolean2;
   }

   public static Object newInstance(ReflectorConstructor constr, Object... params) {
      Constructor constructor = constr.getTargetConstructor();
      if (constructor == null) {
         return null;
      }

      try {
         return constructor.newInstance(params);
      } catch (Throwable throwable) {
         handleException(throwable, constr, params);
         return null;
      }
   }

   public static boolean matchesTypes(Class[] pTypes, Class[] cTypes) {
      if (pTypes.length != cTypes.length) {
         return false;
      }

      for (int i = 0; i < cTypes.length; i++) {
         Class class2 = pTypes[i];
         Class class3 = cTypes[i];
         if (class2 != class3) {
            return false;
         }
      }

      return true;
   }

   private static void handleException(Throwable eIn, Object obj, ReflectorMethod refMethod, Object[] params) {
      if (eIn instanceof InvocationTargetException) {
         Throwable throwable = eIn.getCause();
         if (throwable instanceof RuntimeException) {
            RuntimeException runtimeException = (RuntimeException)throwable;
            throw runtimeException;
         }

         eIn.printStackTrace();
      } else {
         if (eIn instanceof IllegalArgumentException) {
            Config.warn("*** IllegalArgumentException ***");
            Config.warn("Method: " + refMethod.getTargetMethod());
            Config.warn("Object: " + obj);
            Config.warn("Parameter classes: " + Config.arrayToString(getClasses(params)));
            Config.warn("Parameters: " + Config.arrayToString(params));
         }

         Config.warn("*** Exception outside of method ***");
         Config.warn("Method deactivated: " + refMethod.getTargetMethod());
         refMethod.deactivate();
         eIn.printStackTrace();
      }
   }

   private static void handleException(Throwable eIn, ReflectorConstructor refConstr, Object[] params) {
      if (eIn instanceof InvocationTargetException) {
         eIn.printStackTrace();
      } else {
         if (eIn instanceof IllegalArgumentException) {
            Config.warn("*** IllegalArgumentException ***");
            Config.warn("Constructor: " + refConstr.getTargetConstructor());
            Config.warn("Parameter classes: " + Config.arrayToString(getClasses(params)));
            Config.warn("Parameters: " + Config.arrayToString(params));
         }

         Config.warn("*** Exception outside of constructor ***");
         Config.warn("Constructor deactivated: " + refConstr.getTargetConstructor());
         refConstr.deactivate();
         eIn.printStackTrace();
      }
   }

   private static Object[] getClasses(Object[] objs) {
      if (objs == null) {
         return new Class[0];
      }

      Class[] classs = new Class[objs.length];

      for (int i = 0; i < classs.length; i++) {
         Object object = objs[i];
         if (object != null) {
            classs[i] = object.getClass();
         }
      }

      return classs;
   }

   private static boolean logEntry(String str) {
      Config.dbg(str);
      return true;
   }
}
