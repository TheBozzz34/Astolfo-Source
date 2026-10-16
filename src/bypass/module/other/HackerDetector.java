package bypass.module.other;

import bypass.anticheat.AutoArmorCheck;
import bypass.anticheat.BlinkCheck;
import bypass.anticheat.Check;
import bypass.anticheat.CheckType;
import bypass.anticheat.IllegalAnglesCheck;
import bypass.anticheat.MovementCheck;
import bypass.anticheat.NoSlowdownCheck;
import bypass.anticheat.PlayerChecks;
import bypass.anticheat.SpoofGroundACheck;
import bypass.anticheat.SpoofGroundBCheck;
import bypass.event.EventHandler;
import bypass.event.Listener;
import bypass.event.network.PacketDirection;
import bypass.event.network.PacketEvent;
import bypass.event.world.WorldLoadEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.module.hidden.BotDetector;
import bypass.relation.Relation;
import bypass.setting.BooleanSetting;
import bypass.setting.MapSetting;
import bypass.setting.MapSettingBuilder;
import bypass.util.GameUtil;
import bypass.util.collection.ShapedMap;
import java.util.ArrayList;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S1CPacketEntityMetadata;

public final class HackerDetector extends Module {
   private final ShapedMap<Integer, PlayerChecks> checksByEntityId = new ShapedMap<>();
   private final MapSetting checks = new MapSettingBuilder("Checks")
      .ordered()
      .description("killaura entiti")
      .add("Spoof-Ground", new BooleanSetting("Spoof-Ground", "Checks for nofall/ground spoof", true))
      .add("Auto-Armor", new BooleanSetting("Auto-Armor", "Checks for Auto-Armor", true))
      .add("Blink", new BooleanSetting("Blink", "Checks for Blink", true))
      .add("No-Slow", new BooleanSetting("No-Slow", "Checks for no slowdown", true))
      .add("Illegal-Angles", new BooleanSetting("Illegal-Angles", "Checks for illegal angles", true))
      .build();
   public static final BooleanSetting debug = new BooleanSetting("Debug", "prints flags to chat", false);
   private final BooleanSetting autoEnemy = new BooleanSetting("Auto-Enemy", "Sets people to enemy", true);
   private final BooleanSetting autoReport = new BooleanSetting("Auto-Report", "Auto reports cheaters", false);
   private final ArrayList<String> detectedPlayers = new ArrayList<>();
   @EventHandler(PacketEvent.class)
   private final Listener<PacketEvent> onPacket = packetEvent -> {
      if (packetEvent.getDirection() == PacketDirection.RECEIVED) {
         if (packetEvent.getPacket() instanceof S14PacketEntity) {
            S14PacketEntity s14PacketEntity = (S14PacketEntity)packetEvent.getPacket();
            if (s14PacketEntity.func_149062_c() == 0 && s14PacketEntity.func_149061_d() == 0 && s14PacketEntity.func_149064_e() == 0 || mc.theWorld == null) {
               return;
            }

            Entity entity = s14PacketEntity.getEntity(mc.theWorld);
            if (entity instanceof EntityPlayer) {
               EntityPlayer entityPlayer = (EntityPlayer)entity;
               if (entityPlayer.ticksExisted < 15
                  || GameUtil.isDead(entityPlayer)
                  || client.getUserManager().getUser(entity.getName()) != null
                  || entity.E()
                  || entityPlayer.capabilities != null && entityPlayer.capabilities.allowFlying
                  || BotDetector.bots.contains(entityPlayer)
                  || client.getRelationManager().isFriend(entity)
                  || client.getRelationManager().isEnemy(entity)
                  || this.detectedPlayers.contains(entityPlayer.getName())) {
                  this.checksByEntityId.remove(entity.getEntityId());
                  return;
               }

               PlayerChecks playerChecks = this.checksByEntityId.get(entity.getEntityId());
               this.checksByEntityId.computeIfAbsent(entity.getEntityId(), integer -> {
                  PlayerChecks playerChecks2 = new PlayerChecks();
                  if (this.checks.getSetting("No-Slow").asBoolean()) {
                     PlayerChecks.addCheckTo(playerChecks2, new NoSlowdownCheck());
                  }

                  if (this.checks.getSetting("Spoof-Ground").asBoolean()) {
                     PlayerChecks.addCheckTo(playerChecks2, new SpoofGroundACheck());
                     PlayerChecks.addCheckTo(playerChecks2, new SpoofGroundBCheck());
                  }

                  if (this.checks.getSetting("Auto-Armor").asBoolean()) {
                     PlayerChecks.addCheckTo(playerChecks2, new AutoArmorCheck());
                  }

                  if (this.checks.getSetting("Illegal-Angles").asBoolean()) {
                     PlayerChecks.addCheckTo(playerChecks2, new IllegalAnglesCheck());
                  }

                  if (this.checks.getSetting("Blink").asBoolean()) {
                     PlayerChecks.addCheckTo(playerChecks2, new BlinkCheck());
                  }

                  return playerChecks2;
               });
               if (playerChecks != null) {
                  for (Object object : PlayerChecks.getChecks(playerChecks)) {
                     Check check = (Check)object;
                     if (check.getCheckType() == CheckType.MOVEMENT) {
                        MovementCheck movementCheck = (MovementCheck)check;
                        double d = entity.serverPosX + s14PacketEntity.func_149062_c();
                        double e = entity.serverPosY + s14PacketEntity.func_149061_d();
                        double d2 = entity.serverPosZ + s14PacketEntity.func_149064_e();
                        double d3 = d / 32.0;
                        double d4 = e / 32.0;
                        double d5 = d2 / 32.0;
                        float f = s14PacketEntity.func_149066_f() * 360 / 256.0F;
                        float g = s14PacketEntity.func_149063_g() * 360 / 256.0F;
                        if (movementCheck.handleMovement(d3, d4, d5, f, g, s14PacketEntity.getOnGround(), entityPlayer) >= movementCheck.getMaxViolations()) {
                           toasts.warningMessage("Hacker Detector", entity.getName() + " has been detected for " + movementCheck.getName());
                           if (this.autoEnemy.getValue()) {
                              client.getRelationManager().add(new Relation(entityPlayer.getName(), entityPlayer.getName(), false));
                           }

                           if (this.autoReport.getValue()) {
                              mc.thePlayer.sendQueue.u(new C01PacketChatMessage("/wdr " + entity.getName() + " ka fly nokb ac speed"));
                           }

                           this.detectedPlayers.add(entityPlayer.getName());
                           movementCheck.D_void();
                           break;
                        }
                     }
                  }
               }
            }
         }

         if (packetEvent.getPacket() instanceof S1CPacketEntityMetadata) {
            S1CPacketEntityMetadata s1CPacketEntityMetadata = (S1CPacketEntityMetadata)packetEvent.getPacket();
            if (s1CPacketEntityMetadata.func_149376_c() != null) {
               for (DataWatcher.WatchableObject watchableObject : s1CPacketEntityMetadata.func_149376_c()) {
                  if (watchableObject.getDataValueId() == 6 && watchableObject.getObject() instanceof Number) {
                     Number number = (Number)watchableObject.getObject();
                     if (number.floatValue() <= 0.0F) {
                        this.checksByEntityId.remove(s1CPacketEntityMetadata.getEntityId());
                     }
                  }
               }
            }
         }
      }
   };
   @EventHandler(WorldLoadEvent.class)
   private final Listener<WorldLoadEvent> onWorldLoad = worldLoadEvent -> this.checksByEntityId.clear();

   public HackerDetector(Category category) {
      super(category);
      this.addSettings(this.checks, this.autoEnemy, this.autoReport);
   }
}
