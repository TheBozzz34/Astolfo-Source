package bypass.module.hidden;

import bypass.event.EventHandler;
import bypass.event.EventState;
import bypass.event.Listener;
import bypass.event.network.PacketDirection;
import bypass.event.network.PacketEvent;
import bypass.event.player.UpdateEvent;
import bypass.event.render.RenderEvent;
import bypass.event.render.RenderType;
import bypass.event.world.WorldLoadEvent;
import bypass.hypixel.PlayerStatsChecker;
import bypass.hypixel.PlayerStatsEntry;
import bypass.module.HiddenModule;
import bypass.module.other.AutoHypixel;
import bypass.module.visuals.PlayerList;
import bypass.util.GameUtil;
import bypass.util.Stopwatch;
import com.google.common.collect.EvictingQueue;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import net.hypixel.api.HypixelAPI;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public final class PlayerListTracker extends HiddenModule {
   private static final IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
   private static final FloatBuffer modelView = GLAllocation.createDirectFloatBuffer(16);
   private static final FloatBuffer projection = GLAllocation.createDirectFloatBuffer(16);
   private static final FloatBuffer projected = BufferUtils.createFloatBuffer(116);
   public static final HashMap<String, BlockPos> lastPositions = new LinkedHashMap<>();
   public static final EvictingQueue<DisconnectedPlayer> disconnectedPlayers = EvictingQueue.create(20);
   @EventHandler(RenderEvent.class)
   private final Listener<RenderEvent> onRender = var1 -> {
      if (var1.getType() == RenderType.WORLD) {
         this.saveMatrices(var1.getPartialTicks());
      }
   };
   @EventHandler(WorldLoadEvent.class)
   private final Listener<WorldLoadEvent> onWorldLoad = var0 -> {
      disconnectedPlayers.clear();
      GameUtil.N = null;
   };
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = var0 -> {
      if (var0.getDirection() == PacketDirection.RECEIVED) {
         if (GuiIngame.A) {
            PlayerList.S.clear();
            lastPositions.clear();
            GuiIngame.A = false;
         }

         if (var0.getPacket() instanceof S38PacketPlayerListItem) {
            S38PacketPlayerListItem var4 = (S38PacketPlayerListItem)var0.getPacket();

            for (S38PacketPlayerListItem.AddPlayerData var6 : var4.getEntries()) {
               if (var4.getAction() == S38PacketPlayerListItem.Action.REMOVE_PLAYER
                  && var6 != null
                  && mc.playerController != null
                  && mc.playerController.netClientHandler != null
                  && mc.playerController.netClientHandler.playerInfoMap != null) {
                  recordDisconnect(mc.playerController.netClientHandler.playerInfoMap.get(var6.getProfile().getId()));
               }
            }
         }

         if (var0.getPacket() instanceof S14PacketEntity) {
            S14PacketEntity var8 = (S14PacketEntity)var0.getPacket();
            if (var8.func_149062_c() == 0 && var8.func_149061_d() == 0 && var8.func_149064_e() == 0) {
               return;
            }

            if (mc.theWorld != null) {
               Entity var9 = var8.getEntity(mc.theWorld);
               if (var9 instanceof EntityPlayer) {
                  EntityPlayer var10 = (EntityPlayer)var9;
                  BlockPos var7 = lastPositions.get(var10.getName());
                  if (var7 == null) {
                     lastPositions.put(var10.getName(), var10.getPosition());
                  } else {
                     lastPositions.replace(var10.getName(), var10.getPosition());
                  }
               }
            }
         }
      }
   };
   public static CopyOnWriteArrayList<PlayerStatsEntry> playerStats = new CopyOnWriteArrayList<>();
   public static final HypixelAPI hypixelApi = new HypixelAPI(UUID.randomUUID());
   public static final CopyOnWriteArrayList<String> checkedPlayers = new CopyOnWriteArrayList<>();
   public static final CopyOnWriteArrayList<String> requestedPlayers = new CopyOnWriteArrayList<>();
   public static final CopyOnWriteArrayList<String> tabListNames = new CopyOnWriteArrayList<>();
   public static Stopwatch checkTimer = new Stopwatch();
   @EventHandler(UpdateEvent.class)
   private final Listener<UpdateEvent> onUpdate = var0 -> {
      long var1 = c ^ 84330177679483L;
      long var3 = var1 ^ 137426619429723L;
      if (var0.getState() == EventState.PRE) {
         if (mc.thePlayer.ticksExisted <= 50
            || !GameUtil.isOnHypixel()
            || !AutoHypixel.autoChecker.getSetting("Enabled").asBoolean()
            || !client.getModuleManager().get(AutoHypixel.class).isEnabled()) {
            playerStats.clear();
         } else if (checkTimer.hasElapsed(15000.0F)) {
            List var6 = GuiPlayerTabOverlay.field_175252_a.sortedCopy(mc.thePlayer.sendQueue.getPlayerInfoMap());
            tabListNames.clear();

            for (Object var8$raw : var6) {
               NetworkPlayerInfo var8 = (NetworkPlayerInfo)var8$raw;
               String var9 = var8.getGameProfile().getName();
               tabListNames.add(var9);
               if ((var8.getGameProfile() == null || !var8.getGameProfile().getName().equals(mc.thePlayer.getGameProfile().getName()))
                  && (var8.getDisplayName() == null || !var8.getDisplayName().getUnformattedText().contains("[NCP]"))) {
                  String var10 = ScorePlayerTeam.formatPlayerName(var8.getPlayerTeam(), var8.getGameProfile().getName());
                  if (var10.contains("§") && !var10.contains("§8") && !checkedPlayers.contains(var8.getGameProfile().getName())) {
                     if (requestedPlayers.contains(var9) && HypixelAPI.O > 0) {
                        if (!var8.lookupTimer.hasElapsed(15000.0F)) {
                           continue;
                        }

                        requestedPlayers.remove(var9);
                     }

                     var8.nickedTicks++;
                     if (var8.nickedTicks > 50 && var8.getGameProfile() != null && !var8.getGameProfile().getName().isEmpty()) {
                        requestedPlayers.add(var9);
                        String var11 = AutoHypixel.autoChecker.getSetting("API-Key").asString();

                        try {
                           hypixelApi.l(UUID.fromString(var11));
                        } catch (Exception var13) {
                           chat.f("Invalid API Key");
                        }

                        PlayerStatsChecker var12 = new PlayerStatsChecker(hypixelApi);
                        var12.h(var8.getGameProfile().getId(), var9, var8);
                        var8.lookupTimer.reset();
                     }
                  }
               }
            }
         }
      }
   };
   private static final long c = 7938261704339L;

   public PlayerListTracker() {
      client.getEventBus().subscribe(this);
   }

   public static void recordDisconnect(NetworkPlayerInfo var0) {
      if (var0 != null) {
         String var4 = var0.getGameProfile().getName();
         disconnectedPlayers.add(new DisconnectedPlayer(System.currentTimeMillis(), var4));
      }
   }

   public static void printRecentDisconnects() {
      ArrayList var3 = new ArrayList();
      StringBuilder var4 = new StringBuilder();
      long var5 = System.currentTimeMillis();

      for (DisconnectedPlayer var8 : disconnectedPlayers) {
         String var9 = var8.name;
         if (var9 != null && var5 - var8.time <= 2000L && !var3.contains(var9)) {
            var3.add(var9);
            var4.append(" ").append(var9);
         }
      }

      if (!var3.isEmpty()) {
         String var10 = var4.toString();
         mc.thePlayer
            .addChatMessage(
               new ChatComponentText(EnumChatFormatting.RED + "Player" + (var3.size() == 1 ? "" : "s") + " disconnected :" + EnumChatFormatting.AQUA + var10)
                  .setChatStyle(
                     new ChatStyle()
                        .setChatHoverEvent(
                           new HoverEvent(
                              HoverEvent.Action.SHOW_TEXT,
                              new ChatComponentText(
                                 EnumChatFormatting.GREEN
                                    + "Player"
                                    + (var3.size() == 1 ? "" : "s")
                                    + " disconnected in the last 2 seconds, click this message to run : \n\n"
                                    + EnumChatFormatting.YELLOW
                                    + "/who"
                                    + var10
                              )
                           )
                        )
                        .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/stalk" + var10))
                  )
            );
      }
   }

   public static float[] project(float var0, float var1, float var2) {
      ((Buffer)projected).clear();
      boolean var6 = GLU.gluProject(var0, var1, var2, modelView, projection, viewport, projected);
      return var6 ? new float[]{projected.get(0), viewport.get(3) - projected.get(1), projected.get(2)} : null;
   }

   private void saveMatrices(float var1) {
      GL11.glPushAttrib(4096);
      GL11.glMatrixMode(5889);
      GL11.glPushMatrix();
      GL11.glMatrixMode(5888);
      GL11.glPushMatrix();
      mc.entityRenderer.setupCameraTransform(var1, 0);
      GlStateManager.getFloat(2983, projection);
      GlStateManager.getFloat(2982, modelView);
      GL11.glGetInteger(2978, viewport);
      GL11.glMatrixMode(5888);
      GL11.glPopMatrix();
      GL11.glMatrixMode(5889);
      GL11.glPopMatrix();
      GL11.glPopAttrib();
   }
}
