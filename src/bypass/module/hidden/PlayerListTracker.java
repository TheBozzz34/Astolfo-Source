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
   private final Listener<RenderEvent> onRender = renderEvent -> {
      if (renderEvent.getType() == RenderType.WORLD) {
         this.saveMatrices(renderEvent.getPartialTicks());
      }
   };
   @EventHandler(WorldLoadEvent.class)
   private final Listener<WorldLoadEvent> onWorldLoad = worldLoadEvent -> {
      disconnectedPlayers.clear();
      GameUtil.N = null;
   };
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = packetEvent -> {
      if (packetEvent.getDirection() == PacketDirection.RECEIVED) {
         if (GuiIngame.A) {
            PlayerList.S.clear();
            lastPositions.clear();
            GuiIngame.A = false;
         }

         if (packetEvent.getPacket() instanceof S38PacketPlayerListItem) {
            S38PacketPlayerListItem s38PacketPlayerListItem = (S38PacketPlayerListItem)packetEvent.getPacket();

            for (S38PacketPlayerListItem.AddPlayerData addPlayerData : s38PacketPlayerListItem.getEntries()) {
               if (s38PacketPlayerListItem.getAction() == S38PacketPlayerListItem.Action.REMOVE_PLAYER
                  && addPlayerData != null
                  && mc.playerController != null
                  && mc.playerController.netClientHandler != null
                  && mc.playerController.netClientHandler.playerInfoMap != null) {
                  recordDisconnect(mc.playerController.netClientHandler.playerInfoMap.get(addPlayerData.getProfile().getId()));
               }
            }
         }

         if (packetEvent.getPacket() instanceof S14PacketEntity) {
            S14PacketEntity s14PacketEntity = (S14PacketEntity)packetEvent.getPacket();
            if (s14PacketEntity.func_149062_c() == 0 && s14PacketEntity.func_149061_d() == 0 && s14PacketEntity.func_149064_e() == 0) {
               return;
            }

            if (mc.theWorld != null) {
               Entity entity = s14PacketEntity.getEntity(mc.theWorld);
               if (entity instanceof EntityPlayer) {
                  EntityPlayer entityPlayer = (EntityPlayer)entity;
                  BlockPos blockPos = lastPositions.get(entityPlayer.getName());
                  if (blockPos == null) {
                     lastPositions.put(entityPlayer.getName(), entityPlayer.getPosition());
                  } else {
                     lastPositions.replace(entityPlayer.getName(), entityPlayer.getPosition());
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
   private final Listener<UpdateEvent> onUpdate = updateEvent -> {
      if (updateEvent.getState() == EventState.PRE) {
         if (mc.thePlayer.ticksExisted <= 50
            || !GameUtil.isOnHypixel()
            || !AutoHypixel.autoChecker.getSetting("Enabled").asBoolean()
            || !client.getModuleManager().get(AutoHypixel.class).isEnabled()) {
            playerStats.clear();
         } else if (checkTimer.hasElapsed(15000.0F)) {
            List list = GuiPlayerTabOverlay.field_175252_a.sortedCopy(mc.thePlayer.sendQueue.getPlayerInfoMap());
            tabListNames.clear();

            for (Object object : list) {
               NetworkPlayerInfo networkPlayerInfo = (NetworkPlayerInfo)object;
               String string = networkPlayerInfo.getGameProfile().getName();
               tabListNames.add(string);
               if ((networkPlayerInfo.getGameProfile() == null || !networkPlayerInfo.getGameProfile().getName().equals(mc.thePlayer.getGameProfile().getName()))
                  && (networkPlayerInfo.getDisplayName() == null || !networkPlayerInfo.getDisplayName().getUnformattedText().contains("[NCP]"))) {
                  String string2 = ScorePlayerTeam.formatPlayerName(networkPlayerInfo.getPlayerTeam(), networkPlayerInfo.getGameProfile().getName());
                  if (string2.contains("§") && !string2.contains("§8") && !checkedPlayers.contains(networkPlayerInfo.getGameProfile().getName())) {
                     if (requestedPlayers.contains(string) && HypixelAPI.O > 0) {
                        if (!networkPlayerInfo.lookupTimer.hasElapsed(15000.0F)) {
                           continue;
                        }

                        requestedPlayers.remove(string);
                     }

                     networkPlayerInfo.nickedTicks++;
                     if (networkPlayerInfo.nickedTicks > 50 && networkPlayerInfo.getGameProfile() != null && !networkPlayerInfo.getGameProfile().getName().isEmpty()) {
                        requestedPlayers.add(string);
                        String string3 = AutoHypixel.autoChecker.getSetting("API-Key").asString();

                        try {
                           hypixelApi.l(UUID.fromString(string3));
                        } catch (Exception exception) {
                           chat.f("Invalid API Key");
                        }

                        PlayerStatsChecker playerStatsChecker = new PlayerStatsChecker(hypixelApi);
                        playerStatsChecker.h(networkPlayerInfo.getGameProfile().getId(), string, networkPlayerInfo);
                        networkPlayerInfo.lookupTimer.reset();
                     }
                  }
               }
            }
         }
      }
   };

   public PlayerListTracker() {
      client.getEventBus().subscribe(this);
   }

   public static void recordDisconnect(NetworkPlayerInfo networkPlayerInfo) {
      if (networkPlayerInfo != null) {
         String string = networkPlayerInfo.getGameProfile().getName();
         disconnectedPlayers.add(new DisconnectedPlayer(System.currentTimeMillis(), string));
      }
   }

   public static void printRecentDisconnects() {
      ArrayList arrayList = new ArrayList();
      StringBuilder stringBuilder = new StringBuilder();
      long l = System.currentTimeMillis();

      for (DisconnectedPlayer disconnectedPlayer : disconnectedPlayers) {
         String string = disconnectedPlayer.name;
         if (string != null && l - disconnectedPlayer.time <= 2000L && !arrayList.contains(string)) {
            arrayList.add(string);
            stringBuilder.append(" ").append(string);
         }
      }

      if (!arrayList.isEmpty()) {
         String string2 = stringBuilder.toString();
         mc.thePlayer
            .addChatMessage(
               new ChatComponentText(EnumChatFormatting.RED + "Player" + (arrayList.size() == 1 ? "" : "s") + " disconnected :" + EnumChatFormatting.AQUA + string2)
                  .setChatStyle(
                     new ChatStyle()
                        .setChatHoverEvent(
                           new HoverEvent(
                              HoverEvent.Action.SHOW_TEXT,
                              new ChatComponentText(
                                 EnumChatFormatting.GREEN
                                    + "Player"
                                    + (arrayList.size() == 1 ? "" : "s")
                                    + " disconnected in the last 2 seconds, click this message to run : \n\n"
                                    + EnumChatFormatting.YELLOW
                                    + "/who"
                                    + string2
                              )
                           )
                        )
                        .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/stalk" + string2))
                  )
            );
      }
   }

   public static float[] project(float f, float g, float h) {
      ((Buffer)projected).clear();
      boolean bl = GLU.gluProject(f, g, h, modelView, projection, viewport, projected);
      return bl ? new float[]{projected.get(0), viewport.get(3) - projected.get(1), projected.get(2)} : null;
   }

   private void saveMatrices(float f) {
      GL11.glPushAttrib(4096);
      GL11.glMatrixMode(5889);
      GL11.glPushMatrix();
      GL11.glMatrixMode(5888);
      GL11.glPushMatrix();
      mc.entityRenderer.setupCameraTransform(f, 0);
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
