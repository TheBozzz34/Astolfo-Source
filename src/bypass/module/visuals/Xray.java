package bypass.module.visuals;

import bypass.event.CancellableEvent;
import bypass.event.EventHandler;
import bypass.event.EventPriority;
import bypass.event.EventState;
import bypass.event.Listener;
import bypass.event.network.PacketDirection;
import bypass.event.network.PacketEvent;
import bypass.event.player.UpdateEvent;
import bypass.event.render.RenderBlockEvent;
import bypass.event.render.RenderEvent;
import bypass.event.render.RenderType;
import bypass.event.render.SetupTerrainEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.module.player.Scaffold;
import bypass.render.RenderUtil;
import bypass.setting.BooleanSetting;
import bypass.setting.EnumSetting;
import bypass.setting.MapSetting;
import bypass.setting.MapSettingBuilder;
import bypass.setting.NumberSetting;
import bypass.util.Stopwatch;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.server.S22PacketMultiBlockChange;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

public final class Xray extends Module {
   public static final LinkedList unusedList = new LinkedList();
   private static final MapSetting ores = new MapSettingBuilder("Ores")
      .ordered()
      .description("ore blocks")
      .add("Diamond", new BooleanSetting("Diamond", "Searches diamond", true))
      .add("Iron", new BooleanSetting("Iron", "Searches iron", false))
      .add("Coal", new BooleanSetting("Coal", "Searches coal", false))
      .add("Emerald", new BooleanSetting("Emerald", "Searches emerald", false))
      .add("Gold", new BooleanSetting("Gold", "Searches gold", false))
      .add("Redstone", new BooleanSetting("Redstone", "Searches redstone", false))
      .add("Lapis", new BooleanSetting("Lapis", "Searches Lapis", false))
      .build();
   private static final MapSetting esp = new MapSettingBuilder("ESP")
      .ordered()
      .description("ore blocks")
      .add("Enable", new BooleanSetting("Enable", "Enables ESP", true))
      .add("Boxes", new BooleanSetting("Boxes", "Enables boxes", true))
      .add("Tracers", new BooleanSetting("Tracers", "Enables tracers", true))
      .add("Distance", new NumberSetting<>("Distance", "Distance of ESP", 30.0, 0.0, 100.0))
      .add("Width", new NumberSetting<>("Width", "Line width", 1.0F, 0.1F, 3.0F))
      .build();
   private static final MapSetting further = new MapSettingBuilder("Further")
      .ordered()
      .description("ore blocks")
      .add("Delay", new NumberSetting<>("Delay", "Delay before scanning a block", 2, 0, 50, "ms"))
      .add("Range", new NumberSetting<>("Range", "Range of further exploit", 4, 1, 8))
      .add("Refresh-Distance", new NumberSetting<>("Refresh-Distance", "Distance before refreshing again", 5, 1, 8))
      .build();
   public static final NumberSetting<Integer> opacity = new NumberSetting<>("Opacity", "Blocks opacity", 10, 0, 100);
   public static final EnumSetting<XrayHypixelMode> hypixel = new EnumSetting<>("Hypixel", "Only shows ores in caves", XrayHypixelMode.FALSE);
   private float savedGamma = -1.0F;
   private int lastOpacity;
   private int savedAmbientOcclusion = -1;
   private final Stopwatch unusedTimer = new Stopwatch();
   public static final Map<BlockPos, Block> foundOres = new ConcurrentHashMap<>();
   public static final ArrayList<BlockPos> revealedOres = new ArrayList<>();
   @EventHandler(value = SetupTerrainEvent.class, priority = EventPriority.LOWEST)
   private final Listener<SetupTerrainEvent> onSetupTerrain = CancellableEvent::cancel;
   @EventHandler(PacketEvent.class)
   private final Listener<PacketEvent> onPacket = var0 -> {
      if (hypixel.getValue() == XrayHypixelMode.FURTHER) {
         if (var0.getDirection() == PacketDirection.RECEIVED) {
            if (var0.getPacket() instanceof S22PacketMultiBlockChange) {
               S22PacketMultiBlockChange var4 = (S22PacketMultiBlockChange)var0.getPacket();

               for (S22PacketMultiBlockChange.BlockUpdateData var8 : var4.getChangedBlocks()) {
                  if (isEnabledOre(var8.getBlockState().getBlock())
                     && mc.thePlayer.getDistance(var8.getPos().getX(), var8.getPos().getY(), var8.getPos().getZ()) <= further.getSetting("Range").asInt()) {
                     revealedOres.add(var8.getPos());
                     foundOres.replace(var8.getPos(), var8.getBlockState().getBlock());
                  }
               }
            }

            if (var0.getPacket() instanceof S23PacketBlockChange) {
               S23PacketBlockChange var9 = (S23PacketBlockChange)var0.getPacket();
               if (isEnabledOre(var9.getBlockState().getBlock())
                  && mc.thePlayer.getDistance(var9.getBlockPosition().getX(), var9.getBlockPosition().getY(), var9.getBlockPosition().getZ())
                     <= further.getSetting("Range").asInt()) {
                  revealedOres.add(var9.getBlockPosition());
                  foundOres.replace(var9.getBlockPosition(), var9.getBlockState().getBlock());
               }
            }
         }
      }
   };
   private XrayScan scan;
   private Vec3 lastScanPos = new Vec3(0.0, 0.0, 0.0);
   @EventHandler(UpdateEvent.class)
   private final Listener<UpdateEvent> onUpdate = var1x -> {
      if (this.lastOpacity != opacity.getValue()) {
         this.reloadChunks(256);
         this.lastOpacity = opacity.getValue();
      }

      if (var1x.getState() == EventState.PRE && hypixel.getValue() == XrayHypixelMode.FURTHER) {
         int var5 = further.getSetting("Range").asInt();
         if (mc.thePlayer.getDistance(this.lastScanPos.xCoord, this.lastScanPos.yCoord, this.lastScanPos.zCoord)
               >= further.getSetting("Refresh-Distance").asInt()
            && (this.scan == null || XrayScan.isDone(this.scan))) {
            this.lastScanPos = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
            Thread var6 = new Thread(() -> {
               if (this.scan != null) {
                  XrayScan.setTotal(this.scan, Math.pow(var5 * 2 + 1, 3.0));
               }

               for (int var5x = -var5; var5x <= var5; var5x++) {
                  for (int var6x = -var5; var6x <= var5; var6x++) {
                     for (int var7 = -var5; var7 <= var5; var7++) {
                        if (this.scan != null) {
                           XrayScan.incrementScanned(this.scan);
                           if (XrayScan.getScanned(this.scan) > XrayScan.getTotal(this.scan)) {
                              break;
                           }
                        }

                        BlockPos var8 = new BlockPos(mc.thePlayer.posX + var5x, mc.thePlayer.posY + var6x, mc.thePlayer.posZ + var7);
                        IBlockState var9 = mc.theWorld.getBlockState(var8);
                        if (var9 != null && Scaffold.canPlaceAgainst(var8) && var9.getBlock() != Blocks.cobblestone && var9.getBlock() != Blocks.air) {
                           C07PacketPlayerDigging var10 = new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, var8, EnumFacing.UP);
                           mc.thePlayer.sendQueue.u(var10);

                           try {
                              Thread.sleep(further.getSetting("Delay").asInt());
                           } catch (InterruptedException var12) {
                              var12.printStackTrace();
                           }
                        }
                     }
                  }
               }

               if (this.scan != null) {
                  XrayScan.setDone(this.scan, true);
               }
            });
            this.scan = new XrayScan(this, var6);
            var6.start();
         }
      }
   };
   private final Color diamondColor = new Color(98, 188, 220);
   private final Color ironColor = new Color(134, 135, 134);
   private final Color coalColor = new Color(78, 78, 78);
   private final Color goldColor = new Color(219, 199, 105);
   private final Color redstoneColor = new Color(217, 73, 73);
   private final Color emeraldColor = new Color(106, 250, 106);
   private final Color lapisColor = new Color(30, 112, 175);
   @EventHandler(RenderBlockEvent.class)
   private final Listener<RenderBlockEvent> onRenderBlock = var0 -> {
      if (esp.getSetting("Enable").asBoolean() && isEnabledOre(var0.getBlock())) {
         BlockPos var4 = new BlockPos(var0.getPos().getX(), var0.getPos().getY(), var0.getPos().getZ());
         float var5 = (float)(mc.thePlayer.posX - var0.getPos().getX());
         float var6 = (float)(mc.thePlayer.posZ - var0.getPos().getZ());
         float var7 = MathHelper.sqrt_float(var5 * var5 + var6 * var6);
         if (var7 < esp.getSetting("Distance").asDouble()) {
            foundOres.computeIfAbsent(var4, var1x -> var0.getBlock());
         }
      }
   };
   @EventHandler(RenderEvent.class)
   private final Listener<RenderEvent> onRender = var1x -> {
      if (var1x.getType() == RenderType.WORLD) {
         Iterator var5 = foundOres.entrySet().iterator();

         while (var5.hasNext()) {
            Entry var6 = (Entry)var5.next();
            if (!isEnabledOre(mc.theWorld.getBlockState((BlockPos)var6.getKey()).getBlock())) {
               var5.remove();
            } else {
               double var7 = ((BlockPos)var6.getKey()).getX() - mc.getRenderManager().renderPosX;
               double var9 = ((BlockPos)var6.getKey()).getY() - mc.getRenderManager().renderPosY;
               double var11 = ((BlockPos)var6.getKey()).getZ() - mc.getRenderManager().renderPosZ;
               Color var13 = null;
               if (var6.getValue() == Blocks.diamond_ore) {
                  var13 = this.diamondColor;
               } else if (var6.getValue() == Blocks.iron_ore) {
                  var13 = this.ironColor;
               } else if (var6.getValue() == Blocks.coal_ore) {
                  var13 = this.coalColor;
               } else if (var6.getValue() == Blocks.emerald_ore) {
                  var13 = this.emeraldColor;
               } else if (var6.getValue() == Blocks.gold_ore) {
                  var13 = this.goldColor;
               } else if (var6.getValue() == Blocks.redstone_ore || var6.getValue() == Blocks.lit_redstone_ore) {
                  var13 = this.redstoneColor;
               } else if (var6.getValue() == Blocks.lapis_ore) {
                  var13 = this.lapisColor;
               }

               if (var13 != null) {
                  boolean var14 = mc.gameSettings.viewBobbing;
                  float var15 = mc.thePlayer.timeInPortal;
                  float var16 = mc.thePlayer.prevTimeInPortal;
                  mc.gameSettings.viewBobbing = false;
                  mc.thePlayer.timeInPortal = 0.0F;
                  mc.thePlayer.prevTimeInPortal = 0.0F;
                  mc.entityRenderer.setupCameraTransform(var1x.getPartialTicks(), 0);
                  drawOre(var1x, var7, var9, var11, var13.getRed(), var13.getGreen(), var13.getBlue(), 255);
                  mc.gameSettings.viewBobbing = var14;
                  mc.thePlayer.timeInPortal = var15;
                  mc.thePlayer.prevTimeInPortal = var16;
               }
            }
         }
      } else if (this.scan != null && !XrayScan.isDone(this.scan)) {
         String var17 = String.format("Scanning blocks (%s/%s)", XrayScan.getScanned(this.scan), (int)XrayScan.getTotal(this.scan));
         float var18 = var1x.getResolution().getScaledWidth() / 2.0F;
         float var19 = var18 - 80.0F;
         float var8 = var18 + 80.0F;
         int var20 = (int)((var19 + var8) / 2.0F - mc.fontRendererObj.getStringWidth(var17) / 4.0F);
         int var10 = (int)(var19 + (var8 - var19) * (XrayScan.getScanned(this.scan) / XrayScan.getTotal(this.scan) * 100.0 * 0.01F));
         RenderUtil.drawThinBorderedRect(
            var19 - 1.0F,
            var1x.getResolution().getScaledHeight() - 91.0F,
            var8 + 1.0F,
            var1x.getResolution().getScaledHeight() - 79.0F,
            new Color(1, 1, 1, 120).getRGB(),
            new Color(1, 1, 1, 200).getRGB()
         );
         RenderUtil.drawRect(
            var10,
            var1x.getResolution().getScaledHeight() - 80,
            var19,
            var1x.getResolution().getScaledHeight() - 90,
            client.getColorSetting().getValue().getRGB()
         );
         GL11.glPushMatrix();
         GL11.glScalef(0.5F, 0.5F, 0.5F);
         mc.fontRendererObj.drawStringWithShadow(var17, var20 * 2, (var1x.getResolution().getScaledHeight() - 87.5F) * 2.0F, -1);
         GL11.glPopMatrix();
      }
   };
   private static final long e = 99221782711289L;

   public Xray(Category var1) {
      super(var1);
      this.addSettings(ores, esp, further, opacity, hypixel);
   }

   public static void drawOre(RenderEvent var0, double var1, double var3, double var5, int var7, int var8, int var9, int var10) {
      long var11 = e ^ 116506298947349L;
      GL11.glPushMatrix();
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      GL11.glDisable(3553);
      GL11.glEnable(2848);
      GL11.glDisable(2929);
      GL11.glDepthMask(false);
      GL11.glLineWidth(esp.getSetting("Width").asFloat());
      GlStateManager.disableLighting();
      GL11.glColor4f(var7 / 255.0F, var8 / 255.0F, var9 / 255.0F, 1.0F);
      if (esp.getSetting("Tracers").asBoolean()) {
         GL11.glBegin(1);
         GL11.glVertex3d(0.0, mc.thePlayer.getEyeHeight(), 0.0);
         GL11.glVertex3d(var1, var3, var5);
         GL11.glEnd();
      }

      if (esp.getSetting("Boxes").asBoolean()) {
         drawOutlinedBox(new AxisAlignedBB(var1, var3, var5, var1 + 1.0, var3 + 1.0, var5 + 1.0));
      }

      GlStateManager.enableLighting();
      GL11.glDisable(2848);
      GL11.glEnable(3553);
      GL11.glEnable(2929);
      GL11.glDepthMask(true);
      long var10000 = 5931225241701262368L ^ var11;
      GL11.glDisable(3042);
      GL11.glPopMatrix();
   }

   public static void drawOutlinedBox(AxisAlignedBB var0) {
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

   @Override
   public void onEnable() {
      this.lastOpacity = opacity.getValue();
      this.savedAmbientOcclusion = mc.gameSettings.ambientOcclusion;
      this.savedGamma = mc.gameSettings.Zu;
      if (mc.theWorld != null) {
         mc.gameSettings.Zu = 10.0F;
         mc.gameSettings.ambientOcclusion = 2;
      }

      this.reloadChunks(256);
      super.onEnable();
   }

   @Override
   public void onDisable() {
      if (this.savedGamma != -1.0F) {
         mc.gameSettings.Zu = this.savedGamma;
      }

      if (this.savedAmbientOcclusion != -1) {
         mc.gameSettings.ambientOcclusion = this.savedAmbientOcclusion;
      }

      unusedList.clear();
      this.reloadChunks(256);
      foundOres.clear();
      this.scan = null;
      revealedOres.clear();
      super.onDisable();
   }

   public void reloadChunks(int var1) {
      if (mc.theWorld != null) {
         int var5 = (int)mc.thePlayer.posX;
         int var6 = (int)mc.thePlayer.posY;
         int var7 = (int)mc.thePlayer.posZ;
         mc.theWorld.markBlockRangeForRenderUpdate(var5 - var1, var6 - var1, var7 - var1, var5 + var1, var6 + var1, var7 + var1);
      }
   }

   public static boolean isEnabledOre(Block var0) {
      if (var0 == Blocks.diamond_ore) {
         return ores.getSetting("Diamond").asBoolean();
      } else if (var0 == Blocks.iron_ore) {
         return ores.getSetting("Iron").asBoolean();
      } else if (var0 == Blocks.coal_ore) {
         return ores.getSetting("Coal").asBoolean();
      } else if (var0 == Blocks.emerald_ore) {
         return ores.getSetting("Emerald").asBoolean();
      } else if (var0 == Blocks.gold_ore) {
         return ores.getSetting("Gold").asBoolean();
      } else if (var0 == Blocks.redstone_ore || var0 == Blocks.lit_redstone_ore) {
         return ores.getSetting("Redstone").asBoolean();
      } else {
         return var0 == Blocks.lapis_ore ? ores.getSetting("Lapis").asBoolean() : false;
      }
   }

   public static boolean isTranslucent() {
      return opacity.getValue() != 0;
   }
}
