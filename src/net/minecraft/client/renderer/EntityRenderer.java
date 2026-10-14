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

   public EntityRenderer(Minecraft mcIn, IResourceManager resourceManagerIn) {
      this.shaderIndex = shaderCount;
      this.useShader = false;
      this.R = 0;
      this.mc = mcIn;
      this.resourceManager = resourceManagerIn;
      this.itemRenderer = mcIn.getItemRenderer();
      this.theMapItemRenderer = new MapItemRenderer(mcIn.getTextureManager());
      this.lightmapTexture = new DynamicTexture(16, 16);
      this.locationLightMap = mcIn.getTextureManager().getDynamicTextureLocation("lightMap", this.lightmapTexture);
      this.lightmapColors = this.lightmapTexture.getTextureData();
      this.theShaderGroup = null;

      for (int i = 0; i < 32; i++) {
         for (int j = 0; j < 32; j++) {
            float f = j - 16;
            float g = i - 16;
            float h = MathHelper.sqrt_float(f * f + g * g);
            this.rainXCoords[i << 5 | j] = -g / h;
            this.rainYCoords[i << 5 | j] = f / h;
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

   public void loadEntityShader(Entity entityIn) {
      if (OpenGlHelper.shadersSupported) {
         if (this.theShaderGroup != null) {
            this.theShaderGroup.deleteShaderGroup();
         }

         this.theShaderGroup = null;
         if (entityIn instanceof EntityCreeper) {
            this.loadShader(new ResourceLocation("shaders/post/creeper.json"));
         } else if (entityIn instanceof EntitySpider) {
            this.loadShader(new ResourceLocation("shaders/post/spider.json"));
         } else if (entityIn instanceof EntityEnderman) {
            this.loadShader(new ResourceLocation("shaders/post/invert.json"));
         } else if (Reflector.ForgeHooksClient_loadEntityShader.exists()) {
            Reflector.call(Reflector.ForgeHooksClient_loadEntityShader, entityIn, this);
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

   public void loadShader(ResourceLocation resourceLocationIn) {
      if (OpenGlHelper.isFramebufferEnabled()) {
         try {
            this.theShaderGroup = new ShaderGroup(this.mc.getTextureManager(), this.resourceManager, this.mc.getFramebuffer(), resourceLocationIn);
            this.theShaderGroup.createBindFramebuffers(this.mc.displayWidth, this.mc.displayHeight);
            this.useShader = true;
         } catch (IOException ioException) {
            logger.warn("Failed to load shader: " + resourceLocationIn, ioException);
            this.shaderIndex = shaderCount;
            this.useShader = false;
         } catch (JsonSyntaxException jsonSyntaxException) {
            logger.warn("Failed to load shader: " + resourceLocationIn, jsonSyntaxException);
            this.shaderIndex = shaderCount;
            this.useShader = false;
         }
      }
   }

   @Override
   public void onResourceManagerReload(IResourceManager resourceManager) {
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
         float f = this.mc.gameSettings.Zm * 0.6F + 0.2F;
         float g = f * f * f * 8.0F;
         this.Zt = this.mouseFilterXAxis.smooth(this.n, 0.05F * g);
         this.K = this.mouseFilterYAxis.smooth(this.M, 0.05F * g);
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

      Entity entity = this.mc.getRenderViewEntity();
      double e = entity.posX;
      double d2 = entity.posY + entity.getEyeHeight();
      double d3 = entity.posZ;
      float h = this.mc.theWorld.getLightBrightness(new BlockPos(e, d2, d3));
      float f2 = this.mc.gameSettings.renderDistanceChunks / 16.0F;
      f2 = MathHelper.clamp_float(f2, 0.0F, 1.0F);
      float f3 = h * (1.0F - f2) + f2;
      this.X = this.X + (f3 - this.X) * 0.1F;
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

   public void updateShaderGroupSize(int width, int height) {
      if (OpenGlHelper.shadersSupported) {
         if (this.theShaderGroup != null) {
            this.theShaderGroup.createBindFramebuffers(width, height);
         }

         this.mc.renderGlobal.createBindEntityOutlineFbs(width, height);
      }
   }

   public void getMouseOver(float partialTicks) {
      Entity entity = this.mc.getRenderViewEntity();
      if (entity != null && this.mc.theWorld != null) {
         this.mc.mcProfiler.startSection("pick");
         this.mc.pointedEntity = null;
         double d = this.mc.playerController.getBlockReachDistance();
         this.mc.objectMouseOver = entity.rayTrace(d, partialTicks);
         double e = d;
         Vec3 vec3 = entity.getPositionEyes(partialTicks);
         boolean bl = false;
         if (this.mc.playerController.L()) {
            d = 6.0;
            e = 6.0;
         } else {
            if (d > 3.0) {
               bl = true;
            }

            d = d;
         }

         if (this.mc.objectMouseOver != null) {
            e = this.mc.objectMouseOver.hitVec.distanceTo(vec3);
         }

         Vec3 vec32 = entity.getLook(partialTicks);
         LookEvent lookEvent = new LookEvent(vec32);
         Astolfo.INSTANCE.getEventBus().post(lookEvent);
         vec32 = lookEvent.getVec();
         Vec3 vec33 = vec3.addVector(vec32.xCoord * d, vec32.yCoord * d, vec32.zCoord * d);
         this.pointedEntity = null;
         Vec3 vec34 = null;
         float f = 1.0F;
         List list = this.mc
            .theWorld
            .getEntitiesInAABBexcluding(
               entity,
               entity.getEntityBoundingBox().addCoord(vec32.xCoord * d, vec32.yCoord * d, vec32.zCoord * d).expand(f, f, f),
               Predicates.and(EntitySelectors.NOT_SPECTATING, new EntityRenderer.rt7(this))
            );
         double d2 = e;

         for (int i = 0; i < list.size(); i++) {
            Entity entity2 = (Entity)list.get(i);
            float g = entity2.getCollisionBorderSize();
            AxisAlignedBB axisAlignedBB = entity2.getEntityBoundingBox().expand(g, g, g);
            MovingObjectPosition movingObjectPosition = axisAlignedBB.calculateIntercept(vec3, vec33);
            if (axisAlignedBB.isVecInside(vec3)) {
               if (d2 >= 0.0 && !ModuleManager.Q.isEnabled()) {
                  this.pointedEntity = entity2;
                  vec34 = movingObjectPosition == null ? vec3 : movingObjectPosition.hitVec;
                  d2 = 0.0;
               }
            } else if (movingObjectPosition != null) {
               double d3 = vec3.distanceTo(movingObjectPosition.hitVec);
               if (d3 < d2 || d2 == 0.0) {
                  boolean bl2 = false;
                  if (Reflector.ForgeEntity_canRiderInteract.exists()) {
                     bl2 = Reflector.callBoolean(entity2, Reflector.ForgeEntity_canRiderInteract);
                  }

                  if (entity2 != entity.ridingEntity || bl2) {
                     this.pointedEntity = entity2;
                     vec34 = movingObjectPosition.hitVec;
                     d2 = d3;
                  } else if (d2 == 0.0) {
                     this.pointedEntity = entity2;
                     vec34 = movingObjectPosition.hitVec;
                  }
               }
            }
         }

         ReachEvent reachEvent = new ReachEvent();
         Astolfo.INSTANCE.getEventBus().post(reachEvent);
         if (this.pointedEntity != null
            && bl
            && vec3.distanceTo(vec34) > (ModuleManager.Z.isEnabled() ? InfiniteReach.range.getValue().floatValue() : reachEvent.getReach())) {
            this.pointedEntity = null;
            this.mc.objectMouseOver = new MovingObjectPosition(MovingObjectPosition.MovingObjectType.MISS, vec34, (EnumFacing)null, new BlockPos(vec34));
         }

         if (this.pointedEntity != null && (d2 < e || this.mc.objectMouseOver == null)) {
            this.mc.objectMouseOver = new MovingObjectPosition(this.pointedEntity, vec34);
            if (this.pointedEntity instanceof EntityLivingBase || this.pointedEntity instanceof EntityItemFrame) {
               this.mc.pointedEntity = this.pointedEntity;
            }
         }

         this.mc.mcProfiler.endSection();
      }
   }

   private void updateFovModifierHand() {
      float f = 1.0F;
      if (this.mc.getRenderViewEntity() instanceof AbstractClientPlayer) {
         AbstractClientPlayer abstractClientPlayer = (AbstractClientPlayer)this.mc.getRenderViewEntity();
         f = abstractClientPlayer.getFovModifier();
      }

      this.fovModifierHandPrev = this.fovModifierHand;
      this.fovModifierHand = this.fovModifierHand + (f - this.fovModifierHand) * 0.5F;
      if (this.fovModifierHand > 1.5F) {
         this.fovModifierHand = 1.5F;
      }

      if (this.fovModifierHand < 0.1F) {
         this.fovModifierHand = 0.1F;
      }
   }

   private float getFOVModifier(float partialTicks, boolean useFOVSetting) {
      if (this.ZL) {
         return 90.0F;
      }

      Entity entity = this.mc.getRenderViewEntity();
      float f = 70.0F;
      if (useFOVSetting) {
         f = this.mc.gameSettings.b;
         if (Config.isDynamicFov()) {
            f *= this.fovModifierHandPrev + (this.fovModifierHand - this.fovModifierHandPrev) * partialTicks;
         }
      }

      f *= Z0;
      boolean bl = false;
      if (this.mc.currentScreen == null) {
         GameSettings gameSettings2 = this.mc.gameSettings;
         bl = GameSettings.isKeyDown(this.mc.gameSettings.ofKeyBindZoom);
      }

      if (bl) {
         if (!Config.zoomMode) {
            Config.zoomMode = true;
            this.mc.gameSettings.lt = true;
         }

         if (Config.zoomMode) {
            f /= 4.0F;
         }
      } else if (Config.zoomMode) {
         Config.zoomMode = false;
         this.mc.gameSettings.lt = false;
         this.mouseFilterXAxis = new MouseFilter();
         this.mouseFilterYAxis = new MouseFilter();
         this.mc.renderGlobal.displayListEntitiesDirty = true;
      }

      if (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).getHealth() <= 0.0F) {
         float g = ((EntityLivingBase)entity).deathTime + partialTicks;
         f /= (1.0F - 500.0F / (g + 500.0F)) * 2.0F + 1.0F;
      }

      Block block = ActiveRenderInfo.getBlockAtEntityViewpoint(this.mc.theWorld, entity, partialTicks);
      if (block.getMaterial() == Material.water) {
         f = f * 60.0F / 70.0F;
      }

      return f;
   }

   private void hurtCameraEffect(float partialTicks) {
      if (this.mc.getRenderViewEntity() instanceof EntityLivingBase) {
         HurtCameraEvent hurtCameraEvent = new HurtCameraEvent();
         Astolfo.INSTANCE.getEventBus().post(hurtCameraEvent);
         if (hurtCameraEvent.isCancelled()) {
            return;
         }

         EntityLivingBase entityLivingBase = (EntityLivingBase)this.mc.getRenderViewEntity();
         float f = entityLivingBase.hurtTime - partialTicks;
         if (entityLivingBase.getHealth() <= 0.0F) {
            float g = entityLivingBase.deathTime + partialTicks;
            GlStateManager.rotate(40.0F - 8000.0F / (g + 200.0F), 0.0F, 0.0F, 1.0F);
         }

         if (f < 0.0F) {
            return;
         }

         f /= entityLivingBase.maxHurtTime;
         f = MathHelper.sin(f * f * f * f * (float) Math.PI);
         float h = entityLivingBase.attackedAtYaw;
         GlStateManager.rotate(-h, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(-f * 14.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.rotate(h, 0.0F, 1.0F, 0.0F);
      }
   }

   private void setupViewBobbing(float partialTicks) {
      if (this.mc.getRenderViewEntity() instanceof EntityPlayer) {
         EntityPlayer entityPlayer = (EntityPlayer)this.mc.getRenderViewEntity();
         float f = entityPlayer.distanceWalkedModified - entityPlayer.prevDistanceWalkedModified;
         float g = -(entityPlayer.distanceWalkedModified + f * partialTicks);
         float h = entityPlayer.prevCameraYaw + (entityPlayer.cameraYaw - entityPlayer.prevCameraYaw) * partialTicks;
         float f2 = entityPlayer.prevCameraPitch + (entityPlayer.cameraPitch - entityPlayer.prevCameraPitch) * partialTicks;
         GlStateManager.translate(MathHelper.sin(g * (float) Math.PI) * h * 0.5F, -Math.abs(MathHelper.cos(g * (float) Math.PI) * h), 0.0F);
         GlStateManager.rotate(MathHelper.sin(g * (float) Math.PI) * h * 3.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.rotate(Math.abs(MathHelper.cos(g * (float) Math.PI - 0.2F) * h) * 5.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotate(f2, 1.0F, 0.0F, 0.0F);
      }
   }

   private void orientCamera(float partialTicks) {
      Entity entity = this.mc.getRenderViewEntity();
      float f = entity.rotationYaw;
      float g = entity.rotationPitch;
      float h = entity.prevRotationYaw;
      float f2 = entity.prevRotationPitch;
      if (q) {
         entity.rotationYaw = S;
         entity.prevRotationYaw = S;
         entity.rotationPitch = r;
         entity.prevRotationPitch = r;
      }

      float f3 = entity.getEyeHeight();
      double d = entity.prevPosX + (entity.posX - entity.prevPosX) * partialTicks;
      double e = entity.prevPosY + (entity.posY - entity.prevPosY) * partialTicks + f3;
      double d2 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * partialTicks;
      if (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).isPlayerSleeping()) {
         f3 = (float)(f3 + 1.0);
         GlStateManager.translate(0.0F, 0.3F, 0.0F);
         if (!this.mc.gameSettings.ZI) {
            BlockPos blockPos = new BlockPos(entity);
            IBlockState iBlockState = this.mc.theWorld.getBlockState(blockPos);
            Block block = iBlockState.getBlock();
            if (Reflector.ForgeHooksClient_orientBedCamera.exists()) {
               Reflector.callVoid(Reflector.ForgeHooksClient_orientBedCamera, this.mc.theWorld, blockPos, iBlockState, entity);
            } else if (block == Blocks.bed) {
               int i = iBlockState.getValue(BlockBed.FACING).getHorizontalIndex();
               GlStateManager.rotate(i * 90, 0.0F, 1.0F, 0.0F);
            }

            GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks + 180.0F, 0.0F, -1.0F, 0.0F);
            GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, -1.0F, 0.0F, 0.0F);
         }
      } else if (this.mc.gameSettings.thirdPersonView > 0) {
         double d3 = 5.0;
         if (this.mc.gameSettings.ZI) {
            GlStateManager.translate(0.0F, 0.0F, (float)(-d3));
         } else {
            float f4 = entity.rotationYaw;
            float f5 = entity.rotationPitch;
            if (this.mc.gameSettings.thirdPersonView == 2) {
               f5 += 180.0F;
            }

            double d4 = -MathHelper.sin(f4 / 180.0F * (float) Math.PI) * MathHelper.cos(f5 / 180.0F * (float) Math.PI) * d3;
            double d5 = MathHelper.cos(f4 / 180.0F * (float) Math.PI) * MathHelper.cos(f5 / 180.0F * (float) Math.PI) * d3;
            double d6 = -MathHelper.sin(f5 / 180.0F * (float) Math.PI) * d3;

            for (int j = 0; j < 8; j++) {
               float f6 = (j & 1) * 2 - 1;
               float f7 = (j >> 1 & 1) * 2 - 1;
               float f8 = (j >> 2 & 1) * 2 - 1;
               f6 *= 0.1F;
               f7 *= 0.1F;
               f8 *= 0.1F;
               MovingObjectPosition movingObjectPosition = this.mc
                  .theWorld
                  .rayTraceBlocks(
                     new Vec3(d + f6, e + f7, d2 + f8),
                     new Vec3(d - d4 + f6 + f8, e - d6 + f7, d2 - d5 + f8)
                  );
               if (movingObjectPosition != null) {
                  double d7 = movingObjectPosition.hitVec.distanceTo(new Vec3(d, e, d2));
                  if (d7 < d3) {
                     d3 = d7;
                  }
               }
            }

            if (this.mc.gameSettings.thirdPersonView == 2) {
               GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            }

            GlStateManager.rotate(entity.rotationPitch - f5, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(entity.rotationYaw - f4, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(0.0 + Freecam2.V, 0.0, (float)(ModuleManager.k.isEnabled() && Camera.viewClip.getValue() ? -5.0 : -d3) + Freecam2.R);
            GlStateManager.rotate(f4 - entity.rotationYaw, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(f5 - entity.rotationPitch, 1.0F, 0.0F, 0.0F);
         }
      } else {
         GlStateManager.translate(0.0F, 0.0F, -0.1F);
      }

      if (Reflector.EntityViewRenderEvent_CameraSetup_Constructor.exists()) {
         if (!this.mc.gameSettings.ZI) {
            float f9 = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks + 180.0F;
            float f10 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
            float f11 = 0.0F;
            if (entity instanceof EntityAnimal) {
               EntityAnimal entityAnimal = (EntityAnimal)entity;
               f9 = entityAnimal.prevRotationYawHead + (entityAnimal.rotationYawHead - entityAnimal.prevRotationYawHead) * partialTicks + 180.0F;
            }

            Block block2 = ActiveRenderInfo.getBlockAtEntityViewpoint(this.mc.theWorld, entity, partialTicks);
            Object object = Reflector.newInstance(Reflector.EntityViewRenderEvent_CameraSetup_Constructor, this, entity, block2, partialTicks, f9, f10, f11);
            Reflector.postForgeBusEvent(object);
            f11 = Reflector.getFieldValueFloat(object, Reflector.EntityViewRenderEvent_CameraSetup_roll, f11);
            f10 = Reflector.getFieldValueFloat(object, Reflector.EntityViewRenderEvent_CameraSetup_pitch, f10);
            f9 = Reflector.getFieldValueFloat(object, Reflector.EntityViewRenderEvent_CameraSetup_yaw, f9);
            GlStateManager.rotate(f11, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(f10, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(f9, 0.0F, 1.0F, 0.0F);
         }
      } else if (!this.mc.gameSettings.ZI) {
         GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, 1.0F, 0.0F, 0.0F);
         if (entity instanceof EntityAnimal) {
            EntityAnimal entityAnimal2 = (EntityAnimal)entity;
            GlStateManager.rotate(entityAnimal2.prevRotationYawHead + (entityAnimal2.rotationYawHead - entityAnimal2.prevRotationYawHead) * partialTicks + 180.0F, 0.0F, 1.0F, 0.0F);
         } else {
            GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks + 180.0F, 0.0F, 1.0F, 0.0F);
         }
      }

      if (q) {
         entity.rotationYaw = f;
         entity.prevRotationYaw = h;
         entity.rotationPitch = g;
         entity.prevRotationPitch = f2;
      }

      GlStateManager.translate(0.0F, -f3, 0.0F);
      d = entity.prevPosX + (entity.posX - entity.prevPosX) * partialTicks;
      e = entity.prevPosY + (entity.posY - entity.prevPosY) * partialTicks + f3;
      d2 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * partialTicks;
      this.s = this.mc.renderGlobal.hasCloudFog(d, e, d2, partialTicks);
   }

   public void setupCameraTransform(float partialTicks, int pass) {
      this.Q = this.mc.gameSettings.renderDistanceChunks * 16;
      if (Config.isFogFancy()) {
         this.Q *= 0.95F;
      }

      if (Config.isFogFast()) {
         this.Q *= 0.83F;
      }

      GlStateManager.matrixMode(5889);
      GlStateManager.loadIdentity();
      float f = 0.07F;
      if (this.mc.gameSettings.anaglyph) {
         GlStateManager.translate(-(pass * 2 - 1) * f, 0.0F, 0.0F);
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

      Project.gluPerspective(this.getFOVModifier(partialTicks, true), (float)this.mc.displayWidth / this.mc.displayHeight, 0.05F, this.clipDistance);
      GlStateManager.matrixMode(ModuleManager.e);
      GlStateManager.loadIdentity();
      if (this.mc.gameSettings.anaglyph) {
         GlStateManager.translate((pass * 2 - 1) * 0.1F, 0.0F, 0.0F);
      }

      this.hurtCameraEffect(partialTicks);
      if (this.mc.gameSettings.viewBobbing) {
         this.setupViewBobbing(partialTicks);
      }

      float g = this.mc.thePlayer.prevTimeInPortal + (this.mc.thePlayer.timeInPortal - this.mc.thePlayer.prevTimeInPortal) * partialTicks;
      if (g > 0.0F) {
         byte b = 20;
         if (this.mc.thePlayer.isPotionActive(Potion.confusion)) {
            b = 7;
         }

         float h = 5.0F / (g * g + 5.0F) - g * 0.04F;
         h *= h;
         GlStateManager.rotate((this.Zn + partialTicks) * b, 0.0F, 1.0F, 1.0F);
         GlStateManager.scale(1.0F / h, 1.0F, 1.0F);
         GlStateManager.rotate(-(this.Zn + partialTicks) * b, 0.0F, 1.0F, 1.0F);
      }

      this.orientCamera(partialTicks);
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

   private void renderHand(float partialTicks, int xOffset) {
      this.renderHand(partialTicks, xOffset, true, true, false);
   }

   public void renderHand(float f, int i, boolean bl, boolean bl2, boolean bl3) {
      if (!this.ZL) {
         GlStateManager.matrixMode(5889);
         GlStateManager.loadIdentity();
         float g = 0.07F;
         if (this.mc.gameSettings.anaglyph) {
            GlStateManager.translate(-(i * 2 - 1) * g, 0.0F, 0.0F);
         }

         if (Config.isShaders()) {
            Shaders.applyHandDepth();
         }

         Project.gluPerspective(
            ModuleManager.k.isEnabled() ? this.getFOVModifier(f, false) * Camera.itemFOV.getValue() : this.getFOVModifier(f, false),
            (float)this.mc.displayWidth / this.mc.displayHeight,
            0.05F,
            this.Q * 2.0F
         );
         GlStateManager.matrixMode(5888);
         GlStateManager.loadIdentity();
         if (this.mc.gameSettings.anaglyph) {
            GlStateManager.translate((i * 2 - 1) * 0.1F, 0.0F, 0.0F);
         }

         boolean bl4 = false;
         if (bl) {
            GlStateManager.pushMatrix();
            this.hurtCameraEffect(f);
            if (this.mc.gameSettings.viewBobbing) {
               this.setupViewBobbing(f);
            }

            bl4 = this.mc.getRenderViewEntity() instanceof EntityLivingBase && ((EntityLivingBase)this.mc.getRenderViewEntity()).isPlayerSleeping();
            boolean bl5 = !ReflectorForge.renderFirstPersonHand(this.mc.renderGlobal, f, i);
            if (bl5 && this.mc.gameSettings.thirdPersonView == 0 && !bl4 && !this.mc.gameSettings.Zn && !this.mc.playerController.W()) {
               this.enableLightmap();
               if (Config.isShaders()) {
                  ShadersRender.renderItemFP(this.itemRenderer, f, bl3);
               } else {
                  this.itemRenderer.renderItemInFirstPerson(f);
               }

               this.disableLightmap();
            }

            GlStateManager.popMatrix();
         }

         if (!bl2) {
            return;
         }

         this.disableLightmap();
         if (this.mc.gameSettings.thirdPersonView == 0 && !bl4) {
            this.itemRenderer.renderOverlays(f);
            this.hurtCameraEffect(f);
         }

         if (this.mc.gameSettings.viewBobbing) {
            this.setupViewBobbing(f);
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
      float f = 0.00390625F;
      GlStateManager.scale(f, f, f);
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

   private void updateLightmap(float partialTicks) {
      if (this.lightmapUpdateNeeded) {
         this.mc.mcProfiler.startSection("lightTex");
         WorldClient worldClient = this.mc.theWorld;
         if (worldClient != null) {
            if (Config.isCustomColors() && CustomColors.l(worldClient, this.torchFlickerX, this.lightmapColors, this.mc.thePlayer.isPotionActive(Potion.nightVision))) {
               this.lightmapTexture.updateDynamicTexture();
               this.lightmapUpdateNeeded = false;
               this.mc.mcProfiler.endSection();
               return;
            }

            float f = worldClient.getSunBrightness(1.0F);
            float g = f * 0.95F + 0.05F;

            for (int i = 0; i < 256; i++) {
               float h = worldClient.provider.getLightBrightnessTable()[i / 16] * g;
               float f2 = worldClient.provider.getLightBrightnessTable()[i % 16] * (this.torchFlickerX * 0.1F + 1.5F);
               if (worldClient.x() > 0) {
                  h = worldClient.provider.getLightBrightnessTable()[i / 16];
               }

               float f3 = h * (f * 0.65F + 0.35F);
               float f4 = h * (f * 0.65F + 0.35F);
               float f5 = f2 * ((f2 * 0.6F + 0.4F) * 0.6F + 0.4F);
               float f6 = f2 * (f2 * f2 * 0.6F + 0.4F);
               float f7 = f3 + f2;
               float f8 = f4 + f5;
               float f9 = h + f6;
               f7 = f7 * 0.96F + 0.03F;
               f8 = f8 * 0.96F + 0.03F;
               f9 = f9 * 0.96F + 0.03F;
               if (this.z > 0.0F) {
                  float f10 = this.Zv + (this.z - this.Zv) * partialTicks;
                  f7 = f7 * (1.0F - f10) + f7 * 0.7F * f10;
                  f8 = f8 * (1.0F - f10) + f8 * 0.6F * f10;
                  f9 = f9 * (1.0F - f10) + f9 * 0.6F * f10;
               }

               if (worldClient.provider.getDimensionId() == 1) {
                  f7 = 0.22F + f2 * 0.75F;
                  f8 = 0.28F + f5 * 0.75F;
                  f9 = 0.25F + f6 * 0.75F;
               }

               if (this.mc.thePlayer.isPotionActive(Potion.nightVision)) {
                  float f11 = this.getNightVisionBrightness(this.mc.thePlayer, partialTicks);
                  float f12 = 1.0F / f7;
                  if (f12 > 1.0F / f8) {
                     f12 = 1.0F / f8;
                  }

                  if (f12 > 1.0F / f9) {
                     f12 = 1.0F / f9;
                  }

                  f7 = f7 * (1.0F - f11) + f7 * f12 * f11;
                  f8 = f8 * (1.0F - f11) + f8 * f12 * f11;
                  f9 = f9 * (1.0F - f11) + f9 * f12 * f11;
               }

               if (f7 > 1.0F) {
                  f7 = 1.0F;
               }

               if (f8 > 1.0F) {
                  f8 = 1.0F;
               }

               if (f9 > 1.0F) {
                  f9 = 1.0F;
               }

               float f13 = this.mc.gameSettings.Zu;
               float f14 = 1.0F - f7;
               float f15 = 1.0F - f8;
               float f16 = 1.0F - f9;
               f14 = 1.0F - f14 * f14 * f14 * f14;
               f15 = 1.0F - f15 * f15 * f15 * f15;
               f16 = 1.0F - f16 * f16 * f16 * f16;
               f7 = f7 * (1.0F - f13) + f14 * f13;
               f8 = f8 * (1.0F - f13) + f15 * f13;
               f9 = f9 * (1.0F - f13) + f16 * f13;
               f7 = f7 * 0.96F + 0.03F;
               f8 = f8 * 0.96F + 0.03F;
               f9 = f9 * 0.96F + 0.03F;
               if (f7 > 1.0F) {
                  f7 = 1.0F;
               }

               if (f8 > 1.0F) {
                  f8 = 1.0F;
               }

               if (f9 > 1.0F) {
                  f9 = 1.0F;
               }

               if (f7 < 0.0F) {
                  f7 = 0.0F;
               }

               if (f8 < 0.0F) {
                  f8 = 0.0F;
               }

               if (f9 < 0.0F) {
                  f9 = 0.0F;
               }

               short s = 255;
               int j = (int)(f7 * 255.0F);
               int k = (int)(f8 * 255.0F);
               int m = (int)(f9 * 255.0F);
               if (ModuleManager.M.isEnabled()) {
                  j = WorldColor.color.getValue().getRed();
                  k = WorldColor.color.getValue().getGreen();
                  m = WorldColor.color.getValue().getBlue();
                  s = (short)WorldColor.color.getValue().getAlpha();
               }

               this.lightmapColors[i] = s << 24 | j << 16 | k << 8 | m;
            }

            this.lightmapTexture.updateDynamicTexture();
            this.lightmapUpdateNeeded = false;
            this.mc.mcProfiler.endSection();
         }
      }
   }

   public float getNightVisionBrightness(EntityLivingBase entitylivingbaseIn, float partialTicks) {
      int i = entitylivingbaseIn.getActivePotionEffect(Potion.nightVision).getDuration();
      return i > 200 ? 1.0F : 0.7F + MathHelper.sin((i - partialTicks) * (float) Math.PI * 0.2F) * 0.3F;
   }

   public void updateCameraAndRender(float partialTicks, long nanoTime) {
      this.frameInit();
      boolean bl = Display.isActive();
      if (!bl && this.mc.gameSettings.G && (!this.mc.gameSettings.touchscreen || !Mouse.isButtonDown(1))) {
         if (Minecraft.getSystemTime() - this.Zy > 500L) {
            this.mc.g();
         }
      } else {
         this.Zy = Minecraft.getSystemTime();
      }

      this.mc.mcProfiler.startSection("mouse");
      if (bl && Minecraft.isRunningOnMac && this.mc.inGameHasFocus && !Mouse.isInsideWindow()) {
         Mouse.setGrabbed(false);
         Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
         Mouse.setGrabbed(true);
      }

      if (this.mc.inGameHasFocus && bl) {
         this.mc.mouseHelper.mouseXYChange();
         float f = this.mc.gameSettings.Zm * 0.6F + 0.2F;
         float h = f * f * f * 8.0F;
         float f2 = this.mc.mouseHelper.deltaX * h;
         float f3 = this.mc.mouseHelper.deltaY * h;
         byte b = 1;
         if (this.mc.gameSettings.invertMouse) {
            b = -1;
         }

         if (this.mc.gameSettings.lt) {
            this.n += f2;
            this.M += f3;
            float f4 = partialTicks - this.d;
            this.d = partialTicks;
            f2 = this.Zt * f4;
            f3 = this.K * f4;
            if (q) {
               S += f2 / 8.0F;
               r += f3 * b / 8.0F;
               if (Math.abs(r) > 90.0F) {
                  r = r > 0.0F ? 90.0F : -90.0F;
               }
            } else {
               this.mc.thePlayer.setAngles(f2, f3 * b);
            }
         } else {
            this.n = 0.0F;
            this.M = 0.0F;
            if (q) {
               S += f2 / 8.0F;
               r += f3 * b / 8.0F;
               if (Math.abs(r) > 90.0F) {
                  r = r > 0.0F ? 90.0F : -90.0F;
               }
            } else {
               this.mc.thePlayer.setAngles(f2, f3 * b);
            }
         }
      }

      this.mc.mcProfiler.endSection();
      if (!this.mc.R) {
         anaglyphEnable = this.mc.gameSettings.anaglyph;
         final ScaledResolution scaledResolution = new ScaledResolution(this.mc);
         int i = scaledResolution.getScaledWidth();
         int j = scaledResolution.getScaledHeight();
         final int k = Mouse.getX() * i / this.mc.displayWidth;
         final int l = j - Mouse.getY() * j / this.mc.displayHeight - 1;
         int m = this.mc.gameSettings.limitFramerate;
         if (this.mc.theWorld != null) {
            this.mc.mcProfiler.startSection("level");
            int i2 = Math.min(Minecraft.B(), m);
            i2 = Math.max(i2, 60);
            long l2 = System.nanoTime() - nanoTime;
            long l3 = Math.max(1000000000 / i2 / 4 - l2, 0L);
            this.renderWorld(partialTicks, System.nanoTime() + l3);
            int i3 = this.mc.gameSettings.guiScale;
            this.mc.gameSettings.guiScale = 1;
            Astolfo.INSTANCE.getEventBus().post(new RenderUnscaledEvent());
            this.mc.gameSettings.guiScale = i3;
            if (OpenGlHelper.shadersSupported) {
               this.mc.renderGlobal.renderEntityOutlineFramebuffer();
               if (this.theShaderGroup != null && this.useShader) {
                  GlStateManager.matrixMode(5890);
                  GlStateManager.pushMatrix();
                  GlStateManager.loadIdentity();
                  this.theShaderGroup.t(partialTicks);
                  GlStateManager.popMatrix();
               }

               this.mc.getFramebuffer().bindFramebuffer(true);
            }

            this.Z4 = System.nanoTime();
            this.mc.mcProfiler.endStartSection("gui");
            if (!this.mc.gameSettings.Zn || this.mc.currentScreen != null) {
               GlStateManager.alphaFunc(516, 0.1F);
               this.mc.ingameGUI.renderGameOverlay(partialTicks);
               if (this.mc.gameSettings.ZQ && !this.mc.gameSettings.n) {
                  Config.drawFps();
               }

               if (this.mc.gameSettings.n) {
                  Lagometer.showLagometer(scaledResolution);
               }
            }

            this.mc.mcProfiler.endSection();
         } else {
            GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
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
                  Reflector.callVoid(Reflector.ForgeHooksClient_drawScreen, this.mc.currentScreen, k, l, partialTicks);
               } else {
                  this.mc.currentScreen.drawScreen(k, l, partialTicks);
               }
            } catch (Throwable throwable) {
               CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Rendering screen");
               CrashReportCategory crashReportCategory = crashReport.makeCategory("Screen render details");
               crashReportCategory.addCrashSectionCallable("Screen name", new EntityRenderer.l9y(this));
               crashReportCategory.addCrashSectionCallable("Mouse location", new Callable<String>() {
                  public String call() throws Exception {
                     return String.format("Scaled: (%d, %d). Absolute: (%d, %d)", k, l, Mouse.getX(), Mouse.getY());
                  }
               });
               crashReportCategory.addCrashSectionCallable(
                  "Screen size",
                  new Callable<String>() {
                     public String call() throws Exception {
                        return String.format(
                           "Scaled: (%d, %d). Absolute: (%d, %d). Scale factor of %d",
                           scaledResolution.getScaledWidth(),
                           scaledResolution.getScaledHeight(),
                           EntityRenderer.this.mc.displayWidth,
                           EntityRenderer.this.mc.displayHeight,
                           scaledResolution.getScaleFactor()
                        );
                     }
                  }
               );
               throw new ReportedException(crashReport);
            }
         }

         if (Astolfo.INSTANCE.getEventBus() != null) {
            Astolfo.INSTANCE.getEventBus().post(new RenderAfterScreenEvent(i, j));
         }
      }

      this.frameFinish();
      this.waitForServerThread();
      Lagometer.updateLagometer();
      if (this.mc.gameSettings.lJ) {
         this.mc.gameSettings.Zj = true;
      }
   }

   public void renderStreamIndicator(float partialTicks) {
      this.setupOverlayRendering();
      this.mc.ingameGUI.renderStreamIndicator(new ScaledResolution(this.mc));
   }

   private boolean N() {
      if (!this.Z7) {
         return false;
      }

      Entity entity = this.mc.getRenderViewEntity();
      boolean bl = entity instanceof EntityPlayer && !this.mc.gameSettings.Zn;
      if (bl && !((EntityPlayer)entity).capabilities.allowEdit) {
         ItemStack itemStack = ((EntityPlayer)entity).getCurrentEquippedItem();
         if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            BlockPos blockPos = this.mc.objectMouseOver.getBlockPos();
            IBlockState iBlockState = this.mc.theWorld.getBlockState(blockPos);
            Block block = iBlockState.getBlock();
            if (this.mc.playerController.getCurrentGameType() == WorldSettings.GameType.SPECTATOR) {
               bl = ReflectorForge.blockHasTileEntity(iBlockState) && this.mc.theWorld.getTileEntity(blockPos) instanceof IInventory;
            } else {
               bl = itemStack != null && (itemStack.canDestroy(block) || itemStack.canPlaceOn(block));
            }
         }
      }

      return bl;
   }

   private void renderWorldDirections(float partialTicks) {
      if (this.mc.gameSettings.n && !this.mc.gameSettings.Zn && !this.mc.thePlayer.w() && !this.mc.gameSettings.reducedDebugInfo) {
         Entity entity = this.mc.getRenderViewEntity();
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
         GL11.glLineWidth(1.0F);
         GlStateManager.disableTexture2D();
         GlStateManager.depthMask(false);
         GlStateManager.pushMatrix();
         GlStateManager.matrixMode(5888);
         GlStateManager.loadIdentity();
         this.orientCamera(partialTicks);
         GlStateManager.translate(0.0F, entity.getEyeHeight(), 0.0F);
         RenderGlobal.drawOutlinedBoundingBox(new AxisAlignedBB(0.0, 0.0, 0.0, 0.005, 1.0E-4, 1.0E-4), 255, 0, 0, 255);
         RenderGlobal.drawOutlinedBoundingBox(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0E-4, 1.0E-4, 0.005), 0, 0, 255, 255);
         RenderGlobal.drawOutlinedBoundingBox(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0E-4, 0.0033, 1.0E-4), 0, 255, 0, 255);
         GlStateManager.popMatrix();
         GlStateManager.depthMask(true);
         GlStateManager.enableTexture2D();
         GlStateManager.disableBlend();
      }
   }

   public void renderWorld(float partialTicks, long finishTimeNano) {
      this.updateLightmap(partialTicks);
      if (this.mc.getRenderViewEntity() == null) {
         this.mc.setRenderViewEntity(this.mc.thePlayer);
      }

      this.getMouseOver(partialTicks);
      if (Config.isShaders()) {
         Shaders.beginRender(this.mc, partialTicks, finishTimeNano);
      }

      GlStateManager.enableDepth();
      GlStateManager.enableAlpha();
      GlStateManager.alphaFunc(516, 0.1F);
      this.mc.mcProfiler.startSection("center");
      if (this.mc.gameSettings.anaglyph) {
         A = 0;
         GlStateManager.colorMask(false, true, true, false);
         this.renderWorldPass(0, partialTicks, finishTimeNano);
         A = 1;
         GlStateManager.colorMask(true, false, false, false);
         this.renderWorldPass(1, partialTicks, finishTimeNano);
         GlStateManager.colorMask(true, true, true, false);
      } else {
         this.renderWorldPass(2, partialTicks, finishTimeNano);
      }

      Astolfo.INSTANCE.getEventBus().post(new WorldRenderedEvent());
      this.mc.mcProfiler.endSection();
   }

   public void renderWorldPass(int pass, float partialTicks, long finishTimeNano) {
      boolean bl = ModuleManager.I.isEnabled();
      boolean bl2 = Config.isShaders();
      if (bl2) {
         Shaders.beginRenderPass(pass, partialTicks, finishTimeNano);
      }

      RenderGlobal renderGlobal2 = this.mc.renderGlobal;
      EffectRenderer effectRenderer2 = this.mc.effectRenderer;
      boolean bl3 = this.N();
      GlStateManager.enableCull();
      this.mc.mcProfiler.endStartSection("clear");
      if (bl2) {
         Shaders.setViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
      } else {
         GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
      }

      this.updateFogColor(partialTicks);
      GlStateManager.clear(C);
      if (bl2) {
         Shaders.clearRenderBuffer();
      }

      this.mc.mcProfiler.endStartSection("camera");
      this.setupCameraTransform(partialTicks, pass);
      if (bl2) {
         Shaders.setCamera(partialTicks);
      }

      ActiveRenderInfo.updateRenderInfo(this.mc.thePlayer, this.mc.gameSettings.thirdPersonView == 2);
      this.mc.mcProfiler.endStartSection("frustum");
      ClippingHelperImpl.getInstance();
      this.mc.mcProfiler.endStartSection("culling");
      Frustum frustum2 = new Frustum();
      Entity entity = this.mc.getRenderViewEntity();
      double d = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
      double d2 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
      double d3 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
      if (bl2) {
         ShadersRender.setFrustrumPosition(frustum2, d, d2, d3);
      } else {
         frustum2.setPosition(d, d2, d3);
      }

      if ((Config.K() || Config.u() || Config.s()) && !Shaders.isShadowPass) {
         this.setupFog(-1, partialTicks);
         this.mc.mcProfiler.endStartSection("sky");
         GlStateManager.matrixMode(5889);
         GlStateManager.loadIdentity();
         Project.gluPerspective(this.getFOVModifier(partialTicks, true), (float)this.mc.displayWidth / this.mc.displayHeight, 0.05F, this.clipDistance);
         GlStateManager.matrixMode(ModuleManager.e);
         if (bl2) {
            Shaders.beginSky();
         }

         renderGlobal2.renderSky(partialTicks, pass);
         if (bl2) {
            Shaders.a();
         }

         GlStateManager.matrixMode(5889);
         GlStateManager.loadIdentity();
         Project.gluPerspective(this.getFOVModifier(partialTicks, true), (float)this.mc.displayWidth / this.mc.displayHeight, 0.05F, this.clipDistance);
         GlStateManager.matrixMode(ModuleManager.e);
      } else {
         GlStateManager.disableBlend();
      }

      this.setupFog(0, partialTicks);
      GlStateManager.shadeModel(7425);
      if (entity.posY + entity.getEyeHeight() < 128.0 + this.mc.gameSettings.ofCloudsHeight * 128.0F) {
         this.renderCloudsCheck(renderGlobal2, partialTicks, pass);
      }

      this.mc.mcProfiler.endStartSection("prepareterrain");
      this.setupFog(0, partialTicks);
      this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
      RenderHelper.disableStandardItemLighting();
      this.mc.mcProfiler.endStartSection("terrain_setup");
      if (bl2) {
         ShadersRender.setupTerrain(renderGlobal2, entity, partialTicks, frustum2, this.R++, !ModuleManager.I.isEnabled());
      } else {
         renderGlobal2.setupTerrain(entity, partialTicks, frustum2, this.R++, !ModuleManager.I.isEnabled());
      }

      if (pass == 0 || pass == 2) {
         this.mc.mcProfiler.endStartSection("updatechunks");
         Lagometer.timerChunkUpload.start();
         this.mc.renderGlobal.updateChunks(finishTimeNano);
         Lagometer.timerChunkUpload.end();
      }

      this.mc.mcProfiler.endStartSection("terrain");
      Lagometer.timerTerrain.start();
      if (this.mc.gameSettings.R && pass > 0) {
         this.mc.mcProfiler.endStartSection("finish");
         GL11.glFinish();
         this.mc.mcProfiler.endStartSection("terrain");
      }

      GlStateManager.matrixMode(ModuleManager.e);
      GlStateManager.pushMatrix();
      GlStateManager.disableAlpha();
      if (bl2) {
         ShadersRender.o();
      }

      boolean bl4 = ModuleManager.t.isEnabled();
      if (bl4) {
         CaveFinder.j();
      }

      renderGlobal2.renderBlockLayer(EnumWorldBlockLayer.SOLID, partialTicks, pass, entity);
      GlStateManager.enableAlpha();
      if (bl2) {
         ShadersRender.z();
      }

      if (!bl4) {
         renderGlobal2.renderBlockLayer(EnumWorldBlockLayer.CUTOUT_MIPPED, partialTicks, pass, entity);
      }

      this.mc.getTextureManager().getTexture(TextureMap.locationBlocksTexture).setBlurMipmap(false, false);
      if (bl2) {
         ShadersRender.I();
      }

      renderGlobal2.renderBlockLayer(EnumWorldBlockLayer.CUTOUT, partialTicks, pass, entity);
      this.mc.getTextureManager().getTexture(TextureMap.locationBlocksTexture).restoreLastBlurMipmap();
      if (bl2) {
         ShadersRender.P();
      }

      if (bl4) {
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

         renderGlobal2.renderEntities(entity, frustum2, partialTicks);
         if (Reflector.ForgeHooksClient_setRenderPass.exists()) {
            Reflector.callVoid(Reflector.ForgeHooksClient_setRenderPass, -1);
         }

         RenderHelper.disableStandardItemLighting();
         this.disableLightmap();
         GlStateManager.matrixMode(5888);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         if (this.mc.objectMouseOver != null && entity.isInsideOfMaterial(Material.water) && bl3) {
            EntityPlayer entityPlayer = (EntityPlayer)entity;
            GlStateManager.disableAlpha();
            this.mc.mcProfiler.endStartSection("outline");
            if ((
                  !Reflector.ForgeHooksClient_onDrawBlockHighlight.exists()
                     || !Reflector.callBoolean(
                        Reflector.ForgeHooksClient_onDrawBlockHighlight, renderGlobal2, entityPlayer, this.mc.objectMouseOver, 0, entityPlayer.getHeldItem(), partialTicks
                     )
               )
               && !this.mc.gameSettings.Zn) {
               renderGlobal2.drawSelectionBox(entityPlayer, this.mc.objectMouseOver, 0, partialTicks);
            }

            GlStateManager.enableAlpha();
         }
      }

      GlStateManager.matrixMode(ModuleManager.e);
      GlStateManager.popMatrix();
      if (bl3 && this.mc.objectMouseOver != null && !entity.isInsideOfMaterial(Material.water)) {
         EntityPlayer entityPlayer2 = (EntityPlayer)entity;
         GlStateManager.disableAlpha();
         this.mc.mcProfiler.endStartSection("outline");
         if ((
               !Reflector.ForgeHooksClient_onDrawBlockHighlight.exists()
                  || !Reflector.callBoolean(
                     Reflector.ForgeHooksClient_onDrawBlockHighlight, renderGlobal2, entityPlayer2, this.mc.objectMouseOver, 0, entityPlayer2.getHeldItem(), partialTicks
                  )
            )
            && !this.mc.gameSettings.Zn) {
            renderGlobal2.drawSelectionBox(entityPlayer2, this.mc.objectMouseOver, 0, partialTicks);
         }

         GlStateManager.enableAlpha();
      }

      if (!renderGlobal2.damagedBlocks.isEmpty()) {
         this.mc.mcProfiler.endStartSection("destroyProgress");
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0);
         this.mc.getTextureManager().getTexture(TextureMap.locationBlocksTexture).setBlurMipmap(false, false);
         renderGlobal2.drawBlockDamageTexture(Tessellator.getInstance(), Tessellator.getInstance().getWorldRenderer(), entity, partialTicks);
         this.mc.getTextureManager().getTexture(TextureMap.locationBlocksTexture).restoreLastBlurMipmap();
         GlStateManager.disableBlend();
      }

      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.disableBlend();
      if (!this.ZL) {
         this.enableLightmap();
         this.mc.mcProfiler.endStartSection("litParticles");
         if (bl2) {
            Shaders.UT();
         }

         effectRenderer2.renderLitParticles(entity, partialTicks);
         RenderHelper.disableStandardItemLighting();
         this.setupFog(0, partialTicks);
         this.mc.mcProfiler.endStartSection("particles");
         if (bl2) {
            Shaders.S();
         }

         effectRenderer2.renderParticles(entity, partialTicks);
         if (bl2) {
            Shaders.U();
         }

         this.disableLightmap();
      }

      GlStateManager.depthMask(false);
      GlStateManager.enableCull();
      this.mc.mcProfiler.endStartSection("weather");
      if (bl2) {
         Shaders.beginWeather();
      }

      this.renderRainSnow(partialTicks);
      if (bl2) {
         Shaders.i();
      }

      GlStateManager.depthMask(true);
      renderGlobal2.renderWorldBorder(entity, partialTicks);
      if (bl2) {
         ShadersRender.B(this, partialTicks, pass);
         Shaders.preWater();
      }

      GlStateManager.disableBlend();
      GlStateManager.enableCull();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.alphaFunc(516, 0.1F);
      this.setupFog(0, partialTicks);
      GlStateManager.enableBlend();
      GlStateManager.depthMask(false);
      this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
      GlStateManager.shadeModel(7425);
      this.mc.mcProfiler.endStartSection("translucent");
      if (bl2) {
         Shaders.beginWater();
      }

      renderGlobal2.renderBlockLayer(EnumWorldBlockLayer.TRANSLUCENT, partialTicks, pass, entity);
      if (bl2) {
         Shaders.u();
      }

      if (Reflector.ForgeHooksClient_setRenderPass.exists() && !this.ZL) {
         RenderHelper.enableStandardItemLighting();
         this.mc.mcProfiler.endStartSection("entities");
         Reflector.callVoid(Reflector.ForgeHooksClient_setRenderPass, 1);
         this.mc.renderGlobal.renderEntities(entity, frustum2, partialTicks);
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
         Reflector.callVoid(Reflector.ForgeHooksClient_setRenderPass, -1);
         RenderHelper.disableStandardItemLighting();
      }

      GlStateManager.shadeModel(7424);
      GlStateManager.depthMask(true);
      GlStateManager.enableCull();
      GlStateManager.disableBlend();
      GlStateManager.disableFog();
      if (entity.posY + entity.getEyeHeight() >= 128.0 + this.mc.gameSettings.ofCloudsHeight * 128.0F) {
         this.mc.mcProfiler.endStartSection("aboveClouds");
         this.renderCloudsCheck(renderGlobal2, partialTicks, pass);
      }

      if (Reflector.ForgeHooksClient_dispatchRenderLast.exists()) {
         this.mc.mcProfiler.endStartSection("forge_render_last");
         Reflector.callVoid(Reflector.ForgeHooksClient_dispatchRenderLast, renderGlobal2, partialTicks);
      }

      if (pass == 2) {
         Astolfo.INSTANCE.getEventBus().post(new RenderEvent(RenderType.WORLD, partialTicks, d, d2, d3));
      }

      this.mc.mcProfiler.endStartSection("hand");
      boolean bl5 = ReflectorForge.renderFirstPersonHand(this.mc.renderGlobal, partialTicks, pass);
      if (!bl5 && this.V && !Shaders.isShadowPass) {
         if (bl2) {
            ShadersRender.l(this, partialTicks, pass);
            Shaders.renderFinal();
         }

         GlStateManager.clear(256);
         if (bl2) {
            ShadersRender.t(this, partialTicks, pass);
         } else {
            this.renderHand(partialTicks, pass);
         }

         this.renderWorldDirections(partialTicks);
      }

      if (bl2) {
         Shaders.endRender();
      }

      RenderEntityItem.w = System.nanoTime();
   }

   private void renderCloudsCheck(RenderGlobal renderGlobalIn, float partialTicks, int pass) {
      if (this.mc.gameSettings.renderDistanceChunks >= 4 && !Config.isCloudsOff() && Shaders.shouldRenderClouds(this.mc.gameSettings)) {
         this.mc.mcProfiler.endStartSection("clouds");
         GlStateManager.matrixMode(5889);
         GlStateManager.loadIdentity();
         Project.gluPerspective(this.getFOVModifier(partialTicks, true), (float)this.mc.displayWidth / this.mc.displayHeight, 0.05F, this.clipDistance * 4.0F);
         GlStateManager.matrixMode(5888);
         GlStateManager.pushMatrix();
         this.setupFog(0, partialTicks);
         renderGlobalIn.renderClouds(partialTicks, pass);
         GlStateManager.disableFog();
         GlStateManager.popMatrix();
         GlStateManager.matrixMode(5889);
         GlStateManager.loadIdentity();
         Project.gluPerspective(this.getFOVModifier(partialTicks, true), (float)this.mc.displayWidth / this.mc.displayHeight, 0.05F, this.clipDistance);
         GlStateManager.matrixMode(5888);
      }
   }

   private void addRainParticles() {
      float f = this.mc.theWorld.getRainStrength(1.0F);
      if (!Config.isRainFancy()) {
         f /= 2.0F;
      }

      if (f != 0.0F && Config.isRainSplash()) {
         this.random.setSeed(this.Zn * 312987231L);
         Entity entity = this.mc.getRenderViewEntity();
         WorldClient worldClient = this.mc.theWorld;
         BlockPos blockPos = new BlockPos(entity);
         byte b = 10;
         double d = 0.0;
         double e = 0.0;
         double d2 = 0.0;
         int i = 0;
         int j = (int)(100.0F * f * f);
         if (this.mc.gameSettings.particleSetting == 1) {
            j >>= 1;
         } else if (this.mc.gameSettings.particleSetting == 2) {
            j = 0;
         }

         for (int k = 0; k < j; k++) {
            BlockPos blockPos2 = worldClient.getPrecipitationHeight(
               blockPos.add(this.random.nextInt(b) - this.random.nextInt(b), 0, this.random.nextInt(b) - this.random.nextInt(b))
            );
            BiomeGenBase biomeGenBase = worldClient.getBiomeGenForCoords(blockPos2);
            BlockPos blockPos3 = blockPos2.down();
            Block block = worldClient.getBlockState(blockPos3).getBlock();
            if (blockPos2.getY() <= blockPos.getY() + b && blockPos2.getY() >= blockPos.getY() - b && biomeGenBase.canRain() && biomeGenBase.getFloatTemperature(blockPos2) >= 0.15F) {
               double d3 = this.random.nextDouble();
               double d4 = this.random.nextDouble();
               if (block.getMaterial() == Material.lava) {
                  this.mc
                     .theWorld
                     .spawnParticle(
                        EnumParticleTypes.SMOKE_NORMAL,
                        blockPos2.getX() + d3,
                        blockPos2.getY() + 0.1F - block.getBlockBoundsMinY(),
                        blockPos2.getZ() + d4,
                        0.0,
                        0.0,
                        0.0
                     );
               } else if (block.getMaterial() != Material.air) {
                  block.setBlockBoundsBasedOnState(worldClient, blockPos3);
                  if (this.random.nextInt(++i) == 0) {
                     d = blockPos3.getX() + d3;
                     e = blockPos3.getY() + 0.1F + block.getBlockBoundsMaxY() - 1.0;
                     d2 = blockPos3.getZ() + d4;
                  }

                  this.mc
                     .theWorld
                     .spawnParticle(
                        EnumParticleTypes.WATER_DROP,
                        blockPos3.getX() + d3,
                        blockPos3.getY() + 0.1F + block.getBlockBoundsMaxY(),
                        blockPos3.getZ() + d4,
                        0.0,
                        0.0,
                        0.0
                     );
               }
            }
         }

         if (i > 0 && this.random.nextInt(3) < this.T++) {
            this.T = 0;
            if (e > blockPos.getY() + 1 && worldClient.getPrecipitationHeight(blockPos).getY() > MathHelper.floor_float(blockPos.getY())) {
               this.mc.theWorld.playSound(d, e, d2, "ambient.weather.rain", 0.1F, 0.5F, false);
            } else {
               this.mc.theWorld.playSound(d, e, d2, "ambient.weather.rain", 0.2F, 1.0F, false);
            }
         }
      }
   }

   public void renderRainSnow(float partialTicks) {
      if (Reflector.ForgeWorldProvider_getWeatherRenderer.exists()) {
         WorldProvider worldProvider = this.mc.theWorld.provider;
         Object object = Reflector.call(worldProvider, Reflector.ForgeWorldProvider_getWeatherRenderer);
         if (object != null) {
            Reflector.callVoid(object, Reflector.IRenderHandler_render, partialTicks, this.mc.theWorld, this.mc);
            return;
         }
      }

      float f = this.mc.theWorld.getRainStrength(partialTicks);
      if (f > 0.0F) {
         if (Config.isRainOff()) {
            return;
         }

         this.enableLightmap();
         Entity entity = this.mc.getRenderViewEntity();
         WorldClient worldClient = this.mc.theWorld;
         int i = MathHelper.floor_double(entity.posX);
         int j = MathHelper.floor_double(entity.posY);
         int k = MathHelper.floor_double(entity.posZ);
         Tessellator tessellator = Tessellator.getInstance();
         WorldRenderer worldRenderer = tessellator.getWorldRenderer();
         GlStateManager.disableCull();
         GL11.glNormal3f(0.0F, 1.0F, 0.0F);
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
         GlStateManager.alphaFunc(516, 0.1F);
         double d = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
         double e = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
         double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
         int l = MathHelper.floor_double(e);
         byte b = 5;
         if (Config.isRainFancy()) {
            b = 10;
         }

         byte b2 = -1;
         float g = this.Zn + partialTicks;
         worldRenderer.setTranslation(-d, -e, -d2);
         if (Config.isRainFancy()) {
            b = 10;
         }

         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

         for (int m = k - b; m <= k + b; m++) {
            for (int n = i - b; n <= i + b; n++) {
               int i2 = (m - k + 16) * 32 + n - i + 16;
               double d3 = this.rainXCoords[i2] * 0.5;
               double d4 = this.rainYCoords[i2] * 0.5;
               mutableBlockPos.set(n, 0, m);
               BiomeGenBase biomeGenBase = worldClient.getBiomeGenForCoords(mutableBlockPos);
               if (biomeGenBase.canRain() || biomeGenBase.getEnableSnow()) {
                  int i3 = worldClient.getPrecipitationHeight(mutableBlockPos).getY();
                  int i4 = j - b;
                  int i5 = j + b;
                  if (i4 < i3) {
                     i4 = i3;
                  }

                  if (i5 < i3) {
                     i5 = i3;
                  }

                  int i6 = i3;
                  if (i3 < l) {
                     i6 = l;
                  }

                  if (i4 != i5) {
                     this.random.setSeed(n * n * 3121 + n * 45238971 ^ m * m * 418711 + m * 13761);
                     mutableBlockPos.set(n, i4, m);
                     float h = biomeGenBase.getFloatTemperature(mutableBlockPos);
                     if (worldClient.getWorldChunkManager().getTemperatureAtHeight(h, i3) >= 0.15F) {
                        if (b2 != 0) {
                           if (b2 >= 0) {
                              tessellator.draw();
                           }

                           b2 = 0;
                           this.mc.getTextureManager().bindTexture(locationRainPng);
                           worldRenderer.begin(7, DefaultVertexFormats.T);
                        }

                        double d5 = (
                              (double)(this.Zn + n * n * 3121 + n * 45238971 + m * m * 418711 + m * 13761 & 31) + partialTicks
                           )
                           / 32.0
                           * (3.0 + this.random.nextDouble());
                        double d6 = n + 0.5F - entity.posX;
                        double d7 = m + 0.5F - entity.posZ;
                        float f2 = MathHelper.sqrt_double(d6 * d6 + d7 * d7) / b;
                        float f3 = ((1.0F - f2 * f2) * 0.5F + 0.5F) * f;
                        mutableBlockPos.set(n, i6, m);
                        int i7 = worldClient.getCombinedLight(mutableBlockPos, 0);
                        int i8 = i7 >> 16 & 65535;
                        int i9 = i7 & 65535;
                        worldRenderer.pos(n - d3 + 0.5, i4, m - d4 + 0.5)
                           .tex(0.0, i4 * 0.25 + d5)
                           .color(1.0F, 1.0F, 1.0F, f3)
                           .lightmap(i8, i9)
                           .endVertex();
                        worldRenderer.pos(n + d3 + 0.5, i4, m + d4 + 0.5)
                           .tex(1.0, i4 * 0.25 + d5)
                           .color(1.0F, 1.0F, 1.0F, f3)
                           .lightmap(i8, i9)
                           .endVertex();
                        worldRenderer.pos(n + d3 + 0.5, i5, m + d4 + 0.5)
                           .tex(1.0, i5 * 0.25 + d5)
                           .color(1.0F, 1.0F, 1.0F, f3)
                           .lightmap(i8, i9)
                           .endVertex();
                        worldRenderer.pos(n - d3 + 0.5, i5, m - d4 + 0.5)
                           .tex(0.0, i5 * 0.25 + d5)
                           .color(1.0F, 1.0F, 1.0F, f3)
                           .lightmap(i8, i9)
                           .endVertex();
                     } else {
                        if (b2 != 1) {
                           if (b2 >= 0) {
                              tessellator.draw();
                           }

                           b2 = 1;
                           this.mc.getTextureManager().bindTexture(locationSnowPng);
                           worldRenderer.begin(7, DefaultVertexFormats.T);
                        }

                        double d8 = ((this.Zn & 511) + partialTicks) / 512.0F;
                        double d9 = this.random.nextDouble() + g * 0.01 * (float)this.random.nextGaussian();
                        double d10 = this.random.nextDouble() + g * (float)this.random.nextGaussian() * 0.001;
                        double d11 = n + 0.5F - entity.posX;
                        double d12 = m + 0.5F - entity.posZ;
                        float f4 = MathHelper.sqrt_double(d11 * d11 + d12 * d12) / b;
                        float f5 = ((1.0F - f4 * f4) * 0.3F + 0.5F) * f;
                        mutableBlockPos.set(n, i6, m);
                        int i10 = (worldClient.getCombinedLight(mutableBlockPos, 0) * 3 + 15728880) / 4;
                        int i11 = i10 >> 16 & 65535;
                        int i12 = i10 & 65535;
                        worldRenderer.pos(n - d3 + 0.5, i4, m - d4 + 0.5)
                           .tex(0.0 + d9, i4 * 0.25 + d8 + d10)
                           .color(1.0F, 1.0F, 1.0F, f5)
                           .lightmap(i11, i12)
                           .endVertex();
                        worldRenderer.pos(n + d3 + 0.5, i4, m + d4 + 0.5)
                           .tex(1.0 + d9, i4 * 0.25 + d8 + d10)
                           .color(1.0F, 1.0F, 1.0F, f5)
                           .lightmap(i11, i12)
                           .endVertex();
                        worldRenderer.pos(n + d3 + 0.5, i5, m + d4 + 0.5)
                           .tex(1.0 + d9, i5 * 0.25 + d8 + d10)
                           .color(1.0F, 1.0F, 1.0F, f5)
                           .lightmap(i11, i12)
                           .endVertex();
                        worldRenderer.pos(n - d3 + 0.5, i5, m - d4 + 0.5)
                           .tex(0.0 + d9, i5 * 0.25 + d8 + d10)
                           .color(1.0F, 1.0F, 1.0F, f5)
                           .lightmap(i11, i12)
                           .endVertex();
                     }
                  }
               }
            }
         }

         if (b2 >= 0) {
            tessellator.draw();
         }

         worldRenderer.setTranslation(0.0, 0.0, 0.0);
         GlStateManager.enableCull();
         GlStateManager.disableBlend();
         GlStateManager.alphaFunc(516, 0.1F);
         this.disableLightmap();
      }
   }

   public void setupOverlayRendering() {
      ScaledResolution scaledResolution = new ScaledResolution(this.mc);
      GlStateManager.clear(256);
      GlStateManager.matrixMode(5889);
      GlStateManager.loadIdentity();
      GlStateManager.ortho(0.0, scaledResolution.getScaledWidth_double(), scaledResolution.getScaledHeight_double(), 0.0, 1000.0, 3000.0);
      GlStateManager.matrixMode(5888);
      GlStateManager.loadIdentity();
      GlStateManager.translate(0.0F, 0.0F, -2000.0F);
   }

   private void updateFogColor(float partialTicks) {
      WorldClient worldClient = this.mc.theWorld;
      Entity entity = this.mc.getRenderViewEntity();
      float f = 0.25F + 0.75F * this.mc.gameSettings.renderDistanceChunks / 32.0F;
      f = 1.0F - (float)Math.pow(f, 0.25);
      Vec3 vec3 = worldClient.getSkyColor(this.mc.getRenderViewEntity(), partialTicks);
      vec3 = CustomColors.getWorldSkyColor(vec3, worldClient, this.mc.getRenderViewEntity(), partialTicks);
      float g = (float)vec3.xCoord;
      float h = (float)vec3.yCoord;
      float f2 = (float)vec3.zCoord;
      Vec3 vec32 = worldClient.getFogColor(partialTicks);
      vec32 = CustomColors.getWorldFogColor(vec32, worldClient, this.mc.getRenderViewEntity(), partialTicks);
      this.ZQ = (float)vec32.xCoord;
      this.N = (float)vec32.yCoord;
      this.ZI = (float)vec32.zCoord;
      if (this.mc.gameSettings.renderDistanceChunks >= 4) {
         double d = -1.0;
         Vec3 vec33 = MathHelper.sin(worldClient.getCelestialAngleRadians(partialTicks)) > 0.0F ? new Vec3(d, 0.0, 0.0) : new Vec3(1.0, 0.0, 0.0);
         float f3 = (float)entity.getLook(partialTicks).dotProduct(vec33);
         if (f3 < 0.0F) {
            f3 = 0.0F;
         }

         if (f3 > 0.0F) {
            float[] floats = worldClient.provider.calcSunriseSunsetColors(worldClient.getCelestialAngle(partialTicks), partialTicks);
            if (floats != null) {
               f3 *= floats[3];
               this.ZQ = this.ZQ * (1.0F - f3) + floats[0] * f3;
               this.N = this.N * (1.0F - f3) + floats[1] * f3;
               this.ZI = this.ZI * (1.0F - f3) + floats[2] * f3;
            }
         }
      }

      this.ZQ = this.ZQ + (g - this.ZQ) * f;
      this.N = this.N + (h - this.N) * f;
      this.ZI = this.ZI + (f2 - this.ZI) * f;
      float f4 = worldClient.getRainStrength(partialTicks);
      if (f4 > 0.0F) {
         float f5 = 1.0F - f4 * 0.5F;
         float f6 = 1.0F - f4 * 0.4F;
         this.ZQ *= f5;
         this.N *= f5;
         this.ZI *= f6;
      }

      float f7 = worldClient.getThunderStrength(partialTicks);
      if (f7 > 0.0F) {
         float f8 = 1.0F - f7 * 0.5F;
         this.ZQ *= f8;
         this.N *= f8;
         this.ZI *= f8;
      }

      Block block = ActiveRenderInfo.getBlockAtEntityViewpoint(this.mc.theWorld, entity, partialTicks);
      if (this.s) {
         Vec3 vec34 = worldClient.getCloudColour(partialTicks);
         this.ZQ = (float)vec34.xCoord;
         this.N = (float)vec34.yCoord;
         this.ZI = (float)vec34.zCoord;
      } else if (block.getMaterial() == Material.water) {
         float f9 = EnchantmentHelper.getRespiration(entity) * 0.2F;
         if (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).isPotionActive(Potion.waterBreathing)) {
            f9 = f9 * 0.3F + 0.6F;
         }

         this.ZQ = 0.02F + f9;
         this.N = 0.02F + f9;
         this.ZI = 0.2F + f9;
         Vec3 vec35 = CustomColors.S(
            this.mc.theWorld, this.mc.getRenderViewEntity().posX, this.mc.getRenderViewEntity().posY + 1.0, this.mc.getRenderViewEntity().posZ
         );
         if (vec35 != null) {
            this.ZQ = (float)vec35.xCoord;
            this.N = (float)vec35.yCoord;
            this.ZI = (float)vec35.zCoord;
         }
      } else if (block.getMaterial() == Material.lava) {
         this.ZQ = 0.6F;
         this.N = 0.1F;
         this.ZI = 0.0F;
      }

      float f10 = this.U + (this.X - this.U) * partialTicks;
      this.ZQ *= f10;
      this.N *= f10;
      this.ZI *= f10;
      double e = worldClient.provider.getVoidFogYFactor();
      double d2 = (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks) * e;
      if (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).isPotionActive(Potion.blindness)) {
         int i = ((EntityLivingBase)entity).getActivePotionEffect(Potion.blindness).getDuration();
         if (i < 20) {
            d2 *= 1.0F - i / 20.0F;
         } else {
            d2 = 0.0;
         }
      }

      if (d2 < 1.0) {
         if (d2 < 0.0) {
            d2 = 0.0;
         }

         d2 *= d2;
         this.ZQ = (float)(this.ZQ * d2);
         this.N = (float)(this.N * d2);
         this.ZI = (float)(this.ZI * d2);
      }

      if (this.z > 0.0F) {
         float f11 = this.Zv + (this.z - this.Zv) * partialTicks;
         this.ZQ = this.ZQ * (1.0F - f11) + this.ZQ * 0.7F * f11;
         this.N = this.N * (1.0F - f11) + this.N * 0.6F * f11;
         this.ZI = this.ZI * (1.0F - f11) + this.ZI * 0.6F * f11;
      }

      if (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).isPotionActive(Potion.nightVision)) {
         float f12 = this.getNightVisionBrightness((EntityLivingBase)entity, partialTicks);
         float f13 = 1.0F / this.ZQ;
         if (f13 > 1.0F / this.N) {
            f13 = 1.0F / this.N;
         }

         if (f13 > 1.0F / this.ZI) {
            f13 = 1.0F / this.ZI;
         }

         this.ZQ = this.ZQ * (1.0F - f12) + this.ZQ * f13 * f12;
         this.N = this.N * (1.0F - f12) + this.N * f13 * f12;
         this.ZI = this.ZI * (1.0F - f12) + this.ZI * f13 * f12;
      }

      if (this.mc.gameSettings.anaglyph) {
         float f14 = (this.ZQ * 30.0F + this.N * 59.0F + this.ZI * 11.0F) / 100.0F;
         float f15 = (this.ZQ * 30.0F + this.N * 70.0F) / 100.0F;
         float f16 = (this.ZQ * 30.0F + this.ZI * 70.0F) / 100.0F;
         this.ZQ = f14;
         this.N = f15;
         this.ZI = f16;
      }

      if (Reflector.EntityViewRenderEvent_FogColors_Constructor.exists()) {
         Object object = Reflector.newInstance(Reflector.EntityViewRenderEvent_FogColors_Constructor, this, entity, block, partialTicks, this.ZQ, this.N, this.ZI);
         Reflector.postForgeBusEvent(object);
         this.ZQ = Reflector.getFieldValueFloat(object, Reflector.EntityViewRenderEvent_FogColors_red, this.ZQ);
         this.N = Reflector.getFieldValueFloat(object, Reflector.EntityViewRenderEvent_FogColors_green, this.N);
         this.ZI = Reflector.getFieldValueFloat(object, Reflector.EntityViewRenderEvent_FogColors_blue, this.ZI);
      }

      Shaders.v(this.ZQ, this.N, this.ZI, 0.0F);
   }

   private void setupFog(int startCoords, float partialTicks) {
      Entity entity = this.mc.getRenderViewEntity();
      this.fogStandard = false;
      if (entity instanceof EntityPlayer) {
         boolean bl = ((EntityPlayer)entity).capabilities.isCreativeMode;
      }

      GL11.glFog(2918, this.setFogColorBuffer(this.ZQ, this.N, this.ZI, 1.0F));
      GL11.glNormal3f(0.0F, -1.0F, 0.0F);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      Block block = ActiveRenderInfo.getBlockAtEntityViewpoint(this.mc.theWorld, entity, partialTicks);
      float f = -1.0F;
      if (Reflector.ForgeHooksClient_getFogDensity.exists()) {
         f = Reflector.callFloat(Reflector.ForgeHooksClient_getFogDensity, this, entity, block, partialTicks, 0.1F);
      }

      if (f >= 0.0F) {
         GlStateManager.setFogDensity(f);
      } else if (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).isPotionActive(Potion.blindness)) {
         float g = 5.0F;
         int i = ((EntityLivingBase)entity).getActivePotionEffect(Potion.blindness).getDuration();
         if (i < 20) {
            g = 5.0F + (this.Q - 5.0F) * (1.0F - i / 20.0F);
         }

         if (Config.isShaders()) {
            Shaders.setFog(9729);
         } else {
            GlStateManager.setFog(9729);
         }

         if (startCoords == -1) {
            GlStateManager.setFogStart(0.0F);
            GlStateManager.setFogEnd(g * 0.8F);
         } else {
            GlStateManager.setFogStart(g * 0.25F);
            GlStateManager.setFogEnd(g);
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
      } else if (block.getMaterial() == Material.water) {
         if (Config.isShaders()) {
            Shaders.setFog(2048);
         } else {
            GlStateManager.setFog(2048);
         }

         if (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).isPotionActive(Potion.waterBreathing)) {
            GlStateManager.setFogDensity(0.01F);
         } else {
            GlStateManager.setFogDensity(0.1F - EnchantmentHelper.getRespiration(entity) * 0.03F);
         }

         if (Config.V()) {
            GlStateManager.setFogDensity(0.02F);
         }
      } else if (block.getMaterial() == Material.lava) {
         if (Config.isShaders()) {
            Shaders.setFog(2048);
         } else {
            GlStateManager.setFog(2048);
         }

         GlStateManager.setFogDensity(2.0F);
      } else {
         float h = this.Q;
         this.fogStandard = true;
         if (Config.isShaders()) {
            Shaders.setFog(9729);
         } else {
            GlStateManager.setFog(9729);
         }

         if (startCoords == -1) {
            GlStateManager.setFogStart(0.0F);
            GlStateManager.setFogEnd(h);
         } else {
            GlStateManager.setFogStart(h * Config.S());
            GlStateManager.setFogEnd(h);
         }

         if (GLContext.getCapabilities().GL_NV_fog_distance) {
            if (Config.isFogFancy()) {
               GL11.glFogi(34138, 34139);
            }

            if (Config.isFogFast()) {
               GL11.glFogi(34138, 34140);
            }
         }

         if (this.mc.theWorld.provider.doesXZShowFog((int)entity.posX, (int)entity.posZ)) {
            GlStateManager.setFogStart(h * 0.05F);
            GlStateManager.setFogEnd(h);
         }

         if (Reflector.ForgeHooksClient_onFogRender.exists()) {
            Reflector.callVoid(Reflector.ForgeHooksClient_onFogRender, this, entity, block, partialTicks, startCoords, h);
         }
      }

      if (ModuleManager.X.isEnabled() && NoEffect.noFog.getValue()) {
         GlStateManager.setFogDensity(0.0F);
      }

      GlStateManager.enableColorMaterial();
      GlStateManager.enableFog();
      GlStateManager.colorMaterial(1028, 4608);
   }

   private FloatBuffer setFogColorBuffer(float red, float green, float blue, float alpha) {
      if (Config.isShaders()) {
         Shaders.setFogColor(red, green, blue);
      }

      ((Buffer)this.fogColorBuffer).clear();
      this.fogColorBuffer.put(red).put(green).put(blue).put(alpha);
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
         IntegratedServer integratedServer = this.mc.getIntegratedServer();
         if (integratedServer != null) {
            boolean bl = this.mc.isGamePaused();
            if (!bl && !(this.mc.currentScreen instanceof GuiDownloadTerrain)) {
               if (this.serverWaitTime > 0) {
                  Lagometer.timerServer.start();
                  Config.sleep(this.serverWaitTime);
                  Lagometer.timerServer.end();
                  this.serverWaitTimeCurrent = this.serverWaitTime;
               }

               long l = System.nanoTime() / 1000000L;
               if (this.lastServerTime != 0L && this.lastServerTicks != 0) {
                  long m = l - this.lastServerTime;
                  if (m < 0L) {
                     this.lastServerTime = l;
                     m = 0L;
                  }

                  if (m >= 50L) {
                     this.lastServerTime = l;
                     int i = integratedServer.getTickCounter();
                     int j = i - this.lastServerTicks;
                     if (j < 0) {
                        this.lastServerTicks = i;
                        j = 0;
                     }

                     if (j < 1 && this.serverWaitTime < 100) {
                        this.serverWaitTime += 2;
                     }

                     if (j > 1 && this.serverWaitTime > 0) {
                        this.serverWaitTime--;
                     }

                     this.lastServerTicks = i;
                  }
               } else {
                  this.lastServerTime = l;
                  this.lastServerTicks = integratedServer.getTickCounter();
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
      WorldClient worldClient = this.mc.theWorld;
      if (worldClient != null && Config.k()) {
         Config.setNotify64BitJava(false);
         ChatComponentText chatComponentText = new ChatComponentText(I18n.format("of.message.java64Bit"));
         this.mc.ingameGUI.getChatGUI().printChatMessage(chatComponentText);
      }

      if (this.mc.currentScreen instanceof GuiMainMenu) {
         this.updateMainMenu((GuiMainMenu)this.mc.currentScreen);
      }

      if (this.updatedWorld != worldClient) {
         RandomMobs.worldChanged(this.updatedWorld, worldClient);
         Config.updateThreadPriorities();
         this.lastServerTime = 0L;
         this.lastServerTicks = 0;
         this.updatedWorld = worldClient;
      }

      if (!this.setFxaaShader(Shaders.configAntialiasingLevel)) {
         Shaders.configAntialiasingLevel = 0;
      }
   }

   private void frameFinish() {
      if (this.mc.theWorld != null) {
         long l = System.currentTimeMillis();
         if (l > this.lastErrorCheckTimeMs + 10000L) {
            this.lastErrorCheckTimeMs = l;
            int i = GL11.glGetError();
            if (i != 0) {
               String string = GLU.gluErrorString(i);
               ChatComponentText chatComponentText = new ChatComponentText(I18n.format("of.message.openglError", i, string));
               this.mc.ingameGUI.getChatGUI().printChatMessage(chatComponentText);
            }
         }
      }
   }

   private void updateMainMenu(GuiMainMenu guiMainMenu) {
      try {
         String string = null;
         Calendar calendar = Calendar.getInstance();
         calendar.setTime(new Date());
         int i = calendar.get(5);
         int j = calendar.get(2) + 1;
         if (i == 8 && j == 4) {
            string = "Happy birthday, OptiFine!";
         }

         if (i == 14 && j == 8) {
            string = "Happy birthday, sp614x!";
         }

         if (string == null) {
            return;
         }

         Field[] fields = GuiMainMenu.class.getDeclaredFields();

         for (int k = 0; k < fields.length; k++) {
            if (fields[k].getType() == String.class) {
               fields[k].setAccessible(true);
               fields[k].set(guiMainMenu, string);
               break;
            }
         }
      } catch (Throwable throwable) {
      }
   }

   public boolean setFxaaShader(int i) {
      if (!OpenGlHelper.isFramebufferEnabled()) {
         return false;
      }

      if (this.theShaderGroup != null && this.theShaderGroup != this.fxaaShaders[2] && this.theShaderGroup != this.fxaaShaders[4]) {
         return true;
      }

      if (i != 2 && i != 4) {
         if (this.theShaderGroup == null) {
            return true;
         }

         this.theShaderGroup.deleteShaderGroup();
         this.theShaderGroup = null;
         return true;
      } else {
         if (this.theShaderGroup != null && this.theShaderGroup == this.fxaaShaders[i]) {
            return true;
         }

         if (this.mc.theWorld == null) {
            return true;
         }

         this.loadShader(new ResourceLocation("shaders/post/fxaa_of_" + i + "x.json"));
         this.fxaaShaders[i] = this.theShaderGroup;
         return this.useShader;
      }
   }

   public static class l9y implements Callable<String> {
      public final EntityRenderer g;

      public l9y(EntityRenderer entityRenderer) {
         this.g = entityRenderer;
      }

      public String call() throws Exception {
         return Minecraft.getMinecraft().currentScreen.getClass().getCanonicalName();
      }
   }

   public static class rt7 implements Predicate {
      public final EntityRenderer k;

      public rt7(EntityRenderer entityRenderer) {
         this.k = entityRenderer;
      }

      public boolean q(Entity entity) {
         return entity.canBeCollidedWith();
      }

      @Override
      public boolean apply(Object object) {
         return this.q((Entity)object);
      }
   }
}
