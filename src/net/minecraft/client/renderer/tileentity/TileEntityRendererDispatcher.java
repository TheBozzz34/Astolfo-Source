package net.minecraft.client.renderer.tileentity;

import bypass.Astolfo;
import bypass.event.EventState;
import bypass.event.render.RenderTileEntityEvent;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ReportedException;
import net.minecraft.world.World;

public class TileEntityRendererDispatcher {
   private Map<Class<? extends TileEntity>, TileEntitySpecialRenderer<? extends TileEntity>> mapSpecialRenderers = Maps.newHashMap();
   public static TileEntityRendererDispatcher instance = new TileEntityRendererDispatcher();
   private FontRenderer fontRenderer;
   public static double staticPlayerX;
   public static double staticPlayerY;
   public static double staticPlayerZ;
   public TextureManager renderEngine;
   public World worldObj;
   public Entity entity;
   public float entityYaw;
   public float entityPitch;
   public double entityX;
   public double entityY;
   public double entityZ;
   private static final long a = 105542391263275L;

   private TileEntityRendererDispatcher() {
      this.mapSpecialRenderers.put(TileEntitySign.class, new TileEntitySignRenderer());
      this.mapSpecialRenderers.put(TileEntityMobSpawner.class, new TileEntityMobSpawnerRenderer());
      this.mapSpecialRenderers.put(TileEntityPiston.class, new TileEntityPistonRenderer());
      this.mapSpecialRenderers.put(TileEntityChest.class, new TileEntityChestRenderer());
      this.mapSpecialRenderers.put(TileEntityEnderChest.class, new TileEntityEnderChestRenderer());
      this.mapSpecialRenderers.put(TileEntityEnchantmentTable.class, new TileEntityEnchantmentTableRenderer());
      this.mapSpecialRenderers.put(TileEntityEndPortal.class, new TileEntityEndPortalRenderer());
      this.mapSpecialRenderers.put(TileEntityBeacon.class, new TileEntityBeaconRenderer());
      this.mapSpecialRenderers.put(TileEntitySkull.class, new TileEntitySkullRenderer());
      this.mapSpecialRenderers.put(TileEntityBanner.class, new TileEntityBannerRenderer());

      for (TileEntitySpecialRenderer var4 : this.mapSpecialRenderers.values()) {
         var4.setRendererDispatcher(this);
      }
   }

   public <T extends TileEntity> TileEntitySpecialRenderer<T> getSpecialRendererByClass(Class<? extends TileEntity> var1) {
      TileEntitySpecialRenderer var5 = this.mapSpecialRenderers.get(var1);
      if (var5 == null && var1 != TileEntity.class) {
         var5 = this.getSpecialRendererByClass((java.lang.Class)(var1.getSuperclass()));
         this.mapSpecialRenderers.put(var1, var5);
      }

      return var5;
   }

   public <T extends TileEntity> TileEntitySpecialRenderer<T> getSpecialRenderer(TileEntity var1) {
      return var1 == null ? null : this.getSpecialRendererByClass((Class<? extends TileEntity>)var1.getClass());
   }

   public void cacheActiveRenderInfo(World var1, TextureManager var2, FontRenderer var3, Entity var4, float var5) {
      if (this.worldObj != var1) {
         this.setWorld(var1);
      }

      this.renderEngine = var2;
      this.entity = var4;
      this.fontRenderer = var3;
      this.entityYaw = var4.prevRotationYaw + (var4.rotationYaw - var4.prevRotationYaw) * var5;
      this.entityPitch = var4.prevRotationPitch + (var4.rotationPitch - var4.prevRotationPitch) * var5;
      this.entityX = var4.lastTickPosX + (var4.posX - var4.lastTickPosX) * var5;
      this.entityY = var4.lastTickPosY + (var4.posY - var4.lastTickPosY) * var5;
      this.entityZ = var4.lastTickPosZ + (var4.posZ - var4.lastTickPosZ) * var5;
   }

   public void renderTileEntity(TileEntity var1, float var2, int var3) {
      if (var1.getDistanceSq(this.entityX, this.entityY, this.entityZ) < var1.getMaxRenderDistanceSquared()) {
         int var7 = this.worldObj.getCombinedLight(var1.getPos(), 0);
         int var8 = var7 % 65536;
         int var9 = var7 / 65536;
         OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, var8 / 1.0F, var9 / 1.0F);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         BlockPos var10 = var1.getPos();
         RenderTileEntityEvent var11 = new RenderTileEntityEvent(this, var1, var2, var3, EventState.PRE);
         Astolfo.INSTANCE.getEventBus().post(var11);
         if (!var11.isCancelled()) {
            this.renderTileEntityAt(var1, var10.getX() - staticPlayerX, var10.getY() - staticPlayerY, var10.getZ() - staticPlayerZ, var2, var3);
         }
      }
   }

   public void renderTileEntityAt(TileEntity var1, double var2, double var4, double var6, float var8) {
      this.renderTileEntityAt(var1, var2, var4, var6, var8, -1);
   }

   public void renderTileEntityAt(TileEntity var1, double var2, double var4, double var6, float var8, int var9) {
      TileEntitySpecialRenderer var13 = this.getSpecialRenderer(var1);
      if (var13 != null) {
         try {
            var13.renderTileEntityAt(var1, var2, var4, var6, var8, var9);
         } catch (Throwable var17) {
            CrashReport var15 = CrashReport.makeCrashReport(var17, "Rendering Block Entity");
            CrashReportCategory var16 = var15.makeCategory("Block Entity Details");
            var1.addInfoToCrashReport(var16);
            throw new ReportedException(var15);
         }
      }
   }

   public void setWorld(World var1) {
      this.worldObj = var1;
   }

   public FontRenderer getFontRenderer() {
      return this.fontRenderer;
   }
}
