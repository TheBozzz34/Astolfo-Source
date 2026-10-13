package net.minecraft.client.renderer;

import bypass.Astolfo;
import bypass.event.player.LookEvent;
import bypass.event.player.ReachEvent;
import bypass.event.render.HurtCameraEvent;
import bypass.event.render.RenderAfterScreenEvent;
import bypass.event.render.RenderEvent;
import bypass.event.render.RenderType;
import bypass.event.render.RenderUnscaledEvent;
import bypass.event.render.WorldRenderedEvent;
import bypass.module.ModuleManager;
import bypass.module.other.InfiniteReach;
import bypass.module.visuals.Camera;
import bypass.module.visuals.CaveFinder;
import bypass.module.visuals.Freecam2;
import bypass.module.visuals.NoEffect;
import bypass.module.visuals.WorldColor;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.MapItemRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderLinkHelper;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.src.Config;
import net.minecraft.src.CustomColors;
import net.minecraft.src.Lagometer;
import net.minecraft.src.RandomMobs;
import net.minecraft.src.Reflector;
import net.minecraft.src.ReflectorForge;
import net.minecraft.src.TextureUtils;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MouseFilter;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.biome.BiomeGenBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Project;
import shadersmod.client.Shaders;
import shadersmod.client.ShadersRender;

public class EntityRenderer implements IResourceManagerReloadListener {
   private static final Logger logger = LogManager.getLogger();
   private static final ResourceLocation locationRainPng = new ResourceLocation("textures/environment/rain.png");
   private static final ResourceLocation locationSnowPng = new ResourceLocation("textures/environment/snow.png");
   public static boolean anaglyphEnable;
   public static int A;
   private Minecraft mc;
   private final IResourceManager resourceManager;
   private Random random = new Random();
   private float Q;
   public ItemRenderer itemRenderer;
   private final MapItemRenderer theMapItemRenderer;
   private int Zn;
   private Entity pointedEntity;
   private MouseFilter mouseFilterXAxis = new MouseFilter();
   private MouseFilter mouseFilterYAxis = new MouseFilter();
   private float H = 4.0F;
   private float m = 4.0F;
   private float n;
   private float M;
   private float Zt;
   private float K;
   private float d;
   private float fovModifierHand;
   private float fovModifierHandPrev;
   private float z;
   private float Zv;
   private boolean s;
   private boolean V = true;
   private boolean Z7 = true;
   private long Zy = Minecraft.getSystemTime();
   private long Z4;
   private final DynamicTexture lightmapTexture;
   private final int[] lightmapColors;
   private final ResourceLocation locationLightMap;
   private boolean lightmapUpdateNeeded;
   private float torchFlickerX;
   private float torchFlickerDX;
   private int T;
   private float[] rainXCoords = new float[1024];
   private float[] rainYCoords = new float[1024];
   private FloatBuffer fogColorBuffer = GLAllocation.createDirectFloatBuffer(16);
   public float ZQ;
   public float N;
   public float ZI;
   private float U;
   private float X;
   private int Z5 = 0;
   private boolean ZL = false;
   private double cameraZoom = 1.0;
   private double cameraYaw;
   private double cameraPitch;
   public ShaderGroup theShaderGroup;
   public static final ResourceLocation[] shaderResourceLocations = new ResourceLocation[]{
      new ResourceLocation("shaders/post/notch.json"),
      new ResourceLocation("shaders/post/fxaa.json"),
      new ResourceLocation("shaders/post/art.json"),
      new ResourceLocation("shaders/post/bumpy.json"),
      new ResourceLocation("shaders/post/blobs2.json"),
      new ResourceLocation("shaders/post/pencil.json"),
      new ResourceLocation("shaders/post/color_convolve.json"),
      new ResourceLocation("shaders/post/deconverge.json"),
      new ResourceLocation("shaders/post/flip.json"),
      new ResourceLocation("shaders/post/invert.json"),
      new ResourceLocation("shaders/post/ntsc.json"),
      new ResourceLocation("shaders/post/outline.json"),
      new ResourceLocation("shaders/post/phosphor.json"),
      new ResourceLocation("shaders/post/scan_pincushion.json"),
      new ResourceLocation("shaders/post/sobel.json"),
      new ResourceLocation("shaders/post/bits.json"),
      new ResourceLocation("shaders/post/desaturate.json"),
      new ResourceLocation("shaders/post/green.json"),
      new ResourceLocation("shaders/post/blur.json"),
      new ResourceLocation("shaders/post/wobble.json"),
      new ResourceLocation("shaders/post/blobs.json"),
      new ResourceLocation("shaders/post/antialias.json"),
      new ResourceLocation("shaders/post/creeper.json"),
      new ResourceLocation("shaders/post/spider.json")
   };
   public static final int shaderCount = shaderResourceLocations.length;
   private int shaderIndex;
   private boolean useShader;
   public int R;
   private boolean initialized = false;
   private World updatedWorld = null;
   private boolean showDebugInfo = false;
   public boolean fogStandard = false;
   private float clipDistance = 128.0F;
   private long lastServerTime = 0L;
   private int lastServerTicks = 0;
   private int serverWaitTime = 0;
   private int serverWaitTimeCurrent = 0;
   private float avgServerTimeDiff = 0.0F;
   private float avgServerTickDiff = 0.0F;
   private long lastErrorCheckTimeMs = 0L;
   private ShaderGroup[] fxaaShaders = new ShaderGroup[10];
   public static int Z0 = -5;
   public static int C = 23;
   public static int ZE = 5672;
   public static float S;
   public static float r;
   public static boolean q;
   private int B;
   private static final long ab = 107532705393986L;

   public EntityRenderer(Minecraft var1, IResourceManager var2) {
      this.shaderIndex = shaderCount;
      this.useShader = false;
      this.R = 0;
      this.mc = var1;
      this.resourceManager = var2;
      this.itemRenderer = var1.getItemRenderer();
      this.theMapItemRenderer = new MapItemRenderer(var1.getTextureManager());
      this.lightmapTexture = new DynamicTexture(16, 16);
      this.locationLightMap = var1.getTextureManager().getDynamicTextureLocation("lightMap", this.lightmapTexture);
      this.lightmapColors = this.lightmapTexture.getTextureData();
      this.theShaderGroup = null;

      for (int var6 = 0; var6 < 32; var6++) {
         for (int var7 = 0; var7 < 32; var7++) {
            float var8 = var7 - 16;
            float var9 = var6 - 16;
            float var10 = MathHelper.sqrt_float(var8 * var8 + var9 * var9);
            this.rainXCoords[var6 << 5 | var7] = -var9 / var10;
            this.rainYCoords[var6 << 5 | var7] = var8 / var10;
         }
      }
   }

   public boolean Q_boolean() {
      return OpenGlHelper.shadersSupported && this.theShaderGroup != null;
   }

   public void Q() {
      if (this.theShaderGroup != null) {
         this.theShaderGroup.deleteShaderGroup();
      }

      this.theShaderGroup = null;
      this.shaderIndex = shaderCount;
   }

   public void E() {
      this.useShader = !this.useShader;
   }

   public void loadEntityShader(Entity var1) {
      if (OpenGlHelper.shadersSupported) {
         if (this.theShaderGroup != null) {
            this.theShaderGroup.deleteShaderGroup();
         }

         this.theShaderGroup = null;
         if (var1 instanceof EntityCreeper) {
            this.loadShader(new ResourceLocation("shaders/post/creeper.json"));
         } else if (var1 instanceof EntitySpider) {
            this.loadShader(new ResourceLocation("shaders/post/spider.json"));
         } else if (var1 instanceof EntityEnderman) {
            this.loadShader(new ResourceLocation("shaders/post/invert.json"));
         } else if (Reflector.ForgeHooksClient_loadEntityShader.exists()) {
            Reflector.call(Reflector.ForgeHooksClient_loadEntityShader, var1, this);
         }
      }
   }

   public void D() {
      if (OpenGlHelper.shadersSupported && this.mc.getRenderViewEntity() instanceof EntityPlayer) {
         if (this.theShaderGroup != null) {
            this.theShaderGroup.deleteShaderGroup();
         }

         this.shaderIndex = (this.shaderIndex + 1) % (shaderResourceLocations.length + 1);
         if (this.shaderIndex != shaderCount) {
            this.loadShader(shaderResourceLocations[this.shaderIndex]);
         } else {
            this.theShaderGroup = null;
         }
      }
   }

   public void loadShader(ResourceLocation var1) {
      if (OpenGlHelper.isFramebufferEnabled()) {
         try {
            this.theShaderGroup = new ShaderGroup(this.mc.getTextureManager(), this.resourceManager, this.mc.getFramebuffer(), var1);
            this.theShaderGroup.createBindFramebuffers(this.mc.displayWidth, this.mc.displayHeight);
            this.useShader = true;
         } catch (IOException var6) {
            logger.warn("Failed to load shader: " + var1, var6);
            this.shaderIndex = shaderCount;
            this.useShader = false;
         } catch (JsonSyntaxException var7) {
            logger.warn("Failed to load shader: " + var1, var7);
            this.shaderIndex = shaderCount;
            this.useShader = false;
         }
      }
   }

   @Override
   public void onResourceManagerReload(IResourceManager var1) {
      if (this.theShaderGroup != null) {
         this.theShaderGroup.deleteShaderGroup();
      }

      this.theShaderGroup = null;
      if (this.shaderIndex != shaderCount) {
         this.loadShader(shaderResourceLocations[this.shaderIndex]);
      } else {
         this.loadEntityShader(this.mc.getRenderViewEntity());
      }
   }

   public void updateRenderer() {
      if (OpenGlHelper.shadersSupported && ShaderLinkHelper.getStaticShaderLinkHelper() == null) {
         ShaderLinkHelper.setNewStaticShaderLinkHelper();
      }

      this.updateFovModifierHand();
      this.updateTorchFlicker();
      this.U = this.X;
      this.m = this.H;
      if (this.mc.gameSettings.lt) {
         float var4 = this.mc.gameSettings.Zm * 0.6F + 0.2F;
         float var5 = var4 * var4 * var4 * 8.0F;
         this.Zt = this.mouseFilterXAxis.smooth(this.n, 0.05F * var5);
         this.K = this.mouseFilterYAxis.smooth(this.M, 0.05F * var5);
         this.d = 0.0F;
         this.n = 0.0F;
         this.M = 0.0F;
      } else {
         this.Zt = 0.0F;
         this.K = 0.0F;
         this.mouseFilterXAxis.reset();
         this.mouseFilterYAxis.reset();
      }

      if (this.mc.getRenderViewEntity() == null) {
         this.mc.setRenderViewEntity(this.mc.thePlayer);
      }

      Entity var14 = this.mc.getRenderViewEntity();
      double var15 = var14.posX;
      double var7 = var14.posY + var14.getEyeHeight();
      double var9 = var14.posZ;
      float var11 = this.mc.theWorld.getLightBrightness(new BlockPos(var15, var7, var9));
      float var12 = this.mc.gameSettings.renderDistanceChunks / 16.0F;
      var12 = MathHelper.clamp_float(var12, 0.0F, 1.0F);
      float var13 = var11 * (1.0F - var12) + var12;
      this.X = this.X + (var13 - this.X) * 0.1F;
      this.Zn++;
      this.itemRenderer.updateEquippedItem();
      this.addRainParticles();
      this.Zv = this.z;
      if (BossStatus.hasColorModifier) {
         this.z += 0.05F;
         if (this.z > 1.0F) {
            this.z = 1.0F;
         }

         BossStatus.hasColorModifier = false;
      } else if (this.z > 0.0F) {
         this.z -= 0.0125F;
      }
   }

   public ShaderGroup getShaderGroup() {
      return this.theShaderGroup;
   }

