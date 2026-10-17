package net.minecraft.client.particle;

import bypass.module.ModuleManager;
import bypass.module.visuals.MoreParticles;
import bypass.module.visuals.NoRender;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.src.Config;
import net.minecraft.src.Reflector;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EffectRenderer {
   private static final ResourceLocation particleTextures = new ResourceLocation("textures/particle/particles.png");
   public World worldObj;
   private List<EntityFX>[][] fxLayers = new List[4][];
   private List<EntityParticleEmitter> particleEmitters = Lists.newArrayList();
   private TextureManager renderer;
   private Random rand = new Random();
   private Map<Integer, IParticleFactory> particleTypes = Maps.newHashMap();

   public EffectRenderer(World worldIn, TextureManager rendererIn) {
      this.worldObj = worldIn;
      this.renderer = rendererIn;

      for (int i = 0; i < 4; i++) {
         this.fxLayers[i] = new List[2];

         for (int j = 0; j < 2; j++) {
            this.fxLayers[i][j] = Lists.newArrayList();
         }
      }

      this.registerVanillaParticles();
   }

   private void registerVanillaParticles() {
      this.registerParticle(EnumParticleTypes.EXPLOSION_NORMAL.getParticleID(), new EntityExplodeFX.Factory());
      this.registerParticle(EnumParticleTypes.WATER_BUBBLE.getParticleID(), new EntityBubbleFX.Factory());
      this.registerParticle(EnumParticleTypes.WATER_SPLASH.getParticleID(), new EntitySplashFX.Factory());
      this.registerParticle(EnumParticleTypes.WATER_WAKE.getParticleID(), new EntityFishWakeFX.Factory());
      this.registerParticle(EnumParticleTypes.WATER_DROP.getParticleID(), new EntityRainFX.Factory());
      this.registerParticle(EnumParticleTypes.SUSPENDED.getParticleID(), new EntitySuspendFX.Factory());
      this.registerParticle(EnumParticleTypes.SUSPENDED_DEPTH.getParticleID(), new EntityAuraFX.Factory());
      this.registerParticle(EnumParticleTypes.CRIT.getParticleID(), new EntityCrit2FX.Factory());
      this.registerParticle(EnumParticleTypes.CRIT_MAGIC.getParticleID(), new EntityCrit2FX.MagicFactory());
      this.registerParticle(EnumParticleTypes.SMOKE_NORMAL.getParticleID(), new EntitySmokeFX.Factory());
      this.registerParticle(EnumParticleTypes.SMOKE_LARGE.getParticleID(), new EntityCritFX.Factory());
      this.registerParticle(EnumParticleTypes.SPELL.getParticleID(), new EntitySpellParticleFX.Factory());
      this.registerParticle(EnumParticleTypes.SPELL_INSTANT.getParticleID(), new EntitySpellParticleFX.InstantFactory());
      this.registerParticle(EnumParticleTypes.SPELL_MOB.getParticleID(), new EntitySpellParticleFX.MobFactory());
      this.registerParticle(EnumParticleTypes.SPELL_MOB_AMBIENT.getParticleID(), new EntitySpellParticleFX.AmbientMobFactory());
      this.registerParticle(EnumParticleTypes.SPELL_WITCH.getParticleID(), new EntitySpellParticleFX.WitchFactory());
      this.registerParticle(EnumParticleTypes.DRIP_WATER.getParticleID(), new EntityDropParticleFX.WaterFactory());
      this.registerParticle(EnumParticleTypes.DRIP_LAVA.getParticleID(), new EntityDropParticleFX.LavaFactory());
      this.registerParticle(EnumParticleTypes.VILLAGER_ANGRY.getParticleID(), new EntityHeartFX.AngryVillagerFactory());
      this.registerParticle(EnumParticleTypes.VILLAGER_HAPPY.getParticleID(), new EntityAuraFX.HappyVillagerFactory());
      this.registerParticle(EnumParticleTypes.TOWN_AURA.getParticleID(), new EntityAuraFX.Factory());
      this.registerParticle(EnumParticleTypes.NOTE.getParticleID(), new EntityNoteFX.Factory());
      this.registerParticle(EnumParticleTypes.PORTAL.getParticleID(), new EntityPortalFX.Factory());
      this.registerParticle(EnumParticleTypes.ENCHANTMENT_TABLE.getParticleID(), new EntityEnchantmentTableParticleFX.EnchantmentTable());
      this.registerParticle(EnumParticleTypes.FLAME.getParticleID(), new EntityFlameFX.Factory());
      this.registerParticle(EnumParticleTypes.LAVA.getParticleID(), new EntityLavaFX.Factory());
      this.registerParticle(EnumParticleTypes.FOOTSTEP.getParticleID(), new EntityFootStepFX.Factory());
      this.registerParticle(EnumParticleTypes.CLOUD.getParticleID(), new EntityCloudFX.Factory());
      this.registerParticle(EnumParticleTypes.REDSTONE.getParticleID(), new EntityReddustFX.Factory());
      this.registerParticle(EnumParticleTypes.SNOWBALL.getParticleID(), new EntityBreakingFX.SnowballFactory());
      this.registerParticle(EnumParticleTypes.SNOW_SHOVEL.getParticleID(), new EntitySnowShovelFX.Factory());
      this.registerParticle(EnumParticleTypes.SLIME.getParticleID(), new EntityBreakingFX.SlimeFactory());
      this.registerParticle(EnumParticleTypes.HEART.getParticleID(), new EntityHeartFX.Factory());
      this.registerParticle(EnumParticleTypes.BARRIER.getParticleID(), new Barrier.Factory());
      this.registerParticle(EnumParticleTypes.ITEM_CRACK.getParticleID(), new EntityBreakingFX.Factory());
      this.registerParticle(EnumParticleTypes.BLOCK_CRACK.getParticleID(), new EntityDiggingFX.Factory());
      this.registerParticle(EnumParticleTypes.BLOCK_DUST.getParticleID(), new EntityBlockDustFX.Factory());
      this.registerParticle(EnumParticleTypes.EXPLOSION_HUGE.getParticleID(), new EntityHugeExplodeFX.Factory());
      this.registerParticle(EnumParticleTypes.EXPLOSION_LARGE.getParticleID(), new EntityLargeExplodeFX.Factory());
      this.registerParticle(EnumParticleTypes.FIREWORKS_SPARK.getParticleID(), new EntityFirework$Factory());
      this.registerParticle(EnumParticleTypes.MOB_APPEARANCE.getParticleID(), new MobAppearance.Factory());
   }

   public void registerParticle(int id, IParticleFactory particleFactory) {
      this.particleTypes.put(id, particleFactory);
   }

   public void emitParticleAtEntity(Entity entityIn, EnumParticleTypes particleTypes) {
      if ((particleTypes == EnumParticleTypes.CRIT || particleTypes == EnumParticleTypes.CRIT_MAGIC) && ModuleManager.moreParticles.isEnabled()) {
         for (int i = 0;
            i < (particleTypes == EnumParticleTypes.CRIT ? MoreParticles.criticalAmount.getValue() : MoreParticles.enchantAmount.getValue());
            i++
         ) {
            this.particleEmitters.add(new EntityParticleEmitter(this.worldObj, entityIn, particleTypes));
         }
      }

      this.particleEmitters.add(new EntityParticleEmitter(this.worldObj, entityIn, particleTypes));
   }

   public EntityFX spawnEffectParticle(
      int particleId, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters
   ) {
      IParticleFactory iParticleFactory = this.particleTypes.get(particleId);
      if (iParticleFactory != null) {
         EntityFX entityFX = iParticleFactory.getEntityFX(particleId, this.worldObj, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
         if (entityFX != null) {
            this.addEffect(entityFX);
            return entityFX;
         }
      }

      return null;
   }

   public void addEffect(EntityFX effect) {
      if (effect != null && (!(effect instanceof EntityFirework$SparkFX) || Config.isFireworkParticles())) {
         int i = effect.getFXLayer();
         int j = effect.getAlpha() != 1.0F ? 0 : 1;
         if (this.fxLayers[i][j].size() >= 4000) {
            this.fxLayers[i][j].remove(0);
         }

         if (!(effect instanceof Barrier) || !this.reuseBarrierParticle(effect, this.fxLayers[i][j])) {
            this.fxLayers[i][j].add(effect);
         }
      }
   }

   public void updateEffects() {
      for (int i = 0; i < 4; i++) {
         this.updateEffectLayer(i);
      }

      ArrayList arrayList = Lists.newArrayList();

      for (Object object : this.particleEmitters) {
         EntityParticleEmitter entityParticleEmitter = (EntityParticleEmitter)object;
         entityParticleEmitter.onUpdate();
         if (entityParticleEmitter.isDead) {
            arrayList.add(entityParticleEmitter);
         }
      }

      this.particleEmitters.removeAll(arrayList);
   }

   private void updateEffectLayer(int layer) {
      for (int i = 0; i < 2; i++) {
         this.updateEffectAlphaLayer(this.fxLayers[layer][i]);
      }
   }

   private void updateEffectAlphaLayer(List<EntityFX> entitiesFX) {
      ArrayList arrayList = Lists.newArrayList();

      for (int i = 0; i < entitiesFX.size(); i++) {
         EntityFX entityFX = (EntityFX)entitiesFX.get(i);
         this.tickParticle(entityFX);
         if (entityFX.isDead) {
            arrayList.add(entityFX);
         }
      }

      entitiesFX.removeAll(arrayList);
   }

   private void tickParticle(final EntityFX particle) {
      try {
         particle.onUpdate();
      } catch (Throwable throwable) {
         CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Ticking Particle");
         CrashReportCategory crashReportCategory = crashReport.makeCategory("Particle being ticked");
         final int i = particle.getFXLayer();
         crashReportCategory.addCrashSectionCallable("Particle", new Callable<String>() {
            public String call() throws Exception {
               return particle.toString();
            }
         });
         crashReportCategory.addCrashSectionCallable("Particle Type", new Callable<String>() {
            public String call() throws Exception {
               return i == 0 ? "MISC_TEXTURE" : (i == 1 ? "TERRAIN_TEXTURE" : (i == 3 ? "ENTITY_PARTICLE_TEXTURE" : "Unknown - " + i));
            }
         });
         throw new ReportedException(crashReport);
      }
   }

   public void renderParticles(Entity entityIn, float partialTicks) {
      if (!ModuleManager.noRender.isEnabled() || Integer.parseInt(Minecraft.getMinecraft().effectRenderer.getStatistics()) <= 500 && NoRender.smart.getValue()) {
         float f = ActiveRenderInfo.getRotationX();
         float g = ActiveRenderInfo.getRotationZ();
         float h = ActiveRenderInfo.getRotationYZ();
         float f2 = ActiveRenderInfo.getRotationXY();
         float f3 = ActiveRenderInfo.getRotationXZ();
         EntityFX.interpPosX = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * partialTicks;
         EntityFX.interpPosY = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * partialTicks;
         EntityFX.interpPosZ = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * partialTicks;
         GlStateManager.enableBlend();
         GlStateManager.blendFunc(770, 771);
         GlStateManager.alphaFunc(516, 0.003921569F);

         for (int i = 0; i < 3; i++) {
            final int j = i;

            for (int k = 0; k < 2; k++) {
               if (!this.fxLayers[j][k].isEmpty()) {
                  switch (k) {
                     case 0:
                        GlStateManager.depthMask(false);
                        break;
                     case 1:
                        GlStateManager.depthMask(true);
                  }

                  switch (j) {
                     case 0:
                     default:
                        this.renderer.bindTexture(particleTextures);
                        break;
                     case 1:
                        this.renderer.bindTexture(TextureMap.locationBlocksTexture);
                  }

                  GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                  Tessellator tessellator = Tessellator.getInstance();
                  WorldRenderer worldRenderer = tessellator.getWorldRenderer();
                  worldRenderer.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);

                  for (int l = 0; l < this.fxLayers[j][k].size(); l++) {
                     final EntityFX entityFX = this.fxLayers[j][k].get(l);

                     try {
                        entityFX.renderParticle(worldRenderer, entityIn, partialTicks, f, f3, g, h, f2);
                     } catch (Throwable throwable) {
                        CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Rendering Particle");
                        CrashReportCategory crashReportCategory = crashReport.makeCategory("Particle being rendered");
                        crashReportCategory.addCrashSectionCallable("Particle", new Callable<String>() {
                           public String call() throws Exception {
                              return entityFX.toString();
                           }
                        });
                        crashReportCategory.addCrashSectionCallable(
                           "Particle Type",
                           new Callable<String>() {
                              public String call() throws Exception {
                                 return j == 0
                                    ? "MISC_TEXTURE"
                                    : (j == 1 ? "TERRAIN_TEXTURE" : (j == 3 ? "ENTITY_PARTICLE_TEXTURE" : "Unknown - " + j));
                              }
                           }
                        );
                        throw new ReportedException(crashReport);
                     }
                  }

                  tessellator.draw();
               }
            }
         }

         GlStateManager.depthMask(true);
         GlStateManager.disableBlend();
         GlStateManager.alphaFunc(516, 0.1F);
      } else {
         this.clearEffects(this.worldObj);
      }
   }

   public void renderLitParticles(Entity entityIn, float partialTick) {
      float f = MathHelper.cos(entityIn.rotationYaw * (float) (Math.PI / 180.0));
      float g = MathHelper.sin(entityIn.rotationYaw * (float) (Math.PI / 180.0));
      float h = -g * MathHelper.sin(entityIn.rotationPitch * (float) (Math.PI / 180.0));
      float f2 = f * MathHelper.sin(entityIn.rotationPitch * (float) (Math.PI / 180.0));
      float f3 = MathHelper.cos(entityIn.rotationPitch * (float) (Math.PI / 180.0));

      for (int i = 0; i < 2; i++) {
         List list = this.fxLayers[3][i];
         if (!list.isEmpty()) {
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldRenderer = tessellator.getWorldRenderer();

            for (int j = 0; j < list.size(); j++) {
               EntityFX entityFX = (EntityFX)list.get(j);
               entityFX.renderParticle(worldRenderer, entityIn, partialTick, f, f3, g, h, f2);
            }
         }
      }
   }

   public void clearEffects(World worldIn) {
      this.worldObj = worldIn;

      for (int i = 0; i < 4; i++) {
         for (int j = 0; j < 2; j++) {
            this.fxLayers[i][j].clear();
         }
      }

      this.particleEmitters.clear();
   }

   public void addBlockDestroyEffects(BlockPos pos, IBlockState state) {
      boolean bl;
      if (Reflector.ForgeBlock_addDestroyEffects.exists() && Reflector.ForgeBlock_isAir.exists()) {
         Block block = state.getBlock();
         Reflector.callBoolean(block, Reflector.ForgeBlock_isAir, this.worldObj, pos);
         bl = !Reflector.callBoolean(block, Reflector.ForgeBlock_isAir, this.worldObj, pos)
            && !Reflector.callBoolean(block, Reflector.ForgeBlock_addDestroyEffects, this.worldObj, pos, this);
      } else {
         bl = state.getBlock().getMaterial() != Material.air;
      }

      if (bl) {
         state = state.getBlock().getActualState(state, this.worldObj, pos);
         byte b = 4;

         for (int i = 0; i < b; i++) {
            for (int j = 0; j < b; j++) {
               for (int k = 0; k < b; k++) {
                  double d = pos.getX() + (i + 0.5) / b;
                  double e = pos.getY() + (j + 0.5) / b;
                  double d2 = pos.getZ() + (k + 0.5) / b;
                  this.addEffect(
                     new EntityDiggingFX(
                           this.worldObj, d, e, d2, d - pos.getX() - 0.5, e - pos.getY() - 0.5, d2 - pos.getZ() - 0.5, state
                        )
                        .setBlockPos(pos)
                  );
               }
            }
         }
      }
   }

   public void addBlockHitEffects(BlockPos pos, EnumFacing side) {
      IBlockState iBlockState = this.worldObj.getBlockState(pos);
      Block block = iBlockState.getBlock();
      if (block.getRenderType() != -1) {
         int i = pos.getX();
         int j = pos.getY();
         int k = pos.getZ();
         float f = 0.1F;
         double d = i
            + this.rand.nextDouble() * (block.getBlockBoundsMaxX() - block.getBlockBoundsMinX() - f * 2.0F)
            + f
            + block.getBlockBoundsMinX();
         double e = j
            + this.rand.nextDouble() * (block.getBlockBoundsMaxY() - block.getBlockBoundsMinY() - f * 2.0F)
            + f
            + block.getBlockBoundsMinY();
         double d2 = k
            + this.rand.nextDouble() * (block.getBlockBoundsMaxZ() - block.getBlockBoundsMinZ() - f * 2.0F)
            + f
            + block.getBlockBoundsMinZ();
         if (side == EnumFacing.DOWN) {
            e = j + block.getBlockBoundsMinY() - f;
         }

         if (side == EnumFacing.UP) {
            e = j + block.getBlockBoundsMaxY() + f;
         }

         if (side == EnumFacing.NORTH) {
            d2 = k + block.getBlockBoundsMinZ() - f;
         }

         if (side == EnumFacing.SOUTH) {
            d2 = k + block.getBlockBoundsMaxZ() + f;
         }

         if (side == EnumFacing.WEST) {
            d = i + block.getBlockBoundsMinX() - f;
         }

         if (side == EnumFacing.EAST) {
            d = i + block.getBlockBoundsMaxX() + f;
         }

         this.addEffect(
            new EntityDiggingFX(this.worldObj, d, e, d2, 0.0, 0.0, 0.0, iBlockState).setBlockPos(pos).multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F)
         );
      }
   }

   public void moveToAlphaLayer(EntityFX effect) {
      this.moveToLayer(effect, 1, 0);
   }

   public void moveToNoAlphaLayer(EntityFX effect) {
      this.moveToLayer(effect, 0, 1);
   }

   private void moveToLayer(EntityFX effect, int layerFrom, int layerTo) {
      for (int i = 0; i < 4; i++) {
         if (this.fxLayers[i][layerFrom].contains(effect)) {
            this.fxLayers[i][layerFrom].remove(effect);
            this.fxLayers[i][layerTo].add(effect);
         }
      }
   }

   public String getStatistics() {
      int i = 0;

      for (int j = 0; j < 4; j++) {
         for (int k = 0; k < 2; k++) {
            i += this.fxLayers[j][k].size();
         }
      }

      return "" + i;
   }

   private boolean reuseBarrierParticle(EntityFX entityFX, List<EntityFX> list) {
      for (EntityFX entityFX2 : list) {
         if (entityFX2 instanceof Barrier && entityFX.posX == entityFX2.posX && entityFX.posY == entityFX2.posY && entityFX.posZ == entityFX2.posZ) {
            entityFX2.particleAge = 0;
            return true;
         }
      }

      return false;
   }

   public void addBlockHitEffects(BlockPos blockPos, MovingObjectPosition movingObjectPosition) {
      Block block = this.worldObj.getBlockState(blockPos).getBlock();
      boolean bl = Reflector.callBoolean(block, Reflector.ForgeBlock_addHitEffects, this.worldObj, movingObjectPosition, this);
      if (block != null && !bl) {
         this.addBlockHitEffects(blockPos, movingObjectPosition.sideHit);
      }
   }
}
