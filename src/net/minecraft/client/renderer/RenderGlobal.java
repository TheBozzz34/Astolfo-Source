package net.minecraft.client.renderer;

import bypass.Astolfo;
import bypass.event.render.SetupTerrainEvent;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockSign;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.client.renderer.chunk.IRenderChunkFactory;
import net.minecraft.client.renderer.chunk.ListChunkFactory;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.chunk.VboChunkFactory;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderLinkHelper;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemRecord;
import net.minecraft.src.ChunkUtils;
import net.minecraft.src.CloudRenderer;
import net.minecraft.src.Config;
import net.minecraft.src.CustomColors;
import net.minecraft.src.CustomSky;
import net.minecraft.src.DynamicLights;
import net.minecraft.src.Lagometer;
import net.minecraft.src.RandomMobs;
import net.minecraft.src.Reflector;
import net.minecraft.src.RenderInfoLazy;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Matrix4f;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vector3d;
import net.minecraft.world.IWorldAccess;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import shadersmod.client.Shaders;
import shadersmod.client.ShadersRender;
import shadersmod.client.ShadowUtils;

public class RenderGlobal implements IWorldAccess, IResourceManagerReloadListener {
   private static final Logger logger = LogManager.getLogger();
   private static final ResourceLocation locationMoonPhasesPng = new ResourceLocation("textures/environment/moon_phases.png");
   private static final ResourceLocation locationSunPng = new ResourceLocation("textures/environment/sun.png");
   private static final ResourceLocation locationCloudsPng = new ResourceLocation("textures/environment/clouds.png");
   private static final ResourceLocation locationEndSkyPng = new ResourceLocation("textures/environment/end_sky.png");
   private static final ResourceLocation locationForcefieldPng = new ResourceLocation("textures/misc/forcefield.png");
   public final Minecraft mc;
   private final TextureManager renderEngine;
   private final RenderManager renderManager;
   private WorldClient theWorld;
   private Set<RenderChunk> chunksToUpdate = Sets.newLinkedHashSet();
   private List<RenderGlobal.ContainerLocalRenderInformation> renderInfos = Lists.newArrayListWithCapacity(69696);
   private final Set A = Sets.newHashSet();
   private ViewFrustum viewFrustum;
   private int starGLCallList = -1;
   private int glSkyList = -1;
   private int glSkyList2 = -1;
   private VertexFormat vertexBufferFormat;
   private VertexBuffer starVBO;
   private VertexBuffer skyVBO;
   private VertexBuffer sky2VBO;
   private int cloudTickCounter;
   public final Map<Integer, DestroyBlockProgress> damagedBlocks = Maps.newHashMap();
   private final Map<BlockPos, ISound> mapSoundPositions = Maps.newHashMap();
   private final TextureAtlasSprite[] destroyBlockIcons = new TextureAtlasSprite[10];
   private Framebuffer entityOutlineFramebuffer;
   private ShaderGroup entityOutlineShader;
   private double frustumUpdatePosX = Double.MIN_VALUE;
   private double frustumUpdatePosY = Double.MIN_VALUE;
   private double frustumUpdatePosZ = Double.MIN_VALUE;
   private int frustumUpdatePosChunkX = Integer.MIN_VALUE;
   private int frustumUpdatePosChunkY = Integer.MIN_VALUE;
   private int frustumUpdatePosChunkZ = Integer.MIN_VALUE;
   private double lastViewEntityX = Double.MIN_VALUE;
   private double lastViewEntityY = Double.MIN_VALUE;
   private double lastViewEntityZ = Double.MIN_VALUE;
   private double lastViewEntityPitch = Double.MIN_VALUE;
   private double lastViewEntityYaw = Double.MIN_VALUE;
   private final ChunkRenderDispatcher renderDispatcher = new ChunkRenderDispatcher();
   private ChunkRenderContainer renderContainer;
   private int renderDistanceChunks = -1;
   private int O = 2;
   private int countEntitiesTotal;
   private int countEntitiesRendered;
   private int countEntitiesHidden;
   private boolean debugFixTerrainFrustum = false;
   private ClippingHelper debugFixedClippingHelper;
   private final Vector4f[] debugTerrainMatrix = new Vector4f[8];
   private final Vector3d debugTerrainFrustumPosition = new Vector3d();
   private boolean vboEnabled = false;
   public IRenderChunkFactory renderChunkFactory;
   private double prevRenderSortX;
   private double prevRenderSortY;
   private double prevRenderSortZ;
   public boolean displayListEntitiesDirty = true;
   private CloudRenderer cloudRenderer;
   public Entity renderedEntity;
   public Set chunksToResortTransparency = new LinkedHashSet();
   public Set chunksToUpdateForced = new LinkedHashSet();
   private Deque visibilityDeque = new ArrayDeque();
   private List renderInfosEntities = new ArrayList(1024);
   private List renderInfosTileEntities = new ArrayList(1024);
   private List renderInfosNormal = new ArrayList(1024);
   private List renderInfosEntitiesNormal = new ArrayList(1024);
   private List renderInfosTileEntitiesNormal = new ArrayList(1024);
   private List renderInfosShadow = new ArrayList(1024);
   private List renderInfosEntitiesShadow = new ArrayList(1024);
   private List renderInfosTileEntitiesShadow = new ArrayList(1024);
   private int renderDistance = 0;
   private int renderDistanceSq = 0;
   private static final Set SET_ALL_FACINGS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(EnumFacing.VALUES)));
   private int countTileEntitiesRendered;
   private static final long ab = 92769071865155L;

   public RenderGlobal(Minecraft var1) {
      this.cloudRenderer = new CloudRenderer(var1);
      this.mc = var1;
      this.renderManager = var1.getRenderManager();
      this.renderEngine = var1.getTextureManager();
      this.renderEngine.bindTexture(locationForcefieldPng);
      GL11.glTexParameteri(3553, 10242, 10497);
      GL11.glTexParameteri(3553, 10243, 10497);
      GlStateManager.bindTexture(0);
      this.updateDestroyBlockIcons();
      this.vboEnabled = OpenGlHelper.useVbo();
      if (this.vboEnabled) {
         this.renderContainer = new VboRenderList();
         this.renderChunkFactory = new VboChunkFactory();
      } else {
         this.renderContainer = new RenderList();
         this.renderChunkFactory = new ListChunkFactory();
      }

      this.vertexBufferFormat = new VertexFormat();
      this.vertexBufferFormat.addElement(new VertexFormatElement(0, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUsage.POSITION, 3));
      this.generateStars();
      this.generateSky();
      this.generateSky2();
   }

   @Override
   public void onResourceManagerReload(IResourceManager var1) {
      this.updateDestroyBlockIcons();
   }

   private void updateDestroyBlockIcons() {
      TextureMap var4 = this.mc.getTextureMapBlocks();

      for (int var5 = 0; var5 < this.destroyBlockIcons.length; var5++) {
         this.destroyBlockIcons[var5] = var4.getAtlasSprite("minecraft:blocks/destroy_stage_" + var5);
      }
   }

   public void makeEntityOutlineShader() {
      if (OpenGlHelper.shadersSupported) {
         if (ShaderLinkHelper.getStaticShaderLinkHelper() == null) {
            ShaderLinkHelper.setNewStaticShaderLinkHelper();
         }

         ResourceLocation var4 = new ResourceLocation("shaders/post/entity_outline.json");

         try {
            this.entityOutlineShader = new ShaderGroup(this.mc.getTextureManager(), this.mc.getResourceManager(), this.mc.getFramebuffer(), var4);
            this.entityOutlineShader.createBindFramebuffers(this.mc.displayWidth, this.mc.displayHeight);
            this.entityOutlineFramebuffer = this.entityOutlineShader.getFramebufferRaw("final");
         } catch (IOException var6) {
            logger.warn("Failed to load shader: " + var4, var6);
            this.entityOutlineShader = null;
            this.entityOutlineFramebuffer = null;
         } catch (JsonSyntaxException var7) {
            logger.warn("Failed to load shader: " + var4, var7);
            this.entityOutlineShader = null;
            this.entityOutlineFramebuffer = null;
         }
      } else {
         this.entityOutlineShader = null;
         this.entityOutlineFramebuffer = null;
      }
   }

   public void renderEntityOutlineFramebuffer() {
      if (this.isRenderEntityOutlines()) {
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
         this.entityOutlineFramebuffer.framebufferRenderExt(this.mc.displayWidth, this.mc.displayHeight, false);
         GlStateManager.disableBlend();
      }
   }

   public boolean isRenderEntityOutlines() {
      return !Config.isFastRender() && !Config.isShaders() && !Config.isAntialiasing()
         ? this.entityOutlineFramebuffer != null
            && this.entityOutlineShader != null
            && this.mc.thePlayer != null
            && this.mc.thePlayer.isSpectator()
            && this.mc.gameSettings.keyBindSpectatorOutlines.isKeyDown()
         : false;
   }

   private void generateSky2() {
      Tessellator var4 = Tessellator.getInstance();
      WorldRenderer var5 = var4.getWorldRenderer();
      if (this.sky2VBO != null) {
         this.sky2VBO.deleteGlBuffers();
      }

      if (this.glSkyList2 >= 0) {
         GLAllocation.deleteDisplayLists(this.glSkyList2);
         this.glSkyList2 = -1;
      }

      if (this.vboEnabled) {
         this.sky2VBO = new VertexBuffer(this.vertexBufferFormat);
         this.renderSky(var5, -16.0F, true);
         var5.finishDrawing();
         var5.reset();
         this.sky2VBO.bufferData(var5.getByteBuffer());
      } else {
         this.glSkyList2 = GLAllocation.generateDisplayLists(1);
         GL11.glNewList(this.glSkyList2, 4864);
         this.renderSky(var5, -16.0F, true);
         var4.draw();
         GL11.glEndList();
      }
   }

   private void generateSky() {
      Tessellator var4 = Tessellator.getInstance();
      WorldRenderer var5 = var4.getWorldRenderer();
      if (this.skyVBO != null) {
         this.skyVBO.deleteGlBuffers();
      }

      if (this.glSkyList >= 0) {
         GLAllocation.deleteDisplayLists(this.glSkyList);
         this.glSkyList = -1;
      }

      if (this.vboEnabled) {
         this.skyVBO = new VertexBuffer(this.vertexBufferFormat);
         this.renderSky(var5, 16.0F, false);
         var5.finishDrawing();
         var5.reset();
         this.skyVBO.bufferData(var5.getByteBuffer());
      } else {
         this.glSkyList = GLAllocation.generateDisplayLists(1);
         GL11.glNewList(this.glSkyList, 4864);
         this.renderSky(var5, 16.0F, false);
         var4.draw();
         GL11.glEndList();
      }
   }

   private void renderSky(WorldRenderer var1, float var2, boolean var3) {
      var1.begin(7, DefaultVertexFormats.POSITION);

      for (int var9 = -384; var9 <= 384; var9 += 64) {
         for (int var10 = -384; var10 <= 384; var10 += 64) {
            float var11 = var9;
            float var12 = var9 + 64;
            if (var3) {
               var12 = var9;
               var11 = var9 + 64;
            }

            var1.pos(var11, var2, var10).endVertex();
            var1.pos(var12, var2, var10).endVertex();
            var1.pos(var12, var2, var10 + 64).endVertex();
            var1.pos(var11, var2, var10 + 64).endVertex();
         }
      }
   }

   private void generateStars() {
      Tessellator var4 = Tessellator.getInstance();
      WorldRenderer var5 = var4.getWorldRenderer();
      if (this.starVBO != null) {
         this.starVBO.deleteGlBuffers();
      }

      if (this.starGLCallList >= 0) {
         GLAllocation.deleteDisplayLists(this.starGLCallList);
         this.starGLCallList = -1;
      }

      if (this.vboEnabled) {
         this.starVBO = new VertexBuffer(this.vertexBufferFormat);
         this.renderStars(var5);
         var5.finishDrawing();
         var5.reset();
         this.starVBO.bufferData(var5.getByteBuffer());
      } else {
         this.starGLCallList = GLAllocation.generateDisplayLists(1);
         GlStateManager.pushMatrix();
         GL11.glNewList(this.starGLCallList, 4864);
         this.renderStars(var5);
         var4.draw();
         GL11.glEndList();
         GlStateManager.popMatrix();
      }
   }

   private void renderStars(WorldRenderer var1) {
      Random var5 = new Random(10842L);
      var1.begin(7, DefaultVertexFormats.POSITION);

      for (int var6 = 0; var6 < 1500; var6++) {
         double var7 = var5.nextFloat() * 2.0F - 1.0F;
         double var9 = var5.nextFloat() * 2.0F - 1.0F;
         double var11 = var5.nextFloat() * 2.0F - 1.0F;
         double var13 = 0.15F + var5.nextFloat() * 0.1F;
         double var15 = var7 * var7 + var9 * var9 + var11 * var11;
         if (var15 < 1.0 && var15 > 0.01) {
            var15 = 1.0 / Math.sqrt(var15);
            var7 *= var15;
            var9 *= var15;
            var11 *= var15;
            double var17 = var7 * 100.0;
            double var19 = var9 * 100.0;
            double var21 = var11 * 100.0;
            double var23 = Math.atan2(var7, var11);
            double var25 = Math.sin(var23);
            double var27 = Math.cos(var23);
            double var29 = Math.atan2(Math.sqrt(var7 * var7 + var11 * var11), var9);
            double var31 = Math.sin(var29);
            double var33 = Math.cos(var29);
            double var35 = var5.nextDouble() * Math.PI * 2.0;
            double var37 = Math.sin(var35);
            double var39 = Math.cos(var35);

            for (int var41 = 0; var41 < 4; var41++) {
               double var44 = ((var41 & 2) - 1) * var13;
               double var46 = ((var41 + 1 & 2) - 1) * var13;
               double var50 = var44 * var39 - var46 * var37;
               double var52 = var46 * var39 + var44 * var37;
               double var54 = var50 * var31 + 0.0 * var33;
               double var56 = 0.0 * var31 - var50 * var33;
               double var58 = var56 * var25 - var52 * var27;
               double var60 = var52 * var25 + var56 * var27;
               var1.pos(var17 + var58, var19 + var54, var21 + var60).endVertex();
            }
         }
      }
   }

   public void setWorldAndLoadRenderers(WorldClient var1) {
      if (this.theWorld != null) {
         this.theWorld.removeWorldAccess(this);
      }

      this.frustumUpdatePosX = Double.MIN_VALUE;
      this.frustumUpdatePosY = Double.MIN_VALUE;
      this.frustumUpdatePosZ = Double.MIN_VALUE;
      this.frustumUpdatePosChunkX = Integer.MIN_VALUE;
      this.frustumUpdatePosChunkY = Integer.MIN_VALUE;
      this.frustumUpdatePosChunkZ = Integer.MIN_VALUE;
      this.renderManager.set(var1);
      this.theWorld = var1;
      if (Config.isDynamicLights()) {
         DynamicLights.clear();
      }

      if (var1 != null) {
         var1.addWorldAccess(this);
         this.loadRenderers();
      }
   }

   public void loadRenderers() {
      if (this.theWorld != null) {
         this.displayListEntitiesDirty = true;
         Blocks.leaves.setGraphicsLevel(Config.isTreesFancy());
         Blocks.leaves2.setGraphicsLevel(Config.isTreesFancy());
         BlockModelRenderer.updateAoLightValue();
         if (Config.isDynamicLights()) {
            DynamicLights.clear();
         }

         this.renderDistanceChunks = this.mc.gameSettings.renderDistanceChunks;
         this.renderDistance = this.renderDistanceChunks * 16;
         this.renderDistanceSq = this.renderDistance * this.renderDistance;
         boolean var4 = this.vboEnabled;
         this.vboEnabled = OpenGlHelper.useVbo();
         if (var4 && !this.vboEnabled) {
            this.renderContainer = new RenderList();
            this.renderChunkFactory = new ListChunkFactory();
         } else if (!var4 && this.vboEnabled) {
            this.renderContainer = new VboRenderList();
            this.renderChunkFactory = new VboChunkFactory();
         }

         if (var4 != this.vboEnabled) {
            this.generateStars();
            this.generateSky();
            this.generateSky2();
         }

         if (this.viewFrustum != null) {
            this.viewFrustum.deleteGlResources();
         }

         this.stopChunkUpdates();
         Set var6 = this.A;
         synchronized (this.A) {
            this.A.clear();
         }

         this.viewFrustum = new ViewFrustum(this.theWorld, this.mc.gameSettings.renderDistanceChunks, this, this.renderChunkFactory);
         if (this.theWorld != null) {
            Entity var9 = this.mc.getRenderViewEntity();
            if (var9 != null) {
               this.viewFrustum.updateChunkPositions(var9.posX, var9.posZ);
            }
         }

         this.O = 2;
      }
   }

   public void stopChunkUpdates() {
      this.chunksToUpdate.clear();
      this.renderDispatcher.stopChunkUpdates();
   }

   public void createBindEntityOutlineFbs(int var1, int var2) {
      if (OpenGlHelper.shadersSupported && this.entityOutlineShader != null) {
         this.entityOutlineShader.createBindFramebuffers(var1, var2);
      }
   }

   public void renderEntities(Entity var1, ICamera var2, float var3) {
      long var4 = ab ^ 25391695357848L;
      int var7 = 0;
      if (Reflector.MinecraftForgeClient_getRenderPass.exists()) {
         var7 = Reflector.callInt(Reflector.MinecraftForgeClient_getRenderPass);
      }

      if (this.O > 0) {
         if (var7 > 0) {
            return;
         }

         this.O--;
      } else {
         double var8 = var1.prevPosX + (var1.posX - var1.prevPosX) * var3;
         double var10 = var1.prevPosY + (var1.posY - var1.prevPosY) * var3;
         double var12 = var1.prevPosZ + (var1.posZ - var1.prevPosZ) * var3;
         this.theWorld.theProfiler.startSection("prepare");
         TileEntityRendererDispatcher.instance
            .cacheActiveRenderInfo(this.theWorld, this.mc.getTextureManager(), this.mc.fontRendererObj, this.mc.getRenderViewEntity(), var3);
         this.renderManager
            .cacheActiveRenderInfo(this.theWorld, this.mc.fontRendererObj, this.mc.getRenderViewEntity(), this.mc.pointedEntity, this.mc.gameSettings, var3);
         if (var7 == 0) {
            this.countEntitiesTotal = 0;
            this.countEntitiesRendered = 0;
            this.countEntitiesHidden = 0;
            this.countTileEntitiesRendered = 0;
         }

         Entity var14 = this.mc.getRenderViewEntity();
         double var15 = var14.lastTickPosX + (var14.posX - var14.lastTickPosX) * var3;
         double var17 = var14.lastTickPosY + (var14.posY - var14.lastTickPosY) * var3;
         double var19 = var14.lastTickPosZ + (var14.posZ - var14.lastTickPosZ) * var3;
         TileEntityRendererDispatcher.staticPlayerX = var15;
         TileEntityRendererDispatcher.staticPlayerY = var17;
         TileEntityRendererDispatcher.staticPlayerZ = var19;
         this.renderManager.setRenderPosition(var15, var17, var19);
         this.mc.entityRenderer.enableLightmap();
         this.theWorld.theProfiler.endStartSection("global");
         List var21 = this.theWorld.getLoadedEntityList();
         if (var7 == 0) {
            this.countEntitiesTotal = var21.size();
         }

         if (Config.isFogOff() && this.mc.entityRenderer.fogStandard) {
            GlStateManager.disableFog();
         }

         boolean var22 = Reflector.B9.exists();
         boolean var23 = Reflector.kC.exists();

         for (int var24 = 0; var24 < this.theWorld.weatherEffects.size(); var24++) {
            Entity var25 = this.theWorld.weatherEffects.get(var24);
            if (!var22 || Reflector.callBoolean(var25, Reflector.B9, var7)) {
               this.countEntitiesRendered++;
               if (var25.isInRangeToRender3d(var8, var10, var12)) {
                  this.renderManager.renderEntitySimple(var25, var3);
               }
            }
         }

         if (this.isRenderEntityOutlines()) {
            GlStateManager.depthFunc(519);
            GlStateManager.disableFog();
            this.entityOutlineFramebuffer.framebufferClear();
            this.entityOutlineFramebuffer.bindFramebuffer(false);
            this.theWorld.theProfiler.endStartSection("entityOutlines");
            RenderHelper.disableStandardItemLighting();
            this.renderManager.S(true);

            for (int var40 = 0; var40 < var21.size(); var40++) {
               Entity var42 = (Entity)var21.get(var40);
               if (!var22 || Reflector.callBoolean(var42, Reflector.B9, var7)) {
                  boolean var26 = this.mc.getRenderViewEntity() instanceof EntityLivingBase
                     && ((EntityLivingBase)this.mc.getRenderViewEntity()).isPlayerSleeping();
                  boolean var27 = var42.isInRangeToRender3d(var8, var10, var12)
                     && (var42.ignoreFrustumCheck || var2.isBoundingBoxInFrustum(var42.getEntityBoundingBox()) || var42.riddenByEntity == this.mc.thePlayer)
                     && var42 instanceof EntityPlayer;
                  if ((var42 != this.mc.getRenderViewEntity() || this.mc.gameSettings.thirdPersonView != 0 || var26) && var27) {
                     this.renderManager.renderEntitySimple(var42, var3);
                  }
               }
            }

            this.renderManager.S(false);
            RenderHelper.enableStandardItemLighting();
            long var10000 = 666213386336602415L ^ var4;
            GlStateManager.depthMask(false);
            this.entityOutlineShader.t(var3);
            GlStateManager.enableLighting();
            GlStateManager.depthMask(true);
            this.mc.getFramebuffer().bindFramebuffer(false);
            GlStateManager.enableFog();
            GlStateManager.enableBlend();
            GlStateManager.enableColorMaterial();
            GlStateManager.depthFunc(515);
            GlStateManager.enableDepth();
            GlStateManager.enableAlpha();
         }

         this.theWorld.theProfiler.endStartSection("entities");
         boolean var41 = Config.isShaders();
         if (var41) {
            Shaders.k();
         }

         Iterator var43 = this.renderInfosEntities.iterator();
         boolean var44 = this.mc.gameSettings.fancyGraphics;
         this.mc.gameSettings.fancyGraphics = Config.g();

         while (var43.hasNext()) {
            RenderGlobal.ContainerLocalRenderInformation var45 = (RenderGlobal.ContainerLocalRenderInformation)var43.next();
            Chunk var28 = this.theWorld.getChunkFromBlockCoords(var45.renderChunk.getPosition());
            ClassInheritanceMultiMap var29 = var28.getEntityLists()[var45.renderChunk.getPosition().getY() / 16];
            if (!var29.isEmpty()) {
               for (Object var31$raw : var29) {
                  Entity var31 = (Entity)var31$raw;
                  if (!var22 || Reflector.callBoolean(var31, Reflector.B9, var7)) {
                     boolean var32 = this.renderManager.shouldRender(var31, var2, var8, var10, var12) || var31.riddenByEntity == this.mc.thePlayer;
                     if (var32) {
                        boolean var33 = this.mc.getRenderViewEntity() instanceof EntityLivingBase
                           ? ((EntityLivingBase)this.mc.getRenderViewEntity()).isPlayerSleeping()
                           : false;
                        if (var31 == this.mc.getRenderViewEntity() && this.mc.gameSettings.thirdPersonView == 0 && !var33
                           || !(var31.posY < 0.0) && !(var31.posY >= 256.0) && !this.theWorld.isBlockLoaded(new BlockPos(var31))) {
                           continue;
                        }

                        this.countEntitiesRendered++;
                        if (var31.getClass() == EntityItemFrame.class) {
                           var31.renderDistanceWeight = 0.06;
                        }

                        this.renderedEntity = var31;
                        if (var41) {
                           Shaders.nextEntity(var31);
                        }

                        this.renderManager.renderEntitySimple(var31, var3);
                        this.renderedEntity = null;
                     }

                     if (!var32 && var31 instanceof EntityWitherSkull) {
                        if (var41) {
                           Shaders.nextEntity(var31);
                        }

                        this.mc.getRenderManager().renderWitherSkull(var31, var3);
                     }
                  }
               }
            }
         }

         this.mc.gameSettings.fancyGraphics = var44;
         FontRenderer var46 = TileEntityRendererDispatcher.instance.getFontRenderer();
         if (var41) {
            Shaders.Q();
            Shaders.beginBlockEntities();
         }

         this.theWorld.theProfiler.endStartSection("blockentities");
         RenderHelper.enableStandardItemLighting();
         if (Reflector.ForgeTileEntityRendererDispatcher_preDrawBatch.exists()) {
            Reflector.call(TileEntityRendererDispatcher.instance, Reflector.ForgeTileEntityRendererDispatcher_preDrawBatch);
         }

         for (Object var49 : this.renderInfosTileEntities) {
            RenderGlobal.ContainerLocalRenderInformation var52 = (RenderGlobal.ContainerLocalRenderInformation)var49;
            List var55 = var52.renderChunk.getCompiledChunk().getTileEntities();
            if (!var55.isEmpty()) {
               for (Object var62$raw : var55) {
                  TileEntity var62 = (TileEntity)var62$raw;
                  if (var23) {
                     if (!Reflector.callBoolean(var62, Reflector.kC, var7)) {
                        continue;
                     }

                     AxisAlignedBB var34 = (AxisAlignedBB)Reflector.call(var62, Reflector.ForgeTileEntity_getRenderBoundingBox);
                     if (var34 != null && !var2.isBoundingBoxInFrustum(var34)) {
                        continue;
                     }
                  }

                  Class var66 = var62.getClass();
                  if (var66 == TileEntitySign.class && !Config.zoomMode) {
                     EntityPlayerSP var35 = this.mc.thePlayer;
                     double var36 = var62.getDistanceSq(var35.posX, var35.posY, var35.posZ);
                     if (var36 > 256.0) {
                        var46.Z = false;
                     }
                  }

                  if (var41) {
                     Shaders.nextBlockEntity(var62);
                  }

                  TileEntityRendererDispatcher.instance.renderTileEntity(var62, var3, -1);
                  this.countTileEntitiesRendered++;
                  var46.Z = true;
               }
            }
         }

         Set var50 = this.A;
         synchronized (this.A) {
            for (Object var56 : this.A) {
               if (var23) {
                  if (!Reflector.callBoolean(var56, Reflector.kC, var7)) {
                     continue;
                  }

                  AxisAlignedBB var59 = (AxisAlignedBB)Reflector.call(var56, Reflector.ForgeTileEntity_getRenderBoundingBox);
                  if (var59 != null && !var2.isBoundingBoxInFrustum(var59)) {
                     continue;
                  }
               }

               Class var60 = var56.getClass();
               if (var60 == TileEntitySign.class && !Config.zoomMode) {
                  EntityPlayerSP var63 = this.mc.thePlayer;
                  double var67 = ((TileEntity)var56).getDistanceSq(var63.posX, var63.posY, var63.posZ);
                  if (var67 > 256.0) {
                     var46.Z = false;
                  }
               }

               if (var41) {
                  Shaders.nextBlockEntity((TileEntity)var56);
               }

               TileEntityRendererDispatcher.instance.renderTileEntity((TileEntity)var56, var3, -1);
               var46.Z = true;
            }
         }

         if (Reflector.ForgeTileEntityRendererDispatcher_drawBatch.exists()) {
            Reflector.call(TileEntityRendererDispatcher.instance, Reflector.ForgeTileEntityRendererDispatcher_drawBatch, var7);
         }

         this.preRenderDamagedBlocks();

         for (Object var54 : this.damagedBlocks.values()) {
            BlockPos var57 = ((DestroyBlockProgress)var54).getPosition();
            TileEntity var61 = this.theWorld.getTileEntity(var57);
            if (var61 instanceof TileEntityChest) {
               TileEntityChest var64 = (TileEntityChest)var61;
               if (var64.adjacentChestXNeg != null) {
                  var57 = var57.offset(EnumFacing.WEST);
                  var61 = this.theWorld.getTileEntity(var57);
               } else if (var64.adjacentChestZNeg != null) {
                  var57 = var57.offset(EnumFacing.NORTH);
                  var61 = this.theWorld.getTileEntity(var57);
               }
            }

            Block var65 = this.theWorld.getBlockState(var57).getBlock();
            boolean var68;
            if (var23) {
               var68 = false;
               if (var61 != null
                  && Reflector.callBoolean(var61, Reflector.kC, var7)
                  && Reflector.callBoolean(var61, Reflector.ForgeTileEntity_canRenderBreaking)) {
                  AxisAlignedBB var69 = (AxisAlignedBB)Reflector.call(var61, Reflector.ForgeTileEntity_getRenderBoundingBox);
                  if (var69 != null) {
                     var68 = var2.isBoundingBoxInFrustum(var69);
                  }
               }
            } else {
               var68 = var61 != null
                  && (var65 instanceof BlockChest || var65 instanceof BlockEnderChest || var65 instanceof BlockSign || var65 instanceof BlockSkull);
            }

            if (var68) {
               if (var41) {
                  Shaders.nextBlockEntity(var61);
               }

               TileEntityRendererDispatcher.instance.renderTileEntity(var61, var3, ((DestroyBlockProgress)var54).getPartialBlockDamage());
            }
         }

         this.postRenderDamagedBlocks();
         this.mc.entityRenderer.disableLightmap();
         this.mc.mcProfiler.endSection();
      }
   }

   public String getDebugInfoRenders() {
      int var4 = this.viewFrustum.renderChunks.length;
      int var5 = 0;

      for (Object var7 : this.renderInfos) {
         RenderGlobal.ContainerLocalRenderInformation var8 = (RenderGlobal.ContainerLocalRenderInformation)var7;
         CompiledChunk var9 = var8.renderChunk.compiledChunk;
         if (var9 != CompiledChunk.DUMMY && !var9.isEmpty()) {
            var5++;
         }
      }

      return String.format(
         "C: %d/%d %sD: %d, %s", var5, var4, this.mc.renderChunksMany ? "(s) " : "", this.renderDistanceChunks, this.renderDispatcher.getDebugInfo()
      );
   }

   public String getDebugInfoEntities() {
      return "E: "
         + this.countEntitiesRendered
         + "/"
         + this.countEntitiesTotal
         + ", B: "
         + this.countEntitiesHidden
         + ", I: "
         + (this.countEntitiesTotal - this.countEntitiesHidden - this.countEntitiesRendered)
         + ", "
         + Config.getVersionDebug();
   }

   public void setupTerrain(Entity var1, double var2, ICamera var4, int var5, boolean var6) {
      SetupTerrainEvent var10 = new SetupTerrainEvent();
      Astolfo.INSTANCE.getEventBus().post(var10);
      if (var10.isCancelled()) {
         var6 = true;
      }

      if (this.mc.gameSettings.renderDistanceChunks != this.renderDistanceChunks) {
         this.loadRenderers();
      }

      this.theWorld.theProfiler.startSection("camera");
      double var11 = var1.posX - this.frustumUpdatePosX;
      double var13 = var1.posY - this.frustumUpdatePosY;
      double var15 = var1.posZ - this.frustumUpdatePosZ;
      if (this.frustumUpdatePosChunkX != var1.chunkCoordX
         || this.frustumUpdatePosChunkY != var1.chunkCoordY
         || this.frustumUpdatePosChunkZ != var1.chunkCoordZ
         || var11 * var11 + var13 * var13 + var15 * var15 > 16.0) {
         this.frustumUpdatePosX = var1.posX;
         this.frustumUpdatePosY = var1.posY;
         this.frustumUpdatePosZ = var1.posZ;
         this.frustumUpdatePosChunkX = var1.chunkCoordX;
         this.frustumUpdatePosChunkY = var1.chunkCoordY;
         this.frustumUpdatePosChunkZ = var1.chunkCoordZ;
         this.viewFrustum.updateChunkPositions(var1.posX, var1.posZ);
      }

      if (Config.isDynamicLights()) {
         DynamicLights.update(this);
      }

      this.theWorld.theProfiler.endStartSection("renderlistcamera");
      double var17 = var1.lastTickPosX + (var1.posX - var1.lastTickPosX) * var2;
      double var19 = var1.lastTickPosY + (var1.posY - var1.lastTickPosY) * var2;
      double var21 = var1.lastTickPosZ + (var1.posZ - var1.lastTickPosZ) * var2;
      this.renderContainer.initialize(var17, var19, var21);
      this.theWorld.theProfiler.endStartSection("cull");
      if (this.debugFixedClippingHelper != null) {
         Frustum var23 = new Frustum(this.debugFixedClippingHelper);
         var23.setPosition(this.debugTerrainFrustumPosition.x, this.debugTerrainFrustumPosition.y, this.debugTerrainFrustumPosition.z);
         var4 = var23;
      }

      this.mc.mcProfiler.endStartSection("culling");
      BlockPos var39 = new BlockPos(var17, var19 + var1.getEyeHeight(), var21);
      RenderChunk var24 = this.viewFrustum.getRenderChunk(var39);
      BlockPos var25 = new BlockPos(
         MathHelper.floor_double(var17 / 16.0) * 16, MathHelper.floor_double(var19 / 16.0) * 16, MathHelper.floor_double(var21 / 16.0) * 16
      );
      this.displayListEntitiesDirty = this.displayListEntitiesDirty
         || !this.chunksToUpdate.isEmpty()
         || var1.posX != this.lastViewEntityX
         || var1.posY != this.lastViewEntityY
         || var1.posZ != this.lastViewEntityZ
         || var1.rotationPitch != this.lastViewEntityPitch
         || var1.rotationYaw != this.lastViewEntityYaw;
      this.lastViewEntityX = var1.posX;
      this.lastViewEntityY = var1.posY;
      this.lastViewEntityZ = var1.posZ;
      this.lastViewEntityPitch = var1.rotationPitch;
      this.lastViewEntityYaw = var1.rotationYaw;
      boolean var26 = this.debugFixedClippingHelper != null;
      Lagometer.timerVisibility.start();
      if (Shaders.isShadowPass) {
         this.renderInfos = this.renderInfosShadow;
         this.renderInfosEntities = this.renderInfosEntitiesShadow;
         this.renderInfosTileEntities = this.renderInfosTileEntitiesShadow;
         if (!var26 && this.displayListEntitiesDirty) {
            this.renderInfos.clear();
            this.renderInfosEntities.clear();
            this.renderInfosTileEntities.clear();
            RenderInfoLazy var27 = new RenderInfoLazy();
            Iterator var28 = ShadowUtils.Q(this.theWorld, var2, var1, this.renderDistanceChunks, this.viewFrustum);

            while (var28.hasNext()) {
               RenderChunk var29 = (RenderChunk)var28.next();
               if (var29 != null) {
                  var27.setRenderChunk(var29);
                  if (!var29.compiledChunk.isEmpty() || var29.Q()) {
                     this.renderInfos.add(var27.getRenderInfo());
                  }

                  BlockPos var30 = var29.getPosition();
                  if (ChunkUtils.hasEntities(this.theWorld.getChunkFromBlockCoords(var30))) {
                     this.renderInfosEntities.add(var27.getRenderInfo());
                  }

                  if (var29.getCompiledChunk().getTileEntities().size() > 0) {
                     this.renderInfosTileEntities.add(var27.getRenderInfo());
                  }
               }
            }
         }
      } else {
         this.renderInfos = this.renderInfosNormal;
         this.renderInfosEntities = this.renderInfosEntitiesNormal;
         this.renderInfosTileEntities = this.renderInfosTileEntitiesNormal;
      }

      if (!var26 && this.displayListEntitiesDirty && !Shaders.isShadowPass) {
         this.displayListEntitiesDirty = false;
         this.renderInfos.clear();
         this.renderInfosEntities.clear();
         this.renderInfosTileEntities.clear();
         this.visibilityDeque.clear();
         Deque var40 = this.visibilityDeque;
         boolean var42 = this.mc.renderChunksMany;
         if (var24 != null) {
            boolean var45 = false;
            RenderGlobal.ContainerLocalRenderInformation var49 = new RenderGlobal.ContainerLocalRenderInformation(var24, (EnumFacing)null, 0);
            Set var52 = SET_ALL_FACINGS;
            if (var52.size() == 1) {
               Vector3f var54 = this.getViewVector(var1, var2);
               EnumFacing var33 = EnumFacing.getFacingFromVector(var54.x, var54.y, var54.z).getOpposite();
               var52.remove(var33);
            }

            if (var52.isEmpty()) {
               var45 = true;
            }

            if (var45 && !var6) {
               this.renderInfos.add(var49);
            } else {
               if (var6 && this.theWorld.getBlockState(var39).getBlock().isOpaqueCube()) {
                  var42 = false;
               }

               var24.setFrameIndex(var5);
               var40.add(var49);
            }
         } else {
            int var44 = var39.getY() > 0 ? 248 : 8;

            for (int var48 = -this.renderDistanceChunks; var48 <= this.renderDistanceChunks; var48++) {
               for (int var31 = -this.renderDistanceChunks; var31 <= this.renderDistanceChunks; var31++) {
                  RenderChunk var32 = this.viewFrustum.getRenderChunk(new BlockPos((var48 << 4) + 8, var44, (var31 << 4) + 8));
                  if (var32 != null && var4.isBoundingBoxInFrustum(var32.boundingBox)) {
                     var32.setFrameIndex(var5);
                     var40.add(new RenderGlobal.ContainerLocalRenderInformation(var32, (EnumFacing)null, 0));
                  }
               }
            }
         }

         EnumFacing[] var46 = EnumFacing.VALUES;
         int var50 = var46.length;

         while (!var40.isEmpty()) {
            RenderGlobal.ContainerLocalRenderInformation var53 = (RenderGlobal.ContainerLocalRenderInformation)var40.poll();
            RenderChunk var55 = var53.renderChunk;
            EnumFacing var56 = var53.facing;
            BlockPos var34 = var55.getPosition();
            if (!var55.compiledChunk.isEmpty() || var55.Q()) {
               this.renderInfos.add(var53);
            }

            if (ChunkUtils.hasEntities(this.theWorld.getChunkFromBlockCoords(var34))) {
               this.renderInfosEntities.add(var53);
            }

            if (var55.getCompiledChunk().getTileEntities().size() > 0) {
               this.renderInfosTileEntities.add(var53);
            }

            for (int var35 = 0; var35 < var50; var35++) {
               EnumFacing var36 = var46[var35];
               if ((!var42 || !var53.setFacing.contains(var36.getOpposite()))
                  && (!var42 || var56 == null || var55.getCompiledChunk().isVisible(var56.getOpposite(), var36))) {
                  RenderChunk var37 = this.getRenderChunkOffset(var39, var55, var36);
                  if (var37 != null && var37.setFrameIndex(var5) && var4.isBoundingBoxInFrustum(var37.boundingBox)) {
                     RenderGlobal.ContainerLocalRenderInformation var38 = new RenderGlobal.ContainerLocalRenderInformation(var37, var36, var53.counter + 1);
                     var38.setFacing.addAll(var53.setFacing);
                     var38.setFacing.add(var36);
                     var40.add(var38);
                  }
               }
            }
         }
      }

      if (this.debugFixTerrainFrustum) {
         this.fixTerrainFrustum(var17, var19, var21);
         this.debugFixTerrainFrustum = false;
      }

      Lagometer.timerVisibility.end();
      if (Shaders.isShadowPass) {
         Shaders.UM();
      } else {
         this.renderDispatcher.clearChunkUpdates();
         Set var41 = this.chunksToUpdate;
         this.chunksToUpdate = Sets.newLinkedHashSet();
         Iterator var43 = this.renderInfos.iterator();
         Lagometer.timerChunkUpdate.start();

         while (var43.hasNext()) {
            RenderGlobal.ContainerLocalRenderInformation var47 = (RenderGlobal.ContainerLocalRenderInformation)var43.next();
            RenderChunk var51 = var47.renderChunk;
            if (var51.Q() || var41.contains(var51)) {
               this.displayListEntitiesDirty = true;
               if (this.isPositionInRenderChunk(var25, var47.renderChunk)) {
                  if (!var51.P()) {
                     this.chunksToUpdateForced.add(var51);
                  } else {
                     this.mc.mcProfiler.startSection("build near");
                     this.renderDispatcher.updateChunkNow(var51);
                     var51.setNeedsUpdate(false);
                     this.mc.mcProfiler.endSection();
                  }
               } else {
                  this.chunksToUpdate.add(var51);
               }
            }
         }

         Lagometer.timerChunkUpdate.end();
         this.chunksToUpdate.addAll(var41);
         this.mc.mcProfiler.endSection();
      }
   }

   private boolean isPositionInRenderChunk(BlockPos var1, RenderChunk var2) {
      BlockPos var6 = var2.getPosition();
      return MathHelper.abs_int(var1.getX() - var6.getX()) > 16
         ? false
         : (MathHelper.abs_int(var1.getY() - var6.getY()) > 16 ? false : MathHelper.abs_int(var1.getZ() - var6.getZ()) <= 16);
   }

   private RenderChunk getRenderChunkOffset(BlockPos var1, RenderChunk var2, EnumFacing var3) {
      BlockPos var7 = var2.getPositionOffset16(var3);
      if (var7.getY() >= 0 && var7.getY() < 256) {
         int var8 = MathHelper.abs_int(var1.getX() - var7.getX());
         int var9 = MathHelper.abs_int(var1.getZ() - var7.getZ());
         if (Config.isFogOff()) {
            if (var8 > this.renderDistance || var9 > this.renderDistance) {
               return null;
            }
         } else {
            int var10 = var8 * var8 + var9 * var9;
            if (var10 > this.renderDistanceSq) {
               return null;
            }
         }

         return this.viewFrustum.getRenderChunk(var7);
      } else {
         return null;
      }
   }

   private void fixTerrainFrustum(double var1, double var3, double var5) {
      this.debugFixedClippingHelper = new ClippingHelperImpl();
      ((ClippingHelperImpl)this.debugFixedClippingHelper).init();
      Matrix4f var10 = new Matrix4f(this.debugFixedClippingHelper.modelviewMatrix);
      var10.transpose();
      Matrix4f var11 = new Matrix4f(this.debugFixedClippingHelper.projectionMatrix);
      var11.transpose();
      Matrix4f var12 = new Matrix4f();
      Matrix4f.mul(var11, var10, var12);
      var12.invert();
      this.debugTerrainFrustumPosition.x = var1;
      this.debugTerrainFrustumPosition.y = var3;
      this.debugTerrainFrustumPosition.z = var5;
      this.debugTerrainMatrix[0] = new Vector4f(-1.0F, -1.0F, -1.0F, 1.0F);
      this.debugTerrainMatrix[1] = new Vector4f(1.0F, -1.0F, -1.0F, 1.0F);
      this.debugTerrainMatrix[2] = new Vector4f(1.0F, 1.0F, -1.0F, 1.0F);
      this.debugTerrainMatrix[3] = new Vector4f(-1.0F, 1.0F, -1.0F, 1.0F);
      this.debugTerrainMatrix[4] = new Vector4f(-1.0F, -1.0F, 1.0F, 1.0F);
      this.debugTerrainMatrix[5] = new Vector4f(1.0F, -1.0F, 1.0F, 1.0F);
      this.debugTerrainMatrix[6] = new Vector4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.debugTerrainMatrix[7] = new Vector4f(-1.0F, 1.0F, 1.0F, 1.0F);

      for (int var13 = 0; var13 < 8; var13++) {
         Matrix4f.transform(var12, this.debugTerrainMatrix[var13], this.debugTerrainMatrix[var13]);
         this.debugTerrainMatrix[var13].x = this.debugTerrainMatrix[var13].x / this.debugTerrainMatrix[var13].w;
         this.debugTerrainMatrix[var13].y = this.debugTerrainMatrix[var13].y / this.debugTerrainMatrix[var13].w;
         this.debugTerrainMatrix[var13].z = this.debugTerrainMatrix[var13].z / this.debugTerrainMatrix[var13].w;
         this.debugTerrainMatrix[var13].w = 1.0F;
      }
   }

   public Vector3f getViewVector(Entity var1, double var2) {
      float var6 = (float)(var1.prevRotationPitch + (var1.rotationPitch - var1.prevRotationPitch) * var2);
      float var7 = (float)(var1.prevRotationYaw + (var1.rotationYaw - var1.prevRotationYaw) * var2);
      if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 2) {
         var6 += 180.0F;
      }

      float var8 = MathHelper.cos(-var7 * (float) (Math.PI / 180.0) - (float) Math.PI);
      float var9 = MathHelper.sin(-var7 * (float) (Math.PI / 180.0) - (float) Math.PI);
      float var10 = -MathHelper.cos(-var6 * (float) (Math.PI / 180.0));
      float var11 = MathHelper.sin(-var6 * (float) (Math.PI / 180.0));
      return new Vector3f(var9 * var10, var11, var8 * var10);
   }

   public int renderBlockLayer(EnumWorldBlockLayer var1, double var2, int var4, Entity var5) {
      RenderHelper.disableStandardItemLighting();
      if (var1 == EnumWorldBlockLayer.TRANSLUCENT) {
         this.mc.mcProfiler.startSection("translucent_sort");
         double var9 = var5.posX - this.prevRenderSortX;
         double var11 = var5.posY - this.prevRenderSortY;
         double var13 = var5.posZ - this.prevRenderSortZ;
         if (var9 * var9 + var11 * var11 + var13 * var13 > 1.0) {
            this.prevRenderSortX = var5.posX;
            this.prevRenderSortY = var5.posY;
            this.prevRenderSortZ = var5.posZ;
            int var15 = 0;
            Iterator var16 = this.renderInfos.iterator();
            this.chunksToResortTransparency.clear();

            while (var16.hasNext()) {
               RenderGlobal.ContainerLocalRenderInformation var17 = (RenderGlobal.ContainerLocalRenderInformation)var16.next();
               if (var17.renderChunk.compiledChunk.isLayerStarted(var1) && var15++ < 15) {
                  this.chunksToResortTransparency.add(var17.renderChunk);
               }
            }
         }

         this.mc.mcProfiler.endSection();
      }

      this.mc.mcProfiler.startSection("filterempty");
      int var18 = 0;
      boolean var10 = var1 == EnumWorldBlockLayer.TRANSLUCENT;
      int var19 = var10 ? this.renderInfos.size() - 1 : 0;
      int var12 = var10 ? -1 : this.renderInfos.size();
      int var20 = var10 ? -1 : 1;

      for (int var14 = var19; var14 != var12; var14 += var20) {
         RenderChunk var21 = this.renderInfos.get(var14).renderChunk;
         if (!var21.getCompiledChunk().isLayerEmpty(var1)) {
            var18++;
            this.renderContainer.addRenderChunk(var21, var1);
         }
      }

      if (var18 == 0) {
         this.mc.mcProfiler.endSection();
         return var18;
      }

      if (Config.isFogOff() && this.mc.entityRenderer.fogStandard) {
         GlStateManager.disableFog();
      }

      this.mc.mcProfiler.endStartSection("render_" + var1);
      this.renderBlockLayer(var1);
      this.mc.mcProfiler.endSection();
      return var18;
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void renderBlockLayer(EnumWorldBlockLayer var1) {
      long var2 = ab ^ 5357906993609L;
      this.mc.entityRenderer.enableLightmap();
      if (OpenGlHelper.useVbo()) {
         GL11.glEnableClientState(32884);
         OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
         long var10000 = 6636993951462363170L ^ var2;
         GL11.glEnableClientState(32888);
         OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
         var10000 = 8218702202729655505L ^ var2;
         GL11.glEnableClientState(32888);
         OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
         var10000 = 8891881639302399360L ^ var2;
         GL11.glEnableClientState(32886);
      }

      if (Config.isShaders()) {
         ShadersRender.preRenderChunkLayer(var1);
      }

      this.renderContainer.renderChunkLayer(var1);
      if (Config.isShaders()) {
         ShadersRender.postRenderChunkLayer(var1);
      }

      if (OpenGlHelper.useVbo()) {
         for (VertexFormatElement var6 : DefaultVertexFormats.BLOCK.getElements()) {
            VertexFormatElement.EnumUsage var7 = var6.getUsage();
            int var8 = var6.getIndex();
            switch (var7) {
               case POSITION:
                  GL11.glDisableClientState(32884);
                  break;
               case UV:
                  OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit + var8);
                  GL11.glDisableClientState(32888);
                  OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
                  break;
               case COLOR:
                  GL11.glDisableClientState(32886);
                  GlStateManager.resetColor();
            }
         }
      }

      this.mc.entityRenderer.disableLightmap();
   }

   private void cleanupDamagedBlocks(Iterator<DestroyBlockProgress> var1) {
      while (var1.hasNext()) {
         DestroyBlockProgress var5 = (DestroyBlockProgress)var1.next();
         int var6 = var5.getCreationCloudUpdateTick();
         if (this.cloudTickCounter - var6 > 400) {
            var1.remove();
         }
      }
   }

   public void updateClouds() {
      if (Config.isShaders() && Keyboard.isKeyDown(61) && Keyboard.isKeyDown(19)) {
         Shaders.uninit();
         Shaders.loadShaderPack();
      }

      this.cloudTickCounter++;
      if (this.cloudTickCounter % 20 == 0) {
         this.cleanupDamagedBlocks(this.damagedBlocks.values().iterator());
      }
   }

   private void renderSkyEnd() {
      long var1 = ab ^ 4544908564813L;
      if (Config.K()) {
         GlStateManager.disableFog();
         GlStateManager.disableAlpha();
         long var10000 = 7682473096698927075L ^ var1;
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
         RenderHelper.disableStandardItemLighting();
         GlStateManager.depthMask(false);
         this.renderEngine.bindTexture(locationEndSkyPng);
         Tessellator var4 = Tessellator.getInstance();
         WorldRenderer var5 = var4.getWorldRenderer();

         for (int var6 = 0; var6 < 6; var6++) {
            GlStateManager.pushMatrix();
            if (var6 == 1) {
               GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            }

            if (var6 == 2) {
               GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
            }

            if (var6 == 3) {
               GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
            }

            if (var6 == 4) {
               GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
            }

            if (var6 == 5) {
               GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);
            }

            var5.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            var5.pos(-100.0, -100.0, -100.0).tex(0.0, 0.0).color(40, 40, 40, 255).endVertex();
            var5.pos(-100.0, -100.0, 100.0).tex(0.0, 16.0).color(40, 40, 40, 255).endVertex();
            var5.pos(100.0, -100.0, 100.0).tex(16.0, 16.0).color(40, 40, 40, 255).endVertex();
            var5.pos(100.0, -100.0, -100.0).tex(16.0, 0.0).color(40, 40, 40, 255).endVertex();
            var4.draw();
            GlStateManager.popMatrix();
         }

         GlStateManager.depthMask(true);
         GlStateManager.enableTexture2D();
         GlStateManager.enableAlpha();
      }
   }

   public void renderSky(float var1, int var2) {
      long var3 = ab ^ 120248783193332L;
      if (Reflector.ForgeWorldProvider_getSkyRenderer.exists()) {
         WorldProvider var6 = this.mc.theWorld.provider;
         Object var7 = Reflector.call(var6, Reflector.ForgeWorldProvider_getSkyRenderer);
         if (var7 != null) {
            Reflector.callVoid(var7, Reflector.IRenderHandler_render, var1, this.theWorld, this.mc);
            return;
         }
      }

      if (this.mc.theWorld.provider.getDimensionId() == 1) {
         this.renderSkyEnd();
      } else if (this.mc.theWorld.provider.isSurfaceWorld()) {
         GlStateManager.disableTexture2D();
         boolean var23 = Config.isShaders();
         if (var23) {
            Shaders.disableTexture2D();
         }

         Vec3 var24 = this.theWorld.getSkyColor(this.mc.getRenderViewEntity(), var1);
         var24 = CustomColors.getSkyColor(
            var24, this.mc.theWorld, this.mc.getRenderViewEntity().posX, this.mc.getRenderViewEntity().posY + 1.0, this.mc.getRenderViewEntity().posZ
         );
         if (var23) {
            Shaders.setSkyColor(var24);
         }

         float var8 = (float)var24.xCoord;
         float var9 = (float)var24.yCoord;
         float var10 = (float)var24.zCoord;
         if (var2 != 2) {
            float var11 = (var8 * 30.0F + var9 * 59.0F + var10 * 11.0F) / 100.0F;
            float var12 = (var8 * 30.0F + var9 * 70.0F) / 100.0F;
            float var13 = (var8 * 30.0F + var10 * 70.0F) / 100.0F;
            var8 = var11;
            var9 = var12;
            var10 = var13;
         }

         GlStateManager.color(var8, var9, var10);
         Tessellator var26 = Tessellator.getInstance();
         WorldRenderer var27 = var26.getWorldRenderer();
         GlStateManager.depthMask(false);
         GlStateManager.enableFog();
         if (var23) {
            Shaders.enableFog();
         }

         GlStateManager.color(var8, var9, var10);
         if (var23) {
            Shaders.preSkyList();
         }

         if (Config.K()) {
            if (this.vboEnabled) {
               this.skyVBO.bindBuffer();
               GL11.glEnableClientState(32884);
               GL11.glVertexPointer(3, 5126, 12, 0L);
               this.skyVBO.drawArrays(7);
               this.skyVBO.unbindBuffer();
               long var10000 = 8618625489232203640L ^ var3;
               GL11.glDisableClientState(32884);
            } else {
               GlStateManager.callList(this.glSkyList);
            }
         }

         GlStateManager.disableFog();
         if (var23) {
            Shaders.disableFog();
         }

         GlStateManager.disableAlpha();
         GlStateManager.enableBlend();
         long var46 = 7682518556400208474L ^ var3;
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
         RenderHelper.disableStandardItemLighting();
         float[] var28 = this.theWorld.provider.calcSunriseSunsetColors(this.theWorld.getCelestialAngle(var1), var1);
         if (var28 != null && Config.u()) {
            GlStateManager.disableTexture2D();
            if (var23) {
               Shaders.disableTexture2D();
            }

            GlStateManager.shadeModel(7425);
            GlStateManager.pushMatrix();
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(MathHelper.sin(this.theWorld.getCelestialAngleRadians(var1)) < 0.0F ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
            float var14 = var28[0];
            float var15 = var28[1];
            float var16 = var28[2];
            if (var2 != 2) {
               float var17 = (var14 * 30.0F + var15 * 59.0F + var16 * 11.0F) / 100.0F;
               float var18 = (var14 * 30.0F + var15 * 70.0F) / 100.0F;
               float var19 = (var14 * 30.0F + var16 * 70.0F) / 100.0F;
               var14 = var17;
               var15 = var18;
               var16 = var19;
            }

            var27.begin(6, DefaultVertexFormats.POSITION_COLOR);
            var27.pos(0.0, 100.0, 0.0).color(var14, var15, var16, var28[3]).endVertex();
            boolean var34 = true;

            for (int var37 = 0; var37 <= 16; var37++) {
               float var39 = var37 * (float) Math.PI * 2.0F / 16.0F;
               float var20 = MathHelper.sin(var39);
               float var21 = MathHelper.cos(var39);
               var27.pos(var20 * 120.0F, var21 * 120.0F, -var21 * 40.0F * var28[3]).color(var28[0], var28[1], var28[2], 0.0F).endVertex();
            }

            var26.draw();
            GlStateManager.popMatrix();
            GlStateManager.shadeModel(7424);
         }

         GlStateManager.enableTexture2D();
         if (var23) {
            Shaders.enableTexture2D();
         }

         GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0);
         GlStateManager.pushMatrix();
         float var29 = 1.0F - this.theWorld.getRainStrength(var1);
         GlStateManager.color(1.0F, 1.0F, 1.0F, var29);
         GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
         CustomSky.J(this.theWorld, this.renderEngine, this.theWorld.getCelestialAngle(var1), var29);
         if (var23) {
            Shaders.preCelestialRotate();
         }

         GlStateManager.rotate(this.theWorld.getCelestialAngle(var1) * 360.0F, 1.0F, 0.0F, 0.0F);
         if (var23) {
            Shaders.postCelestialRotate();
         }

         float var30 = 30.0F;
         if (Config.pR()) {
            this.renderEngine.bindTexture(locationSunPng);
            var27.begin(7, DefaultVertexFormats.POSITION_TEX);
            var27.pos(-var30, 100.0, -var30).tex(0.0, 0.0).endVertex();
            var27.pos(var30, 100.0, -var30).tex(1.0, 0.0).endVertex();
            var27.pos(var30, 100.0, var30).tex(1.0, 1.0).endVertex();
            var27.pos(-var30, 100.0, var30).tex(0.0, 1.0).endVertex();
            var26.draw();
         }

         var30 = 20.0F;
         if (Config.pM()) {
            this.renderEngine.bindTexture(locationMoonPhasesPng);
            int var32 = this.theWorld.f();
            int var35 = var32 % 4;
            int var38 = var32 / 4 % 2;
            float var40 = (var35 + 0) / 4.0F;
            float var42 = (var38 + 0) / 2.0F;
            float var44 = (var35 + 1) / 4.0F;
            float var22 = (var38 + 1) / 2.0F;
            var27.begin(7, DefaultVertexFormats.POSITION_TEX);
            var27.pos(-var30, -100.0, var30).tex(var44, var22).endVertex();
            var27.pos(var30, -100.0, var30).tex(var40, var22).endVertex();
            var27.pos(var30, -100.0, -var30).tex(var40, var42).endVertex();
            var27.pos(-var30, -100.0, -var30).tex(var44, var42).endVertex();
            var26.draw();
         }

         GlStateManager.disableTexture2D();
         if (var23) {
            Shaders.disableTexture2D();
         }

         float var33 = this.theWorld.getStarBrightness(var1) * var29;
         if (var33 > 0.0F && Config.s() && !CustomSky.hasSkyLayers(this.theWorld)) {
            GlStateManager.color(var33, var33, var33, var33);
            if (this.vboEnabled) {
               this.starVBO.bindBuffer();
               GL11.glEnableClientState(32884);
               GL11.glVertexPointer(3, 5126, 12, 0L);
               this.starVBO.drawArrays(7);
               this.starVBO.unbindBuffer();
               var46 = 8618625489232203640L ^ var3;
               GL11.glDisableClientState(32884);
            } else {
               GlStateManager.callList(this.starGLCallList);
            }
         }

         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.disableBlend();
         GlStateManager.enableAlpha();
         GlStateManager.enableFog();
         if (var23) {
            Shaders.enableFog();
         }

         GlStateManager.popMatrix();
         GlStateManager.disableTexture2D();
         if (var23) {
            Shaders.disableTexture2D();
         }

         GlStateManager.color(0.0F, 0.0F, 0.0F);
         double var36 = this.mc.thePlayer.getPositionEyes(var1).yCoord - this.theWorld.getHorizon();
         if (var36 < 0.0) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, 12.0F, 0.0F);
            if (this.vboEnabled) {
               this.sky2VBO.bindBuffer();
               GL11.glEnableClientState(32884);
               GL11.glVertexPointer(3, 5126, 12, 0L);
               this.sky2VBO.drawArrays(7);
               this.sky2VBO.unbindBuffer();
               var46 = 8618625489232203640L ^ var3;
               GL11.glDisableClientState(32884);
            } else {
               GlStateManager.callList(this.glSkyList2);
            }

            GlStateManager.popMatrix();
            float var41 = 1.0F;
            float var43 = -((float)(var36 + 65.0));
            float var45 = -1.0F;
            var27.begin(7, DefaultVertexFormats.POSITION_COLOR);
            var27.pos(-1.0, var43, 1.0).color(0, 0, 0, 255).endVertex();
            var27.pos(1.0, var43, 1.0).color(0, 0, 0, 255).endVertex();
            var27.pos(1.0, -1.0, 1.0).color(0, 0, 0, 255).endVertex();
            var27.pos(-1.0, -1.0, 1.0).color(0, 0, 0, 255).endVertex();
            var27.pos(-1.0, -1.0, -1.0).color(0, 0, 0, 255).endVertex();
            var27.pos(1.0, -1.0, -1.0).color(0, 0, 0, 255).endVertex();
            var27.pos(1.0, var43, -1.0).color(0, 0, 0, 255).endVertex();
            var27.pos(-1.0, var43, -1.0).color(0, 0, 0, 255).endVertex();
            var27.pos(1.0, -1.0, -1.0).color(0, 0, 0, 255).endVertex();
            var27.pos(1.0, -1.0, 1.0).color(0, 0, 0, 255).endVertex();
            var27.pos(1.0, var43, 1.0).color(0, 0, 0, 255).endVertex();
            var27.pos(1.0, var43, -1.0).color(0, 0, 0, 255).endVertex();
            var27.pos(-1.0, var43, -1.0).color(0, 0, 0, 255).endVertex();
            var27.pos(-1.0, var43, 1.0).color(0, 0, 0, 255).endVertex();
            var27.pos(-1.0, -1.0, 1.0).color(0, 0, 0, 255).endVertex();
            var27.pos(-1.0, -1.0, -1.0).color(0, 0, 0, 255).endVertex();
            var27.pos(-1.0, -1.0, -1.0).color(0, 0, 0, 255).endVertex();
            var27.pos(-1.0, -1.0, 1.0).color(0, 0, 0, 255).endVertex();
            var27.pos(1.0, -1.0, 1.0).color(0, 0, 0, 255).endVertex();
            var27.pos(1.0, -1.0, -1.0).color(0, 0, 0, 255).endVertex();
            var26.draw();
         }

         if (this.theWorld.provider.isSkyColored()) {
            GlStateManager.color(var8 * 0.2F + 0.04F, var9 * 0.2F + 0.04F, var10 * 0.6F + 0.1F);
         } else {
            GlStateManager.color(var8, var9, var10);
         }

         if (this.mc.gameSettings.renderDistanceChunks <= 4) {
            GlStateManager.color(this.mc.entityRenderer.ZQ, this.mc.entityRenderer.N, this.mc.entityRenderer.ZI);
         }

         GlStateManager.pushMatrix();
         GlStateManager.translate(0.0F, -((float)(var36 - 16.0)), 0.0F);
         if (Config.K()) {
            GlStateManager.callList(this.glSkyList2);
         }

         GlStateManager.popMatrix();
         GlStateManager.enableTexture2D();
         if (var23) {
            Shaders.enableTexture2D();
         }

         GlStateManager.depthMask(true);
      }
   }

   public void renderClouds(float var1, int var2) {
      long var3 = ab ^ 127515741575820L;
      if (!Config.isCloudsOff()) {
         if (Reflector.ForgeWorldProvider_getCloudRenderer.exists()) {
            WorldProvider var6 = this.mc.theWorld.provider;
            Object var7 = Reflector.call(var6, Reflector.ForgeWorldProvider_getCloudRenderer);
            if (var7 != null) {
               Reflector.callVoid(var7, Reflector.IRenderHandler_render, var1, this.theWorld, this.mc);
               return;
            }
         }

         if (this.mc.theWorld.provider.isSurfaceWorld()) {
            if (Config.isShaders()) {
               Shaders.beginClouds();
            }

            if (Config.c()) {
               this.renderCloudsFancy(var1, var2);
            } else {
               this.cloudRenderer.u(false, this.cloudTickCounter, var1);
               var1 = 0.0F;
               GlStateManager.disableCull();
               float var30 = (float)(
                  this.mc.getRenderViewEntity().lastTickPosY + (this.mc.getRenderViewEntity().posY - this.mc.getRenderViewEntity().lastTickPosY) * var1
               );
               boolean var31 = true;
               Tessellator var9 = Tessellator.getInstance();
               WorldRenderer var10 = var9.getWorldRenderer();
               this.renderEngine.bindTexture(locationCloudsPng);
               GlStateManager.enableBlend();
               long var10000 = 7682533245060109346L ^ var3;
               GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
               if (this.cloudRenderer.shouldUpdateGlList()) {
                  this.cloudRenderer.startUpdateGlList();
                  Vec3 var11 = this.theWorld.getCloudColour(var1);
                  float var12 = (float)var11.xCoord;
                  float var13 = (float)var11.yCoord;
                  float var14 = (float)var11.zCoord;
                  if (var2 != 2) {
                     float var15 = (var12 * 30.0F + var13 * 59.0F + var14 * 11.0F) / 100.0F;
                     float var16 = (var12 * 30.0F + var13 * 70.0F) / 100.0F;
                     float var17 = (var12 * 30.0F + var14 * 70.0F) / 100.0F;
                     var12 = var15;
                     var13 = var16;
                     var14 = var17;
                  }

                  float var32 = 4.8828125E-4F;
                  double var33 = this.cloudTickCounter + var1;
                  double var18 = this.mc.getRenderViewEntity().prevPosX
                     + (this.mc.getRenderViewEntity().posX - this.mc.getRenderViewEntity().prevPosX) * var1
                     + var33 * 0.03F;
                  double var20 = this.mc.getRenderViewEntity().prevPosZ + (this.mc.getRenderViewEntity().posZ - this.mc.getRenderViewEntity().prevPosZ) * var1;
                  int var22 = MathHelper.floor_double(var18 / 2048.0);
                  int var23 = MathHelper.floor_double(var20 / 2048.0);
                  var18 -= var22 * 2048;
                  var20 -= var23 * 2048;
                  float var24 = this.theWorld.provider.getCloudHeight() - var30 + 0.33F;
                  var24 += this.mc.gameSettings.ofCloudsHeight * 128.0F;
                  float var25 = (float)(var18 * 4.8828125E-4);
                  float var26 = (float)(var20 * 4.8828125E-4);
                  var10.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);

                  for (int var27 = -256; var27 < 256; var27 += 32) {
                     for (int var28 = -256; var28 < 256; var28 += 32) {
                        var10.pos(var27 + 0, var24, var28 + 32)
                           .tex((var27 + 0) * 4.8828125E-4F + var25, (var28 + 32) * 4.8828125E-4F + var26)
                           .color(var12, var13, var14, 0.8F)
                           .endVertex();
                        var10.pos(var27 + 32, var24, var28 + 32)
                           .tex((var27 + 32) * 4.8828125E-4F + var25, (var28 + 32) * 4.8828125E-4F + var26)
                           .color(var12, var13, var14, 0.8F)
                           .endVertex();
                        var10.pos(var27 + 32, var24, var28 + 0)
                           .tex((var27 + 32) * 4.8828125E-4F + var25, (var28 + 0) * 4.8828125E-4F + var26)
                           .color(var12, var13, var14, 0.8F)
                           .endVertex();
                        var10.pos(var27 + 0, var24, var28 + 0)
                           .tex((var27 + 0) * 4.8828125E-4F + var25, (var28 + 0) * 4.8828125E-4F + var26)
                           .color(var12, var13, var14, 0.8F)
                           .endVertex();
                     }
                  }

                  var9.draw();
                  this.cloudRenderer.endUpdateGlList();
               }

               this.cloudRenderer.renderGlList();
               GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
               GlStateManager.disableBlend();
               GlStateManager.enableCull();
            }

            if (Config.isShaders()) {
               Shaders.Z();
            }
         }
      }
   }

   public boolean hasCloudFog(double var1, double var3, double var5, float var7) {
      return false;
   }

   private void renderCloudsFancy(float var1, int var2) {
      this.cloudRenderer.u(true, this.cloudTickCounter, var1);
      var1 = 0.0F;
      GlStateManager.disableCull();
      float var6 = (float)(
         this.mc.getRenderViewEntity().lastTickPosY + (this.mc.getRenderViewEntity().posY - this.mc.getRenderViewEntity().lastTickPosY) * var1
      );
      Tessellator var7 = Tessellator.getInstance();
      WorldRenderer var8 = var7.getWorldRenderer();
      double var11 = this.cloudTickCounter + var1;
      double var13 = (
            this.mc.getRenderViewEntity().prevPosX + (this.mc.getRenderViewEntity().posX - this.mc.getRenderViewEntity().prevPosX) * var1 + var11 * 0.03F
         )
         / 12.0;
      double var15 = (this.mc.getRenderViewEntity().prevPosZ + (this.mc.getRenderViewEntity().posZ - this.mc.getRenderViewEntity().prevPosZ) * var1) / 12.0
         + 0.33F;
      float var17 = this.theWorld.provider.getCloudHeight() - var6 + 0.33F;
      var17 += this.mc.gameSettings.ofCloudsHeight * 128.0F;
      int var18 = MathHelper.floor_double(var13 / 2048.0);
      int var19 = MathHelper.floor_double(var15 / 2048.0);
      var13 -= var18 * 2048;
      var15 -= var19 * 2048;
      this.renderEngine.bindTexture(locationCloudsPng);
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      Vec3 var20 = this.theWorld.getCloudColour(var1);
      float var21 = (float)var20.xCoord;
      float var22 = (float)var20.yCoord;
      float var23 = (float)var20.zCoord;
      if (var2 != 2) {
         float var24 = (var21 * 30.0F + var22 * 59.0F + var23 * 11.0F) / 100.0F;
         float var25 = (var21 * 30.0F + var22 * 70.0F) / 100.0F;
         float var26 = (var21 * 30.0F + var23 * 70.0F) / 100.0F;
         var21 = var24;
         var22 = var25;
         var23 = var26;
      }

      float var52 = var21 * 0.9F;
      float var53 = var22 * 0.9F;
      float var54 = var23 * 0.9F;
      float var27 = var21 * 0.7F;
      float var28 = var22 * 0.7F;
      float var29 = var23 * 0.7F;
      float var30 = var21 * 0.8F;
      float var31 = var22 * 0.8F;
      float var32 = var23 * 0.8F;
      float var34 = MathHelper.floor_double(var13) * 0.00390625F;
      float var35 = MathHelper.floor_double(var15) * 0.00390625F;
      float var36 = (float)(var13 - MathHelper.floor_double(var13));
      float var37 = (float)(var15 - MathHelper.floor_double(var15));
      GlStateManager.scale(12.0F, 1.0F, 12.0F);

      for (int var41 = 0; var41 < 2; var41++) {
         if (var41 == 0) {
            GlStateManager.colorMask(false, false, false, false);
         } else {
            switch (var2) {
               case 0:
                  GlStateManager.colorMask(false, true, true, true);
                  break;
               case 1:
                  GlStateManager.colorMask(true, false, false, true);
                  break;
               case 2:
                  GlStateManager.colorMask(true, true, true, true);
            }
         }

         this.cloudRenderer.renderGlList();
      }

      if (this.cloudRenderer.shouldUpdateGlList()) {
         this.cloudRenderer.startUpdateGlList();

         for (int var55 = -3; var55 <= 4; var55++) {
            for (int var42 = -3; var42 <= 4; var42++) {
               var8.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
               float var43 = var55 * 8;
               float var44 = var42 * 8;
               float var45 = var43 - var36;
               float var46 = var44 - var37;
               if (var17 > -5.0F) {
                  var8.pos(var45 + 0.0F, var17 + 0.0F, var46 + 8.0F)
                     .tex((var43 + 0.0F) * 0.00390625F + var34, (var44 + 8.0F) * 0.00390625F + var35)
                     .color(var27, var28, var29, 0.8F)
                     .normal(0.0F, -1.0F, 0.0F)
                     .endVertex();
                  var8.pos(var45 + 8.0F, var17 + 0.0F, var46 + 8.0F)
                     .tex((var43 + 8.0F) * 0.00390625F + var34, (var44 + 8.0F) * 0.00390625F + var35)
                     .color(var27, var28, var29, 0.8F)
                     .normal(0.0F, -1.0F, 0.0F)
                     .endVertex();
                  var8.pos(var45 + 8.0F, var17 + 0.0F, var46 + 0.0F)
                     .tex((var43 + 8.0F) * 0.00390625F + var34, (var44 + 0.0F) * 0.00390625F + var35)
                     .color(var27, var28, var29, 0.8F)
                     .normal(0.0F, -1.0F, 0.0F)
                     .endVertex();
                  var8.pos(var45 + 0.0F, var17 + 0.0F, var46 + 0.0F)
                     .tex((var43 + 0.0F) * 0.00390625F + var34, (var44 + 0.0F) * 0.00390625F + var35)
                     .color(var27, var28, var29, 0.8F)
                     .normal(0.0F, -1.0F, 0.0F)
                     .endVertex();
               }

               if (var17 <= 5.0F) {
                  var8.pos(var45 + 0.0F, var17 + 4.0F - 9.765625E-4F, var46 + 8.0F)
                     .tex((var43 + 0.0F) * 0.00390625F + var34, (var44 + 8.0F) * 0.00390625F + var35)
                     .color(var21, var22, var23, 0.8F)
                     .normal(0.0F, 1.0F, 0.0F)
                     .endVertex();
                  var8.pos(var45 + 8.0F, var17 + 4.0F - 9.765625E-4F, var46 + 8.0F)
                     .tex((var43 + 8.0F) * 0.00390625F + var34, (var44 + 8.0F) * 0.00390625F + var35)
                     .color(var21, var22, var23, 0.8F)
                     .normal(0.0F, 1.0F, 0.0F)
                     .endVertex();
                  var8.pos(var45 + 8.0F, var17 + 4.0F - 9.765625E-4F, var46 + 0.0F)
                     .tex((var43 + 8.0F) * 0.00390625F + var34, (var44 + 0.0F) * 0.00390625F + var35)
                     .color(var21, var22, var23, 0.8F)
                     .normal(0.0F, 1.0F, 0.0F)
                     .endVertex();
                  var8.pos(var45 + 0.0F, var17 + 4.0F - 9.765625E-4F, var46 + 0.0F)
                     .tex((var43 + 0.0F) * 0.00390625F + var34, (var44 + 0.0F) * 0.00390625F + var35)
                     .color(var21, var22, var23, 0.8F)
                     .normal(0.0F, 1.0F, 0.0F)
                     .endVertex();
               }

               if (var55 > -1) {
                  for (int var47 = 0; var47 < 8; var47++) {
                     var8.pos(var45 + var47 + 0.0F, var17 + 0.0F, var46 + 8.0F)
                        .tex((var43 + var47 + 0.5F) * 0.00390625F + var34, (var44 + 8.0F) * 0.00390625F + var35)
                        .color(var52, var53, var54, 0.8F)
                        .normal(-1.0F, 0.0F, 0.0F)
                        .endVertex();
                     var8.pos(var45 + var47 + 0.0F, var17 + 4.0F, var46 + 8.0F)
                        .tex((var43 + var47 + 0.5F) * 0.00390625F + var34, (var44 + 8.0F) * 0.00390625F + var35)
                        .color(var52, var53, var54, 0.8F)
                        .normal(-1.0F, 0.0F, 0.0F)
                        .endVertex();
                     var8.pos(var45 + var47 + 0.0F, var17 + 4.0F, var46 + 0.0F)
                        .tex((var43 + var47 + 0.5F) * 0.00390625F + var34, (var44 + 0.0F) * 0.00390625F + var35)
                        .color(var52, var53, var54, 0.8F)
                        .normal(-1.0F, 0.0F, 0.0F)
                        .endVertex();
                     var8.pos(var45 + var47 + 0.0F, var17 + 0.0F, var46 + 0.0F)
                        .tex((var43 + var47 + 0.5F) * 0.00390625F + var34, (var44 + 0.0F) * 0.00390625F + var35)
                        .color(var52, var53, var54, 0.8F)
                        .normal(-1.0F, 0.0F, 0.0F)
                        .endVertex();
                  }
               }

               if (var55 <= 1) {
                  for (int var56 = 0; var56 < 8; var56++) {
                     var8.pos(var45 + var56 + 1.0F - 9.765625E-4F, var17 + 0.0F, var46 + 8.0F)
                        .tex((var43 + var56 + 0.5F) * 0.00390625F + var34, (var44 + 8.0F) * 0.00390625F + var35)
                        .color(var52, var53, var54, 0.8F)
                        .normal(1.0F, 0.0F, 0.0F)
                        .endVertex();
                     var8.pos(var45 + var56 + 1.0F - 9.765625E-4F, var17 + 4.0F, var46 + 8.0F)
                        .tex((var43 + var56 + 0.5F) * 0.00390625F + var34, (var44 + 8.0F) * 0.00390625F + var35)
                        .color(var52, var53, var54, 0.8F)
                        .normal(1.0F, 0.0F, 0.0F)
                        .endVertex();
                     var8.pos(var45 + var56 + 1.0F - 9.765625E-4F, var17 + 4.0F, var46 + 0.0F)
                        .tex((var43 + var56 + 0.5F) * 0.00390625F + var34, (var44 + 0.0F) * 0.00390625F + var35)
                        .color(var52, var53, var54, 0.8F)
                        .normal(1.0F, 0.0F, 0.0F)
                        .endVertex();
                     var8.pos(var45 + var56 + 1.0F - 9.765625E-4F, var17 + 0.0F, var46 + 0.0F)
                        .tex((var43 + var56 + 0.5F) * 0.00390625F + var34, (var44 + 0.0F) * 0.00390625F + var35)
                        .color(var52, var53, var54, 0.8F)
                        .normal(1.0F, 0.0F, 0.0F)
                        .endVertex();
                  }
               }

               if (var42 > -1) {
                  for (int var57 = 0; var57 < 8; var57++) {
                     var8.pos(var45 + 0.0F, var17 + 4.0F, var46 + var57 + 0.0F)
                        .tex((var43 + 0.0F) * 0.00390625F + var34, (var44 + var57 + 0.5F) * 0.00390625F + var35)
                        .color(var30, var31, var32, 0.8F)
                        .normal(0.0F, 0.0F, -1.0F)
                        .endVertex();
                     var8.pos(var45 + 8.0F, var17 + 4.0F, var46 + var57 + 0.0F)
                        .tex((var43 + 8.0F) * 0.00390625F + var34, (var44 + var57 + 0.5F) * 0.00390625F + var35)
                        .color(var30, var31, var32, 0.8F)
                        .normal(0.0F, 0.0F, -1.0F)
                        .endVertex();
                     var8.pos(var45 + 8.0F, var17 + 0.0F, var46 + var57 + 0.0F)
                        .tex((var43 + 8.0F) * 0.00390625F + var34, (var44 + var57 + 0.5F) * 0.00390625F + var35)
                        .color(var30, var31, var32, 0.8F)
                        .normal(0.0F, 0.0F, -1.0F)
                        .endVertex();
                     var8.pos(var45 + 0.0F, var17 + 0.0F, var46 + var57 + 0.0F)
                        .tex((var43 + 0.0F) * 0.00390625F + var34, (var44 + var57 + 0.5F) * 0.00390625F + var35)
                        .color(var30, var31, var32, 0.8F)
                        .normal(0.0F, 0.0F, -1.0F)
                        .endVertex();
                  }
               }

               if (var42 <= 1) {
                  for (int var58 = 0; var58 < 8; var58++) {
                     var8.pos(var45 + 0.0F, var17 + 4.0F, var46 + var58 + 1.0F - 9.765625E-4F)
                        .tex((var43 + 0.0F) * 0.00390625F + var34, (var44 + var58 + 0.5F) * 0.00390625F + var35)
                        .color(var30, var31, var32, 0.8F)
                        .normal(0.0F, 0.0F, 1.0F)
                        .endVertex();
                     var8.pos(var45 + 8.0F, var17 + 4.0F, var46 + var58 + 1.0F - 9.765625E-4F)
                        .tex((var43 + 8.0F) * 0.00390625F + var34, (var44 + var58 + 0.5F) * 0.00390625F + var35)
                        .color(var30, var31, var32, 0.8F)
                        .normal(0.0F, 0.0F, 1.0F)
                        .endVertex();
                     var8.pos(var45 + 8.0F, var17 + 0.0F, var46 + var58 + 1.0F - 9.765625E-4F)
                        .tex((var43 + 8.0F) * 0.00390625F + var34, (var44 + var58 + 0.5F) * 0.00390625F + var35)
                        .color(var30, var31, var32, 0.8F)
                        .normal(0.0F, 0.0F, 1.0F)
                        .endVertex();
                     var8.pos(var45 + 0.0F, var17 + 0.0F, var46 + var58 + 1.0F - 9.765625E-4F)
                        .tex((var43 + 0.0F) * 0.00390625F + var34, (var44 + var58 + 0.5F) * 0.00390625F + var35)
                        .color(var30, var31, var32, 0.8F)
                        .normal(0.0F, 0.0F, 1.0F)
                        .endVertex();
                  }
               }

               var7.draw();
            }
         }

         this.cloudRenderer.endUpdateGlList();
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableBlend();
      GlStateManager.enableCull();
   }

   public void updateChunks(long var1) {
      var1 = (long)(var1 + 1.0E8);
      this.displayListEntitiesDirty = this.displayListEntitiesDirty | this.renderDispatcher.runChunkUploads(var1);
      if (this.chunksToUpdateForced.size() > 0) {
         Iterator var6 = this.chunksToUpdateForced.iterator();

         while (var6.hasNext()) {
            RenderChunk var7 = (RenderChunk)var6.next();
            if (!this.renderDispatcher.N(var7)) {
               break;
            }

            var7.setNeedsUpdate(false);
            var6.remove();
            this.chunksToUpdate.remove(var7);
            this.chunksToResortTransparency.remove(var7);
         }
      }

      if (this.chunksToResortTransparency.size() > 0) {
         Iterator var12 = this.chunksToResortTransparency.iterator();
         if (var12.hasNext()) {
            RenderChunk var14 = (RenderChunk)var12.next();
            if (this.renderDispatcher.p(var14)) {
               var12.remove();
            }
         }
      }

      int var13 = 0;
      int var15 = Config.B();
      int var8 = var15 * 2;
      Iterator var9 = this.chunksToUpdate.iterator();

      while (var9.hasNext()) {
         RenderChunk var10 = (RenderChunk)var9.next();
         if (!this.renderDispatcher.N(var10)) {
            break;
         }

         var10.setNeedsUpdate(false);
         var9.remove();
         if (var10.getCompiledChunk().isEmpty() && var15 < var8) {
            var15++;
         }

         if (++var13 >= var15) {
            break;
         }
      }
   }

   public void renderWorldBorder(Entity var1, float var2) {
      long var3 = ab ^ 40372035067889L;
      Tessellator var6 = Tessellator.getInstance();
      WorldRenderer var7 = var6.getWorldRenderer();
      WorldBorder var8 = this.theWorld.getWorldBorder();
      double var9 = this.mc.gameSettings.renderDistanceChunks * 16;
      if (var1.posX >= var8.maxX() - var9 || var1.posX <= var8.minX() + var9 || var1.posZ >= var8.maxZ() - var9 || var1.posZ <= var8.minZ() + var9) {
         double var11 = 1.0 - var8.getClosestDistance(var1) / var9;
         var11 = Math.pow(var11, 4.0);
         long var10000 = 6290795213829567164L ^ var3;
         double var13 = var1.lastTickPosX + (var1.posX - var1.lastTickPosX) * var2;
         double var15 = var1.lastTickPosY + (var1.posY - var1.lastTickPosY) * var2;
         double var17 = var1.lastTickPosZ + (var1.posZ - var1.lastTickPosZ) * var2;
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0);
         this.renderEngine.bindTexture(locationForcefieldPng);
         GlStateManager.depthMask(false);
         GlStateManager.pushMatrix();
         int var19 = var8.getStatus().getID();
         float var20 = (var19 >> 16 & 0xFF) / 255.0F;
         float var21 = (var19 >> 8 & 0xFF) / 255.0F;
         float var22 = (var19 & 0xFF) / 255.0F;
         GlStateManager.color(var20, var21, var22, (float)var11);
         var10000 = 8480944119209075206L ^ var3;
         GlStateManager.doPolygonOffset(-3.0F, -3.0F);
         GlStateManager.enablePolygonOffset();
         GlStateManager.alphaFunc(516, 0.1F);
         GlStateManager.enableAlpha();
         GlStateManager.disableCull();
         float var23 = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F;
         var7.begin(7, DefaultVertexFormats.POSITION_TEX);
         var7.setTranslation(-var13, -var15, -var17);
         double var27 = Math.max(MathHelper.floor_double(var17 - var9), var8.minZ());
         double var29 = Math.min(MathHelper.ceiling_double_int(var17 + var9), var8.maxZ());
         if (var13 > var8.maxX() - var9) {
            float var31 = 0.0F;

            for (double var32 = var27; var32 < var29; var31 += 0.5F) {
               double var34 = Math.min(1.0, var29 - var32);
               float var36 = (float)var34 * 0.5F;
               var7.pos(var8.maxX(), 256.0, var32).tex(var23 + var31, var23 + 0.0F).endVertex();
               var7.pos(var8.maxX(), 256.0, var32 + var34).tex(var23 + var36 + var31, var23 + 0.0F).endVertex();
               var7.pos(var8.maxX(), 0.0, var32 + var34).tex(var23 + var36 + var31, var23 + 128.0F).endVertex();
               var7.pos(var8.maxX(), 0.0, var32).tex(var23 + var31, var23 + 128.0F).endVertex();
               var32++;
            }
         }

         if (var13 < var8.minX() + var9) {
            float var40 = 0.0F;

            for (double var43 = var27; var43 < var29; var40 += 0.5F) {
               double var46 = Math.min(1.0, var29 - var43);
               float var49 = (float)var46 * 0.5F;
               var7.pos(var8.minX(), 256.0, var43).tex(var23 + var40, var23 + 0.0F).endVertex();
               var7.pos(var8.minX(), 256.0, var43 + var46).tex(var23 + var49 + var40, var23 + 0.0F).endVertex();
               var7.pos(var8.minX(), 0.0, var43 + var46).tex(var23 + var49 + var40, var23 + 128.0F).endVertex();
               var7.pos(var8.minX(), 0.0, var43).tex(var23 + var40, var23 + 128.0F).endVertex();
               var43++;
            }
         }

         var27 = Math.max(MathHelper.floor_double(var13 - var9), var8.minX());
         var29 = Math.min(MathHelper.ceiling_double_int(var13 + var9), var8.maxX());
         if (var17 > var8.maxZ() - var9) {
            float var41 = 0.0F;

            for (double var44 = var27; var44 < var29; var41 += 0.5F) {
               double var47 = Math.min(1.0, var29 - var44);
               float var50 = (float)var47 * 0.5F;
               var7.pos(var44, 256.0, var8.maxZ()).tex(var23 + var41, var23 + 0.0F).endVertex();
               var7.pos(var44 + var47, 256.0, var8.maxZ()).tex(var23 + var50 + var41, var23 + 0.0F).endVertex();
               var7.pos(var44 + var47, 0.0, var8.maxZ()).tex(var23 + var50 + var41, var23 + 128.0F).endVertex();
               var7.pos(var44, 0.0, var8.maxZ()).tex(var23 + var41, var23 + 128.0F).endVertex();
               var44++;
            }
         }

         if (var17 < var8.minZ() + var9) {
            float var42 = 0.0F;

            for (double var45 = var27; var45 < var29; var42 += 0.5F) {
               double var48 = Math.min(1.0, var29 - var45);
               float var51 = (float)var48 * 0.5F;
               var7.pos(var45, 256.0, var8.minZ()).tex(var23 + var42, var23 + 0.0F).endVertex();
               var7.pos(var45 + var48, 256.0, var8.minZ()).tex(var23 + var51 + var42, var23 + 0.0F).endVertex();
               var7.pos(var45 + var48, 0.0, var8.minZ()).tex(var23 + var51 + var42, var23 + 128.0F).endVertex();
               var7.pos(var45, 0.0, var8.minZ()).tex(var23 + var42, var23 + 128.0F).endVertex();
               var45++;
            }
         }

         var6.draw();
         var7.setTranslation(0.0, 0.0, 0.0);
         GlStateManager.enableCull();
         GlStateManager.disableAlpha();
         GlStateManager.doPolygonOffset(0.0F, 0.0F);
         GlStateManager.disablePolygonOffset();
         GlStateManager.enableAlpha();
         GlStateManager.disableBlend();
         GlStateManager.popMatrix();
         GlStateManager.depthMask(true);
      }
   }

   private void preRenderDamagedBlocks() {
      GlStateManager.tryBlendFuncSeparate(774, 768, 1, 0);
      GlStateManager.enableBlend();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F);
      GlStateManager.doPolygonOffset(-3.0F, -3.0F);
      GlStateManager.enablePolygonOffset();
      GlStateManager.alphaFunc(516, 0.1F);
      GlStateManager.enableAlpha();
      GlStateManager.pushMatrix();
      if (Config.isShaders()) {
         ShadersRender.beginBlockDamage();
      }
   }

   private void postRenderDamagedBlocks() {
      GlStateManager.disableAlpha();
      GlStateManager.doPolygonOffset(0.0F, 0.0F);
      GlStateManager.disablePolygonOffset();
      GlStateManager.enableAlpha();
      GlStateManager.depthMask(true);
      GlStateManager.popMatrix();
      if (Config.isShaders()) {
         ShadersRender.G();
      }
   }

   public void drawBlockDamageTexture(Tessellator var1, WorldRenderer var2, Entity var3, float var4) {
      double var8 = var3.lastTickPosX + (var3.posX - var3.lastTickPosX) * var4;
      double var10 = var3.lastTickPosY + (var3.posY - var3.lastTickPosY) * var4;
      double var12 = var3.lastTickPosZ + (var3.posZ - var3.lastTickPosZ) * var4;
      if (!this.damagedBlocks.isEmpty()) {
         this.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
         this.preRenderDamagedBlocks();
         var2.begin(7, DefaultVertexFormats.BLOCK);
         var2.setTranslation(-var8, -var10, -var12);
         var2.noColor();
         Iterator var14 = this.damagedBlocks.values().iterator();

         while (var14.hasNext()) {
            DestroyBlockProgress var15 = (DestroyBlockProgress)var14.next();
            BlockPos var16 = var15.getPosition();
            double var17 = var16.getX() - var8;
            double var19 = var16.getY() - var10;
            double var21 = var16.getZ() - var12;
            Block var23 = this.theWorld.getBlockState(var16).getBlock();
            boolean var24;
            if (Reflector.ForgeTileEntity_canRenderBreaking.exists()) {
               boolean var25 = var23 instanceof BlockChest || var23 instanceof BlockEnderChest || var23 instanceof BlockSign || var23 instanceof BlockSkull;
               if (!var25) {
                  TileEntity var26 = this.theWorld.getTileEntity(var16);
                  if (var26 != null) {
                     var25 = Reflector.callBoolean(var26, Reflector.ForgeTileEntity_canRenderBreaking);
                  }
               }

               var24 = !var25;
            } else {
               var24 = !(var23 instanceof BlockChest) && !(var23 instanceof BlockEnderChest) && !(var23 instanceof BlockSign) && !(var23 instanceof BlockSkull);
            }

            if (var24) {
               if (var17 * var17 + var19 * var19 + var21 * var21 > 1024.0) {
                  var14.remove();
               } else {
                  IBlockState var29 = this.theWorld.getBlockState(var16);
                  if (var29.getBlock().getMaterial() != Material.air) {
                     int var30 = var15.getPartialBlockDamage();
                     TextureAtlasSprite var27 = this.destroyBlockIcons[var30];
                     BlockRendererDispatcher var28 = this.mc.getBlockRendererDispatcher();
                     var28.renderBlockDamage(var29, var16, var27, this.theWorld);
                  }
               }
            }
         }

         var1.draw();
         var2.setTranslation(0.0, 0.0, 0.0);
         this.postRenderDamagedBlocks();
      }
   }

   public void drawSelectionBox(EntityPlayer var1, MovingObjectPosition var2, int var3, float var4) {
      if (var3 == 0 && var2.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
         GlStateManager.color(0.0F, 0.0F, 0.0F, 0.4F);
         GL11.glLineWidth(2.0F);
         GlStateManager.disableTexture2D();
         if (Config.isShaders()) {
            Shaders.disableTexture2D();
         }

         GlStateManager.depthMask(false);
         BlockPos var9 = var2.getBlockPos();
         Block var10 = this.theWorld.getBlockState(var9).getBlock();
         if (var10.getMaterial() != Material.air && this.theWorld.getWorldBorder().contains(var9)) {
            var10.setBlockBoundsBasedOnState(this.theWorld, var9);
            double var11 = var1.lastTickPosX + (var1.posX - var1.lastTickPosX) * var4;
            double var13 = var1.lastTickPosY + (var1.posY - var1.lastTickPosY) * var4;
            double var15 = var1.lastTickPosZ + (var1.posZ - var1.lastTickPosZ) * var4;
            drawSelectionBoundingBox(var10.getSelectedBoundingBox(this.theWorld, var9).expand(0.002F, 0.002F, 0.002F).offset(-var11, -var13, -var15));
         }

         GlStateManager.depthMask(true);
         GlStateManager.enableTexture2D();
         if (Config.isShaders()) {
            Shaders.enableTexture2D();
         }

         GlStateManager.disableBlend();
      }
   }

   public static void drawSelectionBoundingBox(AxisAlignedBB var0) {
      Tessellator var1 = Tessellator.getInstance();
      WorldRenderer var2 = var1.getWorldRenderer();
      var2.begin(3, DefaultVertexFormats.POSITION);
      var2.pos(var0.minX, var0.minY, var0.minZ).endVertex();
      var2.pos(var0.maxX, var0.minY, var0.minZ).endVertex();
      var2.pos(var0.maxX, var0.minY, var0.maxZ).endVertex();
      var2.pos(var0.minX, var0.minY, var0.maxZ).endVertex();
      var2.pos(var0.minX, var0.minY, var0.minZ).endVertex();
      var1.draw();
      var2.begin(3, DefaultVertexFormats.POSITION);
      var2.pos(var0.minX, var0.maxY, var0.minZ).endVertex();
      var2.pos(var0.maxX, var0.maxY, var0.minZ).endVertex();
      var2.pos(var0.maxX, var0.maxY, var0.maxZ).endVertex();
      var2.pos(var0.minX, var0.maxY, var0.maxZ).endVertex();
      var2.pos(var0.minX, var0.maxY, var0.minZ).endVertex();
      var1.draw();
      var2.begin(1, DefaultVertexFormats.POSITION);
      var2.pos(var0.minX, var0.minY, var0.minZ).endVertex();
      var2.pos(var0.minX, var0.maxY, var0.minZ).endVertex();
      var2.pos(var0.maxX, var0.minY, var0.minZ).endVertex();
      var2.pos(var0.maxX, var0.maxY, var0.minZ).endVertex();
      var2.pos(var0.maxX, var0.minY, var0.maxZ).endVertex();
      var2.pos(var0.maxX, var0.maxY, var0.maxZ).endVertex();
      var2.pos(var0.minX, var0.minY, var0.maxZ).endVertex();
      var2.pos(var0.minX, var0.maxY, var0.maxZ).endVertex();
      var1.draw();
   }

   public static void drawOutlinedBoundingBox(AxisAlignedBB var0, int var1, int var2, int var3, int var4) {
      Tessellator var5 = Tessellator.getInstance();
      WorldRenderer var6 = var5.getWorldRenderer();
      var6.begin(3, DefaultVertexFormats.POSITION_COLOR);
      var6.pos(var0.minX, var0.minY, var0.minZ).color(var1, var2, var3, var4).endVertex();
      var6.pos(var0.maxX, var0.minY, var0.minZ).color(var1, var2, var3, var4).endVertex();
      var6.pos(var0.maxX, var0.minY, var0.maxZ).color(var1, var2, var3, var4).endVertex();
      var6.pos(var0.minX, var0.minY, var0.maxZ).color(var1, var2, var3, var4).endVertex();
      var6.pos(var0.minX, var0.minY, var0.minZ).color(var1, var2, var3, var4).endVertex();
      var5.draw();
      var6.begin(3, DefaultVertexFormats.POSITION_COLOR);
      var6.pos(var0.minX, var0.maxY, var0.minZ).color(var1, var2, var3, var4).endVertex();
      var6.pos(var0.maxX, var0.maxY, var0.minZ).color(var1, var2, var3, var4).endVertex();
      var6.pos(var0.maxX, var0.maxY, var0.maxZ).color(var1, var2, var3, var4).endVertex();
      var6.pos(var0.minX, var0.maxY, var0.maxZ).color(var1, var2, var3, var4).endVertex();
      var6.pos(var0.minX, var0.maxY, var0.minZ).color(var1, var2, var3, var4).endVertex();
      var5.draw();
      var6.begin(1, DefaultVertexFormats.POSITION_COLOR);
      var6.pos(var0.minX, var0.minY, var0.minZ).color(var1, var2, var3, var4).endVertex();
      var6.pos(var0.minX, var0.maxY, var0.minZ).color(var1, var2, var3, var4).endVertex();
      var6.pos(var0.maxX, var0.minY, var0.minZ).color(var1, var2, var3, var4).endVertex();
      var6.pos(var0.maxX, var0.maxY, var0.minZ).color(var1, var2, var3, var4).endVertex();
      var6.pos(var0.maxX, var0.minY, var0.maxZ).color(var1, var2, var3, var4).endVertex();
      var6.pos(var0.maxX, var0.maxY, var0.maxZ).color(var1, var2, var3, var4).endVertex();
      var6.pos(var0.minX, var0.minY, var0.maxZ).color(var1, var2, var3, var4).endVertex();
      var6.pos(var0.minX, var0.maxY, var0.maxZ).color(var1, var2, var3, var4).endVertex();
      var5.draw();
   }

   private void u(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.viewFrustum.markBlocksForUpdate(var1, var2, var3, var4, var5, var6);
   }

   @Override
   public void markBlockForUpdate(BlockPos var1) {
      int var2 = var1.getX();
      int var3 = var1.getY();
      int var4 = var1.getZ();
      this.u(var2 - 1, var3 - 1, var4 - 1, var2 + 1, var3 + 1, var4 + 1);
   }

   @Override
   public void notifyLightSet(BlockPos var1) {
      int var2 = var1.getX();
      int var3 = var1.getY();
      int var4 = var1.getZ();
      this.u(var2 - 1, var3 - 1, var4 - 1, var2 + 1, var3 + 1, var4 + 1);
   }

   @Override
   public void markBlockRangeForRenderUpdate(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.u(var1 - 1, var2 - 1, var3 - 1, var4 + 1, var5 + 1, var6 + 1);
   }

   @Override
   public void playRecord(String var1, BlockPos var2) {
      ISound var6 = this.mapSoundPositions.get(var2);
      if (var6 != null) {
         this.mc.getSoundHandler().stopSound(var6);
         this.mapSoundPositions.remove(var2);
      }

      if (var1 != null) {
         ItemRecord var7 = ItemRecord.getRecord(var1);
         if (var7 != null) {
            this.mc.ingameGUI.setRecordPlayingMessage(var7.getRecordNameLocal());
         }

         ResourceLocation var8 = null;
         if (Reflector.ForgeItemRecord_getRecordResource.exists() && var7 != null) {
            var8 = (ResourceLocation)Reflector.call(var7, Reflector.ForgeItemRecord_getRecordResource, var1);
         }

         if (var8 == null) {
            var8 = new ResourceLocation(var1);
         }

         PositionedSoundRecord var9 = PositionedSoundRecord.create(var8, var2.getX(), var2.getY(), var2.getZ());
         this.mapSoundPositions.put(var2, var9);
         this.mc.getSoundHandler().playSound(var9);
      }
   }

   @Override
   public void playSound(String var1, double var2, double var4, double var6, float var8, float var9) {
   }

   @Override
   public void playSoundToNearExcept(EntityPlayer var1, String var2, double var3, double var5, double var7, float var9, float var10) {
   }

   @Override
   public void spawnParticle(
      int var1, boolean var2, final double var3, final double var5, final double var7, double var9, double var11, double var13, int... var15
   ) {
      try {
         this.spawnEntityFX(var1, var2, var3, var5, var7, var9, var11, var13, var15);
      } catch (Throwable var22) {
         CrashReport var20 = CrashReport.makeCrashReport(var22, "Exception while adding particle");
         CrashReportCategory var21 = var20.makeCategory("Particle being added");
         var21.addCrashSection("ID", var1);
         if (var15 != null) {
            var21.addCrashSection("Parameters", var15);
         }

         var21.addCrashSectionCallable("Position", new Callable<String>() {
            public String call() throws Exception {
               return CrashReportCategory.getCoordinateInfo(var3, var5, var7);
            }
         });
         throw new ReportedException(var20);
      }
   }

   private void spawnParticle(EnumParticleTypes var1, double var2, double var4, double var6, double var8, double var10, double var12, int... var14) {
      this.spawnParticle(var1.getParticleID(), var1.getShouldIgnoreRange(), var2, var4, var6, var8, var10, var12, var14);
   }

   private EntityFX spawnEntityFX(int var1, boolean var2, double var3, double var5, double var7, double var9, double var11, double var13, int... var15) {
      if (this.mc != null && this.mc.getRenderViewEntity() != null && this.mc.effectRenderer != null) {
         int var19 = this.mc.gameSettings.particleSetting;
         if (var19 == 1 && this.theWorld.rand.nextInt(3) == 0) {
            var19 = 2;
         }

         double var20 = this.mc.getRenderViewEntity().posX - var3;
         double var22 = this.mc.getRenderViewEntity().posY - var5;
         double var24 = this.mc.getRenderViewEntity().posZ - var7;
         if (var1 == EnumParticleTypes.EXPLOSION_HUGE.getParticleID() && !Config.isAnimatedExplosion()) {
            return null;
         }

         if (var1 == EnumParticleTypes.EXPLOSION_LARGE.getParticleID() && !Config.isAnimatedExplosion()) {
            return null;
         }

         if (var1 == EnumParticleTypes.EXPLOSION_NORMAL.getParticleID() && !Config.isAnimatedExplosion()) {
            return null;
         }

         if (var1 == EnumParticleTypes.SUSPENDED.getParticleID() && !Config.isWaterParticles()) {
            return null;
         }

         if (var1 == EnumParticleTypes.SUSPENDED_DEPTH.getParticleID() && !Config.isVoidParticles()) {
            return null;
         }

         if (var1 == EnumParticleTypes.SMOKE_NORMAL.getParticleID() && !Config.isAnimatedSmoke()) {
            return null;
         }

         if (var1 == EnumParticleTypes.SMOKE_LARGE.getParticleID() && !Config.isAnimatedSmoke()) {
            return null;
         }

         if (var1 == EnumParticleTypes.SPELL_MOB.getParticleID() && !Config.isPotionParticles()) {
            return null;
         }

         if (var1 == EnumParticleTypes.SPELL_MOB_AMBIENT.getParticleID() && !Config.isPotionParticles()) {
            return null;
         }

         if (var1 == EnumParticleTypes.SPELL.getParticleID() && !Config.isPotionParticles()) {
            return null;
         }

         if (var1 == EnumParticleTypes.SPELL_INSTANT.getParticleID() && !Config.isPotionParticles()) {
            return null;
         }

         if (var1 == EnumParticleTypes.SPELL_WITCH.getParticleID() && !Config.isPotionParticles()) {
            return null;
         }

         if (var1 == EnumParticleTypes.PORTAL.getParticleID() && !Config.isAnimatedPortal()) {
            return null;
         }

         if (var1 == EnumParticleTypes.FLAME.getParticleID() && !Config.isAnimatedFlame()) {
            return null;
         }

         if (var1 == EnumParticleTypes.REDSTONE.getParticleID() && !Config.isAnimatedRedstone()) {
            return null;
         }

         if (var1 == EnumParticleTypes.DRIP_WATER.getParticleID() && !Config.isDrippingWaterLava()) {
            return null;
         }

         if (var1 == EnumParticleTypes.DRIP_LAVA.getParticleID() && !Config.isDrippingWaterLava()) {
            return null;
         }

         if (var1 == EnumParticleTypes.FIREWORKS_SPARK.getParticleID() && !Config.isFireworkParticles()) {
            return null;
         }

         if (var2) {
            return this.mc.effectRenderer.spawnEffectParticle(var1, var3, var5, var7, var9, var11, var13, var15);
         }

         double var28 = 256.0;
         if (var1 == EnumParticleTypes.CRIT.getParticleID()) {
            var28 = 38416.0;
         }

         if (var20 * var20 + var22 * var22 + var24 * var24 > var28) {
            return null;
         }

         if (var19 > 1) {
            return null;
         }

         EntityFX var30 = this.mc.effectRenderer.spawnEffectParticle(var1, var3, var5, var7, var9, var11, var13, var15);
         if (var1 == EnumParticleTypes.WATER_BUBBLE.getParticleID()) {
            CustomColors.updateWaterFX(var30, this.theWorld, var3, var5, var7);
         }

         if (var1 == EnumParticleTypes.WATER_SPLASH.getParticleID()) {
            CustomColors.updateWaterFX(var30, this.theWorld, var3, var5, var7);
         }

         if (var1 == EnumParticleTypes.WATER_DROP.getParticleID()) {
            CustomColors.updateWaterFX(var30, this.theWorld, var3, var5, var7);
         }

         if (var1 == EnumParticleTypes.TOWN_AURA.getParticleID()) {
            CustomColors.updateMyceliumFX(var30);
         }

         if (var1 == EnumParticleTypes.PORTAL.getParticleID()) {
            CustomColors.updatePortalFX(var30);
         }

         if (var1 == EnumParticleTypes.REDSTONE.getParticleID()) {
            CustomColors.updateReddustFX(var30, this.theWorld, var3, var5, var7);
         }

         return var30;
      } else {
         return null;
      }
   }

   @Override
   public void onEntityAdded(Entity var1) {
      RandomMobs.entityLoaded(var1, this.theWorld);
      if (Config.isDynamicLights()) {
         DynamicLights.P(var1, this);
      }
   }

   @Override
   public void onEntityRemoved(Entity var1) {
      if (Config.isDynamicLights()) {
         DynamicLights.z(var1, this);
      }
   }

   public void deleteAllDisplayLists() {
   }

   @Override
   public void broadcastSound(int var1, BlockPos var2, int var3) {
      switch (var1) {
         case 1013:
         case 1018:
            if (this.mc.getRenderViewEntity() != null) {
               double var7 = var2.getX() - this.mc.getRenderViewEntity().posX;
               double var9 = var2.getY() - this.mc.getRenderViewEntity().posY;
               double var11 = var2.getZ() - this.mc.getRenderViewEntity().posZ;
               double var13 = Math.sqrt(var7 * var7 + var9 * var9 + var11 * var11);
               double var15 = this.mc.getRenderViewEntity().posX;
               double var17 = this.mc.getRenderViewEntity().posY;
               double var19 = this.mc.getRenderViewEntity().posZ;
               if (var13 > 0.0) {
                  var15 += var7 / var13 * 2.0;
                  var17 += var9 / var13 * 2.0;
                  var19 += var11 / var13 * 2.0;
               }

               if (var1 == 1013) {
                  this.theWorld.playSound(var15, var17, var19, "mob.wither.spawn", 1.0F, 1.0F, false);
               } else {
                  this.theWorld.playSound(var15, var17, var19, "mob.enderdragon.end", 5.0F, 1.0F, false);
               }
            }
      }
   }

   @Override
   public void playAuxSFX(EntityPlayer var1, int var2, BlockPos var3, int var4) {
      Random var8 = this.theWorld.rand;
      switch (var2) {
         case 1000:
            this.theWorld.playSoundAtPos(var3, "random.click", 1.0F, 1.0F, false);
            break;
         case 1001:
            this.theWorld.playSoundAtPos(var3, "random.click", 1.0F, 1.2F, false);
            break;
         case 1002:
            this.theWorld.playSoundAtPos(var3, "random.bow", 1.0F, 1.2F, false);
            break;
         case 1003:
            this.theWorld.playSoundAtPos(var3, "random.door_open", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 1004:
            this.theWorld.playSoundAtPos(var3, "random.fizz", 0.5F, 2.6F + (var8.nextFloat() - var8.nextFloat()) * 0.8F, false);
            break;
         case 1005:
            if (Item.getItemById(var4) instanceof ItemRecord) {
               this.theWorld.playRecord(var3, "records." + ((ItemRecord)Item.getItemById(var4)).recordName);
            } else {
               this.theWorld.playRecord(var3, (String)null);
            }
            break;
         case 1006:
            this.theWorld.playSoundAtPos(var3, "random.door_close", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 1007:
            this.theWorld.playSoundAtPos(var3, "mob.ghast.charge", 10.0F, (var8.nextFloat() - var8.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1008:
            this.theWorld.playSoundAtPos(var3, "mob.ghast.fireball", 10.0F, (var8.nextFloat() - var8.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1009:
            this.theWorld.playSoundAtPos(var3, "mob.ghast.fireball", 2.0F, (var8.nextFloat() - var8.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1010:
            this.theWorld.playSoundAtPos(var3, "mob.zombie.wood", 2.0F, (var8.nextFloat() - var8.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1011:
            this.theWorld.playSoundAtPos(var3, "mob.zombie.metal", 2.0F, (var8.nextFloat() - var8.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1012:
            this.theWorld.playSoundAtPos(var3, "mob.zombie.woodbreak", 2.0F, (var8.nextFloat() - var8.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1014:
            this.theWorld.playSoundAtPos(var3, "mob.wither.shoot", 2.0F, (var8.nextFloat() - var8.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1015:
            this.theWorld.playSoundAtPos(var3, "mob.bat.takeoff", 0.05F, (var8.nextFloat() - var8.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1016:
            this.theWorld.playSoundAtPos(var3, "mob.zombie.infect", 2.0F, (var8.nextFloat() - var8.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1017:
            this.theWorld.playSoundAtPos(var3, "mob.zombie.unfect", 2.0F, (var8.nextFloat() - var8.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1020:
            this.theWorld.playSoundAtPos(var3, "random.anvil_break", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 1021:
            this.theWorld.playSoundAtPos(var3, "random.anvil_use", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 1022:
            this.theWorld.playSoundAtPos(var3, "random.anvil_land", 0.3F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 2000:
            int var9 = var4 % 3 - 1;
            int var10 = var4 / 3 % 3 - 1;
            double var11 = var3.getX() + var9 * 0.6 + 0.5;
            double var13 = var3.getY() + 0.5;
            double var15 = var3.getZ() + var10 * 0.6 + 0.5;

            for (int var42 = 0; var42 < 10; var42++) {
               double var43 = var8.nextDouble() * 0.2 + 0.01;
               double var44 = var11 + var9 * 0.01 + (var8.nextDouble() - 0.5) * var10 * 0.5;
               double var45 = var13 + (var8.nextDouble() - 0.5) * 0.5;
               double var47 = var15 + var10 * 0.01 + (var8.nextDouble() - 0.5) * var9 * 0.5;
               double var48 = var9 * var43 + var8.nextGaussian() * 0.01;
               double var49 = -0.03 + var8.nextGaussian() * 0.01;
               double var51 = var10 * var43 + var8.nextGaussian() * 0.01;
               this.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, var44, var45, var47, var48, var49, var51);
            }

            return;
         case 2001:
            Block var17 = Block.getBlockById(var4 & 4095);
            if (var17.getMaterial() != Material.air) {
               this.mc
                  .getSoundHandler()
                  .playSound(
                     new PositionedSoundRecord(
                        new ResourceLocation(var17.stepSound.getBreakSound()),
                        (var17.stepSound.getVolume() + 1.0F) / 2.0F,
                        var17.stepSound.getFrequency() * 0.8F,
                        var3.getX() + 0.5F,
                        var3.getY() + 0.5F,
                        var3.getZ() + 0.5F
                     )
                  );
            }

            this.mc.effectRenderer.addBlockDestroyEffects(var3, var17.getStateFromMeta(var4 >> 12 & 0xFF));
            break;
         case 2002:
            double var18 = var3.getX();
            double var20 = var3.getY();
            double var22 = var3.getZ();

            for (int var24 = 0; var24 < 8; var24++) {
               this.spawnParticle(
                  EnumParticleTypes.ITEM_CRACK,
                  var18,
                  var20,
                  var22,
                  var8.nextGaussian() * 0.15,
                  var8.nextDouble() * 0.2,
                  var8.nextGaussian() * 0.15,
                  Item.getIdFromItem(Items.potionitem),
                  var4
               );
            }

            int var46 = Items.potionitem.getColorFromDamage(var4);
            float var25 = (var46 >> 16 & 0xFF) / 255.0F;
            float var26 = (var46 >> 8 & 0xFF) / 255.0F;
            float var27 = (var46 >> 0 & 0xFF) / 255.0F;
            EnumParticleTypes var28 = EnumParticleTypes.SPELL;
            if (Items.potionitem.isEffectInstant(var4)) {
               var28 = EnumParticleTypes.SPELL_INSTANT;
            }

            for (int var50 = 0; var50 < 100; var50++) {
               double var30 = var8.nextDouble() * 4.0;
               double var32 = var8.nextDouble() * Math.PI * 2.0;
               double var34 = Math.cos(var32) * var30;
               double var54 = 0.01 + var8.nextDouble() * 0.5;
               double var55 = Math.sin(var32) * var30;
               EntityFX var56 = this.spawnEntityFX(
                  var28.getParticleID(), var28.getShouldIgnoreRange(), var18 + var34 * 0.1, var20 + 0.3, var22 + var55 * 0.1, var34, var54, var55
               );
               if (var56 != null) {
                  float var41 = 0.75F + var8.nextFloat() * 0.25F;
                  var56.setRBGColorF(var25 * var41, var26 * var41, var27 * var41);
                  var56.multiplyVelocity((float)var30);
               }
            }

            this.theWorld.playSoundAtPos(var3, "game.potion.smash", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 2003:
            double var29 = var3.getX() + 0.5;
            double var31 = var3.getY();
            double var33 = var3.getZ() + 0.5;

            for (int var52 = 0; var52 < 8; var52++) {
               this.spawnParticle(
                  EnumParticleTypes.ITEM_CRACK,
                  var29,
                  var31,
                  var33,
                  var8.nextGaussian() * 0.15,
                  var8.nextDouble() * 0.2,
                  var8.nextGaussian() * 0.15,
                  Item.getIdFromItem(Items.ender_eye)
               );
            }

            for (double var53 = 0.0; var53 < Math.PI * 2; var53 += Math.PI / 20) {
               this.spawnParticle(
                  EnumParticleTypes.PORTAL,
                  var29 + Math.cos(var53) * 5.0,
                  var31 - 0.4,
                  var33 + Math.sin(var53) * 5.0,
                  Math.cos(var53) * -5.0,
                  0.0,
                  Math.sin(var53) * -5.0
               );
               this.spawnParticle(
                  EnumParticleTypes.PORTAL,
                  var29 + Math.cos(var53) * 5.0,
                  var31 - 0.4,
                  var33 + Math.sin(var53) * 5.0,
                  Math.cos(var53) * -7.0,
                  0.0,
                  Math.sin(var53) * -7.0
               );
            }

            return;
         case 2004:
            for (int var35 = 0; var35 < 20; var35++) {
               double var36 = var3.getX() + 0.5 + (this.theWorld.rand.nextFloat() - 0.5) * 2.0;
               double var38 = var3.getY() + 0.5 + (this.theWorld.rand.nextFloat() - 0.5) * 2.0;
               double var40 = var3.getZ() + 0.5 + (this.theWorld.rand.nextFloat() - 0.5) * 2.0;
               this.theWorld.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, var36, var38, var40, 0.0, 0.0, 0.0);
               this.theWorld.spawnParticle(EnumParticleTypes.FLAME, var36, var38, var40, 0.0, 0.0, 0.0);
            }

            return;
         case 2005:
            ItemDye.spawnBonemealParticles(this.theWorld, var3, var4);
      }
   }

   @Override
   public void sendBlockBreakProgress(int var1, BlockPos var2, int var3) {
      if (var3 >= 0 && var3 < 10) {
         DestroyBlockProgress var7 = this.damagedBlocks.get(var1);
         if (var7 == null || var7.getPosition().getX() != var2.getX() || var7.getPosition().getY() != var2.getY() || var7.getPosition().getZ() != var2.getZ()) {
            var7 = new DestroyBlockProgress(var1, var2);
            this.damagedBlocks.put(var1, var7);
         }

         var7.setPartialBlockDamage(var3);
         var7.setCloudUpdateTick(this.cloudTickCounter);
      } else {
         this.damagedBlocks.remove(var1);
      }
   }

   public void L() {
      this.displayListEntitiesDirty = true;
   }

   public void resetClouds() {
      this.cloudRenderer.reset();
   }

   public int T() {
      return this.viewFrustum.renderChunks.length;
   }

   public int getCountActiveRenderers() {
      return this.renderInfos.size();
   }

   public int P() {
      return this.countEntitiesRendered;
   }

   public int Z() {
      return this.countTileEntitiesRendered;
   }

   public RenderChunk getRenderChunk(BlockPos var1) {
      return this.viewFrustum.getRenderChunk(var1);
   }

   public RenderChunk getRenderChunk(RenderChunk var1, EnumFacing var2) {
      if (var1 == null) {
         return null;
      }

      BlockPos var6 = var1.getBlockPosOffset16(var2);
      return this.viewFrustum.getRenderChunk(var6);
   }

   public WorldClient getWorld() {
      return this.theWorld;
   }

   public void updateTileEntities(Collection<TileEntity> var1, Collection<TileEntity> var2) {
      Set var4 = this.A;
      synchronized (this.A) {
         this.A.removeAll(var1);
         this.A.addAll(var2);
      }
   }

   public static class ContainerLocalRenderInformation {
      public final RenderChunk renderChunk;
      public final EnumFacing facing;
      public final Set<EnumFacing> setFacing = EnumSet.noneOf(EnumFacing.class);
      public final int counter;

      public ContainerLocalRenderInformation(RenderChunk var1, EnumFacing var2, int var3) {
         this.renderChunk = var1;
         this.facing = var2;
         this.counter = var3;
      }
   }
}
