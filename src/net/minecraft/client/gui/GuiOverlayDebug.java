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

   public GuiOverlayDebug(Minecraft mc) {
      this.mc = mc;
      this.fontRenderer = mc.fontRendererObj;
   }

   public void renderDebugInfo(ScaledResolution scaledResolutionIn) {
      this.mc.mcProfiler.startSection("debug");
      GlStateManager.pushMatrix();
      this.renderDebugInfoLeft();
      this.renderDebugInfoRight(scaledResolutionIn);
      GlStateManager.popMatrix();
      this.mc.mcProfiler.endSection();
   }

   private boolean isReducedDebug() {
      return this.mc.thePlayer.w() || this.mc.gameSettings.reducedDebugInfo;
   }

   public void renderDebugInfoLeft() {
      List list = this.call();

      for (int i = 0; i < list.size(); i++) {
         String string = (String)list.get(i);
         if (!Strings.isNullOrEmpty(string)) {
            int j = this.fontRenderer.FONT_HEIGHT;
            int k = this.fontRenderer.getStringWidth(string);
            int l = 2 + j * i;
            drawRect(1, l - 1, 2 + k + 1, l + j - 1, -1873784752);
            this.fontRenderer.D(string, 2.0F, l, 14737632);
         }
      }
   }

   public void renderDebugInfoRight(ScaledResolution scaledRes) {
      List list = this.getDebugInfoRight();

      for (int i = 0; i < list.size(); i++) {
         String string = (String)list.get(i);
         if (!Strings.isNullOrEmpty(string)) {
            int j = this.fontRenderer.FONT_HEIGHT;
            int k = this.fontRenderer.getStringWidth(string);
            int l = scaledRes.getScaledWidth() - 2 - k;
            int m = 2 + j * i;
            drawRect(l - 1, m - 1, l + k + 1, m + j - 1, -1873784752);
            this.fontRenderer.D(string, l, m, 14737632);
         }
      }
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public List<String> call() {
      BlockPos blockPos = new BlockPos(
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
            String.format("Chunk-relative: %d %d %d", blockPos.getX() & 15, blockPos.getY() & 15, blockPos.getZ() & 15)
         );
      }

      Entity entity = this.mc.getRenderViewEntity();
      EnumFacing enumFacing = entity.getHorizontalFacing();
      String string = "Invalid";
      switch (enumFacing) {
         case NORTH:
            string = "Towards negative Z";
            break;
         case SOUTH:
            string = "Towards positive Z";
            break;
         case WEST:
            string = "Towards negative X";
            break;
         case EAST:
            string = "Towards positive X";
      }

      ArrayList arrayList = Lists.newArrayList(
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
         String.format("Block: %d %d %d", blockPos.getX(), blockPos.getY(), blockPos.getZ()),
         String.format(
            "Chunk: %d %d %d in %d %d %d", blockPos.getX() & 15, blockPos.getY() & 15, blockPos.getZ() & 15, blockPos.getX() >> 4, blockPos.getY() >> 4, blockPos.getZ() >> 4
         ),
         String.format(
            "Facing: %s (%s) (%.1f / %.1f)", enumFacing, string, MathHelper.wrapAngleTo180_float(entity.rotationYaw), MathHelper.wrapAngleTo180_float(entity.rotationPitch)
         )
      );
      if (this.mc.theWorld != null && this.mc.theWorld.isBlockLoaded(blockPos)) {
         Chunk chunk = this.mc.theWorld.getChunkFromBlockCoords(blockPos);
         arrayList.add("Biome: " + chunk.getBiome(blockPos, this.mc.theWorld.getWorldChunkManager()).biomeName);
         arrayList.add(
            "Light: "
               + chunk.getLightSubtracted(blockPos, 0)
               + " ("
               + chunk.getLightFor(EnumSkyBlock.SKY, blockPos)
               + " sky, "
               + chunk.getLightFor(EnumSkyBlock.BLOCK, blockPos)
               + " block)"
         );
         DifficultyInstance difficultyInstance = this.mc.theWorld.getDifficultyForLocation(blockPos);
         if (this.mc.isIntegratedServerRunning() && this.mc.getIntegratedServer() != null) {
            EntityPlayerMP entityPlayerMP = this.mc.getIntegratedServer().getConfigurationManager().getPlayerByUUID(this.mc.thePlayer.getUniqueID());
            if (entityPlayerMP != null) {
               difficultyInstance = entityPlayerMP.worldObj.getDifficultyForLocation(new BlockPos(entityPlayerMP));
            }
         }

         arrayList.add(String.format("Local Difficulty: %.2f (Day %d)", difficultyInstance.getAdditionalDifficulty(), this.mc.theWorld.getWorldTime() / 24000L));
      }

      if (this.mc.entityRenderer != null && this.mc.entityRenderer.Q_boolean()) {
         arrayList.add("Shader: " + this.mc.entityRenderer.getShaderGroup().getShaderGroupName());
      }

      if (this.mc.objectMouseOver != null
         && this.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK
         && this.mc.objectMouseOver.getBlockPos() != null) {
         BlockPos blockPos2 = this.mc.objectMouseOver.getBlockPos();
         arrayList.add(String.format("Looking at: %d %d %d", blockPos2.getX(), blockPos2.getY(), blockPos2.getZ()));
      }

      return arrayList;
   }

   public List<String> getDebugInfoRight() {
      long l = Runtime.getRuntime().maxMemory();
      long m = Runtime.getRuntime().totalMemory();
      long n = Runtime.getRuntime().freeMemory();
      long l2 = m - n;
      ArrayList arrayList = Lists.newArrayList(
         String.format("Java: %s %dbit", System.getProperty("java.version"), this.mc.isJava64bit() ? 64 : 32),
         String.format("Mem: % 2d%% %03d/%03dMB", l2 * 100L / l, bytesToMb(l2), bytesToMb(l)),
         String.format("Allocated: % 2d%% %03dMB", m * 100L / l, bytesToMb(m)),
         "",
         String.format("CPU: %s", OpenGlHelper.getCpu()),
         "",
         String.format("Display: %dx%d (%s)", Display.getWidth(), Display.getHeight(), GL11.glGetString(7936)),
         GL11.glGetString(7937),
         GL11.glGetString(7938)
      );
      if (Reflector.FMLCommonHandler_getBrandings.exists()) {
         Object object = Reflector.call(Reflector.FMLCommonHandler_instance);
         arrayList.add("");
         arrayList.addAll((Collection)Reflector.call(object, Reflector.FMLCommonHandler_getBrandings, false));
      }

      if (this.isReducedDebug()) {
         return arrayList;
      }

      if (this.mc.objectMouseOver != null
         && this.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK
         && this.mc.objectMouseOver.getBlockPos() != null) {
         BlockPos blockPos = this.mc.objectMouseOver.getBlockPos();
         IBlockState iBlockState = this.mc.theWorld.getBlockState(blockPos);
         if (this.mc.theWorld.getWorldType() != WorldType.DEBUG_WORLD) {
            iBlockState = iBlockState.getBlock().getActualState(iBlockState, this.mc.theWorld, blockPos);
         }

         arrayList.add("");
         arrayList.add(String.valueOf(Block.blockRegistry.getNameForObject(iBlockState.getBlock())));

         for (Entry entry : iBlockState.getProperties().entrySet()) {
            String string = ((Comparable)entry.getValue()).toString();
            if (entry.getValue() == Boolean.TRUE) {
               string = EnumChatFormatting.GREEN + string;
            } else if (entry.getValue() == Boolean.FALSE) {
               string = EnumChatFormatting.RED + string;
            }

            arrayList.add(((IProperty)entry.getKey()).getName() + ": " + string);
         }
      }

      return arrayList;
   }

   private static long bytesToMb(long bytes) {
      return bytes / 1024L / 1024L;
   }
}