   public void updateShaderGroupSize(int var1, int var2) {
      if (OpenGlHelper.shadersSupported) {
         if (this.theShaderGroup != null) {
            this.theShaderGroup.createBindFramebuffers(var1, var2);
         }

         this.mc.renderGlobal.createBindEntityOutlineFbs(var1, var2);
      }
   }

   public void getMouseOver(float var1) {
      Entity var5 = this.mc.getRenderViewEntity();
      if (var5 != null && this.mc.theWorld != null) {
         this.mc.mcProfiler.startSection("pick");
         this.mc.pointedEntity = null;
         double var6 = this.mc.playerController.getBlockReachDistance();
         this.mc.objectMouseOver = var5.rayTrace(var6, var1);
         double var8 = var6;
         Vec3 var10 = var5.getPositionEyes(var1);
         boolean var11 = false;
         if (this.mc.playerController.L()) {
            var6 = 6.0;
            var8 = 6.0;
         } else {
            if (var6 > 3.0) {
               var11 = true;
            }

            var6 = var6;
         }

         if (this.mc.objectMouseOver != null) {
            var8 = this.mc.objectMouseOver.hitVec.distanceTo(var10);
         }

         Vec3 var13 = var5.getLook(var1);
         LookEvent var14 = new LookEvent(var13);
         Astolfo.INSTANCE.getEventBus().post(var14);
         var13 = var14.getVec();
         Vec3 var15 = var10.addVector(var13.xCoord * var6, var13.yCoord * var6, var13.zCoord * var6);
         this.pointedEntity = null;
         Vec3 var16 = null;
         float var17 = 1.0F;
         List var18 = this.mc
            .theWorld
            .getEntitiesInAABBexcluding(
               var5,
               var5.getEntityBoundingBox().addCoord(var13.xCoord * var6, var13.yCoord * var6, var13.zCoord * var6).expand(var17, var17, var17),
               Predicates.and(EntitySelectors.NOT_SPECTATING, new EntityRenderer.rt7(this))
            );
         double var19 = var8;

         for (int var21 = 0; var21 < var18.size(); var21++) {
            Entity var22 = (Entity)var18.get(var21);
            float var23 = var22.getCollisionBorderSize();
            AxisAlignedBB var24 = var22.getEntityBoundingBox().expand(var23, var23, var23);
            MovingObjectPosition var25 = var24.calculateIntercept(var10, var15);
            if (var24.isVecInside(var10)) {
               if (var19 >= 0.0 && !ModuleManager.Q.isEnabled()) {
                  this.pointedEntity = var22;
                  var16 = var25 == null ? var10 : var25.hitVec;
                  var19 = 0.0;
               }
            } else if (var25 != null) {
               double var26 = var10.distanceTo(var25.hitVec);
               if (var26 < var19 || var19 == 0.0) {
                  boolean var28 = false;
                  if (Reflector.ForgeEntity_canRiderInteract.exists()) {
                     var28 = Reflector.callBoolean(var22, Reflector.ForgeEntity_canRiderInteract);
                  }

                  if (var22 != var5.ridingEntity || var28) {
                     this.pointedEntity = var22;
                     var16 = var25.hitVec;
                     var19 = var26;
                  } else if (var19 == 0.0) {
                     this.pointedEntity = var22;
                     var16 = var25.hitVec;
                  }
               }
            }
         }

         ReachEvent var31 = new ReachEvent();
         Astolfo.INSTANCE.getEventBus().post(var31);
         if (this.pointedEntity != null
            && var11
            && var10.distanceTo(var16) > (ModuleManager.Z.isEnabled() ? InfiniteReach.range.getValue().floatValue() : var31.getReach())) {
            this.pointedEntity = null;
            this.mc.objectMouseOver = new MovingObjectPosition(MovingObjectPosition.MovingObjectType.MISS, var16, (EnumFacing)null, new BlockPos(var16));
         }

         if (this.pointedEntity != null && (var19 < var8 || this.mc.objectMouseOver == null)) {
            this.mc.objectMouseOver = new MovingObjectPosition(this.pointedEntity, var16);
            if (this.pointedEntity instanceof EntityLivingBase || this.pointedEntity instanceof EntityItemFrame) {
               this.mc.pointedEntity = this.pointedEntity;
            }
         }

         this.mc.mcProfiler.endSection();
      }
   }

   private void updateFovModifierHand() {
      float var4 = 1.0F;
      if (this.mc.getRenderViewEntity() instanceof AbstractClientPlayer) {
         AbstractClientPlayer var5 = (AbstractClientPlayer)this.mc.getRenderViewEntity();
         var4 = var5.getFovModifier();
      }

      this.fovModifierHandPrev = this.fovModifierHand;
      this.fovModifierHand = this.fovModifierHand + (var4 - this.fovModifierHand) * 0.5F;
      if (this.fovModifierHand > 1.5F) {
         this.fovModifierHand = 1.5F;
      }

      if (this.fovModifierHand < 0.1F) {
         this.fovModifierHand = 0.1F;
      }
   }

   private float getFOVModifier(float var1, boolean var2) {
      if (this.ZL) {
         return 90.0F;
      }

      Entity var6 = this.mc.getRenderViewEntity();
      float var7 = 70.0F;
      if (var2) {
         var7 = this.mc.gameSettings.b;
         if (Config.isDynamicFov()) {
            var7 *= this.fovModifierHandPrev + (this.fovModifierHand - this.fovModifierHandPrev) * var1;
         }
      }

      var7 *= Z0;
      boolean var8 = false;
      if (this.mc.currentScreen == null) {
         GameSettings var9 = this.mc.gameSettings;
         var8 = GameSettings.isKeyDown(this.mc.gameSettings.ofKeyBindZoom);
      }

      if (var8) {
         if (!Config.zoomMode) {
            Config.zoomMode = true;
            this.mc.gameSettings.lt = true;
         }

         if (Config.zoomMode) {
            var7 /= 4.0F;
         }
      } else if (Config.zoomMode) {
         Config.zoomMode = false;
         this.mc.gameSettings.lt = false;
         this.mouseFilterXAxis = new MouseFilter();
         this.mouseFilterYAxis = new MouseFilter();
         this.mc.renderGlobal.displayListEntitiesDirty = true;
      }

      if (var6 instanceof EntityLivingBase && ((EntityLivingBase)var6).getHealth() <= 0.0F) {
         float var11 = ((EntityLivingBase)var6).deathTime + var1;
         var7 /= (1.0F - 500.0F / (var11 + 500.0F)) * 2.0F + 1.0F;
      }

      Block var12 = ActiveRenderInfo.getBlockAtEntityViewpoint(this.mc.theWorld, var6, var1);
      if (var12.getMaterial() == Material.water) {
         var7 = var7 * 60.0F / 70.0F;
      }

      return var7;
   }

