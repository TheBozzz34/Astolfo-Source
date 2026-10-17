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
   private final Set setTileEntities = Sets.newHashSet();
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
   private int renderEntitiesStartupCounter = 2;
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

   public RenderGlobal(Minecraft mcIn) {
      this.cloudRenderer = new CloudRenderer(mcIn);
      this.mc = mcIn;
      this.renderManager = mcIn.getRenderManager();
      this.renderEngine = mcIn.getTextureManager();
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
   public void onResourceManagerReload(IResourceManager resourceManager) {
      this.updateDestroyBlockIcons();
   }

   private void updateDestroyBlockIcons() {
      TextureMap textureMap = this.mc.getTextureMapBlocks();

      for (int i = 0; i < this.destroyBlockIcons.length; i++) {
         this.destroyBlockIcons[i] = textureMap.getAtlasSprite("minecraft:blocks/destroy_stage_" + i);
      }
   }

   public void makeEntityOutlineShader() {
      if (OpenGlHelper.shadersSupported) {
         if (ShaderLinkHelper.getStaticShaderLinkHelper() == null) {
            ShaderLinkHelper.setNewStaticShaderLinkHelper();
         }

         ResourceLocation resourceLocation = new ResourceLocation("shaders/post/entity_outline.json");

         try {
            this.entityOutlineShader = new ShaderGroup(this.mc.getTextureManager(), this.mc.getResourceManager(), this.mc.getFramebuffer(), resourceLocation);
            this.entityOutlineShader.createBindFramebuffers(this.mc.displayWidth, this.mc.displayHeight);
            this.entityOutlineFramebuffer = this.entityOutlineShader.getFramebufferRaw("final");
         } catch (IOException ioException) {
            logger.warn("Failed to load shader: " + resourceLocation, ioException);
            this.entityOutlineShader = null;
            this.entityOutlineFramebuffer = null;
         } catch (JsonSyntaxException jsonSyntaxException) {
            logger.warn("Failed to load shader: " + resourceLocation, jsonSyntaxException);
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
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();
      if (this.sky2VBO != null) {
         this.sky2VBO.deleteGlBuffers();
      }

      if (this.glSkyList2 >= 0) {
         GLAllocation.deleteDisplayLists(this.glSkyList2);
         this.glSkyList2 = -1;
      }

      if (this.vboEnabled) {
         this.sky2VBO = new VertexBuffer(this.vertexBufferFormat);
         this.renderSky(worldRenderer, -16.0F, true);
         worldRenderer.finishDrawing();
         worldRenderer.reset();
         this.sky2VBO.bufferData(worldRenderer.getByteBuffer());
      } else {
         this.glSkyList2 = GLAllocation.generateDisplayLists(1);
         GL11.glNewList(this.glSkyList2, 4864);
         this.renderSky(worldRenderer, -16.0F, true);
         tessellator.draw();
         GL11.glEndList();
      }
   }

   private void generateSky() {
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();
      if (this.skyVBO != null) {
         this.skyVBO.deleteGlBuffers();
      }

      if (this.glSkyList >= 0) {
         GLAllocation.deleteDisplayLists(this.glSkyList);
         this.glSkyList = -1;
      }

      if (this.vboEnabled) {
         this.skyVBO = new VertexBuffer(this.vertexBufferFormat);
         this.renderSky(worldRenderer, 16.0F, false);
         worldRenderer.finishDrawing();
         worldRenderer.reset();
         this.skyVBO.bufferData(worldRenderer.getByteBuffer());
      } else {
         this.glSkyList = GLAllocation.generateDisplayLists(1);
         GL11.glNewList(this.glSkyList, 4864);
         this.renderSky(worldRenderer, 16.0F, false);
         tessellator.draw();
         GL11.glEndList();
      }
   }

   private void renderSky(WorldRenderer worldRendererIn, float posY, boolean reverseX) {
      worldRendererIn.begin(7, DefaultVertexFormats.POSITION);

      for (int i = -384; i <= 384; i += 64) {
         for (int j = -384; j <= 384; j += 64) {
            float f = i;
            float g = i + 64;
            if (reverseX) {
               g = i;
               f = i + 64;
            }

            worldRendererIn.pos(f, posY, j).endVertex();
            worldRendererIn.pos(g, posY, j).endVertex();
            worldRendererIn.pos(g, posY, j + 64).endVertex();
            worldRendererIn.pos(f, posY, j + 64).endVertex();
         }
      }
   }

   private void generateStars() {
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();
      if (this.starVBO != null) {
         this.starVBO.deleteGlBuffers();
      }

      if (this.starGLCallList >= 0) {
         GLAllocation.deleteDisplayLists(this.starGLCallList);
         this.starGLCallList = -1;
      }

      if (this.vboEnabled) {
         this.starVBO = new VertexBuffer(this.vertexBufferFormat);
         this.renderStars(worldRenderer);
         worldRenderer.finishDrawing();
         worldRenderer.reset();
         this.starVBO.bufferData(worldRenderer.getByteBuffer());
      } else {
         this.starGLCallList = GLAllocation.generateDisplayLists(1);
         GlStateManager.pushMatrix();
         GL11.glNewList(this.starGLCallList, 4864);
         this.renderStars(worldRenderer);
         tessellator.draw();
         GL11.glEndList();
         GlStateManager.popMatrix();
      }
   }

   private void renderStars(WorldRenderer worldRendererIn) {
      Random random = new Random(10842L);
      worldRendererIn.begin(7, DefaultVertexFormats.POSITION);

      for (int i = 0; i < 1500; i++) {
         double d = random.nextFloat() * 2.0F - 1.0F;
         double e = random.nextFloat() * 2.0F - 1.0F;
         double d2 = random.nextFloat() * 2.0F - 1.0F;
         double d3 = 0.15F + random.nextFloat() * 0.1F;
         double d4 = d * d + e * e + d2 * d2;
         if (d4 < 1.0 && d4 > 0.01) {
            d4 = 1.0 / Math.sqrt(d4);
            d *= d4;
            e *= d4;
            d2 *= d4;
            double d5 = d * 100.0;
            double d6 = e * 100.0;
            double d7 = d2 * 100.0;
            double d8 = Math.atan2(d, d2);
            double d9 = Math.sin(d8);
            double d10 = Math.cos(d8);
            double d11 = Math.atan2(Math.sqrt(d * d + d2 * d2), e);
            double d12 = Math.sin(d11);
            double d13 = Math.cos(d11);
            double d14 = random.nextDouble() * Math.PI * 2.0;
            double d15 = Math.sin(d14);
            double d16 = Math.cos(d14);

            for (int j = 0; j < 4; j++) {
               double d17 = ((j & 2) - 1) * d3;
               double d18 = ((j + 1 & 2) - 1) * d3;
               double d19 = d17 * d16 - d18 * d15;
               double d20 = d18 * d16 + d17 * d15;
               double d21 = d19 * d12 + 0.0 * d13;
               double d22 = 0.0 * d12 - d19 * d13;
               double d23 = d22 * d9 - d20 * d10;
               double d24 = d20 * d9 + d22 * d10;
               worldRendererIn.pos(d5 + d23, d6 + d21, d7 + d24).endVertex();
            }
         }
      }
   }

   public void setWorldAndLoadRenderers(WorldClient worldClientIn) {
      if (this.theWorld != null) {
         this.theWorld.removeWorldAccess(this);
      }

      this.frustumUpdatePosX = Double.MIN_VALUE;
      this.frustumUpdatePosY = Double.MIN_VALUE;
      this.frustumUpdatePosZ = Double.MIN_VALUE;
      this.frustumUpdatePosChunkX = Integer.MIN_VALUE;
      this.frustumUpdatePosChunkY = Integer.MIN_VALUE;
      this.frustumUpdatePosChunkZ = Integer.MIN_VALUE;
      this.renderManager.set(worldClientIn);
      this.theWorld = worldClientIn;
      if (Config.isDynamicLights()) {
         DynamicLights.clear();
      }

      if (worldClientIn != null) {
         worldClientIn.addWorldAccess(this);
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
         boolean bl = this.vboEnabled;
         this.vboEnabled = OpenGlHelper.useVbo();
         if (bl && !this.vboEnabled) {
            this.renderContainer = new RenderList();
            this.renderChunkFactory = new ListChunkFactory();
         } else if (!bl && this.vboEnabled) {
            this.renderContainer = new VboRenderList();
            this.renderChunkFactory = new VboChunkFactory();
         }

         if (bl != this.vboEnabled) {
            this.generateStars();
            this.generateSky();
            this.generateSky2();
         }

         if (this.viewFrustum != null) {
            this.viewFrustum.deleteGlResources();
         }

         this.stopChunkUpdates();
         Set set = this.setTileEntities;
         synchronized (this.setTileEntities) {
            this.setTileEntities.clear();
         }

         this.viewFrustum = new ViewFrustum(this.theWorld, this.mc.gameSettings.renderDistanceChunks, this, this.renderChunkFactory);
         if (this.theWorld != null) {
            Entity entity = this.mc.getRenderViewEntity();
            if (entity != null) {
               this.viewFrustum.updateChunkPositions(entity.posX, entity.posZ);
            }
         }

         this.renderEntitiesStartupCounter = 2;
      }
   }

   public void stopChunkUpdates() {
      this.chunksToUpdate.clear();
      this.renderDispatcher.stopChunkUpdates();
   }

   public void createBindEntityOutlineFbs(int width, int height) {
      if (OpenGlHelper.shadersSupported && this.entityOutlineShader != null) {
         this.entityOutlineShader.createBindFramebuffers(width, height);
      }
   }

   public void renderEntities(Entity renderViewEntity, ICamera camera, float partialTicks) {
      int i = 0;
      if (Reflector.MinecraftForgeClient_getRenderPass.exists()) {
         i = Reflector.callInt(Reflector.MinecraftForgeClient_getRenderPass);
      }

      if (this.renderEntitiesStartupCounter > 0) {
         if (i > 0) {
            return;
         }

         this.renderEntitiesStartupCounter--;
      } else {
         double d = renderViewEntity.prevPosX + (renderViewEntity.posX - renderViewEntity.prevPosX) * partialTicks;
         double e = renderViewEntity.prevPosY + (renderViewEntity.posY - renderViewEntity.prevPosY) * partialTicks;
         double d2 = renderViewEntity.prevPosZ + (renderViewEntity.posZ - renderViewEntity.prevPosZ) * partialTicks;
         this.theWorld.theProfiler.startSection("prepare");
         TileEntityRendererDispatcher.instance
            .cacheActiveRenderInfo(this.theWorld, this.mc.getTextureManager(), this.mc.fontRendererObj, this.mc.getRenderViewEntity(), partialTicks);
         this.renderManager
            .cacheActiveRenderInfo(
               this.theWorld, this.mc.fontRendererObj, this.mc.getRenderViewEntity(), this.mc.pointedEntity, this.mc.gameSettings, partialTicks
            );
         if (i == 0) {
            this.countEntitiesTotal = 0;
            this.countEntitiesRendered = 0;
            this.countEntitiesHidden = 0;
            this.countTileEntitiesRendered = 0;
         }

         Entity entity = this.mc.getRenderViewEntity();
         double d3 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
         double d4 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
         double d5 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
         TileEntityRendererDispatcher.staticPlayerX = d3;
         TileEntityRendererDispatcher.staticPlayerY = d4;
         TileEntityRendererDispatcher.staticPlayerZ = d5;
         this.renderManager.setRenderPosition(d3, d4, d5);
         this.mc.entityRenderer.enableLightmap();
         this.theWorld.theProfiler.endStartSection("global");
         List list = this.theWorld.getLoadedEntityList();
         if (i == 0) {
            this.countEntitiesTotal = list.size();
         }

         if (Config.isFogOff() && this.mc.entityRenderer.fogStandard) {
            GlStateManager.disableFog();
         }

         boolean bl = Reflector.ForgeEntity_shouldRenderInPass.exists();
         boolean bl2 = Reflector.ForgeTileEntity_shouldRenderInPass.exists();

         for (int j = 0; j < this.theWorld.weatherEffects.size(); j++) {
            Entity entity2 = this.theWorld.weatherEffects.get(j);
            if (!bl || Reflector.callBoolean(entity2, Reflector.ForgeEntity_shouldRenderInPass, i)) {
               this.countEntitiesRendered++;
               if (entity2.isInRangeToRender3d(d, e, d2)) {
                  this.renderManager.renderEntitySimple(entity2, partialTicks);
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
            this.renderManager.setRenderOutlines(true);

            for (int k = 0; k < list.size(); k++) {
               Entity entity3 = (Entity)list.get(k);
               if (!bl || Reflector.callBoolean(entity3, Reflector.ForgeEntity_shouldRenderInPass, i)) {
                  boolean bl3 = this.mc.getRenderViewEntity() instanceof EntityLivingBase
                     && ((EntityLivingBase)this.mc.getRenderViewEntity()).isPlayerSleeping();
                  boolean bl4 = entity3.isInRangeToRender3d(d, e, d2)
                     && (entity3.ignoreFrustumCheck || camera.isBoundingBoxInFrustum(entity3.getEntityBoundingBox()) || entity3.riddenByEntity == this.mc.thePlayer)
                     && entity3 instanceof EntityPlayer;
                  if ((entity3 != this.mc.getRenderViewEntity() || this.mc.gameSettings.thirdPersonView != 0 || bl3) && bl4) {
                     this.renderManager.renderEntitySimple(entity3, partialTicks);
                  }
               }
            }

            this.renderManager.setRenderOutlines(false);
            RenderHelper.enableStandardItemLighting();
            GlStateManager.depthMask(false);
            this.entityOutlineShader.loadShaderGroup(partialTicks);
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
         boolean bl5 = Config.isShaders();
         if (bl5) {
            Shaders.beginEntities();
         }

         Iterator iterator2 = this.renderInfosEntities.iterator();
         boolean bl6 = this.mc.gameSettings.fancyGraphics;
         this.mc.gameSettings.fancyGraphics = Config.isDroppedItemsFancy();

         while (iterator2.hasNext()) {
            RenderGlobal.ContainerLocalRenderInformation containerLocalRenderInformation = (RenderGlobal.ContainerLocalRenderInformation)iterator2.next();
            Chunk chunk = this.theWorld.getChunkFromBlockCoords(containerLocalRenderInformation.renderChunk.getPosition());
            ClassInheritanceMultiMap classInheritanceMultiMap = chunk.getEntityLists()[containerLocalRenderInformation.renderChunk.getPosition().getY() / 16];
            if (!classInheritanceMultiMap.isEmpty()) {
               for (Object object : classInheritanceMultiMap) {
                  Entity entity4 = (Entity)object;
                  if (!bl || Reflector.callBoolean(entity4, Reflector.ForgeEntity_shouldRenderInPass, i)) {
                     boolean bl7 = this.renderManager.shouldRender(entity4, camera, d, e, d2) || entity4.riddenByEntity == this.mc.thePlayer;
                     if (bl7) {
                        boolean bl8 = this.mc.getRenderViewEntity() instanceof EntityLivingBase
                           ? ((EntityLivingBase)this.mc.getRenderViewEntity()).isPlayerSleeping()
                           : false;
                        if (entity4 == this.mc.getRenderViewEntity() && this.mc.gameSettings.thirdPersonView == 0 && !bl8
                           || !(entity4.posY < 0.0) && !(entity4.posY >= 256.0) && !this.theWorld.isBlockLoaded(new BlockPos(entity4))) {
                           continue;
                        }

                        this.countEntitiesRendered++;
                        if (entity4.getClass() == EntityItemFrame.class) {
                           entity4.renderDistanceWeight = 0.06;
                        }

                        this.renderedEntity = entity4;
                        if (bl5) {
                           Shaders.nextEntity(entity4);
                        }

                        this.renderManager.renderEntitySimple(entity4, partialTicks);
                        this.renderedEntity = null;
                     }

                     if (!bl7 && entity4 instanceof EntityWitherSkull) {
                        if (bl5) {
                           Shaders.nextEntity(entity4);
                        }

                        this.mc.getRenderManager().renderWitherSkull(entity4, partialTicks);
                     }
                  }
               }
            }
         }

         this.mc.gameSettings.fancyGraphics = bl6;
         FontRenderer fontRenderer = TileEntityRendererDispatcher.instance.getFontRenderer();
         if (bl5) {
            Shaders.endEntities();
            Shaders.beginBlockEntities();
         }

         this.theWorld.theProfiler.endStartSection("blockentities");
         RenderHelper.enableStandardItemLighting();
         if (Reflector.ForgeTileEntityRendererDispatcher_preDrawBatch.exists()) {
            Reflector.call(TileEntityRendererDispatcher.instance, Reflector.ForgeTileEntityRendererDispatcher_preDrawBatch);
         }

         for (Object object2 : this.renderInfosTileEntities) {
            RenderGlobal.ContainerLocalRenderInformation containerLocalRenderInformation2 = (RenderGlobal.ContainerLocalRenderInformation)object2;
            List list2 = containerLocalRenderInformation2.renderChunk.getCompiledChunk().getTileEntities();
            if (!list2.isEmpty()) {
               for (Object object3 : list2) {
                  TileEntity tileEntity = (TileEntity)object3;
                  if (bl2) {
                     if (!Reflector.callBoolean(tileEntity, Reflector.ForgeTileEntity_shouldRenderInPass, i)) {
                        continue;
                     }

                     AxisAlignedBB axisAlignedBB = (AxisAlignedBB)Reflector.call(tileEntity, Reflector.ForgeTileEntity_getRenderBoundingBox);
                     if (axisAlignedBB != null && !camera.isBoundingBoxInFrustum(axisAlignedBB)) {
                        continue;
                     }
                  }

                  Class class2 = tileEntity.getClass();
                  if (class2 == TileEntitySign.class && !Config.zoomMode) {
                     EntityPlayerSP entityPlayerSP = this.mc.thePlayer;
                     double d6 = tileEntity.getDistanceSq(entityPlayerSP.posX, entityPlayerSP.posY, entityPlayerSP.posZ);
                     if (d6 > 256.0) {
                        fontRenderer.enabled = false;
                     }
                  }

                  if (bl5) {
                     Shaders.nextBlockEntity(tileEntity);
                  }

                  TileEntityRendererDispatcher.instance.renderTileEntity(tileEntity, partialTicks, -1);
                  this.countTileEntitiesRendered++;
                  fontRenderer.enabled = true;
               }
            }
         }

         Set set = this.setTileEntities;
         synchronized (this.setTileEntities) {
            for (Object object4 : this.setTileEntities) {
               if (bl2) {
                  if (!Reflector.callBoolean(object4, Reflector.ForgeTileEntity_shouldRenderInPass, i)) {
                     continue;
                  }

                  AxisAlignedBB axisAlignedBB2 = (AxisAlignedBB)Reflector.call(object4, Reflector.ForgeTileEntity_getRenderBoundingBox);
                  if (axisAlignedBB2 != null && !camera.isBoundingBoxInFrustum(axisAlignedBB2)) {
                     continue;
                  }
               }

               Class class3 = object4.getClass();
               if (class3 == TileEntitySign.class && !Config.zoomMode) {
                  EntityPlayerSP entityPlayerSP2 = this.mc.thePlayer;
                  double d7 = ((TileEntity)object4).getDistanceSq(entityPlayerSP2.posX, entityPlayerSP2.posY, entityPlayerSP2.posZ);
                  if (d7 > 256.0) {
                     fontRenderer.enabled = false;
                  }
               }

               if (bl5) {
                  Shaders.nextBlockEntity((TileEntity)object4);
               }

               TileEntityRendererDispatcher.instance.renderTileEntity((TileEntity)object4, partialTicks, -1);
               fontRenderer.enabled = true;
            }
         }

         if (Reflector.ForgeTileEntityRendererDispatcher_drawBatch.exists()) {
            Reflector.call(TileEntityRendererDispatcher.instance, Reflector.ForgeTileEntityRendererDispatcher_drawBatch, i);
         }

         this.preRenderDamagedBlocks();

         for (Object object5 : this.damagedBlocks.values()) {
            BlockPos blockPos = ((DestroyBlockProgress)object5).getPosition();
            TileEntity tileEntity2 = this.theWorld.getTileEntity(blockPos);
            if (tileEntity2 instanceof TileEntityChest) {
               TileEntityChest tileEntityChest = (TileEntityChest)tileEntity2;
               if (tileEntityChest.adjacentChestXNeg != null) {
                  blockPos = blockPos.offset(EnumFacing.WEST);
                  tileEntity2 = this.theWorld.getTileEntity(blockPos);
               } else if (tileEntityChest.adjacentChestZNeg != null) {
                  blockPos = blockPos.offset(EnumFacing.NORTH);
                  tileEntity2 = this.theWorld.getTileEntity(blockPos);
               }
            }

            Block block = this.theWorld.getBlockState(blockPos).getBlock();
            boolean bl9;
            if (bl2) {
               bl9 = false;
               if (tileEntity2 != null
                  && Reflector.callBoolean(tileEntity2, Reflector.ForgeTileEntity_shouldRenderInPass, i)
                  && Reflector.callBoolean(tileEntity2, Reflector.ForgeTileEntity_canRenderBreaking)) {
                  AxisAlignedBB axisAlignedBB3 = (AxisAlignedBB)Reflector.call(tileEntity2, Reflector.ForgeTileEntity_getRenderBoundingBox);
                  if (axisAlignedBB3 != null) {
                     bl9 = camera.isBoundingBoxInFrustum(axisAlignedBB3);
                  }
               }
            } else {
               bl9 = tileEntity2 != null
                  && (block instanceof BlockChest || block instanceof BlockEnderChest || block instanceof BlockSign || block instanceof BlockSkull);
            }

            if (bl9) {
               if (bl5) {
                  Shaders.nextBlockEntity(tileEntity2);
               }

               TileEntityRendererDispatcher.instance.renderTileEntity(tileEntity2, partialTicks, ((DestroyBlockProgress)object5).getPartialBlockDamage());
            }
         }

         this.postRenderDamagedBlocks();
         this.mc.entityRenderer.disableLightmap();
         this.mc.mcProfiler.endSection();
      }
   }

   public String getDebugInfoRenders() {
      int i = this.viewFrustum.renderChunks.length;
      int j = 0;

      for (Object object : this.renderInfos) {
         RenderGlobal.ContainerLocalRenderInformation containerLocalRenderInformation = (RenderGlobal.ContainerLocalRenderInformation)object;
         CompiledChunk compiledChunk2 = containerLocalRenderInformation.renderChunk.compiledChunk;
         if (compiledChunk2 != CompiledChunk.DUMMY && !compiledChunk2.isEmpty()) {
            j++;
         }
      }

      return String.format(
         "C: %d/%d %sD: %d, %s", j, i, this.mc.renderChunksMany ? "(s) " : "", this.renderDistanceChunks, this.renderDispatcher.getDebugInfo()
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

   public void setupTerrain(Entity viewEntity, double partialTicks, ICamera camera, int frameCount, boolean playerSpectator) {
      SetupTerrainEvent setupTerrainEvent = new SetupTerrainEvent();
      Astolfo.INSTANCE.getEventBus().post(setupTerrainEvent);
      if (setupTerrainEvent.isCancelled()) {
         playerSpectator = true;
      }

      if (this.mc.gameSettings.renderDistanceChunks != this.renderDistanceChunks) {
         this.loadRenderers();
      }

      this.theWorld.theProfiler.startSection("camera");
      double d = viewEntity.posX - this.frustumUpdatePosX;
      double e = viewEntity.posY - this.frustumUpdatePosY;
      double d2 = viewEntity.posZ - this.frustumUpdatePosZ;
      if (this.frustumUpdatePosChunkX != viewEntity.chunkCoordX
         || this.frustumUpdatePosChunkY != viewEntity.chunkCoordY
         || this.frustumUpdatePosChunkZ != viewEntity.chunkCoordZ
         || d * d + e * e + d2 * d2 > 16.0) {
         this.frustumUpdatePosX = viewEntity.posX;
         this.frustumUpdatePosY = viewEntity.posY;
         this.frustumUpdatePosZ = viewEntity.posZ;
         this.frustumUpdatePosChunkX = viewEntity.chunkCoordX;
         this.frustumUpdatePosChunkY = viewEntity.chunkCoordY;
         this.frustumUpdatePosChunkZ = viewEntity.chunkCoordZ;
         this.viewFrustum.updateChunkPositions(viewEntity.posX, viewEntity.posZ);
      }

      if (Config.isDynamicLights()) {
         DynamicLights.update(this);
      }

      this.theWorld.theProfiler.endStartSection("renderlistcamera");
      double d3 = viewEntity.lastTickPosX + (viewEntity.posX - viewEntity.lastTickPosX) * partialTicks;
      double d4 = viewEntity.lastTickPosY + (viewEntity.posY - viewEntity.lastTickPosY) * partialTicks;
      double d5 = viewEntity.lastTickPosZ + (viewEntity.posZ - viewEntity.lastTickPosZ) * partialTicks;
      this.renderContainer.initialize(d3, d4, d5);
      this.theWorld.theProfiler.endStartSection("cull");
      if (this.debugFixedClippingHelper != null) {
         Frustum frustum = new Frustum(this.debugFixedClippingHelper);
         frustum.setPosition(this.debugTerrainFrustumPosition.x, this.debugTerrainFrustumPosition.y, this.debugTerrainFrustumPosition.z);
         camera = frustum;
      }

      this.mc.mcProfiler.endStartSection("culling");
      BlockPos blockPos = new BlockPos(d3, d4 + viewEntity.getEyeHeight(), d5);
      RenderChunk renderChunk2 = this.viewFrustum.getRenderChunk(blockPos);
      BlockPos blockPos2 = new BlockPos(
         MathHelper.floor_double(d3 / 16.0) * 16, MathHelper.floor_double(d4 / 16.0) * 16, MathHelper.floor_double(d5 / 16.0) * 16
      );
      this.displayListEntitiesDirty = this.displayListEntitiesDirty
         || !this.chunksToUpdate.isEmpty()
         || viewEntity.posX != this.lastViewEntityX
         || viewEntity.posY != this.lastViewEntityY
         || viewEntity.posZ != this.lastViewEntityZ
         || viewEntity.rotationPitch != this.lastViewEntityPitch
         || viewEntity.rotationYaw != this.lastViewEntityYaw;
      this.lastViewEntityX = viewEntity.posX;
      this.lastViewEntityY = viewEntity.posY;
      this.lastViewEntityZ = viewEntity.posZ;
      this.lastViewEntityPitch = viewEntity.rotationPitch;
      this.lastViewEntityYaw = viewEntity.rotationYaw;
      boolean bl = this.debugFixedClippingHelper != null;
      Lagometer.timerVisibility.start();
      if (Shaders.isShadowPass) {
         this.renderInfos = this.renderInfosShadow;
         this.renderInfosEntities = this.renderInfosEntitiesShadow;
         this.renderInfosTileEntities = this.renderInfosTileEntitiesShadow;
         if (!bl && this.displayListEntitiesDirty) {
            this.renderInfos.clear();
            this.renderInfosEntities.clear();
            this.renderInfosTileEntities.clear();
            RenderInfoLazy renderInfoLazy = new RenderInfoLazy();
            Iterator iterator2 = ShadowUtils.makeShadowChunkIterator(this.theWorld, partialTicks, viewEntity, this.renderDistanceChunks, this.viewFrustum);

            while (iterator2.hasNext()) {
               RenderChunk renderChunk3 = (RenderChunk)iterator2.next();
               if (renderChunk3 != null) {
                  renderInfoLazy.setRenderChunk(renderChunk3);
                  if (!renderChunk3.compiledChunk.isEmpty() || renderChunk3.isNeedsUpdate()) {
                     this.renderInfos.add(renderInfoLazy.getRenderInfo());
                  }

                  BlockPos blockPos3 = renderChunk3.getPosition();
                  if (ChunkUtils.hasEntities(this.theWorld.getChunkFromBlockCoords(blockPos3))) {
                     this.renderInfosEntities.add(renderInfoLazy.getRenderInfo());
                  }

                  if (renderChunk3.getCompiledChunk().getTileEntities().size() > 0) {
                     this.renderInfosTileEntities.add(renderInfoLazy.getRenderInfo());
                  }
               }
            }
         }
      } else {
         this.renderInfos = this.renderInfosNormal;
         this.renderInfosEntities = this.renderInfosEntitiesNormal;
         this.renderInfosTileEntities = this.renderInfosTileEntitiesNormal;
      }

      if (!bl && this.displayListEntitiesDirty && !Shaders.isShadowPass) {
         this.displayListEntitiesDirty = false;
         this.renderInfos.clear();
         this.renderInfosEntities.clear();
         this.renderInfosTileEntities.clear();
         this.visibilityDeque.clear();
         Deque deque = this.visibilityDeque;
         boolean bl2 = this.mc.renderChunksMany;
         if (renderChunk2 != null) {
            boolean bl3 = false;
            RenderGlobal.ContainerLocalRenderInformation containerLocalRenderInformation = new RenderGlobal.ContainerLocalRenderInformation(renderChunk2, (EnumFacing)null, 0);
            Set set = SET_ALL_FACINGS;
            if (set.size() == 1) {
               Vector3f vector3f = this.getViewVector(viewEntity, partialTicks);
               EnumFacing enumFacing = EnumFacing.getFacingFromVector(vector3f.x, vector3f.y, vector3f.z).getOpposite();
               set.remove(enumFacing);
            }

            if (set.isEmpty()) {
               bl3 = true;
            }

            if (bl3 && !playerSpectator) {
               this.renderInfos.add(containerLocalRenderInformation);
            } else {
               if (playerSpectator && this.theWorld.getBlockState(blockPos).getBlock().isOpaqueCube()) {
                  bl2 = false;
               }

               renderChunk2.setFrameIndex(frameCount);
               deque.add(containerLocalRenderInformation);
            }
         } else {
            int i = blockPos.getY() > 0 ? 248 : 8;

            for (int j = -this.renderDistanceChunks; j <= this.renderDistanceChunks; j++) {
               for (int k = -this.renderDistanceChunks; k <= this.renderDistanceChunks; k++) {
                  RenderChunk renderChunk4 = this.viewFrustum.getRenderChunk(new BlockPos((j << 4) + 8, i, (k << 4) + 8));
                  if (renderChunk4 != null && camera.isBoundingBoxInFrustum(renderChunk4.boundingBox)) {
                     renderChunk4.setFrameIndex(frameCount);
                     deque.add(new RenderGlobal.ContainerLocalRenderInformation(renderChunk4, (EnumFacing)null, 0));
                  }
               }
            }
         }

         EnumFacing[] enumFacings = EnumFacing.VALUES;
         int l = enumFacings.length;

         while (!deque.isEmpty()) {
            RenderGlobal.ContainerLocalRenderInformation containerLocalRenderInformation2 = (RenderGlobal.ContainerLocalRenderInformation)deque.poll();
            RenderChunk renderChunk5 = containerLocalRenderInformation2.renderChunk;
            EnumFacing enumFacing2 = containerLocalRenderInformation2.facing;
            BlockPos blockPos4 = renderChunk5.getPosition();
            if (!renderChunk5.compiledChunk.isEmpty() || renderChunk5.isNeedsUpdate()) {
               this.renderInfos.add(containerLocalRenderInformation2);
            }

            if (ChunkUtils.hasEntities(this.theWorld.getChunkFromBlockCoords(blockPos4))) {
               this.renderInfosEntities.add(containerLocalRenderInformation2);
            }

            if (renderChunk5.getCompiledChunk().getTileEntities().size() > 0) {
               this.renderInfosTileEntities.add(containerLocalRenderInformation2);
            }

            for (int m = 0; m < l; m++) {
               EnumFacing enumFacing3 = enumFacings[m];
               if ((!bl2 || !containerLocalRenderInformation2.setFacing.contains(enumFacing3.getOpposite()))
                  && (!bl2 || enumFacing2 == null || renderChunk5.getCompiledChunk().isVisible(enumFacing2.getOpposite(), enumFacing3))) {
                  RenderChunk renderChunk6 = this.getRenderChunkOffset(blockPos, renderChunk5, enumFacing3);
                  if (renderChunk6 != null && renderChunk6.setFrameIndex(frameCount) && camera.isBoundingBoxInFrustum(renderChunk6.boundingBox)) {
                     RenderGlobal.ContainerLocalRenderInformation containerLocalRenderInformation3 = new RenderGlobal.ContainerLocalRenderInformation(renderChunk6, enumFacing3, containerLocalRenderInformation2.counter + 1);
                     containerLocalRenderInformation3.setFacing.addAll(containerLocalRenderInformation2.setFacing);
                     containerLocalRenderInformation3.setFacing.add(enumFacing3);
                     deque.add(containerLocalRenderInformation3);
                  }
               }
            }
         }
      }

      if (this.debugFixTerrainFrustum) {
         this.fixTerrainFrustum(d3, d4, d5);
         this.debugFixTerrainFrustum = false;
      }

      Lagometer.timerVisibility.end();
      if (Shaders.isShadowPass) {
         Shaders.mcProfilerEndSection();
      } else {
         this.renderDispatcher.clearChunkUpdates();
         Set set2 = this.chunksToUpdate;
         this.chunksToUpdate = Sets.newLinkedHashSet();
         Iterator iterator3 = this.renderInfos.iterator();
         Lagometer.timerChunkUpdate.start();

         while (iterator3.hasNext()) {
            RenderGlobal.ContainerLocalRenderInformation containerLocalRenderInformation4 = (RenderGlobal.ContainerLocalRenderInformation)iterator3.next();
            RenderChunk renderChunk7 = containerLocalRenderInformation4.renderChunk;
            if (renderChunk7.isNeedsUpdate() || set2.contains(renderChunk7)) {
               this.displayListEntitiesDirty = true;
               if (this.isPositionInRenderChunk(blockPos2, containerLocalRenderInformation4.renderChunk)) {
                  if (!renderChunk7.isPlayerUpdate()) {
                     this.chunksToUpdateForced.add(renderChunk7);
                  } else {
                     this.mc.mcProfiler.startSection("build near");
                     this.renderDispatcher.updateChunkNow(renderChunk7);
                     renderChunk7.setNeedsUpdate(false);
                     this.mc.mcProfiler.endSection();
                  }
               } else {
                  this.chunksToUpdate.add(renderChunk7);
               }
            }
         }

         Lagometer.timerChunkUpdate.end();
         this.chunksToUpdate.addAll(set2);
         this.mc.mcProfiler.endSection();
      }
   }

   private boolean isPositionInRenderChunk(BlockPos pos, RenderChunk renderChunkIn) {
      BlockPos blockPos = renderChunkIn.getPosition();
      return MathHelper.abs_int(pos.getX() - blockPos.getX()) > 16
         ? false
         : (MathHelper.abs_int(pos.getY() - blockPos.getY()) > 16 ? false : MathHelper.abs_int(pos.getZ() - blockPos.getZ()) <= 16);
   }

   private RenderChunk getRenderChunkOffset(BlockPos playerPos, RenderChunk renderChunkBase, EnumFacing facing) {
      BlockPos blockPos = renderChunkBase.getPositionOffset16(facing);
      if (blockPos.getY() >= 0 && blockPos.getY() < 256) {
         int i = MathHelper.abs_int(playerPos.getX() - blockPos.getX());
         int j = MathHelper.abs_int(playerPos.getZ() - blockPos.getZ());
         if (Config.isFogOff()) {
            if (i > this.renderDistance || j > this.renderDistance) {
               return null;
            }
         } else {
            int k = i * i + j * j;
            if (k > this.renderDistanceSq) {
               return null;
            }
         }

         return this.viewFrustum.getRenderChunk(blockPos);
      } else {
         return null;
      }
   }

   private void fixTerrainFrustum(double x, double y, double z) {
      this.debugFixedClippingHelper = new ClippingHelperImpl();
      ((ClippingHelperImpl)this.debugFixedClippingHelper).init();
      Matrix4f matrix4f = new Matrix4f(this.debugFixedClippingHelper.modelviewMatrix);
      matrix4f.transpose();
      Matrix4f matrix4f2 = new Matrix4f(this.debugFixedClippingHelper.projectionMatrix);
      matrix4f2.transpose();
      Matrix4f matrix4f3 = new Matrix4f();
      Matrix4f.mul(matrix4f2, matrix4f, matrix4f3);
      matrix4f3.invert();
      this.debugTerrainFrustumPosition.x = x;
      this.debugTerrainFrustumPosition.y = y;
      this.debugTerrainFrustumPosition.z = z;
      this.debugTerrainMatrix[0] = new Vector4f(-1.0F, -1.0F, -1.0F, 1.0F);
      this.debugTerrainMatrix[1] = new Vector4f(1.0F, -1.0F, -1.0F, 1.0F);
      this.debugTerrainMatrix[2] = new Vector4f(1.0F, 1.0F, -1.0F, 1.0F);
      this.debugTerrainMatrix[3] = new Vector4f(-1.0F, 1.0F, -1.0F, 1.0F);
      this.debugTerrainMatrix[4] = new Vector4f(-1.0F, -1.0F, 1.0F, 1.0F);
      this.debugTerrainMatrix[5] = new Vector4f(1.0F, -1.0F, 1.0F, 1.0F);
      this.debugTerrainMatrix[6] = new Vector4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.debugTerrainMatrix[7] = new Vector4f(-1.0F, 1.0F, 1.0F, 1.0F);

      for (int i = 0; i < 8; i++) {
         Matrix4f.transform(matrix4f3, this.debugTerrainMatrix[i], this.debugTerrainMatrix[i]);
         this.debugTerrainMatrix[i].x = this.debugTerrainMatrix[i].x / this.debugTerrainMatrix[i].w;
         this.debugTerrainMatrix[i].y = this.debugTerrainMatrix[i].y / this.debugTerrainMatrix[i].w;
         this.debugTerrainMatrix[i].z = this.debugTerrainMatrix[i].z / this.debugTerrainMatrix[i].w;
         this.debugTerrainMatrix[i].w = 1.0F;
      }
   }

   public Vector3f getViewVector(Entity entityIn, double partialTicks) {
      float f = (float)(entityIn.prevRotationPitch + (entityIn.rotationPitch - entityIn.prevRotationPitch) * partialTicks);
      float g = (float)(entityIn.prevRotationYaw + (entityIn.rotationYaw - entityIn.prevRotationYaw) * partialTicks);
      if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 2) {
         f += 180.0F;
      }

      float h = MathHelper.cos(-g * (float) (Math.PI / 180.0) - (float) Math.PI);
      float f2 = MathHelper.sin(-g * (float) (Math.PI / 180.0) - (float) Math.PI);
      float f3 = -MathHelper.cos(-f * (float) (Math.PI / 180.0));
      float f4 = MathHelper.sin(-f * (float) (Math.PI / 180.0));
      return new Vector3f(f2 * f3, f4, h * f3);
   }

   public int renderBlockLayer(EnumWorldBlockLayer blockLayerIn, double partialTicks, int pass, Entity entityIn) {
      RenderHelper.disableStandardItemLighting();
      if (blockLayerIn == EnumWorldBlockLayer.TRANSLUCENT) {
         this.mc.mcProfiler.startSection("translucent_sort");
         double d = entityIn.posX - this.prevRenderSortX;
         double e = entityIn.posY - this.prevRenderSortY;
         double d2 = entityIn.posZ - this.prevRenderSortZ;
         if (d * d + e * e + d2 * d2 > 1.0) {
            this.prevRenderSortX = entityIn.posX;
            this.prevRenderSortY = entityIn.posY;
            this.prevRenderSortZ = entityIn.posZ;
            int i = 0;
            Iterator iterator2 = this.renderInfos.iterator();
            this.chunksToResortTransparency.clear();

            while (iterator2.hasNext()) {
               RenderGlobal.ContainerLocalRenderInformation containerLocalRenderInformation = (RenderGlobal.ContainerLocalRenderInformation)iterator2.next();
               if (containerLocalRenderInformation.renderChunk.compiledChunk.isLayerStarted(blockLayerIn) && i++ < 15) {
                  this.chunksToResortTransparency.add(containerLocalRenderInformation.renderChunk);
               }
            }
         }

         this.mc.mcProfiler.endSection();
      }

      this.mc.mcProfiler.startSection("filterempty");
      int j = 0;
      boolean bl = blockLayerIn == EnumWorldBlockLayer.TRANSLUCENT;
      int k = bl ? this.renderInfos.size() - 1 : 0;
      int l = bl ? -1 : this.renderInfos.size();
      int m = bl ? -1 : 1;

      for (int n = k; n != l; n += m) {
         RenderChunk renderChunk2 = this.renderInfos.get(n).renderChunk;
         if (!renderChunk2.getCompiledChunk().isLayerEmpty(blockLayerIn)) {
            j++;
            this.renderContainer.addRenderChunk(renderChunk2, blockLayerIn);
         }
      }

      if (j == 0) {
         this.mc.mcProfiler.endSection();
         return j;
      }

      if (Config.isFogOff() && this.mc.entityRenderer.fogStandard) {
         GlStateManager.disableFog();
      }

      this.mc.mcProfiler.endStartSection("render_" + blockLayerIn);
      this.renderBlockLayer(blockLayerIn);
      this.mc.mcProfiler.endSection();
      return j;
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void renderBlockLayer(EnumWorldBlockLayer blockLayerIn) {
      this.mc.entityRenderer.enableLightmap();
      if (OpenGlHelper.useVbo()) {
         GL11.glEnableClientState(32884);
         OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
         GL11.glEnableClientState(32888);
         OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
         GL11.glEnableClientState(32888);
         OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
         GL11.glEnableClientState(32886);
      }

      if (Config.isShaders()) {
         ShadersRender.preRenderChunkLayer(blockLayerIn);
      }

      this.renderContainer.renderChunkLayer(blockLayerIn);
      if (Config.isShaders()) {
         ShadersRender.postRenderChunkLayer(blockLayerIn);
      }

      if (OpenGlHelper.useVbo()) {
         for (VertexFormatElement vertexFormatElement : DefaultVertexFormats.BLOCK.getElements()) {
            VertexFormatElement.EnumUsage enumUsage = vertexFormatElement.getUsage();
            int i = vertexFormatElement.getIndex();
            switch (enumUsage) {
               case POSITION:
                  GL11.glDisableClientState(32884);
                  break;
               case UV:
                  OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit + i);
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

   private void cleanupDamagedBlocks(Iterator<DestroyBlockProgress> iteratorIn) {
      while (iteratorIn.hasNext()) {
         DestroyBlockProgress destroyBlockProgress = (DestroyBlockProgress)iteratorIn.next();
         int i = destroyBlockProgress.getCreationCloudUpdateTick();
         if (this.cloudTickCounter - i > 400) {
            iteratorIn.remove();
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
      if (Config.isSkyEnabled()) {
         GlStateManager.disableFog();
         GlStateManager.disableAlpha();
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
         RenderHelper.disableStandardItemLighting();
         GlStateManager.depthMask(false);
         this.renderEngine.bindTexture(locationEndSkyPng);
         Tessellator tessellator = Tessellator.getInstance();
         WorldRenderer worldRenderer = tessellator.getWorldRenderer();

         for (int i = 0; i < 6; i++) {
            GlStateManager.pushMatrix();
            if (i == 1) {
               GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            }

            if (i == 2) {
               GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
            }

            if (i == 3) {
               GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
            }

            if (i == 4) {
               GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
            }

            if (i == 5) {
               GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);
            }

            worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            worldRenderer.pos(-100.0, -100.0, -100.0).tex(0.0, 0.0).color(40, 40, 40, 255).endVertex();
            worldRenderer.pos(-100.0, -100.0, 100.0).tex(0.0, 16.0).color(40, 40, 40, 255).endVertex();
            worldRenderer.pos(100.0, -100.0, 100.0).tex(16.0, 16.0).color(40, 40, 40, 255).endVertex();
            worldRenderer.pos(100.0, -100.0, -100.0).tex(16.0, 0.0).color(40, 40, 40, 255).endVertex();
            tessellator.draw();
            GlStateManager.popMatrix();
         }

         GlStateManager.depthMask(true);
         GlStateManager.enableTexture2D();
         GlStateManager.enableAlpha();
      }
   }

   public void renderSky(float partialTicks, int pass) {
      if (Reflector.ForgeWorldProvider_getSkyRenderer.exists()) {
         WorldProvider worldProvider = this.mc.theWorld.provider;
         Object object = Reflector.call(worldProvider, Reflector.ForgeWorldProvider_getSkyRenderer);
         if (object != null) {
            Reflector.callVoid(object, Reflector.IRenderHandler_render, partialTicks, this.theWorld, this.mc);
            return;
         }
      }

      if (this.mc.theWorld.provider.getDimensionId() == 1) {
         this.renderSkyEnd();
      } else if (this.mc.theWorld.provider.isSurfaceWorld()) {
         GlStateManager.disableTexture2D();
         boolean bl = Config.isShaders();
         if (bl) {
            Shaders.disableTexture2D();
         }

         Vec3 vec3 = this.theWorld.getSkyColor(this.mc.getRenderViewEntity(), partialTicks);
         vec3 = CustomColors.getSkyColor(
            vec3, this.mc.theWorld, this.mc.getRenderViewEntity().posX, this.mc.getRenderViewEntity().posY + 1.0, this.mc.getRenderViewEntity().posZ
         );
         if (bl) {
            Shaders.setSkyColor(vec3);
         }

         float f = (float)vec3.xCoord;
         float g = (float)vec3.yCoord;
         float h = (float)vec3.zCoord;
         if (pass != 2) {
            float f2 = (f * 30.0F + g * 59.0F + h * 11.0F) / 100.0F;
            float f3 = (f * 30.0F + g * 70.0F) / 100.0F;
            float f4 = (f * 30.0F + h * 70.0F) / 100.0F;
            f = f2;
            g = f3;
            h = f4;
         }

         GlStateManager.color(f, g, h);
         Tessellator tessellator = Tessellator.getInstance();
         WorldRenderer worldRenderer = tessellator.getWorldRenderer();
         GlStateManager.depthMask(false);
         GlStateManager.enableFog();
         if (bl) {
            Shaders.enableFog();
         }

         GlStateManager.color(f, g, h);
         if (bl) {
            Shaders.preSkyList();
         }

         if (Config.isSkyEnabled()) {
            if (this.vboEnabled) {
               this.skyVBO.bindBuffer();
               GL11.glEnableClientState(32884);
               GL11.glVertexPointer(3, 5126, 12, 0L);
               this.skyVBO.drawArrays(7);
               this.skyVBO.unbindBuffer();
               GL11.glDisableClientState(32884);
            } else {
               GlStateManager.callList(this.glSkyList);
            }
         }

         GlStateManager.disableFog();
         if (bl) {
            Shaders.disableFog();
         }

         GlStateManager.disableAlpha();
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
         RenderHelper.disableStandardItemLighting();
         float[] floats = this.theWorld.provider.calcSunriseSunsetColors(this.theWorld.getCelestialAngle(partialTicks), partialTicks);
         if (floats != null && Config.isSunMoonEnabled()) {
            GlStateManager.disableTexture2D();
            if (bl) {
               Shaders.disableTexture2D();
            }

            GlStateManager.shadeModel(7425);
            GlStateManager.pushMatrix();
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(MathHelper.sin(this.theWorld.getCelestialAngleRadians(partialTicks)) < 0.0F ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
            float f5 = floats[0];
            float f6 = floats[1];
            float f7 = floats[2];
            if (pass != 2) {
               float f8 = (f5 * 30.0F + f6 * 59.0F + f7 * 11.0F) / 100.0F;
               float f9 = (f5 * 30.0F + f6 * 70.0F) / 100.0F;
               float f10 = (f5 * 30.0F + f7 * 70.0F) / 100.0F;
               f5 = f8;
               f6 = f9;
               f7 = f10;
            }

            worldRenderer.begin(6, DefaultVertexFormats.POSITION_COLOR);
            worldRenderer.pos(0.0, 100.0, 0.0).color(f5, f6, f7, floats[3]).endVertex();
            boolean bl2 = true;

            for (int i = 0; i <= 16; i++) {
               float f11 = i * (float) Math.PI * 2.0F / 16.0F;
               float f12 = MathHelper.sin(f11);
               float f13 = MathHelper.cos(f11);
               worldRenderer.pos(f12 * 120.0F, f13 * 120.0F, -f13 * 40.0F * floats[3]).color(floats[0], floats[1], floats[2], 0.0F).endVertex();
            }

            tessellator.draw();
            GlStateManager.popMatrix();
            GlStateManager.shadeModel(7424);
         }

         GlStateManager.enableTexture2D();
         if (bl) {
            Shaders.enableTexture2D();
         }

         GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0);
         GlStateManager.pushMatrix();
         float f14 = 1.0F - this.theWorld.getRainStrength(partialTicks);
         GlStateManager.color(1.0F, 1.0F, 1.0F, f14);
         GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
         CustomSky.renderSky(this.theWorld, this.renderEngine, this.theWorld.getCelestialAngle(partialTicks), f14);
         if (bl) {
            Shaders.preCelestialRotate();
         }

         GlStateManager.rotate(this.theWorld.getCelestialAngle(partialTicks) * 360.0F, 1.0F, 0.0F, 0.0F);
         if (bl) {
            Shaders.postCelestialRotate();
         }

         float f15 = 30.0F;
         if (Config.isSunTexture()) {
            this.renderEngine.bindTexture(locationSunPng);
            worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
            worldRenderer.pos(-f15, 100.0, -f15).tex(0.0, 0.0).endVertex();
            worldRenderer.pos(f15, 100.0, -f15).tex(1.0, 0.0).endVertex();
            worldRenderer.pos(f15, 100.0, f15).tex(1.0, 1.0).endVertex();
            worldRenderer.pos(-f15, 100.0, f15).tex(0.0, 1.0).endVertex();
            tessellator.draw();
         }

         f15 = 20.0F;
         if (Config.isMoonTexture()) {
            this.renderEngine.bindTexture(locationMoonPhasesPng);
            int j = this.theWorld.getMoonPhase();
            int k = j % 4;
            int l = j / 4 % 2;
            float f16 = (k + 0) / 4.0F;
            float f17 = (l + 0) / 2.0F;
            float f18 = (k + 1) / 4.0F;
            float f19 = (l + 1) / 2.0F;
            worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
            worldRenderer.pos(-f15, -100.0, f15).tex(f18, f19).endVertex();
            worldRenderer.pos(f15, -100.0, f15).tex(f16, f19).endVertex();
            worldRenderer.pos(f15, -100.0, -f15).tex(f16, f17).endVertex();
            worldRenderer.pos(-f15, -100.0, -f15).tex(f18, f17).endVertex();
            tessellator.draw();
         }

         GlStateManager.disableTexture2D();
         if (bl) {
            Shaders.disableTexture2D();
         }

         float f20 = this.theWorld.getStarBrightness(partialTicks) * f14;
         if (f20 > 0.0F && Config.isStarsEnabled() && !CustomSky.hasSkyLayers(this.theWorld)) {
            GlStateManager.color(f20, f20, f20, f20);
            if (this.vboEnabled) {
               this.starVBO.bindBuffer();
               GL11.glEnableClientState(32884);
               GL11.glVertexPointer(3, 5126, 12, 0L);
               this.starVBO.drawArrays(7);
               this.starVBO.unbindBuffer();
               GL11.glDisableClientState(32884);
            } else {
               GlStateManager.callList(this.starGLCallList);
            }
         }

         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.disableBlend();
         GlStateManager.enableAlpha();
         GlStateManager.enableFog();
         if (bl) {
            Shaders.enableFog();
         }

         GlStateManager.popMatrix();
         GlStateManager.disableTexture2D();
         if (bl) {
            Shaders.disableTexture2D();
         }

         GlStateManager.color(0.0F, 0.0F, 0.0F);
         double d = this.mc.thePlayer.getPositionEyes(partialTicks).yCoord - this.theWorld.getHorizon();
         if (d < 0.0) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, 12.0F, 0.0F);
            if (this.vboEnabled) {
               this.sky2VBO.bindBuffer();
               GL11.glEnableClientState(32884);
               GL11.glVertexPointer(3, 5126, 12, 0L);
               this.sky2VBO.drawArrays(7);
               this.sky2VBO.unbindBuffer();
               GL11.glDisableClientState(32884);
            } else {
               GlStateManager.callList(this.glSkyList2);
            }

            GlStateManager.popMatrix();
            float f21 = 1.0F;
            float f22 = -((float)(d + 65.0));
            float f23 = -1.0F;
            worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
            worldRenderer.pos(-1.0, f22, 1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer.pos(1.0, f22, 1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer.pos(1.0, -1.0, 1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer.pos(-1.0, -1.0, 1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer.pos(-1.0, -1.0, -1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer.pos(1.0, -1.0, -1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer.pos(1.0, f22, -1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer.pos(-1.0, f22, -1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer.pos(1.0, -1.0, -1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer.pos(1.0, -1.0, 1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer.pos(1.0, f22, 1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer.pos(1.0, f22, -1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer.pos(-1.0, f22, -1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer.pos(-1.0, f22, 1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer.pos(-1.0, -1.0, 1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer.pos(-1.0, -1.0, -1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer.pos(-1.0, -1.0, -1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer.pos(-1.0, -1.0, 1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer.pos(1.0, -1.0, 1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer.pos(1.0, -1.0, -1.0).color(0, 0, 0, 255).endVertex();
            tessellator.draw();
         }

         if (this.theWorld.provider.isSkyColored()) {
            GlStateManager.color(f * 0.2F + 0.04F, g * 0.2F + 0.04F, h * 0.6F + 0.1F);
         } else {
            GlStateManager.color(f, g, h);
         }

         if (this.mc.gameSettings.renderDistanceChunks <= 4) {
            GlStateManager.color(this.mc.entityRenderer.fogColorRed, this.mc.entityRenderer.fogColorGreen, this.mc.entityRenderer.fogColorBlue);
         }

         GlStateManager.pushMatrix();
         GlStateManager.translate(0.0F, -((float)(d - 16.0)), 0.0F);
         if (Config.isSkyEnabled()) {
            GlStateManager.callList(this.glSkyList2);
         }

         GlStateManager.popMatrix();
         GlStateManager.enableTexture2D();
         if (bl) {
            Shaders.enableTexture2D();
         }

         GlStateManager.depthMask(true);
      }
   }

   public void renderClouds(float partialTicks, int pass) {
      if (!Config.isCloudsOff()) {
         if (Reflector.ForgeWorldProvider_getCloudRenderer.exists()) {
            WorldProvider worldProvider = this.mc.theWorld.provider;
            Object object = Reflector.call(worldProvider, Reflector.ForgeWorldProvider_getCloudRenderer);
            if (object != null) {
               Reflector.callVoid(object, Reflector.IRenderHandler_render, partialTicks, this.theWorld, this.mc);
               return;
            }
         }

         if (this.mc.theWorld.provider.isSurfaceWorld()) {
            if (Config.isShaders()) {
               Shaders.beginClouds();
            }

            if (Config.isCloudsFancy()) {
               this.renderCloudsFancy(partialTicks, pass);
            } else {
               this.cloudRenderer.prepareToRender(false, this.cloudTickCounter, partialTicks);
               partialTicks = 0.0F;
               GlStateManager.disableCull();
               float f = (float)(
                  this.mc.getRenderViewEntity().lastTickPosY + (this.mc.getRenderViewEntity().posY - this.mc.getRenderViewEntity().lastTickPosY) * partialTicks
               );
               boolean bl = true;
               Tessellator tessellator = Tessellator.getInstance();
               WorldRenderer worldRenderer = tessellator.getWorldRenderer();
               this.renderEngine.bindTexture(locationCloudsPng);
               GlStateManager.enableBlend();
               GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
               if (this.cloudRenderer.shouldUpdateGlList()) {
                  this.cloudRenderer.startUpdateGlList();
                  Vec3 vec3 = this.theWorld.getCloudColour(partialTicks);
                  float g = (float)vec3.xCoord;
                  float h = (float)vec3.yCoord;
                  float f2 = (float)vec3.zCoord;
                  if (pass != 2) {
                     float f3 = (g * 30.0F + h * 59.0F + f2 * 11.0F) / 100.0F;
                     float f4 = (g * 30.0F + h * 70.0F) / 100.0F;
                     float f5 = (g * 30.0F + f2 * 70.0F) / 100.0F;
                     g = f3;
                     h = f4;
                     f2 = f5;
                  }

                  float f6 = 4.8828125E-4F;
                  double d = this.cloudTickCounter + partialTicks;
                  double e = this.mc.getRenderViewEntity().prevPosX
                     + (this.mc.getRenderViewEntity().posX - this.mc.getRenderViewEntity().prevPosX) * partialTicks
                     + d * 0.03F;
                  double d2 = this.mc.getRenderViewEntity().prevPosZ
                     + (this.mc.getRenderViewEntity().posZ - this.mc.getRenderViewEntity().prevPosZ) * partialTicks;
                  int i = MathHelper.floor_double(e / 2048.0);
                  int j = MathHelper.floor_double(d2 / 2048.0);
                  e -= i * 2048;
                  d2 -= j * 2048;
                  float f7 = this.theWorld.provider.getCloudHeight() - f + 0.33F;
                  f7 += this.mc.gameSettings.ofCloudsHeight * 128.0F;
                  float f8 = (float)(e * 4.8828125E-4);
                  float f9 = (float)(d2 * 4.8828125E-4);
                  worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);

                  for (int k = -256; k < 256; k += 32) {
                     for (int l = -256; l < 256; l += 32) {
                        worldRenderer.pos(k + 0, f7, l + 32)
                           .tex((k + 0) * 4.8828125E-4F + f8, (l + 32) * 4.8828125E-4F + f9)
                           .color(g, h, f2, 0.8F)
                           .endVertex();
                        worldRenderer.pos(k + 32, f7, l + 32)
                           .tex((k + 32) * 4.8828125E-4F + f8, (l + 32) * 4.8828125E-4F + f9)
                           .color(g, h, f2, 0.8F)
                           .endVertex();
                        worldRenderer.pos(k + 32, f7, l + 0)
                           .tex((k + 32) * 4.8828125E-4F + f8, (l + 0) * 4.8828125E-4F + f9)
                           .color(g, h, f2, 0.8F)
                           .endVertex();
                        worldRenderer.pos(k + 0, f7, l + 0)
                           .tex((k + 0) * 4.8828125E-4F + f8, (l + 0) * 4.8828125E-4F + f9)
                           .color(g, h, f2, 0.8F)
                           .endVertex();
                     }
                  }

                  tessellator.draw();
                  this.cloudRenderer.endUpdateGlList();
               }

               this.cloudRenderer.renderGlList();
               GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
               GlStateManager.disableBlend();
               GlStateManager.enableCull();
            }

            if (Config.isShaders()) {
               Shaders.endClouds();
            }
         }
      }
   }

   public boolean hasCloudFog(double x, double y, double z, float partialTicks) {
      return false;
   }

   private void renderCloudsFancy(float partialTicks, int pass) {
      this.cloudRenderer.prepareToRender(true, this.cloudTickCounter, partialTicks);
      partialTicks = 0.0F;
      GlStateManager.disableCull();
      float f = (float)(
         this.mc.getRenderViewEntity().lastTickPosY + (this.mc.getRenderViewEntity().posY - this.mc.getRenderViewEntity().lastTickPosY) * partialTicks
      );
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();
      double d = this.cloudTickCounter + partialTicks;
      double e = (
            this.mc.getRenderViewEntity().prevPosX
               + (this.mc.getRenderViewEntity().posX - this.mc.getRenderViewEntity().prevPosX) * partialTicks
               + d * 0.03F
         )
         / 12.0;
      double d2 = (this.mc.getRenderViewEntity().prevPosZ + (this.mc.getRenderViewEntity().posZ - this.mc.getRenderViewEntity().prevPosZ) * partialTicks)
            / 12.0
         + 0.33F;
      float g = this.theWorld.provider.getCloudHeight() - f + 0.33F;
      g += this.mc.gameSettings.ofCloudsHeight * 128.0F;
      int i = MathHelper.floor_double(e / 2048.0);
      int j = MathHelper.floor_double(d2 / 2048.0);
      e -= i * 2048;
      d2 -= j * 2048;
      this.renderEngine.bindTexture(locationCloudsPng);
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      Vec3 vec3 = this.theWorld.getCloudColour(partialTicks);
      float h = (float)vec3.xCoord;
      float f2 = (float)vec3.yCoord;
      float f3 = (float)vec3.zCoord;
      if (pass != 2) {
         float f4 = (h * 30.0F + f2 * 59.0F + f3 * 11.0F) / 100.0F;
         float f5 = (h * 30.0F + f2 * 70.0F) / 100.0F;
         float f6 = (h * 30.0F + f3 * 70.0F) / 100.0F;
         h = f4;
         f2 = f5;
         f3 = f6;
      }

      float f7 = h * 0.9F;
      float f8 = f2 * 0.9F;
      float f9 = f3 * 0.9F;
      float f10 = h * 0.7F;
      float f11 = f2 * 0.7F;
      float f12 = f3 * 0.7F;
      float f13 = h * 0.8F;
      float f14 = f2 * 0.8F;
      float f15 = f3 * 0.8F;
      float f16 = MathHelper.floor_double(e) * 0.00390625F;
      float f17 = MathHelper.floor_double(d2) * 0.00390625F;
      float f18 = (float)(e - MathHelper.floor_double(e));
      float f19 = (float)(d2 - MathHelper.floor_double(d2));
      GlStateManager.scale(12.0F, 1.0F, 12.0F);

      for (int k = 0; k < 2; k++) {
         if (k == 0) {
            GlStateManager.colorMask(false, false, false, false);
         } else {
            switch (pass) {
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

         for (int l = -3; l <= 4; l++) {
            for (int m = -3; m <= 4; m++) {
               worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
               float f20 = l * 8;
               float f21 = m * 8;
               float f22 = f20 - f18;
               float f23 = f21 - f19;
               if (g > -5.0F) {
                  worldRenderer.pos(f22 + 0.0F, g + 0.0F, f23 + 8.0F)
                     .tex((f20 + 0.0F) * 0.00390625F + f16, (f21 + 8.0F) * 0.00390625F + f17)
                     .color(f10, f11, f12, 0.8F)
                     .normal(0.0F, -1.0F, 0.0F)
                     .endVertex();
                  worldRenderer.pos(f22 + 8.0F, g + 0.0F, f23 + 8.0F)
                     .tex((f20 + 8.0F) * 0.00390625F + f16, (f21 + 8.0F) * 0.00390625F + f17)
                     .color(f10, f11, f12, 0.8F)
                     .normal(0.0F, -1.0F, 0.0F)
                     .endVertex();
                  worldRenderer.pos(f22 + 8.0F, g + 0.0F, f23 + 0.0F)
                     .tex((f20 + 8.0F) * 0.00390625F + f16, (f21 + 0.0F) * 0.00390625F + f17)
                     .color(f10, f11, f12, 0.8F)
                     .normal(0.0F, -1.0F, 0.0F)
                     .endVertex();
                  worldRenderer.pos(f22 + 0.0F, g + 0.0F, f23 + 0.0F)
                     .tex((f20 + 0.0F) * 0.00390625F + f16, (f21 + 0.0F) * 0.00390625F + f17)
                     .color(f10, f11, f12, 0.8F)
                     .normal(0.0F, -1.0F, 0.0F)
                     .endVertex();
               }

               if (g <= 5.0F) {
                  worldRenderer.pos(f22 + 0.0F, g + 4.0F - 9.765625E-4F, f23 + 8.0F)
                     .tex((f20 + 0.0F) * 0.00390625F + f16, (f21 + 8.0F) * 0.00390625F + f17)
                     .color(h, f2, f3, 0.8F)
                     .normal(0.0F, 1.0F, 0.0F)
                     .endVertex();
                  worldRenderer.pos(f22 + 8.0F, g + 4.0F - 9.765625E-4F, f23 + 8.0F)
                     .tex((f20 + 8.0F) * 0.00390625F + f16, (f21 + 8.0F) * 0.00390625F + f17)
                     .color(h, f2, f3, 0.8F)
                     .normal(0.0F, 1.0F, 0.0F)
                     .endVertex();
                  worldRenderer.pos(f22 + 8.0F, g + 4.0F - 9.765625E-4F, f23 + 0.0F)
                     .tex((f20 + 8.0F) * 0.00390625F + f16, (f21 + 0.0F) * 0.00390625F + f17)
                     .color(h, f2, f3, 0.8F)
                     .normal(0.0F, 1.0F, 0.0F)
                     .endVertex();
                  worldRenderer.pos(f22 + 0.0F, g + 4.0F - 9.765625E-4F, f23 + 0.0F)
                     .tex((f20 + 0.0F) * 0.00390625F + f16, (f21 + 0.0F) * 0.00390625F + f17)
                     .color(h, f2, f3, 0.8F)
                     .normal(0.0F, 1.0F, 0.0F)
                     .endVertex();
               }

               if (l > -1) {
                  for (int n = 0; n < 8; n++) {
                     worldRenderer.pos(f22 + n + 0.0F, g + 0.0F, f23 + 8.0F)
                        .tex((f20 + n + 0.5F) * 0.00390625F + f16, (f21 + 8.0F) * 0.00390625F + f17)
                        .color(f7, f8, f9, 0.8F)
                        .normal(-1.0F, 0.0F, 0.0F)
                        .endVertex();
                     worldRenderer.pos(f22 + n + 0.0F, g + 4.0F, f23 + 8.0F)
                        .tex((f20 + n + 0.5F) * 0.00390625F + f16, (f21 + 8.0F) * 0.00390625F + f17)
                        .color(f7, f8, f9, 0.8F)
                        .normal(-1.0F, 0.0F, 0.0F)
                        .endVertex();
                     worldRenderer.pos(f22 + n + 0.0F, g + 4.0F, f23 + 0.0F)
                        .tex((f20 + n + 0.5F) * 0.00390625F + f16, (f21 + 0.0F) * 0.00390625F + f17)
                        .color(f7, f8, f9, 0.8F)
                        .normal(-1.0F, 0.0F, 0.0F)
                        .endVertex();
                     worldRenderer.pos(f22 + n + 0.0F, g + 0.0F, f23 + 0.0F)
                        .tex((f20 + n + 0.5F) * 0.00390625F + f16, (f21 + 0.0F) * 0.00390625F + f17)
                        .color(f7, f8, f9, 0.8F)
                        .normal(-1.0F, 0.0F, 0.0F)
                        .endVertex();
                  }
               }

               if (l <= 1) {
                  for (int i2 = 0; i2 < 8; i2++) {
                     worldRenderer.pos(f22 + i2 + 1.0F - 9.765625E-4F, g + 0.0F, f23 + 8.0F)
                        .tex((f20 + i2 + 0.5F) * 0.00390625F + f16, (f21 + 8.0F) * 0.00390625F + f17)
                        .color(f7, f8, f9, 0.8F)
                        .normal(1.0F, 0.0F, 0.0F)
                        .endVertex();
                     worldRenderer.pos(f22 + i2 + 1.0F - 9.765625E-4F, g + 4.0F, f23 + 8.0F)
                        .tex((f20 + i2 + 0.5F) * 0.00390625F + f16, (f21 + 8.0F) * 0.00390625F + f17)
                        .color(f7, f8, f9, 0.8F)
                        .normal(1.0F, 0.0F, 0.0F)
                        .endVertex();
                     worldRenderer.pos(f22 + i2 + 1.0F - 9.765625E-4F, g + 4.0F, f23 + 0.0F)
                        .tex((f20 + i2 + 0.5F) * 0.00390625F + f16, (f21 + 0.0F) * 0.00390625F + f17)
                        .color(f7, f8, f9, 0.8F)
                        .normal(1.0F, 0.0F, 0.0F)
                        .endVertex();
                     worldRenderer.pos(f22 + i2 + 1.0F - 9.765625E-4F, g + 0.0F, f23 + 0.0F)
                        .tex((f20 + i2 + 0.5F) * 0.00390625F + f16, (f21 + 0.0F) * 0.00390625F + f17)
                        .color(f7, f8, f9, 0.8F)
                        .normal(1.0F, 0.0F, 0.0F)
                        .endVertex();
                  }
               }

               if (m > -1) {
                  for (int i3 = 0; i3 < 8; i3++) {
                     worldRenderer.pos(f22 + 0.0F, g + 4.0F, f23 + i3 + 0.0F)
                        .tex((f20 + 0.0F) * 0.00390625F + f16, (f21 + i3 + 0.5F) * 0.00390625F + f17)
                        .color(f13, f14, f15, 0.8F)
                        .normal(0.0F, 0.0F, -1.0F)
                        .endVertex();
                     worldRenderer.pos(f22 + 8.0F, g + 4.0F, f23 + i3 + 0.0F)
                        .tex((f20 + 8.0F) * 0.00390625F + f16, (f21 + i3 + 0.5F) * 0.00390625F + f17)
                        .color(f13, f14, f15, 0.8F)
                        .normal(0.0F, 0.0F, -1.0F)
                        .endVertex();
                     worldRenderer.pos(f22 + 8.0F, g + 0.0F, f23 + i3 + 0.0F)
                        .tex((f20 + 8.0F) * 0.00390625F + f16, (f21 + i3 + 0.5F) * 0.00390625F + f17)
                        .color(f13, f14, f15, 0.8F)
                        .normal(0.0F, 0.0F, -1.0F)
                        .endVertex();
                     worldRenderer.pos(f22 + 0.0F, g + 0.0F, f23 + i3 + 0.0F)
                        .tex((f20 + 0.0F) * 0.00390625F + f16, (f21 + i3 + 0.5F) * 0.00390625F + f17)
                        .color(f13, f14, f15, 0.8F)
                        .normal(0.0F, 0.0F, -1.0F)
                        .endVertex();
                  }
               }

               if (m <= 1) {
                  for (int i4 = 0; i4 < 8; i4++) {
                     worldRenderer.pos(f22 + 0.0F, g + 4.0F, f23 + i4 + 1.0F - 9.765625E-4F)
                        .tex((f20 + 0.0F) * 0.00390625F + f16, (f21 + i4 + 0.5F) * 0.00390625F + f17)
                        .color(f13, f14, f15, 0.8F)
                        .normal(0.0F, 0.0F, 1.0F)
                        .endVertex();
                     worldRenderer.pos(f22 + 8.0F, g + 4.0F, f23 + i4 + 1.0F - 9.765625E-4F)
                        .tex((f20 + 8.0F) * 0.00390625F + f16, (f21 + i4 + 0.5F) * 0.00390625F + f17)
                        .color(f13, f14, f15, 0.8F)
                        .normal(0.0F, 0.0F, 1.0F)
                        .endVertex();
                     worldRenderer.pos(f22 + 8.0F, g + 0.0F, f23 + i4 + 1.0F - 9.765625E-4F)
                        .tex((f20 + 8.0F) * 0.00390625F + f16, (f21 + i4 + 0.5F) * 0.00390625F + f17)
                        .color(f13, f14, f15, 0.8F)
                        .normal(0.0F, 0.0F, 1.0F)
                        .endVertex();
                     worldRenderer.pos(f22 + 0.0F, g + 0.0F, f23 + i4 + 1.0F - 9.765625E-4F)
                        .tex((f20 + 0.0F) * 0.00390625F + f16, (f21 + i4 + 0.5F) * 0.00390625F + f17)
                        .color(f13, f14, f15, 0.8F)
                        .normal(0.0F, 0.0F, 1.0F)
                        .endVertex();
                  }
               }

               tessellator.draw();
            }
         }

         this.cloudRenderer.endUpdateGlList();
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableBlend();
      GlStateManager.enableCull();
   }

   public void updateChunks(long finishTimeNano) {
      finishTimeNano = (long)(finishTimeNano + 1.0E8);
      this.displayListEntitiesDirty = this.displayListEntitiesDirty | this.renderDispatcher.runChunkUploads(finishTimeNano);
      if (this.chunksToUpdateForced.size() > 0) {
         Iterator iterator2 = this.chunksToUpdateForced.iterator();

         while (iterator2.hasNext()) {
            RenderChunk renderChunk = (RenderChunk)iterator2.next();
            if (!this.renderDispatcher.updateChunkLater(renderChunk)) {
               break;
            }

            renderChunk.setNeedsUpdate(false);
            iterator2.remove();
            this.chunksToUpdate.remove(renderChunk);
            this.chunksToResortTransparency.remove(renderChunk);
         }
      }

      if (this.chunksToResortTransparency.size() > 0) {
         Iterator iterator3 = this.chunksToResortTransparency.iterator();
         if (iterator3.hasNext()) {
            RenderChunk renderChunk2 = (RenderChunk)iterator3.next();
            if (this.renderDispatcher.updateTransparencyLater(renderChunk2)) {
               iterator3.remove();
            }
         }
      }

      int i = 0;
      int j = Config.getUpdatesPerFrame();
      int k = j * 2;
      Iterator iterator4 = this.chunksToUpdate.iterator();

      while (iterator4.hasNext()) {
         RenderChunk renderChunk3 = (RenderChunk)iterator4.next();
         if (!this.renderDispatcher.updateChunkLater(renderChunk3)) {
            break;
         }

         renderChunk3.setNeedsUpdate(false);
         iterator4.remove();
         if (renderChunk3.getCompiledChunk().isEmpty() && j < k) {
            j++;
         }

         if (++i >= j) {
            break;
         }
      }
   }

   public void renderWorldBorder(Entity entityIn, float partialTicks) {
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();
      WorldBorder worldBorder = this.theWorld.getWorldBorder();
      double d = this.mc.gameSettings.renderDistanceChunks * 16;
      if (entityIn.posX >= worldBorder.maxX() - d
         || entityIn.posX <= worldBorder.minX() + d
         || entityIn.posZ >= worldBorder.maxZ() - d
         || entityIn.posZ <= worldBorder.minZ() + d) {
         double e = 1.0 - worldBorder.getClosestDistance(entityIn) / d;
         e = Math.pow(e, 4.0);
         double d2 = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * partialTicks;
         double d3 = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * partialTicks;
         double d4 = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * partialTicks;
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0);
         this.renderEngine.bindTexture(locationForcefieldPng);
         GlStateManager.depthMask(false);
         GlStateManager.pushMatrix();
         int i = worldBorder.getStatus().getID();
         float f = (i >> 16 & 0xFF) / 255.0F;
         float g = (i >> 8 & 0xFF) / 255.0F;
         float h = (i & 0xFF) / 255.0F;
         GlStateManager.color(f, g, h, (float)e);
         GlStateManager.doPolygonOffset(-3.0F, -3.0F);
         GlStateManager.enablePolygonOffset();
         GlStateManager.alphaFunc(516, 0.1F);
         GlStateManager.enableAlpha();
         GlStateManager.disableCull();
         float f2 = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F;
         worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
         worldRenderer.setTranslation(-d2, -d3, -d4);
         double d5 = Math.max(MathHelper.floor_double(d4 - d), worldBorder.minZ());
         double d6 = Math.min(MathHelper.ceiling_double_int(d4 + d), worldBorder.maxZ());
         if (d2 > worldBorder.maxX() - d) {
            float f3 = 0.0F;

            for (double d7 = d5; d7 < d6; f3 += 0.5F) {
               double d8 = Math.min(1.0, d6 - d7);
               float f4 = (float)d8 * 0.5F;
               worldRenderer.pos(worldBorder.maxX(), 256.0, d7).tex(f2 + f3, f2 + 0.0F).endVertex();
               worldRenderer.pos(worldBorder.maxX(), 256.0, d7 + d8).tex(f2 + f4 + f3, f2 + 0.0F).endVertex();
               worldRenderer.pos(worldBorder.maxX(), 0.0, d7 + d8).tex(f2 + f4 + f3, f2 + 128.0F).endVertex();
               worldRenderer.pos(worldBorder.maxX(), 0.0, d7).tex(f2 + f3, f2 + 128.0F).endVertex();
               d7++;
            }
         }

         if (d2 < worldBorder.minX() + d) {
            float f5 = 0.0F;

            for (double d9 = d5; d9 < d6; f5 += 0.5F) {
               double d10 = Math.min(1.0, d6 - d9);
               float f6 = (float)d10 * 0.5F;
               worldRenderer.pos(worldBorder.minX(), 256.0, d9).tex(f2 + f5, f2 + 0.0F).endVertex();
               worldRenderer.pos(worldBorder.minX(), 256.0, d9 + d10).tex(f2 + f6 + f5, f2 + 0.0F).endVertex();
               worldRenderer.pos(worldBorder.minX(), 0.0, d9 + d10).tex(f2 + f6 + f5, f2 + 128.0F).endVertex();
               worldRenderer.pos(worldBorder.minX(), 0.0, d9).tex(f2 + f5, f2 + 128.0F).endVertex();
               d9++;
            }
         }

         d5 = Math.max(MathHelper.floor_double(d2 - d), worldBorder.minX());
         d6 = Math.min(MathHelper.ceiling_double_int(d2 + d), worldBorder.maxX());
         if (d4 > worldBorder.maxZ() - d) {
            float f7 = 0.0F;

            for (double d11 = d5; d11 < d6; f7 += 0.5F) {
               double d12 = Math.min(1.0, d6 - d11);
               float f8 = (float)d12 * 0.5F;
               worldRenderer.pos(d11, 256.0, worldBorder.maxZ()).tex(f2 + f7, f2 + 0.0F).endVertex();
               worldRenderer.pos(d11 + d12, 256.0, worldBorder.maxZ()).tex(f2 + f8 + f7, f2 + 0.0F).endVertex();
               worldRenderer.pos(d11 + d12, 0.0, worldBorder.maxZ()).tex(f2 + f8 + f7, f2 + 128.0F).endVertex();
               worldRenderer.pos(d11, 0.0, worldBorder.maxZ()).tex(f2 + f7, f2 + 128.0F).endVertex();
               d11++;
            }
         }

         if (d4 < worldBorder.minZ() + d) {
            float f9 = 0.0F;

            for (double d13 = d5; d13 < d6; f9 += 0.5F) {
               double d14 = Math.min(1.0, d6 - d13);
               float f10 = (float)d14 * 0.5F;
               worldRenderer.pos(d13, 256.0, worldBorder.minZ()).tex(f2 + f9, f2 + 0.0F).endVertex();
               worldRenderer.pos(d13 + d14, 256.0, worldBorder.minZ()).tex(f2 + f10 + f9, f2 + 0.0F).endVertex();
               worldRenderer.pos(d13 + d14, 0.0, worldBorder.minZ()).tex(f2 + f10 + f9, f2 + 128.0F).endVertex();
               worldRenderer.pos(d13, 0.0, worldBorder.minZ()).tex(f2 + f9, f2 + 128.0F).endVertex();
               d13++;
            }
         }

         tessellator.draw();
         worldRenderer.setTranslation(0.0, 0.0, 0.0);
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
         ShadersRender.endBlockDamage();
      }
   }

   public void drawBlockDamageTexture(Tessellator tessellatorIn, WorldRenderer worldRendererIn, Entity entityIn, float partialTicks) {
      double d = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * partialTicks;
      double e = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * partialTicks;
      double d2 = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * partialTicks;
      if (!this.damagedBlocks.isEmpty()) {
         this.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
         this.preRenderDamagedBlocks();
         worldRendererIn.begin(7, DefaultVertexFormats.BLOCK);
         worldRendererIn.setTranslation(-d, -e, -d2);
         worldRendererIn.noColor();
         Iterator iterator2 = this.damagedBlocks.values().iterator();

         while (iterator2.hasNext()) {
            DestroyBlockProgress destroyBlockProgress = (DestroyBlockProgress)iterator2.next();
            BlockPos blockPos = destroyBlockProgress.getPosition();
            double d3 = blockPos.getX() - d;
            double d4 = blockPos.getY() - e;
            double d5 = blockPos.getZ() - d2;
            Block block = this.theWorld.getBlockState(blockPos).getBlock();
            boolean bl;
            if (Reflector.ForgeTileEntity_canRenderBreaking.exists()) {
               boolean bl2 = block instanceof BlockChest || block instanceof BlockEnderChest || block instanceof BlockSign || block instanceof BlockSkull;
               if (!bl2) {
                  TileEntity tileEntity = this.theWorld.getTileEntity(blockPos);
                  if (tileEntity != null) {
                     bl2 = Reflector.callBoolean(tileEntity, Reflector.ForgeTileEntity_canRenderBreaking);
                  }
               }

               bl = !bl2;
            } else {
               bl = !(block instanceof BlockChest) && !(block instanceof BlockEnderChest) && !(block instanceof BlockSign) && !(block instanceof BlockSkull);
            }

            if (bl) {
               if (d3 * d3 + d4 * d4 + d5 * d5 > 1024.0) {
                  iterator2.remove();
               } else {
                  IBlockState iBlockState = this.theWorld.getBlockState(blockPos);
                  if (iBlockState.getBlock().getMaterial() != Material.air) {
                     int i = destroyBlockProgress.getPartialBlockDamage();
                     TextureAtlasSprite textureAtlasSprite = this.destroyBlockIcons[i];
                     BlockRendererDispatcher blockRendererDispatcher = this.mc.getBlockRendererDispatcher();
                     blockRendererDispatcher.renderBlockDamage(iBlockState, blockPos, textureAtlasSprite, this.theWorld);
                  }
               }
            }
         }

         tessellatorIn.draw();
         worldRendererIn.setTranslation(0.0, 0.0, 0.0);
         this.postRenderDamagedBlocks();
      }
   }

   public void drawSelectionBox(EntityPlayer player, MovingObjectPosition movingObjectPositionIn, int execute, float partialTicks) {
      if (execute == 0 && movingObjectPositionIn.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
         GlStateManager.color(0.0F, 0.0F, 0.0F, 0.4F);
         GL11.glLineWidth(2.0F);
         GlStateManager.disableTexture2D();
         if (Config.isShaders()) {
            Shaders.disableTexture2D();
         }

         GlStateManager.depthMask(false);
         BlockPos blockPos = movingObjectPositionIn.getBlockPos();
         Block block = this.theWorld.getBlockState(blockPos).getBlock();
         if (block.getMaterial() != Material.air && this.theWorld.getWorldBorder().contains(blockPos)) {
            block.setBlockBoundsBasedOnState(this.theWorld, blockPos);
            double d = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
            double e = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
            double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
            drawSelectionBoundingBox(block.getSelectedBoundingBox(this.theWorld, blockPos).expand(0.002F, 0.002F, 0.002F).offset(-d, -e, -d2));
         }

         GlStateManager.depthMask(true);
         GlStateManager.enableTexture2D();
         if (Config.isShaders()) {
            Shaders.enableTexture2D();
         }

         GlStateManager.disableBlend();
      }
   }

   public static void drawSelectionBoundingBox(AxisAlignedBB boundingBox) {
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();
      worldRenderer.begin(3, DefaultVertexFormats.POSITION);
      worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
      tessellator.draw();
      worldRenderer.begin(3, DefaultVertexFormats.POSITION);
      worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
      tessellator.draw();
      worldRenderer.begin(1, DefaultVertexFormats.POSITION);
      worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex();
      tessellator.draw();
   }

   public static void drawOutlinedBoundingBox(AxisAlignedBB boundingBox, int red, int green, int blue, int alpha) {
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();
      worldRenderer.begin(3, DefaultVertexFormats.POSITION_COLOR);
      worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).color(red, green, blue, alpha).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).color(red, green, blue, alpha).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).color(red, green, blue, alpha).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).color(red, green, blue, alpha).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).color(red, green, blue, alpha).endVertex();
      tessellator.draw();
      worldRenderer.begin(3, DefaultVertexFormats.POSITION_COLOR);
      worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).color(red, green, blue, alpha).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).color(red, green, blue, alpha).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).color(red, green, blue, alpha).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).color(red, green, blue, alpha).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).color(red, green, blue, alpha).endVertex();
      tessellator.draw();
      worldRenderer.begin(1, DefaultVertexFormats.POSITION_COLOR);
      worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).color(red, green, blue, alpha).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).color(red, green, blue, alpha).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).color(red, green, blue, alpha).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).color(red, green, blue, alpha).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).color(red, green, blue, alpha).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).color(red, green, blue, alpha).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).color(red, green, blue, alpha).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).color(red, green, blue, alpha).endVertex();
      tessellator.draw();
   }

   private void markBlocksForUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {
      this.viewFrustum.markBlocksForUpdate(x1, y1, z1, x2, y2, z2);
   }

   @Override
   public void markBlockForUpdate(BlockPos pos) {
      int i = pos.getX();
      int j = pos.getY();
      int k = pos.getZ();
      this.markBlocksForUpdate(i - 1, j - 1, k - 1, i + 1, j + 1, k + 1);
   }

   @Override
   public void notifyLightSet(BlockPos pos) {
      int i = pos.getX();
      int j = pos.getY();
      int k = pos.getZ();
      this.markBlocksForUpdate(i - 1, j - 1, k - 1, i + 1, j + 1, k + 1);
   }

   @Override
   public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {
      this.markBlocksForUpdate(x1 - 1, y1 - 1, z1 - 1, x2 + 1, y2 + 1, z2 + 1);
   }

   @Override
   public void playRecord(String recordName, BlockPos blockPosIn) {
      ISound iSound = this.mapSoundPositions.get(blockPosIn);
      if (iSound != null) {
         this.mc.getSoundHandler().stopSound(iSound);
         this.mapSoundPositions.remove(blockPosIn);
      }

      if (recordName != null) {
         ItemRecord itemRecord = ItemRecord.getRecord(recordName);
         if (itemRecord != null) {
            this.mc.ingameGUI.setRecordPlayingMessage(itemRecord.getRecordNameLocal());
         }

         ResourceLocation resourceLocation = null;
         if (Reflector.ForgeItemRecord_getRecordResource.exists() && itemRecord != null) {
            resourceLocation = (ResourceLocation)Reflector.call(itemRecord, Reflector.ForgeItemRecord_getRecordResource, recordName);
         }

         if (resourceLocation == null) {
            resourceLocation = new ResourceLocation(recordName);
         }

         PositionedSoundRecord positionedSoundRecord = PositionedSoundRecord.create(resourceLocation, blockPosIn.getX(), blockPosIn.getY(), blockPosIn.getZ());
         this.mapSoundPositions.put(blockPosIn, positionedSoundRecord);
         this.mc.getSoundHandler().playSound(positionedSoundRecord);
      }
   }

   @Override
   public void playSound(String soundName, double x, double y, double z, float volume, float pitch) {
   }

   @Override
   public void playSoundToNearExcept(EntityPlayer except, String soundName, double x, double y, double z, float volume, float pitch) {
   }

   @Override
   public void spawnParticle(
      int particleID,
      boolean ignoreRange,
      final double xCoord,
      final double yCoord,
      final double zCoord,
      double xOffset,
      double yOffset,
      double zOffset,
      int... parameters
   ) {
      try {
         this.spawnEntityFX(particleID, ignoreRange, xCoord, yCoord, zCoord, xOffset, yOffset, zOffset, parameters);
      } catch (Throwable throwable) {
         CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Exception while adding particle");
         CrashReportCategory crashReportCategory = crashReport.makeCategory("Particle being added");
         crashReportCategory.addCrashSection("ID", particleID);
         if (parameters != null) {
            crashReportCategory.addCrashSection("Parameters", parameters);
         }

         crashReportCategory.addCrashSectionCallable("Position", new Callable<String>() {
            public String call() throws Exception {
               return CrashReportCategory.getCoordinateInfo(xCoord, yCoord, zCoord);
            }
         });
         throw new ReportedException(crashReport);
      }
   }

   private void spawnParticle(
      EnumParticleTypes particleIn, double xCoord, double yCoord, double zCoord, double xOffset, double yOffset, double zOffset, int... parameters
   ) {
      this.spawnParticle(particleIn.getParticleID(), particleIn.getShouldIgnoreRange(), xCoord, yCoord, zCoord, xOffset, yOffset, zOffset, parameters);
   }

   private EntityFX spawnEntityFX(
      int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xOffset, double yOffset, double zOffset, int... parameters
   ) {
      if (this.mc != null && this.mc.getRenderViewEntity() != null && this.mc.effectRenderer != null) {
         int i = this.mc.gameSettings.particleSetting;
         if (i == 1 && this.theWorld.rand.nextInt(3) == 0) {
            i = 2;
         }

         double d = this.mc.getRenderViewEntity().posX - xCoord;
         double e = this.mc.getRenderViewEntity().posY - yCoord;
         double d2 = this.mc.getRenderViewEntity().posZ - zCoord;
         if (particleID == EnumParticleTypes.EXPLOSION_HUGE.getParticleID() && !Config.isAnimatedExplosion()) {
            return null;
         }

         if (particleID == EnumParticleTypes.EXPLOSION_LARGE.getParticleID() && !Config.isAnimatedExplosion()) {
            return null;
         }

         if (particleID == EnumParticleTypes.EXPLOSION_NORMAL.getParticleID() && !Config.isAnimatedExplosion()) {
            return null;
         }

         if (particleID == EnumParticleTypes.SUSPENDED.getParticleID() && !Config.isWaterParticles()) {
            return null;
         }

         if (particleID == EnumParticleTypes.SUSPENDED_DEPTH.getParticleID() && !Config.isVoidParticles()) {
            return null;
         }

         if (particleID == EnumParticleTypes.SMOKE_NORMAL.getParticleID() && !Config.isAnimatedSmoke()) {
            return null;
         }

         if (particleID == EnumParticleTypes.SMOKE_LARGE.getParticleID() && !Config.isAnimatedSmoke()) {
            return null;
         }

         if (particleID == EnumParticleTypes.SPELL_MOB.getParticleID() && !Config.isPotionParticles()) {
            return null;
         }

         if (particleID == EnumParticleTypes.SPELL_MOB_AMBIENT.getParticleID() && !Config.isPotionParticles()) {
            return null;
         }

         if (particleID == EnumParticleTypes.SPELL.getParticleID() && !Config.isPotionParticles()) {
            return null;
         }

         if (particleID == EnumParticleTypes.SPELL_INSTANT.getParticleID() && !Config.isPotionParticles()) {
            return null;
         }

         if (particleID == EnumParticleTypes.SPELL_WITCH.getParticleID() && !Config.isPotionParticles()) {
            return null;
         }

         if (particleID == EnumParticleTypes.PORTAL.getParticleID() && !Config.isAnimatedPortal()) {
            return null;
         }

         if (particleID == EnumParticleTypes.FLAME.getParticleID() && !Config.isAnimatedFlame()) {
            return null;
         }

         if (particleID == EnumParticleTypes.REDSTONE.getParticleID() && !Config.isAnimatedRedstone()) {
            return null;
         }

         if (particleID == EnumParticleTypes.DRIP_WATER.getParticleID() && !Config.isDrippingWaterLava()) {
            return null;
         }

         if (particleID == EnumParticleTypes.DRIP_LAVA.getParticleID() && !Config.isDrippingWaterLava()) {
            return null;
         }

         if (particleID == EnumParticleTypes.FIREWORKS_SPARK.getParticleID() && !Config.isFireworkParticles()) {
            return null;
         }

         if (ignoreRange) {
            return this.mc.effectRenderer.spawnEffectParticle(particleID, xCoord, yCoord, zCoord, xOffset, yOffset, zOffset, parameters);
         }

         double d3 = 256.0;
         if (particleID == EnumParticleTypes.CRIT.getParticleID()) {
            d3 = 38416.0;
         }

         if (d * d + e * e + d2 * d2 > d3) {
            return null;
         }

         if (i > 1) {
            return null;
         }

         EntityFX entityFX = this.mc.effectRenderer.spawnEffectParticle(particleID, xCoord, yCoord, zCoord, xOffset, yOffset, zOffset, parameters);
         if (particleID == EnumParticleTypes.WATER_BUBBLE.getParticleID()) {
            CustomColors.updateWaterFX(entityFX, this.theWorld, xCoord, yCoord, zCoord);
         }

         if (particleID == EnumParticleTypes.WATER_SPLASH.getParticleID()) {
            CustomColors.updateWaterFX(entityFX, this.theWorld, xCoord, yCoord, zCoord);
         }

         if (particleID == EnumParticleTypes.WATER_DROP.getParticleID()) {
            CustomColors.updateWaterFX(entityFX, this.theWorld, xCoord, yCoord, zCoord);
         }

         if (particleID == EnumParticleTypes.TOWN_AURA.getParticleID()) {
            CustomColors.updateMyceliumFX(entityFX);
         }

         if (particleID == EnumParticleTypes.PORTAL.getParticleID()) {
            CustomColors.updatePortalFX(entityFX);
         }

         if (particleID == EnumParticleTypes.REDSTONE.getParticleID()) {
            CustomColors.updateReddustFX(entityFX, this.theWorld, xCoord, yCoord, zCoord);
         }

         return entityFX;
      } else {
         return null;
      }
   }

   @Override
   public void onEntityAdded(Entity entityIn) {
      RandomMobs.entityLoaded(entityIn, this.theWorld);
      if (Config.isDynamicLights()) {
         DynamicLights.entityAdded(entityIn, this);
      }
   }

   @Override
   public void onEntityRemoved(Entity entityIn) {
      if (Config.isDynamicLights()) {
         DynamicLights.entityRemoved(entityIn, this);
      }
   }

   public void deleteAllDisplayLists() {
   }

   @Override
   public void broadcastSound(int soundID, BlockPos pos, int data) {
      switch (soundID) {
         case 1013:
         case 1018:
            if (this.mc.getRenderViewEntity() != null) {
               double d = pos.getX() - this.mc.getRenderViewEntity().posX;
               double e = pos.getY() - this.mc.getRenderViewEntity().posY;
               double d2 = pos.getZ() - this.mc.getRenderViewEntity().posZ;
               double d3 = Math.sqrt(d * d + e * e + d2 * d2);
               double d4 = this.mc.getRenderViewEntity().posX;
               double d5 = this.mc.getRenderViewEntity().posY;
               double d6 = this.mc.getRenderViewEntity().posZ;
               if (d3 > 0.0) {
                  d4 += d / d3 * 2.0;
                  d5 += e / d3 * 2.0;
                  d6 += d2 / d3 * 2.0;
               }

               if (soundID == 1013) {
                  this.theWorld.playSound(d4, d5, d6, "mob.wither.spawn", 1.0F, 1.0F, false);
               } else {
                  this.theWorld.playSound(d4, d5, d6, "mob.enderdragon.end", 5.0F, 1.0F, false);
               }
            }
      }
   }

   @Override
   public void playAuxSFX(EntityPlayer player, int sfxType, BlockPos blockPosIn, int data) {
      Random random2 = this.theWorld.rand;
      switch (sfxType) {
         case 1000:
            this.theWorld.playSoundAtPos(blockPosIn, "random.click", 1.0F, 1.0F, false);
            break;
         case 1001:
            this.theWorld.playSoundAtPos(blockPosIn, "random.click", 1.0F, 1.2F, false);
            break;
         case 1002:
            this.theWorld.playSoundAtPos(blockPosIn, "random.bow", 1.0F, 1.2F, false);
            break;
         case 1003:
            this.theWorld.playSoundAtPos(blockPosIn, "random.door_open", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 1004:
            this.theWorld.playSoundAtPos(blockPosIn, "random.fizz", 0.5F, 2.6F + (random2.nextFloat() - random2.nextFloat()) * 0.8F, false);
            break;
         case 1005:
            if (Item.getItemById(data) instanceof ItemRecord) {
               this.theWorld.playRecord(blockPosIn, "records." + ((ItemRecord)Item.getItemById(data)).recordName);
            } else {
               this.theWorld.playRecord(blockPosIn, (String)null);
            }
            break;
         case 1006:
            this.theWorld.playSoundAtPos(blockPosIn, "random.door_close", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 1007:
            this.theWorld.playSoundAtPos(blockPosIn, "mob.ghast.charge", 10.0F, (random2.nextFloat() - random2.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1008:
            this.theWorld.playSoundAtPos(blockPosIn, "mob.ghast.fireball", 10.0F, (random2.nextFloat() - random2.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1009:
            this.theWorld.playSoundAtPos(blockPosIn, "mob.ghast.fireball", 2.0F, (random2.nextFloat() - random2.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1010:
            this.theWorld.playSoundAtPos(blockPosIn, "mob.zombie.wood", 2.0F, (random2.nextFloat() - random2.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1011:
            this.theWorld.playSoundAtPos(blockPosIn, "mob.zombie.metal", 2.0F, (random2.nextFloat() - random2.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1012:
            this.theWorld.playSoundAtPos(blockPosIn, "mob.zombie.woodbreak", 2.0F, (random2.nextFloat() - random2.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1014:
            this.theWorld.playSoundAtPos(blockPosIn, "mob.wither.shoot", 2.0F, (random2.nextFloat() - random2.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1015:
            this.theWorld.playSoundAtPos(blockPosIn, "mob.bat.takeoff", 0.05F, (random2.nextFloat() - random2.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1016:
            this.theWorld.playSoundAtPos(blockPosIn, "mob.zombie.infect", 2.0F, (random2.nextFloat() - random2.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1017:
            this.theWorld.playSoundAtPos(blockPosIn, "mob.zombie.unfect", 2.0F, (random2.nextFloat() - random2.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1020:
            this.theWorld.playSoundAtPos(blockPosIn, "random.anvil_break", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 1021:
            this.theWorld.playSoundAtPos(blockPosIn, "random.anvil_use", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 1022:
            this.theWorld.playSoundAtPos(blockPosIn, "random.anvil_land", 0.3F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 2000:
            int i = data % 3 - 1;
            int j = data / 3 % 3 - 1;
            double d = blockPosIn.getX() + i * 0.6 + 0.5;
            double e = blockPosIn.getY() + 0.5;
            double d2 = blockPosIn.getZ() + j * 0.6 + 0.5;

            for (int k = 0; k < 10; k++) {
               double d3 = random2.nextDouble() * 0.2 + 0.01;
               double d4 = d + i * 0.01 + (random2.nextDouble() - 0.5) * j * 0.5;
               double d5 = e + (random2.nextDouble() - 0.5) * 0.5;
               double d6 = d2 + j * 0.01 + (random2.nextDouble() - 0.5) * i * 0.5;
               double d7 = i * d3 + random2.nextGaussian() * 0.01;
               double d8 = -0.03 + random2.nextGaussian() * 0.01;
               double d9 = j * d3 + random2.nextGaussian() * 0.01;
               this.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d4, d5, d6, d7, d8, d9);
            }

            return;
         case 2001:
            Block block = Block.getBlockById(data & 4095);
            if (block.getMaterial() != Material.air) {
               this.mc
                  .getSoundHandler()
                  .playSound(
                     new PositionedSoundRecord(
                        new ResourceLocation(block.stepSound.getBreakSound()),
                        (block.stepSound.getVolume() + 1.0F) / 2.0F,
                        block.stepSound.getFrequency() * 0.8F,
                        blockPosIn.getX() + 0.5F,
                        blockPosIn.getY() + 0.5F,
                        blockPosIn.getZ() + 0.5F
                     )
                  );
            }

            this.mc.effectRenderer.addBlockDestroyEffects(blockPosIn, block.getStateFromMeta(data >> 12 & 0xFF));
            break;
         case 2002:
            double d10 = blockPosIn.getX();
            double d11 = blockPosIn.getY();
            double d12 = blockPosIn.getZ();

            for (int l = 0; l < 8; l++) {
               this.spawnParticle(
                  EnumParticleTypes.ITEM_CRACK,
                  d10,
                  d11,
                  d12,
                  random2.nextGaussian() * 0.15,
                  random2.nextDouble() * 0.2,
                  random2.nextGaussian() * 0.15,
                  Item.getIdFromItem(Items.potionitem),
                  data
               );
            }

            int m = Items.potionitem.getColorFromDamage(data);
            float f = (m >> 16 & 0xFF) / 255.0F;
            float g = (m >> 8 & 0xFF) / 255.0F;
            float h = (m >> 0 & 0xFF) / 255.0F;
            EnumParticleTypes enumParticleTypes = EnumParticleTypes.SPELL;
            if (Items.potionitem.isEffectInstant(data)) {
               enumParticleTypes = EnumParticleTypes.SPELL_INSTANT;
            }

            for (int n = 0; n < 100; n++) {
               double d13 = random2.nextDouble() * 4.0;
               double d14 = random2.nextDouble() * Math.PI * 2.0;
               double d15 = Math.cos(d14) * d13;
               double d16 = 0.01 + random2.nextDouble() * 0.5;
               double d17 = Math.sin(d14) * d13;
               EntityFX entityFX = this.spawnEntityFX(
                  enumParticleTypes.getParticleID(), enumParticleTypes.getShouldIgnoreRange(), d10 + d15 * 0.1, d11 + 0.3, d12 + d17 * 0.1, d15, d16, d17
               );
               if (entityFX != null) {
                  float f2 = 0.75F + random2.nextFloat() * 0.25F;
                  entityFX.setRBGColorF(f * f2, g * f2, h * f2);
                  entityFX.multiplyVelocity((float)d13);
               }
            }

            this.theWorld.playSoundAtPos(blockPosIn, "game.potion.smash", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 2003:
            double d18 = blockPosIn.getX() + 0.5;
            double d19 = blockPosIn.getY();
            double d20 = blockPosIn.getZ() + 0.5;

            for (int i2 = 0; i2 < 8; i2++) {
               this.spawnParticle(
                  EnumParticleTypes.ITEM_CRACK,
                  d18,
                  d19,
                  d20,
                  random2.nextGaussian() * 0.15,
                  random2.nextDouble() * 0.2,
                  random2.nextGaussian() * 0.15,
                  Item.getIdFromItem(Items.ender_eye)
               );
            }

            for (double d21 = 0.0; d21 < Math.PI * 2; d21 += Math.PI / 20) {
               this.spawnParticle(
                  EnumParticleTypes.PORTAL,
                  d18 + Math.cos(d21) * 5.0,
                  d19 - 0.4,
                  d20 + Math.sin(d21) * 5.0,
                  Math.cos(d21) * -5.0,
                  0.0,
                  Math.sin(d21) * -5.0
               );
               this.spawnParticle(
                  EnumParticleTypes.PORTAL,
                  d18 + Math.cos(d21) * 5.0,
                  d19 - 0.4,
                  d20 + Math.sin(d21) * 5.0,
                  Math.cos(d21) * -7.0,
                  0.0,
                  Math.sin(d21) * -7.0
               );
            }

            return;
         case 2004:
            for (int i3 = 0; i3 < 20; i3++) {
               double d22 = blockPosIn.getX() + 0.5 + (this.theWorld.rand.nextFloat() - 0.5) * 2.0;
               double d23 = blockPosIn.getY() + 0.5 + (this.theWorld.rand.nextFloat() - 0.5) * 2.0;
               double d24 = blockPosIn.getZ() + 0.5 + (this.theWorld.rand.nextFloat() - 0.5) * 2.0;
               this.theWorld.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d22, d23, d24, 0.0, 0.0, 0.0);
               this.theWorld.spawnParticle(EnumParticleTypes.FLAME, d22, d23, d24, 0.0, 0.0, 0.0);
            }

            return;
         case 2005:
            ItemDye.spawnBonemealParticles(this.theWorld, blockPosIn, data);
      }
   }

   @Override
   public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {
      if (progress >= 0 && progress < 10) {
         DestroyBlockProgress destroyBlockProgress = this.damagedBlocks.get(breakerId);
         if (destroyBlockProgress == null || destroyBlockProgress.getPosition().getX() != pos.getX() || destroyBlockProgress.getPosition().getY() != pos.getY() || destroyBlockProgress.getPosition().getZ() != pos.getZ()) {
            destroyBlockProgress = new DestroyBlockProgress(breakerId, pos);
            this.damagedBlocks.put(breakerId, destroyBlockProgress);
         }

         destroyBlockProgress.setPartialBlockDamage(progress);
         destroyBlockProgress.setCloudUpdateTick(this.cloudTickCounter);
      } else {
         this.damagedBlocks.remove(breakerId);
      }
   }

   public void setDisplayListEntitiesDirty() {
      this.displayListEntitiesDirty = true;
   }

   public void resetClouds() {
      this.cloudRenderer.reset();
   }

   public int getCountRenderers() {
      return this.viewFrustum.renderChunks.length;
   }

   public int getCountActiveRenderers() {
      return this.renderInfos.size();
   }

   public int getCountEntitiesRendered() {
      return this.countEntitiesRendered;
   }

   public int getCountTileEntitiesRendered() {
      return this.countTileEntitiesRendered;
   }

   public RenderChunk getRenderChunk(BlockPos blockPos) {
      return this.viewFrustum.getRenderChunk(blockPos);
   }

   public RenderChunk getRenderChunk(RenderChunk renderChunk, EnumFacing enumFacing) {
      if (renderChunk == null) {
         return null;
      }

      BlockPos blockPos = renderChunk.getBlockPosOffset16(enumFacing);
      return this.viewFrustum.getRenderChunk(blockPos);
   }

   public WorldClient getWorld() {
      return this.theWorld;
   }

   public void updateTileEntities(Collection<TileEntity> tileEntitiesToRemove, Collection<TileEntity> tileEntitiesToAdd) {
      Set set = this.setTileEntities;
      synchronized (this.setTileEntities) {
         this.setTileEntities.removeAll(tileEntitiesToRemove);
         this.setTileEntities.addAll(tileEntitiesToAdd);
      }
   }

   public static class ContainerLocalRenderInformation {
      public final RenderChunk renderChunk;
      public final EnumFacing facing;
      public final Set<EnumFacing> setFacing = EnumSet.noneOf(EnumFacing.class);
      public final int counter;

      public ContainerLocalRenderInformation(RenderChunk renderChunk, EnumFacing enumFacing, int i) {
         this.renderChunk = renderChunk;
         this.facing = enumFacing;
         this.counter = i;
      }
   }
}
