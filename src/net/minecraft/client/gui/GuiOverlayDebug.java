package net.minecraft.client.gui;

import bypass.Astolfo;
import bypass.module.movement.Flight;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.src.Reflector;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public class GuiOverlayDebug extends Gui {
   private final Minecraft mc;
   private final FontRenderer fontRenderer;
   private static final long a = 118522087579577L;

   public GuiOverlayDebug(Minecraft var1) {
      this.mc = var1;
      this.fontRenderer = var1.fontRendererObj;
   }

   public void renderDebugInfo(ScaledResolution var1) {
      this.mc.mcProfiler.startSection("debug");
      GlStateManager.pushMatrix();
      this.renderDebugInfoLeft();
      this.renderDebugInfoRight(var1);
      GlStateManager.popMatrix();
      this.mc.mcProfiler.endSection();
   }

   private boolean isReducedDebug() {
      return this.mc.thePlayer.w() || this.mc.gameSettings.reducedDebugInfo;
   }

   public void renderDebugInfoLeft() {
      List var4 = this.call();

      for (int var5 = 0; var5 < var4.size(); var5++) {
         String var6 = (String)var4.get(var5);
         if (!Strings.isNullOrEmpty(var6)) {
            int var7 = this.fontRenderer.FONT_HEIGHT;
            int var8 = this.fontRenderer.getStringWidth(var6);
            int var10 = 2 + var7 * var5;
            drawRect(1, var10 - 1, 2 + var8 + 1, var10 + var7 - 1, -1873784752);
            this.fontRenderer.D(var6, 2.0F, var10, 14737632);
         }
      }
   }

   public void renderDebugInfoRight(ScaledResolution var1) {
      List var5 = this.getDebugInfoRight();

      for (int var6 = 0; var6 < var5.size(); var6++) {
         String var7 = (String)var5.get(var6);
         if (!Strings.isNullOrEmpty(var7)) {
            int var8 = this.fontRenderer.FONT_HEIGHT;
            int var9 = this.fontRenderer.getStringWidth(var7);
            int var10 = var1.getScaledWidth() - 2 - var9;
            int var11 = 2 + var8 * var6;
            drawRect(var10 - 1, var11 - 1, var10 + var9 + 1, var11 + var8 - 1, -1873784752);
            this.fontRenderer.D(var7, var10, var11, 14737632);
         }
      }
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public List<String> call() {
      long var1 = a ^ 104508566965719L;
      long var3 = var1 ^ 4057086717917L;
      BlockPos var6 = new BlockPos(
         this.mc.getRenderViewEntity().posX, this.mc.getRenderViewEntity().getEntityBoundingBox().minY, this.mc.getRenderViewEntity().posZ
      );
      if (this.isReducedDebug()) {
         return Lists.newArrayList(
            "Minecraft 1.8.8 (" + this.mc.getVersion() + "/" + ClientBrandRetriever.getClientModName() + ")",
            this.mc.xQ,
            this.mc.renderGlobal.getDebugInfoRenders(),
            this.mc.renderGlobal.getDebugInfoEntities(),
            "P: " + this.mc.effectRenderer.getStatistics() + ". T: " + this.mc.theWorld.getDebugLoadedEntities(),
            this.mc.theWorld.getProviderName(),
            "",
            String.format("Chunk-relative: %d %d %d", var6.getX() & 15, var6.getY() & 15, var6.getZ() & 15)
         );
      }

      Entity var7 = this.mc.getRenderViewEntity();
      EnumFacing var8 = var7.getHorizontalFacing();
      String var9 = "Invalid";
      switch (var8) {
         case NORTH:
            var9 = "Towards negative Z";
            break;
         case SOUTH:
            var9 = "Towards positive Z";
            break;
         case WEST:
            var9 = "Towards negative X";
            break;
         case EAST:
            var9 = "Towards positive X";
      }

      ArrayList var10 = Lists.newArrayList(
         "Minecraft 1.8.8 (" + this.mc.getVersion() + "/" + ClientBrandRetriever.getClientModName() + ")",
         this.mc.xQ,
         this.mc.renderGlobal.getDebugInfoRenders(),
         this.mc.renderGlobal.getDebugInfoEntities(),
         "P: " + this.mc.effectRenderer.getStatistics() + ". T: " + this.mc.theWorld.getDebugLoadedEntities(),
         this.mc.theWorld.getProviderName(),
         "",
         String.format(
            "XYZ: %.3f / %.5f / %.3f",
            this.mc.getRenderViewEntity().posX,
            Astolfo.INSTANCE.getModuleManager().get(Flight.class).isEnabled()
               ? Math.floor(Double.valueOf(this.mc.getRenderViewEntity().getEntityBoundingBox().minY))
               : Double.valueOf(this.mc.getRenderViewEntity().getEntityBoundingBox().minY),
            this.mc.getRenderViewEntity().posZ
         ),
         String.format("Block: %d %d %d", var6.getX(), var6.getY(), var6.getZ()),
         String.format(
            "Chunk: %d %d %d in %d %d %d", var6.getX() & 15, var6.getY() & 15, var6.getZ() & 15, var6.getX() >> 4, var6.getY() >> 4, var6.getZ() >> 4
         ),
         String.format(
            "Facing: %s (%s) (%.1f / %.1f)", var8, var9, MathHelper.wrapAngleTo180_float(var7.rotationYaw), MathHelper.wrapAngleTo180_float(var7.rotationPitch)
         )
      );
      if (this.mc.theWorld != null && this.mc.theWorld.isBlockLoaded(var6)) {
         Chunk var11 = this.mc.theWorld.getChunkFromBlockCoords(var6);
         var10.add("Biome: " + var11.getBiome(var6, this.mc.theWorld.getWorldChunkManager()).biomeName);
         var10.add(
            "Light: "
               + var11.getLightSubtracted(var6, 0)
               + " ("
               + var11.getLightFor(EnumSkyBlock.SKY, var6)
               + " sky, "
               + var11.getLightFor(EnumSkyBlock.BLOCK, var6)
               + " block)"
         );
         DifficultyInstance var12 = this.mc.theWorld.getDifficultyForLocation(var6);
         if (this.mc.isIntegratedServerRunning() && this.mc.getIntegratedServer() != null) {
            EntityPlayerMP var13 = this.mc.getIntegratedServer().getConfigurationManager().getPlayerByUUID(this.mc.thePlayer.getUniqueID());
            if (var13 != null) {
               var12 = var13.worldObj.getDifficultyForLocation(new BlockPos(var13));
            }
         }

         var10.add(String.format("Local Difficulty: %.2f (Day %d)", var12.getAdditionalDifficulty(), this.mc.theWorld.getWorldTime() / 24000L));
      }

      if (this.mc.entityRenderer != null && this.mc.entityRenderer.Q_boolean()) {
         var10.add("Shader: " + this.mc.entityRenderer.getShaderGroup().getShaderGroupName());
      }

      if (this.mc.objectMouseOver != null
         && this.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK
         && this.mc.objectMouseOver.getBlockPos() != null) {
         BlockPos var14 = this.mc.objectMouseOver.getBlockPos();
         var10.add(String.format("Looking at: %d %d %d", var14.getX(), var14.getY(), var14.getZ()));
      }

      return var10;
   }

   public List<String> getDebugInfoRight() {
      long var4 = Runtime.getRuntime().maxMemory();
      long var6 = Runtime.getRuntime().totalMemory();
      long var8 = Runtime.getRuntime().freeMemory();
      long var10 = var6 - var8;
      ArrayList var12 = Lists.newArrayList(
         String.format("Java: %s %dbit", System.getProperty("java.version"), this.mc.isJava64bit() ? 64 : 32),
         String.format("Mem: % 2d%% %03d/%03dMB", var10 * 100L / var4, bytesToMb(var10), bytesToMb(var4)),
         String.format("Allocated: % 2d%% %03dMB", var6 * 100L / var4, bytesToMb(var6)),
         "",
         String.format("CPU: %s", OpenGlHelper.getCpu()),
         "",
         String.format("Display: %dx%d (%s)", Display.getWidth(), Display.getHeight(), GL11.glGetString(7936)),
         GL11.glGetString(7937),
         GL11.glGetString(7938)
      );
      if (Reflector.FMLCommonHandler_getBrandings.exists()) {
         Object var13 = Reflector.call(Reflector.FMLCommonHandler_instance);
         var12.add("");
         var12.addAll((Collection)Reflector.call(var13, Reflector.FMLCommonHandler_getBrandings, false));
      }

      if (this.isReducedDebug()) {
         return var12;
      }

      if (this.mc.objectMouseOver != null
         && this.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK
         && this.mc.objectMouseOver.getBlockPos() != null) {
         BlockPos var18 = this.mc.objectMouseOver.getBlockPos();
         IBlockState var14 = this.mc.theWorld.getBlockState(var18);
         if (this.mc.theWorld.getWorldType() != WorldType.DEBUG_WORLD) {
            var14 = var14.getBlock().getActualState(var14, this.mc.theWorld, var18);
         }

         var12.add("");
         var12.add(String.valueOf(Block.blockRegistry.getNameForObject(var14.getBlock())));

         for (Entry var15 : var14.getProperties().entrySet()) {
            String var16 = ((Comparable)var15.getValue()).toString();
            if (var15.getValue() == Boolean.TRUE) {
               var16 = EnumChatFormatting.GREEN + var16;
            } else if (var15.getValue() == Boolean.FALSE) {
               var16 = EnumChatFormatting.RED + var16;
            }

            var12.add(((IProperty)var15.getKey()).getName() + ": " + var16);
         }
      }

      return var12;
   }

   private static long bytesToMb(long var0) {
      return var0 / 1024L / 1024L;
   }
}