   private void hurtCameraEffect(float var1) {
      if (this.mc.getRenderViewEntity() instanceof EntityLivingBase) {
         HurtCameraEvent var5 = new HurtCameraEvent();
         Astolfo.INSTANCE.getEventBus().post(var5);
         if (var5.isCancelled()) {
            return;
         }

         EntityLivingBase var6 = (EntityLivingBase)this.mc.getRenderViewEntity();
         float var7 = var6.hurtTime - var1;
         if (var6.getHealth() <= 0.0F) {
            float var8 = var6.deathTime + var1;
            GlStateManager.rotate(40.0F - 8000.0F / (var8 + 200.0F), 0.0F, 0.0F, 1.0F);
         }

         if (var7 < 0.0F) {
            return;
         }

         var7 /= var6.maxHurtTime;
         var7 = MathHelper.sin(var7 * var7 * var7 * var7 * (float) Math.PI);
         float var11 = var6.attackedAtYaw;
         GlStateManager.rotate(-var11, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(-var7 * 14.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.rotate(var11, 0.0F, 1.0F, 0.0F);
      }
   }

   private void setupViewBobbing(float var1) {
      if (this.mc.getRenderViewEntity() instanceof EntityPlayer) {
         EntityPlayer var5 = (EntityPlayer)this.mc.getRenderViewEntity();
         float var6 = var5.distanceWalkedModified - var5.prevDistanceWalkedModified;
         float var7 = -(var5.distanceWalkedModified + var6 * var1);
         float var8 = var5.prevCameraYaw + (var5.cameraYaw - var5.prevCameraYaw) * var1;
         float var9 = var5.prevCameraPitch + (var5.cameraPitch - var5.prevCameraPitch) * var1;
         GlStateManager.translate(MathHelper.sin(var7 * (float) Math.PI) * var8 * 0.5F, -Math.abs(MathHelper.cos(var7 * (float) Math.PI) * var8), 0.0F);
         GlStateManager.rotate(MathHelper.sin(var7 * (float) Math.PI) * var8 * 3.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.rotate(Math.abs(MathHelper.cos(var7 * (float) Math.PI - 0.2F) * var8) * 5.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotate(var9, 1.0F, 0.0F, 0.0F);
      }
   }

   private void orientCamera(float var1) {
      Entity var5 = this.mc.getRenderViewEntity();
      float var6 = var5.rotationYaw;
      float var7 = var5.rotationPitch;
      float var8 = var5.prevRotationYaw;
      float var9 = var5.prevRotationPitch;
      if (q) {
         var5.rotationYaw = S;
         var5.prevRotationYaw = S;
         var5.rotationPitch = r;
         var5.prevRotationPitch = r;
      }

      float var10 = var5.getEyeHeight();
      double var11 = var5.prevPosX + (var5.posX - var5.prevPosX) * var1;
      double var13 = var5.prevPosY + (var5.posY - var5.prevPosY) * var1 + var10;
      double var15 = var5.prevPosZ + (var5.posZ - var5.prevPosZ) * var1;
      if (var5 instanceof EntityLivingBase && ((EntityLivingBase)var5).isPlayerSleeping()) {
         var10 = (float)(var10 + 1.0);
         GlStateManager.translate(0.0F, 0.3F, 0.0F);
         if (!this.mc.gameSettings.ZI) {
            BlockPos var37 = new BlockPos(var5);
            IBlockState var18 = this.mc.theWorld.getBlockState(var37);
            Block var43 = var18.getBlock();
            if (Reflector.ForgeHooksClient_orientBedCamera.exists()) {
               Reflector.callVoid(Reflector.ForgeHooksClient_orientBedCamera, this.mc.theWorld, var37, var18, var5);
            } else if (var43 == Blocks.bed) {
               int var46 = var18.getValue(BlockBed.FACING).getHorizontalIndex();
               GlStateManager.rotate(var46 * 90, 0.0F, 1.0F, 0.0F);
            }

            GlStateManager.rotate(var5.prevRotationYaw + (var5.rotationYaw - var5.prevRotationYaw) * var1 + 180.0F, 0.0F, -1.0F, 0.0F);
            GlStateManager.rotate(var5.prevRotationPitch + (var5.rotationPitch - var5.prevRotationPitch) * var1, -1.0F, 0.0F, 0.0F);
         }
      } else if (this.mc.gameSettings.thirdPersonView > 0) {
         double var17 = 5.0;
         if (this.mc.gameSettings.ZI) {
            GlStateManager.translate(0.0F, 0.0F, (float)(-var17));
         } else {
            float var19 = var5.rotationYaw;
            float var20 = var5.rotationPitch;
            if (this.mc.gameSettings.thirdPersonView == 2) {
               var20 += 180.0F;
            }

            double var21 = -MathHelper.sin(var19 / 180.0F * (float) Math.PI) * MathHelper.cos(var20 / 180.0F * (float) Math.PI) * var17;
            double var23 = MathHelper.cos(var19 / 180.0F * (float) Math.PI) * MathHelper.cos(var20 / 180.0F * (float) Math.PI) * var17;
            double var25 = -MathHelper.sin(var20 / 180.0F * (float) Math.PI) * var17;

            for (int var27 = 0; var27 < 8; var27++) {
               float var28 = (var27 & 1) * 2 - 1;
               float var29 = (var27 >> 1 & 1) * 2 - 1;
               float var30 = (var27 >> 2 & 1) * 2 - 1;
               var28 *= 0.1F;
               var29 *= 0.1F;
               var30 *= 0.1F;
               MovingObjectPosition var31 = this.mc
                  .theWorld
                  .rayTraceBlocks(
                     new Vec3(var11 + var28, var13 + var29, var15 + var30),
                     new Vec3(var11 - var21 + var28 + var30, var13 - var25 + var29, var15 - var23 + var30)
                  );
               if (var31 != null) {
                  double var32 = var31.hitVec.distanceTo(new Vec3(var11, var13, var15));
                  if (var32 < var17) {
                     var17 = var32;
                  }
               }
            }

            if (this.mc.gameSettings.thirdPersonView == 2) {
               GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            }

            GlStateManager.rotate(var5.rotationPitch - var20, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(var5.rotationYaw - var19, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(0.0 + Freecam2.V, 0.0, (float)(ModuleManager.k.isEnabled() && Camera.viewClip.getValue() ? -5.0 : -var17) + Freecam2.R);
            GlStateManager.rotate(var19 - var5.rotationYaw, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(var20 - var5.rotationPitch, 1.0F, 0.0F, 0.0F);
         }
      } else {
         GlStateManager.translate(0.0F, 0.0F, -0.1F);
      }

      if (Reflector.EntityViewRenderEvent_CameraSetup_Constructor.exists()) {
         if (!this.mc.gameSettings.ZI) {
            float var38 = var5.prevRotationYaw + (var5.rotationYaw - var5.prevRotationYaw) * var1 + 180.0F;
            float var41 = var5.prevRotationPitch + (var5.rotationPitch - var5.prevRotationPitch) * var1;
            float var44 = 0.0F;
            if (var5 instanceof EntityAnimal) {
               EntityAnimal var47 = (EntityAnimal)var5;
               var38 = var47.prevRotationYawHead + (var47.rotationYawHead - var47.prevRotationYawHead) * var1 + 180.0F;
            }

            Block var48 = ActiveRenderInfo.getBlockAtEntityViewpoint(this.mc.theWorld, var5, var1);
            Object var49 = Reflector.newInstance(Reflector.EntityViewRenderEvent_CameraSetup_Constructor, this, var5, var48, var1, var38, var41, var44);
            Reflector.postForgeBusEvent(var49);
            var44 = Reflector.getFieldValueFloat(var49, Reflector.EntityViewRenderEvent_CameraSetup_roll, var44);
            var41 = Reflector.getFieldValueFloat(var49, Reflector.EntityViewRenderEvent_CameraSetup_pitch, var41);
            var38 = Reflector.getFieldValueFloat(var49, Reflector.EntityViewRenderEvent_CameraSetup_yaw, var38);
            GlStateManager.rotate(var44, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(var41, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(var38, 0.0F, 1.0F, 0.0F);
         }
      } else if (!this.mc.gameSettings.ZI) {
         GlStateManager.rotate(var5.prevRotationPitch + (var5.rotationPitch - var5.prevRotationPitch) * var1, 1.0F, 0.0F, 0.0F);
         if (var5 instanceof EntityAnimal) {
            EntityAnimal var40 = (EntityAnimal)var5;
            GlStateManager.rotate(var40.prevRotationYawHead + (var40.rotationYawHead - var40.prevRotationYawHead) * var1 + 180.0F, 0.0F, 1.0F, 0.0F);
         } else {
            GlStateManager.rotate(var5.prevRotationYaw + (var5.rotationYaw - var5.prevRotationYaw) * var1 + 180.0F, 0.0F, 1.0F, 0.0F);
         }
      }

      if (q) {
         var5.rotationYaw = var6;
         var5.prevRotationYaw = var8;
         var5.rotationPitch = var7;
         var5.prevRotationPitch = var9;
      }

      GlStateManager.translate(0.0F, -var10, 0.0F);
      var11 = var5.prevPosX + (var5.posX - var5.prevPosX) * var1;
      var13 = var5.prevPosY + (var5.posY - var5.prevPosY) * var1 + var10;
      var15 = var5.prevPosZ + (var5.posZ - var5.prevPosZ) * var1;
      this.s = this.mc.renderGlobal.hasCloudFog(var11, var13, var15, var1);
   }

   public void setupCameraTransform(float var1, int var2) {
      this.Q = this.mc.gameSettings.renderDistanceChunks * 16;
      if (Config.isFogFancy()) {
         this.Q *= 0.95F;
      }

      if (Config.isFogFast()) {
         this.Q *= 0.83F;
      }

      GlStateManager.matrixMode(5889);
      GlStateManager.loadIdentity();
      float var6 = 0.07F;
      if (this.mc.gameSettings.anaglyph) {
         GlStateManager.translate(-(var2 * 2 - 1) * var6, 0.0F, 0.0F);
      }

      this.clipDistance = this.Q * 2.0F;
      if (this.clipDistance < 173.0F) {
         this.clipDistance = 173.0F;
      }

      if (this.mc.theWorld.provider.getDimensionId() == 1) {
         this.clipDistance = 256.0F;
      }

      if (this.cameraZoom != 1.0) {
         GlStateManager.translate((float)this.cameraYaw, (float)(-this.cameraPitch), 0.0F);
         GlStateManager.scale(this.cameraZoom, this.cameraZoom, 1.0);
      }

      Project.gluPerspective(this.getFOVModifier(var1, true), (float)this.mc.displayWidth / this.mc.displayHeight, 0.05F, this.clipDistance);
      GlStateManager.matrixMode(ModuleManager.e);
      GlStateManager.loadIdentity();
      if (this.mc.gameSettings.anaglyph) {
         GlStateManager.translate((var2 * 2 - 1) * 0.1F, 0.0F, 0.0F);
      }

      this.hurtCameraEffect(var1);
      if (this.mc.gameSettings.viewBobbing) {
         this.setupViewBobbing(var1);
      }

      float var7 = this.mc.thePlayer.prevTimeInPortal + (this.mc.thePlayer.timeInPortal - this.mc.thePlayer.prevTimeInPortal) * var1;
      if (var7 > 0.0F) {
         byte var8 = 20;
         if (this.mc.thePlayer.isPotionActive(Potion.confusion)) {
            var8 = 7;
         }

         float var9 = 5.0F / (var7 * var7 + 5.0F) - var7 * 0.04F;
         var9 *= var9;
         GlStateManager.rotate((this.Zn + var1) * var8, 0.0F, 1.0F, 1.0F);
         GlStateManager.scale(1.0F / var9, 1.0F, 1.0F);
         GlStateManager.rotate(-(this.Zn + var1) * var8, 0.0F, 1.0F, 1.0F);
      }

      this.orientCamera(var1);
      if (this.ZL) {
         switch (this.Z5) {
            case 0:
               GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
               break;
            case 1:
               GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
               break;
            case 2:
               GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
               break;
            case 3:
               GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
               break;
            case 4:
               GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
         }
      }
   }

   private void renderHand(float var1, int var2) {
      this.renderHand(var1, var2, true, true, false);
   }

   public void renderHand(float var1, int var2, boolean var3, boolean var4, boolean var5) {
      if (!this.ZL) {
         GlStateManager.matrixMode(5889);
         GlStateManager.loadIdentity();
         float var9 = 0.07F;
         if (this.mc.gameSettings.anaglyph) {
            GlStateManager.translate(-(var2 * 2 - 1) * var9, 0.0F, 0.0F);
         }

         if (Config.isShaders()) {
            Shaders.applyHandDepth();
         }

         Project.gluPerspective(
            ModuleManager.k.isEnabled() ? this.getFOVModifier(var1, false) * Camera.itemFOV.getValue() : this.getFOVModifier(var1, false),
            (float)this.mc.displayWidth / this.mc.displayHeight,
            0.05F,
            this.Q * 2.0F
         );
         GlStateManager.matrixMode(5888);
         GlStateManager.loadIdentity();
         if (this.mc.gameSettings.anaglyph) {
            GlStateManager.translate((var2 * 2 - 1) * 0.1F, 0.0F, 0.0F);
         }

         boolean var10 = false;
         if (var3) {
            GlStateManager.pushMatrix();
            this.hurtCameraEffect(var1);
            if (this.mc.gameSettings.viewBobbing) {
               this.setupViewBobbing(var1);
            }

            var10 = this.mc.getRenderViewEntity() instanceof EntityLivingBase && ((EntityLivingBase)this.mc.getRenderViewEntity()).isPlayerSleeping();
            boolean var11 = !ReflectorForge.renderFirstPersonHand(this.mc.renderGlobal, var1, var2);
            if (var11 && this.mc.gameSettings.thirdPersonView == 0 && !var10 && !this.mc.gameSettings.Zn && !this.mc.playerController.W()) {
               this.enableLightmap();
               if (Config.isShaders()) {
                  ShadersRender.renderItemFP(this.itemRenderer, var1, var5);
               } else {
                  this.itemRenderer.renderItemInFirstPerson(var1);
               }

               this.disableLightmap();
            }

            GlStateManager.popMatrix();
         }

         if (!var4) {
            return;
         }

         this.disableLightmap();
         if (this.mc.gameSettings.thirdPersonView == 0 && !var10) {
            this.itemRenderer.renderOverlays(var1);
            this.hurtCameraEffect(var1);
         }

         if (this.mc.gameSettings.viewBobbing) {
            this.setupViewBobbing(var1);
         }
      }
   }

   public void disableLightmap() {
      GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
      GlStateManager.disableTexture2D();
      GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
      if (Config.isShaders()) {
         Shaders.q();
      }
   }

   public void enableLightmap() {
      GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
      GlStateManager.matrixMode(5890);
      GlStateManager.loadIdentity();
      float var3 = 0.00390625F;
      GlStateManager.scale(var3, var3, var3);
      GlStateManager.translate(8.0F, 8.0F, 8.0F);
      GlStateManager.matrixMode(ModuleManager.e);
      this.mc.getTextureManager().bindTexture(this.locationLightMap);
      GL11.glTexParameteri(3553, 10241, 9729);
      GL11.glTexParameteri(3553, 10240, 9729);
      GL11.glTexParameteri(3553, 10242, 10496);
      GL11.glTexParameteri(3553, 10243, 10496);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.enableTexture2D();
      GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
      if (Config.isShaders()) {
         Shaders.enableLightmap();
      }
   }

   private void updateTorchFlicker() {
      this.torchFlickerDX = (float)(this.torchFlickerDX + (Math.random() - Math.random()) * Math.random() * Math.random());
      this.torchFlickerDX = (float)(this.torchFlickerDX * 0.9);
      this.torchFlickerX = this.torchFlickerX + (this.torchFlickerDX - this.torchFlickerX) * 1.0F;
      this.lightmapUpdateNeeded = true;
   }

   private void updateLightmap(float var1) {
      if (this.lightmapUpdateNeeded) {
         this.mc.mcProfiler.startSection("lightTex");
         WorldClient var5 = this.mc.theWorld;
         if (var5 != null) {
            if (Config.isCustomColors() && CustomColors.l(var5, this.torchFlickerX, this.lightmapColors, this.mc.thePlayer.isPotionActive(Potion.nightVision))) {
               this.lightmapTexture.updateDynamicTexture();
               this.lightmapUpdateNeeded = false;
               this.mc.mcProfiler.endSection();
               return;
            }

            float var6 = var5.getSunBrightness(1.0F);
            float var7 = var6 * 0.95F + 0.05F;

            for (int var8 = 0; var8 < 256; var8++) {
               float var9 = var5.provider.getLightBrightnessTable()[var8 / 16] * var7;
               float var10 = var5.provider.getLightBrightnessTable()[var8 % 16] * (this.torchFlickerX * 0.1F + 1.5F);
               if (var5.x() > 0) {
                  var9 = var5.provider.getLightBrightnessTable()[var8 / 16];
               }

               float var11 = var9 * (var6 * 0.65F + 0.35F);
               float var12 = var9 * (var6 * 0.65F + 0.35F);
               float var13 = var10 * ((var10 * 0.6F + 0.4F) * 0.6F + 0.4F);
               float var14 = var10 * (var10 * var10 * 0.6F + 0.4F);
               float var15 = var11 + var10;
               float var16 = var12 + var13;
               float var17 = var9 + var14;
               var15 = var15 * 0.96F + 0.03F;
               var16 = var16 * 0.96F + 0.03F;
               var17 = var17 * 0.96F + 0.03F;
               if (this.z > 0.0F) {
                  float var18 = this.Zv + (this.z - this.Zv) * var1;
                  var15 = var15 * (1.0F - var18) + var15 * 0.7F * var18;
                  var16 = var16 * (1.0F - var18) + var16 * 0.6F * var18;
                  var17 = var17 * (1.0F - var18) + var17 * 0.6F * var18;
               }

               if (var5.provider.getDimensionId() == 1) {
                  var15 = 0.22F + var10 * 0.75F;
                  var16 = 0.28F + var13 * 0.75F;
                  var17 = 0.25F + var14 * 0.75F;
               }

               if (this.mc.thePlayer.isPotionActive(Potion.nightVision)) {
                  float var35 = this.getNightVisionBrightness(this.mc.thePlayer, var1);
                  float var19 = 1.0F / var15;
                  if (var19 > 1.0F / var16) {
                     var19 = 1.0F / var16;
                  }

                  if (var19 > 1.0F / var17) {
                     var19 = 1.0F / var17;
                  }

                  var15 = var15 * (1.0F - var35) + var15 * var19 * var35;
                  var16 = var16 * (1.0F - var35) + var16 * var19 * var35;
                  var17 = var17 * (1.0F - var35) + var17 * var19 * var35;
               }

               if (var15 > 1.0F) {
                  var15 = 1.0F;
               }

               if (var16 > 1.0F) {
                  var16 = 1.0F;
               }

               if (var17 > 1.0F) {
                  var17 = 1.0F;
               }

               float var36 = this.mc.gameSettings.Zu;
               float var37 = 1.0F - var15;
               float var20 = 1.0F - var16;
               float var21 = 1.0F - var17;
               var37 = 1.0F - var37 * var37 * var37 * var37;
               var20 = 1.0F - var20 * var20 * var20 * var20;
               var21 = 1.0F - var21 * var21 * var21 * var21;
               var15 = var15 * (1.0F - var36) + var37 * var36;
               var16 = var16 * (1.0F - var36) + var20 * var36;
               var17 = var17 * (1.0F - var36) + var21 * var36;
               var15 = var15 * 0.96F + 0.03F;
               var16 = var16 * 0.96F + 0.03F;
               var17 = var17 * 0.96F + 0.03F;
               if (var15 > 1.0F) {
                  var15 = 1.0F;
               }

               if (var16 > 1.0F) {
                  var16 = 1.0F;
               }

               if (var17 > 1.0F) {
                  var17 = 1.0F;
               }

               if (var15 < 0.0F) {
                  var15 = 0.0F;
               }

               if (var16 < 0.0F) {
                  var16 = 0.0F;
               }

               if (var17 < 0.0F) {
                  var17 = 0.0F;
               }

               short var22 = 255;
               int var23 = (int)(var15 * 255.0F);
               int var24 = (int)(var16 * 255.0F);
               int var25 = (int)(var17 * 255.0F);
               if (ModuleManager.M.isEnabled()) {
                  var23 = WorldColor.color.getValue().getRed();
                  var24 = WorldColor.color.getValue().getGreen();
                  var25 = WorldColor.color.getValue().getBlue();
                  var22 = (short)WorldColor.color.getValue().getAlpha();
               }

               this.lightmapColors[var8] = var22 << 24 | var23 << 16 | var24 << 8 | var25;
            }

            this.lightmapTexture.updateDynamicTexture();
            this.lightmapUpdateNeeded = false;
            this.mc.mcProfiler.endSection();
         }
      }
   }

   public float getNightVisionBrightness(EntityLivingBase var1, float var2) {
      int var5 = var1.getActivePotionEffect(Potion.nightVision).getDuration();
      return var5 > 200 ? 1.0F : 0.7F + MathHelper.sin((var5 - var2) * (float) Math.PI * 0.2F) * 0.3F;
   }

   public void updateCameraAndRender(float var1, long var2) {
      long var4 = ab ^ 97474039348583L;
      this.frameInit();
      boolean var7 = Display.isActive();
      if (!var7 && this.mc.gameSettings.G && (!this.mc.gameSettings.touchscreen || !Mouse.isButtonDown(1))) {
         if (Minecraft.getSystemTime() - this.Zy > 500L) {
            this.mc.g();
         }
      } else {
         this.Zy = Minecraft.getSystemTime();
      }

      this.mc.mcProfiler.startSection("mouse");
      if (var7 && Minecraft.isRunningOnMac && this.mc.inGameHasFocus && !Mouse.isInsideWindow()) {
         Mouse.setGrabbed(false);
         Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
         Mouse.setGrabbed(true);
      }

      if (this.mc.inGameHasFocus && var7) {
         this.mc.mouseHelper.mouseXYChange();
         float var8 = this.mc.gameSettings.Zm * 0.6F + 0.2F;
         float var9 = var8 * var8 * var8 * 8.0F;
         float var10 = this.mc.mouseHelper.deltaX * var9;
         float var11 = this.mc.mouseHelper.deltaY * var9;
         byte var12 = 1;
         if (this.mc.gameSettings.invertMouse) {
            var12 = -1;
         }

         if (this.mc.gameSettings.lt) {
            this.n += var10;
            this.M += var11;
            float var13 = var1 - this.d;
            this.d = var1;
            var10 = this.Zt * var13;
            var11 = this.K * var13;
            if (q) {
               S += var10 / 8.0F;
               r += var11 * var12 / 8.0F;
               if (Math.abs(r) > 90.0F) {
                  r = r > 0.0F ? 90.0F : -90.0F;
               }
            } else {
               this.mc.thePlayer.setAngles(var10, var11 * var12);
            }
         } else {
            this.n = 0.0F;
            this.M = 0.0F;
            if (q) {
               S += var10 / 8.0F;
               r += var11 * var12 / 8.0F;
               if (Math.abs(r) > 90.0F) {
                  r = r > 0.0F ? 90.0F : -90.0F;
               }
            } else {
               this.mc.thePlayer.setAngles(var10, var11 * var12);
            }
         }
      }

      this.mc.mcProfiler.endSection();
      if (!this.mc.R) {
         anaglyphEnable = this.mc.gameSettings.anaglyph;
         final ScaledResolution var21 = new ScaledResolution(this.mc);
         int var22 = var21.getScaledWidth();
         int var24 = var21.getScaledHeight();
         final int var26 = Mouse.getX() * var22 / this.mc.displayWidth;
         final int var27 = var24 - Mouse.getY() * var24 / this.mc.displayHeight - 1;
         int var28 = this.mc.gameSettings.limitFramerate;
         if (this.mc.theWorld != null) {
            this.mc.mcProfiler.startSection("level");
            int var14 = Math.min(Minecraft.B(), var28);
            var14 = Math.max(var14, 60);
            long var15 = System.nanoTime() - var2;
            long var31 = 4166374377702587042L ^ var4;
            long var17 = Math.max(1000000000 / var14 / 4 - var15, 0L);
            this.renderWorld(var1, System.nanoTime() + var17);
            int var19 = this.mc.gameSettings.guiScale;
            this.mc.gameSettings.guiScale = 1;
            Astolfo.INSTANCE.getEventBus().post(new RenderUnscaledEvent());
            this.mc.gameSettings.guiScale = var19;
            if (OpenGlHelper.shadersSupported) {
               this.mc.renderGlobal.renderEntityOutlineFramebuffer();
               if (this.theShaderGroup != null && this.useShader) {
                  GlStateManager.matrixMode(5890);
                  GlStateManager.pushMatrix();
                  GlStateManager.loadIdentity();
                  this.theShaderGroup.t(var1);
                  GlStateManager.popMatrix();
               }

               this.mc.getFramebuffer().bindFramebuffer(true);
            }

            this.Z4 = System.nanoTime();
            this.mc.mcProfiler.endStartSection("gui");
            if (!this.mc.gameSettings.Zn || this.mc.currentScreen != null) {
               GlStateManager.alphaFunc(516, 0.1F);
               this.mc.ingameGUI.renderGameOverlay(var1);
               if (this.mc.gameSettings.ZQ && !this.mc.gameSettings.n) {
                  Config.drawFps();
               }

               if (this.mc.gameSettings.n) {
                  Lagometer.showLagometer(var21);
               }
            }

            this.mc.mcProfiler.endSection();
         } else {
            GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            long var10000 = 3750456705327409363L ^ var4;
            GlStateManager.matrixMode(5888);
            GlStateManager.loadIdentity();
            this.setupOverlayRendering();
            this.Z4 = System.nanoTime();
            TileEntityRendererDispatcher.instance.renderEngine = this.mc.getTextureManager();
         }

         if (this.mc.currentScreen != null) {
            GlStateManager.clear(256);

            try {
               if (Reflector.ForgeHooksClient_drawScreen.exists()) {
                  Reflector.callVoid(Reflector.ForgeHooksClient_drawScreen, this.mc.currentScreen, var26, var27, var1);
               } else {
                  this.mc.currentScreen.drawScreen(var26, var27, var1);
               }
            } catch (Throwable var20) {
               CrashReport var30 = CrashReport.makeCrashReport(var20, "Rendering screen");
               CrashReportCategory var16 = var30.makeCategory("Screen render details");
               var16.addCrashSectionCallable("Screen name", new EntityRenderer.l9y(this));
               var16.addCrashSectionCallable("Mouse location", new Callable<String>() {
                  private static final long a = 90440530419737L;

                  public String call() throws Exception {
                     return String.format("Scaled: (%d, %d). Absolute: (%d, %d)", var26, var27, Mouse.getX(), Mouse.getY());
                  }
               });
               var16.addCrashSectionCallable(
                  "Screen size",
                  new Callable<String>() {
                     private static final long a = 61456425806550L;

                     public String call() throws Exception {
                        return String.format(
                           "Scaled: (%d, %d). Absolute: (%d, %d). Scale factor of %d",
                           var21.getScaledWidth(),
                           var21.getScaledHeight(),
                           EntityRenderer.this.mc.displayWidth,
                           EntityRenderer.this.mc.displayHeight,
                           var21.getScaleFactor()
                        );
                     }
                  }
               );
               throw new ReportedException(var30);
            }
         }

         if (Astolfo.INSTANCE.getEventBus() != null) {
            Astolfo.INSTANCE.getEventBus().post(new RenderAfterScreenEvent(var22, var24));
         }
      }

      this.frameFinish();
      this.waitForServerThread();
      Lagometer.updateLagometer();
      if (this.mc.gameSettings.lJ) {
         this.mc.gameSettings.Zj = true;
      }
   }

   public void renderStreamIndicator(float var1) {
      this.setupOverlayRendering();
      this.mc.ingameGUI.renderStreamIndicator(new ScaledResolution(this.mc));
   }

   private boolean N() {
      if (!this.Z7) {
         return false;
      }

      Entity var4 = this.mc.getRenderViewEntity();
      boolean var5 = var4 instanceof EntityPlayer && !this.mc.gameSettings.Zn;
      if (var5 && !((EntityPlayer)var4).capabilities.allowEdit) {
         ItemStack var6 = ((EntityPlayer)var4).getCurrentEquippedItem();
         if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            BlockPos var7 = this.mc.objectMouseOver.getBlockPos();
            IBlockState var8 = this.mc.theWorld.getBlockState(var7);
            Block var9 = var8.getBlock();
            if (this.mc.playerController.getCurrentGameType() == WorldSettings.GameType.SPECTATOR) {
               var5 = ReflectorForge.blockHasTileEntity(var8) && this.mc.theWorld.getTileEntity(var7) instanceof IInventory;
            } else {
               var5 = var6 != null && (var6.canDestroy(var9) || var6.canPlaceOn(var9));
            }
         }
      }

      return var5;
   }

   private void renderWorldDirections(float var1) {
      long var2 = ab ^ 17441971639063L;
      if (this.mc.gameSettings.n && !this.mc.gameSettings.Zn && !this.mc.thePlayer.w() && !this.mc.gameSettings.reducedDebugInfo) {
         Entity var5 = this.mc.getRenderViewEntity();
         GlStateManager.enableBlend();
         long var10000 = 2585288838404813119L ^ var2;
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
         GL11.glLineWidth(1.0F);
         var10000 = 3750403355709458083L ^ var2;
         GlStateManager.disableTexture2D();
         GlStateManager.depthMask(false);
         GlStateManager.pushMatrix();
         GlStateManager.matrixMode(5888);
         GlStateManager.loadIdentity();
         this.orientCamera(var1);
         GlStateManager.translate(0.0F, var5.getEyeHeight(), 0.0F);
         RenderGlobal.drawOutlinedBoundingBox(new AxisAlignedBB(0.0, 0.0, 0.0, 0.005, 1.0E-4, 1.0E-4), 255, 0, 0, 255);
         RenderGlobal.drawOutlinedBoundingBox(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0E-4, 1.0E-4, 0.005), 0, 0, 255, 255);
         RenderGlobal.drawOutlinedBoundingBox(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0E-4, 0.0033, 1.0E-4), 0, 255, 0, 255);
         GlStateManager.popMatrix();
         GlStateManager.depthMask(true);
         GlStateManager.enableTexture2D();
         GlStateManager.disableBlend();
      }
   }

   public void renderWorld(float var1, long var2) {
      long var4 = ab ^ 45453690627919L;
      this.updateLightmap(var1);
      if (this.mc.getRenderViewEntity() == null) {
         this.mc.setRenderViewEntity(this.mc.thePlayer);
      }

      this.getMouseOver(var1);
      if (Config.isShaders()) {
         Shaders.beginRender(this.mc, var1, var2);
      }

      GlStateManager.enableDepth();
      GlStateManager.enableAlpha();
      long var10000 = 8710763311798112729L ^ var4;
      GlStateManager.alphaFunc(516, 0.1F);
      this.mc.mcProfiler.startSection("center");
      if (this.mc.gameSettings.anaglyph) {
         A = 0;
         GlStateManager.colorMask(false, true, true, false);
         this.renderWorldPass(0, var1, var2);
         A = 1;
         GlStateManager.colorMask(true, false, false, false);
         this.renderWorldPass(1, var1, var2);
         GlStateManager.colorMask(true, true, true, false);
      } else {
         this.renderWorldPass(2, var1, var2);
      }

      Astolfo.INSTANCE.getEventBus().post(new WorldRenderedEvent());
      this.mc.mcProfiler.endSection();
   }

   public void renderWorldPass(int var1, float var2, long var3) {
      long var5 = ab ^ 22300767793757L;
      boolean var9 = ModuleManager.I.isEnabled();
      boolean var10 = Config.isShaders();
      if (var10) {
         Shaders.beginRenderPass(var1, var2, var3);
      }

      RenderGlobal var11 = this.mc.renderGlobal;
      EffectRenderer var12 = this.mc.effectRenderer;
      boolean var13 = this.N();
      GlStateManager.enableCull();
      this.mc.mcProfiler.endStartSection("clear");
      if (var10) {
         Shaders.setViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
      } else {
         GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
      }

      this.updateFogColor(var2);
      GlStateManager.clear(C);
      if (var10) {
         Shaders.clearRenderBuffer();
      }

      this.mc.mcProfiler.endStartSection("camera");
      this.setupCameraTransform(var2, var1);
      if (var10) {
         Shaders.setCamera(var2);
      }

      ActiveRenderInfo.updateRenderInfo(this.mc.thePlayer, this.mc.gameSettings.thirdPersonView == 2);
      this.mc.mcProfiler.endStartSection("frustum");
      ClippingHelperImpl.getInstance();
      this.mc.mcProfiler.endStartSection("culling");
      Frustum var14 = new Frustum();
      Entity var15 = this.mc.getRenderViewEntity();
      double var16 = var15.lastTickPosX + (var15.posX - var15.lastTickPosX) * var2;
      double var18 = var15.lastTickPosY + (var15.posY - var15.lastTickPosY) * var2;
      double var20 = var15.lastTickPosZ + (var15.posZ - var15.lastTickPosZ) * var2;
      if (var10) {
         ShadersRender.setFrustrumPosition(var14, var16, var18, var20);
      } else {
         var14.setPosition(var16, var18, var20);
      }

      if ((Config.K() || Config.u() || Config.s()) && !Shaders.isShadowPass) {
         this.setupFog(-1, var2);
         this.mc.mcProfiler.endStartSection("sky");
         long var10000 = 2591644373877917181L ^ var5;
         GlStateManager.matrixMode(5889);
         GlStateManager.loadIdentity();
         Project.gluPerspective(this.getFOVModifier(var2, true), (float)this.mc.displayWidth / this.mc.displayHeight, 0.05F, this.clipDistance);
         GlStateManager.matrixMode(ModuleManager.e);
         if (var10) {
            Shaders.beginSky();
         }

         var11.renderSky(var2, var1);
         if (var10) {
            Shaders.a();
         }

         GlStateManager.matrixMode(5889);
         GlStateManager.loadIdentity();
         Project.gluPerspective(this.getFOVModifier(var2, true), (float)this.mc.displayWidth / this.mc.displayHeight, 0.05F, this.clipDistance);
         GlStateManager.matrixMode(ModuleManager.e);
      } else {
         GlStateManager.disableBlend();
      }

      this.setupFog(0, var2);
      GlStateManager.shadeModel(7425);
      if (var15.posY + var15.getEyeHeight() < 128.0 + this.mc.gameSettings.ofCloudsHeight * 128.0F) {
         this.renderCloudsCheck(var11, var2, var1);
      }

      this.mc.mcProfiler.endStartSection("prepareterrain");
      this.setupFog(0, var2);
      this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
      RenderHelper.disableStandardItemLighting();
      this.mc.mcProfiler.endStartSection("terrain_setup");
      if (var10) {
         ShadersRender.setupTerrain(var11, var15, var2, var14, this.R++, !ModuleManager.I.isEnabled());
      } else {
         var11.setupTerrain(var15, var2, var14, this.R++, !ModuleManager.I.isEnabled());
      }

      if (var1 == 0 || var1 == 2) {
         this.mc.mcProfiler.endStartSection("updatechunks");
         Lagometer.timerChunkUpload.start();
         this.mc.renderGlobal.updateChunks(var3);
         Lagometer.timerChunkUpload.end();
      }

      this.mc.mcProfiler.endStartSection("terrain");
      Lagometer.timerTerrain.start();
      if (this.mc.gameSettings.R && var1 > 0) {
         this.mc.mcProfiler.endStartSection("finish");
         GL11.glFinish();
         this.mc.mcProfiler.endStartSection("terrain");
      }

      GlStateManager.matrixMode(ModuleManager.e);
      GlStateManager.pushMatrix();
      GlStateManager.disableAlpha();
      if (var10) {
         ShadersRender.o();
      }

      boolean var22 = ModuleManager.t.isEnabled();
      if (var22) {
         CaveFinder.j();
      }

      var11.renderBlockLayer(EnumWorldBlockLayer.SOLID, var2, var1, var15);
      GlStateManager.enableAlpha();
      if (var10) {
         ShadersRender.z();
      }

      if (!var22) {
         var11.renderBlockLayer(EnumWorldBlockLayer.CUTOUT_MIPPED, var2, var1, var15);
      }

      this.mc.getTextureManager().getTexture(TextureMap.locationBlocksTexture).setBlurMipmap(false, false);
      if (var10) {
         ShadersRender.I();
      }

      var11.renderBlockLayer(EnumWorldBlockLayer.CUTOUT, var2, var1, var15);
      this.mc.getTextureManager().getTexture(TextureMap.locationBlocksTexture).restoreLastBlurMipmap();
      if (var10) {
         ShadersRender.P();
      }

      if (var22) {
         CaveFinder.B();
      }

      Lagometer.timerTerrain.end();
      GlStateManager.shadeModel(7424);
      GlStateManager.alphaFunc(516, 0.1F);
      if (!this.ZL) {
         GlStateManager.matrixMode(5888);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         RenderHelper.enableStandardItemLighting();
         this.mc.mcProfiler.endStartSection("entities");
         if (Reflector.ForgeHooksClient_setRenderPass.exists()) {
            Reflector.callVoid(Reflector.ForgeHooksClient_setRenderPass, 0);
         }

         var11.renderEntities(var15, var14, var2);
         if (Reflector.ForgeHooksClient_setRenderPass.exists()) {
            Reflector.callVoid(Reflector.ForgeHooksClient_setRenderPass, -1);
         }

         RenderHelper.disableStandardItemLighting();
         this.disableLightmap();
         long var26 = 3750373062385559529L ^ var5;
         GlStateManager.matrixMode(5888);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         if (this.mc.objectMouseOver != null && var15.isInsideOfMaterial(Material.water) && var13) {
            EntityPlayer var23 = (EntityPlayer)var15;
            GlStateManager.disableAlpha();
            this.mc.mcProfiler.endStartSection("outline");
            if ((
                  !Reflector.ForgeHooksClient_onDrawBlockHighlight.exists()
                     || !Reflector.callBoolean(
                        Reflector.ForgeHooksClient_onDrawBlockHighlight, var11, var23, this.mc.objectMouseOver, 0, var23.getHeldItem(), var2
                     )
               )
               && !this.mc.gameSettings.Zn) {
               var11.drawSelectionBox(var23, this.mc.objectMouseOver, 0, var2);
            }

            GlStateManager.enableAlpha();
         }
      }

      GlStateManager.matrixMode(ModuleManager.e);
      GlStateManager.popMatrix();
      if (var13 && this.mc.objectMouseOver != null && !var15.isInsideOfMaterial(Material.water)) {
         EntityPlayer var24 = (EntityPlayer)var15;
         GlStateManager.disableAlpha();
         this.mc.mcProfiler.endStartSection("outline");
         if ((
               !Reflector.ForgeHooksClient_onDrawBlockHighlight.exists()
                  || !Reflector.callBoolean(
                     Reflector.ForgeHooksClient_onDrawBlockHighlight, var11, var24, this.mc.objectMouseOver, 0, var24.getHeldItem(), var2
                  )
            )
            && !this.mc.gameSettings.Zn) {
            var11.drawSelectionBox(var24, this.mc.objectMouseOver, 0, var2);
         }

         GlStateManager.enableAlpha();
      }

      if (!var11.damagedBlocks.isEmpty()) {
         this.mc.mcProfiler.endStartSection("destroyProgress");
         GlStateManager.enableBlend();
         long var27 = 1514772171860492431L ^ var5;
         GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0);
         this.mc.getTextureManager().getTexture(TextureMap.locationBlocksTexture).setBlurMipmap(false, false);
         var11.drawBlockDamageTexture(Tessellator.getInstance(), Tessellator.getInstance().getWorldRenderer(), var15, var2);
         this.mc.getTextureManager().getTexture(TextureMap.locationBlocksTexture).restoreLastBlurMipmap();
         GlStateManager.disableBlend();
      }

      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.disableBlend();
      if (!this.ZL) {
         this.enableLightmap();
         this.mc.mcProfiler.endStartSection("litParticles");
         if (var10) {
            Shaders.UT();
         }

         var12.renderLitParticles(var15, var2);
         RenderHelper.disableStandardItemLighting();
         this.setupFog(0, var2);
         this.mc.mcProfiler.endStartSection("particles");
         if (var10) {
            Shaders.S();
         }

         var12.renderParticles(var15, var2);
         if (var10) {
            Shaders.U();
         }

         this.disableLightmap();
      }

      GlStateManager.depthMask(false);
      GlStateManager.enableCull();
      this.mc.mcProfiler.endStartSection("weather");
      if (var10) {
         Shaders.beginWeather();
      }

      this.renderRainSnow(var2);
      if (var10) {
         Shaders.i();
      }

      GlStateManager.depthMask(true);
      var11.renderWorldBorder(var15, var2);
      if (var10) {
         ShadersRender.B(this, var2, var1);
         Shaders.preWater();
      }

      GlStateManager.disableBlend();
      GlStateManager.enableCull();
      long var28 = 1514772171860492431L ^ var5;
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.alphaFunc(516, 0.1F);
      this.setupFog(0, var2);
      var28 = 6841559995089646377L ^ var5;
      GlStateManager.enableBlend();
      GlStateManager.depthMask(false);
      this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
      GlStateManager.shadeModel(7425);
      this.mc.mcProfiler.endStartSection("translucent");
      if (var10) {
         Shaders.beginWater();
      }

      var11.renderBlockLayer(EnumWorldBlockLayer.TRANSLUCENT, var2, var1, var15);
      if (var10) {
         Shaders.u();
      }

      if (Reflector.ForgeHooksClient_setRenderPass.exists() && !this.ZL) {
         RenderHelper.enableStandardItemLighting();
         this.mc.mcProfiler.endStartSection("entities");
         var28 = 1514772171860492431L ^ var5;
         Reflector.callVoid(Reflector.ForgeHooksClient_setRenderPass, 1);
         this.mc.renderGlobal.renderEntities(var15, var14, var2);
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
         Reflector.callVoid(Reflector.ForgeHooksClient_setRenderPass, -1);
         RenderHelper.disableStandardItemLighting();
      }

      GlStateManager.shadeModel(7424);
      GlStateManager.depthMask(true);
      GlStateManager.enableCull();
      GlStateManager.disableBlend();
      GlStateManager.disableFog();
      if (var15.posY + var15.getEyeHeight() >= 128.0 + this.mc.gameSettings.ofCloudsHeight * 128.0F) {
         this.mc.mcProfiler.endStartSection("aboveClouds");
         this.renderCloudsCheck(var11, var2, var1);
      }

      if (Reflector.ForgeHooksClient_dispatchRenderLast.exists()) {
         this.mc.mcProfiler.endStartSection("forge_render_last");
         Reflector.callVoid(Reflector.ForgeHooksClient_dispatchRenderLast, var11, var2);
      }

      if (var1 == 2) {
         Astolfo.INSTANCE.getEventBus().post(new RenderEvent(RenderType.WORLD, var2, var16, var18, var20));
      }

      this.mc.mcProfiler.endStartSection("hand");
      boolean var25 = ReflectorForge.renderFirstPersonHand(this.mc.renderGlobal, var2, var1);
      if (!var25 && this.V && !Shaders.isShadowPass) {
         if (var10) {
            ShadersRender.l(this, var2, var1);
            Shaders.renderFinal();
         }

         GlStateManager.clear(256);
         if (var10) {
            ShadersRender.t(this, var2, var1);
         } else {
            this.renderHand(var2, var1);
         }

         this.renderWorldDirections(var2);
      }

      if (var10) {
         Shaders.endRender();
      }

      RenderEntityItem.w = System.nanoTime();
   }

   private void renderCloudsCheck(RenderGlobal var1, float var2, int var3) {
      long var4 = ab ^ 68787490973403L;
      if (this.mc.gameSettings.renderDistanceChunks >= 4 && !Config.isCloudsOff() && Shaders.shouldRenderClouds(this.mc.gameSettings)) {
         this.mc.mcProfiler.endStartSection("clouds");
         GlStateManager.matrixMode(5889);
         GlStateManager.loadIdentity();
         long var10000 = 510502869545917421L ^ var4;
         Project.gluPerspective(this.getFOVModifier(var2, true), (float)this.mc.displayWidth / this.mc.displayHeight, 0.05F, this.clipDistance * 4.0F);
         GlStateManager.matrixMode(5888);
         GlStateManager.pushMatrix();
         var10000 = 2591667740519649659L ^ var4;
         this.setupFog(0, var2);
         var1.renderClouds(var2, var3);
         GlStateManager.disableFog();
         GlStateManager.popMatrix();
         GlStateManager.matrixMode(5889);
         GlStateManager.loadIdentity();
         var10000 = 3750419497548133231L ^ var4;
         Project.gluPerspective(this.getFOVModifier(var2, true), (float)this.mc.displayWidth / this.mc.displayHeight, 0.05F, this.clipDistance);
         GlStateManager.matrixMode(5888);
      }
   }

   private void addRainParticles() {
      long var1 = ab ^ 38969763802216L;
      float var4 = this.mc.theWorld.getRainStrength(1.0F);
      if (!Config.isRainFancy()) {
         var4 /= 2.0F;
      }

      if (var4 != 0.0F && Config.isRainSplash()) {
         this.random.setSeed(this.Zn * 312987231L);
         Entity var5 = this.mc.getRenderViewEntity();
         long var10000 = 2821679848249053153L ^ var1;
         WorldClient var6 = this.mc.theWorld;
         BlockPos var7 = new BlockPos(var5);
         byte var8 = 10;
         double var9 = 0.0;
         double var11 = 0.0;
         double var13 = 0.0;
         int var15 = 0;
         int var16 = (int)(100.0F * var4 * var4);
         if (this.mc.gameSettings.particleSetting == 1) {
            var16 >>= 1;
         } else if (this.mc.gameSettings.particleSetting == 2) {
            var16 = 0;
         }

         for (int var17 = 0; var17 < var16; var17++) {
            BlockPos var18 = var6.getPrecipitationHeight(
               var7.add(this.random.nextInt(var8) - this.random.nextInt(var8), 0, this.random.nextInt(var8) - this.random.nextInt(var8))
            );
            BiomeGenBase var19 = var6.getBiomeGenForCoords(var18);
            BlockPos var20 = var18.down();
            Block var21 = var6.getBlockState(var20).getBlock();
            if (var18.getY() <= var7.getY() + var8 && var18.getY() >= var7.getY() - var8 && var19.canRain() && var19.getFloatTemperature(var18) >= 0.15F) {
               double var22 = this.random.nextDouble();
               double var24 = this.random.nextDouble();
               if (var21.getMaterial() == Material.lava) {
                  this.mc
                     .theWorld
                     .spawnParticle(
                        EnumParticleTypes.SMOKE_NORMAL,
                        var18.getX() + var22,
                        var18.getY() + 0.1F - var21.getBlockBoundsMinY(),
                        var18.getZ() + var24,
                        0.0,
                        0.0,
                        0.0
                     );
               } else if (var21.getMaterial() != Material.air) {
                  var21.setBlockBoundsBasedOnState(var6, var20);
                  if (this.random.nextInt(++var15) == 0) {
                     var9 = var20.getX() + var22;
                     var11 = var20.getY() + 0.1F + var21.getBlockBoundsMaxY() - 1.0;
                     var13 = var20.getZ() + var24;
                  }

                  this.mc
                     .theWorld
                     .spawnParticle(
                        EnumParticleTypes.WATER_DROP,
                        var20.getX() + var22,
                        var20.getY() + 0.1F + var21.getBlockBoundsMaxY(),
                        var20.getZ() + var24,
                        0.0,
                        0.0,
                        0.0
                     );
               }
            }
         }

         if (var15 > 0 && this.random.nextInt(3) < this.T++) {
            this.T = 0;
            if (var11 > var7.getY() + 1 && var6.getPrecipitationHeight(var7).getY() > MathHelper.floor_float(var7.getY())) {
               this.mc.theWorld.playSound(var9, var11, var13, "ambient.weather.rain", 0.1F, 0.5F, false);
            } else {
               this.mc.theWorld.playSound(var9, var11, var13, "ambient.weather.rain", 0.2F, 1.0F, false);
            }
         }
      }
   }

   public void renderRainSnow(float var1) {
      long var2 = ab ^ 19719073196369L;
      if (Reflector.ForgeWorldProvider_getWeatherRenderer.exists()) {
         WorldProvider var5 = this.mc.theWorld.provider;
         Object var6 = Reflector.call(var5, Reflector.ForgeWorldProvider_getWeatherRenderer);
         if (var6 != null) {
            Reflector.callVoid(var6, Reflector.IRenderHandler_render, var1, this.mc.theWorld, this.mc);
            return;
         }
      }

      float var52 = this.mc.theWorld.getRainStrength(var1);
      if (var52 > 0.0F) {
         if (Config.isRainOff()) {
            return;
         }

         this.enableLightmap();
         Entity var53 = this.mc.getRenderViewEntity();
         long var10000 = 1514767365641444227L ^ var2;
         WorldClient var7 = this.mc.theWorld;
         int var8 = MathHelper.floor_double(var53.posX);
         int var9 = MathHelper.floor_double(var53.posY);
         int var10 = MathHelper.floor_double(var53.posZ);
         Tessellator var11 = Tessellator.getInstance();
         WorldRenderer var12 = var11.getWorldRenderer();
         GlStateManager.disableCull();
         GL11.glNormal3f(0.0F, 1.0F, 0.0F);
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
         GlStateManager.alphaFunc(516, 0.1F);
         double var13 = var53.lastTickPosX + (var53.posX - var53.lastTickPosX) * var1;
         double var15 = var53.lastTickPosY + (var53.posY - var53.lastTickPosY) * var1;
         double var17 = var53.lastTickPosZ + (var53.posZ - var53.lastTickPosZ) * var1;
         int var19 = MathHelper.floor_double(var15);
         byte var20 = 5;
         if (Config.isRainFancy()) {
            var20 = 10;
         }

         byte var21 = -1;
         float var22 = this.Zn + var1;
         var12.setTranslation(-var13, -var15, -var17);
         if (Config.isRainFancy()) {
            var20 = 10;
         }

         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         BlockPos.MutableBlockPos var23 = new BlockPos.MutableBlockPos();

         for (int var24 = var10 - var20; var24 <= var10 + var20; var24++) {
            for (int var25 = var8 - var20; var25 <= var8 + var20; var25++) {
               int var26 = (var24 - var10 + 16) * 32 + var25 - var8 + 16;
               double var27 = this.rainXCoords[var26] * 0.5;
               double var29 = this.rainYCoords[var26] * 0.5;
               var23.set(var25, 0, var24);
               BiomeGenBase var31 = var7.getBiomeGenForCoords(var23);
               if (var31.canRain() || var31.getEnableSnow()) {
                  int var32 = var7.getPrecipitationHeight(var23).getY();
                  int var33 = var9 - var20;
                  int var34 = var9 + var20;
                  if (var33 < var32) {
                     var33 = var32;
                  }

                  if (var34 < var32) {
                     var34 = var32;
                  }

                  int var35 = var32;
                  if (var32 < var19) {
                     var35 = var19;
                  }

                  if (var33 != var34) {
                     this.random.setSeed(var25 * var25 * 3121 + var25 * 45238971 ^ var24 * var24 * 418711 + var24 * 13761);
                     var23.set(var25, var33, var24);
                     float var36 = var31.getFloatTemperature(var23);
                     if (var7.getWorldChunkManager().getTemperatureAtHeight(var36, var32) >= 0.15F) {
                        if (var21 != 0) {
                           if (var21 >= 0) {
                              var11.draw();
                           }

                           var21 = 0;
                           this.mc.getTextureManager().bindTexture(locationRainPng);
                           var12.begin(7, DefaultVertexFormats.T);
                        }

                        double var37 = ((double)(this.Zn + var25 * var25 * 3121 + var25 * 45238971 + var24 * var24 * 418711 + var24 * 13761 & 31) + var1)
                           / 32.0
                           * (3.0 + this.random.nextDouble());
                        double var39 = var25 + 0.5F - var53.posX;
                        double var41 = var24 + 0.5F - var53.posZ;
                        float var43 = MathHelper.sqrt_double(var39 * var39 + var41 * var41) / var20;
                        float var44 = ((1.0F - var43 * var43) * 0.5F + 0.5F) * var52;
                        var23.set(var25, var35, var24);
                        int var45 = var7.getCombinedLight(var23, 0);
                        int var46 = var45 >> 16 & 65535;
                        int var47 = var45 & 65535;
                        var12.pos(var25 - var27 + 0.5, var33, var24 - var29 + 0.5)
                           .tex(0.0, var33 * 0.25 + var37)
                           .color(1.0F, 1.0F, 1.0F, var44)
                           .lightmap(var46, var47)
                           .endVertex();
                        var12.pos(var25 + var27 + 0.5, var33, var24 + var29 + 0.5)
                           .tex(1.0, var33 * 0.25 + var37)
                           .color(1.0F, 1.0F, 1.0F, var44)
                           .lightmap(var46, var47)
                           .endVertex();
                        var12.pos(var25 + var27 + 0.5, var34, var24 + var29 + 0.5)
                           .tex(1.0, var34 * 0.25 + var37)
                           .color(1.0F, 1.0F, 1.0F, var44)
                           .lightmap(var46, var47)
                           .endVertex();
                        var12.pos(var25 - var27 + 0.5, var34, var24 - var29 + 0.5)
                           .tex(0.0, var34 * 0.25 + var37)
                           .color(1.0F, 1.0F, 1.0F, var44)
                           .lightmap(var46, var47)
                           .endVertex();
                     } else {
                        if (var21 != 1) {
                           if (var21 >= 0) {
                              var11.draw();
                           }

                           var21 = 1;
                           this.mc.getTextureManager().bindTexture(locationSnowPng);
                           var12.begin(7, DefaultVertexFormats.T);
                        }

                        double var54 = ((this.Zn & 511) + var1) / 512.0F;
                        double var55 = this.random.nextDouble() + var22 * 0.01 * (float)this.random.nextGaussian();
                        double var56 = this.random.nextDouble() + var22 * (float)this.random.nextGaussian() * 0.001;
                        double var57 = var25 + 0.5F - var53.posX;
                        double var58 = var24 + 0.5F - var53.posZ;
                        float var59 = MathHelper.sqrt_double(var57 * var57 + var58 * var58) / var20;
                        float var48 = ((1.0F - var59 * var59) * 0.3F + 0.5F) * var52;
                        var23.set(var25, var35, var24);
                        int var49 = (var7.getCombinedLight(var23, 0) * 3 + 15728880) / 4;
                        int var50 = var49 >> 16 & 65535;
                        int var51 = var49 & 65535;
                        var12.pos(var25 - var27 + 0.5, var33, var24 - var29 + 0.5)
                           .tex(0.0 + var55, var33 * 0.25 + var54 + var56)
                           .color(1.0F, 1.0F, 1.0F, var48)
                           .lightmap(var50, var51)
                           .endVertex();
                        var12.pos(var25 + var27 + 0.5, var33, var24 + var29 + 0.5)
                           .tex(1.0 + var55, var33 * 0.25 + var54 + var56)
                           .color(1.0F, 1.0F, 1.0F, var48)
                           .lightmap(var50, var51)
                           .endVertex();
                        var12.pos(var25 + var27 + 0.5, var34, var24 + var29 + 0.5)
                           .tex(1.0 + var55, var34 * 0.25 + var54 + var56)
                           .color(1.0F, 1.0F, 1.0F, var48)
                           .lightmap(var50, var51)
                           .endVertex();
                        var12.pos(var25 - var27 + 0.5, var34, var24 - var29 + 0.5)
                           .tex(0.0 + var55, var34 * 0.25 + var54 + var56)
                           .color(1.0F, 1.0F, 1.0F, var48)
                           .lightmap(var50, var51)
                           .endVertex();
                     }
                  }
               }
            }
         }

         if (var21 >= 0) {
            var11.draw();
         }

         var12.setTranslation(0.0, 0.0, 0.0);
         GlStateManager.enableCull();
         var10000 = 7938689958138754267L ^ var2;
         GlStateManager.disableBlend();
         GlStateManager.alphaFunc(516, 0.1F);
         this.disableLightmap();
      }
   }

   public void setupOverlayRendering() {
      ScaledResolution var3 = new ScaledResolution(this.mc);
      GlStateManager.clear(256);
      GlStateManager.matrixMode(5889);
      GlStateManager.loadIdentity();
      GlStateManager.ortho(0.0, var3.getScaledWidth_double(), var3.getScaledHeight_double(), 0.0, 1000.0, 3000.0);
      GlStateManager.matrixMode(5888);
      GlStateManager.loadIdentity();
      GlStateManager.translate(0.0F, 0.0F, -2000.0F);
   }

   private void updateFogColor(float var1) {
      WorldClient var5 = this.mc.theWorld;
      Entity var6 = this.mc.getRenderViewEntity();
      float var7 = 0.25F + 0.75F * this.mc.gameSettings.renderDistanceChunks / 32.0F;
      var7 = 1.0F - (float)Math.pow(var7, 0.25);
      Vec3 var8 = var5.getSkyColor(this.mc.getRenderViewEntity(), var1);
      var8 = CustomColors.getWorldSkyColor(var8, var5, this.mc.getRenderViewEntity(), var1);
      float var9 = (float)var8.xCoord;
      float var10 = (float)var8.yCoord;
      float var11 = (float)var8.zCoord;
      Vec3 var12 = var5.getFogColor(var1);
      var12 = CustomColors.getWorldFogColor(var12, var5, this.mc.getRenderViewEntity(), var1);
      this.ZQ = (float)var12.xCoord;
      this.N = (float)var12.yCoord;
      this.ZI = (float)var12.zCoord;
      if (this.mc.gameSettings.renderDistanceChunks >= 4) {
         double var13 = -1.0;
         Vec3 var15 = MathHelper.sin(var5.getCelestialAngleRadians(var1)) > 0.0F ? new Vec3(var13, 0.0, 0.0) : new Vec3(1.0, 0.0, 0.0);
         float var16 = (float)var6.getLook(var1).dotProduct(var15);
         if (var16 < 0.0F) {
            var16 = 0.0F;
         }

         if (var16 > 0.0F) {
            float[] var17 = var5.provider.calcSunriseSunsetColors(var5.getCelestialAngle(var1), var1);
            if (var17 != null) {
               var16 *= var17[3];
               this.ZQ = this.ZQ * (1.0F - var16) + var17[0] * var16;
               this.N = this.N * (1.0F - var16) + var17[1] * var16;
               this.ZI = this.ZI * (1.0F - var16) + var17[2] * var16;
            }
         }
      }

      this.ZQ = this.ZQ + (var9 - this.ZQ) * var7;
      this.N = this.N + (var10 - this.N) * var7;
      this.ZI = this.ZI + (var11 - this.ZI) * var7;
      float var27 = var5.getRainStrength(var1);
      if (var27 > 0.0F) {
         float var14 = 1.0F - var27 * 0.5F;
         float var29 = 1.0F - var27 * 0.4F;
         this.ZQ *= var14;
         this.N *= var14;
         this.ZI *= var29;
      }

      float var28 = var5.getThunderStrength(var1);
      if (var28 > 0.0F) {
         float var30 = 1.0F - var28 * 0.5F;
         this.ZQ *= var30;
         this.N *= var30;
         this.ZI *= var30;
      }

      Block var31 = ActiveRenderInfo.getBlockAtEntityViewpoint(this.mc.theWorld, var6, var1);
      if (this.s) {
         Vec3 var33 = var5.getCloudColour(var1);
         this.ZQ = (float)var33.xCoord;
         this.N = (float)var33.yCoord;
         this.ZI = (float)var33.zCoord;
      } else if (var31.getMaterial() == Material.water) {
         float var34 = EnchantmentHelper.getRespiration(var6) * 0.2F;
         if (var6 instanceof EntityLivingBase && ((EntityLivingBase)var6).isPotionActive(Potion.waterBreathing)) {
            var34 = var34 * 0.3F + 0.6F;
         }

         this.ZQ = 0.02F + var34;
         this.N = 0.02F + var34;
         this.ZI = 0.2F + var34;
         Vec3 var36 = CustomColors.S(
            this.mc.theWorld, this.mc.getRenderViewEntity().posX, this.mc.getRenderViewEntity().posY + 1.0, this.mc.getRenderViewEntity().posZ
         );
         if (var36 != null) {
            this.ZQ = (float)var36.xCoord;
            this.N = (float)var36.yCoord;
            this.ZI = (float)var36.zCoord;
         }
      } else if (var31.getMaterial() == Material.lava) {
         this.ZQ = 0.6F;
         this.N = 0.1F;
         this.ZI = 0.0F;
      }

      float var35 = this.U + (this.X - this.U) * var1;
      this.ZQ *= var35;
      this.N *= var35;
      this.ZI *= var35;
      double var37 = var5.provider.getVoidFogYFactor();
      double var19 = (var6.lastTickPosY + (var6.posY - var6.lastTickPosY) * var1) * var37;
      if (var6 instanceof EntityLivingBase && ((EntityLivingBase)var6).isPotionActive(Potion.blindness)) {
         int var21 = ((EntityLivingBase)var6).getActivePotionEffect(Potion.blindness).getDuration();
         if (var21 < 20) {
            var19 *= 1.0F - var21 / 20.0F;
         } else {
            var19 = 0.0;
         }
      }

      if (var19 < 1.0) {
         if (var19 < 0.0) {
            var19 = 0.0;
         }

         var19 *= var19;
         this.ZQ = (float)(this.ZQ * var19);
         this.N = (float)(this.N * var19);
         this.ZI = (float)(this.ZI * var19);
      }

      if (this.z > 0.0F) {
         float var39 = this.Zv + (this.z - this.Zv) * var1;
         this.ZQ = this.ZQ * (1.0F - var39) + this.ZQ * 0.7F * var39;
         this.N = this.N * (1.0F - var39) + this.N * 0.6F * var39;
         this.ZI = this.ZI * (1.0F - var39) + this.ZI * 0.6F * var39;
      }

      if (var6 instanceof EntityLivingBase && ((EntityLivingBase)var6).isPotionActive(Potion.nightVision)) {
         float var40 = this.getNightVisionBrightness((EntityLivingBase)var6, var1);
         float var22 = 1.0F / this.ZQ;
         if (var22 > 1.0F / this.N) {
            var22 = 1.0F / this.N;
         }

         if (var22 > 1.0F / this.ZI) {
            var22 = 1.0F / this.ZI;
         }

         this.ZQ = this.ZQ * (1.0F - var40) + this.ZQ * var22 * var40;
         this.N = this.N * (1.0F - var40) + this.N * var22 * var40;
         this.ZI = this.ZI * (1.0F - var40) + this.ZI * var22 * var40;
      }

      if (this.mc.gameSettings.anaglyph) {
         float var41 = (this.ZQ * 30.0F + this.N * 59.0F + this.ZI * 11.0F) / 100.0F;
         float var43 = (this.ZQ * 30.0F + this.N * 70.0F) / 100.0F;
         float var23 = (this.ZQ * 30.0F + this.ZI * 70.0F) / 100.0F;
         this.ZQ = var41;
         this.N = var43;
         this.ZI = var23;
      }

      if (Reflector.EntityViewRenderEvent_FogColors_Constructor.exists()) {
         Object var42 = Reflector.newInstance(Reflector.EntityViewRenderEvent_FogColors_Constructor, this, var6, var31, var1, this.ZQ, this.N, this.ZI);
         Reflector.postForgeBusEvent(var42);
         this.ZQ = Reflector.getFieldValueFloat(var42, Reflector.EntityViewRenderEvent_FogColors_red, this.ZQ);
         this.N = Reflector.getFieldValueFloat(var42, Reflector.EntityViewRenderEvent_FogColors_green, this.N);
         this.ZI = Reflector.getFieldValueFloat(var42, Reflector.EntityViewRenderEvent_FogColors_blue, this.ZI);
      }

      Shaders.v(this.ZQ, this.N, this.ZI, 0.0F);
   }

   private void setupFog(int var1, float var2) {
      long var3 = ab ^ 10880606322796L;
      Entity var6 = this.mc.getRenderViewEntity();
      this.fogStandard = false;
      if (var6 instanceof EntityPlayer) {
         boolean var7 = ((EntityPlayer)var6).capabilities.isCreativeMode;
      }

      GL11.glFog(2918, this.setFogColorBuffer(this.ZQ, this.N, this.ZI, 1.0F));
      GL11.glNormal3f(0.0F, -1.0F, 0.0F);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      Block var8 = ActiveRenderInfo.getBlockAtEntityViewpoint(this.mc.theWorld, var6, var2);
      float var9 = -1.0F;
      if (Reflector.ForgeHooksClient_getFogDensity.exists()) {
         var9 = Reflector.callFloat(Reflector.ForgeHooksClient_getFogDensity, this, var6, var8, var2, 0.1F);
      }

      if (var9 >= 0.0F) {
         GlStateManager.setFogDensity(var9);
      } else if (var6 instanceof EntityLivingBase && ((EntityLivingBase)var6).isPotionActive(Potion.blindness)) {
         float var12 = 5.0F;
         int var11 = ((EntityLivingBase)var6).getActivePotionEffect(Potion.blindness).getDuration();
         if (var11 < 20) {
            var12 = 5.0F + (this.Q - 5.0F) * (1.0F - var11 / 20.0F);
         }

         if (Config.isShaders()) {
            Shaders.setFog(9729);
         } else {
            GlStateManager.setFog(9729);
         }

         if (var1 == -1) {
            GlStateManager.setFogStart(0.0F);
            GlStateManager.setFogEnd(var12 * 0.8F);
         } else {
            GlStateManager.setFogStart(var12 * 0.25F);
            GlStateManager.setFogEnd(var12);
         }

         if (GLContext.getCapabilities().GL_NV_fog_distance && Config.isFogFancy()) {
            GL11.glFogi(34138, 34139);
         }
      } else if (this.s) {
         if (Config.isShaders()) {
            Shaders.setFog(2048);
         } else {
            GlStateManager.setFog(2048);
         }

         GlStateManager.setFogDensity(0.1F);
      } else if (var8.getMaterial() == Material.water) {
         if (Config.isShaders()) {
            Shaders.setFog(2048);
         } else {
            GlStateManager.setFog(2048);
         }

         if (var6 instanceof EntityLivingBase && ((EntityLivingBase)var6).isPotionActive(Potion.waterBreathing)) {
            GlStateManager.setFogDensity(0.01F);
         } else {
            GlStateManager.setFogDensity(0.1F - EnchantmentHelper.getRespiration(var6) * 0.03F);
         }

         if (Config.V()) {
            GlStateManager.setFogDensity(0.02F);
         }
      } else if (var8.getMaterial() == Material.lava) {
         if (Config.isShaders()) {
            Shaders.setFog(2048);
         } else {
            GlStateManager.setFog(2048);
         }

         GlStateManager.setFogDensity(2.0F);
      } else {
         float var10 = this.Q;
         this.fogStandard = true;
         if (Config.isShaders()) {
            Shaders.setFog(9729);
         } else {
            GlStateManager.setFog(9729);
         }

         if (var1 == -1) {
            GlStateManager.setFogStart(0.0F);
            GlStateManager.setFogEnd(var10);
         } else {
            GlStateManager.setFogStart(var10 * Config.S());
            GlStateManager.setFogEnd(var10);
         }

         if (GLContext.getCapabilities().GL_NV_fog_distance) {
            if (Config.isFogFancy()) {
               GL11.glFogi(34138, 34139);
            }

            if (Config.isFogFast()) {
               GL11.glFogi(34138, 34140);
            }
         }

         if (this.mc.theWorld.provider.doesXZShowFog((int)var6.posX, (int)var6.posZ)) {
            GlStateManager.setFogStart(var10 * 0.05F);
            GlStateManager.setFogEnd(var10);
         }

         if (Reflector.ForgeHooksClient_onFogRender.exists()) {
            Reflector.callVoid(Reflector.ForgeHooksClient_onFogRender, this, var6, var8, var2, var1, var10);
         }
      }

      if (ModuleManager.X.isEnabled() && NoEffect.noFog.getValue()) {
         GlStateManager.setFogDensity(0.0F);
      }

      GlStateManager.enableColorMaterial();
      GlStateManager.enableFog();
      long var10000 = 869995111203382790L ^ var3;
      GlStateManager.colorMaterial(1028, 4608);
   }

   private FloatBuffer setFogColorBuffer(float var1, float var2, float var3, float var4) {
      if (Config.isShaders()) {
         Shaders.setFogColor(var1, var2, var3);
      }

      ((Buffer)this.fogColorBuffer).clear();
      this.fogColorBuffer.put(var1).put(var2).put(var3).put(var4);
      ((Buffer)this.fogColorBuffer).flip();
      return this.fogColorBuffer;
   }

   public MapItemRenderer getMapItemRenderer() {
      return this.theMapItemRenderer;
   }

   private void waitForServerThread() {
      this.serverWaitTimeCurrent = 0;
      if (!Config.isSmoothWorld() || !Config.isSingleProcessor()) {
         this.lastServerTime = 0L;
         this.lastServerTicks = 0;
      } else if (this.mc.isIntegratedServerRunning()) {
         IntegratedServer var4 = this.mc.getIntegratedServer();
         if (var4 != null) {
            boolean var5 = this.mc.isGamePaused();
            if (!var5 && !(this.mc.currentScreen instanceof GuiDownloadTerrain)) {
               if (this.serverWaitTime > 0) {
                  Lagometer.timerServer.start();
                  Config.sleep(this.serverWaitTime);
                  Lagometer.timerServer.end();
                  this.serverWaitTimeCurrent = this.serverWaitTime;
               }

               long var6 = System.nanoTime() / 1000000L;
               if (this.lastServerTime != 0L && this.lastServerTicks != 0) {
                  long var8 = var6 - this.lastServerTime;
                  if (var8 < 0L) {
                     this.lastServerTime = var6;
                     var8 = 0L;
                  }

                  if (var8 >= 50L) {
                     this.lastServerTime = var6;
                     int var10 = var4.getTickCounter();
                     int var11 = var10 - this.lastServerTicks;
                     if (var11 < 0) {
                        this.lastServerTicks = var10;
                        var11 = 0;
                     }

                     if (var11 < 1 && this.serverWaitTime < 100) {
                        this.serverWaitTime += 2;
                     }

                     if (var11 > 1 && this.serverWaitTime > 0) {
                        this.serverWaitTime--;
                     }

                     this.lastServerTicks = var10;
                  }
               } else {
                  this.lastServerTime = var6;
                  this.lastServerTicks = var4.getTickCounter();
                  this.avgServerTickDiff = 1.0F;
                  this.avgServerTimeDiff = 50.0F;
               }
            } else {
               if (this.mc.currentScreen instanceof GuiDownloadTerrain) {
                  Config.sleep(20L);
               }

               this.lastServerTime = 0L;
               this.lastServerTicks = 0;
            }
         }
      }
   }

   private void frameInit() {
      if (!this.initialized) {
         TextureUtils.registerResourceListener();
         if (Config.getBitsOs() == 64 && Config.getBitsJre() == 32) {
            Config.setNotify64BitJava(true);
         }

         this.initialized = true;
      }

      Config.checkDisplayMode();
      WorldClient var4 = this.mc.theWorld;
      if (var4 != null && Config.k()) {
         Config.setNotify64BitJava(false);
         ChatComponentText var5 = new ChatComponentText(I18n.format("of.message.java64Bit"));
         this.mc.ingameGUI.getChatGUI().printChatMessage(var5);
      }

      if (this.mc.currentScreen instanceof GuiMainMenu) {
         this.updateMainMenu((GuiMainMenu)this.mc.currentScreen);
      }

      if (this.updatedWorld != var4) {
         RandomMobs.worldChanged(this.updatedWorld, var4);
         Config.updateThreadPriorities();
         this.lastServerTime = 0L;
         this.lastServerTicks = 0;
         this.updatedWorld = var4;
      }

      if (!this.setFxaaShader(Shaders.configAntialiasingLevel)) {
         Shaders.configAntialiasingLevel = 0;
      }
   }

   private void frameFinish() {
      if (this.mc.theWorld != null) {
         long var4 = System.currentTimeMillis();
         if (var4 > this.lastErrorCheckTimeMs + 10000L) {
            this.lastErrorCheckTimeMs = var4;
            int var6 = GL11.glGetError();
            if (var6 != 0) {
               String var7 = GLU.gluErrorString(var6);
               ChatComponentText var8 = new ChatComponentText(I18n.format("of.message.openglError", var6, var7));
               this.mc.ingameGUI.getChatGUI().printChatMessage(var8);
            }
         }
      }
   }

   private void updateMainMenu(GuiMainMenu var1) {
      try {
         String var5 = null;
         Calendar var6 = Calendar.getInstance();
         var6.setTime(new Date());
         int var7 = var6.get(5);
         int var8 = var6.get(2) + 1;
         if (var7 == 8 && var8 == 4) {
            var5 = "Happy birthday, OptiFine!";
         }

         if (var7 == 14 && var8 == 8) {
            var5 = "Happy birthday, sp614x!";
         }

         if (var5 == null) {
            return;
         }

         Field[] var9 = GuiMainMenu.class.getDeclaredFields();

         for (int var10 = 0; var10 < var9.length; var10++) {
            if (var9[var10].getType() == String.class) {
               var9[var10].setAccessible(true);
               var9[var10].set(var1, var5);
               break;
            }
         }
      } catch (Throwable var11) {
      }
   }

   public boolean setFxaaShader(int var1) {
      if (!OpenGlHelper.isFramebufferEnabled()) {
         return false;
      }

      if (this.theShaderGroup != null && this.theShaderGroup != this.fxaaShaders[2] && this.theShaderGroup != this.fxaaShaders[4]) {
         return true;
      }

      if (var1 != 2 && var1 != 4) {
         if (this.theShaderGroup == null) {
            return true;
         }

         this.theShaderGroup.deleteShaderGroup();
         this.theShaderGroup = null;
         return true;
      } else {
         if (this.theShaderGroup != null && this.theShaderGroup == this.fxaaShaders[var1]) {
            return true;
         }

         if (this.mc.theWorld == null) {
            return true;
         }

         this.loadShader(new ResourceLocation("shaders/post/fxaa_of_" + var1 + "x.json"));
         this.fxaaShaders[var1] = this.theShaderGroup;
         return this.useShader;
      }
   }

   static {
      long var20 = ab ^ 47822493829642L;
      long var10000 = 7564348073725898507L ^ var20;
      var10000 = 5137595995454081203L ^ var20;
   }

   public static class l9y implements Callable<String> {
      public final EntityRenderer g;

      public l9y(EntityRenderer var1) {
         this.g = var1;
      }

      public String call() throws Exception {
         return Minecraft.getMinecraft().currentScreen.getClass().getCanonicalName();
      }
   }

   public static class rt7 implements Predicate {
      public final EntityRenderer k;

      public rt7(EntityRenderer var1) {
         this.k = var1;
      }

      public boolean q(Entity var1) {
         return var1.canBeCollidedWith();
      }

      @Override
      public boolean apply(Object var1) {
         return this.q((Entity)var1);
      }
   }
}
