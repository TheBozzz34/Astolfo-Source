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
   private static final long a = 60341087835996L;

   public EffectRenderer(World var1, TextureManager var2) {
      this.worldObj = var1;
      this.renderer = var2;

      for (int var6 = 0; var6 < 4; var6++) {
         this.fxLayers[var6] = new List[2];

         for (int var7 = 0; var7 < 2; var7++) {
            this.fxLayers[var6][var7] = Lists.newArrayList();
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

   public void registerParticle(int var1, IParticleFactory var2) {
      this.particleTypes.put(var1, var2);
   }

   public void emitParticleAtEntity(Entity var1, EnumParticleTypes var2) {
      if ((var2 == EnumParticleTypes.CRIT || var2 == EnumParticleTypes.CRIT_MAGIC) && ModuleManager.N.isEnabled()) {
         for (int var6 = 0; var6 < (var2 == EnumParticleTypes.CRIT ? MoreParticles.criticalAmount.getValue() : MoreParticles.enchantAmount.getValue()); var6++) {
            this.particleEmitters.add(new EntityParticleEmitter(this.worldObj, var1, var2));
         }
      }

      this.particleEmitters.add(new EntityParticleEmitter(this.worldObj, var1, var2));
   }

   public EntityFX spawnEffectParticle(int var1, double var2, double var4, double var6, double var8, double var10, double var12, int... var14) {
      IParticleFactory var18 = this.particleTypes.get(var1);
      if (var18 != null) {
         EntityFX var19 = var18.getEntityFX(var1, this.worldObj, var2, var4, var6, var8, var10, var12, var14);
         if (var19 != null) {
            this.addEffect(var19);
            return var19;
         }
      }

      return null;
   }

   public void addEffect(EntityFX var1) {
      if (var1 != null && (!(var1 instanceof EntityFirework$SparkFX) || Config.isFireworkParticles())) {
         int var5 = var1.getFXLayer();
         int var6 = var1.getAlpha() != 1.0F ? 0 : 1;
         if (this.fxLayers[var5][var6].size() >= 4000) {
            this.fxLayers[var5][var6].remove(0);
         }

         if (!(var1 instanceof Barrier) || !this.reuseBarrierParticle(var1, this.fxLayers[var5][var6])) {
            this.fxLayers[var5][var6].add(var1);
         }
      }
   }

   public void updateEffects() {
      for (int var4 = 0; var4 < 4; var4++) {
         this.updateEffectLayer(var4);
      }

      ArrayList var8 = Lists.newArrayList();

      for (Object var6 : this.particleEmitters) {
         EntityParticleEmitter var7 = (EntityParticleEmitter)var6;
         var7.onUpdate();
         if (var7.isDead) {
            var8.add(var7);
         }
      }

      this.particleEmitters.removeAll(var8);
   }

   private void updateEffectLayer(int var1) {
      for (int var5 = 0; var5 < 2; var5++) {
         this.updateEffectAlphaLayer(this.fxLayers[var1][var5]);
      }
   }

   private void updateEffectAlphaLayer(List<EntityFX> var1) {
      ArrayList var5 = Lists.newArrayList();

      for (int var6 = 0; var6 < var1.size(); var6++) {
         EntityFX var7 = (EntityFX)var1.get(var6);
         this.tickParticle(var7);
         if (var7.isDead) {
            var5.add(var7);
         }
      }

      var1.removeAll(var5);
   }

   private void tickParticle(final EntityFX var1) {
      try {
         var1.onUpdate();
      } catch (Throwable var8) {
         CrashReport var5 = CrashReport.makeCrashReport(var8, "Ticking Particle");
         CrashReportCategory var6 = var5.makeCategory("Particle being ticked");
         final int var7 = var1.getFXLayer();
         var6.addCrashSectionCallable("Particle", new Callable<String>() {
            public String call() throws Exception {
               return var1.toString();
            }
         });
         var6.addCrashSectionCallable("Particle Type", new Callable<String>() {
            private static final long a = 83601153650809L;

            public String call() throws Exception {
               return var7 == 0 ? "MISC_TEXTURE" : (var7 == 1 ? "TERRAIN_TEXTURE" : (var7 == 3 ? "ENTITY_PARTICLE_TEXTURE" : "Unknown - " + var7));
            }
         });
         throw new ReportedException(var5);
      }
   }

   public void renderParticles(Entity var1, float var2) {
      long var3 = a ^ 113718086392696L;
      if (!ModuleManager.I.isEnabled() || Integer.parseInt(Minecraft.getMinecraft().effectRenderer.getStatistics()) <= 500 && NoRender.smart.getValue()) {
         float var6 = ActiveRenderInfo.S();
         float var7 = ActiveRenderInfo.N();
         long var10000 = 166292431802065631L ^ var3;
         float var8 = ActiveRenderInfo.T();
         float var9 = ActiveRenderInfo.U();
         float var10 = ActiveRenderInfo.w();
         EntityFX.interpPosX = var1.lastTickPosX + (var1.posX - var1.lastTickPosX) * var2;
         EntityFX.interpPosY = var1.lastTickPosY + (var1.posY - var1.lastTickPosY) * var2;
         EntityFX.interpPosZ = var1.lastTickPosZ + (var1.posZ - var1.lastTickPosZ) * var2;
         GlStateManager.enableBlend();
         GlStateManager.blendFunc(770, 771);
         GlStateManager.alphaFunc(516, 0.003921569F);

         for (int var11 = 0; var11 < 3; var11++) {
            final int var12 = var11;

            for (int var13 = 0; var13 < 2; var13++) {
               if (!this.fxLayers[var12][var13].isEmpty()) {
                  switch (var13) {
                     case 0:
                        GlStateManager.depthMask(false);
                        break;
                     case 1:
                        GlStateManager.depthMask(true);
                  }

                  switch (var12) {
                     case 0:
                     default:
                        this.renderer.bindTexture(particleTextures);
                        break;
                     case 1:
                        this.renderer.bindTexture(TextureMap.locationBlocksTexture);
                  }

                  GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                  Tessellator var14 = Tessellator.getInstance();
                  WorldRenderer var15 = var14.getWorldRenderer();
                  var15.begin(7, DefaultVertexFormats.T);

                  for (int var16 = 0; var16 < this.fxLayers[var12][var13].size(); var16++) {
                     final EntityFX var17 = this.fxLayers[var12][var13].get(var16);

                     try {
                        var17.renderParticle(var15, var1, var2, var6, var10, var7, var8, var9);
                     } catch (Throwable var21) {
                        CrashReport var19 = CrashReport.makeCrashReport(var21, "Rendering Particle");
                        CrashReportCategory var20 = var19.makeCategory("Particle being rendered");
                        var20.addCrashSectionCallable("Particle", new Callable<String>() {
                           public String call() throws Exception {
                              return var17.toString();
                           }
                        });
                        var20.addCrashSectionCallable(
                           "Particle Type",
                           new Callable<String>() {
                              private static final long a = 103408599645973L;

                              public String call() throws Exception {
                                 return var12 == 0
                                    ? "MISC_TEXTURE"
                                    : (var12 == 1 ? "TERRAIN_TEXTURE" : (var12 == 3 ? "ENTITY_PARTICLE_TEXTURE" : "Unknown - " + var12));
                              }
                           }
                        );
                        throw new ReportedException(var19);
                     }
                  }

                  var14.draw();
               }
            }
         }

         GlStateManager.depthMask(true);
         GlStateManager.disableBlend();
         var10000 = 7325560801518072968L ^ var3;
         GlStateManager.alphaFunc(516, 0.1F);
      } else {
         this.clearEffects(this.worldObj);
      }
   }

   public void renderLitParticles(Entity var1, float var2) {
      float var7 = MathHelper.cos(var1.rotationYaw * (float) (Math.PI / 180.0));
      float var8 = MathHelper.sin(var1.rotationYaw * (float) (Math.PI / 180.0));
      float var9 = -var8 * MathHelper.sin(var1.rotationPitch * (float) (Math.PI / 180.0));
      float var10 = var7 * MathHelper.sin(var1.rotationPitch * (float) (Math.PI / 180.0));
      float var11 = MathHelper.cos(var1.rotationPitch * (float) (Math.PI / 180.0));

      for (int var12 = 0; var12 < 2; var12++) {
         List var13 = this.fxLayers[3][var12];
         if (!var13.isEmpty()) {
            Tessellator var14 = Tessellator.getInstance();
            WorldRenderer var15 = var14.getWorldRenderer();

            for (int var16 = 0; var16 < var13.size(); var16++) {
               EntityFX var17 = (EntityFX)var13.get(var16);
               var17.renderParticle(var15, var1, var2, var7, var11, var8, var9, var10);
            }
         }
      }
   }

   public void clearEffects(World var1) {
      this.worldObj = var1;

      for (int var5 = 0; var5 < 4; var5++) {
         for (int var6 = 0; var6 < 2; var6++) {
            this.fxLayers[var5][var6].clear();
         }
      }

      this.particleEmitters.clear();
   }

   public void addBlockDestroyEffects(BlockPos var1, IBlockState var2) {
      boolean var6;
      if (Reflector.ForgeBlock_addDestroyEffects.exists() && Reflector.ForgeBlock_isAir.exists()) {
         Block var7 = var2.getBlock();
         Reflector.callBoolean(var7, Reflector.ForgeBlock_isAir, this.worldObj, var1);
         var6 = !Reflector.callBoolean(var7, Reflector.ForgeBlock_isAir, this.worldObj, var1)
            && !Reflector.callBoolean(var7, Reflector.ForgeBlock_addDestroyEffects, this.worldObj, var1, this);
      } else {
         var6 = var2.getBlock().getMaterial() != Material.air;
      }

      if (var6) {
         var2 = var2.getBlock().getActualState(var2, this.worldObj, var1);
         byte var18 = 4;

         for (int var8 = 0; var8 < var18; var8++) {
            for (int var9 = 0; var9 < var18; var9++) {
               for (int var10 = 0; var10 < var18; var10++) {
                  double var11 = var1.getX() + (var8 + 0.5) / var18;
                  double var13 = var1.getY() + (var9 + 0.5) / var18;
                  double var15 = var1.getZ() + (var10 + 0.5) / var18;
                  this.addEffect(
                     new EntityDiggingFX(
                           this.worldObj, var11, var13, var15, var11 - var1.getX() - 0.5, var13 - var1.getY() - 0.5, var15 - var1.getZ() - 0.5, var2
                        )
                        .setBlockPos(var1)
                  );
               }
            }
         }
      }
   }

   public void addBlockHitEffects(BlockPos var1, EnumFacing var2) {
      IBlockState var6 = this.worldObj.getBlockState(var1);
      Block var7 = var6.getBlock();
      if (var7.getRenderType() != -1) {
         int var8 = var1.getX();
         int var9 = var1.getY();
         int var10 = var1.getZ();
         float var11 = 0.1F;
         double var12 = var8
            + this.rand.nextDouble() * (var7.getBlockBoundsMaxX() - var7.getBlockBoundsMinX() - var11 * 2.0F)
            + var11
            + var7.getBlockBoundsMinX();
         double var14 = var9
            + this.rand.nextDouble() * (var7.getBlockBoundsMaxY() - var7.getBlockBoundsMinY() - var11 * 2.0F)
            + var11
            + var7.getBlockBoundsMinY();
         double var16 = var10
            + this.rand.nextDouble() * (var7.getBlockBoundsMaxZ() - var7.getBlockBoundsMinZ() - var11 * 2.0F)
            + var11
            + var7.getBlockBoundsMinZ();
         if (var2 == EnumFacing.DOWN) {
            var14 = var9 + var7.getBlockBoundsMinY() - var11;
         }

         if (var2 == EnumFacing.UP) {
            var14 = var9 + var7.getBlockBoundsMaxY() + var11;
         }

         if (var2 == EnumFacing.NORTH) {
            var16 = var10 + var7.getBlockBoundsMinZ() - var11;
         }

         if (var2 == EnumFacing.SOUTH) {
            var16 = var10 + var7.getBlockBoundsMaxZ() + var11;
         }

         if (var2 == EnumFacing.WEST) {
            var12 = var8 + var7.getBlockBoundsMinX() - var11;
         }

         if (var2 == EnumFacing.EAST) {
            var12 = var8 + var7.getBlockBoundsMaxX() + var11;
         }

         this.addEffect(
            new EntityDiggingFX(this.worldObj, var12, var14, var16, 0.0, 0.0, 0.0, var6).setBlockPos(var1).multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F)
         );
      }
   }

   public void moveToAlphaLayer(EntityFX var1) {
      this.moveToLayer(var1, 1, 0);
   }

   public void moveToNoAlphaLayer(EntityFX var1) {
      this.moveToLayer(var1, 0, 1);
   }

   private void moveToLayer(EntityFX var1, int var2, int var3) {
      for (int var7 = 0; var7 < 4; var7++) {
         if (this.fxLayers[var7][var2].contains(var1)) {
            this.fxLayers[var7][var2].remove(var1);
            this.fxLayers[var7][var3].add(var1);
         }
      }
   }

   public String getStatistics() {
      int var4 = 0;

      for (int var5 = 0; var5 < 4; var5++) {
         for (int var6 = 0; var6 < 2; var6++) {
            var4 += this.fxLayers[var5][var6].size();
         }
      }

      return "" + var4;
   }

   private boolean reuseBarrierParticle(EntityFX var1, List<EntityFX> var2) {
      for (EntityFX var7 : var2) {
         if (var7 instanceof Barrier && var1.posX == var7.posX && var1.posY == var7.posY && var1.posZ == var7.posZ) {
            var7.particleAge = 0;
            return true;
         }
      }

      return false;
   }

   public void addBlockHitEffects(BlockPos var1, MovingObjectPosition var2) {
      Block var5 = this.worldObj.getBlockState(var1).getBlock();
      boolean var6 = Reflector.callBoolean(var5, Reflector.ForgeBlock_addHitEffects, this.worldObj, var2, this);
      if (var5 != null && !var6) {
         this.addBlockHitEffects(var1, var2.sideHit);
      }
   }
}
