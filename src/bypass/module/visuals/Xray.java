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
   private final Listener<PacketEvent> onPacket = packetEvent -> {
      if (hypixel.getValue() == XrayHypixelMode.FURTHER) {
         if (packetEvent.getDirection() == PacketDirection.RECEIVED) {
            if (packetEvent.getPacket() instanceof S22PacketMultiBlockChange) {
               S22PacketMultiBlockChange s22PacketMultiBlockChange = (S22PacketMultiBlockChange)packetEvent.getPacket();

               for (S22PacketMultiBlockChange.BlockUpdateData blockUpdateData : s22PacketMultiBlockChange.getChangedBlocks()) {
                  if (isEnabledOre(blockUpdateData.getBlockState().getBlock())
                     && mc.thePlayer.getDistance(blockUpdateData.getPos().getX(), blockUpdateData.getPos().getY(), blockUpdateData.getPos().getZ()) <= further.getSetting("Range").asInt()) {
                     revealedOres.add(blockUpdateData.getPos());
                     foundOres.replace(blockUpdateData.getPos(), blockUpdateData.getBlockState().getBlock());
                  }
               }
            }

            if (packetEvent.getPacket() instanceof S23PacketBlockChange) {
               S23PacketBlockChange s23PacketBlockChange = (S23PacketBlockChange)packetEvent.getPacket();
               if (isEnabledOre(s23PacketBlockChange.getBlockState().getBlock())
                  && mc.thePlayer.getDistance(s23PacketBlockChange.getBlockPosition().getX(), s23PacketBlockChange.getBlockPosition().getY(), s23PacketBlockChange.getBlockPosition().getZ())
                     <= further.getSetting("Range").asInt()) {
                  revealedOres.add(s23PacketBlockChange.getBlockPosition());
                  foundOres.replace(s23PacketBlockChange.getBlockPosition(), s23PacketBlockChange.getBlockState().getBlock());
               }
            }
         }
      }
   };
   private XrayScan scan;
   private Vec3 lastScanPos = new Vec3(0.0, 0.0, 0.0);
   @EventHandler(UpdateEvent.class)
   private final Listener<UpdateEvent> onUpdate = updateEvent -> {
      if (this.lastOpacity != opacity.getValue()) {
         this.reloadChunks(256);
         this.lastOpacity = opacity.getValue();
      }

      if (updateEvent.getState() == EventState.PRE && hypixel.getValue() == XrayHypixelMode.FURTHER) {
         int i = further.getSetting("Range").asInt();
         if (mc.thePlayer.getDistance(this.lastScanPos.xCoord, this.lastScanPos.yCoord, this.lastScanPos.zCoord)
               >= further.getSetting("Refresh-Distance").asInt()
            && (this.scan == null || XrayScan.isDone(this.scan))) {
            this.lastScanPos = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
            Thread thread = new Thread(() -> {
               if (this.scan != null) {
                  XrayScan.setTotal(this.scan, Math.pow(i * 2 + 1, 3.0));
               }

               for (int j = -i; j <= i; j++) {
                  for (int k = -i; k <= i; k++) {
                     for (int l = -i; l <= i; l++) {
                        if (this.scan != null) {
                           XrayScan.incrementScanned(this.scan);
                           if (XrayScan.getScanned(this.scan) > XrayScan.getTotal(this.scan)) {
                              break;
                           }
                        }

                        BlockPos blockPos = new BlockPos(mc.thePlayer.posX + j, mc.thePlayer.posY + k, mc.thePlayer.posZ + l);
                        IBlockState iBlockState = mc.theWorld.getBlockState(blockPos);
                        if (iBlockState != null && Scaffold.canPlaceAgainst(blockPos) && iBlockState.getBlock() != Blocks.cobblestone && iBlockState.getBlock() != Blocks.air) {
                           C07PacketPlayerDigging c07PacketPlayerDigging = new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, blockPos, EnumFacing.UP);
                           mc.thePlayer.sendQueue.u(c07PacketPlayerDigging);

                           try {
                              Thread.sleep(further.getSetting("Delay").asInt());
                           } catch (InterruptedException interruptedException) {
                              interruptedException.printStackTrace();
                           }
                        }
                     }
                  }
               }

               if (this.scan != null) {
                  XrayScan.setDone(this.scan, true);
               }
            });
            this.scan = new XrayScan(this, thread);
            thread.start();
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
   private final Listener<RenderBlockEvent> onRenderBlock = renderBlockEvent -> {
      if (esp.getSetting("Enable").asBoolean() && isEnabledOre(renderBlockEvent.getBlock())) {
         BlockPos blockPos = new BlockPos(renderBlockEvent.getPos().getX(), renderBlockEvent.getPos().getY(), renderBlockEvent.getPos().getZ());
         float f = (float)(mc.thePlayer.posX - renderBlockEvent.getPos().getX());
         float g = (float)(mc.thePlayer.posZ - renderBlockEvent.getPos().getZ());
         float h = MathHelper.sqrt_float(f * f + g * g);
         if (h < esp.getSetting("Distance").asDouble()) {
            foundOres.computeIfAbsent(blockPos, blockPos2 -> renderBlockEvent.getBlock());
         }
      }
   };
   @EventHandler(RenderEvent.class)
   private final Listener<RenderEvent> onRender = renderEvent -> {
      if (renderEvent.getType() == RenderType.WORLD) {
         Iterator iterator2 = foundOres.entrySet().iterator();

         while (iterator2.hasNext()) {
            Entry entry = (Entry)iterator2.next();
            if (!isEnabledOre(mc.theWorld.getBlockState((BlockPos)entry.getKey()).getBlock())) {
               iterator2.remove();
            } else {
               double d = ((BlockPos)entry.getKey()).getX() - mc.getRenderManager().renderPosX;
               double e = ((BlockPos)entry.getKey()).getY() - mc.getRenderManager().renderPosY;
               double d2 = ((BlockPos)entry.getKey()).getZ() - mc.getRenderManager().renderPosZ;
               Color color = null;
               if (entry.getValue() == Blocks.diamond_ore) {
                  color = this.diamondColor;
               } else if (entry.getValue() == Blocks.iron_ore) {
                  color = this.ironColor;
               } else if (entry.getValue() == Blocks.coal_ore) {
                  color = this.coalColor;
               } else if (entry.getValue() == Blocks.emerald_ore) {
                  color = this.emeraldColor;
               } else if (entry.getValue() == Blocks.gold_ore) {
                  color = this.goldColor;
               } else if (entry.getValue() == Blocks.redstone_ore || entry.getValue() == Blocks.lit_redstone_ore) {
                  color = this.redstoneColor;
               } else if (entry.getValue() == Blocks.lapis_ore) {
                  color = this.lapisColor;
               }

               if (color != null) {
                  boolean bl = mc.gameSettings.viewBobbing;
                  float f = mc.thePlayer.timeInPortal;
                  float g = mc.thePlayer.prevTimeInPortal;
                  mc.gameSettings.viewBobbing = false;
                  mc.thePlayer.timeInPortal = 0.0F;
                  mc.thePlayer.prevTimeInPortal = 0.0F;
                  mc.entityRenderer.setupCameraTransform(renderEvent.getPartialTicks(), 0);
                  drawOre(renderEvent, d, e, d2, color.getRed(), color.getGreen(), color.getBlue(), 255);
                  mc.gameSettings.viewBobbing = bl;
                  mc.thePlayer.timeInPortal = f;
                  mc.thePlayer.prevTimeInPortal = g;
               }
            }
         }
      } else if (this.scan != null && !XrayScan.isDone(this.scan)) {
         String string = String.format("Scanning blocks (%s/%s)", XrayScan.getScanned(this.scan), (int)XrayScan.getTotal(this.scan));
         float h = renderEvent.getResolution().getScaledWidth() / 2.0F;
         float f2 = h - 80.0F;
         float f3 = h + 80.0F;
         int i = (int)((f2 + f3) / 2.0F - mc.fontRendererObj.getStringWidth(string) / 4.0F);
         int j = (int)(f2 + (f3 - f2) * (XrayScan.getScanned(this.scan) / XrayScan.getTotal(this.scan) * 100.0 * 0.01F));
         RenderUtil.drawThinBorderedRect(
            f2 - 1.0F,
            renderEvent.getResolution().getScaledHeight() - 91.0F,
            f3 + 1.0F,
            renderEvent.getResolution().getScaledHeight() - 79.0F,
            new Color(1, 1, 1, 120).getRGB(),
            new Color(1, 1, 1, 200).getRGB()
         );
         RenderUtil.drawRect(
            j,
            renderEvent.getResolution().getScaledHeight() - 80,
            f2,
            renderEvent.getResolution().getScaledHeight() - 90,
            client.getColorSetting().getValue().getRGB()
         );
         GL11.glPushMatrix();
         GL11.glScalef(0.5F, 0.5F, 0.5F);
         mc.fontRendererObj.drawStringWithShadow(string, i * 2, (renderEvent.getResolution().getScaledHeight() - 87.5F) * 2.0F, -1);
         GL11.glPopMatrix();
      }
   };
   private static final long e = 99221782711289L;

   public Xray(Category category) {
      super(category);
      this.addSettings(ores, esp, further, opacity, hypixel);
   }

   public static void drawOre(RenderEvent renderEvent, double d, double d2, double d3, int i, int j, int k, int l) {
      long m = e ^ 116506298947349L;
      GL11.glPushMatrix();
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      GL11.glDisable(3553);
      GL11.glEnable(2848);
      GL11.glDisable(2929);
      GL11.glDepthMask(false);
      GL11.glLineWidth(esp.getSetting("Width").asFloat());
      GlStateManager.disableLighting();
      GL11.glColor4f(i / 255.0F, j / 255.0F, k / 255.0F, 1.0F);
      if (esp.getSetting("Tracers").asBoolean()) {
         GL11.glBegin(1);
         GL11.glVertex3d(0.0, mc.thePlayer.getEyeHeight(), 0.0);
         GL11.glVertex3d(d, d2, d3);
         GL11.glEnd();
      }

      if (esp.getSetting("Boxes").asBoolean()) {
         drawOutlinedBox(new AxisAlignedBB(d, d2, d3, d + 1.0, d2 + 1.0, d3 + 1.0));
      }

      GlStateManager.enableLighting();
      GL11.glDisable(2848);
      GL11.glEnable(3553);
      GL11.glEnable(2929);
      GL11.glDepthMask(true);
      long n = 5931225241701262368L ^ m;
      GL11.glDisable(3042);
      GL11.glPopMatrix();
   }

   public static void drawOutlinedBox(AxisAlignedBB axisAlignedBB) {
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();
      worldRenderer.begin(3, DefaultVertexFormats.POSITION);
      worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
      worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
      worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
      worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
      worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
      tessellator.draw();
      worldRenderer.begin(3, DefaultVertexFormats.POSITION);
      worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
      worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
      worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
      worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
      worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
      tessellator.draw();
      worldRenderer.begin(1, DefaultVertexFormats.POSITION);
      worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
      worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
      worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
      worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
      worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
      worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
      worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
      worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
      tessellator.draw();
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

   public void reloadChunks(int i) {
      if (mc.theWorld != null) {
         int j = (int)mc.thePlayer.posX;
         int k = (int)mc.thePlayer.posY;
         int l = (int)mc.thePlayer.posZ;
         mc.theWorld.markBlockRangeForRenderUpdate(j - i, k - i, l - i, j + i, k + i, l + i);
      }
   }

   public static boolean isEnabledOre(Block block) {
      if (block == Blocks.diamond_ore) {
         return ores.getSetting("Diamond").asBoolean();
      } else if (block == Blocks.iron_ore) {
         return ores.getSetting("Iron").asBoolean();
      } else if (block == Blocks.coal_ore) {
         return ores.getSetting("Coal").asBoolean();
      } else if (block == Blocks.emerald_ore) {
         return ores.getSetting("Emerald").asBoolean();
      } else if (block == Blocks.gold_ore) {
         return ores.getSetting("Gold").asBoolean();
      } else if (block == Blocks.redstone_ore || block == Blocks.lit_redstone_ore) {
         return ores.getSetting("Redstone").asBoolean();
      } else {
         return block == Blocks.lapis_ore ? ores.getSetting("Lapis").asBoolean() : false;
      }
   }

   public static boolean isTranslucent() {
      return opacity.getValue() != 0;
   }
}
